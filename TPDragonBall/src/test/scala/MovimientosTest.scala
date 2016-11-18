import domain._
import domain.TiposMovimientos._
import org.scalatest._

class MovimientosTest extends FlatSpec with Matchers with BeforeAndAfter {

  it should "Persona carga su Ki sin pasarse del maximo" in {
    val pedro = Guerrero(100, Ki(80), List(CargarKi), Nil, Consciente, 0, Humano)
    println("pedro antes de cargar energia")
    println(pedro)
    println("pedro despues de cargar energia")
    val pedroConEnergia = pedro.ejecutar(CargarKi, null)._1
    println(pedroConEnergia)
    assert(pedroConEnergia.energia.cant == 100)
  }

  it should "Persona elige un movimiento" in {
    val chumbo = Arma(tipo = DeFuego)

    val usarArma = UsarItem(chumbo)(_,_)

    val goku  = Guerrero(100, Ki(80), List(CargarKi, CargarMenosKi, UsarSemilla, usarArma), List(Semilla), Consciente, 0, Humano)
    val vegeta = Guerrero(100, Ki(20), Nil, Nil, Consciente, 0, Humano)

    val mejorMov = goku.movimientoMasEfectivoContra(vegeta, DejarMasKi)

    assert(mejorMov == UsarSemilla)
  }

  it should "Pelear un round" in {
    val krillin  = Guerrero(100, Ki(10), List(CargarKi, CargarMenosKi), List(Semilla), Consciente, 0, Humano)

    val piccolo = Guerrero(100, Ki(20), List(GolpesNinja), Nil, Consciente, 0, Namekusein)

    val round = piccolo.pelearRound(GolpesNinja)(krillin)
    println(round._2.energia)
    assert(round._3.contains(piccolo))
  }
}

 /*
goku.masefectivocontra(vegeta) {
  (goku.movimientos, goku, vegeta) = for {
    movimiento <- goku.movimientos

  } yield movimiento(goku, vegeta)
}
*/