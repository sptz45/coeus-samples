package blog
package web

import com.google.inject.Inject
import com.tzavellas.coeus.mvc._
import domain.{ Comment, Blog }

@Path("")
class BlogController @Inject() (blog: Blog) extends AbstractController {
  
  @Get("/")
  def index() = {
    request("posts") = loadPosts()
    "blog"
  }
  
  @Get("/feed.xml")
  def atomFeed() = new AtomView(settings, loadPosts())
  
  @Get
  def post() = blog.findPost(params("id")) match { 
    case Some(post) =>
      request("post") = post 
      model += new Comment
      "post"
    case None =>
      response.status = 404
      rendered
  }

  @Post
  def comment() = {
    val result = bindAndValidate(new Comment)
    if (result.hasErrors) {
      request("post") = blog.findPost(params("postId")).get
      render("post")
    } else {
      val post = blog.findPost(params("postId")).get
      post.addComment(result.target)
      blog.savePost(post)
      redirect("/post", "id" -> post.id)
    }
  }
  
  private def loadPosts() = {
    val postsPerPage = settings.postsPerPage
    val page = params.parse[Int]("page").getOrElse(0)
    blog.findPublishedPosts(params.get("tag"), page * postsPerPage, postsPerPage)
  }
  
  private def settings = SettingsHolder.get(application)
}
