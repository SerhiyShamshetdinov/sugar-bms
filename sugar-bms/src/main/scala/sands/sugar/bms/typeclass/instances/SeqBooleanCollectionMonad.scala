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
 * at 07.08.2021 04:28
 */

trait SeqBooleanCollectionMonad extends BooleanCollectionMonad[Seq, NoContext] {
  def name: String = "Seq BooleanCollectionMonad"

  def empty[A]: Seq[A] = Seq.empty[A]
  def pure[A](a: A): Seq[A] = Seq(a)
  def pure[A](a: A*): Seq[A] = a.toSeq

  def map[A, B](ma: Seq[A], f: A => B)(implicit c: NoContext): Seq[B] = ma.map(f)
  def flatMap[A, B](ma: Seq[A], f: A => Seq[B])(implicit c: NoContext): Seq[B] = ma.flatMap(f)

  def isEmpty(bc: Seq[Boolean])(implicit c: NoContext): Boolean = bc.isEmpty

  def forallIsFalse(bc: Seq[Boolean])(implicit c: NoContext): Boolean = !bc.contains(true)
  def forallIsTrue(bc: Seq[Boolean])(implicit c: NoContext): Boolean = !bc.contains(false)

  def takeHead(bc: Seq[Boolean])(implicit c: NoContext): Seq[Boolean] = bc.take(1)

  def zip(bc1: Seq[Boolean], bc2: Seq[Boolean])(implicit c: NoContext): Seq[(Boolean, Boolean)] = bc1.zip(bc2)
}
