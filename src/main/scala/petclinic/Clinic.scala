package petclinic

import org.springframework.dao.DataAccessException

/**
 * The high-level PetClinic business interface.
 *
 * <p>This is basically a data access object. PetClinic doesn't have a
 * dedicated business facade.</p>
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
trait Clinic {

  /**
   * Retrieve all <code>Vet</code>s from the data store.
   * @return a <code>Collection</code> of <code>Vet</code>s
   */
  @throws(classOf[DataAccessException])
  def getVets(): Vets

  /**
   * Retrieve all <code>PetType</code>s from the data store.
   * @return a <code>Collection</code> of <code>PetType</code>s
   */
  @throws(classOf[DataAccessException])
  def getPetTypes(): Seq[PetType]

  /**
   * Retrieve <code>Owner</code>s from the data store by last name,
   * returning all owners whose last name <i>starts</i> with the given name.
   * @param lastName Value to search for
   * @return a <code>Collection</code> of matching <code>Owner</code>s
   * (or an empty <code>Collection</code> if none found)
   */
  @throws(classOf[DataAccessException])
  def findOwners(lastName: String): Seq[Owner]

  /**
   * Retrieve an <code>Owner</code> from the data store by id.
   * @param id the id to search for
   * @return the <code>Owner</code> if found
   * @throws org.springframework.dao.DataRetrievalFailureException if not found
   */
  @throws(classOf[DataAccessException])
  def loadOwner(id: Int): Owner

  /**
   * Retrieve a <code>Pet</code> from the data store by id.
   * @param id the id to search for
   * @return the <code>Pet</code> if found
   * @throws org.springframework.dao.DataRetrievalFailureException if not found
   */
  @throws(classOf[DataAccessException])
  def loadPet(id: Int): Pet

  /**
   * Save an <code>Owner</code> to the data store, either inserting or updating it.
   * @param owner the <code>Owner</code> to save
   * @see BaseEntity#isNew
   */
  @throws(classOf[DataAccessException])
  def storeOwner(owner: Owner)

  /**
   * Save a <code>Pet</code> to the data store, either inserting or updating it.
   * @param pet the <code>Pet</code> to save
   * @see BaseEntity#isNew
   */
  @throws(classOf[DataAccessException])
  def storePet(pet: Pet)

  /**
   * Save a <code>Visit</code> to the data store, either inserting or updating it.
   * @param visit the <code>Visit</code> to save
   * @see BaseEntity#isNew
   */
  @throws(classOf[DataAccessException])
  def storeVisit(visit: Visit)

  /**
   * Deletes a <code>Pet</code> from the data store.
   */
  @throws(classOf[DataAccessException])
  def deletePet(id: Int)
}