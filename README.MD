# Crypto Backend

## Getting started: ##

### VS CODE: ###
1. Initialise gradle environment -> gradle wrapper
2. Create Executable JAR for docker -> ./gradlew buildFatJar
3. Build Docker Image -> docker compose up --build
4. Done!

TODO: Implement EXTERNAL port forwarding, as of now, you have to change the IP in several places to be that of your local IP address

### Adminer ###

Adminer is a tool used to inspect databases, useful as a GUI for tables and records
1. Go to -> localhost:8080
2. Submit the information from environment variables in the docker-compose.yml file 
3. Use the host as the local IP address, for now (this will be fixed, and you'll be able to use localhost)

### Connection to Websocket ###

Create a JS websocket
Once the local docker server is running:
  Use this as the url -> ws://127.0.0.1/ws/data
It should begin to stream data to the websocket