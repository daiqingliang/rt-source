package com.sun.jndi.toolkit.dir;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public final class LazySearchEnumerationImpl extends Object implements NamingEnumeration<SearchResult> {
  private NamingEnumeration<Binding> candidates;
  
  private SearchResult nextMatch = null;
  
  private SearchControls cons;
  
  private AttrFilter filter;
  
  private Context context;
  
  private Hashtable<String, Object> env;
  
  private boolean useFactory = true;
  
  public LazySearchEnumerationImpl(NamingEnumeration<Binding> paramNamingEnumeration, AttrFilter paramAttrFilter, SearchControls paramSearchControls) throws NamingException {
    this.candidates = paramNamingEnumeration;
    this.filter = paramAttrFilter;
    if (paramSearchControls == null) {
      this.cons = new SearchControls();
    } else {
      this.cons = paramSearchControls;
    } 
  }
  
  public LazySearchEnumerationImpl(NamingEnumeration<Binding> paramNamingEnumeration, AttrFilter paramAttrFilter, SearchControls paramSearchControls, Context paramContext, Hashtable<String, Object> paramHashtable, boolean paramBoolean) throws NamingException {
    this.candidates = paramNamingEnumeration;
    this.filter = paramAttrFilter;
    this.env = (Hashtable)((paramHashtable == null) ? null : paramHashtable.clone());
    this.context = paramContext;
    this.useFactory = paramBoolean;
    if (paramSearchControls == null) {
      this.cons = new SearchControls();
    } else {
      this.cons = paramSearchControls;
    } 
  }
  
  public LazySearchEnumerationImpl(NamingEnumeration<Binding> paramNamingEnumeration, AttrFilter paramAttrFilter, SearchControls paramSearchControls, Context paramContext, Hashtable<String, Object> paramHashtable) throws NamingException { this(paramNamingEnumeration, paramAttrFilter, paramSearchControls, paramContext, paramHashtable, true); }
  
  public boolean hasMore() throws NamingException { return (findNextMatch(false) != null); }
  
  public boolean hasMoreElements() throws NamingException {
    try {
      return hasMore();
    } catch (NamingException namingException) {
      return false;
    } 
  }
  
  public SearchResult nextElement() {
    try {
      return findNextMatch(true);
    } catch (NamingException namingException) {
      throw new NoSuchElementException(namingException.toString());
    } 
  }
  
  public SearchResult next() { return findNextMatch(true); }
  
  public void close() throws NamingException {
    if (this.candidates != null)
      this.candidates.close(); 
  }
  
  private SearchResult findNextMatch(boolean paramBoolean) throws NamingException {
    if (this.nextMatch != null) {
      SearchResult searchResult = this.nextMatch;
      if (paramBoolean)
        this.nextMatch = null; 
      return searchResult;
    } 
    while (this.candidates.hasMore()) {
      Binding binding = (Binding)this.candidates.next();
      Object object = binding.getObject();
      if (object instanceof DirContext) {
        Attributes attributes = ((DirContext)object).getAttributes("");
        if (this.filter.check(attributes)) {
          if (!this.cons.getReturningObjFlag()) {
            object = null;
          } else if (this.useFactory) {
            try {
              CompositeName compositeName = (this.context != null) ? new CompositeName(binding.getName()) : null;
              object = DirectoryManager.getObjectInstance(object, compositeName, this.context, this.env, attributes);
            } catch (NamingException namingException) {
              throw namingException;
            } catch (Exception exception) {
              NamingException namingException = new NamingException("problem generating object using object factory");
              namingException.setRootCause(exception);
              throw namingException;
            } 
          } 
          SearchResult searchResult = new SearchResult(binding.getName(), binding.getClassName(), object, SearchFilter.selectAttributes(attributes, this.cons.getReturningAttributes()), true);
          if (!paramBoolean)
            this.nextMatch = searchResult; 
          return searchResult;
        } 
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\dir\LazySearchEnumerationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */