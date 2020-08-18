#!/usr/bin/env bash

# shellcheck disable=SC2046
kill -9 `ps -ef|grep ongdb-lab-http-1.0.0-jar-with-dependencies.jar|grep -v grep|awk '{print $2}'`


