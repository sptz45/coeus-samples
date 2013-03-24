package blog
package web

import com.google.inject.Inject
import com.tzavellas.coeus.mvc._
import domain.{ User, UserDatabase }

@Path("")
class AuthenticationController @Inject() (userDb: UserDatabase) extends AbstractController {

  @Get
  def login() { }

  @Post("/login")
  def submit() = {
    try {
      val user = userDb.findByUsername(params("username")).get
      if (user.password == params("password")) {
        request.resetSession()
        session += user
        redirect("/admin/posts")
      } else {
        failure
      }
    } catch {
      case e: RuntimeException => failure
    }
  }

  @Get
  def logout() = {
    session.invalidate()
    redirect("login")
  }

  private val failure = redirect("/login", "error" -> true) 
}
