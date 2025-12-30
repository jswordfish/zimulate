nohup java -jar bsrf-2.1.1.RELEASE.jar > log.txt 2>&1 &
echo $! > /path/to/app/pid.file