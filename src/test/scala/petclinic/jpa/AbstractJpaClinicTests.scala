package petclinic
package jpa

import java.util.Collection
import java.util.Date
import javax.persistence.{EntityManager, EntityManagerFactory, PersistenceContext}

import org.junit.Test
import org.junit.Assert._

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests

import util.EntityUtils

/**
 * <p>
 * This class extends {@link AbstractJpaTests}, one of the valuable test
 * superclasses provided in the <code>org.springframework.test</code> package.
 * This represents best practice for integration tests with Spring for JPA based
 * tests which require <em>shadow class loading</em>. For all other types of
 * integration testing, the <em>Spring TestContext Framework</em> is
 * preferred.
 * </p>
 * <p>
 * AbstractJpaTests and its superclasses provide the following services:
 * <ul>
 * <li>Injects test dependencies, meaning that we don't need to perform
 * application context lookups. See the setClinic() method. Injection uses
 * autowiring by type.</li>
 * <li>Executes each test method in its own transaction, which is automatically
 * rolled back by default. This means that even if tests insert or otherwise
 * change database state, there is no need for a teardown or cleanup script.</li>
 * <li>Provides useful inherited protected fields, such as a
 * {@link SimpleJdbcTemplate} that can be used to verify database state after
 * test operations, or verify the results of queries performed by application
 * code. Alternatively, you can use protected convenience methods such as
 * {@link #countRowsInTable(String)}, {@link #deleteFromTables(String[])},
 * etc. An ApplicationContext is also inherited, and can be used for explicit
 * lookup if necessary.</li>
 * </ul>
 * <p>
 * {@link AbstractJpaTests} and related classes are shipped in
 * <code>spring-test.jar</code>.
 * </p>
 *
 * @author Rod Johnson
 * @author Sam Brannen
 * @see AbstractJpaTests
 */
abstract class AbstractJpaClinicTests extends AbstractTransactionalJUnit4SpringContextTests {

  @Autowired
  var clinic: Clinic = _
  
  @PersistenceContext
  var sharedEntityManager: EntityManager = _ 

  @Test(expected=classOf[IllegalArgumentException])
  def testBogusJpql() {
    sharedEntityManager.createQuery("SELECT RUBBISH FROM RUBBISH HEAP").executeUpdate()
  }

  @Test
  def testGetVets() {
    val vets = clinic.getVets()
    // Use the inherited countRowsInTable() convenience method (from
    // AbstractTransactionalDataSourceSpringContextTests) to verify the
    // results.
    assertEquals("JDBC query must show the same number of vets", countRowsInTable("VETS"), vets.size)
    val v1 = EntityUtils.getById(vets, 2)
    assertEquals("Leary", v1.lastName)
    assertEquals(1, v1.specialties.size)
    assertEquals("radiology", (v1.getSpecialties(0)).name)
    val v2 = EntityUtils.getById(vets, 3)
    assertEquals("Douglas", v2.lastName)
    assertEquals(2, v2.specialties.size)
    assertEquals("dentistry", v2.getSpecialties(0).name)
    assertEquals("surgery", (v2.getSpecialties(1)).name)
  }

  @Test
  def testGetPetTypes() {
    val petTypes = clinic.getPetTypes
    assertEquals("JDBC query must show the same number of pet types", countRowsInTable("TYPES"),
        petTypes.size)
    val t1 = EntityUtils.getById(petTypes, 1)
    assertEquals("cat", t1.name)
    val t4 = EntityUtils.getById(petTypes, 4)
    assertEquals("snake", t4.name)
  }

  @Test
  def testFindOwners() {
    var owners = clinic.findOwners("Davis")
    assertEquals(2, owners.size)
    owners = clinic.findOwners("Daviss")
    assertEquals(0, owners.size)
  }

  @Test
  def testLoadOwner() {
    val o1 = clinic.loadOwner(1)
    assertTrue(o1.lastName.startsWith("Franklin"))
    val o10 = clinic.loadOwner(10)
    assertEquals("Carlos", o10.firstName)

    // Check lazy loading, by ending the transaction
    sharedEntityManager.close()

    // Now Owners are "disconnected" from the data store.
    // We might need to touch this collection if we switched to lazy loading
    // in mapping files, but this test would pick this up.
    o1.pets
  }

  @Test
  def testInsertOwner() {
    var owners = clinic.findOwners("Schultz")
    val found = owners.size
    val owner = new Owner
    owner.lastName = "Schultz"
    clinic.storeOwner(owner)
    // assertTrue(!owner.isNew()); -- NOT TRUE FOR TOPLINK (before commit)
    owners = clinic.findOwners("Schultz")
    assertEquals(found + 1, owners.size)
  }

  @Test
  def testUpdateOwner() {
    var o1 = clinic.loadOwner(1)
    val old = o1.lastName
    o1.lastName = old + "X"
    clinic.storeOwner(o1)
    o1 = clinic.loadOwner(1)
    assertEquals(old + "X", o1.lastName)
  }

  @Test
  def testLoadPet() {
    val types = clinic.getPetTypes()
    val p7 = clinic.loadPet(7)
    assertTrue(p7.name.startsWith("Samantha"))
    assertEquals(EntityUtils.getById(types, 1).id, p7.`type`.id)
    assertEquals("Jean", p7.owner.firstName)
    val p6 = clinic.loadPet(6)
    assertEquals("George", p6.name)
    assertEquals(EntityUtils.getById(types, 4).id, p6.`type`.id)
    assertEquals("Peter", p6.owner.firstName)
  }

  @Test
  def testInsertPet() {
    var o6 = clinic.loadOwner(6)
    val found = o6.pets.size
    val pet = new Pet
    pet.name = "bowser"
    val types = clinic.getPetTypes();
    pet.`type` = EntityUtils.getById(types, 2)
    pet.birthDate = new Date
    o6.addPet(pet)
    assertEquals(found + 1, o6.pets.size)
    clinic.storeOwner(o6)
    // assertTrue(!pet.isNew()); -- NOT TRUE FOR TOPLINK (before commit)
    o6 = clinic.loadOwner(6)
    assertEquals(found + 1, o6.pets.size)
  }

  @Test
  def testUpdatePet() {
    var p7 = clinic.loadPet(7)
    val old = p7.name
    p7.name = old + "X"
    clinic.storePet(p7)
    p7 = clinic.loadPet(7)
    assertEquals(old + "X", p7.name)
  }

  @Test
  def testInsertVisit() {
    var p7 = clinic.loadPet(7)
    val found = p7.visits.size
    val visit = new Visit
    p7.addVisit(visit)
    visit.description = "test"
    clinic.storePet(p7)
    // assertTrue(!visit.isNew()); -- NOT TRUE FOR TOPLINK (before commit)
    p7 = clinic.loadPet(7)
    assertEquals(found + 1, p7.visits.size)
  }
}