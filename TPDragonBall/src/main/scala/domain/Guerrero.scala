package domain

import domain.TiposMovimientos._
import enums.TipoMonstruo
import enums.TipoMonstruo._

import scala.util.Try

case class Guerrero(energiaMaxima: Int,
                    energia: FuenteDeEnergia,
                    movimientos: List[Movimiento],
                    inventario: List[Item],
                    estado: Estado,
                    roundsDejadoFajar: Int,
                    raza: Raza) {

  def ejecutar(mov: Movimiento, enemigo: Guerrero): (Guerrero, Guerrero) = { //GAS: Lo cambio a guerrero, guerrero
    estado match {
      case Consciente                         => mov(this, enemigo)
      case Inconsciente if mov == UsarSemilla => mov(this, enemigo)
      case _                                  => (this, enemigo) //MUERTO NO PUEDE USAR SEMILLA
    }
  }

  def dejarseFajar: Guerrero = this

  def alterarEstado(estadoNuevo: Estado): Guerrero = {
    estadoNuevo match {
      case Consciente => this.copy(estado = estadoNuevo)
      case _ =>
        raza match {
          case Saiyajin(cola, _) => this.copy(raza = Saiyajin(cola, Normal), roundsDejadoFajar = 0) //cambio algo aca. QUE ES ESTO?
          case _                 => this.copy(roundsDejadoFajar = 0)
        }
    }

    this.copy(estado = estadoNuevo)
  }

  def tieneItem(esTipoItem: Item => Boolean): Boolean = {
    inventario.filter { item => esTipoItem(item) }.size > 0
  }
  //Creo abstracciones para leer mas sencillo

  def queSeaMunicion = { i: Item => i.getClass == Municion }

  def tieneMunicion = this.tieneItem { queSeaMunicion };
  
  //TODO Podria rescribirse usando tieneItem
  def tiene7Esferas(): Boolean = {
    inventario.exists {
      case esfera: Esfera => esfera.cantidad == 7
    }
  }

  def puedeUsarItem(item: Item): Boolean = {
    item match {
      case arma: Arma if arma.tipo == DeFuego => this.inventario.contains(item) && this.tieneMunicion
      case _                                  => this.inventario.contains(item)
    }
  }

  def consumirItem(esTipoItem: Item => Boolean): Guerrero = {
    val itemPorConsumir = inventario.find { item => esTipoItem(item) }
    val nuevoInventario: List[Item] = inventario.filterNot { i => i == itemPorConsumir }

    this.copy(inventario = nuevoInventario)
  }
  def consumirMunicion = this.consumirItem { queSeaMunicion }

  //Usa Todas las esferas que tiene
  def usarEsferas: Guerrero = {
    this.copy(inventario = inventario.filter(_.getClass != Esfera))
  }

  //Creo metodo porque se esta repitiendo todo el tiempo lo mismo
  def cambiarEnergia(valor: Int): Guerrero = {
    if (sePasaDeEnergia(valor))
      this.copy(energia = Ki(this.energiaMaxima))
    else if (energiaMenorOIgualACero(valor))
      this.copy(energia = Ki(0), estado = Muerto)
    else
      this.copy(energia = Ki(energia.cant + valor))
  }

  def sePasaDeEnergia(valor: Int): Boolean = energia.cant + valor > energiaMaxima

  def energiaMenorOIgualACero(valor: Int): Boolean = energia.cant + valor <= 0

  def movimientoMasEfectivoContra(enemigo: Guerrero, unCriterio: Criterio): Movimiento = {
    val resultados: List[(Movimiento, Int)] = for {

      //Por cada movimiento del guerrero
      mov <- this.movimientos

      //ejecuto movimiento
      guerreroPostMov = this.ejecutar(mov, enemigo)

      //valoro movimiento segun criterio
      valor = unCriterio(guerreroPostMov._1)

    } yield (mov, valor)

    //Ordeno por Mayor puntaje segun criterio y obtengo el primero
    resultados.sortBy(_._2).map(_._1).reverse.head
  }

  def pelearRound(mov: Movimiento)(enemigo: Guerrero): (Guerrero, Guerrero, Option[Guerrero]) = {
    val (guerreroResultado, enemigoResultado) = ejecutar(mov, enemigo)
    val (enemigoFinal, guerreroFinal) = enemigoResultado.ejecutar(enemigoResultado.movimientoMasEfectivoContra(guerreroResultado, DejarMasKi), guerreroResultado)
    if (enemigoFinal.estado == Muerto) {
      (guerreroFinal, enemigoFinal, Some(guerreroFinal))
    } else if (guerreroFinal.estado == Muerto) {
      (guerreroFinal, enemigoFinal, Some(enemigoFinal))
    } else {
      (guerreroFinal, enemigoFinal, None)
    }
  }

  def recibirGolpeKi(cantidad: Int): Guerrero = {
    raza match {
      case monstruo: Monstruo => this.cambiarEnergia((-2) * cantidad) //GAS: Le agrego para que matchee
      case Androide           => this.cambiarEnergia(cantidad)
      case _                  => this.cambiarEnergia(-cantidad)
    }
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

case class Monstruo(tipoMonstruo: TipoMonstruo) extends Raza {
  /*
  def comerseAlOponente(guerreroAComer: Guerrero) = { //TODO esta aca pq solo este movimiento lo hacen los mounstruos, por ahora no tiene sentido modelarlo afuera
    tipoMonstruo match {
      case TipoMonstruo.CELL =>
        guerreroAComer match {
          case morfi: Androide => this.copy(movimientos = this.movimientos ::: morfi.movimientos)
          case _ => this
        }
      case TipoMonstruo.MAJIN_BUU => this.copy(movimientos = List(this.movimientos.reverse.head)) //TODO cambiar
    }
  }
*/
}

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

// ------------------------- FUENTES DE ENERGIA --------------------------

abstract class FuenteDeEnergia {
  def cant: Int
}

case class Ki(cant: Int) extends FuenteDeEnergia
case class Bateria(cant: Int) extends FuenteDeEnergia


