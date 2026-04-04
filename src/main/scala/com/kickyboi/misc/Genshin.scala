package com.kickyboi.misc

import org.joda.time.format.DateTimeFormat
import org.joda.time.{Days, LocalDate}
import spire.math.Rational

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Random

// Please ignore the vars, im not proud of this
object Genshin extends App {
  private val patch50 = LocalDate.parse("2024-08-28")

  var date = LocalDate.parse("2026-01-24")

  var keys = 4
  var chars = 0

  while (keys <= 8) {
    date = date.plusDays(1)
    if (isOddPatchDay(date)) {
      chars += 2
      println(s"($keys keys) ($chars echoes) ${Utils.formatFancyDate(date)}, new odd patch 5.${getPatchNumber(date)}, 2 new echoes")
    }
    if (keys >= 2 && chars > 0) {
      val buys = math.min(keys / 2, chars)
      chars -= buys
      keys -= buys * 2
      println(s"($keys keys) ($chars echoes) bought $buys echoes")
    }
    if (isFirstOfMonth(date)) {
      keys += 2
      println(s"($keys keys) ($chars echoes) 1st of ${DateTimeFormat.forPattern("MMMM").print(date)} ${date.getYear}, new theater drops")
    }
    if (keys >= 2 && chars > 0) {
      val buys = math.min(keys / 2, chars)
      chars -= buys
      keys -= buys * 2
      println(s"($keys keys) ($chars echoes) bought $buys echoes")
    }
  }

  private def getPatchNumber(date: LocalDate): Int = Days.daysBetween(patch50, date).getDays.abs / 42

  private def isOddPatchDay(date: LocalDate): Boolean = Days.daysBetween(patch50, date).getDays.abs % 84 == 42

  private def isFirstOfMonth(date: LocalDate): Boolean = date.getDayOfMonth == 1
}

object Patch extends App {
  private val patch50 = LocalDate.parse("2024-08-28")
  var date = patch50

  var num = 0

  while (num <= 20) {
    date = date.plusDays(1)
    if (isPatchDay(date)) {
      num += 1
      if (num > 12) println(s"patch ${(num / 9) + 5}.${num % 9}: $date")
    }
  }

  private def isPatchDay(day: LocalDate): Boolean = Days.daysBetween(patch50, day).getDays.abs % 42 == 0
}

object Patch2 extends App {
  for (num <- 6 to 20) println(s"patch ${(num / 9) + 5}.${num % 9}: ${LocalDate.parse("2024-08-28").plusDays(42 * num)}")
}

object FormatSurvey {
  def main(args: Array[String]): Unit = {
    val inputPath = "C:\\Users\\saura\\projects\\genshin\\src\\main\\scala\\com\\kickyboi\\misc\\SurveyBase.md"
    val outputPath = "C:\\Users\\saura\\projects\\genshin\\src\\main\\scala\\com\\kickyboi\\misc\\Survey.md"

    val source = Source.fromFile(inputPath)
    val lines = source.getLines().toList
    source.close()

    val numberedLines = lines
      .filter(_.trim.nonEmpty) // skip empty lines
      .zipWithIndex
      .map { case (line, idx) => s"${idx + 1}: $line" }

    val writer = new PrintWriter(new File(outputPath))
    numberedLines.foreach(writer.println)
    writer.close()

    println(s"Done! Wrote ${numberedLines.length} lines to $outputPath")
  }
}

object CoinFlipSimulation extends App {
  private val pointOdds: Map[Int, Double] = Map(
    0 -> 0.5,
    1 -> 0.58,
    2 -> 0.75,
    3 -> 1.0
  )

  private val rng = new Random(System.nanoTime())

  private def coinFlipWin(odds: Double): Boolean =
    rng.nextDouble() < odds

  private def updatePoints(points: Int, flip: Boolean): Int =
    (points, flip) match {
      case (3, false) => 3
      case (_, false) => points + 1
      case (0, true) => 0
      case (1, true) => 0
      case (2, true) => 1
      case (3, true) => 1
    }

  private def simulate(flips: Int): Double = {
    val (_, wins) = (1 to flips).foldLeft((1, 0)) {
      case ((points, wins), _) =>
        val flip = coinFlipWin(pointOdds(points))
        val newPoints = updatePoints(points, flip)
        val newWins = if (flip) wins + 1 else wins
        (newPoints, newWins)
    }
    wins.toDouble / flips
  }

  println(simulate(10_000_000))
}

object CoinFlipOdds extends App {
  private def calc(a1: Double, a2: Double): Rational = {
    0.5 * Rational(232, 537) +
      a1 * Rational(200, 537) +
      a2 * Rational(84, 537) +
      1 * Rational(21, 537)
  }

  private def truncate(x: Double, after: Int): Double = {
    (x * math.pow(10, after)).toLong / math.pow(10, after)
  }

  private def generateMatrix(calcFunc: (Double, Double) => Rational,
                             a1Start: Double, a1End: Double, a1Step: Double,
                             a2Start: Double, a2End: Double, a2Step: Double): Seq[Seq[Double]] = {

    val a1Values = BigDecimal(a1Start) to BigDecimal(a1End) by BigDecimal(a1Step)
    val a2Values = BigDecimal(a2Start) to BigDecimal(a2End) by BigDecimal(a2Step)

    for (a1 <- a1Values)
      yield for (a2 <- a2Values)
        yield truncate(calcFunc(a1.toDouble, a2.toDouble).toDouble * 100, 3)
  }

  // Run the function
  private val matrix = generateMatrix(calc, 0.5, 0.6, 0.01, 0.7, 0.8, 0.01)

  // Pretty-print the matrix
  matrix.zipWithIndex.foreach { case (row, i) =>
    println(s"a1 = ${0.5 + i * 0.01} -> " + row.mkString(", "))
  }
}

object CoinFlipOddsPretty extends App {
  def calc(a1: Double, a2: Double): Rational = {
    0.5 * Rational(232, 537) +
    a1 * Rational(200, 537) +
    a2 * Rational(84, 537) +
    1 * Rational(21, 537)
  }

  def generateMatrix(calcFunc: (Double, Double) => Rational,
                     a1Start: Double, a1End: Double, a1Step: Double,
                     a2Start: Double, a2End: Double, a2Step: Double
                    ): (Seq[Double], Seq[Double], Seq[Seq[Rational]]) = {

    val a1Vals = BigDecimal(a1Start) to BigDecimal(a1End) by BigDecimal(a1Step)
    val a2Vals = BigDecimal(a2Start) to BigDecimal(a2End) by BigDecimal(a2Step)

    val matrix =
      for (a1 <- a1Vals)
        yield for (a2 <- a2Vals)
          yield calcFunc(a1.toDouble, a2.toDouble)

    (a1Vals.map(_.toDouble), a2Vals.map(_.toDouble), matrix.map(_.toSeq))
  }

  // Truncate helper for printing
  private def truncate(x: Double, after: Int): Double = {
    (x * math.pow(10, after)).toLong / math.pow(10, after)
  }

  // Pretty table printer
  private def printTable(a1Vals: Seq[Double], a2Vals: Seq[Double], matrix: Seq[Seq[Rational]]): Unit = {
    // Header row
    print(f"${"a1\\a2"}%-3s")
    a2Vals.foreach(a2 => print(f"\u001b[32m${truncate(a2, 3)}%7.2f\u001b[0m"))
    println()

    // Rows
    for ((a1, row) <- a1Vals.zip(matrix)) {
      print(f"\u001b[32m ${truncate(a1, 3)}%3.2f\u001b[0m")
      for (value <- row) {
        print(f"${truncate(value.toDouble * 100, 1)}%7.1f")
      }
      println()
    }
  }

  val (a1Vals, a2Vals, matrix) = generateMatrix(calc, 0.5, 0.6, 0.01, 0.7, 0.8, 0.01)
  printTable(a1Vals, a2Vals, matrix)
}