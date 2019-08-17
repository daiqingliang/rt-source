package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

class XSDWildcardTraverser extends XSDAbstractTraverser {
  XSDWildcardTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  XSParticleDecl traverseAny(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    XSWildcardDecl xSWildcardDecl = traverseWildcardDecl(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    XSParticleDecl xSParticleDecl = null;
    if (xSWildcardDecl != null) {
      int i = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
      int j = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
      if (j != 0) {
        if (this.fSchemaHandler.fDeclPool != null) {
          xSParticleDecl = this.fSchemaHandler.fDeclPool.getParticleDecl();
        } else {
          xSParticleDecl = new XSParticleDecl();
        } 
        xSParticleDecl.fType = 2;
        xSParticleDecl.fValue = xSWildcardDecl;
        xSParticleDecl.fMinOccurs = i;
        xSParticleDecl.fMaxOccurs = j;
        xSParticleDecl.fAnnotations = xSWildcardDecl.fAnnotations;
      } 
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSParticleDecl;
  }
  
  XSWildcardDecl traverseAnyAttribute(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    XSWildcardDecl xSWildcardDecl = traverseWildcardDecl(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSWildcardDecl;
  }
  
  XSWildcardDecl traverseWildcardDecl(Element paramElement, Object[] paramArrayOfObject, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    XSObjectListImpl xSObjectListImpl;
    XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
    XInt xInt1 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_NAMESPACE];
    xSWildcardDecl.fType = xInt1.shortValue();
    xSWildcardDecl.fNamespaceList = (String[])paramArrayOfObject[XSAttributeChecker.ATTIDX_NAMESPACE_LIST];
    XInt xInt2 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_PROCESSCONTENTS];
    xSWildcardDecl.fProcessContents = xInt2.shortValue();
    Element element = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl xSAnnotationImpl = null;
    if (element != null) {
      if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
        xSAnnotationImpl = traverseAnnotationDecl(element, paramArrayOfObject, false, paramXSDocumentInfo);
        element = DOMUtil.getNextSiblingElement(element);
      } else {
        xSObjectListImpl = DOMUtil.getSyntheticAnnotation(paramElement);
        if (xSObjectListImpl != null)
          xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, xSObjectListImpl, paramArrayOfObject, false, paramXSDocumentInfo); 
      } 
      if (element != null)
        reportSchemaError("s4s-elt-must-match.1", new Object[] { "wildcard", "(annotation?)", DOMUtil.getLocalName(element) }, paramElement); 
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
    xSWildcardDecl.fAnnotations = xSObjectListImpl;
    return xSWildcardDecl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDWildcardTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */