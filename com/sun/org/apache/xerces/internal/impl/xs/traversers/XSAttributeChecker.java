package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSGrammarBucket;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XIntPool;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class XSAttributeChecker {
  private static final String ELEMENT_N = "element_n";
  
  private static final String ELEMENT_R = "element_r";
  
  private static final String ATTRIBUTE_N = "attribute_n";
  
  private static final String ATTRIBUTE_R = "attribute_r";
  
  private static int ATTIDX_COUNT = 0;
  
  public static final int ATTIDX_ABSTRACT = ATTIDX_COUNT++;
  
  public static final int ATTIDX_AFORMDEFAULT = ATTIDX_COUNT++;
  
  public static final int ATTIDX_BASE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_BLOCK = ATTIDX_COUNT++;
  
  public static final int ATTIDX_BLOCKDEFAULT = ATTIDX_COUNT++;
  
  public static final int ATTIDX_DEFAULT = ATTIDX_COUNT++;
  
  public static final int ATTIDX_EFORMDEFAULT = ATTIDX_COUNT++;
  
  public static final int ATTIDX_FINAL = ATTIDX_COUNT++;
  
  public static final int ATTIDX_FINALDEFAULT = ATTIDX_COUNT++;
  
  public static final int ATTIDX_FIXED = ATTIDX_COUNT++;
  
  public static final int ATTIDX_FORM = ATTIDX_COUNT++;
  
  public static final int ATTIDX_ID = ATTIDX_COUNT++;
  
  public static final int ATTIDX_ITEMTYPE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_MAXOCCURS = ATTIDX_COUNT++;
  
  public static final int ATTIDX_MEMBERTYPES = ATTIDX_COUNT++;
  
  public static final int ATTIDX_MINOCCURS = ATTIDX_COUNT++;
  
  public static final int ATTIDX_MIXED = ATTIDX_COUNT++;
  
  public static final int ATTIDX_NAME = ATTIDX_COUNT++;
  
  public static final int ATTIDX_NAMESPACE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_NAMESPACE_LIST = ATTIDX_COUNT++;
  
  public static final int ATTIDX_NILLABLE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_NONSCHEMA = ATTIDX_COUNT++;
  
  public static final int ATTIDX_PROCESSCONTENTS = ATTIDX_COUNT++;
  
  public static final int ATTIDX_PUBLIC = ATTIDX_COUNT++;
  
  public static final int ATTIDX_REF = ATTIDX_COUNT++;
  
  public static final int ATTIDX_REFER = ATTIDX_COUNT++;
  
  public static final int ATTIDX_SCHEMALOCATION = ATTIDX_COUNT++;
  
  public static final int ATTIDX_SOURCE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_SUBSGROUP = ATTIDX_COUNT++;
  
  public static final int ATTIDX_SYSTEM = ATTIDX_COUNT++;
  
  public static final int ATTIDX_TARGETNAMESPACE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_TYPE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_USE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_VALUE = ATTIDX_COUNT++;
  
  public static final int ATTIDX_ENUMNSDECLS = ATTIDX_COUNT++;
  
  public static final int ATTIDX_VERSION = ATTIDX_COUNT++;
  
  public static final int ATTIDX_XML_LANG = ATTIDX_COUNT++;
  
  public static final int ATTIDX_XPATH = ATTIDX_COUNT++;
  
  public static final int ATTIDX_FROMDEFAULT = ATTIDX_COUNT++;
  
  public static final int ATTIDX_ISRETURNED = ATTIDX_COUNT++;
  
  private static final XIntPool fXIntPool = new XIntPool();
  
  private static final XInt INT_QUALIFIED = fXIntPool.getXInt(1);
  
  private static final XInt INT_UNQUALIFIED = fXIntPool.getXInt(0);
  
  private static final XInt INT_EMPTY_SET = fXIntPool.getXInt(0);
  
  private static final XInt INT_ANY_STRICT = fXIntPool.getXInt(1);
  
  private static final XInt INT_ANY_LAX = fXIntPool.getXInt(3);
  
  private static final XInt INT_ANY_SKIP = fXIntPool.getXInt(2);
  
  private static final XInt INT_ANY_ANY = fXIntPool.getXInt(1);
  
  private static final XInt INT_ANY_LIST = fXIntPool.getXInt(3);
  
  private static final XInt INT_ANY_NOT = fXIntPool.getXInt(2);
  
  private static final XInt INT_USE_OPTIONAL = fXIntPool.getXInt(0);
  
  private static final XInt INT_USE_REQUIRED = fXIntPool.getXInt(1);
  
  private static final XInt INT_USE_PROHIBITED = fXIntPool.getXInt(2);
  
  private static final XInt INT_WS_PRESERVE = fXIntPool.getXInt(0);
  
  private static final XInt INT_WS_REPLACE = fXIntPool.getXInt(1);
  
  private static final XInt INT_WS_COLLAPSE = fXIntPool.getXInt(2);
  
  private static final XInt INT_UNBOUNDED = fXIntPool.getXInt(-1);
  
  private static final Map fEleAttrsMapG = new HashMap(29);
  
  private static final Map fEleAttrsMapL = new HashMap(79);
  
  protected static final int DT_ANYURI = 0;
  
  protected static final int DT_ID = 1;
  
  protected static final int DT_QNAME = 2;
  
  protected static final int DT_STRING = 3;
  
  protected static final int DT_TOKEN = 4;
  
  protected static final int DT_NCNAME = 5;
  
  protected static final int DT_XPATH = 6;
  
  protected static final int DT_XPATH1 = 7;
  
  protected static final int DT_LANGUAGE = 8;
  
  protected static final int DT_COUNT = 9;
  
  private static final XSSimpleType[] fExtraDVs = new XSSimpleType[9];
  
  protected static final int DT_BLOCK = -1;
  
  protected static final int DT_BLOCK1 = -2;
  
  protected static final int DT_FINAL = -3;
  
  protected static final int DT_FINAL1 = -4;
  
  protected static final int DT_FINAL2 = -5;
  
  protected static final int DT_FORM = -6;
  
  protected static final int DT_MAXOCCURS = -7;
  
  protected static final int DT_MAXOCCURS1 = -8;
  
  protected static final int DT_MEMBERTYPES = -9;
  
  protected static final int DT_MINOCCURS1 = -10;
  
  protected static final int DT_NAMESPACE = -11;
  
  protected static final int DT_PROCESSCONTENTS = -12;
  
  protected static final int DT_USE = -13;
  
  protected static final int DT_WHITESPACE = -14;
  
  protected static final int DT_BOOLEAN = -15;
  
  protected static final int DT_NONNEGINT = -16;
  
  protected static final int DT_POSINT = -17;
  
  protected XSDHandler fSchemaHandler = null;
  
  protected SymbolTable fSymbolTable = null;
  
  protected Map fNonSchemaAttrs = new HashMap();
  
  protected Vector fNamespaceList = new Vector();
  
  protected boolean[] fSeen = new boolean[ATTIDX_COUNT];
  
  private static boolean[] fSeenTemp;
  
  static final int INIT_POOL_SIZE = 10;
  
  static final int INC_POOL_SIZE = 10;
  
  Object[][] fArrayPool = new Object[10][ATTIDX_COUNT];
  
  private static Object[] fTempArray;
  
  int fPoolPos = 0;
  
  public XSAttributeChecker(XSDHandler paramXSDHandler) { this.fSchemaHandler = paramXSDHandler; }
  
  public void reset(SymbolTable paramSymbolTable) {
    this.fSymbolTable = paramSymbolTable;
    this.fNonSchemaAttrs.clear();
  }
  
  public Object[] checkAttributes(Element paramElement, boolean paramBoolean, XSDocumentInfo paramXSDocumentInfo) { return checkAttributes(paramElement, paramBoolean, paramXSDocumentInfo, false); }
  
  public Object[] checkAttributes(Element paramElement, boolean paramBoolean1, XSDocumentInfo paramXSDocumentInfo, boolean paramBoolean2) {
    if (paramElement == null)
      return null; 
    Attr[] arrayOfAttr = DOMUtil.getAttrs(paramElement);
    resolveNamespace(paramElement, arrayOfAttr, paramXSDocumentInfo.fNamespaceSupport);
    String str1 = DOMUtil.getNamespaceURI(paramElement);
    String str2 = DOMUtil.getLocalName(paramElement);
    if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(str1))
      reportSchemaError("s4s-elt-schema-ns", new Object[] { str2 }, paramElement); 
    Map map = fEleAttrsMapG;
    String str3 = str2;
    if (!paramBoolean1) {
      map = fEleAttrsMapL;
      if (str2.equals(SchemaSymbols.ELT_ELEMENT)) {
        if (DOMUtil.getAttr(paramElement, SchemaSymbols.ATT_REF) != null) {
          str3 = "element_r";
        } else {
          str3 = "element_n";
        } 
      } else if (str2.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
        if (DOMUtil.getAttr(paramElement, SchemaSymbols.ATT_REF) != null) {
          str3 = "attribute_r";
        } else {
          str3 = "attribute_n";
        } 
      } 
    } 
    Container container = (Container)map.get(str3);
    if (container == null) {
      reportSchemaError("s4s-elt-invalid", new Object[] { str2 }, paramElement);
      return null;
    } 
    Object[] arrayOfObject = getAvailableArray();
    long l = 0L;
    System.arraycopy(fSeenTemp, 0, this.fSeen, 0, ATTIDX_COUNT);
    int i = arrayOfAttr.length;
    Attr attr = null;
    for (byte b = 0; b < i; b++) {
      attr = arrayOfAttr[b];
      String str4 = attr.getName();
      String str5 = DOMUtil.getNamespaceURI(attr);
      String str6 = DOMUtil.getValue(attr);
      if (str4.startsWith("xml")) {
        String str = DOMUtil.getPrefix(attr);
        if ("xmlns".equals(str) || "xmlns".equals(str4))
          continue; 
        if (SchemaSymbols.ATT_XML_LANG.equals(str4) && (SchemaSymbols.ELT_SCHEMA.equals(str2) || SchemaSymbols.ELT_DOCUMENTATION.equals(str2)))
          str5 = null; 
      } 
      if (str5 != null && str5.length() != 0) {
        if (str5.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
          reportSchemaError("s4s-att-not-allowed", new Object[] { str2, str4 }, paramElement);
        } else {
          if (arrayOfObject[ATTIDX_NONSCHEMA] == null)
            arrayOfObject[ATTIDX_NONSCHEMA] = new Vector(4, 2); 
          ((Vector)arrayOfObject[ATTIDX_NONSCHEMA]).addElement(str4);
          ((Vector)arrayOfObject[ATTIDX_NONSCHEMA]).addElement(str6);
        } 
      } else {
        OneAttr oneAttr = container.get(str4);
        if (oneAttr == null) {
          reportSchemaError("s4s-att-not-allowed", new Object[] { str2, str4 }, paramElement);
        } else {
          this.fSeen[oneAttr.valueIndex] = true;
          try {
            if (oneAttr.dvIndex >= 0) {
              if (oneAttr.dvIndex != 3 && oneAttr.dvIndex != 6 && oneAttr.dvIndex != 7) {
                XSSimpleType xSSimpleType = fExtraDVs[oneAttr.dvIndex];
                Object object = xSSimpleType.validate(str6, paramXSDocumentInfo.fValidationContext, null);
                if (oneAttr.dvIndex == 2) {
                  QName qName = (QName)object;
                  if (qName.prefix == XMLSymbols.EMPTY_STRING && qName.uri == null && paramXSDocumentInfo.fIsChameleonSchema)
                    qName.uri = paramXSDocumentInfo.fTargetNamespace; 
                } 
                arrayOfObject[oneAttr.valueIndex] = object;
              } else {
                arrayOfObject[oneAttr.valueIndex] = str6;
              } 
            } else {
              arrayOfObject[oneAttr.valueIndex] = validate(arrayOfObject, str4, str6, oneAttr.dvIndex, paramXSDocumentInfo);
            } 
          } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
            reportSchemaError("s4s-att-invalid-value", new Object[] { str2, str4, invalidDatatypeValueException.getMessage() }, paramElement);
            if (oneAttr.dfltValue != null)
              arrayOfObject[oneAttr.valueIndex] = oneAttr.dfltValue; 
          } 
          if (str2.equals(SchemaSymbols.ELT_ENUMERATION) && paramBoolean2)
            arrayOfObject[ATTIDX_ENUMNSDECLS] = new SchemaNamespaceSupport(paramXSDocumentInfo.fNamespaceSupport); 
        } 
      } 
      continue;
    } 
    OneAttr[] arrayOfOneAttr = container.values;
    int j;
    for (j = 0; j < arrayOfOneAttr.length; j++) {
      OneAttr oneAttr = arrayOfOneAttr[j];
      if (oneAttr.dfltValue != null && !this.fSeen[oneAttr.valueIndex]) {
        arrayOfObject[oneAttr.valueIndex] = oneAttr.dfltValue;
        l |= (1 << oneAttr.valueIndex);
      } 
    } 
    arrayOfObject[ATTIDX_FROMDEFAULT] = new Long(l);
    if (arrayOfObject[ATTIDX_MAXOCCURS] != null) {
      j = ((XInt)arrayOfObject[ATTIDX_MINOCCURS]).intValue();
      int k = ((XInt)arrayOfObject[ATTIDX_MAXOCCURS]).intValue();
      if (k != -1) {
        if (this.fSchemaHandler.fSecurityManager != null) {
          String str = paramElement.getLocalName();
          boolean bool = ((str.equals("element") || str.equals("any")) && paramElement.getNextSibling() == null && paramElement.getPreviousSibling() == null && paramElement.getParentNode().getLocalName().equals("sequence")) ? 1 : 0;
          if (!bool) {
            int m = this.fSchemaHandler.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT);
            if (k > m && !this.fSchemaHandler.fSecurityManager.isNoLimit(m)) {
              reportSchemaFatalError("MaxOccurLimit", new Object[] { new Integer(m) }, paramElement);
              arrayOfObject[ATTIDX_MAXOCCURS] = fXIntPool.getXInt(m);
              k = m;
            } 
          } 
        } 
        if (j > k) {
          reportSchemaError("p-props-correct.2.1", new Object[] { str2, arrayOfObject[ATTIDX_MINOCCURS], arrayOfObject[ATTIDX_MAXOCCURS] }, paramElement);
          arrayOfObject[ATTIDX_MINOCCURS] = arrayOfObject[ATTIDX_MAXOCCURS];
        } 
      } 
    } 
    return arrayOfObject;
  }
  
  private Object validate(Object[] paramArrayOfObject, String paramString1, String paramString2, int paramInt, XSDocumentInfo paramXSDocumentInfo) throws InvalidDatatypeValueException {
    String[] arrayOfString;
    int i;
    StringTokenizer stringTokenizer;
    byte b;
    Vector vector;
    if (paramString2 == null)
      return null; 
    String str = XMLChar.trim(paramString2);
    XInt xInt = null;
    switch (paramInt) {
      case -15:
        if (str.equals("false") || str.equals("0")) {
          xInt = Boolean.FALSE;
          break;
        } 
        if (str.equals("true") || str.equals("1")) {
          xInt = Boolean.TRUE;
          break;
        } 
        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { str, "boolean" });
      case -16:
        try {
          if (str.length() > 0 && str.charAt(0) == '+')
            str = str.substring(1); 
          xInt = fXIntPool.getXInt(Integer.parseInt(str));
        } catch (NumberFormatException numberFormatException) {
          throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { str, "nonNegativeInteger" });
        } 
        if (((XInt)xInt).intValue() < 0)
          throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { str, "nonNegativeInteger" }); 
        break;
      case -17:
        try {
          if (str.length() > 0 && str.charAt(0) == '+')
            str = str.substring(1); 
          xInt = fXIntPool.getXInt(Integer.parseInt(str));
        } catch (NumberFormatException numberFormatException) {
          throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { str, "positiveInteger" });
        } 
        if (((XInt)xInt).intValue() <= 0)
          throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { str, "positiveInteger" }); 
        break;
      case -1:
        b = 0;
        if (str.equals("#all")) {
          b = 7;
        } else {
          StringTokenizer stringTokenizer1 = new StringTokenizer(str, " \n\t\r");
          while (stringTokenizer1.hasMoreTokens()) {
            String str1 = stringTokenizer1.nextToken();
            if (str1.equals("extension")) {
              b |= 0x1;
              continue;
            } 
            if (str1.equals("restriction")) {
              b |= 0x2;
              continue;
            } 
            if (str1.equals("substitution")) {
              b |= 0x4;
              continue;
            } 
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { str, "(#all | List of (extension | restriction | substitution))" });
          } 
        } 
        xInt = fXIntPool.getXInt(b);
        break;
      case -3:
      case -2:
        b = 0;
        if (str.equals("#all")) {
          b = 31;
        } else {
          StringTokenizer stringTokenizer1 = new StringTokenizer(str, " \n\t\r");
          while (stringTokenizer1.hasMoreTokens()) {
            String str1 = stringTokenizer1.nextToken();
            if (str1.equals("extension")) {
              b |= 0x1;
              continue;
            } 
            if (str1.equals("restriction")) {
              b |= 0x2;
              continue;
            } 
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { str, "(#all | List of (extension | restriction))" });
          } 
        } 
        xInt = fXIntPool.getXInt(b);
        break;
      case -4:
        b = 0;
        if (str.equals("#all")) {
          b = 31;
        } else {
          StringTokenizer stringTokenizer1 = new StringTokenizer(str, " \n\t\r");
          while (stringTokenizer1.hasMoreTokens()) {
            String str1 = stringTokenizer1.nextToken();
            if (str1.equals("list")) {
              b |= 0x10;
              continue;
            } 
            if (str1.equals("union")) {
              b |= 0x8;
              continue;
            } 
            if (str1.equals("restriction")) {
              b |= 0x2;
              continue;
            } 
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { str, "(#all | List of (list | union | restriction))" });
          } 
        } 
        xInt = fXIntPool.getXInt(b);
        break;
      case -5:
        b = 0;
        if (str.equals("#all")) {
          b = 31;
        } else {
          StringTokenizer stringTokenizer1 = new StringTokenizer(str, " \n\t\r");
          while (stringTokenizer1.hasMoreTokens()) {
            String str1 = stringTokenizer1.nextToken();
            if (str1.equals("extension")) {
              b |= 0x1;
              continue;
            } 
            if (str1.equals("restriction")) {
              b |= 0x2;
              continue;
            } 
            if (str1.equals("list")) {
              b |= 0x10;
              continue;
            } 
            if (str1.equals("union")) {
              b |= 0x8;
              continue;
            } 
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { str, "(#all | List of (extension | restriction | list | union))" });
          } 
        } 
        xInt = fXIntPool.getXInt(b);
        break;
      case -6:
        if (str.equals("qualified")) {
          xInt = INT_QUALIFIED;
          break;
        } 
        if (str.equals("unqualified")) {
          xInt = INT_UNQUALIFIED;
          break;
        } 
        throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { str, "(qualified | unqualified)" });
      case -7:
        if (str.equals("unbounded")) {
          xInt = INT_UNBOUNDED;
          break;
        } 
        try {
          Object object = validate(paramArrayOfObject, paramString1, str, -16, paramXSDocumentInfo);
        } catch (NumberFormatException numberFormatException) {
          throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { str, "(nonNegativeInteger | unbounded)" });
        } 
        break;
      case -8:
        if (str.equals("1")) {
          xInt = fXIntPool.getXInt(1);
          break;
        } 
        throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { str, "(1)" });
      case -9:
        vector = new Vector();
        try {
          StringTokenizer stringTokenizer1 = new StringTokenizer(str, " \n\t\r");
          while (stringTokenizer1.hasMoreTokens()) {
            String str1 = stringTokenizer1.nextToken();
            QName qName = (QName)fExtraDVs[2].validate(str1, paramXSDocumentInfo.fValidationContext, null);
            if (qName.prefix == XMLSymbols.EMPTY_STRING && qName.uri == null && paramXSDocumentInfo.fIsChameleonSchema)
              qName.uri = paramXSDocumentInfo.fTargetNamespace; 
            vector.addElement(qName);
          } 
          Vector vector1 = vector;
        } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
          throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.2", new Object[] { str, "(List of QName)" });
        } 
        break;
      case -10:
        if (str.equals("0")) {
          xInt = fXIntPool.getXInt(0);
          break;
        } 
        if (str.equals("1")) {
          xInt = fXIntPool.getXInt(1);
          break;
        } 
        throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { str, "(0 | 1)" });
      case -11:
        if (str.equals("##any")) {
          xInt = INT_ANY_ANY;
          break;
        } 
        if (str.equals("##other")) {
          xInt = INT_ANY_NOT;
          String[] arrayOfString1 = new String[2];
          arrayOfString1[0] = paramXSDocumentInfo.fTargetNamespace;
          arrayOfString1[1] = null;
          paramArrayOfObject[ATTIDX_NAMESPACE_LIST] = arrayOfString1;
          break;
        } 
        xInt = INT_ANY_LIST;
        this.fNamespaceList.removeAllElements();
        stringTokenizer = new StringTokenizer(str, " \n\t\r");
        try {
          while (stringTokenizer.hasMoreTokens()) {
            String str2;
            String str1 = stringTokenizer.nextToken();
            if (str1.equals("##local")) {
              str2 = null;
            } else if (str1.equals("##targetNamespace")) {
              str2 = paramXSDocumentInfo.fTargetNamespace;
            } else {
              fExtraDVs[0].validate(str1, paramXSDocumentInfo.fValidationContext, null);
              str2 = this.fSymbolTable.addSymbol(str1);
            } 
            if (!this.fNamespaceList.contains(str2))
              this.fNamespaceList.addElement(str2); 
          } 
        } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
          throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { str, "((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )" });
        } 
        i = this.fNamespaceList.size();
        arrayOfString = new String[i];
        this.fNamespaceList.copyInto(arrayOfString);
        paramArrayOfObject[ATTIDX_NAMESPACE_LIST] = arrayOfString;
        break;
      case -12:
        if (str.equals("strict")) {
          xInt = INT_ANY_STRICT;
          break;
        } 
        if (str.equals("lax")) {
          xInt = INT_ANY_LAX;
          break;
        } 
        if (str.equals("skip")) {
          xInt = INT_ANY_SKIP;
          break;
        } 
        throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { str, "(lax | skip | strict)" });
      case -13:
        if (str.equals("optional")) {
          xInt = INT_USE_OPTIONAL;
          break;
        } 
        if (str.equals("required")) {
          xInt = INT_USE_REQUIRED;
          break;
        } 
        if (str.equals("prohibited")) {
          xInt = INT_USE_PROHIBITED;
          break;
        } 
        throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { str, "(optional | prohibited | required)" });
      case -14:
        if (str.equals("preserve")) {
          xInt = INT_WS_PRESERVE;
          break;
        } 
        if (str.equals("replace")) {
          xInt = INT_WS_REPLACE;
          break;
        } 
        if (str.equals("collapse")) {
          xInt = INT_WS_COLLAPSE;
          break;
        } 
        throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { str, "(preserve | replace | collapse)" });
    } 
    return xInt;
  }
  
  void reportSchemaFatalError(String paramString, Object[] paramArrayOfObject, Element paramElement) { this.fSchemaHandler.reportSchemaFatalError(paramString, paramArrayOfObject, paramElement); }
  
  void reportSchemaError(String paramString, Object[] paramArrayOfObject, Element paramElement) { this.fSchemaHandler.reportSchemaError(paramString, paramArrayOfObject, paramElement); }
  
  public void checkNonSchemaAttributes(XSGrammarBucket paramXSGrammarBucket) {
    for (Map.Entry entry : this.fNonSchemaAttrs.entrySet()) {
      String str1 = (String)entry.getKey();
      String str2 = str1.substring(0, str1.indexOf(','));
      String str3 = str1.substring(str1.indexOf(',') + 1);
      SchemaGrammar schemaGrammar = paramXSGrammarBucket.getGrammar(str2);
      if (schemaGrammar == null)
        continue; 
      XSAttributeDecl xSAttributeDecl = schemaGrammar.getGlobalAttributeDecl(str3);
      if (xSAttributeDecl == null)
        continue; 
      XSSimpleType xSSimpleType = (XSSimpleType)xSAttributeDecl.getTypeDefinition();
      if (xSSimpleType == null)
        continue; 
      Vector vector = (Vector)entry.getValue();
      String str4 = (String)vector.elementAt(0);
      int i = vector.size();
      for (byte b = 1; b < i; b += 2) {
        String str = (String)vector.elementAt(b);
        try {
          xSSimpleType.validate((String)vector.elementAt(b + 1), null, null);
        } catch (InvalidDatatypeValueException invalidDatatypeValueException) {
          reportSchemaError("s4s-att-invalid-value", new Object[] { str, str4, invalidDatatypeValueException.getMessage() }, null);
        } 
      } 
    } 
  }
  
  public static String normalize(String paramString, short paramShort) {
    boolean bool = (paramString == null) ? 0 : paramString.length();
    if (!bool || paramShort == 0)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer();
    if (paramShort == 1) {
      for (byte b = 0; b < bool; b++) {
        char c = paramString.charAt(b);
        if (c != '\t' && c != '\n' && c != '\r') {
          stringBuffer.append(c);
        } else {
          stringBuffer.append(' ');
        } 
      } 
    } else {
      boolean bool1 = true;
      for (byte b = 0; b < bool; b++) {
        char c = paramString.charAt(b);
        if (c != '\t' && c != '\n' && c != '\r' && c != ' ') {
          stringBuffer.append(c);
          bool1 = false;
        } else {
          while (b < bool - true) {
            c = paramString.charAt(b + 1);
            if (c != '\t' && c != '\n' && c != '\r' && c != ' ')
              break; 
            b++;
          } 
          if (b < bool - true && !bool1)
            stringBuffer.append(' '); 
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  protected Object[] getAvailableArray() {
    if (this.fArrayPool.length == this.fPoolPos) {
      this.fArrayPool = new Object[this.fPoolPos + 10][];
      for (int i = this.fPoolPos; i < this.fArrayPool.length; i++)
        this.fArrayPool[i] = new Object[ATTIDX_COUNT]; 
    } 
    Object[] arrayOfObject = this.fArrayPool[this.fPoolPos];
    this.fArrayPool[this.fPoolPos++] = null;
    System.arraycopy(fTempArray, 0, arrayOfObject, 0, ATTIDX_COUNT - 1);
    arrayOfObject[ATTIDX_ISRETURNED] = Boolean.FALSE;
    return arrayOfObject;
  }
  
  public void returnAttrArray(Object[] paramArrayOfObject, XSDocumentInfo paramXSDocumentInfo) {
    if (paramXSDocumentInfo != null)
      paramXSDocumentInfo.fNamespaceSupport.popContext(); 
    if (this.fPoolPos == 0 || paramArrayOfObject == null || paramArrayOfObject.length != ATTIDX_COUNT || ((Boolean)paramArrayOfObject[ATTIDX_ISRETURNED]).booleanValue())
      return; 
    paramArrayOfObject[ATTIDX_ISRETURNED] = Boolean.TRUE;
    if (paramArrayOfObject[ATTIDX_NONSCHEMA] != null)
      ((Vector)paramArrayOfObject[ATTIDX_NONSCHEMA]).clear(); 
    this.fArrayPool[--this.fPoolPos] = paramArrayOfObject;
  }
  
  public void resolveNamespace(Element paramElement, Attr[] paramArrayOfAttr, SchemaNamespaceSupport paramSchemaNamespaceSupport) {
    paramSchemaNamespaceSupport.pushContext();
    int i = paramArrayOfAttr.length;
    Attr attr = null;
    for (byte b = 0; b < i; b++) {
      attr = paramArrayOfAttr[b];
      String str1 = DOMUtil.getName(attr);
      String str2 = null;
      if (str1.equals(XMLSymbols.PREFIX_XMLNS)) {
        str2 = XMLSymbols.EMPTY_STRING;
      } else if (str1.startsWith("xmlns:")) {
        str2 = this.fSymbolTable.addSymbol(DOMUtil.getLocalName(attr));
      } 
      if (str2 != null) {
        String str = this.fSymbolTable.addSymbol(DOMUtil.getValue(attr));
        paramSchemaNamespaceSupport.declarePrefix(str2, (str.length() != 0) ? str : null);
      } 
    } 
  }
  
  static  {
    SchemaGrammar.BuiltinSchemaGrammar builtinSchemaGrammar = SchemaGrammar.SG_SchemaNS;
    fExtraDVs[0] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("anyURI");
    fExtraDVs[1] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("ID");
    fExtraDVs[2] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("QName");
    fExtraDVs[3] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("string");
    fExtraDVs[4] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("token");
    fExtraDVs[5] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("NCName");
    fExtraDVs[6] = fExtraDVs[3];
    fExtraDVs[6] = fExtraDVs[3];
    fExtraDVs[8] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("language");
    byte b1 = 0;
    byte b2 = b1++;
    byte b3 = b1++;
    byte b4 = b1++;
    byte b5 = b1++;
    byte b6 = b1++;
    byte b7 = b1++;
    byte b8 = b1++;
    byte b9 = b1++;
    byte b10 = b1++;
    byte b11 = b1++;
    byte b12 = b1++;
    byte b13 = b1++;
    byte b14 = b1++;
    byte b15 = b1++;
    byte b16 = b1++;
    byte b17 = b1++;
    byte b18 = b1++;
    byte b19 = b1++;
    byte b20 = b1++;
    byte b21 = b1++;
    byte b22 = b1++;
    byte b23 = b1++;
    byte b24 = b1++;
    byte b25 = b1++;
    byte b26 = b1++;
    byte b27 = b1++;
    byte b28 = b1++;
    byte b29 = b1++;
    byte b30 = b1++;
    byte b31 = b1++;
    byte b32 = b1++;
    byte b33 = b1++;
    byte b34 = b1++;
    byte b35 = b1++;
    byte b36 = b1++;
    byte b37 = b1++;
    byte b38 = b1++;
    byte b39 = b1++;
    byte b40 = b1++;
    byte b41 = b1++;
    byte b42 = b1++;
    byte b43 = b1++;
    byte b44 = b1++;
    byte b45 = b1++;
    byte b46 = b1++;
    byte b47 = b1++;
    byte b48 = b1++;
    byte b49 = b1++;
    OneAttr[] arrayOfOneAttr = new OneAttr[b1];
    arrayOfOneAttr[b2] = new OneAttr(SchemaSymbols.ATT_ABSTRACT, -15, ATTIDX_ABSTRACT, Boolean.FALSE);
    arrayOfOneAttr[b3] = new OneAttr(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, -6, ATTIDX_AFORMDEFAULT, INT_UNQUALIFIED);
    arrayOfOneAttr[b4] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, null);
    arrayOfOneAttr[b5] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, null);
    arrayOfOneAttr[b6] = new OneAttr(SchemaSymbols.ATT_BLOCK, -1, ATTIDX_BLOCK, null);
    arrayOfOneAttr[b7] = new OneAttr(SchemaSymbols.ATT_BLOCK, -2, ATTIDX_BLOCK, null);
    arrayOfOneAttr[b8] = new OneAttr(SchemaSymbols.ATT_BLOCKDEFAULT, -1, ATTIDX_BLOCKDEFAULT, INT_EMPTY_SET);
    arrayOfOneAttr[b9] = new OneAttr(SchemaSymbols.ATT_DEFAULT, 3, ATTIDX_DEFAULT, null);
    arrayOfOneAttr[b10] = new OneAttr(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, -6, ATTIDX_EFORMDEFAULT, INT_UNQUALIFIED);
    arrayOfOneAttr[b11] = new OneAttr(SchemaSymbols.ATT_FINAL, -3, ATTIDX_FINAL, null);
    arrayOfOneAttr[b12] = new OneAttr(SchemaSymbols.ATT_FINAL, -4, ATTIDX_FINAL, null);
    arrayOfOneAttr[b13] = new OneAttr(SchemaSymbols.ATT_FINALDEFAULT, -5, ATTIDX_FINALDEFAULT, INT_EMPTY_SET);
    arrayOfOneAttr[b14] = new OneAttr(SchemaSymbols.ATT_FIXED, 3, ATTIDX_FIXED, null);
    arrayOfOneAttr[b15] = new OneAttr(SchemaSymbols.ATT_FIXED, -15, ATTIDX_FIXED, Boolean.FALSE);
    arrayOfOneAttr[b16] = new OneAttr(SchemaSymbols.ATT_FORM, -6, ATTIDX_FORM, null);
    arrayOfOneAttr[b17] = new OneAttr(SchemaSymbols.ATT_ID, 1, ATTIDX_ID, null);
    arrayOfOneAttr[b18] = new OneAttr(SchemaSymbols.ATT_ITEMTYPE, 2, ATTIDX_ITEMTYPE, null);
    arrayOfOneAttr[b19] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -7, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
    arrayOfOneAttr[b20] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -8, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
    arrayOfOneAttr[b21] = new OneAttr(SchemaSymbols.ATT_MEMBERTYPES, -9, ATTIDX_MEMBERTYPES, null);
    arrayOfOneAttr[b22] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -16, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
    arrayOfOneAttr[b23] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -10, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
    arrayOfOneAttr[b24] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, Boolean.FALSE);
    arrayOfOneAttr[b25] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, null);
    arrayOfOneAttr[b26] = new OneAttr(SchemaSymbols.ATT_NAME, 5, ATTIDX_NAME, null);
    arrayOfOneAttr[b27] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, -11, ATTIDX_NAMESPACE, INT_ANY_ANY);
    arrayOfOneAttr[b28] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, 0, ATTIDX_NAMESPACE, null);
    arrayOfOneAttr[b29] = new OneAttr(SchemaSymbols.ATT_NILLABLE, -15, ATTIDX_NILLABLE, Boolean.FALSE);
    arrayOfOneAttr[b30] = new OneAttr(SchemaSymbols.ATT_PROCESSCONTENTS, -12, ATTIDX_PROCESSCONTENTS, INT_ANY_STRICT);
    arrayOfOneAttr[b31] = new OneAttr(SchemaSymbols.ATT_PUBLIC, 4, ATTIDX_PUBLIC, null);
    arrayOfOneAttr[b32] = new OneAttr(SchemaSymbols.ATT_REF, 2, ATTIDX_REF, null);
    arrayOfOneAttr[b33] = new OneAttr(SchemaSymbols.ATT_REFER, 2, ATTIDX_REFER, null);
    arrayOfOneAttr[b34] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, null);
    arrayOfOneAttr[b35] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, null);
    arrayOfOneAttr[b36] = new OneAttr(SchemaSymbols.ATT_SOURCE, 0, ATTIDX_SOURCE, null);
    arrayOfOneAttr[b37] = new OneAttr(SchemaSymbols.ATT_SUBSTITUTIONGROUP, 2, ATTIDX_SUBSGROUP, null);
    arrayOfOneAttr[b38] = new OneAttr(SchemaSymbols.ATT_SYSTEM, 0, ATTIDX_SYSTEM, null);
    arrayOfOneAttr[b39] = new OneAttr(SchemaSymbols.ATT_TARGETNAMESPACE, 0, ATTIDX_TARGETNAMESPACE, null);
    arrayOfOneAttr[b40] = new OneAttr(SchemaSymbols.ATT_TYPE, 2, ATTIDX_TYPE, null);
    arrayOfOneAttr[b41] = new OneAttr(SchemaSymbols.ATT_USE, -13, ATTIDX_USE, INT_USE_OPTIONAL);
    arrayOfOneAttr[b42] = new OneAttr(SchemaSymbols.ATT_VALUE, -16, ATTIDX_VALUE, null);
    arrayOfOneAttr[b43] = new OneAttr(SchemaSymbols.ATT_VALUE, -17, ATTIDX_VALUE, null);
    arrayOfOneAttr[b44] = new OneAttr(SchemaSymbols.ATT_VALUE, 3, ATTIDX_VALUE, null);
    arrayOfOneAttr[b45] = new OneAttr(SchemaSymbols.ATT_VALUE, -14, ATTIDX_VALUE, null);
    arrayOfOneAttr[b46] = new OneAttr(SchemaSymbols.ATT_VERSION, 4, ATTIDX_VERSION, null);
    arrayOfOneAttr[b47] = new OneAttr(SchemaSymbols.ATT_XML_LANG, 8, ATTIDX_XML_LANG, null);
    arrayOfOneAttr[b48] = new OneAttr(SchemaSymbols.ATT_XPATH, 6, ATTIDX_XPATH, null);
    arrayOfOneAttr[b49] = new OneAttr(SchemaSymbols.ATT_XPATH, 7, ATTIDX_XPATH, null);
    Container container = Container.getContainer(5);
    container.put(SchemaSymbols.ATT_DEFAULT, arrayOfOneAttr[b9]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b14]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    container.put(SchemaSymbols.ATT_TYPE, arrayOfOneAttr[b40]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTE, container);
    container = Container.getContainer(7);
    container.put(SchemaSymbols.ATT_DEFAULT, arrayOfOneAttr[b9]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b14]);
    container.put(SchemaSymbols.ATT_FORM, arrayOfOneAttr[b16]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    container.put(SchemaSymbols.ATT_TYPE, arrayOfOneAttr[b40]);
    container.put(SchemaSymbols.ATT_USE, arrayOfOneAttr[b41]);
    fEleAttrsMapL.put("attribute_n", container);
    container = Container.getContainer(5);
    container.put(SchemaSymbols.ATT_DEFAULT, arrayOfOneAttr[b9]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b14]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_REF, arrayOfOneAttr[b32]);
    container.put(SchemaSymbols.ATT_USE, arrayOfOneAttr[b41]);
    fEleAttrsMapL.put("attribute_r", container);
    container = Container.getContainer(10);
    container.put(SchemaSymbols.ATT_ABSTRACT, arrayOfOneAttr[b2]);
    container.put(SchemaSymbols.ATT_BLOCK, arrayOfOneAttr[b6]);
    container.put(SchemaSymbols.ATT_DEFAULT, arrayOfOneAttr[b9]);
    container.put(SchemaSymbols.ATT_FINAL, arrayOfOneAttr[b11]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b14]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    container.put(SchemaSymbols.ATT_NILLABLE, arrayOfOneAttr[b29]);
    container.put(SchemaSymbols.ATT_SUBSTITUTIONGROUP, arrayOfOneAttr[b37]);
    container.put(SchemaSymbols.ATT_TYPE, arrayOfOneAttr[b40]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_ELEMENT, container);
    container = Container.getContainer(10);
    container.put(SchemaSymbols.ATT_BLOCK, arrayOfOneAttr[b6]);
    container.put(SchemaSymbols.ATT_DEFAULT, arrayOfOneAttr[b9]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b14]);
    container.put(SchemaSymbols.ATT_FORM, arrayOfOneAttr[b16]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MAXOCCURS, arrayOfOneAttr[b19]);
    container.put(SchemaSymbols.ATT_MINOCCURS, arrayOfOneAttr[b22]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    container.put(SchemaSymbols.ATT_NILLABLE, arrayOfOneAttr[b29]);
    container.put(SchemaSymbols.ATT_TYPE, arrayOfOneAttr[b40]);
    fEleAttrsMapL.put("element_n", container);
    container = Container.getContainer(4);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MAXOCCURS, arrayOfOneAttr[b19]);
    container.put(SchemaSymbols.ATT_MINOCCURS, arrayOfOneAttr[b22]);
    container.put(SchemaSymbols.ATT_REF, arrayOfOneAttr[b32]);
    fEleAttrsMapL.put("element_r", container);
    container = Container.getContainer(6);
    container.put(SchemaSymbols.ATT_ABSTRACT, arrayOfOneAttr[b2]);
    container.put(SchemaSymbols.ATT_BLOCK, arrayOfOneAttr[b7]);
    container.put(SchemaSymbols.ATT_FINAL, arrayOfOneAttr[b11]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MIXED, arrayOfOneAttr[b24]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_COMPLEXTYPE, container);
    container = Container.getContainer(4);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    container.put(SchemaSymbols.ATT_PUBLIC, arrayOfOneAttr[b31]);
    container.put(SchemaSymbols.ATT_SYSTEM, arrayOfOneAttr[b38]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_NOTATION, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MIXED, arrayOfOneAttr[b24]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXTYPE, container);
    container = Container.getContainer(1);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLECONTENT, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_BASE, arrayOfOneAttr[b5]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_RESTRICTION, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_BASE, arrayOfOneAttr[b4]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_EXTENSION, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_REF, arrayOfOneAttr[b32]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAMESPACE, arrayOfOneAttr[b27]);
    container.put(SchemaSymbols.ATT_PROCESSCONTENTS, arrayOfOneAttr[b30]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_ANYATTRIBUTE, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MIXED, arrayOfOneAttr[b25]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXCONTENT, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_GROUP, container);
    container = Container.getContainer(4);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MAXOCCURS, arrayOfOneAttr[b19]);
    container.put(SchemaSymbols.ATT_MINOCCURS, arrayOfOneAttr[b22]);
    container.put(SchemaSymbols.ATT_REF, arrayOfOneAttr[b32]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_GROUP, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MAXOCCURS, arrayOfOneAttr[b20]);
    container.put(SchemaSymbols.ATT_MINOCCURS, arrayOfOneAttr[b23]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_ALL, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MAXOCCURS, arrayOfOneAttr[b19]);
    container.put(SchemaSymbols.ATT_MINOCCURS, arrayOfOneAttr[b22]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_CHOICE, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_SEQUENCE, container);
    container = Container.getContainer(5);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MAXOCCURS, arrayOfOneAttr[b19]);
    container.put(SchemaSymbols.ATT_MINOCCURS, arrayOfOneAttr[b22]);
    container.put(SchemaSymbols.ATT_NAMESPACE, arrayOfOneAttr[b27]);
    container.put(SchemaSymbols.ATT_PROCESSCONTENTS, arrayOfOneAttr[b30]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_ANY, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_UNIQUE, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_KEY, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    container.put(SchemaSymbols.ATT_REFER, arrayOfOneAttr[b33]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_KEYREF, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_XPATH, arrayOfOneAttr[b48]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_SELECTOR, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_XPATH, arrayOfOneAttr[b49]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_FIELD, container);
    container = Container.getContainer(1);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_ANNOTATION, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_ANNOTATION, container);
    container = Container.getContainer(1);
    container.put(SchemaSymbols.ATT_SOURCE, arrayOfOneAttr[b36]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_APPINFO, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_APPINFO, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_SOURCE, arrayOfOneAttr[b36]);
    container.put(SchemaSymbols.ATT_XML_LANG, arrayOfOneAttr[b47]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_DOCUMENTATION, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_DOCUMENTATION, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_FINAL, arrayOfOneAttr[b12]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAME, arrayOfOneAttr[b26]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_SIMPLETYPE, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_FINAL, arrayOfOneAttr[b12]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLETYPE, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_ITEMTYPE, arrayOfOneAttr[b18]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_LIST, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_MEMBERTYPES, arrayOfOneAttr[b21]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_UNION, container);
    container = Container.getContainer(8);
    container.put(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, arrayOfOneAttr[b3]);
    container.put(SchemaSymbols.ATT_BLOCKDEFAULT, arrayOfOneAttr[b8]);
    container.put(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, arrayOfOneAttr[b10]);
    container.put(SchemaSymbols.ATT_FINALDEFAULT, arrayOfOneAttr[b13]);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_TARGETNAMESPACE, arrayOfOneAttr[b39]);
    container.put(SchemaSymbols.ATT_VERSION, arrayOfOneAttr[b46]);
    container.put(SchemaSymbols.ATT_XML_LANG, arrayOfOneAttr[b47]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_SCHEMA, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_SCHEMALOCATION, arrayOfOneAttr[b34]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_INCLUDE, container);
    fEleAttrsMapG.put(SchemaSymbols.ELT_REDEFINE, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_NAMESPACE, arrayOfOneAttr[b28]);
    container.put(SchemaSymbols.ATT_SCHEMALOCATION, arrayOfOneAttr[b35]);
    fEleAttrsMapG.put(SchemaSymbols.ELT_IMPORT, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_VALUE, arrayOfOneAttr[b42]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b15]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_LENGTH, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_MINLENGTH, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_MAXLENGTH, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_FRACTIONDIGITS, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_VALUE, arrayOfOneAttr[b43]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b15]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_TOTALDIGITS, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_VALUE, arrayOfOneAttr[b44]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_PATTERN, container);
    container = Container.getContainer(2);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_VALUE, arrayOfOneAttr[b44]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_ENUMERATION, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_VALUE, arrayOfOneAttr[b45]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b15]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_WHITESPACE, container);
    container = Container.getContainer(3);
    container.put(SchemaSymbols.ATT_ID, arrayOfOneAttr[b17]);
    container.put(SchemaSymbols.ATT_VALUE, arrayOfOneAttr[b44]);
    container.put(SchemaSymbols.ATT_FIXED, arrayOfOneAttr[b15]);
    fEleAttrsMapL.put(SchemaSymbols.ELT_MAXINCLUSIVE, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_MAXEXCLUSIVE, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_MININCLUSIVE, container);
    fEleAttrsMapL.put(SchemaSymbols.ELT_MINEXCLUSIVE, container);
    fSeenTemp = new boolean[ATTIDX_COUNT];
    fTempArray = new Object[ATTIDX_COUNT];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSAttributeChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */