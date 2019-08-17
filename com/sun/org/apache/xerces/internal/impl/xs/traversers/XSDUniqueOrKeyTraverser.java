package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

class XSDUniqueOrKeyTraverser extends XSDAbstractIDConstraintTraverser {
  public XSDUniqueOrKeyTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  void traverse(Element paramElement, XSElementDecl paramXSElementDecl, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    String str = (String)arrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    if (str == null) {
      reportSchemaError("s4s-att-must-appear", new Object[] { DOMUtil.getLocalName(paramElement), SchemaSymbols.ATT_NAME }, paramElement);
      this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
      return;
    } 
    UniqueOrKey uniqueOrKey = null;
    if (DOMUtil.getLocalName(paramElement).equals(SchemaSymbols.ELT_UNIQUE)) {
      uniqueOrKey = new UniqueOrKey(paramXSDocumentInfo.fTargetNamespace, str, paramXSElementDecl.fName, (short)3);
    } else {
      uniqueOrKey = new UniqueOrKey(paramXSDocumentInfo.fTargetNamespace, str, paramXSElementDecl.fName, (short)1);
    } 
    if (traverseIdentityConstraint(uniqueOrKey, paramElement, paramXSDocumentInfo, arrayOfObject)) {
      if (paramSchemaGrammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName()) == null)
        paramSchemaGrammar.addIDConstraintDecl(paramXSElementDecl, uniqueOrKey); 
      String str1 = this.fSchemaHandler.schemaDocument2SystemId(paramXSDocumentInfo);
      IdentityConstraint identityConstraint = paramSchemaGrammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName(), str1);
      if (identityConstraint == null)
        paramSchemaGrammar.addIDConstraintDecl(paramXSElementDecl, uniqueOrKey, str1); 
      if (this.fSchemaHandler.fTolerateDuplicates) {
        if (identityConstraint != null && identityConstraint instanceof UniqueOrKey)
          uniqueOrKey = uniqueOrKey; 
        this.fSchemaHandler.addIDConstraintDecl(uniqueOrKey);
      } 
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDUniqueOrKeyTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */