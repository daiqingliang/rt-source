package com.sun.jndi.toolkit.ctx;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public abstract class PartialCompositeDirContext extends AtomicContext implements DirContext {
  protected abstract Attributes p_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract void p_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext p_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> p_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext p_getSchema(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext p_getSchemaClassDefinition(Name paramName, Continuation paramContinuation) throws NamingException;
  
  public Attributes getAttributes(String paramString) throws NamingException { return getAttributes(paramString, null); }
  
  public Attributes getAttributes(Name paramName) throws NamingException { return getAttributes(paramName, null); }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException { return getAttributes(new CompositeName(paramString), paramArrayOfString); }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException {
    Attributes attributes;
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (attributes = partialCompositeDirContext.p_getAttributes(name, paramArrayOfString, continuation); continuation.isContinue(); attributes = partialCompositeDirContext.p_getAttributes(name, paramArrayOfString, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      attributes = dirContext.getAttributes(cannotProceedException.getRemainingName(), paramArrayOfString);
    } 
    return attributes;
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException { modifyAttributes(new CompositeName(paramString), paramInt, paramAttributes); }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException {
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      partialCompositeDirContext.p_modifyAttributes(name, paramInt, paramAttributes, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
        partialCompositeDirContext.p_modifyAttributes(name, paramInt, paramAttributes, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      dirContext.modifyAttributes(cannotProceedException.getRemainingName(), paramInt, paramAttributes);
    } 
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException { modifyAttributes(new CompositeName(paramString), paramArrayOfModificationItem); }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      partialCompositeDirContext.p_modifyAttributes(name, paramArrayOfModificationItem, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
        partialCompositeDirContext.p_modifyAttributes(name, paramArrayOfModificationItem, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      dirContext.modifyAttributes(cannotProceedException.getRemainingName(), paramArrayOfModificationItem);
    } 
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { bind(new CompositeName(paramString), paramObject, paramAttributes); }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      partialCompositeDirContext.p_bind(name, paramObject, paramAttributes, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
        partialCompositeDirContext.p_bind(name, paramObject, paramAttributes, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      dirContext.bind(cannotProceedException.getRemainingName(), paramObject, paramAttributes);
    } 
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { rebind(new CompositeName(paramString), paramObject, paramAttributes); }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      partialCompositeDirContext.p_rebind(name, paramObject, paramAttributes, continuation);
      while (continuation.isContinue()) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
        partialCompositeDirContext.p_rebind(name, paramObject, paramAttributes, continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      dirContext.rebind(cannotProceedException.getRemainingName(), paramObject, paramAttributes);
    } 
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException { return createSubcontext(new CompositeName(paramString), paramAttributes); }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    DirContext dirContext;
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (dirContext = partialCompositeDirContext.p_createSubcontext(name, paramAttributes, continuation); continuation.isContinue(); dirContext = partialCompositeDirContext.p_createSubcontext(name, paramAttributes, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext1 = DirectoryManager.getContinuationDirContext(cannotProceedException);
      dirContext = dirContext1.createSubcontext(cannotProceedException.getRemainingName(), paramAttributes);
    } 
    return dirContext;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException { return search(paramString, paramAttributes, null); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException { return search(paramName, paramAttributes, null); }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException { return search(new CompositeName(paramString), paramAttributes, paramArrayOfString); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    NamingEnumeration namingEnumeration;
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (namingEnumeration = partialCompositeDirContext.p_search(name, paramAttributes, paramArrayOfString, continuation); continuation.isContinue(); namingEnumeration = partialCompositeDirContext.p_search(name, paramAttributes, paramArrayOfString, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      namingEnumeration = dirContext.search(cannotProceedException.getRemainingName(), paramAttributes, paramArrayOfString);
    } 
    return namingEnumeration;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException { return search(new CompositeName(paramString1), paramString2, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException {
    NamingEnumeration namingEnumeration;
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (namingEnumeration = partialCompositeDirContext.p_search(name, paramString, paramSearchControls, continuation); continuation.isContinue(); namingEnumeration = partialCompositeDirContext.p_search(name, paramString, paramSearchControls, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      namingEnumeration = dirContext.search(cannotProceedException.getRemainingName(), paramString, paramSearchControls);
    } 
    return namingEnumeration;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException { return search(new CompositeName(paramString1), paramString2, paramArrayOfObject, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    NamingEnumeration namingEnumeration;
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (namingEnumeration = partialCompositeDirContext.p_search(name, paramString, paramArrayOfObject, paramSearchControls, continuation); continuation.isContinue(); namingEnumeration = partialCompositeDirContext.p_search(name, paramString, paramArrayOfObject, paramSearchControls, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext = DirectoryManager.getContinuationDirContext(cannotProceedException);
      namingEnumeration = dirContext.search(cannotProceedException.getRemainingName(), paramString, paramArrayOfObject, paramSearchControls);
    } 
    return namingEnumeration;
  }
  
  public DirContext getSchema(String paramString) throws NamingException { return getSchema(new CompositeName(paramString)); }
  
  public DirContext getSchema(Name paramName) throws NamingException {
    DirContext dirContext;
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (dirContext = partialCompositeDirContext.p_getSchema(name, continuation); continuation.isContinue(); dirContext = partialCompositeDirContext.p_getSchema(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext1 = DirectoryManager.getContinuationDirContext(cannotProceedException);
      dirContext = dirContext1.getSchema(cannotProceedException.getRemainingName());
    } 
    return dirContext;
  }
  
  public DirContext getSchemaClassDefinition(String paramString) throws NamingException { return getSchemaClassDefinition(new CompositeName(paramString)); }
  
  public DirContext getSchemaClassDefinition(Name paramName) throws NamingException {
    DirContext dirContext;
    PartialCompositeDirContext partialCompositeDirContext = this;
    Hashtable hashtable = p_getEnvironment();
    Continuation continuation = new Continuation(paramName, hashtable);
    Name name = paramName;
    try {
      for (dirContext = partialCompositeDirContext.p_getSchemaClassDefinition(name, continuation); continuation.isContinue(); dirContext = partialCompositeDirContext.p_getSchemaClassDefinition(name, continuation)) {
        name = continuation.getRemainingName();
        partialCompositeDirContext = getPCDirContext(continuation);
      } 
    } catch (CannotProceedException cannotProceedException) {
      DirContext dirContext1 = DirectoryManager.getContinuationDirContext(cannotProceedException);
      dirContext = dirContext1.getSchemaClassDefinition(cannotProceedException.getRemainingName());
    } 
    return dirContext;
  }
  
  protected static PartialCompositeDirContext getPCDirContext(Continuation paramContinuation) throws NamingException {
    PartialCompositeContext partialCompositeContext = PartialCompositeContext.getPCContext(paramContinuation);
    if (!(partialCompositeContext instanceof PartialCompositeDirContext))
      throw paramContinuation.fillInException(new NotContextException("Resolved object is not a DirContext.")); 
    return (PartialCompositeDirContext)partialCompositeContext;
  }
  
  protected StringHeadTail c_parseComponent(String paramString, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected Object a_lookup(String paramString, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected Object a_lookupLink(String paramString, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected NamingEnumeration<NameClassPair> a_list(Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected NamingEnumeration<Binding> a_listBindings(Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected void a_bind(String paramString, Object paramObject, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected void a_rebind(String paramString, Object paramObject, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected void a_unbind(String paramString, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected void a_destroySubcontext(String paramString, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected Context a_createSubcontext(String paramString, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected void a_rename(String paramString, Name paramName, Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
  
  protected NameParser a_getNameParser(Continuation paramContinuation) throws NamingException {
    OperationNotSupportedException operationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(operationNotSupportedException);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\PartialCompositeDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */