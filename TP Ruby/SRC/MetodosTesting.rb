require_relative "../SRC/Haber_Recibido"
require_relative "../SRC/Espia"
require_relative "../SRC/Evaluador"

module MetodosTesting

  def ser(argumento)

    # Discrimino Comportamiento segun Sea un Proc o un Objeto
    if argumento.respond_to? :call
      return argumento
    else
      return proc { |param|
        raise FalloTest.new("Se esperaba '#{param.to_s}' pero llego '#{argumento.to_s}'") unless param.equal? argumento
      }
    end

  end

  def mayor_a(parametro)
    return Evaluador.mayor_a(parametro)
  end

  def menor_a(parametro)
    return Evaluador.menor_a(parametro)
  end

  def uno_de_estos(*parametro)
    return Evaluador.uno_de_estos(*parametro)
  end

  def method_missing(symbol, *args)
    if symbol.to_s[0..3] == "ser_"
      mensaje = symbol.to_s[4..(symbol.to_s.length-1)] + "?"
      return Evaluador.ser_(mensaje)

      elsif symbol.to_s[0..5] == "tener_"
       mensaje = symbol.to_s[6..(symbol.to_s.length-1)].to_sym
       return proc { |var| var.instance_variable_get("@#{mensaje}".to_sym).deberia ser args[0] }
    end

    super(symbol, *args)
  end

  def entender(symbol)
    return Evaluador.entender(symbol)
  end

  def explotar_con(exception)
    return Evaluador.explotar_con(exception)
  end


  def haber_recibido(symbol)
    return Haber_Recibido.new(symbol)
  end

  def espiar(objeto)
    return Espiador.new(objeto)
  end
end

