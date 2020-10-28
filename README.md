docker-machine start
docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --publish 1414:1414 --publish 9443:9443 --detach ibmcom/mq
docker-machine ip

//kafka
cd E:\kafka\kafka_2.12-2.1.0\bin\windows
zookeeper-server-start.bat ..\..\config\zookeeper.properties
kafka-server-start.bat ..\..\config\server.properties
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic flink_output
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic flink_input
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic rabbitmqkafka
#kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic mqkafka
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic byte


zookeeper-server-start ../../config/zookeeper.properties
kafka-server-start ../../config/server.properties
kafka-topics --zookeeper localhost:2181 --list


kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic rabbitmqkafka --from-beginning --max-messages 100
kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic rabbitmqkafka --from-beginning --max-messages 100

xml->avro
1. https://github.com/elodina/xml-avro
   SimpleConverter good, use this
2. https://www.confluent.io/blog/kafka-connect-single-message-transformation-tutorial-with-examples/
   NA
3. https://github.com/mit-ll/xml-avro-converter
   complicated, hard to implement
   
#############################################
cd /cygdrive/e/confluent-6.0.0
./bin/schema-registry-start ./etc/schema-registry/schema-registry.properties

#############################################

http://localhost:8081/subjects

{  \"namespace\": \"com.mq.demo\",  \"protocol\": \"xml\",   \"types\": [{\"name\": \"Attribute\", \"type\": \"record\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}, {\"name\": \"value\", \"type\": \"string\" }]}, {\"name\": \"Element\", \"type\": \"record\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}, {\"name\": \"attributes\", \"type\": {\"type\": \"array\", \"items\": \"Attribute\"}}, {\"name\": \"children\", \"type\": {\"type\": \"array\", \"items\": [\"Element\", \"string\"]} } ] } ] }

curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" --data '{"schema": "{  \"namespace\": \"com.mq.demo\",  \"protocol\": \"xml\",   \"types\": [{\"name\": \"Attribute\", \"type\": \"record\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}, {\"name\": \"value\", \"type\": \"string\" }]}, {\"name\": \"Element\", \"type\": \"record\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}, {\"name\": \"attributes\", \"type\": {\"type\": \"array\", \"items\": \"Attribute\"}}, {\"name\": \"children\", \"type\": {\"type\": \"array\", \"items\": [\"Element\", \"string\"]} } ] } ] }"}' http://localhost:8081/subjects/rabbitmqkafka-value-value/versions

##############################################

java -jar "E:\m2repo\repository\org\apache\avro\avro-tools\1.10.0\avro-tools-1.10.0.jar" compile schema xml.avsc .

##################################################

xml -> avro file
ByteArraySerializer

###################################################

activemq
cd E:\apache-activemq-5.16.0\bin\win64

