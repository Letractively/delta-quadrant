#
# Xoxa startup script.
#

name="XoXa"

case "$1" in
	start)
		echo ""
		echo "Starting xoxa ..."
		echo ""
		java -jar /home/k42b3/xoxa_0.0.2_beta.jar > /dev/null
		;;
	stop)
		echo ""
		echo "Stopping xoxa ..."
		echo ""
		pkill -f java > /dev/null 2>&1
		;;
	*)
		echo ""
		echo "Usage: ${name} { start | stop }"
		echo ""
		exit 64
		;;
