package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class ContinuationContext implements Context, Resolver {
  protected CannotProceedException cpe;
  
  protected Hashtable<?, ?> env;
  
  protected Context contCtx = null;
  
  protected ContinuationContext(CannotProceedException paramCannotProceedException, Hashtable<?, ?> paramHashtable) {
    this.cpe = paramCannotProceedException;
    this.env = paramHashtable;
  }
  
  protected Context getTargetContext() throws NamingException {
    if (this.contCtx == null) {
      if (this.cpe.getResolvedObj() == null)
        throw (NamingException)this.cpe.fillInStackTrace(); 
      this.contCtx = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
      if (this.contCtx == null)
        throw (NamingException)this.cpe.fillInStackTrace(); 
    } 
    return this.contCtx;
  }
  
  public Object lookup(Name paramName) throws NamingException {
    Context context = getTargetContext();
    return context.lookup(paramName);
  }
  
  public Object lookup(String paramString) throws NamingException {
    Context context = getTargetContext();
    return context.lookup(paramString);
  }
  
  public void bind(Name paramName, Object paramObject) throws NamingException {
    Context context = getTargetContext();
    context.bind(paramName, paramObject);
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException {
    Context context = getTargetContext();
    context.bind(paramString, paramObject);
  }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException {
    Context context = getTargetContext();
    context.rebind(paramName, paramObject);
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException {
    Context context = getTargetContext();
    context.rebind(paramString, paramObject);
  }
  
  public void unbind(Name paramName) throws NamingException {
    Context context = getTargetContext();
    context.unbind(paramName);
  }
  
  public void unbind(String paramString) throws NamingException {
    Context context = getTargetContext();
    context.unbind(paramString);
  }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    Context context = getTargetContext();
    context.rename(paramName1, paramName2);
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException {
    Context context = getTargetContext();
    context.rename(paramString1, paramString2);
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException {
    Context context = getTargetContext();
    return context.list(paramName);
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException {
    Context context = getTargetContext();
    return context.list(paramString);
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    Context context = getTargetContext();
    return context.listBindings(paramName);
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException {
    Context context = getTargetContext();
    return context.listBindings(paramString);
  }
  
  public void destroySubcontext(Name paramName) throws NamingException {
    Context context = getTargetContext();
    context.destroySubcontext(paramName);
  }
  
  public void destroySubcontext(String paramString) throws NamingException {
    Context context = getTargetContext();
    context.destroySubcontext(paramString);
  }
  
  public Context createSubcontext(Name paramName) throws NamingException {
    Context context = getTargetContext();
    return context.createSubcontext(paramName);
  }
  
  public Context createSubcontext(String paramString) throws NamingException {
    Context context = getTargetContext();
    return context.createSubcontext(paramString);
  }
  
  public Object lookupLink(Name paramName) throws NamingException {
    Context context = getTargetContext();
    return context.lookupLink(paramName);
  }
  
  public Object lookupLink(String paramString) throws NamingException {
    Context context = getTargetContext();
    return context.lookupLink(paramString);
  }
  
  public NameParser getNameParser(Name paramName) throws NamingException {
    Context context = getTargetContext();
    return context.getNameParser(paramName);
  }
  
  public NameParser getNameParser(String paramString) throws NamingException {
    Context context = getTargetContext();
    return context.getNameParser(paramString);
  }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    Context context = getTargetContext();
    return context.composeName(paramName1, paramName2);
  }
  
  public String composeName(String paramString1, String paramString2) throws NamingException {
    Context context = getTargetContext();
    return context.composeName(paramString1, paramString2);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    Context context = getTargetContext();
    return context.addToEnvironment(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString) throws NamingException {
    Context context = getTargetContext();
    return context.removeFromEnvironment(paramString);
  }
  
  public Hashtable<?, ?> getEnvironment() throws NamingException {
    Context context = getTargetContext();
    return context.getEnvironment();
  }
  
  public String getNameInNamespace() throws NamingException {
    Context context = getTargetContext();
    return context.getNameInNamespace();
  }
  
  public ResolveResult resolveToClass(Name paramName, Class<? extends Context> paramClass) throws NamingException {
    if (this.cpe.getResolvedObj() == null)
      throw (NamingException)this.cpe.fillInStackTrace(); 
    Resolver resolver = NamingManager.getResolver(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
    if (resolver == null)
      throw (NamingException)this.cpe.fillInStackTrace(); 
    return resolver.resolveToClass(paramName, paramClass);
  }
  
  public ResolveResult resolveToClass(String paramString, Class<? extends Context> paramClass) throws NamingException {
    if (this.cpe.getResolvedObj() == null)
      throw (NamingException)this.cpe.fillInStackTrace(); 
    Resolver resolver = NamingManager.getResolver(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
    if (resolver == null)
      throw (NamingException)this.cpe.fillInStackTrace(); 
    return resolver.resolveToClass(paramString, paramClass);
  }
  
  public void close() throws NamingException {
    this.cpe = null;
    this.env = null;
    if (this.contCtx != null) {
      this.contCtx.close();
      this.contCtx = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\ContinuationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */