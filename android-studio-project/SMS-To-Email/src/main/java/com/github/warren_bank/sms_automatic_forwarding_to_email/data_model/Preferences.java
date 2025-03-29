package com.github.warren_bank.sms_automatic_forwarding_to_email.data_model;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

public final class Preferences {
  private static final String PREFS_FILENAME              = "PREFS";
  private static final String PREF_ENABLED                = "ENABLED";
  private static final String PREF_RECIPIENT_LIST_ITEMS   = "RECIPIENT_LIST_ITEMS";
  private static final String PREF_SMTP_SERVER_LIST_ITEMS = "SMTP_SERVER_LIST_ITEMS";
  private static final String PREF_SMTP_SERVER_LIST_INDEX = "SMTP_SERVER_LIST_INDEX";

  private static SharedPreferences getPrefs(Context context) {
    return context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
  }

  private static SharedPreferences.Editor getPrefsEditor(Context context) {
    return getPrefs(context).edit();
  }

  public static boolean isEnabled(Context context) {
    SharedPreferences prefs = getPrefs(context);
    return prefs.getBoolean(PREF_ENABLED, true);
  }

  public static void setEnabled(Context context, boolean enabled) {
    SharedPreferences.Editor prefs_editor = getPrefsEditor(context);
    prefs_editor.putBoolean(PREF_ENABLED, enabled);
    prefs_editor.apply();
  }

  public static ArrayList<RecipientListItem> getRecipientListItems(Context context) {
    SharedPreferences prefs = getPrefs(context);
    String json = prefs.getString(PREF_RECIPIENT_LIST_ITEMS, null);

    return (json == null)
      ? new ArrayList<RecipientListItem>()
      : RecipientListItem.fromJson(json)
    ;
  }

  public static void setRecipientListItems(Context context, ArrayList<RecipientListItem> listItems) {
    String json = RecipientListItem.toJson(listItems);

    SharedPreferences.Editor prefs_editor = getPrefsEditor(context);
    prefs_editor.putString(PREF_RECIPIENT_LIST_ITEMS, json);
    prefs_editor.apply();
  }

  public static ArrayList<SmtpServerListItem> getSmtpServerListItems(Context context) {
    SharedPreferences prefs = getPrefs(context);
    String json = prefs.getString(PREF_SMTP_SERVER_LIST_ITEMS, null);

    return (json == null)
      ? new ArrayList<SmtpServerListItem>()
      : SmtpServerListItem.fromJson(json)
    ;
  }

  public static void setSmtpServerListItems(Context context, ArrayList<SmtpServerListItem> listItems) {
    String json = SmtpServerListItem.toJson(listItems);

    SharedPreferences.Editor prefs_editor = getPrefsEditor(context);
    prefs_editor.putString(PREF_SMTP_SERVER_LIST_ITEMS, json);
    prefs_editor.apply();
  }

  public static int getSmtpServerListItemIndex(Context context) {
    SharedPreferences prefs = getPrefs(context);
    return prefs.getInt(PREF_SMTP_SERVER_LIST_INDEX, 0);
  }

  public static void setSmtpServerListItemIndex(Context context, int index) {
    SharedPreferences.Editor prefs_editor = getPrefsEditor(context);
    prefs_editor.putInt(PREF_SMTP_SERVER_LIST_INDEX, index);
    prefs_editor.apply();
  }

  // convenience method

  public static SmtpServerListItem getSmtpServerListItem(Context context) {
    try {
      int index = getSmtpServerListItemIndex(context);
      ArrayList<SmtpServerListItem> listItems = getSmtpServerListItems(context);
      return listItems.get(index);
    }
    catch(Exception e) {}
    return null;
  }
}
