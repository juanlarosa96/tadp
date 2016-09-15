require "rspec"
require_relative "../SRC/TADsPec"

describe 'Framework de Testing' do
  # Suite para probar
  class SuiteDePrueba

    def testear_que_funciona
      true
    end

  end

  class ClaseNoSuite

    def saludar
      'Hello World'
    end

  end

  it 'No Deberia dar ninguna Excepcion al Correr la Suite Entera' do
    expect { TADsPec.testear SuiteDePrueba }.to_not raise_error
  end

  it 'Deberia decir que si ya es una suite de test' do
    expect(TADsPec.es_suite_testing SuiteDePrueba).to eq(true)
  end

  it 'Deberia decir que no ya que no es una suite de test' do
    expect(TADsPec.es_suite_testing ClaseNoSuite).to eq(false)
  end

end