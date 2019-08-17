package com.sun.xml.internal.ws.policy.privateutil;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class PolicyUtils {
  public static class Collections {
    public static <E, T extends Collection<? extends E>, U extends Collection<? extends E>> Collection<Collection<E>> combine(U param1U, Collection<T> param1Collection, boolean param1Boolean) {
      ArrayList arrayList = null;
      if (param1Collection == null || param1Collection.isEmpty()) {
        if (param1U != null) {
          arrayList = new ArrayList(1);
          arrayList.add(new ArrayList(param1U));
        } 
        return arrayList;
      } 
      LinkedList linkedList1 = new LinkedList();
      if (param1U != null && !param1U.isEmpty())
        linkedList1.addAll(param1U); 
      int i = 1;
      LinkedList linkedList2 = new LinkedList();
      for (Collection collection : param1Collection) {
        int j = collection.size();
        if (j == 0) {
          if (!param1Boolean)
            return null; 
          continue;
        } 
        if (j == 1) {
          linkedList1.addAll(collection);
          continue;
        } 
        linkedList2.offer(collection);
        i *= j;
      } 
      arrayList = new ArrayList(i);
      arrayList.add(linkedList1);
      if (i > 1) {
        Collection collection;
        while ((collection = (Collection)linkedList2.poll()) != null) {
          int j = arrayList.size();
          int k = j * collection.size();
          int m = 0;
          for (Object object : collection) {
            for (byte b = 0; b < j; b++) {
              Collection collection1 = (Collection)arrayList.get(m);
              if (m + j < k)
                arrayList.add(new LinkedList(collection1)); 
              collection1.add(object);
              m++;
            } 
          } 
        } 
      } 
      return arrayList;
    }
  }
  
  public static class Commons {
    public static String getStackMethodName(int param1Int) {
      String str;
      StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
      if (arrayOfStackTraceElement.length > param1Int + 1) {
        str = arrayOfStackTraceElement[param1Int].getMethodName();
      } else {
        str = "UNKNOWN METHOD";
      } 
      return str;
    }
    
    public static String getCallerMethodName() {
      String str = getStackMethodName(5);
      if (str.equals("invoke0"))
        str = getStackMethodName(4); 
      return str;
    }
  }
  
  public static class Comparison {
    public static final Comparator<QName> QNAME_COMPARATOR = new Comparator<QName>() {
        public int compare(QName param2QName1, QName param2QName2) {
          if (param2QName1 == param2QName2 || param2QName1.equals(param2QName2))
            return 0; 
          int i = param2QName1.getNamespaceURI().compareTo(param2QName2.getNamespaceURI());
          return (i != 0) ? i : param2QName1.getLocalPart().compareTo(param2QName2.getLocalPart());
        }
      };
    
    public static int compareBoolean(boolean param1Boolean1, boolean param1Boolean2) {
      byte b1 = param1Boolean1 ? 1 : 0;
      byte b2 = param1Boolean2 ? 1 : 0;
      return b1 - b2;
    }
    
    public static int compareNullableStrings(String param1String1, String param1String2) { return (param1String1 == null) ? ((param1String2 == null) ? 0 : -1) : ((param1String2 == null) ? 1 : param1String1.compareTo(param1String2)); }
  }
  
  public static class ConfigFile {
    public static String generateFullName(String param1String) throws PolicyException {
      if (param1String != null) {
        StringBuffer stringBuffer = new StringBuffer("wsit-");
        stringBuffer.append(param1String).append(".xml");
        return stringBuffer.toString();
      } 
      throw new PolicyException(LocalizationMessages.WSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL());
    }
    
    public static URL loadFromContext(String param1String, Object param1Object) { return (URL)PolicyUtils.Reflection.invoke(param1Object, "getResource", URL.class, new Object[] { param1String }); }
    
    public static URL loadFromClasspath(String param1String) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      return (classLoader == null) ? ClassLoader.getSystemResource(param1String) : classLoader.getResource(param1String);
    }
  }
  
  public static class IO {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(IO.class);
    
    public static void closeResource(Closeable param1Closeable) {
      if (param1Closeable != null)
        try {
          param1Closeable.close();
        } catch (IOException iOException) {
          LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(param1Closeable.toString()), iOException);
        }  
    }
    
    public static void closeResource(XMLStreamReader param1XMLStreamReader) {
      if (param1XMLStreamReader != null)
        try {
          param1XMLStreamReader.close();
        } catch (XMLStreamException xMLStreamException) {
          LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(param1XMLStreamReader.toString()), xMLStreamException);
        }  
    }
  }
  
  static class Reflection {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(Reflection.class);
    
    static <T> T invoke(Object param1Object, String param1String, Class<T> param1Class, Object... param1VarArgs) throws RuntimePolicyUtilsException {
      Class[] arrayOfClass;
      if (param1VarArgs != null && param1VarArgs.length > 0) {
        arrayOfClass = new Class[param1VarArgs.length];
        byte b = 0;
        for (Object object : param1VarArgs)
          arrayOfClass[b++] = object.getClass(); 
      } else {
        arrayOfClass = null;
      } 
      return (T)invoke(param1Object, param1String, param1Class, param1VarArgs, arrayOfClass);
    }
    
    public static <T> T invoke(Object param1Object, String param1String, Class<T> param1Class, Object[] param1ArrayOfObject, Class[] param1ArrayOfClass) throws RuntimePolicyUtilsException {
      try {
        Method method = param1Object.getClass().getMethod(param1String, param1ArrayOfClass);
        Object object = MethodUtil.invoke(param1Object, method, param1ArrayOfObject);
        return (T)param1Class.cast(object);
      } catch (IllegalArgumentException illegalArgumentException) {
        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(param1Object, param1ArrayOfObject, param1String), illegalArgumentException));
      } catch (InvocationTargetException invocationTargetException) {
        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(param1Object, param1ArrayOfObject, param1String), invocationTargetException));
      } catch (IllegalAccessException illegalAccessException) {
        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(param1Object, param1ArrayOfObject, param1String), illegalAccessException.getCause()));
      } catch (SecurityException securityException) {
        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(param1Object, param1ArrayOfObject, param1String), securityException));
      } catch (NoSuchMethodException noSuchMethodException) {
        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(param1Object, param1ArrayOfObject, param1String), noSuchMethodException));
      } 
    }
    
    private static String createExceptionMessage(Object param1Object, Object[] param1ArrayOfObject, String param1String) { return LocalizationMessages.WSP_0061_METHOD_INVOCATION_FAILED(param1Object.getClass().getName(), param1String, (param1ArrayOfObject == null) ? null : Arrays.asList(param1ArrayOfObject).toString()); }
  }
  
  public static class Rfc2396 {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtils.Reflection.class);
    
    public static String unquote(String param1String) throws PolicyException {
      if (null == param1String)
        return null; 
      byte[] arrayOfByte = new byte[param1String.length()];
      byte b1 = 0;
      for (b2 = 0; b2 < param1String.length(); b2++) {
        char c = param1String.charAt(b2);
        if ('%' == c) {
          if (b2 + 2 >= param1String.length())
            throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(param1String)), false); 
          int i = Character.digit(param1String.charAt(++b2), 16);
          int j = Character.digit(param1String.charAt(++b2), 16);
          if (0 > i || 0 > j)
            throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(param1String)), false); 
          arrayOfByte[b1++] = (byte)(i * 16 + j);
        } else {
          arrayOfByte[b1++] = (byte)c;
        } 
      } 
      try {
        return new String(arrayOfByte, 0, b1, "utf-8");
      } catch (UnsupportedEncodingException b2) {
        UnsupportedEncodingException unsupportedEncodingException;
        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(param1String), unsupportedEncodingException));
      } 
    }
  }
  
  public static class ServiceProvider {
    public static <T> T[] load(Class<T> param1Class, ClassLoader param1ClassLoader) { return (T[])ServiceFinder.find(param1Class, param1ClassLoader).toArray(); }
    
    public static <T> T[] load(Class<T> param1Class) { return (T[])ServiceFinder.find(param1Class).toArray(); }
  }
  
  public static class Text {
    public static final String NEW_LINE = System.getProperty("line.separator");
    
    public static String createIndent(int param1Int) {
      char[] arrayOfChar = new char[param1Int * 4];
      Arrays.fill(arrayOfChar, ' ');
      return String.valueOf(arrayOfChar);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\privateutil\PolicyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */