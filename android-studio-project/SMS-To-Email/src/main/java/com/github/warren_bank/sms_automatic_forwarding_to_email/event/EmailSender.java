package com.github.warren_bank.sms_automatic_forwarding_to_email.event;

import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.RecipientListItem;
import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.SmtpServerListItem;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Properties;

public final class EmailSender {

  public static void forward(final SmtpServerListItem smtp_server, final ArrayList<RecipientListItem> recipients, final String default_subject, final String sender, final String body) {
    Thread thread = new Thread() {
      @Override
      public void run() {
        EmailSender.sendEmail(smtp_server, recipients, default_subject, sender, body);
      }
    };

    thread.start();
  }

  private static void sendEmail(SmtpServerListItem smtp_server, ArrayList<RecipientListItem> recipients, String default_subject, String sender, String body) {
    if (smtp_server == null)
      return;

    if (recipients.isEmpty())
      return;

    Session session;
    RecipientListItem recipient;
    MimeMessage mm;

    try {
      session = getSession(smtp_server);
    }
    catch(Exception e) { return; }

    for (int i=0; i < recipients.size(); i++) {
      try {
        recipient = recipients.get(i);
        mm = getMimeMessage(session, smtp_server.from, recipient, default_subject, sender, body);

        Transport.send(mm);
      }
      catch(Exception e) { continue; }
    }
  }

  private static Session getSession(SmtpServerListItem smtp_server) {
    Properties props = getProperties(smtp_server);

    Session session = Session.getInstance(
      props,
      new javax.mail.Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(
            smtp_server.username,
            smtp_server.password
          );
        }
      }
    );

    return session;
  }

  private static Properties getProperties(SmtpServerListItem smtp_server) {
    Properties props = new Properties();

    props.put("mail.transport.protocol",         "smtp");
    props.put("mail.smtp.host",                  smtp_server.hostname);
    props.put("mail.smtp.port",                  smtp_server.port);

    if (smtp_server.useSSL) {
      props.put("mail.smtp.ssl.enable",          "true");
      props.put("mail.smtp.ssl.trust",           "*");
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.socketFactory.port",  smtp_server.port);
    }

    if (smtp_server.useTLS) {
      props.put("mail.smtp.starttls.enable",     "true");
    }

    if (smtp_server.useAuth) {
      props.put("mail.smtp.auth",                "true");
    }

    return props;
  }

  private static MimeMessage getMimeMessage(Session session, String from, RecipientListItem recipient, String default_subject, String sender, String body) throws Exception {
    MimeMessage mm = new MimeMessage(session);

    mm.setFrom(
      new InternetAddress(from)
    );
    mm.addRecipient(
      Message.RecipientType.TO,
      new InternetAddress(recipient.email_recipient)
    );
    mm.setSubject(
      getSubject(recipient.email_subject, default_subject, sender)
    );
    mm.setText(body);

    return mm;
  }

  private static String getSubject(String subject, String default_subject, String sender) {
    String value = ((subject == null) || subject.isEmpty())
      ? default_subject
      : subject;

    value = value.replace("{{sender}}", sender);

    return value;
  }
}
