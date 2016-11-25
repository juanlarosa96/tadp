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
                    raza: Raza) {

  def ejecutar(mov: Movimiento, enemigo: Guerrero): (Guerrero, Guerrero) = { //GAS: Lo cambio a guerrero, guerrero
    if (puedeEjecutarMovimiento(mov)) {
      if (mov == DejarseFajar || mov == UsarGenkidama)
        mov(this, enemigo)
      else
        estado match {
          case Consciente                         => mov(this, enemigo)
          case Inconsciente if mov == UsarSemilla => mov(this, enemigo)
          case DejandoseFajar(cant)               => mov(this.copy(estado = Consciente), enemigo)
          case _                                  => (this, enemigo) //MUERTO NO PUEDE USAR SEMILLA
        }
    } else (this, enemigo)
  } //TODO ---------> VER CAMBIO

  def puedeEjecutarMovimiento(mov: Movimiento): Boolean = {
    if (movimientos.contains(mov))
      mov match {
        case UsarSemilla => puedeUsarItem(Semilla)
        case UsarArmaDeFuego => puedeUsarItem(Arma(DeFuego))
        case _ => estado match {
          case Consciente => true
          case DejandoseFajar(_) => true
          case _ => false
        }
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
          case Saiyajin(cola, _) => copy(raza = Saiyajin(cola, Normal)) //cambio algo aca. QUE ES ESTO?
          case _                 => this //????
        }
    }

    this.copy(estado = estadoNuevo)
  } //TODO ---------> REVISAR

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

  def usarEsferas: Guerrero = copy(inventario = inventario.filter(_ != Esfera))

  def cambiarEnergia(valor: Int): Guerrero = {
    if (sePasaDeEnergia(valor))
      copy(energia = energiaMaxima)
    else if (energiaMenorOIgualACero(valor))
      copy(energia = 0, estado = Muerto)
    else
      copy(energia = energia + valor)
  }

  def sePasaDeEnergia(valor: Int): Boolean = energia + valor > energiaMaxima

  def murio = (unGuerrero: Guerrero) => unGuerrero.estado == Muerto

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
    Try(resultados.sortBy(_._2).map(_._1).reverse.head).toOption
    //if (resultados.isEmpty) None else Some(resultados.sortBy(_._2).map(_._1).reverse.head)
  }

  def resultadoAtaque(guerrero: Guerrero, enemigo: Guerrero): ResultadoAtaque = {
    (murio(guerrero), murio(enemigo)) match {
      case (_, true) => ResultadoAtaque(guerrero, enemigo, Some(guerrero))
      case (true, false) => ResultadoAtaque(guerrero, enemigo, Some(enemigo))
      case (false, false) => ResultadoAtaque(guerrero, enemigo, None)
    }
  }

/*
  def pelearRound(mov: Movimiento)(enemigo: Guerrero): ResultadoAtaque = {
    val (guerreroResultado, enemigoResultado) = ejecutar(mov, enemigo)

    resultadoAtaque(guerreroResultado, enemigoResultado) match {
      case ResultadoAtaque(_, _, Some(alguien)) => ResultadoAtaque(guerreroResultado, enemigoResultado, Some(alguien))
      case _ => val (enemigoFinal, guerreroFinal) = //el enemigo contraataca si no muere
        enemigoResultado.ejecutar(enemigoResultado
          .movimientoMasEfectivoContra(guerreroResultado, DejarMasKi), guerreroResultado)

        resultadoAtaque(guerreroFinal, enemigoFinal)
    }
  }
*/

  def pelearRound(mov: Movimiento)(enemigo: Guerrero): ResultadoAtaque = {
    val (guerreroResultado, enemigoResultado) = ejecutar(mov, enemigo)

    resultadoAtaque(guerreroResultado, enemigoResultado) match {
      case ResultadoAtaque(_, _, Some(alguien)) => ResultadoAtaque(guerreroResultado, enemigoResultado, Some(alguien))
      case _ => val (enemigoFinal, guerreroFinal) = //el enemigo contraataca si no muere
        if(enemigoResultado.movimientoMasEfectivoContra(guerreroResultado, DejarMasKi).isEmpty){
           (enemigoResultado, guerreroResultado) //TODO LOS DEVUELVE COMO ESTABAN ------> (guerreroResultado, enemigoResultado) asi estaba y esta mal, es al reves
        } else{
      enemigoResultado.ejecutar(enemigoResultado.movimientoMasEfectivoContra(guerreroResultado, DejarMasKi).get, guerreroResultado)
        }
        resultadoAtaque(guerreroFinal, enemigoFinal)
    }
  } //TODO VER ESTO

  def planDeAtaqueContra(enemigo: Guerrero, cantidadDeRounds: Int)(unCriterio: Criterio): Option[PlanDeAtaque] = {
    var movimientosElegidos: List[Movimiento] = Nil
    val guerreros: ResultadoAtaque = ResultadoAtaque(this, enemigo, None)

    //Defino funciones para abstraer nombre a lo de arriba
    def miGuerrero  =   guerreros.peleador
    def elEnemigo   =   guerreros.enemigo

    val resultadoAtaque: ResultadoAtaque = (1 to cantidadDeRounds).toList.foldLeft(guerreros){
      (e1,_) =>

      val movim = miGuerrero.movimientoMasEfectivoContra(e1.enemigo, unCriterio)
      val result = movim.fold(e1) {  mov =>
        movimientosElegidos = movimientosElegidos union List(mov)
        e1.peleador.pelearRound(mov)(e1.enemigo)
      }

        result
    }

    Try {
      require(cantidadDeRounds == movimientosElegidos.size || resultadoAtaque.ganador.isDefined, "No se pudo completar el plan de ataque")
      val plan = PlanDeAtaque(movimientosElegidos, cantidadDeRounds, unCriterio)
      plan
    }.toOption

  }


  def pelearContra(enemigo:Guerrero)(plan:PlanDeAtaque): ResultadoAtaque = {

    var guerreros: ResultadoAtaque = ResultadoAtaque(this, enemigo, None) //TODO var o val ?

    def miGuerrero = guerreros.peleador
    def elEnemigo = guerreros.enemigo

    def func (guer: ResultadoAtaque, mov: Movimiento): ResultadoAtaque = {
    guer.ganador.fold(guer: ResultadoAtaque) {_ =>
      miGuerrero.pelearRound(mov)(elEnemigo)}
    }
    plan.movimientos.foldLeft(guerreros){func}

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
case class DejandoseFajar(turnos: Int) extends Estado {
  def resetear(g: Guerrero): Guerrero = g.copy(estado = Consciente)
}
// ------------------------- ESTADOS SAIYAN --------------------------

trait Transformacion

case class SuperSaiyajin(nivel: Int) extends Transformacion //Antes extendia de Guerrero
case object Mono extends Transformacion
case object Normal extends Transformacion
