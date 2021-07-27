package sands.sugar.bms

import sands.sugar.bms.BooleanMonads._
import sands.sugar.bms.typeclass.functions.BooleanFunctions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}

/*
 * Created by Serhiy Shamshetdinov
 * at 27.07.2021 18:00
 */

/** to play with library */
object Main extends App {

  lAND(false, {println("1"); false})
  lAND(true, {println("2"); false})

  true && Some(true)
  Some(true) && false
  true && Option(true)
  val o = Some(true) && Some(true)

  true && Try(true)
  val t = Success(true) && Success(true)
  true && List(true)
  val l = List(true) && List(true)

  def ar[A](future: Future[A]): Try[A] = Try(Await.result(future, 1.minute))
  val failedFuture = Future.failed[Boolean](new Exception("Failed"))
  val s0 = Future.successful(0)
  val s1 = Future.successful(1)
  val sTrue = Future.successful(true)
  val sFalse = Future.successful(false)

  val res0 = ar(true && Future.successful(true))
  val res1 = ar(true && Future(true))
  val res2 = ar(true && failedFuture)
  val res3 = ar(false && Future.successful(false))
  val res4 = ar(false && failedFuture)
  val res5 = ar(fmIf(Future.successful(false), Future.successful(true), Future.successful(false)))
  println(res0, res1, res2, res3, res4, res5)

  val res10 = ar(Future(true) && Future.successful(true))
  val res11 = ar(Future.successful(true) && Future.successful(true))
  val res12 = ar(Future.successful(true) && failedFuture)
  val res13 = ar(Future.successful(false) && Future.successful(false))
  val res14 = ar(Future.successful(false) && failedFuture)
  val res15 = ar(failedFuture && Future(true))
  println(res10, res11, res12, res13, res14, res15)

  val res20 = ar(mIf(sTrue, 1, 0))
  val res21 = ar(fmIf(sTrue, s1, s0))
  val res22 = mIf(Some(false), 1, 0)
  val res23 = fmIf(Some(false), Some(1), Some(0))
  val res24 = mIf(sTrue.value.get, 1, 0)
  val res25 = fmIf(sTrue.value.get, Success(1), Success(0))
  println(res20, res21, res22, res23, res24, res25)
}
