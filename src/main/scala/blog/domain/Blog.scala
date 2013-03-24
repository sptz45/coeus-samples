package blog.domain

trait Blog {

  def findPost(id: Int): Option[Post]
  
  def allPosts(): Seq[Post]
  
  def findPublishedPosts(tag: Option[String], offset: Int, limit: Int): Seq[Post]
  
  def deletePost(postId: Int)
  
  def savePost(post: Post)
}
