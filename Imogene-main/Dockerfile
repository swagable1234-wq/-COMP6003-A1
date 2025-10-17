FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache git make

WORKDIR /app

RUN git clone https://github.com/SergeyOvchinnik/Imogene.git

WORKDIR /app/Imogene

EXPOSE 8080

ENTRYPOINT ["make", "backend"]
