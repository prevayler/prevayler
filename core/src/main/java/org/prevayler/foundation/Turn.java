package org.prevayler.foundation;

/**
 * Used to control progression of threads through the stages of a processing
 * pipeline.
 */
public class Turn {

  /**
   * The first turn through a pipeline, allowed to flow freely through all
   * stages.
   */
  public static Turn first() {
    return new Turn(true);
  }

  private Turn _next;

  private int _allowed;

  private Turn(boolean first) {
    _next = null;
    _allowed = first ? Integer.MAX_VALUE : 0;
  }

  /**
   * The next turn through the pipeline, allowed to flow only as far as this
   * turn has already gone.
   *
   * @throws IllegalStateException if this or any preceding turn has been aborted.
   */
  public synchronized Turn next() {
    if (_allowed < 0) {
      throw new IllegalStateException("All transaction processing is now aborted, probably due to an earlier IOException.");
    }
    if (_next == null) {
      _next = new Turn(false);
    }
    return _next;
  }

  /**
   * Start a stage in the pipeline. Will block until the preceding turn has
   * ended the same stage.
   *
   * @throws IllegalStateException if this or any preceding turn has been aborted.
   */
  public synchronized void start() {
    while (_allowed == 0) {
      Cool.wait(this);
    }
    if (_allowed < 0) {
      throw new IllegalStateException("All transaction processing is now aborted, probably due to an earlier IOException.");
    }
    _allowed--;
  }

  /**
   * End a stage in the pipeline. Allows the next turn to start the same
   * stage.
   */
  public void end() {
    next().allow();
  }

  private synchronized void allow() {
    _allowed++;
    notifyAll();
  }

  /**
   * Abort the pipeline. Prevents this or any following turn from continuing,
   * but doesn't affect preceding turns already further along in the pipeline.
   *
   * @throws IllegalStateException always, with the given message and cause.
   */
  public void abort(String message, Throwable cause) {
    Turn turn = this;
    while (turn != null) {
      turn = turn.die();
    }
    throw new IllegalStateException(message, cause);
  }

  private synchronized Turn die() {
    _allowed = Integer.MIN_VALUE;
    notifyAll();
    return _next;
  }

}
