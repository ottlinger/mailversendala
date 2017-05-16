package de.aikiit.mailversendala;

/**
 * Created by hirsch on 15.05.17.
 */
public interface MailConfig {

     String SMTP_HOST = "smtp.example.com";
     int SMTP_PORT = 465;

     String USERNAME = "mails@example.com";
     String PASSWORD = "password";
     String FROM = "from@bar.com";
     String TO = "to@bar.com";
     String SUBJECT = "My subject";
}
