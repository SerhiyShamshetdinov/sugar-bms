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

package sands.sugar.bms.future

import sands.sugar.bms.BmsSingleElementMonadTestData
import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BmsTestPatterns._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/*
 * Created by Serhiy Shamshetdinov
 * at 11.08.2021 03:41
 */

object FutureTestData extends BmsSingleElementMonadTestData[Future] {
  implicit val futureExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val MonadImports = Seq(
    "import scala.concurrent.Future",
    "import scala.concurrent.ExecutionContext.Implicits.global"
  )
  val MonadName = "Future"

  def pure[T](v: T): Future[T] = Future.successful(v)
  val sPure = "Future.successful"

  val PureFalse: Future[Boolean] = pure(false)
  val sPureFalse = s"$sPure(false)"
  val PureTrue: Future[Boolean] = pure(true)
  val sPureTrue = s"$sPure(true)"

  val BooleanEmpty: Future[Boolean] = Future.failed[Boolean](NoValueException)
  val sBooleanEmpty = "Future.failed[Boolean](NoValueException)"

  def tryResultToTryExpected[A](tf: Try[Future[A]]): Try[A] = tf.flatMap(ready[A](_).value.get)

  val expressionBooleans: Seq[String] = Seq[String](
    sPureFalse,
    sPureTrue,
    sBooleanEmpty,
    sFalse,
    sTrue
  )
  lazy val expressionBooleansToExpected: Seq[(String, String)] = expressionBooleans.map(valueWithLogical)
  lazy val quaternaryArgumentsCombinations: Seq[(List[String], List[String])] = // is here since it is common for quaternary expressions paged test
    prependedArgumentsCombinationsWithoutPureBooleans(expressionBooleansToExpected, 4)

  val ifPredicates: Seq[String] = Seq[String](
    sPureFalse,
    sPureTrue,
    sBooleanEmpty
  )

  val ifBranchesToExpected: Seq[(String, String)] = Seq(
    "()" -> "()",
    "1" -> "1",
    """"s"""" -> """"s""""
  )

  val ifMonadicBranchesToExpected: Seq[(String, String)] = Seq(
    s"$sPure(())" -> "()",
    s"$sPure(1)" -> "1",
    s"""$sPure("ms")""" -> """"ms"""",
    sBooleanEmpty -> "na"
  )

  lazy val ifBinaryPatternPairs: Seq[(String, String)] = mapIfMIfBinaryPatternPairs
  lazy val ifTernaryPatternPairs: Seq[(String, String)] = mapIfMIfTernaryPatternPairs
  lazy val flatIfBinaryPatternPairs: Seq[(String, String)] = flatMapIfFmIfBinaryUnitPatternPairs
  lazy val flatIfTernaryPatternPairs: Seq[(String, String)] = flatMapIfFmIfTernaryPatternPairs
}
