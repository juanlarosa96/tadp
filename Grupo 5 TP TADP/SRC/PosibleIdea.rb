require_relative "../SRC/FalloTest"

class MiSuite

  def initialize()
    @hola = 2
    @jaja = 1
  end

  # TODO Hay que Decidir pasarlo a Object o Class o donde sea
  def deberia(proc)
    resultado = proc.call self
  end

end