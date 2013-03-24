package petclinic
package util

import org.springframework.orm.ObjectRetrievalFailureException

/**
 * Utility methods for handling entities. Separate from the BaseEntity class
 * mainly because of dependency on the ORM-associated
 * ObjectRetrievalFailureException.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 29.10.2003
 * @see org.springframework.samples.petclinic.BaseEntity
 */
object EntityUtils {

  /**
   * Look up the entity of the given class with the given id in the given
   * collection.
   *
   * @param entities the collection to search
   * @param id the entity id to look up
   * @return the found entity
   * @throws ObjectRetrievalFailureException if the entity was not found
   */ 
  def getById[T <: BaseEntity](entities: Traversable[T], id: Int)(implicit m: Manifest[T]) = {
    val entityClass = m.runtimeClass
    entities.find(_.id == id).getOrElse(throw new ObjectRetrievalFailureException(entityClass, id))
  }
}
