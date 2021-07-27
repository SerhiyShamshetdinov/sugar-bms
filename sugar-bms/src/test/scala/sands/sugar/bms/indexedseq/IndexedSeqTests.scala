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

package sands.sugar.bms.indexedseq

import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.BooleanMonad.{CollectionsAsMonad, indexedSeqZippingBooleanMonad}
import sands.sugar.bms.{ElseExtension => _, _}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class IndexedSeqZippedGeneralTest extends BmsCollectionGeneralTest(IndexedSeqTestData) with IndexedSeqTypeTest
class IndexedSeqZippedIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)
class IndexedSeqZippedBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)
class IndexedSeqZippedTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)
class IndexedSeqZippedQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)
class IndexedSeqZippedQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)
class IndexedSeqZippedQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)
class IndexedSeqZippedQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)
class IndexedSeqZippedQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, IndexedSeqTestData)(indexedSeqZippingBooleanMonad.name)

class IndexedSeqMonadicGeneralTest extends BmsCollectionGeneralTest(IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad) with IndexedSeqTypeTest
class IndexedSeqMonadicIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)
class IndexedSeqMonadicBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)
class IndexedSeqMonadicTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)
class IndexedSeqMonadicQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)
class IndexedSeqMonadicQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)
class IndexedSeqMonadicQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)
class IndexedSeqMonadicQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)
class IndexedSeqMonadicQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, IndexedSeqTestData)(CollectionsAsMonad.indexedSeqBooleanMonad.name)

trait IndexedSeqTypeTest extends BmsTestBase {
  def testData: BmsCollectionTestData[IndexedSeq]

  it should "provide type constants" in {
    IndexedSeq.False shouldBe testData.PureFalse
    IndexedSeq.True shouldBe testData.PureTrue
    IndexedSeq.Unit shouldBe testData.PureUnit
  }
}
