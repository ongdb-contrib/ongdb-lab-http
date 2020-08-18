#!/usr/bin/env bash

myJarPath=./lib/ongdb-lab-http-1.0.0-jar-with-dependencies.jar

# ---------------------------MONITOR CSV SERVER---------------------------
nohup java -Xmx128m -cp ${myJarPath} data.lab.ongdb.http.extra.server.HttpService >>logs/HttpService.server.log 2>&1 &

