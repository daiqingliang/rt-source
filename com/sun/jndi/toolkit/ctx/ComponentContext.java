package com.sun.jndi.toolkit.ctx;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ResolveResult;

public abstract class ComponentContext extends PartialCompositeContext {
  private static int debug = 0;
  
  protected static final byte USE_CONTINUATION = 1;
  
  protected static final byte TERMINAL_COMPONENT = 2;
  
  protected static final byte TERMINAL_NNS_COMPONENT = 3;
  
  protected abstract Object c_lookup(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract Object c_lookupLink(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<NameClassPair> c_list(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract NamingEnumeration<Binding> c_listBindings(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_bind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_rebind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_unbind(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_destroySubcontext(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract Context c_createSubcontext(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected abstract void c_rename(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException;
  
  protected abstract NameParser c_getNameParser(Name paramName, Continuation paramContinuation) throws NamingException;
  
  protected HeadTail p_parseComponent(Name paramName, Continuation paramContinuation) throws NamingException {
    Name name2;
    Name name1;
    byte b;
    if (paramName.isEmpty() || paramName.get(0).equals("")) {
      b = 0;
    } else {
      b = 1;
    } 
    if (paramName instanceof CompositeName) {
      name1 = paramName.getPrefix(b);
      name2 = paramName.getSuffix(b);
    } else {
      name1 = (new CompositeName()).add(paramName.toString());
      name2 = null;
    } 
    if (debug > 2) {
      System.err.println("ORIG: " + paramName);
      System.err.println("PREFIX: " + paramName);
      System.err.println("SUFFIX: " + null);
    } 
    return new HeadTail(name1, name2);
  }
  
  protected Object c_resolveIntermediate_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    try {
      final Object obj = c_lookup(paramName, paramContinuation);
      if (object != null && getClass().isInstance(object)) {
        paramContinuation.setContinueNNS(object, paramName, this);
        return null;
      } 
      if (object != null && !(object instanceof Context)) {
        RefAddr refAddr = new RefAddr("nns") {
            private static final long serialVersionUID = -8831204798861786362L;
            
            public Object getContent() { return obj; }
          };
        Reference reference = new Reference("java.lang.Object", refAddr);
        CompositeName compositeName = (CompositeName)paramName.clone();
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
  
  protected Object c_lookup_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected Object c_lookupLink_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<NameClassPair> c_list_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<Binding> c_listBindings_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_bind_nns(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected void c_rebind_nns(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected void c_unbind_nns(Name paramName, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected Context c_createSubcontext_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_destroySubcontext_nns(Name paramName, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName, paramContinuation); }
  
  protected void c_rename_nns(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException { c_processJunction_nns(paramName1, paramContinuation); }
  
  protected NameParser c_getNameParser_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_processJunction_nns(Name paramName, Continuation paramContinuation) throws NamingException {
    if (paramName.isEmpty()) {
      RefAddr refAddr = new RefAddr("nns") {
          private static final long serialVersionUID = -1389472957988053402L;
          
          public Object getContent() { return ComponentContext.this; }
        };
      Reference reference = new Reference("java.lang.Object", refAddr);
      paramContinuation.setContinue(reference, _NNS_NAME, this);
      return;
    } 
    try {
      Object object = c_lookup(paramName, paramContinuation);
      if (paramContinuation.isContinue()) {
        paramContinuation.appendRemainingComponent("");
      } else {
        paramContinuation.setContinueNNS(object, paramName, this);
      } 
    } catch (NamingException namingException) {
      namingException.appendRemainingComponent("");
      throw namingException;
    } 
  }
  
  protected HeadTail p_resolveIntermediate(Name paramName, Continuation paramContinuation) throws NamingException {
    byte b = 1;
    paramContinuation.setSuccess();
    HeadTail headTail = p_parseComponent(paramName, paramContinuation);
    Name name1 = headTail.getTail();
    Name name2 = headTail.getHead();
    if (name1 == null || name1.isEmpty()) {
      b = 2;
    } else if (!name1.get(0).equals("")) {
      try {
        Object object = c_resolveIntermediate_nns(name2, paramContinuation);
        if (object != null) {
          paramContinuation.setContinue(object, name2, this, name1);
        } else if (paramContinuation.isContinue()) {
          checkAndAdjustRemainingName(paramContinuation.getRemainingName());
          paramContinuation.appendRemainingName(name1);
        } 
      } catch (NamingException namingException) {
        checkAndAdjustRemainingName(namingException.getRemainingName());
        namingException.appendRemainingName(name1);
        throw namingException;
      } 
    } else if (name1.size() == 1) {
      b = 3;
    } else if (name2.isEmpty() || isAllEmpty(name1)) {
      Name name = name1.getSuffix(1);
      try {
        Object object = c_lookup_nns(name2, paramContinuation);
        if (object != null) {
          paramContinuation.setContinue(object, name2, this, name);
        } else if (paramContinuation.isContinue()) {
          paramContinuation.appendRemainingName(name);
        } 
      } catch (NamingException namingException) {
        namingException.appendRemainingName(name);
        throw namingException;
      } 
    } else {
      try {
        Object object = c_resolveIntermediate_nns(name2, paramContinuation);
        if (object != null) {
          paramContinuation.setContinue(object, name2, this, name1);
        } else if (paramContinuation.isContinue()) {
          checkAndAdjustRemainingName(paramContinuation.getRemainingName());
          paramContinuation.appendRemainingName(name1);
        } 
      } catch (NamingException namingException) {
        checkAndAdjustRemainingName(namingException.getRemainingName());
        namingException.appendRemainingName(name1);
        throw namingException;
      } 
    } 
    headTail.setStatus(b);
    return headTail;
  }
  
  void checkAndAdjustRemainingName(Name paramName) throws InvalidNameException {
    int i;
    if (paramName != null && (i = paramName.size()) > 1 && paramName.get(i - 1).equals(""))
      paramName.remove(i - 1); 
  }
  
  protected boolean isAllEmpty(Name paramName) {
    int i = paramName.size();
    for (byte b = 0; b < i; b++) {
      if (!paramName.get(b).equals(""))
        return false; 
    } 
    return true;
  }
  
  protected ResolveResult p_resolveToClass(Name paramName, Class<?> paramClass, Continuation paramContinuation) throws NamingException {
    Object object;
    if (paramClass.isInstance(this)) {
      paramContinuation.setSuccess();
      return new ResolveResult(this, paramName);
    } 
    ResolveResult resolveResult = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        object = p_lookup(paramName, paramContinuation);
        if (!paramContinuation.isContinue() && paramClass.isInstance(object))
          resolveResult = new ResolveResult(object, _EMPTY_NAME); 
        break;
      case 2:
        paramContinuation.setSuccess();
        break;
    } 
    return resolveResult;
  }
  
  protected Object p_lookup(Name paramName, Continuation paramContinuation) throws NamingException {
    Object object = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        object = c_lookup_nns(headTail.getHead(), paramContinuation);
        if (object instanceof javax.naming.LinkRef) {
          paramContinuation.setContinue(object, headTail.getHead(), this);
          object = null;
        } 
        break;
      case 2:
        object = c_lookup(headTail.getHead(), paramContinuation);
        if (object instanceof javax.naming.LinkRef) {
          paramContinuation.setContinue(object, headTail.getHead(), this);
          object = null;
        } 
        break;
    } 
    return object;
  }
  
  protected NamingEnumeration<NameClassPair> p_list(Name paramName, Continuation paramContinuation) throws NamingException {
    NamingEnumeration namingEnumeration = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        if (debug > 0)
          System.out.println("c_list_nns(" + headTail.getHead() + ")"); 
        namingEnumeration = c_list_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        if (debug > 0)
          System.out.println("c_list(" + headTail.getHead() + ")"); 
        namingEnumeration = c_list(headTail.getHead(), paramContinuation);
        break;
    } 
    return namingEnumeration;
  }
  
  protected NamingEnumeration<Binding> p_listBindings(Name paramName, Continuation paramContinuation) throws NamingException {
    NamingEnumeration namingEnumeration = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        namingEnumeration = c_listBindings_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        namingEnumeration = c_listBindings(headTail.getHead(), paramContinuation);
        break;
    } 
    return namingEnumeration;
  }
  
  protected void p_bind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_bind_nns(headTail.getHead(), paramObject, paramContinuation);
        break;
      case 2:
        c_bind(headTail.getHead(), paramObject, paramContinuation);
        break;
    } 
  }
  
  protected void p_rebind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_rebind_nns(headTail.getHead(), paramObject, paramContinuation);
        break;
      case 2:
        c_rebind(headTail.getHead(), paramObject, paramContinuation);
        break;
    } 
  }
  
  protected void p_unbind(Name paramName, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_unbind_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        c_unbind(headTail.getHead(), paramContinuation);
        break;
    } 
  }
  
  protected void p_destroySubcontext(Name paramName, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_destroySubcontext_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        c_destroySubcontext(headTail.getHead(), paramContinuation);
        break;
    } 
  }
  
  protected Context p_createSubcontext(Name paramName, Continuation paramContinuation) throws NamingException {
    Context context = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        context = c_createSubcontext_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        context = c_createSubcontext(headTail.getHead(), paramContinuation);
        break;
    } 
    return context;
  }
  
  protected void p_rename(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException {
    HeadTail headTail = p_resolveIntermediate(paramName1, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        c_rename_nns(headTail.getHead(), paramName2, paramContinuation);
        break;
      case 2:
        c_rename(headTail.getHead(), paramName2, paramContinuation);
        break;
    } 
  }
  
  protected NameParser p_getNameParser(Name paramName, Continuation paramContinuation) throws NamingException {
    NameParser nameParser = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        nameParser = c_getNameParser_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        nameParser = c_getNameParser(headTail.getHead(), paramContinuation);
        break;
    } 
    return nameParser;
  }
  
  protected Object p_lookupLink(Name paramName, Continuation paramContinuation) throws NamingException {
    Object object = null;
    HeadTail headTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (headTail.getStatus()) {
      case 3:
        object = c_lookupLink_nns(headTail.getHead(), paramContinuation);
        break;
      case 2:
        object = c_lookupLink(headTail.getHead(), paramContinuation);
        break;
    } 
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\ComponentContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */