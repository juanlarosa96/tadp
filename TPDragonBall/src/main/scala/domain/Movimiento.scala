package domain

import scala.util.Random


object TiposMovimientos {
  type Movimiento = (Guerrero, Guerrero) => (Guerrero, Guerrero)

  val DejarseFajar: Movimiento = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
      guerrero.estado match {
        case DejandoseFajar(turnos) =>
          (guerrero.copy(estado = DejandoseFajar(turnos + 1)), enemigo)
        case _ =>
          (guerrero.copy(estado = DejandoseFajar(1)), enemigo)
      }
  }

  val CargarKi: Movimiento = {
    (guerrero: Guerrero, None) =>
      guerrero.raza match {
        case Saiyajin(cola, transformacion) =>
          transformacion match {
            case SuperSaiyajin(nivel) => (guerrero.cambiarEnergia(150 * nivel), None)
            case _ => (guerrero.cambiarEnergia(100), None)
          }
        case Androide => (guerrero, None)
        case _ => (guerrero.cambiarEnergia(100), None)
      }
  }

  //TODO ver eso de cambiar el estado arbritariamente => en vez de pasasr algun estado a alterar estado, habria que tirar random
  val Magia: Movimiento = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
        
      //Elige Arbitrariamente quien afecta y cual estado
      def randomAlterar:(Guerrero,Guerrero) = {
            var lista_temporal = List(guerrero, enemigo);
            var guerreroSeleccionado:Guerrero = lista_temporal( Random.nextInt(lista_temporal.size) )
            
            //Me quedo feo lo de abajo, solamente quiero obtener el otro guerrero quitando de la lista el que ya seleccionamos
            def elOtro:Guerrero = lista_temporal.filterNot { guerr => guerr == guerreroSeleccionado }.head;
            
            //Elige albitrariamente el estado
            guerreroSeleccionado = guerreroSeleccionado.alterarEstadoRandom
            
            //Devuelvo los guerreros en el orden correcto
            if(elOtro==guerrero)        ( elOtro              , guerreroSeleccionado)    //El guerrero seleccionado fue el 2do
            else                        ( guerreroSeleccionado, elOtro)                  //El guerrero seleccionado fue el 1ero
      }

    
      guerrero.raza match {
        case Namekusein => randomAlterar
        case monstruo: Monstruo => randomAlterar
        case _ if guerrero.tiene7Esferas => randomAlterar
      }
  }

  //GAS: delegar un poco en cada item (o aunque sea en arma)

  val UsarSemilla: Movimiento = usarItem(Semilla)
  val UsarEspada: Movimiento = usarItem(Arma(Filosa))
  val UsarArmaDeFuego: Movimiento = usarItem(Arma(DeFuego))

  def usarItem(item: Item)(guerrero: Guerrero, enemigo: Guerrero): (Guerrero, Guerrero) = {
    if (guerrero.puedeUsarItem(item)) {
      item match {
        case arma: Arma =>
          arma.tipo match {
            case DeFuego =>
               DeFuego.atacar(guerrero, enemigo)
            case Roma if Roma.puedeBajar(enemigo) =>
              (guerrero, enemigo.copy(estado = Inconsciente))
            case Filosa =>
              Filosa.atacar(guerrero, enemigo)             
            case otro =>(guerrero,enemigo)
          }
        case Semilla =>
          (guerrero.copy(energia = guerrero.energiaMaxima).consumirItem(Semilla), enemigo) //TODO sacar semillas
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
          (guerrero.copy(energiaMaxima = guerrero.energiaMaxima*3, energia = guerrero.energiaMaxima*3, raza = Saiyajin(cola = true, Mono)), enemigo)
        case _ =>
          (guerrero, enemigo)
      }
  }
  
  val TransformarEnSuperSaiyajin: Movimiento = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
      guerrero.raza match{
        case saiyajin: Saiyajin if guerrero.energia >= (guerrero.energiaMaxima/2)=> (guerrero.copy(energiaMaxima = guerrero.energiaMaxima*5, raza = saiyajin.transformarEnSuperSaiyajin),enemigo)
        case _ => (guerrero, enemigo)
      }
  }

  //val FusionCon(compañero: Guerrero): (Guerrero, Guerrero) = fusion(compañero)(_,_)

  def fusion(companiero: Guerrero)(guerrero: Guerrero)(enemigo: Guerrero): (Guerrero, Guerrero) = {
    if (guerrero.puedenFusionarseCon(companiero)) {
      val guerreroFusionado =
        Guerrero(energiaMaxima = guerrero.energiaMaxima + companiero.energiaMaxima,
          energia = guerrero.energia + companiero.energia,
          movimientos = (guerrero.movimientos ::: companiero.movimientos).distinct,
          inventario = guerrero.inventario ::: companiero.inventario,
          estado = Consciente,
          raza = Fusion(companiero, guerrero))
      (guerreroFusionado, enemigo)
    } else (guerrero, enemigo)
  }

  val GolpesNinja: (Guerrero, Guerrero) => (Guerrero, Guerrero) = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
      guerrero.raza match {
        case Humano if enemigo.raza == Androide => (guerrero.cambiarEnergia(-10), enemigo)
        case _ => if (enemigo.energia > guerrero.energia) (guerrero.cambiarEnergia(-20), enemigo) else (guerrero, enemigo.cambiarEnergia(-10))
      }
  }

  val Explotar: (Guerrero, Guerrero) => (Guerrero, Guerrero) = {
    (guerrero: Guerrero, enemigo: Guerrero) =>
      guerrero.raza match {
        case Androide => (guerrero.cambiarEnergia(-guerrero.energiaMaxima), enemigo.cambiarEnergia(guerrero.energia * (-3)))
        case Namekusein => (guerrero.cambiarEnergia(1 - guerrero.energia), enemigo.cambiarEnergia(guerrero.energia * (-2)))
        case _ => (guerrero, enemigo)
      }
  }

  def onda(onda: Onda)(guerrero: Guerrero, enemigo: Guerrero): (Guerrero, Guerrero) = {
    onda match {
      case Genkidama =>
        guerrero.estado match {
          case DejandoseFajar(cant) => (guerrero.alterarEstado(Consciente), enemigo.recibirGolpeKi(math.pow(10, cant).toInt))
          case _ => (guerrero.alterarEstado(Consciente), enemigo.recibirGolpeKi(1)) //TODO xque la genkidama se eleva a la cant dejado fajar.. algo a la 0 = 1
        }
      case ondaChica: OndaChica if guerrero.energia >= ondaChica.cantidadKiRequerida => (guerrero, enemigo.recibirGolpeKi(ondaChica.cantidadKiRequerida))
      case _ => (guerrero, enemigo)
    }
  }

  abstract class Onda
  case object Genkidama extends Onda

  abstract class OndaChica extends Onda { val cantidadKiRequerida: Int }
  case object Kamehameha extends OndaChica { val cantidadKiRequerida = 100 }
  case object Dodonpa extends OndaChica { val cantidadKiRequerida = 20}
  case object FinalFlash extends OndaChica { val cantidadKiRequerida = 150 }

  val UsarGenkidama: Movimiento = onda(Genkidama)
  val UsarKamehameha: Movimiento = onda(Kamehameha)
  val UsarFinalFlash: Movimiento = onda(FinalFlash)
}



