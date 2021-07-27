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

import sands.sugar.bms.BmsCollectionTestData
import sands.sugar.bms.BmsTestCommons.{NoValueException, sFalse, sTrue}
import sands.sugar.bms.BmsTestPatterns._

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

/*
 * Created by Serhiy Shamshetdinov
 * at 16.08.2021 21:41
 */

object IterableTestData extends BmsCollectionTestData[Iterable] {
  val TypeImports = Seq()
  val MonadName = "Iterable"
  val AsMonadImports = Seq("import sands.sugar.bms.typeclass.BooleanMonad.CollectionsAsMonad.iterableBooleanMonad")
  val TestDataImports = Seq("import sands.sugar.bms.iterable.IterableTestData.{Empty, lift}")

  def lift(b: Boolean): Iterable[Boolean] = if(b) PureTrue else PureFalse
  def lift(bl: Iterable[Boolean]): Iterable[Boolean] = bl

  def pure[T: ClassTag](v: T*): Iterable[T] = v.toIterable
  val sPure = "Iterable"

  val PureFalse: Iterable[Boolean] = pure(false)
  val sPureFalse = s"$sPure(false)"
  val PureTrue: Iterable[Boolean] = pure(true)
  val sPureTrue = s"$sPure(true)"
  val PureUnit: Iterable[Unit] = pure(())
  val Empty: Iterable[Nothing] = Iterable()

  val sPureFalseFalse = s"$sPure(false,false)"
  val sPureFalseTrue = s"$sPure(false,true)"
  val sPureTrueFalse = s"$sPure(true,false)"
  val sPureTrueTrue = s"$sPure(true,true)"

  val BooleanEmpty: Iterable[Boolean] = Iterable[Boolean]()
  val sBooleanEmpty = "Iterable[Boolean]()"

  val tryGetBoolean: Try[Iterable[Boolean]] => Try[Boolean] = _.flatMap(mc => if (mc.size != 1) Failure(NoValueException) else Success(mc.head))

  /// boolean expressions tests data

  private val expressionBooleans: Seq[String] = Seq[String](
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

  def getExpressionBooleans(isMonadicTest: Boolean): Seq[String] = expressionBooleans

  // in 2.13 6 argument variants (1280 runBmsCase in 1 test) fail in toolbox test quaternaryBooleanExpressionExpectedMonadicOpsPattern with error:
  //scala.tools.reflect.ToolBoxError: reflective compilation has failed:
  //Error while emitting __wrapper$1$b9a9976f2f324b3282b2e98887b58001/__wrapper$1$b9a9976f2f324b3282b2e98887b58001$
  //Class too large: __wrapper$1$b9a9976f2f324b3282b2e98887b58001/__wrapper$1$b9a9976f2f324b3282b2e98887b58001$
  private val quaternaryExpressionBooleans: Seq[String] = Seq[String](
    sPureFalseFalse,
    sPureFalseTrue,
    sPureTrueFalse,
    sPureTrueTrue,
    sBooleanEmpty
  )
  private lazy val quaternaryExpressionBooleansToExpected: Seq[(String, String)] = quaternaryExpressionBooleans.zip(quaternaryExpressionBooleans)

  private lazy val quaternaryArgumentsCombinations: Seq[(List[String], List[String])] = // here since it is common for quaternary expressions paged test
    prependedArgumentsCombinationsWithoutPureBooleans(quaternaryExpressionBooleansToExpected, 4)

  def getQuaternaryArgumentsCombinations(isMonadicTest: Boolean): Seq[(List[String], List[String])] = quaternaryArgumentsCombinations

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
