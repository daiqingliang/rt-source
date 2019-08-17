package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Locale;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

class XSDElementTraverser extends XSDAbstractTraverser {
  protected final XSElementDecl fTempElementDecl = new XSElementDecl();
  
  boolean fDeferTraversingLocalElements;
  
  XSDElementTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  XSParticleDecl traverseLocal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, int paramInt, XSObject paramXSObject) {
    XSParticleDecl xSParticleDecl = null;
    if (this.fSchemaHandler.fDeclPool != null) {
      xSParticleDecl = this.fSchemaHandler.fDeclPool.getParticleDecl();
    } else {
      xSParticleDecl = new XSParticleDecl();
    } 
    if (this.fDeferTraversingLocalElements) {
      xSParticleDecl.fType = 1;
      Attr attr = paramElement.getAttributeNode(SchemaSymbols.ATT_MINOCCURS);
      if (attr != null) {
        String str = attr.getValue();
        try {
          int i = Integer.parseInt(XMLChar.trim(str));
          if (i >= 0)
            xSParticleDecl.fMinOccurs = i; 
        } catch (NumberFormatException numberFormatException) {}
      } 
      this.fSchemaHandler.fillInLocalElemInfo(paramElement, paramXSDocumentInfo, paramInt, paramXSObject, xSParticleDecl);
    } else {
      traverseLocal(xSParticleDecl, paramElement, paramXSDocumentInfo, paramSchemaGrammar, paramInt, paramXSObject, null);
      if (xSParticleDecl.fType == 0)
        xSParticleDecl = null; 
    } 
    return xSParticleDecl;
  }
  
  protected void traverseLocal(XSParticleDecl paramXSParticleDecl, Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, int paramInt, XSObject paramXSObject, String[] paramArrayOfString) {
    if (paramArrayOfString != null)
      paramXSDocumentInfo.fNamespaceSupport.setEffectiveContext(paramArrayOfString); 
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    QName qName = (QName)arrayOfObject[XSAttributeChecker.ATTIDX_REF];
    XInt xInt1 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MINOCCURS];
    XInt xInt2 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MAXOCCURS];
    XSElementDecl xSElementDecl = null;
    XSAnnotationImpl xSAnnotationImpl = null;
    if (paramElement.getAttributeNode(SchemaSymbols.ATT_REF) != null) {
      if (qName != null) {
        xSElementDecl = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 3, qName, paramElement);
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
          reportSchemaError("src-element.2.2", new Object[] { qName.rawname, DOMUtil.getLocalName(element) }, element); 
      } else {
        xSElementDecl = null;
      } 
    } else {
      xSElementDecl = traverseNamedElement(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar, false, paramXSObject);
    } 
    paramXSParticleDecl.fMinOccurs = xInt1.intValue();
    paramXSParticleDecl.fMaxOccurs = xInt2.intValue();
    if (xSElementDecl != null) {
      paramXSParticleDecl.fType = 1;
      paramXSParticleDecl.fValue = xSElementDecl;
    } else {
      paramXSParticleDecl.fType = 0;
    } 
    if (qName != null) {
      XSObjectListImpl xSObjectListImpl;
      if (xSAnnotationImpl != null) {
        xSObjectListImpl = new XSObjectListImpl();
        ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
      } else {
        xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
      } 
      paramXSParticleDecl.fAnnotations = xSObjectListImpl;
    } else {
      paramXSParticleDecl.fAnnotations = (xSElementDecl != null) ? xSElementDecl.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    } 
    Long long = (Long)arrayOfObject[XSAttributeChecker.ATTIDX_FROMDEFAULT];
    checkOccurrences(paramXSParticleDecl, SchemaSymbols.ELT_ELEMENT, (Element)paramElement.getParentNode(), paramInt, long.longValue());
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
  }
  
  XSElementDecl traverseGlobal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, true, paramXSDocumentInfo);
    XSElementDecl xSElementDecl = traverseNamedElement(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar, true, null);
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSElementDecl;
  }
  
  XSElementDecl traverseNamedElement(Element paramElement, Object[] paramArrayOfObject, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, boolean paramBoolean, XSObject paramXSObject) {
    XSObjectListImpl xSObjectListImpl;
    Boolean bool1 = (Boolean)paramArrayOfObject[XSAttributeChecker.ATTIDX_ABSTRACT];
    XInt xInt1 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_BLOCK];
    String str1 = (String)paramArrayOfObject[XSAttributeChecker.ATTIDX_DEFAULT];
    XInt xInt2 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_FINAL];
    String str2 = (String)paramArrayOfObject[XSAttributeChecker.ATTIDX_FIXED];
    XInt xInt3 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_FORM];
    String str3 = (String)paramArrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    Boolean bool2 = (Boolean)paramArrayOfObject[XSAttributeChecker.ATTIDX_NILLABLE];
    QName qName1 = (QName)paramArrayOfObject[XSAttributeChecker.ATTIDX_SUBSGROUP];
    QName qName2 = (QName)paramArrayOfObject[XSAttributeChecker.ATTIDX_TYPE];
    XSElementDecl xSElementDecl = null;
    if (this.fSchemaHandler.fDeclPool != null) {
      xSElementDecl = this.fSchemaHandler.fDeclPool.getElementDecl();
    } else {
      xSElementDecl = new XSElementDecl();
    } 
    if (str3 != null)
      xSElementDecl.fName = this.fSymbolTable.addSymbol(str3); 
    if (paramBoolean) {
      xSElementDecl.fTargetNamespace = paramXSDocumentInfo.fTargetNamespace;
      xSElementDecl.setIsGlobal();
    } else {
      if (paramXSObject instanceof XSComplexTypeDecl)
        xSElementDecl.setIsLocal((XSComplexTypeDecl)paramXSObject); 
      if (xInt3 != null) {
        if (xInt3.intValue() == 1) {
          xSElementDecl.fTargetNamespace = paramXSDocumentInfo.fTargetNamespace;
        } else {
          xSElementDecl.fTargetNamespace = null;
        } 
      } else if (paramXSDocumentInfo.fAreLocalElementsQualified) {
        xSElementDecl.fTargetNamespace = paramXSDocumentInfo.fTargetNamespace;
      } else {
        xSElementDecl.fTargetNamespace = null;
      } 
    } 
    if (xInt1 == null) {
      xSElementDecl.fBlock = paramXSDocumentInfo.fBlockDefault;
      if (xSElementDecl.fBlock != 31)
        xSElementDecl.fBlock = (short)(xSElementDecl.fBlock & 0x7); 
    } else {
      xSElementDecl.fBlock = xInt1.shortValue();
      if (xSElementDecl.fBlock != 31 && (xSElementDecl.fBlock | 0x7) != 7)
        reportSchemaError("s4s-att-invalid-value", new Object[] { xSElementDecl.fName, "block", "must be (#all | List of (extension | restriction | substitution))" }, paramElement); 
    } 
    xSElementDecl.fFinal = (xInt2 == null) ? paramXSDocumentInfo.fFinalDefault : xInt2.shortValue();
    xSElementDecl.fFinal = (short)(xSElementDecl.fFinal & 0x3);
    if (bool2.booleanValue())
      xSElementDecl.setIsNillable(); 
    if (bool1 != null && bool1.booleanValue())
      xSElementDecl.setIsAbstract(); 
    if (str2 != null) {
      xSElementDecl.fDefault = new ValidatedInfo();
      xSElementDecl.fDefault.normalizedValue = str2;
      xSElementDecl.setConstraintType((short)2);
    } else if (str1 != null) {
      xSElementDecl.fDefault = new ValidatedInfo();
      xSElementDecl.fDefault.normalizedValue = str1;
      xSElementDecl.setConstraintType((short)1);
    } else {
      xSElementDecl.setConstraintType((short)0);
    } 
    if (qName1 != null)
      xSElementDecl.fSubGroup = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 3, qName1, paramElement); 
    Element element = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl xSAnnotationImpl = null;
    if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
      xSAnnotationImpl = traverseAnnotationDecl(element, paramArrayOfObject, false, paramXSDocumentInfo);
      element = DOMUtil.getNextSiblingElement(element);
    } else {
      xSObjectListImpl = DOMUtil.getSyntheticAnnotation(paramElement);
      if (xSObjectListImpl != null)
        xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, xSObjectListImpl, paramArrayOfObject, false, paramXSDocumentInfo); 
    } 
    if (xSAnnotationImpl != null) {
      xSObjectListImpl = new XSObjectListImpl();
      ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
    } else {
      xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
    } 
    xSElementDecl.fAnnotations = xSObjectListImpl;
    XSTypeDefinition xSTypeDefinition = null;
    boolean bool = false;
    if (element != null) {
      String str = DOMUtil.getLocalName(element);
      if (str.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
        xSTypeDefinition = this.fSchemaHandler.fComplexTypeTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar);
        bool = true;
        element = DOMUtil.getNextSiblingElement(element);
      } else if (str.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
        xSTypeDefinition = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar);
        bool = true;
        element = DOMUtil.getNextSiblingElement(element);
      } 
    } 
    if (xSTypeDefinition == null && qName2 != null) {
      xSTypeDefinition = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 7, qName2, paramElement);
      if (xSTypeDefinition == null)
        xSElementDecl.fUnresolvedTypeName = qName2; 
    } 
    if (xSTypeDefinition == null && xSElementDecl.fSubGroup != null)
      xSTypeDefinition = xSElementDecl.fSubGroup.fType; 
    if (xSTypeDefinition == null)
      xSTypeDefinition = SchemaGrammar.fAnyType; 
    xSElementDecl.fType = xSTypeDefinition;
    if (element != null) {
      String str = DOMUtil.getLocalName(element);
      while (element != null && (str.equals(SchemaSymbols.ELT_KEY) || str.equals(SchemaSymbols.ELT_KEYREF) || str.equals(SchemaSymbols.ELT_UNIQUE))) {
        if (str.equals(SchemaSymbols.ELT_KEY) || str.equals(SchemaSymbols.ELT_UNIQUE)) {
          DOMUtil.setHidden(element, this.fSchemaHandler.fHiddenNodes);
          this.fSchemaHandler.fUniqueOrKeyTraverser.traverse(element, xSElementDecl, paramXSDocumentInfo, paramSchemaGrammar);
          if (DOMUtil.getAttrValue(element, SchemaSymbols.ATT_NAME).length() != 0) {
            this.fSchemaHandler;
            this.fSchemaHandler.checkForDuplicateNames((paramXSDocumentInfo.fTargetNamespace == null) ? ("," + DOMUtil.getAttrValue(element, SchemaSymbols.ATT_NAME)) : (paramXSDocumentInfo.fTargetNamespace + "," + DOMUtil.getAttrValue(element, SchemaSymbols.ATT_NAME)), 1, this.fSchemaHandler.getIDRegistry(), this.fSchemaHandler.getIDRegistry_sub(), element, paramXSDocumentInfo);
          } 
        } else if (str.equals(SchemaSymbols.ELT_KEYREF)) {
          this.fSchemaHandler.storeKeyRef(element, paramXSDocumentInfo, xSElementDecl);
        } 
        element = DOMUtil.getNextSiblingElement(element);
        if (element != null)
          str = DOMUtil.getLocalName(element); 
      } 
    } 
    if (str3 == null) {
      if (paramBoolean) {
        reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ELEMENT, SchemaSymbols.ATT_NAME }, paramElement);
      } else {
        reportSchemaError("src-element.2.1", null, paramElement);
      } 
      str3 = "(no name)";
    } 
    if (element != null)
      reportSchemaError("s4s-elt-must-match.1", new Object[] { str3, "(annotation?, (simpleType | complexType)?, (unique | key | keyref)*))", DOMUtil.getLocalName(element) }, element); 
    if (str1 != null && str2 != null)
      reportSchemaError("src-element.1", new Object[] { str3 }, paramElement); 
    if (bool && qName2 != null)
      reportSchemaError("src-element.3", new Object[] { str3 }, paramElement); 
    checkNotationType(str3, xSTypeDefinition, paramElement);
    if (xSElementDecl.fDefault != null) {
      this.fValidationState.setNamespaceSupport(paramXSDocumentInfo.fNamespaceSupport);
      if (XSConstraints.ElementDefaultValidImmediate(xSElementDecl.fType, xSElementDecl.fDefault.normalizedValue, this.fValidationState, xSElementDecl.fDefault) == null) {
        reportSchemaError("e-props-correct.2", new Object[] { str3, xSElementDecl.fDefault.normalizedValue }, paramElement);
        xSElementDecl.fDefault = null;
        xSElementDecl.setConstraintType((short)0);
      } 
    } 
    if (xSElementDecl.fSubGroup != null && !XSConstraints.checkTypeDerivationOk(xSElementDecl.fType, xSElementDecl.fSubGroup.fType, xSElementDecl.fSubGroup.fFinal)) {
      reportSchemaError("e-props-correct.4", new Object[] { str3, qName1.prefix + ":" + qName1.localpart }, paramElement);
      xSElementDecl.fSubGroup = null;
    } 
    if (xSElementDecl.fDefault != null && ((xSTypeDefinition.getTypeCategory() == 16 && ((XSSimpleType)xSTypeDefinition).isIDType()) || (xSTypeDefinition.getTypeCategory() == 15 && ((XSComplexTypeDecl)xSTypeDefinition).containsTypeID()))) {
      reportSchemaError("e-props-correct.5", new Object[] { xSElementDecl.fName }, paramElement);
      xSElementDecl.fDefault = null;
      xSElementDecl.setConstraintType((short)0);
    } 
    if (xSElementDecl.fName == null)
      return null; 
    if (paramBoolean) {
      paramSchemaGrammar.addGlobalElementDeclAll(xSElementDecl);
      if (paramSchemaGrammar.getGlobalElementDecl(xSElementDecl.fName) == null)
        paramSchemaGrammar.addGlobalElementDecl(xSElementDecl); 
      String str = this.fSchemaHandler.schemaDocument2SystemId(paramXSDocumentInfo);
      XSElementDecl xSElementDecl1 = paramSchemaGrammar.getGlobalElementDecl(xSElementDecl.fName, str);
      if (xSElementDecl1 == null)
        paramSchemaGrammar.addGlobalElementDecl(xSElementDecl, str); 
      if (this.fSchemaHandler.fTolerateDuplicates) {
        if (xSElementDecl1 != null)
          xSElementDecl = xSElementDecl1; 
        this.fSchemaHandler.addGlobalElementDecl(xSElementDecl);
      } 
    } 
    return xSElementDecl;
  }
  
  void reset(SymbolTable paramSymbolTable, boolean paramBoolean, Locale paramLocale) {
    super.reset(paramSymbolTable, paramBoolean, paramLocale);
    this.fDeferTraversingLocalElements = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDElementTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */