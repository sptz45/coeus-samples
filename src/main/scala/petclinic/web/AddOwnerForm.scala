package petclinic
package web

import com.tzavellas.coeus.mvc._
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import validation.OwnerValidator

@Controller
@Path("/owners/new")
class AddOwnerForm @Autowired() (clinic: Clinic) extends FormController {

  override def storeModelInSession = false
  
  @Get
  def setupForm() = {
    model += new Owner
    formView
  }
  
  @Post
  def processSubmit() = ifValid(new Owner) { owner =>
    clinic.storeOwner(owner)
    redirect("owners/" + owner.id)
  }
  
  override val formView = render("owners/form")
}