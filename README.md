# Rate-Limiter
When I was calling an Azure API to fetch all the subscriptions and its cost, I was getting an error showing "Error Code-429 | Too Many Requests" and I wondered that why am I facing such an error, then I discovered that Azure is using a rate limiter to limit the number of requests in order to prevent large number of requests to hit server resulting in system crash or slow down. It also has an application at the time of DDoS Attacks and also ensures smooth traffic. It prevents misuse of resources like CPU, memory, database or network.

# Upgraded Version of Rate Limiter (Inside Token Bucket)
- Inside token bucket, I have upgraded my code to run a CPU monitor and starting a new thread where we are fetching CPU usage every 200ms and storing it to our deque, CpuHistory. Now, we have taken a size of cpuHistory as 5 where we are allowing 5 values of CPU usage, where every time CPU usage goes out of size, we are polling first value out of deque and adding cpu usage at last of deque. This way we are getting real time CPU values stored in a deque, detecting our cpu spike every 200ms.
- Now, we have a whole set of CPU usage values, so we calculate every spike value and find out average of all spikes and if they are more than 0.05, then we are predicting that the spike can go up with time and we initiate predictive throttling.
- Now, if we get the spike average as less than 0.05, then we will move forward and get latest cpu usage and then initiate reactive throttling.
- Now, predictive throttling and reactive throttling are just terms, what we are doing is adjusting our rate limiter's refill rate in such a way that CPU spike wouldn't effect our system and we would get an efficient adaptive rate limiter.
- So, firstly, we need to predict as per the average spike that whether cpu is spiking or not and if it is true, we adjust the refill rate to 0.4 * refillRate
- Now, if it is not, then maybe we get some cpu usage value which gradually make it worse and cpu spikes up, then we adjust the refill rate as per spike, whether is more than 80%, 60% or 40%, between 40 and 60, it is normal spike rate but still we optimise refill rate more to get better request handling. Above 80% is a risky zone, because then we need to scale the instance or use load balancer for better traffic distribution.
- The next thing is how we are ordering between predictive and reactive throttling, since more than 80% spike is dangerous, we need to consider reactive first, then predictive and then we can use reactive for 40% or 60% spike because that is not causing much trouble in the upcoming go.

# Reference
System Design Interview Part-1 | By Alex Xu (Chapter-4)

Another interesting thing that I have read which can be implemented to my project is CAS loop(Compare-And-Swap). It is actually an instruction supported by CPU and it helps in comparing the expected value by reading the data and swapping it to a new value. But, there is an issue that what if it is changed from 10 to 5 and then again to 10. Maybe we should use AtomicReference for it to compare data as it adds a version number and timestamp to it.

Library name -> (java.util.concurrent.atomic)
