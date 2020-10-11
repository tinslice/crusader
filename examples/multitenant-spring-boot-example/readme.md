# Multitenant Spring Boot Example

Multitenant configuration example [test-env/config/todo/spring/application.yml](./test-env/config/todo/spring/application.yml)

## Build

```bash
mvn clean package
```

## Run 

### Start postgresql server

```bash
cd test-env
docker-compose up
```

### Start application

```bash
java -jar target/multitenant-spring-boot-example-*.jar --spring.config.location=classpath:/,./test-env/config/todo/spring/
```