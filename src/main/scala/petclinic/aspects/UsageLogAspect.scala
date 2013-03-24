package petclinic.aspects

import scala.collection.mutable.ArrayBuffer
import org.aspectj.lang.annotation.{Aspect, Before}

/**
 * Sample AspectJ annotation-style aspect that saves
 * every owner name requested to the clinic.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
@Aspect
class UsageLogAspect {

  var historySize = 100

  // Of course saving all names is not suitable for
  // production use, but this is a simple example.
  var namesRequested = new ArrayBuffer[String](historySize)


  def setHistorySize(size: Int) = synchronized {
    historySize = size
    namesRequested = new ArrayBuffer[String](historySize)
  }

  @Before("execution(* *.findOwners(String)) && args(name)")
  def logNameRequest(name: String) = synchronized {
    // Not the most efficient implementation,
    // but we're aiming to illustrate the power of
    // @AspectJ AOP, not write perfect code here :-)
    if (namesRequested.size > historySize) {
      namesRequested.remove(0)
    }
    namesRequested += name
  }

  def getNamesRequested = synchronized { namesRequested.toList }
}