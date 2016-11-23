package domain

import domain.TiposMovimientos._
import enums.TipoMonstruo._

import scala.util.Try
import scala.util.control.Breaks._


case class Guerrero(energiaMaxima: Int,
                    energia: Int,
                    movimientos: List[Movimiento],
                    inventario: List[Item],
                    estado: Estado,
                    roundsDejadoFajar: Int,
                    raza: Raza) {

  def ejecutar(mov: Movimiento, enemigo: Guerrero): (Guerrero, Guerrero) = { //GAS: Lo cambio a guerrero, guerrero
    if (puedeEjecutarMovimiento(mov)) {
      var guerrero = this
      if (mov != DejarseFajar) {
        guerrero = this.copy(roundsDejadoFajar = 0)
      }
      estado match {
        case Consciente                         => mov(guerrero, enemigo)
        case Inconsciente if mov == UsarSemilla => mov(guerrero, enemigo)
        case _                                  => (this, enemigo) //MUERTO NO PUEDE USAR SEMILLA
      }
    } else (this, enemigo)
  }

  def puedeEjecutarMovimiento(mov: Movimiento): Boolean = {
    if (movimientos.contains(mov))
      mov match {
        case UsarSemilla => puedeUsarItem(Semilla)
        case UsarArmaDeFuego => puedeUsarItem(Arma(DeFuego))
        case _ => estado == Consciente
      }
    else false
  }

  def aprenderMovimiento(mov: Movimiento): Guerrero = {
    if (!conoceMovimiento(mov))
      copy(movimientos = mov :: movimientos)
    else
      this
  }

  def conoceMovimiento(mov: Movimiento): Boolean = movimientos.contains(mov)

  def alterarEstado(estadoNuevo: Estado): Guerrero = {
    estadoNuevo match {
      case Consciente => this.copy(estado = estadoNuevo)
      case _ =>
        raza match {
          case Saiyajin(cola, _) => copy(raza = Saiyajin(cola, Normal), roundsDejadoFajar = 0) //cambio algo aca. QUE ES ESTO?
          case _                 => copy(roundsDejadoFajar = 0)
        }
    }

    this.copy(estado = estadoNuevo)
  }

  def tieneItem(item: Item): Boolean = inventario.contains(item)

  def tiene7Esferas: Boolean = inventario.count(_ == Esfera) == 7

  def puedeUsarItem(item: Item): Boolean = {
    item match {
      case arma: Arma if arma.tipo == DeFuego => inventario.contains(item) && tieneItem(Municion)
      case _                                  => inventario.contains(item)
    }
  }

  def recibirGolpeKi(cantidad: Int): Guerrero = {
    raza match {
      case monstruo: Monstruo => cambiarEnergia((-2) * cantidad)
      case Androide           => cambiarEnergia(cantidad)
      case _                  => cambiarEnergia(-cantidad)
    }
  }

  def consumirItem(item: Item): Guerrero = {
    val index = inventario.indexOf(item)
    if (index < 0) {
      this
    } else if (index == 0) {
      copy(inventario = inventario.tail)
    } else {
      val (ppioLista, finLista) = inventario.splitAt(index)
      copy(inventario = ppioLista ++ finLista.tail)
    }
  }

  //Usa Todas las esferas que tiene
  def usarEsferas: Guerrero = copy(inventario = inventario.filter(_ != Esfera))

  //Creo metodo porque se esta repitiendo todo el tiempo lo mismo
  def cambiarEnergia(valor: Int): Guerrero = {
    if (sePasaDeEnergia(valor))
      copy(energia = energiaMaxima)
    else if (energiaMenorOIgualACero(valor))
      copy(energia = 0, estado = Muerto)
    else
      copy(energia = energia + valor)
  }

  def sePasaDeEnergia(valor: Int): Boolean = energia + valor > energiaMaxima

  def energiaMenorOIgualACero(valor: Int): Boolean = energia + valor <= 0

  def movimientoMasEfectivoContra(enemigo: Guerrero, unCriterio: Criterio): Option[Movimiento] = {
    val resultados: List[(Movimiento, Int)] = for {

      //Por cada movimiento del guerrero
      mov <- this.movimientos.filter(puedeEjecutarMovimiento)

      //ejecuto movimiento
      guerreroPostMov = this.ejecutar(mov, enemigo)

      //valoro movimiento segun criterio
      valor = unCriterio(guerreroPostMov._1, guerreroPostMov._2)

    } yield (mov, valor)

    //Ordeno por Mayor puntaje segun criterio y obtengo el primero
    if (resultados.isEmpty) None else Some(resultados.sortBy(_._2).map(_._1).reverse.head)
  }
  def murio = (unGuerrero: Guerrero) => unGuerrero.estado == Muerto

  def resultadoAtaque(guerrero: Guerrero, enemigo: Guerrero): ResultadoPelea = {
    (murio(guerrero), murio(enemigo)) match {
      case (_, true) => ResultadoPelea(guerrero, enemigo, Some(guerrero))
      case (true, false) => ResultadoPelea(guerrero, enemigo, Some(enemigo))
      case (false, false) => ResultadoPelea(guerrero, enemigo, None)
    }
  }

  def pelearRound(mov: Movimiento)(enemigo: Guerrero): ResultadoPelea = {
    val (guerreroResultado, enemigoResultado) = ejecutar(mov, enemigo)

    resultadoAtaque(guerreroResultado, enemigoResultado) match {
      case ResultadoPelea(_, _, Some(alguien)) => ResultadoPelea(guerreroResultado, enemigoResultado, Some(alguien))
      case _ => val (enemigoFinal, guerreroFinal) = //el enemigo contraataca si no muere
        if(enemigoResultado.movimientoMasEfectivoContra(guerreroResultado, DejarMasKi).isEmpty){
           (guerreroResultado,enemigoResultado)
        } else{            
      enemigoResultado.ejecutar(enemigoResultado.movimientoMasEfectivoContra(guerreroResultado, DejarMasKi).get, guerreroResultado)
        }
        resultadoAtaque(guerreroFinal, enemigoFinal)
    }
  }

  def planDeAtaqueContra(enemigo: Guerrero, cantidadDeRounds: Int)(unCriterio: Criterio): Try[PlanDeAtaque] = {
    var movimientosElegidos: List[Movimiento] = Nil
    var guerreros: ResultadoPelea = ResultadoPelea(this, enemigo, None)

    //Defino funciones para abstraer nombre a lo de arriba
    def miGuerrero  =   guerreros.peleador
    def elEnemigo   =   guerreros.enemigo

    breakable {
      for (roundActual <- 1 to cantidadDeRounds) {
        //Elijo Movimiento mas efectivo
        val mov = miGuerrero.movimientoMasEfectivoContra(elEnemigo, unCriterio)
        if (mov.isDefined){
        //Simulo la Pelea y guardo los resultados
        guerreros = miGuerrero.pelearRound(mov.get)(elEnemigo)

        //Podria hacerse alguna validacion antes de agregar el movimiento, si fuera necesario..
        movimientosElegidos = movimientosElegidos union List(mov.get)
        
        if (guerreros.hayGanador)
          break
        }
      }
    }

    Try {
      require(cantidadDeRounds == movimientosElegidos.size, "No se pudo completar el plan de ataque")
      val plan = PlanDeAtaque(movimientosElegidos, cantidadDeRounds, unCriterio)
      plan
    }

  }

  def pelearContra(enemigo:Guerrero)(plan:PlanDeAtaque):ResultadoPelea = {

    var guerreros: ResultadoPelea = ResultadoPelea(this, enemigo, None)

    def miGuerrero = guerreros.peleador
    def elEnemigo = guerreros.enemigo

    //Defino funciones auxiliares con Nombre Representativo

    //Para que pueda usar Break en el For
    breakable {
          for( roundActual <- 1 to plan.cantidadRunds ){
            //Elijo Movimiento mas efectivo
             val mov = miGuerrero.movimientoMasEfectivoContra(elEnemigo, plan.criterio)
             if(mov.isDefined){
             //Simulo la Pelea y guardo los resultados
             guerreros = miGuerrero.pelearRound(mov.get)(elEnemigo)

             //Corto si Alguno Murio, el otro Gano
             if (guerreros.hayGanador)
            break
             }
          }
    }
    guerreros
  }

}

// ------------------------------ TIPOS DE GUERRERO ------------------------------

abstract class Raza

case object Humano extends Raza
case object Androide extends Raza
case object Namekusein extends Raza

case class Saiyajin(cola: Boolean,
                    transformacion: Transformacion) extends Raza {

  def cortarCola = {
    transformacion match {
      case Mono => this.copy(transformacion = Normal, cola = false)
      case _    => this.copy(cola = false)
    }
  }

  def transformarEnMono = this.copy(transformacion = Mono)

  def transformarEnSuperSaiyajin = transformacion match {
    case saiyajin: SuperSaiyajin => this.copy(transformacion = SuperSaiyajin(saiyajin.nivel+1))
    case Normal => this.copy(transformacion = SuperSaiyajin(1))
    case Mono => this
  }

}

case class Monstruo(tipoMonstruo: TipoMonstruo) extends Raza

// ------------------------- ESTADOS DE VIDA --------------------------

trait Estado

case object Consciente extends Estado
case object Muerto extends Estado
case object Inconsciente extends Estado

// ------------------------- ESTADOS SAIYAN --------------------------

trait Transformacion

case class SuperSaiyajin(nivel: Int) extends Transformacion //Antes extendia de Guerrero
case object Mono extends Transformacion
case object Normal extends Transformacion
