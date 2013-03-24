package petclinic

import java.util.{Set, HashSet, Date}
import scala.collection.JavaConversions._

/**
 * Simple JavaBean business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
class Pet extends NamedEntity {

  var birthDate: Date = _

  var `type`: PetType = _

  var owner: Owner = _

  var visits: Set[Visit] = new HashSet

  def visitsHistory = visits.toList.sortWith((v1, v2) => v1.date after v2.date)

  def addVisit(visit: Visit) {
    visits.add(visit)
    visit.pet = this
  }
}