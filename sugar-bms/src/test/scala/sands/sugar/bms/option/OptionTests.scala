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

package sands.sugar.bms.option

import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.option.OptionTestData._
import sands.sugar.bms.{ElseExtension => _, _}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class OptionIfStatementsTest extends BmsMonadIfStatementsTest(BooleanMonadsImport, OptionTestData)
class OptionBinaryExpressionsTest extends BmsMonadBinaryExpressionsTest(BooleanMonadsImport, OptionTestData)
class OptionTernaryExpressionsTest extends BmsMonadTernaryExpressionsTest(BooleanMonadsImport, OptionTestData)
class OptionQuaternaryExpressionsPage1Test extends BmsMonadQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, OptionTestData)
class OptionQuaternaryExpressionsPage2Test extends BmsMonadQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, OptionTestData)
class OptionQuaternaryExpressionsPage3Test extends BmsMonadQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, OptionTestData)
class OptionQuaternaryExpressionsPage4Test extends BmsMonadQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, OptionTestData)
class OptionQuaternaryExpressionsPage5Test extends BmsMonadQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, OptionTestData)

class OptionGeneralTest extends BmsToolBoxTestBase {

  behavior of s"Boolean $MonadName"

  it should "return constants" in {
    OptionFalse shouldBe PureFalse
    OptionTrue  shouldBe PureTrue
    OptionUnit  shouldBe PureUnit

    Option.False shouldBe PureFalse
    Option.True  shouldBe PureTrue
    Option.Unit  shouldBe PureUnit
  }

  it should "invert values" in {
    !PureFalse    shouldBe PureTrue
    !PureTrue     shouldBe PureFalse
    !BooleanEmpty shouldBe BooleanEmpty
  }

  it should "use Unit in place of absent monadic false-branch" in {
    PureFalse.mapIf(333) shouldBe OptionUnit
    mapIf(PureFalse, 333) shouldBe OptionUnit
    mapIf(PureFalse)(333) shouldBe OptionUnit

    PureFalse.mIf(333) shouldBe OptionUnit
    mIf(PureFalse, 333) shouldBe OptionUnit
    mIf(PureFalse)(333) shouldBe OptionUnit
  }

  it should "use None[A] in place of absent flat monadic false-branch with Option[A] trueOnlyBranch" in {
    PureFalse.flatMapIf(pure(333)) shouldBe Option.empty[Int]
    flatMapIf(PureFalse)(pure(333)) shouldBe Option.empty[Int]

    PureFalse.fmIf(pure(333)) shouldBe Option.empty[Int]
    fmIf(PureFalse)(pure(333)) shouldBe Option.empty[Int]
  }

  it should "return 'if' results with nearest common base type" in {
    // this is mostly compilation test, type of the value compared to is not important

    // map 'if' with one branch
    identity[Option[AnyVal]](BooleanEmpty.mapIf(333)) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mapIf(BooleanEmpty, 333)) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mapIf(BooleanEmpty)(333)) shouldBe BooleanEmpty
    // map 'if' with two branches
    identity[Option[AnyVal]](BooleanEmpty.mapIf(333, {})) shouldBe BooleanEmpty
    identity[Option[AnyVal]](BooleanEmpty.mapIf(333 Else {})) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mapIf(BooleanEmpty, 333, {})) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mapIf(BooleanEmpty)(333 Else {})) shouldBe BooleanEmpty
    // flatMap 'if' with one branch
    identity[Option[AnyVal]](BooleanEmpty.flatMapIf(pure(333))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](flatMapIf(BooleanEmpty)(pure(333))) shouldBe BooleanEmpty
    // flatMap 'if' with two branches
    identity[Option[AnyVal]](BooleanEmpty.flatMapIf(pure(333), pure({}))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](BooleanEmpty.flatMapIf(pure(333) Else pure({}))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](flatMapIf(BooleanEmpty, pure(333), pure({}))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](flatMapIf(BooleanEmpty)(pure(333) Else pure({}))) shouldBe BooleanEmpty

    // map 'if' with one branch
    identity[Option[Unit]](BooleanEmpty.mapIf({})) shouldBe BooleanEmpty
    identity[Option[Unit]](mapIf(BooleanEmpty, {})) shouldBe BooleanEmpty
    identity[Option[Unit]](mapIf(BooleanEmpty)({})) shouldBe BooleanEmpty
    // map 'if' with two branches
    identity[Option[Unit]](BooleanEmpty.mapIf({}, {})) shouldBe BooleanEmpty
    identity[Option[Unit]](BooleanEmpty.mapIf({} Else {})) shouldBe BooleanEmpty
    identity[Option[Unit]](mapIf(BooleanEmpty, {}, {})) shouldBe BooleanEmpty
    identity[Option[Unit]](mapIf(BooleanEmpty)({} Else {})) shouldBe BooleanEmpty
    // flatMap 'if' with one branch
    identity[Option[Unit]](BooleanEmpty.flatMapIf(pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](flatMapIf(BooleanEmpty)(pure({}))) shouldBe BooleanEmpty
    // flatMap 'if' with two branches
    identity[Option[Unit]](BooleanEmpty.flatMapIf(pure({}), pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](BooleanEmpty.flatMapIf(pure({}) Else pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](flatMapIf(BooleanEmpty, pure({}), pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](flatMapIf(BooleanEmpty)(pure({}) Else pure({}))) shouldBe BooleanEmpty


    // monadic 'if' with one branch
    identity[Option[AnyVal]](BooleanEmpty.mIf(333)) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mIf(BooleanEmpty, 333)) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mIf(BooleanEmpty)(333)) shouldBe BooleanEmpty
    // monadic 'if' with two branches
    identity[Option[AnyVal]](BooleanEmpty.mIf(333, {})) shouldBe BooleanEmpty
    identity[Option[AnyVal]](BooleanEmpty.mIf(333 Else {})) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mIf(BooleanEmpty, 333, {})) shouldBe BooleanEmpty
    identity[Option[AnyVal]](mIf(BooleanEmpty)(333 Else {})) shouldBe BooleanEmpty
    // flat monadic 'if' with one branch
    identity[Option[AnyVal]](BooleanEmpty.fmIf(pure(333))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](fmIf(BooleanEmpty)(pure(333))) shouldBe BooleanEmpty
    // flat monadic 'if' with two branches
    identity[Option[AnyVal]](BooleanEmpty.fmIf(pure(333), pure({}))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](BooleanEmpty.fmIf(pure(333) Else pure({}))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](fmIf(BooleanEmpty, pure(333), pure({}))) shouldBe BooleanEmpty
    identity[Option[AnyVal]](fmIf(BooleanEmpty)(pure(333) Else pure({}))) shouldBe BooleanEmpty

    // monadic 'if' with one branch
    identity[Option[Unit]](BooleanEmpty.mIf({})) shouldBe BooleanEmpty
    identity[Option[Unit]](mIf(BooleanEmpty, {})) shouldBe BooleanEmpty
    identity[Option[Unit]](mIf(BooleanEmpty)({})) shouldBe BooleanEmpty
    // monadic 'if' with two branches
    identity[Option[Unit]](BooleanEmpty.mIf({}, {})) shouldBe BooleanEmpty
    identity[Option[Unit]](BooleanEmpty.mIf({} Else {})) shouldBe BooleanEmpty
    identity[Option[Unit]](mIf(BooleanEmpty, {}, {})) shouldBe BooleanEmpty
    identity[Option[Unit]](mIf(BooleanEmpty)({} Else {})) shouldBe BooleanEmpty
    // flat monadic 'if' with one branch
    identity[Option[Unit]](BooleanEmpty.fmIf(pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](fmIf(BooleanEmpty)(pure({}))) shouldBe BooleanEmpty
    // flat monadic 'if' with two branches
    identity[Option[Unit]](BooleanEmpty.fmIf(pure({}), pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](BooleanEmpty.fmIf(pure({}) Else pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](fmIf(BooleanEmpty, pure({}), pure({}))) shouldBe BooleanEmpty
    identity[Option[Unit]](fmIf(BooleanEmpty)(pure({}) Else pure({}))) shouldBe BooleanEmpty
  }

  it should "iterate monadic loops" in {
    implicit val oc: OrderCollector = new OrderCollector()
    import oc.ov

    var i = 0
    val testCaseResults = List(
      // mLoop
      runBmsCase[Option, Boolean, Boolean]("mLoop with no boolean", mLoop(ov(0)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[Option, Boolean, Boolean]("mLoop with false monad", mLoop(ov(0)(PureFalse)), ov(0)(false)),
      runBmsCase[Option, Boolean, Boolean]("mLoop iterating", {i = 3; mLoop(ov(0)({i -= 1; pure(i > 0)}))}, {i = 3; do i -= 1 while (ov(0)(i > 0)); false}),
      // mWhile
      runBmsCase[Option, Boolean, Boolean]("mWhile with no boolean predicate", mWhile(ov(0)(BooleanEmpty))(ov(1)(())), ov(0)(nb)),
      runBmsCase[Option, Boolean, Boolean]("mWhile with false monad predicate", mWhile(ov(0)(PureFalse))(ov(1)(())), ov(0)(false)),
      runBmsCase[Option, Boolean, Boolean]("mWhile iterating", {i = 3; mWhile(ov(0)(pure(i > 0)))(ov(1)(i -= 1))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // fmWhile
      runBmsCase[Option, Boolean, Boolean]("fmWhile with no boolean predicate", fmWhile(ov(0)(BooleanEmpty))(ov(1)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[Option, Boolean, Boolean]("fmWhile with false monad predicate", fmWhile(ov(0)(PureFalse))(ov(1)(BooleanEmpty)), ov(0)(false)),
      runBmsCase[Option, Boolean, Boolean]("fmWhile with finalising body", fmWhile(ov(0)(PureTrue))(ov(1)(BooleanEmpty)), {ov(0)(true); ov(1)(nb)}),
      runBmsCase[Option, Boolean, Boolean]("fmWhile iterating", {i = 3; fmWhile(ov(0)(pure(i > 0)))(ov(1)(pure(i -= 1)))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // mDoWhile
      runBmsCase[Option, Boolean, Boolean]("mDoWhile with no boolean predicate", mDoWhile(ov(0)(()), ov(1)(BooleanEmpty)), {ov(0)(()); ov(1)(nb)}),
      runBmsCase[Option, Boolean, Boolean]("mDoWhile with false monad predicate", mDoWhile(ov(0)(()), ov(1)(PureFalse)), {ov(0)(()); ov(1)(false)}),
      runBmsCase[Option, Boolean, Boolean]("mDoWhile iterating", {i = 3; mDoWhile(ov(0)(i -= 1), ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      // fmDoWhile
      runBmsCase[Option, Boolean, Boolean]("fmDoWhile with finalising body", fmDoWhile(ov(0)(BooleanEmpty))(ov(1)(PureTrue)), ov(0)(nb)),
      runBmsCase[Option, Boolean, Boolean]("fmDoWhile with no boolean predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(BooleanEmpty)), {ov(0)(3); ov(1)(nb)}),
      runBmsCase[Option, Boolean, Boolean]("fmDoWhile with false monad predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(PureFalse)), {ov(0)(3); ov(1)(false)}),
      runBmsCase[Option, Boolean, Boolean]("fmDoWhile iterating", {i = 3; fmDoWhile(ov(0)(pure(i -= 1)))(ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false})
    )

    validateBmsCaseResults(testCaseResults, tryResultToTryExpected[Boolean])
  }
}
