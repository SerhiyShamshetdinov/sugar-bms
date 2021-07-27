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

import sands.sugar.bms.typeclass.BooleanMonad

import scala.concurrent.{ExecutionContext, Future}

/*
 * Created by Serhiy Shamshetdinov
 * at 16.08.2021 01:58
 */

trait FutureBooleanMonad extends BooleanMonad[Future, ExecutionContext] {
  def name: String = "Future BooleanMonad"

  def pure[A](a: A): Future[A] = Future.successful(a)

  def map[A, B](ma: Future[A], f: A => B)(implicit c: ExecutionContext): Future[B] = ma.map(f)

  def flatMap[A, B](ma: Future[A], f: A => Future[B])(implicit c: ExecutionContext): Future[B] = ma.flatMap(f)

  final def tailRecLoop(bodyWithCondition: => Future[Boolean])(implicit c: ExecutionContext): Future[Boolean] =
    bodyWithCondition.flatMap(p => if (p) tailRecLoop(bodyWithCondition) else False)
}
