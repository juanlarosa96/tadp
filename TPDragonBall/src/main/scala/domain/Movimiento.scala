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
            case SuperSaiyajin(nivel) => (guerrero.cambiarEnergia(150 * nivel), None)
            case _ => (guerrero.cambiarEnergia(100), None)
          }
        case Androide => (guerrero, None)
        case _ => (guerrero.cambiarEnergia(100), None)
      }
  }

  val CargarMenosKi: Movimiento = {
    (guerrero: Guerrero, None) =>
      guerrero.raza match {
        case Saiyajin(cola, transformacion) =>
          transformacion match {
            //TODO que error ?
            //El ERROR dice que hay que diferenciar entre estados, no entre saiyayins y tiene razon, hay que implementar los estados de Saiyayin. No se quien lo estaba haciendo y si avanzo con eso
            case SuperSaiyajin(nivel) => (guerrero.cambiarEnergia(25 * nivel), None)
            case _ => (guerrero.cambiarEnergia(25), None)
          }
        case Androide => (guerrero, None)
        case _ => (guerrero.cambiarEnergia(25), None)
      }
  }
  //LEO: No se si lo vamos a usar, pero lo actualice
  //val DejarseFajar: Movimiento = { (guerrero: Guerrero, enemigo: Guerrero) => guerrero }

  //TODO ver eso de cambiar el estado arbritariamente => en vez de pasasr algun estado a alterar estado, habria que tirar random
  val Magia: Movimiento = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
      guerrero.raza match {
        case Namekusein => (guerrero.alterarEstado(Consciente), enemigo)
        case monstruo: Monstruo => (guerrero.alterarEstado(Inconsciente), enemigo)
        case _ if guerrero.tiene7Esferas() => (guerrero.usarEsferas.alterarEstado(Consciente), enemigo)
      }
  }

  //GAS: falta chequear que el guerrero tenga el item en su inventario
  //GAS: delegar un poco en cada item (o aunque sea en arma)

  val UsarSemilla: Movimiento = UsarItem(Semilla)(_, _)

  def UsarItem(item: Item)(guerrero: Guerrero, enemigo: Guerrero): (Guerrero, Guerrero) = {
    if (guerrero.puedeUsarItem(item)) {
      item match {
        case arma: Arma =>
          arma.tipo match {
            case DeFuego =>
                enemigo.raza match {
                  case Humano =>
                    (guerrero.consumirMunicion, enemigo.cambiarEnergia(-20))
                  case Namekusein if enemigo.estado == Inconsciente =>
                    (guerrero.consumirMunicion, enemigo.cambiarEnergia(-10))
                  case otro =>
                    (guerrero.consumirMunicion, enemigo)
              }
            case Roma if Roma.puedeBajar(enemigo) =>
              (guerrero, enemigo.copy(estado = Inconsciente))
            case Filosa =>
              enemigo.raza match {
                case saiyajin: Saiyajin if saiyajin.cola =>
                  (guerrero, enemigo.alterarEstado(Inconsciente).copy(raza = saiyajin.cortarCola, energia = Ki(1)))
                      //enemigo.copy(energia = Ki(1), raza = Saiyajin(cola = false, transformacion = Normal)))
                case otro =>
                  (guerrero, enemigo.cambiarEnergia(-(guerrero.energia.cant / 100)))
              }
            case otro =>(guerrero,enemigo)
          }
        case Semilla =>
          (guerrero.copy(energia = Ki(1000)), enemigo) //TODO sacar semillas
        //TODO Pendiente las demas armas
      }
    } else {
      (guerrero, enemigo)
    }
  }

  val TransformarseEnMono: Movimiento = {
    (guerrero: Guerrero, enemigo : Guerrero) =>
      guerrero.raza match {
        case Saiyajin(cola, transf) if guerrero.inventario.contains(FotoLuna) && cola && transf != Mono =>
          (guerrero.copy(energiaMaxima = guerrero.energiaMaxima*3, energia = Ki(guerrero.energiaMaxima*3),raza = Saiyajin(cola = true, Mono)), enemigo)
        case _ =>
          (guerrero, enemigo)
      }
  }
  
  //val TransformarEnSuperSaiyajin

  def Fusion(compañero: Guerrero)(guerrero: Guerrero, enemigo: Guerrero): (Guerrero, Guerrero) = {
    val guerreroFusionado =
      Guerrero(energiaMaxima = guerrero.energiaMaxima + compañero.energiaMaxima,
        energia = Ki(guerrero.energia.cant + compañero.energia.cant),
        movimientos = guerrero.movimientos ::: compañero.movimientos,
        inventario = guerrero.inventario ::: compañero.inventario,
        estado = Consciente,
        roundsDejadoFajar = 0,
        raza = null)
    (guerreroFusionado, enemigo)
  }

  val GolpesNinja: (Guerrero, Guerrero) => (Guerrero, Guerrero) = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
      guerrero.raza match {
        case Humano if enemigo.raza == Androide => (guerrero.cambiarEnergia(-10), enemigo)
        case _ => if (enemigo.energia.cant > guerrero.energia.cant) (guerrero.cambiarEnergia(-20), enemigo) else (guerrero, enemigo.cambiarEnergia(-10))
      }
  }

  val Explotar: (Guerrero, Guerrero) => (Guerrero, Guerrero) = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
      guerrero.raza match {
        case Androide => (guerrero.cambiarEnergia(-guerrero.energiaMaxima), enemigo.cambiarEnergia(guerrero.energia.cant * (-3)))
        case Namekusein => (guerrero.cambiarEnergia(1 - guerrero.energia.cant), enemigo.cambiarEnergia(guerrero.energia.cant * (-2)))
        case _ => (guerrero, enemigo)
      }
  }

  def Onda(onda: Onda)(guerrero: Guerrero, enemigo: Guerrero): (Guerrero, Guerrero) = {
    onda match {
      case Genkidama if guerrero.roundsDejadoFajar > 1 => (guerrero, enemigo.recibirGolpeKi(math.pow(10, guerrero.roundsDejadoFajar).toInt))
      case ondaChica: OndaChica if guerrero.energia.cant >= ondaChica.cantidadKiRequerida => (guerrero, enemigo.recibirGolpeKi(ondaChica.cantidadKiRequerida))
      case _ => (guerrero, enemigo)
    }
  }
}

abstract class Onda
case object Genkidama extends Onda

abstract class OndaChica extends Onda { val cantidadKiRequerida: Int }
case object Kamehameha extends OndaChica { val cantidadKiRequerida = 100 }
case object Dodonpa extends OndaChica { val cantidadKiRequerida = 20}




