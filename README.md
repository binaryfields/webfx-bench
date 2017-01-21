# webfx-bench

## Overview

webfx-bench measures performance of a number of selected 2017 web frameworks used to implement a simple REST web service. It is a microbenchmark so it only looks at a very specific scenario and by no means tries to provide overall picture.

## Resuls

| Name           | Version | #LOC | Req/s     |
|----------------|---------|-----:|----------:|
| akka-http      | 10.0.1  | 50   | 87098.28
| express        | 4.14.0  | 36   | 42636.52
| finch          | 0.12.0  | 36   | 90578.62
| play           | 2.5.11  | 56   | 77447.72
| spray          | 1.3.3   | 58   | 89008.38
| tokio-minihttp | 0.1.0   | 90   | 177319.4

## Environment

* Client

        httperf --server localhost --port 8080 --num-conns 100 --num-calls 10000 --rate 10000 --uri /v1/tweets

* Hardware

        vendor_id       : GenuineIntel
        cpu family      : 6
        model           : 60
        model name      : Intel(R) Core(TM) i7-4800MQ CPU @ 2.70GHz
        stepping        : 3
        microcode       : 0x1c
        cpu MHz         : 2700.823
        cache size      : 6144 KB

* Operating System

        Linux skyline 4.9.3-200.fc25.x86_64 #1 SMP Fri Jan 13 01:01:13 UTC 2017 x86_64 x86_64 x86_64 GNU/Linux

* Java

        openjdk version "1.8.0_111"
        OpenJDK Runtime Environment (build 1.8.0_111-b16)
        OpenJDK 64-Bit Server VM (build 25.111-b16, mixed mode)

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

        Server Software:        akka-http/10.0.1
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        61 bytes

        Concurrency Level:      100
        Time taken for tests:   11.481 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      219000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    87098.28 [#/sec] (mean)
        Time per request:       1.148 [ms] (mean)
        Time per request:       0.011 [ms] (mean, across all concurrent requests)
        Transfer rate:          18627.46 [Kbytes/sec] received

* express

        Server Software:
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        61 bytes

        Concurrency Level:      100
        Time taken for tests:   23.454 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      268000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    42636.52 [#/sec] (mean)
        Time per request:       2.345 [ms] (mean)
        Time per request:       0.023 [ms] (mean, across all concurrent requests)
        Transfer rate:          11158.78 [Kbytes/sec] received

* finch

        Server Software:
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /tweets
        Document Length:        61 bytes

        Concurrency Level:      100
        Time taken for tests:   11.040 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      156000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    90578.62 [#/sec] (mean)
        Time per request:       1.104 [ms] (mean)
        Time per request:       0.011 [ms] (mean, across all concurrent requests)
        Transfer rate:          13799.09 [Kbytes/sec] received

* play

        Server Software:
        Server Hostname:        localhost
        Server Port:            9000

        Document Path:          /v1/tweets
        Document Length:        61 bytes

        Concurrency Level:      100
        Time taken for tests:   12.912 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      193000000 bytes
        HTML transferred:       61000000 bytes
        Requests per second:    77447.72 [#/sec] (mean)
        Time per request:       1.291 [ms] (mean)
        Time per request:       0.013 [ms] (mean, across all concurrent requests)
        Transfer rate:          14597.08 [Kbytes/sec] received

* spray

        Server Software:        spray-can/1.3.3
        Server Hostname:        localhost
        Server Port:            8080

        Document Path:          /v1/tweets
        Document Length:        74 bytes

        Concurrency Level:      100
        Time taken for tests:   11.235 seconds
        Complete requests:      1000000
        Failed requests:        0
        Keep-Alive requests:    1000000
        Total transferred:      246000000 bytes
        HTML transferred:       74000000 bytes
        Requests per second:    89008.38 [#/sec] (mean)
        Time per request:       1.123 [ms] (mean)
        Time per request:       0.011 [ms] (mean, across all concurrent requests)
        Transfer rate:          21382.87 [Kbytes/sec] received
