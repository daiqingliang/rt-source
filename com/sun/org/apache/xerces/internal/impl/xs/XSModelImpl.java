package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItemList;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

public final class XSModelImpl extends AbstractList implements XSModel, XSNamespaceItemList {
  private static final short MAX_COMP_IDX = 16;
  
  private static final boolean[] GLOBAL_COMP = { 
      false, true, true, true, false, true, true, false, false, false, 
      false, true, false, false, false, true, true };
  
  private final int fGrammarCount;
  
  private final String[] fNamespaces;
  
  private final SchemaGrammar[] fGrammarList;
  
  private final SymbolHash fGrammarMap;
  
  private final SymbolHash fSubGroupMap;
  
  private final XSNamedMap[] fGlobalComponents;
  
  private final XSNamedMap[][] fNSComponents;
  
  private final StringList fNamespacesList;
  
  private XSObjectList fAnnotations = null;
  
  private final boolean fHasIDC;
  
  public XSModelImpl(SchemaGrammar[] paramArrayOfSchemaGrammar) { this(paramArrayOfSchemaGrammar, (short)1); }
  
  public XSModelImpl(SchemaGrammar[] paramArrayOfSchemaGrammar, short paramShort) {
    int i = paramArrayOfSchemaGrammar.length;
    int j = Math.max(i + 1, 5);
    String[] arrayOfString = new String[j];
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[j];
    boolean bool1 = false;
    for (byte b1 = 0; b1 < i; b1++) {
      SchemaGrammar schemaGrammar = paramArrayOfSchemaGrammar[b1];
      String str = schemaGrammar.getTargetNamespace();
      arrayOfString[b1] = str;
      arrayOfSchemaGrammar[b1] = schemaGrammar;
      if (str == SchemaSymbols.URI_SCHEMAFORSCHEMA)
        bool1 = true; 
    } 
    if (!bool1) {
      arrayOfString[i] = SchemaSymbols.URI_SCHEMAFORSCHEMA;
      arrayOfSchemaGrammar[i++] = SchemaGrammar.getS4SGrammar(paramShort);
    } 
    byte b2;
    for (b2 = 0; b2 < i; b2++) {
      SchemaGrammar schemaGrammar = arrayOfSchemaGrammar[b2];
      Vector vector = schemaGrammar.getImportedGrammars();
      for (byte b = (vector == null) ? -1 : (vector.size() - 1); b >= 0; b--) {
        SchemaGrammar schemaGrammar1 = (SchemaGrammar)vector.elementAt(b);
        byte b3;
        for (b3 = 0; b3 < i && schemaGrammar1 != arrayOfSchemaGrammar[b3]; b3++);
        if (b3 == i) {
          if (i == arrayOfSchemaGrammar.length) {
            String[] arrayOfString1 = new String[i * 2];
            System.arraycopy(arrayOfString, 0, arrayOfString1, 0, i);
            arrayOfString = arrayOfString1;
            SchemaGrammar[] arrayOfSchemaGrammar1 = new SchemaGrammar[i * 2];
            System.arraycopy(arrayOfSchemaGrammar, 0, arrayOfSchemaGrammar1, 0, i);
            arrayOfSchemaGrammar = arrayOfSchemaGrammar1;
          } 
          arrayOfString[i] = schemaGrammar1.getTargetNamespace();
          arrayOfSchemaGrammar[i] = schemaGrammar1;
          i++;
        } 
      } 
    } 
    this.fNamespaces = arrayOfString;
    this.fGrammarList = arrayOfSchemaGrammar;
    boolean bool2 = false;
    this.fGrammarMap = new SymbolHash(i * 2);
    for (b2 = 0; b2 < i; b2++) {
      this.fGrammarMap.put(null2EmptyString(this.fNamespaces[b2]), this.fGrammarList[b2]);
      if (this.fGrammarList[b2].hasIDConstraints())
        bool2 = true; 
    } 
    this.fHasIDC = bool2;
    this.fGrammarCount = i;
    this.fGlobalComponents = new XSNamedMap[17];
    this.fNSComponents = new XSNamedMap[i][17];
    this.fNamespacesList = new StringListImpl(this.fNamespaces, this.fGrammarCount);
    this.fSubGroupMap = buildSubGroups();
  }
  
  private SymbolHash buildSubGroups_Org() {
    SubstitutionGroupHandler substitutionGroupHandler = new SubstitutionGroupHandler(null);
    for (byte b1 = 0; b1 < this.fGrammarCount; b1++)
      substitutionGroupHandler.addSubstitutionGroup(this.fGrammarList[b1].getSubstitutionGroups()); 
    XSNamedMap xSNamedMap = getComponents((short)2);
    int i = xSNamedMap.getLength();
    SymbolHash symbolHash = new SymbolHash(i * 2);
    for (byte b2 = 0; b2 < i; b2++) {
      XSElementDecl xSElementDecl = (XSElementDecl)xSNamedMap.item(b2);
      XSElementDecl[] arrayOfXSElementDecl = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl);
      symbolHash.put(xSElementDecl, (arrayOfXSElementDecl.length > 0) ? new XSObjectListImpl(arrayOfXSElementDecl, arrayOfXSElementDecl.length) : XSObjectListImpl.EMPTY_LIST);
    } 
    return symbolHash;
  }
  
  private SymbolHash buildSubGroups() {
    SubstitutionGroupHandler substitutionGroupHandler = new SubstitutionGroupHandler(null);
    for (byte b1 = 0; b1 < this.fGrammarCount; b1++)
      substitutionGroupHandler.addSubstitutionGroup(this.fGrammarList[b1].getSubstitutionGroups()); 
    XSObjectListImpl xSObjectListImpl = getGlobalElements();
    int i = xSObjectListImpl.getLength();
    SymbolHash symbolHash = new SymbolHash(i * 2);
    for (byte b2 = 0; b2 < i; b2++) {
      XSElementDecl xSElementDecl = (XSElementDecl)xSObjectListImpl.item(b2);
      XSElementDecl[] arrayOfXSElementDecl = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl);
      symbolHash.put(xSElementDecl, (arrayOfXSElementDecl.length > 0) ? new XSObjectListImpl(arrayOfXSElementDecl, arrayOfXSElementDecl.length) : XSObjectListImpl.EMPTY_LIST);
    } 
    return symbolHash;
  }
  
  private XSObjectListImpl getGlobalElements() {
    SymbolHash[] arrayOfSymbolHash = new SymbolHash[this.fGrammarCount];
    int i = 0;
    for (byte b1 = 0; b1 < this.fGrammarCount; b1++) {
      arrayOfSymbolHash[b1] = (this.fGrammarList[b1]).fAllGlobalElemDecls;
      i += arrayOfSymbolHash[b1].getLength();
    } 
    if (i == 0)
      return XSObjectListImpl.EMPTY_LIST; 
    XSObject[] arrayOfXSObject = new XSObject[i];
    int j = 0;
    for (byte b2 = 0; b2 < this.fGrammarCount; b2++) {
      arrayOfSymbolHash[b2].getValues(arrayOfXSObject, j);
      j += arrayOfSymbolHash[b2].getLength();
    } 
    return new XSObjectListImpl(arrayOfXSObject, i);
  }
  
  public StringList getNamespaces() { return this.fNamespacesList; }
  
  public XSNamespaceItemList getNamespaceItems() { return this; }
  
  public XSNamedMap getComponents(short paramShort) {
    if (paramShort <= 0 || paramShort > 16 || !GLOBAL_COMP[paramShort])
      return XSNamedMapImpl.EMPTY_MAP; 
    SymbolHash[] arrayOfSymbolHash = new SymbolHash[this.fGrammarCount];
    if (this.fGlobalComponents[paramShort] == null) {
      for (byte b = 0; b < this.fGrammarCount; b++) {
        switch (paramShort) {
          case 3:
          case 15:
          case 16:
            arrayOfSymbolHash[b] = (this.fGrammarList[b]).fGlobalTypeDecls;
            break;
          case 1:
            arrayOfSymbolHash[b] = (this.fGrammarList[b]).fGlobalAttrDecls;
            break;
          case 2:
            arrayOfSymbolHash[b] = (this.fGrammarList[b]).fGlobalElemDecls;
            break;
          case 5:
            arrayOfSymbolHash[b] = (this.fGrammarList[b]).fGlobalAttrGrpDecls;
            break;
          case 6:
            arrayOfSymbolHash[b] = (this.fGrammarList[b]).fGlobalGroupDecls;
            break;
          case 11:
            arrayOfSymbolHash[b] = (this.fGrammarList[b]).fGlobalNotationDecls;
            break;
        } 
      } 
      if (paramShort == 15 || paramShort == 16) {
        this.fGlobalComponents[paramShort] = new XSNamedMap4Types(this.fNamespaces, arrayOfSymbolHash, this.fGrammarCount, paramShort);
      } else {
        this.fGlobalComponents[paramShort] = new XSNamedMapImpl(this.fNamespaces, arrayOfSymbolHash, this.fGrammarCount);
      } 
    } 
    return this.fGlobalComponents[paramShort];
  }
  
  public XSNamedMap getComponentsByNamespace(short paramShort, String paramString) {
    if (paramShort <= 0 || paramShort > 16 || !GLOBAL_COMP[paramShort])
      return XSNamedMapImpl.EMPTY_MAP; 
    byte b = 0;
    if (paramString != null) {
      while (b < this.fGrammarCount && !paramString.equals(this.fNamespaces[b]))
        b++; 
    } else {
      while (b < this.fGrammarCount && this.fNamespaces[b] != null)
        b++; 
    } 
    if (b == this.fGrammarCount)
      return XSNamedMapImpl.EMPTY_MAP; 
    if (this.fNSComponents[b][paramShort] == null) {
      SymbolHash symbolHash = null;
      switch (paramShort) {
        case 3:
        case 15:
        case 16:
          symbolHash = (this.fGrammarList[b]).fGlobalTypeDecls;
          break;
        case 1:
          symbolHash = (this.fGrammarList[b]).fGlobalAttrDecls;
          break;
        case 2:
          symbolHash = (this.fGrammarList[b]).fGlobalElemDecls;
          break;
        case 5:
          symbolHash = (this.fGrammarList[b]).fGlobalAttrGrpDecls;
          break;
        case 6:
          symbolHash = (this.fGrammarList[b]).fGlobalGroupDecls;
          break;
        case 11:
          symbolHash = (this.fGrammarList[b]).fGlobalNotationDecls;
          break;
      } 
      if (paramShort == 15 || paramShort == 16) {
        this.fNSComponents[b][paramShort] = new XSNamedMap4Types(paramString, symbolHash, paramShort);
      } else {
        this.fNSComponents[b][paramShort] = new XSNamedMapImpl(paramString, symbolHash);
      } 
    } 
    return this.fNSComponents[b][paramShort];
  }
  
  public XSTypeDefinition getTypeDefinition(String paramString1, String paramString2) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : (XSTypeDefinition)schemaGrammar.fGlobalTypeDecls.get(paramString1);
  }
  
  public XSTypeDefinition getTypeDefinition(String paramString1, String paramString2, String paramString3) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : schemaGrammar.getGlobalTypeDecl(paramString1, paramString3);
  }
  
  public XSAttributeDeclaration getAttributeDeclaration(String paramString1, String paramString2) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : (XSAttributeDeclaration)schemaGrammar.fGlobalAttrDecls.get(paramString1);
  }
  
  public XSAttributeDeclaration getAttributeDeclaration(String paramString1, String paramString2, String paramString3) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : schemaGrammar.getGlobalAttributeDecl(paramString1, paramString3);
  }
  
  public XSElementDeclaration getElementDeclaration(String paramString1, String paramString2) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : (XSElementDeclaration)schemaGrammar.fGlobalElemDecls.get(paramString1);
  }
  
  public XSElementDeclaration getElementDeclaration(String paramString1, String paramString2, String paramString3) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : schemaGrammar.getGlobalElementDecl(paramString1, paramString3);
  }
  
  public XSAttributeGroupDefinition getAttributeGroup(String paramString1, String paramString2) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : (XSAttributeGroupDefinition)schemaGrammar.fGlobalAttrGrpDecls.get(paramString1);
  }
  
  public XSAttributeGroupDefinition getAttributeGroup(String paramString1, String paramString2, String paramString3) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : schemaGrammar.getGlobalAttributeGroupDecl(paramString1, paramString3);
  }
  
  public XSModelGroupDefinition getModelGroupDefinition(String paramString1, String paramString2) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : (XSModelGroupDefinition)schemaGrammar.fGlobalGroupDecls.get(paramString1);
  }
  
  public XSModelGroupDefinition getModelGroupDefinition(String paramString1, String paramString2, String paramString3) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : schemaGrammar.getGlobalGroupDecl(paramString1, paramString3);
  }
  
  public XSNotationDeclaration getNotationDeclaration(String paramString1, String paramString2) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : (XSNotationDeclaration)schemaGrammar.fGlobalNotationDecls.get(paramString1);
  }
  
  public XSNotationDeclaration getNotationDeclaration(String paramString1, String paramString2, String paramString3) {
    SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(paramString2));
    return (schemaGrammar == null) ? null : schemaGrammar.getGlobalNotationDecl(paramString1, paramString3);
  }
  
  public XSObjectList getAnnotations() {
    if (this.fAnnotations != null)
      return this.fAnnotations; 
    int i = 0;
    for (byte b1 = 0; b1 < this.fGrammarCount; b1++)
      i += (this.fGrammarList[b1]).fNumAnnotations; 
    if (i == 0) {
      this.fAnnotations = XSObjectListImpl.EMPTY_LIST;
      return this.fAnnotations;
    } 
    XSAnnotationImpl[] arrayOfXSAnnotationImpl = new XSAnnotationImpl[i];
    int j = 0;
    for (byte b2 = 0; b2 < this.fGrammarCount; b2++) {
      SchemaGrammar schemaGrammar = this.fGrammarList[b2];
      if (schemaGrammar.fNumAnnotations > 0) {
        System.arraycopy(schemaGrammar.fAnnotations, 0, arrayOfXSAnnotationImpl, j, schemaGrammar.fNumAnnotations);
        j += schemaGrammar.fNumAnnotations;
      } 
    } 
    this.fAnnotations = new XSObjectListImpl(arrayOfXSAnnotationImpl, arrayOfXSAnnotationImpl.length);
    return this.fAnnotations;
  }
  
  private static final String null2EmptyString(String paramString) { return (paramString == null) ? XMLSymbols.EMPTY_STRING : paramString; }
  
  public boolean hasIDConstraints() { return this.fHasIDC; }
  
  public XSObjectList getSubstitutionGroup(XSElementDeclaration paramXSElementDeclaration) { return (XSObjectList)this.fSubGroupMap.get(paramXSElementDeclaration); }
  
  public int getLength() { return this.fGrammarCount; }
  
  public XSNamespaceItem item(int paramInt) { return (paramInt < 0 || paramInt >= this.fGrammarCount) ? null : this.fGrammarList[paramInt]; }
  
  public Object get(int paramInt) {
    if (paramInt >= 0 && paramInt < this.fGrammarCount)
      return this.fGrammarList[paramInt]; 
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size() { return getLength(); }
  
  public Iterator iterator() { return listIterator0(0); }
  
  public ListIterator listIterator() { return listIterator0(0); }
  
  public ListIterator listIterator(int paramInt) {
    if (paramInt >= 0 && paramInt < this.fGrammarCount)
      return listIterator0(paramInt); 
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  private ListIterator listIterator0(int paramInt) { return new XSNamespaceItemListIterator(paramInt); }
  
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[this.fGrammarCount];
    toArray0(arrayOfObject);
    return arrayOfObject;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject) {
    if (paramArrayOfObject.length < this.fGrammarCount) {
      Class clazz1 = paramArrayOfObject.getClass();
      Class clazz2 = clazz1.getComponentType();
      paramArrayOfObject = (Object[])Array.newInstance(clazz2, this.fGrammarCount);
    } 
    toArray0(paramArrayOfObject);
    if (paramArrayOfObject.length > this.fGrammarCount)
      paramArrayOfObject[this.fGrammarCount] = null; 
    return paramArrayOfObject;
  }
  
  private void toArray0(Object[] paramArrayOfObject) {
    if (this.fGrammarCount > 0)
      System.arraycopy(this.fGrammarList, 0, paramArrayOfObject, 0, this.fGrammarCount); 
  }
  
  private final class XSNamespaceItemListIterator implements ListIterator {
    private int index;
    
    public XSNamespaceItemListIterator(int param1Int) { this.index = param1Int; }
    
    public boolean hasNext() { return (this.index < XSModelImpl.this.fGrammarCount); }
    
    public Object next() {
      if (this.index < XSModelImpl.this.fGrammarCount)
        return XSModelImpl.this.fGrammarList[this.index++]; 
      throw new NoSuchElementException();
    }
    
    public boolean hasPrevious() { return (this.index > 0); }
    
    public Object previous() {
      if (this.index > 0)
        return XSModelImpl.this.fGrammarList[--this.index]; 
      throw new NoSuchElementException();
    }
    
    public int nextIndex() { return this.index; }
    
    public int previousIndex() { return this.index - 1; }
    
    public void remove() { throw new UnsupportedOperationException(); }
    
    public void set(Object param1Object) { throw new UnsupportedOperationException(); }
    
    public void add(Object param1Object) { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSModelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */