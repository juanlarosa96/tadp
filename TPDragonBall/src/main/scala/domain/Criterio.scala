package domain

trait Criterio {
  def apply(guerreroFInal: Guerrero): Int
}

case object RealizarMasDanio extends Criterio {
   def apply(guerreroFinal: Guerrero): Int = {
     //Devuelvo la diferencia con respecto a la vida maxima, osea lo que le quitaron, osea el daño recibido
     return guerreroFinal.energiaMaxima - guerreroFinal.energia.cant;
   }
}

case object DejarMasKi extends Criterio {
  def apply(guerreroFinal: Guerrero): Int = {
    //Devuelvo simplemente el KI, porque luego se ordenara por mayor Ki
    return guerreroFinal.energia.cant
  }
}

case object TacaniosMasItems extends Criterio {
  def apply(guerreroFinal: Guerrero): Int = {
    //Devuelvo la cantidad de items que le quedan al finalizar el movimiento, elegira el que le haga perder menos (se quede con mas)
    return guerreroFinal.inventario.size;
  }
}

case object KrillinNoMorir extends Criterio {
  def apply(guerreroFinal: Guerrero): Int = {
    //Es medio feucho porque tiene que elegir a todos los movimientos por igual mientras no lo maten. 
    //Le puse 5 si el movimiento si no lo mata y 0 si lo hace.
    //Al Ordenar ordenara los de 5 primeros y al final los de 0
    //TODO Estaria bueno cambiarlo por algo mejor. ¿Sugerencias?
    if (guerreroFinal.energia.cant >= 1) return 5
    else return 0
  }
}