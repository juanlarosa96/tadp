package domain

import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import domain.Persona

class PersonaTest {
  var persona: Persona = _

  @Before
  def setup() = {
    persona = new Persona(1)
  }

  @Test
  def cumpliAnio_test() = {
    persona.cumpliAnio()
    assertEquals(2, persona.edad)
  }

  @Test
  def cumpli2Anios() = {
    persona.cumpliAnio()
    persona.cumpliAnio()
    assertEquals(3, persona.edad)
  }
}