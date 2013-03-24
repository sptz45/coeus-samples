package blog

import javax.servlet.ServletContext
import javax.persistence.{ Persistence, EntityManagerFactory }
import com.google.inject.{ Singleton, Provides } 
import com.tzavellas.sse.guice.ScalaModule
import com.tzavellas.coeus.mvc.controller.Controller
import com.tzavellas.coeus.core.config.DispatcherConfig

import persistence._
import domain._
import startup._
import web._

class GuiceModule(dispatcherConfig: DispatcherConfig) extends ScalaModule {
  
  def configure() {
    // Servlet classes
    bind[ServletContext].toInstance(dispatcherConfig.servletContext)
    
    // Repositories
    bind[Blog].to[JpaBlog].in[Singleton]
    bind[TagManager].to[JpaTagManager].in[Singleton]
    bind[UserDatabase].to[JpaUserDatabase].in[Singleton]
    bind[SettingsPersister].to[JpaSettingsPersister].in[Singleton]
    
    // Controllers
    controller[BlogController]
    controller[AuthenticationController]
    controller[RegistrationController]
    controller[admin.UsersController]
    controller[admin.PostsController]
    controller[admin.TagsController]
    controller[admin.SettingsController]
    
    // Startup commands
    startupCommand[SettingsInitializer]
  }
  
  
  // -- Providers -------------------------------------------------------------
  
  @Provides
  def entityManagerFactory = Persistence.createEntityManagerFactory("coeus-blog")
  
  
  // -- Helper methods --------------------------------------------------------
  
  def controller[C <: Controller](implicit c: Manifest[C]) = {
    bind(c.runtimeClass).asEagerSingleton()
  }
  
  def startupCommand[C <: StartupCommand](implicit c: Manifest[C]) = {
    bind(c.runtimeClass).asEagerSingleton()
  }
}
