package blog.web.admin

import com.tzavellas.coeus.mvc.scope.ScopeAccessor
import com.tzavellas.coeus.mvc.view.helper.FormHelper
import blog.domain.Tag

object ViewHelper {
  
  object formHelper extends FormHelper

  def tagRenameForm(tag: Tag)(implicit scopes: ScopeAccessor) = {
    <div>
    <h3>Enter a new name for tag</h3>
    <form action="/admin/tags/rename" method="post">
    <input type="hidden" name="id" value={tag.id.toString} />
    {formHelper.csrf_token}
    <input type="text" name="tag" value={tag.name}/>
    <input type="submit" value="Rename"/>
    </form>
    </div>
  }
}
