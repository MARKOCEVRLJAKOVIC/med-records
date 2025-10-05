# EMR Application (Medical Records)

This is a **Spring Boot** application for managing electronic medical records (EMR), designed to handle users, providers, appointments, and medical services in a secure and modular way.

---

## Features 

- **Medical Records Management:**  
  Create, update, and retrieve medical records with notes, dates, and provider information.

- **User and Provider Management:**  
  Handle user accounts and healthcare providers with secure authentication.

- **SMS Notifications:**  
  Send SMS messages using **Twilio API** for appointment reminders or notifications.

- **File Uploads:**  
  Upload and manage documents or images related to medical records with modular storage service (`PhotoStorageService`).

- **Aspect-Oriented Programming (AOP):**  
  Automatic logging of important methods and actions for easier debugging and monitoring.

- **Global Exception Handling:**  
  Custom exceptions and error messages for a clean and user-friendly API.

- **Security:**  
  Implemented with **Spring Security**, including roles and authentication.

---

## Technologies 

- **Backend:** Java, Spring Boot, Spring Security, Hibernate  
- **Database:** MySQL / PostgreSQL  
- **Messaging:** Twilio SMS API  
- **Tools & Libraries:** Lombok, Maven, Postman, AOP

---

## Project Structure 

- `controllers/` – REST controllers for users, providers, and medical records  
- `services/` – Business logic and integration with external services (SMS, file storage)  
- `repositories/` – Spring Data JPA repositories for database operations  
- `models/` – Entity classes for database mapping  
- `exceptions/` – Custom exceptions and global exception handler  
- `aspect/` – Logging aspect for AOP

---

## How to Run 

1. Clone the repository:
```bash
git clone https://github.com/marko/emr-application.git
