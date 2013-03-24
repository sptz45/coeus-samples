package petclinic
package web

import com.tzavellas.coeus.mvc._
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired

@Controller
class FindOwnersForm @Autowired() (clinic: Clinic) extends FormController {
  
  override def storeModelInSession = false

  @Get("/owners/search")
  def setupForm() {
    model += new Owner
  }
  
  @Get("/owners")
  def processSubmit(): View = {
    
    val owner = new Owner
    val result = binder.bind(params, owner, request.locale)
    model.addBindingResult("owner", result)
    
    // allow parameterless GET request for /owners to return all records
    if (owner.lastName eq null) {
      owner.lastName = "" // empty string signifies broadest possible search
    }

    // find owners by last name
    val results = clinic.findOwners(owner.lastName)
    if (results.size < 1) {
      // no owners found
      result.addError("lastName", "notFound") // "not found"
      render("owners/search")
    }
    else if (results.size > 1) {
      // multiple owners found
      request("selections")  = results
      render("owners/list")
    }
    else {
      // 1 owner found
      val owner1 = results.iterator.next
      redirect("/owners/" + owner1.id)
    }
  }

}