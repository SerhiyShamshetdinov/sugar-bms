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

import sands.sugar.bms.BmsTestCommons.{sFalse, sInversion, sTrue}

import scala.annotation.tailrec

/*
 * Created by Serhiy Shamshetdinov
 * at 18.08.2021 02:22
 */

object BmsTestPatterns {

  /// operations and arguments patterns

  val ArgumentPlaceholder = "#" // not a '$' or '\'
  private val OperationPlaceholder = "%" // not a '$' or '\'

  // boolean expressions for single element monads
  val binaryBooleanExpressionOpsPattern = "ov(0)(#) % ov(1)(#)"
  val ternaryBooleanExpressionOpsPattern = "ov(0)(#) % ov(1)(#) % ov(2)(#)"
  private val quaternaryBooleanExpressionOpsPattern = "ov(0)(#) % ov(1)(#) % ov(2)(#) % ov(3)(#)"
  // Further naming is here: https://english.stackexchange.com/questions/25116/what-follows-next-in-the-sequence-unary-binary-ternary :)

  // boolean expected expressions for collections (trailing space ' ' is required for stupid String.split)
  val binaryBooleanExpressionCollectionOpsPattern = "# % # "
  val ternaryBooleanExpressionCollectionOpsPattern = "# % # % # "
  private val quaternaryBooleanExpressionCollectionOpsPattern = "# % # % # % # "

  // boolean expected expressions for zipped collections
  val binaryBooleanExpressionExpectedZippedOpsPattern = "lift(#).zip(lift(#)).map(zz => zz._1 % zz._2)"
  val ternaryBooleanExpressionExpectedZippedOpsPattern = "lift(#).zip(lift(#).zip(lift(#))).map(zz => zz._1 % zz._2._1 % zz._2._2)"
  private val quaternaryBooleanExpressionExpectedZippedOpsPattern = "(lift(#).zip(lift(#))).zip(lift(#).zip(lift(#))).map(zz => zz._1._1 % zz._1._2 % zz._2._1 % zz._2._2)"

  // boolean expected expressions for monadic collections
  val binaryBooleanExpressionExpectedMonadicOpsPattern = "for(v1 <- lift(#); v2 <- lift(#)) yield v1 % v2"
  val ternaryBooleanExpressionExpectedMonadicOpsPattern = "for(v1 <- lift(#); v2 <- lift(#); v3 <- lift(#)) yield v1 % v2 % v3"
  private val quaternaryBooleanExpressionExpectedMonadicOpsPattern = "for(v1 <- lift(#); v2 <- lift(#); v3 <- lift(#); v4 <- lift(#)) yield v1 % v2 % v3 % v4"

  // extension methods .mapIf, .flatMapIf, .mIf, .fmIf
  private val binaryOneArgExtensionOpsPattern = "ov(0)(#).%(ov(1)(#))"
  private val ternaryTwoArgsExtensionOpsPattern = "ov(0)(#).%(ov(1)(#), ov(2)(#))"
  private val ternaryElseExtensionOpsPattern = "ov(0)(#).%(ov(1)(#) Else ov(2)(#))"
  // statement methods mapIf(), flatMapIf(), mIf(), fmIf()
  private val binaryOneGroupStatementOpsPattern = "%(ov(0)(#), ov(1)(#))"
  private val binaryTwoGroupsStatementOpsPattern = "%(ov(0)(#))(ov(1)(#))"
  private val ternaryOneGroupStatementOpsPattern = "%(ov(0)(#), ov(1)(#), ov(2)(#))"
  private val ternaryElseStatementOpsPattern = "%(ov(0)(#))(ov(1)(#) Else ov(2)(#))"


  /// arguments patterns (with substituted operations combinations)

  // boolean expressions argument pattern pairs (test -> expected)

  val booleanOperators: Seq[String] = Seq("&&", "||", "&", "^", "|")

  @tailrec
  def withBooleanOperators(patterns: Seq[String], replaceOperators: Int): Seq[String] =
    if (replaceOperators <= 0) patterns
    else withBooleanOperators(patterns.flatMap(pat => booleanOperators.map(pat.replaceFirst(OperationPlaceholder, _))), replaceOperators - 1)

  @tailrec
  def pairsWithBooleanOperators(patternPairs: Seq[(String, String)], replaceOperators: Int): Seq[(String, String)] =
    if (replaceOperators <= 0) patternPairs
    else {
      val withEachOperator = patternPairs.flatMap {
        case (pat1, pat2) => booleanOperators.map { bop =>
          (pat1.replaceFirst(OperationPlaceholder, bop), pat2.replaceFirst(OperationPlaceholder, bop))
        }
      }
      pairsWithBooleanOperators(withEachOperator, replaceOperators - 1)
    }

  private lazy val quaternaryExpressionPatterns: Seq[String] = withBooleanOperators(Seq(quaternaryBooleanExpressionOpsPattern), 3) // used by several paged tests
  lazy val quaternaryExpressionPatternPairs: Seq[(String, String)] = quaternaryExpressionPatterns.zip(quaternaryExpressionPatterns)

  lazy val quaternaryExpressionZippedCollectionPatternPairs: Seq[(String, String)] =
    pairsWithBooleanOperators(Seq(quaternaryBooleanExpressionCollectionOpsPattern -> quaternaryBooleanExpressionExpectedZippedOpsPattern), 3)

  lazy val quaternaryExpressionMonadicCollectionPatternPairs: Seq[(String, String)] =
    pairsWithBooleanOperators(Seq(quaternaryBooleanExpressionCollectionOpsPattern -> quaternaryBooleanExpressionExpectedMonadicOpsPattern), 3)


  /// 'if' statements argument patterns

  // 'if' variants
  private val mapIf = "mapIf"
  private val mIf = "mIf"
  private val flatMapIf = "flatMapIf"
  private val fmIf = "fmIf"

  private implicit class StringPatternExtensions(val sp: String) extends AnyVal {
    def withOperation(op: String): String = sp.replace(OperationPlaceholder, op)
  }

  // 'if' expected patterns for single element monads

  private val binaryIfPattern = "if (ov(0)(#)) ov(1)(#)"
  private val binaryIfElseUnitPattern = "if (ov(0)(#)) ov(1)(#) else ov(1)(())" // for methods with 1 branch `if`s with ifBranches parameter: ifBranches always is evaluated (even if 'false') to get falseBranch with Unit from ifBranches
  private val binaryIfElseEmptyPattern = "if (ov(0)(#)) ov(1)(#) else ov(1)(nu)" // for methods with 1 branch `if`s with ifBranches parameter: ifBranches always is evaluated (even if 'false') to get falseBranch with Empty[Unit] from ifBranches
  private val ternaryIfPattern = "if (ov(0)(#)) ov(1)(#) else ov(2)(#)"

  // 'if' test pattern pairs for single element monads

  lazy val mapIfMIfBinaryPatternPairs: Seq[(String, String)] = Seq(
    binaryOneArgExtensionOpsPattern.withOperation(mapIf) -> binaryIfPattern,
    binaryOneGroupStatementOpsPattern.withOperation(mapIf) -> binaryIfPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(mapIf) -> binaryIfElseUnitPattern,
    binaryOneArgExtensionOpsPattern.withOperation(mIf) -> binaryIfPattern,
    binaryOneGroupStatementOpsPattern.withOperation(mIf) -> binaryIfPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(mIf) -> binaryIfElseUnitPattern
  )

  lazy val mapIfMIfTernaryPatternPairs: Seq[(String, String)] = Seq(
    ternaryTwoArgsExtensionOpsPattern.withOperation(mapIf) -> ternaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(mapIf) -> ternaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(mapIf) -> ternaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(mapIf) -> ternaryIfPattern,
    ternaryTwoArgsExtensionOpsPattern.withOperation(mIf) -> ternaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(mIf) -> ternaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(mIf) -> ternaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(mIf) -> ternaryIfPattern
  )

  lazy val flatMapIfFmIfBinaryUnitPatternPairs: Seq[(String, String)] = Seq(
    binaryOneArgExtensionOpsPattern.withOperation(flatMapIf) -> binaryIfElseUnitPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(flatMapIf) -> binaryIfElseUnitPattern,
    binaryOneArgExtensionOpsPattern.withOperation(fmIf) -> binaryIfElseUnitPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(fmIf) -> binaryIfElseUnitPattern
  )

  lazy val flatMapIfFmIfBinaryEmptyPatternPairs: Seq[(String, String)] = Seq(
    binaryOneArgExtensionOpsPattern.withOperation(flatMapIf) -> binaryIfElseEmptyPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(flatMapIf) -> binaryIfElseEmptyPattern,
    binaryOneArgExtensionOpsPattern.withOperation(fmIf) -> binaryIfElseEmptyPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(fmIf) -> binaryIfElseEmptyPattern
  )

  lazy val flatMapIfFmIfTernaryPatternPairs: Seq[(String, String)] = Seq(
    ternaryTwoArgsExtensionOpsPattern.withOperation(flatMapIf) -> ternaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(flatMapIf) -> ternaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(flatMapIf) -> ternaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(flatMapIf) -> ternaryIfPattern,
    ternaryTwoArgsExtensionOpsPattern.withOperation(fmIf) -> ternaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(fmIf) -> ternaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(fmIf) -> ternaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(fmIf) -> ternaryIfPattern
  )

  // 'if' expected patterns for collections

  private val collectionMapBinaryIfPattern = "ov(0)(#).map(b => if (b) ov(1)(#))"
  private val collectionMapBinaryIfElseUnitPattern = "{val m = ov(0)(#); ov(1)(); m}.map(b => if (b) # )" // for methods with 1 branch `if`s with ifBranches parameter: ifBranches always is evaluated and exactly once! (even if 'false') to get falseBranch with Unit from ifBranches
  private val collectionMapTernaryIfPattern = "ov(0)(#).map(b => if (b) ov(1)(#) else ov(2)(#))"

  private val collectionFlatMapBinaryIfElseEmptyPattern = "{val m = ov(0)(#); ov(1)(); m}.flatMap(b => if (b) # else Empty)" // for methods with 1 branch `if`s with ifBranches parameter: ifBranches always is evaluated and exactly once! (even if 'false') to get falseBranch with Empty[Unit] from ifBranches
  private val collectionFlatMapTernaryIfPattern = "ov(0)(#).flatMap(b => if (b) ov(1)(#) else ov(2)(#))"

  private val collectionMBinaryIfPattern = "{val m = ov(0)(#); m.take(1).map(_ => if (m.exists(_ == true)) ov(1)(#)) }"
  private val collectionMBinaryIfElseUnitPattern = "{val m = ov(0)(#); ov(1)(); m.take(1).map(_ => if (m.exists(_ == true)) #) }" // for methods with 1 branch `if`s with ifBranches parameter: ifBranches always is evaluated and exactly once! (even if 'false') to get falseBranch with Unit from ifBranches
  private val collectionMTernaryIfPattern = "{val m = ov(0)(#); m.take(1).map(_ => if (m.exists(_ == true)) ov(1)(#) else ov(2)(#)) }"

  private val collectionFmBinaryIfElseEmptyPattern = "{val m = ov(0)(#); ov(1)(); m.take(1).flatMap(_ => if (m.exists(_ == true)) # else Empty) }" // for methods with 1 branch `if`s with ifBranches parameter: ifBranches always is evaluated and exactly once! (even if 'false') to get falseBranch with Empty[Unit] from ifBranches
  private val collectionFmTernaryIfPattern = "{val m = ov(0)(#); m.take(1).flatMap(_ => if (m.exists(_ == true)) ov(1)(#) else ov(2)(#)) }"

  // 'if' test pattern pairs for collections

  lazy val collectionIfBinaryPatternPairs: Seq[(String, String)] = Seq(
    binaryOneArgExtensionOpsPattern.withOperation(mapIf) -> collectionMapBinaryIfPattern,
    binaryOneGroupStatementOpsPattern.withOperation(mapIf) -> collectionMapBinaryIfPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(mapIf) -> collectionMapBinaryIfElseUnitPattern,
    binaryOneArgExtensionOpsPattern.withOperation(mIf) -> collectionMBinaryIfPattern,
    binaryOneGroupStatementOpsPattern.withOperation(mIf) -> collectionMBinaryIfPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(mIf) -> collectionMBinaryIfElseUnitPattern
  )

  lazy val collectionIfTernaryPatternPairs: Seq[(String, String)] = Seq(
    ternaryTwoArgsExtensionOpsPattern.withOperation(mapIf) -> collectionMapTernaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(mapIf) -> collectionMapTernaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(mapIf) -> collectionMapTernaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(mapIf) -> collectionMapTernaryIfPattern,
    ternaryTwoArgsExtensionOpsPattern.withOperation(mIf) -> collectionMTernaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(mIf) -> collectionMTernaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(mIf) -> collectionMTernaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(mIf) -> collectionMTernaryIfPattern
  )

  lazy val collectionFlatIfBinaryPatternPairs: Seq[(String, String)] = Seq(
    binaryOneArgExtensionOpsPattern.withOperation(flatMapIf) -> collectionFlatMapBinaryIfElseEmptyPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(flatMapIf) -> collectionFlatMapBinaryIfElseEmptyPattern,
    binaryOneArgExtensionOpsPattern.withOperation(fmIf) -> collectionFmBinaryIfElseEmptyPattern,
    binaryTwoGroupsStatementOpsPattern.withOperation(fmIf) -> collectionFmBinaryIfElseEmptyPattern
  )

  lazy val collectionFlatIfTernaryPatternPairs: Seq[(String, String)] = Seq(
    ternaryTwoArgsExtensionOpsPattern.withOperation(flatMapIf) -> collectionFlatMapTernaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(flatMapIf) -> collectionFlatMapTernaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(flatMapIf) -> collectionFlatMapTernaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(flatMapIf) -> collectionFlatMapTernaryIfPattern,
    ternaryTwoArgsExtensionOpsPattern.withOperation(fmIf) -> collectionFmTernaryIfPattern,
    ternaryElseExtensionOpsPattern.withOperation(fmIf) -> collectionFmTernaryIfPattern,
    ternaryOneGroupStatementOpsPattern.withOperation(fmIf) -> collectionFmTernaryIfPattern,
    ternaryElseStatementOpsPattern.withOperation(fmIf) -> collectionFmTernaryIfPattern
  )


  /// helper methods to generate argument lists combinations

  @tailrec
  final def appendArgumentsCombinations(arguments: Seq[(String, String)], times: Int, initialCombinations: Seq[(Seq[String], Seq[String])] = Seq(Nil -> Nil)): Seq[(Seq[String], Seq[String])] =
    if (times <= 0) initialCombinations
    else
      appendArgumentsCombinations(
        arguments,
        times - 1,
        initialCombinations.flatMap { pairOfSeqs =>
          arguments.map(vp => (pairOfSeqs._1 :+ vp._1, pairOfSeqs._2 :+ vp._2))
        }
      )

  def prependedArgumentsCombinationsWithoutPureBooleans(arguments: Seq[(String, String)], times: Int): Seq[(List[String], List[String])] =
    dropPureBooleanCombinations(prependArgumentsCombinations(arguments, times))

  private val BooleanLiterals: Set[String] = Set(sFalse, sTrue, sInversion + sFalse, sInversion + sTrue)

  private def dropPureBooleanCombinations(combinations: Seq[(List[String], List[String])]): Seq[(List[String], List[String])] =
    combinations.filterNot(_._1.forall(BooleanLiterals))

  @tailrec
  private final def prependArgumentsCombinations(arguments: Seq[(String, String)], times: Int, initialCombinations: Seq[(List[String], List[String])] = Seq(Nil -> Nil)): Seq[(List[String], List[String])] =
    if (times <= 0) initialCombinations
    else
      prependArgumentsCombinations(
        arguments,
        times - 1,
        initialCombinations.flatMap { pairOfLists =>
          arguments.map(vp => (vp._1 :: pairOfLists._1, vp._2 :: pairOfLists._2))
        }
      )
}
