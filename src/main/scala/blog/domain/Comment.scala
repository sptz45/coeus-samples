package blog.domain

import scala.reflect.BeanProperty
import java.util.Date
import javax.persistence._
import javax.validation.Valid
import javax.validation.constraints._
import org.hibernate.validator.constraints._

@Entity
class Comment extends BaseEntity with Serializable {
    
    @Embedded
    @Valid
    @BeanProperty
    val author = new Comment.Author
    
    @Lob
    @Column(nullable=false)
    @NotEmpty
    @BeanProperty
    var content: String = _
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    var created = new Date
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable=false)
    var post: Post = _
}

object Comment {

  @Embeddable
  class Author extends Serializable {
    
    @Column
    @NotEmpty
    @BeanProperty
    var name: String = _
    
    @Column
    @NotEmpty
    @BeanProperty
    var email: String = _
    
    var website: String = _
    
    var ipAddress: String = _
  }
}
