package blog.persistence

import scala.collection.JavaConversions._
import blog.domain._

class JpaTagManager extends AbstractRepository with TagManager {
  
  def findById(id: Int) = Option(em.find(classOf[Tag], id))

  def allTags(): Seq[Tag] = {
    val q = em.createQuery("select t from Tag t")
              .setHint("org.hibernate.cacheable", true)
    resultSeq[Tag](q)
  }
  
  def saveOrUpdate(tag: Tag) = transactional {
    saveOrUpdate(em, tag)
  }
  
  def delete(tagId: Int) = transactional {
    deleteFromId(em, classOf[Tag], tagId)
  }
}
