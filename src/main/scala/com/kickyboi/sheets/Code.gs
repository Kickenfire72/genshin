// MAKERS

function PRIMER(goalDate) { return MAKER(goalDate, addPrimosToday) }
function MORAER(goalDate) { return MAKER(goalDate, addMoraToday) / 1000 }
function EXPER(goalDate) { return MAKER(goalDate, addExpToday) }

function MAKER(goalDate, makerFunction) {
  var sum = 0

  var today = new Date()
  today.setDate(today.getDate() + 1)

  var goal = new Date(goalDate)
  //goal.setHours(0, 0, 0, 0);
  //Logger.log(goal)
  goal.setDate(goal.getDate() + 1)

  for (var date = today; date <= goal; date.setDate(date.getDate() + 1)) {
    Logger.log(date)
    sum += makerFunction(date)
  }

  return sum
}

function BADDER(bossDays, talentDays, curMora, curExp, goalMora, goalExp, resin, currentBpLevel, maxBpLevel) {
  counter = 0

  var start = new Date()
  start.setHours(0, 0, 0, 0);

  spDaysMora = (bossDays*moraDayBosses - talentDays*moraDayTalents)/1000
  do {
    start.setDate(start.getDate() + 1)
    counter++

    neededDaysE = Math.max((goalExp - curExp - EXPER(start))/fullExpDay, 0)
    expDaysMora = neededDaysE*moraDayResources/1000
    neededDaysM = Math.max((goalMora - curMora - MORAER(start) - spDaysMora - expDaysMora)/fullMoraDay, 0)

  } while ( (neededDaysM + neededDaysE + bossDays + talentDays - resin/dayResin) >= counter )

  return STRINGDATE(start)
}
