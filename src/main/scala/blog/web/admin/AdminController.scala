package blog.web.admin

import com.tzavellas.coeus.mvc._
import com.tzavellas.coeus.http.security.CsrfProtection

abstract class AdminController extends AbstractController with BeforeFilter {

  override def before() = {
    CsrfProtection.assertOrigin(request)
    if (!session.contains("user")) stopAndRender(redirect("/login"))
  }
}
