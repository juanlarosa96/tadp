package domain

case class ResultadoAtaque(peleador: Guerrero, enemigo: Guerrero, ganador:Option[Guerrero]){
  def hayGanador: Boolean = ganador.nonEmpty
}
