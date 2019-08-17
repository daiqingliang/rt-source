package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSIDCDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public abstract class IdentityConstraint implements XSIDCDefinition {
  protected short type;
  
  protected String fNamespace;
  
  protected String fIdentityConstraintName;
  
  protected String fElementName;
  
  protected Selector fSelector;
  
  protected int fFieldCount;
  
  protected Field[] fFields;
  
  protected XSAnnotationImpl[] fAnnotations = null;
  
  protected int fNumAnnotations;
  
  protected IdentityConstraint(String paramString1, String paramString2, String paramString3) {
    this.fNamespace = paramString1;
    this.fIdentityConstraintName = paramString2;
    this.fElementName = paramString3;
  }
  
  public String getIdentityConstraintName() { return this.fIdentityConstraintName; }
  
  public void setSelector(Selector paramSelector) { this.fSelector = paramSelector; }
  
  public Selector getSelector() { return this.fSelector; }
  
  public void addField(Field paramField) {
    if (this.fFields == null) {
      this.fFields = new Field[4];
    } else if (this.fFieldCount == this.fFields.length) {
      this.fFields = resize(this.fFields, this.fFieldCount * 2);
    } 
    this.fFields[this.fFieldCount++] = paramField;
  }
  
  public int getFieldCount() { return this.fFieldCount; }
  
  public Field getFieldAt(int paramInt) { return this.fFields[paramInt]; }
  
  public String getElementName() { return this.fElementName; }
  
  public String toString() {
    String str = super.toString();
    int i = str.lastIndexOf('$');
    if (i != -1)
      return str.substring(i + 1); 
    int j = str.lastIndexOf('.');
    return (j != -1) ? str.substring(j + 1) : str;
  }
  
  public boolean equals(IdentityConstraint paramIdentityConstraint) {
    boolean bool = this.fIdentityConstraintName.equals(paramIdentityConstraint.fIdentityConstraintName);
    if (!bool)
      return false; 
    bool = this.fSelector.toString().equals(paramIdentityConstraint.fSelector.toString());
    if (!bool)
      return false; 
    bool = (this.fFieldCount == paramIdentityConstraint.fFieldCount);
    if (!bool)
      return false; 
    for (byte b = 0; b < this.fFieldCount; b++) {
      if (!this.fFields[b].toString().equals(paramIdentityConstraint.fFields[b].toString()))
        return false; 
    } 
    return true;
  }
  
  static final Field[] resize(Field[] paramArrayOfField, int paramInt) {
    Field[] arrayOfField = new Field[paramInt];
    System.arraycopy(paramArrayOfField, 0, arrayOfField, 0, paramArrayOfField.length);
    return arrayOfField;
  }
  
  public short getType() { return 10; }
  
  public String getName() { return this.fIdentityConstraintName; }
  
  public String getNamespace() { return this.fNamespace; }
  
  public short getCategory() { return this.type; }
  
  public String getSelectorStr() { return (this.fSelector != null) ? this.fSelector.toString() : null; }
  
  public StringList getFieldStrs() {
    String[] arrayOfString = new String[this.fFieldCount];
    for (byte b = 0; b < this.fFieldCount; b++)
      arrayOfString[b] = this.fFields[b].toString(); 
    return new StringListImpl(arrayOfString, this.fFieldCount);
  }
  
  public XSIDCDefinition getRefKey() { return null; }
  
  public XSObjectList getAnnotations() { return new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations); }
  
  public XSNamespaceItem getNamespaceItem() { return null; }
  
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\IdentityConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */