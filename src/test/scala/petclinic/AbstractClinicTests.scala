package petclinic

import java.util.Date

import org.junit.Assert._
import org.junit.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests

import util.EntityUtils

/**
 * <p>
 * Base class for {@link Clinic} integration tests.
 * </p>
 * <p>
 * &quot;AbstractClinicTests-context.xml&quot; declares a common
 * {@link javax.sql.DataSource DataSource}. Subclasses should specify
 * additional context locations which declare a
 * {@link org.springframework.transaction.PlatformTransactionManager PlatformTransactionManager}
 * and a concrete implementation of {@link Clinic}.
 * </p>
 * <p>
 * This class extends {@link AbstractTransactionalJUnit4SpringContextTests},
 * one of the valuable testing support classes provided by the
 * <em>Spring TestContext Framework</em> found in the
 * <code>org.springframework.test.context</code> package. The
 * annotation-driven configuration used here represents best practice for
 * integration tests with Spring. Note, however, that
 * AbstractTransactionalJUnit4SpringContextTests serves only as a convenience
 * for extension. For example, if you do not wish for your test classes to be
 * tied to a Spring-specific class hierarchy, you may configure your tests with
 * annotations such as {@link ContextConfiguration @ContextConfiguration},
 * {@link org.springframework.test.context.TestExecutionListeners @TestExecutionListeners},
 * {@link org.springframework.transaction.annotation.Transactional @Transactional},
 * etc.
 * </p>
 * <p>
 * AbstractClinicTests and its subclasses benefit from the following services
 * provided by the Spring TestContext Framework:
 * </p>
 * <ul>
 * <li><strong>Spring IoC container caching</strong> which spares us
 * unnecessary set up time between test execution.</li>
 * <li><strong>Dependency Injection</strong> of test fixture instances,
 * meaning that we don't need to perform application context lookups. See the
 * use of {@link Autowired @Autowired} on the <code>clinic</code> instance
 * variable, which uses autowiring <em>by type</em>. As an alternative, we
 * could annotate <code>clinic</code> with
 * {@link javax.annotation.Resource @Resource} to achieve dependency injection
 * <em>by name</em>.
 * <em>(see: {@link ContextConfiguration @ContextConfiguration},
 * {@link org.springframework.test.context.support.DependencyInjectionTestExecutionListener DependencyInjectionTestExecutionListener})</em></li>
 * <li><strong>Transaction management</strong>, meaning each test method is
 * executed in its own transaction, which is automatically rolled back by
 * default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script.
 * <em>(see: {@link org.springframework.test.context.transaction.TransactionConfiguration @TransactionConfiguration},
 * {@link org.springframework.transaction.annotation.Transactional @Transactional},
 * {@link org.springframework.test.context.transaction.TransactionalTestExecutionListener TransactionalTestExecutionListener})</em></li>
 * <li><strong>Useful inherited protected fields</strong>, such as a
 * {@link org.springframework.jdbc.core.simple.SimpleJdbcTemplate SimpleJdbcTemplate}
 * that can be used to verify database state after test operations or to verify
 * the results of queries performed by application code. An
 * {@link org.springframework.context.ApplicationContext ApplicationContext} is
 * also inherited and can be used for explicit bean lookup if necessary.
 * <em>(see: {@link org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests AbstractJUnit4SpringContextTests},
 * {@link AbstractTransactionalJUnit4SpringContextTests})</em></li>
 * </ul>
 * <p>
 * The Spring TestContext Framework and related unit and integration testing
 * support classes are shipped in <code>spring-test.jar</code>.
 * </p>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@ContextConfiguration
abstract class AbstractClinicTests extends AbstractTransactionalJUnit4SpringContextTests {

  @Autowired
  protected var clinic: Clinic = _


  @Test
  def getVets() {
    val vets = clinic.getVets
    // Use the inherited countRowsInTable() convenience method (from
    // AbstractTransactionalJUnit4SpringContextTests) to verify the results.
    assertEquals("JDBC query must show the same number of vets", countRowsInTable("VETS"), vets.size)
    val v1 = EntityUtils.getById(vets, 2)
    assertEquals("Leary", v1.lastName)
    assertEquals(1, v1.specialties.size)
    assertEquals("radiology", (v1.getSpecialties(0)).name)
    val v2 = EntityUtils.getById(vets, 3)
    assertEquals("Douglas", v2.lastName)
    assertEquals(2, v2.specialties.size)
    assertEquals("dentistry", (v2.getSpecialties(0)).name)
    assertEquals("surgery", (v2.getSpecialties(1)).name)
  }

  @Test
  def getPetTypes() {
    val petTypes = clinic.getPetTypes()
    assertEquals("JDBC query must show the same number of pet types", countRowsInTable("TYPES"),
        petTypes.size)
    val t1 = EntityUtils.getById(petTypes, 1)
    assertEquals("cat", t1.name)
    val t4 = EntityUtils.getById(petTypes, 4)
    assertEquals("snake", t4.name)
  }

  @Test
  def findOwners() {
    var owners = this.clinic.findOwners("Davis")
    assertEquals(2, owners.size)
    owners = clinic.findOwners("Daviss")
    assertEquals(0, owners.size)
  }

  @Test
  def loadOwner() {
    val o1 = clinic.loadOwner(1)
    assertTrue(o1.lastName.startsWith("Franklin"))
    val o10 = clinic.loadOwner(10)
    assertEquals("Carlos", o10.firstName)

    // XXX: Add programmatic support for ending transactions with the
    // TestContext Framework.

    // Check lazy loading, by ending the transaction:
    // endTransaction();

    // Now Owners are "disconnected" from the data store.
    // We might need to touch this collection if we switched to lazy loading
    // in mapping files, but this test would pick this up.
    o1.pets
  }

  @Test
  def insertOwner() {
    var owners = this.clinic.findOwners("Schultz")
    val found = owners.size
    val owner = new Owner
    owner.lastName = "Schultz"
    clinic.storeOwner(owner)
    // assertTrue(!owner.isNew()); -- NOT TRUE FOR TOPLINK (before commit)
    owners = clinic.findOwners("Schultz")
    assertEquals("Verifying number of owners after inserting a new one.", found + 1, owners.size)
  }

  @Test
  def updateOwner() {
    var o1 = this.clinic.loadOwner(1)
    val old = o1.lastName
    o1.lastName = old + "X"
    clinic.storeOwner(o1)
    o1 = clinic.loadOwner(1)
    assertEquals(old + "X", o1.lastName)
  }

  @Test
  def loadPet() {
    val types = clinic.getPetTypes
    val p7 = clinic.loadPet(7)
    assertTrue(p7.name.startsWith("Samantha"))
    assertEquals(EntityUtils.getById(types, 1).id, p7.`type`.id)
    assertEquals("Jean", p7.owner.firstName)
    val p6 = this.clinic.loadPet(6)
    assertEquals("George", p6.name)
    assertEquals(EntityUtils.getById(types, 4).id, p6.`type`.id)
    assertEquals("Peter", p6.owner.firstName)
  }

  @Test
  def insertPet() {
    var o6 = clinic.loadOwner(6)
    val found = o6.pets.size
    val pet = new Pet
    pet.name = "bowser"
    val types = clinic.getPetTypes()
    pet.`type` = EntityUtils.getById(types, 2)
    pet.birthDate = new Date
    o6.addPet(pet)
    assertEquals(found + 1, o6.pets.size)
    // both storePet and storeOwner are necessary to cover all ORM tools
    clinic.storePet(pet)
    clinic.storeOwner(o6)
    // assertTrue(!pet.isNew()); -- NOT TRUE FOR TOPLINK (before commit)
    o6 = this.clinic.loadOwner(6)
    assertEquals(found + 1, o6.pets.size)
  }

  @Test
  def updatePet() {
    var p7 = clinic.loadPet(7)
    val old = p7.name
    p7.name = old + "X"
    clinic.storePet(p7)
    p7 = clinic.loadPet(7)
    assertEquals(old + "X", p7.name)
  }

  @Test
  def insertVisit() {
    var p7 = clinic.loadPet(7)
    val found = p7.visits.size
    val visit = new Visit
    p7.addVisit(visit)
    visit.description = "test"
    // both storeVisit and storePet are necessary to cover all ORM tools
    clinic.storeVisit(visit)
    clinic.storePet(p7)
    // assertTrue(!visit.isNew()); -- NOT TRUE FOR TOPLINK (before commit)
    p7 = clinic.loadPet(7)
    assertEquals(found + 1, p7.visits.size)
  }

}