package domain


trait Movimiento { //TODO no deja cargarlos en la lista de movimientos, pero si se puede tratar polimorficamente viendolo como un movimiento, ej en ejecutar, preguntar
  //Por LEO: Fijate si ahora dejar Cargarlos, deberia dejarte
  //Debe ser Try porque podria fallar un guerrero al intentar ejecutar algo que no deberia
  def apply(guerrero :Guerrero, enemigo:Guerrero) : Guerrero
}


case object CargarKi extends Movimiento {
   def apply(guerrero: Guerrero, enemigo: Guerrero) : Guerrero = {
    guerrero match {
      case humano: Humano =>humano.copy(energia = Ki(humano.energia.cant +100))
      case namekusein: Namekusein => namekusein.copy(energia = Ki(namekusein.energia.cant +100))
      case monstruo: Monstruo => monstruo.copy(energia = Ki(monstruo.energia.cant +100))
      case saiyajin: Saiyajin =>
        saiyajin.estado match {
          //El Error dice que hay que diferenciar entre estados, no entre saiyayins y tiene razon, hay que implementar los estados de Saiyayin
          case SuperSaiyajin(nivel) => saiyajin.copy(energia = Ki(saiyajin.energia.cant + 150 * nivel))
          case _ => saiyajin.copy(energia = Ki(saiyajin.energia.cant +100))
      }
      case androide: Androide => guerrero
      case _ => guerrero.copy(energia = Ki(guerrero.energia.cant +100))
    }
  }
}

case object DejarseFajar extends Movimiento

case object Magia extends Movimiento {
  def apply(guerrero :Guerrero, enemigo:Guerrero) : Guerrero = {
    guerrero match {
      case namekusein :Namekusein  =>  namekusein.copy(estado = Consciente) //TODO ver eso de cambiar el estado arbritariamente, preguntar!!
      case monstruo :Monstruo =>       monstruo.copy(estado = Consciente)
      //case _  => if (guerrero.inventario.filter(_.getClass == Esfera).size >= 7) guerrero.copy(estado = Normal) else guerrero
    }
  }
}