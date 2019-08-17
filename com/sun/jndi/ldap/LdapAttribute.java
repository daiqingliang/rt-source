package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

final class LdapAttribute extends BasicAttribute {
  static final long serialVersionUID = -4288716561020779584L;
  
  private DirContext baseCtx = null;
  
  private Name rdn = new CompositeName();
  
  private String baseCtxURL;
  
  private Hashtable<String, ? super String> baseCtxEnv;
  
  public Object clone() {
    LdapAttribute ldapAttribute = new LdapAttribute(this.attrID, this.baseCtx, this.rdn);
    ldapAttribute.values = (Vector)this.values.clone();
    return ldapAttribute;
  }
  
  public boolean add(Object paramObject) {
    this.values.addElement(paramObject);
    return true;
  }
  
  LdapAttribute(String paramString) { super(paramString); }
  
  private LdapAttribute(String paramString, DirContext paramDirContext, Name paramName) {
    super(paramString);
    this.baseCtx = paramDirContext;
    this.rdn = paramName;
  }
  
  void setParent(DirContext paramDirContext, Name paramName) {
    this.baseCtx = paramDirContext;
    this.rdn = paramName;
  }
  
  private DirContext getBaseCtx() throws NamingException {
    if (this.baseCtx == null) {
      if (this.baseCtxEnv == null)
        this.baseCtxEnv = new Hashtable(3); 
      this.baseCtxEnv.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      this.baseCtxEnv.put("java.naming.provider.url", this.baseCtxURL);
      this.baseCtx = new InitialDirContext(this.baseCtxEnv);
    } 
    return this.baseCtx;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    setBaseCtxInfo();
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void setBaseCtxInfo() {
    Hashtable hashtable1 = null;
    Hashtable hashtable2 = null;
    if (this.baseCtx != null) {
      hashtable1 = ((LdapCtx)this.baseCtx).envprops;
      this.baseCtxURL = ((LdapCtx)this.baseCtx).getURL();
    } 
    if (hashtable1 != null && hashtable1.size() > 0)
      for (String str : hashtable1.keySet()) {
        if (str.indexOf("security") != -1) {
          if (hashtable2 == null)
            hashtable2 = (Hashtable)hashtable1.clone(); 
          hashtable2.remove(str);
        } 
      }  
    this.baseCtxEnv = (hashtable2 == null) ? hashtable1 : hashtable2;
  }
  
  public DirContext getAttributeSyntaxDefinition() throws NamingException {
    DirContext dirContext1 = getBaseCtx().getSchema(this.rdn);
    DirContext dirContext2 = (DirContext)dirContext1.lookup("AttributeDefinition/" + getID());
    Attribute attribute = dirContext2.getAttributes("").get("SYNTAX");
    if (attribute == null || attribute.size() == 0)
      throw new NameNotFoundException(getID() + "does not have a syntax associated with it"); 
    String str = (String)attribute.get();
    return (DirContext)dirContext1.lookup("SyntaxDefinition/" + str);
  }
  
  public DirContext getAttributeDefinition() throws NamingException {
    DirContext dirContext = getBaseCtx().getSchema(this.rdn);
    return (DirContext)dirContext.lookup("AttributeDefinition/" + getID());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */