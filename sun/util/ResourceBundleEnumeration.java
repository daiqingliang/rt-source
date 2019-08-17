package sun.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class ResourceBundleEnumeration extends Object implements Enumeration<String> {
  Set<String> set;
  
  Iterator<String> iterator;
  
  Enumeration<String> enumeration;
  
  String next = null;
  
  public ResourceBundleEnumeration(Set<String> paramSet, Enumeration<String> paramEnumeration) {
    this.set = paramSet;
    this.iterator = paramSet.iterator();
    this.enumeration = paramEnumeration;
  }
  
  public boolean hasMoreElements() {
    if (this.next == null)
      if (this.iterator.hasNext()) {
        this.next = (String)this.iterator.next();
      } else if (this.enumeration != null) {
        while (this.next == null && this.enumeration.hasMoreElements()) {
          this.next = (String)this.enumeration.nextElement();
          if (this.set.contains(this.next))
            this.next = null; 
        } 
      }  
    return (this.next != null);
  }
  
  public String nextElement() {
    if (hasMoreElements()) {
      String str = this.next;
      this.next = null;
      return str;
    } 
    throw new NoSuchElementException();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\ResourceBundleEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */