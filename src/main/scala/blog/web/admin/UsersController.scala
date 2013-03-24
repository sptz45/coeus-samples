package blog.web.admin

import com.google.inject.Inject
import com.tzavellas.coeus.mvc._
import blog.domain.UserDatabase

class UsersController @Inject() (userDb: UserDatabase) extends AdminController {

  @Get("")
  def list() {
    request("users") = userDb.allUsers()
  }

  @Delete
  def delete() = {
    val id: Int = params("id")
    userDb.delete(id)
    send(JsHelper.detachDeletedTableRow(id))
  }
}
