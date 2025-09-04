import java.util.*;

public class TokenBucket {
    private final long capacity;
    private final long refillRate;
    private double tokens;
    private long lastRefillTimestamp;

    public TokenBucket(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.nanoTime();
    }

    private void refill() {
        long now = System.nanoTime();
        double tokenToAdd = ((now-lastRefillTimestamp)/1e9)*refillRate;
        tokens = Math.min(capacity, tokens+tokenToAdd);
        lastRefillTimestamp = now;
    }

    public synchronized boolean allowRequest() {
        refill();
        if(tokens>=1) {
            tokens-=1;
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
        for(int i=0; i<10; i++){
            System.out.println(bucket.allowRequest() ? "Allowed": "Rate Limited");
            Thread.sleep(elapsedTime);
        }
    }
}