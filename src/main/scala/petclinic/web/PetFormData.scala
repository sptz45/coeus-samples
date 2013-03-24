package petclinic
package web

import com.tzavellas.coeus.mvc.controller.AfterFilter

trait PetFormData extends AfterFilter {

  this: FormController =>
  
  val clinic: Clinic
  
  def after(error: Option[Exception]) = {
    if (request.method == "GET" || model.hasErrors) {
      request("types") = clinic.getPetTypes()
    }
    None
  }
}