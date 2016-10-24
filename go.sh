mvn clean install -DskipTests && cp app/target/Pastie-1.0.ear ~/wildfly-10.0.0.Final/standalone/deployments/ && ~/wildfly-10.0.0.Final/bin/standalone.sh --debug
