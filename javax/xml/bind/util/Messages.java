package javax.xml.bind.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages {
  static final String UNRECOGNIZED_SEVERITY = "ValidationEventCollector.UnrecognizedSeverity";
  
  static final String RESULT_NULL_CONTEXT = "JAXBResult.NullContext";
  
  static final String RESULT_NULL_UNMARSHALLER = "JAXBResult.NullUnmarshaller";
  
  static final String SOURCE_NULL_CONTEXT = "JAXBSource.NullContext";
  
  static final String SOURCE_NULL_CONTENT = "JAXBSource.NullContent";
  
  static final String SOURCE_NULL_MARSHALLER = "JAXBSource.NullMarshaller";
  
  static String format(String paramString) { return format(paramString, null); }
  
  static String format(String paramString, Object paramObject) { return format(paramString, new Object[] { paramObject }); }
  
  static String format(String paramString, Object paramObject1, Object paramObject2) { return format(paramString, new Object[] { paramObject1, paramObject2 }); }
  
  static String format(String paramString, Object paramObject1, Object paramObject2, Object paramObject3) { return format(paramString, new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  static String format(String paramString, Object[] paramArrayOfObject) {
    String str = ResourceBundle.getBundle(Messages.class.getName()).getString(paramString);
    return MessageFormat.format(str, paramArrayOfObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bin\\util\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */