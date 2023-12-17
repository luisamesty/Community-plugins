#  COMMAND TO Build Maven Plugins
# mvn verify -Didempiere.target=org.community.p2.targetplatform -X
mvn -Dmaven.repo.local=$HOME/.m2/repository_10_OK clean install

# NOTE: Compilation is configured for local MAC OS development environment
# Compiling in a different server setup you need to change relativePath and Location.
#
# 1. Change Relative Path in files:
#   org.community.p2.site/pom.xml
#   org.community.p2.targetplatform/pom.xml
#   org.community.ui.zk.specialeditor/pom.xml
#   org.community.ui.zk.specialeditor_examples/pom.xml
#   org.community.model-generator/pom.xml
#	org.community.kanban-board/pom.xml
#  Relative Path is set as: 
#  <relativePath>../../idempiere/org.idempiere.parent/pom.xml</relativePath>
#
# 2. Change the repository location in file:
#   org.community.p2.targetplatform/org.community.p2.targetplatform.target
#   Repository Location is set as: 
# 		<repository location="file:///Users/luisamesty/sources/idempiere/org.idempiere.p2/target/repository"/>
