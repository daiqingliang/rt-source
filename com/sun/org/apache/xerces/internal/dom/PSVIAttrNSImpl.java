package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PSVIAttrNSImpl extends AttrNSImpl implements AttributePSVI {
  static final long serialVersionUID = -3241738699421018889L;
  
  protected XSAttributeDeclaration fDeclaration = null;
  
  protected XSTypeDefinition fTypeDecl = null;
  
  protected boolean fSpecified = true;
  
  protected String fNormalizedValue = null;
  
  protected Object fActualValue = null;
  
  protected short fActualValueType = 45;
  
  protected ShortList fItemValueTypes = null;
  
  protected XSSimpleTypeDefinition fMemberType = null;
  
  protected short fValidationAttempted = 0;
  
  protected short fValidity = 0;
  
  protected StringList fErrorCodes = null;
  
  protected String fValidationContext = null;
  
  public PSVIAttrNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3) { super(paramCoreDocumentImpl, paramString1, paramString2, paramString3); }
  
  public PSVIAttrNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2) { super(paramCoreDocumentImpl, paramString1, paramString2); }
  
  public String getSchemaDefault() { return (this.fDeclaration == null) ? null : this.fDeclaration.getConstraintValue(); }
  
  public String getSchemaNormalizedValue() { return this.fNormalizedValue; }
  
  public boolean getIsSchemaSpecified() { return this.fSpecified; }
  
  public short getValidationAttempted() { return this.fValidationAttempted; }
  
  public short getValidity() { return this.fValidity; }
  
  public StringList getErrorCodes() { return this.fErrorCodes; }
  
  public String getValidationContext() { return this.fValidationContext; }
  
  public XSTypeDefinition getTypeDefinition() { return this.fTypeDecl; }
  
  public XSSimpleTypeDefinition getMemberTypeDefinition() { return this.fMemberType; }
  
  public XSAttributeDeclaration getAttributeDeclaration() { return this.fDeclaration; }
  
  public void setPSVI(AttributePSVI paramAttributePSVI) {
    this.fDeclaration = paramAttributePSVI.getAttributeDeclaration();
    this.fValidationContext = paramAttributePSVI.getValidationContext();
    this.fValidity = paramAttributePSVI.getValidity();
    this.fValidationAttempted = paramAttributePSVI.getValidationAttempted();
    this.fErrorCodes = paramAttributePSVI.getErrorCodes();
    this.fNormalizedValue = paramAttributePSVI.getSchemaNormalizedValue();
    this.fActualValue = paramAttributePSVI.getActualNormalizedValue();
    this.fActualValueType = paramAttributePSVI.getActualNormalizedValueType();
    this.fItemValueTypes = paramAttributePSVI.getItemValueTypes();
    this.fTypeDecl = paramAttributePSVI.getTypeDefinition();
    this.fMemberType = paramAttributePSVI.getMemberTypeDefinition();
    this.fSpecified = paramAttributePSVI.getIsSchemaSpecified();
  }
  
  public Object getActualNormalizedValue() { return this.fActualValue; }
  
  public short getActualNormalizedValueType() { return this.fActualValueType; }
  
  public ShortList getItemValueTypes() { return this.fItemValueTypes; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { throw new NotSerializableException(getClass().getName()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { throw new NotSerializableException(getClass().getName()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\PSVIAttrNSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */