package petclinic
package web

import com.tzavellas.coeus.mvc._
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import validation.VisitValidator

@Controller
@Path("/owners/*/pets/{petId}/visits/new")
class AddVisitForm @Autowired() (clinic: Clinic) extends FormController {
  
  @Get
  def setupForm() = {
    val pet = clinic.loadPet(path("petId"))
    val visit = new Visit
    pet.addVisit(visit)
    model += visit
    formView
  }

  @Post
  def processSubmit() = ifValid { visit: Visit =>
    clinic.storeVisit(visit)
    redirect("/owners/" + visit.pet.owner.id)
  }
  
  override val formView = render("pets/visitForm")
}