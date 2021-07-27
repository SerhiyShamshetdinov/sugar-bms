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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sands.sugar.bms.BmsTestCommons._
import sands.sugar.bms.BmsTestOptions._

import scala.reflect.runtime.universe
import scala.tools.reflect.{ToolBox, mkConsoleFrontEnd}
import scala.util.Try

/*
 * Created by Serhiy Shamshetdinov
 * at 27.07.2021 18:00
 */

trait BmsTestBase extends AnyFlatSpec with Matchers

trait BmsToolBoxTestBase extends BmsTestBase {

  val BmsToolboxTestCodeHeader: String =
    """import sands.sugar.bms.BmsTestCommons._
      |
      |implicit val oc = new OrderCollector()
      |import oc.ov
      |""".stripMargin

  trait ToolboxTest {
    val toolbox: ToolBox[universe.type] = reflect.runtime.currentMirror.mkToolBox(mkConsoleFrontEnd(ToolboxReporterMinSeverity), ToolboxScalacOptions)

    import toolbox.u._

    def parseAndLogTrees(code: String): Tree = { // in theory, it may return different trees in test Trace & No Trace variants (Scala 2 reflection dependent)
      val tree = toolbox.parse(code)
      if (TestDebugEnabled) {
        println(s"Toolbox input parsed code:\n${withTestCaseNewLines(tree.toString)}")
        if (TestTraceEnabled) {
          val typeCheckedTree = toolbox.typecheck(tree)
          println(s"Toolbox type checked input code:\n${withTestCaseNewLines(typeCheckedTree.toString)}")
          toolbox.untypecheck(typeCheckedTree)
        } else
          tree
      } else
        tree
    }

    def evaluateTree(tree: Tree): Any = toolbox.compile(tree)()

    def evaluateCode(code: String): Any = evaluateTree(parseAndLogTrees(code))

    def evaluatedCode(code: String): (String, Any) = {
      val tree = parseAndLogTrees(code)
      (tree.toString, evaluateTree(tree))
    }
  }

  def buildToolBoxCodePatternBmsCases(imports: Seq[String],
                                      sTestTypes: String,
                                      testPattern: String,
                                      expectedPattern: String,
                                      argumentsPairs: Seq[(Seq[String], Seq[String])],
                                      initialBuilderCapacity: Int): String = {
    val sb = new StringBuilder(initialBuilderCapacity, BmsToolboxTestCodeHeader)
    sb.append(imports.mkString("\n"))
    appendListOfRunBmsCases(sb, sTestTypes, testPattern, expectedPattern, argumentsPairs)

    val code = sb.result()
    if (TestDebugEnabled) println(s"Built toolbox code length=${code.length}")

    code
  }

  def toolBoxTestPatternsWithValueCombinations[M[_], R, E](imports: Seq[String],
                                                           sMonadType: String,
                                                           sResultType: String,
                                                           sExpectedType: String,
                                                           patternPairs: Seq[(String, String)],
                                                           argumentsPairs: Seq[(Seq[String], Seq[String])],
                                                           tryResultToTryExpected: Try[M[R]] => Try[E],
                                                           initialBuilderCapacity: Int = 50000): Unit = {
    for {
      (testPattern, expectedPattern) <- patternPairs
    } {
      it should s"evaluate pattern $testPattern for $sMonadType" in new ToolboxTest {
        val toolBoxCode: String = buildToolBoxCodePatternBmsCases(
          imports,
          s"$sMonadType,$sResultType,$sExpectedType",
          testPattern,
          expectedPattern,
          argumentsPairs,
          initialBuilderCapacity
        )

        val caseResults: List[BmsCaseResult[M, R, E]] = evaluateCode(toolBoxCode).asInstanceOf[List[BmsCaseResult[M, R, E]]]

        validateBmsCaseResults[M, R, E](caseResults, tryResultToTryExpected)
      }
    }
  }
}