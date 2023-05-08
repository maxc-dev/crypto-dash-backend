# Crypto Backend

Crypto Backend is Surrey Final Year project aiming to stream formatted crypto exchange data to a front-end websocket

## Setup in VS Code ##
1. Initialise gradle environment
```bash
gradle wrapper
```
2. Create Executable JAR for docker
```bash
./gradlew buildFatJar
```
3. In the docker-compose file do the following:
   * Find your `LOCALIP` address:
```bash
MacOS: ipconfig getifaddr en0
```
   * Change this environment variable in the `web` microservice:
```bash
SERVER_IP=LOCALIP
```
   * Change this environment variable in the `kafka` microservice: 
```bash
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://LOCALIP:9092
```
   * Remember to change the LOCALIP variable to your local ip address

4. Build Docker Image
```bash
docker compose up --build
```
5. Done!

## Adminer ##

Adminer is a tool used to inspect databases, useful as a GUI for tables and records
1. Go to
```bash
localhost:8080
```
2. Submit the information from environment variables in the docker-compose.yml file 
3. Use the host as the local IP address, for now (this will be fixed, and you'll be able to use localhost)

## Connection to Websocket ##

Install wscat
Once the docker image is running
```bash
wscat -c "ws://127.0.0.1/ws/data"
```
The stream of crypto prices should then be sent to the terminal