# Fermista  
Integrated project symfony-java
## Overview  
This project was developed as part of the coursework for **PIDEV 3A44** at [Esprit University](https://esprit.tn).  
**Fermista** is a smart farm desktop application built with JavaFX. It connects to a smart collar equipped with sensors (via an IoT platform) and provides veterinarians and farmers with real-time monitoring tools.  

The application enables:
- Monitoring cow health status and detecting anomalies
- Managing online veterinary consultations and appointments
- Generating digital medical reports in PDF format
- Visualizing farm location via integrated maps
- Integrating AI recommendations for veterinary actions
- Supporting educational workshops and resource links for farmers

## Features  
- 🐄 Real-time cow health monitoring (via sensors)  
- 🩺 Veterinary consultation scheduling and management  
- 📍 Map integration for locating farms  
- 📄 PDF generation for medical reports  
- 🤖 AI insights via Gemini API  
- 👨⚕️ JavaFX-based desktop UI for veterinarians and admins  

## Tech Stack  

### Frontend (JavaFX)  
- JavaFX 19  
- JFoenix (Material Design for JavaFX)  
- Scene Builder (UI design)  
- FXML  

### Backend & Data Access  
- JDBC (Java Database Connectivity)  
- MySQL  

### APIs & External Services  
- **Mailtrap API** 
- **Google Maps API** 
- **PDF Generator**  
- **Gemini AI API** 
- **reCAPTCHA ** 
- **Twilio**
- **QR code**


### Other Tools  
- Git & GitHub  
- Postman (for testing APIs if applicable)  
- Figma (UI/UX prototyping)  
- ESP32 + capteurs (smart collar hardware)  











## Directory Structure  
📁 fermista_java/
├── 📁 .idea/
├── 📁 src/
│   └── 📁 main/
│       ├── 📁 java/
│       │   └── 📁 tn/fermista/
│       │       ├── 📁 controllers/
│       │       ├── 📁 models/
│       │       ├── 📁 services/
│       │       ├── 📁 tests/
│       │       └── 📁 utils/
│       └── 📁 resources/
├── 📁 target/
├── 📄 .gitignore
├── 📄 pom.xml



## Getting Started  

1. **Clone the repository**  
   
   git clone https://github.com/your-username/fermista-javafx.git
   cd fermista-javafx

2. Open in your IDE
•	Use IntelliJ IDEA or Eclipse
•	Open as a Maven project
•	Ensure Java 17+ and JavaFX SDK are installed and configured
3.Configure the database connection
Update the DBConnexion.java file:
conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/fermista_db", "root", "password");


4.Run the application
o	Launch the Main.java file
o	The JavaFX interface should appear

##Acknowledgments
This project was completed under the guidance of  Fakhreddine GHALLEB at Esprit School of Engineering.

