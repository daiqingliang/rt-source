package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.w3c.dom.Element;

class XSDAbstractIDConstraintTraverser extends XSDAbstractTraverser {
  public XSDAbstractIDConstraintTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  boolean traverseIdentityConstraint(IdentityConstraint paramIdentityConstraint, Element paramElement, XSDocumentInfo paramXSDocumentInfo, Object[] paramArrayOfObject) {
    Element element1 = DOMUtil.getFirstChildElement(paramElement);
    if (element1 == null) {
      reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, paramElement);
      return false;
    } 
    if (DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION)) {
      paramIdentityConstraint.addAnnotation(traverseAnnotationDecl(element1, paramArrayOfObject, false, paramXSDocumentInfo));
      element1 = DOMUtil.getNextSiblingElement(element1);
      if (element1 == null) {
        reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, paramElement);
        return false;
      } 
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(paramElement);
      if (str1 != null)
        paramIdentityConstraint.addAnnotation(traverseSyntheticAnnotation(paramElement, str1, paramArrayOfObject, false, paramXSDocumentInfo)); 
    } 
    if (!DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_SELECTOR)) {
      reportSchemaError("s4s-elt-must-match.1", new Object[] { "identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_SELECTOR }, element1);
      return false;
    } 
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(element1, false, paramXSDocumentInfo);
    Element element2 = DOMUtil.getFirstChildElement(element1);
    if (element2 != null) {
      if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
        paramIdentityConstraint.addAnnotation(traverseAnnotationDecl(element2, arrayOfObject, false, paramXSDocumentInfo));
        element2 = DOMUtil.getNextSiblingElement(element2);
      } else {
        reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(element2) }, element2);
      } 
      if (element2 != null)
        reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(element2) }, element2); 
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(element1);
      if (str1 != null)
        paramIdentityConstraint.addAnnotation(traverseSyntheticAnnotation(paramElement, str1, arrayOfObject, false, paramXSDocumentInfo)); 
    } 
    String str = (String)arrayOfObject[XSAttributeChecker.ATTIDX_XPATH];
    if (str == null) {
      reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_SELECTOR, SchemaSymbols.ATT_XPATH }, element1);
      return false;
    } 
    str = XMLChar.trim(str);
    Selector.XPath xPath = null;
    try {
      xPath = new Selector.XPath(str, this.fSymbolTable, paramXSDocumentInfo.fNamespaceSupport);
      Selector selector = new Selector(xPath, paramIdentityConstraint);
      paramIdentityConstraint.setSelector(selector);
    } catch (XPathException xPathException) {
      reportSchemaError(xPathException.getKey(), new Object[] { str }, element1);
      this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
      return false;
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    Element element3 = DOMUtil.getNextSiblingElement(element1);
    if (element3 == null) {
      reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, element1);
      return false;
    } 
    while (element3 != null) {
      if (!DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_FIELD)) {
        reportSchemaError("s4s-elt-must-match.1", new Object[] { "identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_FIELD }, element3);
        element3 = DOMUtil.getNextSiblingElement(element3);
        continue;
      } 
      arrayOfObject = this.fAttrChecker.checkAttributes(element3, false, paramXSDocumentInfo);
      Element element = DOMUtil.getFirstChildElement(element3);
      if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
        paramIdentityConstraint.addAnnotation(traverseAnnotationDecl(element, arrayOfObject, false, paramXSDocumentInfo));
        element = DOMUtil.getNextSiblingElement(element);
      } 
      if (element != null) {
        reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_FIELD, "(annotation?)", DOMUtil.getLocalName(element) }, element);
      } else {
        String str2 = DOMUtil.getSyntheticAnnotation(element3);
        if (str2 != null)
          paramIdentityConstraint.addAnnotation(traverseSyntheticAnnotation(paramElement, str2, arrayOfObject, false, paramXSDocumentInfo)); 
      } 
      String str1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_XPATH];
      if (str1 == null) {
        reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_FIELD, SchemaSymbols.ATT_XPATH }, element3);
        this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
        return false;
      } 
      str1 = XMLChar.trim(str1);
      try {
        Field.XPath xPath1 = new Field.XPath(str1, this.fSymbolTable, paramXSDocumentInfo.fNamespaceSupport);
        Field field = new Field(xPath1, paramIdentityConstraint);
        paramIdentityConstraint.addField(field);
      } catch (XPathException xPathException) {
        reportSchemaError(xPathException.getKey(), new Object[] { str1 }, element3);
        this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
        return false;
      } 
      element3 = DOMUtil.getNextSiblingElement(element3);
      this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    } 
    return (paramIdentityConstraint.getFieldCount() > 0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDAbstractIDConstraintTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */