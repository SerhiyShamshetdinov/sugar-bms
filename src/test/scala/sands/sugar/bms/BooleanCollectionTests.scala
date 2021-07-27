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

/*
 * Created by Serhiy Shamshetdinov
 * at 26.08.2021 20:33
 */

class ZippingBooleanListTest extends ZippingBooleanCollectionFeaturesTest[List]
class ZippingBooleanSeqTest extends ZippingBooleanCollectionFeaturesTest[Seq]
class ZippingBooleanIndexedSeqTest extends ZippingBooleanCollectionFeaturesTest[IndexedSeq]
class ZippingBooleanVectorTest extends ZippingBooleanCollectionFeaturesTest[Vector]
class ZippingBooleanIterableTest extends ZippingBooleanCollectionFeaturesTest[Iterable]
class ZippingBooleanStreamTest extends ZippingBooleanCollectionFeaturesTest[Stream]

import sands.sugar.bms.typeclass.BooleanMonad.CollectionsAsMonad._

class MonadicBooleanListTest extends MonadicBooleanCollectionFeaturesTest[List]
class MonadicBooleanSeqTest extends MonadicBooleanCollectionFeaturesTest[Seq]
class MonadicBooleanIndexedSeqTest extends MonadicBooleanCollectionFeaturesTest[IndexedSeq]
class MonadicBooleanVectorTest extends MonadicBooleanCollectionFeaturesTest[Vector]
class MonadicBooleanIterableTest extends MonadicBooleanCollectionFeaturesTest[Iterable]
class MonadicBooleanStreamTest extends MonadicBooleanCollectionFeaturesTest[Stream]
