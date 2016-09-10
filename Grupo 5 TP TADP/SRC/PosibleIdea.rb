require_relative "../SRC/FalloTest"

class MiSuite

  def initialize()
    @hola = 2
    @jaja = 1
  end

  # TODO Iria a Mixin de Suites
  def self.ser(argumento)
    # Tiene varios Puts para Debug y Pruebas
    puts
    puts argumento.class

    # Discrimino Comportamiento segun Sea un Proc o un Objeto
    if argumento.respond_to? :call
      # FIXME Borrar Puts para debug
      puts "Es un bloque"
      return proc   { |param|   resultado = argumento.call param
                                raise FalloTest.new("Se esperaba 'True' pero llego '#{resultado.to_s}'") unless true.equal?(resultado)
                    }
    else
      puts "No es un bloque, es un Objeto"
      return proc   {|param|
                              raise FalloTest.new("Se esperaba '#{param.to_s}' pero llego '#{argumento.to_s}'") unless param.equal? argumento
                    }
    end

  end

  # TODO Hay que Decidir pasarlo a Object o Class o donde sea
  def deberia(proc)
    resultado = proc.call self

    #FIXME Put para Debug, iria sin los Put
    puts resultado
    puts puts
  end

  # TODO Iria a Mixin de Suites
  def self.mayor_a(parametro)
    return proc { |var|  var > parametro }
  end

  def self.menor_a(parametro)
    return proc { |var|  var < parametro }
  end

  def self.uno_de_estos(lista)
    return proc { |var| lista.include? var }
  end

  def method_missing(symbol, *args)
    if symbol.to_s[0..3] == "ser_"
      mensaje = symbol.to_s[4..(symbol.to_s.length-1)] + "?"
      return proc {|var| var.send(mensaje.to_sym)}
    end
    super
  end

  # TODO Iria a Mixin de Suites
  def self.entender(symbol)
    return proc { |var|  var.respond_to? symbol }
  end

  def self.explotar_con(exception)
    return proc {|bloque|
      begin
        bloque.call
        return false

      rescue Exception => e
        return e.is_a? exception
      end
    }
  end

  #Ejemplo Codigo para Correr
  mitest = MiSuite.new

  # Explota porque comparamos 2 cosas distintas
  # mitest.deberia ser mayor_a 6

  # Da True
  mitest.deberia ser mitest

  # Da True
  mitest.deberia entender :deberia

  # Da False (Excepcion)
  mitest.deberia ser proc { false }

  # Da False (Excepcion)
  mitest.deberia ser 9

end