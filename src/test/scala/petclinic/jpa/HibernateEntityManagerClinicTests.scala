package petclinic.jpa

import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration

/**
 * <p>
 * Tests for the DAO variant based on the shared EntityManager approach, using
 * Hibernate EntityManager for testing instead of the reference implementation.
 * </p>
 * <p>
 * Specifically tests usage of an <code>orm.xml</code> file, loaded by the
 * persistence provider through the Spring-provided persistence unit root URL.
 * </p>
 *
 * @author Juergen Hoeller
 */
@DirtiesContext
@ContextConfiguration(locations = Array(
		"applicationContext-jpaCommon.xml",
		"applicationContext-hibernateAdapter.xml",
		"applicationContext-entityManager.xml"))
class HibernateEntityManagerClinicTests extends EntityManagerClinicTests