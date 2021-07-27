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

package sands.sugar.bms.typeclass.functions

import sands.sugar.bms.typeclass.NoContext
import sands.sugar.bms.typeclass.functions.BooleanFunctions.BooleanOperator

import scala.reflect.ClassTag

/*
 * Created by Serhiy Shamshetdinov
 * at 06.08.2021 18:22
 */

trait BooleanFunctions[M[_], C] {
  def name: String

  def apply(b:  Boolean, bop: BooleanOperator, mbn: => M[Boolean])(implicit c: C): M[Boolean]
  def apply(mb: M[Boolean], bop: BooleanOperator, bn: => Boolean, dummy: Null = null)(implicit c: C): M[Boolean]
  def apply(mb: M[Boolean], bop: BooleanOperator, mbn: => M[Boolean])(implicit c: C): M[Boolean]
}

object BooleanFunctions {
  def apply[M[_]](implicit BF: BooleanFunctions[M, NoContext]): BooleanFunctions[M, NoContext] = BF
  def apply[M[_], C](implicit BF: BooleanFunctions[M, C], c: C): BooleanFunctions[M, C] = BF

  val ZippingName = "Zipping"
  val MonadicName = "Monadic"

  trait BooleanOperator extends {
    def apply(b1: Boolean, b2: => Boolean): Boolean
  }

  val lAND: BooleanOperator = new BooleanOperator {
    def apply(b1: Boolean, b2: => Boolean): Boolean = b1 && b2
  }
  val lOR: BooleanOperator = new BooleanOperator {
    def apply(b1: Boolean, b2: => Boolean): Boolean = b1 || b2
  }

  val AND: BooleanOperator = new BooleanOperator {
    def apply(b1: Boolean, b2: => Boolean): Boolean = b1 & b2
  }
  val XOR: BooleanOperator = new BooleanOperator {
    def apply(b1: Boolean, b2: => Boolean): Boolean = b1 ^ b2
  }
  val OR: BooleanOperator = new BooleanOperator {
    def apply(b1: Boolean, b2: => Boolean): Boolean = b1 | b2
  }

  implicit val arrayZippingBooleanFunctions: BooleanFunctions[Array, ClassTag[Boolean]] = new ZippingArrayBooleanFunctions

  object ArrayAsMonad {
    implicit val arrayMonadicBooleanFunctions: BooleanFunctions[Array, ClassTag[Boolean]] = new MonadicArrayBooleanFunctions
  }
}
