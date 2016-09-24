require_relative "../SRC/Haber_Recibido"
require_relative "../SRC/Espia"

module NuestraSuite

  def ser(argumento)

    # Discrimino Comportamiento segun Sea un Proc o un Objeto
    if argumento.respond_to? :call
      return proc { |param| resultado = argumento.call param
      raise FalloTest.new("Se esperaba 'True' pero llego '#{resultado.to_s}'") unless true.equal?(resultado)
      }
    else
      return proc { |param|
        raise FalloTest.new("Se esperaba '#{param.to_s}' pero llego '#{argumento.to_s}'") unless param.equal? argumento
      }
    end

  end

  def mayor_a(parametro)
    return proc { |var| var > parametro }
  end

  def menor_a(parametro)
    return proc { |var| var < parametro }
  end

  def uno_de_estos(lista)
    return proc { |var| lista.include? var }
  end

  def method_missing(symbol, *args)
    if symbol.to_s[0..3] == "ser_"
      mensaje = symbol.to_s[4..(symbol.to_s.length-1)] + "?"
      return proc { |var| var.send(mensaje.to_sym) }

      elsif symbol.to_s[0..5] == "tener_"
       mensaje = symbol.to_s[6..(symbol.to_s.length-1)].to_sym
       return proc { |var| var.instance_variable_get("@#{mensaje}".to_sym).deberia ser args[0] }
    end

    super(symbol, *args)
  end

  def entender(symbol)
    return proc { |var| raise FalloTest.new("El objeto no entiende el mensaje #{symbol}") unless var.respond_to? symbol }
  end

  def explotar_con(exception)
    return proc { |bloque|
      begin
        bloque.call
        raise FalloTest.new("Se esperaba Excepcion '#{exception.to_s}' pero no sucedio")

      rescue Exception => e
       if not(e.is_a? exception)
         raise FalloTest.new("Se esperaba Excepcion '#{exception.to_s}' pero se produjo Excepcion #{e.class.to_s}")
       end
      end
    }
  end


  def haber_recibido(symbol)
    return Haber_Recibido.new(symbol)
  end

  def espiar(objeto)
    return Espiador.new(objeto)
  end
end

