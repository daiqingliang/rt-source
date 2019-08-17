package com.sun.jndi.toolkit.dir;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeModificationException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirStateFactory;
import javax.naming.spi.DirectoryManager;

public class HierMemDirCtx implements DirContext {
  private static final boolean debug = false;
  
  private static final NameParser defaultParser = new HierarchicalNameParser();
  
  protected Hashtable<String, Object> myEnv;
  
  protected Hashtable<Name, Object> bindings;
  
  protected Attributes attrs;
  
  protected boolean ignoreCase = false;
  
  protected NamingException readOnlyEx = null;
  
  protected NameParser myParser = defaultParser;
  
  private boolean alwaysUseFactory;
  
  public void close() throws NamingException {
    this.myEnv = null;
    this.bindings = null;
    this.attrs = null;
  }
  
  public String getNameInNamespace() throws NamingException { throw new OperationNotSupportedException("Cannot determine full name"); }
  
  public HierMemDirCtx() throws NamingException { this(null, false, false); }
  
  public HierMemDirCtx(boolean paramBoolean) { this(null, paramBoolean, false); }
  
  public HierMemDirCtx(Hashtable<String, Object> paramHashtable, boolean paramBoolean) { this(paramHashtable, paramBoolean, false); }
  
  protected HierMemDirCtx(Hashtable<String, Object> paramHashtable, boolean paramBoolean1, boolean paramBoolean2) {
    this.myEnv = paramHashtable;
    this.ignoreCase = paramBoolean1;
    init();
    this.alwaysUseFactory = paramBoolean2;
  }
  
  private void init() throws NamingException {
    this.attrs = new BasicAttributes(this.ignoreCase);
    this.bindings = new Hashtable(11, 0.75F);
  }
  
  public Object lookup(String paramString) throws NamingException { return lookup(this.myParser.parse(paramString)); }
  
  public Object lookup(Name paramName) throws NamingException { return doLookup(paramName, this.alwaysUseFactory); }
  
  public Object doLookup(Name paramName, boolean paramBoolean) throws NamingException {
    Object object;
    HierMemDirCtx hierMemDirCtx1 = null;
    paramName = canonizeName(paramName);
    switch (paramName.size()) {
      case 0:
        hierMemDirCtx1 = this;
        break;
      case 1:
        object = this.bindings.get(paramName);
        break;
      default:
        hierMemDirCtx2 = (HierMemDirCtx)this.bindings.get(paramName.getPrefix(1));
        if (hierMemDirCtx2 == null) {
          object = null;
          break;
        } 
        object = hierMemDirCtx2.doLookup(paramName.getSuffix(1), false);
        break;
    } 
    if (object == null)
      throw new NameNotFoundException(paramName.toString()); 
    if (paramBoolean)
      try {
        return DirectoryManager.getObjectInstance(object, paramName, this, this.myEnv, (object instanceof HierMemDirCtx) ? ((HierMemDirCtx)object).attrs : null);
      } catch (NamingException hierMemDirCtx2) {
        throw hierMemDirCtx2;
      } catch (Exception hierMemDirCtx2) {
        NamingException namingException = new NamingException("Problem calling getObjectInstance");
        namingException.setRootCause(hierMemDirCtx2);
        throw namingException;
      }  
    return object;
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException { bind(this.myParser.parse(paramString), paramObject); }
  
  public void bind(Name paramName, Object paramObject) throws NamingException { doBind(paramName, paramObject, null, this.alwaysUseFactory); }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { bind(this.myParser.parse(paramString), paramObject, paramAttributes); }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException { doBind(paramName, paramObject, paramAttributes, this.alwaysUseFactory); }
  
  protected void doBind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean) throws NamingException {
    if (paramName.isEmpty())
      throw new InvalidNameException("Cannot bind empty name"); 
    if (paramBoolean) {
      DirStateFactory.Result result = DirectoryManager.getStateToBind(paramObject, paramName, this, this.myEnv, paramAttributes);
      paramObject = result.getObject();
      paramAttributes = result.getAttributes();
    } 
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    hierMemDirCtx.doBindAux(getLeafName(paramName), paramObject);
    if (paramAttributes != null && paramAttributes.size() > 0)
      modifyAttributes(paramName, 1, paramAttributes); 
  }
  
  protected void doBindAux(Name paramName, Object paramObject) throws NamingException {
    if (this.readOnlyEx != null)
      throw (NamingException)this.readOnlyEx.fillInStackTrace(); 
    if (this.bindings.get(paramName) != null)
      throw new NameAlreadyBoundException(paramName.toString()); 
    if (paramObject instanceof HierMemDirCtx) {
      this.bindings.put(paramName, paramObject);
    } else {
      throw new SchemaViolationException("This context only supports binding objects of it's own kind");
    } 
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException { rebind(this.myParser.parse(paramString), paramObject); }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException { doRebind(paramName, paramObject, null, this.alwaysUseFactory); }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { rebind(this.myParser.parse(paramString), paramObject, paramAttributes); }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException { doRebind(paramName, paramObject, paramAttributes, this.alwaysUseFactory); }
  
  protected void doRebind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean) throws NamingException {
    if (paramName.isEmpty())
      throw new InvalidNameException("Cannot rebind empty name"); 
    if (paramBoolean) {
      DirStateFactory.Result result = DirectoryManager.getStateToBind(paramObject, paramName, this, this.myEnv, paramAttributes);
      paramObject = result.getObject();
      paramAttributes = result.getAttributes();
    } 
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    hierMemDirCtx.doRebindAux(getLeafName(paramName), paramObject);
    if (paramAttributes != null && paramAttributes.size() > 0)
      modifyAttributes(paramName, 1, paramAttributes); 
  }
  
  protected void doRebindAux(Name paramName, Object paramObject) throws NamingException {
    if (this.readOnlyEx != null)
      throw (NamingException)this.readOnlyEx.fillInStackTrace(); 
    if (paramObject instanceof HierMemDirCtx) {
      this.bindings.put(paramName, paramObject);
    } else {
      throw new SchemaViolationException("This context only supports binding objects of it's own kind");
    } 
  }
  
  public void unbind(String paramString) throws NamingException { unbind(this.myParser.parse(paramString)); }
  
  public void unbind(Name paramName) throws NamingException {
    if (paramName.isEmpty())
      throw new InvalidNameException("Cannot unbind empty name"); 
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    hierMemDirCtx.doUnbind(getLeafName(paramName));
  }
  
  protected void doUnbind(Name paramName) throws NamingException {
    if (this.readOnlyEx != null)
      throw (NamingException)this.readOnlyEx.fillInStackTrace(); 
    this.bindings.remove(paramName);
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException { rename(this.myParser.parse(paramString1), this.myParser.parse(paramString2)); }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    if (paramName2.isEmpty() || paramName1.isEmpty())
      throw new InvalidNameException("Cannot rename empty name"); 
    if (!getInternalName(paramName2).equals(getInternalName(paramName1)))
      throw new InvalidNameException("Cannot rename across contexts"); 
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName2), false);
    hierMemDirCtx.doRename(getLeafName(paramName1), getLeafName(paramName2));
  }
  
  protected void doRename(Name paramName1, Name paramName2) throws NamingException {
    if (this.readOnlyEx != null)
      throw (NamingException)this.readOnlyEx.fillInStackTrace(); 
    paramName1 = canonizeName(paramName1);
    paramName2 = canonizeName(paramName2);
    if (this.bindings.get(paramName2) != null)
      throw new NameAlreadyBoundException(paramName2.toString()); 
    Object object = this.bindings.remove(paramName1);
    if (object == null)
      throw new NameNotFoundException(paramName1.toString()); 
    this.bindings.put(paramName2, object);
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException { return list(this.myParser.parse(paramString)); }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return hierMemDirCtx.doList();
  }
  
  protected NamingEnumeration<NameClassPair> doList() throws NamingException { return new FlatNames(this.bindings.keys()); }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException { return listBindings(this.myParser.parse(paramString)); }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return hierMemDirCtx.doListBindings(this.alwaysUseFactory);
  }
  
  protected NamingEnumeration<Binding> doListBindings(boolean paramBoolean) throws NamingException { return new FlatBindings(this.bindings, this.myEnv, paramBoolean); }
  
  public void destroySubcontext(String paramString) throws NamingException { destroySubcontext(this.myParser.parse(paramString)); }
  
  public void destroySubcontext(Name paramName) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    hierMemDirCtx.doDestroySubcontext(getLeafName(paramName));
  }
  
  protected void doDestroySubcontext(Name paramName) throws NamingException {
    if (this.readOnlyEx != null)
      throw (NamingException)this.readOnlyEx.fillInStackTrace(); 
    paramName = canonizeName(paramName);
    this.bindings.remove(paramName);
  }
  
  public Context createSubcontext(String paramString) throws NamingException { return createSubcontext(this.myParser.parse(paramString)); }
  
  public Context createSubcontext(Name paramName) throws NamingException { return createSubcontext(paramName, null); }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException { return createSubcontext(this.myParser.parse(paramString), paramAttributes); }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(getInternalName(paramName), false);
    return hierMemDirCtx.doCreateSubcontext(getLeafName(paramName), paramAttributes);
  }
  
  protected DirContext doCreateSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    if (this.readOnlyEx != null)
      throw (NamingException)this.readOnlyEx.fillInStackTrace(); 
    paramName = canonizeName(paramName);
    if (this.bindings.get(paramName) != null)
      throw new NameAlreadyBoundException(paramName.toString()); 
    HierMemDirCtx hierMemDirCtx = createNewCtx();
    this.bindings.put(paramName, hierMemDirCtx);
    if (paramAttributes != null)
      hierMemDirCtx.modifyAttributes("", 1, paramAttributes); 
    return hierMemDirCtx;
  }
  
  public Object lookupLink(String paramString) throws NamingException { return lookupLink(this.myParser.parse(paramString)); }
  
  public Object lookupLink(Name paramName) throws NamingException { return lookup(paramName); }
  
  public NameParser getNameParser(String paramString) throws NamingException { return this.myParser; }
  
  public NameParser getNameParser(Name paramName) throws NamingException { return this.myParser; }
  
  public String composeName(String paramString1, String paramString2) throws NamingException {
    Name name = composeName(new CompositeName(paramString1), new CompositeName(paramString2));
    return name.toString();
  }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    paramName1 = canonizeName(paramName1);
    paramName2 = canonizeName(paramName2);
    Name name = (Name)paramName2.clone();
    name.addAll(paramName1);
    return name;
  }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    this.myEnv = (this.myEnv == null) ? new Hashtable(11, 0.75F) : (Hashtable)this.myEnv.clone();
    return this.myEnv.put(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString) throws NamingException {
    if (this.myEnv == null)
      return null; 
    this.myEnv = (Hashtable)this.myEnv.clone();
    return this.myEnv.remove(paramString);
  }
  
  public Hashtable<String, Object> getEnvironment() throws NamingException { return (this.myEnv == null) ? new Hashtable(5, 0.75F) : (Hashtable)this.myEnv.clone(); }
  
  public Attributes getAttributes(String paramString) throws NamingException { return getAttributes(this.myParser.parse(paramString)); }
  
  public Attributes getAttributes(Name paramName) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return hierMemDirCtx.doGetAttributes();
  }
  
  protected Attributes doGetAttributes() throws NamingException { return (Attributes)this.attrs.clone(); }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException { return getAttributes(this.myParser.parse(paramString), paramArrayOfString); }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    return hierMemDirCtx.doGetAttributes(paramArrayOfString);
  }
  
  protected Attributes doGetAttributes(String[] paramArrayOfString) throws NamingException {
    if (paramArrayOfString == null)
      return doGetAttributes(); 
    BasicAttributes basicAttributes = new BasicAttributes(this.ignoreCase);
    Attribute attribute = null;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      attribute = this.attrs.get(paramArrayOfString[b]);
      if (attribute != null)
        basicAttributes.put(attribute); 
    } 
    return basicAttributes;
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException { modifyAttributes(this.myParser.parse(paramString), paramInt, paramAttributes); }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException {
    if (paramAttributes == null || paramAttributes.size() == 0)
      throw new IllegalArgumentException("Cannot modify without an attribute"); 
    NamingEnumeration namingEnumeration = paramAttributes.getAll();
    ModificationItem[] arrayOfModificationItem = new ModificationItem[paramAttributes.size()];
    for (byte b = 0; b < arrayOfModificationItem.length && namingEnumeration.hasMoreElements(); b++)
      arrayOfModificationItem[b] = new ModificationItem(paramInt, (Attribute)namingEnumeration.next()); 
    modifyAttributes(paramName, arrayOfModificationItem);
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException { modifyAttributes(this.myParser.parse(paramString), paramArrayOfModificationItem); }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    hierMemDirCtx.doModifyAttributes(paramArrayOfModificationItem);
  }
  
  protected void doModifyAttributes(ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    if (this.readOnlyEx != null)
      throw (NamingException)this.readOnlyEx.fillInStackTrace(); 
    applyMods(paramArrayOfModificationItem, this.attrs);
  }
  
  protected static Attributes applyMods(ModificationItem[] paramArrayOfModificationItem, Attributes paramAttributes) throws NamingException {
    for (byte b = 0; b < paramArrayOfModificationItem.length; b++) {
      NamingEnumeration namingEnumeration;
      Attribute attribute1;
      ModificationItem modificationItem = paramArrayOfModificationItem[b];
      Attribute attribute2 = modificationItem.getAttribute();
      switch (modificationItem.getModificationOp()) {
        case 1:
          attribute1 = paramAttributes.get(attribute2.getID());
          if (attribute1 == null) {
            paramAttributes.put((Attribute)attribute2.clone());
            break;
          } 
          namingEnumeration = attribute2.getAll();
          while (namingEnumeration.hasMore())
            attribute1.add(namingEnumeration.next()); 
          break;
        case 2:
          if (attribute2.size() == 0) {
            paramAttributes.remove(attribute2.getID());
            break;
          } 
          paramAttributes.put((Attribute)attribute2.clone());
          break;
        case 3:
          attribute1 = paramAttributes.get(attribute2.getID());
          if (attribute1 != null) {
            if (attribute2.size() == 0) {
              paramAttributes.remove(attribute2.getID());
              break;
            } 
            namingEnumeration = attribute2.getAll();
            while (namingEnumeration.hasMore())
              attribute1.remove(namingEnumeration.next()); 
            if (attribute1.size() == 0)
              paramAttributes.remove(attribute2.getID()); 
          } 
          break;
        default:
          throw new AttributeModificationException("Unknown mod_op");
      } 
    } 
    return paramAttributes;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException { return search(paramString, paramAttributes, null); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException { return search(paramName, paramAttributes, null); }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException { return search(this.myParser.parse(paramString), paramAttributes, paramArrayOfString); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)doLookup(paramName, false);
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningAttributes(paramArrayOfString);
    return new LazySearchEnumerationImpl(hierMemDirCtx.doListBindings(false), new ContainmentFilter(paramAttributes), searchControls, this, this.myEnv, false);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException {
    DirContext dirContext = (DirContext)doLookup(paramName, false);
    SearchFilter searchFilter = new SearchFilter(paramString);
    return new LazySearchEnumerationImpl(new HierContextEnumerator(this, dirContext, (paramSearchControls != null) ? paramSearchControls.getSearchScope() : 1), searchFilter, paramSearchControls, this, this.myEnv, this.alwaysUseFactory);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    String str = SearchFilter.format(paramString, paramArrayOfObject);
    return search(paramName, str, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException { return search(this.myParser.parse(paramString1), paramString2, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException { return search(this.myParser.parse(paramString1), paramString2, paramArrayOfObject, paramSearchControls); }
  
  protected HierMemDirCtx createNewCtx() throws NamingException { return new HierMemDirCtx(this.myEnv, this.ignoreCase); }
  
  protected Name canonizeName(Name paramName) throws NamingException {
    Name name = paramName;
    if (!(paramName instanceof HierarchicalName)) {
      name = new HierarchicalName();
      int i = paramName.size();
      for (byte b = 0; b < i; b++)
        name.add(b, paramName.get(b)); 
    } 
    return name;
  }
  
  protected Name getInternalName(Name paramName) throws NamingException { return paramName.getPrefix(paramName.size() - 1); }
  
  protected Name getLeafName(Name paramName) throws NamingException { return paramName.getSuffix(paramName.size() - 1); }
  
  public DirContext getSchema(String paramString) throws NamingException { throw new OperationNotSupportedException(); }
  
  public DirContext getSchema(Name paramName) throws NamingException { throw new OperationNotSupportedException(); }
  
  public DirContext getSchemaClassDefinition(String paramString) throws NamingException { throw new OperationNotSupportedException(); }
  
  public DirContext getSchemaClassDefinition(Name paramName) throws NamingException { throw new OperationNotSupportedException(); }
  
  public void setReadOnly(NamingException paramNamingException) { this.readOnlyEx = paramNamingException; }
  
  public void setIgnoreCase(boolean paramBoolean) { this.ignoreCase = paramBoolean; }
  
  public void setNameParser(NameParser paramNameParser) { this.myParser = paramNameParser; }
  
  private abstract class BaseFlatNames<T> extends Object implements NamingEnumeration<T> {
    Enumeration<Name> names;
    
    BaseFlatNames(Enumeration<Name> param1Enumeration) { this.names = param1Enumeration; }
    
    public final boolean hasMoreElements() {
      try {
        return hasMore();
      } catch (NamingException namingException) {
        return false;
      } 
    }
    
    public final boolean hasMore() { return this.names.hasMoreElements(); }
    
    public final T nextElement() {
      try {
        return (T)next();
      } catch (NamingException namingException) {
        throw new NoSuchElementException(namingException.toString());
      } 
    }
    
    public abstract T next();
    
    public final void close() throws NamingException { this.names = null; }
  }
  
  private final class FlatBindings extends BaseFlatNames<Binding> {
    private Hashtable<Name, Object> bds;
    
    private Hashtable<String, Object> env;
    
    private boolean useFactory;
    
    FlatBindings(Hashtable<Name, Object> param1Hashtable1, Hashtable<String, Object> param1Hashtable2, boolean param1Boolean) {
      super(HierMemDirCtx.this, param1Hashtable1.keys());
      this.env = param1Hashtable2;
      this.bds = param1Hashtable1;
      this.useFactory = param1Boolean;
    }
    
    public Binding next() throws NamingException {
      Name name = (Name)this.names.nextElement();
      HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)this.bds.get(name);
      Object object = hierMemDirCtx;
      if (this.useFactory) {
        Attributes attributes = hierMemDirCtx.getAttributes("");
        try {
          object = DirectoryManager.getObjectInstance(hierMemDirCtx, name, HierMemDirCtx.this, this.env, attributes);
        } catch (NamingException namingException) {
          throw namingException;
        } catch (Exception exception) {
          NamingException namingException = new NamingException("Problem calling getObjectInstance");
          namingException.setRootCause(exception);
          throw namingException;
        } 
      } 
      return new Binding(name.toString(), object);
    }
  }
  
  private final class FlatNames extends BaseFlatNames<NameClassPair> {
    FlatNames(Enumeration<Name> param1Enumeration) { super(HierMemDirCtx.this, param1Enumeration); }
    
    public NameClassPair next() throws NamingException {
      Name name = (Name)this.names.nextElement();
      String str = HierMemDirCtx.this.bindings.get(name).getClass().getName();
      return new NameClassPair(name.toString(), str);
    }
  }
  
  public class HierContextEnumerator extends ContextEnumerator {
    public HierContextEnumerator(Context param1Context, int param1Int) throws NamingException { super(param1Context, param1Int); }
    
    protected HierContextEnumerator(Context param1Context, int param1Int, String param1String, boolean param1Boolean) throws NamingException { super(param1Context, param1Int, param1String, param1Boolean); }
    
    protected NamingEnumeration<Binding> getImmediateChildren(Context param1Context) throws NamingException { return ((HierMemDirCtx)param1Context).doListBindings(false); }
    
    protected ContextEnumerator newEnumerator(Context param1Context, int param1Int, String param1String, boolean param1Boolean) throws NamingException { return new HierContextEnumerator(HierMemDirCtx.this, param1Context, param1Int, param1String, param1Boolean); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\dir\HierMemDirCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */