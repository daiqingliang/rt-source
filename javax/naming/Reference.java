package javax.naming;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class Reference implements Cloneable, Serializable {
  protected String className;
  
  protected Vector<RefAddr> addrs = null;
  
  protected String classFactory = null;
  
  protected String classFactoryLocation = null;
  
  private static final long serialVersionUID = -1673475790065791735L;
  
  public Reference(String paramString) {
    this.className = paramString;
    this.addrs = new Vector();
  }
  
  public Reference(String paramString, RefAddr paramRefAddr) {
    this.className = paramString;
    this.addrs = new Vector();
    this.addrs.addElement(paramRefAddr);
  }
  
  public Reference(String paramString1, String paramString2, String paramString3) {
    this(paramString1);
    this.classFactory = paramString2;
    this.classFactoryLocation = paramString3;
  }
  
  public Reference(String paramString1, RefAddr paramRefAddr, String paramString2, String paramString3) {
    this(paramString1, paramRefAddr);
    this.classFactory = paramString2;
    this.classFactoryLocation = paramString3;
  }
  
  public String getClassName() { return this.className; }
  
  public String getFactoryClassName() { return this.classFactory; }
  
  public String getFactoryClassLocation() { return this.classFactoryLocation; }
  
  public RefAddr get(String paramString) {
    int i = this.addrs.size();
    for (byte b = 0; b < i; b++) {
      RefAddr refAddr = (RefAddr)this.addrs.elementAt(b);
      if (refAddr.getType().compareTo(paramString) == 0)
        return refAddr; 
    } 
    return null;
  }
  
  public RefAddr get(int paramInt) { return (RefAddr)this.addrs.elementAt(paramInt); }
  
  public Enumeration<RefAddr> getAll() { return this.addrs.elements(); }
  
  public int size() { return this.addrs.size(); }
  
  public void add(RefAddr paramRefAddr) { this.addrs.addElement(paramRefAddr); }
  
  public void add(int paramInt, RefAddr paramRefAddr) { this.addrs.insertElementAt(paramRefAddr, paramInt); }
  
  public Object remove(int paramInt) {
    Object object = this.addrs.elementAt(paramInt);
    this.addrs.removeElementAt(paramInt);
    return object;
  }
  
  public void clear() { this.addrs.setSize(0); }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof Reference) {
      Reference reference = (Reference)paramObject;
      if (reference.className.equals(this.className) && reference.size() == size()) {
        Enumeration enumeration1 = getAll();
        Enumeration enumeration2 = reference.getAll();
        while (enumeration1.hasMoreElements()) {
          if (!((RefAddr)enumeration1.nextElement()).equals(enumeration2.nextElement()))
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public int hashCode() {
    int i = this.className.hashCode();
    Enumeration enumeration = getAll();
    while (enumeration.hasMoreElements())
      i += ((RefAddr)enumeration.nextElement()).hashCode(); 
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("Reference Class Name: " + this.className + "\n");
    int i = this.addrs.size();
    for (byte b = 0; b < i; b++)
      stringBuffer.append(get(b).toString()); 
    return stringBuffer.toString();
  }
  
  public Object clone() {
    Reference reference = new Reference(this.className, this.classFactory, this.classFactoryLocation);
    Enumeration enumeration = getAll();
    reference.addrs = new Vector();
    while (enumeration.hasMoreElements())
      reference.addrs.addElement(enumeration.nextElement()); 
    return reference;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */