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
    guerrero.raza match {
      case Saiyajin(cola, transformacion) =>
        transformacion match {
            //TODO que error ?
          //El ERROR dice que hay que diferenciar entre estados, no entre saiyayins y tiene razon, hay que implementar los estados de Saiyayin. No se quien lo estaba haciendo y si avanzo con eso
          case SuperSaiyajin(nivel) => (guerrero.cambiarEnergia(150 * nivel) , None)
          case _ => (guerrero.cambiarEnergia(100) , None)
      }
      case Androide => (guerrero, None)
      case _ => (guerrero.cambiarEnergia(100) , None)
    }
  }
  
  //LEO: No se si lo vamos a usar, pero lo actualice
  //val DejarseFajar: Movimiento = { (guerrero: Guerrero, enemigo: Guerrero) => guerrero }

  //TODO ver eso de cambiar el estado arbritariamente => en vez de pasasr algun estado a alterar estado, habria que tirar random
  val Magia: Movimiento = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
    guerrero.raza match {
      case Namekusein  => (guerrero.alterarEstado(Consciente), enemigo)
      case monstruo : Monstruo => (guerrero.alterarEstado(Inconsciente), enemigo)
      case _ if guerrero.tiene7Esferas => (guerrero.alterarEstado(Consciente).usarEsferas, enemigo)
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
               enemigo.raza match {
                 case Humano =>
                   (guerrero.consumirMunicion , enemigo.cambiarEnergia(-20))
                 case Namekusein if enemigo.estado == Inconsciente =>
                   (guerrero.consumirMunicion , enemigo.cambiarEnergia(-10))
                 case otro =>
                   (guerrero.consumirMunicion, enemigo)
               }
             }
             else (guerrero, enemigo)
           case Roma if Roma.puedeBajar(enemigo) =>
             (guerrero, enemigo.copy(estado = Inconsciente))
           case Filosa =>
             enemigo.raza match {
               case saiyajin: Saiyajin if saiyajin.cola =>
                 (guerrero, enemigo.copy(energia = Ki(1), raza = Saiyajin(cola = false, transformacion = Normal)))
               case otro =>
                 (guerrero, enemigo.cambiarEnergia(guerrero.energia.cant / 100))
             }
         }
       case semilla: Semilla =>
         (guerrero, enemigo) //TODO sacar semillas
       //TODO Pendiente las demas armas
     }
  }
  
  val TransformarseEnMono: (Guerrero, Guerrero) => (Guerrero, Guerrero) = {
    (guerrero: Guerrero, None) =>
      guerrero.raza match{
          case Saiyajin(cola, transf) if guerrero.inventario.contains(FotoLuna) && cola && transf != Mono  =>
            (guerrero.copy(raza = Saiyajin(cola = true, Mono)), None)
          case _ =>
            (guerrero, None)
      }    
  }

    
}