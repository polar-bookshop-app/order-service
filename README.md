# order-service

Order Service Spring Boot Reactive REST API application.

## Build and Run locally

* Build application without tests

```bash
./gradlew build -i -x test
```

* Start Postgres DB in docker (detached mode)

```bash
./db/start-db.sh
```

* Start service locally

```bash
./scripts/run.sh
```

* Or with live reloading

```bash
./gradlew build --continuous & ./gradlew bootRun
```

## Docker image

* Create catalog-service docker image using Cloud Native Buildpacks

```bash
./gradlew bootBuildImage
```

* Start Postgres DB in docker (detached mode)

```bash
./db/start-db.sh
```

## Vulnerability Scan

We will use [grype](https://github.com/anchore/grype) as our vulnerability scanner for CI and locally.

* Scan local repository

```bash
grype . --name order-service
```

* Scan docker image

```bash
grype docker:order-service:latest
```

## K8S and Local Registry

If for some reason port-forwarding failed just execute:

## Logo

Logo generated using https://patorjk.com/software/taag/ and `Standard` type.

## References

* Initial template created
  using [start.spring.io](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=3.5.6&packaging=jar&jvmVersion=25&groupId=com.github.polar&artifactId=order-service&name=order-service&description=Order%20Service%20Spring%20Boot%20Reactive%20API&packageName=com.github.polar.orderservice&dependencies=postgresql,testcontainers,devtools,data-jdbc,flyway,webflux,data-r2dbc)
