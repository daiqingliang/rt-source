package sun.security.acl;

import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

final class AclEnumerator extends Object implements Enumeration<AclEntry> {
  Acl acl;
  
  Enumeration<AclEntry> u1;
  
  Enumeration<AclEntry> u2;
  
  Enumeration<AclEntry> g1;
  
  Enumeration<AclEntry> g2;
  
  AclEnumerator(Acl paramAcl, Hashtable<?, AclEntry> paramHashtable1, Hashtable<?, AclEntry> paramHashtable2, Hashtable<?, AclEntry> paramHashtable3, Hashtable<?, AclEntry> paramHashtable4) {
    this.acl = paramAcl;
    this.u1 = paramHashtable1.elements();
    this.u2 = paramHashtable3.elements();
    this.g1 = paramHashtable2.elements();
    this.g2 = paramHashtable4.elements();
  }
  
  public boolean hasMoreElements() { return (this.u1.hasMoreElements() || this.u2.hasMoreElements() || this.g1.hasMoreElements() || this.g2.hasMoreElements()); }
  
  public AclEntry nextElement() {
    synchronized (this.acl) {
      if (this.u1.hasMoreElements())
        return (AclEntry)this.u1.nextElement(); 
      if (this.u2.hasMoreElements())
        return (AclEntry)this.u2.nextElement(); 
      if (this.g1.hasMoreElements())
        return (AclEntry)this.g1.nextElement(); 
      if (this.g2.hasMoreElements())
        return (AclEntry)this.g2.nextElement(); 
    } 
    throw new NoSuchElementException("Acl Enumerator");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\AclEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */