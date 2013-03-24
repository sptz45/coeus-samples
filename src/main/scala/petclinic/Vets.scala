package petclinic

/**
 * Simple JavaBean domain object representing a list of veterinarians.
 * Mostly here to be used for the 'vets'
 * {@link org.springframework.web.servlet.view.xml.MarshallingView}.
 *
 * @author Arjen Poutsma
 */
class Vets(vets: Seq[Vet]) extends Traversable[Vet] {

  def foreach[U](f: Vet => U) = vets.foreach(f)
    
  def toXml = {
    <veterinarians>
    { for (v <- vets) yield v.toXml }
    </veterinarians>
  }
}