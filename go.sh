mvn clean install -DskipTests && \
rm -rf ~/wildfly-10.0.0.Final/standalone/deployments/* && \
cp -r app/target/Pastie-1.0 ~/wildfly-10.0.0.Final/standalone/deployments/ && \
mv ~/wildfly-10.0.0.Final/standalone/deployments/Pastie-1.0 ~/wildfly-10.0.0.Final/standalone/deployments/Pastie-1.0.ear && \
touch ~/wildfly-10.0.0.Final/standalone/deployments/Pastie-1.0.ear.dodeploy && \
~/wildfly-10.0.0.Final/bin/standalone.sh --debug
