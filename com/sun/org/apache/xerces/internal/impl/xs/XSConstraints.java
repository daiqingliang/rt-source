package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class XSConstraints {
  static final int OCCURRENCE_UNKNOWN = -2;
  
  static final XSSimpleType STRING_TYPE = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("string");
  
  private static XSParticleDecl fEmptyParticle = null;
  
  private static final Comparator ELEMENT_PARTICLE_COMPARATOR = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        XSParticleDecl xSParticleDecl1 = (XSParticleDecl)param1Object1;
        XSParticleDecl xSParticleDecl2 = (XSParticleDecl)param1Object2;
        XSElementDecl xSElementDecl1 = (XSElementDecl)xSParticleDecl1.fValue;
        XSElementDecl xSElementDecl2 = (XSElementDecl)xSParticleDecl2.fValue;
        String str1 = xSElementDecl1.getNamespace();
        String str2 = xSElementDecl2.getNamespace();
        String str3 = xSElementDecl1.getName();
        String str4 = xSElementDecl2.getName();
        boolean bool = (str1 == str2) ? 1 : 0;
        int i = 0;
        if (!bool)
          if (str1 != null) {
            if (str2 != null) {
              i = str1.compareTo(str2);
            } else {
              i = 1;
            } 
          } else {
            i = -1;
          }  
        return (i != 0) ? i : str3.compareTo(str4);
      }
    };
  
  public static XSParticleDecl getEmptySequence() {
    if (fEmptyParticle == null) {
      XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
      xSModelGroupImpl.fCompositor = 102;
      xSModelGroupImpl.fParticleCount = 0;
      xSModelGroupImpl.fParticles = null;
      xSModelGroupImpl.fAnnotations = XSObjectListImpl.EMPTY_LIST;
      XSParticleDecl xSParticleDecl = new XSParticleDecl();
      xSParticleDecl.fType = 3;
      xSParticleDecl.fValue = xSModelGroupImpl;
      xSParticleDecl.fAnnotations = XSObjectListImpl.EMPTY_LIST;
      fEmptyParticle = xSParticleDecl;
    } 
    return fEmptyParticle;
  }
  
  public static boolean checkTypeDerivationOk(XSTypeDefinition paramXSTypeDefinition1, XSTypeDefinition paramXSTypeDefinition2, short paramShort) {
    if (paramXSTypeDefinition1 == SchemaGrammar.fAnyType)
      return (paramXSTypeDefinition1 == paramXSTypeDefinition2); 
    if (paramXSTypeDefinition1 == SchemaGrammar.fAnySimpleType)
      return (paramXSTypeDefinition2 == SchemaGrammar.fAnyType || paramXSTypeDefinition2 == SchemaGrammar.fAnySimpleType); 
    if (paramXSTypeDefinition1.getTypeCategory() == 16) {
      if (paramXSTypeDefinition2.getTypeCategory() == 15)
        if (paramXSTypeDefinition2 == SchemaGrammar.fAnyType) {
          paramXSTypeDefinition2 = SchemaGrammar.fAnySimpleType;
        } else {
          return false;
        }  
      return checkSimpleDerivation((XSSimpleType)paramXSTypeDefinition1, (XSSimpleType)paramXSTypeDefinition2, paramShort);
    } 
    return checkComplexDerivation((XSComplexTypeDecl)paramXSTypeDefinition1, paramXSTypeDefinition2, paramShort);
  }
  
  public static boolean checkSimpleDerivationOk(XSSimpleType paramXSSimpleType, XSTypeDefinition paramXSTypeDefinition, short paramShort) {
    if (paramXSSimpleType == SchemaGrammar.fAnySimpleType)
      return (paramXSTypeDefinition == SchemaGrammar.fAnyType || paramXSTypeDefinition == SchemaGrammar.fAnySimpleType); 
    if (paramXSTypeDefinition.getTypeCategory() == 15)
      if (paramXSTypeDefinition == SchemaGrammar.fAnyType) {
        paramXSTypeDefinition = SchemaGrammar.fAnySimpleType;
      } else {
        return false;
      }  
    return checkSimpleDerivation(paramXSSimpleType, (XSSimpleType)paramXSTypeDefinition, paramShort);
  }
  
  public static boolean checkComplexDerivationOk(XSComplexTypeDecl paramXSComplexTypeDecl, XSTypeDefinition paramXSTypeDefinition, short paramShort) { return (paramXSComplexTypeDecl == SchemaGrammar.fAnyType) ? ((paramXSComplexTypeDecl == paramXSTypeDefinition)) : checkComplexDerivation(paramXSComplexTypeDecl, paramXSTypeDefinition, paramShort); }
  
  private static boolean checkSimpleDerivation(XSSimpleType paramXSSimpleType1, XSSimpleType paramXSSimpleType2, short paramShort) {
    if (paramXSSimpleType1 == paramXSSimpleType2)
      return true; 
    if ((paramShort & 0x2) != 0 || (paramXSSimpleType1.getBaseType().getFinal() & 0x2) != 0)
      return false; 
    XSSimpleType xSSimpleType = (XSSimpleType)paramXSSimpleType1.getBaseType();
    if (xSSimpleType == paramXSSimpleType2)
      return true; 
    if (xSSimpleType != SchemaGrammar.fAnySimpleType && checkSimpleDerivation(xSSimpleType, paramXSSimpleType2, paramShort))
      return true; 
    if ((paramXSSimpleType1.getVariety() == 2 || paramXSSimpleType1.getVariety() == 3) && paramXSSimpleType2 == SchemaGrammar.fAnySimpleType)
      return true; 
    if (paramXSSimpleType2.getVariety() == 3) {
      XSObjectList xSObjectList = paramXSSimpleType2.getMemberTypes();
      int i = xSObjectList.getLength();
      for (byte b = 0; b < i; b++) {
        paramXSSimpleType2 = (XSSimpleType)xSObjectList.item(b);
        if (checkSimpleDerivation(paramXSSimpleType1, paramXSSimpleType2, paramShort))
          return true; 
      } 
    } 
    return false;
  }
  
  private static boolean checkComplexDerivation(XSComplexTypeDecl paramXSComplexTypeDecl, XSTypeDefinition paramXSTypeDefinition, short paramShort) {
    if (paramXSComplexTypeDecl == paramXSTypeDefinition)
      return true; 
    if ((paramXSComplexTypeDecl.fDerivedBy & paramShort) != 0)
      return false; 
    XSTypeDefinition xSTypeDefinition = paramXSComplexTypeDecl.fBaseType;
    if (xSTypeDefinition == paramXSTypeDefinition)
      return true; 
    if (xSTypeDefinition == SchemaGrammar.fAnyType || xSTypeDefinition == SchemaGrammar.fAnySimpleType)
      return false; 
    if (xSTypeDefinition.getTypeCategory() == 15)
      return checkComplexDerivation((XSComplexTypeDecl)xSTypeDefinition, paramXSTypeDefinition, paramShort); 
    if (xSTypeDefinition.getTypeCategory() == 16) {
      if (paramXSTypeDefinition.getTypeCategory() == 15)
        if (paramXSTypeDefinition == SchemaGrammar.fAnyType) {
          paramXSTypeDefinition = SchemaGrammar.fAnySimpleType;
        } else {
          return false;
        }  
      return checkSimpleDerivation((XSSimpleType)xSTypeDefinition, (XSSimpleType)paramXSTypeDefinition, paramShort);
    } 
    return false;
  }
  
  public static Object ElementDefaultValidImmediate(XSTypeDefinition paramXSTypeDefinition, String paramString, ValidationContext paramValidationContext, ValidatedInfo paramValidatedInfo) {
    XSSimpleType xSSimpleType = null;
    if (paramXSTypeDefinition.getTypeCategory() == 16) {
      xSSimpleType = (XSSimpleType)paramXSTypeDefinition;
    } else {
      XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)paramXSTypeDefinition;
      if (xSComplexTypeDecl.fContentType == 1) {
        xSSimpleType = xSComplexTypeDecl.fXSSimpleType;
      } else if (xSComplexTypeDecl.fContentType == 3) {
        if (!((XSParticleDecl)xSComplexTypeDecl.getParticle()).emptiable())
          return null; 
      } else {
        return null;
      } 
    } 
    Object object = null;
    if (xSSimpleType == null)
      xSSimpleType = STRING_TYPE; 
    try {
      object = xSSimpleType.validate(paramString, paramValidationContext, paramValidatedInfo);
      if (paramValidatedInfo != null)
        object = xSSimpleType.validate(paramValidatedInfo.stringValue(), paramValidationContext, paramValidatedInfo); 
    } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
      return null;
    } 
    return object;
  }
  
  static void reportSchemaError(XMLErrorReporter paramXMLErrorReporter, SimpleLocator paramSimpleLocator, String paramString, Object[] paramArrayOfObject) {
    if (paramSimpleLocator != null) {
      paramXMLErrorReporter.reportError(paramSimpleLocator, "http://www.w3.org/TR/xml-schema-1", paramString, paramArrayOfObject, (short)1);
    } else {
      paramXMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", paramString, paramArrayOfObject, (short)1);
    } 
  }
  
  public static void fullSchemaChecking(XSGrammarBucket paramXSGrammarBucket, SubstitutionGroupHandler paramSubstitutionGroupHandler, CMBuilder paramCMBuilder, XMLErrorReporter paramXMLErrorReporter) {
    SchemaGrammar[] arrayOfSchemaGrammar = paramXSGrammarBucket.getGrammars();
    for (int i = arrayOfSchemaGrammar.length - 1; i >= 0; i--)
      paramSubstitutionGroupHandler.addSubstitutionGroup(arrayOfSchemaGrammar[i].getSubstitutionGroups()); 
    XSParticleDecl xSParticleDecl1 = new XSParticleDecl();
    XSParticleDecl xSParticleDecl2 = new XSParticleDecl();
    xSParticleDecl1.fType = 3;
    xSParticleDecl2.fType = 3;
    for (int j = arrayOfSchemaGrammar.length - 1; j >= 0; j--) {
      XSGroupDecl[] arrayOfXSGroupDecl = arrayOfSchemaGrammar[j].getRedefinedGroupDecls();
      SimpleLocator[] arrayOfSimpleLocator = arrayOfSchemaGrammar[j].getRGLocators();
      byte b = 0;
      while (b < arrayOfXSGroupDecl.length) {
        XSGroupDecl xSGroupDecl1 = arrayOfXSGroupDecl[b++];
        XSModelGroupImpl xSModelGroupImpl1 = xSGroupDecl1.fModelGroup;
        XSGroupDecl xSGroupDecl2 = arrayOfXSGroupDecl[b++];
        XSModelGroupImpl xSModelGroupImpl2 = xSGroupDecl2.fModelGroup;
        xSParticleDecl1.fValue = xSModelGroupImpl1;
        xSParticleDecl2.fValue = xSModelGroupImpl2;
        if (xSModelGroupImpl2 == null) {
          if (xSModelGroupImpl1 != null)
            reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b / 2 - 1], "src-redefine.6.2.2", new Object[] { xSGroupDecl1.fName, "rcase-Recurse.2" }); 
          continue;
        } 
        if (xSModelGroupImpl1 == null) {
          if (!xSParticleDecl2.emptiable())
            reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b / 2 - 1], "src-redefine.6.2.2", new Object[] { xSGroupDecl1.fName, "rcase-Recurse.2" }); 
          continue;
        } 
        try {
          particleValidRestriction(xSParticleDecl1, paramSubstitutionGroupHandler, xSParticleDecl2, paramSubstitutionGroupHandler);
        } catch (XMLSchemaException xMLSchemaException) {
          String str = xMLSchemaException.getKey();
          reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b / 2 - 1], str, xMLSchemaException.getArgs());
          reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b / 2 - 1], "src-redefine.6.2.2", new Object[] { xSGroupDecl1.fName, str });
        } 
      } 
    } 
    SymbolHash symbolHash = new SymbolHash();
    for (int k = arrayOfSchemaGrammar.length - 1; k >= 0; k--) {
      byte b1 = 0;
      boolean bool = (arrayOfSchemaGrammar[k]).fFullChecked;
      XSComplexTypeDecl[] arrayOfXSComplexTypeDecl = arrayOfSchemaGrammar[k].getUncheckedComplexTypeDecls();
      SimpleLocator[] arrayOfSimpleLocator = arrayOfSchemaGrammar[k].getUncheckedCTLocators();
      for (byte b2 = 0; b2 < arrayOfXSComplexTypeDecl.length; b2++) {
        if (!bool && (arrayOfXSComplexTypeDecl[b2]).fParticle != null) {
          symbolHash.clear();
          try {
            checkElementDeclsConsistent(arrayOfXSComplexTypeDecl[b2], (arrayOfXSComplexTypeDecl[b2]).fParticle, symbolHash, paramSubstitutionGroupHandler);
          } catch (XMLSchemaException xMLSchemaException) {
            reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b2], xMLSchemaException.getKey(), xMLSchemaException.getArgs());
          } 
        } 
        if ((arrayOfXSComplexTypeDecl[b2]).fBaseType != null && (arrayOfXSComplexTypeDecl[b2]).fBaseType != SchemaGrammar.fAnyType && (arrayOfXSComplexTypeDecl[b2]).fDerivedBy == 2 && (arrayOfXSComplexTypeDecl[b2]).fBaseType instanceof XSComplexTypeDecl) {
          XSParticleDecl xSParticleDecl3 = (arrayOfXSComplexTypeDecl[b2]).fParticle;
          XSParticleDecl xSParticleDecl4 = ((XSComplexTypeDecl)(arrayOfXSComplexTypeDecl[b2]).fBaseType).fParticle;
          if (xSParticleDecl3 == null) {
            if (xSParticleDecl4 != null && !xSParticleDecl4.emptiable())
              reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b2], "derivation-ok-restriction.5.3.2", new Object[] { (arrayOfXSComplexTypeDecl[b2]).fName, (arrayOfXSComplexTypeDecl[b2]).fBaseType.getName() }); 
          } else if (xSParticleDecl4 != null) {
            try {
              particleValidRestriction((arrayOfXSComplexTypeDecl[b2]).fParticle, paramSubstitutionGroupHandler, ((XSComplexTypeDecl)(arrayOfXSComplexTypeDecl[b2]).fBaseType).fParticle, paramSubstitutionGroupHandler);
            } catch (XMLSchemaException xMLSchemaException) {
              reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b2], xMLSchemaException.getKey(), xMLSchemaException.getArgs());
              reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b2], "derivation-ok-restriction.5.4.2", new Object[] { (arrayOfXSComplexTypeDecl[b2]).fName });
            } 
          } else {
            reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b2], "derivation-ok-restriction.5.4.2", new Object[] { (arrayOfXSComplexTypeDecl[b2]).fName });
          } 
        } 
        XSCMValidator xSCMValidator = arrayOfXSComplexTypeDecl[b2].getContentModel(paramCMBuilder);
        boolean bool1 = false;
        if (xSCMValidator != null)
          try {
            bool1 = xSCMValidator.checkUniqueParticleAttribution(paramSubstitutionGroupHandler);
          } catch (XMLSchemaException xMLSchemaException) {
            reportSchemaError(paramXMLErrorReporter, arrayOfSimpleLocator[b2], xMLSchemaException.getKey(), xMLSchemaException.getArgs());
          }  
        if (!bool && bool1)
          arrayOfXSComplexTypeDecl[b1++] = arrayOfXSComplexTypeDecl[b2]; 
      } 
      if (!bool) {
        arrayOfSchemaGrammar[k].setUncheckedTypeNum(b1);
        (arrayOfSchemaGrammar[k]).fFullChecked = true;
      } 
    } 
  }
  
  public static void checkElementDeclsConsistent(XSComplexTypeDecl paramXSComplexTypeDecl, XSParticleDecl paramXSParticleDecl, SymbolHash paramSymbolHash, SubstitutionGroupHandler paramSubstitutionGroupHandler) throws XMLSchemaException {
    short s = paramXSParticleDecl.fType;
    if (s == 2)
      return; 
    if (s == 1) {
      XSElementDecl xSElementDecl = (XSElementDecl)paramXSParticleDecl.fValue;
      findElemInTable(paramXSComplexTypeDecl, xSElementDecl, paramSymbolHash);
      if (xSElementDecl.fScope == 1) {
        XSElementDecl[] arrayOfXSElementDecl = paramSubstitutionGroupHandler.getSubstitutionGroup(xSElementDecl);
        for (byte b1 = 0; b1 < arrayOfXSElementDecl.length; b1++)
          findElemInTable(paramXSComplexTypeDecl, arrayOfXSElementDecl[b1], paramSymbolHash); 
      } 
      return;
    } 
    XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)paramXSParticleDecl.fValue;
    for (byte b = 0; b < xSModelGroupImpl.fParticleCount; b++)
      checkElementDeclsConsistent(paramXSComplexTypeDecl, xSModelGroupImpl.fParticles[b], paramSymbolHash, paramSubstitutionGroupHandler); 
  }
  
  public static void findElemInTable(XSComplexTypeDecl paramXSComplexTypeDecl, XSElementDecl paramXSElementDecl, SymbolHash paramSymbolHash) throws XMLSchemaException {
    String str = paramXSElementDecl.fName + "," + paramXSElementDecl.fTargetNamespace;
    XSElementDecl xSElementDecl = null;
    if ((xSElementDecl = (XSElementDecl)paramSymbolHash.get(str)) == null) {
      paramSymbolHash.put(str, paramXSElementDecl);
    } else {
      if (paramXSElementDecl == xSElementDecl)
        return; 
      if (paramXSElementDecl.fType != xSElementDecl.fType)
        throw new XMLSchemaException("cos-element-consistent", new Object[] { paramXSComplexTypeDecl.fName, paramXSElementDecl.fName }); 
    } 
  }
  
  private static boolean particleValidRestriction(XSParticleDecl paramXSParticleDecl1, SubstitutionGroupHandler paramSubstitutionGroupHandler1, XSParticleDecl paramXSParticleDecl2, SubstitutionGroupHandler paramSubstitutionGroupHandler2) throws XMLSchemaException { return particleValidRestriction(paramXSParticleDecl1, paramSubstitutionGroupHandler1, paramXSParticleDecl2, paramSubstitutionGroupHandler2, true); }
  
  private static boolean particleValidRestriction(XSParticleDecl paramXSParticleDecl1, SubstitutionGroupHandler paramSubstitutionGroupHandler1, XSParticleDecl paramXSParticleDecl2, SubstitutionGroupHandler paramSubstitutionGroupHandler2, boolean paramBoolean) throws XMLSchemaException {
    int i3;
    int i2;
    Vector vector1 = null;
    Vector vector2 = null;
    int i = -2;
    int j = -2;
    boolean bool = false;
    if (paramXSParticleDecl1.isEmpty() && !paramXSParticleDecl2.emptiable())
      throw new XMLSchemaException("cos-particle-restrict.a", null); 
    if (!paramXSParticleDecl1.isEmpty() && paramXSParticleDecl2.isEmpty())
      throw new XMLSchemaException("cos-particle-restrict.b", null); 
    short s1 = paramXSParticleDecl1.fType;
    if (s1 == 3) {
      s1 = ((XSModelGroupImpl)paramXSParticleDecl1.fValue).fCompositor;
      XSParticleDecl xSParticleDecl = getNonUnaryGroup(paramXSParticleDecl1);
      if (xSParticleDecl != paramXSParticleDecl1) {
        paramXSParticleDecl1 = xSParticleDecl;
        s1 = paramXSParticleDecl1.fType;
        if (s1 == 3)
          s1 = ((XSModelGroupImpl)paramXSParticleDecl1.fValue).fCompositor; 
      } 
      vector1 = removePointlessChildren(paramXSParticleDecl1);
    } 
    int k = paramXSParticleDecl1.fMinOccurs;
    int m = paramXSParticleDecl1.fMaxOccurs;
    if (paramSubstitutionGroupHandler1 != null && s1 == 1) {
      XSElementDecl xSElementDecl = (XSElementDecl)paramXSParticleDecl1.fValue;
      if (xSElementDecl.fScope == 1) {
        XSElementDecl[] arrayOfXSElementDecl = paramSubstitutionGroupHandler1.getSubstitutionGroup(xSElementDecl);
        if (arrayOfXSElementDecl.length > 0) {
          s1 = 101;
          i = k;
          j = m;
          vector1 = new Vector(arrayOfXSElementDecl.length + 1);
          for (byte b = 0; b < arrayOfXSElementDecl.length; b++)
            addElementToParticleVector(vector1, arrayOfXSElementDecl[b]); 
          addElementToParticleVector(vector1, xSElementDecl);
          Collections.sort(vector1, ELEMENT_PARTICLE_COMPARATOR);
          paramSubstitutionGroupHandler1 = null;
        } 
      } 
    } 
    short s2 = paramXSParticleDecl2.fType;
    if (s2 == 3) {
      s2 = ((XSModelGroupImpl)paramXSParticleDecl2.fValue).fCompositor;
      XSParticleDecl xSParticleDecl = getNonUnaryGroup(paramXSParticleDecl2);
      if (xSParticleDecl != paramXSParticleDecl2) {
        paramXSParticleDecl2 = xSParticleDecl;
        s2 = paramXSParticleDecl2.fType;
        if (s2 == 3)
          s2 = ((XSModelGroupImpl)paramXSParticleDecl2.fValue).fCompositor; 
      } 
      vector2 = removePointlessChildren(paramXSParticleDecl2);
    } 
    int n = paramXSParticleDecl2.fMinOccurs;
    int i1 = paramXSParticleDecl2.fMaxOccurs;
    if (paramSubstitutionGroupHandler2 != null && s2 == 1) {
      XSElementDecl xSElementDecl = (XSElementDecl)paramXSParticleDecl2.fValue;
      if (xSElementDecl.fScope == 1) {
        XSElementDecl[] arrayOfXSElementDecl = paramSubstitutionGroupHandler2.getSubstitutionGroup(xSElementDecl);
        if (arrayOfXSElementDecl.length > 0) {
          s2 = 101;
          vector2 = new Vector(arrayOfXSElementDecl.length + 1);
          for (byte b = 0; b < arrayOfXSElementDecl.length; b++)
            addElementToParticleVector(vector2, arrayOfXSElementDecl[b]); 
          addElementToParticleVector(vector2, xSElementDecl);
          Collections.sort(vector2, ELEMENT_PARTICLE_COMPARATOR);
          paramSubstitutionGroupHandler2 = null;
          bool = true;
        } 
      } 
    } 
    switch (s1) {
      case 1:
        switch (s2) {
          case 1:
            checkNameAndTypeOK((XSElementDecl)paramXSParticleDecl1.fValue, k, m, (XSElementDecl)paramXSParticleDecl2.fValue, n, i1);
            return bool;
          case 2:
            checkNSCompat((XSElementDecl)paramXSParticleDecl1.fValue, k, m, (XSWildcardDecl)paramXSParticleDecl2.fValue, n, i1, paramBoolean);
            return bool;
          case 101:
            vector1 = new Vector();
            vector1.addElement(paramXSParticleDecl1);
            checkRecurseLax(vector1, 1, 1, paramSubstitutionGroupHandler1, vector2, n, i1, paramSubstitutionGroupHandler2);
            return bool;
          case 102:
          case 103:
            vector1 = new Vector();
            vector1.addElement(paramXSParticleDecl1);
            checkRecurse(vector1, 1, 1, paramSubstitutionGroupHandler1, vector2, n, i1, paramSubstitutionGroupHandler2);
            return bool;
        } 
        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
      case 2:
        switch (s2) {
          case 2:
            checkNSSubset((XSWildcardDecl)paramXSParticleDecl1.fValue, k, m, (XSWildcardDecl)paramXSParticleDecl2.fValue, n, i1);
            return bool;
          case 1:
          case 101:
          case 102:
          case 103:
            throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "any:choice,sequence,all,elt" });
        } 
        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
      case 103:
        switch (s2) {
          case 2:
            if (i == -2)
              i = paramXSParticleDecl1.minEffectiveTotalRange(); 
            if (j == -2)
              j = paramXSParticleDecl1.maxEffectiveTotalRange(); 
            checkNSRecurseCheckCardinality(vector1, i, j, paramSubstitutionGroupHandler1, paramXSParticleDecl2, n, i1, paramBoolean);
            return bool;
          case 103:
            checkRecurse(vector1, k, m, paramSubstitutionGroupHandler1, vector2, n, i1, paramSubstitutionGroupHandler2);
            return bool;
          case 1:
          case 101:
          case 102:
            throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "all:choice,sequence,elt" });
        } 
        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
      case 101:
        switch (s2) {
          case 2:
            if (i == -2)
              i = paramXSParticleDecl1.minEffectiveTotalRange(); 
            if (j == -2)
              j = paramXSParticleDecl1.maxEffectiveTotalRange(); 
            checkNSRecurseCheckCardinality(vector1, i, j, paramSubstitutionGroupHandler1, paramXSParticleDecl2, n, i1, paramBoolean);
            return bool;
          case 101:
            checkRecurseLax(vector1, k, m, paramSubstitutionGroupHandler1, vector2, n, i1, paramSubstitutionGroupHandler2);
            return bool;
          case 1:
          case 102:
          case 103:
            throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "choice:all,sequence,elt" });
        } 
        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
      case 102:
        switch (s2) {
          case 2:
            if (i == -2)
              i = paramXSParticleDecl1.minEffectiveTotalRange(); 
            if (j == -2)
              j = paramXSParticleDecl1.maxEffectiveTotalRange(); 
            checkNSRecurseCheckCardinality(vector1, i, j, paramSubstitutionGroupHandler1, paramXSParticleDecl2, n, i1, paramBoolean);
            return bool;
          case 103:
            checkRecurseUnordered(vector1, k, m, paramSubstitutionGroupHandler1, vector2, n, i1, paramSubstitutionGroupHandler2);
            return bool;
          case 102:
            checkRecurse(vector1, k, m, paramSubstitutionGroupHandler1, vector2, n, i1, paramSubstitutionGroupHandler2);
            return bool;
          case 101:
            i2 = k * vector1.size();
            i3 = (m == -1) ? m : (m * vector1.size());
            checkMapAndSum(vector1, i2, i3, paramSubstitutionGroupHandler1, vector2, n, i1, paramSubstitutionGroupHandler2);
            return bool;
          case 1:
            throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "seq:elt" });
        } 
        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
    } 
    return bool;
  }
  
  private static void addElementToParticleVector(Vector paramVector, XSElementDecl paramXSElementDecl) {
    XSParticleDecl xSParticleDecl = new XSParticleDecl();
    xSParticleDecl.fValue = paramXSElementDecl;
    xSParticleDecl.fType = 1;
    paramVector.addElement(xSParticleDecl);
  }
  
  private static XSParticleDecl getNonUnaryGroup(XSParticleDecl paramXSParticleDecl) { return (paramXSParticleDecl.fType == 1 || paramXSParticleDecl.fType == 2) ? paramXSParticleDecl : ((paramXSParticleDecl.fMinOccurs == 1 && paramXSParticleDecl.fMaxOccurs == 1 && paramXSParticleDecl.fValue != null && ((XSModelGroupImpl)paramXSParticleDecl.fValue).fParticleCount == 1) ? getNonUnaryGroup(((XSModelGroupImpl)paramXSParticleDecl.fValue).fParticles[0]) : paramXSParticleDecl); }
  
  private static Vector removePointlessChildren(XSParticleDecl paramXSParticleDecl) {
    if (paramXSParticleDecl.fType == 1 || paramXSParticleDecl.fType == 2)
      return null; 
    Vector vector = new Vector();
    XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)paramXSParticleDecl.fValue;
    for (byte b = 0; b < xSModelGroupImpl.fParticleCount; b++)
      gatherChildren(xSModelGroupImpl.fCompositor, xSModelGroupImpl.fParticles[b], vector); 
    return vector;
  }
  
  private static void gatherChildren(int paramInt, XSParticleDecl paramXSParticleDecl, Vector paramVector) {
    int i = paramXSParticleDecl.fMinOccurs;
    int j = paramXSParticleDecl.fMaxOccurs;
    short s = paramXSParticleDecl.fType;
    if (s == 3)
      s = ((XSModelGroupImpl)paramXSParticleDecl.fValue).fCompositor; 
    if (s == 1 || s == 2) {
      paramVector.addElement(paramXSParticleDecl);
      return;
    } 
    if (i != 1 || j != 1) {
      paramVector.addElement(paramXSParticleDecl);
    } else if (paramInt == s) {
      XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)paramXSParticleDecl.fValue;
      for (byte b = 0; b < xSModelGroupImpl.fParticleCount; b++)
        gatherChildren(s, xSModelGroupImpl.fParticles[b], paramVector); 
    } else if (!paramXSParticleDecl.isEmpty()) {
      paramVector.addElement(paramXSParticleDecl);
    } 
  }
  
  private static void checkNameAndTypeOK(XSElementDecl paramXSElementDecl1, int paramInt1, int paramInt2, XSElementDecl paramXSElementDecl2, int paramInt3, int paramInt4) throws XMLSchemaException {
    if (paramXSElementDecl1.fName != paramXSElementDecl2.fName || paramXSElementDecl1.fTargetNamespace != paramXSElementDecl2.fTargetNamespace)
      throw new XMLSchemaException("rcase-NameAndTypeOK.1", new Object[] { paramXSElementDecl1.fName, paramXSElementDecl1.fTargetNamespace, paramXSElementDecl2.fName, paramXSElementDecl2.fTargetNamespace }); 
    if (!paramXSElementDecl2.getNillable() && paramXSElementDecl1.getNillable())
      throw new XMLSchemaException("rcase-NameAndTypeOK.2", new Object[] { paramXSElementDecl1.fName }); 
    if (!checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-NameAndTypeOK.3", new Object[] { paramXSElementDecl1.fName, Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    if (paramXSElementDecl2.getConstraintType() == 2) {
      if (paramXSElementDecl1.getConstraintType() != 2)
        throw new XMLSchemaException("rcase-NameAndTypeOK.4.a", new Object[] { paramXSElementDecl1.fName, paramXSElementDecl2.fDefault.stringValue() }); 
      boolean bool = false;
      if (paramXSElementDecl1.fType.getTypeCategory() == 16 || ((XSComplexTypeDecl)paramXSElementDecl1.fType).fContentType == 1)
        bool = true; 
      if ((!bool && !paramXSElementDecl2.fDefault.normalizedValue.equals(paramXSElementDecl1.fDefault.normalizedValue)) || (bool && !paramXSElementDecl2.fDefault.actualValue.equals(paramXSElementDecl1.fDefault.actualValue)))
        throw new XMLSchemaException("rcase-NameAndTypeOK.4.b", new Object[] { paramXSElementDecl1.fName, paramXSElementDecl1.fDefault.stringValue(), paramXSElementDecl2.fDefault.stringValue() }); 
    } 
    checkIDConstraintRestriction(paramXSElementDecl1, paramXSElementDecl2);
    short s1 = paramXSElementDecl1.fBlock;
    short s2 = paramXSElementDecl2.fBlock;
    if ((s1 & s2) != s2 || (s1 == 0 && s2 != 0))
      throw new XMLSchemaException("rcase-NameAndTypeOK.6", new Object[] { paramXSElementDecl1.fName }); 
    if (!checkTypeDerivationOk(paramXSElementDecl1.fType, paramXSElementDecl2.fType, (short)25))
      throw new XMLSchemaException("rcase-NameAndTypeOK.7", new Object[] { paramXSElementDecl1.fName, paramXSElementDecl1.fType.getName(), paramXSElementDecl2.fType.getName() }); 
  }
  
  private static void checkIDConstraintRestriction(XSElementDecl paramXSElementDecl1, XSElementDecl paramXSElementDecl2) throws XMLSchemaException {}
  
  private static boolean checkOccurrenceRange(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (paramInt1 >= paramInt3 && (paramInt4 == -1 || (paramInt2 != -1 && paramInt2 <= paramInt4))); }
  
  private static void checkNSCompat(XSElementDecl paramXSElementDecl, int paramInt1, int paramInt2, XSWildcardDecl paramXSWildcardDecl, int paramInt3, int paramInt4, boolean paramBoolean) throws XMLSchemaException {
    if (paramBoolean && !checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-NSCompat.2", new Object[] { paramXSElementDecl.fName, Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    if (!paramXSWildcardDecl.allowNamespace(paramXSElementDecl.fTargetNamespace))
      throw new XMLSchemaException("rcase-NSCompat.1", new Object[] { paramXSElementDecl.fName, paramXSElementDecl.fTargetNamespace }); 
  }
  
  private static void checkNSSubset(XSWildcardDecl paramXSWildcardDecl1, int paramInt1, int paramInt2, XSWildcardDecl paramXSWildcardDecl2, int paramInt3, int paramInt4) throws XMLSchemaException {
    if (!checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-NSSubset.2", new Object[] { Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    if (!paramXSWildcardDecl1.isSubsetOf(paramXSWildcardDecl2))
      throw new XMLSchemaException("rcase-NSSubset.1", null); 
    if (paramXSWildcardDecl1.weakerProcessContents(paramXSWildcardDecl2))
      throw new XMLSchemaException("rcase-NSSubset.3", new Object[] { paramXSWildcardDecl1.getProcessContentsAsString(), paramXSWildcardDecl2.getProcessContentsAsString() }); 
  }
  
  private static void checkNSRecurseCheckCardinality(Vector paramVector, int paramInt1, int paramInt2, SubstitutionGroupHandler paramSubstitutionGroupHandler, XSParticleDecl paramXSParticleDecl, int paramInt3, int paramInt4, boolean paramBoolean) throws XMLSchemaException {
    if (paramBoolean && !checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.2", new Object[] { Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    int i = paramVector.size();
    try {
      for (byte b = 0; b < i; b++) {
        XSParticleDecl xSParticleDecl = (XSParticleDecl)paramVector.elementAt(b);
        particleValidRestriction(xSParticleDecl, paramSubstitutionGroupHandler, paramXSParticleDecl, null, false);
      } 
    } catch (XMLSchemaException xMLSchemaException) {
      throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.1", null);
    } 
  }
  
  private static void checkRecurse(Vector paramVector1, int paramInt1, int paramInt2, SubstitutionGroupHandler paramSubstitutionGroupHandler1, Vector paramVector2, int paramInt3, int paramInt4, SubstitutionGroupHandler paramSubstitutionGroupHandler2) throws XMLSchemaException {
    if (!checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-Recurse.1", new Object[] { Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    int i = paramVector1.size();
    int j = paramVector2.size();
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < i; b2++) {
      XSParticleDecl xSParticleDecl = (XSParticleDecl)paramVector1.elementAt(b2);
      byte b = b1;
      while (true) {
        if (b < j) {
          XSParticleDecl xSParticleDecl1 = (XSParticleDecl)paramVector2.elementAt(b);
          b1++;
          try {
            particleValidRestriction(xSParticleDecl, paramSubstitutionGroupHandler1, xSParticleDecl1, paramSubstitutionGroupHandler2);
            break;
          } catch (XMLSchemaException xMLSchemaException) {
            if (!xSParticleDecl1.emptiable())
              throw new XMLSchemaException("rcase-Recurse.2", null); 
            b++;
            continue;
          } 
        } 
        throw new XMLSchemaException("rcase-Recurse.2", null);
      } 
    } 
    for (b2 = b1; b2 < j; b2++) {
      XSParticleDecl xSParticleDecl = (XSParticleDecl)paramVector2.elementAt(b2);
      if (!xSParticleDecl.emptiable())
        throw new XMLSchemaException("rcase-Recurse.2", null); 
    } 
  }
  
  private static void checkRecurseUnordered(Vector paramVector1, int paramInt1, int paramInt2, SubstitutionGroupHandler paramSubstitutionGroupHandler1, Vector paramVector2, int paramInt3, int paramInt4, SubstitutionGroupHandler paramSubstitutionGroupHandler2) throws XMLSchemaException {
    if (!checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-RecurseUnordered.1", new Object[] { Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    int i = paramVector1.size();
    int j = paramVector2.size();
    boolean[] arrayOfBoolean = new boolean[j];
    byte b;
    for (b = 0; b < i; b++) {
      XSParticleDecl xSParticleDecl = (XSParticleDecl)paramVector1.elementAt(b);
      byte b1 = 0;
      while (true) {
        if (b1 < j) {
          XSParticleDecl xSParticleDecl1 = (XSParticleDecl)paramVector2.elementAt(b1);
          try {
            particleValidRestriction(xSParticleDecl, paramSubstitutionGroupHandler1, xSParticleDecl1, paramSubstitutionGroupHandler2);
            if (arrayOfBoolean[b1])
              throw new XMLSchemaException("rcase-RecurseUnordered.2", null); 
            arrayOfBoolean[b1] = true;
            break;
          } catch (XMLSchemaException xMLSchemaException) {
            b1++;
            continue;
          } 
        } 
        throw new XMLSchemaException("rcase-RecurseUnordered.2", null);
      } 
    } 
    for (b = 0; b < j; b++) {
      XSParticleDecl xSParticleDecl = (XSParticleDecl)paramVector2.elementAt(b);
      if (!arrayOfBoolean[b] && !xSParticleDecl.emptiable())
        throw new XMLSchemaException("rcase-RecurseUnordered.2", null); 
    } 
  }
  
  private static void checkRecurseLax(Vector paramVector1, int paramInt1, int paramInt2, SubstitutionGroupHandler paramSubstitutionGroupHandler1, Vector paramVector2, int paramInt3, int paramInt4, SubstitutionGroupHandler paramSubstitutionGroupHandler2) throws XMLSchemaException {
    if (!checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-RecurseLax.1", new Object[] { Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    int i = paramVector1.size();
    int j = paramVector2.size();
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      XSParticleDecl xSParticleDecl = (XSParticleDecl)paramVector1.elementAt(b2);
      byte b = b1;
      while (true) {
        if (b < j) {
          XSParticleDecl xSParticleDecl1 = (XSParticleDecl)paramVector2.elementAt(b);
          b1++;
          try {
            if (particleValidRestriction(xSParticleDecl, paramSubstitutionGroupHandler1, xSParticleDecl1, paramSubstitutionGroupHandler2))
              b1--; 
            break;
          } catch (XMLSchemaException xMLSchemaException) {
            b++;
            continue;
          } 
        } 
        throw new XMLSchemaException("rcase-RecurseLax.2", null);
      } 
    } 
  }
  
  private static void checkMapAndSum(Vector paramVector1, int paramInt1, int paramInt2, SubstitutionGroupHandler paramSubstitutionGroupHandler1, Vector paramVector2, int paramInt3, int paramInt4, SubstitutionGroupHandler paramSubstitutionGroupHandler2) throws XMLSchemaException {
    if (!checkOccurrenceRange(paramInt1, paramInt2, paramInt3, paramInt4))
      throw new XMLSchemaException("rcase-MapAndSum.2", new Object[] { Integer.toString(paramInt1), (paramInt2 == -1) ? "unbounded" : Integer.toString(paramInt2), Integer.toString(paramInt3), (paramInt4 == -1) ? "unbounded" : Integer.toString(paramInt4) }); 
    int i = paramVector1.size();
    int j = paramVector2.size();
    for (byte b = 0; b < i; b++) {
      XSParticleDecl xSParticleDecl = (XSParticleDecl)paramVector1.elementAt(b);
      byte b1 = 0;
      while (true) {
        if (b1 < j) {
          XSParticleDecl xSParticleDecl1 = (XSParticleDecl)paramVector2.elementAt(b1);
          try {
            particleValidRestriction(xSParticleDecl, paramSubstitutionGroupHandler1, xSParticleDecl1, paramSubstitutionGroupHandler2);
            break;
          } catch (XMLSchemaException xMLSchemaException) {
            b1++;
            continue;
          } 
        } 
        throw new XMLSchemaException("rcase-MapAndSum.1", null);
      } 
    } 
  }
  
  public static boolean overlapUPA(XSElementDecl paramXSElementDecl1, XSElementDecl paramXSElementDecl2, SubstitutionGroupHandler paramSubstitutionGroupHandler) {
    if (paramXSElementDecl1.fName == paramXSElementDecl2.fName && paramXSElementDecl1.fTargetNamespace == paramXSElementDecl2.fTargetNamespace)
      return true; 
    XSElementDecl[] arrayOfXSElementDecl = paramSubstitutionGroupHandler.getSubstitutionGroup(paramXSElementDecl1);
    int i;
    for (i = arrayOfXSElementDecl.length - 1; i >= 0; i--) {
      if ((arrayOfXSElementDecl[i]).fName == paramXSElementDecl2.fName && (arrayOfXSElementDecl[i]).fTargetNamespace == paramXSElementDecl2.fTargetNamespace)
        return true; 
    } 
    arrayOfXSElementDecl = paramSubstitutionGroupHandler.getSubstitutionGroup(paramXSElementDecl2);
    for (i = arrayOfXSElementDecl.length - 1; i >= 0; i--) {
      if ((arrayOfXSElementDecl[i]).fName == paramXSElementDecl1.fName && (arrayOfXSElementDecl[i]).fTargetNamespace == paramXSElementDecl1.fTargetNamespace)
        return true; 
    } 
    return false;
  }
  
  public static boolean overlapUPA(XSElementDecl paramXSElementDecl, XSWildcardDecl paramXSWildcardDecl, SubstitutionGroupHandler paramSubstitutionGroupHandler) {
    if (paramXSWildcardDecl.allowNamespace(paramXSElementDecl.fTargetNamespace))
      return true; 
    XSElementDecl[] arrayOfXSElementDecl = paramSubstitutionGroupHandler.getSubstitutionGroup(paramXSElementDecl);
    for (int i = arrayOfXSElementDecl.length - 1; i >= 0; i--) {
      if (paramXSWildcardDecl.allowNamespace((arrayOfXSElementDecl[i]).fTargetNamespace))
        return true; 
    } 
    return false;
  }
  
  public static boolean overlapUPA(XSWildcardDecl paramXSWildcardDecl1, XSWildcardDecl paramXSWildcardDecl2) {
    XSWildcardDecl xSWildcardDecl = paramXSWildcardDecl1.performIntersectionWith(paramXSWildcardDecl2, paramXSWildcardDecl1.fProcessContents);
    return (xSWildcardDecl == null || xSWildcardDecl.fType != 3 || xSWildcardDecl.fNamespaceList.length != 0);
  }
  
  public static boolean overlapUPA(Object paramObject1, Object paramObject2, SubstitutionGroupHandler paramSubstitutionGroupHandler) { return (paramObject1 instanceof XSElementDecl) ? ((paramObject2 instanceof XSElementDecl) ? overlapUPA((XSElementDecl)paramObject1, (XSElementDecl)paramObject2, paramSubstitutionGroupHandler) : overlapUPA((XSElementDecl)paramObject1, (XSWildcardDecl)paramObject2, paramSubstitutionGroupHandler)) : ((paramObject2 instanceof XSElementDecl) ? overlapUPA((XSElementDecl)paramObject2, (XSWildcardDecl)paramObject1, paramSubstitutionGroupHandler) : overlapUPA((XSWildcardDecl)paramObject1, (XSWildcardDecl)paramObject2)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */