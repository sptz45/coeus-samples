package petclinic
package web

import com.tzavellas.coeus.mvc._
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import validation.PetValidator

@Controller
@Path("/owners/*/pets/{petId}/edit")
class EditPetForm @Autowired() (val clinic: Clinic) extends FormController with PetFormData {

  @Get
  def setupForm() = {
    model("pet") = clinic.loadPet(path("petId"))
    formView
  }

  @Put
  def processSubmit() = ifValid { pet: Pet =>
    clinic.storePet(pet)
    redirect("/owners/" + pet.owner.id)
  }

  @Delete
  def deletePet() = {
    val pet = clinic.loadPet(path("petId"))
    clinic.deletePet(pet.id)
    redirect("/owners/" + pet.owner.id)
  }
  
  override val formView = render("pets/form")
}