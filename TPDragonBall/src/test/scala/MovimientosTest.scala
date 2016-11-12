/**
  * Created by javierz on 04/11/16.
  */
import domain.{Humano, Ki}
import org.scalatest._

class MovimientosTest extends FlatSpec with Matchers {

  it should "Persona carga su Ki" in {
    val pedro = Humano(100,Ki(80),null,null)
    println("pedro antes de cargar energia")
    println(pedro)
    println("pedro despues de cargar energia")
    val pedroConEnergia = pedro.ejecutar(CargarKi)
    println(pedroConEnergia)
    assert(pedroConEnergia.energia.cant == 180)
  }
}