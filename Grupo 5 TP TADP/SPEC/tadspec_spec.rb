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

  class ClaseNoSuite

    def saludar
      'Hello World'
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

  def mockear(nombre_del_metodo, &block)
    self.class.class_eval do
      alias_method "mock_#{nombre_del_metodo}".to_sym, nombre_del_metodo.to_sym
    end
    self.define_singleton_method(nombre_del_metodo.to_sym) { block.call }
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
    expect(TADsPec.testear_contexto).to eq(EJECUCION_CORRECTA)
  end

  it 'asdadadsdaddads' do
    expect { TADsPec.testear SuiteDePrueba, :testear_que_funciona_el_tener}.to_not raise_error
  end

end