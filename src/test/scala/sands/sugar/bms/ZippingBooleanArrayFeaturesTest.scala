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
import sands.sugar.bms.typeclass.functions.BooleanFunctions
import sands.sugar.bms.typeclass.{BooleanCollectionMonad, NoContext}

import scala.reflect.ClassTag

/*
 * Created by Serhiy Shamshetdinov
 * at 25.08.2021 18:00
 */

class ZippingBooleanArrayFeaturesTest extends AnyFlatSpec with Matchers {

  /// >>> the difference in the behavior of Zipping and Monadic Boolean Collections is only in the way boolean expressions are evaluated

  behavior of s"Boolean Array with ${BooleanFunctions[Array, ClassTag[Boolean]].name}"

  def mPure[A: ClassTag](a: A*): Array[A] = Array(a: _*)

  val mFalse: Array[Boolean] = mPure(false)
  val mTrue:  Array[Boolean] = mPure(true)
  val mUnit:  Array[Unit] =    mPure({})

  val mFalseFalse: Array[Boolean] = mPure(false, false)
  val mFalseTrue: Array[Boolean]  = mPure(false, true)
  val mTrueFalse: Array[Boolean]  = mPure(true, false)
  val mTrueTrue: Array[Boolean]   = mPure(true, true)

  def mEmpty[A: ClassTag]: Array[A] = Array.empty[A]

  val mBooleanEmpty: Array[Boolean] = mEmpty[Boolean]

  it should "have type constants" in {

    Array.False shouldBe mFalse
    Array.True  shouldBe mTrue
    Array.Unit  shouldBe mUnit
  }

  /// >>> the following expressions test is copy-paste for Zipping Boolean Collections and Zipping Array

  it should "have logical operators extensions" in {

    !mBooleanEmpty shouldBe mBooleanEmpty
    !mFalseTrue shouldBe mTrueFalse

    false && mFalseTrue  shouldBe mFalse // lazy && does not work for collections to always have precise result length
    true  || mFalseFalse shouldBe mTrue  // lazy || does not work for collections to always have precise result length
    true  &  mFalseTrue  shouldBe mFalse
    false ^  mTrueFalse  shouldBe mTrue
    false |  mFalseFalse shouldBe mFalse

    mFalseTrue  && true  shouldBe mFalse // here lazy && works since the "length" of pure Boolean is known
    mTrueFalse  || false shouldBe mTrue  // here lazy || works since the "length" of pure Boolean is known
    mFalseTrue  &  true  shouldBe mFalse
    mTrueFalse  ^  false shouldBe mTrue
    mFalseFalse |  false shouldBe mFalse

    mTrueFalse  && mTrue   shouldBe mTrue
    mFalseFalse || mFalse  shouldBe mFalse
    mFalseTrue  &  mFalse  shouldBe mFalse
    mFalseTrue  ^  mTrue   shouldBe mTrue
    mFalseFalse |  mFalse  shouldBe mFalse

    mTrueFalse  && mTrueTrue   shouldBe mTrueFalse
    mFalseFalse || mFalseTrue  shouldBe mFalseTrue
    mFalseTrue  &  mFalseTrue  shouldBe mFalseTrue
    mFalseTrue  ^  mTrueFalse  shouldBe mTrueTrue
    mFalseFalse |  mFalseFalse shouldBe mFalseFalse

    false && mBooleanEmpty shouldBe mBooleanEmpty // lazy && does not work for collections to always have precise result length
    true  || mBooleanEmpty shouldBe mBooleanEmpty // lazy || does not work for collections to always have precise result length
    true  &  mBooleanEmpty shouldBe mBooleanEmpty
    true  ^  mBooleanEmpty shouldBe mBooleanEmpty
    true  |  mBooleanEmpty shouldBe mBooleanEmpty

    mFalseFalse && mBooleanEmpty shouldBe mBooleanEmpty // lazy && does not work for collections to always have precise result length
    mFalseTrue  || mBooleanEmpty shouldBe mBooleanEmpty // lazy || does not work for collections to always have precise result length
    mFalseFalse &  mBooleanEmpty shouldBe mBooleanEmpty
    mFalseFalse ^  mBooleanEmpty shouldBe mBooleanEmpty
    mFalseFalse |  mBooleanEmpty shouldBe mBooleanEmpty

    mBooleanEmpty && false shouldBe mBooleanEmpty // here lazy && works since for empty right operand result is always empty
    mBooleanEmpty || false shouldBe mBooleanEmpty // here lazy || works since for empty right operand result is always empty
    mBooleanEmpty &  false shouldBe mBooleanEmpty
    mBooleanEmpty ^  false shouldBe mBooleanEmpty
    mBooleanEmpty |  false shouldBe mBooleanEmpty

    mBooleanEmpty && mFalseTrue shouldBe mBooleanEmpty // here lazy && works since for empty right operand result is always empty
    mBooleanEmpty || mFalseTrue shouldBe mBooleanEmpty // here lazy || works since for empty right operand result is always empty
    mBooleanEmpty &  mFalseTrue shouldBe mBooleanEmpty
    mBooleanEmpty ^  mFalseTrue shouldBe mBooleanEmpty
    mBooleanEmpty |  mFalseTrue shouldBe mBooleanEmpty


    true && mFalseTrue || mTrue ^ mTrueTrue & mFalseFalse | false shouldBe mTrue
  }

  /// >>> the following tests are copy-paste for Zipping and Monadic Boolean Collections (including Array)

  it should "have monadic 'if' extensions and standalone methods" in {

    // >>> mapIf

    // true-branch only

    mTrue.mapIf(5) shouldBe mPure[AnyVal](5)
    mFalse.mapIf(5) shouldBe mUnit
    mFalseTrue.mapIf(5) shouldBe mPure[AnyVal]({}, 5)
    mBooleanEmpty.mapIf(5) shouldBe mEmpty[AnyVal]

    mTrue.mapIf {
      5
    } shouldBe mPure[AnyVal](5)

    mFalse.mapIf {
      5
    } shouldBe mUnit

    mFalseTrue.mapIf {
      5
    } shouldBe mPure[AnyVal]({}, 5)

    mBooleanEmpty.mapIf {
      5
    } shouldBe mEmpty[AnyVal]

    mapIf(mTrue, 5) shouldBe mPure[AnyVal](5)
    mapIf(mFalse, 5) shouldBe mUnit
    mapIf(mFalseTrue, 5) shouldBe mPure[AnyVal]({}, 5)
    mapIf(mBooleanEmpty, 5) shouldBe mEmpty[AnyVal]

    mapIf(mTrue)(5) shouldBe mPure[AnyVal](5)
    mapIf(mFalse)(5) shouldBe mUnit
    mapIf(mFalseTrue)(5) shouldBe mPure[AnyVal]({}, 5)
    mapIf(mBooleanEmpty)(5) shouldBe mEmpty[AnyVal]

    mapIf(mTrue) {
      5
    } shouldBe mPure[AnyVal](5)

    mapIf(mFalse) {
      5
    } shouldBe mUnit

    mapIf(mFalseTrue) {
      5
    } shouldBe mPure[AnyVal]({}, 5)

    mapIf(mBooleanEmpty) {
      5
    } shouldBe mEmpty[AnyVal]

    // both branches

    mTrue.mapIf(5, 0) shouldBe mPure[Int](5)
    mFalse.mapIf(5, 0) shouldBe mPure[Int](0)
    mFalseTrue.mapIf(5, 0) shouldBe mPure[Int](0, 5)
    mBooleanEmpty.mapIf(5, 0) shouldBe mEmpty[Int]

    mTrue.mapIf(5 Else 0) shouldBe mPure[Int](5)
    mFalse.mapIf(5 Else 0) shouldBe mPure[Int](0)
    mFalseTrue.mapIf(5 Else 0) shouldBe mPure[Int](0, 5)
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

    mFalseTrue.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0, 5)

    mBooleanEmpty.mapIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]

    mapIf(mTrue, 5, 0) shouldBe mPure[Int](5)
    mapIf(mFalse, 5, 0) shouldBe mPure[Int](0)
    mapIf(mFalseTrue, 5, 0) shouldBe mPure[Int](0, 5)
    mapIf(mBooleanEmpty, 5, 0) shouldBe mEmpty[Int]

    mapIf(mTrue)(5 Else 0) shouldBe mPure[Int](5)
    mapIf(mFalse)(5 Else 0) shouldBe mPure[Int](0)
    mapIf(mFalseTrue)(5 Else 0) shouldBe mPure[Int](0, 5)
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

    mapIf(mFalseTrue)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0, 5)

    mapIf(mBooleanEmpty)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]

    // >>> mIf : condition interpretation for collections differs: branches are evaluated only once on "true exists" rule

    // true-branch only

    mTrue.mIf(5) shouldBe mPure[AnyVal](5)
    mFalse.mIf(5) shouldBe mUnit
    mFalseFalse.mIf(5) shouldBe mUnit
    mFalseTrue.mIf(5) shouldBe mPure[AnyVal](5)
    mBooleanEmpty.mIf(5) shouldBe mEmpty[AnyVal]

    mTrue.mIf {
      5
    } shouldBe mPure[AnyVal](5)

    mFalse.mIf {
      5
    } shouldBe mUnit

    mFalseFalse.mIf {
      5
    } shouldBe mUnit

    mFalseTrue.mIf {
      5
    } shouldBe mPure[AnyVal](5)

    mBooleanEmpty.mIf {
      5
    } shouldBe mEmpty[AnyVal]

    mIf(mTrue, 5) shouldBe mPure[AnyVal](5)
    mIf(mFalse, 5) shouldBe mUnit
    mIf(mFalseFalse, 5) shouldBe mUnit
    mIf(mFalseTrue, 5) shouldBe mPure[AnyVal](5)
    mIf(mBooleanEmpty, 5) shouldBe mEmpty[AnyVal]

    mIf(mTrue)(5) shouldBe mPure[AnyVal](5)
    mIf(mFalse)(5) shouldBe mUnit
    mIf(mFalseFalse)(5) shouldBe mUnit
    mIf(mFalseTrue)(5) shouldBe mPure[AnyVal](5)
    mIf(mBooleanEmpty)(5) shouldBe mEmpty[AnyVal]

    mIf(mTrue) {
      5
    } shouldBe mPure[AnyVal](5)

    mIf(mFalse) {
      5
    } shouldBe mUnit

    mIf(mFalseFalse) {
      5
    } shouldBe mUnit

    mIf(mFalseTrue) {
      5
    } shouldBe mPure[AnyVal](5)

    mIf(mBooleanEmpty) {
      5
    } shouldBe mEmpty[AnyVal]

    // both branches

    mTrue.mIf(5, 0) shouldBe mPure[Int](5)
    mFalse.mIf(5, 0) shouldBe mPure[Int](0)
    mFalseFalse.mIf(5, 0) shouldBe mPure[Int](0)
    mFalseTrue.mIf(5, 0) shouldBe mPure[Int](5)
    mBooleanEmpty.mIf(5, 0) shouldBe mEmpty[Int]

    mTrue.mIf(5 Else 0) shouldBe mPure[Int](5)
    mFalse.mIf(5 Else 0) shouldBe mPure[Int](0)
    mFalseFalse.mIf(5 Else 0) shouldBe mPure[Int](0)
    mFalseTrue.mIf(5 Else 0) shouldBe mPure[Int](5)
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

    mFalseFalse.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0)

    mFalseTrue.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](5)

    mBooleanEmpty.mIf({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]

    mIf(mTrue, 5, 0) shouldBe mPure[Int](5)
    mIf(mFalse, 5, 0) shouldBe mPure[Int](0)
    mIf(mFalseFalse, 5, 0) shouldBe mPure[Int](0)
    mIf(mFalseTrue, 5, 0) shouldBe mPure[Int](5)
    mIf(mBooleanEmpty, 5, 0) shouldBe mEmpty[Int]

    mIf(mTrue)(5 Else 0) shouldBe mPure[Int](5)
    mIf(mFalse)(5 Else 0) shouldBe mPure[Int](0)
    mIf(mFalseFalse)(5 Else 0) shouldBe mPure[Int](0)
    mIf(mFalseTrue)(5 Else 0) shouldBe mPure[Int](5)
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

    mIf(mFalseFalse)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](0)

    mIf(mFalseTrue)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mPure[Int](5)

    mIf(mBooleanEmpty)({ // acknowledge ()
      5
    } Else {
      0
    }) shouldBe mEmpty[Int]
  }
  
  it should "have flat monadic 'if' extensions and standalone methods" in {

    // >>> flatMapIf

    // true-branch only

    mTrueTrue.flatMapIf(mPure(5)) shouldBe mPure[Int](5, 5)
    mTrueFalse.flatMapIf(mPure(5)) shouldBe mPure[Int](5)
    mFalseFalse.flatMapIf(mPure(5)) shouldBe mEmpty[Int]
    mBooleanEmpty.flatMapIf(mPure(5)) shouldBe mEmpty[Int]

    mTrueTrue.flatMapIf {
      mPure(5)
    } shouldBe mPure[Int](5, 5)

    mTrueFalse.flatMapIf {
      mPure(5)
    } shouldBe mPure[Int](5)

    mFalseFalse.flatMapIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    mBooleanEmpty.flatMapIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    flatMapIf(mTrueTrue)(mPure(5)) shouldBe mPure[Int](5, 5)
    flatMapIf(mTrueFalse)(mPure(5)) shouldBe mPure[Int](5)
    flatMapIf(mFalseFalse)(mPure(5)) shouldBe mEmpty[Int]
    flatMapIf(mBooleanEmpty)(mPure(5)) shouldBe mEmpty[Int]

    flatMapIf(mTrueTrue) {
      mPure(5)
    } shouldBe mPure[Int](5, 5)

    flatMapIf(mTrueFalse) {
      mPure(5)
    } shouldBe mPure[Int](5)

    flatMapIf(mFalseFalse) {
      mPure(5)
    } shouldBe mEmpty[Int]

    flatMapIf(mBooleanEmpty) {
      mPure(5)
    } shouldBe mEmpty[Int]

    // both branches

    mTrueTrue.flatMapIf(mPure(5), mPure(0)) shouldBe mPure[Int](5, 5)
    mTrueFalse.flatMapIf(mPure(5), mPure(0)) shouldBe mPure[Int](5, 0)
    mFalseFalse.flatMapIf(mPure(5), mPure(0)) shouldBe mPure[Int](0, 0)
    mBooleanEmpty.flatMapIf(mPure(5), mPure(0)) shouldBe mEmpty[Int]

    mTrueTrue.flatMapIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](5, 5)
    mTrueFalse.flatMapIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](5, 0)
    mFalseFalse.flatMapIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](0, 0)
    mBooleanEmpty.flatMapIf(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    mTrueTrue.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5, 5)

    mTrueFalse.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5, 0)

    mFalseFalse.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](0, 0)

    mBooleanEmpty.flatMapIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mEmpty[Int]

    flatMapIf(mTrueTrue, mPure(5), mPure(0)) shouldBe mPure[Int](5, 5)
    flatMapIf(mTrueFalse, mPure(5), mPure(0)) shouldBe mPure[Int](5, 0)
    flatMapIf(mFalseFalse, mPure(5), mPure(0)) shouldBe mPure[Int](0, 0)
    flatMapIf(mBooleanEmpty, mPure(5), mPure(0)) shouldBe mEmpty[Int]

    flatMapIf(mTrueTrue)(mPure(5) Else mPure(0)) shouldBe mPure[Int](5, 5)
    flatMapIf(mTrueFalse)(mPure(5) Else mPure(0)) shouldBe mPure[Int](5, 0)
    flatMapIf(mFalseFalse)(mPure(5) Else mPure(0)) shouldBe mPure[Int](0, 0)
    flatMapIf(mBooleanEmpty)(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    flatMapIf(mTrueTrue)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5, 5)

    flatMapIf(mTrueFalse)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5, 0)

    flatMapIf(mFalseFalse)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](0, 0)

    flatMapIf(mBooleanEmpty)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mEmpty[Int]

    // >>> fmIf : condition interpretation for collections differs: branches are evaluated only once on "true exists" rule

    // true-branch only

    mTrueTrue.fmIf(mPure(5)) shouldBe mPure[Int](5)
    mTrueFalse.fmIf(mPure(5)) shouldBe mPure[Int](5)
    mFalseFalse.fmIf(mPure(5)) shouldBe mEmpty[Int]
    mBooleanEmpty.fmIf(mPure(5)) shouldBe mEmpty[Int]

    mTrueTrue.fmIf {
      mPure(5)
    } shouldBe mPure[Int](5)

    mTrueFalse.fmIf {
      mPure(5)
    } shouldBe mPure[Int](5)

    mFalseFalse.fmIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    mBooleanEmpty.fmIf {
      mPure(5)
    } shouldBe mEmpty[Int]

    fmIf(mTrueTrue)(mPure(5)) shouldBe mPure[Int](5)
    fmIf(mTrueFalse)(mPure(5)) shouldBe mPure[Int](5)
    fmIf(mFalseFalse)(mPure(5)) shouldBe mEmpty[Int]
    fmIf(mBooleanEmpty)(mPure(5)) shouldBe mEmpty[Int]

    fmIf(mTrueTrue) {
      mPure(5)
    } shouldBe mPure[Int](5)

    fmIf(mTrueFalse) {
      mPure(5)
    } shouldBe mPure[Int](5)

    fmIf(mFalseFalse) {
      mPure(5)
    } shouldBe mEmpty[Int]

    fmIf(mBooleanEmpty) {
      mPure(5)
    } shouldBe mEmpty[Int]

    // both branches

    mTrueTrue.fmIf(mPure(5), mPure(0)) shouldBe mPure[Int](5)
    mTrueFalse.fmIf(mPure(5), mPure(0)) shouldBe mPure[Int](5)
    mFalseFalse.fmIf(mPure(5), mPure(0)) shouldBe mPure[Int](0)
    mBooleanEmpty.fmIf(mPure(5), mPure(0)) shouldBe mEmpty[Int]

    mTrueTrue.fmIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    mTrueFalse.fmIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    mFalseFalse.fmIf(mPure(5) Else mPure(0)) shouldBe mPure[Int](0)
    mBooleanEmpty.fmIf(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    mTrueTrue.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    mTrueFalse.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    mFalseFalse.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](0)

    mBooleanEmpty.fmIf({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mEmpty[Int]

    fmIf(mTrueTrue, mPure(5), mPure(0)) shouldBe mPure[Int](5)
    fmIf(mTrueFalse, mPure(5), mPure(0)) shouldBe mPure[Int](5)
    fmIf(mFalseFalse, mPure(5), mPure(0)) shouldBe mPure[Int](0)
    fmIf(mBooleanEmpty, mPure(5), mPure(0)) shouldBe mEmpty[Int]

    fmIf(mTrueTrue)(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    fmIf(mTrueFalse)(mPure(5) Else mPure(0)) shouldBe mPure[Int](5)
    fmIf(mFalseFalse)(mPure(5) Else mPure(0)) shouldBe mPure[Int](0)
    fmIf(mBooleanEmpty)(mPure(5) Else mPure(0)) shouldBe mEmpty[Int]

    fmIf(mTrueTrue)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    fmIf(mTrueFalse)({ // acknowledge ()
      mPure(5)
    } Else {
      mPure(0)
    }) shouldBe mPure[Int](5)

    fmIf(mFalseFalse)({ // acknowledge ()
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
    def mmDecrementedCounterGEZ() = {
      i -= 1
      mPure(false, i >= 0, i >= 0)
    }

    // >>> tailRec mLoop (it is the base for all other loop variants)

    mLoop(mBooleanEmpty) shouldBe mBooleanEmpty // empty condition result also stops cycling

    mLoop(mFalse) shouldBe mFalse

    mLoop(mFalseFalse) shouldBe mFalse

    i = 3; bodyCycles = 0
    mLoop {
      bodyCycles += 1
      mmDecrementedCounterGEZ()
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

    bodyCycles = 0
    mWhile(mFalseFalse) {
      bodyCycles += 1
    } shouldBe mFalse
    bodyCycles shouldBe 0

    i = 3; bodyCycles = 0
    mWhile(mmDecrementedCounterGEZ()) {
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
    fmWhile(mFalseFalse) {
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
    fmWhile(mmDecrementedCounterGEZ()) {
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

    bodyCycles = 0
    mDoWhile({
      bodyCycles += 1
    }, mFalseFalse) shouldBe mFalse
    bodyCycles shouldBe 1

    i = 3; bodyCycles = 0
    mDoWhile({
      bodyCycles += 1
    }, mmDecrementedCounterGEZ()) shouldBe mFalse

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
      mPure(bodyCycles += 1)
    } (mFalseFalse) shouldBe mFalse
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
    } (mmDecrementedCounterGEZ()) shouldBe mFalse

    bodyCycles shouldBe 4
  }
}
