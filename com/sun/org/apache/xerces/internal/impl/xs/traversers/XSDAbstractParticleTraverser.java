package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import org.w3c.dom.Element;

abstract class XSDAbstractParticleTraverser extends XSDAbstractTraverser {
  ParticleArray fPArray = new ParticleArray();
  
  XSDAbstractParticleTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  XSParticleDecl traverseAll(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, int paramInt, XSObject paramXSObject) {
    XSObjectListImpl xSObjectListImpl;
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    Element element = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl xSAnnotationImpl = null;
    if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
      xSAnnotationImpl = traverseAnnotationDecl(element, arrayOfObject, false, paramXSDocumentInfo);
      element = DOMUtil.getNextSiblingElement(element);
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(paramElement);
      if (str1 != null)
        xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, str1, arrayOfObject, false, paramXSDocumentInfo); 
    } 
    String str = null;
    this.fPArray.pushContext();
    while (element != null) {
      XSParticleDecl xSParticleDecl1 = null;
      str = DOMUtil.getLocalName(element);
      if (str.equals(SchemaSymbols.ELT_ELEMENT)) {
        xSParticleDecl1 = this.fSchemaHandler.fElementTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar, 1, paramXSObject);
      } else {
        Object[] arrayOfObject1 = { "all", "(annotation?, element*)", DOMUtil.getLocalName(element) };
        reportSchemaError("s4s-elt-must-match.1", arrayOfObject1, element);
      } 
      if (xSParticleDecl1 != null)
        this.fPArray.addParticle(xSParticleDecl1); 
      element = DOMUtil.getNextSiblingElement(element);
    } 
    XSParticleDecl xSParticleDecl = null;
    XInt xInt1 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MINOCCURS];
    XInt xInt2 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MAXOCCURS];
    Long long = (Long)arrayOfObject[XSAttributeChecker.ATTIDX_FROMDEFAULT];
    XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
    xSModelGroupImpl.fCompositor = 103;
    xSModelGroupImpl.fParticleCount = this.fPArray.getParticleCount();
    xSModelGroupImpl.fParticles = this.fPArray.popContext();
    if (xSAnnotationImpl != null) {
      xSObjectListImpl = new XSObjectListImpl();
      ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
    } else {
      xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
    } 
    xSModelGroupImpl.fAnnotations = xSObjectListImpl;
    xSParticleDecl = new XSParticleDecl();
    xSParticleDecl.fType = 3;
    xSParticleDecl.fMinOccurs = xInt1.intValue();
    xSParticleDecl.fMaxOccurs = xInt2.intValue();
    xSParticleDecl.fValue = xSModelGroupImpl;
    xSParticleDecl.fAnnotations = xSObjectListImpl;
    xSParticleDecl = checkOccurrences(xSParticleDecl, SchemaSymbols.ELT_ALL, (Element)paramElement.getParentNode(), paramInt, long.longValue());
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSParticleDecl;
  }
  
  XSParticleDecl traverseSequence(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, int paramInt, XSObject paramXSObject) { return traverseSeqChoice(paramElement, paramXSDocumentInfo, paramSchemaGrammar, paramInt, false, paramXSObject); }
  
  XSParticleDecl traverseChoice(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, int paramInt, XSObject paramXSObject) { return traverseSeqChoice(paramElement, paramXSDocumentInfo, paramSchemaGrammar, paramInt, true, paramXSObject); }
  
  private XSParticleDecl traverseSeqChoice(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, int paramInt, boolean paramBoolean, XSObject paramXSObject) {
    XSObjectListImpl xSObjectListImpl;
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    Element element = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl xSAnnotationImpl = null;
    if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
      xSAnnotationImpl = traverseAnnotationDecl(element, arrayOfObject, false, paramXSDocumentInfo);
      element = DOMUtil.getNextSiblingElement(element);
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(paramElement);
      if (str1 != null)
        xSAnnotationImpl = traverseSyntheticAnnotation(paramElement, str1, arrayOfObject, false, paramXSDocumentInfo); 
    } 
    String str = null;
    this.fPArray.pushContext();
    while (element != null) {
      XSParticleDecl xSParticleDecl1 = null;
      str = DOMUtil.getLocalName(element);
      if (str.equals(SchemaSymbols.ELT_ELEMENT)) {
        xSParticleDecl1 = this.fSchemaHandler.fElementTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar, 0, paramXSObject);
      } else if (str.equals(SchemaSymbols.ELT_GROUP)) {
        xSParticleDecl1 = this.fSchemaHandler.fGroupTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar);
        if (hasAllContent(xSParticleDecl1)) {
          xSParticleDecl1 = null;
          reportSchemaError("cos-all-limited.1.2", null, element);
        } 
      } else if (str.equals(SchemaSymbols.ELT_CHOICE)) {
        xSParticleDecl1 = traverseChoice(element, paramXSDocumentInfo, paramSchemaGrammar, 0, paramXSObject);
      } else if (str.equals(SchemaSymbols.ELT_SEQUENCE)) {
        xSParticleDecl1 = traverseSequence(element, paramXSDocumentInfo, paramSchemaGrammar, 0, paramXSObject);
      } else if (str.equals(SchemaSymbols.ELT_ANY)) {
        xSParticleDecl1 = this.fSchemaHandler.fWildCardTraverser.traverseAny(element, paramXSDocumentInfo, paramSchemaGrammar);
      } else {
        Object[] arrayOfObject1;
        if (paramBoolean) {
          arrayOfObject1 = new Object[] { "choice", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(element) };
        } else {
          arrayOfObject1 = new Object[] { "sequence", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(element) };
        } 
        reportSchemaError("s4s-elt-must-match.1", arrayOfObject1, element);
      } 
      if (xSParticleDecl1 != null)
        this.fPArray.addParticle(xSParticleDecl1); 
      element = DOMUtil.getNextSiblingElement(element);
    } 
    XSParticleDecl xSParticleDecl = null;
    XInt xInt1 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MINOCCURS];
    XInt xInt2 = (XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MAXOCCURS];
    Long long = (Long)arrayOfObject[XSAttributeChecker.ATTIDX_FROMDEFAULT];
    XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
    xSModelGroupImpl.fCompositor = paramBoolean ? 101 : 102;
    xSModelGroupImpl.fParticleCount = this.fPArray.getParticleCount();
    xSModelGroupImpl.fParticles = this.fPArray.popContext();
    if (xSAnnotationImpl != null) {
      xSObjectListImpl = new XSObjectListImpl();
      ((XSObjectListImpl)xSObjectListImpl).addXSObject(xSAnnotationImpl);
    } else {
      xSObjectListImpl = XSObjectListImpl.EMPTY_LIST;
    } 
    xSModelGroupImpl.fAnnotations = xSObjectListImpl;
    xSParticleDecl = new XSParticleDecl();
    xSParticleDecl.fType = 3;
    xSParticleDecl.fMinOccurs = xInt1.intValue();
    xSParticleDecl.fMaxOccurs = xInt2.intValue();
    xSParticleDecl.fValue = xSModelGroupImpl;
    xSParticleDecl.fAnnotations = xSObjectListImpl;
    xSParticleDecl = checkOccurrences(xSParticleDecl, paramBoolean ? SchemaSymbols.ELT_CHOICE : SchemaSymbols.ELT_SEQUENCE, (Element)paramElement.getParentNode(), paramInt, long.longValue());
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSParticleDecl;
  }
  
  protected boolean hasAllContent(XSParticleDecl paramXSParticleDecl) { return (paramXSParticleDecl != null && paramXSParticleDecl.fType == 3) ? ((((XSModelGroupImpl)paramXSParticleDecl.fValue).fCompositor == 103)) : false; }
  
  protected static class ParticleArray {
    XSParticleDecl[] fParticles = new XSParticleDecl[10];
    
    int[] fPos = new int[5];
    
    int fContextCount = 0;
    
    void pushContext() {
      this.fContextCount++;
      if (this.fContextCount == this.fPos.length) {
        int i = this.fContextCount * 2;
        int[] arrayOfInt = new int[i];
        System.arraycopy(this.fPos, 0, arrayOfInt, 0, this.fContextCount);
        this.fPos = arrayOfInt;
      } 
      this.fPos[this.fContextCount] = this.fPos[this.fContextCount - 1];
    }
    
    int getParticleCount() { return this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1]; }
    
    void addParticle(XSParticleDecl param1XSParticleDecl) {
      if (this.fPos[this.fContextCount] == this.fParticles.length) {
        int i = this.fPos[this.fContextCount] * 2;
        XSParticleDecl[] arrayOfXSParticleDecl = new XSParticleDecl[i];
        System.arraycopy(this.fParticles, 0, arrayOfXSParticleDecl, 0, this.fPos[this.fContextCount]);
        this.fParticles = arrayOfXSParticleDecl;
      } 
      this.fPos[this.fContextCount] = this.fPos[this.fContextCount] + 1;
      this.fParticles[this.fPos[this.fContextCount]] = param1XSParticleDecl;
    }
    
    XSParticleDecl[] popContext() {
      int i = this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1];
      XSParticleDecl[] arrayOfXSParticleDecl = null;
      if (i != 0) {
        arrayOfXSParticleDecl = new XSParticleDecl[i];
        System.arraycopy(this.fParticles, this.fPos[this.fContextCount - 1], arrayOfXSParticleDecl, 0, i);
        for (int j = this.fPos[this.fContextCount - 1]; j < this.fPos[this.fContextCount]; j++)
          this.fParticles[j] = null; 
      } 
      this.fContextCount--;
      return arrayOfXSParticleDecl;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDAbstractParticleTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */