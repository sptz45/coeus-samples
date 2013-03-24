package blog.persistence

import javax.persistence.EntityManagerFactory

trait TransactionSupport {
  
  def emf: EntityManagerFactory
  
  def transactional[T](code: => T): T = {
    
    val tx = PersistenceContext.getEntityManager(emf).getTransaction()
    var shouldCommit = false
    
    try {
      if (!tx.isActive) {
        tx.begin()
        shouldCommit = true
      }
      
      val result = code
      
      if (shouldCommit)
        tx.commit()
      
      result
    
    } catch {
      case ex: Exception =>
        if (tx.isActive)
          tx.rollback()
        throw ex
    }
  }
}
