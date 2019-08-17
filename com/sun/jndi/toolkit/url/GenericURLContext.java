package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;

public abstract class GenericURLContext implements Context {
  protected Hashtable<String, Object> myEnv = null;
  
  public GenericURLContext(Hashtable<?, ?> paramHashtable) { this.myEnv = (Hashtable)((paramHashtable == null) ? null : paramHashtable.clone()); }
  
  public void close() throws NamingException { this.myEnv = null; }
  
  public String getNameInNamespace() throws NamingException { return ""; }
  
  protected abstract ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException;
  
  protected Name getURLSuffix(String paramString1, String paramString2) throws NamingException {
    String str = paramString2.substring(paramString1.length());
    if (str.length() == 0)
      return new CompositeName(); 
    if (str.charAt(0) == '/')
      str = str.substring(1); 
    try {
      return (new CompositeName()).add(UrlUtil.decode(str));
    } catch (MalformedURLException malformedURLException) {
      throw new InvalidNameException(malformedURLException.getMessage());
    } 
  }
  
  protected String getURLPrefix(String paramString) throws NamingException {
    int i = paramString.indexOf(":");
    if (i < 0)
      throw new OperationNotSupportedException("Invalid URL: " + paramString); 
    if (paramString.startsWith("//", ++i)) {
      i += 2;
      int j = paramString.indexOf("/", i);
      if (j >= 0) {
        i = j;
      } else {
        i = paramString.length();
      } 
    } 
    return paramString.substring(0, i);
  }
  
  protected boolean urlEquals(String paramString1, String paramString2) { return paramString1.equals(paramString2); }
  
  protected Context getContinuationContext(Name paramName) throws NamingException {
    Object object = lookup(paramName.get(0));
    CannotProceedException cannotProceedException = new CannotProceedException();
    cannotProceedException.setResolvedObj(object);
    cannotProceedException.setEnvironment(this.myEnv);
    return NamingManager.getContinuationContext(cannotProceedException);
  }
  
  public Object lookup(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      return context.lookup(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public Object lookup(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return lookup(paramName.get(0)); 
    context = getContinuationContext(paramName);
    try {
      return context.lookup(paramName.getSuffix(1));
    } finally {
      context.close();
    } 
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      context.bind(resolveResult.getRemainingName(), paramObject);
    } finally {
      context.close();
    } 
  }
  
  public void bind(Name paramName, Object paramObject) throws NamingException {
    if (paramName.size() == 1) {
      bind(paramName.get(0), paramObject);
    } else {
      context = getContinuationContext(paramName);
      try {
        context.bind(paramName.getSuffix(1), paramObject);
      } finally {
        context.close();
      } 
    } 
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      context.rebind(resolveResult.getRemainingName(), paramObject);
    } finally {
      context.close();
    } 
  }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException {
    if (paramName.size() == 1) {
      rebind(paramName.get(0), paramObject);
    } else {
      context = getContinuationContext(paramName);
      try {
        context.rebind(paramName.getSuffix(1), paramObject);
      } finally {
        context.close();
      } 
    } 
  }
  
  public void unbind(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      context.unbind(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public void unbind(Name paramName) throws NamingException {
    if (paramName.size() == 1) {
      unbind(paramName.get(0));
    } else {
      context = getContinuationContext(paramName);
      try {
        context.unbind(paramName.getSuffix(1));
      } finally {
        context.close();
      } 
    } 
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException {
    String str1 = getURLPrefix(paramString1);
    String str2 = getURLPrefix(paramString2);
    if (!urlEquals(str1, str2))
      throw new OperationNotSupportedException("Renaming using different URL prefixes not supported : " + paramString1 + " " + paramString2); 
    ResolveResult resolveResult = getRootURLContext(paramString1, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      context.rename(resolveResult.getRemainingName(), getURLSuffix(str2, paramString2));
    } finally {
      context.close();
    } 
  }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    if (paramName1.size() == 1) {
      if (paramName2.size() != 1)
        throw new OperationNotSupportedException("Renaming to a Name with more components not supported: " + paramName2); 
      rename(paramName1.get(0), paramName2.get(0));
    } else {
      if (!urlEquals(paramName1.get(0), paramName2.get(0)))
        throw new OperationNotSupportedException("Renaming using different URLs as first components not supported: " + paramName1 + " " + paramName2); 
      context = getContinuationContext(paramName1);
      try {
        context.rename(paramName1.getSuffix(1), paramName2.getSuffix(1));
      } finally {
        context.close();
      } 
    } 
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      return context.list(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return list(paramName.get(0)); 
    context = getContinuationContext(paramName);
    try {
      return context.list(paramName.getSuffix(1));
    } finally {
      context.close();
    } 
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      return context.listBindings(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return listBindings(paramName.get(0)); 
    context = getContinuationContext(paramName);
    try {
      return context.listBindings(paramName.getSuffix(1));
    } finally {
      context.close();
    } 
  }
  
  public void destroySubcontext(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      context.destroySubcontext(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public void destroySubcontext(Name paramName) throws NamingException {
    if (paramName.size() == 1) {
      destroySubcontext(paramName.get(0));
    } else {
      context = getContinuationContext(paramName);
      try {
        context.destroySubcontext(paramName.getSuffix(1));
      } finally {
        context.close();
      } 
    } 
  }
  
  public Context createSubcontext(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      return context.createSubcontext(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public Context createSubcontext(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return createSubcontext(paramName.get(0)); 
    context = getContinuationContext(paramName);
    try {
      return context.createSubcontext(paramName.getSuffix(1));
    } finally {
      context.close();
    } 
  }
  
  public Object lookupLink(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      return context.lookupLink(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public Object lookupLink(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return lookupLink(paramName.get(0)); 
    context = getContinuationContext(paramName);
    try {
      return context.lookupLink(paramName.getSuffix(1));
    } finally {
      context.close();
    } 
  }
  
  public NameParser getNameParser(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    context = (Context)resolveResult.getResolvedObj();
    try {
      return context.getNameParser(resolveResult.getRemainingName());
    } finally {
      context.close();
    } 
  }
  
  public NameParser getNameParser(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return getNameParser(paramName.get(0)); 
    context = getContinuationContext(paramName);
    try {
      return context.getNameParser(paramName.getSuffix(1));
    } finally {
      context.close();
    } 
  }
  
  public String composeName(String paramString1, String paramString2) throws NamingException { return paramString2.equals("") ? paramString1 : (paramString1.equals("") ? paramString2 : (paramString2 + "/" + paramString1)); }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    Name name = (Name)paramName2.clone();
    name.addAll(paramName1);
    return name;
  }
  
  public Object removeFromEnvironment(String paramString) throws NamingException { return (this.myEnv == null) ? null : this.myEnv.remove(paramString); }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    if (this.myEnv == null)
      this.myEnv = new Hashtable(11, 0.75F); 
    return this.myEnv.put(paramString, paramObject);
  }
  
  public Hashtable<String, Object> getEnvironment() throws NamingException { return (this.myEnv == null) ? new Hashtable(5, 0.75F) : (Hashtable)this.myEnv.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolki\\url\GenericURLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */