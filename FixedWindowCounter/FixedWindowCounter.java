import java.util.*;

public class FixedWindowCounter {
    public final int maxRequests;
    public final long windowSizeMillis;
    private int requestCount;
    private long windowStart;

    FixedWindowCounter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.requestCount = 0;
        this.windowStart = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        if(now-windowStart>=windowSizeMillis) {
            windowStart = now;
            requestCount = 0;
        }
        if(requestCount<maxRequests) {
            requestCount++;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        FixedWindowCounter limiter = new FixedWindowCounter(5, 1000);
        for(int i=0; i<10; i++){
            System.out.println(limiter.allowRequest() ? "Allowed" : "Rate Limiter");
            Thread.sleep(100);
        }
    }
}