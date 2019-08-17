package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;

final class CNBindingEnumeration extends Object implements NamingEnumeration<Binding> {
  private static final int DEFAULT_BATCHSIZE = 100;
  
  private BindingListHolder _bindingList;
  
  private BindingIterator _bindingIter;
  
  private int counter;
  
  private int batchsize = 100;
  
  private CNCtx _ctx;
  
  private Hashtable<?, ?> _env;
  
  private boolean more = false;
  
  private boolean isLookedUpCtx = false;
  
  CNBindingEnumeration(CNCtx paramCNCtx, boolean paramBoolean, Hashtable<?, ?> paramHashtable) {
    String str = (paramHashtable != null) ? (String)paramHashtable.get("java.naming.batchsize") : null;
    if (str != null)
      try {
        this.batchsize = Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
        throw new IllegalArgumentException("Batch size not numeric: " + str);
      }  
    this._ctx = paramCNCtx;
    this._ctx.incEnumCount();
    this.isLookedUpCtx = paramBoolean;
    this._env = paramHashtable;
    this._bindingList = new BindingListHolder();
    BindingIteratorHolder bindingIteratorHolder = new BindingIteratorHolder();
    this._ctx._nc.list(0, this._bindingList, bindingIteratorHolder);
    this._bindingIter = bindingIteratorHolder.value;
    if (this._bindingIter != null) {
      this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
    } else {
      this.more = false;
    } 
    this.counter = 0;
  }
  
  public Binding next() throws NamingException {
    if (this.more && this.counter >= this._bindingList.value.length)
      getMore(); 
    if (this.more && this.counter < this._bindingList.value.length) {
      Binding binding = this._bindingList.value[this.counter];
      this.counter++;
      return mapBinding(binding);
    } 
    throw new NoSuchElementException();
  }
  
  public boolean hasMore() throws NamingException { return this.more ? ((this.counter < this._bindingList.value.length || getMore())) : false; }
  
  public boolean hasMoreElements() throws NamingException {
    try {
      return hasMore();
    } catch (NamingException namingException) {
      return false;
    } 
  }
  
  public Binding nextElement() throws NamingException {
    try {
      return next();
    } catch (NamingException namingException) {
      throw new NoSuchElementException();
    } 
  }
  
  public void close() throws NamingException {
    this.more = false;
    if (this._bindingIter != null) {
      this._bindingIter.destroy();
      this._bindingIter = null;
    } 
    if (this._ctx != null) {
      this._ctx.decEnumCount();
      if (this.isLookedUpCtx)
        this._ctx.close(); 
      this._ctx = null;
    } 
  }
  
  protected void finalize() throws NamingException {
    try {
      close();
    } catch (NamingException namingException) {}
  }
  
  private boolean getMore() throws NamingException {
    try {
      this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
      this.counter = 0;
    } catch (Exception exception) {
      this.more = false;
      NamingException namingException = new NamingException("Problem getting binding list");
      namingException.setRootCause(exception);
      throw namingException;
    } 
    return this.more;
  }
  
  private Binding mapBinding(Binding paramBinding) throws NamingException {
    Object object = this._ctx.callResolve(paramBinding.binding_name);
    Name name = CNNameParser.cosNameToName(paramBinding.binding_name);
    try {
      if (CorbaUtils.isObjectFactoryTrusted(object))
        object = NamingManager.getObjectInstance(object, name, this._ctx, this._env); 
    } catch (NamingException namingException) {
      throw namingException;
    } catch (Exception exception) {
      NamingException namingException = new NamingException("problem generating object using object factory");
      namingException.setRootCause(exception);
      throw namingException;
    } 
    String str1 = name.toString();
    Binding binding = new Binding(str1, object);
    NameComponent[] arrayOfNameComponent = this._ctx.makeFullName(paramBinding.binding_name);
    String str2 = CNNameParser.cosNameToInsString(arrayOfNameComponent);
    binding.setNameInNamespace(str2);
    return binding;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\CNBindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */