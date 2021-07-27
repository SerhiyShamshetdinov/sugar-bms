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

package sands.sugar.bms.stream

import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.BooleanMonad.{CollectionsAsMonad, streamZippingBooleanMonad}
import sands.sugar.bms.{ElseExtension => _, _}

/*
 * Created by Serhiy Shamshetdinov
 * at 08.08.2021 23:05
 */

class StreamZippedGeneralTest extends BmsCollectionGeneralTest(StreamTestData) with StreamTypeTest
class StreamZippedIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)
class StreamZippedBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)
class StreamZippedTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)
class StreamZippedQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)
class StreamZippedQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)
class StreamZippedQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)
class StreamZippedQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)
class StreamZippedQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, StreamTestData)(streamZippingBooleanMonad.name)

class StreamMonadicGeneralTest extends BmsCollectionGeneralTest(StreamTestData)(CollectionsAsMonad.streamBooleanMonad) with StreamTypeTest
class StreamMonadicIfStatementsTest extends BmsCovariantCollectionIfStatementsTest(BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)
class StreamMonadicBinaryExpressionsTest extends BmsCollectionBinaryExpressionsTest(BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)
class StreamMonadicTernaryExpressionsTest extends BmsCollectionTernaryExpressionsTest(BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)
class StreamMonadicQuaternaryExpressionsPage1Test extends BmsCollectionQuaternaryExpressionsPagedTest(1, BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)
class StreamMonadicQuaternaryExpressionsPage2Test extends BmsCollectionQuaternaryExpressionsPagedTest(2, BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)
class StreamMonadicQuaternaryExpressionsPage3Test extends BmsCollectionQuaternaryExpressionsPagedTest(3, BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)
class StreamMonadicQuaternaryExpressionsPage4Test extends BmsCollectionQuaternaryExpressionsPagedTest(4, BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)
class StreamMonadicQuaternaryExpressionsPage5Test extends BmsCollectionQuaternaryExpressionsPagedTest(5, BooleanMonadsImport, StreamTestData)(CollectionsAsMonad.streamBooleanMonad.name)

trait StreamTypeTest extends BmsTestBase {
  def testData: BmsCollectionTestData[Stream]

  it should "provide type constants" in {
    Stream.False shouldBe testData.PureFalse
    Stream.True shouldBe testData.PureTrue
    Stream.Unit shouldBe testData.PureUnit
  }
}
