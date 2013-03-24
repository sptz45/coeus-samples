package petclinic
package hibernate

import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration

/**
 * Integration tests for the {@link HibernateClinic} implementation.
 * 
 * <p>"HibernateClinicTests-context.xml" determines the actual beans to test.</p>
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@DirtiesContext
@ContextConfiguration
class HibernateClinicTests extends AbstractClinicTests