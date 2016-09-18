require_relative "../SRC/NuestraSuite"
require_relative "../SRC/FalloTest"


# Constantes de Resultado Ejecutar Tests
EJECUCION_CORRECTA  = 0
EJECUCION_FALLIDA   = 1
EJECUCION_EXPLOTO   = 2


class TADsPec
  #Devuelve la Peor Tipo de Ejecucion
  def self.preparar_resultado(antes, ahora)
    return antes > ahora ? antes : ahora
  end


  def self.es_test(nombre_metodo)
    return nombre_metodo.include? "testear_que_"
  end

  def self.obtener_tests(clase)
    # Obtengo todos los metodos de instancia (que definieron en la misma clase, serian nuestros posibles tests)
    metodos = clase.instance_methods

    # Devuelvo solo los metodos que contienen "testear_que_", los demas los ignoro
    return metodos.select { |metodo| self.es_test(metodo.to_s) }
  end



  def self.tiene_tests(clase)
    return self.obtener_tests(clase).size > 0
  end

  def self.es_suite_testing(clase)
    return self.tiene_tests clase
  end



  def self.testear_test(instancia_suite, un_test)
    begin
      instancia_suite.send un_test.to_sym

    # Si se ejecuta sin problemas, lo considero bien, sino tira una excepcion y salta mas abajo
      puts "    Ejecuto Bien '#{un_test.to_s}'"
      return EJECUCION_CORRECTA

    # Cuando Falla un Test sin Explotar, se controla con una excepcion nuestra
    rescue FalloTest => e
      puts "    Fallo Test '#{un_test.to_s}'"
      puts e.message
      puts e.backtrace.inspect
      return EJECUCION_FALLIDA

    # Cualquier Otra Excepcion considero que Exploto el test
    rescue Exception => e
      puts "    Exploto Test '#{un_test.to_s}'"
      puts e.message
      puts e.backtrace.inspect
      return EJECUCION_EXPLOTO
    end
  end



  # Tipo 3: Corre Solo los tests indicados de la Suite
  def self.testear_algunos_tests(clase_suite, tests)
    # Como tenemos la Clase de Suite, tenemos que crear una instancia para poder pedirle que ejecute los metodos
    instancia_suite = clase_suite.new
    # Le incluyo el Mixin con los Metodos de Testing, asi tiene todos los metodos para poder testear
    instancia_suite.extend NuestraSuite

    resultado_final = EJECUCION_CORRECTA    # Inicializo

    # Recorremos todos los Tests y vamos llamandolos, con las Excepciones controlamos la Ejecucion de estos
    tests.each { |un_test|
      resultado_test = self.testear_test(instancia_suite, un_test)
      # Aca seria un buen lugar para guardar resultados de los Tests si quisieramos devolvermos afuera de la ejecucion del TADsPec
      resultado_final = self.preparar_resultado(resultado_test, resultado_final)
    }

    return resultado_final
  end



  # Tipo 2: Corre todos los tests de la Suite
  def self.testear_suite(clase_suite)
    puts "Corriendo Tests de Suite '#{clase_suite.to_s}'"

    if not es_suite_testing clase_suite
      raise "No es una Suite de Testing"
    end

    if not self.tiene_tests clase_suite
      raise "Esta Suite no tiene Tests"
    end

    tests = self.obtener_tests(clase_suite)
    resultado_final = self.testear_algunos_tests(clase_suite, tests)
    return resultado_final
  end



  # Tipo 1: Corre todos los tests de todas las suites en contexto
  def self.testear_contexto
    simbolos_del_contexto = Object.constants.map{ |symbol| Object.const_get symbol }

    # Filtro Simbolos que son clases
    clases_del_contexto = simbolos_del_contexto.select{ |elem| elem.is_a? Class }

    # Filtro Clases Suite de Test
    suites_del_contexto = clases_del_contexto.select { |clase| es_suite_testing(clase) }

    resultado_final = EJECUCION_CORRECTA    # Inicializo

    # Testeo Suites
    suites_del_contexto.each { |suite|
      resultado_suite = self.testear_suite(suite)
      resultado_final = self.preparar_resultado(resultado_suite, resultado_final)
    }
    return resultado_final
  end



  # Metodo para Correr tests
  # NOTA: Diferencio la "Sobrecarga del metodo" por los argumentos, ya que Ruby no tiene Sobrecarga como los lenguajes Estaticos
  def self.testear(*args)
    # Inyecto metodo Deberia en clase Object
    # TODO

    # Primer argumento es la Suite, Los Demas son symbols a los metodos (son parte del nombre del metodo)

    if args.size == 0
      # Tipo 1: Corre todos los tests de todas las suites en contexto
      resultado_final = self.testear_contexto

    elsif args.size == 1
      # Tipo 2: Corre todos los tests de la Suite
      resultado_final = self.testear_suite(args[0])

    else
      # Tipo 3: Corre Solo los tests indicados de la Suite

      # Quito primer argumento de la coleccion antes de pasarla al metodo (le quito la suite)
      resultado_final = self.testear_algunos_tests(args[0], args.delete_at(0) )
    end

    return resultado_final
  end

end

