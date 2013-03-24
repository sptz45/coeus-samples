package blog.startup

import com.google.inject.Inject
import javax.servlet.ServletContext
import blog.web.SettingsHolder
import blog.domain.SettingsPersister

class SettingsInitializer @Inject() (
  settings: SettingsPersister,
  servletContext: ServletContext) extends StartupCommand {

  def execute() {
    SettingsHolder.update(servletContext, settings.initialize())
  }
}
