package petclinic

import java.util.Date

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 */
class Visit extends BaseEntity {

  var date: Date = _
  var description: String = _
  var pet: Pet = _
}