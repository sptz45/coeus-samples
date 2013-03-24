package petclinic
package web

import java.util.Locale
import com.tzavellas.coeus.bind.Converter

class PetTypeConverter(clinic: Clinic) extends Converter[PetType] {

  def format(pt: PetType, locale: Locale) = pt.name 
  
  def parse(text: String, locale: Locale) = {
    val result = clinic.getPetTypes.find(_.name == text)
    require(result != None)
    result.get
  }
}