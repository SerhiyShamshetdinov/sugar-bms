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

package sands.sugar.bms

import scala.util.{Either, Right}

/*
 * Created by Serhiy Shamshetdinov
 * at 21.08.2021 05:05
 */

object Scala211Helper {

  /** 2.11 helper methods copied from Either.scala of Scala 2.13.6 */
  implicit class Either211Syntax[A, B](val either: Either[A, B]) extends AnyVal {

    /** Binds the given function across `Right`.
     *
     *  @param f The function to bind across `Right`.
     */
    def flatMap[A1 >: A, B1](f: B => Either[A1, B1]): Either[A1, B1] = either match {
      case Right(b) => f(b)
      case _        => either.asInstanceOf[Either[A1, B1]]
    }

    /** The given function is applied if this is a `Right`.
     *
     *  {{{
     *  Right(12).map(x => "flower") // Result: Right("flower")
     *  Left(12).map(x => "flower")  // Result: Left(12)
     *  }}}
     */
    def map[B1](f: B => B1): Either[A, B1] = either match {
      case Right(b) => Right(f(b))
      case _        => either.asInstanceOf[Either[A, B1]]
    }
  }
}
