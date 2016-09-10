require "rspec"
require_relative "../SRC/TADsPec"

describe 'Framework de Testing' do
  # Suite para probar
  class SuiteDePrueba

    def testear_que_funciona
      true
    end

  end



  it 'No Deberia dar ninguna Excepcion al Correr la Suite Entera' do
    expect { TADsPec.testear SuiteDePrueba }.to_not raise_error
  end
end