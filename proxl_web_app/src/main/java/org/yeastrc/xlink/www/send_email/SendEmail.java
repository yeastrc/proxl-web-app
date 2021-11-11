package org.yeastrc.xlink.www.send_email;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;
/**
 * 
 *
 */
public class SendEmail {
	
	private static final SendEmail instance = new SendEmail();
	private SendEmail() { }
	public static SendEmail getInstance() { return instance; }
	private static final Logger log = LoggerFactory.getLogger( SendEmail.class);
	
	/**
	 * @param sendEmailDTO
	 * @throws Exception
	 */
	public void sendEmail( SendEmailDTO sendEmailDTO ) throws Exception  {
		
		String smtpServerHost = ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_HOST_URL_KEY );
		String smtpServerPort = ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_PORT_KEY );
		
		if ( StringUtils.isEmpty( smtpServerHost ) ) {
			
			String msg = "Cannot send email: No entry in config table for key '" + ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_HOST_URL_KEY
					+ "'.";
			log.error(msg);
			throw new ProxlWebappConfigException( msg );
		}

		//  Both smtpAuthUsername and smtpAuthPassword MUST be populated.  Ignored if only one is populated
						
		String smtpAuthUsername = ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_AUTH_USERNAME_KEY );
		String smtpAuthPassword = ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_AUTH_PASSWORD_KEY );
		
		
		// Generate and send the email to the user.
		try {
			
			String fromEmailAddress = sendEmailDTO.getFromEmailAddress();

			// set the SMTP host property value
			Properties properties = System.getProperties();
			
			final String SMTP_TIMEOUT = "3000";  // in Milliseconds.  String since put in Properties object
			
			properties.put("mail.smtp.timeout", SMTP_TIMEOUT);    
			properties.put("mail.smtp.connectiontimeout", SMTP_TIMEOUT); 
			
			properties.put( "mail.smtp.host", smtpServerHost );
			
			if ( StringUtils.isNotEmpty(smtpServerPort) ) {
				properties.put( "mail.smtp.port", smtpServerPort );
			}
			
			
			// create a JavaMail session
			javax.mail.Session mailSession = javax.mail.Session.getInstance(properties, null);
			// create a new MIME message
			MimeMessage message = new MimeMessage(mailSession);
			// set the from address
			Address fromAddress = new InternetAddress( fromEmailAddress );
			message.setFrom(fromAddress);
			// set the to address
			Address[] toAddress = InternetAddress.parse( sendEmailDTO.getToEmailAddress() );
			message.setRecipients(Message.RecipientType.TO, toAddress);
			// set the subject
			message.setSubject( sendEmailDTO.getEmailSubject() );
			message.setText( sendEmailDTO.getEmailBody() );
			
			Transport mailTransport = null;
			try {
				mailTransport = mailSession.getTransport("smtp");
				
				if ( StringUtils.isNotEmpty(smtpAuthUsername) && StringUtils.isNotEmpty(smtpAuthPassword) ) {
					//  YES SMTP Username/Password
					mailTransport.connect( smtpAuthUsername, smtpAuthPassword );
				} else {
					//  NO SMTP Username/Password
					mailTransport.connect();	
				}
				
				message.saveChanges();      // don't forget this
				
				mailTransport.sendMessage(message, message.getAllRecipients());
				
			} finally {
				if ( mailTransport != null ) {
					mailTransport.close();
				}
			}
			
		}
		catch (AddressException e) {
			// Invalid email address format
			//				errors.add("email", new ActionMessage("error.resetpassword.sendmailerror"));
			log.warn( "Error sending email to Smtp Server.  AddressException: to email address: " + sendEmailDTO.getToEmailAddress(), e );
			throw e; 
		}
		catch (SendFailedException e) {
			// Invalid email address format
			log.error( "Error sending email to Smtp Server.  SendFailedException: to email address: " + sendEmailDTO.getToEmailAddress()
					+ ", Smtp Server Host: " + smtpServerHost, e );
			throw e; 
		}
		catch (MessagingException e) {
			// Invalid email address format
			log.error( "Error sending email to Smtp Server.  MessagingException: to email address: " + sendEmailDTO.getToEmailAddress()
					+ ", Smtp Server Host: " + smtpServerHost, e );
			throw e; 
		}
		catch (Exception e) {
			// Invalid email address format
			log.error( "Error sending email to Smtp Server.  Exception: to email address: " + sendEmailDTO.getToEmailAddress()
					+ ", Smtp Server Host: " + smtpServerHost, e );
			throw e; 
		}
	
		
//		{
//			MimeMessage message = null;
//			// Generate and send the email to the user.
//			try {
//				message = createSMTPMailMessageToSend( sendEmailDTO );
//				// send the message
//				Transport.send(message);
//			}
//			catch (AddressException e) {
//				// Invalid email address format
//				//				errors.add("email", new ActionMessage("error.resetpassword.sendmailerror"));
//				log.warn( "AddressException: to email address: " + sendEmailDTO.getToEmailAddress(), e );
//				throw e; 
//			}
//			catch (SendFailedException e) {
//				// Invalid email address format
//				log.error( "SendFailedException: to email address: " + sendEmailDTO.getToEmailAddress()
//						+ ", Smtp Server Host: " + GetEmailConfig.getSmtpServerURL(), e );
//				throw e; 
//			}
//			catch (MessagingException e) {
//				// Invalid email address format
//				log.error( "MessagingException: to email address: " + sendEmailDTO.getToEmailAddress()
//						+ ", Smtp Server Host: " + GetEmailConfig.getSmtpServerURL(), e );
//				throw e; 
//			}
//			catch (Exception e) {
//				// Invalid email address format
//				log.error( "Exception: to email address: " + sendEmailDTO.getToEmailAddress()
//						+ ", Smtp Server Host: " + GetEmailConfig.getSmtpServerURL(), e );
//				throw e; 
//			}
//		}
	}
	
//	/**
//	 * @param sendEmailDTO
//	 * @return
//	 * @throws Exception 
//	 */
//	private MimeMessage createSMTPMailMessageToSend( SendEmailDTO sendEmailDTO ) throws Exception {
//		// set the SMTP host property value
//		Properties properties = System.getProperties();
//		properties.put( "mail.smtp.host", GetEmailConfig.getSmtpServerURL() );
//		// create a JavaMail session
//		javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);
//		// create a new MIME message
//		MimeMessage message = new MimeMessage(mSession);
//		// set the from address
//		Address fromAddress = new InternetAddress( sendEmailDTO.getFromEmailAddress() );
//		message.setFrom(fromAddress);
//		// set the to address
//		Address[] toAddress = InternetAddress.parse( sendEmailDTO.getToEmailAddress() );
//		message.setRecipients(Message.RecipientType.TO, toAddress);
//		// set the subject
//		message.setSubject( sendEmailDTO.getEmailSubject() );
//		message.setText( sendEmailDTO.getEmailBody() );
//		return message;
//	}
}
