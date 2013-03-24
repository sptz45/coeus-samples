package petclinic

import org.junit.Test
import org.junit.Assert._

/**
 * JUnit test for the {@link Owner} class.
 *
 * @author Ken Krebs
 */
class OwnerTests {

  @Test
  def testHasPet() {
    val owner = new Owner
    val fido = new Pet
    fido.name = "Fido"
    assertNull(owner.getPet("Fido"))
    assertNull(owner.getPet("fido"))
    owner.addPet(fido)
    assertEquals(fido, owner.getPet("Fido"))
    assertEquals(fido, owner.getPet("fido"))
  }

}