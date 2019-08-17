package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.orbutil.ObjectUtility;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class IDLNameTranslatorImpl implements IDLNameTranslator {
  private static String[] IDL_KEYWORDS = { 
      "abstract", "any", "attribute", "boolean", "case", "char", "const", "context", "custom", "default", 
      "double", "enum", "exception", "factory", "FALSE", "fixed", "float", "in", "inout", "interface", 
      "long", "module", "native", "Object", "octet", "oneway", "out", "private", "public", "raises", 
      "readonly", "sequence", "short", "string", "struct", "supports", "switch", "TRUE", "truncatable", "typedef", 
      "unsigned", "union", "ValueBase", "valuetype", "void", "wchar", "wstring" };
  
  private static char[] HEX_DIGITS = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  private static final String UNDERSCORE = "_";
  
  private static final String INNER_CLASS_SEPARATOR = "__";
  
  private static final String[] BASE_IDL_ARRAY_MODULE_TYPE = { "org", "omg", "boxedRMI" };
  
  private static final String BASE_IDL_ARRAY_ELEMENT_TYPE = "seq";
  
  private static final String LEADING_UNDERSCORE_CHAR = "J";
  
  private static final String ID_CONTAINER_CLASH_CHAR = "_";
  
  private static final String OVERLOADED_TYPE_SEPARATOR = "__";
  
  private static final String ATTRIBUTE_METHOD_CLASH_MANGLE_CHARS = "__";
  
  private static final String GET_ATTRIBUTE_PREFIX = "_get_";
  
  private static final String SET_ATTRIBUTE_PREFIX = "_set_";
  
  private static final String IS_ATTRIBUTE_PREFIX = "_get_";
  
  private static Set idlKeywords_ = new HashSet();
  
  private Class[] interf_;
  
  private Map methodToIDLNameMap_;
  
  private Map IDLNameToMethodMap_;
  
  private Method[] methods_;
  
  public static IDLNameTranslator get(Class paramClass) { return new IDLNameTranslatorImpl(new Class[] { paramClass }); }
  
  public static IDLNameTranslator get(Class[] paramArrayOfClass) { return new IDLNameTranslatorImpl(paramArrayOfClass); }
  
  public static String getExceptionId(Class paramClass) {
    IDLType iDLType = classToIDLType(paramClass);
    return iDLType.getExceptionName();
  }
  
  public Class[] getInterfaces() { return this.interf_; }
  
  public Method[] getMethods() { return this.methods_; }
  
  public Method getMethod(String paramString) { return (Method)this.IDLNameToMethodMap_.get(paramString); }
  
  public String getIDLName(Method paramMethod) { return (String)this.methodToIDLNameMap_.get(paramMethod); }
  
  private IDLNameTranslatorImpl(Class[] paramArrayOfClass) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new DynamicAccessPermission("access")); 
    try {
      IDLTypesUtil iDLTypesUtil = new IDLTypesUtil();
      for (byte b = 0; b < paramArrayOfClass.length; b++)
        iDLTypesUtil.validateRemoteInterface(paramArrayOfClass[b]); 
      this.interf_ = paramArrayOfClass;
      buildNameTranslation();
    } catch (IDLTypeException iDLTypeException) {
      String str = iDLTypeException.getMessage();
      IllegalStateException illegalStateException = new IllegalStateException(str);
      illegalStateException.initCause(iDLTypeException);
      throw illegalStateException;
    } 
  }
  
  private void buildNameTranslation() {
    HashMap hashMap = new HashMap();
    byte b;
    for (b = 0; b < this.interf_.length; b++) {
      Class clazz = this.interf_[b];
      IDLTypesUtil iDLTypesUtil = new IDLTypesUtil();
      final Method[] methods = clazz.getMethods();
      AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              Method.setAccessible(methods, true);
              return null;
            }
          });
      for (byte b1 = 0; b1 < arrayOfMethod.length; b1++) {
        Method method = arrayOfMethod[b1];
        IDLMethodInfo iDLMethodInfo = new IDLMethodInfo(null);
        iDLMethodInfo.method = method;
        if (iDLTypesUtil.isPropertyAccessorMethod(method, clazz)) {
          iDLMethodInfo.isProperty = true;
          String str = iDLTypesUtil.getAttributeNameForProperty(method.getName());
          iDLMethodInfo.originalName = str;
          iDLMethodInfo.mangledName = str;
        } else {
          iDLMethodInfo.isProperty = false;
          iDLMethodInfo.originalName = method.getName();
          iDLMethodInfo.mangledName = method.getName();
        } 
        hashMap.put(method, iDLMethodInfo);
      } 
    } 
    for (IDLMethodInfo iDLMethodInfo : hashMap.values()) {
      for (IDLMethodInfo iDLMethodInfo1 : hashMap.values()) {
        if (iDLMethodInfo != iDLMethodInfo1 && !iDLMethodInfo.originalName.equals(iDLMethodInfo1.originalName) && iDLMethodInfo.originalName.equalsIgnoreCase(iDLMethodInfo1.originalName))
          iDLMethodInfo.mangledName = mangleCaseSensitiveCollision(iDLMethodInfo.originalName); 
      } 
    } 
    for (IDLMethodInfo iDLMethodInfo : hashMap.values())
      iDLMethodInfo.mangledName = mangleIdentifier(iDLMethodInfo.mangledName, iDLMethodInfo.isProperty); 
    for (IDLMethodInfo iDLMethodInfo : hashMap.values()) {
      if (iDLMethodInfo.isProperty)
        continue; 
      for (IDLMethodInfo iDLMethodInfo1 : hashMap.values()) {
        if (iDLMethodInfo != iDLMethodInfo1 && !iDLMethodInfo1.isProperty && iDLMethodInfo.originalName.equals(iDLMethodInfo1.originalName))
          iDLMethodInfo.mangledName = mangleOverloadedMethod(iDLMethodInfo.mangledName, iDLMethodInfo.method); 
      } 
    } 
    for (IDLMethodInfo iDLMethodInfo : hashMap.values()) {
      if (!iDLMethodInfo.isProperty)
        continue; 
      for (IDLMethodInfo iDLMethodInfo1 : hashMap.values()) {
        if (iDLMethodInfo != iDLMethodInfo1 && !iDLMethodInfo1.isProperty && iDLMethodInfo.mangledName.equals(iDLMethodInfo1.mangledName))
          iDLMethodInfo.mangledName += "__"; 
      } 
    } 
    for (b = 0; b < this.interf_.length; b++) {
      Class clazz = this.interf_[b];
      String str = getMappedContainerName(clazz);
      for (IDLMethodInfo iDLMethodInfo : hashMap.values()) {
        if (!iDLMethodInfo.isProperty && identifierClashesWithContainer(str, iDLMethodInfo.mangledName))
          iDLMethodInfo.mangledName = mangleContainerClash(iDLMethodInfo.mangledName); 
      } 
    } 
    this.methodToIDLNameMap_ = new HashMap();
    this.IDLNameToMethodMap_ = new HashMap();
    this.methods_ = (Method[])hashMap.keySet().toArray(new Method[0]);
    for (IDLMethodInfo iDLMethodInfo : hashMap.values()) {
      String str = iDLMethodInfo.mangledName;
      if (iDLMethodInfo.isProperty) {
        String str1 = iDLMethodInfo.method.getName();
        String str2 = "";
        if (str1.startsWith("get")) {
          str2 = "_get_";
        } else if (str1.startsWith("set")) {
          str2 = "_set_";
        } else {
          str2 = "_get_";
        } 
        str = str2 + iDLMethodInfo.mangledName;
      } 
      this.methodToIDLNameMap_.put(iDLMethodInfo.method, str);
      if (this.IDLNameToMethodMap_.containsKey(str)) {
        Method method = (Method)this.IDLNameToMethodMap_.get(str);
        throw new IllegalStateException("Error : methods " + method + " and " + iDLMethodInfo.method + " both result in IDL name '" + str + "'");
      } 
      this.IDLNameToMethodMap_.put(str, iDLMethodInfo.method);
    } 
  }
  
  private static String mangleIdentifier(String paramString) { return mangleIdentifier(paramString, false); }
  
  private static String mangleIdentifier(String paramString, boolean paramBoolean) {
    String str = paramString;
    if (hasLeadingUnderscore(str))
      str = mangleLeadingUnderscore(str); 
    if (!paramBoolean && isIDLKeyword(str))
      str = mangleIDLKeywordClash(str); 
    if (!isIDLIdentifier(str))
      str = mangleUnicodeChars(str); 
    return str;
  }
  
  static boolean isIDLKeyword(String paramString) {
    String str = paramString.toUpperCase();
    return idlKeywords_.contains(str);
  }
  
  static String mangleIDLKeywordClash(String paramString) { return "_" + paramString; }
  
  private static String mangleLeadingUnderscore(String paramString) { return "J" + paramString; }
  
  private static boolean hasLeadingUnderscore(String paramString) { return paramString.startsWith("_"); }
  
  static String mangleUnicodeChars(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (isIDLIdentifierChar(c)) {
        stringBuffer.append(c);
      } else {
        String str = charToUnicodeRepresentation(c);
        stringBuffer.append(str);
      } 
    } 
    return stringBuffer.toString();
  }
  
  String mangleCaseSensitiveCollision(String paramString) {
    StringBuffer stringBuffer = new StringBuffer(paramString);
    stringBuffer.append("_");
    boolean bool = false;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (Character.isUpperCase(c)) {
        if (bool)
          stringBuffer.append("_"); 
        stringBuffer.append(b);
        bool = true;
      } 
    } 
    return stringBuffer.toString();
  }
  
  private static String mangleContainerClash(String paramString) { return paramString + "_"; }
  
  private static boolean identifierClashesWithContainer(String paramString1, String paramString2) { return paramString2.equalsIgnoreCase(paramString1); }
  
  public static String charToUnicodeRepresentation(char paramChar) {
    char c1 = paramChar;
    StringBuffer stringBuffer = new StringBuffer();
    for (char c2 = c1; c2 > '\000'; c2 = c3) {
      char c3 = c2 / '\020';
      char c4 = c2 % '\020';
      stringBuffer.insert(0, HEX_DIGITS[c4]);
    } 
    int i = 4 - stringBuffer.length();
    for (byte b = 0; b < i; b++)
      stringBuffer.insert(0, "0"); 
    stringBuffer.insert(0, "U");
    return stringBuffer.toString();
  }
  
  private static boolean isIDLIdentifier(String paramString) {
    boolean bool = true;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      bool = (b == 0) ? isIDLAlphabeticChar(c) : isIDLIdentifierChar(c);
      if (!bool)
        break; 
    } 
    return bool;
  }
  
  private static boolean isIDLIdentifierChar(char paramChar) { return (isIDLAlphabeticChar(paramChar) || isIDLDecimalDigit(paramChar) || isUnderscore(paramChar)); }
  
  private static boolean isIDLAlphabeticChar(char paramChar) { return ((paramChar >= 'A' && paramChar <= 'Z') || (paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'À' && paramChar <= 'ÿ' && paramChar != '×' && paramChar != '÷')); }
  
  private static boolean isIDLDecimalDigit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  private static boolean isUnderscore(char paramChar) { return (paramChar == '_'); }
  
  private static String mangleOverloadedMethod(String paramString, Method paramMethod) {
    IDLTypesUtil iDLTypesUtil = new IDLTypesUtil();
    String str = paramString + "__";
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      if (b)
        str = str + "__"; 
      IDLType iDLType = classToIDLType(clazz);
      String str1 = iDLType.getModuleName();
      String str2 = iDLType.getMemberName();
      String str3 = (str1.length() > 0) ? (str1 + "_" + str2) : str2;
      if (!iDLTypesUtil.isPrimitive(clazz) && iDLTypesUtil.getSpecialCaseIDLTypeMapping(clazz) == null && isIDLKeyword(str3))
        str3 = mangleIDLKeywordClash(str3); 
      str3 = mangleUnicodeChars(str3);
      str = str + str3;
    } 
    return str;
  }
  
  private static IDLType classToIDLType(Class paramClass) {
    IDLType iDLType = null;
    IDLTypesUtil iDLTypesUtil = new IDLTypesUtil();
    if (iDLTypesUtil.isPrimitive(paramClass)) {
      iDLType = iDLTypesUtil.getPrimitiveIDLTypeMapping(paramClass);
    } else if (paramClass.isArray()) {
      Class clazz = paramClass.getComponentType();
      byte b;
      for (b = 1; clazz.isArray(); b++)
        clazz = clazz.getComponentType(); 
      IDLType iDLType1 = classToIDLType(clazz);
      String[] arrayOfString = BASE_IDL_ARRAY_MODULE_TYPE;
      if (iDLType1.hasModule())
        arrayOfString = (String[])ObjectUtility.concatenateArrays(arrayOfString, iDLType1.getModules()); 
      String str = "seq" + b + "_" + iDLType1.getMemberName();
      iDLType = new IDLType(paramClass, arrayOfString, str);
    } else {
      iDLType = iDLTypesUtil.getSpecialCaseIDLTypeMapping(paramClass);
      if (iDLType == null) {
        String str1 = getUnmappedContainerName(paramClass);
        str1 = str1.replaceAll("\\$", "__");
        if (hasLeadingUnderscore(str1))
          str1 = mangleLeadingUnderscore(str1); 
        String str2 = getPackageName(paramClass);
        if (str2 == null) {
          iDLType = new IDLType(paramClass, str1);
        } else {
          if (iDLTypesUtil.isEntity(paramClass))
            str2 = "org.omg.boxedIDL." + str2; 
          StringTokenizer stringTokenizer = new StringTokenizer(str2, ".");
          String[] arrayOfString = new String[stringTokenizer.countTokens()];
          byte b = 0;
          while (stringTokenizer.hasMoreElements()) {
            String str3 = stringTokenizer.nextToken();
            String str4 = hasLeadingUnderscore(str3) ? mangleLeadingUnderscore(str3) : str3;
            arrayOfString[b++] = str4;
          } 
          iDLType = new IDLType(paramClass, arrayOfString, str1);
        } 
      } 
    } 
    return iDLType;
  }
  
  private static String getPackageName(Class paramClass) {
    Package package = paramClass.getPackage();
    String str = null;
    if (package != null) {
      str = package.getName();
    } else {
      String str1 = paramClass.getName();
      int i = str1.indexOf('.');
      str = (i == -1) ? null : str1.substring(0, i);
    } 
    return str;
  }
  
  private static String getMappedContainerName(Class paramClass) {
    String str = getUnmappedContainerName(paramClass);
    return mangleIdentifier(str);
  }
  
  private static String getUnmappedContainerName(Class paramClass) {
    String str1 = null;
    String str2 = getPackageName(paramClass);
    String str3 = paramClass.getName();
    if (str2 != null) {
      int i = str2.length();
      str1 = str3.substring(i + 1);
    } else {
      str1 = str3;
    } 
    return str1;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("IDLNameTranslator[");
    for (byte b = 0; b < this.interf_.length; b++) {
      if (b)
        stringBuffer.append(" "); 
      stringBuffer.append(this.interf_[b].getName());
    } 
    stringBuffer.append("]\n");
    for (Method method : this.methodToIDLNameMap_.keySet()) {
      String str = (String)this.methodToIDLNameMap_.get(method);
      stringBuffer.append(str + ":" + method + "\n");
    } 
    return stringBuffer.toString();
  }
  
  static  {
    for (byte b = 0; b < IDL_KEYWORDS.length; b++) {
      String str1 = IDL_KEYWORDS[b];
      String str2 = str1.toUpperCase();
      idlKeywords_.add(str2);
    } 
  }
  
  private static class IDLMethodInfo {
    public Method method;
    
    public boolean isProperty;
    
    public String originalName;
    
    public String mangledName;
    
    private IDLMethodInfo() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\IDLNameTranslatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */