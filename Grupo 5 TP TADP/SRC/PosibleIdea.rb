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
    if argumento.is_a? Proc
      # FIXME Borrar Puts para debug
      puts "Es un bloque"
      return proc { |param|  true.equal?(argumento.call param) }
    else
      puts "No es un bloque, es un Objeto"
      return proc  {|param|  param.equal? argumento }
    end

  end


  #TODO FIXEAR MENSAJE TENER
  def self.tener(argumento, val)
    puts argumento.class
    proc {self.(argumento).equal? val }
  end



  # TODO Hay que Decidir pasarlo a Object o Class o donde sea
  def deberia(proc)
    #FIXME Put para Debug, iria sin los Put
    puts proc.call self
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
    else if symbol.to_s[0..5] == "tener_"        #AGREGO LOCURA PARA EL tener_, hay que mejorar la anidacion de ifs?
      mensaje = '@' + symbol.to_s[6..(symbol.to_s.length-1)] + '?'
      return proc {|var| var.send(mensaje.to_sym)}
         end
    super
    end
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

  # Explora porque comparamos 2 cosas distintas
  # mitest.deberia ser mayor_a 6

  # Da True
  mitest.deberia ser mitest

  # Da False
  mitest.deberia ser 9

  # Da True
  mitest.deberia entender :deberia

  mitest.deberia tener_hola 2
end

