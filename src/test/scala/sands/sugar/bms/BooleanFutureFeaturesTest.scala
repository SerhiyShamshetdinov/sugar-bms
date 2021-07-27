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

import org.scalactic.Equality
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.BooleanMonad

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/*
 * Created by Serhiy Shamshetdinov
 * at 26.08.2021 18:00
 */

class BooleanFutureFeaturesTest extends AnyFlatSpec with Matchers {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  object FutureEquality extends Equality[Future[_]] {
    def areEqual(a: Future[_], b: Any): Boolean = b match {
      case fb: Future[_] => Await.ready(a, 3.minutes).value == Await.ready(fb, 3.minutes).value
      case _ => false
    }
  }
  implicit def futureEquality[T]: Equality[Future[T]] = FutureEquality.asInstanceOf[Equality[Future[T]]]
  
  behavior of BooleanMonad[Future, ExecutionContext].name

  def mPure[A](a: A): Future[A] = Future.successful[A](a)

  val mFalse: Future[Boolean] = mPure(false)
  val mTrue:  Future[Boolean] = mPure(true)
  val mUnit:  Future[Unit] =    mPure({})

  val noBooleanException = new Exception("No Boolean")
  def mEmpty[A]: Future[A] = Future.failed[A](noBooleanException)

  val mBooleanEmpty: Future[Boolean] = mEmpty[Boolean]

  it should "have type constants" in {

    Future.False shouldEqual mFalse
    Future.True  shouldEqual mTrue
    Future.Unit  shouldEqual mUnit

    BooleanMonad[Future, ExecutionContext].False shouldEqual mFalse
    BooleanMonad[Future, ExecutionContext].True  shouldEqual mTrue
    BooleanMonad[Future, ExecutionContext].Unit  shouldEqual mUnit
  }

  /// >>> the following tests except "flat monadic 'if' // for true-branch only" for Option are copy-paste for Option, Try, Either & Future

  it should "have logical operators extensions" in {

    !mBooleanEmpty shouldEqual mBooleanEmpty
    !mFalse shouldEqual mTrue

    true  && mTrue  shouldEqual mTrue
    true  || mFalse shouldEqual mTrue
    true  &  mFalse shouldEqual mFalse
    false ^  mTrue  shouldEqual mTrue
    false |  mFalse shouldEqual mFalse

    mTrue  && true  shouldEqual mTrue
    mFalse || true  shouldEqual mTrue
    mFalse &  true  shouldEqual mFalse
    mTrue  ^  false shouldEqual mTrue
    mFalse |  false shouldEqual mFalse

    mTrue  && mTrue  shouldEqual mTrue
    mFalse || mTrue  shouldEqual mTrue
    mFalse &  mTrue  shouldEqual mFalse
    mTrue  ^  mFalse shouldEqual mTrue
    mFalse |  mFalse shouldEqual mFalse

    true  && mBooleanEmpty shouldEqual mBooleanEmpty
    true  || mBooleanEmpty shouldEqual mTrue // lazy || does not compute second operand
    true  &  mBooleanEmpty shouldEqual mBooleanEmpty
    true  ^  mBooleanEmpty shouldEqual mBooleanEmpty
    true  |  mBooleanEmpty shouldEqual mBooleanEmpty

    mFalse && mBooleanEmpty shouldEqual mFalse // lazy && does not compute second operand
    mFalse || mBooleanEmpty shouldEqual mBooleanEmpty
    mFalse &  mBooleanEmpty shouldEqual mBooleanEmpty
    mFalse ^  mBooleanEmpty shouldEqual mBooleanEmpty
    mFalse |  mBooleanEmpty shouldEqual mBooleanEmpty

    mBooleanEmpty && false shouldEqual mBooleanEmpty
    mBooleanEmpty || false shouldEqual mBooleanEmpty
    mBooleanEmpty &  false shouldEqual mBooleanEmpty
    mBooleanEmpty ^  false shouldEqual mBooleanEmpty
    mBooleanEmpty |  false shouldEqual mBooleanEmpty

    mBooleanEmpty && mTrue shouldEqual mBooleanEmpty
    mBooleanEmpty || mTrue shouldEqual mBooleanEmpty
    mBooleanEmpty &  mTrue shouldEqual mBooleanEmpty
    mBooleanEmpty ^  mTrue shouldEqual mBooleanEmpty
    mBooleanEmpty |  mTrue shouldEqual mBooleanEmpty

    true && mFalse || mTrue ^ mTrue & mFalse | false shouldEqual mTrue
  }

  it should "have monadic 'if' extensions and standalone methods" in {

    // >>> mapIf

    // true-branch only

    mTrue.mapIf(5) shouldEqual mPure[AnyVal](5)
    mFalse.mapIf(5) shouldEqual mUnit
    mBooleanEmpty.mapIf(5) shouldEqual mEmpty[AnyVal]

    mTrue.mapIf {
      5
    } shouldEqual mPure[AnyVal](5)

    mFalse.mapIf {
      5
    } shouldEqual mUnit

    mBooleanEmpty.mapIf {
      5
    } shouldEqual mEmpty[AnyVal]

    mapIf(mTrue, 5) shouldEqual mPure[AnyVal](5)
    mapIf(mFalse, 5) shouldEqual mUnit
    mapIf(mBooleanEmpty, 5) shouldEqual mEmpty[AnyVal]

    mapIf(mTrue)(5) shouldEqual mPure[AnyVal](5)
    mapIf(mFalse)(5) shouldEqual mUnit
    mapIf(mBooleanEmpty)(5) shouldEqual mEmpty[AnyVal]

    mapIf(mTrue) {
      5
    } shouldEqual mPure[AnyVal](5)

    mapIf(mFalse) {
      5
    } shouldEqual mUnit

    mapIf(mBooleanEmpty) {
      5
    } shouldEqual mEmpty[AnyVal]

    // both branches

    mTrue.mapIf(5, 0) shouldEqual mPure[Int](5)
    mFalse.mapIf(5, 0) shouldEqual mPure[Int](0)
    mBooleanEmpty.mapIf(5, 0) shouldEqual mEmpty[Int]

    mTrue.mapIf(5 Else 0) shouldEqual mPure[Int](5)
    mFalse.mapIf(5 Else 0) shouldEqual mPure[Int](0)
    mBooleanEmpty.mapIf(5 Else 0) shouldEqual mEmpty[Int]

    mTrue.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](5)

    mFalse.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](0)

    mBooleanEmpty.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mEmpty[Int]

    mapIf(mTrue, 5, 0) shouldEqual mPure[Int](5)
    mapIf(mFalse, 5, 0) shouldEqual mPure[Int](0)
    mapIf(mBooleanEmpty, 5, 0) shouldEqual mEmpty[Int]

    mapIf(mTrue)(5 Else 0) shouldEqual mPure[Int](5)
    mapIf(mFalse)(5 Else 0) shouldEqual mPure[Int](0)
    mapIf(mBooleanEmpty)(5 Else 0) shouldEqual mEmpty[Int]

    mapIf(mTrue)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](5)

    mapIf(mFalse)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](0)

    mapIf(mBooleanEmpty)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mEmpty[Int]

    // >>> mIf : the same results (difference between mapIf & mIf only exists in condition interpretation for collections)

    // true-branch only

    mTrue.mIf(5) shouldEqual mPure[AnyVal](5)
    mFalse.mIf(5) shouldEqual mUnit
    mBooleanEmpty.mIf(5) shouldEqual mEmpty[AnyVal]

    mTrue.mIf {
      5
    } shouldEqual mPure[AnyVal](5)

    mFalse.mIf {
      5
    } shouldEqual mUnit

    mBooleanEmpty.mIf {
      5
    } shouldEqual mEmpty[AnyVal]

    mIf(mTrue, 5) shouldEqual mPure[AnyVal](5)
    mIf(mFalse, 5) shouldEqual mUnit
    mIf(mBooleanEmpty, 5) shouldEqual mEmpty[AnyVal]

    mIf(mTrue)(5) shouldEqual mPure[AnyVal](5)
    mIf(mFalse)(5) shouldEqual mUnit
    mIf(mBooleanEmpty)(5) shouldEqual mEmpty[AnyVal]

    mIf(mTrue) {
      5
    } shouldEqual mPure[AnyVal](5)

    mIf(mFalse) {
      5
    } shouldEqual mUnit

    mIf(mBooleanEmpty) {
      5
    } shouldEqual mEmpty[AnyVal]

    // both branches

    mTrue.mIf(5, 0) shouldEqual mPure[Int](5)
    mFalse.mIf(5, 0) shouldEqual mPure[Int](0)
    mBooleanEmpty.mIf(5, 0) shouldEqual mEmpty[Int]

    mTrue.mIf(5 Else 0) shouldEqual mPure[Int](5)
    mFalse.mIf(5 Else 0) shouldEqual mPure[Int](0)
    mBooleanEmpty.mIf(5 Else 0) shouldEqual mEmpty[Int]

    mTrue.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](5)

    mFalse.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](0)

    mBooleanEmpty.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mEmpty[Int]

    mIf(mTrue, 5, 0) shouldEqual mPure[Int](5)
    mIf(mFalse, 5, 0) shouldEqual mPure[Int](0)
    mIf(mBooleanEmpty, 5, 0) shouldEqual mEmpty[Int]

    mIf(mTrue)(5 Else 0) shouldEqual mPure[Int](5)
    mIf(mFalse)(5 Else 0) shouldEqual mPure[Int](0)
    mIf(mBooleanEmpty)(5 Else 0) shouldEqual mEmpty[Int]

    mIf(mTrue)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](5)

    mIf(mFalse)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mPure[Int](0)

    mIf(mBooleanEmpty)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldEqual mEmpty[Int]
  }
  
  it should "have flat monadic 'if' extensions and standalone methods" in {

    // >>> flatMapIf

    // true-branch only

    /// >>> the only difference of "flatMapIf and fmIf true-branch only" tests between Either, Try & Future and Option is:
    // Option uses None in place of absent false-branch but Either, Try & Future use Unit = pure({})
    
    mTrue.flatMapIf(mPure(5)) shouldEqual mPure[Int](5)
    mFalse.flatMapIf(mPure(5)) shouldEqual mUnit
    mBooleanEmpty.flatMapIf(mPure(5)) shouldEqual mEmpty[Int]

    mTrue.flatMapIf {
      mPure(5)
    } shouldEqual mPure[Int](5)

    mFalse.flatMapIf {
      mPure(5)
    } shouldEqual mUnit

    mBooleanEmpty.flatMapIf {
      mPure(5)
    } shouldEqual mEmpty[Int]

    flatMapIf(mTrue)(mPure(5)) shouldEqual mPure[Int](5)
    flatMapIf(mFalse)(mPure(5)) shouldEqual mUnit
    flatMapIf(mBooleanEmpty)(mPure(5)) shouldEqual mEmpty[Int]

    flatMapIf(mTrue) {
      mPure(5)
    } shouldEqual mPure[Int](5)

    flatMapIf(mFalse) {
      mPure(5)
    } shouldEqual mUnit

    flatMapIf(mBooleanEmpty) {
      mPure(5)
    } shouldEqual mEmpty[Int]

    // both branches

    mTrue.flatMapIf(mPure(5), mPure(0)) shouldEqual mPure[Int](5)
    mFalse.flatMapIf(mPure(5), mPure(0)) shouldEqual mPure[Int](0)
    mBooleanEmpty.flatMapIf(mPure(5), mPure(0)) shouldEqual mEmpty[Int]

    mTrue.flatMapIf(mPure(5) Else mPure(0)) shouldEqual mPure[Int](5)
    mFalse.flatMapIf(mPure(5) Else mPure(0)) shouldEqual mPure[Int](0)
    mBooleanEmpty.flatMapIf(mPure(5) Else mPure(0)) shouldEqual mEmpty[Int]

    mTrue.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](5)

    mFalse.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](0)

    mBooleanEmpty.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mEmpty[Int]

    flatMapIf(mTrue, mPure(5), mPure(0)) shouldEqual mPure[Int](5)
    flatMapIf(mFalse, mPure(5), mPure(0)) shouldEqual mPure[Int](0)
    flatMapIf(mBooleanEmpty, mPure(5), mPure(0)) shouldEqual mEmpty[Int]

    flatMapIf(mTrue)(mPure(5) Else mPure(0)) shouldEqual mPure[Int](5)
    flatMapIf(mFalse)(mPure(5) Else mPure(0)) shouldEqual mPure[Int](0)
    flatMapIf(mBooleanEmpty)(mPure(5) Else mPure(0)) shouldEqual mEmpty[Int]

    flatMapIf(mTrue)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](5)

    flatMapIf(mFalse)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](0)

    flatMapIf(mBooleanEmpty)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mEmpty[Int]

    // >>> fmIf : the same results (difference between flatMapIf & fmIf only exists in condition interpretation for collections)

    // true-branch only

    /// >>> the only difference of "flatMapIf and fmIf true-branch only" tests between Either, Try & Future and Option is:
    // Option uses None in place of absent false-branch but Either, Try & Future use Unit = pure({})

    mTrue.fmIf(mPure(5)) shouldEqual mPure[Int](5)
    mFalse.fmIf(mPure(5)) shouldEqual mUnit
    mBooleanEmpty.fmIf(mPure(5)) shouldEqual mEmpty[Int]

    mTrue.fmIf {
      mPure(5)
    } shouldEqual mPure[Int](5)

    mFalse.fmIf {
      mPure(5)
    } shouldEqual mUnit

    mBooleanEmpty.fmIf {
      mPure(5)
    } shouldEqual mEmpty[Int]

    fmIf(mTrue)(mPure(5)) shouldEqual mPure[Int](5)
    fmIf(mFalse)(mPure(5)) shouldEqual mUnit
    fmIf(mBooleanEmpty)(mPure(5)) shouldEqual mEmpty[Int]

    fmIf(mTrue) {
      mPure(5)
    } shouldEqual mPure[Int](5)

    fmIf(mFalse) {
      mPure(5)
    } shouldEqual mUnit

    fmIf(mBooleanEmpty) {
      mPure(5)
    } shouldEqual mEmpty[Int]

    // both branches

    mTrue.fmIf(mPure(5), mPure(0)) shouldEqual mPure[Int](5)
    mFalse.fmIf(mPure(5), mPure(0)) shouldEqual mPure[Int](0)
    mBooleanEmpty.fmIf(mPure(5), mPure(0)) shouldEqual mEmpty[Int]

    mTrue.fmIf(mPure(5) Else mPure(0)) shouldEqual mPure[Int](5)
    mFalse.fmIf(mPure(5) Else mPure(0)) shouldEqual mPure[Int](0)
    mBooleanEmpty.fmIf(mPure(5) Else mPure(0)) shouldEqual mEmpty[Int]

    mTrue.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](5)

    mFalse.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](0)

    mBooleanEmpty.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mEmpty[Int]

    fmIf(mTrue, mPure(5), mPure(0)) shouldEqual mPure[Int](5)
    fmIf(mFalse, mPure(5), mPure(0)) shouldEqual mPure[Int](0)
    fmIf(mBooleanEmpty, mPure(5), mPure(0)) shouldEqual mEmpty[Int]

    fmIf(mTrue)(mPure(5) Else mPure(0)) shouldEqual mPure[Int](5)
    fmIf(mFalse)(mPure(5) Else mPure(0)) shouldEqual mPure[Int](0)
    fmIf(mBooleanEmpty)(mPure(5) Else mPure(0)) shouldEqual mEmpty[Int]

    fmIf(mTrue)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](5)

    fmIf(mFalse)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mPure[Int](0)

    fmIf(mBooleanEmpty)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldEqual mEmpty[Int]
  }

  it should "have monadic loops methods" in {
    var bodyCycles = 0
    var i = 0
    def mDecrementedCounterGEZ() = {
      i -= 1
      mPure(i >= 0)
    }


    // >>> tailRec mLoop (it is the base for all other loop variants)

    mLoop(mBooleanEmpty) shouldEqual mBooleanEmpty // empty condition result also stops cycling

    mLoop(mFalse) shouldEqual mFalse

    i = 3; bodyCycles = 0
    mLoop {
      bodyCycles += 1
      mDecrementedCounterGEZ()
    } shouldEqual mFalse
    bodyCycles shouldEqual 4


    // >>> tailRec mWhile

    bodyCycles = 0
    mWhile(mBooleanEmpty) { // empty condition result also stops cycling
      bodyCycles += 1
    } shouldEqual mBooleanEmpty
    bodyCycles shouldEqual 0

    bodyCycles = 0
    mWhile(mFalse) {
      bodyCycles += 1
    } shouldEqual mFalse
    bodyCycles shouldEqual 0

    i = 3; bodyCycles = 0
    mWhile(mDecrementedCounterGEZ()) {
      bodyCycles += 1
    } shouldEqual mFalse
    bodyCycles shouldEqual 3


    // >>> tailRec fmWhile

    bodyCycles = 0
    fmWhile(mBooleanEmpty) { // empty condition result also stops cycling
      mPure(bodyCycles += 1)
    } shouldEqual mBooleanEmpty
    bodyCycles shouldEqual 0

    bodyCycles = 0
    fmWhile(mFalse) {
      mPure(bodyCycles += 1)
    } shouldEqual mFalse
    bodyCycles shouldEqual 0

    bodyCycles = 0
    fmWhile(mTrue) {
      bodyCycles += 1
      mEmpty[Any] // empty body result also stops cycling
    } shouldEqual mEmpty[Any]
    bodyCycles shouldEqual 1

    i = 3; bodyCycles = 0
    fmWhile(mDecrementedCounterGEZ()) {
      mPure(bodyCycles += 1)
    } shouldEqual mFalse
    bodyCycles shouldEqual 3


    // >>> tailRec mDoWhile

    bodyCycles = 0
    // one group of 2 parameters only variant. Scala 2 may distinguish overloads only on the 1st group of parameters
    mDoWhile({ // empty condition result also stops cycling
      bodyCycles += 1
    }, mBooleanEmpty) shouldEqual mBooleanEmpty
    bodyCycles shouldEqual 1

    bodyCycles = 0
    mDoWhile({
      bodyCycles += 1
    }, mFalse) shouldEqual mFalse
    bodyCycles shouldEqual 1

    i = 3; bodyCycles = 0
    mDoWhile({
      bodyCycles += 1
    }, mDecrementedCounterGEZ()) shouldEqual mFalse

    bodyCycles shouldEqual 4


    // >>> tailRec fmDoWhile

    bodyCycles = 0
    fmDoWhile { // empty condition result also stops cycling
      mPure(bodyCycles += 1)
    } (mBooleanEmpty) shouldEqual mBooleanEmpty
    bodyCycles shouldEqual 1

    bodyCycles = 0
    fmDoWhile {
      mPure(bodyCycles += 1)
    } (mFalse) shouldEqual mFalse
    bodyCycles shouldEqual 1

    bodyCycles = 0
    fmDoWhile {
      bodyCycles += 1
      mEmpty[Any] // empty body result also stops cycling
    } (mTrue) shouldEqual mEmpty[Any]
    bodyCycles shouldEqual 1

    i = 3; bodyCycles = 0
    fmDoWhile {
      mPure(bodyCycles += 1)
    } (mDecrementedCounterGEZ()) shouldEqual mFalse

    bodyCycles shouldEqual 4
  }
}
