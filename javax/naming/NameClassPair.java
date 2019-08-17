package javax.naming;

import java.io.Serializable;

public class NameClassPair implements Serializable {
  private String name;
  
  private String className;
  
  private String fullName = null;
  
  private boolean isRel = true;
  
  private static final long serialVersionUID = 5620776610160863339L;
  
  public NameClassPair(String paramString1, String paramString2) {
    this.name = paramString1;
    this.className = paramString2;
  }
  
  public NameClassPair(String paramString1, String paramString2, boolean paramBoolean) {
    this.name = paramString1;
    this.className = paramString2;
    this.isRel = paramBoolean;
  }
  
  public String getClassName() { return this.className; }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public void setClassName(String paramString) { this.className = paramString; }
  
  public boolean isRelative() { return this.isRel; }
  
  public void setRelative(boolean paramBoolean) { this.isRel = paramBoolean; }
  
  public String getNameInNamespace() {
    if (this.fullName == null)
      throw new UnsupportedOperationException(); 
    return this.fullName;
  }
  
  public void setNameInNamespace(String paramString) { this.fullName = paramString; }
  
  public String toString() { return (isRelative() ? "" : "(not relative)") + getName() + ": " + getClassName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\NameClassPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */