package petclinic

/**
 * Simple JavaBean domain object with an id property.
 * Used as a base class for objects needing this property.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
class BaseEntity {

  var id: Int = _
  
  def isNew = id == 0
}