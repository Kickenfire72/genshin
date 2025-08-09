package com.kickyboi

import org.joda.time.format.DateTimeFormat
import org.joda.time.{Days, LocalDate}

import scala.io.Source
import java.io.{File, PrintWriter}

// Please ignore the vars, im not proud of this
object Genshin extends App {
  private val patch50 = LocalDate.parse("2024-08-28")

  var date = LocalDate.parse("2025-04-12")

  var keys = 0
  var chars = 1

  while (keys <= 4) {
    date = date.plusDays(1)
    if (isOddPatchDay(date)) {
      chars += 2
      println(s"($keys keys) ($chars echoes) ${Utility.formatFancyDate(date)}, new odd patch 5.${getPatchNumber(date)}, 2 new echoes")
    }
    if (keys >= 2 && chars > 0) {
      val buys = math.min(keys / 2, chars)
      chars -= buys
      keys -= buys * 2
      println(s"($keys keys) ($chars echoes) bought $buys echoes")
    }
    if (isFirstOfMonth(date)) {
      keys += 2
      println(s"($keys keys) ($chars echoes) 1st of ${DateTimeFormat.forPattern("MMMM").print(date)}, new theater drops")
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
      if (num > 5) println(s"patch ${(num/9)+5}.${num%9}: $date")
    }
  }

  private def isPatchDay(day: LocalDate): Boolean = Days.daysBetween(patch50, day).getDays.abs % 42 == 0
}

object Patch2 extends App {
  for (num <- 6 to 20) println(s"patch ${(num/9)+5}.${num%9}: ${LocalDate.parse("2024-08-28").plusDays(42*num)}")
}

object FormatSurvey {
  def main(args: Array[String]): Unit = {
    val inputPath = "C:\\Users\\saura\\projects\\genshin\\src\\main\\scala\\com\\kickyboi\\SurveyBase.md"
    val outputPath = "C:\\Users\\saura\\projects\\genshin\\src\\main\\scala\\com\\kickyboi\\Survey.md"

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