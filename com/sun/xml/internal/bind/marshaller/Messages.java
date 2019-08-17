package com.sun.xml.internal.bind.marshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
  public static final String NOT_MARSHALLABLE = "MarshallerImpl.NotMarshallable";
  
  public static final String UNSUPPORTED_RESULT = "MarshallerImpl.UnsupportedResult";
  
  public static final String UNSUPPORTED_ENCODING = "MarshallerImpl.UnsupportedEncoding";
  
  public static final String NULL_WRITER = "MarshallerImpl.NullWriterParam";
  
  public static final String ASSERT_FAILED = "SAXMarshaller.AssertFailed";
  
  public static final String ERR_MISSING_OBJECT = "SAXMarshaller.MissingObject";
  
  public static final String ERR_DANGLING_IDREF = "SAXMarshaller.DanglingIDREF";
  
  public static final String ERR_NOT_IDENTIFIABLE = "SAXMarshaller.NotIdentifiable";
  
  public static final String DOM_IMPL_DOESNT_SUPPORT_CREATELEMENTNS = "SAX2DOMEx.DomImplDoesntSupportCreateElementNs";
  
  public static String format(String paramString) { return format(paramString, null); }
  
  public static String format(String paramString, Object paramObject) { return format(paramString, new Object[] { paramObject }); }
  
  public static String format(String paramString, Object paramObject1, Object paramObject2) { return format(paramString, new Object[] { paramObject1, paramObject2 }); }
  
  public static String format(String paramString, Object paramObject1, Object paramObject2, Object paramObject3) { return format(paramString, new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  static String format(String paramString, Object[] paramArrayOfObject) {
    String str = ResourceBundle.getBundle(Messages.class.getName()).getString(paramString);
    return MessageFormat.format(str, paramArrayOfObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\marshaller\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */