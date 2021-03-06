# XML-XSD-Validator

Simple Java microservice for validating XML against XSD. Developed using Spring WebFlux.

## How to use

1. Create folder `xsd` in same folder where docker-compose file located. Make sure that it contains required XSD 
file that defined in `REFERENCE_XSD_PATH` env property in compose file.

2. Run via `docker-compose up`

3. Send `POST` to `localhost:18080/api/validate` with `application/xml` body type.

## Performance check

You can check validation performance using ApacheBench (ab) CLI tool:

```bash
ab -T 'application/xml' -n 100 -c 1 -p src/test/resources/sample.xml localhost:18080/api/validate
```