package petclinic

import org.springframework.core.style.ToStringCreator
import java.util.{Set, HashSet}
import scala.collection.JavaConversions._

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
class Owner extends Person {

  var address: String = _
  var city: String = _
  var telephone: String = _
  
  var pets: Set[Pet] = new HashSet

  def petList = pets.toList.sortBy(_.name)

  def addPet(pet: Pet) {
    pets.add(pet)
    pet.owner = this
  }

  /**
   * Return the Pet with the given name, or null if none found for this Owner.
   *
   * @param name to test
   */
  def getPet(name: String): Pet = pets.find(_.name equalsIgnoreCase name).getOrElse(null)

  override def toString = new ToStringCreator(this)
    .append("id", id)
    .append("new", isNew)
    .append("lastName", lastName)
    .append("firstName", firstName)
    .append("address", address)
    .append("city", city)
    .append("telephone", telephone)
    .toString
}