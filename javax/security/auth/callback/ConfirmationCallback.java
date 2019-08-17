package javax.security.auth.callback;

import java.io.Serializable;

public class ConfirmationCallback implements Callback, Serializable {
  private static final long serialVersionUID = -9095656433782481624L;
  
  public static final int UNSPECIFIED_OPTION = -1;
  
  public static final int YES_NO_OPTION = 0;
  
  public static final int YES_NO_CANCEL_OPTION = 1;
  
  public static final int OK_CANCEL_OPTION = 2;
  
  public static final int YES = 0;
  
  public static final int NO = 1;
  
  public static final int CANCEL = 2;
  
  public static final int OK = 3;
  
  public static final int INFORMATION = 0;
  
  public static final int WARNING = 1;
  
  public static final int ERROR = 2;
  
  private String prompt;
  
  private int messageType;
  
  private int optionType = -1;
  
  private int defaultOption;
  
  private String[] options;
  
  private int selection;
  
  public ConfirmationCallback(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramInt1 > 2 || paramInt2 < 0 || paramInt2 > 2)
      throw new IllegalArgumentException(); 
    switch (paramInt2) {
      case 0:
        if (paramInt3 != 0 && paramInt3 != 1)
          throw new IllegalArgumentException(); 
        break;
      case 1:
        if (paramInt3 != 0 && paramInt3 != 1 && paramInt3 != 2)
          throw new IllegalArgumentException(); 
        break;
      case 2:
        if (paramInt3 != 3 && paramInt3 != 2)
          throw new IllegalArgumentException(); 
        break;
    } 
    this.messageType = paramInt1;
    this.optionType = paramInt2;
    this.defaultOption = paramInt3;
  }
  
  public ConfirmationCallback(int paramInt1, String[] paramArrayOfString, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > 2 || paramArrayOfString == null || paramArrayOfString.length == 0 || paramInt2 < 0 || paramInt2 >= paramArrayOfString.length)
      throw new IllegalArgumentException(); 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] == null || paramArrayOfString[b].length() == 0)
        throw new IllegalArgumentException(); 
    } 
    this.messageType = paramInt1;
    this.options = paramArrayOfString;
    this.defaultOption = paramInt2;
  }
  
  public ConfirmationCallback(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (paramString == null || paramString.length() == 0 || paramInt1 < 0 || paramInt1 > 2 || paramInt2 < 0 || paramInt2 > 2)
      throw new IllegalArgumentException(); 
    switch (paramInt2) {
      case 0:
        if (paramInt3 != 0 && paramInt3 != 1)
          throw new IllegalArgumentException(); 
        break;
      case 1:
        if (paramInt3 != 0 && paramInt3 != 1 && paramInt3 != 2)
          throw new IllegalArgumentException(); 
        break;
      case 2:
        if (paramInt3 != 3 && paramInt3 != 2)
          throw new IllegalArgumentException(); 
        break;
    } 
    this.prompt = paramString;
    this.messageType = paramInt1;
    this.optionType = paramInt2;
    this.defaultOption = paramInt3;
  }
  
  public ConfirmationCallback(String paramString, int paramInt1, String[] paramArrayOfString, int paramInt2) {
    if (paramString == null || paramString.length() == 0 || paramInt1 < 0 || paramInt1 > 2 || paramArrayOfString == null || paramArrayOfString.length == 0 || paramInt2 < 0 || paramInt2 >= paramArrayOfString.length)
      throw new IllegalArgumentException(); 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] == null || paramArrayOfString[b].length() == 0)
        throw new IllegalArgumentException(); 
    } 
    this.prompt = paramString;
    this.messageType = paramInt1;
    this.options = paramArrayOfString;
    this.defaultOption = paramInt2;
  }
  
  public String getPrompt() { return this.prompt; }
  
  public int getMessageType() { return this.messageType; }
  
  public int getOptionType() { return this.optionType; }
  
  public String[] getOptions() { return this.options; }
  
  public int getDefaultOption() { return this.defaultOption; }
  
  public void setSelectedIndex(int paramInt) { this.selection = paramInt; }
  
  public int getSelectedIndex() { return this.selection; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\ConfirmationCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */