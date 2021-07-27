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

import scala.annotation.tailrec

/*
 * Created by Serhiy Shamshetdinov
 * at 27.07.2021 17:43
 */

object BooleanCollectionMonad {
  def apply[M[_]](implicit BCM: BooleanCollectionMonad[M, NoContext]): BooleanCollectionMonad[M, NoContext] = BCM
  def apply[M[_], C](implicit BCM: BooleanCollectionMonad[M, C], c: C): BooleanCollectionMonad[M, C] = BCM
}

trait BooleanCollectionMonad[M[_], C] extends BooleanMonad[M, C] {
  def empty[A]: M[A]
  def pure[A](a: A*): M[A]

  /** For collections: "monad is true" when at least 1 `true` exists in the collection */
  override def toMonadicPredicate(mb: M[Boolean])(implicit c: C): M[Boolean] = // takeHead(mb).map(_ => !forallIsFalse(mb))
    if (isEmpty(mb)) mb
    else if (forallIsFalse(mb)) False
    else True

  @tailrec
  override final def tailRecLoop(bodyWithCondition: => M[Boolean])(implicit c: C): M[Boolean] = {
    val predicate = toMonadicPredicate(bodyWithCondition)
    if (forallIsFalse(predicate)) predicate else tailRecLoop(bodyWithCondition)
  }

  def isEmpty(bc: M[Boolean])(implicit c: C): Boolean

  def forallIsFalse(bc: M[Boolean])(implicit c: C): Boolean
  def forallIsTrue(bc: M[Boolean])(implicit c: C): Boolean

  def takeHead(bc: M[Boolean])(implicit c: C): M[Boolean]

  def zip(bc1: M[Boolean], bc2: M[Boolean])(implicit c: C): M[(Boolean, Boolean)]
}


