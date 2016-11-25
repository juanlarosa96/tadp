import domain._
import domain.TiposMovimientos._
import enums.TipoMonstruo
import org.scalatest._

class MovimientosTest extends FlatSpec with Matchers with BeforeAndAfter {

  it should "Persona carga su Ki sin pasarse del maximo" in {
    val pedro = Guerrero(100, 80, List(CargarKi), Nil, Consciente, Humano)
    val pedroConEnergia = pedro.ejecutar(CargarKi, null)._1

    assert(pedroConEnergia.energia == 100)
  }


  it should "Guerrero usa las esferas" in {
    val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera), Consciente, Humano)
    val krillinConsume = krillin.usarEsferas

    assertResult(true){
      krillinConsume.inventario == List(Semilla)//Es medio feucho porque tiene que elegir a todos los movimientos por igual mientras no lo maten.
    //Le puse 5 si el movimiento si no lo mata y 0 si lo hace.

    }
  }


  it should "Guerrero tiene item" in {
    val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera), Consciente, Humano)

    assertResult(true){
      krillin.tieneItem(Semilla)
    }
  }


  it should "Guerrero no conoce movimiento" in {
    val super17 = Guerrero(4000, 3000, Nil, Nil, Consciente, Androide)

    assertResult(false){
      super17.conoceMovimiento(CargarKi)
    }
  }


  it should "Guerrero aprende movimiento" in {
    val super17 = Guerrero(4000, 3000, Nil, Nil, Consciente, Androide)

    val super17ConFinalFlash = super17.aprenderMovimiento(UsarFinalFlash)

    assert(super17ConFinalFlash.movimientos == List(UsarFinalFlash))
  }


  it should "Androide no hace nada si carga ki" in {
    val super17 = Guerrero(4000, 3000, List(CargarKi), Nil, Consciente, Androide)
    val goku = Guerrero(10000, 9000, Nil, Nil, Consciente, Saiyajin(cola = true, Normal))

    val super17CargaKi = super17.ejecutar(CargarKi, goku)._1

    assert(super17 == super17CargaKi)
  }


  it should "Guerrero no puede ejecutar movimiento que desconoce" in {
    val super17 = Guerrero(4000, 3000, Nil, Nil, Consciente, Androide)

    val super17TiraKamehameha = super17.puedeEjecutarMovimiento(CargarKi)

    assert(!super17TiraKamehameha)
  }


  it should "Guerrero intenta usar mov que no conoce y no hace nada" in {
    val super17 = Guerrero(4000, 3000, Nil, Nil, Consciente, Androide)
    val goku = Guerrero(10000, 9000, Nil, Nil, Consciente, Saiyajin(cola = true, Normal))

    val super17TiraGenkidama = super17.ejecutar(UsarGenkidama, goku)

    assert(super17TiraGenkidama == (super17, goku))
  }


  it should "Guerrero tiene 7 esferas" in {
    val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera, Esfera), Consciente, Humano)
    val krillinEsferoso = krillin

    assertResult(true){
      krillin.tiene7Esferas
    }
  }


  it should "Goku y Vegeta se fusionan" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, UsarGenkidama, UsarKamehameha), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val vegeta = Guerrero(1000, 500, List(GolpesNinja, UsarGenkidama, UsarFinalFlash), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val super17 = Guerrero(4000, 3000, Nil, Nil, Consciente, Androide)

    val vegetto = fusion(goku, vegeta)(super17)._1

    assert(vegetto.energiaMaxima == 3000)
    assert(vegetto.energia == 2500)
    assert(vegetto.inventario.isEmpty)
    assert(vegetto.estado == Consciente)
    assert(vegetto.raza == Fusion(goku, vegeta))
  }


  it should "Vegetto muere y devuelve a Goku" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, UsarGenkidama, UsarKamehameha), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val vegeta = Guerrero(1000, 500, List(GolpesNinja, UsarGenkidama, UsarFinalFlash), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val super17 = Guerrero(4000, 3000, Nil, Nil, Consciente, Androide)

    val vegetto = fusion(goku, vegeta)(super17)._1

    val gokuDesfusionado = vegetto.morir

    assert(gokuDesfusionado == goku.morir)
  }

  //TODO pto 1
  it should "Persona elige un movimiento que deje mas ki" in {
    val chumbo = Arma(tipo = DeFuego)
    val usarArma = usarItem(chumbo)(_,_)

    val goku  = Guerrero(1000, 80, List(UsarSemilla, CargarKi, usarArma), List(Semilla), Consciente, Humano)
    val vegeta = Guerrero(100, 20, Nil, Nil, Consciente, Humano)

    val mejorMov = goku.movimientoMasEfectivoContra(vegeta, DejarMasKi)

    assert(mejorMov.contains(UsarSemilla))
  }


  it should "Persona elige movimiento que mas pegue" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, UsarGenkidama, UsarKamehameha, CargarKi), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val vegeta = Guerrero(3000, 3000, Nil, Nil, Consciente, Saiyajin(cola = false, Normal))

    val mejorMov = goku.movimientoMasEfectivoContra(vegeta, RealizarMasDanio)

    assert(mejorMov.contains(UsarGenkidama))
  }

  //TODO pto 2
  it should "Pelear un round" in {
    val krillin  = Guerrero(100, 10, List(CargarKi), List(Semilla), Consciente, Humano)
    val piccolo = Guerrero(100, 20, List(GolpesNinja), Nil, Consciente, Namekusein)

    val round = piccolo.pelearRound(GolpesNinja)(krillin)

    assert(round.hayGanador)
    assert(round.ganador.contains(piccolo))
  }


  it should "Goku mata a Vegeta en un round" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, UsarGenkidama, UsarKamehameha), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val vegeta = Guerrero(1000, 1000, List(GolpesNinja, UsarGenkidama), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))

    val round = goku.pelearRound(UsarGenkidama)(vegeta)

    assert(round.ganador.contains(goku))
  }

  //TODO pto 3a
  it should "Yajirobe tiene su plan de ataque" in {
    val yajirobe = Guerrero(1000, 1000, List(CargarKi, UsarSemilla, GolpesNinja, UsarEspada), List(Arma(Filosa), Semilla), Consciente, Humano)
    val cell = Guerrero(2000, 1500, List(GolpesNinja), Nil, Consciente, Monstruo(TipoMonstruo.CELL))

    val planDeAtaqueDeYajirobe = yajirobe.planDeAtaqueContra(cell, 2)(DejarMasKi)

    assertResult(true){
      planDeAtaqueDeYajirobe.get.movimientos == List(UsarEspada, UsarSemilla) //IMPORTA EL ORDEN EN QUE SE GUARDAN LOS MOVIMIENTOS DE IGUAL VALOR PARA EL CRITERIO
    }
  }


  it should "plan de ataque falla" in {
    val krillin  = Guerrero(100, 60, List(GolpesNinja), List(Semilla), Consciente, Humano)
    val piccolo = Guerrero(100, 20, List(GolpesNinja), Nil, Consciente, Namekusein)

    val planDeAtaqueDePiccolo = piccolo.planDeAtaqueContra(krillin, 3)(RealizarMasDanio)

    assert(planDeAtaqueDePiccolo.isEmpty)
  }


  //TODO pto 4
  it should "Goku pelea contra Vegeta y gana con Genkidama" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, UsarGenkidama, UsarKamehameha), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val vegeta = Guerrero(1000, 1000, List(GolpesNinja), Nil, Consciente, Saiyajin(cola = false, Normal))

    val elPlanDeGoku = goku.planDeAtaqueContra(vegeta, 1)(RealizarMasDanio).get //GAS ??????

    val resultado = goku.pelearContra(vegeta)(elPlanDeGoku)

    assert(resultado.ganador.contains(goku))
  }


  it should "Vegeta le gana a goku x empezar (tirando Genkidama)" in {
    val goku = Guerrero(2000, 2000, List(GolpesNinja, UsarGenkidama, UsarKamehameha), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))
    val vegeta = Guerrero(1000, 1000, List(UsarGenkidama), Nil, DejandoseFajar(4), Saiyajin(cola = false, Normal))

    val elPlanDeVegeta = vegeta.planDeAtaqueContra(goku, 1)(RealizarMasDanio).get

    val resultado = vegeta.pelearContra(goku)(elPlanDeVegeta)

    assert(resultado.ganador.contains(vegeta))
  }

}
