import util.concurrent.Executor;
import util.concurrent.ExecutorService;
import util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class T1 {
    public static void main(String[] args) {
        /**
         * 三个构造方法：
         */


        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(1);
        /**
         * 核心线程池大小：3
         * 最大线程池大小：6
         * 超时时间：50s
         * 任务队列：5
         */
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                3, 6, 10,
                TimeUnit.SECONDS, workQueue);
        /**
         * 一、执行3个任务，查看线程池状态
         */

//        pool.allowCoreThreadTimeOut(true);

        for (int i = 0; i < 3; i++) {
            pool.submit(new SayHello());
        }


        try{
            Thread.sleep(15000);
        } catch (Exception e){}

        System.out.println("HHHH");
        pool.shutdown();
    }
}

    /**
     * 任务，每10s打印一次
     */
    class SayHello implements Runnable {
        private static int id = 0;
        private int name ;

        SayHello(){
            name = id++;
        }

        @Override
        public void run() {
            int i = 0;
            while(i < 2) {

                System.out.println("任务" + name + ":说你好~");
                i++;
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                }
            }
        }
    }
