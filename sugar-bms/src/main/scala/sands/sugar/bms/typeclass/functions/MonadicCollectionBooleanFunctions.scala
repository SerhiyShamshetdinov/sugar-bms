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

import sands.sugar.bms.typeclass.BooleanCollectionMonad
import sands.sugar.bms.typeclass.functions.BooleanFunctions._

/*
 * Created by Serhiy Shamshetdinov
 * at 07.08.2021 04:25
 */

trait MonadicCollectionBooleanFunctions[M[_], C] extends BooleanFunctions[M, C] { _: BooleanCollectionMonad[M, C] =>
  abstract override val name: String = super.name + s" with $MonadicName Collection functions"

  def apply(b: Boolean, bop: BooleanOperator, mbn: => M[Boolean])(implicit c: C): M[Boolean] =
    // here lazy ops `&&` and `||` are never lazy since the length of mbn collection is unknown: mbn should be evaluated in any case
    map[Boolean, Boolean](mbn, bop(b, _))

  def apply(mb: M[Boolean], bop: BooleanOperator, bn: => Boolean, dummy: Null = null)(implicit c: C): M[Boolean] =
    if (isEmpty(mb) || bop == lAND && forallIsFalse(mb) || bop == lOR && forallIsTrue(mb)) mb
    else {
      // here bn is evaluated only once (not per each mb element!)
      val bnValue = bn
      map[Boolean, Boolean](mb, bop(_, bnValue))
    }

  def apply(mb: M[Boolean], bop: BooleanOperator, mbn: => M[Boolean])(implicit c: C): M[Boolean] =
    // here lazy mbn evaluation in `&&` and `||` works only in case mb is empty otherwise we should know the length of mbn collection (so, evaluate it)
    if (isEmpty(mb)) mb
    else {
      // here mbn is evaluated only once (not per each mb element!).
      // Further lazy optimization is not possible here since for any mb element (even when all are `true` or `false`) we should know mbn length thus should evaluate mbn
      val mbnValue = mbn
      flatMap[Boolean, Boolean](mb, apply(_, bop, mbnValue))
    }
}
