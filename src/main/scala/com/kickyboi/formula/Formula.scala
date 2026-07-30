package com.kickyboi.formula

import com.kickyboi.formula.Element.*
import com.kickyboi.formula.ScalingType.*
import com.kickyboi.formula.MotionType.*
import com.kickyboi.formula.WeaponType.*

enum WeaponType:
  case Sword, Greatsword, Polearm, Bow, Catalyst
enum MotionType:
  case Normal, Charged, Plunge, Skill, Burst, LunarEC, LunarBloom, LunarCR, StellarSC, StellarSW
enum ScalingType:
  case Hp, Atk, Def, Em

enum Element:
  case Pyro, Hydro, Electro, Cryo, Anemo, Geo, Dendro, Physical

enum Reaction:
  case Vape, Melt, Overload, Superconduct, ElectroCharged, Burning, Shattered, Swirl, Bloom, Hyperbloom, Burgeon,
  Aggravate, Spread, LunarEC, LunarBloom, LunarCR, StellarSC, StellarSW

def getEmMultiplier(reaction: Reaction, em: Int): Double =
  import com.kickyboi.formula.Reaction.*
  reaction match
    case Vape | Melt => 2.78 * em / (em + 1400)
    case Overload | Superconduct | ElectroCharged | Burning | Shattered | Swirl | Bloom | Hyperbloom | Burgeon => 16 * em / (em + 2000)
    case Aggravate | Spread => 5 * em / (em + 1200)
    case LunarEC | LunarBloom | LunarCR | StellarSC | StellarSW => 6 * em / (em + 2000)

def getResMultiplier(res: Double): Double = {
  if res < 0 then 1-res/2
  else if res < 0.75 then 1-res
  else 1 / (4*res+1)
}

case class Scaling(`type`: ScalingType, values: Seq[Double])
case class MotionModifiers(quill: Int = 0, isMelt: Boolean = false, isVape: Boolean = false, isSpread: Boolean = false, isAggravate: Boolean = false,
                           isHyperBloom: Boolean = false, isHyperBloomX2: Boolean = false, isBurgeon: Boolean = false, isBurgeonX2: Boolean = false)
case class Motion(name: String, `type`: MotionType, element: Element, scalings: Seq[Scaling])

case class Character(name: String, level: Int, weaponType: WeaponType, element: Element,
                     baseHP: Int, baseATK: Int, baseDEF: Int,
                     baseEM: Int, baseCR: Double, baseCD: Double,
                     motions: Map[String, Motion])

case class Target(level: Int, res: Double)

val target = Target(105, 10)

case class Weapon(name: String, weaponType: WeaponType, baseATK: Int,
                  hpPct: Option[Double], atkPct: Option[Double], defPct: Option[Double],
                  em: Option[Int], cr: Option[Double], cd: Option[Double])

case class Buff(flatHp: Option[Int] = None, flatAtk: Option[Int] = None, flatDef: Option[Int] = None,
                hpPct: Option[Double] = None, atkPct: Option[Double] = None, defPct: Option[Double] = None,
                em: Option[Int] = None, cr: Option[Double] = None, cd: Option[Double] = None,
                normalDmgBonus: Option[Double] = None, chargeDmgBonus: Option[Double] = None, plungeDmgBonus: Option[Double] = None, skillDmgBonus: Option[Double] = None, burstDmgBonus: Option[Double] = None,
                pyroDmgBonus: Option[Double] = None, hydroDmgBonus: Option[Double] = None, electroDmgBonus: Option[Double] = None, cryoDmgBonus: Option[Double] = None, anemoDmgBonus: Option[Double] = None, geoDmgBonus: Option[Double] = None, dendroDmgBonus: Option[Double] = None, physicalDmgBonus: Option[Double] = None,
                lunarECDmgBonus: Option[Double] = None, lunarBloomDmgBonus: Option[Double] = None, lunarCRDmgBonus: Option[Double] = None, stellarSCDmgBonus: Option[Double] = None, stellarSWDmgBonus: Option[Double] = None,
                lunarECBaseIncrease: Option[Int] = None, lunarBloomBaseIncrease: Option[Int] = None, lunarCRBaseIncrease: Option[Int] = None, stellarSCBaseIncrease: Option[Int] = None, stellarSWBaseIncrease: Option[Int] = None,
                stellarSCStacks: Option[Int] = None, stellarSWLevel: Option[Int] = None,
                lunarECElevation: Option[Double] = None, lunarBloomElevation: Option[Double] = None, lunarCRElevation: Option[Double] = None, stellarSCElevation: Option[Double] = None, stellarSWElevation: Option[Double] = None,
                pyroResShred: Option[Double] = None, hydroResShred: Option[Double] = None, electroResShred: Option[Double] = None, cryoResShred: Option[Double] = None, anemoResShred: Option[Double] = None, geoResShred: Option[Double] = None, dendroResShred: Option[Double] = None, physicalResShred: Option[Double] = None,
                defShred: Option[Double] = None)

case class Period(startTime: Double, endTime: Double) {
  def contains(time: Double): Boolean = (startTime <= time) && (endTime >= time)
}
case class CombatBuff(period: Period, buff: Buff)

case class CombatMotion(time: Double, motion: Motion, modifiers: MotionModifiers) {
  def calcDamage(character: Character, combatBuffs: Seq[CombatBuff]): Double =
    val applyingBuffs = combatBuffs.filter(_.period.contains(time)).map(_.buff)

    val totalHp = character.baseHP * (1 + applyingBuffs.flatMap(_.hpPct).sum / 100) + applyingBuffs.flatMap(_.flatHp).sum
    val totalAtk = character.baseATK * (1 + applyingBuffs.flatMap(_.atkPct).sum / 100) + applyingBuffs.flatMap(_.flatAtk).sum
    val totalDef = character.baseHP * (1 + applyingBuffs.flatMap(_.defPct).sum / 100) + applyingBuffs.flatMap(_.flatDef).sum
    val totalEm = character.baseEM + applyingBuffs.flatMap(_.em).sum

    val scalingTotal = motion.scalings.map{ scaling =>
      val scalingValue: Double = scaling.`type` match
        case Hp => totalHp
        case Atk => totalAtk
        case Def => totalDef
        case Em => totalEm
      scalingValue * scaling.values(9) / 100
    }.sum + modifiers.quill

    val elementalDmgBonus = motion.element match
      case Pyro => applyingBuffs.flatMap(_.pyroDmgBonus).sum
      case Hydro => applyingBuffs.flatMap(_.hydroDmgBonus).sum
      case Electro => applyingBuffs.flatMap(_.electroDmgBonus).sum
      case Cryo => applyingBuffs.flatMap(_.cryoDmgBonus).sum
      case Anemo => applyingBuffs.flatMap(_.anemoDmgBonus).sum
      case Geo => applyingBuffs.flatMap(_.geoDmgBonus).sum
      case Dendro => applyingBuffs.flatMap(_.dendroDmgBonus).sum
      case Physical => applyingBuffs.flatMap(_.physicalDmgBonus).sum

    val damageBonus: Double = motion.`type` match
      case Normal => applyingBuffs.flatMap(_.normalDmgBonus).sum + elementalDmgBonus
      case Charged => applyingBuffs.flatMap(_.chargeDmgBonus).sum + elementalDmgBonus
      case Plunge => applyingBuffs.flatMap(_.plungeDmgBonus).sum + elementalDmgBonus
      case Skill => applyingBuffs.flatMap(_.skillDmgBonus).sum + elementalDmgBonus
      case Burst => applyingBuffs.flatMap(_.burstDmgBonus).sum + elementalDmgBonus
      case LunarEC => applyingBuffs.flatMap(_.lunarECDmgBonus).sum + getEmMultiplier(Reaction.LunarEC, totalEm)
      case LunarBloom => applyingBuffs.flatMap(_.lunarBloomDmgBonus).sum + getEmMultiplier(Reaction.LunarBloom, totalEm)
      case LunarCR => applyingBuffs.flatMap(_.lunarCRDmgBonus).sum + getEmMultiplier(Reaction.LunarCR, totalEm)
      case StellarSC => applyingBuffs.flatMap(_.stellarSCDmgBonus).sum + getEmMultiplier(Reaction.StellarSC, totalEm)
      case StellarSW => applyingBuffs.flatMap(_.stellarSWDmgBonus).sum + getEmMultiplier(Reaction.StellarSW, totalEm)

    val baseIncrease: Int = motion.`type` match
      case LunarEC => applyingBuffs.flatMap(_.lunarECBaseIncrease).sum
      case LunarBloom => applyingBuffs.flatMap(_.lunarBloomBaseIncrease).sum
      case LunarCR => applyingBuffs.flatMap(_.lunarCRBaseIncrease).sum
      case StellarSC => applyingBuffs.flatMap(_.stellarSCBaseIncrease).sum
      case StellarSW => applyingBuffs.flatMap(_.stellarSWBaseIncrease).sum
      case _ => 0

    val stellarSCStacks = applyingBuffs.flatMap(_.stellarSCStacks).sum
    val baseMultiplier: Double = motion.`type` match
      case LunarEC => 3
      case LunarCR => 1.6
      case StellarSC => if stellarSCStacks == 0 then 1 else 1.4 + stellarSCStacks * 0.05
      case _ => 1
    val elevation: Double = motion.`type` match
      case LunarEC => applyingBuffs.flatMap(_.lunarECElevation).sum
      case LunarBloom => applyingBuffs.flatMap(_.lunarBloomElevation).sum
      case LunarCR => applyingBuffs.flatMap(_.lunarCRElevation).sum
      case StellarSC => applyingBuffs.flatMap(_.stellarSCElevation).sum
      case StellarSW => applyingBuffs.flatMap(_.stellarSWElevation).sum
      case _ => 0

    val cr = character.baseCR + applyingBuffs.flatMap(_.cr).sum
    val cd = character.baseCD + applyingBuffs.flatMap(_.cd).sum
    val critMultiplier = 1 + Math.min(1, cr/100) * cd/100

    val resShred = motion.element match
      case Pyro => applyingBuffs.flatMap(_.pyroResShred).sum
      case Hydro => applyingBuffs.flatMap(_.hydroResShred).sum
      case Electro => applyingBuffs.flatMap(_.electroResShred).sum
      case Cryo => applyingBuffs.flatMap(_.cryoResShred).sum
      case Anemo => applyingBuffs.flatMap(_.anemoResShred).sum
      case Geo => applyingBuffs.flatMap(_.geoResShred).sum
      case Dendro => applyingBuffs.flatMap(_.dendroResShred).sum
      case Physical => applyingBuffs.flatMap(_.physicalResShred).sum
    val res = target.res - resShred
    val resMultiplier = getResMultiplier(res/100)
    
    val defShred = applyingBuffs.flatMap(_.defShred).sum
    val levelMultiplier: Double = (character.level.toDouble + 100)/(character.level + target.level + 200)
    val defMultiplier: Double = levelMultiplier / (1 - defShred/100)

    // TODO: additive damage
    (baseMultiplier * scalingTotal * (1+baseIncrease/100) * (1+damageBonus/100) + 0) // additive
      * critMultiplier * (1+elevation/100) * resMultiplier * defMultiplier

}
case class CharacterCombatMotions(character: Character, combatMotions: Seq[CombatMotion])

val emptyMotion = Motion("none", Normal, Physical, Seq(Scaling(Atk, Seq[Double](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))))

object Formula {
  def main(): Unit =
    val neferMotions = Map[String, Motion](
      "shades" -> Motion("shades", LunarBloom, Dendro, Seq(Scaling(Em, Seq[Double](96, 103.2, 110.4, 120, 127.2, 134.4, 144, 153.6, 163.2, 172.8, 182.4, 192, 204, 216, 228))))
    )
    val nefer = Character("nefer", 90, Catalyst, Dendro, 12704, 344, 799, 100, 5.0, 88.4, neferMotions)
    val blackMarrow = Weapon("blackMarrowLantern", Catalyst, 454, None, None, None, Some(221), None, None)

    val neferRotation = Seq(
      CombatMotion(1.0, nefer.motions.getOrElse("shades", emptyMotion), MotionModifiers())
    )

    val neferBuffs = Seq(
      CombatBuff(Period(0.0, 2.0), Buff(flatHp = Some(4780), em = Some(56), cd = Some(21.0), cr = Some(3.9), flatAtk = Some(18)))
    )
}

object FormulaXiao extends App{
  def main(): Unit =
    val xiaoMotions = Map[String, Motion](
      "highPlunge" -> Motion("shades", Plunge, Anemo, Seq(Scaling(Atk, Seq[Double](0, 0, 0, 0, 0, 0, 0, 0, 0, 404, 0, 0, 0, 0, 0))))
    )
    val xiao = Character("xiao", 90, Polearm, Anemo, 12704, 859, 799, 0, 100, 237.2, xiaoMotions)
    val blackcliff = Weapon("blackcliff", Polearm, 454, None, None, None, Some(221), None, None)

    val xiaoRotation = Seq(
      CombatMotion(1.0, xiao.motions.getOrElse("highPlunge", emptyMotion), MotionModifiers(8000))
    )

    val xiaoBuffs = Seq(
      CombatBuff(Period(0.0, 2.0), Buff(flatAtk = Some(311), atkPct = Some(46.6), anemoDmgBonus = Some(46.6))),
      CombatBuff(Period(0.0, 2.0), Buff(flatAtk = Some(79), atkPct = Some(17.5))),
      CombatBuff(Period(0.0, 2.0), Buff(atkPct = Some(40))),
      CombatBuff(Period(0.0, 2.0), Buff(plungeDmgBonus = Some(95.2))),
      CombatBuff(Period(0.0, 2.0), Buff(plungeDmgBonus = Some(50))),
      CombatBuff(Period(0.0, 2.0), Buff(plungeDmgBonus = Some(5))),
      CombatBuff(Period(0.0, 2.0), Buff(anemoDmgBonus = Some(38.3))),
      CombatBuff(Period(0.0, 2.0), Buff(cd = Some(40))),
      CombatBuff(Period(0.0, 2.0), Buff(anemoResShred = Some(30))),

      CombatBuff(Period(2.0, 3.0), Buff(cd = Some(400))),
    )

    val baseDamage = xiaoRotation.map(_.calcDamage(xiao, xiaoBuffs)).sum

    println(baseDamage)

  main()
}
