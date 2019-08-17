package com.sun.security.auth.callback;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import jdk.Exported;

@Exported(false)
@Deprecated
public class DialogCallbackHandler implements CallbackHandler {
  private Component parentComponent;
  
  private static final int JPasswordFieldLen = 8;
  
  private static final int JTextFieldLen = 8;
  
  public DialogCallbackHandler() {}
  
  public DialogCallbackHandler(Component paramComponent) { this.parentComponent = paramComponent; }
  
  public void handle(Callback[] paramArrayOfCallback) throws UnsupportedCallbackException {
    ArrayList arrayList1 = new ArrayList(3);
    ArrayList arrayList2 = new ArrayList(2);
    ConfirmationInfo confirmationInfo = new ConfirmationInfo(null);
    int i;
    for (i = 0; i < paramArrayOfCallback.length; i++) {
      if (paramArrayOfCallback[i] instanceof TextOutputCallback) {
        TextOutputCallback textOutputCallback = (TextOutputCallback)paramArrayOfCallback[i];
        switch (textOutputCallback.getMessageType()) {
          case 0:
            confirmationInfo.messageType = 1;
            break;
          case 1:
            confirmationInfo.messageType = 2;
            break;
          case 2:
            confirmationInfo.messageType = 0;
            break;
          default:
            throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized message type");
        } 
        arrayList1.add(textOutputCallback.getMessage());
      } else if (paramArrayOfCallback[i] instanceof NameCallback) {
        final NameCallback nc = (NameCallback)paramArrayOfCallback[i];
        JLabel jLabel = new JLabel(nameCallback.getPrompt());
        final JTextField name = new JTextField(8);
        String str = nameCallback.getDefaultName();
        if (str != null)
          jTextField.setText(str); 
        Box box = Box.createHorizontalBox();
        box.add(jLabel);
        box.add(jTextField);
        arrayList1.add(box);
        arrayList2.add(new Action() {
              public void perform() { nc.setName(name.getText()); }
            });
      } else if (paramArrayOfCallback[i] instanceof PasswordCallback) {
        final PasswordCallback pc = (PasswordCallback)paramArrayOfCallback[i];
        JLabel jLabel = new JLabel(passwordCallback.getPrompt());
        final JPasswordField password = new JPasswordField(8);
        if (!passwordCallback.isEchoOn())
          jPasswordField.setEchoChar('*'); 
        Box box = Box.createHorizontalBox();
        box.add(jLabel);
        box.add(jPasswordField);
        arrayList1.add(box);
        arrayList2.add(new Action() {
              public void perform() { pc.setPassword(password.getPassword()); }
            });
      } else if (paramArrayOfCallback[i] instanceof ConfirmationCallback) {
        ConfirmationCallback confirmationCallback = (ConfirmationCallback)paramArrayOfCallback[i];
        confirmationInfo.setCallback(confirmationCallback);
        if (confirmationCallback.getPrompt() != null)
          arrayList1.add(confirmationCallback.getPrompt()); 
      } else {
        throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized Callback");
      } 
    } 
    i = JOptionPane.showOptionDialog(this.parentComponent, arrayList1.toArray(), "Confirmation", confirmationInfo.optionType, confirmationInfo.messageType, null, confirmationInfo.options, confirmationInfo.initialValue);
    if (i == 0 || i == 0) {
      Iterator iterator = arrayList2.iterator();
      while (iterator.hasNext())
        ((Action)iterator.next()).perform(); 
    } 
    confirmationInfo.handleResult(i);
  }
  
  private static interface Action {
    void perform();
  }
  
  private static class ConfirmationInfo {
    private int[] translations;
    
    int optionType = 2;
    
    Object[] options = null;
    
    Object initialValue = null;
    
    int messageType = 3;
    
    private ConfirmationCallback callback;
    
    private ConfirmationInfo() {}
    
    void setCallback(ConfirmationCallback param1ConfirmationCallback) throws UnsupportedCallbackException {
      this.callback = param1ConfirmationCallback;
      int i = param1ConfirmationCallback.getOptionType();
      switch (i) {
        case 0:
          this.optionType = 0;
          this.translations = new int[] { 0, 0, 1, 1, -1, 1 };
          break;
        case 1:
          this.optionType = 1;
          this.translations = new int[] { 0, 0, 1, 1, 2, 2, -1, 2 };
          break;
        case 2:
          this.optionType = 2;
          this.translations = new int[] { 0, 3, 2, 2, -1, 2 };
          break;
        case -1:
          this.options = param1ConfirmationCallback.getOptions();
          this.translations = new int[] { -1, param1ConfirmationCallback.getDefaultOption() };
          break;
        default:
          throw new UnsupportedCallbackException(param1ConfirmationCallback, "Unrecognized option type: " + i);
      } 
      int j = param1ConfirmationCallback.getMessageType();
      switch (j) {
        case 1:
          this.messageType = 2;
          return;
        case 2:
          this.messageType = 0;
          return;
        case 0:
          this.messageType = 1;
          return;
      } 
      throw new UnsupportedCallbackException(param1ConfirmationCallback, "Unrecognized message type: " + j);
    }
    
    void handleResult(int param1Int) {
      if (this.callback == null)
        return; 
      for (boolean bool = false; bool < this.translations.length; bool += true) {
        if (this.translations[bool] == param1Int) {
          param1Int = this.translations[bool + true];
          break;
        } 
      } 
      this.callback.setSelectedIndex(param1Int);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\callback\DialogCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */