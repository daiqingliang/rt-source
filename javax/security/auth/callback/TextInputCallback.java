package javax.security.auth.callback;

import java.io.Serializable;

public class TextInputCallback implements Callback, Serializable {
  private static final long serialVersionUID = -8064222478852811804L;
  
  private String prompt;
  
  private String defaultText;
  
  private String inputText;
  
  public TextInputCallback(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException(); 
    this.prompt = paramString;
  }
  
  public TextInputCallback(String paramString1, String paramString2) {
    if (paramString1 == null || paramString1.length() == 0 || paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException(); 
    this.prompt = paramString1;
    this.defaultText = paramString2;
  }
  
  public String getPrompt() { return this.prompt; }
  
  public String getDefaultText() { return this.defaultText; }
  
  public void setText(String paramString) { this.inputText = paramString; }
  
  public String getText() { return this.inputText; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\TextInputCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */