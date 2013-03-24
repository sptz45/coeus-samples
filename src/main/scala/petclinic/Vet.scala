package petclinic

import java.util.{Set, HashSet}
import scala.collection.JavaConversions._

/**
 * Simple JavaBean domain object representing a veterinarian.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Arjen Poutsma
 */
class Vet extends Person {

  var specialties: Set[Specialty] =  new HashSet
  
  def getSpecialties = specialties.toList.sortBy(_.name)
  
  def hasNoSpecialties = specialties.isEmpty

  def addSpecialty(specialty: Specialty) {
    specialties.add(specialty)
  }
  
  def toXml = {
    <vet>
      <id>{id}</id>
      <firstName>{firstName}</firstName>
      <lastName>{lastName}</lastName>
      <specialties>
      { specialties.map(s => <specialty>s.name</specialty>) }
      </specialties>
    </vet>
  }
}

