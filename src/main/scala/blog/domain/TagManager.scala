package blog.domain

trait TagManager {
  
  def findById(id: Int): Option[Tag]

  def allTags(): Seq[Tag]
  
  def saveOrUpdate(tag: Tag)
  
  def delete(tagId: Int)
}
