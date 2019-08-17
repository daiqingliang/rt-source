package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.MARSHAL;

public class RepositoryId {
  private static final byte[] IDL_IDENTIFIER_CHARS = { 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 
      0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 
      1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 
      1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 
      1, 1, 1, 0, 0, 1 };
  
  private static final long serialVersionUID = 123456789L;
  
  private static String defaultServerURL = null;
  
  private static boolean useCodebaseOnly = false;
  
  private static IdentityHashtable classToRepStr;
  
  private static IdentityHashtable classIDLToRepStr;
  
  private static IdentityHashtable classSeqToRepStr;
  
  private static final IdentityHashtable repStrToByteArray;
  
  private static Hashtable repStrToClass;
  
  private String repId = null;
  
  private boolean isSupportedFormat = true;
  
  private String typeString = null;
  
  private String versionString = null;
  
  private boolean isSequence = false;
  
  private boolean isRMIValueType = false;
  
  private boolean isIDLType = false;
  
  private String completeClassName = null;
  
  private String unqualifiedName = null;
  
  private String definedInId = null;
  
  private Class clazz = null;
  
  private String suid = null;
  
  private String actualSuid = null;
  
  private long suidLong = -1L;
  
  private long actualSuidLong = -1L;
  
  private static final String kSequenceKeyword = "seq";
  
  private static final String kValuePrefix = "RMI:";
  
  private static final String kIDLPrefix = "IDL:";
  
  private static final String kIDLNamePrefix = "omg.org/";
  
  private static final String kIDLClassnamePrefix = "org.omg.";
  
  private static final String kSequencePrefix = "[";
  
  private static final String kCORBAPrefix = "CORBA/";
  
  private static final String kArrayPrefix = "RMI:[CORBA/";
  
  private static final int kValuePrefixLength;
  
  private static final int kIDLPrefixLength;
  
  private static final int kSequencePrefixLength;
  
  private static final String kInterfaceHashCode = ":0000000000000000";
  
  private static final String kInterfaceOnlyHashStr = "0000000000000000";
  
  private static final String kExternalizableHashStr = "0000000000000001";
  
  public static final int kInitialValueTag = 2147483392;
  
  public static final int kNoTypeInfo = 0;
  
  public static final int kSingleRepTypeInfo = 2;
  
  public static final int kPartialListTypeInfo = 6;
  
  public static final int kChunkedMask = 8;
  
  public static final int kPreComputed_StandardRMIUnchunked;
  
  public static final int kPreComputed_CodeBaseRMIUnchunked;
  
  public static final int kPreComputed_StandardRMIChunked;
  
  public static final int kPreComputed_CodeBaseRMIChunked;
  
  public static final int kPreComputed_StandardRMIUnchunked_NoRep;
  
  public static final int kPreComputed_CodeBaseRMIUnchunked_NoRep;
  
  public static final int kPreComputed_StandardRMIChunked_NoRep;
  
  public static final int kPreComputed_CodeBaseRMIChunked_NoRep;
  
  public static final String kWStringValueVersion = "1.0";
  
  public static final String kWStringValueHash = ":1.0";
  
  public static final String kWStringStubValue = "WStringValue";
  
  public static final String kWStringTypeStr = "omg.org/CORBA/WStringValue";
  
  public static final String kWStringValueRepID = "IDL:omg.org/CORBA/WStringValue:1.0";
  
  public static final String kAnyRepID = "IDL:omg.org/CORBA/Any";
  
  public static final String kClassDescValueHash;
  
  public static final String kClassDescStubValue = "ClassDesc";
  
  public static final String kClassDescTypeStr = "javax.rmi.CORBA.ClassDesc";
  
  public static final String kClassDescValueRepID;
  
  public static final String kObjectValueHash = ":1.0";
  
  public static final String kObjectStubValue = "Object";
  
  public static final String kSequenceValueHash = ":1.0";
  
  public static final String kPrimitiveSequenceValueHash = ":0000000000000000";
  
  public static final String kSerializableValueHash = ":1.0";
  
  public static final String kSerializableStubValue = "Serializable";
  
  public static final String kExternalizableValueHash = ":1.0";
  
  public static final String kExternalizableStubValue = "Externalizable";
  
  public static final String kRemoteValueHash = "";
  
  public static final String kRemoteStubValue = "";
  
  public static final String kRemoteTypeStr = "";
  
  public static final String kRemoteValueRepID = "";
  
  private static final Hashtable kSpecialArrayTypeStrings;
  
  private static final Hashtable kSpecialCasesRepIDs;
  
  private static final Hashtable kSpecialCasesStubValues;
  
  private static final Hashtable kSpecialCasesVersions;
  
  private static final Hashtable kSpecialCasesClasses;
  
  private static final Hashtable kSpecialCasesArrayPrefix;
  
  private static final Hashtable kSpecialPrimitives;
  
  private static final byte[] ASCII_HEX;
  
  public static final RepositoryIdCache cache;
  
  public static final String kjava_rmi_Remote;
  
  public static final String korg_omg_CORBA_Object;
  
  public static final Class[] kNoParamTypes;
  
  public static final Object[] kNoArgs;
  
  RepositoryId() {}
  
  RepositoryId(String paramString) { init(paramString); }
  
  RepositoryId init(String paramString) {
    this.repId = paramString;
    if (paramString.length() == 0) {
      this.clazz = java.rmi.Remote.class;
      this.typeString = "";
      this.isRMIValueType = true;
      this.suid = "0000000000000000";
      return this;
    } 
    if (paramString.equals("IDL:omg.org/CORBA/WStringValue:1.0")) {
      this.clazz = String.class;
      this.typeString = "omg.org/CORBA/WStringValue";
      this.isIDLType = true;
      this.completeClassName = "java.lang.String";
      this.versionString = "1.0";
      return this;
    } 
    String str = convertFromISOLatin1(paramString);
    int i = str.indexOf(':');
    if (i == -1)
      throw new IllegalArgumentException("RepsitoryId must have the form <type>:<body>"); 
    int j = str.indexOf(':', i + 1);
    if (j == -1) {
      this.versionString = "";
    } else {
      this.versionString = str.substring(j);
    } 
    if (str.startsWith("IDL:")) {
      this.typeString = str.substring(kIDLPrefixLength, str.indexOf(':', kIDLPrefixLength));
      this.isIDLType = true;
      if (this.typeString.startsWith("omg.org/")) {
        this.completeClassName = "org.omg." + this.typeString.substring("omg.org/".length()).replace('/', '.');
      } else {
        this.completeClassName = this.typeString.replace('/', '.');
      } 
    } else if (str.startsWith("RMI:")) {
      this.typeString = str.substring(kValuePrefixLength, str.indexOf(':', kValuePrefixLength));
      this.isRMIValueType = true;
      if (this.versionString.indexOf('.') == -1) {
        this.actualSuid = this.versionString.substring(1);
        this.suid = this.actualSuid;
        if (this.actualSuid.indexOf(':') != -1) {
          int k = this.actualSuid.indexOf(':') + 1;
          this.suid = this.actualSuid.substring(k);
          this.actualSuid = this.actualSuid.substring(0, k - 1);
        } 
      } 
    } else {
      this.isSupportedFormat = false;
      this.typeString = "";
    } 
    if (this.typeString.startsWith("["))
      this.isSequence = true; 
    return this;
  }
  
  public final String getUnqualifiedName() {
    if (this.unqualifiedName == null) {
      String str = getClassName();
      int i = str.lastIndexOf('.');
      if (i == -1) {
        this.unqualifiedName = str;
        this.definedInId = "IDL::1.0";
      } else {
        this.unqualifiedName = str.substring(i);
        this.definedInId = "IDL:" + str.substring(0, i).replace('.', '/') + ":1.0";
      } 
    } 
    return this.unqualifiedName;
  }
  
  public final String getDefinedInId() {
    if (this.definedInId == null)
      getUnqualifiedName(); 
    return this.definedInId;
  }
  
  public final String getTypeString() { return this.typeString; }
  
  public final String getVersionString() { return this.versionString; }
  
  public final String getSerialVersionUID() { return this.suid; }
  
  public final String getActualSerialVersionUID() { return this.actualSuid; }
  
  public final long getSerialVersionUIDAsLong() { return this.suidLong; }
  
  public final long getActualSerialVersionUIDAsLong() { return this.actualSuidLong; }
  
  public final boolean isRMIValueType() { return this.isRMIValueType; }
  
  public final boolean isIDLType() { return this.isIDLType; }
  
  public final String getRepositoryId() { return this.repId; }
  
  public static byte[] getByteArray(String paramString) {
    synchronized (repStrToByteArray) {
      return (byte[])repStrToByteArray.get(paramString);
    } 
  }
  
  public static void setByteArray(String paramString, byte[] paramArrayOfByte) {
    synchronized (repStrToByteArray) {
      repStrToByteArray.put(paramString, paramArrayOfByte);
    } 
  }
  
  public final boolean isSequence() { return this.isSequence; }
  
  public final boolean isSupportedFormat() { return this.isSupportedFormat; }
  
  public final String getClassName() { return this.isRMIValueType ? this.typeString : (this.isIDLType ? this.completeClassName : null); }
  
  public final Class getAnyClassFromType() throws ClassNotFoundException {
    try {
      return getClassFromType();
    } catch (ClassNotFoundException classNotFoundException) {
      Class clazz1 = (Class)repStrToClass.get(this.repId);
      if (clazz1 != null)
        return clazz1; 
      throw classNotFoundException;
    } 
  }
  
  public final Class getClassFromType() throws ClassNotFoundException {
    if (this.clazz != null)
      return this.clazz; 
    Class clazz1 = (Class)kSpecialCasesClasses.get(getClassName());
    if (clazz1 != null) {
      this.clazz = clazz1;
      return clazz1;
    } 
    try {
      return Util.loadClass(getClassName(), null, null);
    } catch (ClassNotFoundException classNotFoundException) {
      if (defaultServerURL != null)
        try {
          return getClassFromType(defaultServerURL);
        } catch (MalformedURLException malformedURLException) {
          throw classNotFoundException;
        }  
      throw classNotFoundException;
    } 
  }
  
  public final Class getClassFromType(Class paramClass, String paramString) throws ClassNotFoundException {
    if (this.clazz != null)
      return this.clazz; 
    Class clazz1 = (Class)kSpecialCasesClasses.get(getClassName());
    if (clazz1 != null) {
      this.clazz = clazz1;
      return clazz1;
    } 
    ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
    return Utility.loadClassOfType(getClassName(), paramString, classLoader, paramClass, classLoader);
  }
  
  public final Class getClassFromType(String paramString) throws ClassNotFoundException, MalformedURLException { return Util.loadClass(getClassName(), paramString, null); }
  
  public final String toString() { return this.repId; }
  
  public static boolean useFullValueDescription(Class paramClass, String paramString) throws IOException {
    RepositoryId repositoryId2;
    RepositoryId repositoryId1;
    String str = createForAnyType(paramClass);
    if (str.equals(paramString))
      return false; 
    synchronized (cache) {
      repositoryId1 = cache.getId(paramString);
      repositoryId2 = cache.getId(str);
    } 
    if (repositoryId1.isRMIValueType() && repositoryId2.isRMIValueType()) {
      if (!repositoryId1.getSerialVersionUID().equals(repositoryId2.getSerialVersionUID())) {
        String str1 = "Mismatched serialization UIDs : Source (Rep. ID" + repositoryId2 + ") = " + repositoryId2.getSerialVersionUID() + " whereas Target (Rep. ID " + paramString + ") = " + repositoryId1.getSerialVersionUID();
        throw new IOException(str1);
      } 
      return true;
    } 
    throw new IOException("The repository ID is not of an RMI value type (Expected ID = " + str + "; Received ID = " + paramString + ")");
  }
  
  private static String createHashString(Serializable paramSerializable) { return createHashString(paramSerializable.getClass()); }
  
  private static String createHashString(Class paramClass) {
    if (paramClass.isInterface() || !Serializable.class.isAssignableFrom(paramClass))
      return ":0000000000000000"; 
    long l1 = ObjectStreamClass.getActualSerialVersionUID(paramClass);
    String str1 = null;
    if (l1 == 0L) {
      str1 = "0000000000000000";
    } else if (l1 == 1L) {
      str1 = "0000000000000001";
    } else {
      str1 = Long.toHexString(l1).toUpperCase();
    } 
    while (str1.length() < 16)
      str1 = "0" + str1; 
    long l2 = ObjectStreamClass.getSerialVersionUID(paramClass);
    String str2 = null;
    if (l2 == 0L) {
      str2 = "0000000000000000";
    } else if (l2 == 1L) {
      str2 = "0000000000000001";
    } else {
      str2 = Long.toHexString(l2).toUpperCase();
    } 
    while (str2.length() < 16)
      str2 = "0" + str2; 
    str1 = str1 + ":" + str2;
    return ":" + str1;
  }
  
  public static String createSequenceRepID(Object paramObject) { return createSequenceRepID(paramObject.getClass()); }
  
  public static String createSequenceRepID(Class paramClass) {
    synchronized (classSeqToRepStr) {
      String str = (String)classSeqToRepStr.get(paramClass);
      if (str != null)
        return str; 
      Class clazz1 = paramClass;
      Class clazz2 = null;
      byte b = 0;
      while ((clazz2 = paramClass.getComponentType()) != null) {
        b++;
        paramClass = clazz2;
      } 
      if (paramClass.isPrimitive()) {
        str = "RMI:" + clazz1.getName() + ":0000000000000000";
      } else {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("RMI:");
        while (b-- > 0)
          stringBuffer.append("["); 
        stringBuffer.append("L");
        stringBuffer.append(convertToISOLatin1(paramClass.getName()));
        stringBuffer.append(";");
        stringBuffer.append(createHashString(paramClass));
        str = stringBuffer.toString();
      } 
      classSeqToRepStr.put(clazz1, str);
      return str;
    } 
  }
  
  public static String createForSpecialCase(Class paramClass) { return paramClass.isArray() ? createSequenceRepID(paramClass) : (String)kSpecialCasesRepIDs.get(paramClass); }
  
  public static String createForSpecialCase(Serializable paramSerializable) {
    Class clazz1 = paramSerializable.getClass();
    return clazz1.isArray() ? createSequenceRepID(paramSerializable) : createForSpecialCase(clazz1);
  }
  
  public static String createForJavaType(Serializable paramSerializable) {
    synchronized (classToRepStr) {
      String str = createForSpecialCase(paramSerializable);
      if (str != null)
        return str; 
      Class clazz1 = paramSerializable.getClass();
      str = (String)classToRepStr.get(clazz1);
      if (str != null)
        return str; 
      str = "RMI:" + convertToISOLatin1(clazz1.getName()) + createHashString(clazz1);
      classToRepStr.put(clazz1, str);
      repStrToClass.put(str, clazz1);
      return str;
    } 
  }
  
  public static String createForJavaType(Class paramClass) {
    synchronized (classToRepStr) {
      String str = createForSpecialCase(paramClass);
      if (str != null)
        return str; 
      str = (String)classToRepStr.get(paramClass);
      if (str != null)
        return str; 
      str = "RMI:" + convertToISOLatin1(paramClass.getName()) + createHashString(paramClass);
      classToRepStr.put(paramClass, str);
      repStrToClass.put(str, paramClass);
      return str;
    } 
  }
  
  public static String createForIDLType(Class paramClass, int paramInt1, int paramInt2) throws TypeMismatchException {
    synchronized (classIDLToRepStr) {
      String str = (String)classIDLToRepStr.get(paramClass);
      if (str != null)
        return str; 
      str = "IDL:" + convertToISOLatin1(paramClass.getName()).replace('.', '/') + ":" + paramInt1 + "." + paramInt2;
      classIDLToRepStr.put(paramClass, str);
      return str;
    } 
  }
  
  private static String getIdFromHelper(Class paramClass) {
    try {
      Class clazz1 = Utility.loadClassForClass(paramClass.getName() + "Helper", null, paramClass.getClassLoader(), paramClass, paramClass.getClassLoader());
      Method method = clazz1.getDeclaredMethod("id", kNoParamTypes);
      return (String)method.invoke(null, kNoArgs);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new MARSHAL(classNotFoundException.toString());
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new MARSHAL(noSuchMethodException.toString());
    } catch (InvocationTargetException invocationTargetException) {
      throw new MARSHAL(invocationTargetException.toString());
    } catch (IllegalAccessException illegalAccessException) {
      throw new MARSHAL(illegalAccessException.toString());
    } 
  }
  
  public static String createForAnyType(Class paramClass) {
    try {
      if (paramClass.isArray())
        return createSequenceRepID(paramClass); 
      if (org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(paramClass))
        try {
          return getIdFromHelper(paramClass);
        } catch (Throwable throwable) {
          return createForIDLType(paramClass, 1, 0);
        }  
      return createForJavaType(paramClass);
    } catch (TypeMismatchException typeMismatchException) {
      return null;
    } 
  }
  
  public static boolean isAbstractBase(Class paramClass) { return (paramClass.isInterface() && org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(paramClass) && !org.omg.CORBA.portable.ValueBase.class.isAssignableFrom(paramClass) && !org.omg.CORBA.Object.class.isAssignableFrom(paramClass)); }
  
  public static boolean isAnyRequired(Class paramClass) { return (paramClass == Object.class || paramClass == Serializable.class || paramClass == java.io.Externalizable.class); }
  
  public static long fromHex(String paramString) { return paramString.startsWith("0x") ? Long.valueOf(paramString.substring(2), 16).longValue() : Long.valueOf(paramString, 16).longValue(); }
  
  public static String convertToISOLatin1(String paramString) {
    int i = paramString.length();
    if (i == 0)
      return paramString; 
    StringBuffer stringBuffer = null;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c > 'ÿ' || IDL_IDENTIFIER_CHARS[c] == 0) {
        if (stringBuffer == null)
          stringBuffer = new StringBuffer(paramString.substring(0, b)); 
        stringBuffer.append("\\U" + (char)ASCII_HEX[(c & 0xF000) >>> '\f'] + (char)ASCII_HEX[(c & 0xF00) >>> '\b'] + (char)ASCII_HEX[(c & 0xF0) >>> '\004'] + (char)ASCII_HEX[c & 0xF]);
      } else if (stringBuffer != null) {
        stringBuffer.append(c);
      } 
    } 
    if (stringBuffer != null)
      paramString = stringBuffer.toString(); 
    return paramString;
  }
  
  private static String convertFromISOLatin1(String paramString) {
    int i = -1;
    StringBuffer stringBuffer = new StringBuffer(paramString);
    while ((i = stringBuffer.toString().indexOf("\\U")) != -1) {
      String str = "0000" + stringBuffer.toString().substring(i + 2, i + 6);
      byte[] arrayOfByte = new byte[(str.length() - 4) / 2];
      byte b1 = 4;
      for (byte b2 = 0; b1 < str.length(); b2++) {
        arrayOfByte[b2] = (byte)(Utility.hexOf(str.charAt(b1)) << 4 & 0xF0);
        arrayOfByte[b2] = (byte)(arrayOfByte[b2] | (byte)(Utility.hexOf(str.charAt(b1 + 1)) << 0 & 0xF));
        b1 += 2;
      } 
      stringBuffer = new StringBuffer(delete(stringBuffer.toString(), i, i + 6));
      stringBuffer.insert(i, (char)arrayOfByte[1]);
    } 
    return stringBuffer.toString();
  }
  
  private static String delete(String paramString, int paramInt1, int paramInt2) { return paramString.substring(0, paramInt1) + paramString.substring(paramInt2, paramString.length()); }
  
  private static String replace(String paramString1, String paramString2, String paramString3) {
    int i = 0;
    for (i = paramString1.indexOf(paramString2); i != -1; i = paramString1.indexOf(paramString2)) {
      String str1 = paramString1.substring(0, i);
      String str2 = paramString1.substring(i + paramString2.length());
      paramString1 = new String(str1 + paramString3 + str2);
    } 
    return paramString1;
  }
  
  public static int computeValueTag(boolean paramBoolean1, int paramInt, boolean paramBoolean2) {
    int i = 2147483392;
    if (paramBoolean1)
      i |= 0x1; 
    i |= paramInt;
    if (paramBoolean2)
      i |= 0x8; 
    return i;
  }
  
  public static boolean isCodeBasePresent(int paramInt) { return ((paramInt & true) == 1); }
  
  public static int getTypeInfo(int paramInt) { return paramInt & 0x6; }
  
  public static boolean isChunkedEncoding(int paramInt) { return ((paramInt & 0x8) != 0); }
  
  public static String getServerURL() { return defaultServerURL; }
  
  static  {
    if (defaultServerURL == null)
      defaultServerURL = JDKBridge.getLocalCodebase(); 
    useCodebaseOnly = JDKBridge.useCodebaseOnly();
    classToRepStr = new IdentityHashtable();
    classIDLToRepStr = new IdentityHashtable();
    classSeqToRepStr = new IdentityHashtable();
    repStrToByteArray = new IdentityHashtable();
    repStrToClass = new Hashtable();
    kValuePrefixLength = "RMI:".length();
    kIDLPrefixLength = "IDL:".length();
    kSequencePrefixLength = "[".length();
    kPreComputed_StandardRMIUnchunked = computeValueTag(false, 2, false);
    kPreComputed_CodeBaseRMIUnchunked = computeValueTag(true, 2, false);
    kPreComputed_StandardRMIChunked = computeValueTag(false, 2, true);
    kPreComputed_CodeBaseRMIChunked = computeValueTag(true, 2, true);
    kPreComputed_StandardRMIUnchunked_NoRep = computeValueTag(false, 0, false);
    kPreComputed_CodeBaseRMIUnchunked_NoRep = computeValueTag(true, 0, false);
    kPreComputed_StandardRMIChunked_NoRep = computeValueTag(false, 0, true);
    kPreComputed_CodeBaseRMIChunked_NoRep = computeValueTag(true, 0, true);
    kClassDescValueHash = ":" + Long.toHexString(ObjectStreamClass.getActualSerialVersionUID(javax.rmi.CORBA.ClassDesc.class)).toUpperCase() + ":" + Long.toHexString(ObjectStreamClass.getSerialVersionUID(javax.rmi.CORBA.ClassDesc.class)).toUpperCase();
    kClassDescValueRepID = "RMI:javax.rmi.CORBA.ClassDesc" + kClassDescValueHash;
    kSpecialArrayTypeStrings = new Hashtable();
    kSpecialArrayTypeStrings.put("CORBA.WStringValue", new StringBuffer(String.class.getName()));
    kSpecialArrayTypeStrings.put("javax.rmi.CORBA.ClassDesc", new StringBuffer(Class.class.getName()));
    kSpecialArrayTypeStrings.put("CORBA.Object", new StringBuffer(java.rmi.Remote.class.getName()));
    kSpecialCasesRepIDs = new Hashtable();
    kSpecialCasesRepIDs.put(String.class, "IDL:omg.org/CORBA/WStringValue:1.0");
    kSpecialCasesRepIDs.put(Class.class, kClassDescValueRepID);
    kSpecialCasesRepIDs.put(java.rmi.Remote.class, "");
    kSpecialCasesStubValues = new Hashtable();
    kSpecialCasesStubValues.put(String.class, "WStringValue");
    kSpecialCasesStubValues.put(Class.class, "ClassDesc");
    kSpecialCasesStubValues.put(Object.class, "Object");
    kSpecialCasesStubValues.put(Serializable.class, "Serializable");
    kSpecialCasesStubValues.put(java.io.Externalizable.class, "Externalizable");
    kSpecialCasesStubValues.put(java.rmi.Remote.class, "");
    kSpecialCasesVersions = new Hashtable();
    kSpecialCasesVersions.put(String.class, ":1.0");
    kSpecialCasesVersions.put(Class.class, kClassDescValueHash);
    kSpecialCasesVersions.put(Object.class, ":1.0");
    kSpecialCasesVersions.put(Serializable.class, ":1.0");
    kSpecialCasesVersions.put(java.io.Externalizable.class, ":1.0");
    kSpecialCasesVersions.put(java.rmi.Remote.class, "");
    kSpecialCasesClasses = new Hashtable();
    kSpecialCasesClasses.put("omg.org/CORBA/WStringValue", String.class);
    kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
    kSpecialCasesClasses.put("", java.rmi.Remote.class);
    kSpecialCasesClasses.put("org.omg.CORBA.WStringValue", String.class);
    kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
    kSpecialCasesArrayPrefix = new Hashtable();
    kSpecialCasesArrayPrefix.put(String.class, "RMI:[CORBA/");
    kSpecialCasesArrayPrefix.put(Class.class, "RMI:[javax/rmi/CORBA/");
    kSpecialCasesArrayPrefix.put(Object.class, "RMI:[java/lang/");
    kSpecialCasesArrayPrefix.put(Serializable.class, "RMI:[java/io/");
    kSpecialCasesArrayPrefix.put(java.io.Externalizable.class, "RMI:[java/io/");
    kSpecialCasesArrayPrefix.put(java.rmi.Remote.class, "RMI:[CORBA/");
    kSpecialPrimitives = new Hashtable();
    kSpecialPrimitives.put("int", "long");
    kSpecialPrimitives.put("long", "longlong");
    kSpecialPrimitives.put("byte", "octet");
    ASCII_HEX = new byte[] { 
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 
        65, 66, 67, 68, 69, 70 };
    cache = new RepositoryIdCache();
    kjava_rmi_Remote = createForAnyType(java.rmi.Remote.class);
    korg_omg_CORBA_Object = createForAnyType(org.omg.CORBA.Object.class);
    kNoParamTypes = new Class[0];
    kNoArgs = new Object[0];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\RepositoryId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */