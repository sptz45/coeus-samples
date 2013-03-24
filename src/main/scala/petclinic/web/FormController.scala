package petclinic.web

import com.tzavellas.coeus.mvc._

abstract class FormController extends AbstractController {
  
  override def storeModelInSession = true
  
  override lazy val binder = new Binder(converters, denyVars=Seq("id"))

}