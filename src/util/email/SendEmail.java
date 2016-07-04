package util.email;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

	private static String USER_NAME = "eyelog2016"; 
	private static String PASSWORD = "2016eyelog"; 
	private static String RECIPIENT = "eyelog2016@gmail.com";

	public static void main(String[] args) {

		 sendFromGMail();
	}

	public static void sendFromGMail() {
		String subject = "Java send mail Eye-Log Project";
		String body = "----";

		String[] to = { RECIPIENT };
		Properties props = System.getProperties();
		String host = "smtp.gmail.com";
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", USER_NAME);
		props.put("mail.smtp.password", PASSWORD);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(USER_NAME));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for (int i = 0; i < to.length; i++) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for (int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);
			message.setText(body);
			
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();
			
			String filename = "eyeLOG_"+now.getTime()+".zip";
			DataSource source = new FileDataSource(filename);
			message.setDataHandler(new DataHandler(source));
			message.setFileName(filename);

			Transport transport = session.getTransport("smtp");
			transport.connect(host, USER_NAME, PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (AddressException ae) {
			ae.printStackTrace();
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
}