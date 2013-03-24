package blog.domain

import scala.reflect.BeanProperty
import javax.persistence._
import javax.validation.constraints._
import org.hibernate.validator.constraints._

@Entity
class Settings extends BaseEntity {
  
  @Version
  var version: Int = _

  @NotEmpty
  @BeanProperty
  var title  = "Coeus Blog"
  
  @NotEmpty
  @BeanProperty
  var description = "A sample blog application using the Coeus web framework"
  
  @Min(1)
  @BeanProperty
  var postsPerPage = 10
  
  var allowComments = true
}
