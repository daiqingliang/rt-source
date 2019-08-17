package com.sun.security.auth.callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import jdk.Exported;
import sun.security.util.Password;

@Exported
public class TextCallbackHandler implements CallbackHandler {
  public void handle(Callback[] paramArrayOfCallback) throws IOException, UnsupportedCallbackException {
    ConfirmationCallback confirmationCallback = null;
    for (byte b = 0; b < paramArrayOfCallback.length; b++) {
      if (paramArrayOfCallback[b] instanceof TextOutputCallback) {
        String str1;
        TextOutputCallback textOutputCallback = (TextOutputCallback)paramArrayOfCallback[b];
        switch (textOutputCallback.getMessageType()) {
          case 0:
            str1 = "";
            break;
          case 1:
            str1 = "Warning: ";
            break;
          case 2:
            str1 = "Error: ";
            break;
          default:
            throw new UnsupportedCallbackException(paramArrayOfCallback[b], "Unrecognized message type");
        } 
        String str2 = textOutputCallback.getMessage();
        if (str2 != null)
          str1 = str1 + str2; 
        if (str1 != null)
          System.err.println(str1); 
      } else if (paramArrayOfCallback[b] instanceof NameCallback) {
        NameCallback nameCallback = (NameCallback)paramArrayOfCallback[b];
        if (nameCallback.getDefaultName() == null) {
          System.err.print(nameCallback.getPrompt());
        } else {
          System.err.print(nameCallback.getPrompt() + " [" + nameCallback.getDefaultName() + "] ");
        } 
        System.err.flush();
        String str = readLine();
        if (str.equals(""))
          str = nameCallback.getDefaultName(); 
        nameCallback.setName(str);
      } else if (paramArrayOfCallback[b] instanceof PasswordCallback) {
        PasswordCallback passwordCallback = (PasswordCallback)paramArrayOfCallback[b];
        System.err.print(passwordCallback.getPrompt());
        System.err.flush();
        passwordCallback.setPassword(Password.readPassword(System.in, passwordCallback.isEchoOn()));
      } else if (paramArrayOfCallback[b] instanceof ConfirmationCallback) {
        confirmationCallback = (ConfirmationCallback)paramArrayOfCallback[b];
      } else {
        throw new UnsupportedCallbackException(paramArrayOfCallback[b], "Unrecognized Callback");
      } 
    } 
    if (confirmationCallback != null)
      doConfirmation(confirmationCallback); 
  }
  
  private String readLine() throws IOException {
    String str = (new BufferedReader(new InputStreamReader(System.in))).readLine();
    if (str == null)
      throw new IOException("Cannot read from System.in"); 
    return str;
  }
  
  private void doConfirmation(ConfirmationCallback paramConfirmationCallback) throws IOException, UnsupportedCallbackException {
    byte b;
    String[] arrayOfString;
    OptionInfo[] arrayOfOptionInfo;
    String str1;
    int i = paramConfirmationCallback.getMessageType();
    switch (i) {
      case 1:
        str1 = "Warning: ";
        break;
      case 2:
        str1 = "Error: ";
        break;
      case 0:
        str1 = "";
        break;
      default:
        throw new UnsupportedCallbackException(paramConfirmationCallback, "Unrecognized message type: " + i);
    } 
    int j = paramConfirmationCallback.getOptionType();
    switch (j) {
      case 0:
        class OptionInfo {
          String name;
          
          int value;
          
          OptionInfo(String param1String, int param1Int) {
            this.name = param1String;
            this.value = param1Int;
          }
        };
        arrayOfOptionInfo = new OptionInfo[] { new OptionInfo("Yes", 0), new OptionInfo(this, "No", 1) };
        break;
      case 1:
        arrayOfOptionInfo = new OptionInfo[] { new OptionInfo(this, "Yes", 0), new OptionInfo(this, "No", 1), new OptionInfo(this, "Cancel", 2) };
        break;
      case 2:
        arrayOfOptionInfo = new OptionInfo[] { new OptionInfo(this, "OK", 3), new OptionInfo(this, "Cancel", 2) };
        break;
      case -1:
        arrayOfString = paramConfirmationCallback.getOptions();
        arrayOfOptionInfo = new OptionInfo[arrayOfString.length];
        for (b = 0; b < arrayOfOptionInfo.length; b++)
          arrayOfOptionInfo[b] = new OptionInfo(this, arrayOfString[b], b); 
        break;
      default:
        throw new UnsupportedCallbackException(paramConfirmationCallback, "Unrecognized option type: " + j);
    } 
    int k = paramConfirmationCallback.getDefaultOption();
    String str2 = paramConfirmationCallback.getPrompt();
    if (str2 == null)
      str2 = ""; 
    str2 = str1 + str2;
    if (!str2.equals(""))
      System.err.println(str2); 
    int m;
    for (m = 0; m < arrayOfOptionInfo.length; m++) {
      if (j == -1) {
        System.err.println(m + ". " + (arrayOfOptionInfo[m]).name + ((m == k) ? " [default]" : ""));
      } else {
        System.err.println(m + ". " + (arrayOfOptionInfo[m]).name + (((arrayOfOptionInfo[m]).value == k) ? " [default]" : ""));
      } 
    } 
    System.err.print("Enter a number: ");
    System.err.flush();
    try {
      m = Integer.parseInt(readLine());
      if (m < 0 || m > arrayOfOptionInfo.length - 1)
        m = k; 
      m = (arrayOfOptionInfo[m]).value;
    } catch (NumberFormatException numberFormatException) {
      m = k;
    } 
    paramConfirmationCallback.setSelectedIndex(m);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\callback\TextCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */