### Redis performance with JMeter

---

```bash
#Environment
docker-compose up -d

#Java
mvn clean package -DskipTests
java -jar target/*.jar --server.port=7070

#JMeter
./jmeter -n -t path-to-jmx/file-name.jmx -l path-to-output/v1-output-file.jtl
```