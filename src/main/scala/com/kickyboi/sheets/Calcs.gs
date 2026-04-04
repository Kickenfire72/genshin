function critMultiplierFromBase(rate, damage) {
  return 1 + rate * damage
}

function critMultiplierFromRelations(totalCv, ratio) {
  return 1 + (ratio * totalCv * totalCv)/(ratio + 2)/(ratio + 2)
}

function ratio(rate, damage) {
  baseMultiplier = critMultiplierFromRelations(240, 2)
  currentMultiplier = critMultiplierFromBase(rate, damage)

  return currentMultiplier / baseMultiplier
}

function output() {
  value = ratio(66.4, 140.6)
  console.log("current / 60:120 :",value)
  console.log("60:120 / current :",1/value)
}

function ratioOutput() {
  to12value = critMultiplierFromRelations(240, 2)
  to13value = critMultiplierFromRelations(240, 4)
  console.log("current / 60:120 :",to13value / to12value)
  console.log("60:120 / current :",to12value / to13value)
}
