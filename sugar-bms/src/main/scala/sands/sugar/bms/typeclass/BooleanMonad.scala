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

import sands.sugar.bms.typeclass.functions._
import sands.sugar.bms.typeclass.instances._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/*
 * Created by Serhiy Shamshetdinov
 * at 27.07.2021 17:43
 */

trait BooleanMonad[M[_], C] extends BooleanFunctions[M, C] {
  def pure[A](a: A): M[A]

  val False: M[Boolean] = pure(false)
  val True:  M[Boolean] = pure(true)
  val Unit:  M[Unit] =    pure[Unit]({})

  def map[A, B](ma: M[A], f: A => B)(implicit c: C): M[B]
  def flatMap[A, B](ma: M[A], f: A => M[B])(implicit c: C): M[B]

  /** returns monadic predicate which stopped the loop: "zero"-elements (error, empty, None, etc.) or one-element `false` inside a monad */
  def tailRecLoop(bodyWithCondition: => M[Boolean])(implicit c: C): M[Boolean]

  /** Used as predicate of `mIf` & `fmIf` statements and loops. For single element monads it is `identity`.
   * For collections it returns reduced collection (1 or 0 elements) with 'true' if collection contains 'true'.
   *
   * Override it to use another mapping of collection to monadic predicate.
   */
  def toMonadicPredicate(mb: M[Boolean])(implicit c: C): M[Boolean] = mb // only for single element monads: override it for collections
}

object BooleanMonad {
  def apply[M[_]](implicit BM: BooleanMonad[M, NoContext]): BooleanMonad[M, NoContext] = BM
  def apply[M[_], C](implicit BM: BooleanMonad[M, C], c: C): BooleanMonad[M, C] = BM

  implicit val futureBooleanMonad: BooleanMonad[Future, ExecutionContext] = new FutureBooleanMonad with SingleElementMonadBooleanFunctions[Future, ExecutionContext]

  implicit val listZippingBooleanMonad: BooleanCollectionMonad[List, NoContext] = new ListBooleanCollectionMonad with ZippingCollectionBooleanFunctions[List, NoContext]
  implicit val seqZippingBooleanMonad: BooleanCollectionMonad[Seq, NoContext] = new SeqBooleanCollectionMonad with ZippingCollectionBooleanFunctions[Seq, NoContext]
  implicit val indexedSeqZippingBooleanMonad: BooleanCollectionMonad[IndexedSeq, NoContext] = new IndexedSeqBooleanCollectionMonad with ZippingCollectionBooleanFunctions[IndexedSeq, NoContext]
  implicit val vectorZippingBooleanMonad: BooleanCollectionMonad[Vector, NoContext] = new VectorBooleanCollectionMonad with ZippingCollectionBooleanFunctions[Vector, NoContext]
  implicit val iterableZippingBooleanMonad: BooleanCollectionMonad[Iterable, NoContext] = new IterableBooleanCollectionMonad with ZippingCollectionBooleanFunctions[Iterable, NoContext]
  implicit val streamZippingBooleanMonad: BooleanCollectionMonad[Stream, NoContext] = new StreamBooleanCollectionMonad with ZippingCollectionBooleanFunctions[Stream, NoContext]

  object CollectionsAsMonad {
    implicit val listBooleanMonad: BooleanCollectionMonad[List, NoContext] = new ListBooleanCollectionMonad with MonadicCollectionBooleanFunctions[List, NoContext]
    implicit val seqBooleanMonad: BooleanCollectionMonad[Seq, NoContext] = new SeqBooleanCollectionMonad with MonadicCollectionBooleanFunctions[Seq, NoContext]
    implicit val indexedSeqBooleanMonad: BooleanCollectionMonad[IndexedSeq, NoContext] = new IndexedSeqBooleanCollectionMonad with MonadicCollectionBooleanFunctions[IndexedSeq, NoContext]
    implicit val vectorBooleanMonad: BooleanCollectionMonad[Vector, NoContext] = new VectorBooleanCollectionMonad with MonadicCollectionBooleanFunctions[Vector, NoContext]
    implicit val iterableBooleanMonad: BooleanCollectionMonad[Iterable, NoContext] = new IterableBooleanCollectionMonad with MonadicCollectionBooleanFunctions[Iterable, NoContext]
    implicit val streamBooleanMonad: BooleanCollectionMonad[Stream, NoContext] = new StreamBooleanCollectionMonad with MonadicCollectionBooleanFunctions[Stream, NoContext]

    implicit val arrayMonadicBooleanFunctions: BooleanFunctions[Array, ClassTag[Boolean]] = sands.sugar.bms.typeclass.functions.BooleanFunctions.ArrayAsMonad.arrayMonadicBooleanFunctions
  }
}
