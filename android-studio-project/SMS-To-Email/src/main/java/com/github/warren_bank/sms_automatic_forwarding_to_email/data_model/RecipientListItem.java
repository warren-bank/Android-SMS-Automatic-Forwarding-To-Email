package com.github.warren_bank.sms_automatic_forwarding_to_email.data_model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public final class RecipientListItem {
  public String email_recipient;
  public String email_subject;
  public String sms_sender;

  public RecipientListItem() {
    this.email_recipient = "";
    this.email_subject   = "";
    this.sms_sender      = "";
  }

  public RecipientListItem(String email_recipient, String email_subject, String sms_sender) {
    this.email_recipient = email_recipient;
    this.email_subject   = email_subject;
    this.sms_sender      = sms_sender;
  }

  @Override
  public String toString() {
    return email_recipient;
  }

  // helpers

  public static ArrayList<RecipientListItem> fromJson(String json) {
    ArrayList<RecipientListItem> arrayList;
    Gson gson = new Gson();
    arrayList = gson.fromJson(json, new TypeToken<ArrayList<RecipientListItem>>(){}.getType());
    return arrayList;
  }

  public static String toJson(ArrayList<RecipientListItem> arrayList) {
    String json = new Gson().toJson(arrayList);
    return json;
  }

  public static ArrayList<RecipientListItem> match(ArrayList<RecipientListItem> arrayList, String sender) {
    ArrayList<RecipientListItem> recipients = new ArrayList<RecipientListItem>();
    RecipientListItem item;

    for (int i=0; i < arrayList.size(); i++) {
      try {
        item = arrayList.get(i);

        // required
        if (!sender.endsWith(item.sms_sender) && !item.sms_sender.equals("*"))
          continue;

        recipients.add(item);
      }
      catch(Exception e) { continue; }
    }
    return recipients;
  }
}
