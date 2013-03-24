package petclinic
package web

import com.tzavellas.coeus.mvc._
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import validation.PetValidator

@Controller
@Path("/owners/{ownerId}/pets/new")
class AddPetForm @Autowired() (val clinic: Clinic) extends FormController with PetFormData {
  
  @Get
  def setupForm() = {
    val owner = clinic.loadOwner(path("ownerId"))
    val pet = new Pet
    owner.addPet(pet)
    model += pet
    formView
  }
  
  @Post
  def processSubmit() = ifValid { pet: Pet =>
    clinic.storePet(pet)
    redirect("/owners/" + pet.owner.id)
  }
  
  override val formView = render("pets/form")
}