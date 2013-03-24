package blog.persistence

import com.google.inject.Inject
import javax.persistence.EntityManagerFactory

abstract class AbstractRepository extends JpaHelper
                                     with TransactionSupport {
  
  @Inject
  var emf: EntityManagerFactory = _

  def em = PersistenceContext.getEntityManager(emf)
}
