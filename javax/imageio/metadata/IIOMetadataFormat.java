package javax.imageio.metadata;

import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;

public interface IIOMetadataFormat {
  public static final int CHILD_POLICY_EMPTY = 0;
  
  public static final int CHILD_POLICY_ALL = 1;
  
  public static final int CHILD_POLICY_SOME = 2;
  
  public static final int CHILD_POLICY_CHOICE = 3;
  
  public static final int CHILD_POLICY_SEQUENCE = 4;
  
  public static final int CHILD_POLICY_REPEAT = 5;
  
  public static final int CHILD_POLICY_MAX = 5;
  
  public static final int VALUE_NONE = 0;
  
  public static final int VALUE_ARBITRARY = 1;
  
  public static final int VALUE_RANGE = 2;
  
  public static final int VALUE_RANGE_MIN_INCLUSIVE_MASK = 4;
  
  public static final int VALUE_RANGE_MAX_INCLUSIVE_MASK = 8;
  
  public static final int VALUE_RANGE_MIN_INCLUSIVE = 6;
  
  public static final int VALUE_RANGE_MAX_INCLUSIVE = 10;
  
  public static final int VALUE_RANGE_MIN_MAX_INCLUSIVE = 14;
  
  public static final int VALUE_ENUMERATION = 16;
  
  public static final int VALUE_LIST = 32;
  
  public static final int DATATYPE_STRING = 0;
  
  public static final int DATATYPE_BOOLEAN = 1;
  
  public static final int DATATYPE_INTEGER = 2;
  
  public static final int DATATYPE_FLOAT = 3;
  
  public static final int DATATYPE_DOUBLE = 4;
  
  String getRootName();
  
  boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier);
  
  int getElementMinChildren(String paramString);
  
  int getElementMaxChildren(String paramString);
  
  String getElementDescription(String paramString, Locale paramLocale);
  
  int getChildPolicy(String paramString);
  
  String[] getChildNames(String paramString);
  
  String[] getAttributeNames(String paramString);
  
  int getAttributeValueType(String paramString1, String paramString2);
  
  int getAttributeDataType(String paramString1, String paramString2);
  
  boolean isAttributeRequired(String paramString1, String paramString2);
  
  String getAttributeDefaultValue(String paramString1, String paramString2);
  
  String[] getAttributeEnumerations(String paramString1, String paramString2);
  
  String getAttributeMinValue(String paramString1, String paramString2);
  
  String getAttributeMaxValue(String paramString1, String paramString2);
  
  int getAttributeListMinLength(String paramString1, String paramString2);
  
  int getAttributeListMaxLength(String paramString1, String paramString2);
  
  String getAttributeDescription(String paramString1, String paramString2, Locale paramLocale);
  
  int getObjectValueType(String paramString);
  
  Class<?> getObjectClass(String paramString);
  
  Object getObjectDefaultValue(String paramString);
  
  Object[] getObjectEnumerations(String paramString);
  
  Comparable<?> getObjectMinValue(String paramString);
  
  Comparable<?> getObjectMaxValue(String paramString);
  
  int getObjectArrayMinLength(String paramString);
  
  int getObjectArrayMaxLength(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIOMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */