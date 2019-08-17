package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.QName;
import org.w3c.dom.Element;

class XSDGroupTraverser extends XSDAbstractParticleTraverser {
  XSDGroupTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  XSParticleDecl traverseLocal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    QName qName = (QName)arrayOfObject[XSAttributeChecker.ATTIDX_REF];
    XInt xInt1 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MINOCCURS];
    XInt xInt2 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MAXOCCURS];
    XSGroupDecl xSGroupDecl = null;
    if (qName == null) {
      reportSchemaError("s4s-att-must-appear", new Object[] { "group (local)", "ref" }, paramElement);
    } else {
      xSGroupDecl = (XSGroupDecl)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 4, qName, paramElement);
    } 
    XSAnnotationImpl xSAnnotationImpl = null;
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
      reportSchemaError("s4s-elt-must-match.1", new Object[] { "group (local)", "(annotation?)", DOMUtil.getLocalName(paramElement) }, paramElement); 
    int i = xInt1.intValue();
    int j = xInt2.intValue();
    XSParticleDecl xSParticleDecl = null;
    if (xSGroupDecl != null && xSGroupDecl.fModelGroup != null && (i != 0 || j != 0)) {
      if (this.fSchemaHandler.fDeclPool != null) {
        xSParticleDecl = this.fSchemaHandler.fDeclPool.getParticleDecl();
      } else {
        xSParticleDecl = new XSParticleDecl();
      } 
      xSParticleDecl.fType = 3;
      xSParticleDecl.fValue = xSGroupDecl.fModelGroup;
      xSParticleDecl.fMinOccurs = i;
      xSParticleDecl.fMaxOccurs = j;
      if (xSGroupDecl.fModelGroup.fCompositor == 103) {
        Long long = (Long)arrayOfObject[XSAttributeChecker.ATTIDX_FROMDEFAULT];
        xSParticleDecl = checkOccurrences(xSParticleDecl, SchemaSymbols.ELT_GROUP, (Element)paramElement.getParentNode(), 2, long.longValue());
      } 
      if (qName != null) {
        XSObjectListImpl xSObjectListImpl;
        if (xSAnnotationImpl != null) {
          xSObjectListImpl = new XSObjectListImpl();
          ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
        } else {
          xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
        } 
        xSParticleDecl.fAnnotations = xSObjectListImpl;
      } else {
        xSParticleDecl.fAnnotations = xSGroupDecl.fAnnotations;
      } 
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSParticleDecl;
  }
  
  XSGroupDecl traverseGlobal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, true, paramXSDocumentInfo);
    String str = (String)arrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    if (str == null)
      reportSchemaError("s4s-att-must-appear", new Object[] { "group (global)", "name" }, paramElement); 
    XSGroupDecl xSGroupDecl = new XSGroupDecl();
    XSParticleDecl xSParticleDecl = null;
    Element element = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl xSAnnotationImpl = null;
    if (element == null) {
      reportSchemaError("s4s-elt-must-match.2", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))" }, paramElement);
    } else {
      String str1 = element.getLocalName();
      if (str1.equals(SchemaSymbols.ELT_ANNOTATION)) {
        xSAnnotationImpl = traverseAnnotationDecl(element, arrayOfObject, true, paramXSDocumentInfo);
        element = DOMUtil.getNextSiblingElement(element);
        if (element != null)
          str1 = element.getLocalName(); 
      } else {
        String str2 = DOMUtil.getSyntheticAnnotation(paramElement);
        if (str2 != null)
          xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, str2, arrayOfObject, false, paramXSDocumentInfo); 
      } 
      if (element == null) {
        reportSchemaError("s4s-elt-must-match.2", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))" }, paramElement);
      } else if (str1.equals(SchemaSymbols.ELT_ALL)) {
        xSParticleDecl = traverseAll(element, paramXSDocumentInfo, paramSchemaGrammar, 4, xSGroupDecl);
      } else if (str1.equals(SchemaSymbols.ELT_CHOICE)) {
        xSParticleDecl = traverseChoice(element, paramXSDocumentInfo, paramSchemaGrammar, 4, xSGroupDecl);
      } else if (str1.equals(SchemaSymbols.ELT_SEQUENCE)) {
        xSParticleDecl = traverseSequence(element, paramXSDocumentInfo, paramSchemaGrammar, 4, xSGroupDecl);
      } else {
        reportSchemaError("s4s-elt-must-match.1", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))", DOMUtil.getLocalName(element) }, element);
      } 
      if (element != null && DOMUtil.getNextSiblingElement(element) != null)
        reportSchemaError("s4s-elt-must-match.1", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(element)) }, DOMUtil.getNextSiblingElement(element)); 
    } 
    if (str != null) {
      XSObjectListImpl xSObjectListImpl;
      xSGroupDecl.fName = str;
      xSGroupDecl.fTargetNamespace = paramXSDocumentInfo.fTargetNamespace;
      if (xSParticleDecl == null)
        xSParticleDecl = XSConstraints.getEmptySequence(); 
      xSGroupDecl.fModelGroup = (XSModelGroupImpl)xSParticleDecl.fValue;
      if (xSAnnotationImpl != null) {
        xSObjectListImpl = new XSObjectListImpl();
        ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
      } else {
        xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
      } 
      xSGroupDecl.fAnnotations = xSObjectListImpl;
      if (paramSchemaGrammar.getGlobalGroupDecl(xSGroupDecl.fName) == null)
        paramSchemaGrammar.addGlobalGroupDecl(xSGroupDecl); 
      String str1 = this.fSchemaHandler.schemaDocument2SystemId(paramXSDocumentInfo);
      XSGroupDecl xSGroupDecl1 = paramSchemaGrammar.getGlobalGroupDecl(xSGroupDecl.fName, str1);
      if (xSGroupDecl1 == null)
        paramSchemaGrammar.addGlobalGroupDecl(xSGroupDecl, str1); 
      if (this.fSchemaHandler.fTolerateDuplicates) {
        if (xSGroupDecl1 != null)
          xSGroupDecl = xSGroupDecl1; 
        this.fSchemaHandler.addGlobalGroupDecl(xSGroupDecl);
      } 
    } else {
      xSGroupDecl = null;
    } 
    if (xSGroupDecl != null) {
      Object object = this.fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(4, new QName(XMLSymbols.EMPTY_STRING, str, str, paramXSDocumentInfo.fTargetNamespace), paramXSDocumentInfo, paramElement);
      if (object != null)
        paramSchemaGrammar.addRedefinedGroupDecl(xSGroupDecl, (XSGroupDecl)object, this.fSchemaHandler.element2Locator(paramElement)); 
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSGroupDecl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDGroupTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */