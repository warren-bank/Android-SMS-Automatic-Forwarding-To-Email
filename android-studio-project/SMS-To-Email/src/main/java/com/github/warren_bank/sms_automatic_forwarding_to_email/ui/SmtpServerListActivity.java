package com.github.warren_bank.sms_automatic_forwarding_to_email.ui;

import com.github.warren_bank.sms_automatic_forwarding_to_email.R;
import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.Preferences;
import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.RecipientListItem;
import com.github.warren_bank.sms_automatic_forwarding_to_email.data_model.SmtpServerListItem;
import com.github.warren_bank.sms_automatic_forwarding_to_email.event.EmailSender;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

public class SmtpServerListActivity extends Activity {
  private ListView listView;

  private int                               listItemSelectedIndex;
  private ArrayList<SmtpServerListItem>     listItems;
  private ArrayAdapter<SmtpServerListItem>  listAdapter;

  // ---------------------------------------------------------------------------------------------
  // Lifecycle Events:
  // ---------------------------------------------------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_smtp_server_list);

    listView = (ListView) findViewById(R.id.listview);

    listItemSelectedIndex = Preferences.getSmtpServerListItemIndex(SmtpServerListActivity.this);
    listItems             = Preferences.getSmtpServerListItems(SmtpServerListActivity.this);
    listAdapter           = new ArrayAdapter<SmtpServerListItem>(SmtpServerListActivity.this, android.R.layout.simple_list_item_1, listItems) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        if (position == listItemSelectedIndex)
          view.setBackgroundResource(R.color.listview_background_highlighted);
        else
          view.setBackgroundResource(0);

        return view;
      }
    };

    listView.setAdapter(listAdapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showEditDialog(position);
        return;
      }
    });

    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == listItemSelectedIndex) return false;

        listItemSelectedIndex = position;
        listAdapter.notifyDataSetChanged();
        Preferences.setSmtpServerListItemIndex(SmtpServerListActivity.this, position);
        return true;
      }
    });
  }

  // ---------------------------------------------------------------------------------------------
  // ActionBar:
  // ---------------------------------------------------------------------------------------------

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getActionBar().setDisplayShowHomeEnabled(false);
    getMenuInflater().inflate(R.menu.activity_smtp_server_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch(menuItem.getItemId()) {
      case R.id.menu_add: {
        showEditDialog(-1);
        return true;
      }
      default: {
        return super.onOptionsItemSelected(menuItem);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Add/Edit Dialog:
  // ---------------------------------------------------------------------------------------------

  private void showEditDialog(final int position) {
    final boolean isAdd = (position < 0);

    final SmtpServerListItem listItem = (isAdd)
      ? new SmtpServerListItem()
      : listItems.get(position)
    ;

    final Dialog dialog = new Dialog(SmtpServerListActivity.this, R.style.app_theme);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_smtp_server_listitem);

    final EditText inputHostname       = (EditText) dialog.findViewById(R.id.input_smtp_hostname);
    final EditText inputPort           = (EditText) dialog.findViewById(R.id.input_smtp_port);
    final Spinner  inputEncryption     = (Spinner)  dialog.findViewById(R.id.input_smtp_encryption);
    final CheckBox inputAuthentication = (CheckBox) dialog.findViewById(R.id.input_smtp_authentication);
    final EditText inputUsername       = (EditText) dialog.findViewById(R.id.input_smtp_username);
    final EditText inputPassword       = (EditText) dialog.findViewById(R.id.input_smtp_password);
    final EditText inputFrom           = (EditText) dialog.findViewById(R.id.input_smtp_from);

    final EditText inputTestRecipient  = (EditText) dialog.findViewById(R.id.input_test_email_recipient);
    final Button   buttonTestSent      = (Button)   dialog.findViewById(R.id.button_test_email_send);

    final Button buttonDelete          = (Button) dialog.findViewById(R.id.button_delete);
    final Button buttonSave            = (Button) dialog.findViewById(R.id.button_save);

    inputHostname.setText(listItem.hostname);
    inputPort.setText(listItem.port);
    inputUsername.setText(listItem.username);
    inputPassword.setText(listItem.password);
    inputFrom.setText(listItem.from);

    if (listItem.useSSL)
      inputEncryption.setSelection(1);
    else if (listItem.useTLS)
      inputEncryption.setSelection(2);
    else
      inputEncryption.setSelection(0);

    inputAuthentication.setChecked(listItem.useAuth);

    if (isAdd) {
      buttonDelete.setText(R.string.label_button_cancel);
    }

    buttonTestSent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String email_recipient = inputTestRecipient.getText().toString().trim();

        if (email_recipient.equals("")) {
          Toast.makeText(SmtpServerListActivity.this, SmtpServerListActivity.this.getString(R.string.error_missing_required_value), Toast.LENGTH_SHORT).show();
          return;
        }

        final String hostname = inputHostname.getText().toString().trim();
        final String port     = inputPort.getText().toString().trim();
        final String username = inputUsername.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        final String from     = inputFrom.getText().toString().trim();

        final int new_encryption = inputEncryption.getSelectedItemPosition();
        final boolean useSSL     = (new_encryption == 1);
        final boolean useTLS     = (new_encryption == 2);
        final boolean useAuth    = inputAuthentication.isChecked();

        if (hostname.equals("") || port.equals("")) {
          Toast.makeText(SmtpServerListActivity.this, SmtpServerListActivity.this.getString(R.string.error_missing_required_value), Toast.LENGTH_SHORT).show();
          return;
        }

        final String email_subject = SmtpServerListActivity.this.getString(R.string.content_test_email_subject);
        final String email_message = SmtpServerListActivity.this.getString(R.string.content_test_email_message);

        SmtpServerListItem smtp_server = new SmtpServerListItem(hostname, port, useSSL, useTLS, useAuth, username, password, from);
        RecipientListItem  recipient   = new RecipientListItem(email_recipient, email_subject, "");

        ArrayList<RecipientListItem> recipients = new ArrayList<RecipientListItem>();
        recipients.add(recipient);

        EmailSender.forward(smtp_server, recipients, email_subject, "", email_message);

        Toast.makeText(SmtpServerListActivity.this, SmtpServerListActivity.this.getString(R.string.notification_test_email_sent), Toast.LENGTH_SHORT).show();
      }
    });

    buttonDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isAdd) {
          listItems.remove(position);
          listAdapter.notifyDataSetChanged();
        }
        dialog.dismiss();
      }
    });

    buttonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String new_hostname = inputHostname.getText().toString().trim();
        final String new_port     = inputPort.getText().toString().trim();
        final String new_username = inputUsername.getText().toString().trim();
        final String new_password = inputPassword.getText().toString().trim();
        final String new_from     = inputFrom.getText().toString().trim();

        final int new_encryption  = inputEncryption.getSelectedItemPosition();
        final boolean new_ssl     = (new_encryption == 1);
        final boolean new_tls     = (new_encryption == 2);
        final boolean new_auth    = inputAuthentication.isChecked();

        final boolean same_hostname = new_hostname.equals(listItem.hostname);
        final boolean same_port     = new_port.equals(listItem.port);
        final boolean same_username = new_username.equals(listItem.username);
        final boolean same_password = new_password.equals(listItem.password);
        final boolean same_from     = new_from.equals(listItem.from);
        final boolean same_ssl      = (new_ssl  == listItem.useSSL);
        final boolean same_tls      = (new_tls  == listItem.useTLS);
        final boolean same_auth     = (new_auth == listItem.useAuth);

        if (new_hostname.equals("") || new_port.equals("")) {
          Toast.makeText(SmtpServerListActivity.this, SmtpServerListActivity.this.getString(R.string.error_missing_required_value), Toast.LENGTH_SHORT).show();
          return;
        }

        if (same_hostname && same_port && same_username && same_password && same_from && same_ssl && same_tls && same_auth) {
          // no change
          dialog.dismiss();
          return;
        }

        if (!same_hostname) {
          listItem.hostname = new_hostname;
        }
        if (!same_port) {
          listItem.port = new_port;
        }
        if (!same_username) {
          listItem.username = new_username;
        }
        if (!same_password) {
          listItem.password = new_password;
        }
        if (!same_from) {
          listItem.from = new_from;
        }
        if (!same_ssl) {
          listItem.useSSL = new_ssl;
        }
        if (!same_tls) {
          listItem.useTLS = new_tls;
        }
        if (!same_auth) {
          listItem.useAuth = new_auth;
        }

        if (isAdd) {
          if (!listItems.add(listItem)) {
            Toast.makeText(SmtpServerListActivity.this, SmtpServerListActivity.this.getString(R.string.error_add_listitem), Toast.LENGTH_SHORT).show();
            return;
          }          
        }

        listAdapter.notifyDataSetChanged();
        dialog.dismiss();

        Preferences.setSmtpServerListItems(SmtpServerListActivity.this, listItems);
      }
    });

    dialog.show();
  }
}
