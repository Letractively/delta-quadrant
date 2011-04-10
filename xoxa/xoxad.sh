#!/bin/sh
case $1 in
  stop)
  echo
  echo "Stopping xoxa ..."
  echo
  pkill -f java > /dev/null 2>&1
  ;;
  start)
  echo
  echo "Starting xoxa ..."
  echo
  nohup java -jar /home/k42b3/xoxa_0.0.2_beta.jar > /dev/null
  ;;
  *)
  echo
  echo "Usage: xoxad.sh { start | stop }"
  echo
  ;;
esac
