package com.sun.jndi.toolkit.dir;

import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class ContextEnumerator extends Object implements NamingEnumeration<Binding> {
  private static boolean debug = false;
  
  private NamingEnumeration<Binding> children = null;
  
  private Binding currentChild = null;
  
  private boolean currentReturned = false;
  
  private Context root;
  
  private ContextEnumerator currentChildEnum = null;
  
  private boolean currentChildExpanded = false;
  
  private boolean rootProcessed = false;
  
  private int scope = 2;
  
  private String contextName = "";
  
  public ContextEnumerator(Context paramContext) throws NamingException { this(paramContext, 2); }
  
  public ContextEnumerator(Context paramContext, int paramInt) throws NamingException { this(paramContext, paramInt, "", (paramInt != 1)); }
  
  protected ContextEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean) throws NamingException {
    if (paramContext == null)
      throw new IllegalArgumentException("null context passed"); 
    this.root = paramContext;
    if (paramInt != 0)
      this.children = getImmediateChildren(paramContext); 
    this.scope = paramInt;
    this.contextName = paramString;
    this.rootProcessed = !paramBoolean;
    prepNextChild();
  }
  
  protected NamingEnumeration<Binding> getImmediateChildren(Context paramContext) throws NamingException { return paramContext.listBindings(""); }
  
  protected ContextEnumerator newEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean) throws NamingException { return new ContextEnumerator(paramContext, paramInt, paramString, paramBoolean); }
  
  public boolean hasMore() throws NamingException { return (!this.rootProcessed || (this.scope != 0 && hasMoreDescendants())); }
  
  public boolean hasMoreElements() throws NamingException {
    try {
      return hasMore();
    } catch (NamingException namingException) {
      return false;
    } 
  }
  
  public Binding nextElement() {
    try {
      return next();
    } catch (NamingException namingException) {
      throw new NoSuchElementException(namingException.toString());
    } 
  }
  
  public Binding next() {
    if (!this.rootProcessed) {
      this.rootProcessed = true;
      return new Binding("", this.root.getClass().getName(), this.root, true);
    } 
    if (this.scope != 0 && hasMoreDescendants())
      return getNextDescendant(); 
    throw new NoSuchElementException();
  }
  
  public void close() throws NamingException { this.root = null; }
  
  private boolean hasMoreChildren() throws NamingException { return (this.children != null && this.children.hasMore()); }
  
  private Binding getNextChild() {
    Binding binding1 = (Binding)this.children.next();
    Binding binding2 = null;
    if (binding1.isRelative() && !this.contextName.equals("")) {
      NameParser nameParser = this.root.getNameParser("");
      Name name = nameParser.parse(this.contextName);
      name.add(binding1.getName());
      if (debug)
        System.out.println("ContextEnumerator: adding " + name); 
      binding2 = new Binding(name.toString(), binding1.getClassName(), binding1.getObject(), binding1.isRelative());
    } else {
      if (debug)
        System.out.println("ContextEnumerator: using old binding"); 
      binding2 = binding1;
    } 
    return binding2;
  }
  
  private boolean hasMoreDescendants() throws NamingException {
    if (!this.currentReturned) {
      if (debug)
        System.out.println("hasMoreDescendants returning " + ((this.currentChild != null) ? 1 : 0)); 
      return (this.currentChild != null);
    } 
    if (this.currentChildExpanded && this.currentChildEnum.hasMore()) {
      if (debug)
        System.out.println("hasMoreDescendants returning true"); 
      return true;
    } 
    if (debug)
      System.out.println("hasMoreDescendants returning hasMoreChildren"); 
    return hasMoreChildren();
  }
  
  private Binding getNextDescendant() {
    if (!this.currentReturned) {
      if (debug)
        System.out.println("getNextDescedant: simple case"); 
      this.currentReturned = true;
      return this.currentChild;
    } 
    if (this.currentChildExpanded && this.currentChildEnum.hasMore()) {
      if (debug)
        System.out.println("getNextDescedant: expanded case"); 
      return this.currentChildEnum.next();
    } 
    if (debug)
      System.out.println("getNextDescedant: next case"); 
    prepNextChild();
    return getNextDescendant();
  }
  
  private void prepNextChild() throws NamingException {
    if (hasMoreChildren()) {
      try {
        this.currentChild = getNextChild();
        this.currentReturned = false;
      } catch (NamingException namingException) {
        if (debug)
          System.out.println(namingException); 
        if (debug)
          namingException.printStackTrace(); 
      } 
    } else {
      this.currentChild = null;
      return;
    } 
    if (this.scope == 2 && this.currentChild.getObject() instanceof Context) {
      this.currentChildEnum = newEnumerator((Context)this.currentChild.getObject(), this.scope, this.currentChild.getName(), false);
      this.currentChildExpanded = true;
      if (debug)
        System.out.println("prepNextChild: expanded"); 
    } else {
      this.currentChildExpanded = false;
      this.currentChildEnum = null;
      if (debug)
        System.out.println("prepNextChild: normal"); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\dir\ContextEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */