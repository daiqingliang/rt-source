package javax.security.auth.callback;

import java.io.Serializable;

public class PasswordCallback implements Callback, Serializable {
  private static final long serialVersionUID = 2267422647454909926L;
  
  private String prompt;
  
  private boolean echoOn;
  
  private char[] inputPassword;
  
  public PasswordCallback(String paramString, boolean paramBoolean) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException(); 
    this.prompt = paramString;
    this.echoOn = paramBoolean;
  }
  
  public String getPrompt() { return this.prompt; }
  
  public boolean isEchoOn() { return this.echoOn; }
  
  public void setPassword(char[] paramArrayOfChar) { this.inputPassword = (paramArrayOfChar == null) ? null : (char[])paramArrayOfChar.clone(); }
  
  public char[] getPassword() { return (this.inputPassword == null) ? null : (char[])this.inputPassword.clone(); }
  
  public void clearPassword() {
    if (this.inputPassword != null)
      for (byte b = 0; b < this.inputPassword.length; b++)
        this.inputPassword[b] = ' ';  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\PasswordCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */