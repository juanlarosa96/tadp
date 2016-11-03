
trait Guerrero {

  val energiaMaxima: Int
  var energia: FuenteDeEnergia
  var movimientos: List[Movimiento]
  var items: List[Item]

  def ejecutar(mov: Movimiento): Guerrero = {
    mov match {
      case CargarKi => cargarKi(this)
    }
  }


}

// ------------------------------ TIPOS DE GUERRERO ------------------------------

case class Humano extends Guerrero
case class Androide extends Guerrero
case class Namekusein extends Guerrero
case class Monstruo extends Guerrero

case class Saiyajin(ki: Int,
                    cola: Boolean,
                    estado: Estado) extends Guerrero

case class Fusion(unGuerrero: Guerrero,
                  otroGuerrero: Guerrero) extends Guerrero {

  val energiaMaxima = unGuerrero.energiaMaxima + otroGuerrero.energiaMaxima
  var energia = unGuerrero.energia + otroGuerrero.energia
  var movimientos = unGuerrero.movimientos ::: otroGuerrero.movimientos
  var items = unGuerrero.items ::: otroGuerrero.movimientos
}


// ------------------------- ESTADOS SAIYAN --------------------------

trait Estado {
  def cargarKi(guerrero: Guerrero): Guerrero = {
    guerrero.energia = guerrero.energia + 100
    guerrero
  }
}

case class SuperSaiyajin(nivel: Int) extends Estado {
  override def cargarKi(guerrero: Guerrero): Guerrero = {
    guerrero.energia = guerrero.energia + 150 * nivel
    guerrero
  }
}
case object Mono extends Estado
case object Normal extends Estado

// ------------------------- FUENTES DE ENERGIA --------------------------

abstract class FuenteDeEnergia

case class Ki(cant: Int) extends FuenteDeEnergia
case class Bateria(cant: Int) extends FuenteDeEnergia