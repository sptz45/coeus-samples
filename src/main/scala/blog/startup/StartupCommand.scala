package blog.startup

import blog.persistence.PersistenceContext

abstract class StartupCommand {
  
  try {    
    execute()
  } finally {
    PersistenceContext.removeEntityManager()
  }
  
  def execute()
}
