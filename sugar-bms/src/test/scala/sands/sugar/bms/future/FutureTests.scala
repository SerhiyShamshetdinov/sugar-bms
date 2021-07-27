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

package sands.sugar.bms.future

import org.scalactic.Equality
import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.future.FutureTestData._
import sands.sugar.bms.typeclass.BooleanMonad
import sands.sugar.bms.typeclass.BooleanMonad._
import sands.sugar.bms.{ElseExtension => _, _}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class FutureIfStatementsTest extends BmsMonadIfStatementsTest(BooleanMonadsImport, FutureTestData)
class FutureBinaryExpressionsTest extends BmsMonadBinaryExpressionsTest(BooleanMonadsImport, FutureTestData)
class FutureTernaryExpressionsTest extends BmsMonadTernaryExpressionsTest(BooleanMonadsImport, FutureTestData)
class FutureQuaternaryExpressionsPage1Test extends BmsMonadQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, FutureTestData)
class FutureQuaternaryExpressionsPage2Test extends BmsMonadQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, FutureTestData)
class FutureQuaternaryExpressionsPage3Test extends BmsMonadQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, FutureTestData)
class FutureQuaternaryExpressionsPage4Test extends BmsMonadQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, FutureTestData)
class FutureQuaternaryExpressionsPage5Test extends BmsMonadQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, FutureTestData)

class FutureGeneralTest extends BmsToolBoxTestBase {

  object FutureEquality extends Equality[Future[_]] {
    def areEqual(a: Future[_], b: Any): Boolean = b match {
      case fb: Future[_] => Await.ready(a, 3.minutes).value == Await.ready(fb, 3.minutes).value
      case _ => false
    }
  }
  implicit def futureEquality[T]: Equality[Future[T]] = FutureEquality.asInstanceOf[Equality[Future[T]]]

  behavior of s"Boolean $MonadName"

  it should "return constants" in {
    False[Future, ExecutionContext] shouldBe futureBooleanMonad.False
    True[Future, ExecutionContext] shouldBe futureBooleanMonad.True
    Unit[Future, ExecutionContext] shouldBe futureBooleanMonad.Unit

    BooleanMonad[Future, ExecutionContext].False shouldBe futureBooleanMonad.False
    BooleanMonad[Future, ExecutionContext].True shouldBe futureBooleanMonad.True
    BooleanMonad[Future, ExecutionContext].Unit shouldBe futureBooleanMonad.Unit

    Future.False shouldBe futureBooleanMonad.False
    Future.True shouldBe futureBooleanMonad.True
    Future.Unit shouldBe futureBooleanMonad.Unit
  }

  it should "invert values" in {
    !Future.False shouldEqual Future.True
    !Future.True shouldEqual Future.False
    !BooleanEmpty shouldEqual BooleanEmpty
  }

  it should "use Unit in place of absent monadic else branch" in {
    PureFalse.mapIf(333) shouldEqual Future.Unit
    mapIf(PureFalse, 333) shouldEqual Future.Unit
    mapIf(PureFalse)(333) shouldEqual Future.Unit

    PureFalse.mIf(333) shouldEqual Future.Unit
    mIf(PureFalse, 333) shouldEqual Future.Unit
    mIf(PureFalse)(333) shouldEqual Future.Unit
  }

  it should "use Future[Unit] in place of absent flat monadic else branch" in {
    PureFalse.flatMapIf(pure(333)) shouldEqual Future.Unit
    flatMapIf(PureFalse)(pure(333)) shouldEqual Future.Unit

    PureFalse.fmIf(pure(333)) shouldEqual Future.Unit
    fmIf(PureFalse)(pure(333)) shouldEqual Future.Unit
  }

  it should "return 'if' results with nearest common base type" in {
    // this is mostly compilation test, type of the value compared to is not important

    // map 'if' with one branch
    identity[Future[AnyVal]](BooleanEmpty.mapIf(333)) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mapIf(BooleanEmpty, 333)) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mapIf(BooleanEmpty)(333)) shouldEqual BooleanEmpty
    // map 'if' with two branches
    identity[Future[AnyVal]](BooleanEmpty.mapIf(333, {})) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](BooleanEmpty.mapIf(333 Else {})) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mapIf(BooleanEmpty, 333, {})) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mapIf(BooleanEmpty)(333 Else {})) shouldEqual BooleanEmpty
    // flatMap 'if' with one branch
    identity[Future[AnyVal]](BooleanEmpty.flatMapIf(pure(333))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](flatMapIf(BooleanEmpty)(pure(333))) shouldEqual BooleanEmpty
    // flatMap 'if' with two branches
    identity[Future[AnyVal]](BooleanEmpty.flatMapIf(pure(333), pure({}))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](BooleanEmpty.flatMapIf(pure(333) Else pure({}))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](flatMapIf(BooleanEmpty, pure(333), pure({}))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](flatMapIf(BooleanEmpty)(pure(333) Else pure({}))) shouldEqual BooleanEmpty

    // map 'if' with one branch
    identity[Future[Unit]](BooleanEmpty.mapIf({})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mapIf(BooleanEmpty, {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mapIf(BooleanEmpty)({})) shouldEqual BooleanEmpty
    // map 'if' with two branches
    identity[Future[Unit]](BooleanEmpty.mapIf({}, {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](BooleanEmpty.mapIf({} Else {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mapIf(BooleanEmpty, {}, {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mapIf(BooleanEmpty)({} Else {})) shouldEqual BooleanEmpty
    // flatMap 'if' with one branch
    identity[Future[Unit]](BooleanEmpty.flatMapIf(pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](flatMapIf(BooleanEmpty)(pure({}))) shouldEqual BooleanEmpty
    // flatMap 'if' with two branches
    identity[Future[Unit]](BooleanEmpty.flatMapIf(pure({}), pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](BooleanEmpty.flatMapIf(pure({}) Else pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](flatMapIf(BooleanEmpty, pure({}), pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](flatMapIf(BooleanEmpty)(pure({}) Else pure({}))) shouldEqual BooleanEmpty


    // monadic 'if' with one branch
    identity[Future[AnyVal]](BooleanEmpty.mIf(333)) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mIf(BooleanEmpty, 333)) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mIf(BooleanEmpty)(333)) shouldEqual BooleanEmpty
    // monadic 'if' with two branches
    identity[Future[AnyVal]](BooleanEmpty.mIf(333, {})) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](BooleanEmpty.mIf(333 Else {})) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mIf(BooleanEmpty, 333, {})) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](mIf(BooleanEmpty)(333 Else {})) shouldEqual BooleanEmpty
    // flat monadic 'if' with one branch
    identity[Future[AnyVal]](BooleanEmpty.fmIf(pure(333))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](fmIf(BooleanEmpty)(pure(333))) shouldEqual BooleanEmpty
    // flat monadic 'if' with two branches
    identity[Future[AnyVal]](BooleanEmpty.fmIf(pure(333), pure({}))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](BooleanEmpty.fmIf(pure(333) Else pure({}))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](fmIf(BooleanEmpty, pure(333), pure({}))) shouldEqual BooleanEmpty
    identity[Future[AnyVal]](fmIf(BooleanEmpty)(pure(333) Else pure({}))) shouldEqual BooleanEmpty

    // monadic 'if' with one branch
    identity[Future[Unit]](BooleanEmpty.mIf({})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mIf(BooleanEmpty, {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mIf(BooleanEmpty)({})) shouldEqual BooleanEmpty
    // monadic 'if' with two branches
    identity[Future[Unit]](BooleanEmpty.mIf({}, {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](BooleanEmpty.mIf({} Else {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mIf(BooleanEmpty, {}, {})) shouldEqual BooleanEmpty
    identity[Future[Unit]](mIf(BooleanEmpty)({} Else {})) shouldEqual BooleanEmpty
    // flat monadic 'if' with one branch
    identity[Future[Unit]](BooleanEmpty.fmIf(pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](fmIf(BooleanEmpty)(pure({}))) shouldEqual BooleanEmpty
    // flat monadic 'if' with two branches
    identity[Future[Unit]](BooleanEmpty.fmIf(pure({}), pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](BooleanEmpty.fmIf(pure({}) Else pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](fmIf(BooleanEmpty, pure({}), pure({}))) shouldEqual BooleanEmpty
    identity[Future[Unit]](fmIf(BooleanEmpty)(pure({}) Else pure({}))) shouldEqual BooleanEmpty
  }

  it should "iterate monadic loops" in {
    implicit val oc: OrderCollector = new OrderCollector()
    import oc.ov

    var i = 0
    val testCaseResults = List(
      // mLoop
      runBmsCase[Future, Boolean, Boolean]("mLoop with no boolean", mLoop(ov(0)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[Future, Boolean, Boolean]("mLoop with false monad", mLoop(ov(0)(PureFalse)), ov(0)(false)),
      runBmsCase[Future, Boolean, Boolean]("mLoop iterating", {i = 3; mLoop(ov(0)({i -= 1; pure(i > 0)}))}, {i = 3; do i -= 1 while (ov(0)(i > 0)); false}),
      // mWhile
      runBmsCase[Future, Boolean, Boolean]("mWhile with no boolean predicate", mWhile(ov(0)(BooleanEmpty))(ov(1)(())), ov(0)(nb)),
      runBmsCase[Future, Boolean, Boolean]("mWhile with false monad predicate", mWhile(ov(0)(PureFalse))(ov(1)(())), ov(0)(false)),
      runBmsCase[Future, Boolean, Boolean]("mWhile iterating", {i = 3; mWhile(ov(0)(pure(i > 0)))(ov(1)(i -= 1))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // fmWhile
      runBmsCase[Future, Boolean, Boolean]("fmWhile with no boolean predicate", fmWhile(ov(0)(BooleanEmpty))(ov(1)(BooleanEmpty)), ov(0)(nb)),
      runBmsCase[Future, Boolean, Boolean]("fmWhile with false monad predicate", fmWhile(ov(0)(PureFalse))(ov(1)(BooleanEmpty)), ov(0)(false)),
      runBmsCase[Future, Boolean, Boolean]("fmWhile with finalising body", fmWhile(ov(0)(PureTrue))(ov(1)(BooleanEmpty)), {ov(0)(true); ov(1)(nb)}),
      runBmsCase[Future, Boolean, Boolean]("fmWhile iterating", {i = 3; fmWhile(ov(0)(pure(i > 0)))(ov(1)(pure(i -= 1)))}, {i = 3; while (ov(0)(i > 0)) ov(1)(i -= 1); false}),
      // mDoWhile
      runBmsCase[Future, Boolean, Boolean]("mDoWhile with no boolean predicate", mDoWhile(ov(0)(()), ov(1)(BooleanEmpty)), {ov(0)(()); ov(1)(nb)}),
      runBmsCase[Future, Boolean, Boolean]("mDoWhile with false monad predicate", mDoWhile(ov(0)(()), ov(1)(PureFalse)), {ov(0)(()); ov(1)(false)}),
      runBmsCase[Future, Boolean, Boolean]("mDoWhile iterating", {i = 3; mDoWhile(ov(0)(i -= 1), ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false}),
      // fmDoWhile
      runBmsCase[Future, Boolean, Boolean]("fmDoWhile with finalising body", fmDoWhile(ov(0)(BooleanEmpty))(ov(1)(PureTrue)), ov(0)(nb)),
      runBmsCase[Future, Boolean, Boolean]("fmDoWhile with no boolean predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(BooleanEmpty)), {ov(0)(3); ov(1)(nb)}),
      runBmsCase[Future, Boolean, Boolean]("fmDoWhile with false monad predicate", fmDoWhile(ov(0)(pure(3)))(ov(1)(PureFalse)), {ov(0)(3); ov(1)(false)}),
      runBmsCase[Future, Boolean, Boolean]("fmDoWhile iterating", {i = 3; fmDoWhile(ov(0)(pure(i -= 1)))(ov(1)(pure(i > 0)))}, {i = 3; do ov(0)(i -= 1) while (ov(1)(i > 0)); false})
    )

    validateBmsCaseResults(testCaseResults, tryResultToTryExpected[Boolean])
  }
}
