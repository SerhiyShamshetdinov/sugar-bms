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

package sands.sugar.bms.either

import sands.sugar.bms.BmsSingleElementMonadTestData
import sands.sugar.bms.BmsTestCommons.{NoValueException, sFalse, sTrue, valueWithLogical}
import sands.sugar.bms.BmsTestPatterns._
import sands.sugar.bms.either.EitherThrowableMonad.{EitherThrowable, RightThrowable}

import scala.util.{Failure, Success, Try}

/*
 * Created by Serhiy Shamshetdinov
 * at 11.08.2021 03:41
 */

object EitherThrowableMonad {
  type EitherThrowable[R] = Either[Throwable, R]

  object RightThrowable {
    def apply[R](v: R): Right[Throwable, R] = Right[Throwable, R](v)
  }
}

object EitherThrowableTestData extends BmsSingleElementMonadTestData[EitherThrowable] {
  val MonadImports = Seq("import sands.sugar.bms.either.EitherThrowableMonad._")
  val MonadName = "EitherThrowable"

  def pure[T](v: T): Right[Throwable, T] = RightThrowable(v)
  val sPure = "RightThrowable"

  val PureFalse: Right[Throwable, Boolean] = pure(false)
  val sPureFalse = s"$sPure(false)"
  val PureTrue: Right[Throwable, Boolean] = pure(true)
  val sPureTrue = s"$sPure(true)"
  val PureUnit: Right[Throwable, Unit] = pure(())

  val BooleanEmpty: Either[Throwable, Boolean] = Left[Throwable, Boolean](NoValueException)
  val sBooleanEmpty = "Left[Throwable, Boolean](NoValueException)"

  def tryResultToTryExpected[A](te: Try[Either[Throwable, A]]): Try[A] = te.flatMap(_.fold[Try[A]](Failure(_), Success(_)))

  /// boolean expressions tests data

  val expressionBooleans: Seq[String] = Seq[String](
    sPureFalse,
    sPureTrue,
    sBooleanEmpty,
    sFalse,
    sTrue
  )
  lazy val expressionBooleansToExpected: Seq[(String, String)] = expressionBooleans.map(valueWithLogical)
  lazy val quaternaryArgumentsCombinations: Seq[(List[String], List[String])] = // here since it is common for quaternary expressions paged test
    prependedArgumentsCombinationsWithoutPureBooleans(expressionBooleansToExpected, 4)

  /// if tests data

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
