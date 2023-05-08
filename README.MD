# Crypto Backend

## SETUP IN VS CODE: ##
1. Initialise gradle environment -> gradle wrapper
2. Create Executable JAR for docker -> ./gradlew buildFatJar
3. In the docker-compose file do the following:
   >a. Find your LOCALIP address -> macOS: ipconfig getifaddr en0
   >b. Change the SEVER_IP to LOCALIP (above)
   >c. Change the KAFKA_ADVERTISED_LISTENERS to PLAINTEXT://LOCALIP:9092 (LOCALIP from step a)
4. Build Docker Image -> docker compose up --build
5. Done!

### Adminer ###

Adminer is a tool used to inspect databases, useful as a GUI for tables and records
1. Go to -> localhost:8080
2. Submit the information from environment variables in the docker-compose.yml file 
3. Use the host as the local IP address, for now (this will be fixed, and you'll be able to use localhost)

### Connection to Websocket ###

Install wscat
Once the docker image is running -> wscat -c "ws://127.0.0.1/ws/data"
The stream of crypto prices should then be sent to the terminal