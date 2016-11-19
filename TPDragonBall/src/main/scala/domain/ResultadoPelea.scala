package domain

case class ResultadoPelea(peleador: Guerrero, enemigo: Guerrero, ganador:Option[Guerrero]){
  def hayGanador:Boolean = ganador.nonEmpty
}
