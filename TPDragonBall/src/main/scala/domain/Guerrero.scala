package domain

import enums.TipoMonstruo
import enums.TipoMonstruo._
import domain.TiposMovimientos.Movimiento
import scala.util.Try


case class Guerrero(energiaMaxima: Int,
                    energia: FuenteDeEnergia,
                    movimientos: List[Movimiento],
                    inventario: List[Item],
                    estado: Estado) {

  def ejecutar(mov: Movimiento, enemigo: Guerrero): Try[Guerrero] = {
    Try(
        mov(this, enemigo)._1
        )  //Obtengo al Guerrero que soy Yo, no me importa como quedo el enemigo aca
  }

  def dejarseFajar: Guerrero = this

  def alterarEstado(estadoNuevo: Estado): Guerrero = {
    this.copy(estado = estadoNuevo)
  }

  def tiene7Esferas: Boolean = {
    inventario.exists {
      case esfera: Esfera => esfera.cantidad == 7
    }
  }

  def usarEsferas: Guerrero = {
      case esfera: Esfera if esfera.cantidad == 7 =>
        copy(inventario = inventario.filter(_.getClass != Esfera))
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

  def movimentoMasEfectivoContra(enemigo: Guerrero, unCriterio: Criterio): Movimiento = {
    val resultados = for {
      //Para cada Movimiento
      mov <- this.movimientos
      //Aplicalos al guerrero actual y al enemigo y devolveme lo que importa (depende del movimiento)
      guerreroFinal <- ejecutar(mov, enemigo).toOption    //Solo me interesan los Guerreros que NO fallaron al ejecutar
      //Aplico el Criterio a ver como puntua el resultado final
      valor = unCriterio(guerreroFinal)
    } yield (mov, valor)

    //Ordeno por Mayor puntaje segun criterio y obtengo el primero
    resultados.sortBy(_._2).map(_._1).reverse.head
  }

  def pelearRound(mov: Movimiento, enemigo: Guerrero): (Guerrero, Guerrero) = {
    mov(this, enemigo)
  }
}



// ------------------------------ TIPOS DE GUERRERO ------------------------------

case class Humano(override val energiaMaxima: Int,
                  override val energia: FuenteDeEnergia,
                  override val movimientos: List[Movimiento],
                  override val inventario: List[Item],
                  override val estado :Estado) extends Guerrero(energiaMaxima: Int,
                                                                energia: FuenteDeEnergia,
                                                                movimientos: List[Movimiento],
                                                                inventario: List[Item],
                                                                estado: Estado)

case class Androide(override val energiaMaxima: Int,
                    override val energia: FuenteDeEnergia,
                    override val movimientos: List[Movimiento],
                    override val inventario: List[Item],
                    override val estado :Estado) extends Guerrero(energiaMaxima: Int,
                                                                  energia: FuenteDeEnergia,
                                                                  movimientos: List[Movimiento],
                                                                  inventario: List[Item],
                                                                  estado: Estado)

case class Namekusein(override val energiaMaxima: Int,
                      override val energia: FuenteDeEnergia,
                      override val movimientos: List[Movimiento],
                      override val inventario: List[Item],
                      override val estado :Estado) extends Guerrero(energiaMaxima: Int,
                                                                    energia: FuenteDeEnergia,
                                                                    movimientos: List[Movimiento],
                                                                    inventario: List[Item],
                                                                    estado: Estado)

case class Saiyajin(override val energiaMaxima: Int,
                    override val energia: FuenteDeEnergia,
                    override val movimientos: List[Movimiento],
                    override val inventario: List[Item],
                    override val estado: Estado,
                    cola: Boolean,
                    transformacion: Transformacion) extends Guerrero(energiaMaxima: Int,
                                                                     energia: FuenteDeEnergia,
                                                                     movimientos: List[Movimiento],
                                                                     inventario: List[Item],
                                                                     estado: Estado){
  def cortarCola = this.copy(cola = false)
}

case class Monstruo(override val energiaMaxima: Int,
                    override val energia: FuenteDeEnergia,
                    override val movimientos: List[Movimiento],
                    override val inventario: List[Item],
                    override val estado :Estado,
                    tipoMonstruo: TipoMonstruo) extends Guerrero(energiaMaxima: Int,
                                                                 energia: FuenteDeEnergia,
                                                                 movimientos: List[Movimiento],
                                                                 inventario: List[Item],
                                                                 estado: Estado) {

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

}


case class Fusion(unGuerrero: Guerrero,
                  otroGuerrero: Guerrero) extends Guerrero(energiaMaxima = unGuerrero.energiaMaxima + otroGuerrero.energiaMaxima,
                                                           energia = Ki(unGuerrero.energia.cant + otroGuerrero.energia.cant),
                                                           movimientos = unGuerrero.movimientos ::: otroGuerrero.movimientos,
                                                           inventario = unGuerrero.inventario ::: otroGuerrero.inventario,
                                                           estado = unGuerrero.estado)

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


