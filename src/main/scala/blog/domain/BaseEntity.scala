package blog.domain

import javax.persistence._

@MappedSuperclass
class BaseEntity {
  
  @Id 
  @GeneratedValue
  var id: Int = -1
  
  def isNew = id == -1
}
