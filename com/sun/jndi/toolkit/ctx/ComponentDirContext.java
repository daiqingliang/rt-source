package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public abstract class ComponentDirContext extends PartialCompositeDirContext {
  protected abstract Attributes c_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext c_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext c_getSchema(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract DirContext c_getSchemaClassDefinition(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected Attributes c_getAttributes_nns(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_modifyAttributes_nns(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected void c_modifyAttributes_nns(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected void c_bind_nns(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected void c_rebind_nns(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected DirContext c_createSubcontext_nns(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected DirContext c_getSchema_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected DirContext c_getSchemaClassDefinition_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected Attributes p_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    Attributes attributes = null;
    switch (headTail.getStatus()) {
      case 3:
        attributes = c_getAttributes_nns(headTail.getHead(), paramArrayOfString, paramContinuation);
        break;
      case 2:
        attributes = c_getAttributes(headTail.getHead(), paramArrayOfString, paramContinuation);
        break;
    } 
    return attributes;
  }
  
  protected void p_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_modifyAttributes_nns(headTail.getHead(), paramInt, paramAttributes, paramContinuation);
        break;
      case 2:
        c_modifyAttributes(headTail.getHead(), paramInt, paramAttributes, paramContinuation);
        break;
    } 
  }
  
  protected void p_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_modifyAttributes_nns(headTail.getHead(), paramArrayOfModificationItem, paramContinuation);
        break;
      case 2:
        c_modifyAttributes(headTail.getHead(), paramArrayOfModificationItem, paramContinuation);
        break;
    } 
  }
  
  protected void p_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_bind_nns(headTail.getHead(), paramObject, paramAttributes, paramContinuation);
        break;
      case 2:
        c_bind(headTail.getHead(), paramObject, paramAttributes, paramContinuation);
        break;
    } 
  }
  
  protected void p_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_rebind_nns(headTail.getHead(), paramObject, paramAttributes, paramContinuation);
        break;
      case 2:
        c_rebind(headTail.getHead(), paramObject, paramAttributes, paramContinuation);
        break;
    } 
  }
  
  protected DirContext p_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    DirContext dirContext = null;
    switch (headTail.getStatus()) {
      case 3:
        dirContext = c_createSubcontext_nns(headTail.getHead(), paramAttributes, paramContinuation);
        break;
      case 2:
        dirContext = c_createSubcontext(headTail.getHead(), paramAttributes, paramContinuation);
        break;
    } 
    return dirContext;
  }
  
  protected NamingEnumeration<SearchResult> p_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    NamingEnumeration namingEnumeration = null;
    switch (headTail.getStatus()) {
      case 3:
        namingEnumeration = c_search_nns(headTail.getHead(), paramAttributes, paramArrayOfString, paramContinuation);
        break;
      case 2:
        namingEnumeration = c_search(headTail.getHead(), paramAttributes, paramArrayOfString, paramContinuation);
        break;
    } 
    return namingEnumeration;
  }
  
  protected NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    NamingEnumeration namingEnumeration = null;
    switch (headTail.getStatus()) {
      case 3:
        namingEnumeration = c_search_nns(headTail.getHead(), paramString, paramSearchControls, paramContinuation);
        break;
      case 2:
        namingEnumeration = c_search(headTail.getHead(), paramString, paramSearchControls, paramContinuation);
        break;
    } 
    return namingEnumeration;
  }
  
  protected NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    NamingEnumeration namingEnumeration = null;
    switch (headTail.getStatus()) {
      case 3:
        namingEnumeration = c_search_nns(headTail.getHead(), paramString, paramArrayOfObject, paramSearchControls, paramContinuation);
        break;
      case 2:
        namingEnumeration = c_search(headTail.getHead(), paramString, paramArrayOfObject, paramSearchControls, paramContinuation);
        break;
    } 
    return namingEnumeration;
  }
  
  protected DirContext p_getSchema(Name paramName, Continuation paramContinuation) throws NamingException {
    DirContext dirContext = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        dirContext = c_getSchema_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        dirContext = c_getSchema(headTail.getHead(), paramContinuation);
        break;
    } 
    return dirContext;
  }
  
  protected DirContext p_getSchemaClassDefinition(Name paramName, Continuation paramContinuation) throws NamingException {
    DirContext dirContext = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        dirContext = c_getSchemaClassDefinition_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        dirContext = c_getSchemaClassDefinition(headTail.getHead(), paramContinuation);
        break;
    } 
    return dirContext;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\ComponentDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */