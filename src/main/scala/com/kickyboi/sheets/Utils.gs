moraDailyComms = 54
moraExpeditions = 31.125

moraDayBosses = 36 // 1k + 3k per 20
moraDayTalents = 33 // 1k + 2.375k per 20
moraDayResources = 9 // 1k per 20
moraDayWeapons = 29 // 1k + 2.2k per 20
moraDayArtifacts = 32 // 1k + 2.525k per 20

moraWeeklyBounties = 150 // 0.03*3 + 0.02*3
moraWeeklyBosses = 22 // 11k per boss, assuming 1
moraWeeklyTeapot = 200

expWeeklyTeapot = 20

fullMoraDay = 0.549 // takes adventure exp stuff into account
fullExpDay = 55

dayResin = 180
dayPrimos = 60

abyssPrimos = 800
abyssMora = 700
abyssExp = 34.5

theaterPrimos = 1000
theaterMora = 600

moraBaseDay = moraDailyComms + moraExpeditions
moraBaseMonday = 2*moraWeeklyBosses
moraBaseArtifactDay = moraBaseDay + moraDayArtifacts

function loginPrimos(dayNb) {
  if (dayNb == 4) { return 20 };
  if (dayNb == 11) { return 20 };
  if (dayNb == 18) { return 20 };

  return 0;
}

function loginMora(dayNb) {
  if (dayNb == 3) { return 5 };
  if (dayNb == 7) { return 8 };
  if (dayNb == 10) { return 5 };
  if (dayNb == 14) { return 8 };
  if (dayNb == 17) { return 5 };
  if (dayNb == 21) { return 8 };
  if (dayNb == 24) { return 5 };
  if (dayNb == 29) { return 5 };
  if (dayNb == 30) { return 5 };

  return 0;
}

function loginExp(dayNb) {
  if (dayNb == 1) { return 0.75 };
  if (dayNb == 6) { return 0.5 };
  if (dayNb == 8) { return 0.75 };
  if (dayNb == 13) { return 0.5 };
  if (dayNb == 15) { return 0.75 };
  if (dayNb == 20) { return 0.5 };
  if (dayNb == 12) { return 1.25 };
  if (dayNb == 25) { return 3 };
  if (dayNb == 27) { return 0.75 };
  if (dayNb == 28) { return 3 };

  return 0;
}

function addPrimosToday(date) {
  dayNb = date.getDate()
  midsum = 0

  midsum += dayPrimos;
  midsum += loginPrimos(dayNb)
  if (dayNb == 1) { midsum += theaterPrimos; };
  if (dayNb == 16) { midsum += abyssPrimos; }

  return midsum
}

function addPrimosTodayNew(date) {
  midsum = 0

  var start56 = createDate(7, 5, 2025);
  number = (date - start56) / (1000 * 60 * 60 * 24)

  if (number % 42 == 7*1) { midsum += theaterPrimos; };
  if (number % 42 == 7*3) { midsum += abyssPrimos; };
  if (number % 42 == 7*5) { midsum += 450; };

  return midsum;
}

function createDate(day, month, year) {
  // Note: Month is zero-based in JavaScript/Google Apps Script (0 = January, 11 = December)
  return new Date(year, month - 1, day);
}

function addMoraToday(date) {
  dayNb = date.getDate()
  dayNm = date.getDay()
  midsum = 0

  midsum += moraBaseDay;
  midsum += loginMora(dayNb - 1);
  if (dayNb == 1) { midsum += theaterMora; };
  if (dayNb == 16) { midsum += abyssMora; };
  if (dayNm == 1) { midsum += moraBaseMonday; };

  return midsum
}

function addExpToday(date) {
  dayNb = date.getDate()
  dayNm = date.getDay()
  midsum = 0

  midsum += loginExp(dayNb);
  if (dayNb == 1) { midsum += 0; };
  if (dayNb == 16) { midsum += abyssExp; };
  if (dayNm == 1) { midsum += expWeeklyTeapot; };

  return midsum
}

function STRINGDATE(day) {
  st = "\"" + day.getFullYear() + "-" + ("0"+(day.getMonth()+1)).slice(-2) + "-" + ("0" + day.getDate()).slice(-2) + "\""
  return st
}

function STRINGMATS(mats) {
  st = " " + mats + " -- --"
  return st
}

tMats = {
  2: 5*1,
  3: 3*3,
  4: 4*3,
  5: 6*3,
  6: 9*3,
  7: 4*9,
  8: 6*9,
  9: 9*9,
  10: 12*9
}

tBooks = {
  2: 3*1,
  3: 2*3,
  4: 4*3,
  5: 6*3,
  6: 9*3,
  7: 4*9,
  8: 6*9,
  9: 12*9,
  10: 16*9
}

tMora = {
  2: 12.5,
  3: 17.5,
  4: 25,
  5: 30,
  6: 37.5,
  7: 120,
  8: 260,
  9: 450,
  10: 700
}

bpMora = {
  2: 0.024,
  8: 0.024,
  12: 0.036,
  18: 0.036,
  22: 0.048,
  28: 0.048,
  32: 0.072,
  38: 0.072,
  42: 0.09,
  48: 0.09
};

bpExp = {
  1: 1.5,
  4: 1.5,
  7: 1.5,
  11: 2.5,
  14: 2.5,
  17: 2.5,
  21: 3,
  24: 3,
  27: 3,
  31: 5,
  34: 5,
  37: 5,
  41: 6,
  44: 6,
  47: 6
};

function sumOverMap(map, a, b) {
  sum = 0
  for (key in map) {
    if (key > a && key <= b) {
      sum += map[key];
    }
  }

  return sum;
}

function GETBPMORA(a, b) { return sumOverMap(bpMora, a, b) }
function GETBPEXP(a, b) { return sumOverMap(bpExp, a, b) }
function TMATS(a, b) { return sumOverMap(tMats, a, b) }
function TBOOKS(a, b) { return sumOverMap(tBooks, a, b) }
function TMORA(a, b) { return sumOverMap(tMora, a, b)/1000 }
