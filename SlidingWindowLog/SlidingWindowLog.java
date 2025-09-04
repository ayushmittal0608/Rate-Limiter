// Reentrant Lock provides explicit locking, a thread can acquire same lock multiple times. 

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowLog {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final Deque<Long> requestTimestamps;
    private final ReentrantLock lock;

    SlidingWindowLog(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.requestTimestamps = new ArrayDeque<>();
        this.lock = new ReentrantLock();
    }

    public boolean allowRequest(){
        long now = System.currentTimeMillis();
        lock.lock();

        try {
            while(!requestTimestamps.isEmpty() && now-requestTimestamps.peekFirst()>=windowSizeMillis) {
                requestTimestamps.pollFirst();
            }
            if(requestTimestamps.size()<maxRequests){
                requestTimestamps.addLast(now);
                return true;
            }
            else{
                return false;
            }
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Max Requests: ");
        int request = sc.nextInt();
        System.out.println("Window Size: ");
        int size = sc.nextInt();
        SlidingWindowLog limiter = new SlidingWindowLog(request, size);
        System.out.println("Elapsed Time: ");
        int elapsedTime = sc.nextInt();
        for(int i=0; i<10; i++){
            System.out.println(limiter.allowRequest() ? "Allowed" : "Rate Limiter");
            Thread.sleep(elapsedTime);
        }
    }
}