package blog.web

import com.google.inject.Inject
import com.tzavellas.coeus.mvc._
import blog.domain.{ User, UserDatabase }

@Path("/register")
class RegistrationController @Inject() (userDb: UserDatabase)
  extends AbstractController {
  
  override lazy val binder = new Binder(converters, denyVars = Seq("id"))

  @Get("")
  def showForm() {
    model += new User
  }
  
  @Post("")
  def handleSubmit() = {
  
    val user = new User
    val result = bindAndValidate(user)
    
    params.get("password2") match {
      case None =>
        result.addError("password2", msg("user.password2.empty"))
      case Some(password2) =>
        if (user.password != password2)
          result.addError("password2", msg("user.passwords.dont.match"))
    }

    if (userDb.findByUsername(user.username) != None)
      result.addError("username", msg("user.username.exists"))

    if (result.hasErrors) {
      render("register")
    } else {
      userDb.persistNewUser(user)
      redirect("/login")
    }
  }
}
