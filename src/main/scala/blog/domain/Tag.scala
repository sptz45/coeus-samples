package blog.domain

import javax.persistence.Entity

@Entity
class Tag extends BaseEntity with Serializable {
  
  def this(name: String) {
    this()
    this.name = name
  }
  
  var name: String = _
}
