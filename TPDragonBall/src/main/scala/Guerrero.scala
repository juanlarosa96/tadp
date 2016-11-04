
trait Guerrero {

  val energiaMaxima: Int
  val energia: Int
  val tipoEnergia: FuenteDeEnergia
  val movimientos: List[Movimiento]
  val items: List[Item]

  def ejecutar(mov: Movimiento): Guerrero = {
    mov match {
      case CargarKi => cargarKi()
      case DejarseFajar => dejarseFajar()
    }
  }

  def dejarseFajar(): Guerrero = this

  def cargarKi(): Guerrero

}

// ------------------------------ TIPOS DE GUERRERO ------------------------------

case class Humano(energiaMaxima: Int,
                  energia: Int,
                  tipoEnergia: Ki,
                  movimientos: List[Movimiento],
                  items: List[Item]) extends Guerrero {

  override def cargarKi(): Guerrero = {

    val energiaActual = energia
    this.copy(energia = energiaActual + 100)
  }
}

case class Androide(energiaMaxima: Int,
                    energia: Int,
                    tipoEnergia: Bateria,
                    movimientos: List[Movimiento],
                    items: List[Item]) extends Guerrero

case class Namekusein(energiaMaxima: Int,
                      energia: Int,
                      tipoEnergia: Ki,
                      movimientos: List[Movimiento],
                      items: List[Item]) extends Guerrero

case class Monstruo(energiaMaxima: Int,
                    energia: Int,
                    tipoEnergia: Ki,
                    movimientos: List[Movimiento],
                    items: List[Item]) extends Guerrero

case class Saiyajin(energiaMaxima: Int,
                    energia: Int,
                    tipoEnergia: Ki,
                    movimientos: List[Movimiento],
                    items: List[Item],
                    ki: Int,
                    cola: Boolean,
                    estado: Estado) extends Guerrero

case class Fusion(unGuerrero: Guerrero,
                  otroGuerrero: Guerrero) extends Guerrero {

  val energiaMaxima = unGuerrero.energiaMaxima + otroGuerrero.energiaMaxima
  val energia = unGuerrero.energia + otroGuerrero.energia
  val tipoEnergia = Ki
  val movimientos = unGuerrero.movimientos ::: otroGuerrero.movimientos
  val items = unGuerrero.items ::: otroGuerrero.movimientos
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

trait FuenteDeEnergia

case object Ki extends FuenteDeEnergia
case object Bateria extends FuenteDeEnergia