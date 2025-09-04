import java.util.*;
import java.util.concurrent.TimeUnit;

public class LeakingBucket {
    private final double capacity;
    private final double leakRate;
    private double finalWater;
    private long leastLeakTimestamp;

    LeakingBucket(double capacity, double leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.finalWater = 0;
        this.leastLeakTimestamp = System.nanoTime();
    }

    public void leak() {
        long now = System.nanoTime();
        double leaked = ((now-leastLeakTimestamp)/1e9)*leakRate;
        finalWater = Math.max(0, finalWater-leaked);
        leastLeakTimestamp = now;
    }

    public synchronized boolean allowRequest() {
        leak();
        if(finalWater<capacity){
            finalWater+=1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Number of Tokens: ");
        int capacity = sc.nextInt();
        System.out.println("Leaking Rate: ");
        int rate = sc.nextInt();
        LeakingBucket bucket = new LeakingBucket(capacity, rate);
        System.out.println("Elapsed Time: ");
        int elapsedTime = sc.nextInt();
        for(int i=0; i<10; i++){
            System.out.println(bucket.allowRequest() ? "Allowed" : "Rate Limiter");
            Thread.sleep(elapsedTime);
        }
    }
}