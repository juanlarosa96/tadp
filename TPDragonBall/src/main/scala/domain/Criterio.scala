package domain

trait Criterio {
  def apply(guerreroFInal: Guerrero): Int
}

case object MasDanio extends Criterio{
   def apply(guerreroFinal: Guerrero): Int = {
     //Devuelvo la diferencia con respecto a la vida maxima, osea lo que le quitaron, osea el daño recibido
     return guerreroFinal.energiaMaxima - guerreroFinal.energia.cant;
   }
}

//TODO faltan los demas