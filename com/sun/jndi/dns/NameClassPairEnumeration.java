package com.sun.jndi.dns;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

final class NameClassPairEnumeration extends BaseNameClassPairEnumeration<NameClassPair> implements NamingEnumeration<NameClassPair> {
  NameClassPairEnumeration(DnsContext paramDnsContext, Hashtable<String, NameNode> paramHashtable) { super(paramDnsContext, paramHashtable); }
  
  public NameClassPair next() throws NamingException {
    if (!hasMore())
      throw new NoSuchElementException(); 
    NameNode nameNode = (NameNode)this.nodes.nextElement();
    String str1 = (nameNode.isZoneCut() || nameNode.getChildren() != null) ? "javax.naming.directory.DirContext" : "java.lang.Object";
    String str2 = nameNode.getLabel();
    Name name1 = (new DnsName()).add(str2);
    Name name2 = (new CompositeName()).add(name1.toString());
    NameClassPair nameClassPair = new NameClassPair(name2.toString(), str1);
    nameClassPair.setNameInNamespace(this.ctx.fullyQualify(name2).toString());
    return nameClassPair;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\NameClassPairEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */