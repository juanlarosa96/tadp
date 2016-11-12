package domain

import enums.TipoMonstruo
import enums.TipoMonstruo._
import domain.Tipos_Movimientos.Movimiento
import scala.util.Try


abstract class Guerrero() {
  def energiaMaxima: Int
  def energia: FuenteDeEnergia
  def movimientos: List[Movimiento]
  def inventario: List[Item]
  def estado: Estado //TODO ver de sacar estado de aca, que solo lo tenga el saiyajin (y ver en ese caso, que pasa con el estado en fusion)


  //Debe ser Try porque podria fallar un guerrero al intentar ejecutar algo que no deberia
  def ejecutar(mov: Movimiento, enemigo: Guerrero): Try[Guerrero] = { //como vamos a tener muchos movimientos, para esto esta mejor el poli ad-hoc
    Try(  
        mov(this, enemigo)._1  
        )  //Obtengo al Guerrero que soy Yo, no me importa como quedo el enemigo aca
  }

  //TODO ver si es necesario que sea try. Enemigo deberia ser option? Para cargarKi, x ejemplo, no se necesita un enemigo

  def dejarseFajar = {}

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

case class Humano(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], inventario: List[Item], estado :Estado) extends Guerrero
case class Androide(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], inventario: List[Item], estado :Estado) extends Guerrero
case class Namekusein(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], inventario: List[Item], estado :Estado) extends Guerrero
case class Monstruo(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], inventario: List[Item], estado :Estado, tipoMonstruo: TipoMonstruo) extends Guerrero {

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

case class Saiyajin(ki: Int,
                    cola: Boolean,
                    estado: Estado,
                    energiaMaxima: Int,
                    energia: FuenteDeEnergia,
                    movimientos: List[Movimiento],
                    inventario: List[Item],
                    transformacion: Transformacion) extends Guerrero

case class Fusion(unGuerrero: Guerrero,
                  otroGuerrero: Guerrero) extends Guerrero {
   def energiaMaxima = unGuerrero.energiaMaxima + otroGuerrero.energiaMaxima
   def energia = Ki(unGuerrero.energia.cant + otroGuerrero.energia.cant)
   def movimientos = unGuerrero.movimientos ::: otroGuerrero.movimientos
   def inventario = unGuerrero.inventario ::: otroGuerrero.inventario
   def estado = unGuerrero.estado
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


