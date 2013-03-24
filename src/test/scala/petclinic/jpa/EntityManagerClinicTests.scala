package petclinic.jpa

import org.springframework.beans.factory.annotation.Autowired
import petclinic.aspects.UsageLogAspect
import org.junit.Test
import org.junit.Assert._

/**
 * <p>
 * Tests for the DAO variant based on the shared EntityManager approach. Uses
 * TopLink Essentials (the reference implementation) for testing.
 * </p>
 * <p>
 * Specifically tests usage of an <code>orm.xml</code> file, loaded by the
 * persistence provider through the Spring-provided persistence unit root URL.
 * </p>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
abstract class EntityManagerClinicTests extends AbstractJpaClinicTests {

  @Autowired
  var usageLogAspect: UsageLogAspect = _

  @Test
  def testUsageLogAspectIsInvoked() {
    val name1 = "Schuurman"
    val name2 = "Greenwood"
    val name3 = "Leau"

    assertTrue(clinic.findOwners(name1).isEmpty)
    assertTrue(clinic.findOwners(name2).isEmpty)

    val namesRequested = usageLogAspect.getNamesRequested
    assertTrue(namesRequested.contains(name1))
    assertTrue(namesRequested.contains(name2))
    assertFalse(namesRequested.contains(name3))
  }

}