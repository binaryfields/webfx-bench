# webfx-bench

## Overview

webfx-bench measures performance of a number of selected 2022 web frameworks used to implement an async REST web service in C10K test. It is a microbenchmark so it only looks at a very specific scenario and by no means tries to provide overall picture.

## Results

| Name                | Ver  | Lang |  Req/s |
|---------------------|------|------|-------:|
| akka-http           | 10.1 | sca  | 39112 23.72MB 25.78ms
| deno-oak            | 10.0 | ts   | 16081 349.94ms
| node-express        | 4.17 | js   | 16375 276.34ms
| play                | 2.8  | sca  | 32903 25.74MB 30.45ms
| play-opt            | 2.8  | sca  | 51080 40.08MB 18.45ms
| spray               | 1.3  | sca  | 51114 41.29MB 19.02ms
| spring-webflux      | 2.7  | kt   | 21736 11.77MB 46.61ms
| spring-webflux-fun  | 2.7  | kt   | x 61393 33.59MB 16.06ms
| spring6-webflux-fun | 3.0  | kt   | 61085 33.42MB 16.20ms
| zio-http            | 2.0  | sca  | 63327 34.66MB 15.34ms

## Environment

* Client

        wrk -t2 -c10000 -d120s http://localhost:8080/v1/tweets

* Hardware

        vendor_id   : GenuineIntel
        cpu family  : 6
        model       : 142
        model name  : Intel(R) Core(TM) i5-8250U CPU @ 1.60GHz
        stepping    : 10
        microcode   : 0xf0
        cpu MHz     : 883.860
        cache size  : 6144 KB

* Operating System

        Linux skyline 5.15.59-2-lts #1 SMP Sat, 06 Aug 2022 21:34:17 +0000 x86_64 GNU/Linux

* Java

        openjdk version "11.0.16" 2022-07-19
        OpenJDK Runtime Environment (build 11.0.16+8)
        OpenJDK 64-Bit Server VM (build 11.0.16+8, mixed mode)

* Java Opts

        -J-Xms4g 
        -J-Xmx4g 
        -J-XX:-UseParallelGC
        -J-XX:+UseG1GC 
        -J-XX:+AlwaysPreTouch
        -J-XX:+ExplicitGCInvokesConcurrent 
        -J-XX:+ParallelRefProcEnabled

