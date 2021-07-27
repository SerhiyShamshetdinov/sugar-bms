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

package sands.sugar.bms.vector

import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.BooleanMonad.{CollectionsAsMonad, vectorZippingBooleanMonad}
import sands.sugar.bms.{ElseExtension => _, _}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class VectorZippedGeneralTest extends BmsCollectionGeneralTest(VectorTestData) with VectorTypeTest
class VectorZippedIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)
class VectorZippedBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)
class VectorZippedTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)
class VectorZippedQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)
class VectorZippedQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)
class VectorZippedQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)
class VectorZippedQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)
class VectorZippedQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, VectorTestData)(vectorZippingBooleanMonad.name)

class VectorMonadicGeneralTest extends BmsCollectionGeneralTest(VectorTestData)(CollectionsAsMonad.vectorBooleanMonad) with VectorTypeTest
class VectorMonadicIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)
class VectorMonadicBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)
class VectorMonadicTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)
class VectorMonadicQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)
class VectorMonadicQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)
class VectorMonadicQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)
class VectorMonadicQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)
class VectorMonadicQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, VectorTestData)(CollectionsAsMonad.vectorBooleanMonad.name)

trait VectorTypeTest extends BmsTestBase {
  def testData: BmsCollectionTestData[Vector]

  it should "provide type constants" in {
    Vector.False shouldBe testData.PureFalse
    Vector.True shouldBe testData.PureTrue
    Vector.Unit shouldBe testData.PureUnit
  }
}
