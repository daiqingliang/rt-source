package javax.xml.bind;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages {
  static final String PROVIDER_NOT_FOUND = "ContextFinder.ProviderNotFound";
  
  static final String COULD_NOT_INSTANTIATE = "ContextFinder.CouldNotInstantiate";
  
  static final String CANT_FIND_PROPERTIES_FILE = "ContextFinder.CantFindPropertiesFile";
  
  static final String CANT_MIX_PROVIDERS = "ContextFinder.CantMixProviders";
  
  static final String MISSING_PROPERTY = "ContextFinder.MissingProperty";
  
  static final String NO_PACKAGE_IN_CONTEXTPATH = "ContextFinder.NoPackageInContextPath";
  
  static final String NAME_VALUE = "PropertyException.NameValue";
  
  static final String CONVERTER_MUST_NOT_BE_NULL = "DatatypeConverter.ConverterMustNotBeNull";
  
  static final String ILLEGAL_CAST = "JAXBContext.IllegalCast";
  
  static String format(String paramString) { return format(paramString, null); }
  
  static String format(String paramString, Object paramObject) { return format(paramString, new Object[] { paramObject }); }
  
  static String format(String paramString, Object paramObject1, Object paramObject2) { return format(paramString, new Object[] { paramObject1, paramObject2 }); }
  
  static String format(String paramString, Object paramObject1, Object paramObject2, Object paramObject3) { return format(paramString, new Object[] { paramObject1, paramObject2, paramObject3 }); }
  
  static String format(String paramString, Object[] paramArrayOfObject) {
    String str = ResourceBundle.getBundle(Messages.class.getName()).getString(paramString);
    return MessageFormat.format(str, paramArrayOfObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */