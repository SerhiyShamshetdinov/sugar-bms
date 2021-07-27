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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sands.sugar.bms.BooleanMonads._

/*
 * Created by Serhiy Shamshetdinov
 * at 25.08.2021 18:00
 */

class BooleanOptionFeaturesTest extends AnyFlatSpec with Matchers {

  behavior of "Boolean Option"

  def mPure[A](a: A): Some[A] = Some(a)

  val mFalse: Some[Boolean] = mPure(false)
  val mTrue:  Some[Boolean] = mPure(true)
  val mUnit:  Some[Unit] =    mPure({})

  def mEmpty[A]: Option[A] = None

  val mBooleanEmpty: Option[Boolean] = mEmpty[Boolean]

  it should "have type constants" in {

    Option.False shouldBe mFalse
    Option.True  shouldBe mTrue
    Option.Unit  shouldBe mUnit
  }

  /// >>> the following tests except "flat monadic 'if' // for true-branch only" for Option are copy-paste for Option, Try, Either & Future

  it should "have logical operators extensions" in {

    !mBooleanEmpty shouldBe mBooleanEmpty
    !mFalse shouldBe mTrue

    true  && mTrue  shouldBe mTrue
    true  || mFalse shouldBe mTrue
    true  &  mFalse shouldBe mFalse
    false ^  mTrue  shouldBe mTrue
    false |  mFalse shouldBe mFalse

    mTrue  && true  shouldBe mTrue
    mFalse || true  shouldBe mTrue
    mFalse &  true  shouldBe mFalse
    mTrue  ^  false shouldBe mTrue
    mFalse |  false shouldBe mFalse

    mTrue  && mTrue  shouldBe mTrue
    mFalse || mTrue  shouldBe mTrue
    mFalse &  mTrue  shouldBe mFalse
    mTrue  ^  mFalse shouldBe mTrue
    mFalse |  mFalse shouldBe mFalse

    true  && mBooleanEmpty shouldBe mBooleanEmpty
    true  || mBooleanEmpty shouldBe mTrue // lazy || does not compute second operand
    true  &  mBooleanEmpty shouldBe mBooleanEmpty
    true  ^  mBooleanEmpty shouldBe mBooleanEmpty
    true  |  mBooleanEmpty shouldBe mBooleanEmpty

    mFalse && mBooleanEmpty shouldBe mFalse // lazy && does not compute second operand
    mFalse || mBooleanEmpty shouldBe mBooleanEmpty
    mFalse &  mBooleanEmpty shouldBe mBooleanEmpty
    mFalse ^  mBooleanEmpty shouldBe mBooleanEmpty
    mFalse |  mBooleanEmpty shouldBe mBooleanEmpty

    mBooleanEmpty && false shouldBe mBooleanEmpty
    mBooleanEmpty || false shouldBe mBooleanEmpty
    mBooleanEmpty &  false shouldBe mBooleanEmpty
    mBooleanEmpty ^  false shouldBe mBooleanEmpty
    mBooleanEmpty |  false shouldBe mBooleanEmpty

    mBooleanEmpty && mTrue shouldBe mBooleanEmpty
    mBooleanEmpty || mTrue shouldBe mBooleanEmpty
    mBooleanEmpty &  mTrue shouldBe mBooleanEmpty
    mBooleanEmpty ^  mTrue shouldBe mBooleanEmpty
    mBooleanEmpty |  mTrue shouldBe mBooleanEmpty

    true && mFalse || mTrue ^ mTrue & mFalse | false shouldBe mTrue
  }

  it should "have monadic 'if' extensions and standalone methods" in {

    // >>> mapIf

    // true-branch only

    mTrue.mapIf(5) shouldBe mPure[AnyVal](5)
    mFalse.mapIf(5) shouldBe mUnit
    mBooleanEmpty.mapIf(5) shouldBe mEmpty[AnyVal]

    mTrue.mapIf {
      5
    } shouldBe mPure[AnyVal](5)

    mFalse.mapIf {
      5
    } shouldBe mUnit

    mBooleanEmpty.mapIf {
      5
    } shouldBe mEmpty[AnyVal]

    mapIf(mTrue, 5) shouldBe mPure[AnyVal](5)
    mapIf(mFalse, 5) shouldBe mUnit
    mapIf(mBooleanEmpty, 5) shouldBe mEmpty[AnyVal]

    mapIf(mTrue)(5) shouldBe mPure[AnyVal](5)
    mapIf(mFalse)(5) shouldBe mUnit
    mapIf(mBooleanEmpty)(5) shouldBe mEmpty[AnyVal]

    mapIf(mTrue) {
      5
    } shouldBe mPure[AnyVal](5)

    mapIf(mFalse) {
      5
    } shouldBe mUnit

    mapIf(mBooleanEmpty) {
      5
    } shouldBe mEmpty[AnyVal]

    // both branches

    mTrue.mapIf(5, 0) shouldBe mPure[Int](5)
    mFalse.mapIf(5, 0) shouldBe mPure[Int](0)
    mBooleanEmpty.mapIf(5, 0) shouldBe mEmpty[Int]

    mTrue.mapIf(5 Else 0) shouldBe mPure[Int](5)
    mFalse.mapIf(5 Else 0) shouldBe mPure[Int](0)
    mBooleanEmpty.mapIf(5 Else 0) shouldBe mEmpty[Int]

    mTrue.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](5)

    mFalse.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0)

    mBooleanEmpty.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]

    mapIf(mTrue, 5, 0) shouldBe mPure[Int](5)
    mapIf(mFalse, 5, 0) shouldBe mPure[Int](0)
    mapIf(mBooleanEmpty, 5, 0) shouldBe mEmpty[Int]

    mapIf(mTrue)(5 Else 0) shouldBe mPure[Int](5)
    mapIf(mFalse)(5 Else 0) shouldBe mPure[Int](0)
    mapIf(mBooleanEmpty)(5 Else 0) shouldBe mEmpty[Int]

    mapIf(mTrue)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](5)

    mapIf(mFalse)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0)

    mapIf(mBooleanEmpty)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]

    // >>> mIf : the same results (difference between mapIf & mIf only exists in condition interpretation for collections)

    // true-branch only

    mTrue.mIf(5) shouldBe mPure[AnyVal](5)
    mFalse.mIf(5) shouldBe mUnit
    mBooleanEmpty.mIf(5) shouldBe mEmpty[AnyVal]

    mTrue.mIf {
      5
    } shouldBe mPure[AnyVal](5)

    mFalse.mIf {
      5
    } shouldBe mUnit

    mBooleanEmpty.mIf {
      5
    } shouldBe mEmpty[AnyVal]

    mIf(mTrue, 5) shouldBe mPure[AnyVal](5)
    mIf(mFalse, 5) shouldBe mUnit
    mIf(mBooleanEmpty, 5) shouldBe mEmpty[AnyVal]

    mIf(mTrue)(5) shouldBe mPure[AnyVal](5)
    mIf(mFalse)(5) shouldBe mUnit
    mIf(mBooleanEmpty)(5) shouldBe mEmpty[AnyVal]

    mIf(mTrue) {
      5
    } shouldBe mPure[AnyVal](5)

    mIf(mFalse) {
      5
    } shouldBe mUnit

    mIf(mBooleanEmpty) {
      5
    } shouldBe mEmpty[AnyVal]

    // both branches

    mTrue.mIf(5, 0) shouldBe mPure[Int](5)
    mFalse.mIf(5, 0) shouldBe mPure[Int](0)
    mBooleanEmpty.mIf(5, 0) shouldBe mEmpty[Int]

    mTrue.mIf(5 Else 0) shouldBe mPure[Int](5)
    mFalse.mIf(5 Else 0) shouldBe mPure[Int](0)
    mBooleanEmpty.mIf(5 Else 0) shouldBe mEmpty[Int]

    mTrue.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](5)

    mFalse.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0)

    mBooleanEmpty.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]

    mIf(mTrue, 5, 0) shouldBe mPure[Int](5)
    mIf(mFalse, 5, 0) shouldBe mPure[Int](0)
    mIf(mBooleanEmpty, 5, 0) shouldBe mEmpty[Int]

    mIf(mTrue)(5 Else 0) shouldBe mPure[Int](5)
    mIf(mFalse)(5 Else 0) shouldBe mPure[Int](0)
    mIf(mBooleanEmpty)(5 Else 0) shouldBe mEmpty[Int]

    mIf(mTrue)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](5)

    mIf(mFalse)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0)

    mIf(mBooleanEmpty)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]
  }
  
  it should "have flat monadic 'if' extensions and standalone methods" in {

    // >>> flatMapIf

    // true-branch only

    /// >>> the only difference of "flatMapIf and fmIf true-branch only" tests between Either, Try & Future and Option is:
    // Option uses None in place of absent false-branch but Either, Try & Future use Unit = pure({})
    
    mTrue.flatMapIf(mPure(5)) shouldBe mPure[Int](5)
    mFalse.flatMapIf(mPure(5)) shouldBe mEmpty[Int]
    mBooleanEmpty.flatMapIf(mPure(5)) shouldBe mEmpty[Int]

    mTrue.flatMapIf {
      mPure(5)
    } shouldBe mPure[Int](5)

    mFalse.flatMapIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    mBooleanEmpty.flatMapIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    flatMapIf(mTrue)(mPure(5)) shouldBe mPure[Int](5)
    flatMapIf(mFalse)(mPure(5)) shouldBe mEmpty[Int]
    flatMapIf(mBooleanEmpty)(mPure(5)) shouldBe mEmpty[Int]

    flatMapIf(mTrue) {
      mPure(5)
    } shouldBe mPure[Int](5)

    flatMapIf(mFalse) {
      mPure(5)
    } shouldBe mEmpty[Int]

    flatMapIf(mBooleanEmpty) {
      mPure(5)
    } shouldBe mEmpty[Int]

    // both branches

    mTrue.flatMapIf(mPure(5), mPure(0)) shouldBe mPure[Int](5)
    mFalse.flatMapIf(mPure(5), mPure(0)) shouldBe mPure[Int](0)
    mBooleanEmpty.flatMapIf(mPure(5), mPure(0)) shouldBe mEmpty[Int]

    mTrue.flatMapIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    mFalse.flatMapIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](0)
    mBooleanEmpty.flatMapIf(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    mTrue.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    mFalse.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](0)

    mBooleanEmpty.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mEmpty[Int]

    flatMapIf(mTrue, mPure(5), mPure(0)) shouldBe mPure[Int](5)
    flatMapIf(mFalse, mPure(5), mPure(0)) shouldBe mPure[Int](0)
    flatMapIf(mBooleanEmpty, mPure(5), mPure(0)) shouldBe mEmpty[Int]

    flatMapIf(mTrue)(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    flatMapIf(mFalse)(mPure(5) Else mPure(0)) shouldBe mPure[Int](0)
    flatMapIf(mBooleanEmpty)(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    flatMapIf(mTrue)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    flatMapIf(mFalse)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](0)

    flatMapIf(mBooleanEmpty)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mEmpty[Int]

    // >>> fmIf : the same results (difference between flatMapIf & fmIf only exists in condition interpretation for collections)

    // true-branch only

    /// >>> the only difference of "flatMapIf and fmIf true-branch only" tests between Either, Try & Future and Option is:
    // Option uses None in place of absent false-branch but Either, Try & Future use Unit = pure({})

    mTrue.fmIf(mPure(5)) shouldBe mPure[Int](5)
    mFalse.fmIf(mPure(5)) shouldBe mEmpty[Int]
    mBooleanEmpty.fmIf(mPure(5)) shouldBe mEmpty[Int]

    mTrue.fmIf {
      mPure(5)
    } shouldBe mPure[Int](5)

    mFalse.fmIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    mBooleanEmpty.fmIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    fmIf(mTrue)(mPure(5)) shouldBe mPure[Int](5)
    fmIf(mFalse)(mPure(5)) shouldBe mEmpty[Int]
    fmIf(mBooleanEmpty)(mPure(5)) shouldBe mEmpty[Int]

    fmIf(mTrue) {
      mPure(5)
    } shouldBe mPure[Int](5)

    fmIf(mFalse) {
      mPure(5)
    } shouldBe mEmpty[Int]

    fmIf(mBooleanEmpty) {
      mPure(5)
    } shouldBe mEmpty[Int]

    // both branches

    mTrue.fmIf(mPure(5), mPure(0)) shouldBe mPure[Int](5)
    mFalse.fmIf(mPure(5), mPure(0)) shouldBe mPure[Int](0)
    mBooleanEmpty.fmIf(mPure(5), mPure(0)) shouldBe mEmpty[Int]

    mTrue.fmIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    mFalse.fmIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](0)
    mBooleanEmpty.fmIf(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    mTrue.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    mFalse.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](0)

    mBooleanEmpty.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mEmpty[Int]

    fmIf(mTrue, mPure(5), mPure(0)) shouldBe mPure[Int](5)
    fmIf(mFalse, mPure(5), mPure(0)) shouldBe mPure[Int](0)
    fmIf(mBooleanEmpty, mPure(5), mPure(0)) shouldBe mEmpty[Int]

    fmIf(mTrue)(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    fmIf(mFalse)(mPure(5) Else mPure(0)) shouldBe mPure[Int](0)
    fmIf(mBooleanEmpty)(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    fmIf(mTrue)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    fmIf(mFalse)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](0)

    fmIf(mBooleanEmpty)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mEmpty[Int]
  }

  it should "have monadic loops methods" in {
    var bodyCycles = 0
    var i = 0
    def mDecrementedCounterGEZ() = {
      i -= 1
      mPure(i >= 0)
    }


    // >>> tailRec mLoop (it is the base for all other loop variants)

    mLoop(mBooleanEmpty) shouldBe mBooleanEmpty // empty condition result also stops cycling

    mLoop(mFalse) shouldBe mFalse

    i = 3; bodyCycles = 0
    mLoop {
      bodyCycles += 1
      mDecrementedCounterGEZ()
    } shouldBe mFalse
    bodyCycles shouldBe 4


    // >>> tailRec mWhile

    bodyCycles = 0
    mWhile(mBooleanEmpty) { // empty condition result also stops cycling
      bodyCycles += 1
    } shouldBe mBooleanEmpty
    bodyCycles shouldBe 0

    bodyCycles = 0
    mWhile(mFalse) {
      bodyCycles += 1
    } shouldBe mFalse
    bodyCycles shouldBe 0

    i = 3; bodyCycles = 0
    mWhile(mDecrementedCounterGEZ()) {
      bodyCycles += 1
    } shouldBe mFalse
    bodyCycles shouldBe 3


    // >>> tailRec fmWhile

    bodyCycles = 0
    fmWhile(mBooleanEmpty) { // empty condition result also stops cycling
      mPure(bodyCycles += 1)
    } shouldBe mBooleanEmpty
    bodyCycles shouldBe 0

    bodyCycles = 0
    fmWhile(mFalse) {
      mPure(bodyCycles += 1)
    } shouldBe mFalse
    bodyCycles shouldBe 0

    bodyCycles = 0
    fmWhile(mTrue) {
      bodyCycles += 1
      mEmpty[Any] // empty body result also stops cycling
    } shouldBe mEmpty[Any]
    bodyCycles shouldBe 1

    i = 3; bodyCycles = 0
    fmWhile(mDecrementedCounterGEZ()) {
      mPure(bodyCycles += 1)
    } shouldBe mFalse
    bodyCycles shouldBe 3


    // >>> tailRec mDoWhile

    bodyCycles = 0
    // one group of 2 parameters only variant. Scala 2 may distinguish overloads only on the 1st group of parameters
    mDoWhile({ // empty condition result also stops cycling
      bodyCycles += 1
    }, mBooleanEmpty) shouldBe mBooleanEmpty
    bodyCycles shouldBe 1

    bodyCycles = 0
    mDoWhile({
      bodyCycles += 1
    }, mFalse) shouldBe mFalse
    bodyCycles shouldBe 1

    i = 3; bodyCycles = 0
    mDoWhile({
      bodyCycles += 1
    }, mDecrementedCounterGEZ()) shouldBe mFalse

    bodyCycles shouldBe 4


    // >>> tailRec fmDoWhile

    bodyCycles = 0
    fmDoWhile { // empty condition result also stops cycling
      mPure(bodyCycles += 1)
    } (mBooleanEmpty) shouldBe mBooleanEmpty
    bodyCycles shouldBe 1

    bodyCycles = 0
    fmDoWhile {
      mPure(bodyCycles += 1)
    } (mFalse) shouldBe mFalse
    bodyCycles shouldBe 1

    bodyCycles = 0
    fmDoWhile {
      bodyCycles += 1
      mEmpty[Any] // empty body result also stops cycling
    } (mTrue) shouldBe mEmpty[Any]
    bodyCycles shouldBe 1

    i = 3; bodyCycles = 0
    fmDoWhile {
      mPure(bodyCycles += 1)
    } (mDecrementedCounterGEZ()) shouldBe mFalse

    bodyCycles shouldBe 4
  }
}
