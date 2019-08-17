package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.dir.HierMemDirCtx;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;

final class LdapSchemaCtx extends HierMemDirCtx {
  private static final boolean debug = false;
  
  private static final int LEAF = 0;
  
  private static final int SCHEMA_ROOT = 1;
  
  static final int OBJECTCLASS_ROOT = 2;
  
  static final int ATTRIBUTE_ROOT = 3;
  
  static final int SYNTAX_ROOT = 4;
  
  static final int MATCHRULE_ROOT = 5;
  
  static final int OBJECTCLASS = 6;
  
  static final int ATTRIBUTE = 7;
  
  static final int SYNTAX = 8;
  
  static final int MATCHRULE = 9;
  
  private SchemaInfo info = null;
  
  private boolean setupMode = true;
  
  private int objectType;
  
  static DirContext createSchemaTree(Hashtable<String, Object> paramHashtable, String paramString, LdapCtx paramLdapCtx, Attributes paramAttributes, boolean paramBoolean) throws NamingException {
    try {
      LdapSchemaParser ldapSchemaParser = new LdapSchemaParser(paramBoolean);
      SchemaInfo schemaInfo = new SchemaInfo(paramString, paramLdapCtx, ldapSchemaParser);
      LdapSchemaCtx ldapSchemaCtx = new LdapSchemaCtx(1, paramHashtable, schemaInfo);
      LdapSchemaParser.LDAP2JNDISchema(paramAttributes, ldapSchemaCtx);
      return ldapSchemaCtx;
    } catch (NamingException namingException) {
      paramLdapCtx.close();
      throw namingException;
    } 
  }
  
  private LdapSchemaCtx(int paramInt, Hashtable<String, Object> paramHashtable, SchemaInfo paramSchemaInfo) {
    super(paramHashtable, true);
    this.objectType = paramInt;
    this.info = paramSchemaInfo;
  }
  
  public void close() throws NamingException { this.info.close(); }
  
  public final void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (!this.setupMode) {
      if (paramObject != null)
        throw new IllegalArgumentException("obj must be null"); 
      addServerSchema(paramAttributes);
    } 
    LdapSchemaCtx ldapSchemaCtx = (LdapSchemaCtx)super.doCreateSubcontext(paramName, paramAttributes);
  }
  
  protected final void doBind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean) throws NamingException {
    if (!this.setupMode)
      throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()"); 
    super.doBind(paramName, paramObject, paramAttributes, false);
  }
  
  public final void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    try {
      doLookup(paramName, false);
      throw new SchemaViolationException("Cannot replace existing schema object");
    } catch (NameNotFoundException nameNotFoundException) {
      bind(paramName, paramObject, paramAttributes);
      return;
    } 
  }
  
  protected final void doRebind(Name paramName, Object paramObject, Attributes paramAttributes, boolean paramBoolean) throws NamingException {
    if (!this.setupMode)
      throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()"); 
    super.doRebind(paramName, paramObject, paramAttributes, false);
  }
  
  protected final void doUnbind(Name paramName) throws NamingException {
    if (!this.setupMode)
      try {
        LdapSchemaCtx ldapSchemaCtx = (LdapSchemaCtx)doLookup(paramName, false);
        deleteServerSchema(ldapSchemaCtx.attrs);
      } catch (NameNotFoundException nameNotFoundException) {
        return;
      }  
    super.doUnbind(paramName);
  }
  
  protected final void doRename(Name paramName1, Name paramName2) throws NamingException {
    if (!this.setupMode)
      throw new SchemaViolationException("Cannot rename a schema object"); 
    super.doRename(paramName1, paramName2);
  }
  
  protected final void doDestroySubcontext(Name paramName) throws NamingException {
    if (!this.setupMode)
      try {
        LdapSchemaCtx ldapSchemaCtx = (LdapSchemaCtx)doLookup(paramName, false);
        deleteServerSchema(ldapSchemaCtx.attrs);
      } catch (NameNotFoundException nameNotFoundException) {
        return;
      }  
    super.doDestroySubcontext(paramName);
  }
  
  final LdapSchemaCtx setup(int paramInt, String paramString, Attributes paramAttributes) throws NamingException {
    try {
      this.setupMode = true;
      LdapSchemaCtx ldapSchemaCtx = (LdapSchemaCtx)super.doCreateSubcontext(new CompositeName(paramString), paramAttributes);
      ldapSchemaCtx.objectType = paramInt;
      ldapSchemaCtx.setupMode = false;
      return ldapSchemaCtx;
    } finally {
      this.setupMode = false;
    } 
  }
  
  protected final DirContext doCreateSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    if (paramAttributes == null || paramAttributes.size() == 0)
      throw new SchemaViolationException("Must supply attributes describing schema"); 
    if (!this.setupMode)
      addServerSchema(paramAttributes); 
    return (LdapSchemaCtx)super.doCreateSubcontext(paramName, paramAttributes);
  }
  
  private static final Attributes deepClone(Attributes paramAttributes) throws NamingException {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    NamingEnumeration namingEnumeration = paramAttributes.getAll();
    while (namingEnumeration.hasMore())
      basicAttributes.put((Attribute)((Attribute)namingEnumeration.next()).clone()); 
    return basicAttributes;
  }
  
  protected final void doModifyAttributes(ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    if (this.setupMode) {
      super.doModifyAttributes(paramArrayOfModificationItem);
    } else {
      Attributes attributes = deepClone(this.attrs);
      applyMods(paramArrayOfModificationItem, attributes);
      modifyServerSchema(this.attrs, attributes);
      this.attrs = attributes;
    } 
  }
  
  protected final HierMemDirCtx createNewCtx() { return new LdapSchemaCtx(0, this.myEnv, this.info); }
  
  private final void addServerSchema(Attributes paramAttributes) throws NamingException {
    Attribute attribute;
    switch (this.objectType) {
      case 2:
        attribute = this.info.parser.stringifyObjDesc(paramAttributes);
        break;
      case 3:
        attribute = this.info.parser.stringifyAttrDesc(paramAttributes);
        break;
      case 4:
        attribute = this.info.parser.stringifySyntaxDesc(paramAttributes);
        break;
      case 5:
        attribute = this.info.parser.stringifyMatchRuleDesc(paramAttributes);
        break;
      case 1:
        throw new SchemaViolationException("Cannot create new entry under schema root");
      default:
        throw new SchemaViolationException("Cannot create child of schema object");
    } 
    BasicAttributes basicAttributes = new BasicAttributes(true);
    basicAttributes.put(attribute);
    this.info.modifyAttributes(this.myEnv, 1, basicAttributes);
  }
  
  private final void deleteServerSchema(Attributes paramAttributes) throws NamingException {
    Attribute attribute;
    switch (this.objectType) {
      case 2:
        attribute = this.info.parser.stringifyObjDesc(paramAttributes);
        break;
      case 3:
        attribute = this.info.parser.stringifyAttrDesc(paramAttributes);
        break;
      case 4:
        attribute = this.info.parser.stringifySyntaxDesc(paramAttributes);
        break;
      case 5:
        attribute = this.info.parser.stringifyMatchRuleDesc(paramAttributes);
        break;
      case 1:
        throw new SchemaViolationException("Cannot delete schema root");
      default:
        throw new SchemaViolationException("Cannot delete child of schema object");
    } 
    ModificationItem[] arrayOfModificationItem = new ModificationItem[1];
    arrayOfModificationItem[0] = new ModificationItem(3, attribute);
    this.info.modifyAttributes(this.myEnv, arrayOfModificationItem);
  }
  
  private final void modifyServerSchema(Attributes paramAttributes1, Attributes paramAttributes2) throws NamingException {
    Attribute attribute2;
    Attribute attribute1;
    switch (this.objectType) {
      case 6:
        attribute2 = this.info.parser.stringifyObjDesc(paramAttributes1);
        attribute1 = this.info.parser.stringifyObjDesc(paramAttributes2);
        break;
      case 7:
        attribute2 = this.info.parser.stringifyAttrDesc(paramAttributes1);
        attribute1 = this.info.parser.stringifyAttrDesc(paramAttributes2);
        break;
      case 8:
        attribute2 = this.info.parser.stringifySyntaxDesc(paramAttributes1);
        attribute1 = this.info.parser.stringifySyntaxDesc(paramAttributes2);
        break;
      case 9:
        attribute2 = this.info.parser.stringifyMatchRuleDesc(paramAttributes1);
        attribute1 = this.info.parser.stringifyMatchRuleDesc(paramAttributes2);
        break;
      default:
        throw new SchemaViolationException("Cannot modify schema root");
    } 
    ModificationItem[] arrayOfModificationItem = new ModificationItem[2];
    arrayOfModificationItem[0] = new ModificationItem(3, attribute2);
    arrayOfModificationItem[1] = new ModificationItem(1, attribute1);
    this.info.modifyAttributes(this.myEnv, arrayOfModificationItem);
  }
  
  private static final class SchemaInfo {
    private LdapCtx schemaEntry;
    
    private String schemaEntryName;
    
    LdapSchemaParser parser;
    
    private String host;
    
    private int port;
    
    private boolean hasLdapsScheme;
    
    SchemaInfo(String param1String, LdapCtx param1LdapCtx, LdapSchemaParser param1LdapSchemaParser) {
      this.schemaEntryName = param1String;
      this.schemaEntry = param1LdapCtx;
      this.parser = param1LdapSchemaParser;
      this.port = param1LdapCtx.port_number;
      this.host = param1LdapCtx.hostname;
      this.hasLdapsScheme = param1LdapCtx.hasLdapsScheme;
    }
    
    void close() throws NamingException {
      if (this.schemaEntry != null) {
        this.schemaEntry.close();
        this.schemaEntry = null;
      } 
    }
    
    private LdapCtx reopenEntry(Hashtable<?, ?> param1Hashtable) throws NamingException { return new LdapCtx(this.schemaEntryName, this.host, this.port, param1Hashtable, this.hasLdapsScheme); }
    
    void modifyAttributes(Hashtable<?, ?> param1Hashtable, ModificationItem[] param1ArrayOfModificationItem) throws NamingException {
      if (this.schemaEntry == null)
        this.schemaEntry = reopenEntry(param1Hashtable); 
      this.schemaEntry.modifyAttributes("", param1ArrayOfModificationItem);
    }
    
    void modifyAttributes(Hashtable<?, ?> param1Hashtable, int param1Int, Attributes param1Attributes) throws NamingException {
      if (this.schemaEntry == null)
        this.schemaEntry = reopenEntry(param1Hashtable); 
      this.schemaEntry.modifyAttributes("", param1Int, param1Attributes);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapSchemaCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */