package blog.web

import scala.xml.XML
import com.tzavellas.coeus.mvc.{ Post => _, _ }
import blog.domain._

class AtomView(settings: Settings, posts: Seq[Post]) extends View {

  def contentType = "application/atom+xml"
  
  def render(request: WebRequest, response: WebResponse) {
    
    val feed =
    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>{settings.title}</title>
      <subtitle>{settings.description}</subtitle>
      <link href={blogUri(request)}/>
      <link href={blogUri(request) + "/feed.atom"} rel="me"/>
      <author>
        <name>author</name>
      </author>
      <id>{blogUri(request)}</id>
      { for (post <- posts) yield
      <entry>
        <title>{post.title}</title>
        <link href={blogUri(request) + "/post?id=" + post.id} rel="alternate"/>
        <id>{post.id}</id>
        <updated>{atomDate(post.timestamp)}</updated>
        <content type="html">{post.content}</content>
       </entry>
      }
    </feed>
    
    XML.write(response.writer, feed, "utf-8", true, null)
  }
  
  private def blogUri(request: WebRequest) = {
    val sr = request.servletRequest
    def port = if (sr.getServerPort == 80) "" else ":" + sr.getServerPort
    def protocol = if (sr.isSecure) "https://" else "http://"
    protocol + sr.getServerName + port + request.application.contextPath
  }
  
  private def atomDate(date: java.util.Date) = {
    new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(date)
  }
}
