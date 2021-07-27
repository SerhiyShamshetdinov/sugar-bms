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

import sands.sugar.bms.BmsCollectionTestData
import sands.sugar.bms.BmsTestCommons.{NoValueException, sFalse, sTrue}
import sands.sugar.bms.BmsTestPatterns._

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

/*
 * Created by Serhiy Shamshetdinov
 * at 16.08.2021 21:41
 */

object StreamTestData extends BmsCollectionTestData[Stream] {
  val TypeImports = Seq()
  val MonadName = "Stream"
  val AsMonadImports = Seq("import sands.sugar.bms.typeclass.BooleanMonad.CollectionsAsMonad.streamBooleanMonad")
  val TestDataImports = Seq("import sands.sugar.bms.stream.StreamTestData.{Empty, FalseTrueInf, FalseFalseTrueTrueInf, lift}")

  def lift(b: Boolean): Stream[Boolean] = if(b) PureTrue else PureFalse
  def lift(bl: Stream[Boolean]): Stream[Boolean] = bl

  def pure[T: ClassTag](v: T*): Stream[T] = v.toStream
  val sPure = "Stream"

  val PureFalse: Stream[Boolean] = pure(false)
  val sPureFalse = s"$sPure(false)"
  val PureTrue: Stream[Boolean] = pure(true)
  val sPureTrue = s"$sPure(true)"
  val PureUnit: Stream[Unit] = pure(())
  val Empty: Stream[Nothing] = Stream()

  val sPureFalseFalse = s"$sPure(false,false)"
  val sPureFalseTrue = s"$sPure(false,true)"
  val sPureTrueFalse = s"$sPure(true,false)"
  val sPureTrueTrue = s"$sPure(true,true)"

  val BooleanEmpty: Stream[Boolean] = Stream[Boolean]()
  val sBooleanEmpty = "Stream[Boolean]()"

  val tryGetBoolean: Try[Stream[Boolean]] => Try[Boolean] = _.flatMap(mc => if (mc.length != 1) Failure(NoValueException) else Success(mc.head))

  /// boolean expressions tests data

  val FalseTrueInf: Stream[Boolean] = {def FTS: Stream[Boolean] = false #:: true #:: FTS; FTS}
  val FalseFalseTrueTrueInf: Stream[Boolean] = {def FFTTS: Stream[Boolean] = false #:: false #:: true #:: true #:: FFTTS; FFTTS}
  val sFalseTrueInf = "FalseTrueInf"
  val sFalseFalseTrueTrueInf = "FalseFalseTrueTrueInf"

  val zippingExpressionBooleans: Seq[String] = Seq[String](
    sFalseTrueInf,
    sFalseFalseTrueTrueInf,
    sPureFalse,
    sPureTrue,
    sBooleanEmpty,
    sFalse,
    sTrue
  )
  val monadicExpressionBooleans: Seq[String] = Seq[String]( // infinite streams do infinite computations when they are in flatMap function position
    sPureFalseFalse,
    sPureFalseTrue,
    sPureTrueFalse,
    sPureTrueTrue,
    sPureFalse,
    sPureTrue,
    sBooleanEmpty,
    sFalse,
    sTrue
  )

  def getExpressionBooleans(isMonadicTest: Boolean): Seq[String] =
    if (isMonadicTest) monadicExpressionBooleans else zippingExpressionBooleans

  // in 2.13 6 argument variants (1280 runBmsCase in 1 test) fail in toolbox test quaternaryBooleanExpressionExpectedMonadicOpsPattern with error:
  //scala.tools.reflect.ToolBoxError: reflective compilation has failed:
  //Error while emitting __wrapper$1$b9a9976f2f324b3282b2e98887b58001/__wrapper$1$b9a9976f2f324b3282b2e98887b58001$
  //Class too large: __wrapper$1$b9a9976f2f324b3282b2e98887b58001/__wrapper$1$b9a9976f2f324b3282b2e98887b58001$
  private val zippingQuaternaryExpressionBooleans: Seq[String] = Seq[String](
    sFalseTrueInf,
    sFalseFalseTrueTrueInf,
    sPureFalse,
    sPureTrue,
    sBooleanEmpty
  )
  private val monadicQuaternaryExpressionBooleans: Seq[String] = Seq[String]( // infinite streams do infinite computations when they are in flatMap function position
    sPureFalseTrue,
    sPureTrueFalse,
    sPureFalse,
    sPureTrue,
    sBooleanEmpty
  )

  private lazy val zippingQuaternaryArgumentsCombinations: Seq[(List[String], List[String])] =
    prependedArgumentsCombinationsWithoutPureBooleans(zippingQuaternaryExpressionBooleans.zip(zippingQuaternaryExpressionBooleans), 4)
  private lazy val monadicQuaternaryArgumentsCombinations: Seq[(List[String], List[String])] =
    prependedArgumentsCombinationsWithoutPureBooleans(monadicQuaternaryExpressionBooleans.zip(monadicQuaternaryExpressionBooleans), 4)

  def getQuaternaryArgumentsCombinations(isMonadicTest: Boolean): Seq[(List[String], List[String])] = // here since it is common for quaternary expressions paged test
    if (isMonadicTest) monadicQuaternaryArgumentsCombinations else zippingQuaternaryArgumentsCombinations

  /// if tests data

  val ifPredicates: Seq[String] = Seq[String](
    sPureFalseFalse,
    sPureFalseTrue,
    sPureTrueFalse,
    sPureTrueTrue,
    sPureFalse,
    sPureTrue,
    sBooleanEmpty
  )

  private val ifBranches: Seq[String] = Seq(
    "()",
    "1",
    """"s""""
  )
  val ifBranchesToExpected: Seq[(String, String)] = ifBranches.zip(ifBranches)

  private val ifMonadicBranches: Seq[String] = Seq(
    s"$sPure(())",
    s"$sPure(1)",
    s"""$sPure("ms")""",
    sBooleanEmpty
  )
  val ifMonadicBranchesToExpected: Seq[(String, String)] = ifMonadicBranches.zip(ifMonadicBranches)
}
