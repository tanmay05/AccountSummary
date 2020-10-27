#=============================================================================
# 
# This software is the property of Vodafone Network Pty Limited and/or
# Vodafone New Zealand Limited and is protected by copyright under the laws
# of Australia , New Zealand and other countries. Unathorised reproduction or
# distribution of this software, or any part of it, is prohibited.
#
# PROJECT     : Promotion Validity Notification
#
# MODULE      : Makefile
#
# DESCRIPTION : The Makefile for vf_promotion_expiry_notify.
#
#==============================================================================
# Document History
#
# DATE   / CHANGE                                             / Author
#========/====================================================/=================
#01.03.18/ Initial Creation				      /  Vijayalakshmi
#===============================================================================
TOP=../../..
include $(TOP)/Makefile.inc
MAKEFILE=Makefile

########### ADD YOUR SOURCE FILE AND TARGET OBJECTS NAME HERE ##################

SRC_VF_ERROR_LIST_XSD=vf_error_list.xsd
SRC_XSLFILE=vf_errors.xsl
SRC_XMLFILE=vf_error_codes.xml

SRC_FILES=

JAVASRCDIR=$(APP_SRC_DIR)/com/vodafone/promotionValidity
SRC_OBJECTS=$(JAVASRCDIR)/*.class
APP_SRC_DIR=$(PIN_HOME)/source/apps/vf_promotion_expiry_notify

WHOAMI=$(shell whoami)
HOSTNAME=$(shell hostname)
cm_port=$(shell $(BINDIR)/GetEntry.pl $(WHOAMI) $(HOSTNAME) cm)
wsg_password=$(shell $(BINDIR)/GetEntry.pl $(WHOAMI) $(HOSTNAME) wsg_password)
email_url = $(shell $(BINDIR)/GetEntry.pl $(WHOAMI) $(HOSTNAME) fusion_email_url)
email_action = $(shell $(BINDIR)/GetEntry.pl $(WHOAMI) $(HOSTNAME) fusion_email_action)

########### ADD YOUR BUILD TARGETS HERE ########################################
TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_JAR=$(APP_SRC_DIR)/vf_promotion_expiry_notify.jar
TARG_BUILD_TEMPLATE_HTML=$(APP_SRC_DIR)/templates/promo_notify.html
TARG_BUILD_INFRANET_PROPS=$(APP_SRC_DIR)/Infranet.properties
TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_SCRIPT=$(APP_SRC_DIR)/vf_promotion_expiry_notify.sh

BUILD_TARG=$(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_JAR)

$(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_JAR) : $(JAVASRCDIR)/*.java
	$(JAVA_HOME)/bin/javac -classpath .:$(PINDIR)/include:$(PINDIR)/jars/pcm.jar:$(PINDIR)/jars/pcmext.jar:$(PINDIR)/jars/ComPortalVodafone.jar:$(PINDIR)/jars/log4j-1.2.17.jar:$(PINDIR)/jars/guava-16.0.1.jar:$(PINDIR)/jars/commons-io-2.4.jar $(JAVASRCDIR)/*.java
	$(JAVA_HOME)/bin/jar cfvm $(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_JAR) $(APP_SRC_DIR)/MANIFEST.MF ./com/vodafone/promotionValidity/*.class

########### ADD YOUR INSTALL TARGETS HERE ######################################
TARG_INSTALL_APP_DIR=$(PIN_HOME)/apps/vf_promotion_expiry_notify
TARG_LOG_DIR=$(PIN_LOG_DIR)/vf_promotion_expiry_notify
TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_SH=$(TARG_INSTALL_APP_DIR)/vf_promotion_expiry_notify.sh
TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_JAR=$(TARG_INSTALL_APP_DIR)/vf_promotion_expiry_notify.jar
TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_PROP_FILE=$(TARG_INSTALL_APP_DIR)/Infranet.properties
TARG_INSTALL_TEMPLATE_HTML=$(TARG_INSTALL_APP_DIR)/templates/promo_notify.html
TARG_INSTALL_TEMPLATE_DIR=$(TARG_INSTALL_APP_DIR)/templates
TARG_INSTALL_HTML_DIR=$(TARG_INSTALL_APP_DIR)/generated_html
TARG_INSTALL_DONE_DIR=$(TARG_INSTALL_APP_DIR)/done


INSTALL_TARG=DIRS \
	$(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_SH) \
	$(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_JAR) \
	$(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_PROP_FILE)\
	$(TARG_INSTALL_TEMPLATE_HTML)


DIRS :
	if [ ! -d $(TARG_INSTALL_APP_DIR) ] ; then mkdir -p $(TARG_INSTALL_APP_DIR) ; fi
	if [ ! -d $(TARG_LOG_DIR) ] ; then mkdir -p $(TARG_LOG_DIR) ; fi
	if [ ! -d $(TARG_INSTALL_TEMPLATE_DIR) ] ; then mkdir -p $(TARG_INSTALL_TEMPLATE_DIR) ; fi
	if [ ! -d $(TARG_INSTALL_HTML_DIR) ] ; then mkdir -p $(TARG_INSTALL_HTML_DIR) ; fi
	if [ ! -d $(TARG_INSTALL_DONE_DIR) ] ; then mkdir -p $(TARG_INSTALL_DONE_DIR) ; fi


$(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_SH) : $(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_SCRIPT) 
	cp -fp $(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_SCRIPT)  $(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_SH)
	chmod 755 $(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_SH)
	dep_touch.pl $? $(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_SH)

$(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_JAR) : $(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_JAR) 
	cp -fp $(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_JAR) $(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_JAR)
	chmod 755 $(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_JAR)
	dep_touch.pl $? $(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_JAR)

$(TARG_INSTALL_TEMPLATE_HTML) : $(TARG_BUILD_TEMPLATE_HTML)
	cp -fp $(TARG_BUILD_TEMPLATE_HTML) $(TARG_INSTALL_TEMPLATE_HTML)
	chmod 755 $(TARG_INSTALL_TEMPLATE_HTML)
	dep_touch.pl $? $(TARG_INSTALL_TEMPLATE_HTML)

$(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_PROP_FILE) : Infranet.properties_src
	sed -e 's|__PIN_LOG_DIR__|$(PIN_LOG_DIR)|' -e 's|__CM_PORT__|$(cm_port)|' -e 's|__CM_HOST__|$(HOSTNAME)|' -e 's|__WSG_PASSWD__|$(wsg_password)|' -e 's|__CM_PORT__|$(cm_port)|' -e 's|__EMAIL_URL__|$(email_url)|' -e 's|__EMAIL_ACTION__|$(email_action)|' Infranet.properties_src > Infranet.properties.tmp
	mv Infranet.properties.tmp $(TARG_INSTALL_VF_PROMOTION_EXPIRY_NOTIFY_PROP_FILE)

########### ADD YOUR DEPLOY TARGETS HERE #######################################

# Programmes used to do deployment of source files
#
DEPLOY_TARG=


# ###########


all: $(BUILD_TARG)

clean:
	rm -f $(SRC_OBJECTS) $(TARG_BUILD_VF_PROMOTION_EXPIRY_NOTIFICATION_JAR) core

clobber: clean
	rm -f $(BUILD_TARG)

lint:
	lint $(CPPFLAGS) $(LINTFLAGS) $(SRC_FILES)

prep_install: $(PREP_TARG)

install: $(INSTALL_TARG)

uninstall:
	rm -f $(INSTALL_TARG)

	deploy: $(DEPLOY_TARG)

undeploy:
	rm -f $(DEPLOY_TARG)
