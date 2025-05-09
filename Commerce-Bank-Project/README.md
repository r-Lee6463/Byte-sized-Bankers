# Commerce-Bank-Project

## This is the Capstone Project for Software Engineering at UMKC with Commerce Bank

### Steps to run the application

This is a _Spring Application_ and to run it you will need (at least) **Java 17** and **Maven 3.9.6** Setup and configured locally.

To run the application first pull it locally and ensure it is up to date.

Next, open your terminal or gitbash and navigate inside of the project directory.

From here you will run the following commands: 
  mvn clean install
  mvn spring-boot:run

Once this is completed successfully you can navigate to your browser and launch the web-facing portion at (default) **localhost:8080**

### For Starting Ollama

1. Go to https://ollama.com/download and downlaod ollama

2. If on Mac make sure the application is in your Applications folder.

3. Run this command in terminal: ollama run llama3.2-vision

This will download the model to your machine and start it on port 127.0.0.1:11434

Once this is running you can go to this website in your browser to confirm its accessible 127.0.0.1:11434 and running.
After that is confirmed you can now use the support bot funtionality within our banking application.


