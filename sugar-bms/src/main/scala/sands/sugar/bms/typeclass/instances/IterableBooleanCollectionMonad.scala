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

trait IterableBooleanCollectionMonad extends BooleanCollectionMonad[Iterable, NoContext] {
  def name: String = "Iterable BooleanCollectionMonad"

  def empty[A]: Iterable[A] = Iterable.empty[A]
  def pure[A](a: A): Iterable[A] = Iterable(a)
  def pure[A](a: A*): Iterable[A] = a.toIterable

  def map[A, B](ma: Iterable[A], f: A => B)(implicit c: NoContext): Iterable[B] = ma.map(f)
  def flatMap[A, B](ma: Iterable[A], f: A => Iterable[B])(implicit c: NoContext): Iterable[B] = ma.flatMap(f)

  def isEmpty(bc: Iterable[Boolean])(implicit c: NoContext): Boolean = bc.isEmpty

  def forallIsFalse(bc: Iterable[Boolean])(implicit c: NoContext): Boolean = !bc.exists(_ == true) // generally faster than forall(_ == false)
  def forallIsTrue(bc: Iterable[Boolean])(implicit c: NoContext): Boolean = !bc.exists(_ == false) // generally faster than forall(_ == true)

  def takeHead(bc: Iterable[Boolean])(implicit c: NoContext): Iterable[Boolean] = bc.take(1)

  def zip(bc1: Iterable[Boolean], bc2: Iterable[Boolean])(implicit c: NoContext): Iterable[(Boolean, Boolean)] = bc1.zip(bc2)
}
