require_relative "../SRC/FalloTest"

class Haber_Recibido
  attr_accessor :mensaje, :cantidad_veces, :argumentos

  def initialize(symbol)
    @mensaje = symbol
    @cantidad_veces = 1
    @argumentos = []    # Array Vacio
  end

  def veces(cantidad)
    @cantidad_veces = cantidad
  end

  def con_argumentos(*args)
    @argumentos = args
  end

  def call(contexto)
    # TODO obtener del contexto los metodos que recibio, cuantas veces y con que argumentos

    if self.argumentos.size > 0
      # Verifico si el metodo fue llamado con esos argumentos
      # TODO

      raise FalloTest.new("El metodo '#{symbol.to_s}' nunca fue llamado con los argumentos '#{argumentos}'") unless true
    else
      # Verifico cantidad veces que fue llamado el metodo
      # TODO

      raise FalloTest.new("El metodo '#{symbol.to_s}' no fue llamado '#{cantidad_veces}' veces") unless true
    end
  end

end