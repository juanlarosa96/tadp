/**
  * Created by gas on 29/10/16.
  */
package domain

abstract class Item
abstract class TipoArma

case class Arma(tipo: TipoArma) extends Item
case object DeFuego extends TipoArma
case object Roma extends TipoArma
case object Filosa extends TipoArma
case object Semilla extends Item
case object Esfera extends Item
