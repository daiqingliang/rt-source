package javax.naming.directory;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;

public class InitialDirContext extends InitialContext implements DirContext {
  protected InitialDirContext(boolean paramBoolean) throws NamingException { super(paramBoolean); }
  
  public InitialDirContext() throws NamingException {}
  
  public InitialDirContext(Hashtable<?, ?> paramHashtable) throws NamingException { super(paramHashtable); }
  
  private DirContext getURLOrDefaultInitDirCtx(String paramString) throws NamingException {
    Context context = getURLOrDefaultInitCtx(paramString);
    if (!(context instanceof DirContext)) {
      if (context == null)
        throw new NoInitialContextException(); 
      throw new NotContextException("Not an instance of DirContext");
    } 
    return (DirContext)context;
  }
  
  private DirContext getURLOrDefaultInitDirCtx(Name paramName) throws NamingException {
    Context context = getURLOrDefaultInitCtx(paramName);
    if (!(context instanceof DirContext)) {
      if (context == null)
        throw new NoInitialContextException(); 
      throw new NotContextException("Not an instance of DirContext");
    } 
    return (DirContext)context;
  }
  
  public Attributes getAttributes(String paramString) throws NamingException { return getAttributes(paramString, null); }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException { return getURLOrDefaultInitDirCtx(paramString).getAttributes(paramString, paramArrayOfString); }
  
  public Attributes getAttributes(Name paramName) throws NamingException { return getAttributes(paramName, null); }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).getAttributes(paramName, paramArrayOfString); }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException { getURLOrDefaultInitDirCtx(paramString).modifyAttributes(paramString, paramInt, paramAttributes); }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException { getURLOrDefaultInitDirCtx(paramName).modifyAttributes(paramName, paramInt, paramAttributes); }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException { getURLOrDefaultInitDirCtx(paramString).modifyAttributes(paramString, paramArrayOfModificationItem); }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException { getURLOrDefaultInitDirCtx(paramName).modifyAttributes(paramName, paramArrayOfModificationItem); }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { getURLOrDefaultInitDirCtx(paramString).bind(paramString, paramObject, paramAttributes); }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException { getURLOrDefaultInitDirCtx(paramName).bind(paramName, paramObject, paramAttributes); }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { getURLOrDefaultInitDirCtx(paramString).rebind(paramString, paramObject, paramAttributes); }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException { getURLOrDefaultInitDirCtx(paramName).rebind(paramName, paramObject, paramAttributes); }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException { return getURLOrDefaultInitDirCtx(paramString).createSubcontext(paramString, paramAttributes); }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).createSubcontext(paramName, paramAttributes); }
  
  public DirContext getSchema(String paramString) throws NamingException { return getURLOrDefaultInitDirCtx(paramString).getSchema(paramString); }
  
  public DirContext getSchema(Name paramName) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).getSchema(paramName); }
  
  public DirContext getSchemaClassDefinition(String paramString) throws NamingException { return getURLOrDefaultInitDirCtx(paramString).getSchemaClassDefinition(paramString); }
  
  public DirContext getSchemaClassDefinition(Name paramName) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).getSchemaClassDefinition(paramName); }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException { return getURLOrDefaultInitDirCtx(paramString).search(paramString, paramAttributes); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).search(paramName, paramAttributes); }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException { return getURLOrDefaultInitDirCtx(paramString).search(paramString, paramAttributes, paramArrayOfString); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).search(paramName, paramAttributes, paramArrayOfString); }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException { return getURLOrDefaultInitDirCtx(paramString1).search(paramString1, paramString2, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).search(paramName, paramString, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException { return getURLOrDefaultInitDirCtx(paramString1).search(paramString1, paramString2, paramArrayOfObject, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException { return getURLOrDefaultInitDirCtx(paramName).search(paramName, paramString, paramArrayOfObject, paramSearchControls); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\directory\InitialDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */