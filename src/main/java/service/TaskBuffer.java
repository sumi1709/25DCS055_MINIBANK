package service;
import java.util.ArrayDeque;
import java.util.Queue;
public class TaskBuffer {
    private final Queue<Runnable> queue=new ArrayDeque<>(); private boolean closed=false;
    public synchronized void put(Runnable task){queue.add(task);notifyAll();}
    public synchronized Runnable take() throws InterruptedException {while(queue.isEmpty()&&!closed)wait();return queue.poll();}
    public synchronized void close(){closed=true;notifyAll();}
}
