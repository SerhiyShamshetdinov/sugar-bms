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

package sands.sugar.bms.list

import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.BooleanMonad.{CollectionsAsMonad, listZippingBooleanMonad}
import sands.sugar.bms.{ElseExtension => _, _}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class ListZippedGeneralTest extends BmsCollectionGeneralTest(ListTestData) with ListTypeTest
class ListZippedIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)
class ListZippedBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)
class ListZippedTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)
class ListZippedQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)
class ListZippedQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)
class ListZippedQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)
class ListZippedQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)
class ListZippedQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, ListTestData)(listZippingBooleanMonad.name)

class ListMonadicGeneralTest extends BmsCollectionGeneralTest(ListTestData)(CollectionsAsMonad.listBooleanMonad) with ListTypeTest
class ListMonadicIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)
class ListMonadicBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)
class ListMonadicTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)
class ListMonadicQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)
class ListMonadicQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)
class ListMonadicQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)
class ListMonadicQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)
class ListMonadicQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, ListTestData)(CollectionsAsMonad.listBooleanMonad.name)

trait ListTypeTest extends BmsTestBase {
  def testData: BmsCollectionTestData[List]

  it should "provide type constants" in {
    List.False shouldBe testData.PureFalse
    List.True shouldBe testData.PureTrue
    List.Unit shouldBe testData.PureUnit
  }
}
