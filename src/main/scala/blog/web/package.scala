package blog

import java.util.Locale
import com.tzavellas.coeus.validation.bean._

package object web {

  implicit val validator = BeanValidator.defaultValidator(offlineLocale = Locale.US)
  
}
