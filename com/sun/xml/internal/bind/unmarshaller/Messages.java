package com.sun.xml.internal.bind.unmarshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
  public static final String UNEXPECTED_ENTER_ELEMENT = "ContentHandlerEx.UnexpectedEnterElement";
  
  public static final String UNEXPECTED_LEAVE_ELEMENT = "ContentHandlerEx.UnexpectedLeaveElement";
  
  public static final String UNEXPECTED_ENTER_ATTRIBUTE = "ContentHandlerEx.UnexpectedEnterAttribute";
  
  public static final String UNEXPECTED_LEAVE_ATTRIBUTE = "ContentHandlerEx.UnexpectedLeaveAttribute";
  
  public static final String UNEXPECTED_TEXT = "ContentHandlerEx.UnexpectedText";
  
  public static final String UNEXPECTED_LEAVE_CHILD = "ContentHandlerEx.UnexpectedLeaveChild";
  
  public static final String UNEXPECTED_ROOT_ELEMENT = "SAXUnmarshallerHandlerImpl.UnexpectedRootElement";
  
  public static final String UNDEFINED_PREFIX = "Util.UndefinedPrefix";
  
  public static final String NULL_READER = "Unmarshaller.NullReader";
  
  public static final String ILLEGAL_READER_STATE = "Unmarshaller.IllegalReaderState";
  
  public static String format(String paramString) { return format(paramString, null); }
  
  public static String format(String paramString, Object paramObject) { return format(paramString, new Object[] { paramObject }); }
  
  public static String format(String paramString, Object paramObject1, Object paramObject2) { return format(paramString, new Object[] { paramObject1, paramObject2 }); }
  
  public static String format(String paramString, Object paramObject1, Object paramObject2, Object paramObject3) { return format(paramString, new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  public static String format(String paramString, Object[] paramArrayOfObject) {
    String str = ResourceBundle.getBundle(Messages.class.getName()).getString(paramString);
    return MessageFormat.format(str, paramArrayOfObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bin\\unmarshaller\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */