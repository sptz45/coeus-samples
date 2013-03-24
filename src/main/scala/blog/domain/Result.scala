package blog.persistence

/**
 * Represents the result of a query.
 * 
 * <p>The query may or may not have been executed until the user calls
 * the <code>get()</code> method on the <code>Result</code> object.</p>
 */
trait Result[T] {
  
  /** Get the result(s) if any. */
  def get(): T
  
  /** Set the first result. */
  def from(firstResult: Int): Result[T]
  
  /** Set the maximum number of results. */
  def max(maxResults: Int): Result[T]
}
