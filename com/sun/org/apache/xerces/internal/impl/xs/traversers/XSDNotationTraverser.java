package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSNotationDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

class XSDNotationTraverser extends XSDAbstractTraverser {
  XSDNotationTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  XSNotationDecl traverse(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    XSObjectListImpl xSObjectListImpl;
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, true, paramXSDocumentInfo);
    String str1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    String str2 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_PUBLIC];
    String str3 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_SYSTEM];
    if (str1 == null) {
      reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_NOTATION, SchemaSymbols.ATT_NAME }, paramElement);
      this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
      return null;
    } 
    if (str3 == null && str2 == null) {
      reportSchemaError("PublicSystemOnNotation", null, paramElement);
      str2 = "missing";
    } 
    XSNotationDecl xSNotationDecl1 = new XSNotationDecl();
    xSNotationDecl1.fName = str1;
    xSNotationDecl1.fTargetNamespace = paramXSDocumentInfo.fTargetNamespace;
    xSNotationDecl1.fPublicId = str2;
    xSNotationDecl1.fSystemId = str3;
    Element element = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl xSAnnotationImpl = null;
    if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
      xSAnnotationImpl = traverseAnnotationDecl(element, arrayOfObject, false, paramXSDocumentInfo);
      element = DOMUtil.getNextSiblingElement(element);
    } else {
      xSObjectListImpl = DOMUtil.getSyntheticAnnotation(paramElement);
      if (xSObjectListImpl != null)
        xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, xSObjectListImpl, arrayOfObject, false, paramXSDocumentInfo); 
    } 
    if (xSAnnotationImpl != null) {
      xSObjectListImpl = new XSObjectListImpl();
      ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
    } else {
      xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
    } 
    xSNotationDecl1.fAnnotations = xSObjectListImpl;
    if (element != null) {
      Object[] arrayOfObject1 = { SchemaSymbols.ELT_NOTATION, "(annotation?)", DOMUtil.getLocalName(element) };
      reportSchemaError("s4s-elt-must-match.1", arrayOfObject1, element);
    } 
    if (paramSchemaGrammar.getGlobalNotationDecl(xSNotationDecl1.fName) == null)
      paramSchemaGrammar.addGlobalNotationDecl(xSNotationDecl1); 
    String str4 = this.fSchemaHandler.schemaDocument2SystemId(paramXSDocumentInfo);
    XSNotationDecl xSNotationDecl2 = paramSchemaGrammar.getGlobalNotationDecl(xSNotationDecl1.fName, str4);
    if (xSNotationDecl2 == null)
      paramSchemaGrammar.addGlobalNotationDecl(xSNotationDecl1, str4); 
    if (this.fSchemaHandler.fTolerateDuplicates) {
      if (xSNotationDecl2 != null)
        xSNotationDecl1 = xSNotationDecl2; 
      this.fSchemaHandler.addGlobalNotationDecl(xSNotationDecl1);
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSNotationDecl1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDNotationTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */