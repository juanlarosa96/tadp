class FalloTest < StandardError
  def initialize(msg="El test no recibio lo esperado")
    super msg
  end
end
