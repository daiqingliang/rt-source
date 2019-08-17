package java.util.regex;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class PatternSyntaxException extends IllegalArgumentException {
  private static final long serialVersionUID = -3864639126226059218L;
  
  private final String desc;
  
  private final String pattern;
  
  private final int index;
  
  private static final String nl = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  
  public PatternSyntaxException(String paramString1, String paramString2, int paramInt) {
    this.desc = paramString1;
    this.pattern = paramString2;
    this.index = paramInt;
  }
  
  public int getIndex() { return this.index; }
  
  public String getDescription() { return this.desc; }
  
  public String getPattern() { return this.pattern; }
  
  public String getMessage() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.desc);
    if (this.index >= 0) {
      stringBuffer.append(" near index ");
      stringBuffer.append(this.index);
    } 
    stringBuffer.append(nl);
    stringBuffer.append(this.pattern);
    if (this.index >= 0 && this.pattern != null && this.index < this.pattern.length()) {
      stringBuffer.append(nl);
      for (byte b = 0; b < this.index; b++)
        stringBuffer.append(' '); 
      stringBuffer.append('^');
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\regex\PatternSyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */