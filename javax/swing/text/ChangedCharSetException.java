package javax.swing.text;

import java.io.IOException;

public class ChangedCharSetException extends IOException {
  String charSetSpec;
  
  boolean charSetKey;
  
  public ChangedCharSetException(String paramString, boolean paramBoolean) {
    this.charSetSpec = paramString;
    this.charSetKey = paramBoolean;
  }
  
  public String getCharSetSpec() { return this.charSetSpec; }
  
  public boolean keyEqualsCharSet() { return this.charSetKey; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\ChangedCharSetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */