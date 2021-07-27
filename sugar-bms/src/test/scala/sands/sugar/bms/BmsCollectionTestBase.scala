/*
 * Statements and Logical Operators on Boolean Monads and Collections
 *
 * Copyright (c) 2021 Serhiy Shamshetdinov (Kyiv, Ukraine)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership and used works.
 */

package sands.sugar.bms

import org.scalatest.Assertions.fail
import sands.sugar.bms.BmsCollectionTestData.isMonadicInstance
import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BmsTestOptions.TestHeavyEnabled
import sands.sugar.bms.BmsTestPatterns._
import sands.sugar.bms.typeclass.functions.BooleanFunctions.{MonadicName, ZippingName}
import sands.sugar.bms.typeclass.{BooleanCollectionMonad, BooleanMonad, NoContext}

import scala.reflect.ClassTag
import scala.util.Try

/*
 * Created by Serhiy Shamshetdinov
 * at 11.08.2021 03:41
 */

trait BmsCollectionTestData[M[_]] {
  def TypeImports: Seq[String]
  def MonadName: String
  def AsMonadImports: Seq[String]
  def TestDataImports: Seq[String]

  def PureFalse: M[Boolean]
  def PureTrue: M[Boolean]
  def PureUnit: M[Unit]
  def Empty: M[Nothing]
  def BooleanEmpty: M[Boolean]

  def pure[T: ClassTag](v: T*): M[T]
  def tryGetBoolean: Try[M[Boolean]] => Try[Boolean]

  def getExpressionBooleans(isMonadicTest: Boolean): Seq[String]
  def expressionBooleansToSameExpected(isMonadicTest: Boolean): Seq[(String, String)] = {
    val expressionBooleans = getExpressionBooleans(isMonadicTest)
    expressionBooleans.zip(expressionBooleans)
  }
  def getQuaternaryArgumentsCombinations(isMonadicTest: Boolean): Seq[(List[String], List[String])]

  def ifPredicates: Seq[String]
  def ifBranchesToExpected: Seq[(String, String)]
  def ifMonadicBranchesToExpected: Seq[(String, String)]
}

object BmsCollectionTestData {
  def isMonadicInstance[M[_]](testeeName: String): Boolean = {
    if (testeeName.contains(MonadicName)) true
    else if (testeeName.contains(ZippingName)) false
    else fail(s"Invalid instance is passed to Collections test: $testeeName")
  }
}


class BmsInvariantCollectionIfStatementsTest[M[_]](testImports: Seq[String],
                                                   testData: BmsCollectionTestData[M])
                                                  (testeeName: String) extends BmsToolBoxTestBase {
  import testData._

  protected val imports: Seq[String] =
    testImports ++ TypeImports ++ TestDataImports ++ (if (isMonadicInstance(testeeName)) AsMonadImports else Seq())

  protected val ifPredicatesToExpectedLists: Seq[(Seq[String], Seq[String])] =
    ifPredicates.map { value =>
      Seq(value) -> Seq(value)
    }

  behavior of s"$testeeName monadic 'if' with one branch"

  toolBoxTestPatternsWithValueCombinations[M, Any, M[Any]](
    imports,
    MonadName,
    AnyClassName,
    s"$MonadName[_]",
    collectionIfBinaryPatternPairs,
    appendArgumentsCombinations(ifBranchesToExpected, 1, ifPredicatesToExpectedLists),
    identity
  )

  behavior of s"$testeeName monadic 'if' with two branches"

  toolBoxTestPatternsWithValueCombinations[M, Any, M[Any]](
    imports,
    MonadName,
    AnyClassName,
    s"$MonadName[_]",
    collectionIfTernaryPatternPairs,
    appendArgumentsCombinations(ifBranchesToExpected, 2, ifPredicatesToExpectedLists),
    identity
  )
}

class BmsCovariantCollectionIfStatementsTest[M[_]](testImports: Seq[String],
                                                   testData: BmsCollectionTestData[M])
                                                  (testeeName: String) extends BmsInvariantCollectionIfStatementsTest(testImports, testData)(testeeName) {

  import testData._

  behavior of s"$testeeName flat monadic 'if' with one branch"

  toolBoxTestPatternsWithValueCombinations[M, Any, M[Any]](
    imports,
    MonadName,
    AnyClassName,
    s"$MonadName[$AnyClassName]",
    collectionFlatIfBinaryPatternPairs,
    appendArgumentsCombinations(ifMonadicBranchesToExpected, 1, ifPredicatesToExpectedLists),
    identity
  )

  behavior of s"$testeeName flat monadic 'if' with two branches"

  toolBoxTestPatternsWithValueCombinations[M, Any, M[Any]](
    imports,
    MonadName,
    AnyClassName,
    s"$MonadName[$AnyClassName]",
    collectionFlatIfTernaryPatternPairs,
    appendArgumentsCombinations(ifMonadicBranchesToExpected, 2, ifPredicatesToExpectedLists),
    identity
  )

}

class BmsCollectionBinaryExpressionsTest[M[_]](testImports: Seq[String],
                                               testData: BmsCollectionTestData[M])
                                              (testeeName: String) extends BmsToolBoxTestBase {
  import testData._

  private val monadicInstance: Boolean = isMonadicInstance(testeeName)

  private val imports: Seq[String] =
    testImports ++ TypeImports ++ TestDataImports ++ (if (monadicInstance) AsMonadImports else Seq())

  private val binaryBooleanExpressionExpectedPattern: String =
    if (monadicInstance) binaryBooleanExpressionExpectedMonadicOpsPattern else binaryBooleanExpressionExpectedZippedOpsPattern

  private val binaryExpressionPatternPairs: Seq[(String, String)] =
    pairsWithBooleanOperators(Seq(binaryBooleanExpressionCollectionOpsPattern -> binaryBooleanExpressionExpectedPattern), 1)

  behavior of s"$testeeName Binary Expressions"

  toolBoxTestPatternsWithValueCombinations[M, Boolean, M[Boolean]](
    imports,
    MonadName,
    BooleanClassName,
    s"$MonadName[$BooleanClassName]",
    binaryExpressionPatternPairs,
    prependedArgumentsCombinationsWithoutPureBooleans(expressionBooleansToSameExpected(monadicInstance), 2),
    identity
  )

  behavior of s"$testeeName Binary Expressions with inverted values"

  private val invertedExpressionBooleans: Seq[String] = getExpressionBooleans(monadicInstance).map(sInversion + _)
  private val invertedExpressionBooleansToExpected: Seq[(String, String)] = invertedExpressionBooleans.zip(invertedExpressionBooleans)

  toolBoxTestPatternsWithValueCombinations[M, Boolean, M[Boolean]](
    imports,
    MonadName,
    BooleanClassName,
    s"$MonadName[$BooleanClassName]",
    binaryExpressionPatternPairs,
    prependedArgumentsCombinationsWithoutPureBooleans(invertedExpressionBooleansToExpected, 2),
    identity
  )
}

class BmsCollectionTernaryExpressionsTest[M[_]](testImports: Seq[String],
                                                testData: BmsCollectionTestData[M])
                                               (testeeName: String) extends BmsToolBoxTestBase {
  import testData._

  private val monadicInstance: Boolean = isMonadicInstance(testeeName)

  private val imports: Seq[String] =
    testImports ++ TypeImports ++ TestDataImports ++ (if (monadicInstance) AsMonadImports else Seq())

  private val ternaryBooleanExpressionExpectedPattern: String =
    if (monadicInstance) ternaryBooleanExpressionExpectedMonadicOpsPattern else ternaryBooleanExpressionExpectedZippedOpsPattern

  private val ternaryExpressionZippedCollectionPatternPairs: Seq[(String, String)] =
    pairsWithBooleanOperators(Seq(ternaryBooleanExpressionCollectionOpsPattern -> ternaryBooleanExpressionExpectedPattern), 2)

  behavior of s"$testeeName Ternary Expressions"

  toolBoxTestPatternsWithValueCombinations[M, Boolean, M[Boolean]](
    imports,
    MonadName,
    BooleanClassName,
    s"$MonadName[$BooleanClassName]",
    ternaryExpressionZippedCollectionPatternPairs,
    prependedArgumentsCombinationsWithoutPureBooleans(expressionBooleansToSameExpected(monadicInstance), 3),
    identity,
    230000 // set by IndexedSeq for Monadic functions
  )
}

class BmsCollectionQuaternaryExpressionsPagedTest[M[_]](pageNumber: Int,
                                                        testImports: Seq[String],
                                                        testData: BmsCollectionTestData[M])
                                                       (testeeName: String) extends BmsToolBoxTestBase {
  import testData._

  val behaviorOf = s"$testeeName Quaternary Expressions (page $pageNumber)"

  if (!TestHeavyEnabled) {
    println(s"[info] * sugar-bms test is skipped due to Heavy tests are disabled: $behaviorOf")
  } else {
    val booleanOpsNumber: Int = booleanOperators.length

    pageNumber.ensuring(p => 1 <= p && p <= booleanOpsNumber, s"QuaternaryExpressionsPageTest page number should be from 1 to $booleanOpsNumber inclusively")

    behavior of behaviorOf

    val monadicInstance: Boolean = isMonadicInstance(testeeName)

    val imports: Seq[String] =
      testImports ++ TypeImports ++ TestDataImports ++ (if (monadicInstance) AsMonadImports else Seq())

    val quaternaryExpressionCollectionPatternPairs: Seq[(String, String)] =
      if (monadicInstance) quaternaryExpressionMonadicCollectionPatternPairs else quaternaryExpressionZippedCollectionPatternPairs

    val pageSize: Int = quaternaryExpressionCollectionPatternPairs.length / booleanOpsNumber
    val pagePatternPairs: Seq[(String, String)] = quaternaryExpressionCollectionPatternPairs.slice(pageSize * (pageNumber - 1), pageSize * pageNumber)

    toolBoxTestPatternsWithValueCombinations[M, Boolean, M[Boolean]](
      imports,
      MonadName,
      BooleanClassName,
      s"$MonadName[$BooleanClassName]",
      pagePatternPairs,
      getQuaternaryArgumentsCombinations(monadicInstance),
      identity,
      300000 // set by IndexedSeq for Monadic functions
    )
  }
}

import sands.sugar.bms.BooleanMonads._

class BmsCollectionGeneralTest[M[+_]](val testData: BmsCollectionTestData[M])
                                    (implicit testeeBm: BooleanCollectionMonad[M, NoContext]) extends BmsTestBase {
  import testData._

  behavior of testeeBm.name

  it should "return constants" in {
    False[M] shouldBe PureFalse
    True[M]  shouldBe PureTrue
    Unit[M]  shouldBe PureUnit

    BooleanMonad[M].False shouldBe PureFalse
    BooleanMonad[M].True  shouldBe PureTrue
    BooleanMonad[M].Unit  shouldBe PureUnit

    False[M, NoContext] shouldBe PureFalse
    True[M, NoContext]  shouldBe PureTrue
    Unit[M, NoContext]  shouldBe PureUnit

    BooleanMonad[M, NoContext].False shouldBe PureFalse
    BooleanMonad[M, NoContext].True  shouldBe PureTrue
    BooleanMonad[M, NoContext].Unit  shouldBe PureUnit
  }

  it should "invert values" in {
    !PureFalse    shouldBe PureTrue
    !PureTrue     shouldBe PureFalse
    !BooleanEmpty shouldBe BooleanEmpty
  }

  it should "use Unit in place of absent monadic false-branch" in {
    PureFalse.mapIf(333) shouldBe PureUnit
    mapIf(PureFalse, 333) shouldBe PureUnit
    mapIf(PureFalse)(333) shouldBe PureUnit

    PureFalse.mIf(333) shouldBe PureUnit
    mIf(PureFalse, 333) shouldBe PureUnit
    mIf(PureFalse)(333) shouldBe PureUnit
  }

  it should "use collection.empty[A] in place of absent flat monadic false-branch with collection[A] trueOnlyBranch" in {
    PureFalse.flatMapIf(pure(333)) shouldBe BooleanCollectionMonad[M, NoContext].empty[Int]
    flatMapIf(PureFalse)(pure(333)) shouldBe BooleanCollectionMonad[M, NoContext].empty[Int]

    PureFalse.fmIf(pure(333)) shouldBe BooleanCollectionMonad[M, NoContext].empty[Int]
    fmIf(PureFalse)(pure(333)) shouldBe BooleanCollectionMonad[M, NoContext].empty[Int]
  }

  it should "return 'if' results with nearest common base type" in {
    // this is mostly compilation test, type of the value compared to is not important

    // map 'if' with one branch
    identity[M[AnyVal]](BooleanEmpty.mapIf(333)) shouldBe BooleanEmpty
    identity[M[AnyVal]](mapIf(BooleanEmpty, 333)) shouldBe BooleanEmpty
    identity[M[AnyVal]](mapIf(BooleanEmpty)(333)) shouldBe BooleanEmpty
    // map 'if' with two branches
    identity[M[AnyVal]](BooleanEmpty.mapIf(333, {})) shouldBe BooleanEmpty
    identity[M[AnyVal]](BooleanEmpty.mapIf(333 Else {})) shouldBe BooleanEmpty
    identity[M[AnyVal]](mapIf(BooleanEmpty, 333, {})) shouldBe BooleanEmpty
    identity[M[AnyVal]](mapIf(BooleanEmpty)(333 Else {})) shouldBe BooleanEmpty
    // flatMap 'if' with one branch
    identity[M[Int]](BooleanEmpty.flatMapIf(pure(333))) shouldBe BooleanEmpty
    identity[M[Int]](flatMapIf(BooleanEmpty)(pure(333))) shouldBe BooleanEmpty
    // flatMap 'if' with two branches
    identity[M[AnyVal]](BooleanEmpty.flatMapIf(pure(333), pure({}))) shouldBe BooleanEmpty
    identity[M[AnyVal]](BooleanEmpty.flatMapIf(pure(333) Else pure({}))) shouldBe BooleanEmpty
    identity[M[AnyVal]](flatMapIf(BooleanEmpty, pure(333), pure({}))) shouldBe BooleanEmpty
    identity[M[AnyVal]](flatMapIf(BooleanEmpty)(pure(333) Else pure({}))) shouldBe BooleanEmpty

    // map 'if' with one branch
    identity[M[Unit]](BooleanEmpty.mapIf({})) shouldBe BooleanEmpty
    identity[M[Unit]](mapIf(BooleanEmpty, {})) shouldBe BooleanEmpty
    identity[M[Unit]](mapIf(BooleanEmpty)({})) shouldBe BooleanEmpty
    // map 'if' with two branches
    identity[M[Unit]](BooleanEmpty.mapIf({}, {})) shouldBe BooleanEmpty
    identity[M[Unit]](BooleanEmpty.mapIf({} Else {})) shouldBe BooleanEmpty
    identity[M[Unit]](mapIf(BooleanEmpty, {}, {})) shouldBe BooleanEmpty
    identity[M[Unit]](mapIf(BooleanEmpty)({} Else {})) shouldBe BooleanEmpty
    // flatMap 'if' with one branch
    identity[M[Unit]](BooleanEmpty.flatMapIf(pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](flatMapIf(BooleanEmpty)(pure({}))) shouldBe BooleanEmpty
    // flatMap 'if' with two branches
    identity[M[Unit]](BooleanEmpty.flatMapIf(pure({}), pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](BooleanEmpty.flatMapIf(pure({}) Else pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](flatMapIf(BooleanEmpty, pure({}), pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](flatMapIf(BooleanEmpty)(pure({}) Else pure({}))) shouldBe BooleanEmpty


    // monadic 'if' with one branch
    identity[M[AnyVal]](BooleanEmpty.mIf(333)) shouldBe BooleanEmpty
    identity[M[AnyVal]](mIf(BooleanEmpty, 333)) shouldBe BooleanEmpty
    identity[M[AnyVal]](mIf(BooleanEmpty)(333)) shouldBe BooleanEmpty
    // monadic 'if' with two branches
    identity[M[AnyVal]](BooleanEmpty.mIf(333, {})) shouldBe BooleanEmpty
    identity[M[AnyVal]](BooleanEmpty.mIf(333 Else {})) shouldBe BooleanEmpty
    identity[M[AnyVal]](mIf(BooleanEmpty, 333, {})) shouldBe BooleanEmpty
    identity[M[AnyVal]](mIf(BooleanEmpty)(333 Else {})) shouldBe BooleanEmpty
    // flat monadic 'if' with one branch
    identity[M[Int]](BooleanEmpty.fmIf(pure(333))) shouldBe BooleanEmpty
    identity[M[Int]](fmIf(BooleanEmpty)(pure(333))) shouldBe BooleanEmpty
    // flat monadic 'if' with two branches
    identity[M[AnyVal]](BooleanEmpty.fmIf(pure(333), pure({}))) shouldBe BooleanEmpty
    identity[M[AnyVal]](BooleanEmpty.fmIf(pure(333) Else pure({}))) shouldBe BooleanEmpty
    identity[M[AnyVal]](fmIf(BooleanEmpty, pure(333), pure({}))) shouldBe BooleanEmpty
    identity[M[AnyVal]](fmIf(BooleanEmpty)(pure(333) Else pure({}))) shouldBe BooleanEmpty

    // monadic 'if' with one branch
    identity[M[Unit]](BooleanEmpty.mIf({})) shouldBe BooleanEmpty
    identity[M[Unit]](mIf(BooleanEmpty, {})) shouldBe BooleanEmpty
    identity[M[Unit]](mIf(BooleanEmpty)({})) shouldBe BooleanEmpty
    // monadic 'if' with two branches
    identity[M[Unit]](BooleanEmpty.mIf({}, {})) shouldBe BooleanEmpty
    identity[M[Unit]](BooleanEmpty.mIf({} Else {})) shouldBe BooleanEmpty
    identity[M[Unit]](mIf(BooleanEmpty, {}, {})) shouldBe BooleanEmpty
    identity[M[Unit]](mIf(BooleanEmpty)({} Else {})) shouldBe BooleanEmpty
    // flat monadic 'if' with one branch
    identity[M[Unit]](BooleanEmpty.fmIf(pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](fmIf(BooleanEmpty)(pure({}))) shouldBe BooleanEmpty
    // flat monadic 'if' with two branches
    identity[M[Unit]](BooleanEmpty.fmIf(pure({}), pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](BooleanEmpty.fmIf(pure({}) Else pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](fmIf(BooleanEmpty, pure({}), pure({}))) shouldBe BooleanEmpty
    identity[M[Unit]](fmIf(BooleanEmpty)(pure({}) Else pure({}))) shouldBe BooleanEmpty
  }

  it should "iterate monadic loops" in {
    implicit val oc: OrderCollector = new OrderCollector()
    import oc.ov

    var i = 0
    val testCaseResults = List(
      // mLoop
      runBmsCase[M, Boolean, Boolean]("mLoop with no boolean", mLoop(ov(0)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[M, Boolean, Boolean]("mLoop with false monad", mLoop(ov(0)(PureFalse)), ov(0)(false)),
      runBmsCase[M, Boolean, Boolean]("mLoop iterating", {i = 3; mLoop(ov(0)({i -= 1; pure(i > 0)}))}, {i = 3; do i -= 1 while (ov(0)(i > 0)); false}),
      runBmsCase[M, Boolean, Boolean]("mLoop iterating 2 elements condition", {i = 3; mLoop(ov(0)({i -= 1; pure(false, i > 0)}))}, {i = 3; do i -= 1 while (ov(0)(i > 0)); false}),
      // mWhile
      runBmsCase[M, Boolean, Boolean]("mWhile with no boolean predicate", mWhile(ov(0)(BooleanEmpty))(ov(1)(())), ov(0)(nb)),
      runBmsCase[M, Boolean, Boolean]("mWhile with false monad predicate", mWhile(ov(0)(PureFalse))(ov(1)(())), ov(0)(false)),
      runBmsCase[M, Boolean, Boolean]("mWhile iterating", {i = 3; mWhile(ov(0)(pure(i > 0)))(ov(1)(i -= 1))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      runBmsCase[M, Boolean, Boolean]("mWhile iterating 2 elements condition", {i = 3; mWhile(ov(0)(pure(false, i > 0)))(ov(1)(i -= 1))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // fmWhile
      runBmsCase[M, Boolean, Boolean]("fmWhile with no boolean predicate", fmWhile(ov(0)(BooleanEmpty))(ov(1)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[M, Boolean, Boolean]("fmWhile with false monad predicate", fmWhile(ov(0)(PureFalse))(ov(1)(BooleanEmpty)), ov(0)(false)),
      runBmsCase[M, Boolean, Boolean]("fmWhile with finalising body", fmWhile(ov(0)(PureTrue))(ov(1)(BooleanEmpty)), {ov(0)(true); ov(1)(nb)}),
      runBmsCase[M, Boolean, Boolean]("fmWhile iterating", {i = 3; fmWhile(ov(0)(pure(i > 0)))(ov(1)(pure(i -= 1)))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      runBmsCase[M, Boolean, Boolean]("fmWhile iterating 2 elements condition", {i = 3; fmWhile(ov(0)(pure(false, i > 0)))(ov(1)(pure(i -= 1)))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // mDoWhile
      runBmsCase[M, Boolean, Boolean]("mDoWhile with no boolean predicate", mDoWhile(ov(0)(()), ov(1)(BooleanEmpty)), {ov(0)(()); ov(1)(nb)}),
      runBmsCase[M, Boolean, Boolean]("mDoWhile with false monad predicate", mDoWhile(ov(0)(()), ov(1)(PureFalse)), {ov(0)(()); ov(1)(false)}),
      runBmsCase[M, Boolean, Boolean]("mDoWhile iterating", {i = 3; mDoWhile(ov(0)(i -= 1), ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      runBmsCase[M, Boolean, Boolean]("mDoWhile iterating 2 elements condition", {i = 3; mDoWhile(ov(0)(i -= 1), ov(1)(pure(false, i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      // fmDoWhile
      runBmsCase[M, Boolean, Boolean]("fmDoWhile with finalising body", fmDoWhile(ov(0)(BooleanEmpty))(ov(1)(PureTrue)), ov(0)(nb)),
      runBmsCase[M, Boolean, Boolean]("fmDoWhile with no boolean predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(BooleanEmpty)), {ov(0)(3); ov(1)(nb)}),
      runBmsCase[M, Boolean, Boolean]("fmDoWhile with false monad predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(PureFalse)), {ov(0)(3); ov(1)(false)}),
      runBmsCase[M, Boolean, Boolean]("fmDoWhile iterating", {i = 3; fmDoWhile(ov(0)(pure(i -= 1)))(ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      runBmsCase[M, Boolean, Boolean]("fmDoWhile iterating 2 elements condition", {i = 3; fmDoWhile(ov(0)(pure(i -= 1)))(ov(1)(pure(false, i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false})
    )

    validateBmsCaseResults(testCaseResults, tryGetBoolean)
  }
}