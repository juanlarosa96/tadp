/**
 * Created by gas on 29/10/16.
 */
package domain

abstract class Item

case object Semilla extends Item

case object Esfera extends Item

case object Municion extends Item

case class Arma(tipo: TipoArma) extends Item

case object FotoLuna extends Item

abstract class TipoArma

case object DeFuego extends TipoArma {
  def atacar(guerrero: Guerrero, enemigo: Guerrero): (Guerrero, Guerrero) = {
    enemigo.raza match {
      case Humano =>
        (guerrero.consumirItem(Municion), enemigo.cambiarEnergia(-20))
      case Namekusein if enemigo.estado == Inconsciente =>
        (guerrero.consumirItem(Municion), enemigo.cambiarEnergia(-10))
      case otro =>
        (guerrero.consumirItem(Municion), enemigo)
    }
  }
}

case object Filosa extends TipoArma {
  def atacar(guerrero: Guerrero, enemigo: Guerrero): (Guerrero, Guerrero) = {
    enemigo.raza match {
      case saiyajin: Saiyajin if saiyajin.cola =>
        saiyajin.transformacion match {
          case Mono =>
            (guerrero, enemigo.alterarEstado(Inconsciente).copy(raza = saiyajin.cortarCola, energia = 1))
          case _ =>
            (guerrero, enemigo.copy(raza = saiyajin.cortarCola, energia = 1))
        }
      case otro =>
            (guerrero, enemigo.cambiarEnergia(-(guerrero.energia / 100)))
    }
  }
}

case object Roma extends TipoArma {
  def puedeBajar(guerrero: Guerrero): Boolean = {
    guerrero.raza match {
      case Androide                       => false
      case otro if guerrero.energia > 300 => true
      case _                              => false
    }
  }
}



