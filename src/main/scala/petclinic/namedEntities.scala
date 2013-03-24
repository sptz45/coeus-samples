package petclinic

/**
 * Simple JavaBean domain object adds a name property to <code>BaseEntity</code>.
 * Used as a base class for objects needing these properties.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
class NamedEntity extends BaseEntity {

  var name: String = _

  override def toString = name
}


class Specialty extends NamedEntity

class PetType extends NamedEntity