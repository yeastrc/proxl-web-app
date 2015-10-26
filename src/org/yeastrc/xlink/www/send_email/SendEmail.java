package org.yeastrc.xlink.www.send_email;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;

/**
 * 
 *
 */
public class SendEmail {

	
	private static final SendEmail instance = new SendEmail();

	private SendEmail() { }
	public static SendEmail getInstance() { return instance; }

	
	private static final Logger log = Logger.getLogger(SendEmail.class);





	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String SUBJECT = "subject";
	private static final String BODY = "body";


	public void sendEmail( SendEmailDTO sendEmailDTO ) throws Exception  {


		GetEmailConfig.validateEmailConfig();
		

		String email_webservice_url = 
				ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_WEBSERVICE_URL_KEY );


		if ( StringUtils.isNotEmpty( email_webservice_url )  ) {


			HttpClient client = null;
			HttpPost post = null;
			BufferedReader rd = null;
			List<NameValuePair> nameValuePairs = null;
			HttpResponse response = null;


			try {

				client = new DefaultHttpClient();

				post = new HttpPost( email_webservice_url );

				nameValuePairs = new ArrayList<NameValuePair>(1);

				nameValuePairs.add(new BasicNameValuePair(TO, sendEmailDTO.getToEmailAddress() ));
				nameValuePairs.add(new BasicNameValuePair(FROM, sendEmailDTO.getFromEmailAddress() ));
				nameValuePairs.add(new BasicNameValuePair(SUBJECT, sendEmailDTO.getEmailSubject() ));
				nameValuePairs.add(new BasicNameValuePair(BODY, sendEmailDTO.getEmailBody() ));

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				response = client.execute(post);
				
				int httpStatusCode = response.getStatusLine().getStatusCode();

				if ( log.isDebugEnabled() ) {

					log.debug("Send Email: Http Response Status code: " + httpStatusCode );
				}

				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
				String line = "";
				while ((line = rd.readLine()) != null) {

					if ( log.isDebugEnabled() ) {

						log.debug("Send Email: Http Response Line: " + line);
					}

				}

				
				if ( httpStatusCode != HttpStatus.SC_OK ) {
					
					String msg = "Failed to send Email.  Http Response Status code: " + httpStatusCode ;
					
					log.error( msg );
					
					throw new Exception(msg);
				}


			} catch (Exception e) {

				log.error("Failed to send email request.  from address = |" + sendEmailDTO.getFromEmailAddress() 
						+ "|, to address = |" + sendEmailDTO.getToEmailAddress() + "|.", e );
				throw e;

			} finally { 

				if ( rd != null ) {
					rd.close();
				}
			}



		} else {




			// Generate and send the email to the user.
			try {

				MimeMessage message = createSMTPMailMessageToSend( sendEmailDTO );

				// send the message
				Transport.send(message);

			}
			catch (AddressException e) {
				// Invalid email address format

				//				errors.add("email", new ActionMessage("error.resetpassword.sendmailerror"));
				log.warn( "AddressException: to email address: " + sendEmailDTO.getToEmailAddress(), e );

				throw e; 
			}
			catch (SendFailedException e) {
				// Invalid email address format

				//				errors.add("email", new ActionMessage("error.resetpassword.sendmail.error.system"));

				log.error( "SendFailedException: to email address: " + sendEmailDTO.getToEmailAddress(), e );

				throw e; 
			}
			catch (MessagingException e) {
				// Invalid email address format

				//				errors.add("email", new ActionMessage("error.resetpassword.sendmail.error.system"));

				log.error( "MessagingException: to email address: " + sendEmailDTO.getToEmailAddress(), e );

				throw e; 
			}
			catch (Exception e) {
				// Invalid email address format

				//				errors.add("email", new ActionMessage("error.resetpassword.sendmail.error.system"));

				log.error( "Exception: to email address: " + sendEmailDTO.getToEmailAddress(), e );

				throw e; 
			}




		}

	}
	
	
	
	/**
	 * @param sendEmailDTO
	 * @return
	 * @throws Exception 
	 */
	private MimeMessage createSMTPMailMessageToSend( SendEmailDTO sendEmailDTO )
			throws Exception {

		// set the SMTP host property value
		Properties properties = System.getProperties();
		properties.put( "mail.smtp.host", GetEmailConfig.getSmtpServerURL() );

		// create a JavaMail session
		javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);

		// create a new MIME message
		MimeMessage message = new MimeMessage(mSession);

		// set the from address
		Address fromAddress = new InternetAddress( sendEmailDTO.getFromEmailAddress() );
		message.setFrom(fromAddress);

		// set the to address
		Address[] toAddress = InternetAddress.parse( sendEmailDTO.getToEmailAddress() );
		message.setRecipients(Message.RecipientType.TO, toAddress);

		// set the subject
		message.setSubject( sendEmailDTO.getEmailSubject() );


		message.setText( sendEmailDTO.getEmailBody() );


		return message;
	}


}
