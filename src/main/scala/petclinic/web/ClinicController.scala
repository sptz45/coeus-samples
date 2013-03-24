package petclinic
package web

import com.tzavellas.coeus.mvc._
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired

@Controller
class ClinicController @Autowired() (clinic: Clinic) extends AbstractController {

  @Get("/")
  def welcomeHandler() = "welcome"
    
  @Get("/vets")
  def vetsHandler() {
    request += clinic.getVets()
  }
  
  @Get("/vets.xml")
  def vetsXmlHandler() = {
    response.contentType = "application/xml"
    clinic.getVets().toXml
  }
  
  @Get("/owners/{ownerId}")
  def ownerHandler() = {
    request += clinic.loadOwner(path("ownerId"))
    "owners/show"
  }
  
  @Get("/owners/*/pets/{petId}/visits")
  def visitsHandler() = {
    request("visits") = clinic.loadPet(path("petId")).visitsHistory
    "visits"
  }
}