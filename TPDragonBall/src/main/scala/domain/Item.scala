/**
  * Created by gas on 29/10/16.
  */
package domain

abstract class Item

case class Semilla(cantidad: Int) extends Item {
  def consumir = this.copy(cantidad - 1)
}

case class Esfera(cantidad: Int) extends Item {
  def consumir = this.copy(cantidad - 1)
}

case class Municion(cantidad: Int) extends Item {
  def consumir = this.copy(cantidad - 1)
}

case class Arma(tipo: TipoArma) extends Item

case object FotoLuna extends Item


abstract class TipoArma

case object DeFuego extends TipoArma
case object Filosa extends TipoArma
case object Roma extends TipoArma {
  def puedeBajar(guerrero: Guerrero): Boolean = {
    guerrero.raza match {
      case Androide => false
      case otro if guerrero.energia.cant > 300 => true
      case _ => false
    }
  }
}



