# Rate-Limiter
When I was calling an Azure API to fetch all the subscriptions and its cost, I was getting an error showing "Error Code-429 | Too Many Requests" and I wondered that why am I facing such an error, then I discovered that Azure is using a rate limiter to limit the number of requests in order to prevent large number of requests to hit server resulting in system crash or slow down. It also has an application at the time of DDoS Attacks and also ensures smooth traffic. It prevents misuse of resources like CPU, memory, database or network.

# Reference
System Design Interview Part-1 | By Alex Xu (Chapter-4)

Another interesting thing that I have read which can be implemented to my project is CAS loop(Compare-And-Swap). It is actually an instruction supported by CPU and it helps in comparing the expected value by reading the data and swapping it to a new value. But, there is an issue that what if it is changed from 10 to 5 and then again to 10. Maybe we should use AtomicReference for it to compare data as it adds a version number and timestamp to it.

Library name -> (java.util.concurrent.atomic)
