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
