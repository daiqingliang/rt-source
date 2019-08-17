package com.sun.jndi.ldap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.spi.DirStateFactory;
import javax.naming.spi.DirectoryManager;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

final class Obj {
  static VersionHelper helper = VersionHelper.getVersionHelper();
  
  static final String[] JAVA_ATTRIBUTES = { "objectClass", "javaSerializedData", "javaClassName", "javaFactory", "javaCodeBase", "javaReferenceAddress", "javaClassNames", "javaRemoteLocation" };
  
  static final int OBJECT_CLASS = 0;
  
  static final int SERIALIZED_DATA = 1;
  
  static final int CLASSNAME = 2;
  
  static final int FACTORY = 3;
  
  static final int CODEBASE = 4;
  
  static final int REF_ADDR = 5;
  
  static final int TYPENAME = 6;
  
  @Deprecated
  private static final int REMOTE_LOC = 7;
  
  static final String[] JAVA_OBJECT_CLASSES = { "javaContainer", "javaObject", "javaNamingReference", "javaSerializedObject", "javaMarshalledObject" };
  
  static final String[] JAVA_OBJECT_CLASSES_LOWER = { "javacontainer", "javaobject", "javanamingreference", "javaserializedobject", "javamarshalledobject" };
  
  static final int STRUCTURAL = 0;
  
  static final int BASE_OBJECT = 1;
  
  static final int REF_OBJECT = 2;
  
  static final int SER_OBJECT = 3;
  
  static final int MAR_OBJECT = 4;
  
  private static Attributes encodeObject(char paramChar, Object paramObject, Attributes paramAttributes, Attribute paramAttribute, boolean paramBoolean) throws NamingException {
    boolean bool = (paramAttribute.size() == 0 || (paramAttribute.size() == 1 && paramAttribute.contains("top"))) ? 1 : 0;
    if (bool)
      paramAttribute.add(JAVA_OBJECT_CLASSES[0]); 
    if (paramObject instanceof Referenceable) {
      paramAttribute.add(JAVA_OBJECT_CLASSES[1]);
      paramAttribute.add(JAVA_OBJECT_CLASSES[2]);
      if (!paramBoolean)
        paramAttributes = (Attributes)paramAttributes.clone(); 
      paramAttributes.put(paramAttribute);
      return encodeReference(paramChar, ((Referenceable)paramObject).getReference(), paramAttributes, paramObject);
    } 
    if (paramObject instanceof Reference) {
      paramAttribute.add(JAVA_OBJECT_CLASSES[1]);
      paramAttribute.add(JAVA_OBJECT_CLASSES[2]);
      if (!paramBoolean)
        paramAttributes = (Attributes)paramAttributes.clone(); 
      paramAttributes.put(paramAttribute);
      return encodeReference(paramChar, (Reference)paramObject, paramAttributes, null);
    } 
    if (paramObject instanceof java.io.Serializable) {
      paramAttribute.add(JAVA_OBJECT_CLASSES[1]);
      if (!paramAttribute.contains(JAVA_OBJECT_CLASSES[4]) && !paramAttribute.contains(JAVA_OBJECT_CLASSES_LOWER[4]))
        paramAttribute.add(JAVA_OBJECT_CLASSES[3]); 
      if (!paramBoolean)
        paramAttributes = (Attributes)paramAttributes.clone(); 
      paramAttributes.put(paramAttribute);
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[1], serializeObject(paramObject)));
      if (paramAttributes.get(JAVA_ATTRIBUTES[2]) == null)
        paramAttributes.put(JAVA_ATTRIBUTES[2], paramObject.getClass().getName()); 
      if (paramAttributes.get(JAVA_ATTRIBUTES[6]) == null) {
        Attribute attribute = LdapCtxFactory.createTypeNameAttr(paramObject.getClass());
        if (attribute != null)
          paramAttributes.put(attribute); 
      } 
    } else if (!(paramObject instanceof DirContext)) {
      throw new IllegalArgumentException("can only bind Referenceable, Serializable, DirContext");
    } 
    return paramAttributes;
  }
  
  private static String[] getCodebases(Attribute paramAttribute) throws NamingException {
    if (paramAttribute == null)
      return null; 
    StringTokenizer stringTokenizer = new StringTokenizer((String)paramAttribute.get());
    Vector vector = new Vector(10);
    while (stringTokenizer.hasMoreTokens())
      vector.addElement(stringTokenizer.nextToken()); 
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = (String)vector.elementAt(b); 
    return arrayOfString;
  }
  
  static Object decodeObject(Attributes paramAttributes) throws NamingException {
    String[] arrayOfString = getCodebases(paramAttributes.get(JAVA_ATTRIBUTES[4]));
    try {
      Attribute attribute;
      if ((attribute = paramAttributes.get(JAVA_ATTRIBUTES[true])) != null) {
        ClassLoader classLoader = helper.getURLClassLoader(arrayOfString);
        return deserializeObject((byte[])attribute.get(), classLoader);
      } 
      if ((attribute = paramAttributes.get(JAVA_ATTRIBUTES[7])) != null)
        return decodeRmiObject((String)paramAttributes.get(JAVA_ATTRIBUTES[2]).get(), (String)attribute.get(), arrayOfString); 
      attribute = paramAttributes.get(JAVA_ATTRIBUTES[0]);
      return (attribute != null && (attribute.contains(JAVA_OBJECT_CLASSES[2]) || attribute.contains(JAVA_OBJECT_CLASSES_LOWER[2]))) ? decodeReference(paramAttributes, arrayOfString) : null;
    } catch (IOException iOException) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(iOException);
      throw namingException;
    } 
  }
  
  private static Attributes encodeReference(char paramChar, Reference paramReference, Attributes paramAttributes, Object paramObject) throws NamingException {
    if (paramReference == null)
      return paramAttributes; 
    String str;
    if ((str = paramReference.getClassName()) != null)
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[2], str)); 
    if ((str = paramReference.getFactoryClassName()) != null)
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[3], str)); 
    if ((str = paramReference.getFactoryClassLocation()) != null)
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[4], str)); 
    if (paramObject != null && paramAttributes.get(JAVA_ATTRIBUTES[6]) != null) {
      Attribute attribute = LdapCtxFactory.createTypeNameAttr(paramObject.getClass());
      if (attribute != null)
        paramAttributes.put(attribute); 
    } 
    int i = paramReference.size();
    if (i > 0) {
      BasicAttribute basicAttribute = new BasicAttribute(JAVA_ATTRIBUTES[5]);
      BASE64Encoder bASE64Encoder = null;
      for (byte b = 0; b < i; b++) {
        RefAddr refAddr = paramReference.get(b);
        if (refAddr instanceof StringRefAddr) {
          basicAttribute.add("" + paramChar + b + paramChar + refAddr.getType() + paramChar + refAddr.getContent());
        } else {
          if (bASE64Encoder == null)
            bASE64Encoder = new BASE64Encoder(); 
          basicAttribute.add("" + paramChar + b + paramChar + refAddr.getType() + paramChar + paramChar + bASE64Encoder.encodeBuffer(serializeObject(refAddr)));
        } 
      } 
      paramAttributes.put(basicAttribute);
    } 
    return paramAttributes;
  }
  
  private static Object decodeRmiObject(String paramString1, String paramString2, String[] paramArrayOfString) throws NamingException { return new Reference(paramString1, new StringRefAddr("URL", paramString2)); }
  
  private static Reference decodeReference(Attributes paramAttributes, String[] paramArrayOfString) throws NamingException, IOException {
    String str1;
    String str2 = null;
    Attribute attribute;
    if ((attribute = paramAttributes.get(JAVA_ATTRIBUTES[2])) != null) {
      str1 = (String)attribute.get();
    } else {
      throw new InvalidAttributesException(JAVA_ATTRIBUTES[2] + " attribute is required");
    } 
    if ((attribute = paramAttributes.get(JAVA_ATTRIBUTES[3])) != null)
      str2 = (String)attribute.get(); 
    Reference reference = new Reference(str1, str2, (paramArrayOfString != null) ? paramArrayOfString[0] : null);
    if ((attribute = paramAttributes.get(JAVA_ATTRIBUTES[5])) != null) {
      BASE64Decoder bASE64Decoder = null;
      ClassLoader classLoader = helper.getURLClassLoader(paramArrayOfString);
      Vector vector = new Vector();
      vector.setSize(attribute.size());
      NamingEnumeration namingEnumeration = attribute.getAll();
      while (namingEnumeration.hasMore()) {
        int k;
        String str3 = (String)namingEnumeration.next();
        if (str3.length() == 0)
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty attribute value"); 
        char c = str3.charAt(0);
        int i = 1;
        int j;
        if ((j = str3.indexOf(c, i)) < 0)
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - separator '" + c + "'not found"); 
        String str4;
        if ((str4 = str3.substring(i, j)) == null)
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr position"); 
        try {
          k = Integer.parseInt(str4);
        } catch (NumberFormatException numberFormatException) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - RefAddr position not an integer");
        } 
        i = j + 1;
        if ((j = str3.indexOf(c, i)) < 0)
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - RefAddr type not found"); 
        String str5;
        if ((str5 = str3.substring(i, j)) == null)
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr type"); 
        i = j + 1;
        if (i == str3.length()) {
          vector.setElementAt(new StringRefAddr(str5, null), k);
          continue;
        } 
        if (str3.charAt(i) == c) {
          i++;
          if (bASE64Decoder == null)
            bASE64Decoder = new BASE64Decoder(); 
          RefAddr refAddr = (RefAddr)deserializeObject(bASE64Decoder.decodeBuffer(str3.substring(i)), classLoader);
          vector.setElementAt(refAddr, k);
          continue;
        } 
        vector.setElementAt(new StringRefAddr(str5, str3.substring(i)), k);
      } 
      for (byte b = 0; b < vector.size(); b++)
        reference.add((RefAddr)vector.elementAt(b)); 
    } 
    return reference;
  }
  
  private static byte[] serializeObject(Object paramObject) throws NamingException {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try (ObjectOutputStream null = new ObjectOutputStream(byteArrayOutputStream)) {
        objectOutputStream.writeObject(paramObject);
      } 
      return byteArrayOutputStream.toByteArray();
    } catch (IOException iOException) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(iOException);
      throw namingException;
    } 
  }
  
  private static Object deserializeObject(byte[] paramArrayOfByte, ClassLoader paramClassLoader) throws NamingException {
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      try (ObjectInputStream null = (paramClassLoader == null) ? new ObjectInputStream(byteArrayInputStream) : new LoaderInputStream(byteArrayInputStream, paramClassLoader)) {
        return objectInputStream.readObject();
      } catch (ClassNotFoundException classNotFoundException) {
        NamingException namingException = new NamingException();
        namingException.setRootCause(classNotFoundException);
        throw namingException;
      } 
    } catch (IOException iOException) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(iOException);
      throw namingException;
    } 
  }
  
  static Attributes determineBindAttrs(char paramChar, Object paramObject, Attributes paramAttributes, boolean paramBoolean, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws NamingException {
    Attribute attribute;
    DirStateFactory.Result result = DirectoryManager.getStateToBind(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
    paramObject = result.getObject();
    paramAttributes = result.getAttributes();
    if (paramObject == null)
      return paramAttributes; 
    if (paramAttributes == null && paramObject instanceof DirContext) {
      paramBoolean = true;
      paramAttributes = ((DirContext)paramObject).getAttributes("");
    } 
    boolean bool = false;
    if (paramAttributes == null || paramAttributes.size() == 0) {
      paramAttributes = new BasicAttributes(true);
      paramBoolean = true;
      attribute = new BasicAttribute("objectClass", "top");
    } else {
      attribute = paramAttributes.get("objectClass");
      if (attribute == null && !paramAttributes.isCaseIgnored())
        attribute = paramAttributes.get("objectclass"); 
      if (attribute == null) {
        attribute = new BasicAttribute("objectClass", "top");
      } else if (bool || !paramBoolean) {
        attribute = (Attribute)attribute.clone();
      } 
    } 
    return encodeObject(paramChar, paramObject, paramAttributes, attribute, paramBoolean);
  }
  
  private static final class LoaderInputStream extends ObjectInputStream {
    private ClassLoader classLoader;
    
    LoaderInputStream(InputStream param1InputStream, ClassLoader param1ClassLoader) throws IOException {
      super(param1InputStream);
      this.classLoader = param1ClassLoader;
    }
    
    protected Class<?> resolveClass(ObjectStreamClass param1ObjectStreamClass) throws IOException, ClassNotFoundException {
      try {
        return this.classLoader.loadClass(param1ObjectStreamClass.getName());
      } catch (ClassNotFoundException classNotFoundException) {
        return super.resolveClass(param1ObjectStreamClass);
      } 
    }
    
    protected Class<?> resolveProxyClass(String[] param1ArrayOfString) throws IOException, ClassNotFoundException {
      ClassLoader classLoader1 = null;
      boolean bool = false;
      Class[] arrayOfClass = new Class[param1ArrayOfString.length];
      for (b = 0; b < param1ArrayOfString.length; b++) {
        Class clazz = Class.forName(param1ArrayOfString[b], false, this.classLoader);
        if ((clazz.getModifiers() & true) == 0)
          if (bool) {
            if (classLoader1 != clazz.getClassLoader())
              throw new IllegalAccessError("conflicting non-public interface class loaders"); 
          } else {
            classLoader1 = clazz.getClassLoader();
            bool = true;
          }  
        arrayOfClass[b] = clazz;
      } 
      try {
        return Proxy.getProxyClass(bool ? classLoader1 : this.classLoader, arrayOfClass);
      } catch (IllegalArgumentException b) {
        IllegalArgumentException illegalArgumentException;
        throw new ClassNotFoundException(null, illegalArgumentException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\Obj.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */