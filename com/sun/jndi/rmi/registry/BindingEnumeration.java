package com.sun.jndi.rmi.registry;

import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class BindingEnumeration extends Object implements NamingEnumeration<Binding> {
  private RegistryContext ctx;
  
  private final String[] names;
  
  private int nextName;
  
  BindingEnumeration(RegistryContext paramRegistryContext, String[] paramArrayOfString) {
    this.ctx = new RegistryContext(paramRegistryContext);
    this.names = paramArrayOfString;
    this.nextName = 0;
  }
  
  protected void finalize() { this.ctx.close(); }
  
  public boolean hasMore() {
    if (this.nextName >= this.names.length)
      this.ctx.close(); 
    return (this.nextName < this.names.length);
  }
  
  public Binding next() throws NamingException {
    if (!hasMore())
      throw new NoSuchElementException(); 
    String str1 = this.names[this.nextName++];
    Name name = (new CompositeName()).add(str1);
    Object object = this.ctx.lookup(name);
    String str2 = name.toString();
    Binding binding = new Binding(str2, object);
    binding.setNameInNamespace(str2);
    return binding;
  }
  
  public boolean hasMoreElements() { return hasMore(); }
  
  public Binding nextElement() throws NamingException {
    try {
      return next();
    } catch (NamingException namingException) {
      throw new NoSuchElementException("javax.naming.NamingException was thrown");
    } 
  }
  
  public void close() { finalize(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\rmi\registry\BindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */