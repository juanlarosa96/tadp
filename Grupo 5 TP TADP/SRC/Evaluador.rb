require_relative "../SRC/FalloTest"

class Evaluador

  attr_accessor :proc_comparador, :fallo

  def initialize(comparador, fallo)
    self.proc_comparador=comparador
    self.fallo=fallo
  end

  def self.mayor_a(parametro)
    new(proc { |var| var > parametro }, FalloTest.new("El objeto no es mayor a #{parametro}"))
  end

  def self.menor_a(parametro)
    new(proc { |var| var < parametro }, FalloTest.new("El objeto no es menor a #{parametro}"))
  end

  def self.uno_de_estos(*parametro)
    if parametro[0].is_a? Array
    new(proc { |var|

        parametro[0].include? var }, FalloTest.new("El objeto no es un elemento de la lista "))
    else
      new(proc { |var|
        parametro.include? var }, FalloTest.new("El objeto no es un elemento de la lista "))
      end
  end

  def self.ser_(mensaje)
    new(proc { |var| begin var.send(mensaje.to_sym)
    rescue NoMethodError
      raise FalloTest.new("El objeto no entiene el mensaje #{mensaje}")
    end
    true
      }, FalloTest.new("El mensaje #{mensaje} no retorna True"))
  end

  def self.entender(parametro)
    new(proc { |var| var.respond_to? parametro }, FalloTest.new("El objeto no entiende el mensaje #{parametro}"))
  end

  def self.explotar_con(parametro)
    new(proc { |bloque|
      begin
        bloque.call
        raise FalloTest.new("Se esperaba Excepcion '#{parametro.to_s}' pero no sucedio")

      rescue Exception => e
        if not(e.is_a? parametro)
          raise FalloTest.new("Se esperaba Excepcion '#{parametro.to_s}' pero se produjo Excepcion #{e.class.to_s}")
        end
      end
      true
    }, nil)
  end

  def call(contexto)
    raise fallo unless proc_comparador.call contexto
  end

end