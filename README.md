### [SMS To Email](https://github.com/warren-bank/Android-SMS-Automatic-Forwarding-To-Email)

Android app that listens for incoming SMS text messages and conditionally forwards them to email.

#### Notes:

* minimum supported version of Android:
  - Android 4.4 (API level 19)

- - - -

### Configuration

#### Forwarding Rules:

* `Enable Service` checkbox:
  - used to enable/disable this service
* `ADD` ActionBar menu item:
  - adds new forwarding rule
* forwarding rule entries are defined as follows:
  - `Email Recipient`:
    * an email address
  - `Email Subject`:
    * default: `New text message from {{sender}}`
    * where: `{{sender}}` is replaced with the phone number of the sender
  - `Sender must end with`:
    * this value specifies a phone number (without any punctuation)
      - a match occurs when the _sender_ of an incoming SMS message ends with this exact value
      - a special match-all glob pattern `*` is supported,<br>which can either be manually entered into the field<br>or automatically set when the field is left empty
    * this value acts as a filter
      - `Email Recipient` will only receive copies of incoming SMS messages that match this value
* forwarding rule entries can be modified
  - clicking on an existing recipient opens a dialog with options to:
    * edit field values, and save changes
    * delete

#### SMTP servers:

* SMTP server entries are defined as follows:
  - `Host`
  - `Port`
  - `Use Encryption`:
    * options: `no`, `SSL`, `TLS`
  - `Require Authentication`
  - `Username`
  - `Password`
  - `From`:
    * an email address
    * typically, the SMTP server validates this value against the account
* SMTP server entries can be modified
  - clicking on an existing SMTP server opens a dialog with options to:
    * edit field values, and save changes
    * delete
* only one SMTP server entry can be enabled at any time
  - long pressing on an existing SMTP server enables the entry
    * it will be used to send all email messages
    * the background color of the entry is highlighted to indicate its enabled state

#### Example SMTP server configs:

* [Zoho Mail](https://www.zoho.com/mail/help/zoho-smtp.html)
  - variation 1:
    ```text
    host = smtp.zoho.com
    port = 587
    use encryption = TLS
    require authentication = true
    username = <my-account-username>@zohomail.com
    password = <my-account-password>
    from = <my-account-username>@zohomail.com
    ```
  - variation 2:
    ```text
    host = smtp.zoho.com
    port = 465
    use encryption = SSL
    require authentication = true
    username = <my-account-username>@zohomail.com
    password = <my-account-password>
    from = <my-account-username>@zohomail.com
    ```
  - pricing:
    * [free tier](https://www.zoho.com/mail/zohomail-pricing.html)
      - no credit card required
      - no limits (within reason)
* [GMX Mail](https://support.gmx.com/pop-imap/pop3/serverdata.html)
  - variation 1:
    ```text
    host = mail.gmx.com
    port = 587
    use encryption = TLS
    require authentication = true
    username = <my-account-username>@gmx.com
    password = <my-account-password>
    from = <my-account-username>@gmx.com
    ```
  - pricing:
    * [free](https://www.gmx.com/mail/)
      - no credit card required
      - no limits (within reason)
* [Mailtrap](https://help.mailtrap.io/article/5-smtp-integration)
  - variation 1 (demo domain):
    ```text
    host = live.smtp.mailtrap.io
    port = 587
    use encryption = TLS
    require authentication = true
    username = smtp@mailtrap.io
    password = <my-smtp-password>
    from = hello@demomailtrap.co
    ```
  - pricing:
    * [free tier](https://mailtrap.io/pricing/)
      - no credit card required
      - 1000 emails/month
      - 200 emails/day
      - 100 distinct email recipients
  - notes:
    * the SMTP password is not the same as the account password;<br>
      a unique random value is provided on the _Integration_ tab
  - demo domain:
    * emails are sent from: `@demomailtrap.co`
    * emails can only be sent to the email address that registered the account

- - - -

#### Caveats:

* _Google Voice_:
  - when an SMS is sent from a _Google Voice_ number to the SIM card number
    * everything works as expected
  - when an SMS is sent to a _Google Voice_ number
    * the sender of the SMS message will always be a number belonging to the _Google Voice_ backend infrastructure
    * keep this in mind when setting forwarding filters

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
