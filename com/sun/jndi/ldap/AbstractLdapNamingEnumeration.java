package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.naming.LimitExceededException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

abstract class AbstractLdapNamingEnumeration<T extends NameClassPair> extends Object implements NamingEnumeration<T>, ReferralEnumeration<T> {
  protected Name listArg;
  
  private boolean cleaned = false;
  
  private LdapResult res;
  
  private LdapClient enumClnt;
  
  private Continuation cont;
  
  private Vector<LdapEntry> entries = null;
  
  private int limit = 0;
  
  private int posn = 0;
  
  protected LdapCtx homeCtx;
  
  private LdapReferralException refEx = null;
  
  private NamingException errEx = null;
  
  private boolean more = true;
  
  private boolean hasMoreCalled = false;
  
  AbstractLdapNamingEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, Name paramName, Continuation paramContinuation) throws NamingException {
    if (paramLdapResult.status != 0 && paramLdapResult.status != 4 && paramLdapResult.status != 3 && paramLdapResult.status != 11 && paramLdapResult.status != 10 && paramLdapResult.status != 9) {
      NamingException namingException = new NamingException(LdapClient.getErrorMessage(paramLdapResult.status, paramLdapResult.errorMessage));
      throw paramContinuation.fillInException(namingException);
    } 
    this.res = paramLdapResult;
    this.entries = paramLdapResult.entries;
    this.limit = (this.entries == null) ? 0 : this.entries.size();
    this.listArg = paramName;
    this.cont = paramContinuation;
    if (paramLdapResult.refEx != null)
      this.refEx = paramLdapResult.refEx; 
    this.homeCtx = paramLdapCtx;
    paramLdapCtx.incEnumCount();
    this.enumClnt = paramLdapCtx.clnt;
  }
  
  public final T nextElement() {
    try {
      return (T)next();
    } catch (NamingException namingException) {
      cleanup();
      return null;
    } 
  }
  
  public final boolean hasMoreElements() {
    try {
      return hasMore();
    } catch (NamingException namingException) {
      cleanup();
      return false;
    } 
  }
  
  private void getNextBatch() throws NamingException {
    this.res = this.homeCtx.getSearchReply(this.enumClnt, this.res);
    if (this.res == null) {
      this.limit = this.posn = 0;
      return;
    } 
    this.entries = this.res.entries;
    this.limit = (this.entries == null) ? 0 : this.entries.size();
    this.posn = 0;
    if (this.res.status != 0 || (this.res.status == 0 && this.res.referrals != null))
      try {
        this.homeCtx.processReturnCode(this.res, this.listArg);
      } catch (LimitExceededException|PartialResultException limitExceededException) {
        setNamingException(limitExceededException);
      }  
    if (this.res.refEx != null) {
      if (this.refEx == null) {
        this.refEx = this.res.refEx;
      } else {
        this.refEx = this.refEx.appendUnprocessedReferrals(this.res.refEx);
      } 
      this.res.refEx = null;
    } 
    if (this.res.resControls != null)
      this.homeCtx.respCtls = this.res.resControls; 
  }
  
  public final boolean hasMore() {
    if (this.hasMoreCalled)
      return this.more; 
    this.hasMoreCalled = true;
    return !this.more ? false : (this.more = hasMoreImpl());
  }
  
  public final T next() {
    if (!this.hasMoreCalled)
      hasMore(); 
    this.hasMoreCalled = false;
    return (T)nextImpl();
  }
  
  private boolean hasMoreImpl() {
    if (this.posn == this.limit)
      getNextBatch(); 
    if (this.posn < this.limit)
      return true; 
    try {
      return hasMoreReferrals();
    } catch (LdapReferralException|LimitExceededException|PartialResultException ldapReferralException) {
      cleanup();
      throw ldapReferralException;
    } catch (NamingException namingException) {
      cleanup();
      PartialResultException partialResultException = new PartialResultException();
      partialResultException.setRootCause(namingException);
      throw partialResultException;
    } 
  }
  
  private T nextImpl() {
    try {
      return (T)nextAux();
    } catch (NamingException namingException) {
      cleanup();
      throw this.cont.fillInException(namingException);
    } 
  }
  
  private T nextAux() {
    if (this.posn == this.limit)
      getNextBatch(); 
    if (this.posn >= this.limit) {
      cleanup();
      throw new NoSuchElementException("invalid enumeration handle");
    } 
    LdapEntry ldapEntry = (LdapEntry)this.entries.elementAt(this.posn++);
    return (T)createItem(ldapEntry.DN, ldapEntry.attributes, ldapEntry.respCtls);
  }
  
  protected final String getAtom(String paramString) {
    try {
      LdapName ldapName = new LdapName(paramString);
      return ldapName.get(ldapName.size() - 1);
    } catch (NamingException namingException) {
      return paramString;
    } 
  }
  
  protected abstract T createItem(String paramString, Attributes paramAttributes, Vector<Control> paramVector) throws NamingException;
  
  public void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException) {
    if (this.refEx != null) {
      this.refEx = this.refEx.appendUnprocessedReferrals(paramLdapReferralException);
    } else {
      this.refEx = paramLdapReferralException.appendUnprocessedReferrals(this.refEx);
    } 
  }
  
  final void setNamingException(NamingException paramNamingException) { this.errEx = paramNamingException; }
  
  protected abstract AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext) throws NamingException;
  
  protected final boolean hasMoreReferrals() {
    if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions())) {
      if (this.homeCtx.handleReferrals == 2)
        throw (NamingException)this.refEx.fillInStackTrace(); 
      while (true) {
        ldapReferralContext = (LdapReferralContext)this.refEx.getReferralContext(this.homeCtx.envprops, this.homeCtx.reqCtls);
        try {
          update(getReferredResults(ldapReferralContext));
          ldapReferralContext.close();
        } catch (LdapReferralException ldapReferralException) {
          if (this.errEx == null)
            this.errEx = ldapReferralException.getNamingException(); 
          this.refEx = ldapReferralException;
        } finally {
          ldapReferralContext.close();
        } 
      } 
      return hasMoreImpl();
    } 
    cleanup();
    if (this.errEx != null)
      throw this.errEx; 
    return false;
  }
  
  protected void update(AbstractLdapNamingEnumeration<? extends NameClassPair> paramAbstractLdapNamingEnumeration) {
    this.homeCtx.decEnumCount();
    this.homeCtx = paramAbstractLdapNamingEnumeration.homeCtx;
    this.enumClnt = paramAbstractLdapNamingEnumeration.enumClnt;
    paramAbstractLdapNamingEnumeration.homeCtx = null;
    this.posn = paramAbstractLdapNamingEnumeration.posn;
    this.limit = paramAbstractLdapNamingEnumeration.limit;
    this.res = paramAbstractLdapNamingEnumeration.res;
    this.entries = paramAbstractLdapNamingEnumeration.entries;
    this.refEx = paramAbstractLdapNamingEnumeration.refEx;
    this.listArg = paramAbstractLdapNamingEnumeration.listArg;
  }
  
  protected final void finalize() throws NamingException { cleanup(); }
  
  protected final void cleanup() throws NamingException {
    if (this.cleaned)
      return; 
    if (this.enumClnt != null)
      this.enumClnt.clearSearchReply(this.res, this.homeCtx.reqCtls); 
    this.enumClnt = null;
    this.cleaned = true;
    if (this.homeCtx != null) {
      this.homeCtx.decEnumCount();
      this.homeCtx = null;
    } 
  }
  
  public final void close() throws NamingException { cleanup(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\AbstractLdapNamingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */