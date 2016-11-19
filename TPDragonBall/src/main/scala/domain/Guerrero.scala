package domain

import domain.TiposMovimientos._
import enums.TipoMonstruo._


case class Guerrero(energiaMaxima: Int,
                    energia: Int,
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

  def puedeEjecutar(mov: Movimiento): Boolean = {
    mov match {
      case UsarSemilla => puedeUsarItem(Semilla)
      case UsarArmaDeFuego => puedeUsarItem(Arma(DeFuego))
      case _ => estado == Consciente
    }
  }

  def dejarseFajar: Guerrero = copy(roundsDejadoFajar = roundsDejadoFajar + 1)

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

  def movimientoMasEfectivoContra(enemigo: Guerrero, unCriterio: Criterio): Movimiento = {
    val resultados: List[(Movimiento, Int)] = for {

      //Por cada movimiento del guerrero
      mov <- this.movimientos.filter(puedeEjecutar)

      //ejecuto movimiento
      guerreroPostMov = this.ejecutar(mov, enemigo)

      //valoro movimiento segun criterio
      valor = unCriterio(guerreroPostMov._1)

    } yield (mov, valor)

    println(resultados.sortBy(_._2).reverse)
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
  
  def planDeAtaqueContra(enemigo: Guerrero, cantidadDeRounds: Int)(unCriterio: Criterio): PlanDeAtaque = {
    var movimientosElegidos: List[Movimiento] = Nil
    var guerreros: (Guerrero, Guerrero, Option[Guerrero]) = (this, enemigo, None)      //Para facilitar trabajar con pelearRound en el Ciclo, guardo en esta variable los resultados paso a paso
    //Defino funciones para abstraer nombre a lo de arriba
    def miGuerrero  =   guerreros._1
    def elEnemigo   =   guerreros._2
    
    
    for( roundActual <- 1 to cantidadDeRounds ){
      //Elijo Movimiento mas efectivo
       val mov = miGuerrero.movimientoMasEfectivoContra(elEnemigo, unCriterio)
       //Simulo la Pelea y guardo los resultados
       guerreros = miGuerrero.pelearRound(mov)(elEnemigo)
       
       //Podria hacerse alguna validacion antes de agregar el movimiento, si fuera necesario..
       movimientosElegidos = movimientosElegidos union List(mov)
    }
    
    PlanDeAtaque(movimientosElegidos, cantidadDeRounds, unCriterio)
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
