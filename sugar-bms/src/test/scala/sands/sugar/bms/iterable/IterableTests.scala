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

package sands.sugar.bms.iterable

import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.BooleanMonad.{CollectionsAsMonad, iterableZippingBooleanMonad}
import sands.sugar.bms.{ElseExtension => _, _}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class IterableZippedGeneralTest extends BmsCollectionGeneralTest(IterableTestData) with IterableTypeTest
class IterableZippedIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)
class IterableZippedBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)
class IterableZippedTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)
class IterableZippedQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)
class IterableZippedQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)
class IterableZippedQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)
class IterableZippedQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)
class IterableZippedQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, IterableTestData)(iterableZippingBooleanMonad.name)

class IterableMonadicGeneralTest extends BmsCollectionGeneralTest(IterableTestData)(CollectionsAsMonad.iterableBooleanMonad) with IterableTypeTest
class IterableMonadicIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)
class IterableMonadicBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)
class IterableMonadicTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)
class IterableMonadicQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)
class IterableMonadicQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)
class IterableMonadicQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)
class IterableMonadicQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)
class IterableMonadicQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, IterableTestData)(CollectionsAsMonad.iterableBooleanMonad.name)

trait IterableTypeTest extends BmsTestBase {
  def testData: BmsCollectionTestData[Iterable]

  it should "provide type constants" in {
    Iterable.False shouldBe testData.PureFalse
    Iterable.True shouldBe testData.PureTrue
    Iterable.Unit shouldBe testData.PureUnit
  }
}
