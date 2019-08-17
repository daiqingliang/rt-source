package com.sun.jndi.toolkit.ctx;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;

public abstract class AtomicContext extends ComponentContext {
  private static int debug = 0;
  
  protected abstract Object a_lookup(String paramString, Continuation paramContinuation) throws NamingException;
  
  protected abstract Object a_lookupLink(String paramString, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<NameClassPair> a_list(Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<Binding> a_listBindings(Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_bind(String paramString, Object paramObject, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_rebind(String paramString, Object paramObject, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_unbind(String paramString, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_destroySubcontext(String paramString, Continuation paramContinuation) throws NamingException;
  
  protected abstract Context a_createSubcontext(String paramString, Continuation paramContinuation) throws NamingException;
  
  protected abstract void a_rename(String paramString, Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract NameParser a_getNameParser(Continuation paramContinuation) throws NamingException;
  
  protected abstract StringHeadTail c_parseComponent(String paramString, Continuation paramContinuation) throws NamingException;
  
  protected Object a_resolveIntermediate_nns(String paramString, Continuation paramContinuation) throws NamingException {
    try {
      final Object obj = a_lookup(paramString, paramContinuation);
      if (object != null && getClass().isInstance(object)) {
        paramContinuation.setContinueNNS(object, paramString, this);
        return null;
      } 
      if (object != null && !(object instanceof Context)) {
        RefAddr refAddr = new RefAddr("nns") {
            private static final long serialVersionUID = -3399518522645918499L;
            
            public Object getContent() { return obj; }
          };
        Reference reference = new Reference("java.lang.Object", refAddr);
        CompositeName compositeName = new CompositeName();
        compositeName.add(paramString);
        compositeName.add("");
        paramContinuation.setContinue(reference, compositeName, this);
        return null;
      } 
      return object;
    } catch (NamingException namingException) {
      namingException.appendRemainingComponent("");
      throw namingException;
    } 
  }
  
  protected Object a_lookup_nns(String paramString, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramString, paramContinuation);
    return null;
  }
  
  protected Object a_lookupLink_nns(String paramString, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramString, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<NameClassPair> a_list_nns(Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<Binding> a_listBindings_nns(Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramContinuation);
    return null;
  }
  
  protected void a_bind_nns(String paramString, Object paramObject, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected void a_rebind_nns(String paramString, Object paramObject, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected void a_unbind_nns(String paramString, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected Context a_createSubcontext_nns(String paramString, Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramString, paramContinuation);
    return null;
  }
  
  protected void a_destroySubcontext_nns(String paramString, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected void a_rename_nns(String paramString, Name paramName, Continuation paramContinuation) throws NamingException { a_processJunction_nns(paramString, paramContinuation); }
  
  protected NameParser a_getNameParser_nns(Continuation paramContinuation) throws NamingException {
    a_processJunction_nns(paramContinuation);
    return null;
  }
  
  protected boolean isEmpty(String paramString) { return (paramString == null || paramString.equals("")); }
  
  protected Object c_lookup(Name paramName, Continuation paramContinuation) throws NamingException {
    Object object = null;
    if (resolve_to_penultimate_context(paramName, paramContinuation)) {
      object = a_lookup(paramName.toString(), paramContinuation);
      if (object != null && object instanceof javax.naming.LinkRef) {
        paramContinuation.setContinue(object, paramName, this);
        object = null;
      } 
    } 
    return object;
  }
  
  protected Object c_lookupLink(Name paramName, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context(paramName, paramContinuation) ? a_lookupLink(paramName.toString(), paramContinuation) : null; }
  
  protected NamingEnumeration<NameClassPair> c_list(Name paramName, Continuation paramContinuation) throws NamingException { return resolve_to_context(paramName, paramContinuation) ? a_list(paramContinuation) : null; }
  
  protected NamingEnumeration<Binding> c_listBindings(Name paramName, Continuation paramContinuation) throws NamingException { return resolve_to_context(paramName, paramContinuation) ? a_listBindings(paramContinuation) : null; }
  
  protected void c_bind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_bind(paramName.toString(), paramObject, paramContinuation); 
  }
  
  protected void c_rebind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_rebind(paramName.toString(), paramObject, paramContinuation); 
  }
  
  protected void c_unbind(Name paramName, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_unbind(paramName.toString(), paramContinuation); 
  }
  
  protected void c_destroySubcontext(Name paramName, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName, paramContinuation))
      a_destroySubcontext(paramName.toString(), paramContinuation); 
  }
  
  protected Context c_createSubcontext(Name paramName, Continuation paramContinuation) throws NamingException { return resolve_to_penultimate_context(paramName, paramContinuation) ? a_createSubcontext(paramName.toString(), paramContinuation) : null; }
  
  protected void c_rename(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException {
    if (resolve_to_penultimate_context(paramName1, paramContinuation))
      a_rename(paramName1.toString(), paramName2, paramContinuation); 
  }
  
  protected NameParser c_getNameParser(Name paramName, Continuation paramContinuation) throws NamingException { return resolve_to_context(paramName, paramContinuation) ? a_getNameParser(paramContinuation) : null; }
  
  protected Object c_resolveIntermediate_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      Object object = null;
      if (resolve_to_penultimate_context_nns(paramName, paramContinuation)) {
        object = a_resolveIntermediate_nns(paramName.toString(), paramContinuation);
        if (object != null && object instanceof javax.naming.LinkRef) {
          paramContinuation.setContinue(object, paramName, this);
          object = null;
        } 
      } 
      return object;
    } 
    return super.c_resolveIntermediate_nns(paramName, paramContinuation);
  }
  
  protected Object c_lookup_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      Object object = null;
      if (resolve_to_penultimate_context_nns(paramName, paramContinuation)) {
        object = a_lookup_nns(paramName.toString(), paramContinuation);
        if (object != null && object instanceof javax.naming.LinkRef) {
          paramContinuation.setContinue(object, paramName, this);
          object = null;
        } 
      } 
      return object;
    } 
    return super.c_lookup_nns(paramName, paramContinuation);
  }
  
  protected Object c_lookupLink_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      resolve_to_nns_and_continue(paramName, paramContinuation);
      return null;
    } 
    return super.c_lookupLink_nns(paramName, paramContinuation);
  }
  
  protected NamingEnumeration<NameClassPair> c_list_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      resolve_to_nns_and_continue(paramName, paramContinuation);
      return null;
    } 
    return super.c_list_nns(paramName, paramContinuation);
  }
  
  protected NamingEnumeration<Binding> c_listBindings_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      resolve_to_nns_and_continue(paramName, paramContinuation);
      return null;
    } 
    return super.c_listBindings_nns(paramName, paramContinuation);
  }
  
  protected void c_bind_nns(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
        a_bind_nns(paramName.toString(), paramObject, paramContinuation); 
    } else {
      super.c_bind_nns(paramName, paramObject, paramContinuation);
    } 
  }
  
  protected void c_rebind_nns(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
        a_rebind_nns(paramName.toString(), paramObject, paramContinuation); 
    } else {
      super.c_rebind_nns(paramName, paramObject, paramContinuation);
    } 
  }
  
  protected void c_unbind_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
        a_unbind_nns(paramName.toString(), paramContinuation); 
    } else {
      super.c_unbind_nns(paramName, paramContinuation);
    } 
  }
  
  protected Context c_createSubcontext_nns(Name paramName, Continuation paramContinuation) throws NamingException { return (this._contextType == 3) ? (resolve_to_penultimate_context_nns(paramName, paramContinuation) ? a_createSubcontext_nns(paramName.toString(), paramContinuation) : null) : super.c_createSubcontext_nns(paramName, paramContinuation); }
  
  protected void c_destroySubcontext_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      if (resolve_to_penultimate_context_nns(paramName, paramContinuation))
        a_destroySubcontext_nns(paramName.toString(), paramContinuation); 
    } else {
      super.c_destroySubcontext_nns(paramName, paramContinuation);
    } 
  }
  
  protected void c_rename_nns(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      if (resolve_to_penultimate_context_nns(paramName1, paramContinuation))
        a_rename_nns(paramName1.toString(), paramName2, paramContinuation); 
    } else {
      super.c_rename_nns(paramName1, paramName2, paramContinuation);
    } 
  }
  
  protected NameParser c_getNameParser_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (this._contextType == 3) {
      resolve_to_nns_and_continue(paramName, paramContinuation);
      return null;
    } 
    return super.c_getNameParser_nns(paramName, paramContinuation);
  }
  
  protected void a_processJunction_nns(String paramString, Continuation paramContinuation) throws NamingException {
    if (paramString.equals("")) {
      NameNotFoundException nameNotFoundException = new NameNotFoundException();
      paramContinuation.setErrorNNS(this, paramString);
      throw paramContinuation.fillInException(nameNotFoundException);
    } 
    try {
      Object object = a_lookup(paramString, paramContinuation);
      if (paramContinuation.isContinue()) {
        paramContinuation.appendRemainingComponent("");
      } else {
        paramContinuation.setContinueNNS(object, paramString, this);
      } 
    } catch (NamingException namingException) {
      namingException.appendRemainingComponent("");
      throw namingException;
    } 
  }
  
  protected void a_processJunction_nns(Continuation paramContinuation) throws NamingException {
    RefAddr refAddr = new RefAddr("nns") {
        private static final long serialVersionUID = 3449785852664978312L;
        
        public Object getContent() { return AtomicContext.this; }
      };
    Reference reference = new Reference("java.lang.Object", refAddr);
    paramContinuation.setContinue(reference, _NNS_NAME, this);
  }
  
  protected boolean resolve_to_context(Name paramName, Continuation paramContinuation) throws NamingException {
    String str1 = paramName.toString();
    StringHeadTail stringHeadTail = c_parseComponent(str1, paramContinuation);
    String str2 = stringHeadTail.getTail();
    String str3 = stringHeadTail.getHead();
    if (debug > 0)
      System.out.println("RESOLVE TO CONTEXT(" + str1 + ") = {" + str3 + ", " + str2 + "}"); 
    if (str3 == null) {
      InvalidNameException invalidNameException = new InvalidNameException();
      throw paramContinuation.fillInException(invalidNameException);
    } 
    if (!isEmpty(str3)) {
      try {
        Object object = a_lookup(str3, paramContinuation);
        if (object != null) {
          paramContinuation.setContinue(object, str3, this, (str2 == null) ? "" : str2);
        } else if (paramContinuation.isContinue()) {
          paramContinuation.appendRemainingComponent(str2);
        } 
      } catch (NamingException namingException) {
        namingException.appendRemainingComponent(str2);
        throw namingException;
      } 
    } else {
      paramContinuation.setSuccess();
      return true;
    } 
    return false;
  }
  
  protected boolean resolve_to_penultimate_context(Name paramName, Continuation paramContinuation) throws NamingException {
    String str1 = paramName.toString();
    if (debug > 0)
      System.out.println("RESOLVE TO PENULTIMATE" + str1); 
    StringHeadTail stringHeadTail = c_parseComponent(str1, paramContinuation);
    String str2 = stringHeadTail.getTail();
    String str3 = stringHeadTail.getHead();
    if (str3 == null) {
      InvalidNameException invalidNameException = new InvalidNameException();
      throw paramContinuation.fillInException(invalidNameException);
    } 
    if (!isEmpty(str2)) {
      try {
        Object object = a_lookup(str3, paramContinuation);
        if (object != null) {
          paramContinuation.setContinue(object, str3, this, str2);
        } else if (paramContinuation.isContinue()) {
          paramContinuation.appendRemainingComponent(str2);
        } 
      } catch (NamingException namingException) {
        namingException.appendRemainingComponent(str2);
        throw namingException;
      } 
    } else {
      paramContinuation.setSuccess();
      return true;
    } 
    return false;
  }
  
  protected boolean resolve_to_penultimate_context_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    try {
      if (debug > 0)
        System.out.println("RESOLVE TO PENULTIMATE NNS" + paramName.toString()); 
      boolean bool = resolve_to_penultimate_context(paramName, paramContinuation);
      if (paramContinuation.isContinue())
        paramContinuation.appendRemainingComponent(""); 
      return bool;
    } catch (NamingException namingException) {
      namingException.appendRemainingComponent("");
      throw namingException;
    } 
  }
  
  protected void resolve_to_nns_and_continue(Name paramName, Continuation paramContinuation) throws NamingException {
    if (debug > 0)
      System.out.println("RESOLVE TO NNS AND CONTINUE" + paramName.toString()); 
    if (resolve_to_penultimate_context_nns(paramName, paramContinuation)) {
      Object object = a_lookup_nns(paramName.toString(), paramContinuation);
      if (object != null)
        paramContinuation.setContinue(object, paramName, this); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\AtomicContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */