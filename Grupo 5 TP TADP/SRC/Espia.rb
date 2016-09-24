class Espiador

  def self.new(*args)
    objeto = args[0]
    metodos = args[0].methods
    metodos.delete(:define_singleton_method)

    metodos.each { |metodo| objeto.define_singleton_method metodo do |*args|
         @llamadasAMetodos ||= []
         @llamadasAMetodos << [metodo].concat(args)
        #printf("Llamada a metodo #{metodo.to_s} con argumentos #{args}\n")
        super(*args)
      end
      }
    objeto.define_singleton_method :llamadasAMetodos do  return @llamadasAMetodos    end
    return objeto
    end
  end
