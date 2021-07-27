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

trait VectorBooleanCollectionMonad extends BooleanCollectionMonad[Vector, NoContext] {
  def name: String = "Vector BooleanCollectionMonad"

  def empty[A]: Vector[A] = Vector.empty[A]
  def pure[A](a: A): Vector[A] = Vector(a)
  def pure[A](a: A*): Vector[A] = a.toVector

  def map[A, B](ma: Vector[A], f: A => B)(implicit c: NoContext): Vector[B] = ma.map(f)
  def flatMap[A, B](ma: Vector[A], f: A => Vector[B])(implicit c: NoContext): Vector[B] = ma.flatMap(f)

  def isEmpty(bc: Vector[Boolean])(implicit c: NoContext): Boolean = bc.isEmpty

  def forallIsFalse(bc: Vector[Boolean])(implicit c: NoContext): Boolean = !bc.contains(true)
  def forallIsTrue(bc: Vector[Boolean])(implicit c: NoContext): Boolean = !bc.contains(false)

  def takeHead(bc: Vector[Boolean])(implicit c: NoContext): Vector[Boolean] = bc.take(1)

  def zip(bc1: Vector[Boolean], bc2: Vector[Boolean])(implicit c: NoContext): Vector[(Boolean, Boolean)] = bc1.zip(bc2)
}
