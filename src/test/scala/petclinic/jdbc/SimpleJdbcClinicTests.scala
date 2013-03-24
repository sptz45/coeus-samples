package petclinic
package jdbc

import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration

/**
 * Integration tests for the {@link SimpleJdbcClinic} implementation.
 * 
 * <p>"SimpleJdbcClinicTests-context.xml" determines the actual beans to
 * test.</p>
 *
 * @author Thomas Risberg
 */
@DirtiesContext
@ContextConfiguration
class SimpleJdbcClinicTests extends AbstractClinicTests