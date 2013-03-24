package blog.persistence

import javax.persistence._

object PersistenceContext {

  private val holder = new ThreadLocal[EntityManager]
  
  def getEntityManager(emf: EntityManagerFactory) = {
    val current = holder.get
    if (current eq null) {
      val em = emf.createEntityManager()
      holder.set(em)
      em
    } else {
      current
    }
  }

  def removeEntityManager() {
    val em = holder.get
    if (em ne null) {
      em.close()
      holder.remove()
    }
  }
}
