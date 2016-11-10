package domain

import scala.util.Try

trait Movimiento { //TODO no deja cargarlos en la lista de movimientos, pero si se puede tratar polimorficamente viendolo como un movimiento, ej en ejecutar, preguntar
  //Por LEO: Fijate si ahora dejar Cargarlos, deberia dejarte
  //Debe ser Try porque podria fallar un guerrero al intentar ejecutar algo que no deberia
  def apply(guerrero :Guerrero, enemigo:Guerrero) : Try[Guerrero]
}


case object CargarKi extends Movimiento {
   def apply(guerrero: Guerrero, enemigo: Guerrero) : Try[Guerrero] = {
    guerrero match {
      case humano: Humano => Try(humano.copy(energia = Ki(humano.energia.cant +100)))
      case namekusein: Namekusein => Try(namekusein.copy(energia = Ki(namekusein.energia.cant +100)))
      case monstruo: Monstruo => Try(monstruo.copy(energia = Ki(monstruo.energia.cant +100)))
      case saiyajin: Saiyajin =>
        saiyajin.estado match {
          case SuperSaiyajin(nivel) => Try(saiyajin.copy(energia = Ki(saiyajin.energia.cant + 150 * nivel)))
          case _ => Try(saiyajin.copy(energia = Ki(saiyajin.energia.cant +100)))
      }
      case androide: Androide => Try(guerrero)
      case _ => Try(guerrero.copy(energia = Ki(guerrero.energia.cant +100)))
    }
  }
}
//case object DejarseFajar extends Movimiento

case object Magia extends Movimiento {
  def apply(guerrero :Guerrero, enemigo:Guerrero) : Try[Guerrero] = {
    guerrero match {
      case namekusein :Namekusein  =>  Try(namekusein.copy(estado = Consciente)) //TODO ver eso de cambiar el estado arbritariamente, preguntar!!
      case monstruo :Monstruo =>       Try(monstruo.copy(estado = Consciente))
      //case _  => if (guerrero.inventario.filter(_.getClass == Esfera).size >= 7) guerrero.copy(estado = Normal) else guerrero
    }
  }
}