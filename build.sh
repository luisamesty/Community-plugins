#  COMMAND TO Build Maven Plugins
mvn verify -Didempiere.target=org.community.p2.targetplatform -X

# NOTE: Compilation is configured for local MAC OS development environment
# Compiling in a different server setup you need to change relativePath and Location.
#
# 1. Change Relative Path in files:
#   org.community.p2.site/pom.xml
#   org.community.p2.targetplatform/pom.xml
#   org.community.ui.zk.specialeditor/pom.xml
#   org.community.ui.zk.specialeditor_examples/pom.xml
#   org.community.ingeint.modelgenerator/pom.xml
#  Relative Path is set as: 
#  <relativePath>../../myexperiment/org.idempiere.parent/pom.xml</relativePath>
#
# 2. Change the repository location in file:
#   org.community.p2.targetplatform/org.community.p2.targetplatform.target
#   Repository Location is set as: 
# 		<repository location="file:///Volumes/Datos/Adempiere/iDempiere7.1srcGitMac/myexperiment/org.idempiere.p2/target/repository"/>
