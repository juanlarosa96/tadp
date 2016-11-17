package domain


import domain.TiposMovimientos.Movimiento
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

  def ejecutar(mov: Movimiento, enemigo: Guerrero): Guerrero = {
    estado match{
      case Conciente => mov(this, enemigo)
      case _ => if(mov == UsarSemilla) mov(this, enemigo) else (this,enemigo)
    }

  }

  def dejarseFajar: Guerrero = this

  def alterarEstado(estadoNuevo: Estado): Guerrero = {
    estadoNuevo match{
      case Consciente => this.copy(estado = estadoNuevo)
      case _ => raza match {
        case Saiyajin => this.copy(raza.transformacion = Normal, roundsDejadoFajar = 0)
        case _ => this.copy(roundsDejadoFajar = 0)
      }
    }}

    this.copy(estado = estadoNuevo)
  }

  def tiene7Esferas(): Boolean = {
    inventario.exists {
      case esfera: Esfera => esfera.cantidad == 7
    }
  }

  def usarEsferas: Guerrero = {
    this.copy(inventario = inventario.filter(_.getClass != Esfera))
  }

   def tieneMunicion: Boolean = {
     inventario.exists {
       case mun: Municion => mun.cantidad >= 1
     }

    def puedeUsarItem(item: Item) : Boolean = {
     item match {
       case arma :Arma if(arma.tipo == DeFuego) => (this.inventario.exists(item) and this.tieneMunicion)
       case _ => this.inventario.exists(item)
     }
    }
   }

   //LEO: Idea: Podria hacerse generico a cualquier tipo de Item, tipo semilla hermitaneo. Hay que ver como pasar como parametro un tipo de clase
   def consumirMunicion: Guerrero = {
     val nuevoInventario = this.inventario.map {
       case mun: Municion => mun.consumir;
       case otro => otro;  //No modifica los demas
     }
    //LEO: Falta hacer el arreglo del Copy. No me acuerdo que habian dicho en clase...
    this.copy(inventario = nuevoInventario)
   }

  //Creo metodo porque se esta repitiendo todo el tiempo lo mismo
  def cambiarEnergia(valor: Int): Guerrero = {
    valor match {
      case this.energia.cant + valor > this.energiaMaxima) => this.copy(energia = Ki(this.energiaMaxima)
      case this.energia.cant + valor < 0 => this.copy(energia = Ki(0), estado = Muerto)
      case _ => this.copy(energia = Ki(energia.cant + valor))
    }
  }

  def movimentoMasEfectivoContra(enemigo: Guerrero, unCriterio: Criterio): Movimiento = {
    val resultados: List[(Movimiento, Int)] = for {

      //Por cada movimiento del guerrero
      mov <- this.movimientos

      //ejecuto movimiento
      guerreroPostMov = this.ejecutar(mov, enemigo)

      //valoro movimiento segun criterio
      valor = unCriterio(guerreroPostMov)

    } yield (mov, valor)

    //Ordeno por Mayor puntaje segun criterio y obtengo el primero
    resultados.sortBy(_._2).map(_._1).reverse.head
  }

  def pelearRound(mov: Movimiento, enemigo: Guerrero): (Guerrero, Guerrero, Option[Guerrero]) = {
    ejecutar(mov, enemigo)
    ejecutar(enemigo.movimentoMasEfectivoContra(this,DejarMasKi),this)
    if(enemigo.estado == Muerto){
      (this,enemigo,Some(this))
    } else if(this.estado == Muerto){
      (this,enemigo,Some(this))
    } else {
      (this,enemigo,None)
    }
  }
  def recibirGolpeKi(cantidad: Int): Guerrero ={
    this.raza match
      case Monstruo => this.cambiarEnergia((-2) *cantidad)
      case Androide => this.cambiarEnergia(cantidad)
      case _ => this.cambiarEnergia(-cantidad)
  }

}



// ------------------------------ TIPOS DE GUERRERO ------------------------------

abstract class Raza

case object Humano extends Raza
case object Androide extends Raza
case object Namekusein extends Raza

case class Saiyajin(cola: Boolean,
                    transformacion: Transformacion) extends Raza {

  def cortarCola = this.copy(cola = false)
  def transformarEnMono = this.copy(transformacion = Mono)
  def transformarEnSuperSaiyajin(nivel: Int) = this.copy(transformacion = SuperSaiyajin(nivel))

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

abstract class  FuenteDeEnergia {
  def cant :Int
}

case class Ki(cant: Int) extends FuenteDeEnergia
case class Bateria(cant: Int) extends FuenteDeEnergia


