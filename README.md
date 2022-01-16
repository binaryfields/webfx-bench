# webfx-bench

## Overview

webfx-bench measures performance of a number of selected 2022 web frameworks used to implement an async REST web service in C10K test. It is a microbenchmark so it only looks at a very specific scenario and by no means tries to provide overall picture.

## Results

| Name               | Ver  | Lang |  Req/s |
|--------------------|------|------|-------:|
| akka-http          | 10.1 | sca  | 55192 18.39ms
| deno-oak           | 10.0 | ts   | 16081 349.94ms
| node-express       | 4.17 | js   | 16375 276.34ms
| finch              | 0.16 | sca  | 73395 134.66ms
| play               | 2.8  | sca  | 49993 20.25ms
| play-opt           | 2.8  | sca  | 52698 19.19ms
| spray              | 1.3  | sca  | 91929 69.94ms
| spring-boot        | 2.6  | kt   | 11934 680.95ms
| spring-webflux     | 2.6  | kt   | 18909 526.45ms
| spring-webflux-fun | 2.6  | kt   | 43772 227.71ms
| tokio-minihttp     | 0.1  | rs   | 

## Environment

* Client

        wrk -t2 -c10000 -d120s http://localhost:8080/v1/tweets

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

        Linux 5.15.8-200.fc35.x86_64 #1 SMP Tue Dec 14 14:26:01 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux

* Java

        openjdk version "11.0.13" 2021-10-19
        OpenJDK Runtime Environment 18.9 (build 11.0.13+8)
        OpenJDK 64-Bit Server VM 18.9 (build 11.0.13+8, mixed mode, sharing)

* Java Opts

        -J-Xms4g 
        -J-Xmx4g 
        -J-XX:-UseParallelGC
        -J-XX:+UseG1GC 
        -J-XX:+AlwaysPreTouch
        -J-XX:+ExplicitGCInvokesConcurrent 
        -J-XX:+ParallelRefProcEnabled

