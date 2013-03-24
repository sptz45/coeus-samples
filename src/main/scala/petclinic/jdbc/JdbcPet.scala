package petclinic
package jdbc

/**
 * Subclass of Pet that carries temporary id properties which
 * are only relevant for a JDBC implmentation of the Clinic.
 *
 * @author Juergen Hoeller
 * @see SimpleJdbcClinic
 */
class JdbcPet extends Pet {

  var typeId: Int = _

  var ownerId: Int = _
}