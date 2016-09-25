# Constantes de Resultado Ejecutar Tests
EJECUCION_CORRECTA  = "Funciono"
EJECUCION_FALLIDA   = "Fallo el Test"
EJECUCION_EXPLOTO   = "Exploto el Test"

class Resultado
  attr_accessor :simbolo_test, :clase_suite, :resultado_ejecucion, :excepcion

  def initialize(simbolo_test, clase_suite, resultado_ejecucion, excepcion = nil)
    @simbolo_test = simbolo_test
    @clase_suite = clase_suite
    @resultado_ejecucion = resultado_ejecucion
    @excepcion = excepcion
  end

  def imprimir
    puts "Suite: '#{clase_suite.to_s}' Test '#{simbolo_test.to_s}': #{resultado_ejecucion}"

    # Solo en caso de que Fallen o Exploten los tests muestro BackTrace de Excepcion
    if resultado_ejecucion == EJECUCION_FALLIDA or resultado_ejecucion == EJECUCION_EXPLOTO
      puts excepcion.message
      puts excepcion.backtrace.inspect
    end
  end

  def funciono
    return resultado_ejecucion == EJECUCION_CORRECTA
  end

  def fallo
    return resultado_ejecucion == EJECUCION_FALLIDA
  end

  def exploto
    return resultado_ejecucion == EJECUCION_EXPLOTO
  end

end



class Resultados
  attr_accessor :coleccion_resultados

  def initialize()
    @coleccion_resultados = Array.new
  end

  def agregar(resultado)
    coleccion_resultados << resultado
    return resultado
  end

  def unir_resultados(otros_resultados)
    coleccion_resultados.concat(otros_resultados.coleccion_resultados)
  end

  def obtener_tests_funcionaron
    return coleccion_resultados.select{ |result| result.funciono }
  end

  def obtener_tests_fallaron
    return coleccion_resultados.select{ |result| result.fallo }
  end

  def obtener_tests_explotaron
    return coleccion_resultados.select{ |result| result.exploto }
  end

  def resultado_final
    return EJECUCION_EXPLOTO unless obtener_tests_explotaron.length <= 0

    return EJECUCION_FALLIDA unless obtener_tests_fallaron.length <= 0

    return EJECUCION_CORRECTA
  end

  def imprimir
    # Los imprimo agrupados por Resultado, me parece mas util porque siempre uno primero quiere ver los que explotan, luegos los que fallan y sino muestra todos los que dieron bien.
    obtener_tests_funcionaron.each { |t| t.imprimir }
    puts puts

    obtener_tests_fallaron.each { |t| t.imprimir }
    puts puts

    obtener_tests_explotaron.each { |t| t.imprimir }
    puts puts
  end

  # Para los "eq" de RSPEC, asi podemos devolver los resultados enteros y al mismo tiempo permite comparar
  def ==(constante_resultado)
    return resultado_final == constante_resultado
  end


end
