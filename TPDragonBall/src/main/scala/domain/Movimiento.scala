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
      case humano: Humano => ( humano.cambiarEnergia(Ki(humano.energia.cant +100)) , enemigo)
      case namekusein: Namekusein => ( namekusein.cambiarEnergia(Ki(namekusein.energia.cant +100)) , enemigo)
      case monstruo: Monstruo => ( monstruo.cambiarEnergia(Ki(monstruo.energia.cant +100)) , enemigo)
      case saiyajin: Saiyajin =>
        saiyajin.estado match {
          //El ERROR dice que hay que diferenciar entre estados, no entre saiyayins y tiene razon, hay que implementar los estados de Saiyayin. No se quien lo estaba haciendo y si avanzo con eso
          case SuperSaiyajin(nivel) => ( saiyajin.cambiarEnergia(Ki(saiyajin.energia.cant + 150 * nivel)) , enemigo)
          case _ => ( saiyajin.cambiarEnergia(Ki(saiyajin.energia.cant +100)) , enemigo)
      }
      case androide: Androide => ( guerrero , enemigo)
      case _ => ( guerrero.cambiarEnergia(Ki(guerrero.energia.cant +100)) , enemigo)
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
  
  val UsarItem: (Item,Guerrero,Guerrero)=> (Guerrero,Guerrero) = {
     (item: Item, guerrero: Guerrero, enemigo: Guerrero) =>
     item match {
       case arma: Arma =>
             arma.tipo match {
               case arma_fuego: DeFuego => 
                   //Veo si tiene municion para usar el arma
                     if (guerrero.tieneMunicion) enemigo match {
                         //Diferencion por quien es el enemigo
                         case humano: Humano => (guerrero.consumirMunicion , humano.cambiarEnergia( Ki(humano.energia.cant - 20) )  )
                         case namekusein :Namekusein  => namekusein.estado match {
                           case Inconsciente => (guerrero.consumirMunicion , namekusein.cambiarEnergia( Ki(namekusein.energia.cant - 10) )  )
                           // Y en caso de otro que hacemos? Por ahora explota
                         }
                         // Y en caso de otro que hacemos? Por ahora explota
                     }
                     //Sino, no hace nada
                     else (guerrero, enemigo)
                     }
       
       //TODO Pendiente las demas armas
     }
  }
    
}