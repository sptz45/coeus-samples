package petclinic
package jpa

import javax.persistence._
import scala.collection.JavaConversions.asScalaBuffer

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.dao.DataAccessException

/**
 * JPA implementation of the Clinic interface using EntityManager.
 *
 * <p>The mappings are defined in "orm.xml" located in the META-INF directory.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @since 22.4.2006
 */
@Repository
@Transactional
class EntityManagerClinic extends Clinic {

  @PersistenceContext
  private var em: EntityManager = _

  private def seqOf[T](q: Query) = asScalaBuffer(q.getResultList).asInstanceOf[Seq[T]]

  @Transactional(readOnly = true)
  def getVets() = {
    new Vets(seqOf[Vet](em.createQuery("SELECT vet FROM Vet vet ORDER BY vet.lastName, vet.firstName")))
  }

  @Transactional(readOnly = true)
  def getPetTypes() = {
    seqOf[PetType](em.createQuery("SELECT ptype FROM PetType ptype ORDER BY ptype.name"))
  }

  @Transactional(readOnly = true)
  def findOwners(lastName: String) = {
    val query = em.createQuery("SELECT owner FROM Owner owner WHERE owner.lastName LIKE :lastName")
    query.setParameter("lastName", lastName + "%")
    seqOf[Owner](query)
  }

  @Transactional(readOnly = true)
  def loadOwner(id: Int) = em.find(classOf[Owner], id)

  @Transactional(readOnly = true)
  def loadPet(id: Int) = em.find(classOf[Pet], id)

  def storeOwner(owner: Owner) {
    // Consider returning the persistent object here, for exposing
    // a newly assigned id using any persistence provider...
    val merged = em.merge(owner)
    em.flush()
    owner.id = merged.id
  }

  def storePet(pet: Pet) {
    // Consider returning the persistent object here, for exposing
    // a newly assigned id using any persistence provider...
    val merged = em.merge(pet)
    em.flush()
    pet.id = merged.id
  }

  def storeVisit(visit: Visit) {
    // Consider returning the persistent object here, for exposing
    // a newly assigned id using any persistence provider...
    val merged = em.merge(visit)
    em.flush()
    visit.id = merged.id
  }

  def deletePet(id: Int) {
    em.remove(loadPet(id))
  }
}