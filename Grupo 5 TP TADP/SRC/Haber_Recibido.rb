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
    self
  end

  def con_argumentos(*args)
    @argumentos = args
    self
  end

  def call(contexto)

    raise StandardError.new("El objeto no fue espiado") unless contexto.respond_to?(:llamadas_a_metodos_espiados)
    llamadas = contexto.llamadas_a_metodos_espiados

    if self.argumentos.size > 0
      # Verifico si el metodo fue llamado con esos argumentos
      llamadas = llamadas.select do |llamada|  llamada[0].equal?(@mensaje) and llamada[1..-1].eql?(@argumentos)
      end
      raise FalloTest.new("El metodo '#{@mensaje.to_s}' nunca fue llamado con los argumentos '#{argumentos}'") unless llamadas.length > 0

    else
      # Verifico cantidad veces que fue llamado el metodo
      llamadas = llamadas.select do |llamada|  llamada[0].equal?(@mensaje) end
      raise FalloTest.new("El metodo '#{@mensaje.to_s}' no fue llamado '#{cantidad_veces}' veces") unless llamadas.length >= @cantidad_veces
    end
  end

end