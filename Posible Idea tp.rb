class MiSuite

	# Iria a Mixin de Suites
	def self.ser(argumento)
		# Tiene varios Puts para Debug y Pruebas
		puts
		puts argumento.class
 
		# Discrimino Comportamiento segun Sea un Proc o un Objeto
		if(argumento.is_a? Proc)
			  puts "Es un bloque"
			  return proc { |param|  true.equal?(argumento.call param) }
		else
			  puts "No es un bloque, es un Objeto"
			  return proc  {|param|  param.equal? argumento }
		end
		
	end

  def self.no_ser(argumento)
		if(argumento.is_a? Proc)
			puts "Es un bloque"
			return proc { |param|  (!(argumento.call param)) }
		else
			puts "No es un bloque, es un Objeto"
			return proc  {|param|  !(param.equal? argumento) }
		end
	end

	# Hay que Decidir pasarlo a Object o Class o donde sea
	def deberia(proc)
		# Put para Debug, iria sin el Put
		puts proc.call self
	end

	# Iria a Mixin de Suites
	def self.mayor_a(parametro)
		return proc { |var|  var > parametro }
  end

  def self.menor_a(parametro)
    return proc { |var|  var < parametro }
  end

  def self.uno_de_estos(lista)
    return proc { |var| lista.include? var }
  end

	#Ejemplo Codigo para Correr
	mitest = MiSuite.new

	# Explora porque comparamos 2 cosas distintas
	# mitest.deberia ser mayor_a 6
	
	# Da True
	mitest.deberia ser mitest
	
	# Da False
	mitest.deberia ser 9

end