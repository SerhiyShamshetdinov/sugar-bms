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

package sands.sugar.bms

import sands.sugar.bms.BmsTestCommons.{AnyClassName, BooleanClassName, sInversion, valueWithLogical}
import sands.sugar.bms.BmsTestOptions.TestHeavyEnabled
import sands.sugar.bms.BmsTestPatterns._

import scala.util.Try

/*
 * Created by Serhiy Shamshetdinov
 * at 11.08.2021 03:41
 */

trait BmsSingleElementMonadTestData[M[_]] {
  def MonadImports: Seq[String]
  def MonadName: String

  def tryResultToTryExpected[A](m: Try[M[A]]): Try[A]

  def expressionBooleans: Seq[String]
  def expressionBooleansToExpected: Seq[(String, String)]
  def quaternaryArgumentsCombinations: Seq[(List[String], List[String])]

  def ifPredicates: Seq[String]
  def ifBranchesToExpected: Seq[(String, String)]
  def ifMonadicBranchesToExpected: Seq[(String, String)]

  def ifBinaryPatternPairs: Seq[(String, String)]
  def ifTernaryPatternPairs: Seq[(String, String)]
  def flatIfBinaryPatternPairs: Seq[(String, String)]
  def flatIfTernaryPatternPairs: Seq[(String, String)]
}


class BmsMonadIfStatementsTest[M[_]](testImports: Seq[String],
                                     testData: BmsSingleElementMonadTestData[M]) extends BmsToolBoxTestBase {
  import testData._

  val ifPredicatesToExpectedLists: Seq[(Seq[String], Seq[String])] =
    ifPredicates.map { value =>
      val valuePair = valueWithLogical(value)
      Seq(valuePair._1) -> Seq(valuePair._2)
    }

  behavior of s"Boolean $MonadName monadic 'if' with one branch"

  toolBoxTestPatternsWithValueCombinations[M, Any, Any](
    testImports ++ MonadImports,
    MonadName,
    AnyClassName,
    AnyClassName,
    ifBinaryPatternPairs,
    appendArgumentsCombinations(ifBranchesToExpected, 1, ifPredicatesToExpectedLists),
    tryResultToTryExpected
  )

  behavior of s"Boolean $MonadName monadic 'if' with two branches"

  toolBoxTestPatternsWithValueCombinations[M, Any, Any](
    testImports ++ MonadImports,
    MonadName,
    AnyClassName,
    AnyClassName,
    ifTernaryPatternPairs,
    appendArgumentsCombinations(ifBranchesToExpected, 2, ifPredicatesToExpectedLists),
    tryResultToTryExpected
  )

  behavior of s"Boolean $MonadName flat monadic 'if' with one branch"

  toolBoxTestPatternsWithValueCombinations[M, Any, Any](
    testImports ++ MonadImports,
    MonadName,
    AnyClassName,
    AnyClassName,
    flatIfBinaryPatternPairs,
    appendArgumentsCombinations(ifMonadicBranchesToExpected, 1, ifPredicatesToExpectedLists),
    tryResultToTryExpected
  )

  behavior of s"Boolean $MonadName flat monadic 'if' with two branches"

  toolBoxTestPatternsWithValueCombinations[M, Any, Any](
    testImports ++ MonadImports,
    MonadName,
    AnyClassName,
    AnyClassName,
    flatIfTernaryPatternPairs,
    appendArgumentsCombinations(ifMonadicBranchesToExpected, 2, ifPredicatesToExpectedLists),
    tryResultToTryExpected
  )
}

class BmsMonadBinaryExpressionsTest[M[_]](testImports: Seq[String],
                                          testData: BmsSingleElementMonadTestData[M]) extends BmsToolBoxTestBase {
  import testData._

  val binaryExpressionPatterns: Seq[String] = withBooleanOperators(Seq(binaryBooleanExpressionOpsPattern), 1)
  val binaryExpressionPatternPairs: Seq[(String, String)] = binaryExpressionPatterns.zip(binaryExpressionPatterns)

  behavior of s"Boolean $MonadName Binary Expressions"

  toolBoxTestPatternsWithValueCombinations[M, Boolean, Boolean](
    testImports ++ MonadImports,
    MonadName,
    BooleanClassName,
    BooleanClassName,
    binaryExpressionPatternPairs,
    prependedArgumentsCombinationsWithoutPureBooleans(expressionBooleansToExpected, 2),
    tryResultToTryExpected
  )

  behavior of s"Boolean $MonadName Binary Expressions with inverted values"

  private val invertedExpressionBooleans: Seq[String] = expressionBooleans.map(sInversion + _)
  val invertedExpressionBooleansToExpected: Seq[(String, String)] = invertedExpressionBooleans.map(valueWithLogical)

  toolBoxTestPatternsWithValueCombinations[M, Boolean, Boolean](
    testImports ++ MonadImports,
    MonadName,
    BooleanClassName,
    BooleanClassName,
    binaryExpressionPatternPairs,
    prependedArgumentsCombinationsWithoutPureBooleans(invertedExpressionBooleansToExpected, 2),
    tryResultToTryExpected
  )
}

class BmsMonadTernaryExpressionsTest[M[_]](testImports: Seq[String],
                                           testData: BmsSingleElementMonadTestData[M]) extends BmsToolBoxTestBase {
  import testData._

  val ternaryExpressionPatterns: Seq[String] = withBooleanOperators(Seq(ternaryBooleanExpressionOpsPattern), 2)
  val ternaryExpressionPatternPairs: Seq[(String, String)] = ternaryExpressionPatterns.zip(ternaryExpressionPatterns)

  behavior of s"Boolean $MonadName Ternary Expressions"

  toolBoxTestPatternsWithValueCombinations[M, Boolean, Boolean](
    testImports ++ MonadImports,
    MonadName,
    BooleanClassName,
    BooleanClassName,
    ternaryExpressionPatternPairs,
    prependedArgumentsCombinationsWithoutPureBooleans(expressionBooleansToExpected, 3),
    tryResultToTryExpected
  )
}

class BmsMonadQuaternaryExpressionsPagedTest[M[_]](pageNumber: Int,
                                                   testImports: Seq[String],
                                                   testData: BmsSingleElementMonadTestData[M]) extends BmsToolBoxTestBase {
  import testData._

  val testTargetName = s"Boolean $MonadName Quaternary Expressions (page $pageNumber)"
  if (!TestHeavyEnabled) {
    println(s"[info] * sugar-bms test is skipped due to Heavy tests are disabled: $testTargetName")
  } else {
    val booleanOpsNumber: Int = booleanOperators.length

    pageNumber.ensuring(p => 1 <= p && p <= booleanOpsNumber, s"QuaternaryExpressionsTest page number should be from 1 to $booleanOpsNumber inclusively")

    behavior of testTargetName

    val pageSize: Int = quaternaryExpressionPatternPairs.length / booleanOpsNumber
    val pagePatternPairs: Seq[(String, String)] = quaternaryExpressionPatternPairs.slice(pageSize * (pageNumber - 1), pageSize * pageNumber)

    toolBoxTestPatternsWithValueCombinations[M, Boolean, Boolean](
      testImports ++ MonadImports,
      MonadName,
      BooleanClassName,
      BooleanClassName,
      pagePatternPairs,
      quaternaryArgumentsCombinations,
      tryResultToTryExpected,
      220000 // set by Future
    )
  }
}
