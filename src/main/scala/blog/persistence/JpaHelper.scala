package blog.persistence

import language.reflectiveCalls
import java.util.{ List => JList }
import javax.persistence._
import scala.collection.convert.Wrappers.JListWrapper

trait JpaHelper {
  
  type Entity = { def isNew: Boolean }
  
  def saveOrUpdate[E <: Entity](em: EntityManager, entity: E): E = {
    if (entity.isNew) {
      em.persist(entity)
      entity
    } else {
      em.merge(entity)
    }
  }

  def find[T](em: EntityManager, queryString: String, args: Any*): Result[Seq[T]] = {
    val query = em.createQuery(queryString)
    var i = 0
    while (i < args.length) {
      query.setParameter(i+1, args(i))
      i += 1
    }
    new Result[Seq[T]]() {
      def from(firstResult: Int) = {
        query.setFirstResult(firstResult)
        this
      }
      def max(maxResults: Int) = {
        query.setMaxResults(maxResults)
        this
      }
      def get = JListWrapper(query.getResultList.asInstanceOf[java.util.List[T]])
    }
  }
  
  def singleResult[T](query: Query): Option[T] = {
    try {
      Some(query.getSingleResult.asInstanceOf[T])
    } catch {
      case _: NoResultException => None
    }
  }
  
  def resultSeq[T](query: Query): Seq[T] = JListWrapper(query.getResultList.asInstanceOf[JList[T]])
  
  def deleteFromId(em: EntityManager, entityClass: Class[_], id: Any): Boolean = {
    try {
      em.remove(em.getReference(entityClass, id))
      true
    } catch {
      case e: EntityNotFoundException => false
    }
  }
}
