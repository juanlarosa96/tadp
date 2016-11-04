/**
  * Created by javierz on 04/11/16.
  */
import domain.{FuenteDeEnergia, Ki}

import collection.mutable.Stack
import org.scalatest._

class ExampleSpec extends FlatSpec with Matchers {

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be (2)
    stack.pop() should be (1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }

  it should "probando algunas cosillas" in {
    val fe :FuenteDeEnergia = Ki(90)
    println("lee estoooo")
    println(fe.cant)
  }
}