package javax.security.auth.callback;

import java.io.Serializable;

public class NameCallback implements Callback, Serializable {
  private static final long serialVersionUID = 3770938795909392253L;
  
  private String prompt;
  
  private String defaultName;
  
  private String inputName;
  
  public NameCallback(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException(); 
    this.prompt = paramString;
  }
  
  public NameCallback(String paramString1, String paramString2) {
    if (paramString1 == null || paramString1.length() == 0 || paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException(); 
    this.prompt = paramString1;
    this.defaultName = paramString2;
  }
  
  public String getPrompt() { return this.prompt; }
  
  public String getDefaultName() { return this.defaultName; }
  
  public void setName(String paramString) { this.inputName = paramString; }
  
  public String getName() { return this.inputName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\NameCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */