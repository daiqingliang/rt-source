package com.sun.jndi.toolkit.ctx;

import java.util.Enumeration;
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
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;
import javax.naming.spi.Resolver;

public abstract class PartialCompositeContext implements Context, Resolver {
  protected static final int _PARTIAL = 1;
  
  protected static final int _COMPONENT = 2;
  
  protected static final int _ATOMIC = 3;
  
  protected int _contextType = 1;
  
  static final CompositeName _EMPTY_NAME = new CompositeName();
  
  static CompositeName _NNS_NAME;
  
  protected abstract ResolveResult p_resolveToClass(Name paramName, Class<?> paramClass, Continuation paramContinuation) throws NamingException;
  
  protected abstract Object p_lookup(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract Object p_lookupLink(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<NameClassPair> p_list(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<Binding> p_listBindings(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_bind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_rebind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_unbind(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_destroySubcontext(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract Context p_createSubcontext(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_rename(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException;
  
  protected abstract NameParser p_getNameParser(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected Hashtable<?, ?> p_getEnvironment() throws NamingException { return getEnvironment(); }
  
  public ResolveResult resolveToClass(String paramString, Class<? extends Context> paramClass) throws NamingException { return resolveToClass(new CompositeName(paramString), paramClass); }
  
  public ResolveResult resolveToClass(Name paramName, Class<? extends Context> paramClass) throws NamingException {
    ResolveResult resolveResult;
    PartialCompositeContext partialCompositeContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (resolveResult = partialCompositeContext.p_resolveToClass(name, paramClass, continuation); continuation.isContinue(); resolveResult = partialCompositeContext.p_resolveToClass(name, paramClass, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      if (!(context instanceof Resolver))
        throw cannotProceedException; 
      resolveResult = ((Resolver)context).resolveToClass(cannotProceedException.getRemainingName(), paramClass);
    } 
    return resolveResult;
  }
  
  public Object lookup(String paramString) throws NamingException { return lookup(new CompositeName(paramString)); }
  
  public Object lookup(Name paramName) throws NamingException {
    Object object;
    PartialCompositeContext partialCompositeContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (object = partialCompositeContext.p_lookup(name, continuation); continuation.isContinue(); object = partialCompositeContext.p_lookup(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      object = context.lookup(cannotProceedException.getRemainingName());
    } 
    return object;
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException { bind(new CompositeName(paramString), paramObject); }
  
  public void bind(Name paramName, Object paramObject) throws NamingException {
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      partialCompositeContext.p_bind(name, paramObject, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
        partialCompositeContext.p_bind(name, paramObject, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      context.bind(cannotProceedException.getRemainingName(), paramObject);
    } 
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException { rebind(new CompositeName(paramString), paramObject); }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException {
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      partialCompositeContext.p_rebind(name, paramObject, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
        partialCompositeContext.p_rebind(name, paramObject, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      context.rebind(cannotProceedException.getRemainingName(), paramObject);
    } 
  }
  
  public void unbind(String paramString) throws NamingException { unbind(new CompositeName(paramString)); }
  
  public void unbind(Name paramName) throws NamingException {
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      partialCompositeContext.p_unbind(name, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
        partialCompositeContext.p_unbind(name, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      context.unbind(cannotProceedException.getRemainingName());
    } 
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException { rename(new CompositeName(paramString1), new CompositeName(paramString2)); }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName1;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName1, hashtable);
    try {
      partialCompositeContext.p_rename(name, paramName2, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
        partialCompositeContext.p_rename(name, paramName2, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      if (cannotProceedException.getRemainingNewName() != null)
        paramName2 = cannotProceedException.getRemainingNewName(); 
      context.rename(cannotProceedException.getRemainingName(), paramName2);
    } 
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException { return list(new CompositeName(paramString)); }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException {
    NamingEnumeration namingEnumeration;
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      for (namingEnumeration = partialCompositeContext.p_list(name, continuation); continuation.isContinue(); namingEnumeration = partialCompositeContext.p_list(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      namingEnumeration = context.list(cannotProceedException.getRemainingName());
    } 
    return namingEnumeration;
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException { return listBindings(new CompositeName(paramString)); }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    NamingEnumeration namingEnumeration;
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      for (namingEnumeration = partialCompositeContext.p_listBindings(name, continuation); continuation.isContinue(); namingEnumeration = partialCompositeContext.p_listBindings(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      namingEnumeration = context.listBindings(cannotProceedException.getRemainingName());
    } 
    return namingEnumeration;
  }
  
  public void destroySubcontext(String paramString) throws NamingException { destroySubcontext(new CompositeName(paramString)); }
  
  public void destroySubcontext(Name paramName) throws NamingException {
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      partialCompositeContext.p_destroySubcontext(name, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
        partialCompositeContext.p_destroySubcontext(name, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      context.destroySubcontext(cannotProceedException.getRemainingName());
    } 
  }
  
  public Context createSubcontext(String paramString) throws NamingException { return createSubcontext(new CompositeName(paramString)); }
  
  public Context createSubcontext(Name paramName) throws NamingException {
    Context context;
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      for (context = partialCompositeContext.p_createSubcontext(name, continuation); continuation.isContinue(); context = partialCompositeContext.p_createSubcontext(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context1 = NamingManager.getContinuationContext(cannotProceedException);
      context = context1.createSubcontext(cannotProceedException.getRemainingName());
    } 
    return context;
  }
  
  public Object lookupLink(String paramString) throws NamingException { return lookupLink(new CompositeName(paramString)); }
  
  public Object lookupLink(Name paramName) throws NamingException {
    Object object;
    PartialCompositeContext partialCompositeContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (object = partialCompositeContext.p_lookupLink(name, continuation); continuation.isContinue(); object = partialCompositeContext.p_lookupLink(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      object = context.lookupLink(cannotProceedException.getRemainingName());
    } 
    return object;
  }
  
  public NameParser getNameParser(String paramString) throws NamingException { return getNameParser(new CompositeName(paramString)); }
  
  public NameParser getNameParser(Name paramName) throws NamingException {
    NameParser nameParser;
    PartialCompositeContext partialCompositeContext = this;
    Name name = paramName;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    try {
      for (nameParser = partialCompositeContext.p_getNameParser(name, continuation); continuation.isContinue(); nameParser = partialCompositeContext.p_getNameParser(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeContext = getPCContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      Context context = NamingManager.getContinuationContext(cannotProceedException);
      nameParser = context.getNameParser(cannotProceedException.getRemainingName());
    } 
    return nameParser;
  }
  
  public String composeName(String paramString1, String paramString2) throws NamingException {
    Name name = composeName(new CompositeName(paramString1), new CompositeName(paramString2));
    return name.toString();
  }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    Name name = (Name)paramName2.clone();
    if (paramName1 == null)
      return name; 
    name.addAll(paramName1);
    String str = (String)p_getEnvironment().get("java.naming.provider.compose.elideEmpty");
    if (str == null || !str.equalsIgnoreCase("true"))
      return name; 
    int i = paramName2.size();
    if (!allEmpty(paramName2) && !allEmpty(paramName1))
      if (name.get(i - 1).equals("")) {
        name.remove(i - 1);
      } else if (name.get(i).equals("")) {
        name.remove(i);
      }  
    return name;
  }
  
  protected static boolean allEmpty(Name paramName) {
    Enumeration enumeration = paramName.getAll();
    while (enumeration.hasMoreElements()) {
      if (!((String)enumeration.nextElement()).isEmpty())
        return false; 
    } 
    return true;
  }
  
  protected static PartialCompositeContext getPCContext(Continuation paramContinuation) throws NamingException {
    Object object = paramContinuation.getResolvedObj();
    Object object1 = null;
    if (object instanceof PartialCompositeContext)
      return (PartialCompositeContext)object; 
    throw paramContinuation.fillInException(new CannotProceedException());
  }
  
  static  {
    try {
      _NNS_NAME = new CompositeName("/");
    } catch (InvalidNameException invalidNameException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\PartialCompositeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */