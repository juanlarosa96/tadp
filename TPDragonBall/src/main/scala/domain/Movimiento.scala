package domain

/*
 * LEO: Lo dejo solo por los comentarios
 * 
 * 
trait Movimiento { //TODO no deja cargarlos en la lista de movimientos, pero si se puede tratar polimorficamente viendolo como un movimiento, ej en ejecutar, preguntar
  //Por LEO: Fijate si ahora dejar Cargarlos, deberia dejarte
  //Debe ser Try porque podria fallar un guerrero al intentar ejecutar algo que no deberia
}
*/


object Tipos_Movimientos {
  //LEO: Debo devolver ambos guerreros porque hay algunos movimiento que modifican a los 2
  type Movimiento = (/*MiGuerrero*/ Guerrero, /*GuerreroEnemigo*/Guerrero) => (/*MiGuerrero*/ Guerrero, /*GuerreroEnemigo*/Guerrero)
  
  
  val CargarKi: Movimiento = {
   (guerrero: Guerrero, enemigo: Guerrero) =>
    guerrero match {
      case humano: Humano => ( humano.copy(energia = Ki(humano.energia.cant +100)) , enemigo)
      case namekusein: Namekusein => ( namekusein.copy(energia = Ki(namekusein.energia.cant +100)) , enemigo)
      case monstruo: Monstruo => ( monstruo.copy(energia = Ki(monstruo.energia.cant +100)) , enemigo)
      case saiyajin: Saiyajin =>
        saiyajin.estado match {
          //El ERROR dice que hay que diferenciar entre estados, no entre saiyayins y tiene razon, hay que implementar los estados de Saiyayin. No se quien lo estaba haciendo y si avanzo con eso
          case SuperSaiyajin(nivel) => ( saiyajin.copy(energia = Ki(saiyajin.energia.cant + 150 * nivel)) , enemigo)
          case _ => ( saiyajin.copy(energia = Ki(saiyajin.energia.cant +100)) , enemigo)
      }
      case androide: Androide => ( guerrero , enemigo)
      case _ => ( guerrero.copy(energia = Ki(guerrero.energia.cant +100)) , enemigo)
    }
  }
  
  //LEO: No se si lo vamos a usar, pero lo actualice
  //val DejarseFajar: Movimiento = { (guerrero: Guerrero, enemigo: Guerrero) => guerrero }
  
  val Magia: Movimiento = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
    guerrero match {
      case namekusein :Namekusein  =>  ( namekusein.copy(estado = Consciente) , enemigo) //TODO ver eso de cambiar el estado arbritariamente, preguntar!!
      case monstruo :Monstruo =>       ( monstruo.copy(estado = Consciente) , enemigo)
      //case _  => if (guerrero.inventario.filter(_.getClass == Esfera).size >= 7) guerrero.copy(estado = Normal) else guerrero
    }
  }
    
}