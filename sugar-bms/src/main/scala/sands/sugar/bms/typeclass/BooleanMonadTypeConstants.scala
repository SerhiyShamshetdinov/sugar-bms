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
import sands.sugar.bms.typeclass.BooleanMonad.{futureBooleanMonad, indexedSeqZippingBooleanMonad, iterableZippingBooleanMonad, listZippingBooleanMonad, seqZippingBooleanMonad, streamZippingBooleanMonad, vectorZippingBooleanMonad}

import scala.concurrent.Future

/*
 * Created by Serhiy Shamshetdinov
 * at 19.08.2021 20:22
 */

trait BooleanMonadTypeConstants {

  def True[M[_]](implicit BM: BooleanMonad[M, NoContext]): M[Boolean] = BM.True
  def True[M[_], C](implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM.True

  def False[M[_]](implicit BM: BooleanMonad[M, NoContext]): M[Boolean] = BM.False
  def False[M[_], C](implicit BM: BooleanMonad[M, C], c: C): M[Boolean] = BM.False

  def Unit[M[_]](implicit BM: BooleanMonad[M, NoContext]): M[Unit] = BM.Unit
  def Unit[M[_], C](implicit BM: BooleanMonad[M, C], c: C): M[Unit] = BM.Unit

  implicit class FutureTypeConstantsSyntax(val t: Future.type) {
    def False: Future[Boolean] = futureBooleanMonad.False
    def True: Future[Boolean] = futureBooleanMonad.True
    def Unit: Future[Unit] = futureBooleanMonad.Unit
  }

  implicit class ListTypeConstantsSyntax(val t: List.type) {
    def False: List[Boolean] = listZippingBooleanMonad.False
    def True: List[Boolean] = listZippingBooleanMonad.True
    def Unit: List[Unit] = listZippingBooleanMonad.Unit
  }

  implicit class SeqTypeConstantsSyntax(val t: Seq.type) {
    def False: Seq[Boolean] = seqZippingBooleanMonad.False
    def True: Seq[Boolean] = seqZippingBooleanMonad.True
    def Unit: Seq[Unit] = seqZippingBooleanMonad.Unit
  }

  implicit class IndexedSeqTypeConstantsSyntax(val t: IndexedSeq.type) {
    def False: IndexedSeq[Boolean] = indexedSeqZippingBooleanMonad.False
    def True: IndexedSeq[Boolean] = indexedSeqZippingBooleanMonad.True
    def Unit: IndexedSeq[Unit] = indexedSeqZippingBooleanMonad.Unit
  }

  implicit class VectorTypeConstantsSyntax(val t: Vector.type) {
    def False: Vector[Boolean] = vectorZippingBooleanMonad.False
    def True: Vector[Boolean] = vectorZippingBooleanMonad.True
    def Unit: Vector[Unit] = vectorZippingBooleanMonad.Unit
  }

  implicit class IterableTypeConstantsSyntax(val t: Iterable.type) {
    def False: Iterable[Boolean] = iterableZippingBooleanMonad.False
    def True: Iterable[Boolean] = iterableZippingBooleanMonad.True
    def Unit: Iterable[Unit] = iterableZippingBooleanMonad.Unit
  }

  implicit class StreamTypeConstantsSyntax(val t: Stream.type) {
    def False: Stream[Boolean] = streamZippingBooleanMonad.False
    def True: Stream[Boolean] = streamZippingBooleanMonad.True
    def Unit: Stream[Unit] = streamZippingBooleanMonad.Unit
  }
}
