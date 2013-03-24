package petclinic.jdbc

/**
 * Interface that defines a cache refresh operation.
 * To be exposed for management via JMX.
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @see SimpleJdbcClinic
 */
trait SimpleJdbcClinicMBean {

  /**
   * Refresh the cache of Vets that the Clinic is holding.
   * @see org.springframework.samples.petclinic.Clinic#getVets()
   * @see SimpleJdbcClinic#refreshVetsCache()
   */
  def refreshVetsCache()

}