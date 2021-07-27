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

import sands.sugar.bms.BooleanMonads.{ArrayTrue, OptionTrue, RightTrue, TryTrue, arrayToMonadicPredicate}
import sands.sugar.bms.Scala211Helper._ // mark it in the IDEA as "always used in the project" to do not loose it for 2.11
import sands.sugar.bms.typeclass.BooleanMonad

import scala.annotation.tailrec
import scala.reflect.ClassTag
import scala.util.Try

/*
 * Created by Serhiy Shamshetdinov
 * at 04.08.2021 22:13
 */

trait BooleanMonadsStatements extends ElseExtension {

  /// BooleanMonad type class

  def mapIf[M[_], A, C](bm: M[Boolean], trueBranch: => A, falseBranch: => A = {})(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.map[Boolean, A](bm, b => if(b) trueBranch else falseBranch)

  def mapIf[M[_], A, C](bm: M[Boolean])(ifBranches: IfBranches[A, A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.map[Boolean, A](bm, b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def flatMapIf[M[_], A, C](bm: M[Boolean], trueBranch: => M[A], falseBranch: => M[A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.flatMap[Boolean, A](bm, b => if(b) trueBranch else falseBranch)

  def flatMapIf[M[_], A, C](bm: M[Boolean])(ifBranches: IfBranches[M[A], M[A]])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.flatMap[Boolean, A](bm, b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  def mIf[M[_], A, C](bm: M[Boolean], trueBranch: => A, falseBranch: => A = {})(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.map[Boolean, A](BM.toMonadicPredicate(bm), b => if(b) trueBranch else falseBranch)

  def mIf[M[_], A, C](bm: M[Boolean])(ifBranches: IfBranches[A, A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.map[Boolean, A](BM.toMonadicPredicate(bm), b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def fmIf[M[_], A, C](bm: M[Boolean], trueBranch: => M[A], falseBranch: => M[A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.flatMap[Boolean, A](BM.toMonadicPredicate(bm), b => if(b) trueBranch else falseBranch)

  def fmIf[M[_], A, C](bm: M[Boolean])(ifBranches: IfBranches[M[A], M[A]])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
    BM.flatMap[Boolean, A](BM.toMonadicPredicate(bm), b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  def mLoop[M[_], C](bodyWithCondition: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] =
    BM.tailRecLoop(bodyWithCondition)

  def mWhile[M[_], C](mc: => M[Boolean])(body: => Any)(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = {
    val mcValue = BM.toMonadicPredicate(mc)
    fmIf(mcValue, mLoop({body; mc}), mcValue)
  }

  /** It also will stop cycling when mBody results with Failure (or empty collection)
   * @param mBody M body to cycle
   * @param mc Boolean M predicate ('true' if collection contains true)
   * @return Failure (or empty) returned by the mBody or mc, or M(false) returned by the mc converted to monadic predicate
   */
  def fmWhile[M[_], B, C](mc: => M[Boolean])(mBody: => M[B])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = {
    val mcValue = BM.toMonadicPredicate(mc)
    fmIf(mcValue, mLoop(BM.flatMap[B, Boolean](mBody, _ => mc)), mcValue)
  }

  // it may not be with 2 parameter groups because Scala 2 senses overloads difference only on the first group of parameters
  def mDoWhile[M[_], C](body: => Any, mc: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] =
    mLoop({body; mc})

  /** It also will stop cycling when mBody results Failure (or empty collection)
   * @param mBody M body to cycle
   * @param mc Boolean M predicate ('true' if collection contains true)
   * @return Failure (or empty) returned by the mBody or mc, or M(false) returned by the mc converted to monadic predicate
   */
  def fmDoWhile[M[_], B, C](mBody: => M[B])(mc: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] =
    mLoop(BM.flatMap[B, Boolean](mBody, _ => mc))


  /// Option

  def mapIf[A](bm: Option[Boolean], trueBranch: => A, falseBranch: => A): Option[A] =
    bm.map(b => if(b) trueBranch else falseBranch)

  def mapIf[T >: Unit](bm: Option[Boolean], trueOnlyBranch: => T): Option[T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    bm.map(b => if(b) trueOnlyBranch)

  def mapIf[A](bm: Option[Boolean])(ifBranches: IfBranches[A, A]): Option[A] =
    bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def flatMapIf[A](bm: Option[Boolean], trueBranch: => Option[A], falseBranch: => Option[A]): Option[A] =
    bm.flatMap(b => if(b) trueBranch else falseBranch)

  def flatMapIf[A](bm: Option[Boolean])(ifBranches: IfBranches[Option[A], Option[A]]): Option[A] =
    bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  def mIf[A](bm: Option[Boolean], trueBranch: => A, falseBranch: => A): Option[A] =
    bm.map(b => if(b) trueBranch else falseBranch)

  def mIf[T >: Unit](bm: Option[Boolean], trueOnlyBranch: => T): Option[T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    bm.map(b => if(b) trueOnlyBranch)

  def mIf[A](bm: Option[Boolean])(ifBranches: IfBranches[A, A]): Option[A] =
    bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def fmIf[A](bm: Option[Boolean], trueBranch: => Option[A], falseBranch: => Option[A]): Option[A] =
    bm.flatMap(b => if(b) trueBranch else falseBranch)

  def fmIf[A](bm: Option[Boolean])(ifBranches: IfBranches[Option[A], Option[A]]): Option[A] =
    bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  @tailrec
  final def mLoop(bodyWithCondition: => Option[Boolean]): Option[Boolean] = bodyWithCondition match {
    case OptionTrue => mLoop(bodyWithCondition)
    case stopValue => stopValue
  }

  def mWhile(mc: => Option[Boolean])(body: => Any): Option[Boolean] = {
    val mcValue = mc
    fmIf(mcValue, mLoop({body; mc}), mcValue)
  }

  /** It also will stop cycling when mBody results None
   * @param mBody Option body to cycle
   * @param mc Boolean Option predicate
   * @return None returned by the mBody or mc, or Some(false) returned by the mc
   */
  def fmWhile[A](mc: => Option[Boolean])(mBody: => Option[A]): Option[Boolean] = {
    val mcValue = mc
    fmIf(mcValue, mLoop(mBody.flatMap(_ => mc)), mcValue)
  }

  def mDoWhile(body: => Any, mc: => Option[Boolean]): Option[Boolean] =
    mLoop({body; mc})

  /** It also will stop cycling when mBody results None
   * @param mBody Option body to cycle
   * @param mc Boolean Option predicate
   * @return None returned by the mBody or mc, or Some(false) returned by the mc
 */
  def fmDoWhile[A](mBody: => Option[A])(mc: => Option[Boolean]): Option[Boolean] =
    mLoop(mBody.flatMap(_ => mc))


  /// Try

  def mapIf[A](bm: Try[Boolean], trueBranch: => A, falseBranch: => A): Try[A] =
    bm.map(b => if(b) trueBranch else falseBranch)

  def mapIf[T >: Unit](bm: Try[Boolean], trueOnlyBranch: => T): Try[T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    bm.map(b => if(b) trueOnlyBranch)

  def mapIf[A](bm: Try[Boolean])(ifBranches: IfBranches[A, A]): Try[A] =
    bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def flatMapIf[A](bm: Try[Boolean], trueBranch: => Try[A], falseBranch: => Try[A]): Try[A] =
    bm.flatMap(b => if(b) trueBranch else falseBranch)

  def flatMapIf[A](bm: Try[Boolean])(ifBranches: IfBranches[Try[A], Try[A]]): Try[A] =
    bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  def mIf[A](bm: Try[Boolean], trueBranch: => A, falseBranch: => A): Try[A] =
    bm.map(b => if(b) trueBranch else falseBranch)

  def mIf[T >: Unit](bm: Try[Boolean], trueOnlyBranch: => T): Try[T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    bm.map(b => if(b) trueOnlyBranch)

  def mIf[A](bm: Try[Boolean])(ifBranches: IfBranches[A, A]): Try[A] =
    bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def fmIf[A](bm: Try[Boolean], trueBranch: => Try[A], falseBranch: => Try[A]): Try[A] =
    bm.flatMap(b => if(b) trueBranch else falseBranch)

  def fmIf[A](bm: Try[Boolean])(ifBranches: IfBranches[Try[A], Try[A]]): Try[A] =
    bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  @tailrec
  final def mLoop(bodyWithCondition: => Try[Boolean]): Try[Boolean] = bodyWithCondition match {
    case TryTrue => mLoop(bodyWithCondition)
    case stopValue => stopValue
  }

  def mWhile(mc: => Try[Boolean])(body: => Any): Try[Boolean] = {
    val mcValue = mc
    fmIf(mcValue, mLoop({body; mc}), mcValue)
  }

  /** It also will stop cycling when mBody results None
   * @param mBody Try body to cycle
   * @param mc Boolean Try predicate
   * @return None returned by the mBody or mc, or Success(false) returned by the mc
   */
  def fmWhile[A](mc: => Try[Boolean])(mBody: => Try[A]): Try[Boolean] = {
    val mcValue = mc
    fmIf(mcValue, mLoop(mBody.flatMap(_ => mc)), mcValue)
  }

  def mDoWhile(body: => Any, mc: => Try[Boolean]): Try[Boolean] =
    mLoop({body; mc})

  /** It also will stop cycling when mBody results None
   * @param mBody Try body to cycle
   * @param mc Boolean Try predicate
   * @return None returned by the mBody or mc, or Success(false) returned by the mc
   */
  def fmDoWhile[A](mBody: => Try[A])(mc: => Try[Boolean]): Try[Boolean] =
    mLoop(mBody.flatMap(_ => mc))


  /// Array

  def mapIf[A: ClassTag](bm: Array[Boolean], trueBranch: => A, falseBranch: => A): Array[A] =
    bm.map(b => if(b) trueBranch else falseBranch)

  def mapIf[T >: Unit: ClassTag](bm: Array[Boolean], trueOnlyBranch: => T): Array[T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    bm.map(b => if(b) trueOnlyBranch)

  def mapIf[A: ClassTag](bm: Array[Boolean])(ifBranches: IfBranches[A, A]): Array[A] =
    bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def flatMapIf[A: ClassTag](bm: Array[Boolean], trueBranch: => Array[A], falseBranch: => Array[A]): Array[A] =
    bm.flatMap(b => if(b) trueBranch else falseBranch)

  def flatMapIf[A: ClassTag](bm: Array[Boolean])(ifBranches: IfBranches[Array[A], Array[A]]): Array[A] =
    bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  def mIf[A: ClassTag](bm: Array[Boolean], trueBranch: => A, falseBranch: => A): Array[A] =
    arrayToMonadicPredicate(bm).map(b => if(b) trueBranch else falseBranch)

  def mIf[T >: Unit: ClassTag](bm: Array[Boolean], trueOnlyBranch: => T): Array[T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    arrayToMonadicPredicate(bm).map(b => if(b) trueOnlyBranch)

  def mIf[A: ClassTag](bm: Array[Boolean])(ifBranches: IfBranches[A, A]): Array[A] =
    arrayToMonadicPredicate(bm).map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def fmIf[A: ClassTag](bm: Array[Boolean], trueBranch: => Array[A], falseBranch: => Array[A]): Array[A] =
    arrayToMonadicPredicate(bm).flatMap(b => if(b) trueBranch else falseBranch)

  def fmIf[A: ClassTag](bm: Array[Boolean])(ifBranches: IfBranches[Array[A], Array[A]]): Array[A] =
    arrayToMonadicPredicate(bm).flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  @tailrec
  final def mLoop(bodyWithCondition: => Array[Boolean]): Array[Boolean] = arrayToMonadicPredicate(bodyWithCondition) match {
    case ArrayTrue => mLoop(bodyWithCondition)
    case stopValue => stopValue
  }

  def mWhile(mc: => Array[Boolean])(body: => Any): Array[Boolean] = {
    val mcValue = arrayToMonadicPredicate(mc)
    fmIf(mcValue, mLoop({body; mc}), mcValue)
  }

  /** It also will stop cycling when mBody results empty Array
   * @param mBody Array body to cycle
   * @param mc Boolean Array predicate ('true' if Array contains true)
   * @return Empty Array returned by the mBody or mc, or Array(false) returned by the mc
   */
  def fmWhile[A](mc: => Array[Boolean])(mBody: => Array[A]): Array[Boolean] = {
    val mcValue = arrayToMonadicPredicate(mc)
    fmIf(mcValue, mLoop(mBody.flatMap(_ => mc)), mcValue)
  }

  def mDoWhile(body: => Any, mc: => Array[Boolean]): Array[Boolean] =
    mLoop({body; mc})

  /** It also will stop cycling when mBody results empty Array
   * @param mBody Array body to cycle
   * @param mc Boolean Array predicate ('true' if Array contains true)
   * @return Empty Array returned by the mBody or mc, or Array(false) returned by the mc
   */
  def fmDoWhile[A](mBody: => Array[A])(mc: => Array[Boolean]): Array[Boolean] =
    mLoop(mBody.flatMap(_ => mc))


  /// Either

  def mapIf[L, A](bm: Either[L, Boolean], trueBranch: => A, falseBranch: => A): Either[L, A] =
    bm.map(b => if(b) trueBranch else falseBranch)

  def mapIf[L, T >: Unit](bm: Either[L, Boolean], trueOnlyBranch: => T): Either[L, T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    bm.map(b => if(b) trueOnlyBranch)

  def mapIf[L, A](bm: Either[L, Boolean])(ifBranches: IfBranches[A, A]): Either[L, A] =
    bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def flatMapIf[L, A](bm: Either[L, Boolean], trueBranch: => Either[L, A], falseBranch: => Either[L, A]): Either[L, A] =
    bm.flatMap(b => if(b) trueBranch else falseBranch)

  def flatMapIf[L, A](bm: Either[L, Boolean])(ifBranches: IfBranches[Either[L, A], Either[L, A]]): Either[L, A] =
    bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  def mIf[L, A](bm: Either[L, Boolean], trueBranch: => A, falseBranch: => A): Either[L, A] =
    bm.map(b => if(b) trueBranch else falseBranch)

  def mIf[L, T >: Unit](bm: Either[L, Boolean], trueOnlyBranch: => T): Either[L, T] = // only one method overload may have default arguments but type class already has, thus here is 2 methods
    bm.map(b => if(b) trueOnlyBranch)

  def mIf[L, A](bm: Either[L, Boolean])(ifBranches: IfBranches[A, A]): Either[L, A] =
    bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

  def fmIf[L, A](bm: Either[L, Boolean], trueBranch: => Either[L, A], falseBranch: => Either[L, A]): Either[L, A] =
    bm.flatMap(b => if(b) trueBranch else falseBranch)

  def fmIf[L, A](bm: Either[L, Boolean])(ifBranches: IfBranches[Either[L, A], Either[L, A]]): Either[L, A] =
    bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


  @tailrec
  final def mLoop[L](bodyWithCondition: => Either[L, Boolean]): Either[L, Boolean] = bodyWithCondition match {
    case RightTrue => mLoop(bodyWithCondition)
    case stopValue => stopValue
  }

  def mWhile[L](mc: => Either[L, Boolean])(body: => Any): Either[L, Boolean] = {
    val mcValue = mc
    fmIf(mcValue, mLoop({body; mc}), mcValue)
  }

  /** It also will stop cycling when mBody results None
   * @param mBody Either body to cycle
   * @param mc Boolean Either predicate
   * @return None returned by the mBody or mc, or Success(false) returned by the mc
   */
  def fmWhile[L, A](mc: => Either[L, Boolean])(mBody: => Either[L, A]): Either[L, Boolean] = {
    val mcValue = mc
    fmIf(mcValue, mLoop(mBody.flatMap(_ => mc)), mcValue)
  }

  def mDoWhile[L](body: => Any, mc: => Either[L, Boolean]): Either[L, Boolean] =
    mLoop({body; mc})

  /** It also will stop cycling when mBody results None
   * @param mBody Either body to cycle
   * @param mc Boolean Either predicate
   * @return None returned by the mBody or mc, or Success(false) returned by the mc
   */
  def fmDoWhile[L, A](mBody: => Either[L, A])(mc: => Either[L, Boolean]): Either[L, Boolean] =
    mLoop(mBody.flatMap(_ => mc))
}
