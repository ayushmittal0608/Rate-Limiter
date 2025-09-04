# Rate-Limiter
When I was calling an Azure API to fetch all the subscriptions and its cost, I was getting an error showing "Error Code-429 | Too Many Requests" and I wondered that why am I facing such an error, then I discovered that Azure is using a rate limiter to limit the number of requests in order to prevent large number of requests to hit server resulting in system crash or slow down. It also has an application at the time of DDoS Attacks and also ensures smooth traffic. It prevents misuse of resources like CPU, memory, database or network.

# Reference
System Design Interview Part-1 | By Alex Xu (Chapter-4)
