package blog.domain

import scala.reflect.BeanProperty
import javax.persistence._
import javax.validation.constraints._
import org.hibernate.validator.constraints._
import org.hibernate.annotations.{ Fetch, FetchMode }
import java.util.{ Date, Set, HashSet, List, ArrayList }

@Entity
class Post extends BaseEntity with Serializable {
  
  @NotEmpty
  @BeanProperty
  var title: String = _
  
  @NotEmpty
  @BeanProperty
  var content: String = _
  
  @Temporal(TemporalType.TIMESTAMP)
  var timestamp: Date = new Date
  
  @Temporal(TemporalType.TIMESTAMP)
  var publicationDate: Date = _
  
  var allowComments: Boolean = true
  
  @ManyToMany(cascade = Array(CascadeType.MERGE, CascadeType.PERSIST))
  @Fetch(FetchMode.SUBSELECT)
  @BeanProperty
  @NotEmpty
  var tags: Set[Tag] = new HashSet
  
  @OneToMany(cascade=Array(CascadeType.ALL), mappedBy="post")
  @Fetch(FetchMode.JOIN)
  @OrderBy("created ASC")
  var comments: List[Comment] = new ArrayList
  
  def addComment(comment: Comment) {
    comment.post = this
    comments.add(comment)
  }
  
  @ManyToOne
  var author: User = _
  
  def updateTimestamp() {
    timestamp = new Date
  }
  
  def isPublished = publicationDate ne null
  
  def publish() {
    publicationDate = new Date
  }
}
