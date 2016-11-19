package domain

import domain.TiposMovimientos._

case class PlanDeAtaque(movimientos:List[Option[Movimiento]], cantidadRunds:Int, criterio:Criterio)