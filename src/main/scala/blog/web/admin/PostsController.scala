package blog.web
package admin

import com.google.inject.Inject
import com.tzavellas.coeus.mvc.{ Post => HttpPost, _ }
import com.tzavellas.coeus.bind.converter.CollectionConverter
import blog.domain._

class PostsController @Inject() (
  blog: Blog,
  tags: TagManager) extends AdminController {
  
  override def storeModelInSession = true
  
  override lazy val binder = {
    val setConv = CollectionConverter.forHashSet(new TagConverter(tags)) 
    val convs = converters.add(classOf[java.util.Set[Tag]], setConv)
    new Binder(convs)
  }

  @Get
  def editor() = {
    model += (params.parse[Int]("id") match {
      case None     => new Post
      case Some(id) => blog.findPost(id).getOrElse(new Post)
    })
    formView
  }
    
  @HttpPost
  def save() = ifValid { post: Post =>
    post.author = session("user")
    blog.savePost(post)
    redirect("/admin/posts")
  }
    
  @Get("")
  def list() {
    request("posts") = blog.allPosts
  }
    
  @HttpPost
  def publish() = {
    for (post <- blog.findPost(params("id"))) {
      post.publish()
      blog.savePost(post)
    }
    send("Published")
  }
    
  @Delete
  def delete() = {
    val id: Int = params("id")
    blog.deletePost(id)
    send(JsHelper.detachDeletedTableRow(id))
  }
  
  override val formView = render("admin/post-editor")
}
