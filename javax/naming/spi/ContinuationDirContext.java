package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

class ContinuationDirContext extends ContinuationContext implements DirContext {
  ContinuationDirContext(CannotProceedException paramCannotProceedException, Hashtable<?, ?> paramHashtable) { super(paramCannotProceedException, paramHashtable); }
  
  protected DirContextNamePair getTargetContext(Name paramName) throws NamingException {
    if (this.cpe.getResolvedObj() == null)
      throw (NamingException)this.cpe.fillInStackTrace(); 
    Context context = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
    if (context == null)
      throw (NamingException)this.cpe.fillInStackTrace(); 
    if (context instanceof DirContext)
      return new DirContextNamePair((DirContext)context, paramName); 
    if (context instanceof Resolver) {
      Resolver resolver = (Resolver)context;
      ResolveResult resolveResult = resolver.resolveToClass(paramName, DirContext.class);
      DirContext dirContext = (DirContext)resolveResult.getResolvedObj();
      return new DirContextNamePair(dirContext, resolveResult.getRemainingName());
    } 
    Object object = context.lookup(paramName);
    if (object instanceof DirContext)
      return new DirContextNamePair((DirContext)object, new CompositeName()); 
    throw (NamingException)this.cpe.fillInStackTrace();
  }
  
  protected DirContextStringPair getTargetContext(String paramString) throws NamingException {
    if (this.cpe.getResolvedObj() == null)
      throw (NamingException)this.cpe.fillInStackTrace(); 
    Context context = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
    if (context instanceof DirContext)
      return new DirContextStringPair((DirContext)context, paramString); 
    if (context instanceof Resolver) {
      Resolver resolver = (Resolver)context;
      ResolveResult resolveResult = resolver.resolveToClass(paramString, DirContext.class);
      DirContext dirContext = (DirContext)resolveResult.getResolvedObj();
      Name name = resolveResult.getRemainingName();
      String str = (name != null) ? name.toString() : "";
      return new DirContextStringPair(dirContext, str);
    } 
    Object object = context.lookup(paramString);
    if (object instanceof DirContext)
      return new DirContextStringPair((DirContext)object, ""); 
    throw (NamingException)this.cpe.fillInStackTrace();
  }
  
  public Attributes getAttributes(String paramString) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    return dirContextStringPair.getDirContext().getAttributes(dirContextStringPair.getString());
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    return dirContextStringPair.getDirContext().getAttributes(dirContextStringPair.getString(), paramArrayOfString);
  }
  
  public Attributes getAttributes(Name paramName) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().getAttributes(dirContextNamePair.getName());
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().getAttributes(dirContextNamePair.getName(), paramArrayOfString);
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    dirContextNamePair.getDirContext().modifyAttributes(dirContextNamePair.getName(), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    dirContextStringPair.getDirContext().modifyAttributes(dirContextStringPair.getString(), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    dirContextNamePair.getDirContext().modifyAttributes(dirContextNamePair.getName(), paramArrayOfModificationItem);
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    dirContextStringPair.getDirContext().modifyAttributes(dirContextStringPair.getString(), paramArrayOfModificationItem);
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    dirContextNamePair.getDirContext().bind(dirContextNamePair.getName(), paramObject, paramAttributes);
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    dirContextStringPair.getDirContext().bind(dirContextStringPair.getString(), paramObject, paramAttributes);
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    dirContextNamePair.getDirContext().rebind(dirContextNamePair.getName(), paramObject, paramAttributes);
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    dirContextStringPair.getDirContext().rebind(dirContextStringPair.getString(), paramObject, paramAttributes);
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().createSubcontext(dirContextNamePair.getName(), paramAttributes);
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    return dirContextStringPair.getDirContext().createSubcontext(dirContextStringPair.getString(), paramAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().search(dirContextNamePair.getName(), paramAttributes, paramArrayOfString);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    return dirContextStringPair.getDirContext().search(dirContextStringPair.getString(), paramAttributes, paramArrayOfString);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().search(dirContextNamePair.getName(), paramAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    return dirContextStringPair.getDirContext().search(dirContextStringPair.getString(), paramAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().search(dirContextNamePair.getName(), paramString, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString1);
    return dirContextStringPair.getDirContext().search(dirContextStringPair.getString(), paramString2, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().search(dirContextNamePair.getName(), paramString, paramArrayOfObject, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString1);
    return dirContextStringPair.getDirContext().search(dirContextStringPair.getString(), paramString2, paramArrayOfObject, paramSearchControls);
  }
  
  public DirContext getSchema(String paramString) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    return dirContextStringPair.getDirContext().getSchema(dirContextStringPair.getString());
  }
  
  public DirContext getSchema(Name paramName) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().getSchema(dirContextNamePair.getName());
  }
  
  public DirContext getSchemaClassDefinition(String paramString) throws NamingException {
    DirContextStringPair dirContextStringPair = getTargetContext(paramString);
    return dirContextStringPair.getDirContext().getSchemaClassDefinition(dirContextStringPair.getString());
  }
  
  public DirContext getSchemaClassDefinition(Name paramName) throws NamingException {
    DirContextNamePair dirContextNamePair = getTargetContext(paramName);
    return dirContextNamePair.getDirContext().getSchemaClassDefinition(dirContextNamePair.getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\ContinuationDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */