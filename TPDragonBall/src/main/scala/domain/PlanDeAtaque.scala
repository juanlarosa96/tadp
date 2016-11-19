package domain

import domain.TiposMovimientos._

case class PlanDeAtaque(movimientos:List[Movimiento], cantidadRunds:Int, criterio:Criterio)