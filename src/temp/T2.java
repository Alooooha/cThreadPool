

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class T2 {
    public static void main(String[] args) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(1);

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                3, 6, 10,
                TimeUnit.SECONDS, workQueue);

        for (int i = 0; i < 3; i++) {
            pool.submit(new SayHello());
        }
    }
}
