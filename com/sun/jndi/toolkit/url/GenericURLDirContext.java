package com.sun.jndi.toolkit.url;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;
import javax.naming.spi.ResolveResult;

public abstract class GenericURLDirContext extends GenericURLContext implements DirContext {
  protected GenericURLDirContext(Hashtable<?, ?> paramHashtable) { super(paramHashtable); }
  
  protected DirContext getContinuationDirContext(Name paramName) throws NamingException {
    Object object = lookup(paramName.get(0));
    CannotProceedException cannotProceedException = new CannotProceedException();
    cannotProceedException.setResolvedObj(object);
    cannotProceedException.setEnvironment(this.myEnv);
    return DirectoryManager.getContinuationDirContext(cannotProceedException);
  }
  
  public Attributes getAttributes(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.getAttributes(resolveResult.getRemainingName());
    } finally {
      dirContext.close();
    } 
  }
  
  public Attributes getAttributes(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return getAttributes(paramName.get(0)); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.getAttributes(paramName.getSuffix(1));
    } finally {
      dirContext.close();
    } 
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.getAttributes(resolveResult.getRemainingName(), paramArrayOfString);
    } finally {
      dirContext.close();
    } 
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException {
    if (paramName.size() == 1)
      return getAttributes(paramName.get(0), paramArrayOfString); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.getAttributes(paramName.getSuffix(1), paramArrayOfString);
    } finally {
      dirContext.close();
    } 
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      dirContext.modifyAttributes(resolveResult.getRemainingName(), paramInt, paramAttributes);
    } finally {
      dirContext.close();
    } 
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException {
    if (paramName.size() == 1) {
      modifyAttributes(paramName.get(0), paramInt, paramAttributes);
    } else {
      dirContext = getContinuationDirContext(paramName);
      try {
        dirContext.modifyAttributes(paramName.getSuffix(1), paramInt, paramAttributes);
      } finally {
        dirContext.close();
      } 
    } 
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      dirContext.modifyAttributes(resolveResult.getRemainingName(), paramArrayOfModificationItem);
    } finally {
      dirContext.close();
    } 
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    if (paramName.size() == 1) {
      modifyAttributes(paramName.get(0), paramArrayOfModificationItem);
    } else {
      dirContext = getContinuationDirContext(paramName);
      try {
        dirContext.modifyAttributes(paramName.getSuffix(1), paramArrayOfModificationItem);
      } finally {
        dirContext.close();
      } 
    } 
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      dirContext.bind(resolveResult.getRemainingName(), paramObject, paramAttributes);
    } finally {
      dirContext.close();
    } 
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (paramName.size() == 1) {
      bind(paramName.get(0), paramObject, paramAttributes);
    } else {
      dirContext = getContinuationDirContext(paramName);
      try {
        dirContext.bind(paramName.getSuffix(1), paramObject, paramAttributes);
      } finally {
        dirContext.close();
      } 
    } 
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      dirContext.rebind(resolveResult.getRemainingName(), paramObject, paramAttributes);
    } finally {
      dirContext.close();
    } 
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (paramName.size() == 1) {
      rebind(paramName.get(0), paramObject, paramAttributes);
    } else {
      dirContext = getContinuationDirContext(paramName);
      try {
        dirContext.rebind(paramName.getSuffix(1), paramObject, paramAttributes);
      } finally {
        dirContext.close();
      } 
    } 
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.createSubcontext(resolveResult.getRemainingName(), paramAttributes);
    } finally {
      dirContext.close();
    } 
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    if (paramName.size() == 1)
      return createSubcontext(paramName.get(0), paramAttributes); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.createSubcontext(paramName.getSuffix(1), paramAttributes);
    } finally {
      dirContext.close();
    } 
  }
  
  public DirContext getSchema(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    DirContext dirContext = (DirContext)resolveResult.getResolvedObj();
    return dirContext.getSchema(resolveResult.getRemainingName());
  }
  
  public DirContext getSchema(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return getSchema(paramName.get(0)); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.getSchema(paramName.getSuffix(1));
    } finally {
      dirContext.close();
    } 
  }
  
  public DirContext getSchemaClassDefinition(String paramString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.getSchemaClassDefinition(resolveResult.getRemainingName());
    } finally {
      dirContext.close();
    } 
  }
  
  public DirContext getSchemaClassDefinition(Name paramName) throws NamingException {
    if (paramName.size() == 1)
      return getSchemaClassDefinition(paramName.get(0)); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.getSchemaClassDefinition(paramName.getSuffix(1));
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.search(resolveResult.getRemainingName(), paramAttributes);
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramAttributes); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.search(paramName.getSuffix(1), paramAttributes);
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.search(resolveResult.getRemainingName(), paramAttributes, paramArrayOfString);
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramAttributes, paramArrayOfString); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.search(paramName.getSuffix(1), paramAttributes, paramArrayOfString);
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString1, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.search(resolveResult.getRemainingName(), paramString2, paramSearchControls);
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramString, paramSearchControls); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.search(paramName.getSuffix(1), paramString, paramSearchControls);
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    ResolveResult resolveResult = getRootURLContext(paramString1, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.search(resolveResult.getRemainingName(), paramString2, paramArrayOfObject, paramSearchControls);
    } finally {
      dirContext.close();
    } 
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramString, paramArrayOfObject, paramSearchControls); 
    dirContext = getContinuationDirContext(paramName);
    try {
      return dirContext.search(paramName.getSuffix(1), paramString, paramArrayOfObject, paramSearchControls);
    } finally {
      dirContext.close();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolki\\url\GenericURLDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */