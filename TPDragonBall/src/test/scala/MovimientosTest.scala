/**
  * Created by javierz on 04/11/16.
  */
import domain.{Consciente, Guerrero, Humano, Ki}
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
}

/*
goku.masefectivocontra(vegeta) {
  (goku.movimientos, goku, vegeta) = for {
    movimiento <- goku.movimientos

  } yield movimiento(goku, vegeta)
}
*/