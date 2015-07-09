package conversion7.engine.utils;

/*
Copyright 2011 Karl-Michael Schneider

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import static java.lang.String.format;

/**
 * A class to wait until a condition becomes true. Periodically calls {@link #until()} and sleeps
 * for a specified amount of time if it returns false. If {@link #until()} does not return true
 * within the timeout, throws a {@link TimedOut} exception. If the timeout is 0, it may wait
 * forever, and a {@link TimedOut} exception is never thrown.
 * <p/>
 * A concrete subclass must implement {@link #until()}.
 */
public abstract class Waiting {

    public static final int DEFAULT_TIMEOUT = 0;
    public static final int DEFAULT_SLEEP = 1000;
    public static final String DEFAULT_TIMEOUT_MESSAGE = "wait timed out";

    protected int timeout;
    protected int sleep;
    protected String timeoutMessage;

    /**
     * Creates and instance with the default settings.
     */
    public Waiting() {
        this(DEFAULT_TIMEOUT, DEFAULT_SLEEP, DEFAULT_TIMEOUT_MESSAGE);
    }

    /**
     * Creates an instance with the specified timeout message.
     *
     * @param timeoutMessage the message in the {@link TimedOut} exception
     */
    public Waiting(String timeoutMessage) {
        this(DEFAULT_TIMEOUT, DEFAULT_SLEEP, timeoutMessage);
    }

    /**
     * Creates an instance that waits at most the specified time.
     *
     * @param timeout the timeout in milliseconds.
     */
    public Waiting(int timeout) {
        this(timeout, DEFAULT_SLEEP, DEFAULT_TIMEOUT_MESSAGE);
    }

    /**
     * Creates an instance that waits at most the specified time.
     *
     * @param timeout        the timeout in milliseconds.
     * @param timeoutMessage the message in the {@link TimedOut} exception
     */
    public Waiting(int timeout, String timeoutMessage) {
        this(timeout, DEFAULT_SLEEP, timeoutMessage);
    }

    /**
     * Creates an instance that waits at most the specified time and checks the condition after the
     * specified time.
     *
     * @param timeout the timeout in milliseconds
     * @param sleep   the time to sleep between calling {@link #until()}, in milliseconds
     */
    public Waiting(int timeout, int sleep) {
        this(timeout, sleep, DEFAULT_TIMEOUT_MESSAGE);
    }

    /**
     * Creates an instance that waits at most the specified time and checks the condition after the
     * specified time.
     *
     * @param timeout        the timeout in milliseconds
     * @param sleep          the time to sleep between calling {@link #until()}, in milliseconds
     * @param timeoutMessage the message in the {@link TimedOut} exception
     */
    public Waiting(int timeout, int sleep, String timeoutMessage) {
        this.timeout = timeout;
        this.sleep = sleep;
        this.timeoutMessage = timeoutMessage;
    }

    /**
     * Waits until {@link #until()} returns true.
     *
     * @throws TimedOut  if {@link #until()} does not return true within in the timeout.
     * @throws Exception if {@link #until()} throws an exception
     */
    public void waitUntil()
            throws TimedOut, Exception {
        waitUntil(this.timeout, this.sleep, this.timeoutMessage);
    }

    /**
     * Waits until {@link #until()} returns true.
     *
     * @param timeoutMessage the timeout message, overrides the message in the constructor
     * @throws TimedOut  if {@link #until()} does not return true within the timeout
     * @throws Exception if {@link #until()} throws an exception
     */
    public void waitUntil(String timeoutMessage)
            throws TimedOut, Exception {
        waitUntil(this.timeout, this.sleep, timeoutMessage);
    }

    /**
     * Waits until {@link #until()} returns true.
     *
     * @param timeout the timeout in milliseconds, overrides the timeout in the constructor
     * @throws TimedOut  if {@link #until()} does not return true within the timeout
     * @throws Exception if {@link #until()} throws an exception
     */
    public void waitUntil(int timeout)
            throws TimedOut, Exception {
        waitUntil(timeout, this.sleep, this.timeoutMessage);
    }

    /**
     * Waits until {@link #until()} returns true.
     *
     * @param timeout        the timeout in milliseconds, overrides the timeout in the constructor
     * @param timeoutMessage the timeout message, overrides the message in the constructor
     * @throws TimedOut  if {@link #until()} does not return true within the timeout
     * @throws Exception if {@link #until()} throws an exception
     */
    public void waitUntil(int timeout, String timeoutMessage)
            throws TimedOut, Exception {
        waitUntil(timeout, this.sleep, timeoutMessage);
    }

    /**
     * Waits until {@link #until()} returns true.
     *
     * @param timeout the timeout in milliseconds, overrides the timeout in the constructor
     * @param sleep   the time between calls to {@link #until()}, in milliseconds, overrides the value
     *                defined in the constructor
     * @throws TimedOut  if {@link #until()} does not return true within the timeout
     * @throws Exception if {@link #until()} throws an exception
     */
    public void waitUntil(int timeout, int sleep)
            throws TimedOut, Exception {
        waitUntil(timeout, sleep, this.timeoutMessage);
    }

    /**
     * Waits until {@link #until()} returns true.
     *
     * @param timeoutMs      the timeout in milliseconds, overrides the timeout in the constructor
     * @param sleep          the time between calls to {@link #until()}, in milliseconds, overrides the value
     *                       defined in the constructor
     * @param timeoutMessage the timeout message, overrides the message in the constructor
     * @throws TimedOut  if {@link #until()} does not return true within the timeout
     * @throws Exception if {@link #until()} throws an exception
     */
    public void waitUntil(final int timeoutMs, final int sleep, final String timeoutMessage)
            throws Exception {
        long startedAt = System.currentTimeMillis();
        long timePassed;
        while (true) {
            if (this.until()) {
                break;
            }
            timePassed = System.currentTimeMillis() - startedAt;
            if (timeoutMs > 0 && timePassed >= timeoutMs) {
                throw new TimedOut(format("timePassed=%d ms. %s", timePassed, timeoutMessage));
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Indicates that the condition is true.
     *
     * @return true if the condition is true
     * @throws Exception if an error occurs while checking the condition
     */
    public abstract boolean until()
            throws Exception;

    /**
     * Exception thrown when the condition does not become true within the timeout.
     */
    public class TimedOut
            extends Exception {

        private static final long serialVersionUID = 1L;

        public TimedOut(String message) {
            super(message);
        }
    }
}

