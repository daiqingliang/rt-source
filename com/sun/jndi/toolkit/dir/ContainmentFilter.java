package com.sun.jndi.toolkit.dir;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class ContainmentFilter implements AttrFilter {
  private Attributes matchingAttrs;
  
  public ContainmentFilter(Attributes paramAttributes) { this.matchingAttrs = paramAttributes; }
  
  public boolean check(Attributes paramAttributes) throws NamingException { return (this.matchingAttrs == null || this.matchingAttrs.size() == 0 || contains(paramAttributes, this.matchingAttrs)); }
  
  public static boolean contains(Attributes paramAttributes1, Attributes paramAttributes2) throws NamingException {
    if (paramAttributes2 == null)
      return true; 
    NamingEnumeration namingEnumeration = paramAttributes2.getAll();
    while (namingEnumeration.hasMore()) {
      if (paramAttributes1 == null)
        return false; 
      Attribute attribute1 = (Attribute)namingEnumeration.next();
      Attribute attribute2 = paramAttributes1.get(attribute1.getID());
      if (attribute2 == null)
        return false; 
      if (attribute1.size() > 0) {
        NamingEnumeration namingEnumeration1 = attribute1.getAll();
        while (namingEnumeration1.hasMore()) {
          if (!attribute2.contains(namingEnumeration1.next()))
            return false; 
        } 
      } 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\dir\ContainmentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */