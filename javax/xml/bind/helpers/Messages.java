package javax.xml.bind.helpers;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages {
  static final String INPUTSTREAM_NOT_NULL = "AbstractUnmarshallerImpl.ISNotNull";
  
  static final String MUST_BE_BOOLEAN = "AbstractMarshallerImpl.MustBeBoolean";
  
  static final String MUST_BE_STRING = "AbstractMarshallerImpl.MustBeString";
  
  static final String SEVERITY_MESSAGE = "DefaultValidationEventHandler.SeverityMessage";
  
  static final String LOCATION_UNAVAILABLE = "DefaultValidationEventHandler.LocationUnavailable";
  
  static final String UNRECOGNIZED_SEVERITY = "DefaultValidationEventHandler.UnrecognizedSeverity";
  
  static final String WARNING = "DefaultValidationEventHandler.Warning";
  
  static final String ERROR = "DefaultValidationEventHandler.Error";
  
  static final String FATAL_ERROR = "DefaultValidationEventHandler.FatalError";
  
  static final String ILLEGAL_SEVERITY = "ValidationEventImpl.IllegalSeverity";
  
  static final String MUST_NOT_BE_NULL = "Shared.MustNotBeNull";
  
  static String format(String paramString) { return format(paramString, null); }
  
  static String format(String paramString, Object paramObject) { return format(paramString, new Object[] { paramObject }); }
  
  static String format(String paramString, Object paramObject1, Object paramObject2) { return format(paramString, new Object[] { paramObject1, paramObject2 }); }
  
  static String format(String paramString, Object paramObject1, Object paramObject2, Object paramObject3) { return format(paramString, new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  static String format(String paramString, Object[] paramArrayOfObject) {
    String str = ResourceBundle.getBundle(Messages.class.getName()).getString(paramString);
    return MessageFormat.format(str, paramArrayOfObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\helpers\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */