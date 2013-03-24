package petclinic
package hibernate

import scala.collection.JavaConversions.asScalaBuffer

import org.hibernate.{SessionFactory, Query}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Hibernate implementation of the Clinic interface.
 *
 * <p>The mappings are defined in "petclinic.hbm.xml", located in the root of the
 * class path.
 *
 * <p>Note that transactions are declared with annotations and that some methods
 * contain "readOnly = true" which is an optimization that is particularly
 * valuable when using Hibernate (to suppress unnecessary flush attempts for
 * read-only operations).
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Mark Fisher
 * @since 19.10.2003
 */
@Repository
@Transactional
class HibernateClinic @Autowired() (sessionFactory: SessionFactory) extends Clinic {

  private def session = sessionFactory.getCurrentSession()
  
  private def seqOf[T](q: Query) = asScalaBuffer(q.list()).asInstanceOf[Seq[T]]

  @Transactional(readOnly = true)
  def getVets() = {
    new Vets(seqOf[Vet](session.createQuery("from Vet vet order by vet.lastName, vet.firstName")))
  }

  @Transactional(readOnly = true)
  def getPetTypes() = {
    seqOf[PetType](session.createQuery("from PetType type order by type.name"))
  }

  @Transactional(readOnly = true)
  def findOwners(lastName: String) = {
    val q = session.createQuery("from Owner owner where owner.lastName like :lastName")
                   .setString("lastName", lastName + "%")
    seqOf[Owner](q)
  }

  @Transactional(readOnly = true)
  def loadOwner(id: Int) = {
    session.load(classOf[Owner], id).asInstanceOf[Owner]
  }

  @Transactional(readOnly = true)
  def loadPet(id: Int) = {
    session.load(classOf[Pet], id).asInstanceOf[Pet]
  }

  def storeOwner(owner: Owner) {
    // Note: Hibernate3's merge operation does not reassociate the object
    // with the current Hibernate Session. Instead, it will always copy the
    // state over to a registered representation of the entity. In case of a
    // new entity, it will register a copy as well, but will not update the
    // id of the passed-in object. To still update the ids of the original
    // objects too, we need to register Spring's
    // IdTransferringMergeEventListener on our SessionFactory.
    session.merge(owner)
  }

  def storePet(pet: Pet) {
    session.merge(pet)
  }

  def storeVisit(visit: Visit) {
    session.merge(visit)
  }

  def deletePet(id: Int) {
    val pet = loadPet(id)
    session.delete(pet)
  }
}