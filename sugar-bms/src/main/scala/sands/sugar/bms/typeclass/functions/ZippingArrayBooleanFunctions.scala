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

import sands.sugar.bms.BooleanMonads.{ArrayFalse, ArrayTrue}
import sands.sugar.bms.typeclass.functions.BooleanFunctions._

import scala.reflect.ClassTag

/*
 * Created by Serhiy Shamshetdinov
 * at 19.08.2021 15:03
 */

class ZippingArrayBooleanFunctions extends BooleanFunctions[Array, ClassTag[Boolean]] {
  val name: String = s"$ZippingName Array functions"

  def apply(b: Boolean, bop: BooleanOperator, mbn: => Array[Boolean])(implicit c: ClassTag[Boolean]): Array[Boolean] = {
    // here lazy ops `&&` and `||` are never lazy since the length of mbn collection is unknown (0 or 1 length to return after optimization): mbn should be evaluated in any case
    val mbnValue = mbn
    if (mbnValue.isEmpty) mbnValue
    else if (bop == lAND && !b) ArrayFalse
    else if (bop == lOR && b) ArrayTrue
    else mbnValue.take(1).map(bop(b, _))
  }

  def apply(mb: Array[Boolean], bop: BooleanOperator, bn: => Boolean, dummy: Null = null)(implicit c: ClassTag[Boolean]): Array[Boolean] = {
    lazy val mbHead = mb.take(1)
    if (mb.isEmpty) mb
    else if (bop == lAND && !mbHead.contains(true) || bop == lOR && !mbHead.contains(false)) mbHead
    else mbHead.map(bop(_, bn))
  }

  def apply(mb: Array[Boolean], bop: BooleanOperator, mbn: => Array[Boolean])(implicit c: ClassTag[Boolean]): Array[Boolean] = {
    // here lazy mbn evaluation in `&&` and `||` works only in case mb is empty otherwise we should know the length of mbn collection (so, evaluate it)
    if (mb.isEmpty) mb
    else mb.zip(mbn).map(z => bop(z._1, z._2))
  }
}
