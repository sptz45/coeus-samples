package petclinic
package jdbc

import java.sql.{ResultSet, SQLException}
import java.util._
import javax.sql.DataSource

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core._
import org.springframework.jdbc.core.simple._
import org.springframework.jmx.export.annotation.{ManagedOperation, ManagedResource}
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._
import util.EntityUtils

/**
 * A simple JDBC-based implementation of the {@link Clinic} interface.
 *
 * <p>This class uses Java 5 language features and the {@link jdbcTemplate}
 * plus {@link SimpleJdbcInsert}. It also takes advantage of classes like
 * {@link BeanPropertySqlParameterSource} and
 * {@link ParameterizedBeanPropertyRowMapper} which provide automatic mapping
 * between JavaBean properties and JDBC parameters or query results.
 *
 * <p>SimpleJdbcClinic is a rewrite of the AbstractJdbcClinic which was the base
 * class for JDBC implementations of the Clinic interface for Spring 2.0.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 */
@Service
@ManagedResource("petclinic:type=Clinic")
class SimpleJdbcClinic extends Clinic with SimpleJdbcClinicMBean {

  val logger = LoggerFactory.getLogger(getClass)

  private var jdbcTemplate: SimpleJdbcTemplate = _
  private var insertOwner, insertPet, insertVisit: SimpleJdbcInsert = _
  
  private val vets = new ArrayList[Vet]


  @Autowired
  def init(dataSource: DataSource) {
    jdbcTemplate = new SimpleJdbcTemplate(dataSource)

    insertOwner = new SimpleJdbcInsert(dataSource)
      .withTableName("owners")
      .usingGeneratedKeyColumns("id")
    insertPet = new SimpleJdbcInsert(dataSource)
      .withTableName("pets")
      .usingGeneratedKeyColumns("id")
    insertVisit = new SimpleJdbcInsert(dataSource)
      .withTableName("visits")
      .usingGeneratedKeyColumns("id")
  }


  /**
   * Refresh the cache of Vets that the Clinic is holding.
   * @see org.springframework.samples.petclinic.Clinic#getVets()
   */
  @ManagedOperation
  @Transactional(readOnly = true)
  def refreshVetsCache() {
    vets synchronized {
      logger.info("Refreshing vets cache")

      // Retrieve the list of all vets.
      vets.clear()
      vets.addAll(jdbcTemplate.query(
          "SELECT id, first_name, last_name FROM vets ORDER BY last_name,first_name",
          new VetRowMapper))

      // Retrieve the list of all possible specialties.
      val specialties = jdbcTemplate.query(
          "SELECT id, name FROM specialties",
          new SpecialtyRowMapper)

      // Build each vet's list of specialties.
      for (vet <- vets) {
        val vetSpecialtiesIds = jdbcTemplate.query(
            "SELECT specialty_id FROM vet_specialties WHERE vet_id=?",
            new RowMapper[Int]() {
              def mapRow(rs: ResultSet, row: Int) = rs.getInt(1)
            },
            vet.id.asInstanceOf[AnyRef])
        for (specialtyId <- vetSpecialtiesIds) {
          val specialty = EntityUtils.getById(specialties, specialtyId)
          vet.addSpecialty(specialty)
        }
      }
    }
  }


  // START of Clinic implementation section *******************************

  @Transactional(readOnly = true)
  def getVets() = vets.synchronized {
    if (vets.isEmpty)
      refreshVetsCache()
    new Vets(vets)
  }

  @Transactional(readOnly = true)
  def getPetTypes() = jdbcTemplate.query(
    "SELECT id, name FROM types ORDER BY name",
    new PetTypeRowMapper)

  /**
   * Loads {@link Owner Owners} from the data store by last name, returning
   * all owners whose last name <i>starts</i> with the given name; also loads
   * the {@link Pet Pets} and {@link Visit Visits} for the corresponding
   * owners, if not already loaded.
   */
  @Transactional(readOnly = true)
  def findOwners(lastName: String) = {
    val owners = jdbcTemplate.query(
      "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name like ?",
      new OwnerRowMapper,
       lastName + "%");
    loadOwnersPetsAndVisits(owners)
    owners
  }

  /**
   * Loads the {@link Owner} with the supplied <code>id</code>; also loads
   * the {@link Pet Pets} and {@link Visit Visits} for the corresponding
   * owner, if not already loaded.
   */
  @Transactional(readOnly = true)
  def loadOwner(id: Int)  = {
    var owner: Owner = null
    try {
      owner = jdbcTemplate.queryForObject(
          "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id=?",
          new OwnerRowMapper,
          id.asInstanceOf[AnyRef])
    }
    catch {
      case ex: EmptyResultDataAccessException =>
        throw new ObjectRetrievalFailureException(classOf[Owner], id)
    }
    loadPetsAndVisits(owner)
    owner
  }

  @Transactional(readOnly = true)
  def loadPet(id: Int): Pet = {
    var pet: JdbcPet = null
    try {
      pet = jdbcTemplate.queryForObject(
          "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id=?",
          new JdbcPetRowMapper,
          id.asInstanceOf[AnyRef])
    }
    catch {
      case ex: EmptyResultDataAccessException =>
        throw new ObjectRetrievalFailureException(classOf[Pet], id)
    }
    val owner = loadOwner(pet.ownerId)
    owner.addPet(pet)
    pet.`type` = EntityUtils.getById(getPetTypes, pet.typeId)
    loadVisits(pet)
    pet
  }

  @Transactional
  def storeOwner(owner: Owner) = {
    if (owner.isNew) {
      val newKey = insertOwner.executeAndReturnKey(createOwnerParameterSource(owner))
      owner.id = newKey.intValue
    }
    else {
      jdbcTemplate.update(
          "UPDATE owners SET first_name=:first_name, last_name=:last_name, address=:address, " +
          "city=:city, telephone=:telephone WHERE id=:id",
          createOwnerParameterSource(owner))
    }
  }

  @Transactional
  def storePet(pet: Pet) {
    if (pet.isNew) {
      val newKey = insertPet.executeAndReturnKey(createPetParameterSource(pet))
      pet.id = newKey.intValue
    }
    else {
      jdbcTemplate.update(
          "UPDATE pets SET name=:name, birth_date=:birth_date, type_id=:type_id, " +
          "owner_id=:owner_id WHERE id=:id",
          createPetParameterSource(pet))
    }
  }

  @Transactional
  def storeVisit(visit: Visit) {
    if (visit.isNew) {
      val newKey = insertVisit.executeAndReturnKey(createVisitParameterSource(visit))
      visit.id = newKey.intValue
    }
    else {
      throw new UnsupportedOperationException("Visit update not supported")
    }
  }

  @Transactional
  def deletePet(id: Int) {
    jdbcTemplate.update("DELETE FROM pets WHERE id=?", id.asInstanceOf[AnyRef])
  }

  // END of Clinic implementation section ************************************

  /**
   * Creates a {@link MapSqlParameterSource} based on data values from the
   * supplied {@link Owner} instance.
   */
  private def createOwnerParameterSource(owner: Owner) = {
    new MapSqlParameterSource()
      .addValue("id",         owner.id)
      .addValue("first_name", owner.firstName)
      .addValue("last_name",  owner.lastName)
      .addValue("address",    owner.address)
      .addValue("city",       owner.city)
      .addValue("telephone",  owner.telephone)
  }

  /**
   * Creates a {@link MapSqlParameterSource} based on data values from the
   * supplied {@link Pet} instance.
   */
  private def createPetParameterSource(pet: Pet) = {
    new MapSqlParameterSource()
      .addValue("id",         pet.id)
      .addValue("name",       pet.name)
      .addValue("birth_date", pet.birthDate)
      .addValue("type_id",    pet.`type`.id)
      .addValue("owner_id",   pet.owner.id)
  }

  /**
   * Creates a {@link MapSqlParameterSource} based on data values from the
   * supplied {@link Visit} instance.
   */
  private def createVisitParameterSource(visit: Visit) = {
    new MapSqlParameterSource()
      .addValue("id",          visit.id)
      .addValue("visit_date",  visit.date)
      .addValue("description", visit.description)
      .addValue("pet_id",      visit.pet.id)
  }

  /**
   * Loads the {@link Visit} data for the supplied {@link Pet}.
   */
  private def loadVisits(pet: JdbcPet) {
    val visits = jdbcTemplate.query(
        "SELECT id, visit_date, description FROM visits WHERE pet_id=?",
        new RowMapper[Visit] {
          def mapRow(rs: ResultSet, row: Int) = {
            val visit = new Visit
            visit.id          = rs.getInt("id")
            visit.date        = rs.getTimestamp("visit_date")
            visit.description = rs.getString("description")
            visit
          }
        },
        pet.id.asInstanceOf[AnyRef])
    visits.foreach(pet.addVisit(_))
  }

  /**
   * Loads the {@link Pet} and {@link Visit} data for the supplied
   * {@link Owner}.
   */
  private def loadPetsAndVisits(owner: Owner) {
    val pets = jdbcTemplate.query(
        "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE owner_id=?",
        new JdbcPetRowMapper,
        owner.id.asInstanceOf[AnyRef])
    for (pet <- pets) {
      owner.addPet(pet)
      pet.`type` = EntityUtils.getById(getPetTypes, pet.typeId)
      loadVisits(pet)
    }
  }

  /**
   * Loads the {@link Pet} and {@link Visit} data for the supplied
   * {@link List} of {@link Owner Owners}.
   *
   * @param owners the list of owners for whom the pet and visit data should be loaded
   * @see #loadPetsAndVisits(Owner)
   */
  private def loadOwnersPetsAndVisits(owners: List[Owner]) {
    owners.foreach(loadPetsAndVisits(_))
  }

  /**
   * {@link ParameterizedRowMapper} implementation mapping data from a
   * {@link ResultSet} to the corresponding properties of the {@link JdbcPet} class.
   */
  private class JdbcPetRowMapper extends RowMapper[JdbcPet] {
    def mapRow(rs: ResultSet, rownum: Int) = {
      val pet = new JdbcPet
      pet.id        = rs.getInt("id")
      pet.name      = rs.getString("name")
      pet.birthDate = rs.getDate("birth_date")
      pet.typeId    = rs.getInt("type_id")
      pet.ownerId   = rs.getInt("owner_id")
      pet
    }
  }
  
  private class PetTypeRowMapper extends RowMapper[PetType] {
    def mapRow(rs: ResultSet, rownum: Int) = {
      val pt = new PetType
      pt.id   = rs.getInt("id")
      pt.name = rs.getString("name")
      pt
    }
  }
  
  private class SpecialtyRowMapper extends RowMapper[Specialty] {
    def mapRow(rs: ResultSet, rownum: Int) = {
      val specialty = new Specialty
      specialty.id   = rs.getInt("id")
      specialty.name = rs.getString("name")
      specialty
    }
  }
  
  private class OwnerRowMapper extends RowMapper[Owner] {
    def mapRow(rs: ResultSet, rownum: Int) = {
      val owner = new Owner
      owner.id        = rs.getInt("id")
      owner.firstName = rs.getString("first_name")
      owner.lastName  = rs.getString("last_name")
      owner.address   = rs.getString("address")
      owner.city      = rs.getString("city")
      owner.telephone = rs.getString("telephone")
      owner
    }
  }
  
  private class VetRowMapper extends RowMapper[Vet] {
    def mapRow(rs: ResultSet, rownum: Int) = {
      val vet = new Vet
      vet.id        = rs.getInt("id")
      vet.firstName = rs.getString("first_name")
      vet.lastName  = rs.getString("last_name")
      vet
    }
  }
}