package javax.naming;

import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.spi.NamingManager;

public class InitialContext implements Context {
  protected Hashtable<Object, Object> myProps = null;
  
  protected Context defaultInitCtx = null;
  
  protected boolean gotDefault = false;
  
  protected InitialContext(boolean paramBoolean) throws NamingException {
    if (!paramBoolean)
      init(null); 
  }
  
  public InitialContext() throws NamingException { init(null); }
  
  public InitialContext(Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramHashtable != null)
      paramHashtable = (Hashtable)paramHashtable.clone(); 
    init(paramHashtable);
  }
  
  protected void init(Hashtable<?, ?> paramHashtable) throws NamingException {
    this.myProps = ResourceManager.getInitialEnvironment(paramHashtable);
    if (this.myProps.get("java.naming.factory.initial") != null)
      getDefaultInitCtx(); 
  }
  
  public static <T> T doLookup(Name paramName) throws NamingException { return (T)(new InitialContext()).lookup(paramName); }
  
  public static <T> T doLookup(String paramString) throws NamingException { return (T)(new InitialContext()).lookup(paramString); }
  
  private static String getURLScheme(String paramString) {
    int i = paramString.indexOf(':');
    int j = paramString.indexOf('/');
    return (i > 0 && (j == -1 || i < j)) ? paramString.substring(0, i) : null;
  }
  
  protected Context getDefaultInitCtx() throws NamingException {
    if (!this.gotDefault) {
      this.defaultInitCtx = NamingManager.getInitialContext(this.myProps);
      this.gotDefault = true;
    } 
    if (this.defaultInitCtx == null)
      throw new NoInitialContextException(); 
    return this.defaultInitCtx;
  }
  
  protected Context getURLOrDefaultInitCtx(String paramString) throws NamingException {
    if (NamingManager.hasInitialContextFactoryBuilder())
      return getDefaultInitCtx(); 
    String str = getURLScheme(paramString);
    if (str != null) {
      Context context = NamingManager.getURLContext(str, this.myProps);
      if (context != null)
        return context; 
    } 
    return getDefaultInitCtx();
  }
  
  protected Context getURLOrDefaultInitCtx(Name paramName) throws NamingException {
    if (NamingManager.hasInitialContextFactoryBuilder())
      return getDefaultInitCtx(); 
    if (paramName.size() > 0) {
      String str1 = paramName.get(0);
      String str2 = getURLScheme(str1);
      if (str2 != null) {
        Context context = NamingManager.getURLContext(str2, this.myProps);
        if (context != null)
          return context; 
      } 
    } 
    return getDefaultInitCtx();
  }
  
  public Object lookup(String paramString) throws NamingException { return getURLOrDefaultInitCtx(paramString).lookup(paramString); }
  
  public Object lookup(Name paramName) throws NamingException { return getURLOrDefaultInitCtx(paramName).lookup(paramName); }
  
  public void bind(String paramString, Object paramObject) throws NamingException { getURLOrDefaultInitCtx(paramString).bind(paramString, paramObject); }
  
  public void bind(Name paramName, Object paramObject) throws NamingException { getURLOrDefaultInitCtx(paramName).bind(paramName, paramObject); }
  
  public void rebind(String paramString, Object paramObject) throws NamingException { getURLOrDefaultInitCtx(paramString).rebind(paramString, paramObject); }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException { getURLOrDefaultInitCtx(paramName).rebind(paramName, paramObject); }
  
  public void unbind(String paramString) throws NamingException { getURLOrDefaultInitCtx(paramString).unbind(paramString); }
  
  public void unbind(Name paramName) throws NamingException { getURLOrDefaultInitCtx(paramName).unbind(paramName); }
  
  public void rename(String paramString1, String paramString2) throws NamingException { getURLOrDefaultInitCtx(paramString1).rename(paramString1, paramString2); }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException { getURLOrDefaultInitCtx(paramName1).rename(paramName1, paramName2); }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException { return getURLOrDefaultInitCtx(paramString).list(paramString); }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException { return getURLOrDefaultInitCtx(paramName).list(paramName); }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException { return getURLOrDefaultInitCtx(paramString).listBindings(paramString); }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException { return getURLOrDefaultInitCtx(paramName).listBindings(paramName); }
  
  public void destroySubcontext(String paramString) throws NamingException { getURLOrDefaultInitCtx(paramString).destroySubcontext(paramString); }
  
  public void destroySubcontext(Name paramName) throws NamingException { getURLOrDefaultInitCtx(paramName).destroySubcontext(paramName); }
  
  public Context createSubcontext(String paramString) throws NamingException { return getURLOrDefaultInitCtx(paramString).createSubcontext(paramString); }
  
  public Context createSubcontext(Name paramName) throws NamingException { return getURLOrDefaultInitCtx(paramName).createSubcontext(paramName); }
  
  public Object lookupLink(String paramString) throws NamingException { return getURLOrDefaultInitCtx(paramString).lookupLink(paramString); }
  
  public Object lookupLink(Name paramName) throws NamingException { return getURLOrDefaultInitCtx(paramName).lookupLink(paramName); }
  
  public NameParser getNameParser(String paramString) throws NamingException { return getURLOrDefaultInitCtx(paramString).getNameParser(paramString); }
  
  public NameParser getNameParser(Name paramName) throws NamingException { return getURLOrDefaultInitCtx(paramName).getNameParser(paramName); }
  
  public String composeName(String paramString1, String paramString2) throws NamingException { return paramString1; }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException { return (Name)paramName1.clone(); }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    this.myProps.put(paramString, paramObject);
    return getDefaultInitCtx().addToEnvironment(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString) throws NamingException {
    this.myProps.remove(paramString);
    return getDefaultInitCtx().removeFromEnvironment(paramString);
  }
  
  public Hashtable<?, ?> getEnvironment() throws NamingException { return getDefaultInitCtx().getEnvironment(); }
  
  public void close() throws NamingException {
    this.myProps = null;
    if (this.defaultInitCtx != null) {
      this.defaultInitCtx.close();
      this.defaultInitCtx = null;
    } 
    this.gotDefault = false;
  }
  
  public String getNameInNamespace() throws NamingException { return getDefaultInitCtx().getNameInNamespace(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\InitialContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */