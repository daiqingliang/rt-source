package sun.net.smtp;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.AccessController;
import sun.net.TransferProtocolClient;
import sun.security.action.GetPropertyAction;

public class SmtpClient extends TransferProtocolClient {
  private static int DEFAULT_SMTP_PORT = 25;
  
  String mailhost;
  
  SmtpPrintStream message;
  
  public void closeServer() throws IOException {
    if (serverIsOpen()) {
      closeMessage();
      issueCommand("QUIT\r\n", 221);
      super.closeServer();
    } 
  }
  
  void issueCommand(String paramString, int paramInt) throws IOException {
    sendServer(paramString);
    int i;
    while ((i = readServerResponse()) != paramInt) {
      if (i != 220)
        throw new SmtpProtocolException(getResponseString()); 
    } 
  }
  
  private void toCanonical(String paramString) throws IOException {
    if (paramString.startsWith("<")) {
      issueCommand("rcpt to: " + paramString + "\r\n", 250);
    } else {
      issueCommand("rcpt to: <" + paramString + ">\r\n", 250);
    } 
  }
  
  public void to(String paramString) throws IOException {
    if (paramString.indexOf('\n') != -1)
      throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return")); 
    byte b1 = 0;
    int i = paramString.length();
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    boolean bool = false;
    while (b2 < i) {
      char c = paramString.charAt(b2);
      if (b4) {
        if (c == '(') {
          b4++;
        } else if (c == ')') {
          b4--;
        } 
        if (b4 == 0)
          if (b3 > b1) {
            bool = true;
          } else {
            b1 = b2 + 1;
          }  
      } else if (c == '(') {
        b4++;
      } else if (c == '<') {
        b1 = b3 = b2 + 1;
      } else if (c == '>') {
        bool = true;
      } else if (c == ',') {
        if (b3 > b1)
          toCanonical(paramString.substring(b1, b3)); 
        b1 = b2 + 1;
        bool = false;
      } else if (c > ' ' && !bool) {
        b3 = b2 + 1;
      } else if (b1 == b2) {
        b1++;
      } 
      b2++;
    } 
    if (b3 > b1)
      toCanonical(paramString.substring(b1, b3)); 
  }
  
  public void from(String paramString) throws IOException {
    if (paramString.indexOf('\n') != -1)
      throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return")); 
    if (paramString.startsWith("<")) {
      issueCommand("mail from: " + paramString + "\r\n", 250);
    } else {
      issueCommand("mail from: <" + paramString + ">\r\n", 250);
    } 
  }
  
  private void openServer(String paramString) throws IOException {
    this.mailhost = paramString;
    openServer(this.mailhost, DEFAULT_SMTP_PORT);
    issueCommand("helo " + InetAddress.getLocalHost().getHostName() + "\r\n", 250);
  }
  
  public PrintStream startMessage() throws IOException {
    issueCommand("data\r\n", 354);
    try {
      this.message = new SmtpPrintStream(this.serverOutput, this);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new InternalError(encoding + " encoding not found", unsupportedEncodingException);
    } 
    return this.message;
  }
  
  void closeMessage() throws IOException {
    if (this.message != null)
      this.message.close(); 
  }
  
  public SmtpClient(String paramString) throws IOException {
    if (paramString != null)
      try {
        openServer(paramString);
        this.mailhost = paramString;
        return;
      } catch (Exception exception) {} 
    try {
      this.mailhost = (String)AccessController.doPrivileged(new GetPropertyAction("mail.host"));
      if (this.mailhost != null) {
        openServer(this.mailhost);
        return;
      } 
    } catch (Exception exception) {}
    try {
      this.mailhost = "localhost";
      openServer(this.mailhost);
    } catch (Exception exception) {
      this.mailhost = "mailhost";
      openServer(this.mailhost);
    } 
  }
  
  public SmtpClient() throws IOException { this(null); }
  
  public SmtpClient(int paramInt) throws IOException {
    setConnectTimeout(paramInt);
    try {
      this.mailhost = (String)AccessController.doPrivileged(new GetPropertyAction("mail.host"));
      if (this.mailhost != null) {
        openServer(this.mailhost);
        return;
      } 
    } catch (Exception exception) {}
    try {
      this.mailhost = "localhost";
      openServer(this.mailhost);
    } catch (Exception exception) {
      this.mailhost = "mailhost";
      openServer(this.mailhost);
    } 
  }
  
  public String getMailHost() { return this.mailhost; }
  
  String getEncoding() { return encoding; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\smtp\SmtpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */