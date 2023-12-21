ps -ef|grep "StartService"|grep -v grep |awk '{print "kill -9"$2}'|sh
echo "autoBackupService stopped"