require "rspec"
require_relative "../SRC/TADsPec"

describe 'Framework de Testing' do
  # Suite para probar
  class SuiteDePrueba

    def testear_que_funciona
      # Si no funciona esto, estamos prendido fuegos
      true.deberia ser true
    end

  end

  class ClaseNoSuite

    def saludar
      'Hello World'
    end

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

end