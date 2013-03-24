package blog.persistence

import com.tzavellas.coeus.core.RequestContext
import com.tzavellas.coeus.core.interception.Interceptor

class PersistenceContextInterceptor extends Interceptor {

  def preHandle(context: RequestContext) = true
  
  def postHandle(context: RequestContext) { }
  
  def afterRender(context: RequestContext) {
    PersistenceContext.removeEntityManager()
  }
}
