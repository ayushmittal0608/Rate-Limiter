import java.util.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class TokenBucket {
    private final long capacity;
    private final long refillRate;
    private double tokens;
    private long lastRefillTimestamp;

    private volatile double currentCpuLoad = 0.0;
    private final Deque<Double> cpuHistory = new ArrayDeque<>();
    private static final int HISTORY_SIZE = 5;

    public TokenBucket(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.nanoTime();
        startCpuMonitor();
    }

    private void startCpuMonitor() {
        new Thread(() -> {
            while(true) {
                double cpu = getCpuUsage();
                currentCpuLoad = cpu;

                synchronized (cpuHistory) {
                    if(cpuHistory.size() == HISTORY_SIZE) {
                        cpuHistory.pollFirst();
                    }
                    cpuHistory.addLast(cpu);
                }

                try {
                    Thread.sleep(200);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private double getCpuUsage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osBean.getSystemCpuLoad();
    }

    private boolean isCpuSpiking() {
        synchronized (cpuHistory) {
            if(cpuHistory.size() < 2) {
                return false;
            }
            Iterator<Double> it = cpuHistory.iterator();
            double prev = it.next();
            double totalIncrease = 0;

            while (it.hasNext()) {
                double curr = it.next();
                totalIncrease += (curr - prev);
                prev = curr;
            }

            double avgIncrease = totalIncrease / (cpuHistory.size() - 1);
            return avgIncrease > 0.05;
        }
    }

    private double getAdaptiveRefillRate() {
        double cpu = currentCpuLoad;
        if (cpu > 0.8) {
            return refillRate * 0.3;
        }
        if (isCpuSpiking()) {
            return refillRate * 0.4;
        }
        if (cpu > 0.6) {
            return refillRate * 0.5;
        }
        if (cpu > 0.4) {
            return refillRate * 0.8;
        }
        return refillRate;
    }

    private void refill() {
        long now = System.nanoTime();
        double adaptiveRate = getAdaptiveRefillRate();
        double tokenToAdd = ((now - lastRefillTimestamp) / 1e9) * adaptiveRate;
        tokens = Math.min(capacity, tokens + tokenToAdd);
        lastRefillTimestamp = now;
    }

    public synchronized boolean allowRequest() {
        refill();
        if(tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Number of Tokens: ");
        int capacity = sc.nextInt();
        System.out.println("Refill Rate: ");
        int rate = sc.nextInt();
        TokenBucket bucket = new TokenBucket(capacity, rate);
        System.out.println("Elapsed Time: ");
        int elapsedTime = sc.nextInt();
        for (int i = 0; i < 10; i++) {
            System.out.println(bucket.allowRequest() ? "Allowed": "Rate Limited");
            Thread.sleep(elapsedTime);
        }
    }
}
