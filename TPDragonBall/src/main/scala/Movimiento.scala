/**
  * Created by gas on 27/10/16.
  */
abstract class Movimiento

case object DejarseFajar extends Movimiento
case object CargarKi extends Movimiento
case object UsarItem extends Movimiento
case object ComerOponente extends Movimiento
case object ConvertirAMono extends Movimiento
case object ConvertirASsj extends Movimiento
case object Fusion extends Movimiento
case object Magia extends Movimiento

case class Ataque(tipo: TipoAtaque) extends Movimiento
abstract class TipoAtaque
abstract class Fisico extends TipoAtaque
abstract class DeEnergia extends TipoAtaque
case object Explotar extends Fisico
case object GolpesNinja extends Fisico
case object Onda extends DeEnergia
case object Genkidama extends DeEnergia