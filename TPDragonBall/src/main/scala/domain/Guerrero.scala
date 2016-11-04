package domain

import enums.TipoMonstruo
import enums.TipoMonstruo._

trait Guerrero {
  def energiaMaxima: Int
  def energia: FuenteDeEnergia
  def movimientos: List[Movimiento]
  def items: List[Item]

  def ejecutar(mov: Movimiento): Guerrero = { //como vamos a tener muchos movimientos, para esto esta mejor el poli ad-hoc
    mov.realizarMovimiento(this)
  }

  def dejarseFajar = {}
}

// ------------------------------ TIPOS DE GUERRERO ------------------------------

case class Humano(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], items: List[Item]) extends Guerrero
case class Androide(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], items: List[Item]) extends Guerrero
case class Namekusein(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], items: List[Item]) extends Guerrero
case class Monstruo(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], items: List[Item], tipoMonstruo: TipoMonstruo) extends Guerrero {
  def comerseAlOponente(guerrero :Option[Guerrero]) = {
    tipoMonstruo match {
      case TipoMonstruo.CELL =>
        guerrero.filter{gue => gue.getClass == Androide}
            .map{gue => this.copy(movimientos = this.movimientos ::: gue.movimientos) }
      case TipoMonstruo.MAJIN_BUU =>
        guerrero.map{gue => this.copy(movimientos = List(gue.movimientos.reverse.head))}
    }
  }
}

case class Saiyajin(ki: Int,
                    cola: Boolean,
                    estado: Estado,
                    energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], items: List[Item]) extends Guerrero

case class Fusion(unGuerrero: Guerrero,
                  otroGuerrero: Guerrero) extends Guerrero {

   val energiaMaxima = unGuerrero.energiaMaxima + otroGuerrero.energiaMaxima
   val energia = Ki(unGuerrero.energia.cant + otroGuerrero.energia.cant)
   val movimientos = unGuerrero.movimientos ::: otroGuerrero.movimientos
   val items = unGuerrero.items ::: otroGuerrero.items
}


// ------------------------- ESTADOS SAIYAN --------------------------

trait Estado {
}

case class SuperSaiyajin(nivel: Int, energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[Movimiento], items: List[Item], estado :Estado) extends Guerrero

case object Mono extends Estado
case object Normal extends Estado

// ------------------------- FUENTES DE ENERGIA --------------------------
abstract class  FuenteDeEnergia {
  def cant :Int
}

case class Ki(cant: Int) extends FuenteDeEnergia


case class Bateria(cant: Int) extends FuenteDeEnergia


