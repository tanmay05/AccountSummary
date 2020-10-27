###############################################################################
#  PROJECT  : Promotion Validity Notify 
#  
#  MODULE   : vf_promotion_expiry_notify.sh
#
#  DESCRIPTION :
#               This script run the java utility for creating customer Interaction with Siebel for
#               accounts whose promotion is going to expire in near future.
# DATE         / CHANGE                                 / AUTHOR
#------------------------------------------------------------------------------
# 01/02/2018   /MOB-4418 /Initial  Creation    	       / Vijayalakshmi
################################################################################

#Exporting necessary jar files needed for this process
export CLASSPATH=$CLASSPATH:$PIN_HOME/jars/pcm.jar:$PIN_HOME/jars/pcmext.jar:$PIN_HOME/jars/ComPortalVodafone.jar:$PIN_HOME/jars/log4j-1.2.17.jar:$PIN_HOME/jars/guava-16.0.1.jar:$PIN_HOME/jars/ojdbc14.jar:$PIN_HOME/jars/commons-io-2.4.jar

java -cp vf_promotion_expiry_notify.jar:$PIN_HOME/jars/pcm.jar:$PIN_HOME/jars/log4j-1.2.17.jar:$PIN_HOME/jars/pcmext.jar:$PIN_HOME/jars/ComPortalVodafone.jar:$PIN_HOME/jars/guava-16.0.1.jar:$PIN_HOME/jars/ojdbc14.jar:$PIN_HOME/jars/commons-io-2.4.jar com.vodafone.promotionValidity.PromotionExpiryNotify

LOGFILE=$PIN_LOG_DIR/vf_promotion_expiry_notify/vf_promotion_expiry_notify.pinlog

if [ ! -d "$PIN_HOME/apps/vf_promotion_expiry_notify/generated_html" ]
then
	echo "`date +'%Y-%m-%d %H:%M:%S'` INFO $PIN_HOME/apps/vf_promotion_expiry_notify/generated_html Directory not found..." | tee -a $LOGFILE
	exit 1;
else
	count=$(ls $PIN_HOME/apps/vf_promotion_expiry_notify/generated_html | wc -l)
	if [ $count -gt 0 ]
	then
		mv ./generated_html/* ./done/
		if [ $? -ne 0 ]
		then
			echo "`date +'%Y-%m-%d %H:%M:%S'` INFO Moving HTML files to done directory is not successful" | tee -a $LOGFILE
		fi
	fi
fi
