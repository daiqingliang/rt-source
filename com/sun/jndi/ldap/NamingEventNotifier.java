package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.io.IOException;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.InterruptedNamingException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.event.EventContext;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.ldap.LdapName;

final class NamingEventNotifier implements Runnable {
  private static final boolean debug = false;
  
  private Vector<NamingListener> namingListeners;
  
  private Thread worker;
  
  private LdapCtx context;
  
  private EventContext eventSrc;
  
  private EventSupport support;
  
  private NamingEnumeration<SearchResult> results;
  
  NotifierArgs info;
  
  NamingEventNotifier(EventSupport paramEventSupport, LdapCtx paramLdapCtx, NotifierArgs paramNotifierArgs, NamingListener paramNamingListener) throws NamingException {
    this.info = paramNotifierArgs;
    this.support = paramEventSupport;
    try {
      persistentSearchControl = new PersistentSearchControl(paramNotifierArgs.mask, true, true, true);
    } catch (IOException iOException) {
      NamingException namingException = new NamingException("Problem creating persistent search control");
      namingException.setRootCause(iOException);
      throw namingException;
    } 
    this.context = (LdapCtx)paramLdapCtx.newInstance(new Control[] { persistentSearchControl });
    this.eventSrc = paramLdapCtx;
    this.namingListeners = new Vector();
    this.namingListeners.addElement(paramNamingListener);
    this.worker = Obj.helper.createThread(this);
    this.worker.setDaemon(true);
    this.worker.start();
  }
  
  void addNamingListener(NamingListener paramNamingListener) { this.namingListeners.addElement(paramNamingListener); }
  
  void removeNamingListener(NamingListener paramNamingListener) { this.namingListeners.removeElement(paramNamingListener); }
  
  boolean hasNamingListeners() { return (this.namingListeners.size() > 0); }
  
  public void run() {
    try {
      Continuation continuation = new Continuation();
      continuation.setError(this, this.info.name);
      CompositeName compositeName = (this.info.name == null || this.info.name.equals("")) ? new CompositeName() : (new CompositeName()).add(this.info.name);
      this.results = this.context.searchAux(compositeName, this.info.filter, this.info.controls, true, false, continuation);
      ((LdapSearchEnumeration)this.results).setStartName(this.context.currentParsedDN);
      while (this.results.hasMore()) {
        SearchResult searchResult = (SearchResult)this.results.next();
        Control[] arrayOfControl = (searchResult instanceof HasControls) ? ((HasControls)searchResult).getControls() : null;
        if (arrayOfControl != null) {
          boolean bool = false;
          if (bool < arrayOfControl.length && arrayOfControl[bool] instanceof EntryChangeResponseControl) {
            EntryChangeResponseControl entryChangeResponseControl = (EntryChangeResponseControl)arrayOfControl[bool];
            long l = entryChangeResponseControl.getChangeNumber();
            switch (entryChangeResponseControl.getChangeType()) {
              case 1:
                fireObjectAdded(searchResult, l);
              case 2:
                fireObjectRemoved(searchResult, l);
              case 4:
                fireObjectChanged(searchResult, l);
              case 8:
                fireObjectRenamed(searchResult, entryChangeResponseControl.getPreviousDN(), l);
            } 
          } 
        } 
      } 
    } catch (InterruptedNamingException interruptedNamingException) {
    
    } catch (NamingException namingException) {
      fireNamingException(namingException);
      this.support.removeDeadNotifier(this.info);
    } finally {
      cleanup();
    } 
  }
  
  private void cleanup() {
    try {
      if (this.results != null) {
        this.results.close();
        this.results = null;
      } 
      if (this.context != null) {
        this.context.close();
        this.context = null;
      } 
    } catch (NamingException namingException) {}
  }
  
  void stop() {
    if (this.worker != null) {
      this.worker.interrupt();
      this.worker = null;
    } 
  }
  
  private void fireObjectAdded(Binding paramBinding, long paramLong) {
    if (this.namingListeners == null || this.namingListeners.size() == 0)
      return; 
    NamingEvent namingEvent = new NamingEvent(this.eventSrc, 0, paramBinding, null, new Long(paramLong));
    this.support.queueEvent(namingEvent, this.namingListeners);
  }
  
  private void fireObjectRemoved(Binding paramBinding, long paramLong) {
    if (this.namingListeners == null || this.namingListeners.size() == 0)
      return; 
    NamingEvent namingEvent = new NamingEvent(this.eventSrc, 1, null, paramBinding, new Long(paramLong));
    this.support.queueEvent(namingEvent, this.namingListeners);
  }
  
  private void fireObjectChanged(Binding paramBinding, long paramLong) {
    if (this.namingListeners == null || this.namingListeners.size() == 0)
      return; 
    Binding binding = new Binding(paramBinding.getName(), null, paramBinding.isRelative());
    NamingEvent namingEvent = new NamingEvent(this.eventSrc, 3, paramBinding, binding, new Long(paramLong));
    this.support.queueEvent(namingEvent, this.namingListeners);
  }
  
  private void fireObjectRenamed(Binding paramBinding, String paramString, long paramLong) {
    if (this.namingListeners == null || this.namingListeners.size() == 0)
      return; 
    Binding binding = null;
    try {
      LdapName ldapName = new LdapName(paramString);
      if (ldapName.startsWith(this.context.currentParsedDN)) {
        String str = ldapName.getSuffix(this.context.currentParsedDN.size()).toString();
        binding = new Binding(str, null);
      } 
    } catch (NamingException namingException) {}
    if (binding == null)
      binding = new Binding(paramString, null, false); 
    NamingEvent namingEvent = new NamingEvent(this.eventSrc, 2, paramBinding, binding, new Long(paramLong));
    this.support.queueEvent(namingEvent, this.namingListeners);
  }
  
  private void fireNamingException(NamingException paramNamingException) {
    if (this.namingListeners == null || this.namingListeners.size() == 0)
      return; 
    NamingExceptionEvent namingExceptionEvent = new NamingExceptionEvent(this.eventSrc, paramNamingException);
    this.support.queueEvent(namingExceptionEvent, this.namingListeners);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\NamingEventNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */