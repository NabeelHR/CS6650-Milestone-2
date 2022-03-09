# Lab 6 Report

GitHub repo: https://github.com/dishant9397/BSDS

[TOC]

## 1 Environment

We are using: Week-3-server + Week-4-client + Week-6-consumer

Local URL: http://localhost:8080/Week_3_Server_war_exploded/



**RabbitMQ management console**

- Local

​		http://localhost:15672/#/

​		Username: guest

​		password: guest

- EC2 instance

  http://{IPaddress}:15672/#/

  Username: admin

  password: admin



## 2 Architecture Diagram

//TODO



## 3 Test

### 3. 1 Test different numbers of threads in the consumer 

We did this test with client configuration as: numofThreads = 64, numSkiers=20000, and numLifts=40.

And below are our results of testing regarding the **numbers of messages that be processed per second** and the n**umbers of threads in consumer.**

![image](https://github.com/dishant9397/BSDS/blob/master/Week-6-Consumer/res/wallTimeAndThroughput.png)

It's obviously shown in this graph that with more consumers in the rabbitmq server, the number of messages that the consumer is able to consume per second is greater. Ideally, if we are using 128 threads, the number of messages be processed each second should be exactly as twice as that would be done with 64 threads. How to reach this ideal number is definitely in our future work.



### 3.2 Test different numbers of threads in the client

In the test part, we are using a **load balancer** to split the work for 4 instances of server on EC2.  

The client would use URL as  `http://NetworkLoadBalancer-53d8101f64200693.elb.us-east-1.amazonaws.com:8080/Week_3_Server_war` to send multiple post requests to the load balancer, handled by the load balancer to decide which server of four would take care of each request.

Below are the graphic results of our testing regarding **wall time and throughput** when using **different numbers of threads in the client**. In our consumer, we are using a 10 threads consumer to pull messages off the queue. 



![image](https://github.com/dishant9397/BSDS/blob/master/Week-6-Consumer/res/rabbitmqRateAnalysis.png)

We can tell from the above image that with the increase in the number of treads, the wall time to execute the entire program is decreasing and this is happening because we have overlapping threads and much of the threads would still be in the queue before the rest of them are reached. Also, the throughput is increasing as more numbers of threads are working together and the response time is less and less. 





### 3.3 Contrary between Local rabbitmq Server and EC2 instance

**Local rabbitmq server**

![image](https://github.com/dishant9397/BSDS/blob/master/Week-6-Consumer/res/localRabbitmq.png)



**EC2 rabbitmq server** 

![image](https://github.com/dishant9397/BSDS/blob/master/Week-6-Consumer/res/EC2Rabbitmq.png)



We can see from the above two screenshots that running a rabbitmq server locally is much faster than running it on an EC2 instance. Our future work would include figuring out how to increase the publish and deliver rate on a rabbitmq EC2 instance.



## 4 Load Balancing

//TODO?



## 5 Challenge

- **Rabbitmq Connection Refused**

  - Local: restart PC and restart rabbitmq-server
  - EC2:  set username and password as "admin"

- **20000 skierNum takes forever to complete** - how to solve it?

  
