import java.util.*;

public class SlidingWindowCounter {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final int subWindowCount;
    private final long subWindowSizeMillis;
    private final int[] counters;
    private long windowStart;

    public SlidingWindowCounter(int maxRequests, long windowSizeMillis, int subWindowCount) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.subWindowCount = subWindowCount;
        this.subWindowSizeMillis = windowSizeMillis / subWindowCount;
        this.counters = new int[subWindowCount];
        this.windowStart = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        long elapsed = now - windowStart;
        int elapsedWindows = (int)(elapsed / subWindowSizeMillis);

        if (elapsedWindows > 0) {
            for (int i = 0; i < Math.min(elapsedWindows, subWindowCount); i++) {
                counters[i] = 0;
            }
            windowStart += elapsedWindows * subWindowSizeMillis;
        }

        int total = 0;
        for (int count : counters) total += count;

        if (total < maxRequests) {
            int index = (int)((now - windowStart) / subWindowSizeMillis) % subWindowCount;
            counters[index]++;
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Max Requests: ");
        int req = sc.nextInt();
        System.out.println("Window Size: ");
        int size = sc.nextInt();
        System.out.println("Sub-window Count: ");
        int count = sc.nextInt();
        SlidingWindowCounter limiter = new SlidingWindowCounter(req, size, count);
        System.out.println("Elapsed Time: ");
        int elapsedTime = sc.nextInt();
        for (int i = 0; i < 10; i++) {
            System.out.println(limiter.allowRequest() ? "Allowed " : "Rate Limited ");
            Thread.sleep(elapsedTime);
        }
    }
}
