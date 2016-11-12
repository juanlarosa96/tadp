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


object TiposMovimientos {
  //LEO: Debo devolver ambos guerreros porque hay algunos movimiento que modifican a los 2
  type Movimiento = (Guerrero, Guerrero) => (Guerrero, Guerrero)
                   //guerrero, enemigo(Gas: Lo puse como option para probar, antes era Guerrero)
  
  val CargarKi: Movimiento = {
   (guerrero: Guerrero, None) =>
    guerrero match {
      case saiyajin: Saiyajin =>
        saiyajin.transformacion match {
            //TODO que error ?
          //El ERROR dice que hay que diferenciar entre estados, no entre saiyayins y tiene razon, hay que implementar los estados de Saiyayin. No se quien lo estaba haciendo y si avanzo con eso
          case SuperSaiyajin(nivel) => (saiyajin.cambiarEnergia(150 * nivel) , None)
          case _ => (saiyajin.cambiarEnergia(100) , None)
      }
      case androide: Androide => (androide, None)
      case _ => (guerrero.cambiarEnergia(100) , None)
    }
  }
  
  //LEO: No se si lo vamos a usar, pero lo actualice
  //val DejarseFajar: Movimiento = { (guerrero: Guerrero, enemigo: Guerrero) => guerrero }

  //TODO ver eso de cambiar el estado arbritariamente => en vez de pasasr algun estado a alterar estado, habria que tirar random
  val Magia: Movimiento = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
    guerrero match {
      case namekusein :Namekusein  => (namekusein.alterarEstado(Consciente), enemigo)
      case monstruo :Monstruo => (monstruo.alterarEstado(Inconsciente), enemigo)
      case otro if otro.tiene7Esferas => (otro.alterarEstado(Consciente).usarEsferas, enemigo)
    }
  }

  //GAS: falta chequear que el guerrero tenga el item en su inventario
  //GAS: delegar un poco en cada item (o aunque sea en arma)
  val UsarItem: (Item, Guerrero, Guerrero) => (Guerrero, Guerrero) = {
     (item: Item, guerrero: Guerrero, enemigo: Guerrero) =>
     item match {
       case arma: Arma =>
         arma.tipo match {
           case DeFuego =>
             if (guerrero.tieneMunicion) {
               enemigo match {
                 case humano: Humano => (guerrero.consumirMunicion , humano.cambiarEnergia(-20))
                 case namekusein: Namekusein if namekusein.estado == Inconsciente =>
                   (guerrero.consumirMunicion , namekusein.cambiarEnergia(-10))
                 case otro => (guerrero.consumirMunicion, enemigo)
               }
             }
             else (guerrero, enemigo)
           case Roma if Roma.puedeBajar(enemigo) => (guerrero, enemigo.copy(estado = Inconsciente))
           case Filosa => enemigo match {
             case saiyajin: Saiyajin if saiyajin.cola => (guerrero, saiyajin.cortarCola())
             case otro => (guerrero, enemigo.cambiarEnergia(guerrero.energia.cant / 100))
           }
         }
       case semilla: Semilla => (guerrero, enemigo) //TODO sacar semillas
       //TODO Pendiente las demas armas
     }
  }
    
}