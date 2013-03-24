package blog.persistence

import blog.domain._

class JpaSettingsPersister extends AbstractRepository with SettingsPersister {
  
  def initialize() = load() match {
    case None =>
      save(new Settings)
      load().get
    case Some(settings) =>
      settings
  }
  
  def save(settings: Settings) = transactional {
    em.merge(settings)
  }
  
  private def load() = {
    singleResult[Settings](em.createQuery("select s from Settings s"))
  }
}
