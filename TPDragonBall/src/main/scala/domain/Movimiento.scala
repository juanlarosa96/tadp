package domain

import domain.SuperSaiyajin

import domain.Saiyajin

import domain.Namekusein

import domain.Monstruo

import domain.Humano

import domain.Guerrero

trait Movimiento {
   def apply(guerrero :Guerrero) : Guerrero
}


case object CargarKi extends Movimiento {
  def apply(guerrero :Guerrero) :Guerrero = {
    guerrero match {
      case humano :Humano => humano.copy(energia = Ki(humano.energia.cant +100))
      case superSaiyajin :SuperSaiyajin => superSaiyajin.copy(energia = Ki(superSaiyajin.energia.cant +150 * superSaiyajin.nivel))
      case namekusein :Namekusein => namekusein.copy(energia = Ki(namekusein.energia.cant +100))
      case monstruo :Monstruo => monstruo.copy(energia = Ki(monstruo.energia.cant +100))
      case saiyajin :Saiyajin => saiyajin.copy(energia = Ki(saiyajin.energia.cant +100))
     // case _  => _
    }
  }
}
case object DejarseFajar extends Movimiento