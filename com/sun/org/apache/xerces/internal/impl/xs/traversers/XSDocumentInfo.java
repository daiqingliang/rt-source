package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.util.Stack;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class XSDocumentInfo {
  protected SchemaNamespaceSupport fNamespaceSupport;
  
  protected SchemaNamespaceSupport fNamespaceSupportRoot;
  
  protected Stack SchemaNamespaceSupportStack = new Stack();
  
  protected boolean fAreLocalAttributesQualified;
  
  protected boolean fAreLocalElementsQualified;
  
  protected short fBlockDefault;
  
  protected short fFinalDefault;
  
  String fTargetNamespace;
  
  protected boolean fIsChameleonSchema;
  
  protected Element fSchemaElement;
  
  Vector fImportedNS = new Vector();
  
  protected ValidationState fValidationContext = new ValidationState();
  
  SymbolTable fSymbolTable = null;
  
  protected XSAttributeChecker fAttrChecker;
  
  protected Object[] fSchemaAttrs;
  
  protected XSAnnotationInfo fAnnotations = null;
  
  private Vector fReportedTNS = null;
  
  XSDocumentInfo(Element paramElement, XSAttributeChecker paramXSAttributeChecker, SymbolTable paramSymbolTable) throws XMLSchemaException {
    this.fSchemaElement = paramElement;
    initNamespaceSupport(paramElement);
    this.fIsChameleonSchema = false;
    this.fSymbolTable = paramSymbolTable;
    this.fAttrChecker = paramXSAttributeChecker;
    if (paramElement != null) {
      Element element = paramElement;
      this.fSchemaAttrs = paramXSAttributeChecker.checkAttributes(element, true, this);
      if (this.fSchemaAttrs == null)
        throw new XMLSchemaException(null, null); 
      this.fAreLocalAttributesQualified = (((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == 1);
      this.fAreLocalElementsQualified = (((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == 1);
      this.fBlockDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
      this.fFinalDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
      this.fTargetNamespace = (String)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
      if (this.fTargetNamespace != null)
        this.fTargetNamespace = paramSymbolTable.addSymbol(this.fTargetNamespace); 
      this.fNamespaceSupportRoot = new SchemaNamespaceSupport(this.fNamespaceSupport);
      this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
      this.fValidationContext.setSymbolTable(paramSymbolTable);
    } 
  }
  
  private void initNamespaceSupport(Element paramElement) {
    this.fNamespaceSupport = new SchemaNamespaceSupport();
    this.fNamespaceSupport.reset();
    for (Node node = paramElement.getParentNode(); node != null && node.getNodeType() == 1 && !node.getNodeName().equals("DOCUMENT_NODE"); node = node.getParentNode()) {
      Element element = (Element)node;
      NamedNodeMap namedNodeMap = element.getAttributes();
      int i = (namedNodeMap != null) ? namedNodeMap.getLength() : 0;
      for (byte b = 0; b < i; b++) {
        Attr attr = (Attr)namedNodeMap.item(b);
        String str = attr.getNamespaceURI();
        if (str != null && str.equals("http://www.w3.org/2000/xmlns/")) {
          String str1 = attr.getLocalName().intern();
          if (str1 == "xmlns")
            str1 = ""; 
          if (this.fNamespaceSupport.getURI(str1) == null)
            this.fNamespaceSupport.declarePrefix(str1, attr.getValue().intern()); 
        } 
      } 
    } 
  }
  
  void backupNSSupport(SchemaNamespaceSupport paramSchemaNamespaceSupport) {
    this.SchemaNamespaceSupportStack.push(this.fNamespaceSupport);
    if (paramSchemaNamespaceSupport == null)
      paramSchemaNamespaceSupport = this.fNamespaceSupportRoot; 
    this.fNamespaceSupport = new SchemaNamespaceSupport(paramSchemaNamespaceSupport);
    this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
  }
  
  void restoreNSSupport() {
    this.fNamespaceSupport = (SchemaNamespaceSupport)this.SchemaNamespaceSupportStack.pop();
    this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
  }
  
  public String toString() { return (this.fTargetNamespace == null) ? "no targetNamspace" : ("targetNamespace is " + this.fTargetNamespace); }
  
  public void addAllowedNS(String paramString) { this.fImportedNS.addElement((paramString == null) ? "" : paramString); }
  
  public boolean isAllowedNS(String paramString) { return this.fImportedNS.contains((paramString == null) ? "" : paramString); }
  
  final boolean needReportTNSError(String paramString) {
    if (this.fReportedTNS == null) {
      this.fReportedTNS = new Vector();
    } else if (this.fReportedTNS.contains(paramString)) {
      return false;
    } 
    this.fReportedTNS.addElement(paramString);
    return true;
  }
  
  Object[] getSchemaAttrs() { return this.fSchemaAttrs; }
  
  void returnSchemaAttrs() {
    this.fAttrChecker.returnAttrArray(this.fSchemaAttrs, null);
    this.fSchemaAttrs = null;
  }
  
  void addAnnotation(XSAnnotationInfo paramXSAnnotationInfo) {
    paramXSAnnotationInfo.next = this.fAnnotations;
    this.fAnnotations = paramXSAnnotationInfo;
  }
  
  XSAnnotationInfo getAnnotations() { return this.fAnnotations; }
  
  void removeAnnotations() { this.fAnnotations = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDocumentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */