package com.github.warren_bank.sms_automatic_forwarding_to_email.data_model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public final class SmtpServerListItem {
  public String hostname;
  public String port;
  public boolean useSSL;
  public boolean useTLS;
  public boolean useAuth;
  public String username;
  public String password;
  public String from;

  public SmtpServerListItem() {
    this.hostname = "";
    this.port     = "";
    this.useSSL   = false;
    this.useTLS   = false;
    this.useAuth  = false;
    this.username = "";
    this.password = "";
    this.from     = "";
  }

  public SmtpServerListItem(String hostname, String port, boolean useSSL, boolean useTLS, boolean useAuth, String username, String password, String from) {
    this.hostname = hostname;
    this.port     = port;
    this.useSSL   = useSSL;
    this.useTLS   = useTLS;
    this.useAuth  = useAuth;
    this.username = username;
    this.password = password;
    this.from     = from;
  }

  @Override
  public String toString() {
    return hostname;
  }

  // helpers

  public static ArrayList<SmtpServerListItem> fromJson(String json) {
    ArrayList<SmtpServerListItem> arrayList;
    Gson gson = new Gson();
    arrayList = gson.fromJson(json, new TypeToken<ArrayList<SmtpServerListItem>>(){}.getType());
    return arrayList;
  }

  public static String toJson(ArrayList<SmtpServerListItem> arrayList) {
    String json = new Gson().toJson(arrayList);
    return json;
  }
}
