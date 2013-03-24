package petclinic.web

import javax.servlet.ServletContext
import org.fusesource.scalate.TemplateEngine
import com.tzavellas.coeus.mvc.view.scalate.ScalateConfigurator
import com.tzavellas.coeus.mvc.view.helper.DefaultViewHelpers


class ScalateConfig(sc: ServletContext) extends ScalateConfigurator(sc) {
  
  templatePrefix = "/WEB-INF/ssp/"
  bind("c" -> new ViewHelpers(servletContext))
  
  override def configure(engine: TemplateEngine) {
    engine.importStatements =
      "import petclinic._" :: engine.importStatements
  }
}

class ViewHelpers(sc: ServletContext) extends DefaultViewHelpers(sc) {
  override val scriptsPrefix     = "/static/js/"
  override val stylesheetsPrefix = "/static/styles/"
  override val imagesPrefix      = "/static/images/"
}