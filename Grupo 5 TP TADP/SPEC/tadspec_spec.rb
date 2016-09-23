require "rspec"
require_relative "../SRC/TADsPec"

describe 'Framework de Testing' do
  # Suite para probar

  class Persona
    @onda = true
  end

  class SuiteDePrueba

    def testear_que_funciona
      # Si no funciona esto, estamos prendido fuegos
      true.deberia ser true
    end


    def testear_que_funciona_el_tener
    javee = Persona.new
    javee.tener_onda true
    end

  end

  class SuiteDePruebaQueFalla

    def testear_que_test_explota
      raise "ERROR"
    end

    def testear_que_test_falla
      1.deberia ser 2
    end

  end


  class ClaseNoSuite

    def saludar
      'Hello World'
    end

  end



  module PersonaMock

    def comer
      puts "mmm..."
    end

    def caminar
      "falta mucho"
    end



  end

  it 'probando mock' do #testeamos y funciono, sacarlo de aca y dejarlo solo en TADsPec
    #Object.send( :define_method, symbol, block )
    Object.send( :define_method, :mockear) do |nombre_del_metodo, &block|
    self.class.class_eval do
      alias_method "mock_#{nombre_del_metodo}".to_sym, nombre_del_metodo.to_sym
    end
    self.define_singleton_method(nombre_del_metodo.to_sym) { block.call }
  end
    persona = PersonaMock.new
    puts(persona.caminar)
    persona.mockear(:caminar) do "454545454554" end
   # puts(persona.mock_caminar)
    puts(persona.caminar)

    expect(true).to eq(true)
  end

  it 'Rspec Funciona Bien' do
    expect(true).to eq(true)
  end


  it 'Deberia decir que si ya es una suite de test' do
    expect(TADsPec.es_suite_testing SuiteDePrueba).to eq(true)
  end

  it 'Deberia decir que no ya que no es una suite de test' do
    expect(TADsPec.es_suite_testing ClaseNoSuite).to eq(false)
  end

  it 'Deberia Ejecutar Bien la Suite Entera' do
    expect(TADsPec.testear SuiteDePrueba).to eq(EJECUCION_CORRECTA)
  end

  it 'Deberia Ejecutar Bien al Correr Todas las Suites del Contexto' do
    expect(TADsPec.testear).to eq(EJECUCION_EXPLOTO)
  end

  it 'Deberia ejecturar un test especifico de una Suite' do
    expect { TADsPec.testear SuiteDePrueba, :testear_que_funciona_el_tener}.to_not raise_error
  end

  it 'Deberia ejecturar una lista de test de una Suite' do
    expect { TADsPec.testear SuiteDePrueba, :testear_que_funciona_el_tener, :testear_que_funciona }.to_not raise_error
  end

  it 'Ejecuta un test y muestra que exploto' do
    expect( TADsPec.testear SuiteDePruebaQueFalla, :testear_que_test_explota ).to eq(EJECUCION_EXPLOTO)
  end

  it 'Ejecuta un test y muestra que fallo' do
    expect( TADsPec.testear SuiteDePruebaQueFalla, :testear_que_test_falla ).to eq(EJECUCION_FALLIDA)
  end

  it 'Mockeo una clase y devuelve el resultado mockeado' do
    TADsPec.inyectarMetodos # Para que inyecte metodo mockear
    ClaseNoSuite.mockear(:saludar) do 'Metodo mockeado' end
    expect( ClaseNoSuite.new.saludar ).to eq('Metodo mockeado')
  end

  it 'Desmockeo una clase y vuelve al estado original' do
    TADsPec.inyectarMetodos # Para que inyecte metodo mockear
    ClaseNoSuite.mockear(:saludar) do 'Metodo mockeado' end
    ClaseNoSuite.desmockear
    expect( ClaseNoSuite.new.saludar ).to eq('Hello World')
  end

  it 'espiar' do
    persona = Persona.new
    persona.extend PersonaMock
    persona.comer
    puts("HOLA")
  end

end