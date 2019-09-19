# cThreadPool
<br>项目描述：对java.util.concurrent包下线程池相关源码进行重新实现，深入研究和学习线程池超时机制、饱和策略、生命周期等知识<br>
<br>ThreadPoolExecutor类下部分方法和内部类介绍：

<br>1、Worker类：
-
*描述：Worker类实现Runnable接口、继承AbstractQueuedSynchronizer类*
<br>
><br>Thread thread : *工作线程，用于处理任务*
<br>Runnable firstTask : *第一个任务，当线程池worker对象达到corePoolSize且workQueue满时，worker对象的firstTask为null*
<br>run（）: *调用runWorker（Worker w）*
<br>*还有一些线程安全，加锁、解锁的方法，不细讲*
<br>

<br>2、void execute(Runnable command):
-
*描述：该方法有对于Callable、Runnable的重载方法，线程池状态不同，有四种执行策略*
><br>处理任务的四种策略：
<br>1、当前线程数 < 核心线程数，添加新的worker并执行任务
<br>2、当前线程数 >= 核心线程数 && workQueue未满，将任务放入workQueue
<br>3、当前线程数 < 最大允许线程数 && workQueue饱和，添加新的worker并从队列获取任务
<br>4、当前线程数 >= 最大允许线程数 && workQueue饱和，执行饱和策略（后面有讲）
<br>

<br>3、boolean addWorker(Runnable firstTask, boolean core):
-
*描述：在线程池中添加worker*
><br>boolean core : 定义线程池最大保留worker数，ture为核心线程数，false为最大线程数

<br>过程：将worker对象添加到workers中，调用worker.thread.start（）方法。这里有个重点，Worker在初始化thread时将自身对象传入其中，由于Worker实现了Runable接口，并复写了run(){ runWorker(this);}。我之前一直以为thread应该执行的是workQueue的任务，却没想到执行的是Worker本身。不过仔细相信也是，每个Worker都应该是独立的线程，由他们去处理任务，这种方法值得学习。
<br>

<br>4、void runWorker(Worker w):
-
*描述：执行worker任务*
><br>boolean completeAbruptly : 线程是否在运行状态中异常退出
<br>Runnable task : worker执行的任务，初始值为firstTask

<br>Worker工作原理：方法中有个while方法，如果task不为空或者getTask（）返回值不为空，将执行task.run()。执行task前后有beforeExecute和afterExecute方法，但是未实现具体内容。
<br>

<br>5、Runnable getTask():
-
*描述：从workQueue中获取任务，该方法可以阻塞*
><br>boolean timeOut : 状态量，初始值为false，当第一次失败取得任务时，变成true
<br>boolean allowCoreThreadTimeOut : 全局变量，是否允许核心线程超时退出，默认为false
<br>boolean timed : allowCoreThreadTimeOut || wc > corePoolSize，用于判断当前worker是否为核心线程
<br>

<br>过程：如果当前线程为核心线程，且allowCoreThreadTimeOut==false，调用workQueue.take()。Worker不是核心线程时，将采用workQueue.poll()方法，指定超时时间为keepAliveTime。

<br>6、processWorkerExit(Worker w, boolean completedAbruptly):
-
*描述：worker结束前调用该方法，用于维护线程池状态*
><br>int completedTaskCount : 线程池完成的总任务数
<br>volatile long completedTasks : worker完成的任务数
<br>

<br>过程：在runWorker方法中得到completedAbruptly值，如果值为true，代表异常退出，此时workerCount-1。然后将worker完成的任务增加到线程池的总任务数。接着调用tryTerminate方法，尝试将线程池状态修改为Terminate。最后一步很重要，动态平衡线程池中worker数，如果线程池允许将worker数保持在corePoolSize，将判断当前worker数是否饱和，如果未饱和，将增加空闲worker。如果线程池不允许空闲线程的存在，将判断当前workQueue是否为空，不为空，至少保证线程池有一个worker执行任务，而不是关闭所有worker。

<br>7、AbortPolicy类
-
*描述：默认饱和策略，拒绝任务，抛出异常。实现RejectedExecutionHandler*
<br>
<br>四种饱和策略：
<br>AbortPolicy：在饱和时抛出RejectedExecutionException
<br>DiscardPolicy:抛弃任务，不做任何处理
<br>DiscardOldestPolicy:将任务队列头部任务抛弃，再尝试提交任务
<br>CallerRunsPolicy:由主线程执行任务，但是在这过程中线程池无法接收其他任务
<br>

<br>8、void shutdown():
-
*描述：不接受新的任务，执行完所有任务后尝试关闭线程池*
><br>checkShutdownAccess():检查当前线程是否可以修改worker线程
<br>advanceRunState(SHUTDOWN):修改当前线程池运行状态，不接受新的任务
<br>interruptIdleWOrkers():中断所有空闲线程
<br>

<br>9、void shutdownNow():
-
*描述：中断所有worker，不再执行workQueue中的任务*
><br>interruptWorkers():中断所有worker，包括工作中的worker
<br>drainQueue():清空workQueue，并得到队列任务
<br>

<br>10、boolean awiatTermination(long timeout, TimeUnit unit):
-
*描述：阻塞，直到线程池退出，可设置等待时间。*
<br>因为shutdown()、shutdownNow()方法不会主动等待执行任务的结束，有时候主线程调用
<br>shutdown后，代码执行结束，主线程关闭。而线程池还在等待任务执行完毕（豆瓣爬虫项
<br>目就有这个情况）,这时候我们就可以使用awaitTermination()来等待线程池结束，再由
<br>主线程结束整个程序。
<br>

<br>ThreadPoolExecutor():
-
*描述：构造方法*
<br>int corePoolSize:核心线程数
<br>int maximumPoolSize:最大线程数
<br>long keepAliveTime:存活时间
<br>TimeUnit unit:时间单位
<br>BlockingQueue<Runnable> workQueue:任务队列
<br>ThreadFactory threadFactory:线程工厂，null时采用默认线程工厂
<br>RejectedExecutionHandler handler:饱和策略，默认使用AbortPolicy
