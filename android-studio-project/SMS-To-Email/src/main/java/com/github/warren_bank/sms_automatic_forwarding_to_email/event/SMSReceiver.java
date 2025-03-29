package com.github.warren_bank.sms_automatic_forwarding_to_email.event;

import com.github.warren_bank.sms_automatic_forwarding_to_email.R;
import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.Preferences;
import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.RecipientListItem;
import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.SmtpServerListItem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.ArrayList;

public class SMSReceiver extends BroadcastReceiver {
  private static final String TAG      = "SMSReceiver";
  private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

  public void onReceive(Context context, Intent intent) {
    if (!Preferences.isEnabled(context))
      return;

    final String action = intent.getAction();
    final Bundle extras = intent.getExtras();

    if (extras == null)
      return;

    if (action.equals(SMS_RECEIVED)) {
      final SmsMessage[] messages = get_SmsMessages(extras);

      if (messages.length == 0)
        return;

      final ArrayList<RecipientListItem> recipientListItems = Preferences.getRecipientListItems(context);

      if (recipientListItems.isEmpty())
        return;

      final SmtpServerListItem smtp_server = Preferences.getSmtpServerListItem(context);

      if (smtp_server == null)
        return;

      final String default_subject            = context.getString(R.string.default_email_subject);

      String sender                           = null;
      String body                             = null;
      ArrayList<RecipientListItem> recipients = null;

      for (SmsMessage message : messages) {
        if (message == null)
          continue;

        try {
          sender     = message.getOriginatingAddress().trim();
          body       = message.getMessageBody().trim();
          recipients = RecipientListItem.match(recipientListItems, sender);

          Log.i(TAG, "SMS received.\nfrom: " + sender + "\nmessage: " + body);

          EmailSender.forward(smtp_server, recipients, default_subject, sender, body);
        }
        catch (Exception e) { continue; }
      }
    }
  }

  private final static SmsMessage[] get_SmsMessages(Bundle extras) {
    final Object[] pdus = (Object[])extras.get("pdus");
    final String format = extras.getString("format", "3gpp");
    final SmsMessage[] messages = new SmsMessage[pdus.length];

    for (int i = 0; i < pdus.length; i++) {
      try {
        messages[i] = (Build.VERSION.SDK_INT >= 23)
          ? SmsMessage.createFromPdu((byte[])pdus[i], format)
          : SmsMessage.createFromPdu((byte[])pdus[i]);
      }
      catch (Exception e) {}
    }
    return messages;
  }
}
