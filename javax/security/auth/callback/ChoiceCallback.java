package javax.security.auth.callback;

import java.io.Serializable;

public class ChoiceCallback implements Callback, Serializable {
  private static final long serialVersionUID = -3975664071579892167L;
  
  private String prompt;
  
  private String[] choices;
  
  private int defaultChoice;
  
  private boolean multipleSelectionsAllowed;
  
  private int[] selections;
  
  public ChoiceCallback(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean) {
    if (paramString == null || paramString.length() == 0 || paramArrayOfString == null || paramArrayOfString.length == 0 || paramInt < 0 || paramInt >= paramArrayOfString.length)
      throw new IllegalArgumentException(); 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] == null || paramArrayOfString[b].length() == 0)
        throw new IllegalArgumentException(); 
    } 
    this.prompt = paramString;
    this.choices = paramArrayOfString;
    this.defaultChoice = paramInt;
    this.multipleSelectionsAllowed = paramBoolean;
  }
  
  public String getPrompt() { return this.prompt; }
  
  public String[] getChoices() { return this.choices; }
  
  public int getDefaultChoice() { return this.defaultChoice; }
  
  public boolean allowMultipleSelections() { return this.multipleSelectionsAllowed; }
  
  public void setSelectedIndex(int paramInt) {
    this.selections = new int[1];
    this.selections[0] = paramInt;
  }
  
  public void setSelectedIndexes(int[] paramArrayOfInt) {
    if (!this.multipleSelectionsAllowed)
      throw new UnsupportedOperationException(); 
    this.selections = paramArrayOfInt;
  }
  
  public int[] getSelectedIndexes() { return this.selections; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\ChoiceCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */