package cropper_helper.notification;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dbarelop on 15/04/15.
 */
public class EmailNotifier implements Notifier {
    private static final Logger logger = Logger.getLogger(EmailNotifier.class.getName());
    private final String USERNAME = "croppersa@gmail.com";
    private final String PASSWORD = "mojhjaksrcuhfcin";
    private final Properties props = new Properties() {{
        put("mail.smtp.auth", "true");
        put("mail.smtp.starttls.enable", "true");
        put("mail.smtp.host", "smtp.gmail.com");
        put("mail.smtp.port", "587");
    }};
    private String destEmail;

    public EmailNotifier(String destEmail) {
        this.destEmail = destEmail;
    }

    @Override
    public void sendNotification(String title, String description) {
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message m = new MimeMessage(session);
        try {

            m.setFrom(new InternetAddress(USERNAME));
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destEmail));
            m.setSubject(title);
            m.setText(description);
            Transport.send(m);
            logger.log(Level.FINE, "Notification sent succesfully to " + destEmail);
            System.out.println("EMAIL: to " + destEmail + ", subject: " + title + ", text: " + description );
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }
}
