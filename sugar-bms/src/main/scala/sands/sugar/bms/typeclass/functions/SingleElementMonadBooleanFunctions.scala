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

import sands.sugar.bms.typeclass.BooleanMonad
import sands.sugar.bms.typeclass.functions.BooleanFunctions._

/*
 * Created by Serhiy Shamshetdinov
 * at 07.08.2021 04:25
 */

trait SingleElementMonadBooleanFunctions[M[_], C] extends BooleanFunctions[M, C] { _: BooleanMonad[M, C] =>
  abstract override val name: String = super.name + " with Single Element Monad functions"

  def apply(b: Boolean, bop: BooleanOperator, mbn: => M[Boolean])(implicit c: C): M[Boolean] =
    if (bop == lAND && !b) False
    else if (bop == lOR && b) True
    else map[Boolean, Boolean](mbn, bop(b, _))

  def apply(mb: M[Boolean], bop: BooleanOperator, bn: => Boolean, dummy: Null = null)(implicit c: C): M[Boolean] =
    map[Boolean, Boolean](mb, bop(_, bn))

  def apply(mb: M[Boolean], bop: BooleanOperator, mbn: => M[Boolean])(implicit c: C): M[Boolean] =
    flatMap[Boolean, Boolean](mb, apply(_, bop, mbn))
}
