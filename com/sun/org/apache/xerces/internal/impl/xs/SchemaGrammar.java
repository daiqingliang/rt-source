package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.util.ObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.lang.ref.SoftReference;
import java.util.Vector;
import org.xml.sax.SAXException;

public class SchemaGrammar implements XSGrammar, XSNamespaceItem {
  String fTargetNamespace;
  
  SymbolHash fGlobalAttrDecls;
  
  SymbolHash fGlobalAttrGrpDecls;
  
  SymbolHash fGlobalElemDecls;
  
  SymbolHash fGlobalGroupDecls;
  
  SymbolHash fGlobalNotationDecls;
  
  SymbolHash fGlobalIDConstraintDecls;
  
  SymbolHash fGlobalTypeDecls;
  
  SymbolHash fGlobalAttrDeclsExt;
  
  SymbolHash fGlobalAttrGrpDeclsExt;
  
  SymbolHash fGlobalElemDeclsExt;
  
  SymbolHash fGlobalGroupDeclsExt;
  
  SymbolHash fGlobalNotationDeclsExt;
  
  SymbolHash fGlobalIDConstraintDeclsExt;
  
  SymbolHash fGlobalTypeDeclsExt;
  
  SymbolHash fAllGlobalElemDecls;
  
  XSDDescription fGrammarDescription = null;
  
  XSAnnotationImpl[] fAnnotations = null;
  
  int fNumAnnotations;
  
  private SymbolTable fSymbolTable = null;
  
  private SoftReference fSAXParser = null;
  
  private SoftReference fDOMParser = null;
  
  private boolean fIsImmutable = false;
  
  private static final int BASICSET_COUNT = 29;
  
  private static final int FULLSET_COUNT = 46;
  
  private static final int GRAMMAR_XS = 1;
  
  private static final int GRAMMAR_XSI = 2;
  
  Vector fImported = null;
  
  private static final int INITIAL_SIZE = 16;
  
  private static final int INC_SIZE = 16;
  
  private int fCTCount = 0;
  
  private XSComplexTypeDecl[] fComplexTypeDecls = new XSComplexTypeDecl[16];
  
  private SimpleLocator[] fCTLocators = new SimpleLocator[16];
  
  private static final int REDEFINED_GROUP_INIT_SIZE = 2;
  
  private int fRGCount = 0;
  
  private XSGroupDecl[] fRedefinedGroupDecls = new XSGroupDecl[2];
  
  private SimpleLocator[] fRGLocators = new SimpleLocator[1];
  
  boolean fFullChecked = false;
  
  private int fSubGroupCount = 0;
  
  private XSElementDecl[] fSubGroups = new XSElementDecl[16];
  
  public static final XSComplexTypeDecl fAnyType = new XSAnyType();
  
  public static final BuiltinSchemaGrammar SG_SchemaNS = new BuiltinSchemaGrammar(1, (short)1);
  
  private static final BuiltinSchemaGrammar SG_SchemaNSExtended = new BuiltinSchemaGrammar(1, (short)2);
  
  public static final XSSimpleType fAnySimpleType = (XSSimpleType)SG_SchemaNS.getGlobalTypeDecl("anySimpleType");
  
  public static final BuiltinSchemaGrammar SG_XSI = new BuiltinSchemaGrammar(2, (short)1);
  
  private static final short MAX_COMP_IDX = 16;
  
  private static final boolean[] GLOBAL_COMP = { 
      false, true, true, true, false, true, true, false, false, false, 
      false, true, false, false, false, true, true };
  
  private XSNamedMap[] fComponents = null;
  
  private ObjectList[] fComponentsExt = null;
  
  private Vector fDocuments = null;
  
  private Vector fLocations = null;
  
  protected SchemaGrammar() {}
  
  public SchemaGrammar(String paramString, XSDDescription paramXSDDescription, SymbolTable paramSymbolTable) {
    this.fTargetNamespace = paramString;
    this.fGrammarDescription = paramXSDDescription;
    this.fSymbolTable = paramSymbolTable;
    this.fGlobalAttrDecls = new SymbolHash();
    this.fGlobalAttrGrpDecls = new SymbolHash();
    this.fGlobalElemDecls = new SymbolHash();
    this.fGlobalGroupDecls = new SymbolHash();
    this.fGlobalNotationDecls = new SymbolHash();
    this.fGlobalIDConstraintDecls = new SymbolHash();
    this.fGlobalAttrDeclsExt = new SymbolHash();
    this.fGlobalAttrGrpDeclsExt = new SymbolHash();
    this.fGlobalElemDeclsExt = new SymbolHash();
    this.fGlobalGroupDeclsExt = new SymbolHash();
    this.fGlobalNotationDeclsExt = new SymbolHash();
    this.fGlobalIDConstraintDeclsExt = new SymbolHash();
    this.fGlobalTypeDeclsExt = new SymbolHash();
    this.fAllGlobalElemDecls = new SymbolHash();
    if (this.fTargetNamespace == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
      this.fGlobalTypeDecls = SG_SchemaNS.fGlobalTypeDecls.makeClone();
    } else {
      this.fGlobalTypeDecls = new SymbolHash();
    } 
  }
  
  public SchemaGrammar(SchemaGrammar paramSchemaGrammar) {
    this.fTargetNamespace = paramSchemaGrammar.fTargetNamespace;
    this.fGrammarDescription = paramSchemaGrammar.fGrammarDescription.makeClone();
    this.fSymbolTable = paramSchemaGrammar.fSymbolTable;
    this.fGlobalAttrDecls = paramSchemaGrammar.fGlobalAttrDecls.makeClone();
    this.fGlobalAttrGrpDecls = paramSchemaGrammar.fGlobalAttrGrpDecls.makeClone();
    this.fGlobalElemDecls = paramSchemaGrammar.fGlobalElemDecls.makeClone();
    this.fGlobalGroupDecls = paramSchemaGrammar.fGlobalGroupDecls.makeClone();
    this.fGlobalNotationDecls = paramSchemaGrammar.fGlobalNotationDecls.makeClone();
    this.fGlobalIDConstraintDecls = paramSchemaGrammar.fGlobalIDConstraintDecls.makeClone();
    this.fGlobalTypeDecls = paramSchemaGrammar.fGlobalTypeDecls.makeClone();
    this.fGlobalAttrDeclsExt = paramSchemaGrammar.fGlobalAttrDeclsExt.makeClone();
    this.fGlobalAttrGrpDeclsExt = paramSchemaGrammar.fGlobalAttrGrpDeclsExt.makeClone();
    this.fGlobalElemDeclsExt = paramSchemaGrammar.fGlobalElemDeclsExt.makeClone();
    this.fGlobalGroupDeclsExt = paramSchemaGrammar.fGlobalGroupDeclsExt.makeClone();
    this.fGlobalNotationDeclsExt = paramSchemaGrammar.fGlobalNotationDeclsExt.makeClone();
    this.fGlobalIDConstraintDeclsExt = paramSchemaGrammar.fGlobalIDConstraintDeclsExt.makeClone();
    this.fGlobalTypeDeclsExt = paramSchemaGrammar.fGlobalTypeDeclsExt.makeClone();
    this.fAllGlobalElemDecls = paramSchemaGrammar.fAllGlobalElemDecls.makeClone();
    this.fNumAnnotations = paramSchemaGrammar.fNumAnnotations;
    if (this.fNumAnnotations > 0) {
      this.fAnnotations = new XSAnnotationImpl[paramSchemaGrammar.fAnnotations.length];
      System.arraycopy(paramSchemaGrammar.fAnnotations, 0, this.fAnnotations, 0, this.fNumAnnotations);
    } 
    this.fSubGroupCount = paramSchemaGrammar.fSubGroupCount;
    if (this.fSubGroupCount > 0) {
      this.fSubGroups = new XSElementDecl[paramSchemaGrammar.fSubGroups.length];
      System.arraycopy(paramSchemaGrammar.fSubGroups, 0, this.fSubGroups, 0, this.fSubGroupCount);
    } 
    this.fCTCount = paramSchemaGrammar.fCTCount;
    if (this.fCTCount > 0) {
      this.fComplexTypeDecls = new XSComplexTypeDecl[paramSchemaGrammar.fComplexTypeDecls.length];
      this.fCTLocators = new SimpleLocator[paramSchemaGrammar.fCTLocators.length];
      System.arraycopy(paramSchemaGrammar.fComplexTypeDecls, 0, this.fComplexTypeDecls, 0, this.fCTCount);
      System.arraycopy(paramSchemaGrammar.fCTLocators, 0, this.fCTLocators, 0, this.fCTCount);
    } 
    this.fRGCount = paramSchemaGrammar.fRGCount;
    if (this.fRGCount > 0) {
      this.fRedefinedGroupDecls = new XSGroupDecl[paramSchemaGrammar.fRedefinedGroupDecls.length];
      this.fRGLocators = new SimpleLocator[paramSchemaGrammar.fRGLocators.length];
      System.arraycopy(paramSchemaGrammar.fRedefinedGroupDecls, 0, this.fRedefinedGroupDecls, 0, this.fRGCount);
      System.arraycopy(paramSchemaGrammar.fRGLocators, 0, this.fRGLocators, 0, this.fRGCount);
    } 
    if (paramSchemaGrammar.fImported != null) {
      this.fImported = new Vector();
      for (byte b = 0; b < paramSchemaGrammar.fImported.size(); b++)
        this.fImported.add(paramSchemaGrammar.fImported.elementAt(b)); 
    } 
    if (paramSchemaGrammar.fLocations != null)
      for (byte b = 0; b < paramSchemaGrammar.fLocations.size(); b++)
        addDocument(null, (String)paramSchemaGrammar.fLocations.elementAt(b));  
  }
  
  public XMLGrammarDescription getGrammarDescription() { return this.fGrammarDescription; }
  
  public boolean isNamespaceAware() { return true; }
  
  public void setImportedGrammars(Vector paramVector) { this.fImported = paramVector; }
  
  public Vector getImportedGrammars() { return this.fImported; }
  
  public final String getTargetNamespace() { return this.fTargetNamespace; }
  
  public void addGlobalAttributeDecl(XSAttributeDecl paramXSAttributeDecl) {
    this.fGlobalAttrDecls.put(paramXSAttributeDecl.fName, paramXSAttributeDecl);
    paramXSAttributeDecl.setNamespaceItem(this);
  }
  
  public void addGlobalAttributeDecl(XSAttributeDecl paramXSAttributeDecl, String paramString) {
    this.fGlobalAttrDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSAttributeDecl.fName, paramXSAttributeDecl);
    if (paramXSAttributeDecl.getNamespaceItem() == null)
      paramXSAttributeDecl.setNamespaceItem(this); 
  }
  
  public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl paramXSAttributeGroupDecl) {
    this.fGlobalAttrGrpDecls.put(paramXSAttributeGroupDecl.fName, paramXSAttributeGroupDecl);
    paramXSAttributeGroupDecl.setNamespaceItem(this);
  }
  
  public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl paramXSAttributeGroupDecl, String paramString) {
    this.fGlobalAttrGrpDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSAttributeGroupDecl.fName, paramXSAttributeGroupDecl);
    if (paramXSAttributeGroupDecl.getNamespaceItem() == null)
      paramXSAttributeGroupDecl.setNamespaceItem(this); 
  }
  
  public void addGlobalElementDeclAll(XSElementDecl paramXSElementDecl) {
    if (this.fAllGlobalElemDecls.get(paramXSElementDecl) == null) {
      this.fAllGlobalElemDecls.put(paramXSElementDecl, paramXSElementDecl);
      if (paramXSElementDecl.fSubGroup != null) {
        if (this.fSubGroupCount == this.fSubGroups.length)
          this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount + 16); 
        this.fSubGroups[this.fSubGroupCount++] = paramXSElementDecl;
      } 
    } 
  }
  
  public void addGlobalElementDecl(XSElementDecl paramXSElementDecl) {
    this.fGlobalElemDecls.put(paramXSElementDecl.fName, paramXSElementDecl);
    paramXSElementDecl.setNamespaceItem(this);
  }
  
  public void addGlobalElementDecl(XSElementDecl paramXSElementDecl, String paramString) {
    this.fGlobalElemDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSElementDecl.fName, paramXSElementDecl);
    if (paramXSElementDecl.getNamespaceItem() == null)
      paramXSElementDecl.setNamespaceItem(this); 
  }
  
  public void addGlobalGroupDecl(XSGroupDecl paramXSGroupDecl) {
    this.fGlobalGroupDecls.put(paramXSGroupDecl.fName, paramXSGroupDecl);
    paramXSGroupDecl.setNamespaceItem(this);
  }
  
  public void addGlobalGroupDecl(XSGroupDecl paramXSGroupDecl, String paramString) {
    this.fGlobalGroupDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSGroupDecl.fName, paramXSGroupDecl);
    if (paramXSGroupDecl.getNamespaceItem() == null)
      paramXSGroupDecl.setNamespaceItem(this); 
  }
  
  public void addGlobalNotationDecl(XSNotationDecl paramXSNotationDecl) {
    this.fGlobalNotationDecls.put(paramXSNotationDecl.fName, paramXSNotationDecl);
    paramXSNotationDecl.setNamespaceItem(this);
  }
  
  public void addGlobalNotationDecl(XSNotationDecl paramXSNotationDecl, String paramString) {
    this.fGlobalNotationDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSNotationDecl.fName, paramXSNotationDecl);
    if (paramXSNotationDecl.getNamespaceItem() == null)
      paramXSNotationDecl.setNamespaceItem(this); 
  }
  
  public void addGlobalTypeDecl(XSTypeDefinition paramXSTypeDefinition) {
    this.fGlobalTypeDecls.put(paramXSTypeDefinition.getName(), paramXSTypeDefinition);
    if (paramXSTypeDefinition instanceof XSComplexTypeDecl) {
      ((XSComplexTypeDecl)paramXSTypeDefinition).setNamespaceItem(this);
    } else if (paramXSTypeDefinition instanceof XSSimpleTypeDecl) {
      ((XSSimpleTypeDecl)paramXSTypeDefinition).setNamespaceItem(this);
    } 
  }
  
  public void addGlobalTypeDecl(XSTypeDefinition paramXSTypeDefinition, String paramString) {
    this.fGlobalTypeDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSTypeDefinition.getName(), paramXSTypeDefinition);
    if (paramXSTypeDefinition.getNamespaceItem() == null)
      if (paramXSTypeDefinition instanceof XSComplexTypeDecl) {
        ((XSComplexTypeDecl)paramXSTypeDefinition).setNamespaceItem(this);
      } else if (paramXSTypeDefinition instanceof XSSimpleTypeDecl) {
        ((XSSimpleTypeDecl)paramXSTypeDefinition).setNamespaceItem(this);
      }  
  }
  
  public void addGlobalComplexTypeDecl(XSComplexTypeDecl paramXSComplexTypeDecl) {
    this.fGlobalTypeDecls.put(paramXSComplexTypeDecl.getName(), paramXSComplexTypeDecl);
    paramXSComplexTypeDecl.setNamespaceItem(this);
  }
  
  public void addGlobalComplexTypeDecl(XSComplexTypeDecl paramXSComplexTypeDecl, String paramString) {
    this.fGlobalTypeDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSComplexTypeDecl.getName(), paramXSComplexTypeDecl);
    if (paramXSComplexTypeDecl.getNamespaceItem() == null)
      paramXSComplexTypeDecl.setNamespaceItem(this); 
  }
  
  public void addGlobalSimpleTypeDecl(XSSimpleType paramXSSimpleType) {
    this.fGlobalTypeDecls.put(paramXSSimpleType.getName(), paramXSSimpleType);
    if (paramXSSimpleType instanceof XSSimpleTypeDecl)
      ((XSSimpleTypeDecl)paramXSSimpleType).setNamespaceItem(this); 
  }
  
  public void addGlobalSimpleTypeDecl(XSSimpleType paramXSSimpleType, String paramString) {
    this.fGlobalTypeDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramXSSimpleType.getName(), paramXSSimpleType);
    if (paramXSSimpleType.getNamespaceItem() == null && paramXSSimpleType instanceof XSSimpleTypeDecl)
      ((XSSimpleTypeDecl)paramXSSimpleType).setNamespaceItem(this); 
  }
  
  public final void addIDConstraintDecl(XSElementDecl paramXSElementDecl, IdentityConstraint paramIdentityConstraint) {
    paramXSElementDecl.addIDConstraint(paramIdentityConstraint);
    this.fGlobalIDConstraintDecls.put(paramIdentityConstraint.getIdentityConstraintName(), paramIdentityConstraint);
  }
  
  public final void addIDConstraintDecl(XSElementDecl paramXSElementDecl, IdentityConstraint paramIdentityConstraint, String paramString) { this.fGlobalIDConstraintDeclsExt.put(((paramString != null) ? paramString : "") + "," + paramIdentityConstraint.getIdentityConstraintName(), paramIdentityConstraint); }
  
  public final XSAttributeDecl getGlobalAttributeDecl(String paramString) { return (XSAttributeDecl)this.fGlobalAttrDecls.get(paramString); }
  
  public final XSAttributeDecl getGlobalAttributeDecl(String paramString1, String paramString2) { return (XSAttributeDecl)this.fGlobalAttrDeclsExt.get(((paramString2 != null) ? paramString2 : "") + "," + paramString1); }
  
  public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String paramString) { return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(paramString); }
  
  public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String paramString1, String paramString2) { return (XSAttributeGroupDecl)this.fGlobalAttrGrpDeclsExt.get(((paramString2 != null) ? paramString2 : "") + "," + paramString1); }
  
  public final XSElementDecl getGlobalElementDecl(String paramString) { return (XSElementDecl)this.fGlobalElemDecls.get(paramString); }
  
  public final XSElementDecl getGlobalElementDecl(String paramString1, String paramString2) { return (XSElementDecl)this.fGlobalElemDeclsExt.get(((paramString2 != null) ? paramString2 : "") + "," + paramString1); }
  
  public final XSGroupDecl getGlobalGroupDecl(String paramString) { return (XSGroupDecl)this.fGlobalGroupDecls.get(paramString); }
  
  public final XSGroupDecl getGlobalGroupDecl(String paramString1, String paramString2) { return (XSGroupDecl)this.fGlobalGroupDeclsExt.get(((paramString2 != null) ? paramString2 : "") + "," + paramString1); }
  
  public final XSNotationDecl getGlobalNotationDecl(String paramString) { return (XSNotationDecl)this.fGlobalNotationDecls.get(paramString); }
  
  public final XSNotationDecl getGlobalNotationDecl(String paramString1, String paramString2) { return (XSNotationDecl)this.fGlobalNotationDeclsExt.get(((paramString2 != null) ? paramString2 : "") + "," + paramString1); }
  
  public final XSTypeDefinition getGlobalTypeDecl(String paramString) { return (XSTypeDefinition)this.fGlobalTypeDecls.get(paramString); }
  
  public final XSTypeDefinition getGlobalTypeDecl(String paramString1, String paramString2) { return (XSTypeDefinition)this.fGlobalTypeDeclsExt.get(((paramString2 != null) ? paramString2 : "") + "," + paramString1); }
  
  public final IdentityConstraint getIDConstraintDecl(String paramString) { return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(paramString); }
  
  public final IdentityConstraint getIDConstraintDecl(String paramString1, String paramString2) { return (IdentityConstraint)this.fGlobalIDConstraintDeclsExt.get(((paramString2 != null) ? paramString2 : "") + "," + paramString1); }
  
  public final boolean hasIDConstraints() { return (this.fGlobalIDConstraintDecls.getLength() > 0); }
  
  public void addComplexTypeDecl(XSComplexTypeDecl paramXSComplexTypeDecl, SimpleLocator paramSimpleLocator) {
    if (this.fCTCount == this.fComplexTypeDecls.length) {
      this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount + 16);
      this.fCTLocators = resize(this.fCTLocators, this.fCTCount + 16);
    } 
    this.fCTLocators[this.fCTCount] = paramSimpleLocator;
    this.fComplexTypeDecls[this.fCTCount++] = paramXSComplexTypeDecl;
  }
  
  public void addRedefinedGroupDecl(XSGroupDecl paramXSGroupDecl1, XSGroupDecl paramXSGroupDecl2, SimpleLocator paramSimpleLocator) {
    if (this.fRGCount == this.fRedefinedGroupDecls.length) {
      this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount << 1);
      this.fRGLocators = resize(this.fRGLocators, this.fRGCount);
    } 
    this.fRGLocators[this.fRGCount / 2] = paramSimpleLocator;
    this.fRedefinedGroupDecls[this.fRGCount++] = paramXSGroupDecl1;
    this.fRedefinedGroupDecls[this.fRGCount++] = paramXSGroupDecl2;
  }
  
  final XSComplexTypeDecl[] getUncheckedComplexTypeDecls() {
    if (this.fCTCount < this.fComplexTypeDecls.length) {
      this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
      this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
    } 
    return this.fComplexTypeDecls;
  }
  
  final SimpleLocator[] getUncheckedCTLocators() {
    if (this.fCTCount < this.fCTLocators.length) {
      this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
      this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
    } 
    return this.fCTLocators;
  }
  
  final XSGroupDecl[] getRedefinedGroupDecls() {
    if (this.fRGCount < this.fRedefinedGroupDecls.length) {
      this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount);
      this.fRGLocators = resize(this.fRGLocators, this.fRGCount / 2);
    } 
    return this.fRedefinedGroupDecls;
  }
  
  final SimpleLocator[] getRGLocators() {
    if (this.fRGCount < this.fRedefinedGroupDecls.length) {
      this.fRedefinedGroupDecls = resize(this.fRedefinedGroupDecls, this.fRGCount);
      this.fRGLocators = resize(this.fRGLocators, this.fRGCount / 2);
    } 
    return this.fRGLocators;
  }
  
  final void setUncheckedTypeNum(int paramInt) {
    this.fCTCount = paramInt;
    this.fComplexTypeDecls = resize(this.fComplexTypeDecls, this.fCTCount);
    this.fCTLocators = resize(this.fCTLocators, this.fCTCount);
  }
  
  final XSElementDecl[] getSubstitutionGroups() {
    if (this.fSubGroupCount < this.fSubGroups.length)
      this.fSubGroups = resize(this.fSubGroups, this.fSubGroupCount); 
    return this.fSubGroups;
  }
  
  public static SchemaGrammar getS4SGrammar(short paramShort) { return (paramShort == 1) ? SG_SchemaNS : SG_SchemaNSExtended; }
  
  static final XSComplexTypeDecl[] resize(XSComplexTypeDecl[] paramArrayOfXSComplexTypeDecl, int paramInt) {
    XSComplexTypeDecl[] arrayOfXSComplexTypeDecl = new XSComplexTypeDecl[paramInt];
    System.arraycopy(paramArrayOfXSComplexTypeDecl, 0, arrayOfXSComplexTypeDecl, 0, Math.min(paramArrayOfXSComplexTypeDecl.length, paramInt));
    return arrayOfXSComplexTypeDecl;
  }
  
  static final XSGroupDecl[] resize(XSGroupDecl[] paramArrayOfXSGroupDecl, int paramInt) {
    XSGroupDecl[] arrayOfXSGroupDecl = new XSGroupDecl[paramInt];
    System.arraycopy(paramArrayOfXSGroupDecl, 0, arrayOfXSGroupDecl, 0, Math.min(paramArrayOfXSGroupDecl.length, paramInt));
    return arrayOfXSGroupDecl;
  }
  
  static final XSElementDecl[] resize(XSElementDecl[] paramArrayOfXSElementDecl, int paramInt) {
    XSElementDecl[] arrayOfXSElementDecl = new XSElementDecl[paramInt];
    System.arraycopy(paramArrayOfXSElementDecl, 0, arrayOfXSElementDecl, 0, Math.min(paramArrayOfXSElementDecl.length, paramInt));
    return arrayOfXSElementDecl;
  }
  
  static final SimpleLocator[] resize(SimpleLocator[] paramArrayOfSimpleLocator, int paramInt) {
    SimpleLocator[] arrayOfSimpleLocator = new SimpleLocator[paramInt];
    System.arraycopy(paramArrayOfSimpleLocator, 0, arrayOfSimpleLocator, 0, Math.min(paramArrayOfSimpleLocator.length, paramInt));
    return arrayOfSimpleLocator;
  }
  
  public void addDocument(Object paramObject, String paramString) {
    if (this.fDocuments == null) {
      this.fDocuments = new Vector();
      this.fLocations = new Vector();
    } 
    this.fDocuments.addElement(paramObject);
    this.fLocations.addElement(paramString);
  }
  
  public void removeDocument(int paramInt) {
    if (this.fDocuments != null && paramInt >= 0 && paramInt < this.fDocuments.size()) {
      this.fDocuments.removeElementAt(paramInt);
      this.fLocations.removeElementAt(paramInt);
    } 
  }
  
  public String getSchemaNamespace() { return this.fTargetNamespace; }
  
  DOMParser getDOMParser() {
    if (this.fDOMParser != null) {
      DOMParser dOMParser1 = (DOMParser)this.fDOMParser.get();
      if (dOMParser1 != null)
        return dOMParser1; 
    } 
    XML11Configuration xML11Configuration = new XML11Configuration(this.fSymbolTable);
    xML11Configuration.setFeature("http://xml.org/sax/features/namespaces", true);
    xML11Configuration.setFeature("http://xml.org/sax/features/validation", false);
    DOMParser dOMParser = new DOMParser(xML11Configuration);
    try {
      dOMParser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
    } catch (SAXException sAXException) {}
    this.fDOMParser = new SoftReference(dOMParser);
    return dOMParser;
  }
  
  SAXParser getSAXParser() {
    if (this.fSAXParser != null) {
      SAXParser sAXParser1 = (SAXParser)this.fSAXParser.get();
      if (sAXParser1 != null)
        return sAXParser1; 
    } 
    XML11Configuration xML11Configuration = new XML11Configuration(this.fSymbolTable);
    xML11Configuration.setFeature("http://xml.org/sax/features/namespaces", true);
    xML11Configuration.setFeature("http://xml.org/sax/features/validation", false);
    SAXParser sAXParser = new SAXParser(xML11Configuration);
    this.fSAXParser = new SoftReference(sAXParser);
    return sAXParser;
  }
  
  public XSNamedMap getComponents(short paramShort) {
    if (paramShort <= 0 || paramShort > 16 || !GLOBAL_COMP[paramShort])
      return XSNamedMapImpl.EMPTY_MAP; 
    if (this.fComponents == null)
      this.fComponents = new XSNamedMap[17]; 
    if (this.fComponents[paramShort] == null) {
      SymbolHash symbolHash = null;
      switch (paramShort) {
        case 3:
        case 15:
        case 16:
          symbolHash = this.fGlobalTypeDecls;
          break;
        case 1:
          symbolHash = this.fGlobalAttrDecls;
          break;
        case 2:
          symbolHash = this.fGlobalElemDecls;
          break;
        case 5:
          symbolHash = this.fGlobalAttrGrpDecls;
          break;
        case 6:
          symbolHash = this.fGlobalGroupDecls;
          break;
        case 11:
          symbolHash = this.fGlobalNotationDecls;
          break;
      } 
      if (paramShort == 15 || paramShort == 16) {
        this.fComponents[paramShort] = new XSNamedMap4Types(this.fTargetNamespace, symbolHash, paramShort);
      } else {
        this.fComponents[paramShort] = new XSNamedMapImpl(this.fTargetNamespace, symbolHash);
      } 
    } 
    return this.fComponents[paramShort];
  }
  
  public ObjectList getComponentsExt(short paramShort) {
    if (paramShort <= 0 || paramShort > 16 || !GLOBAL_COMP[paramShort])
      return ObjectListImpl.EMPTY_LIST; 
    if (this.fComponentsExt == null)
      this.fComponentsExt = new ObjectList[17]; 
    if (this.fComponentsExt[paramShort] == null) {
      SymbolHash symbolHash = null;
      switch (paramShort) {
        case 3:
        case 15:
        case 16:
          symbolHash = this.fGlobalTypeDeclsExt;
          break;
        case 1:
          symbolHash = this.fGlobalAttrDeclsExt;
          break;
        case 2:
          symbolHash = this.fGlobalElemDeclsExt;
          break;
        case 5:
          symbolHash = this.fGlobalAttrGrpDeclsExt;
          break;
        case 6:
          symbolHash = this.fGlobalGroupDeclsExt;
          break;
        case 11:
          symbolHash = this.fGlobalNotationDeclsExt;
          break;
      } 
      Object[] arrayOfObject = symbolHash.getEntries();
      this.fComponentsExt[paramShort] = new ObjectListImpl(arrayOfObject, arrayOfObject.length);
    } 
    return this.fComponentsExt[paramShort];
  }
  
  public void resetComponents() {
    this.fComponents = null;
    this.fComponentsExt = null;
  }
  
  public XSTypeDefinition getTypeDefinition(String paramString) { return getGlobalTypeDecl(paramString); }
  
  public XSAttributeDeclaration getAttributeDeclaration(String paramString) { return getGlobalAttributeDecl(paramString); }
  
  public XSElementDeclaration getElementDeclaration(String paramString) { return getGlobalElementDecl(paramString); }
  
  public XSAttributeGroupDefinition getAttributeGroup(String paramString) { return getGlobalAttributeGroupDecl(paramString); }
  
  public XSModelGroupDefinition getModelGroupDefinition(String paramString) { return getGlobalGroupDecl(paramString); }
  
  public XSNotationDeclaration getNotationDeclaration(String paramString) { return getGlobalNotationDecl(paramString); }
  
  public StringList getDocumentLocations() { return new StringListImpl(this.fLocations); }
  
  public XSModel toXSModel() { return new XSModelImpl(new SchemaGrammar[] { this }); }
  
  public XSModel toXSModel(XSGrammar[] paramArrayOfXSGrammar) {
    if (paramArrayOfXSGrammar == null || paramArrayOfXSGrammar.length == 0)
      return toXSModel(); 
    int i = paramArrayOfXSGrammar.length;
    boolean bool = false;
    for (byte b1 = 0; b1 < i; b1++) {
      if (paramArrayOfXSGrammar[b1] == this) {
        bool = true;
        break;
      } 
    } 
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[bool ? i : (i + 1)];
    for (byte b2 = 0; b2 < i; b2++)
      arrayOfSchemaGrammar[b2] = (SchemaGrammar)paramArrayOfXSGrammar[b2]; 
    if (!bool)
      arrayOfSchemaGrammar[i] = this; 
    return new XSModelImpl(arrayOfSchemaGrammar);
  }
  
  public XSObjectList getAnnotations() { return (this.fNumAnnotations == 0) ? XSObjectListImpl.EMPTY_LIST : new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations); }
  
  public void addAnnotation(XSAnnotationImpl paramXSAnnotationImpl) {
    if (paramXSAnnotationImpl == null)
      return; 
    if (this.fAnnotations == null) {
      this.fAnnotations = new XSAnnotationImpl[2];
    } else if (this.fNumAnnotations == this.fAnnotations.length) {
      XSAnnotationImpl[] arrayOfXSAnnotationImpl = new XSAnnotationImpl[this.fNumAnnotations << 1];
      System.arraycopy(this.fAnnotations, 0, arrayOfXSAnnotationImpl, 0, this.fNumAnnotations);
      this.fAnnotations = arrayOfXSAnnotationImpl;
    } 
    this.fAnnotations[this.fNumAnnotations++] = paramXSAnnotationImpl;
  }
  
  public void setImmutable(boolean paramBoolean) { this.fIsImmutable = paramBoolean; }
  
  public boolean isImmutable() { return this.fIsImmutable; }
  
  private static class BuiltinAttrDecl extends XSAttributeDecl {
    public BuiltinAttrDecl(String param1String1, String param1String2, XSSimpleType param1XSSimpleType, short param1Short) {
      this.fName = param1String1;
      this.fTargetNamespace = param1String2;
      this.fType = param1XSSimpleType;
      this.fScope = param1Short;
    }
    
    public void setValues(String param1String1, String param1String2, XSSimpleType param1XSSimpleType, short param1Short1, short param1Short2, ValidatedInfo param1ValidatedInfo, XSComplexTypeDecl param1XSComplexTypeDecl) {}
    
    public void reset() {}
    
    public XSAnnotation getAnnotation() { return null; }
    
    public XSNamespaceItem getNamespaceItem() { return SchemaGrammar.SG_XSI; }
  }
  
  public static class BuiltinSchemaGrammar extends SchemaGrammar {
    private static final String EXTENDED_SCHEMA_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.ExtendedSchemaDVFactoryImpl";
    
    public BuiltinSchemaGrammar(int param1Int, short param1Short) {
      if (param1Short == 1) {
        schemaDVFactory = SchemaDVFactory.getInstance();
      } else {
        schemaDVFactory = SchemaDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.xs.ExtendedSchemaDVFactoryImpl");
      } 
      if (param1Int == 1) {
        this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
        this.fGrammarDescription = new XSDDescription();
        this.fGrammarDescription.fContextType = 3;
        this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
        this.fGlobalAttrDecls = new SymbolHash(1);
        this.fGlobalAttrGrpDecls = new SymbolHash(1);
        this.fGlobalElemDecls = new SymbolHash(1);
        this.fGlobalGroupDecls = new SymbolHash(1);
        this.fGlobalNotationDecls = new SymbolHash(1);
        this.fGlobalIDConstraintDecls = new SymbolHash(1);
        this.fGlobalAttrDeclsExt = new SymbolHash(1);
        this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
        this.fGlobalElemDeclsExt = new SymbolHash(1);
        this.fGlobalGroupDeclsExt = new SymbolHash(1);
        this.fGlobalNotationDeclsExt = new SymbolHash(1);
        this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
        this.fGlobalTypeDeclsExt = new SymbolHash(1);
        this.fAllGlobalElemDecls = new SymbolHash(1);
        this.fGlobalTypeDecls = schemaDVFactory.getBuiltInTypes();
        int i = this.fGlobalTypeDecls.getLength();
        XSTypeDefinition[] arrayOfXSTypeDefinition = new XSTypeDefinition[i];
        this.fGlobalTypeDecls.getValues(arrayOfXSTypeDefinition, 0);
        for (byte b = 0; b < i; b++) {
          XSTypeDefinition xSTypeDefinition = arrayOfXSTypeDefinition[b];
          if (xSTypeDefinition instanceof XSSimpleTypeDecl)
            ((XSSimpleTypeDecl)xSTypeDefinition).setNamespaceItem(this); 
        } 
        this.fGlobalTypeDecls.put(fAnyType.getName(), fAnyType);
      } else if (param1Int == 2) {
        this.fTargetNamespace = SchemaSymbols.URI_XSI;
        this.fGrammarDescription = new XSDDescription();
        this.fGrammarDescription.fContextType = 3;
        this.fGrammarDescription.setNamespace(SchemaSymbols.URI_XSI);
        this.fGlobalAttrGrpDecls = new SymbolHash(1);
        this.fGlobalElemDecls = new SymbolHash(1);
        this.fGlobalGroupDecls = new SymbolHash(1);
        this.fGlobalNotationDecls = new SymbolHash(1);
        this.fGlobalIDConstraintDecls = new SymbolHash(1);
        this.fGlobalTypeDecls = new SymbolHash(1);
        this.fGlobalAttrDeclsExt = new SymbolHash(1);
        this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
        this.fGlobalElemDeclsExt = new SymbolHash(1);
        this.fGlobalGroupDeclsExt = new SymbolHash(1);
        this.fGlobalNotationDeclsExt = new SymbolHash(1);
        this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
        this.fGlobalTypeDeclsExt = new SymbolHash(1);
        this.fAllGlobalElemDecls = new SymbolHash(1);
        this.fGlobalAttrDecls = new SymbolHash(8);
        String str1 = null;
        String str2 = null;
        XSSimpleType xSSimpleType1 = null;
        int i = 1;
        str1 = SchemaSymbols.XSI_TYPE;
        str2 = SchemaSymbols.URI_XSI;
        xSSimpleType1 = schemaDVFactory.getBuiltInType("QName");
        this.fGlobalAttrDecls.put(str1, new SchemaGrammar.BuiltinAttrDecl(str1, str2, xSSimpleType1, i));
        str1 = SchemaSymbols.XSI_NIL;
        str2 = SchemaSymbols.URI_XSI;
        xSSimpleType1 = schemaDVFactory.getBuiltInType("boolean");
        this.fGlobalAttrDecls.put(str1, new SchemaGrammar.BuiltinAttrDecl(str1, str2, xSSimpleType1, i));
        XSSimpleType xSSimpleType2 = schemaDVFactory.getBuiltInType("anyURI");
        str1 = SchemaSymbols.XSI_SCHEMALOCATION;
        str2 = SchemaSymbols.URI_XSI;
        xSSimpleType1 = schemaDVFactory.createTypeList("#AnonType_schemaLocation", SchemaSymbols.URI_XSI, (short)0, xSSimpleType2, null);
        if (xSSimpleType1 instanceof XSSimpleTypeDecl)
          ((XSSimpleTypeDecl)xSSimpleType1).setAnonymous(true); 
        this.fGlobalAttrDecls.put(str1, new SchemaGrammar.BuiltinAttrDecl(str1, str2, xSSimpleType1, i));
        str1 = SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION;
        str2 = SchemaSymbols.URI_XSI;
        xSSimpleType1 = xSSimpleType2;
        this.fGlobalAttrDecls.put(str1, new SchemaGrammar.BuiltinAttrDecl(str1, str2, xSSimpleType1, i));
      } 
    }
    
    public XMLGrammarDescription getGrammarDescription() { return this.fGrammarDescription.makeClone(); }
    
    public void setImportedGrammars(Vector param1Vector) {}
    
    public void addGlobalAttributeDecl(XSAttributeDecl param1XSAttributeDecl) {}
    
    public void addGlobalAttributeDecl(XSAttributeDecl param1XSAttributeDecl, String param1String) {}
    
    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl param1XSAttributeGroupDecl) {}
    
    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl param1XSAttributeGroupDecl, String param1String) {}
    
    public void addGlobalElementDecl(XSElementDecl param1XSElementDecl) {}
    
    public void addGlobalElementDecl(XSElementDecl param1XSElementDecl, String param1String) {}
    
    public void addGlobalElementDeclAll(XSElementDecl param1XSElementDecl) {}
    
    public void addGlobalGroupDecl(XSGroupDecl param1XSGroupDecl) {}
    
    public void addGlobalGroupDecl(XSGroupDecl param1XSGroupDecl, String param1String) {}
    
    public void addGlobalNotationDecl(XSNotationDecl param1XSNotationDecl) {}
    
    public void addGlobalNotationDecl(XSNotationDecl param1XSNotationDecl, String param1String) {}
    
    public void addGlobalTypeDecl(XSTypeDefinition param1XSTypeDefinition) {}
    
    public void addGlobalTypeDecl(XSTypeDefinition param1XSTypeDefinition, String param1String) {}
    
    public void addGlobalComplexTypeDecl(XSComplexTypeDecl param1XSComplexTypeDecl) {}
    
    public void addGlobalComplexTypeDecl(XSComplexTypeDecl param1XSComplexTypeDecl, String param1String) {}
    
    public void addGlobalSimpleTypeDecl(XSSimpleType param1XSSimpleType) {}
    
    public void addGlobalSimpleTypeDecl(XSSimpleType param1XSSimpleType, String param1String) {}
    
    public void addComplexTypeDecl(XSComplexTypeDecl param1XSComplexTypeDecl, SimpleLocator param1SimpleLocator) {}
    
    public void addRedefinedGroupDecl(XSGroupDecl param1XSGroupDecl1, XSGroupDecl param1XSGroupDecl2, SimpleLocator param1SimpleLocator) {}
    
    public void addDocument(Object param1Object, String param1String) {}
    
    DOMParser getDOMParser() { return null; }
    
    SAXParser getSAXParser() { return null; }
  }
  
  public static final class Schema4Annotations extends SchemaGrammar {
    public static final Schema4Annotations INSTANCE = new Schema4Annotations();
    
    private Schema4Annotations() {
      this.fGrammarDescription.fContextType = 3;
      this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
      this.fGlobalAttrDecls = new SymbolHash(1);
      this.fGlobalAttrGrpDecls = new SymbolHash(1);
      this.fGlobalElemDecls = new SymbolHash(6);
      this.fGlobalGroupDecls = new SymbolHash(1);
      this.fGlobalNotationDecls = new SymbolHash(1);
      this.fGlobalIDConstraintDecls = new SymbolHash(1);
      this.fGlobalAttrDeclsExt = new SymbolHash(1);
      this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
      this.fGlobalElemDeclsExt = new SymbolHash(6);
      this.fGlobalGroupDeclsExt = new SymbolHash(1);
      this.fGlobalNotationDeclsExt = new SymbolHash(1);
      this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
      this.fGlobalTypeDeclsExt = new SymbolHash(1);
      this.fAllGlobalElemDecls = new SymbolHash(6);
      this.fGlobalTypeDecls = SG_SchemaNS.fGlobalTypeDecls;
      XSElementDecl xSElementDecl1 = createAnnotationElementDecl(SchemaSymbols.ELT_ANNOTATION);
      XSElementDecl xSElementDecl2 = createAnnotationElementDecl(SchemaSymbols.ELT_DOCUMENTATION);
      XSElementDecl xSElementDecl3 = createAnnotationElementDecl(SchemaSymbols.ELT_APPINFO);
      this.fGlobalElemDecls.put(xSElementDecl1.fName, xSElementDecl1);
      this.fGlobalElemDecls.put(xSElementDecl2.fName, xSElementDecl2);
      this.fGlobalElemDecls.put(xSElementDecl3.fName, xSElementDecl3);
      this.fGlobalElemDeclsExt.put("," + xSElementDecl1.fName, xSElementDecl1);
      this.fGlobalElemDeclsExt.put("," + xSElementDecl2.fName, xSElementDecl2);
      this.fGlobalElemDeclsExt.put("," + xSElementDecl3.fName, xSElementDecl3);
      this.fAllGlobalElemDecls.put(xSElementDecl1, xSElementDecl1);
      this.fAllGlobalElemDecls.put(xSElementDecl2, xSElementDecl2);
      this.fAllGlobalElemDecls.put(xSElementDecl3, xSElementDecl3);
      XSComplexTypeDecl xSComplexTypeDecl1 = new XSComplexTypeDecl();
      XSComplexTypeDecl xSComplexTypeDecl2 = new XSComplexTypeDecl();
      XSComplexTypeDecl xSComplexTypeDecl3 = new XSComplexTypeDecl();
      xSElementDecl1.fType = xSComplexTypeDecl1;
      xSElementDecl2.fType = xSComplexTypeDecl2;
      xSElementDecl3.fType = xSComplexTypeDecl3;
      XSAttributeGroupDecl xSAttributeGroupDecl1 = new XSAttributeGroupDecl();
      XSAttributeGroupDecl xSAttributeGroupDecl2 = new XSAttributeGroupDecl();
      XSAttributeGroupDecl xSAttributeGroupDecl3 = new XSAttributeGroupDecl();
      XSAttributeUseImpl xSAttributeUseImpl1 = new XSAttributeUseImpl();
      xSAttributeUseImpl1.fAttrDecl = new XSAttributeDecl();
      xSAttributeUseImpl1.fAttrDecl.setValues(SchemaSymbols.ATT_ID, null, (XSSimpleType)this.fGlobalTypeDecls.get("ID"), (short)0, (short)2, null, xSComplexTypeDecl1, null);
      xSAttributeUseImpl1.fUse = 0;
      xSAttributeUseImpl1.fConstraintType = 0;
      XSAttributeUseImpl xSAttributeUseImpl2 = new XSAttributeUseImpl();
      xSAttributeUseImpl2.fAttrDecl = new XSAttributeDecl();
      xSAttributeUseImpl2.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, xSComplexTypeDecl2, null);
      xSAttributeUseImpl2.fUse = 0;
      xSAttributeUseImpl2.fConstraintType = 0;
      XSAttributeUseImpl xSAttributeUseImpl3 = new XSAttributeUseImpl();
      xSAttributeUseImpl3.fAttrDecl = new XSAttributeDecl();
      xSAttributeUseImpl3.fAttrDecl.setValues("lang".intern(), NamespaceContext.XML_URI, (XSSimpleType)this.fGlobalTypeDecls.get("language"), (short)0, (short)2, null, xSComplexTypeDecl2, null);
      xSAttributeUseImpl3.fUse = 0;
      xSAttributeUseImpl3.fConstraintType = 0;
      XSAttributeUseImpl xSAttributeUseImpl4 = new XSAttributeUseImpl();
      xSAttributeUseImpl4.fAttrDecl = new XSAttributeDecl();
      xSAttributeUseImpl4.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, xSComplexTypeDecl3, null);
      xSAttributeUseImpl4.fUse = 0;
      xSAttributeUseImpl4.fConstraintType = 0;
      XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
      xSWildcardDecl.fNamespaceList = new String[] { this.fTargetNamespace, null };
      xSWildcardDecl.fType = 2;
      xSWildcardDecl.fProcessContents = 3;
      xSAttributeGroupDecl1.addAttributeUse(xSAttributeUseImpl1);
      xSAttributeGroupDecl1.fAttributeWC = xSWildcardDecl;
      xSAttributeGroupDecl2.addAttributeUse(xSAttributeUseImpl2);
      xSAttributeGroupDecl2.addAttributeUse(xSAttributeUseImpl3);
      xSAttributeGroupDecl2.fAttributeWC = xSWildcardDecl;
      xSAttributeGroupDecl3.addAttributeUse(xSAttributeUseImpl4);
      xSAttributeGroupDecl3.fAttributeWC = xSWildcardDecl;
      XSParticleDecl xSParticleDecl1 = createUnboundedModelGroupParticle();
      XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
      xSModelGroupImpl.fCompositor = 101;
      xSModelGroupImpl.fParticleCount = 2;
      xSModelGroupImpl.fParticles = new XSParticleDecl[2];
      xSModelGroupImpl.fParticles[0] = createChoiceElementParticle(xSElementDecl3);
      xSModelGroupImpl.fParticles[1] = createChoiceElementParticle(xSElementDecl2);
      xSParticleDecl1.fValue = xSModelGroupImpl;
      XSParticleDecl xSParticleDecl2 = createUnboundedAnyWildcardSequenceParticle();
      xSComplexTypeDecl1.setValues("#AnonType_" + SchemaSymbols.ELT_ANNOTATION, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)2, false, xSAttributeGroupDecl1, null, xSParticleDecl1, new XSObjectListImpl(null, 0));
      xSComplexTypeDecl1.setName("#AnonType_" + SchemaSymbols.ELT_ANNOTATION);
      xSComplexTypeDecl1.setIsAnonymous();
      xSComplexTypeDecl2.setValues("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)3, false, xSAttributeGroupDecl2, null, xSParticleDecl2, new XSObjectListImpl(null, 0));
      xSComplexTypeDecl2.setName("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION);
      xSComplexTypeDecl2.setIsAnonymous();
      xSComplexTypeDecl3.setValues("#AnonType_" + SchemaSymbols.ELT_APPINFO, this.fTargetNamespace, SchemaGrammar.fAnyType, (short)2, (short)0, (short)3, (short)3, false, xSAttributeGroupDecl3, null, xSParticleDecl2, new XSObjectListImpl(null, 0));
      xSComplexTypeDecl3.setName("#AnonType_" + SchemaSymbols.ELT_APPINFO);
      xSComplexTypeDecl3.setIsAnonymous();
    }
    
    public XMLGrammarDescription getGrammarDescription() { return this.fGrammarDescription.makeClone(); }
    
    public void setImportedGrammars(Vector param1Vector) {}
    
    public void addGlobalAttributeDecl(XSAttributeDecl param1XSAttributeDecl) {}
    
    public void addGlobalAttributeDecl(XSAttributeGroupDecl param1XSAttributeGroupDecl, String param1String) {}
    
    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl param1XSAttributeGroupDecl) {}
    
    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl param1XSAttributeGroupDecl, String param1String) {}
    
    public void addGlobalElementDecl(XSElementDecl param1XSElementDecl) {}
    
    public void addGlobalElementDecl(XSElementDecl param1XSElementDecl, String param1String) {}
    
    public void addGlobalElementDeclAll(XSElementDecl param1XSElementDecl) {}
    
    public void addGlobalGroupDecl(XSGroupDecl param1XSGroupDecl) {}
    
    public void addGlobalGroupDecl(XSGroupDecl param1XSGroupDecl, String param1String) {}
    
    public void addGlobalNotationDecl(XSNotationDecl param1XSNotationDecl) {}
    
    public void addGlobalNotationDecl(XSNotationDecl param1XSNotationDecl, String param1String) {}
    
    public void addGlobalTypeDecl(XSTypeDefinition param1XSTypeDefinition) {}
    
    public void addGlobalTypeDecl(XSTypeDefinition param1XSTypeDefinition, String param1String) {}
    
    public void addGlobalComplexTypeDecl(XSComplexTypeDecl param1XSComplexTypeDecl) {}
    
    public void addGlobalComplexTypeDecl(XSComplexTypeDecl param1XSComplexTypeDecl, String param1String) {}
    
    public void addGlobalSimpleTypeDecl(XSSimpleType param1XSSimpleType) {}
    
    public void addGlobalSimpleTypeDecl(XSSimpleType param1XSSimpleType, String param1String) {}
    
    public void addComplexTypeDecl(XSComplexTypeDecl param1XSComplexTypeDecl, SimpleLocator param1SimpleLocator) {}
    
    public void addRedefinedGroupDecl(XSGroupDecl param1XSGroupDecl1, XSGroupDecl param1XSGroupDecl2, SimpleLocator param1SimpleLocator) {}
    
    public void addDocument(Object param1Object, String param1String) {}
    
    DOMParser getDOMParser() { return null; }
    
    SAXParser getSAXParser() { return null; }
    
    private XSElementDecl createAnnotationElementDecl(String param1String) {
      XSElementDecl xSElementDecl = new XSElementDecl();
      xSElementDecl.fName = param1String;
      xSElementDecl.fTargetNamespace = this.fTargetNamespace;
      xSElementDecl.setIsGlobal();
      xSElementDecl.fBlock = 7;
      xSElementDecl.setConstraintType((short)0);
      return xSElementDecl;
    }
    
    private XSParticleDecl createUnboundedModelGroupParticle() {
      XSParticleDecl xSParticleDecl = new XSParticleDecl();
      xSParticleDecl.fMinOccurs = 0;
      xSParticleDecl.fMaxOccurs = -1;
      xSParticleDecl.fType = 3;
      return xSParticleDecl;
    }
    
    private XSParticleDecl createChoiceElementParticle(XSElementDecl param1XSElementDecl) {
      XSParticleDecl xSParticleDecl = new XSParticleDecl();
      xSParticleDecl.fMinOccurs = 1;
      xSParticleDecl.fMaxOccurs = 1;
      xSParticleDecl.fType = 1;
      xSParticleDecl.fValue = param1XSElementDecl;
      return xSParticleDecl;
    }
    
    private XSParticleDecl createUnboundedAnyWildcardSequenceParticle() {
      XSParticleDecl xSParticleDecl = createUnboundedModelGroupParticle();
      XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
      xSModelGroupImpl.fCompositor = 102;
      xSModelGroupImpl.fParticleCount = 1;
      xSModelGroupImpl.fParticles = new XSParticleDecl[1];
      xSModelGroupImpl.fParticles[0] = createAnyLaxWildcardParticle();
      xSParticleDecl.fValue = xSModelGroupImpl;
      return xSParticleDecl;
    }
    
    private XSParticleDecl createAnyLaxWildcardParticle() {
      XSParticleDecl xSParticleDecl = new XSParticleDecl();
      xSParticleDecl.fMinOccurs = 1;
      xSParticleDecl.fMaxOccurs = 1;
      xSParticleDecl.fType = 2;
      XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
      xSWildcardDecl.fNamespaceList = null;
      xSWildcardDecl.fType = 1;
      xSWildcardDecl.fProcessContents = 3;
      xSParticleDecl.fValue = xSWildcardDecl;
      return xSParticleDecl;
    }
  }
  
  private static class XSAnyType extends XSComplexTypeDecl {
    public XSAnyType() {
      this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
      this.fBaseType = this;
      this.fDerivedBy = 2;
      this.fContentType = 3;
      this.fParticle = null;
      this.fAttrGrp = null;
    }
    
    public void setValues(String param1String1, String param1String2, XSTypeDefinition param1XSTypeDefinition, short param1Short1, short param1Short2, short param1Short3, short param1Short4, boolean param1Boolean, XSAttributeGroupDecl param1XSAttributeGroupDecl, XSSimpleType param1XSSimpleType, XSParticleDecl param1XSParticleDecl) {}
    
    public void setName(String param1String) {}
    
    public void setIsAbstractType() {}
    
    public void setContainsTypeID() {}
    
    public void setIsAnonymous() {}
    
    public void reset() {}
    
    public XSObjectList getAttributeUses() { return XSObjectListImpl.EMPTY_LIST; }
    
    public XSAttributeGroupDecl getAttrGrp() {
      XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
      xSWildcardDecl.fProcessContents = 3;
      XSAttributeGroupDecl xSAttributeGroupDecl = new XSAttributeGroupDecl();
      xSAttributeGroupDecl.fAttributeWC = xSWildcardDecl;
      return xSAttributeGroupDecl;
    }
    
    public XSWildcard getAttributeWildcard() {
      XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
      xSWildcardDecl.fProcessContents = 3;
      return xSWildcardDecl;
    }
    
    public XSParticle getParticle() {
      XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
      xSWildcardDecl.fProcessContents = 3;
      XSParticleDecl xSParticleDecl1 = new XSParticleDecl();
      xSParticleDecl1.fMinOccurs = 0;
      xSParticleDecl1.fMaxOccurs = -1;
      xSParticleDecl1.fType = 2;
      xSParticleDecl1.fValue = xSWildcardDecl;
      XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
      xSModelGroupImpl.fCompositor = 102;
      xSModelGroupImpl.fParticleCount = 1;
      xSModelGroupImpl.fParticles = new XSParticleDecl[1];
      xSModelGroupImpl.fParticles[0] = xSParticleDecl1;
      XSParticleDecl xSParticleDecl2 = new XSParticleDecl();
      xSParticleDecl2.fType = 3;
      xSParticleDecl2.fValue = xSModelGroupImpl;
      return xSParticleDecl2;
    }
    
    public XSObjectList getAnnotations() { return XSObjectListImpl.EMPTY_LIST; }
    
    public XSNamespaceItem getNamespaceItem() { return SchemaGrammar.SG_SchemaNS; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\SchemaGrammar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */