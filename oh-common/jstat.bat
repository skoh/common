jstat -gc -h10 32584 1000 1000 | awk '{print $3+$4+$6+$8" "$3" "$4" "$6" "$8}'