package com.sun.jndi.dns;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

abstract class BaseNameClassPairEnumeration<T> extends Object implements NamingEnumeration<T> {
  protected Enumeration<NameNode> nodes;
  
  protected DnsContext ctx;
  
  BaseNameClassPairEnumeration(DnsContext paramDnsContext, Hashtable<String, NameNode> paramHashtable) {
    this.ctx = paramDnsContext;
    this.nodes = (paramHashtable != null) ? paramHashtable.elements() : null;
  }
  
  public final void close() {
    this.nodes = null;
    this.ctx = null;
  }
  
  public final boolean hasMore() {
    boolean bool = (this.nodes != null && this.nodes.hasMoreElements());
    if (!bool)
      close(); 
    return bool;
  }
  
  public final boolean hasMoreElements() { return hasMore(); }
  
  public abstract T next() throws NamingException;
  
  public final T nextElement() throws NamingException {
    try {
      return (T)next();
    } catch (NamingException namingException) {
      NoSuchElementException noSuchElementException = new NoSuchElementException();
      noSuchElementException.initCause(namingException);
      throw noSuchElementException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\BaseNameClassPairEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */