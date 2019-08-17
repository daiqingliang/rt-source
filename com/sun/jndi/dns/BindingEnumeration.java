package com.sun.jndi.dns;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.DirectoryManager;

final class BindingEnumeration extends BaseNameClassPairEnumeration<Binding> implements NamingEnumeration<Binding> {
  BindingEnumeration(DnsContext paramDnsContext, Hashtable<String, NameNode> paramHashtable) { super(paramDnsContext, paramHashtable); }
  
  public Binding next() throws NamingException {
    if (!hasMore())
      throw new NoSuchElementException(); 
    NameNode nameNode = (NameNode)this.nodes.nextElement();
    String str1 = nameNode.getLabel();
    Name name1 = (new DnsName()).add(str1);
    String str2 = name1.toString();
    Name name2 = (new CompositeName()).add(str2);
    String str3 = name2.toString();
    DnsName dnsName = this.ctx.fullyQualify(name1);
    DnsContext dnsContext = new DnsContext(this.ctx, dnsName);
    try {
      Object object = DirectoryManager.getObjectInstance(dnsContext, name2, this.ctx, dnsContext.environment, null);
      Binding binding = new Binding(str3, object);
      binding.setNameInNamespace(this.ctx.fullyQualify(name2).toString());
      return binding;
    } catch (Exception exception) {
      NamingException namingException = new NamingException("Problem generating object using object factory");
      namingException.setRootCause(exception);
      throw namingException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\BindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */