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

package sands.sugar.bms.seq

import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.BooleanMonad.{CollectionsAsMonad, seqZippingBooleanMonad}
import sands.sugar.bms.{ElseExtension => _, _}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class SeqZippedGeneralTest extends BmsCollectionGeneralTest(SeqTestData) with SeqTypeTest
class SeqZippedIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)
class SeqZippedBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)
class SeqZippedTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)
class SeqZippedQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)
class SeqZippedQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)
class SeqZippedQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)
class SeqZippedQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)
class SeqZippedQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, SeqTestData)(seqZippingBooleanMonad.name)

class SeqMonadicGeneralTest extends BmsCollectionGeneralTest(SeqTestData)(CollectionsAsMonad.seqBooleanMonad) with SeqTypeTest
class SeqMonadicIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)
class SeqMonadicBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)
class SeqMonadicTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)
class SeqMonadicQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)
class SeqMonadicQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)
class SeqMonadicQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)
class SeqMonadicQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)
class SeqMonadicQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, SeqTestData)(CollectionsAsMonad.seqBooleanMonad.name)

trait SeqTypeTest extends BmsTestBase {
  def testData: BmsCollectionTestData[Seq]

  it should "provide type constants" in {
    Seq.False shouldBe testData.PureFalse
    Seq.True shouldBe testData.PureTrue
    Seq.Unit shouldBe testData.PureUnit
  }
}
