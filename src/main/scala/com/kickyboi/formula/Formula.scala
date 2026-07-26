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


case class Scaling(`type`: ScalingType, values: Seq[Double])
case class Motion(name: String, `type`: MotionType, element: Element, scalings: Seq[Scaling])

case class Character(name: String, weaponType: WeaponType, element: Element,
                     baseHP: Int, baseATK: Int, baseDEF: Int,
                     baseEM: Int, baseCR: Double, baseCD: Double,
                     motions: Map[String, Motion])

case class Weapon(name: String, weaponType: WeaponType, baseATK: Int,
                  hpPct: Option[Double], atkPct: Option[Double], defPct: Option[Double],
                  em: Option[Int], cr: Option[Double], cd: Option[Double])

case class CombatMotion(time: Double, motion: Motion) {
  def calcDamage(character: Character, combatBuffs: Seq[CombatBuff]): Double =
    val applyingBuffs = combatBuffs.filter(_.period.contains(time))
    val scalingValue = motion.scalings.map(calcScalingDamage(_, character, applyingBuffs)).sum
    val damageBonus: Double = motion.`type` match
      case Normal | Charged | Plunge | Skill | Burst =>
        val typeDmgBonus = motion.`type` match
          case Normal => applyingBuffs.flatMap(_.buff.normalDmgBonus).sum / 100
          case Charged => applyingBuffs.flatMap(_.buff.chargeDmgBonus).sum / 100
          case Plunge => applyingBuffs.flatMap(_.buff.plungeDmgBonus).sum / 100
          case Skill => applyingBuffs.flatMap(_.buff.skillDmgBonus).sum / 100
          case Burst => applyingBuffs.flatMap(_.buff.burstDmgBonus).sum / 100
        val elementDmgBonus = motion.element match
          case Pyro => applyingBuffs.flatMap(_.buff.pyroDmgBonus).sum / 100
          case Hydro => applyingBuffs.flatMap(_.buff.hydroDmgBonus).sum / 100
          case Electro => applyingBuffs.flatMap(_.buff.electroDmgBonus).sum / 100
          case Cryo => applyingBuffs.flatMap(_.buff.cryoDmgBonus).sum / 100
          case Anemo => applyingBuffs.flatMap(_.buff.anemoDmgBonus).sum / 100
          case Geo => applyingBuffs.flatMap(_.buff.geoDmgBonus).sum / 100
          case Dendro => applyingBuffs.flatMap(_.buff.dendroDmgBonus).sum / 100
          case Physical => applyingBuffs.flatMap(_.buff.physicalDmgBonus).sum / 100
        typeDmgBonus + elementDmgBonus
      case LunarEC => applyingBuffs.flatMap(_.buff.lunarECDmgBonus).sum / 100
      case LunarBloom => applyingBuffs.flatMap(_.buff.lunarBloomDmgBonus).sum / 100
      case LunarCR => applyingBuffs.flatMap(_.buff.lunarCRDmgBonus).sum / 100
      case StellarSC => applyingBuffs.flatMap(_.buff.stellarSCDmgBonus).sum / 100
      case StellarSW => applyingBuffs.flatMap(_.buff.stellarSWDmgBonus).sum / 100
    1 + damageBonus / 100

  private def calcScalingDamage(scaling: Scaling, character: Character, applyingBuffs: Seq[CombatBuff]): Double =
    scaling match
      case Hp => character.baseHP * (1 + applyingBuffs.flatMap(_.buff.hpPct).sum / 100) + applyingBuffs.flatMap(_.buff.flatHp).sum
      case Atk => character.baseATK * (1 + applyingBuffs.flatMap(_.buff.atkPct).sum / 100) + applyingBuffs.flatMap(_.buff.flatAtk).sum
      case Def => character.baseHP * (1 + applyingBuffs.flatMap(_.buff.defPct).sum / 100) + applyingBuffs.flatMap(_.buff.flatDef).sum
      case Em => character.baseEM + applyingBuffs.flatMap(_.buff.em).sum
}
case class CharacterCombatMotions(character: Character, combatMotions: Seq[CombatMotion])

case class Buff(flatHp: Option[Int] = None, flatAtk: Option[Int] = None, flatDef: Option[Int] = None,
                hpPct: Option[Double] = None, atkPct: Option[Double] = None, defPct: Option[Double] = None,
                em: Option[Int] = None, cr: Option[Double] = None, cd: Option[Double] = None,
                normalDmgBonus: Option[Double] = None, chargeDmgBonus: Option[Double] = None, plungeDmgBonus: Option[Double] = None, skillDmgBonus: Option[Double] = None, burstDmgBonus: Option[Double] = None,
                pyroDmgBonus: Option[Double] = None, hydroDmgBonus: Option[Double] = None, electroDmgBonus: Option[Double] = None, cryoDmgBonus: Option[Double] = None, anemoDmgBonus: Option[Double] = None, geoDmgBonus: Option[Double] = None, dendroDmgBonus: Option[Double] = None, physicalDmgBonus: Option[Double] = None,
                lunarECDmgBonus: Option[Double] = None, lunarBloomDmgBonus: Option[Double] = None, lunarCRDmgBonus: Option[Double] = None, stellarSCDmgBonus: Option[Double] = None, stellarSWDmgBonus: Option[Double] = None)

case class Period(startTime: Double, endTime: Double) {
  def contains(time: Double): Boolean = (startTime <= time) && (endTime >= time)
}
case class CombatBuff(period: Period, buff: Buff)

object Formula {
  @main def main(): Unit =
    val emptyMotion = Motion("none", Normal, Physical, Seq(Scaling(Atk, Seq[Double](0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))))
    val motions = Map[String, Motion](
      "shades" -> Motion("shades", LunarBloom, Dendro, Seq(Scaling(Em, Seq[Double](96, 103.2, 110.4, 120, 127.2, 134.4, 144, 153.6, 163.2, 172.8, 182.4, 192, 204, 216, 228))))
    )
    val nefer = Character("nefer", Catalyst, Dendro, 12704, 344, 799, 100, 5.0, 88.4, motions)
    val blackMarrow = Weapon("blackMarrowLantern", Catalyst, 454, None, None, None, Some(221), None, None)

    val neferRotation = Seq(
      CombatMotion(1.0, nefer.motions.getOrElse("shades", emptyMotion))
    )

    val neferBuffs = Seq(
      CombatBuff(Period(0.0, 2.0), Buff(flatHp = Some(4780), em = Some(56), cd = Some(21.0), cr = Some(3.9), flatAtk = Some(18)))
    )
}
