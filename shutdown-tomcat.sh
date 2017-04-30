`ps -ef|grep tomcat|awk '{print $2}'|xargs kill -9`
echo "shutdown ok!"