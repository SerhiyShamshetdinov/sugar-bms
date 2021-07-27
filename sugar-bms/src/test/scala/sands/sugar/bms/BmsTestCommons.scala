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

import org.scalatest.Assertions.fail
import sands.sugar.bms.BmsTestOptions.TestTraceEnabled
import sands.sugar.bms.BmsTestPatterns.ArgumentPlaceholder

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.control.NoStackTrace
import scala.util.{Failure, Success, Try}

/*
 * Created by Serhiy Shamshetdinov
 * at 18.08.2021 02:22
 */

object BmsTestCommons {
  val BooleanMonadsImport = Seq("import sands.sugar.bms.BooleanMonads._")

  object NoValueException extends Exception("No value") with NoStackTrace

  def na: Any = throw NoValueException

  def nu: Unit = throw NoValueException

  def nb: Boolean = throw NoValueException

  private val sNb = "nb"

  class OrderCollector() {
    @volatile private var _order: List[Int] = Nil // @volatile since it may be accessed from different threads for Future (to be non cashed in CPU)

    def order: String = _order.reverse.mkString

    // Adding is non thread safe but access is never concurrent: it always adds values sequentially inside 1 test case (even for Future).
    // Test cases for one OrderCollector instance of the one toolbox test also are sequential:
    // next test case will start only after all Futures of the previous test case are ready.
    def ov[V](n: Int)(v: => V): V = { // collects passed `ov` order `n` and returns evaluated `v`
      _order = n :: _order
      v
    }

    def clear(): Unit =
      _order = Nil
  }

  val AnyClassName = "Any"
  val AnyRefClassName = "AnyRef"
  val BooleanClassName = "Boolean"
  val IntClassName = "Int"
  val sFalse = "false"
  val sTrue = "true"
  val sInversion = "!"

  def valueWithLogical(value: String): (String, String) = {
    val containsFalse = value.contains(sFalse)
    val containsTrue = value.contains(sTrue)
    val containsInversion = value.contains(sInversion)

    val logical = if (containsFalse && containsTrue)
      throw new IllegalArgumentException(s"Value may not contain '$sFalse' and '$sTrue' at the same time. Got '$value'")
    else if (containsFalse)
      if (containsInversion) sTrue else sFalse
    else if (containsTrue)
      if (containsInversion) sFalse else sTrue
    else
      sNb

    value -> logical
  }

  case class BmsCaseResult[M[_], R, E](testName: String,
                                       resultValue: Try[M[R]],
                                       resultEvalOrder: String,
                                       expectedValue: Try[E],
                                       expectedEvalOrder: String) {
    override def toString: String = {
      val evalOrder = if (resultEvalOrder.nonEmpty || expectedEvalOrder.nonEmpty) s"\nEval order: $resultEvalOrder, expected /or 'starts with' when the 'expected inner' is an Exception/: $expectedEvalOrder" else ""
      s"Test name/code: $testName\nResult: $resultValue, expected (or inner): $expectedValue$evalOrder"
    }
  }

  val runBmsCaseName = "runBmsCase"
  val nlRunBmsCaseName = s"\n$runBmsCaseName"

  def withTestCaseNewLines(s: String): String = s.replace(runBmsCaseName, nlRunBmsCaseName)

  def ready[T](f: Future[T]): Future[T] = Await.ready[T](f, 3.minute)

  def runBmsCase[M[_], R, E](testName: String, testExpression: => M[R], expectedExpression: => E)(implicit oc: OrderCollector): BmsCaseResult[M, R, E] = {
    oc.clear()
    val resultTryValue = Try[M[R]](testExpression)
    resultTryValue match {
      case Success(f: Future[_]) => ready(f)
      case _ =>
    }
    val resultEvalOrder = oc.order

    oc.clear()
    val expectedTryValue = Try[E](expectedExpression) // currently E is never Future
    val expectedEvalOrder = oc.order

    BmsCaseResult[M, R, E](testName, resultTryValue, resultEvalOrder, expectedTryValue, expectedEvalOrder)
  }

  val StreamMaxCompareLength = 10

  def validateBmsCaseResult[M[_], R, E](caseResult: BmsCaseResult[M, R, E], tryResultToTryExpected: Try[M[R]] => Try[E]): Boolean = {
    import caseResult._

    def evalOrderIsValid: Boolean = {
      if (expectedValue == Failure(NoValueException))
        resultEvalOrder.startsWith(expectedEvalOrder)
      else
        resultEvalOrder == expectedEvalOrder
    }

    val resultIsExpected = {
      val triedResultToExpected = tryResultToTryExpected(resultValue)

      triedResultToExpected match {
        case Success(arte: Array[_]) =>
          expectedValue match {
            case Success(ae: Array[_]) => arte.sameElements(ae)
            case _ => false
          }
        case Success(srte: Stream[_]) =>
          expectedValue match {
            case Success(se: Stream[_]) => srte.take(StreamMaxCompareLength).sameElements(se.take(StreamMaxCompareLength))
            case _ => false
          }
        case _ =>
          triedResultToExpected == expectedValue
      }
    }

    if (resultIsExpected && evalOrderIsValid) {
      if (TestTraceEnabled) println(s"\n\n$caseResult")
      true
    } else {
      val reason = if (!resultIsExpected) "unexpected value" else "unexpected evaluation order"
      println(s"\n\n*** FAILURE Reason: $reason\n$caseResult")
      false
    }
  }

  def validateBmsCaseResults[M[_], R, E](caseResults: Seq[BmsCaseResult[M, R, E]], tryResultToTryExpected: Try[M[R]] => Try[E]): Unit = {
    if (caseResults.map(validateBmsCaseResult[M, R, E](_, tryResultToTryExpected)).contains(false))
      fail("*** Failed test case exists")
  }

  private def appendPatternWithArguments(sb: StringBuilder, patternParts: Array[String], arguments: Seq[String]): Unit = {
    var vList = arguments
    var part = 1
    sb.append(patternParts(0))
    while (vList.nonEmpty) {
      sb.append(vList.head);
      vList = vList.tail
      sb.append(patternParts(part));
      part += 1
    }
  }

  val stringQuotes = "\"\"\""

  def appendListOfRunBmsCases(sb: StringBuilder,
                              sTestTypes: String,
                              testPattern: String,
                              expectedPattern: String,
                              argumentsPairs: Seq[(Seq[String], Seq[String])]): Unit = {
    val tmpSb = new StringBuilder(100)
    sb.append("\nList(")
    val testPatternParts = testPattern.split(ArgumentPlaceholder)
    val expectedPatternParts = expectedPattern.split(ArgumentPlaceholder)
    val runBmsCasePrefix = s"\n$runBmsCaseName[$sTestTypes]($stringQuotes" // dropping [$sTestTypes] changes the place of single branch IfBranches implicit applying: it is applied inside ov(1)(trueOnlyBranch). With types - outside ov(1)(...)

    argumentsPairs.map { case (testArguments, expectedArguments) =>
      tmpSb.clear()

      sb.append(runBmsCasePrefix)
      appendPatternWithArguments(tmpSb, testPatternParts, testArguments)
      sb.append(tmpSb)
      sb.append(stringQuotes)
      sb.append(", ")
      sb.append(tmpSb)
      sb.append(", ")
      appendPatternWithArguments(sb, expectedPatternParts, expectedArguments)
      sb.append("),")
    }
    sb.setLength(sb.length - 1) // to drop last ',' in the List
    sb.append("\n)")
  }
}
