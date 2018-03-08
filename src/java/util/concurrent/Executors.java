package util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池的工具类：
 */
public class Executors {

    public static ThreadFactory defaultThreadFactory() { return new DefualtThreadFactory(); }

    static class DefualtThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefualtThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
            namePrefix = "BeiwEi-" +
                          poolNumber.getAndIncrement() +
                          "-thread-";
        }

        @Override
        public Thread newThread(Runnable r){
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
