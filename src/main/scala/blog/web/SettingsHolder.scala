package blog.web

import javax.servlet.ServletContext
import com.tzavellas.coeus.mvc.scope.ApplicationScope
import blog.domain.Settings
  
object SettingsHolder {

  private val SETTINGS_NAME = "coeus-blog-settings"
 
  def get(application: ApplicationScope) = application[Settings](SETTINGS_NAME)

  def update(application: ApplicationScope, settings: Settings) {
    application(SETTINGS_NAME) = settings
  }
  
  def update(servletContext: ServletContext, settings: Settings) {
    servletContext.setAttribute(SETTINGS_NAME, settings)
  }
}

