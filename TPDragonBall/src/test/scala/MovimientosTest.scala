import domain._
import domain.TiposMovimientos._
import org.scalatest._

class MovimientosTest extends FlatSpec with Matchers {

  it should "Persona carga su Ki" in {
    val pedro = Guerrero(100,Ki(80), List(CargarKi), Nil, Consciente, Humano)
    println("pedro antes de cargar energia")
    println(pedro)
    println("pedro despues de cargar energia")
    val pedroConEnergia = pedro.ejecutar(CargarKi, null)
    println(pedroConEnergia)
    assert(pedroConEnergia.energia.cant == 180)
  }

  it should "Persona elige un movimiento" in {
    val chumbo = Arma(tipo = DeFuego)

    //val usarSemilla = UsarItem(Semilla)(_,_)

    val usarArma = UsarItem(chumbo)(_,_)

    //val fusionarConKrillin = Fusion(krillin)(_,_)

    val goku = Guerrero(100, Ki(80), List(CargarKi, CargarMenosKi, UsarSemilla, usarArma), Nil, Consciente, Humano)
    val vegeta = Guerrero(100, Ki(20), Nil, Nil, Consciente, Humano)

    val mejorMov = goku.movimentoMasEfectivoContra(vegeta, DejarMasKi)

    assert(mejorMov == UsarSemilla)
  }

}

 /*
goku.masefectivocontra(vegeta) {
  (goku.movimientos, goku, vegeta) = for {
    movimiento <- goku.movimientos

  } yield movimiento(goku, vegeta)
}
*/