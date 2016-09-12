module NuestraSuite
  # TODO Pendiente

  def ser(argumento)

    # Discrimino Comportamiento segun Sea un Proc o un Objeto
    if argumento.respond_to? :call
      return proc   { |param|   resultado = argumento.call param
      raise FalloTest.new("Se esperaba 'True' pero llego '#{resultado.to_s}'") unless true.equal?(resultado)
      }
    else
      return proc   {|param|
        raise FalloTest.new("Se esperaba '#{param.to_s}' pero llego '#{argumento.to_s}'") unless param.equal? argumento
      }
    end

  end

  def mayor_a(parametro)
    return proc { |var|  var > parametro }
  end

  def menor_a(parametro)
    return proc { |var|  var < parametro }
  end

  def uno_de_estos(lista)
    return proc { |var| lista.include? var }
  end

  def method_missing(symbol, *args)
    if symbol.to_s[0..3] == "ser_"
      mensaje = symbol.to_s[4..(symbol.to_s.length-1)] + "?"
      return proc {|var| var.send(mensaje.to_sym)}
    end
    super
  end

  def entender(symbol)
    return proc { |var|  var.respond_to? symbol }
  end

  def explotar_con(exception)
    return proc {|bloque|
      begin
        bloque.call
        return false
      rescue Exception => e
        return e.is_a? exception
      end
    }
  end
end

