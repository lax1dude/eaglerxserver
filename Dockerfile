FROM docker.io/amazoncorretto:17-alpine3.21-full AS builder

WORKDIR /data

COPY . .

RUN ["chmod", "+x", "gradlew"]
RUN ["chmod", "+x", "build_all.sh"]
RUN ["sh", "-c", "GRADLE_OPTS=-Dorg.gradle.daemon=false ./build_all.sh"]

RUN ["mkdir", "jars"]
RUN ["sh", "-c", "mv *.jar jars"]

FROM docker.io/alpine:3.21.3

WORKDIR /data/

COPY --from=builder /data/jars .

ENTRYPOINT ["sh", "-c", "mkdir -p jars && cp *.jar jars"]
