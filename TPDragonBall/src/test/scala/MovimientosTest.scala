import domain._
import domain.TiposMovimientos._
import enums.TipoMonstruo
import org.scalatest._

class MovimientosTest extends FlatSpec with Matchers with BeforeAndAfter {

  it should "Persona carga su Ki sin pasarse del maximo" in {
    val pedro = Guerrero(100, 80, List(CargarKi), Nil, Consciente, 0, Humano)
    val pedroConEnergia = pedro.ejecutar(Some(CargarKi), null)._1

    assert(pedroConEnergia.energia == 100)
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

  it should "Persona elige un movimiento que deje mas ki" in {
    val chumbo = Arma(tipo = DeFuego)
    val usarArma = UsarItem(chumbo)(_,_)

    val goku  = Guerrero(1000, 80, List(UsarSemilla, CargarKi, usarArma), List(Semilla), Consciente, 0, Humano)
    val vegeta = Guerrero(100, 20, Nil, Nil, Consciente, 0, Humano)

    val mejorMov = goku.movimientoMasEfectivoContra(vegeta, DejarMasKi)

    assert(mejorMov.contains(UsarSemilla))
  }

  it should "Persona elige movimiento que mas pegue" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, UsarGenkidama, UsarKamehameha, CargarKi), Nil, Consciente, 4, Saiyajin(cola = false, Normal))
    val vegeta = Guerrero(3000, 3000, Nil, Nil, Consciente, 0, Saiyajin(cola = false, Normal))

    val mejorMov = goku.movimientoMasEfectivoContra(vegeta, RealizarMasDanio)

    assert(mejorMov.contains(UsarGenkidama))
  }

  it should "Pelear un round" in {
    val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla), Consciente, 0, Humano)
    val piccolo = Guerrero(100, 50, List(GolpesNinja), Nil, Consciente, 0, Namekusein)

    val round = piccolo.pelearRound(GolpesNinja)(krillin)

    assert(round._3.contains(piccolo))
  }

  it should "Yajirobe tiene su plan de ataque" in {
    val yajirobe = Guerrero(1000, 1000, List(CargarKi, UsarSemilla, GolpesNinja, UsarEspada), List(Arma(Filosa), Semilla), Consciente, 0, Humano)
    val cell = Guerrero(2000, 1500, List(GolpesNinja), Nil, Consciente, 0, Monstruo(TipoMonstruo.CELL))

    val planDeAtaqueDeYajirobe = yajirobe.planDeAtaqueContra(cell, 2)(DejarMasKi)

    assertResult(true){
      planDeAtaqueDeYajirobe.movimientos == List(Some(UsarEspada), Some(UsarSemilla)) //IMPORTA EL ORDEN EN QUE SE GUARDAN LOS MOVIMIENTOS DE IGUAL VALOR PARA EL CRITERIO
    }
  }

  /*
  it should "Goku pelea contra Vegeta" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, Onda(Genkidama), Onda(Kamehameha)), Nil, Consciente, 0, Saiyajin(cola = false, Normal))
  }
*/

}
