package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public abstract class AtomicDirContext extends ComponentDirContext {
  protected abstract Attributes a_getAttributes(String paramString, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_modifyAttributes(String paramString, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_bind(String paramString, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_rebind(String paramString, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext a_createSubcontext(String paramString, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> a_search(Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> a_search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> a_search(String paramString1, String paramString2, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext a_getSchema(Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext a_getSchemaClassDefinition(Continuation paramContinuation) throws NamingException;
  
  protected Attributes a_getAttributes_nns(String paramString, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramString, paramContinuation);
    return null;
  }
  
  protected void a_modifyAttributes_nns(String paramString, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected void a_modifyAttributes_nns(String paramString, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected void a_bind_nns(String paramString, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected void a_rebind_nns(String paramString, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected DirContext a_createSubcontext_nns(String paramString, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramString, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> a_search_nns(Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> a_search_nns(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramString1, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> a_search_nns(String paramString1, String paramString2, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramString1, paramContinuation);
    return null;
  }
  
  protected DirContext a_getSchema_nns(Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramContinuation);
    return null;
  }
  
  protected DirContext a_getSchemaDefinition_nns(Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramContinuation);
    return null;
  }
  
  protected Attributes c_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context(paramName, paramContinuation) ? a_getAttributes(paramName.toString(), paramArrayOfString, paramContinuation) : null; }
  
  protected void c_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_modifyAttributes(paramName.toString(), paramInt, paramAttributes, paramContinuation); 
  }
  
  protected void c_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_modifyAttributes(paramName.toString(), paramArrayOfModificationItem, paramContinuation); 
  }
  
  protected void c_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_bind(paramName.toString(), paramObject, paramAttributes, paramContinuation); 
  }
  
  protected void c_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_rebind(paramName.toString(), paramObject, paramAttributes, paramContinuation); 
  }
  
  protected DirContext c_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context(paramName, paramContinuation) ? a_createSubcontext(paramName.toString(), paramAttributes, paramContinuation) : null; }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException { return resolve_to_context(paramName, paramContinuation) ? a_search(paramAttributes, paramArrayOfString, paramContinuation) : null; }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context(paramName, paramContinuation) ? a_search(paramName.toString(), paramString, paramSearchControls, paramContinuation) : null; }
  
  protected NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context(paramName, paramContinuation) ? a_search(paramName.toString(), paramString, paramArrayOfObject, paramSearchControls, paramContinuation) : null; }
  
  protected DirContext c_getSchema(Name paramName, Continuation paramContinuation) throws NamingException { return resolve_to_context(paramName, paramContinuation) ? a_getSchema(paramContinuation) : null; }
  
  protected DirContext c_getSchemaClassDefinition(Name paramName, Continuation paramContinuation) throws NamingException { return resolve_to_context(paramName, paramContinuation) ? a_getSchemaClassDefinition(paramContinuation) : null; }
  
  protected Attributes c_getAttributes_nns(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context_nns(paramName, paramContinuation) ? a_getAttributes_nns(paramName.toString(), paramArrayOfString, paramContinuation) : null; }
  
  protected void c_modifyAttributes_nns(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
      a_modifyAttributes_nns(paramName.toString(), paramInt, paramAttributes, paramContinuation); 
  }
  
  protected void c_modifyAttributes_nns(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
      a_modifyAttributes_nns(paramName.toString(), paramArrayOfModificationItem, paramContinuation); 
  }
  
  protected void c_bind_nns(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
      a_bind_nns(paramName.toString(), paramObject, paramAttributes, paramContinuation); 
  }
  
  protected void c_rebind_nns(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
      a_rebind_nns(paramName.toString(), paramObject, paramAttributes, paramContinuation); 
  }
  
  protected DirContext c_createSubcontext_nns(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context_nns(paramName, paramContinuation) ? a_createSubcontext_nns(paramName.toString(), paramAttributes, paramContinuation) : null; }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    resolve_to_nns_and_continue(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context_nns(paramName, paramContinuation) ? a_search_nns(paramName.toString(), paramString, paramSearchControls, paramContinuation) : null; }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context_nns(paramName, paramContinuation) ? a_search_nns(paramName.toString(), paramString, paramArrayOfObject, paramSearchControls, paramContinuation) : null; }
  
  protected DirContext c_getSchema_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    resolve_to_nns_and_continue(paramName, paramContinuation);
    return null;
  }
  
  protected DirContext c_getSchemaClassDefinition_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    resolve_to_nns_and_continue(paramName, paramContinuation);
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\AtomicDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */