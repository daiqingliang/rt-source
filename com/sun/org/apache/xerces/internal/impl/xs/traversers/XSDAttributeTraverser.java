package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Element;

class XSDAttributeTraverser extends XSDAbstractTraverser {
  public XSDAttributeTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  protected XSAttributeUseImpl traverseLocal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, XSComplexTypeDecl paramXSComplexTypeDecl) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    String str1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_DEFAULT];
    String str2 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_FIXED];
    String str3 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    QName qName = (QName)arrayOfObject[XSAttributeChecker.ATTIDX_REF];
    XInt xInt = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_USE];
    XSAttributeDecl xSAttributeDecl = null;
    XSAnnotationImpl xSAnnotationImpl = null;
    if (paramElement.getAttributeNode(SchemaSymbols.ATT_REF) != null) {
      if (qName != null) {
        xSAttributeDecl = (XSAttributeDecl)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 1, qName, paramElement);
        Element element = DOMUtil.getFirstChildElement(paramElement);
        if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
          xSAnnotationImpl = traverseAnnotationDecl(element, arrayOfObject, false, paramXSDocumentInfo);
          element = DOMUtil.getNextSiblingElement(element);
        } else {
          String str = DOMUtil.getSyntheticAnnotation(paramElement);
          if (str != null)
            xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, str, arrayOfObject, false, paramXSDocumentInfo); 
        } 
        if (element != null)
          reportSchemaError("src-attribute.3.2", new Object[] { qName.rawname }, element); 
        str3 = qName.localpart;
      } else {
        xSAttributeDecl = null;
      } 
    } else {
      xSAttributeDecl = traverseNamedAttr(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar, false, paramXSComplexTypeDecl);
    } 
    byte b = 0;
    if (str1 != null) {
      b = 1;
    } else if (str2 != null) {
      b = 2;
      str1 = str2;
      str2 = null;
    } 
    XSAttributeUseImpl xSAttributeUseImpl = null;
    if (xSAttributeDecl != null) {
      if (this.fSchemaHandler.fDeclPool != null) {
        xSAttributeUseImpl = this.fSchemaHandler.fDeclPool.getAttributeUse();
      } else {
        xSAttributeUseImpl = new XSAttributeUseImpl();
      } 
      xSAttributeUseImpl.fAttrDecl = xSAttributeDecl;
      xSAttributeUseImpl.fUse = xInt.shortValue();
      xSAttributeUseImpl.fConstraintType = b;
      if (str1 != null) {
        xSAttributeUseImpl.fDefault = new ValidatedInfo();
        xSAttributeUseImpl.fDefault.normalizedValue = str1;
      } 
      if (paramElement.getAttributeNode(SchemaSymbols.ATT_REF) == null) {
        xSAttributeUseImpl.fAnnotations = xSAttributeDecl.getAnnotations();
      } else {
        XSObjectListImpl xSObjectListImpl;
        if (xSAnnotationImpl != null) {
          xSObjectListImpl = new XSObjectListImpl();
          ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
        } else {
          xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
        } 
        xSAttributeUseImpl.fAnnotations = xSObjectListImpl;
      } 
    } 
    if (str1 != null && str2 != null)
      reportSchemaError("src-attribute.1", new Object[] { str3 }, paramElement); 
    if (b == 1 && xInt != null && xInt.intValue() != 0) {
      reportSchemaError("src-attribute.2", new Object[] { str3 }, paramElement);
      xSAttributeUseImpl.fUse = 0;
    } 
    if (str1 != null && xSAttributeUseImpl != null) {
      this.fValidationState.setNamespaceSupport(paramXSDocumentInfo.fNamespaceSupport);
      try {
        checkDefaultValid(xSAttributeUseImpl);
      } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
        reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), paramElement);
        reportSchemaError("a-props-correct.2", new Object[] { str3, str1 }, paramElement);
        xSAttributeUseImpl.fDefault = null;
        xSAttributeUseImpl.fConstraintType = 0;
      } 
      if (((XSSimpleType)xSAttributeDecl.getTypeDefinition()).isIDType()) {
        reportSchemaError("a-props-correct.3", new Object[] { str3 }, paramElement);
        xSAttributeUseImpl.fDefault = null;
        xSAttributeUseImpl.fConstraintType = 0;
      } 
      if (xSAttributeUseImpl.fAttrDecl.getConstraintType() == 2 && xSAttributeUseImpl.fConstraintType != 0 && (xSAttributeUseImpl.fConstraintType != 2 || !(xSAttributeUseImpl.fAttrDecl.getValInfo()).actualValue.equals(xSAttributeUseImpl.fDefault.actualValue))) {
        reportSchemaError("au-props-correct.2", new Object[] { str3, xSAttributeUseImpl.fAttrDecl.getValInfo().stringValue() }, paramElement);
        xSAttributeUseImpl.fDefault = xSAttributeUseImpl.fAttrDecl.getValInfo();
        xSAttributeUseImpl.fConstraintType = 2;
      } 
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSAttributeUseImpl;
  }
  
  protected XSAttributeDecl traverseGlobal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, true, paramXSDocumentInfo);
    XSAttributeDecl xSAttributeDecl = traverseNamedAttr(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar, true, null);
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSAttributeDecl;
  }
  
  XSAttributeDecl traverseNamedAttr(Element paramElement, Object[] paramArrayOfObject, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, boolean paramBoolean, XSComplexTypeDecl paramXSComplexTypeDecl) {
    XSObjectListImpl xSObjectListImpl;
    String str1 = (String)paramArrayOfObject[XSAttributeChecker.ATTIDX_DEFAULT];
    String str2 = (String)paramArrayOfObject[XSAttributeChecker.ATTIDX_FIXED];
    XInt xInt = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_FORM];
    String str3 = (String)paramArrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    QName qName = (QName)paramArrayOfObject[XSAttributeChecker.ATTIDX_TYPE];
    XSAttributeDecl xSAttributeDecl = null;
    if (this.fSchemaHandler.fDeclPool != null) {
      xSAttributeDecl = this.fSchemaHandler.fDeclPool.getAttributeDecl();
    } else {
      xSAttributeDecl = new XSAttributeDecl();
    } 
    if (str3 != null)
      str3 = this.fSymbolTable.addSymbol(str3); 
    String str4 = null;
    XSComplexTypeDecl xSComplexTypeDecl = null;
    int i = 0;
    if (paramBoolean) {
      str4 = paramXSDocumentInfo.fTargetNamespace;
      i = 1;
    } else {
      if (paramXSComplexTypeDecl != null) {
        xSComplexTypeDecl = paramXSComplexTypeDecl;
        i = 2;
      } 
      if (xInt != null) {
        if (xInt.intValue() == 1)
          str4 = paramXSDocumentInfo.fTargetNamespace; 
      } else if (paramXSDocumentInfo.fAreLocalAttributesQualified) {
        str4 = paramXSDocumentInfo.fTargetNamespace;
      } 
    } 
    ValidatedInfo validatedInfo = null;
    int j = 0;
    if (paramBoolean)
      if (str2 != null) {
        validatedInfo = new ValidatedInfo();
        validatedInfo.normalizedValue = str2;
        j = 2;
      } else if (str1 != null) {
        validatedInfo = new ValidatedInfo();
        validatedInfo.normalizedValue = str1;
        j = 1;
      }  
    Element element = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl xSAnnotationImpl = null;
    if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
      xSAnnotationImpl = traverseAnnotationDecl(element, paramArrayOfObject, false, paramXSDocumentInfo);
      element = DOMUtil.getNextSiblingElement(element);
    } else {
      String str = DOMUtil.getSyntheticAnnotation(paramElement);
      if (str != null)
        xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, str, paramArrayOfObject, false, paramXSDocumentInfo); 
    } 
    XSSimpleType xSSimpleType = null;
    boolean bool = false;
    if (element != null) {
      xSObjectListImpl = DOMUtil.getLocalName(element);
      if (xSObjectListImpl.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
        xSSimpleType = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar);
        bool = true;
        element = DOMUtil.getNextSiblingElement(element);
      } 
    } 
    if (xSSimpleType == null && qName != null) {
      xSObjectListImpl = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 7, qName, paramElement);
      if (xSObjectListImpl != null && xSObjectListImpl.getTypeCategory() == 16) {
        xSSimpleType = (XSSimpleType)xSObjectListImpl;
      } else {
        reportSchemaError("src-resolve", new Object[] { qName.rawname, "simpleType definition" }, paramElement);
        if (xSObjectListImpl == null)
          xSAttributeDecl.fUnresolvedTypeName = qName; 
      } 
    } 
    if (xSSimpleType == null)
      xSSimpleType = SchemaGrammar.fAnySimpleType; 
    if (xSAnnotationImpl != null) {
      xSObjectListImpl = new XSObjectListImpl();
      ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
    } else {
      xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
    } 
    xSAttributeDecl.setValues(str3, str4, xSSimpleType, j, i, validatedInfo, xSComplexTypeDecl, xSObjectListImpl);
    if (str3 == null) {
      if (paramBoolean) {
        reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ATTRIBUTE, SchemaSymbols.ATT_NAME }, paramElement);
      } else {
        reportSchemaError("src-attribute.3.1", null, paramElement);
      } 
      str3 = "(no name)";
    } 
    if (element != null)
      reportSchemaError("s4s-elt-must-match.1", new Object[] { str3, "(annotation?, (simpleType?))", DOMUtil.getLocalName(element) }, element); 
    if (str1 != null && str2 != null)
      reportSchemaError("src-attribute.1", new Object[] { str3 }, paramElement); 
    if (bool && qName != null)
      reportSchemaError("src-attribute.4", new Object[] { str3 }, paramElement); 
    checkNotationType(str3, xSSimpleType, paramElement);
    if (validatedInfo != null) {
      this.fValidationState.setNamespaceSupport(paramXSDocumentInfo.fNamespaceSupport);
      try {
        checkDefaultValid(xSAttributeDecl);
      } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
        reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), paramElement);
        reportSchemaError("a-props-correct.2", new Object[] { str3, validatedInfo.normalizedValue }, paramElement);
        validatedInfo = null;
        j = 0;
        xSAttributeDecl.setValues(str3, str4, xSSimpleType, j, i, validatedInfo, xSComplexTypeDecl, xSObjectListImpl);
      } 
    } 
    if (validatedInfo != null && xSSimpleType.isIDType()) {
      reportSchemaError("a-props-correct.3", new Object[] { str3 }, paramElement);
      validatedInfo = null;
      j = 0;
      xSAttributeDecl.setValues(str3, str4, xSSimpleType, j, i, validatedInfo, xSComplexTypeDecl, xSObjectListImpl);
    } 
    if (str3 != null && str3.equals(XMLSymbols.PREFIX_XMLNS)) {
      reportSchemaError("no-xmlns", null, paramElement);
      return null;
    } 
    if (str4 != null && str4.equals(SchemaSymbols.URI_XSI)) {
      reportSchemaError("no-xsi", new Object[] { SchemaSymbols.URI_XSI }, paramElement);
      return null;
    } 
    if (str3.equals("(no name)"))
      return null; 
    if (paramBoolean) {
      if (paramSchemaGrammar.getGlobalAttributeDecl(str3) == null)
        paramSchemaGrammar.addGlobalAttributeDecl(xSAttributeDecl); 
      String str = this.fSchemaHandler.schemaDocument2SystemId(paramXSDocumentInfo);
      XSAttributeDecl xSAttributeDecl1 = paramSchemaGrammar.getGlobalAttributeDecl(str3, str);
      if (xSAttributeDecl1 == null)
        paramSchemaGrammar.addGlobalAttributeDecl(xSAttributeDecl, str); 
      if (this.fSchemaHandler.fTolerateDuplicates) {
        if (xSAttributeDecl1 != null)
          xSAttributeDecl = xSAttributeDecl1; 
        this.fSchemaHandler.addGlobalAttributeDecl(xSAttributeDecl);
      } 
    } 
    return xSAttributeDecl;
  }
  
  void checkDefaultValid(XSAttributeDecl paramXSAttributeDecl) throws InvalidDatatypeValueException {
    ((XSSimpleType)paramXSAttributeDecl.getTypeDefinition()).validate((paramXSAttributeDecl.getValInfo()).normalizedValue, this.fValidationState, paramXSAttributeDecl.getValInfo());
    ((XSSimpleType)paramXSAttributeDecl.getTypeDefinition()).validate(paramXSAttributeDecl.getValInfo().stringValue(), this.fValidationState, paramXSAttributeDecl.getValInfo());
  }
  
  void checkDefaultValid(XSAttributeUseImpl paramXSAttributeUseImpl) throws InvalidDatatypeValueException {
    ((XSSimpleType)paramXSAttributeUseImpl.fAttrDecl.getTypeDefinition()).validate(paramXSAttributeUseImpl.fDefault.normalizedValue, this.fValidationState, paramXSAttributeUseImpl.fDefault);
    ((XSSimpleType)paramXSAttributeUseImpl.fAttrDecl.getTypeDefinition()).validate(paramXSAttributeUseImpl.fDefault.stringValue(), this.fValidationState, paramXSAttributeUseImpl.fDefault);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDAttributeTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */