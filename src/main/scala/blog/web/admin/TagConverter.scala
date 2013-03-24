package blog.web.admin

import java.util.Locale
import com.tzavellas.coeus.bind.Converter
import blog.domain.{ Tag, TagManager }


class TagConverter(tags: TagManager) extends Converter[Tag] {

  def format(value: Tag, locale: Locale) = value.name 

  def parse(text: String, locale: Locale) = {
    val name = text.trim
    tags.allTags.find(_.name equalsIgnoreCase name).getOrElse(new Tag(name))
  }
}
