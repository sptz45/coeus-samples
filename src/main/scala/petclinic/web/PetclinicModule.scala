package petclinic
package web

import javax.servlet.ServletConfig
import org.springframework.dao.DataAccessException
import org.springframework.transaction.TransactionException
import com.tzavellas.coeus.core.config._
import com.tzavellas.coeus.core.error.ExceptionHandler
import com.tzavellas.coeus.i18n.msg.ClasspathMessageBundle
import com.tzavellas.coeus.mvc.view.ViewFactory._
import com.tzavellas.coeus.mvc.view.scalate.ScalateViewResolver
import com.tzavellas.coeus.spring.config.SpringSupport


class PetclinicModule(c: ServletConfig) extends WebModule(c) with SpringSupport {

  overrideHttpMethod   = true
  classNameTranslator  = ControllerConventions.ignoreClassName
  methodNameTranslator = ControllerConventions.ignoreMethodName

  converters = converters.add(new PetTypeConverter(bean("clinic")))
    
  exceptionHandler = ExceptionHandler.forServlet(c.getServletName) {
    case e: DataAccessException  => render("dataAccessFailure")
    case e: TransactionException => render("dataAccessFailure")
  }
  
  messageBundle = new ClasspathMessageBundle(timeToLive=1000)
  
  viewResolver = new ScalateViewResolver(new ScalateConfig(servletContext))  
}

