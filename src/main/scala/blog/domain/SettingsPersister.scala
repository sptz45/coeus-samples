package blog.domain

trait SettingsPersister {
  
  def initialize(): Settings  

  def save(settings: Settings): Settings
}
