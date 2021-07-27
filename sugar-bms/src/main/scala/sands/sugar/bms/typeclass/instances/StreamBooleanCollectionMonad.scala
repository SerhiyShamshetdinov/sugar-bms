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

trait StreamBooleanCollectionMonad extends BooleanCollectionMonad[Stream, NoContext] {
  def name: String = "Stream BooleanCollectionMonad"

  def empty[A]: Stream[A] = Stream.empty[A]
  def pure[A](a: A): Stream[A] = Stream(a)
  def pure[A](a: A*): Stream[A] = a.toStream

  def map[A, B](ma: Stream[A], f: A => B)(implicit c: NoContext): Stream[B] = ma.map(f)
  def flatMap[A, B](ma: Stream[A], f: A => Stream[B])(implicit c: NoContext): Stream[B] = ma.flatMap(f)

  def isEmpty(bc: Stream[Boolean])(implicit c: NoContext): Boolean = bc.isEmpty

  def forallIsFalse(bc: Stream[Boolean])(implicit c: NoContext): Boolean = !bc.contains(true)
  def forallIsTrue(bc: Stream[Boolean])(implicit c: NoContext): Boolean = !bc.contains(false)

  def takeHead(bc: Stream[Boolean])(implicit c: NoContext): Stream[Boolean] = bc.take(1)

  def zip(bc1: Stream[Boolean], bc2: Stream[Boolean])(implicit c: NoContext): Stream[(Boolean, Boolean)] = bc1.zip(bc2)
}
