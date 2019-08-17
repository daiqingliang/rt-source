package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Element;

class XSDComplexTypeTraverser extends XSDAbstractParticleTraverser {
  private static final int GLOBAL_NUM = 11;
  
  private static XSParticleDecl fErrorContent = null;
  
  private static XSWildcardDecl fErrorWildcard = null;
  
  private String fName = null;
  
  private String fTargetNamespace = null;
  
  private short fDerivedBy = 2;
  
  private short fFinal = 0;
  
  private short fBlock = 0;
  
  private short fContentType = 0;
  
  private XSTypeDefinition fBaseType = null;
  
  private XSAttributeGroupDecl fAttrGrp = null;
  
  private XSSimpleType fXSSimpleType = null;
  
  private XSParticleDecl fParticle = null;
  
  private boolean fIsAbstract = false;
  
  private XSComplexTypeDecl fComplexTypeDecl = null;
  
  private XSAnnotationImpl[] fAnnotations = null;
  
  private Object[] fGlobalStore = null;
  
  private int fGlobalStorePos = 0;
  
  private static final boolean DEBUG = false;
  
  private static XSParticleDecl getErrorContent() {
    if (fErrorContent == null) {
      XSParticleDecl xSParticleDecl1 = new XSParticleDecl();
      xSParticleDecl1.fType = 2;
      xSParticleDecl1.fValue = getErrorWildcard();
      xSParticleDecl1.fMinOccurs = 0;
      xSParticleDecl1.fMaxOccurs = -1;
      XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
      xSModelGroupImpl.fCompositor = 102;
      xSModelGroupImpl.fParticleCount = 1;
      xSModelGroupImpl.fParticles = new XSParticleDecl[1];
      xSModelGroupImpl.fParticles[0] = xSParticleDecl1;
      XSParticleDecl xSParticleDecl2 = new XSParticleDecl();
      xSParticleDecl2.fType = 3;
      xSParticleDecl2.fValue = xSModelGroupImpl;
      fErrorContent = xSParticleDecl2;
    } 
    return fErrorContent;
  }
  
  private static XSWildcardDecl getErrorWildcard() {
    if (fErrorWildcard == null) {
      XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
      xSWildcardDecl.fProcessContents = 2;
      fErrorWildcard = xSWildcardDecl;
    } 
    return fErrorWildcard;
  }
  
  XSDComplexTypeTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) { super(paramXSDHandler, paramXSAttributeChecker); }
  
  XSComplexTypeDecl traverseLocal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    String str = genAnonTypeName(paramElement);
    contentBackup();
    XSComplexTypeDecl xSComplexTypeDecl = traverseComplexTypeDecl(paramElement, str, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    contentRestore();
    paramSchemaGrammar.addComplexTypeDecl(xSComplexTypeDecl, this.fSchemaHandler.element2Locator(paramElement));
    xSComplexTypeDecl.setIsAnonymous();
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSComplexTypeDecl;
  }
  
  XSComplexTypeDecl traverseGlobal(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, true, paramXSDocumentInfo);
    String str = (String)arrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    contentBackup();
    XSComplexTypeDecl xSComplexTypeDecl = traverseComplexTypeDecl(paramElement, str, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    contentRestore();
    paramSchemaGrammar.addComplexTypeDecl(xSComplexTypeDecl, this.fSchemaHandler.element2Locator(paramElement));
    if (str == null) {
      reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_COMPLEXTYPE, SchemaSymbols.ATT_NAME }, paramElement);
      xSComplexTypeDecl = null;
    } else {
      if (paramSchemaGrammar.getGlobalTypeDecl(xSComplexTypeDecl.getName()) == null)
        paramSchemaGrammar.addGlobalComplexTypeDecl(xSComplexTypeDecl); 
      String str1 = this.fSchemaHandler.schemaDocument2SystemId(paramXSDocumentInfo);
      XSTypeDefinition xSTypeDefinition = paramSchemaGrammar.getGlobalTypeDecl(xSComplexTypeDecl.getName(), str1);
      if (xSTypeDefinition == null)
        paramSchemaGrammar.addGlobalComplexTypeDecl(xSComplexTypeDecl, str1); 
      if (this.fSchemaHandler.fTolerateDuplicates) {
        if (xSTypeDefinition != null && xSTypeDefinition instanceof XSComplexTypeDecl)
          xSComplexTypeDecl = (XSComplexTypeDecl)xSTypeDefinition; 
        this.fSchemaHandler.addGlobalTypeDecl(xSComplexTypeDecl);
      } 
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return xSComplexTypeDecl;
  }
  
  private XSComplexTypeDecl traverseComplexTypeDecl(Element paramElement, String paramString, Object[] paramArrayOfObject, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) {
    this.fComplexTypeDecl = new XSComplexTypeDecl();
    this.fAttrGrp = new XSAttributeGroupDecl();
    Boolean bool1 = (Boolean)paramArrayOfObject[XSAttributeChecker.ATTIDX_ABSTRACT];
    XInt xInt1 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_BLOCK];
    Boolean bool2 = (Boolean)paramArrayOfObject[XSAttributeChecker.ATTIDX_MIXED];
    XInt xInt2 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_FINAL];
    this.fName = paramString;
    this.fComplexTypeDecl.setName(this.fName);
    this.fTargetNamespace = paramXSDocumentInfo.fTargetNamespace;
    this.fBlock = (xInt1 == null) ? paramXSDocumentInfo.fBlockDefault : xInt1.shortValue();
    this.fFinal = (xInt2 == null) ? paramXSDocumentInfo.fFinalDefault : xInt2.shortValue();
    this.fBlock = (short)(this.fBlock & 0x3);
    this.fFinal = (short)(this.fFinal & 0x3);
    this.fIsAbstract = (bool1 != null && bool1.booleanValue());
    this.fAnnotations = null;
    Element element = null;
    try {
      element = DOMUtil.getFirstChildElement(paramElement);
      if (element != null) {
        if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
          addAnnotation(traverseAnnotationDecl(element, paramArrayOfObject, false, paramXSDocumentInfo));
          element = DOMUtil.getNextSiblingElement(element);
        } else {
          String str = DOMUtil.getSyntheticAnnotation(paramElement);
          if (str != null)
            addAnnotation(traverseSyntheticAnnotation(paramElement, str, paramArrayOfObject, false, paramXSDocumentInfo)); 
        } 
        if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION))
          throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, element); 
      } else {
        String str = DOMUtil.getSyntheticAnnotation(paramElement);
        if (str != null)
          addAnnotation(traverseSyntheticAnnotation(paramElement, str, paramArrayOfObject, false, paramXSDocumentInfo)); 
      } 
      if (element == null) {
        this.fBaseType = SchemaGrammar.fAnyType;
        this.fDerivedBy = 2;
        processComplexContent(element, bool2.booleanValue(), false, paramXSDocumentInfo, paramSchemaGrammar);
      } else if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_SIMPLECONTENT)) {
        traverseSimpleContent(element, paramXSDocumentInfo, paramSchemaGrammar);
        Element element1 = DOMUtil.getNextSiblingElement(element);
        if (element1 != null) {
          String str = DOMUtil.getLocalName(element1);
          throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, str }, element1);
        } 
      } else if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_COMPLEXCONTENT)) {
        traverseComplexContent(element, bool2.booleanValue(), paramXSDocumentInfo, paramSchemaGrammar);
        Element element1 = DOMUtil.getNextSiblingElement(element);
        if (element1 != null) {
          String str = DOMUtil.getLocalName(element1);
          throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, str }, element1);
        } 
      } else {
        this.fBaseType = SchemaGrammar.fAnyType;
        this.fDerivedBy = 2;
        processComplexContent(element, bool2.booleanValue(), false, paramXSDocumentInfo, paramSchemaGrammar);
      } 
    } catch (ComplexTypeRecoverableError complexTypeRecoverableError) {
      handleComplexTypeError(complexTypeRecoverableError.getMessage(), complexTypeRecoverableError.errorSubstText, complexTypeRecoverableError.errorElem);
    } 
    this.fComplexTypeDecl.setValues(this.fName, this.fTargetNamespace, this.fBaseType, this.fDerivedBy, this.fFinal, this.fBlock, this.fContentType, this.fIsAbstract, this.fAttrGrp, this.fXSSimpleType, this.fParticle, new XSObjectListImpl(this.fAnnotations, (this.fAnnotations == null) ? 0 : this.fAnnotations.length));
    return this.fComplexTypeDecl;
  }
  
  private void traverseSimpleContent(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) throws ComplexTypeRecoverableError {
    Object[] arrayOfObject1 = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    this.fContentType = 1;
    this.fParticle = null;
    Element element1 = DOMUtil.getFirstChildElement(paramElement);
    if (element1 != null && DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION)) {
      addAnnotation(traverseAnnotationDecl(element1, arrayOfObject1, false, paramXSDocumentInfo));
      element1 = DOMUtil.getNextSiblingElement(element1);
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(paramElement);
      if (str1 != null)
        addAnnotation(traverseSyntheticAnnotation(paramElement, str1, arrayOfObject1, false, paramXSDocumentInfo)); 
    } 
    if (element1 == null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[] { this.fName, SchemaSymbols.ELT_SIMPLECONTENT }, paramElement);
    } 
    String str = DOMUtil.getLocalName(element1);
    if (str.equals(SchemaSymbols.ELT_RESTRICTION)) {
      this.fDerivedBy = 2;
    } else if (str.equals(SchemaSymbols.ELT_EXTENSION)) {
      this.fDerivedBy = 1;
    } else {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, str }, element1);
    } 
    Element element2 = DOMUtil.getNextSiblingElement(element1);
    if (element2 != null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      String str1 = DOMUtil.getLocalName(element2);
      throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, str1 }, element2);
    } 
    Object[] arrayOfObject2 = this.fAttrChecker.checkAttributes(element1, false, paramXSDocumentInfo);
    QName qName = (QName)arrayOfObject2[XSAttributeChecker.ATTIDX_BASE];
    if (qName == null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError("s4s-att-must-appear", new Object[] { str, "base" }, element1);
    } 
    XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 7, qName, element1);
    if (xSTypeDefinition == null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError();
    } 
    this.fBaseType = xSTypeDefinition;
    XSSimpleType xSSimpleType = null;
    XSComplexTypeDecl xSComplexTypeDecl = null;
    short s = 0;
    if (xSTypeDefinition.getTypeCategory() == 15) {
      xSComplexTypeDecl = (XSComplexTypeDecl)xSTypeDefinition;
      s = xSComplexTypeDecl.getFinal();
      if (xSComplexTypeDecl.getContentType() == 1) {
        xSSimpleType = (XSSimpleType)xSComplexTypeDecl.getSimpleType();
      } else if (this.fDerivedBy != 2 || xSComplexTypeDecl.getContentType() != 3 || !((XSParticleDecl)xSComplexTypeDecl.getParticle()).emptiable()) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw new ComplexTypeRecoverableError("src-ct.2.1", new Object[] { this.fName, xSComplexTypeDecl.getName() }, element1);
      } 
    } else {
      xSSimpleType = (XSSimpleType)xSTypeDefinition;
      if (this.fDerivedBy == 2) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw new ComplexTypeRecoverableError("src-ct.2.1", new Object[] { this.fName, xSSimpleType.getName() }, element1);
      } 
      s = xSSimpleType.getFinal();
    } 
    if ((s & this.fDerivedBy) != 0) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      String str1 = (this.fDerivedBy == 1) ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1";
      throw new ComplexTypeRecoverableError(str1, new Object[] { this.fName, this.fBaseType.getName() }, element1);
    } 
    Element element3 = element1;
    element1 = DOMUtil.getFirstChildElement(element1);
    if (element1 != null) {
      if (DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION)) {
        addAnnotation(traverseAnnotationDecl(element1, arrayOfObject2, false, paramXSDocumentInfo));
        element1 = DOMUtil.getNextSiblingElement(element1);
      } else {
        String str1 = DOMUtil.getSyntheticAnnotation(element3);
        if (str1 != null)
          addAnnotation(traverseSyntheticAnnotation(element3, str1, arrayOfObject2, false, paramXSDocumentInfo)); 
      } 
      if (element1 != null && DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION)) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, element1);
      } 
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(element3);
      if (str1 != null)
        addAnnotation(traverseSyntheticAnnotation(element3, str1, arrayOfObject2, false, paramXSDocumentInfo)); 
    } 
    if (this.fDerivedBy == 2) {
      if (element1 != null && DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
        XSSimpleType xSSimpleType1 = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(element1, paramXSDocumentInfo, paramSchemaGrammar);
        if (xSSimpleType1 == null) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError();
        } 
        if (xSSimpleType != null && !XSConstraints.checkSimpleDerivationOk(xSSimpleType1, xSSimpleType, xSSimpleType.getFinal())) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("derivation-ok-restriction.5.2.2.1", new Object[] { this.fName, xSSimpleType1.getName(), xSSimpleType.getName() }, element1);
        } 
        xSSimpleType = xSSimpleType1;
        element1 = DOMUtil.getNextSiblingElement(element1);
      } 
      if (xSSimpleType == null) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw new ComplexTypeRecoverableError("src-ct.2.2", new Object[] { this.fName }, element1);
      } 
      Element element = null;
      XSFacets xSFacets = null;
      short s1 = 0;
      short s2 = 0;
      if (element1 != null) {
        XSDAbstractTraverser.FacetInfo facetInfo = traverseFacets(element1, xSSimpleType, paramXSDocumentInfo);
        element = facetInfo.nodeAfterFacets;
        xSFacets = facetInfo.facetdata;
        s1 = facetInfo.fPresentFacets;
        s2 = facetInfo.fFixedFacets;
      } 
      String str1 = genAnonTypeName(paramElement);
      this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(str1, paramXSDocumentInfo.fTargetNamespace, (short)0, xSSimpleType, null);
      try {
        this.fValidationState.setNamespaceSupport(paramXSDocumentInfo.fNamespaceSupport);
        this.fXSSimpleType.applyFacets(xSFacets, s1, s2, this.fValidationState);
      } catch (InvalidDatatypeFacetException invalidDatatypeFacetException) {
        reportSchemaError(invalidDatatypeFacetException.getKey(), invalidDatatypeFacetException.getArgs(), element1);
        this.fXSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(str1, paramXSDocumentInfo.fTargetNamespace, (short)0, xSSimpleType, null);
      } 
      if (this.fXSSimpleType instanceof XSSimpleTypeDecl)
        ((XSSimpleTypeDecl)this.fXSSimpleType).setAnonymous(true); 
      if (element != null) {
        if (!isAttrOrAttrGroup(element)) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element) }, element);
        } 
        Element element4 = traverseAttrsAndAttrGrps(element, this.fAttrGrp, paramXSDocumentInfo, paramSchemaGrammar, this.fComplexTypeDecl);
        if (element4 != null) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element4) }, element4);
        } 
      } 
      try {
        mergeAttributes(xSComplexTypeDecl.getAttrGrp(), this.fAttrGrp, this.fName, false, paramElement);
      } catch (ComplexTypeRecoverableError complexTypeRecoverableError) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw complexTypeRecoverableError;
      } 
      this.fAttrGrp.removeProhibitedAttrs();
      Object[] arrayOfObject = this.fAttrGrp.validRestrictionOf(this.fName, xSComplexTypeDecl.getAttrGrp());
      if (arrayOfObject != null) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw new ComplexTypeRecoverableError((String)arrayOfObject[arrayOfObject.length - 1], arrayOfObject, element);
      } 
    } else {
      this.fXSSimpleType = xSSimpleType;
      if (element1 != null) {
        Element element4 = element1;
        if (!isAttrOrAttrGroup(element4)) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element4) }, element4);
        } 
        Element element5 = traverseAttrsAndAttrGrps(element4, this.fAttrGrp, paramXSDocumentInfo, paramSchemaGrammar, this.fComplexTypeDecl);
        if (element5 != null) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element5) }, element5);
        } 
        this.fAttrGrp.removeProhibitedAttrs();
      } 
      if (xSComplexTypeDecl != null)
        try {
          mergeAttributes(xSComplexTypeDecl.getAttrGrp(), this.fAttrGrp, this.fName, true, paramElement);
        } catch (ComplexTypeRecoverableError complexTypeRecoverableError) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw complexTypeRecoverableError;
        }  
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
    this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
  }
  
  private void traverseComplexContent(Element paramElement, boolean paramBoolean, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) throws ComplexTypeRecoverableError {
    Object[] arrayOfObject1 = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    boolean bool = paramBoolean;
    Boolean bool1 = (Boolean)arrayOfObject1[XSAttributeChecker.ATTIDX_MIXED];
    if (bool1 != null)
      bool = bool1.booleanValue(); 
    this.fXSSimpleType = null;
    Element element1 = DOMUtil.getFirstChildElement(paramElement);
    if (element1 != null && DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION)) {
      addAnnotation(traverseAnnotationDecl(element1, arrayOfObject1, false, paramXSDocumentInfo));
      element1 = DOMUtil.getNextSiblingElement(element1);
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(paramElement);
      if (str1 != null)
        addAnnotation(traverseSyntheticAnnotation(paramElement, str1, arrayOfObject1, false, paramXSDocumentInfo)); 
    } 
    if (element1 == null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.2", new Object[] { this.fName, SchemaSymbols.ELT_COMPLEXCONTENT }, paramElement);
    } 
    String str = DOMUtil.getLocalName(element1);
    if (str.equals(SchemaSymbols.ELT_RESTRICTION)) {
      this.fDerivedBy = 2;
    } else if (str.equals(SchemaSymbols.ELT_EXTENSION)) {
      this.fDerivedBy = 1;
    } else {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, str }, element1);
    } 
    Element element2 = DOMUtil.getNextSiblingElement(element1);
    if (element2 != null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      String str1 = DOMUtil.getLocalName(element2);
      throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, str1 }, element2);
    } 
    Object[] arrayOfObject2 = this.fAttrChecker.checkAttributes(element1, false, paramXSDocumentInfo);
    QName qName = (QName)arrayOfObject2[XSAttributeChecker.ATTIDX_BASE];
    if (qName == null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError("s4s-att-must-appear", new Object[] { str, "base" }, element1);
    } 
    XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 7, qName, element1);
    if (xSTypeDefinition == null) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError();
    } 
    if (!(xSTypeDefinition instanceof XSComplexTypeDecl)) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      throw new ComplexTypeRecoverableError("src-ct.1", new Object[] { this.fName, xSTypeDefinition.getName() }, element1);
    } 
    XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)xSTypeDefinition;
    this.fBaseType = xSComplexTypeDecl;
    if ((xSComplexTypeDecl.getFinal() & this.fDerivedBy) != 0) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      String str1 = (this.fDerivedBy == 1) ? "cos-ct-extends.1.1" : "derivation-ok-restriction.1";
      throw new ComplexTypeRecoverableError(str1, new Object[] { this.fName, this.fBaseType.getName() }, element1);
    } 
    element1 = DOMUtil.getFirstChildElement(element1);
    if (element1 != null) {
      if (DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION)) {
        addAnnotation(traverseAnnotationDecl(element1, arrayOfObject2, false, paramXSDocumentInfo));
        element1 = DOMUtil.getNextSiblingElement(element1);
      } else {
        String str1 = DOMUtil.getSyntheticAnnotation(element1);
        if (str1 != null)
          addAnnotation(traverseSyntheticAnnotation(element1, str1, arrayOfObject2, false, paramXSDocumentInfo)); 
      } 
      if (element1 != null && DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION)) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, SchemaSymbols.ELT_ANNOTATION }, element1);
      } 
    } else {
      String str1 = DOMUtil.getSyntheticAnnotation(element1);
      if (str1 != null)
        addAnnotation(traverseSyntheticAnnotation(element1, str1, arrayOfObject2, false, paramXSDocumentInfo)); 
    } 
    try {
      processComplexContent(element1, bool, true, paramXSDocumentInfo, paramSchemaGrammar);
    } catch (ComplexTypeRecoverableError complexTypeRecoverableError) {
      this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
      this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
      throw complexTypeRecoverableError;
    } 
    XSParticleDecl xSParticleDecl = (XSParticleDecl)xSComplexTypeDecl.getParticle();
    if (this.fDerivedBy == 2) {
      if (this.fContentType == 3 && xSComplexTypeDecl.getContentType() != 3) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw new ComplexTypeRecoverableError("derivation-ok-restriction.5.4.1.2", new Object[] { this.fName, xSComplexTypeDecl.getName() }, element1);
      } 
      try {
        mergeAttributes(xSComplexTypeDecl.getAttrGrp(), this.fAttrGrp, this.fName, false, element1);
      } catch (ComplexTypeRecoverableError complexTypeRecoverableError) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw complexTypeRecoverableError;
      } 
      this.fAttrGrp.removeProhibitedAttrs();
      if (xSComplexTypeDecl != SchemaGrammar.fAnyType) {
        Object[] arrayOfObject = this.fAttrGrp.validRestrictionOf(this.fName, xSComplexTypeDecl.getAttrGrp());
        if (arrayOfObject != null) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError((String)arrayOfObject[arrayOfObject.length - 1], arrayOfObject, element1);
        } 
      } 
    } else {
      if (this.fParticle == null) {
        this.fContentType = xSComplexTypeDecl.getContentType();
        this.fXSSimpleType = (XSSimpleType)xSComplexTypeDecl.getSimpleType();
        this.fParticle = xSParticleDecl;
      } else if (xSComplexTypeDecl.getContentType() != 0) {
        if (this.fContentType == 2 && xSComplexTypeDecl.getContentType() != 2) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.a", new Object[] { this.fName }, element1);
        } 
        if (this.fContentType == 3 && xSComplexTypeDecl.getContentType() != 3) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("cos-ct-extends.1.4.3.2.2.1.b", new Object[] { this.fName }, element1);
        } 
        if ((this.fParticle.fType == 3 && ((XSModelGroupImpl)this.fParticle.fValue).fCompositor == 103) || (((XSParticleDecl)xSComplexTypeDecl.getParticle()).fType == 3 && ((XSModelGroupImpl)((XSParticleDecl)xSComplexTypeDecl.getParticle()).fValue).fCompositor == 103)) {
          this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
          throw new ComplexTypeRecoverableError("cos-all-limited.1.2", new Object[0], element1);
        } 
        XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
        xSModelGroupImpl.fCompositor = 102;
        xSModelGroupImpl.fParticleCount = 2;
        xSModelGroupImpl.fParticles = new XSParticleDecl[2];
        xSModelGroupImpl.fParticles[0] = (XSParticleDecl)xSComplexTypeDecl.getParticle();
        xSModelGroupImpl.fParticles[1] = this.fParticle;
        xSModelGroupImpl.fAnnotations = XSObjectListImpl.EMPTY_LIST;
        XSParticleDecl xSParticleDecl1 = new XSParticleDecl();
        xSParticleDecl1.fType = 3;
        xSParticleDecl1.fValue = xSModelGroupImpl;
        xSParticleDecl1.fAnnotations = XSObjectListImpl.EMPTY_LIST;
        this.fParticle = xSParticleDecl1;
      } 
      this.fAttrGrp.removeProhibitedAttrs();
      try {
        mergeAttributes(xSComplexTypeDecl.getAttrGrp(), this.fAttrGrp, this.fName, true, element1);
      } catch (ComplexTypeRecoverableError complexTypeRecoverableError) {
        this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
        this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
        throw complexTypeRecoverableError;
      } 
    } 
    this.fAttrChecker.returnAttrArray(arrayOfObject1, paramXSDocumentInfo);
    this.fAttrChecker.returnAttrArray(arrayOfObject2, paramXSDocumentInfo);
  }
  
  private void mergeAttributes(XSAttributeGroupDecl paramXSAttributeGroupDecl1, XSAttributeGroupDecl paramXSAttributeGroupDecl2, String paramString, boolean paramBoolean, Element paramElement) throws ComplexTypeRecoverableError {
    XSObjectList xSObjectList = paramXSAttributeGroupDecl1.getAttributeUses();
    XSAttributeUseImpl xSAttributeUseImpl = null;
    int i = xSObjectList.getLength();
    for (byte b = 0; b < i; b++) {
      xSAttributeUseImpl = (XSAttributeUseImpl)xSObjectList.item(b);
      XSAttributeUse xSAttributeUse = paramXSAttributeGroupDecl2.getAttributeUse(xSAttributeUseImpl.fAttrDecl.getNamespace(), xSAttributeUseImpl.fAttrDecl.getName());
      if (xSAttributeUse == null) {
        String str = paramXSAttributeGroupDecl2.addAttributeUse(xSAttributeUseImpl);
        if (str != null)
          throw new ComplexTypeRecoverableError("ct-props-correct.5", new Object[] { paramString, str, xSAttributeUseImpl.fAttrDecl.getName() }, paramElement); 
      } else if (xSAttributeUse != xSAttributeUseImpl && paramBoolean) {
        reportSchemaError("ct-props-correct.4", new Object[] { paramString, xSAttributeUseImpl.fAttrDecl.getName() }, paramElement);
        paramXSAttributeGroupDecl2.replaceAttributeUse(xSAttributeUse, xSAttributeUseImpl);
      } 
    } 
    if (paramBoolean)
      if (paramXSAttributeGroupDecl2.fAttributeWC == null) {
        paramXSAttributeGroupDecl2.fAttributeWC = paramXSAttributeGroupDecl1.fAttributeWC;
      } else if (paramXSAttributeGroupDecl1.fAttributeWC != null) {
        paramXSAttributeGroupDecl2.fAttributeWC = paramXSAttributeGroupDecl2.fAttributeWC.performUnionWith(paramXSAttributeGroupDecl1.fAttributeWC, paramXSAttributeGroupDecl2.fAttributeWC.fProcessContents);
        if (paramXSAttributeGroupDecl2.fAttributeWC == null)
          throw new ComplexTypeRecoverableError("src-ct.5", new Object[] { paramString }, paramElement); 
      }  
  }
  
  private void processComplexContent(Element paramElement, boolean paramBoolean1, boolean paramBoolean2, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar) throws ComplexTypeRecoverableError {
    Element element = null;
    XSParticleDecl xSParticleDecl = null;
    boolean bool = false;
    if (paramElement != null) {
      String str = DOMUtil.getLocalName(paramElement);
      if (str.equals(SchemaSymbols.ELT_GROUP)) {
        xSParticleDecl = this.fSchemaHandler.fGroupTraverser.traverseLocal(paramElement, paramXSDocumentInfo, paramSchemaGrammar);
        element = DOMUtil.getNextSiblingElement(paramElement);
      } else if (str.equals(SchemaSymbols.ELT_SEQUENCE)) {
        xSParticleDecl = traverseSequence(paramElement, paramXSDocumentInfo, paramSchemaGrammar, 0, this.fComplexTypeDecl);
        if (xSParticleDecl != null) {
          XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
          if (xSModelGroupImpl.fParticleCount == 0)
            bool = true; 
        } 
        element = DOMUtil.getNextSiblingElement(paramElement);
      } else if (str.equals(SchemaSymbols.ELT_CHOICE)) {
        xSParticleDecl = traverseChoice(paramElement, paramXSDocumentInfo, paramSchemaGrammar, 0, this.fComplexTypeDecl);
        if (xSParticleDecl != null && xSParticleDecl.fMinOccurs == 0) {
          XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
          if (xSModelGroupImpl.fParticleCount == 0)
            bool = true; 
        } 
        element = DOMUtil.getNextSiblingElement(paramElement);
      } else if (str.equals(SchemaSymbols.ELT_ALL)) {
        xSParticleDecl = traverseAll(paramElement, paramXSDocumentInfo, paramSchemaGrammar, 8, this.fComplexTypeDecl);
        if (xSParticleDecl != null) {
          XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
          if (xSModelGroupImpl.fParticleCount == 0)
            bool = true; 
        } 
        element = DOMUtil.getNextSiblingElement(paramElement);
      } else {
        element = paramElement;
      } 
    } 
    if (bool) {
      Element element1 = DOMUtil.getFirstChildElement(paramElement);
      if (element1 != null && DOMUtil.getLocalName(element1).equals(SchemaSymbols.ELT_ANNOTATION))
        element1 = DOMUtil.getNextSiblingElement(element1); 
      if (element1 == null)
        xSParticleDecl = null; 
    } 
    if (xSParticleDecl == null && paramBoolean1)
      xSParticleDecl = XSConstraints.getEmptySequence(); 
    this.fParticle = xSParticleDecl;
    if (this.fParticle == null) {
      this.fContentType = 0;
    } else if (paramBoolean1) {
      this.fContentType = 3;
    } else {
      this.fContentType = 2;
    } 
    if (element != null) {
      if (!isAttrOrAttrGroup(element))
        throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element) }, element); 
      Element element1 = traverseAttrsAndAttrGrps(element, this.fAttrGrp, paramXSDocumentInfo, paramSchemaGrammar, this.fComplexTypeDecl);
      if (element1 != null)
        throw new ComplexTypeRecoverableError("s4s-elt-invalid-content.1", new Object[] { this.fName, DOMUtil.getLocalName(element1) }, element1); 
      if (!paramBoolean2)
        this.fAttrGrp.removeProhibitedAttrs(); 
    } 
  }
  
  private boolean isAttrOrAttrGroup(Element paramElement) {
    String str = DOMUtil.getLocalName(paramElement);
    return (str.equals(SchemaSymbols.ELT_ATTRIBUTE) || str.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP) || str.equals(SchemaSymbols.ELT_ANYATTRIBUTE));
  }
  
  private void traverseSimpleContentDecl(Element paramElement) {}
  
  private void traverseComplexContentDecl(Element paramElement, boolean paramBoolean) {}
  
  private String genAnonTypeName(Element paramElement) {
    StringBuffer stringBuffer = new StringBuffer("#AnonType_");
    for (Element element = DOMUtil.getParent(paramElement); element != null && element != DOMUtil.getRoot(DOMUtil.getDocument(element)); element = DOMUtil.getParent(element))
      stringBuffer.append(element.getAttribute(SchemaSymbols.ATT_NAME)); 
    return stringBuffer.toString();
  }
  
  private void handleComplexTypeError(String paramString, Object[] paramArrayOfObject, Element paramElement) {
    if (paramString != null)
      reportSchemaError(paramString, paramArrayOfObject, paramElement); 
    this.fBaseType = SchemaGrammar.fAnyType;
    this.fContentType = 3;
    this.fXSSimpleType = null;
    this.fParticle = getErrorContent();
    this.fAttrGrp.fAttributeWC = getErrorWildcard();
  }
  
  private void contentBackup() {
    if (this.fGlobalStore == null) {
      this.fGlobalStore = new Object[11];
      this.fGlobalStorePos = 0;
    } 
    if (this.fGlobalStorePos == this.fGlobalStore.length) {
      Object[] arrayOfObject = new Object[this.fGlobalStorePos + 11];
      System.arraycopy(this.fGlobalStore, 0, arrayOfObject, 0, this.fGlobalStorePos);
      this.fGlobalStore = arrayOfObject;
    } 
    this.fGlobalStore[this.fGlobalStorePos++] = this.fComplexTypeDecl;
    this.fGlobalStore[this.fGlobalStorePos++] = this.fIsAbstract ? Boolean.TRUE : Boolean.FALSE;
    this.fGlobalStore[this.fGlobalStorePos++] = this.fName;
    this.fGlobalStore[this.fGlobalStorePos++] = this.fTargetNamespace;
    this.fGlobalStore[this.fGlobalStorePos++] = new Integer((this.fDerivedBy << 16) + this.fFinal);
    this.fGlobalStore[this.fGlobalStorePos++] = new Integer((this.fBlock << 16) + this.fContentType);
    this.fGlobalStore[this.fGlobalStorePos++] = this.fBaseType;
    this.fGlobalStore[this.fGlobalStorePos++] = this.fAttrGrp;
    this.fGlobalStore[this.fGlobalStorePos++] = this.fParticle;
    this.fGlobalStore[this.fGlobalStorePos++] = this.fXSSimpleType;
    this.fGlobalStore[this.fGlobalStorePos++] = this.fAnnotations;
  }
  
  private void contentRestore() {
    this.fAnnotations = (XSAnnotationImpl[])this.fGlobalStore[--this.fGlobalStorePos];
    this.fXSSimpleType = (XSSimpleType)this.fGlobalStore[--this.fGlobalStorePos];
    this.fParticle = (XSParticleDecl)this.fGlobalStore[--this.fGlobalStorePos];
    this.fAttrGrp = (XSAttributeGroupDecl)this.fGlobalStore[--this.fGlobalStorePos];
    this.fBaseType = (XSTypeDefinition)this.fGlobalStore[--this.fGlobalStorePos];
    int i = ((Integer)this.fGlobalStore[--this.fGlobalStorePos]).intValue();
    this.fBlock = (short)(i >> 16);
    this.fContentType = (short)i;
    i = ((Integer)this.fGlobalStore[--this.fGlobalStorePos]).intValue();
    this.fDerivedBy = (short)(i >> 16);
    this.fFinal = (short)i;
    this.fTargetNamespace = (String)this.fGlobalStore[--this.fGlobalStorePos];
    this.fName = (String)this.fGlobalStore[--this.fGlobalStorePos];
    this.fIsAbstract = ((Boolean)this.fGlobalStore[--this.fGlobalStorePos]).booleanValue();
    this.fComplexTypeDecl = (XSComplexTypeDecl)this.fGlobalStore[--this.fGlobalStorePos];
  }
  
  private void addAnnotation(XSAnnotationImpl paramXSAnnotationImpl) {
    if (paramXSAnnotationImpl == null)
      return; 
    if (this.fAnnotations == null) {
      this.fAnnotations = new XSAnnotationImpl[1];
    } else {
      XSAnnotationImpl[] arrayOfXSAnnotationImpl = new XSAnnotationImpl[this.fAnnotations.length + 1];
      System.arraycopy(this.fAnnotations, 0, arrayOfXSAnnotationImpl, 0, this.fAnnotations.length);
      this.fAnnotations = arrayOfXSAnnotationImpl;
    } 
    this.fAnnotations[this.fAnnotations.length - 1] = paramXSAnnotationImpl;
  }
  
  private static final class ComplexTypeRecoverableError extends Exception {
    private static final long serialVersionUID = 6802729912091130335L;
    
    Object[] errorSubstText = null;
    
    Element errorElem = null;
    
    ComplexTypeRecoverableError() {}
    
    ComplexTypeRecoverableError(String param1String, Object[] param1ArrayOfObject, Element param1Element) {
      super(param1String);
      this.errorSubstText = param1ArrayOfObject;
      this.errorElem = param1Element;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDComplexTypeTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */