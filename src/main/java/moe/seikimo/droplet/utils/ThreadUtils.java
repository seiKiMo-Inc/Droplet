package moe.seikimo.droplet.utils;

public interface ThreadUtils {
    /**
     * Sleeps the current thread for the given amount of time.
     *
     * @param time The time to sleep.
     */
    static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Runs the given runnable after the given delay.
     *
     * @param runnable The runnable to run.
     * @param delay The delay.
     */
    static void runAfter(Runnable runnable, long delay) {
        new Thread(() -> {
            sleep(delay);
            runnable.run();
        }).start();
    }
}
