package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.util.Properties;
import org.xml.sax.ContentHandler;

public final class SerializerFactory {
  public static Serializer getSerializer(Properties paramProperties) {
    SerializationHandler serializationHandler;
    try {
      String str1 = paramProperties.getProperty("method");
      if (str1 == null) {
        String str = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[] { "method" });
        throw new IllegalArgumentException(str);
      } 
      String str2 = paramProperties.getProperty("{http://xml.apache.org/xalan}content-handler");
      if (null == str2) {
        Properties properties = OutputPropertiesFactory.getDefaultMethodProperties(str1);
        str2 = properties.getProperty("{http://xml.apache.org/xalan}content-handler");
        if (null == str2) {
          String str = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[] { "{http://xml.apache.org/xalan}content-handler" });
          throw new IllegalArgumentException(str);
        } 
      } 
      Class clazz = ObjectFactory.findProviderClass(str2, true);
      Object object = clazz.newInstance();
      if (object instanceof SerializationHandler) {
        serializationHandler = (Serializer)clazz.newInstance();
        serializationHandler.setOutputFormat(paramProperties);
      } else if (object instanceof ContentHandler) {
        str2 = "com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler";
        clazz = ObjectFactory.findProviderClass(str2, true);
        SerializationHandler serializationHandler1 = (SerializationHandler)clazz.newInstance();
        serializationHandler1.setContentHandler((ContentHandler)object);
        serializationHandler1.setOutputFormat(paramProperties);
        serializationHandler = serializationHandler1;
      } else {
        throw new Exception(Utils.messages.createMessage("ER_SERIALIZER_NOT_CONTENTHANDLER", new Object[] { str2 }));
      } 
    } catch (Exception exception) {
      throw new WrappedRuntimeException(exception);
    } 
    return serializationHandler;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\SerializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */