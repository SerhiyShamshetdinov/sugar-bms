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

/*
 * Created by Serhiy Shamshetdinov
 * at 18.08.2021 02:22
 */

object BmsTestOptions {
  val ScalaStringVersion: String = util.Properties.versionNumberString
  val JavaStringVersion: String = util.Properties.javaVersion

  def getBooleanSysPropOrEnvVal(key: String): Option[Boolean] =
    scala.sys.props.get(key).orElse(scala.sys.env.get(key)).map(_.equalsIgnoreCase("true"))

  val TestDebugEnabled: Boolean = getBooleanSysPropOrEnvVal("bmsTestDebug").getOrElse(false)
  val TestTraceEnabled: Boolean = getBooleanSysPropOrEnvVal("bmsTestTrace").getOrElse(TestDebugEnabled)
  val TestHeavyEnabled: Boolean = getBooleanSysPropOrEnvVal("bmsTestHeavy").getOrElse(false) //(ScalaStringVersion >= "2.12")

  println(s"[info] * sugar-bms test options: bmsTestDebug=$TestDebugEnabled bmsTestTrace=$TestTraceEnabled bmsTestHeavy=$TestHeavyEnabled")

  val ToolboxReporterMinSeverity = 2 // 0 stands for INFO, 1 stands for WARNING and 2 stands for ERROR
  val ToolboxScalacOptions = "-unchecked -language:_" // tests are passed without this too
}
