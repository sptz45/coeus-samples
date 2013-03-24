package blog.web.admin

import com.google.inject.Inject
import com.tzavellas.coeus.mvc._
import blog.domain.{ Tag, TagManager }

class TagsController @Inject() (tags: TagManager) extends AdminController {
  
  @Get("")
  def list() {
    request("tags") = tags.allTags
  }
  
  @Post("")
  def create() = {
    for (tag <- params.get("tag")) {
      if (!tags.allTags.exists(_.name.equalsIgnoreCase(tag)))
        tags.saveOrUpdate(new Tag(tag))
    }
    redirect("/admin/tags")
  }

  @Get("rename")
  def renameForm() = {
    val tag = tags.findById(params("id")).get
    ViewHelper.tagRenameForm(tag)
  }

  @Post
  def rename() = {
    val tag = tags.findById(params("id")).get
    tag.name = params("tag")
    tags.saveOrUpdate(tag)
    redirect("/admin/tags")
  }

  @Delete
  def delete() = {
    val id: Int = params("id")
    tags.delete(id)
    send(JsHelper.detachDeletedTableRow(id))
  }
}
