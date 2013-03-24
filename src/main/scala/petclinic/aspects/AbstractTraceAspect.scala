package petclinic.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation._
import org.slf4j.{Logger, LoggerFactory}

/**
 * Aspect to illustrate Spring-driven load-time weaving.
 *
 * @author Ramnivas Laddad
 * @since 2.5
 */
@Aspect
abstract class AbstractTraceAspect {

  private val logger = LoggerFactory.getLogger(classOf[AbstractTraceAspect])

  @Pointcut
  def traced()

  @Before("traced()")
  def trace(jpsp: JoinPoint.StaticPart) {
    if (logger.isTraceEnabled) {
      logger.trace("Entering " + jpsp.getSignature.toLongString)
    }
  }
}