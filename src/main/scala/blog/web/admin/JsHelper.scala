package blog.web.admin

object JsHelper {
  
  def detachDeletedTableRow(id: Any) = {
    "$('td a[data-method=delete]').filter('[href$=\"id="+id+"\"]').closest('tr').detach()"
  }

}
