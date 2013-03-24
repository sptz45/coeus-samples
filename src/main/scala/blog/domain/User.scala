package blog.domain

import scala.reflect.BeanProperty
import javax.persistence._
import javax.validation.constraints._
import org.hibernate.validator.constraints._

@Entity
class User extends BaseEntity with Serializable {
  
  def this(username: String, password: String, email: String) = {
    this()
    this.username = username
    this.password = password
    this.email = email
  }
  
  @NotEmpty
  @BeanProperty
  var username: String = _
  
  @NotEmpty
  @BeanProperty
  var password: String = _
  
  @Email
  @NotEmpty
  @BeanProperty
  var email: String = _
}
