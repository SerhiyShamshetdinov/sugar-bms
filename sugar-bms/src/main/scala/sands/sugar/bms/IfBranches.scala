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

import scala.language.implicitConversions

/*
 * Created by Serhiy Shamshetdinov
 * at 27.07.2021 17:41
 */

class IfBranches[+T, +F](val trueBranch: () => T, val falseBranch: () => F)

object IfBranches {

  implicit def anyToIfBranches[T](trueOnlyBranch: => T): IfBranches[T, Unit] =
    new IfBranches[T, Unit](() => trueOnlyBranch, () => ())
}
