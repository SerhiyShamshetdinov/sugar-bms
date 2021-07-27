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

package sands.sugar.bms.typeclass.instances

import sands.sugar.bms.typeclass.{BooleanCollectionMonad, NoContext}

/*
 * Created by Serhiy Shamshetdinov
 * at 20.08.2021 16:28
 */

trait IndexedSeqBooleanCollectionMonad extends BooleanCollectionMonad[IndexedSeq, NoContext] {
  def name: String = "IndexedSeq BooleanCollectionMonad"

  def empty[A]: IndexedSeq[A] = IndexedSeq.empty[A]
  def pure[A](a: A): IndexedSeq[A] = IndexedSeq(a)
  def pure[A](a: A*): IndexedSeq[A] = a.toIndexedSeq

  def map[A, B](ma: IndexedSeq[A], f: A => B)(implicit c: NoContext): IndexedSeq[B] = ma.map(f)
  def flatMap[A, B](ma: IndexedSeq[A], f: A => IndexedSeq[B])(implicit c: NoContext): IndexedSeq[B] = ma.flatMap(f)

  def isEmpty(bc: IndexedSeq[Boolean])(implicit c: NoContext): Boolean = bc.isEmpty

  def forallIsFalse(bc: IndexedSeq[Boolean])(implicit c: NoContext): Boolean = !bc.contains(true)
  def forallIsTrue(bc: IndexedSeq[Boolean])(implicit c: NoContext): Boolean = !bc.contains(false)

  def takeHead(bc: IndexedSeq[Boolean])(implicit c: NoContext): IndexedSeq[Boolean] = bc.take(1)

  def zip(bc1: IndexedSeq[Boolean], bc2: IndexedSeq[Boolean])(implicit c: NoContext): IndexedSeq[(Boolean, Boolean)] = bc1.zip(bc2)
}
