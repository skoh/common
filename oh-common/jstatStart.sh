jstat -gc -h10 `cat app.pid` 1000 1000 | awk '{print $3+$4+$6+$8" "$3" "$4" "$6" "$8}'
