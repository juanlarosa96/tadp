import domain._
import domain.TiposMovimientos._
import enums.TipoMonstruo
import org.scalatest._

class MovimientosTest extends FlatSpec with Matchers with BeforeAndAfter {

  it should "Persona carga su Ki sin pasarse del maximo" in {
    val pedro = Guerrero(100, 80, List(CargarKi), Nil, Consciente, 0, Humano)
    val pedroConEnergia = pedro.ejecutar(CargarKi, null)._1

    assert(pedroConEnergia.energia == 100)
  }
  /*
    it should "Persona elige un movimiento" in {
      val chumbo = Arma(tipo = DeFuego)
      val usarArma = UsarItem(chumbo)(_,_)

      val goku  = Guerrero(1000, 80, List(UsarSemilla, CargarKi, usarArma), List(Semilla), Consciente, 0, Humano)
      val vegeta = Guerrero(100, 20, Nil, Nil, Consciente, 0, Humano)

      val mejorMov = goku.movimientoMasEfectivoContra(vegeta, DejarMasKi)

      assert(mejorMov == UsarSemilla)
    }

    it should "Pelear un round" in {
      val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla), Consciente, 0, Humano)
      val piccolo = Guerrero(100, 20, List(GolpesNinja), Nil, Consciente, 0, Namekusein)

      val round = piccolo.pelearRound(GolpesNinja)(krillin)

      assert(round._3.contains(piccolo))
    }

    it should "Guerrero usa las esferas" in {
      val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera), Consciente, 0, Humano)
      val krillinConsume = krillin.usarEsferas

      assertResult(true){
        krillinConsume.inventario == List(Semilla)
      }
    }

    it should "Guerrero tiene item" in {
      val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera), Consciente, 0, Humano)

      assertResult(true){
        krillin.tieneItem(Semilla)
      }
    }

    it should "Guerrero tiene 7 esferas" in {
      val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera), Consciente, 0, Humano)
      val krillinEsferoso = krillin

      assertResult(true){
        krillin.tiene7Esferas
      }
    }
  */
  it should "Yajirobe tiene su plan de ataque" in {
    val yajirobe = Guerrero(1000, 1000, List(CargarKi, UsarSemilla, GolpesNinja, UsarEspada), List(Arma(Filosa), Semilla), Consciente, 0, Humano)
    val cell = Guerrero(2000, 1500, List(GolpesNinja), Nil, Consciente, 0, Monstruo(TipoMonstruo.CELL))

    val planDeAtaqueDeYajirobe = yajirobe.planDeAtaqueContra(cell, 2)(DejarMasKi)

    assertResult(true){
      planDeAtaqueDeYajirobe.movimientos == List(UsarEspada, UsarSemilla) //IMPORTA EL ORDEN EN QUE SE GUARDAN LOS MOVIMIENTOS DE IGUAL VALOR PARA EL CRITERIO
    }
  }
}

 /*
goku.masefectivocontra(vegeta) {
  (goku.movimientos, goku, vegeta) = for {
    movimiento <- goku.movimientos

  } yield movimiento(goku, vegeta)
}
*/