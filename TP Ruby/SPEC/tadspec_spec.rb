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
      leandro. edad. deberia ser uno_de_estos 7, 22, "hola"
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
      pato  = Persona.new(23)
      pato  = espiar(pato)
      pato.viejo?
      pato.edad=20
      pato.edad=21
      pato.deberia haber_recibido(:edad)
      pato.deberia haber_recibido(:viejo?).con_argumentos
      pato.deberia haber_recibido(:edad=).con_argumentos(20)
      pato.deberia haber_recibido(:edad=).veces(2)
    end

    def testear_que_funcione_explotar
      leandro = Persona.new(22)
      proc { 7/0 }.deberia explotar_con  ZeroDivisionError
      proc { leandro.nombre }.deberia explotar_con  NoMethodError
      proc { leandro.nombre}.deberia explotar_con StandardError
    end

    def testear_que_funcione_mockear
      ClaseNoSuite.mockear(:numerito) do 7 end
      ClaseNoSuite.new.numerito.deberia ser 7
    end

    def testear_que_desmockeo
      ClaseNoSuite.new.numerito.deberia ser 8
    end

  end

  class SuiteDePruebaQueFalla

    def testear_que_test_explota
      raise "ERROR"
    end

    def testear_que_test_falla
      1.deberia ser 2
    end

    def testear_que_falla_veces_espiar
      pato  = Persona.new(23)
      pato  = espiar(pato)
      pato.edad=20
      pato.edad=21
      pato.deberia haber_recibido(:edad)

      pato.deberia haber_recibido(:edad=).veces(3)
    end

    def testear_que_falla_con_argumentos_espiar
      pato  = Persona.new(23)
      pato  = espiar(pato)
      pato.edad=20
      pato.edad=21
      pato.deberia haber_recibido(:edad=).con_argumentos(22)

    end

    def testear_que_falla_explotar
      proc { 7/0 }.deberia explotar_con  NoMethodError
    end

    def testear_que_falla_entender
      leandro = Persona.new(22)
      leandro.deberia entender :metodo_inexistente

    end

    def testear_que_falla_haber_recibido_cuando_objeto_no_es_espiado
      leandro = Persona.new(22)
      leandro.edad
      leandro.deberia haber_recibido(:edad)
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

  class PersonaMock

    def comer
      "mmm..."
    end

    def caminar
      "falta mucho"
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

  it 'Ejecute Bien la Suite que Funciona' do
    expect(TADsPec.testear SuiteDePrueba).to eq(EJECUCION_CORRECTA)
  end

  it 'Ejecute y Explote la Suite que Explota' do
    expect(TADsPec.testear SuiteDePruebaQueFalla).to eq(EJECUCION_EXPLOTO)
  end

  it 'Ejecute Bien al Correr Todas las Suites del Contexto' do
    expect{TADsPec.testear}.to_not raise_error
  end

  it 'Ejecute Bien un test especifico de una Suite' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funciona_el_tener).to eq(EJECUCION_CORRECTA)
  end

  it 'Ejecute Bien una lista de test de una Suite' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funciona_el_tener, :testear_que_funciona).to eq(EJECUCION_CORRECTA)
  end

  it 'Ejecuta un test y muestra que Exploto' do
    expect(TADsPec.testear SuiteDePruebaQueFalla, :testear_que_test_explota ).to eq(EJECUCION_EXPLOTO)
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


  it 'Mockeo una clase y se ejecuta el bloque asignado al mock' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funcione_mockear).to eq(EJECUCION_CORRECTA)

  end


  it 'Mockeo una clase y devuelve el resultado mockeado, la desmockeo y vuelve a la normalidad' do
    TADsPec.inyectar_metodos # Para que inyecte metodo mockear
    ClaseNoSuite.mockear(:saludar) do 'Metodo mockeado' end
    expect( ClaseNoSuite.new.saludar ).to eq('Metodo mockeado')
    ClaseNoSuite.desmockear
    expect( ClaseNoSuite.new.saludar ).to eq('Hello World')
  end

  it 'Espio un objeto y se registran correctamente los metodos llamados' do
    expect(TADsPec.testear SuiteDePrueba, :testear_que_funcione_espiar).to eq(EJECUCION_CORRECTA)
  end


end