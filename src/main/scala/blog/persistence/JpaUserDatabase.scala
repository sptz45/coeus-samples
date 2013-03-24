package blog.persistence

import scala.collection.JavaConversions._
import blog.domain._

class JpaUserDatabase extends AbstractRepository with UserDatabase {

  def persistNewUser(user: User) = transactional {
    em.persist(user)
  }
  
  def delete(userId: Int) = transactional {
    deleteFromId(em, classOf[User], userId)
  }
  
  def findByUsername(username: String) = {
    val query = em.createQuery("select u from User u where u.username = ?1")
                  .setParameter(1, username)
    singleResult[User](query)
  }
  
  def allUsers(): Seq[User] = {
    resultSeq[User](em.createQuery("select u from User u"))
  }
}
