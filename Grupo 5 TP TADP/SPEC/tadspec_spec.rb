require "rspec"
require_relative "../SRC/TADsPec"
require_relative "../SRC/Espia"

describe 'Framework de Testing' do
  # Suite para probar

  class Persona
    attr_accessor  :edad, :onda


    def initialize(edad_persona)
      self.edad = edad_persona
      self.onda = true
    end

    def viejo?
      self. edad  > 29
    end
  end

  class SuiteDePrueba

    def testear_que_funciona
      # Si no funciona esto, estamos prendido fuegos
      true.deberia ser true
    end

    def testear_que_funcionan_aserciones
      7.deberia ser  7
      leandro = Persona.new(22)
      leandro. edad. deberia ser mayor_a  20
      leandro. edad. deberia ser menor_a  25
      leandro. edad. deberia ser uno_de_estos  [ 7, 22, "hola"]
    end

    def testear_que_funciona_ser_guion
      nico = Persona.new(30)
      nico.deberia ser_viejo
      nico.viejo?.deberia ser  true
    end

    def testear_que_funciona_entender
      leandro = Persona.new(22)
      leandro. deberia entender  :viejo?
      leandro. deberia entender :class
    end


    def testear_que_funciona_el_tener
    javee = Persona.new(20)
    javee.deberia tener_onda true
    end

    def testear_que_funcione_espiar
      pato  = Persona. new(23)
      pato  = espiar(pato)
      pato.viejo?
      pato.deberia haber_recibido(:edad)
      pato.deberia haber_recibido(:viejo?).con_argumentos
    end

    def testear_que_funcione_explotar
      leandro = Persona.new(22)
      proc { 7/0 }.deberia explotar_con  ZeroDivisionError
      proc { leandro.nombre }.deberia explotar_con  NoMethodError
      proc { leandro.nombre}.deberia explotar_con Error
    end

    def testear_que_funcione_mockear
      ClaseNoSuite.mockear(:numerito) do 7 end
      ClaseNoSuite.new.numerito.deberia ser 7
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

    def numerito
      8
    end

  end


  it 'Rspec Funciona Bien' do
    expect(true).to eq(true)
  end

  it 'Reconozca Suites' do
    expect(TADsPec.es_suite_testing SuiteDePrueba).to eq(true)
  end

  it 'Reconozca Clases No Suites' do
    expect(TADsPec.es_suite_testing ClaseNoSuite).to eq(false)
  end

  it 'Ejecute Bien la Suite Entera' do
    expect(TADsPec.testear SuiteDePrueba).to eq(EJECUCION_CORRECTA)
  end

  it 'Ejecute Bien al Correr Todas las Suites del Contexto' do
    expect(TADsPec.testear).to eq(EJECUCION_EXPLOTO)
  end

  it 'Ejecute Bien un test especifico de una Suite' do
    expect { TADsPec.testear SuiteDePrueba, :testear_que_funciona_el_tener}.to_not raise_error
  end

  it 'Ejecute Bien una lista de test de una Suite' do
    expect { TADsPec.testear SuiteDePrueba, :testear_que_funciona_el_tener, :testear_que_funciona }.to_not raise_error
  end

  it 'Ejecuta un test y muestra que Exploto' do
    expect( TADsPec.testear SuiteDePruebaQueFalla, :testear_que_test_explota ).to eq(EJECUCION_EXPLOTO)
  end

  it 'Ejecuta un test y muestra que Fallo' do
    expect( TADsPec.testear SuiteDePruebaQueFalla, :testear_que_test_falla ).to eq(EJECUCION_FALLIDA)
  end

  it 'Funcionan Aserciones' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funcionan_aserciones).to eq(EJECUCION_CORRECTA)
  end

  it 'Funciona Ser_' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funciona_ser_guion).to eq(EJECUCION_CORRECTA)
  end

  it 'Funciona Entender' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funciona_entender).to eq(EJECUCION_CORRECTA)
  end

  it 'Funciona Mockear' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funcione_mockear).to eq(EJECUCION_CORRECTA)
  end

  it 'Mockeo una clase y devuelve el resultado mockeado, la desmockeo y vuelve a la normalidad' do
    TADsPec.inyectarMetodos # Para que inyecte metodo mockear
    ClaseNoSuite.mockear(:saludar) do 'Metodo mockeado' end
    expect( ClaseNoSuite.new.saludar ).to eq('Metodo mockeado')
    ClaseNoSuite.desmockear
    expect( ClaseNoSuite.new.saludar ).to eq('Hello World')
  end

#  it 'Desmockeo una clase y vuelve al estado original' do
#    TADsPec.inyectarMetodos # Para que inyecte metodo mockear
#    ClaseNoSuite.mockear(:saludar) do 'Metodo mockeado' end
#    ClaseNoSuite.desmockear
#    expect( ClaseNoSuite.new.saludar ).to eq('Hello World')
#  end

  it 'espiar' do
    persona = Persona.new(20)
    persona.extend PersonaMock
    persona.comer
    puts("HOLA")
  end

  it 'pruebo espia' do
    persona = Espiador.new(PersonaMock.new)
    persona.sumar(1,2)
    puts("Metodos llamados: #{persona.llamadasAMetodos}")
  end

  it 'Funcione Espiar' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funcione_espiar).to eq(EJECUCION_CORRECTA)
  end

end