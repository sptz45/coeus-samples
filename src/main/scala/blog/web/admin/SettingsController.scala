package blog.web
package admin

import com.google.inject.Inject
import com.tzavellas.coeus.mvc._
import blog.domain.{ Settings, SettingsPersister }

class SettingsController @Inject() (persister: SettingsPersister)
  extends AdminController {

  @Get("")
  def show() {
    model += SettingsHolder.get(application)
  }

  @Post("")
  def create() = ifValid(new Settings) { settings =>
    val current = SettingsHolder.get(application)
    settings.id = current.id
    val newSettings = persister.save(settings)
    SettingsHolder.update(application, newSettings)
    redirect("/admin/settings")
  }
}
