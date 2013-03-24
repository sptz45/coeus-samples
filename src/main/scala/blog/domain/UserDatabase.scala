package blog.domain

trait UserDatabase {

  def persistNewUser(user: User)
  
  def delete(userId: Int)
  
  def findByUsername(username: String): Option[User]
  
  def allUsers(): Seq[User]
}
