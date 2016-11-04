package domain

import scala.util.Try

trait Movimiento { //TODO no deja cargarlos en la lista de movimientos, pero si se puede tratar polimorficamente viendolo como un movimiento, ej en ejecutar, preguntar
  //Por LEO: Fijate si ahora dejar Cargarlos, deberia dejarte
  //Debe ser Try porque podria fallar un guerrero al intentar ejecutar algo que no deberia
  def apply(guerrero :Guerrero, enemigo:Guerrero) : Try[Guerrero]
}


case object CargarKi extends Movimiento {
   def apply(guerrero :Guerrero, enemigo:Guerrero) : Try[Guerrero] = {
    guerrero match {
      case humano :Humano =>               Try( humano.copy(         energia = Ki(humano.energia.cant +100) ) )
      case superSaiyajin :SuperSaiyajin => Try( superSaiyajin.copy(  energia = Ki(superSaiyajin.energia.cant +150 * superSaiyajin.nivel) ) )
      case namekusein :Namekusein =>       Try( namekusein.copy(     energia = Ki(namekusein.energia.cant +100) ) )
      case monstruo :Monstruo =>           Try( monstruo.copy(       energia = Ki(monstruo.energia.cant +100) ) )
      case saiyajin :Saiyajin =>           Try( saiyajin.copy(       energia = Ki(saiyajin.energia.cant +100) ) )
      case _  => Try( guerrero )
    }
  }
}
//case object DejarseFajar extends Movimiento

case object Magia extends Movimiento {
  def apply(guerrero :Guerrero, enemigo:Guerrero) : Try[Guerrero] = {
    guerrero match {
      case namekusein :Namekusein  =>  Try( namekusein.copy(  estado = Normal) ) //TODO ver eso de cambiar el estado arbritariamente, preguntar!!
      case monstruo :Monstruo =>       Try( monstruo.copy(    estado = Normal) )
      //case _  => if (guerrero.inventario.filter(_.getClass == Esfera).size >= 7) guerrero.copy(estado = Normal) else guerrero
    }
  }
}