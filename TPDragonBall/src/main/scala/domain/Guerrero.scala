package domain

import domain.Movimiento

import domain.Item

trait Guerrero {
  val energiaMaxima: Int
  val energia: FuenteDeEnergia
  val movimientos: List[String]
  val items: List[Item]

  def ejecutar(mov: Movimiento): Guerrero = { //como vamos a tener muchos movimientos, para esto esta mejor el poli ad-hoc
    mov.apply(this)
  }

  def dejarseFajar = {}
}

// ------------------------------ TIPOS DE GUERRERO ------------------------------

case class Humano(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[String], items: List[Item]) extends Guerrero
case class Androide(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[String], items: List[Item]) extends Guerrero
case class Namekusein(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[String], items: List[Item]) extends Guerrero
case class Monstruo(energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[String], items: List[Item]) extends Guerrero

case class Saiyajin(ki: Int,
                    cola: Boolean,
                    estado: Estado,
                    energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[String], items: List[Item]) extends Guerrero

case class Fusion(unGuerrero: Guerrero,
                  otroGuerrero: Guerrero) extends Guerrero {
/*
  var energiaMaxima: Int = ???
  var energia: FuenteDeEnergia = null
  var movimientos: List[String] = null
  var items: List[Item] = null
 */
  def cargarKi = this

  def apply = {
    energiaMaxima = unGuerrero.energiaMaxima + otroGuerrero.energiaMaxima
    energia = Ki(unGuerrero.energia.cant + otroGuerrero.energia.cant)
    movimientos = unGuerrero.movimientos ::: otroGuerrero.movimientos
    items = unGuerrero.items ::: otroGuerrero.items
    this
  }
}


// ------------------------- ESTADOS SAIYAN --------------------------

trait Estado {
}

case class SuperSaiyajin(nivel: Int, energiaMaxima: Int, energia: FuenteDeEnergia, movimientos: List[String], items: List[Item]) extends Estado with Guerrero

case object Mono extends Estado
case object Normal extends Estado

// ------------------------- FUENTES DE ENERGIA --------------------------
abstract class  FuenteDeEnergia {
  def cant :Int
}

case class Ki(cant: Int) extends FuenteDeEnergia


case class Bateria(cant: Int) extends FuenteDeEnergia


