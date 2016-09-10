require_relative "../SRC/NuestraSuite"

class TADsPec

  def self.es_suite_testing(clase)
    return clase.is_a? Nuestra  Suite
  end


  def self.obtener_tests(clase)
    # Obtengo todos los metodos de instancia (que definieron en la misma clase, serian nuestros posibles tests)
    metodos = clase.methods

    # Devuelvo solo los metodos que contienen "testear_que_", los demas los ignoro
    return metodos.select { |str| str.to_s.include? "testear_que_"}
  end


  def self.tiene_tests(clase)
    return self.obtener_tests(clase) > 0
  end


  # Tipo 1: Corre todos los tests de todas las suites en contexto
  def self.testear_contexto
    # TODO Pendiente
  end

  # Tipo 2: Corre todos los tests de la Suite
  def self.testear_suite(clase_suite)
    if not es_suite_testing clase_suite
      raise "No es una Suite de Testing"
    end

    if not self.tiene_tests clase_suite
      raise "Esta Suite no tiene Tests"
    end

    tests = self.obtener_tests(clase_suite)

    # Como tenemos la Clase de Suite, tenemos que crear una instancia para poder pedirle que ejecute los metodos
    instancia_suite = clase_suite.new

    puts "Corriendo Tests de Suite '#{clase_suite}'"
    # Recorremos todos los Tests y vamos llamandolos, con las Excepciones controlamos la Ejecucion de estos
    tests.each { |un_test|
      begin
        instancia_suite.send un_test
        puts "  Ejecuto Bien '#{un_test.to_s}'"

        # TODO Pendiente cuando Falla un Test sin Explotar, se controlaria con una excepcion nuestra
      rescue Exception => e
        puts "  Exploto Test '#{un_test.to_s}'"
        puts e.message
        puts e.backtrace.inspect
      end
    }
  end

  # Tipo 3: Corre Solo los tests indicados de la Suite
  def self.testear_algunos_tests(suite, tests)
    # TODO Pendiente
  end


  # Metodo para Correr tests
  # NOTA: Diferencio la "Sobrecarga del metodo" por los argumentos, ya que Ruby no tiene Sobrecarga como los lenguajes Estaticos
  def self.testear(*args)
    # Primer argumento es la Suite, Los Demas son symbols a los metodos (son parte del nombre del metodo)

    if (args[0].is_a? NilClass) and (args[1].is_a? NilClass)
      # Tipo 1: Corre todos los tests de todas las suites en contexto
      self.testear_contexto

    elsif args[1].is_a? NilClass
      # Tipo 2: Corre todos los tests de la Suite
      self.testear_suite(args[0])

    else
      # Tipo 3: Corre Solo los tests indicados de la Suite

      # TODO, quitar primer argumento de la coleccion antes de pasarla al metodo
      #  self.testear_algunos_tests((args[0], tests)
    end
  end



  # Debe Explotar porque puse cualquier cosa
  TADsPec.testear "String"


end