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

package sands.sugar.bms.typeclass

import sands.sugar.bms.IfBranches
import sands.sugar.bms.typeclass.functions.BooleanFunctions.{AND, OR, XOR, lAND, lOR}

/*
 * Created by Serhiy Shamshetdinov
 * at 19.08.2021 20:22
 */

trait BooleanMonadSyntax extends BooleanMonadSyntaxLowPriority with BooleanMonadTypeConstants {

  implicit class BooleanAndMonadSyntax(b: Boolean) {

    def &&[M[_], C](mb: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(b, lAND, mb)
    def ||[M[_], C](mb: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(b, lOR, mb)

    def &[M[_], C](mb: M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(b, AND, mb)
    def ^[M[_], C](mb: M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(b, XOR, mb)
    def |[M[_], C](mb: M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(b, OR, mb)
  }

  implicit class MonadAndBooleanOrMonadSyntax[M[_]](bm: M[Boolean]) {

    def &&[C](b: => Boolean, dummy: Null = null)(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, lAND, b)
    def ||[C](b: => Boolean, dummy: Null = null)(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, lOR, b)

    // 3 following non lazy boolean ops also have call-by-name b parameter to provide correct
    // (like sync boolean logic has) evaluation order with Future monads.
    // To see async reordering comment `=>` and run FutureTernaryExpressionsTest or FutureQuaternaryExpressionsPage* tests
    def &[C](b: => Boolean, dummy: Null = null)(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, AND, b)
    def ^[C](b: => Boolean, dummy: Null = null)(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, XOR, b)
    def |[C](b: => Boolean, dummy: Null = null)(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, OR, b)

    def unary_![C](implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM.map[Boolean, Boolean](bm, b => !b)

    def &&[C](mb: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, lAND, mb)
    def ||[C](mb: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, lOR, mb)

    // 3 following non lazy boolean ops also have call-by-name mb parameter to provide correct
    // (like sync boolean logic has) evaluation order with Future monads.
    // To see async reordering comment `=>` and run FutureTernaryExpressionsTest or FutureQuaternaryExpressionsPage* tests
    def &[C](mb: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, AND, mb)
    def ^[C](mb: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, XOR, mb)
    def |[C](mb: => M[Boolean])(implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM(bm, OR, mb)


    def mapIf[A, C](trueBranch: => A, falseBranch: => A = {})(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.map[Boolean, A](bm, b => if (b) trueBranch else falseBranch)

    def mapIf[A, C](ifBranches: IfBranches[A, A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.map[Boolean, A](bm, b => if (b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def flatMapIf[A, C](trueBranch: => M[A], falseBranch: => M[A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.flatMap[Boolean, A](bm, b => if (b) trueBranch else falseBranch)

    def flatMapIf[A, C](ifBranches: IfBranches[M[A], M[A]])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.flatMap[Boolean, A](bm, b => if (b) ifBranches.trueBranch() else ifBranches.falseBranch())


    def mIf[A, C](trueBranch: => A, falseBranch: => A = {})(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.map[Boolean, A](BM.toMonadicPredicate(bm), b => if (b) trueBranch else falseBranch)

    def mIf[A, C](ifBranches: IfBranches[A, A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.map[Boolean, A](BM.toMonadicPredicate(bm), b => if (b) ifBranches.trueBranch() else ifBranches.falseBranch())

    def fmIf[A, C](trueBranch: => M[A], falseBranch: => M[A])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.flatMap[Boolean, A](BM.toMonadicPredicate(bm), b => if (b) trueBranch else falseBranch)

    def fmIf[A, C](ifBranches: IfBranches[M[A], M[A]])(implicit BM: BooleanMonad[M, C], c: C): M[A] =
      BM.flatMap[Boolean, A](BM.toMonadicPredicate(bm), b => if (b) ifBranches.trueBranch() else ifBranches.falseBranch())
  }

  implicit def collectionMonadToIfBranches[M[_], T, C](trueOnlyBranch: => M[T])(implicit BCM: BooleanCollectionMonad[M, C], c: C): IfBranches[M[T], M[T]] =
    new IfBranches[M[T], M[T]](() => trueOnlyBranch, () => BCM.empty[T])
}

trait BooleanMonadSyntaxLowPriority {

  implicit def monadToIfBranches[M[_], T, C](trueOnlyBranch: => M[T])(implicit BM: BooleanMonad[M, C], c: C): IfBranches[M[T], M[Unit]] =
    new IfBranches[M[T], M[Unit]](() => trueOnlyBranch, () => BM.Unit)
}