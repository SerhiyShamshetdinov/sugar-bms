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

package sands.sugar.bms.array

import sands.sugar.bms.BmsCollectionTestData.isMonadicInstance
import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BmsTestPatterns.{appendArgumentsCombinations, collectionFlatIfBinaryPatternPairs, collectionFlatIfTernaryPatternPairs}
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.array.ArrayGeneralTest.arrayWith
import sands.sugar.bms.typeclass.functions.BooleanFunctions
import sands.sugar.bms.typeclass.functions.BooleanFunctions.ArrayAsMonad
import sands.sugar.bms.{ElseExtension => _, _}

import scala.reflect.ClassTag

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class ArrayZippedGeneralTest extends ArrayGeneralTest(BooleanMonadsImport)(BooleanFunctions.arrayZippingBooleanFunctions)
class ArrayZippedIfStatementsTest extends BmsInvariantCollectionIfStatementsTest(BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))
class ArrayZippedBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))
class ArrayZippedTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))
class ArrayZippedQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))
class ArrayZippedQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))
class ArrayZippedQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))
class ArrayZippedQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))
class ArrayZippedQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, ArrayTestData)(arrayWith(BooleanFunctions.arrayZippingBooleanFunctions.name))

class ArrayMonadicGeneralTest extends ArrayGeneralTest(BooleanMonadsImport)(ArrayAsMonad.arrayMonadicBooleanFunctions)
class ArrayMonadicIfStatementsTest extends BmsInvariantCollectionIfStatementsTest(BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))
class ArrayMonadicBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))
class ArrayMonadicTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))
class ArrayMonadicQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))
class ArrayMonadicQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))
class ArrayMonadicQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))
class ArrayMonadicQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))
class ArrayMonadicQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, ArrayTestData)(arrayWith(ArrayAsMonad.arrayMonadicBooleanFunctions.name))


class ArrayGeneralTest(testImports: Seq[String])(implicit abf: BooleanFunctions[Array, ClassTag[Boolean]]) extends BmsToolBoxTestBase {

  import ArrayTestData._

  private val testeeName: String = arrayWith(abf.name)

  behavior of testeeName

  it should "return constants" in {
    ArrayFalse shouldBe PureFalse
    ArrayTrue shouldBe PureTrue
    ArrayUnit shouldBe PureUnit

    Array.False shouldBe PureFalse
    Array.True shouldBe PureTrue
    Array.Unit shouldBe PureUnit
  }

  it should "invert values" in {
    !PureFalse shouldBe PureTrue
    !PureTrue shouldBe PureFalse
    !BooleanEmpty shouldBe BooleanEmpty
  }

  it should "use empty Array[T] in place of absent flat monadic false-branch with Array[T] trueOnlyBranch" in {
    PureFalse.flatMapIf(pure[Int](333)) shouldBe Array.empty[Int]
    flatMapIf(PureFalse)(pure[Int](333)) shouldBe Array.empty[Int]

    PureFalse.fmIf(pure[Int](333)) shouldBe Array.empty[Int]
    fmIf(PureFalse)(pure[Int](333)) shouldBe Array.empty[Int]
  }

  it should "iterate monadic loops" in {
    implicit val oc: OrderCollector = new OrderCollector()
    import oc.ov

    var i = 0
    val testCaseResults = List(
      // mLoop
      runBmsCase[Array, Boolean, Boolean]("mLoop with no boolean", mLoop(ov(0)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[Array, Boolean, Boolean]("mLoop with false monad", mLoop(ov(0)(PureFalse)), ov(0)(false)),
      runBmsCase[Array, Boolean, Boolean]("mLoop iterating", {i = 3; mLoop(ov(0)({i -= 1; pure(i > 0)}))}, {i = 3; do i -= 1 while (ov(0)(i > 0)); false}),
      runBmsCase[Array, Boolean, Boolean]("mLoop iterating 2 elements condition", {i = 3; mLoop(ov(0)({i -= 1; pure(false, i > 0)}))}, {i = 3; do i -= 1 while (ov(0)(i > 0)); false}),
      // mWhile
      runBmsCase[Array, Boolean, Boolean]("mWhile with no boolean predicate", mWhile(ov(0)(BooleanEmpty))(ov(1)(())), ov(0)(nb)),
      runBmsCase[Array, Boolean, Boolean]("mWhile with false monad predicate", mWhile(ov(0)(PureFalse))(ov(1)(())), ov(0)(false)),
      runBmsCase[Array, Boolean, Boolean]("mWhile iterating", {i = 3; mWhile(ov(0)(pure(i > 0)))(ov(1)(i -= 1))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      runBmsCase[Array, Boolean, Boolean]("mWhile iterating 2 elements condition", {i = 3; mWhile(ov(0)(pure(false, i > 0)))(ov(1)(i -= 1))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // fmWhile
      runBmsCase[Array, Boolean, Boolean]("fmWhile with no boolean predicate", fmWhile(ov(0)(BooleanEmpty))(ov(1)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[Array, Boolean, Boolean]("fmWhile with false monad predicate", fmWhile(ov(0)(PureFalse))(ov(1)(BooleanEmpty)), ov(0)(false)),
      runBmsCase[Array, Boolean, Boolean]("fmWhile with finalising body", fmWhile(ov(0)(PureTrue))(ov(1)(BooleanEmpty)), {ov(0)(true); ov(1)(nb)}),
      runBmsCase[Array, Boolean, Boolean]("fmWhile iterating", {i = 3; fmWhile(ov(0)(pure(i > 0)))(ov(1)(pure(i -= 1)))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      runBmsCase[Array, Boolean, Boolean]("fmWhile iterating 2 elements condition", {i = 3; fmWhile(ov(0)(pure(false, i > 0)))(ov(1)(pure(i -= 1)))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // mDoWhile
      runBmsCase[Array, Boolean, Boolean]("mDoWhile with no boolean predicate", mDoWhile(ov(0)(()), ov(1)(BooleanEmpty)), {ov(0)(()); ov(1)(nb)}),
      runBmsCase[Array, Boolean, Boolean]("mDoWhile with false monad predicate", mDoWhile(ov(0)(()), ov(1)(PureFalse)), {ov(0)(()); ov(1)(false)}),
      runBmsCase[Array, Boolean, Boolean]("mDoWhile iterating", {i = 3; mDoWhile(ov(0)(i -= 1), ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      runBmsCase[Array, Boolean, Boolean]("mDoWhile iterating 2 elements condition", {i = 3; mDoWhile(ov(0)(i -= 1), ov(1)(pure(false, i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      // fmDoWhile
      runBmsCase[Array, Boolean, Boolean]("fmDoWhile with finalising body", fmDoWhile(ov(0)(BooleanEmpty))(ov(1)(PureTrue)), ov(0)(nb)),
      runBmsCase[Array, Boolean, Boolean]("fmDoWhile with no boolean predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(BooleanEmpty)), {ov(0)(3); ov(1)(nb)}),
      runBmsCase[Array, Boolean, Boolean]("fmDoWhile with false monad predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(PureFalse)), {ov(0)(3); ov(1)(false)}),
      runBmsCase[Array, Boolean, Boolean]("fmDoWhile iterating", {i = 3; fmDoWhile(ov(0)(pure(i -= 1)))(ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      runBmsCase[Array, Boolean, Boolean]("fmDoWhile iterating 2 elements condition", {i = 3; fmDoWhile(ov(0)(pure(i -= 1)))(ov(1)(pure(false, i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false})
    )

    validateBmsCaseResults(testCaseResults, tryGetBoolean)
  }

  val imports: Seq[String] =
    testImports ++ TypeImports ++ TestDataImports ++ (if (isMonadicInstance(testeeName)) AsMonadImports else Seq())

  val ifPredicatesToExpectedLists: Seq[(Seq[String], Seq[String])] =
    ifPredicates.map { value =>
      Seq(value) -> Seq(value)
    }

  behavior of s"$testeeName flat monadic 'if' with one branch for Array[AnyRef]"

  val ifMonadicAnyRefBranches: Seq[String] = Seq(
    "Array[AnyRef](None, None)",
    "Array[AnyRef](None)",
    "Array[AnyRef]()"
  )

  toolBoxTestPatternsWithValueCombinations[Array, AnyRef, Array[AnyRef]](
    imports,
    MonadName,
    AnyRefClassName,
    s"$MonadName[AnyRef]",
    collectionFlatIfBinaryPatternPairs,
    appendArgumentsCombinations(ifMonadicAnyRefBranches.zip(ifMonadicAnyRefBranches), 1, ifPredicatesToExpectedLists),
    identity
  )

  behavior of s"$testeeName flat monadic 'if' with two branches for both Array[Int]"

  val ifMonadicIntRefBranches: Seq[String] = Seq(
    "Array[Int](1, 2)",
    "Array[Int](3)"
  )

  toolBoxTestPatternsWithValueCombinations[Array, Int, Array[Int]](
    imports,
    MonadName,
    IntClassName,
    s"$MonadName[Int]",
    collectionFlatIfTernaryPatternPairs,
    appendArgumentsCombinations(ifMonadicIntRefBranches.zip(ifMonadicIntRefBranches), 2, ifPredicatesToExpectedLists),
    identity
  )
}

object ArrayGeneralTest {
  def arrayWith(functionsName: String): String = s"Boolean Array with $functionsName"  
}
