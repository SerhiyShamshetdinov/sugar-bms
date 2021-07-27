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

import sands.sugar.bms.Scala211Helper._ // mark it in the IDEA as "always used in the project" to do not loose it for 2.11
import sands.sugar.bms.typeclass.BooleanMonadSyntax
import sands.sugar.bms.typeclass.functions.BooleanFunctions
import sands.sugar.bms.typeclass.functions.BooleanFunctions._

import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.{Either, Right, Success, Try}

/*
 * Created by Serhiy Shamshetdinov
 * at 27.07.2021 17:39
 */

object BooleanMonads extends BooleanMonadSyntax with BooleanMonadsStatements {

  /// Option

  private[bms] val OptionFalse: Option[Boolean] = Some(false)
  private[bms] val OptionTrue:  Option[Boolean] = Some(true)
  private[bms] val OptionUnit:  Option[Unit] =    Some(())

  implicit class OptionTypeConstantsSyntax(val t: Option.type) extends AnyVal {
    def False: Option[Boolean] = OptionFalse
    def True:  Option[Boolean] = OptionTrue
    def Unit:  Option[Unit] =    OptionUnit
  }

  implicit class BooleanAndOptionSyntax(val b: Boolean) extends AnyVal {

    def &&(mb: => Option[Boolean]): Option[Boolean] = if (b) mb else OptionFalse
    def ||(mb: => Option[Boolean]): Option[Boolean] = if (b) OptionTrue else mb

    def &(mb: Option[Boolean]): Option[Boolean] = mb.map(b & _)
    def ^(mb: Option[Boolean]): Option[Boolean] = mb.map(b ^ _)
    def |(mb: Option[Boolean]): Option[Boolean] = mb.map(b | _)
  }

  implicit class OptionAndBooleanOrOptionSyntax(val bm: Option[Boolean]) extends AnyVal {

    def &&(b: => Boolean, dummy: Null = null): Option[Boolean] = bm.map(_ && b)
    def ||(b: => Boolean, dummy: Null = null): Option[Boolean] = bm.map(_ || b)

    def &(b: Boolean): Option[Boolean] = bm.map(_ & b)
    def ^(b: Boolean): Option[Boolean] = bm.map(_ ^ b)
    def |(b: Boolean): Option[Boolean] = bm.map(_ | b)

    def unary_! : Option[Boolean] = bm.map(!_)

    def &&(mb: => Option[Boolean]): Option[Boolean] = bm.flatMap(_ && mb)
    def ||(mb: => Option[Boolean]): Option[Boolean] = bm.flatMap(_ || mb)

    def &(mb: Option[Boolean]): Option[Boolean] = bm.flatMap(_ & mb)
    def ^(mb: Option[Boolean]): Option[Boolean] = bm.flatMap(_ ^ mb)
    def |(mb: Option[Boolean]): Option[Boolean] = bm.flatMap(_ | mb)


    def mapIf[A](trueBranch: => A, falseBranch: => A = {}): Option[A] =
      bm.map(b => if(b) trueBranch else falseBranch)

    def mapIf[A](ifBranches: IfBranches[A, A]): Option[A] =
      bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def flatMapIf[A](trueBranch: => Option[A], falseBranch: => Option[A]): Option[A] =
      bm.flatMap(b => if(b) trueBranch else falseBranch)

    def flatMapIf[A](ifBranches: IfBranches[Option[A], Option[A]]): Option[A] =
      bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


    def mIf[A](trueBranch: => A, falseBranch: => A = {}): Option[A] =
      bm.map(b => if(b) trueBranch else falseBranch)

    def mIf[A](ifBranches: IfBranches[A, A]): Option[A] =
      bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def fmIf[A](trueBranch: => Option[A], falseBranch: => Option[A]): Option[A] =
      bm.flatMap(b => if(b) trueBranch else falseBranch)

    def fmIf[A](ifBranches: IfBranches[Option[A], Option[A]]): Option[A] =
      bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())
  }

  implicit def optionToIfBranches[T](trueOnlyBranch: => Option[T]): IfBranches[Option[T], Option[T]] =
    new IfBranches[Option[T], Option[T]](() => trueOnlyBranch, () => Option.empty[T])


  /// Try

  private[bms] val TryFalse: Try[Boolean] = Success(false)
  private[bms] val TryTrue:  Try[Boolean] = Success(true)
  private[bms] val TryUnit:  Try[Unit] =    Success(())

  implicit class TryTypeConstantsSyntax(val t: Try.type) extends AnyVal {
    def False: Try[Boolean] = TryFalse
    def True:  Try[Boolean] = TryTrue
    def Unit:  Try[Unit] =    TryUnit
  }

  implicit class BooleanAndTrySyntax(val b: Boolean) extends AnyVal {

    def &&(mb: => Try[Boolean]): Try[Boolean] = if (b) mb else TryFalse
    def ||(mb: => Try[Boolean]): Try[Boolean] = if (b) TryTrue else mb

    def &(mb: Try[Boolean]): Try[Boolean] = mb.map(b & _)
    def ^(mb: Try[Boolean]): Try[Boolean] = mb.map(b ^ _)
    def |(mb: Try[Boolean]): Try[Boolean] = mb.map(b | _)
  }

  implicit class TryAndBooleanOrTrySyntax(val bm: Try[Boolean]) extends AnyVal {

    def &&(b: => Boolean, dummy: Null = null): Try[Boolean] = bm.map(_ && b)
    def ||(b: => Boolean, dummy: Null = null): Try[Boolean] = bm.map(_ || b)

    def &(b: Boolean): Try[Boolean] = bm.map(_ & b)
    def ^(b: Boolean): Try[Boolean] = bm.map(_ ^ b)
    def |(b: Boolean): Try[Boolean] = bm.map(_ | b)

    def unary_! : Try[Boolean] = bm.map(!_)

    def &&(mb: => Try[Boolean]): Try[Boolean] = bm.flatMap(_ && mb)
    def ||(mb: => Try[Boolean]): Try[Boolean] = bm.flatMap(_ || mb)

    def &(mb: Try[Boolean]): Try[Boolean] = bm.flatMap(_ & mb)
    def ^(mb: Try[Boolean]): Try[Boolean] = bm.flatMap(_ ^ mb)
    def |(mb: Try[Boolean]): Try[Boolean] = bm.flatMap(_ | mb)


    def mapIf[A](trueBranch: => A, falseBranch: => A = {}): Try[A] =
      bm.map(b => if(b) trueBranch else falseBranch)

    def mapIf[A](ifBranches: IfBranches[A, A]): Try[A] =
      bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def flatMapIf[A](trueBranch: => Try[A], falseBranch: => Try[A]): Try[A] =
      bm.flatMap(b => if(b) trueBranch else falseBranch)

    def flatMapIf[A](ifBranches: IfBranches[Try[A], Try[A]]): Try[A] =
      bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


    def mIf[A](trueBranch: => A, falseBranch: => A = {}): Try[A] =
      bm.map(b => if(b) trueBranch else falseBranch)

    def mIf[A](ifBranches: IfBranches[A, A]): Try[A] =
      bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def fmIf[A](trueBranch: => Try[A], falseBranch: => Try[A]): Try[A] =
      bm.flatMap(b => if(b) trueBranch else falseBranch)

    def fmIf[A](ifBranches: IfBranches[Try[A], Try[A]]): Try[A] =
      bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())
  }

  implicit def tryToIfBranches[T](trueOnlyBranch: => Try[T]): IfBranches[Try[T], Try[Unit]] =
    new IfBranches[Try[T], Try[Unit]](() => trueOnlyBranch, () => TryUnit)


  /// Array

  private[bms] val ArrayFalse: Array[Boolean] = Array(false)
  private[bms] val ArrayTrue:  Array[Boolean] = Array(true)
  private[bms] val ArrayUnit:  Array[Unit] =    Array(())

  implicit class ArrayTypeConstantsSyntax(val t: Array.type) extends AnyVal {
    def False: Array[Boolean] = ArrayFalse
    def True:  Array[Boolean] = ArrayTrue
    def Unit:  Array[Unit] =    ArrayUnit
  }

  implicit class BooleanAndArraySyntax(b: Boolean) {

    def &&(mb: => Array[Boolean])(implicit ABF: BooleanFunctions[Array, ClassTag[Boolean]]): Array[Boolean] = ABF(b, lAND, mb)
    def ||(mb: => Array[Boolean])(implicit ABF: BooleanFunctions[Array, ClassTag[Boolean]]): Array[Boolean] = ABF(b, lOR, mb)

    def &(mb: Array[Boolean])(implicit ABF: BooleanFunctions[Array, ClassTag[Boolean]]): Array[Boolean] = ABF(b, AND, mb)
    def ^(mb: Array[Boolean])(implicit ABF: BooleanFunctions[Array, ClassTag[Boolean]]): Array[Boolean] = ABF(b, XOR, mb)
    def |(mb: Array[Boolean])(implicit ABF: BooleanFunctions[Array, ClassTag[Boolean]]): Array[Boolean] = ABF(b, OR, mb)
  }

  implicit class ArrayAndBooleanOrArraySyntax(bm: Array[Boolean])(implicit ABF: BooleanFunctions[Array, ClassTag[Boolean]]) {

    def &&(b: => Boolean, dummy: Null = null): Array[Boolean] = ABF(bm, lAND, b)
    def ||(b: => Boolean, dummy: Null = null): Array[Boolean] = ABF(bm, lOR, b)

    def &(b: Boolean): Array[Boolean] = ABF(bm, AND, b)
    def ^(b: Boolean): Array[Boolean] = ABF(bm, XOR, b)
    def |(b: Boolean): Array[Boolean] = ABF(bm, OR, b)

    def unary_! : Array[Boolean] = bm.map(!_)

    def &&(mb: => Array[Boolean]): Array[Boolean] = ABF(bm, lAND, mb)
    def ||(mb: => Array[Boolean]): Array[Boolean] = ABF(bm, lOR, mb)

    def &(mb: Array[Boolean]): Array[Boolean] = ABF(bm, AND, mb)
    def ^(mb: Array[Boolean]): Array[Boolean] = ABF(bm, XOR, mb)
    def |(mb: Array[Boolean]): Array[Boolean] = ABF(bm, OR, mb)


    def mapIf[A: ClassTag](trueBranch: => A, falseBranch: => A = {}): Array[A] =
      bm.map(b => if(b) trueBranch else falseBranch)

    def mapIf[A: ClassTag](ifBranches: IfBranches[A, A]): Array[A] =
      bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def flatMapIf[A: ClassTag](trueBranch: => Array[A], falseBranch: => Array[A]): Array[A] =
      bm.flatMap(b => if(b) trueBranch else falseBranch)

    def flatMapIf[A: ClassTag](ifBranches: IfBranches[Array[A], Array[A]]): Array[A] =
      bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


    def mIf[A: ClassTag](trueBranch: => A, falseBranch: => A = {}): Array[A] =
      arrayToMonadicPredicate(bm).map(b => if(b) trueBranch else falseBranch)

    def mIf[A: ClassTag](ifBranches: IfBranches[A, A]): Array[A] =
      arrayToMonadicPredicate(bm).map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def fmIf[A: ClassTag](trueBranch: => Array[A], falseBranch: => Array[A]): Array[A] =
      arrayToMonadicPredicate(bm).flatMap(b => if(b) trueBranch else falseBranch)

    def fmIf[A: ClassTag](ifBranches: IfBranches[Array[A], Array[A]]): Array[A] =
      arrayToMonadicPredicate(bm).flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())
  }

  implicit def arrayToIfBranches[T: ClassTag](trueOnlyBranch: => Array[T]): IfBranches[Array[T], Array[T]] =
    new IfBranches[Array[T], Array[T]](() => trueOnlyBranch, () => Array.empty[T])

  private[bms] def arrayToMonadicPredicate(ba: Array[Boolean]): Array[Boolean] =
    if (ba.isEmpty) ba
    else if (ba.contains(true)) ArrayTrue
    else ArrayFalse


  /// Either

  private[bms] val RightFalse: Either[Nothing, Boolean] = Right(false)
  private[bms] val RightTrue:  Either[Nothing, Boolean] = Right(true)
  private[bms] val RightUnit:  Either[Nothing, Unit]    = Right(())

  private[bms] def EitherFalse[L]: Either[L, Boolean] = RightFalse
  private[bms] def EitherTrue[L]:  Either[L, Boolean] = RightTrue
  private[bms] def EitherUnit[L]:  Either[L, Unit]    = RightUnit

  implicit class EitherTypeConstantsSyntax(val t: Either.type) extends AnyVal {
    def False[L]: Either[L, Boolean] = EitherFalse[L]
    def True[L]:  Either[L, Boolean] = EitherTrue[L]
    def Unit[L]:  Either[L, Unit]    = EitherUnit[L]
  }

  implicit class BooleanAndEitherSyntax(val b: Boolean) extends AnyVal {

    def &&[L](mb: => Either[L, Boolean]): Either[L, Boolean] = if (b) mb else EitherFalse
    def ||[L](mb: => Either[L, Boolean]): Either[L, Boolean] = if (b) EitherTrue else mb

    def &[L](mb: Either[L, Boolean]): Either[L, Boolean] = mb.map(b & _)
    def ^[L](mb: Either[L, Boolean]): Either[L, Boolean] = mb.map(b ^ _)
    def |[L](mb: Either[L, Boolean]): Either[L, Boolean] = mb.map(b | _)
  }

  implicit class EitherAndBooleanOrEitherSyntax[L](val bm: Either[L, Boolean]) extends AnyVal {

    def &&(b: => Boolean, dummy: Null = null): Either[L, Boolean] = bm.map(_ && b)
    def ||(b: => Boolean, dummy: Null = null): Either[L, Boolean] = bm.map(_ || b)

    def &(b: Boolean): Either[L, Boolean] = bm.map(_ & b)
    def ^(b: Boolean): Either[L, Boolean] = bm.map(_ ^ b)
    def |(b: Boolean): Either[L, Boolean] = bm.map(_ | b)

    def unary_! : Either[L, Boolean] = bm.map(!_)

    def &&(mb: => Either[L, Boolean]): Either[L, Boolean] = bm.flatMap(_ && mb)
    def ||(mb: => Either[L, Boolean]): Either[L, Boolean] = bm.flatMap(_ || mb)

    def &(mb: Either[L, Boolean]): Either[L, Boolean] = bm.flatMap(_ & mb)
    def ^(mb: Either[L, Boolean]): Either[L, Boolean] = bm.flatMap(_ ^ mb)
    def |(mb: Either[L, Boolean]): Either[L, Boolean] = bm.flatMap(_ | mb)


    def mapIf[A](trueBranch: => A, falseBranch: => A = {}): Either[L, A] =
      bm.map(b => if(b) trueBranch else falseBranch)

    def mapIf[A](ifBranches: IfBranches[A, A]): Either[L, A] =
      bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def flatMapIf[A](trueBranch: => Either[L, A], falseBranch: => Either[L, A]): Either[L, A] =
      bm.flatMap(b => if(b) trueBranch else falseBranch)

    def flatMapIf[A](ifBranches: IfBranches[Either[L, A], Either[L, A]]): Either[L, A] =
      bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())


    def mIf[A](trueBranch: => A, falseBranch: => A = {}): Either[L, A] =
      bm.map(b => if(b) trueBranch else falseBranch)

    def mIf[A](ifBranches: IfBranches[A, A]): Either[L, A] =
      bm.map(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def fmIf[A](trueBranch: => Either[L, A], falseBranch: => Either[L, A]): Either[L, A] =
      bm.flatMap(b => if(b) trueBranch else falseBranch)

    def fmIf[A](ifBranches: IfBranches[Either[L, A], Either[L, A]]): Either[L, A] =
      bm.flatMap(b => if(b) ifBranches.trueBranch() else ifBranches.falseBranch())
  }

  implicit def EitherToIfBranches[L, T](trueOnlyBranch: => Either[L, T]): IfBranches[Either[L, T], Either[L, Unit]] =
    new IfBranches[Either[L, T], Either[L, Unit]](() => trueOnlyBranch, () => EitherUnit[L])
}
