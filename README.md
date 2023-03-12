# webfx-bench

## Overview

webfx-bench measures performance of a number of selected 2022 JVM web frameworks used to implement an async REST web service in C10K test. It is a microbenchmark so it only looks at a very specific scenario and by no means tries to provide overall picture.

## Results

| Name                | Ver  | Lang |  Req/s |
|---------------------|------|------|-------:|
| akka-http           | 10.1 | sca  | 114456.05 69.97MB 7.97ms
| http4s              | 1.0  | sca  | 122717.48 71.86MB 7.83ms
| play                | 2.8  | sca  | 143509.29 113.18MB 6.26ms
| play-opt            | 2.8  | sca  | 181046.65 143.41MB 4.84ms
| spray               | 1.3  | sca  | 207588.35 169.98MB 32.39ms
| spring-webflux      | 2.7  | kt   | 21736 11.77MB 46.61ms
| spring-webflux-fun  | 2.7  | kt   | x 61393 33.59MB 16.06ms
| spring6-webflux-fun | 3.0  | kt   | 61085 33.42MB 16.20ms
| zio-http            | 2.0  | sca  | 240983.73 134.24MB 21.87ms

## Environment

* Client

        wrk -t2 -c10000 -d120s http://localhost:8080/v1/tweets

* Hardware

        MacBook Pro Apple M2 Max

* VM/Container

        podman run -ti --name openjdk --volume $(pwd):/app docker.io/eclipse-temurin:17-jammy bash

* Operating System

        Linux f72ba8fc8c8c 6.1.11-200.fc37.aarch64 #1 SMP PREEMPT_DYNAMIC Thu Feb  9 19:41:51 UTC 2023 aarch64 aarch64 aarch64 GNU/Linux

* Java

        openjdk version "17.0.6" 2023-01-17
        OpenJDK Runtime Environment Temurin-17.0.6+10 (build 17.0.6+10)
        OpenJDK 64-Bit Server VM Temurin-17.0.6+10 (build 17.0.6+10, mixed mode, sharing)

* Java Opts

        -J-Xms1g 
        -J-Xmx1g 
        -J-XX:-UseParallelGC
        -J-XX:+UseG1GC 
        -J-XX:+AlwaysPreTouch
        -J-XX:+ExplicitGCInvokesConcurrent 
        -J-XX:+ParallelRefProcEnabled
