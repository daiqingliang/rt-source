package javax.naming.directory;

import javax.naming.NamingException;

public class AttributeModificationException extends NamingException {
  private ModificationItem[] unexecs = null;
  
  private static final long serialVersionUID = 8060676069678710186L;
  
  public AttributeModificationException(String paramString) { super(paramString); }
  
  public AttributeModificationException() {}
  
  public void setUnexecutedModifications(ModificationItem[] paramArrayOfModificationItem) { this.unexecs = paramArrayOfModificationItem; }
  
  public ModificationItem[] getUnexecutedModifications() { return this.unexecs; }
  
  public String toString() {
    String str = super.toString();
    if (this.unexecs != null)
      str = str + "First unexecuted modification: " + this.unexecs[0].toString(); 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\directory\AttributeModificationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */