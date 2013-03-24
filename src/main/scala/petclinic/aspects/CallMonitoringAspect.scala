package petclinic.aspects

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect}

import org.springframework.jmx.export.annotation._
import org.springframework.util.StopWatch

/**
 * Simple AspectJ aspect that monitors call count and call invocation time.
 * Implements the CallMonitor management interface.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.5
 */
@Aspect
@ManagedResource("petclinic:type=CallMonitor")
class CallMonitoringAspect {

  private var enabled = true
  private var callCount = 0
  private var accumulatedCallTime: Long = 0


  @ManagedAttribute
  def setEnabled(enabled: Boolean) {
    this.enabled = enabled
  }

  @ManagedAttribute
  def isEnabled = enabled

  @ManagedOperation
  def reset() = synchronized {
    callCount = 0
    accumulatedCallTime = 0
  }

  @ManagedAttribute
  def getCallCount = callCount

  @ManagedAttribute
  def getCallTime = if (callCount > 0) accumulatedCallTime / callCount else 0


  @Around("within(@org.springframework.stereotype.Service *)")
  @throws(classOf[Throwable])
  def invoke(joinPoint: ProceedingJoinPoint): Any = {
    if (enabled) {
      val sw = new StopWatch(joinPoint.toShortString)
      sw.start("invoke")

      try {
        return joinPoint.proceed()

      } finally {
        sw.stop()
        this synchronized {
          callCount += 1
          accumulatedCallTime += sw.getTotalTimeMillis
        }
      }
    } else {
      joinPoint.proceed()
    }
  }
}
