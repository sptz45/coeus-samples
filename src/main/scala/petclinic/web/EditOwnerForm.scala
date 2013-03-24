package petclinic
package web

import com.tzavellas.coeus.mvc._
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import validation.OwnerValidator

@Controller
@Path("/owners/{ownerId}/edit")
class EditOwnerForm @Autowired() (clinic: Clinic) extends FormController { 
  
  @Get
  def setupForm() = {
    model += clinic.loadOwner(path("ownerId"))
    formView
  }
  
  @Put
  def processSubmit() = ifValid { owner: Owner =>
    clinic.storeOwner(owner)
    redirect("owners/" + owner.id)
  }
  
  override val formView = render("owners/form")
}