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

trait ListBooleanCollectionMonad extends BooleanCollectionMonad[List, NoContext] {
  def name: String = "List BooleanCollectionMonad"

  def empty[A]: List[A] = List.empty[A]
  def pure[A](a: A): List[A] = List(a)
  def pure[A](a: A*): List[A] = a.toList

  def map[A, B](ma: List[A], f: A => B)(implicit c: NoContext): List[B] = ma.map(f)
  def flatMap[A, B](ma: List[A], f: A => List[B])(implicit c: NoContext): List[B] = ma.flatMap(f)

  def isEmpty(bc: List[Boolean])(implicit c: NoContext): Boolean = bc.isEmpty

  def forallIsFalse(bc: List[Boolean])(implicit c: NoContext): Boolean = !bc.contains(true)
  def forallIsTrue(bc: List[Boolean])(implicit c: NoContext): Boolean = !bc.contains(false)

  def takeHead(bc: List[Boolean])(implicit c: NoContext): List[Boolean] = bc.take(1)

  def zip(bc1: List[Boolean], bc2: List[Boolean])(implicit c: NoContext): List[(Boolean, Boolean)] = bc1.zip(bc2)
}
