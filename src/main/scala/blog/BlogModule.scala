package blog

import javax.servlet.ServletConfig
import com.google.inject.Guice
import org.fusesource.scalate.TemplateEngine
import com.tzavellas.coeus.core.config._
import com.tzavellas.coeus.mvc.view.scalate._

import persistence.PersistenceContextInterceptor

class BlogModule(sc: ServletConfig) extends WebModule(sc) with GuiceSupport {
  
  classNameTranslator = ControllerConventions.useClassName(packages = Seq("com.tzavellas.coeus.sample.blog.web"))
  viewResolver = new ScalateViewResolver(ScalateConfig)
  
  lazy val injector = Guice.createInjector(new GuiceModule(this))
  
  /* Register the interceptor for JPA's PersistentContext handling. */
  interceptors += new PersistenceContextInterceptor
  
  object ScalateConfig extends ScalateConfigurator(servletContext) {
    override def configure(engine: TemplateEngine) {
      engine.importStatements =
        "import blog.domain._" ::
        "import blog.web.SettingsHolder" ::
        engine.importStatements
    }
  }
}
