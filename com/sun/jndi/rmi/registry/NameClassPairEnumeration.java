package com.sun.jndi.rmi.registry;

import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class NameClassPairEnumeration extends Object implements NamingEnumeration<NameClassPair> {
  private final String[] names;
  
  private int nextName;
  
  NameClassPairEnumeration(String[] paramArrayOfString) {
    this.names = paramArrayOfString;
    this.nextName = 0;
  }
  
  public boolean hasMore() { return (this.nextName < this.names.length); }
  
  public NameClassPair next() throws NamingException {
    if (!hasMore())
      throw new NoSuchElementException(); 
    String str = this.names[this.nextName++];
    Name name = (new CompositeName()).add(str);
    NameClassPair nameClassPair = new NameClassPair(name.toString(), "java.lang.Object");
    nameClassPair.setNameInNamespace(str);
    return nameClassPair;
  }
  
  public boolean hasMoreElements() { return hasMore(); }
  
  public NameClassPair nextElement() throws NamingException {
    try {
      return next();
    } catch (NamingException namingException) {
      throw new NoSuchElementException("javax.naming.NamingException was thrown");
    } 
  }
  
  public void close() { this.nextName = this.names.length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\rmi\registry\NameClassPairEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */