package javax.naming;

import java.io.Serializable;

public abstract class RefAddr implements Serializable {
  protected String addrType;
  
  private static final long serialVersionUID = -1468165120479154358L;
  
  protected RefAddr(String paramString) { this.addrType = paramString; }
  
  public String getType() { return this.addrType; }
  
  public abstract Object getContent();
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof RefAddr) {
      RefAddr refAddr = (RefAddr)paramObject;
      if (this.addrType.compareTo(refAddr.addrType) == 0) {
        Object object1 = getContent();
        Object object2 = refAddr.getContent();
        if (object1 == object2)
          return true; 
        if (object1 != null)
          return object1.equals(object2); 
      } 
    } 
    return false;
  }
  
  public int hashCode() { return (getContent() == null) ? this.addrType.hashCode() : (this.addrType.hashCode() + getContent().hashCode()); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("Type: " + this.addrType + "\n");
    stringBuffer.append("Content: " + getContent() + "\n");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\RefAddr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */