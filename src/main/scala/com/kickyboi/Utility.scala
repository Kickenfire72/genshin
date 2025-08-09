package com.kickyboi

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

object Utility {
  def formatFancyDate(date: LocalDate): String =
    s"${dayWithSuffix(date.getDayOfMonth)} of ${DateTimeFormat.forPattern("MMMM").print(date)}"

  private def dayWithSuffix(day: Int): String = {
    if (day >= 11 && day <= 13) s"${day}th"
    else {
      day % 10 match {
        case 1 => s"${day}st"
        case 2 => s"${day}nd"
        case 3 => s"${day}rd"
        case _ => s"${day}th"
      }
    }
  }
}
