package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class SerializerFactory {
  public static final String FactoriesProperty = "com.sun.org.apache.xml.internal.serialize.factories";
  
  private static final Map<String, SerializerFactory> _factories = Collections.synchronizedMap(new HashMap());
  
  public static void registerSerializerFactory(SerializerFactory paramSerializerFactory) {
    synchronized (_factories) {
      String str = paramSerializerFactory.getSupportedMethod();
      _factories.put(str, paramSerializerFactory);
    } 
  }
  
  public static SerializerFactory getSerializerFactory(String paramString) { return (SerializerFactory)_factories.get(paramString); }
  
  protected abstract String getSupportedMethod();
  
  public abstract Serializer makeSerializer(OutputFormat paramOutputFormat);
  
  public abstract Serializer makeSerializer(Writer paramWriter, OutputFormat paramOutputFormat);
  
  public abstract Serializer makeSerializer(OutputStream paramOutputStream, OutputFormat paramOutputFormat) throws UnsupportedEncodingException;
  
  static  {
    SerializerFactoryImpl serializerFactoryImpl = new SerializerFactoryImpl("xml");
    registerSerializerFactory(serializerFactoryImpl);
    serializerFactoryImpl = new SerializerFactoryImpl("html");
    registerSerializerFactory(serializerFactoryImpl);
    serializerFactoryImpl = new SerializerFactoryImpl("xhtml");
    registerSerializerFactory(serializerFactoryImpl);
    serializerFactoryImpl = new SerializerFactoryImpl("text");
    registerSerializerFactory(serializerFactoryImpl);
    String str = SecuritySupport.getSystemProperty("com.sun.org.apache.xml.internal.serialize.factories");
    if (str != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str, " ;,:");
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        try {
          SerializerFactory serializerFactory = (SerializerFactory)ObjectFactory.newInstance(str1, true);
          if (_factories.containsKey(serializerFactory.getSupportedMethod()))
            _factories.put(serializerFactory.getSupportedMethod(), serializerFactory); 
        } catch (Exception exception) {}
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\SerializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */