package domain

import domain.Movimientos.Movimiento
import enums.TipoMonstruo
import enums.TipoMonstruo._

import scala.util.Try


case class Guerrero(energiaMaxima: Int,
                    energia: FuenteDeEnergia,
                    movimientos: List[Movimiento],
                    inventario: List[Item],
                    estado: Estado,
                    raza: Raza) {

  def ejecutar(mov: Movimiento, enemigo: Guerrero): Guerrero = {
        mov(this, enemigo)._1
  }

  def dejarseFajar: Guerrero = this

  def alterarEstado(estadoNuevo: Estado): Guerrero = {
    this.copy(estado = estadoNuevo)
  }

  def tiene7Esferas(): Boolean = {
    inventario.exists {
      case esfera: Esfera => esfera.cantidad == 7
    }
  }

  def usarEsferas(guerrero: Guerrero): Guerrero = {
    guerrero.copy(inventario = inventario.filter(_.getClass != Esfera))
  }

   def tieneMunicion: Boolean = {
     inventario.exists {
       case mun: Municion => mun.cantidad >= 1
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
    this.copy(energia = Ki(energia.cant + valor))
  }
/*
  def movimentoMasEfectivoContra(enemigo: Guerrero, unCriterio: Criterio): Movimiento = {
    val resultados = for {
      //Para cada Movimiento
      mov <- this.movimientos
      //Aplicalos al guerrero actual y al enemigo y devolveme lo que importa (depende del movimiento)
      guerreroFinal <- ejecutar(mov, enemigo)    //Solo me interesan los Guerreros que NO fallaron al ejecutar
      //Aplico el Criterio a ver como puntua el resultado final
      valor = unCriterio(guerreroFinal)
    } yield (mov, valor)

    //Ordeno por Mayor puntaje segun criterio y obtengo el primero
    resultados.sortBy(_._2).map(_._1).reverse.head
  }
*/
  def pelearRound(mov: Movimiento, enemigo: Guerrero): (Guerrero, Guerrero) = {
    mov(this, enemigo)
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


