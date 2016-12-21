#!/bin/sh
./mvnw spring-cloud-contract:generateStubs spring-cloud-contract:convert spring-cloud-contract:run -Dspring.cloud.contract.verifier.http.port=0
