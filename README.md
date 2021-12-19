# webfx-bench

## Overview

webfx-bench measures performance of a number of selected 2017 web frameworks used to implement a simple REST web service. It is a microbenchmark so it only looks at a very specific scenario and by no means tries to provide overall picture.

## Results

| Name           | Version | Lang  | #LOC | File Size | Req/s     |
|----------------|---------|-------|-----:|----------:|----------:|
| akka-http      | 10.1.12 | Scala | 55   | 22214KB   | 85691
| deno           | 10.0.0  | TS    | 40   | 88000KB   | 23084
| express        | 4.17.2  | JS    | 36   | 1009KB    | 13972
| finch          | 0.12.0  | Scala | 36   | 35054KB   | 89330
| play           | 2.8.11  | Scala | 56   | 38186KB?  | 51455
| spray          | 1.3.4   | Scala | 61   | 16418KB   | 90960
| tokio-minihttp | 0.1.0   | Rust  | 86   | 891KB     | 118431

## Environment

* Client

        ab -c 40 -n 1000000 -k http://localhost:8080/v1/tweets

* Hardware

        vendor_id   : GenuineIntel
        cpu family  : 6
        model       : 142
        model name  : Intel(R) Core(TM) i5-8250U CPU @ 1.60GHz
        stepping    : 10
        microcode   : 0xea
        cpu MHz     : 3163.811
        cache size  : 6144 KB

* Operating System

        Linux skyline.origin 5.15.8-200.fc35.x86_64 #1 SMP Tue Dec 14 14:26:01 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux

* Java

        openjdk version "1.8.0_312"
        OpenJDK Runtime Environment (build 1.8.0_312-b07)
        OpenJDK 64-Bit Server VM (build 25.312-b07, mixed mode)

* Java Opts

        -J-server
        -J-Xms1g
        -J-Xmx1g
        -J-XX:NewSize=256m
        -J-XX:MaxNewSize=256m
        -J-XX:+AlwaysPreTouch
        -J-XX:MaxTenuringThreshold=2
        -J-XX:+UseConcMarkSweepGC
        -J-XX:+ParallelRefProcEnabled
        -J-XX:+UseCMSInitiatingOccupancyOnly
        -J-XX:CMSInitiatingOccupancyFraction=80

## Detailed Results

* akka-http

        Server Software:        akka-http/10.1.12
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        61 bytes

        Concurrency Level:      40
        Time taken for tests:   11.670 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      220000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    85691.40 [#/sec] (mean)
        Time per request:       0.467 [ms] (mean)
        Time per request:       0.012 [ms] (mean, across all concurrent requests)
        Transfer rate:          18410.26 [Kbytes/sec] received

* dano-oak (1cpu)

        Server Software:
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        61 bytes

        Concurrency Level:      100
        Time taken for tests:   43.319 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      208000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    23084.63 [#/sec] (mean)
        Time per request:       4.332 [ms] (mean)
        Time per request:       0.043 [ms] (mean, across all concurrent requests)
        Transfer rate:          4689.07 [Kbytes/sec] received

* express

        Server Software:
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        61 bytes

        Concurrency Level:      40
        Time taken for tests:   71.567 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      296000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    13972.98 [#/sec] (mean)
        Time per request:       2.863 [ms] (mean)
        Time per request:       0.072 [ms] (mean, across all concurrent requests)
        Transfer rate:          4039.06 [Kbytes/sec] received

* finch

        Server Software:
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        0 bytes

        Concurrency Level:      40
        Time taken for tests:   11.194 seconds
        Complete requests:      1000000
        Failed requests:        0
        Non-2xx responses:      1000000
        Keep-Alive requests:    1000000
        Total transferred:      69000000 bytes
        HTML transferred:       0 bytes
        Requests per second:    89330.77 [#/sec] (mean)
        Time per request:       0.448 [ms] (mean)
        Time per request:       0.011 [ms] (mean, across all concurrent requests)
        Transfer rate:          6019.36 [Kbytes/sec] received

* play

        Server Software:
        Server Hostname:        localhost
        Server Port:            9000

        Document Path:          /v1/tweets
        Document Length:        61 bytes

        Concurrency Level:      40
        Time taken for tests:   19.434 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      406000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    51455.54 [#/sec] (mean)
        Time per request:       0.777 [ms] (mean)
        Time per request:       0.019 [ms] (mean, across all concurrent requests)
        Transfer rate:          20401.32 [Kbytes/sec] received

* spray

        Server Software:        spray-can/1.3.4
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        74 bytes

        Concurrency Level:      40
        Time taken for tests:   10.994 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      246000000 bytes
        HTML transferred:       74000000 bytes
        Requests per second:    90960.00 [#/sec] (mean)
        Time per request:       0.440 [ms] (mean)
        Time per request:       0.011 [ms] (mean, across all concurrent requests)
        Transfer rate:          21851.72 [Kbytes/sec] received
