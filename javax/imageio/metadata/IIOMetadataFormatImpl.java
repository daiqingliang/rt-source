package javax.imageio.metadata;

import com.sun.imageio.plugins.common.StandardMetadataFormat;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.ImageTypeSpecifier;

public abstract class IIOMetadataFormatImpl implements IIOMetadataFormat {
  public static final String standardMetadataFormatName = "javax_imageio_1.0";
  
  private static IIOMetadataFormat standardFormat = null;
  
  private String resourceBaseName = getClass().getName() + "Resources";
  
  private String rootName;
  
  private HashMap elementMap = new HashMap();
  
  public IIOMetadataFormatImpl(String paramString, int paramInt) {
    if (paramString == null)
      throw new IllegalArgumentException("rootName == null!"); 
    if (paramInt < 0 || paramInt > 5 || paramInt == 5)
      throw new IllegalArgumentException("Invalid value for childPolicy!"); 
    this.rootName = paramString;
    Element element = new Element();
    element.elementName = paramString;
    element.childPolicy = paramInt;
    this.elementMap.put(paramString, element);
  }
  
  public IIOMetadataFormatImpl(String paramString, int paramInt1, int paramInt2) {
    if (paramString == null)
      throw new IllegalArgumentException("rootName == null!"); 
    if (paramInt1 < 0)
      throw new IllegalArgumentException("minChildren < 0!"); 
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("minChildren > maxChildren!"); 
    Element element = new Element();
    element.elementName = paramString;
    element.childPolicy = 5;
    element.minChildren = paramInt1;
    element.maxChildren = paramInt2;
    this.rootName = paramString;
    this.elementMap.put(paramString, element);
  }
  
  protected void setResourceBaseName(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("resourceBaseName == null!"); 
    this.resourceBaseName = paramString;
  }
  
  protected String getResourceBaseName() { return this.resourceBaseName; }
  
  private Element getElement(String paramString, boolean paramBoolean) {
    if (paramBoolean && paramString == null)
      throw new IllegalArgumentException("element name is null!"); 
    Element element = (Element)this.elementMap.get(paramString);
    if (paramBoolean && element == null)
      throw new IllegalArgumentException("No such element: " + paramString); 
    return element;
  }
  
  private Element getElement(String paramString) { return getElement(paramString, true); }
  
  private Attribute getAttribute(String paramString1, String paramString2) {
    Element element = getElement(paramString1);
    Attribute attribute = (Attribute)element.attrMap.get(paramString2);
    if (attribute == null)
      throw new IllegalArgumentException("No such attribute \"" + paramString2 + "\"!"); 
    return attribute;
  }
  
  protected void addElement(String paramString1, String paramString2, int paramInt) {
    Element element1 = getElement(paramString2);
    if (paramInt < 0 || paramInt > 5 || paramInt == 5)
      throw new IllegalArgumentException("Invalid value for childPolicy!"); 
    Element element2 = new Element();
    element2.elementName = paramString1;
    element2.childPolicy = paramInt;
    element1.childList.add(paramString1);
    element2.parentList.add(paramString2);
    this.elementMap.put(paramString1, element2);
  }
  
  protected void addElement(String paramString1, String paramString2, int paramInt1, int paramInt2) {
    Element element1 = getElement(paramString2);
    if (paramInt1 < 0)
      throw new IllegalArgumentException("minChildren < 0!"); 
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("minChildren > maxChildren!"); 
    Element element2 = new Element();
    element2.elementName = paramString1;
    element2.childPolicy = 5;
    element2.minChildren = paramInt1;
    element2.maxChildren = paramInt2;
    element1.childList.add(paramString1);
    element2.parentList.add(paramString2);
    this.elementMap.put(paramString1, element2);
  }
  
  protected void addChildElement(String paramString1, String paramString2) {
    Element element1 = getElement(paramString2);
    Element element2 = getElement(paramString1);
    element1.childList.add(paramString1);
    element2.parentList.add(paramString2);
  }
  
  protected void removeElement(String paramString) {
    Element element = getElement(paramString, false);
    if (element != null) {
      for (String str : element.parentList) {
        Element element1 = getElement(str, false);
        if (element1 != null)
          element1.childList.remove(paramString); 
      } 
      this.elementMap.remove(paramString);
    } 
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt, boolean paramBoolean, String paramString3) {
    Element element = getElement(paramString1);
    if (paramString2 == null)
      throw new IllegalArgumentException("attrName == null!"); 
    if (paramInt < 0 || paramInt > 4)
      throw new IllegalArgumentException("Invalid value for dataType!"); 
    Attribute attribute = new Attribute();
    attribute.attrName = paramString2;
    attribute.valueType = 1;
    attribute.dataType = paramInt;
    attribute.required = paramBoolean;
    attribute.defaultValue = paramString3;
    element.attrList.add(paramString2);
    element.attrMap.put(paramString2, attribute);
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt, boolean paramBoolean, String paramString3, List<String> paramList) {
    Element element = getElement(paramString1);
    if (paramString2 == null)
      throw new IllegalArgumentException("attrName == null!"); 
    if (paramInt < 0 || paramInt > 4)
      throw new IllegalArgumentException("Invalid value for dataType!"); 
    if (paramList == null)
      throw new IllegalArgumentException("enumeratedValues == null!"); 
    if (paramList.size() == 0)
      throw new IllegalArgumentException("enumeratedValues is empty!"); 
    for (Object object : paramList) {
      if (object == null)
        throw new IllegalArgumentException("enumeratedValues contains a null!"); 
      if (!(object instanceof String))
        throw new IllegalArgumentException("enumeratedValues contains a non-String value!"); 
    } 
    Attribute attribute = new Attribute();
    attribute.attrName = paramString2;
    attribute.valueType = 16;
    attribute.dataType = paramInt;
    attribute.required = paramBoolean;
    attribute.defaultValue = paramString3;
    attribute.enumeratedValues = paramList;
    element.attrList.add(paramString2);
    element.attrMap.put(paramString2, attribute);
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt, boolean paramBoolean1, String paramString3, String paramString4, String paramString5, boolean paramBoolean2, boolean paramBoolean3) {
    Element element = getElement(paramString1);
    if (paramString2 == null)
      throw new IllegalArgumentException("attrName == null!"); 
    if (paramInt < 0 || paramInt > 4)
      throw new IllegalArgumentException("Invalid value for dataType!"); 
    Attribute attribute = new Attribute();
    attribute.attrName = paramString2;
    attribute.valueType = 2;
    if (paramBoolean2)
      attribute.valueType |= 0x4; 
    if (paramBoolean3)
      attribute.valueType |= 0x8; 
    attribute.dataType = paramInt;
    attribute.required = paramBoolean1;
    attribute.defaultValue = paramString3;
    attribute.minValue = paramString4;
    attribute.maxValue = paramString5;
    element.attrList.add(paramString2);
    element.attrMap.put(paramString2, attribute);
  }
  
  protected void addAttribute(String paramString1, String paramString2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3) {
    Element element = getElement(paramString1);
    if (paramString2 == null)
      throw new IllegalArgumentException("attrName == null!"); 
    if (paramInt1 < 0 || paramInt1 > 4)
      throw new IllegalArgumentException("Invalid value for dataType!"); 
    if (paramInt2 < 0 || paramInt2 > paramInt3)
      throw new IllegalArgumentException("Invalid list bounds!"); 
    Attribute attribute = new Attribute();
    attribute.attrName = paramString2;
    attribute.valueType = 32;
    attribute.dataType = paramInt1;
    attribute.required = paramBoolean;
    attribute.listMinLength = paramInt2;
    attribute.listMaxLength = paramInt3;
    element.attrList.add(paramString2);
    element.attrMap.put(paramString2, attribute);
  }
  
  protected void addBooleanAttribute(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) {
    ArrayList arrayList = new ArrayList();
    arrayList.add("TRUE");
    arrayList.add("FALSE");
    String str = null;
    if (paramBoolean1)
      str = paramBoolean2 ? "TRUE" : "FALSE"; 
    addAttribute(paramString1, paramString2, 1, true, str, arrayList);
  }
  
  protected void removeAttribute(String paramString1, String paramString2) {
    Element element = getElement(paramString1);
    element.attrList.remove(paramString2);
    element.attrMap.remove(paramString2);
  }
  
  protected <T> void addObjectValue(String paramString, Class<T> paramClass, boolean paramBoolean, T paramT) {
    Element element = getElement(paramString);
    ObjectValue objectValue = new ObjectValue();
    objectValue.valueType = 1;
    objectValue.classType = paramClass;
    objectValue.defaultValue = paramT;
    element.objectValue = objectValue;
  }
  
  protected <T> void addObjectValue(String paramString, Class<T> paramClass, boolean paramBoolean, T paramT, List<? extends T> paramList) {
    Element element = getElement(paramString);
    if (paramList == null)
      throw new IllegalArgumentException("enumeratedValues == null!"); 
    if (paramList.size() == 0)
      throw new IllegalArgumentException("enumeratedValues is empty!"); 
    for (Object object : paramList) {
      if (object == null)
        throw new IllegalArgumentException("enumeratedValues contains a null!"); 
      if (!paramClass.isInstance(object))
        throw new IllegalArgumentException("enumeratedValues contains a value not of class classType!"); 
    } 
    ObjectValue objectValue = new ObjectValue();
    objectValue.valueType = 16;
    objectValue.classType = paramClass;
    objectValue.defaultValue = paramT;
    objectValue.enumeratedValues = paramList;
    element.objectValue = objectValue;
  }
  
  protected <T extends Comparable<? super T>> void addObjectValue(String paramString, Class<T> paramClass, T paramT, Comparable<? super T> paramComparable1, Comparable<? super T> paramComparable2, boolean paramBoolean1, boolean paramBoolean2) {
    Element element = getElement(paramString);
    ObjectValue objectValue = new ObjectValue();
    objectValue.valueType = 2;
    if (paramBoolean1)
      objectValue.valueType |= 0x4; 
    if (paramBoolean2)
      objectValue.valueType |= 0x8; 
    objectValue.classType = paramClass;
    objectValue.defaultValue = paramT;
    objectValue.minValue = paramComparable1;
    objectValue.maxValue = paramComparable2;
    element.objectValue = objectValue;
  }
  
  protected void addObjectValue(String paramString, Class<?> paramClass, int paramInt1, int paramInt2) {
    Element element = getElement(paramString);
    ObjectValue objectValue = new ObjectValue();
    objectValue.valueType = 32;
    objectValue.classType = paramClass;
    objectValue.arrayMinLength = paramInt1;
    objectValue.arrayMaxLength = paramInt2;
    element.objectValue = objectValue;
  }
  
  protected void removeObjectValue(String paramString) {
    Element element = getElement(paramString);
    element.objectValue = null;
  }
  
  public String getRootName() { return this.rootName; }
  
  public abstract boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier);
  
  public int getElementMinChildren(String paramString) {
    Element element = getElement(paramString);
    if (element.childPolicy != 5)
      throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!"); 
    return element.minChildren;
  }
  
  public int getElementMaxChildren(String paramString) {
    Element element = getElement(paramString);
    if (element.childPolicy != 5)
      throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!"); 
    return element.maxChildren;
  }
  
  private String getResource(String paramString, Locale paramLocale) {
    if (paramLocale == null)
      paramLocale = Locale.getDefault(); 
    ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return Thread.currentThread().getContextClassLoader(); }
        });
    ResourceBundle resourceBundle = null;
    try {
      resourceBundle = ResourceBundle.getBundle(this.resourceBaseName, paramLocale, classLoader);
    } catch (MissingResourceException missingResourceException) {
      try {
        resourceBundle = ResourceBundle.getBundle(this.resourceBaseName, paramLocale);
      } catch (MissingResourceException missingResourceException1) {
        return null;
      } 
    } 
    try {
      return resourceBundle.getString(paramString);
    } catch (MissingResourceException missingResourceException) {
      return null;
    } 
  }
  
  public String getElementDescription(String paramString, Locale paramLocale) {
    Element element = getElement(paramString);
    return getResource(paramString, paramLocale);
  }
  
  public int getChildPolicy(String paramString) {
    Element element = getElement(paramString);
    return element.childPolicy;
  }
  
  public String[] getChildNames(String paramString) {
    Element element = getElement(paramString);
    return (element.childPolicy == 0) ? null : (String[])element.childList.toArray(new String[0]);
  }
  
  public String[] getAttributeNames(String paramString) {
    Element element = getElement(paramString);
    List list = element.attrList;
    String[] arrayOfString = new String[list.size()];
    return (String[])list.toArray(arrayOfString);
  }
  
  public int getAttributeValueType(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    return attribute.valueType;
  }
  
  public int getAttributeDataType(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    return attribute.dataType;
  }
  
  public boolean isAttributeRequired(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    return attribute.required;
  }
  
  public String getAttributeDefaultValue(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    return attribute.defaultValue;
  }
  
  public String[] getAttributeEnumerations(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    if (attribute.valueType != 16)
      throw new IllegalArgumentException("Attribute not an enumeration!"); 
    List list = attribute.enumeratedValues;
    Iterator iterator = list.iterator();
    String[] arrayOfString = new String[list.size()];
    return (String[])list.toArray(arrayOfString);
  }
  
  public String getAttributeMinValue(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    if (attribute.valueType != 2 && attribute.valueType != 6 && attribute.valueType != 10 && attribute.valueType != 14)
      throw new IllegalArgumentException("Attribute not a range!"); 
    return attribute.minValue;
  }
  
  public String getAttributeMaxValue(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    if (attribute.valueType != 2 && attribute.valueType != 6 && attribute.valueType != 10 && attribute.valueType != 14)
      throw new IllegalArgumentException("Attribute not a range!"); 
    return attribute.maxValue;
  }
  
  public int getAttributeListMinLength(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    if (attribute.valueType != 32)
      throw new IllegalArgumentException("Attribute not a list!"); 
    return attribute.listMinLength;
  }
  
  public int getAttributeListMaxLength(String paramString1, String paramString2) {
    Attribute attribute = getAttribute(paramString1, paramString2);
    if (attribute.valueType != 32)
      throw new IllegalArgumentException("Attribute not a list!"); 
    return attribute.listMaxLength;
  }
  
  public String getAttributeDescription(String paramString1, String paramString2, Locale paramLocale) {
    Element element = getElement(paramString1);
    if (paramString2 == null)
      throw new IllegalArgumentException("attrName == null!"); 
    Attribute attribute = (Attribute)element.attrMap.get(paramString2);
    if (attribute == null)
      throw new IllegalArgumentException("No such attribute!"); 
    String str = paramString1 + "/" + paramString2;
    return getResource(str, paramLocale);
  }
  
  private ObjectValue getObjectValue(String paramString) {
    Element element = getElement(paramString);
    ObjectValue objectValue = element.objectValue;
    if (objectValue == null)
      throw new IllegalArgumentException("No object within element " + paramString + "!"); 
    return objectValue;
  }
  
  public int getObjectValueType(String paramString) {
    Element element = getElement(paramString);
    ObjectValue objectValue = element.objectValue;
    return (objectValue == null) ? 0 : objectValue.valueType;
  }
  
  public Class<?> getObjectClass(String paramString) {
    ObjectValue objectValue = getObjectValue(paramString);
    return objectValue.classType;
  }
  
  public Object getObjectDefaultValue(String paramString) {
    ObjectValue objectValue = getObjectValue(paramString);
    return objectValue.defaultValue;
  }
  
  public Object[] getObjectEnumerations(String paramString) {
    ObjectValue objectValue = getObjectValue(paramString);
    if (objectValue.valueType != 16)
      throw new IllegalArgumentException("Not an enumeration!"); 
    List list = objectValue.enumeratedValues;
    Object[] arrayOfObject = new Object[list.size()];
    return list.toArray(arrayOfObject);
  }
  
  public Comparable<?> getObjectMinValue(String paramString) {
    ObjectValue objectValue = getObjectValue(paramString);
    if ((objectValue.valueType & 0x2) != 2)
      throw new IllegalArgumentException("Not a range!"); 
    return objectValue.minValue;
  }
  
  public Comparable<?> getObjectMaxValue(String paramString) {
    ObjectValue objectValue = getObjectValue(paramString);
    if ((objectValue.valueType & 0x2) != 2)
      throw new IllegalArgumentException("Not a range!"); 
    return objectValue.maxValue;
  }
  
  public int getObjectArrayMinLength(String paramString) {
    ObjectValue objectValue = getObjectValue(paramString);
    if (objectValue.valueType != 32)
      throw new IllegalArgumentException("Not a list!"); 
    return objectValue.arrayMinLength;
  }
  
  public int getObjectArrayMaxLength(String paramString) {
    ObjectValue objectValue = getObjectValue(paramString);
    if (objectValue.valueType != 32)
      throw new IllegalArgumentException("Not a list!"); 
    return objectValue.arrayMaxLength;
  }
  
  private static void createStandardFormat() {
    if (standardFormat == null)
      standardFormat = new StandardMetadataFormat(); 
  }
  
  public static IIOMetadataFormat getStandardFormatInstance() {
    createStandardFormat();
    return standardFormat;
  }
  
  class Attribute {
    String attrName;
    
    int valueType = 1;
    
    int dataType;
    
    boolean required;
    
    String defaultValue = null;
    
    List enumeratedValues;
    
    String minValue;
    
    String maxValue;
    
    int listMinLength;
    
    int listMaxLength;
  }
  
  class Element {
    String elementName;
    
    int childPolicy;
    
    int minChildren = 0;
    
    int maxChildren = 0;
    
    List childList = new ArrayList();
    
    List parentList = new ArrayList();
    
    List attrList = new ArrayList();
    
    Map attrMap = new HashMap();
    
    IIOMetadataFormatImpl.ObjectValue objectValue;
  }
  
  class ObjectValue {
    int valueType = 0;
    
    Class classType = null;
    
    Object defaultValue = null;
    
    List enumeratedValues = null;
    
    Comparable minValue = null;
    
    Comparable maxValue = null;
    
    int arrayMinLength = 0;
    
    int arrayMaxLength = 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIOMetadataFormatImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */