package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Locale;
import java.util.Vector;
import org.w3c.dom.Element;

abstract class XSDAbstractTraverser {
  protected static final String NO_NAME = "(no name)";
  
  protected static final int NOT_ALL_CONTEXT = 0;
  
  protected static final int PROCESSING_ALL_EL = 1;
  
  protected static final int GROUP_REF_WITH_ALL = 2;
  
  protected static final int CHILD_OF_GROUP = 4;
  
  protected static final int PROCESSING_ALL_GP = 8;
  
  protected XSDHandler fSchemaHandler = null;
  
  protected SymbolTable fSymbolTable = null;
  
  protected XSAttributeChecker fAttrChecker = null;
  
  protected boolean fValidateAnnotations = false;
  
  ValidationState fValidationState = new ValidationState();
  
  private static final XSSimpleType fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
  
  private StringBuffer fPattern = new StringBuffer();
  
  private final XSFacets xsFacets = new XSFacets();
  
  XSDAbstractTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker) {
    this.fSchemaHandler = paramXSDHandler;
    this.fAttrChecker = paramXSAttributeChecker;
  }
  
  void reset(SymbolTable paramSymbolTable, boolean paramBoolean, Locale paramLocale) {
    this.fSymbolTable = paramSymbolTable;
    this.fValidateAnnotations = paramBoolean;
    this.fValidationState.setExtraChecking(false);
    this.fValidationState.setSymbolTable(paramSymbolTable);
    this.fValidationState.setLocale(paramLocale);
  }
  
  XSAnnotationImpl traverseAnnotationDecl(Element paramElement, Object[] paramArrayOfObject, boolean paramBoolean, XSDocumentInfo paramXSDocumentInfo) {
    Object[] arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, paramBoolean, paramXSDocumentInfo);
    this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    String str = DOMUtil.getAnnotation(paramElement);
    Element element = DOMUtil.getFirstChildElement(paramElement);
    if (element != null)
      do {
        String str1 = DOMUtil.getLocalName(element);
        if (!str1.equals(SchemaSymbols.ELT_APPINFO) && !str1.equals(SchemaSymbols.ELT_DOCUMENTATION)) {
          reportSchemaError("src-annotation", new Object[] { str1 }, element);
        } else {
          arrayOfObject = this.fAttrChecker.checkAttributes(element, true, paramXSDocumentInfo);
          this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
        } 
        element = DOMUtil.getNextSiblingElement(element);
      } while (element != null); 
    if (str == null)
      return null; 
    SchemaGrammar schemaGrammar = this.fSchemaHandler.getGrammar(paramXSDocumentInfo.fTargetNamespace);
    Vector vector = (Vector)paramArrayOfObject[XSAttributeChecker.ATTIDX_NONSCHEMA];
    if (vector != null && !vector.isEmpty()) {
      StringBuffer stringBuffer1 = new StringBuffer(64);
      stringBuffer1.append(" ");
      byte b = 0;
      while (b < vector.size()) {
        String str4;
        String str3;
        String str2 = (String)vector.elementAt(b++);
        int j = str2.indexOf(':');
        if (j == -1) {
          str3 = "";
          str4 = str2;
        } else {
          str3 = str2.substring(0, j);
          str4 = str2.substring(j + 1);
        } 
        String str5 = paramXSDocumentInfo.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(str3));
        if (paramElement.getAttributeNS(str5, str4).length() != 0) {
          b++;
          continue;
        } 
        stringBuffer1.append(str2).append("=\"");
        String str6 = (String)vector.elementAt(b++);
        str6 = processAttValue(str6);
        stringBuffer1.append(str6).append("\" ");
      } 
      StringBuffer stringBuffer2 = new StringBuffer(str.length() + stringBuffer1.length());
      int i = str.indexOf(SchemaSymbols.ELT_ANNOTATION);
      if (i == -1)
        return null; 
      i += SchemaSymbols.ELT_ANNOTATION.length();
      stringBuffer2.append(str.substring(0, i));
      stringBuffer2.append(stringBuffer1.toString());
      stringBuffer2.append(str.substring(i, str.length()));
      String str1 = stringBuffer2.toString();
      if (this.fValidateAnnotations)
        paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str1, paramElement)); 
      return new XSAnnotationImpl(str1, schemaGrammar);
    } 
    if (this.fValidateAnnotations)
      paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str, paramElement)); 
    return new XSAnnotationImpl(str, schemaGrammar);
  }
  
  XSAnnotationImpl traverseSyntheticAnnotation(Element paramElement, String paramString, Object[] paramArrayOfObject, boolean paramBoolean, XSDocumentInfo paramXSDocumentInfo) {
    String str = paramString;
    SchemaGrammar schemaGrammar = this.fSchemaHandler.getGrammar(paramXSDocumentInfo.fTargetNamespace);
    Vector vector = (Vector)paramArrayOfObject[XSAttributeChecker.ATTIDX_NONSCHEMA];
    if (vector != null && !vector.isEmpty()) {
      StringBuffer stringBuffer1 = new StringBuffer(64);
      stringBuffer1.append(" ");
      byte b = 0;
      while (b < vector.size()) {
        String str3;
        String str2 = (String)vector.elementAt(b++);
        int j = str2.indexOf(':');
        if (j == -1) {
          str3 = "";
          String str6 = str2;
        } else {
          str3 = str2.substring(0, j);
          String str6 = str2.substring(j + 1);
        } 
        String str4 = paramXSDocumentInfo.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(str3));
        stringBuffer1.append(str2).append("=\"");
        String str5 = (String)vector.elementAt(b++);
        str5 = processAttValue(str5);
        stringBuffer1.append(str5).append("\" ");
      } 
      StringBuffer stringBuffer2 = new StringBuffer(str.length() + stringBuffer1.length());
      int i = str.indexOf(SchemaSymbols.ELT_ANNOTATION);
      if (i == -1)
        return null; 
      i += SchemaSymbols.ELT_ANNOTATION.length();
      stringBuffer2.append(str.substring(0, i));
      stringBuffer2.append(stringBuffer1.toString());
      stringBuffer2.append(str.substring(i, str.length()));
      String str1 = stringBuffer2.toString();
      if (this.fValidateAnnotations)
        paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str1, paramElement)); 
      return new XSAnnotationImpl(str1, schemaGrammar);
    } 
    if (this.fValidateAnnotations)
      paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str, paramElement)); 
    return new XSAnnotationImpl(str, schemaGrammar);
  }
  
  FacetInfo traverseFacets(Element paramElement, XSSimpleType paramXSSimpleType, XSDocumentInfo paramXSDocumentInfo) {
    short s1 = 0;
    short s2 = 0;
    boolean bool = containsQName(paramXSSimpleType);
    Vector vector1 = null;
    XSObjectListImpl xSObjectListImpl1 = null;
    XSObjectListImpl xSObjectListImpl2 = null;
    Vector vector2 = bool ? new Vector() : null;
    short s = 0;
    this.xsFacets.reset();
    while (paramElement != null) {
      Object[] arrayOfObject = null;
      String str = DOMUtil.getLocalName(paramElement);
      if (str.equals(SchemaSymbols.ELT_ENUMERATION)) {
        arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo, bool);
        String str1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
        if (str1 == null) {
          reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ENUMERATION, SchemaSymbols.ATT_VALUE }, paramElement);
          this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        } 
        NamespaceSupport namespaceSupport = (NamespaceSupport)arrayOfObject[XSAttributeChecker.ATTIDX_ENUMNSDECLS];
        if (paramXSSimpleType.getVariety() == 1 && paramXSSimpleType.getPrimitiveKind() == 20) {
          paramXSDocumentInfo.fValidationContext.setNamespaceSupport(namespaceSupport);
          Object object = null;
          try {
            QName qName = (QName)fQNameDV.validate(str1, paramXSDocumentInfo.fValidationContext, null);
            object = this.fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 6, qName, paramElement);
          } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
            reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), paramElement);
          } 
          if (object == null) {
            this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
            paramElement = DOMUtil.getNextSiblingElement(paramElement);
            continue;
          } 
          paramXSDocumentInfo.fValidationContext.setNamespaceSupport(paramXSDocumentInfo.fNamespaceSupport);
        } 
        if (vector1 == null) {
          vector1 = new Vector();
          xSObjectListImpl1 = new XSObjectListImpl();
        } 
        vector1.addElement(str1);
        xSObjectListImpl1.addXSObject(null);
        if (bool)
          vector2.addElement(namespaceSupport); 
        Element element = DOMUtil.getFirstChildElement(paramElement);
        if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
          xSObjectListImpl1.addXSObject(xSObjectListImpl1.getLength() - 1, traverseAnnotationDecl(element, arrayOfObject, false, paramXSDocumentInfo));
          element = DOMUtil.getNextSiblingElement(element);
        } else {
          String str2 = DOMUtil.getSyntheticAnnotation(paramElement);
          if (str2 != null)
            xSObjectListImpl1.addXSObject(xSObjectListImpl1.getLength() - 1, traverseSyntheticAnnotation(paramElement, str2, arrayOfObject, false, paramXSDocumentInfo)); 
        } 
        if (element != null)
          reportSchemaError("s4s-elt-must-match.1", new Object[] { "enumeration", "(annotation?)", DOMUtil.getLocalName(element) }, element); 
      } else if (str.equals(SchemaSymbols.ELT_PATTERN)) {
        s1 = (short)(s1 | 0x8);
        arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
        String str1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
        if (str1 == null) {
          reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_PATTERN, SchemaSymbols.ATT_VALUE }, paramElement);
          this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        } 
        if (this.fPattern.length() == 0) {
          this.fPattern.append(str1);
        } else {
          this.fPattern.append("|");
          this.fPattern.append(str1);
        } 
        Element element = DOMUtil.getFirstChildElement(paramElement);
        if (element != null && DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_ANNOTATION)) {
          if (xSObjectListImpl2 == null)
            xSObjectListImpl2 = new XSObjectListImpl(); 
          xSObjectListImpl2.addXSObject(traverseAnnotationDecl(element, arrayOfObject, false, paramXSDocumentInfo));
          element = DOMUtil.getNextSiblingElement(element);
        } else {
          String str2 = DOMUtil.getSyntheticAnnotation(paramElement);
          if (str2 != null) {
            if (xSObjectListImpl2 == null)
              xSObjectListImpl2 = new XSObjectListImpl(); 
            xSObjectListImpl2.addXSObject(traverseSyntheticAnnotation(paramElement, str2, arrayOfObject, false, paramXSDocumentInfo));
          } 
        } 
        if (element != null)
          reportSchemaError("s4s-elt-must-match.1", new Object[] { "pattern", "(annotation?)", DOMUtil.getLocalName(element) }, element); 
      } else {
        if (str.equals(SchemaSymbols.ELT_MINLENGTH)) {
          s = 2;
        } else if (str.equals(SchemaSymbols.ELT_MAXLENGTH)) {
          s = 4;
        } else if (str.equals(SchemaSymbols.ELT_MAXEXCLUSIVE)) {
          s = 64;
        } else if (str.equals(SchemaSymbols.ELT_MAXINCLUSIVE)) {
          s = 32;
        } else if (str.equals(SchemaSymbols.ELT_MINEXCLUSIVE)) {
          s = 128;
        } else if (str.equals(SchemaSymbols.ELT_MININCLUSIVE)) {
          s = 256;
        } else if (str.equals(SchemaSymbols.ELT_TOTALDIGITS)) {
          s = 512;
        } else if (str.equals(SchemaSymbols.ELT_FRACTIONDIGITS)) {
          s = 1024;
        } else if (str.equals(SchemaSymbols.ELT_WHITESPACE)) {
          s = 16;
        } else if (str.equals(SchemaSymbols.ELT_LENGTH)) {
          s = 1;
        } else {
          break;
        } 
        arrayOfObject = this.fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
        if ((s1 & s) != 0) {
          reportSchemaError("src-single-facet-value", new Object[] { str }, paramElement);
          this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        } 
        if (arrayOfObject[XSAttributeChecker.ATTIDX_VALUE] == null) {
          if (paramElement.getAttributeNodeNS(null, "value") == null)
            reportSchemaError("s4s-att-must-appear", new Object[] { paramElement.getLocalName(), SchemaSymbols.ATT_VALUE }, paramElement); 
          this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        } 
        s1 = (short)(s1 | s);
        if (((Boolean)arrayOfObject[XSAttributeChecker.ATTIDX_FIXED]).booleanValue())
          s2 = (short)(s2 | s); 
        switch (s) {
          case 2:
            this.xsFacets.minLength = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
            break;
          case 4:
            this.xsFacets.maxLength = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
            break;
          case 64:
            this.xsFacets.maxExclusive = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
            break;
          case 32:
            this.xsFacets.maxInclusive = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
            break;
          case 128:
            this.xsFacets.minExclusive = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
            break;
          case 256:
            this.xsFacets.minInclusive = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
            break;
          case 512:
            this.xsFacets.totalDigits = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
            break;
          case 1024:
            this.xsFacets.fractionDigits = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
            break;
          case 16:
            this.xsFacets.whiteSpace = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).shortValue();
            break;
          case 1:
            this.xsFacets.length = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
            break;
        } 
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
        switch (s) {
          case 2:
            this.xsFacets.minLengthAnnotation = xSAnnotationImpl;
            break;
          case 4:
            this.xsFacets.maxLengthAnnotation = xSAnnotationImpl;
            break;
          case 64:
            this.xsFacets.maxExclusiveAnnotation = xSAnnotationImpl;
            break;
          case 32:
            this.xsFacets.maxInclusiveAnnotation = xSAnnotationImpl;
            break;
          case 128:
            this.xsFacets.minExclusiveAnnotation = xSAnnotationImpl;
            break;
          case 256:
            this.xsFacets.minInclusiveAnnotation = xSAnnotationImpl;
            break;
          case 512:
            this.xsFacets.totalDigitsAnnotation = xSAnnotationImpl;
            break;
          case 1024:
            this.xsFacets.fractionDigitsAnnotation = xSAnnotationImpl;
            break;
          case 16:
            this.xsFacets.whiteSpaceAnnotation = xSAnnotationImpl;
            break;
          case 1:
            this.xsFacets.lengthAnnotation = xSAnnotationImpl;
            break;
        } 
        if (element != null)
          reportSchemaError("s4s-elt-must-match.1", new Object[] { str, "(annotation?)", DOMUtil.getLocalName(element) }, element); 
      } 
      this.fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
      paramElement = DOMUtil.getNextSiblingElement(paramElement);
    } 
    if (vector1 != null) {
      s1 = (short)(s1 | 0x800);
      this.xsFacets.enumeration = vector1;
      this.xsFacets.enumNSDecls = vector2;
      this.xsFacets.enumAnnotations = xSObjectListImpl1;
    } 
    if ((s1 & 0x8) != 0) {
      this.xsFacets.pattern = this.fPattern.toString();
      this.xsFacets.patternAnnotations = xSObjectListImpl2;
    } 
    this.fPattern.setLength(0);
    return new FacetInfo(this.xsFacets, paramElement, s1, s2);
  }
  
  private boolean containsQName(XSSimpleType paramXSSimpleType) {
    if (paramXSSimpleType.getVariety() == 1) {
      short s = paramXSSimpleType.getPrimitiveKind();
      return (s == 18 || s == 20);
    } 
    if (paramXSSimpleType.getVariety() == 2)
      return containsQName((XSSimpleType)paramXSSimpleType.getItemType()); 
    if (paramXSSimpleType.getVariety() == 3) {
      XSObjectList xSObjectList = paramXSSimpleType.getMemberTypes();
      for (byte b = 0; b < xSObjectList.getLength(); b++) {
        if (containsQName((XSSimpleType)xSObjectList.item(b)))
          return true; 
      } 
    } 
    return false;
  }
  
  Element traverseAttrsAndAttrGrps(Element paramElement, XSAttributeGroupDecl paramXSAttributeGroupDecl, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, XSComplexTypeDecl paramXSComplexTypeDecl) {
    Element element = null;
    XSAttributeGroupDecl xSAttributeGroupDecl = null;
    XSAttributeUseImpl xSAttributeUseImpl = null;
    XSAttributeUse xSAttributeUse = null;
    for (element = paramElement; element != null; element = DOMUtil.getNextSiblingElement(element)) {
      String str = DOMUtil.getLocalName(element);
      if (str.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
        xSAttributeUseImpl = this.fSchemaHandler.fAttributeTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar, paramXSComplexTypeDecl);
        if (xSAttributeUseImpl != null)
          if (xSAttributeUseImpl.fUse == 2) {
            paramXSAttributeGroupDecl.addAttributeUse(xSAttributeUseImpl);
          } else {
            xSAttributeUse = paramXSAttributeGroupDecl.getAttributeUseNoProhibited(xSAttributeUseImpl.fAttrDecl.getNamespace(), xSAttributeUseImpl.fAttrDecl.getName());
            if (xSAttributeUse == null) {
              String str1 = paramXSAttributeGroupDecl.addAttributeUse(xSAttributeUseImpl);
              if (str1 != null) {
                String str2 = (paramXSComplexTypeDecl == null) ? "ag-props-correct.3" : "ct-props-correct.5";
                String str3 = (paramXSComplexTypeDecl == null) ? paramXSAttributeGroupDecl.fName : paramXSComplexTypeDecl.getName();
                reportSchemaError(str2, new Object[] { str3, xSAttributeUseImpl.fAttrDecl.getName(), str1 }, element);
              } 
            } else if (xSAttributeUse != xSAttributeUseImpl) {
              String str1 = (paramXSComplexTypeDecl == null) ? "ag-props-correct.2" : "ct-props-correct.4";
              String str2 = (paramXSComplexTypeDecl == null) ? paramXSAttributeGroupDecl.fName : paramXSComplexTypeDecl.getName();
              reportSchemaError(str1, new Object[] { str2, xSAttributeUseImpl.fAttrDecl.getName() }, element);
            } 
          }  
      } else if (str.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
        xSAttributeGroupDecl = this.fSchemaHandler.fAttributeGroupTraverser.traverseLocal(element, paramXSDocumentInfo, paramSchemaGrammar);
        if (xSAttributeGroupDecl != null) {
          XSObjectList xSObjectList = xSAttributeGroupDecl.getAttributeUses();
          int i = xSObjectList.getLength();
          for (byte b = 0; b < i; b++) {
            XSAttributeUseImpl xSAttributeUseImpl1 = (XSAttributeUseImpl)xSObjectList.item(b);
            if (xSAttributeUseImpl1.fUse == 2) {
              paramXSAttributeGroupDecl.addAttributeUse(xSAttributeUseImpl1);
            } else {
              xSAttributeUse = paramXSAttributeGroupDecl.getAttributeUseNoProhibited(xSAttributeUseImpl1.fAttrDecl.getNamespace(), xSAttributeUseImpl1.fAttrDecl.getName());
              if (xSAttributeUse == null) {
                String str1 = paramXSAttributeGroupDecl.addAttributeUse(xSAttributeUseImpl1);
                if (str1 != null) {
                  String str2 = (paramXSComplexTypeDecl == null) ? "ag-props-correct.3" : "ct-props-correct.5";
                  String str3 = (paramXSComplexTypeDecl == null) ? paramXSAttributeGroupDecl.fName : paramXSComplexTypeDecl.getName();
                  reportSchemaError(str2, new Object[] { str3, xSAttributeUseImpl1.fAttrDecl.getName(), str1 }, element);
                } 
              } else if (xSAttributeUseImpl1 != xSAttributeUse) {
                String str1 = (paramXSComplexTypeDecl == null) ? "ag-props-correct.2" : "ct-props-correct.4";
                String str2 = (paramXSComplexTypeDecl == null) ? paramXSAttributeGroupDecl.fName : paramXSComplexTypeDecl.getName();
                reportSchemaError(str1, new Object[] { str2, xSAttributeUseImpl1.fAttrDecl.getName() }, element);
              } 
            } 
          } 
          if (xSAttributeGroupDecl.fAttributeWC != null)
            if (paramXSAttributeGroupDecl.fAttributeWC == null) {
              paramXSAttributeGroupDecl.fAttributeWC = xSAttributeGroupDecl.fAttributeWC;
            } else {
              paramXSAttributeGroupDecl.fAttributeWC = paramXSAttributeGroupDecl.fAttributeWC.performIntersectionWith(xSAttributeGroupDecl.fAttributeWC, paramXSAttributeGroupDecl.fAttributeWC.fProcessContents);
              if (paramXSAttributeGroupDecl.fAttributeWC == null) {
                String str1 = (paramXSComplexTypeDecl == null) ? "src-attribute_group.2" : "src-ct.4";
                String str2 = (paramXSComplexTypeDecl == null) ? paramXSAttributeGroupDecl.fName : paramXSComplexTypeDecl.getName();
                reportSchemaError(str1, new Object[] { str2 }, element);
              } 
            }  
        } 
      } else {
        break;
      } 
    } 
    if (element != null) {
      String str = DOMUtil.getLocalName(element);
      if (str.equals(SchemaSymbols.ELT_ANYATTRIBUTE)) {
        XSWildcardDecl xSWildcardDecl = this.fSchemaHandler.fWildCardTraverser.traverseAnyAttribute(element, paramXSDocumentInfo, paramSchemaGrammar);
        if (paramXSAttributeGroupDecl.fAttributeWC == null) {
          paramXSAttributeGroupDecl.fAttributeWC = xSWildcardDecl;
        } else {
          paramXSAttributeGroupDecl.fAttributeWC = xSWildcardDecl.performIntersectionWith(paramXSAttributeGroupDecl.fAttributeWC, xSWildcardDecl.fProcessContents);
          if (paramXSAttributeGroupDecl.fAttributeWC == null) {
            String str1 = (paramXSComplexTypeDecl == null) ? "src-attribute_group.2" : "src-ct.4";
            String str2 = (paramXSComplexTypeDecl == null) ? paramXSAttributeGroupDecl.fName : paramXSComplexTypeDecl.getName();
            reportSchemaError(str1, new Object[] { str2 }, element);
          } 
        } 
        element = DOMUtil.getNextSiblingElement(element);
      } 
    } 
    return element;
  }
  
  void reportSchemaError(String paramString, Object[] paramArrayOfObject, Element paramElement) { this.fSchemaHandler.reportSchemaError(paramString, paramArrayOfObject, paramElement); }
  
  void checkNotationType(String paramString, XSTypeDefinition paramXSTypeDefinition, Element paramElement) {
    if (paramXSTypeDefinition.getTypeCategory() == 16 && ((XSSimpleType)paramXSTypeDefinition).getVariety() == 1 && ((XSSimpleType)paramXSTypeDefinition).getPrimitiveKind() == 20 && (((XSSimpleType)paramXSTypeDefinition).getDefinedFacets() & 0x800) == 0)
      reportSchemaError("enumeration-required-notation", new Object[] { paramXSTypeDefinition.getName(), paramString, DOMUtil.getLocalName(paramElement) }, paramElement); 
  }
  
  protected XSParticleDecl checkOccurrences(XSParticleDecl paramXSParticleDecl, String paramString, Element paramElement, int paramInt, long paramLong) {
    int i = paramXSParticleDecl.fMinOccurs;
    int j = paramXSParticleDecl.fMaxOccurs;
    boolean bool1 = ((paramLong & (1 << XSAttributeChecker.ATTIDX_MINOCCURS)) != 0L) ? 1 : 0;
    boolean bool2 = ((paramLong & (1 << XSAttributeChecker.ATTIDX_MAXOCCURS)) != 0L) ? 1 : 0;
    boolean bool3 = ((paramInt & true) != 0) ? 1 : 0;
    boolean bool4 = ((paramInt & 0x8) != 0) ? 1 : 0;
    boolean bool5 = ((paramInt & 0x2) != 0) ? 1 : 0;
    boolean bool6 = ((paramInt & 0x4) != 0) ? 1 : 0;
    if (bool6) {
      if (!bool1) {
        Object[] arrayOfObject = { paramString, "minOccurs" };
        reportSchemaError("s4s-att-not-allowed", arrayOfObject, paramElement);
        i = 1;
      } 
      if (!bool2) {
        Object[] arrayOfObject = { paramString, "maxOccurs" };
        reportSchemaError("s4s-att-not-allowed", arrayOfObject, paramElement);
        j = 1;
      } 
    } 
    if (i == 0 && j == 0) {
      paramXSParticleDecl.fType = 0;
      return null;
    } 
    if (bool3) {
      if (j != 1) {
        reportSchemaError("cos-all-limited.2", new Object[] { (j == -1) ? "unbounded" : Integer.toString(j), ((XSElementDecl)paramXSParticleDecl.fValue).getName() }, paramElement);
        j = 1;
        if (i > 1)
          i = 1; 
      } 
    } else if ((bool4 || bool5) && j != 1) {
      reportSchemaError("cos-all-limited.1.2", null, paramElement);
      if (i > 1)
        i = 1; 
      j = 1;
    } 
    paramXSParticleDecl.fMinOccurs = i;
    paramXSParticleDecl.fMaxOccurs = j;
    return paramXSParticleDecl;
  }
  
  private static String processAttValue(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c == '"' || c == '<' || c == '&' || c == '\t' || c == '\n' || c == '\r')
        return escapeAttValue(paramString, b); 
    } 
    return paramString;
  }
  
  private static String escapeAttValue(String paramString, int paramInt) {
    int j = paramString.length();
    StringBuffer stringBuffer = new StringBuffer(j);
    stringBuffer.append(paramString.substring(0, paramInt));
    for (int i = paramInt; i < j; i++) {
      char c = paramString.charAt(i);
      if (c == '"') {
        stringBuffer.append("&quot;");
      } else if (c == '<') {
        stringBuffer.append("&lt;");
      } else if (c == '&') {
        stringBuffer.append("&amp;");
      } else if (c == '\t') {
        stringBuffer.append("&#x9;");
      } else if (c == '\n') {
        stringBuffer.append("&#xA;");
      } else if (c == '\r') {
        stringBuffer.append("&#xD;");
      } else {
        stringBuffer.append(c);
      } 
    } 
    return stringBuffer.toString();
  }
  
  static final class FacetInfo {
    final XSFacets facetdata;
    
    final Element nodeAfterFacets;
    
    final short fPresentFacets;
    
    final short fFixedFacets;
    
    FacetInfo(XSFacets param1XSFacets, Element param1Element, short param1Short1, short param1Short2) {
      this.facetdata = param1XSFacets;
      this.nodeAfterFacets = param1Element;
      this.fPresentFacets = param1Short1;
      this.fFixedFacets = param1Short2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDAbstractTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */