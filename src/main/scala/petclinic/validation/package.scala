package petclinic

import scala.collection.JavaConversions._
import com.tzavellas.coeus.bind.BindingResult
import com.tzavellas.coeus.validation.vspec.DefaultVSpec

package object validation {

  implicit object OwnerValidator extends DefaultVSpec[Owner] {
    ensure('firstName, hasText)
    ensure('lastName,  hasText)
    ensure('address,   hasText)
    ensure('city,      hasText)
    ensure('telephone, hasText, satisfies { t: String => t.forall(_.isDigit) })
  }

  implicit object PetValidator extends DefaultVSpec[Pet] {
    ensure("name", hasText)

    override def extraValidation(result: BindingResult[Pet]) {
      val pet = result.target
      if (pet.owner.pets.exists(p => p.id != pet.id && p.name.equalsIgnoreCase(pet.name)))
        result.addError("name", "duplicate")
    }
  }

  implicit object VisitValidator extends DefaultVSpec[Visit] {
    ensure('description, hasText)
  }
}
