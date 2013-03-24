package blog.persistence

import blog.domain._

class JpaBlog extends AbstractRepository with Blog {

  def findPost(id: Int) = Option(em.find(classOf[Post], id))
  
  def allPosts() = resultSeq[Post](em.createQuery("select p from Post p"))
  
  def findPublishedPosts(tag: Option[String], offset: Int, limit: Int) = {
    
    def published = {
      val q = em.createQuery("select p from Post p where p.publicationDate is not null")
      q.setFirstResult(offset)
      q.setMaxResults(limit)
    }
    
    def publishedWithTag(tag: String) = {
      val q = em.createQuery("select p from Post p inner join p.tags as tags where p.publicationDate is not null and tags.name = ?1")
      q.setFirstResult(offset)
      q.setMaxResults(limit)
      q.setParameter(1, tag)
    }
    
    val query = tag match {
      case None      => published
      case Some(tag) => publishedWithTag(tag)
    }
    
    resultSeq[Post](query)
  }
  
  def deletePost(postId: Int) = transactional {
    deleteFromId(em, classOf[Post], postId)
  }
  
  def savePost(post: Post) = transactional {
    post.updateTimestamp()
    saveOrUpdate(em, post)
  }
}
