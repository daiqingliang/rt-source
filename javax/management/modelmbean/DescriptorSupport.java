package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import sun.reflect.misc.ReflectUtil;

public class DescriptorSupport implements Descriptor {
  private static final long oldSerialVersionUID = 8071560848919417985L;
  
  private static final long newSerialVersionUID = -6292969195866300415L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("descriptor", HashMap.class), new ObjectStreamField("currClass", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("descriptor", HashMap.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static final String serialForm;
  
  private SortedMap<String, Object> descriptorMap;
  
  private static final String currClass = "DescriptorSupport";
  
  private static final String[] entities;
  
  private static final Map<String, Character> entityToCharMap;
  
  private static final String[] charToEntityMap;
  
  public DescriptorSupport() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "DescriptorSupport()", "Constructor"); 
    init(null);
  }
  
  public DescriptorSupport(int paramInt) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields = " + paramInt + ")", "Constructor"); 
    if (paramInt <= 0) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(initNumFields)", "Illegal arguments: initNumFields <= 0"); 
      String str = "Descriptor field limit invalid: " + paramInt;
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException(str);
      throw new RuntimeOperationsException(illegalArgumentException, str);
    } 
    init(null);
  }
  
  public DescriptorSupport(DescriptorSupport paramDescriptorSupport) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(Descriptor)", "Constructor"); 
    if (paramDescriptorSupport == null) {
      init(null);
    } else {
      init(paramDescriptorSupport.descriptorMap);
    } 
  }
  
  public DescriptorSupport(String paramString) throws MBeanException, RuntimeOperationsException, XMLParseException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = '" + paramString + "')", "Constructor"); 
    if (paramString == null) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String = null)", "Illegal arguments"); 
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("String in parameter is null");
      throw new RuntimeOperationsException(illegalArgumentException, "String in parameter is null");
    } 
    String str1 = paramString.toLowerCase();
    if (!str1.startsWith("<descriptor>") || !str1.endsWith("</descriptor>"))
      throw new XMLParseException("No <descriptor>, </descriptor> pair"); 
    init(null);
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "<> \t\n\r\f");
    boolean bool1 = false;
    boolean bool2 = false;
    String str2 = null;
    String str3 = null;
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      if (str.equalsIgnoreCase("FIELD")) {
        bool1 = true;
        continue;
      } 
      if (str.equalsIgnoreCase("/FIELD")) {
        if (str2 != null && str3 != null) {
          str2 = str2.substring(str2.indexOf('"') + 1, str2.lastIndexOf('"'));
          Object object = parseQuotedFieldValue(str3);
          setField(str2, object);
        } 
        str2 = null;
        str3 = null;
        bool1 = false;
        continue;
      } 
      if (str.equalsIgnoreCase("DESCRIPTOR")) {
        bool2 = true;
        continue;
      } 
      if (str.equalsIgnoreCase("/DESCRIPTOR")) {
        bool2 = false;
        str2 = null;
        str3 = null;
        bool1 = false;
        continue;
      } 
      if (bool1 && bool2) {
        int i = str.indexOf("=");
        if (i > 0) {
          String str5 = str.substring(0, i);
          String str6 = str.substring(i + 1);
          if (str5.equalsIgnoreCase("NAME")) {
            str2 = str6;
            continue;
          } 
          if (str5.equalsIgnoreCase("VALUE")) {
            str3 = str6;
            continue;
          } 
          String str7 = "Expected `name' or `value', got `" + str + "'";
          throw new XMLParseException(str7);
        } 
        String str4 = "Expected `keyword=value', got `" + str + "'";
        throw new XMLParseException(str4);
      } 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(XMLString)", "Exit"); 
  }
  
  public DescriptorSupport(String[] paramArrayOfString, Object[] paramArrayOfObject) throws RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Constructor"); 
    if (paramArrayOfString == null || paramArrayOfObject == null || paramArrayOfString.length != paramArrayOfObject.length) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Illegal arguments"); 
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Null or invalid fieldNames or fieldValues");
      throw new RuntimeOperationsException(illegalArgumentException, "Null or invalid fieldNames or fieldValues");
    } 
    init(null);
    for (byte b = 0; b < paramArrayOfString.length; b++)
      setField(paramArrayOfString[b], paramArrayOfObject[b]); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(fieldNames,fieldObjects)", "Exit"); 
  }
  
  public DescriptorSupport(String... paramVarArgs) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Constructor"); 
    init(null);
    if (paramVarArgs == null || paramVarArgs.length == 0)
      return; 
    init(null);
    for (byte b = 0; b < paramVarArgs.length; b++) {
      if (paramVarArgs[b] != null && !paramVarArgs[b].equals("")) {
        int i = paramVarArgs[b].indexOf("=");
        if (i < 0) {
          if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: field does not have '=' as a name and value separator"); 
          IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Field in invalid format: no equals sign");
          throw new RuntimeOperationsException(illegalArgumentException, "Field in invalid format: no equals sign");
        } 
        String str1 = paramVarArgs[b].substring(0, i);
        String str2 = null;
        if (i < paramVarArgs[b].length())
          str2 = paramVarArgs[b].substring(i + 1); 
        if (str1.equals("")) {
          if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Illegal arguments: fieldName is empty"); 
          IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Field in invalid format: no fieldName");
          throw new RuntimeOperationsException(illegalArgumentException, "Field in invalid format: no fieldName");
        } 
        setField(str1, str2);
      } 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "Descriptor(String... fields)", "Exit"); 
  }
  
  private void init(Map<String, ?> paramMap) {
    this.descriptorMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    if (paramMap != null)
      this.descriptorMap.putAll(paramMap); 
  }
  
  public Object getFieldValue(String paramString) throws RuntimeOperationsException {
    if (paramString == null || paramString.equals("")) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName)", "Illegal arguments: null field name"); 
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Fieldname requested is null");
      throw new RuntimeOperationsException(illegalArgumentException, "Fieldname requested is null");
    } 
    Object object = this.descriptorMap.get(paramString);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValue(String fieldName = " + paramString + ")", "Returns '" + object + "'"); 
    return object;
  }
  
  public void setField(String paramString, Object paramObject) throws RuntimeOperationsException {
    if (paramString == null || paramString.equals("")) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments: null or empty field name"); 
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Field name to be set is null or empty");
      throw new RuntimeOperationsException(illegalArgumentException, "Field name to be set is null or empty");
    } 
    if (!validateField(paramString, paramObject)) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Illegal arguments"); 
      String str = "Field value invalid: " + paramString + "=" + paramObject;
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException(str);
      throw new RuntimeOperationsException(illegalArgumentException, str);
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setField(fieldName,fieldValue)", "Entry: setting '" + paramString + "' to '" + paramObject + "'"); 
    this.descriptorMap.put(paramString, paramObject);
  }
  
  public String[] getFields() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Entry"); 
    int i = this.descriptorMap.size();
    String[] arrayOfString = new String[i];
    Set set = this.descriptorMap.entrySet();
    byte b = 0;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Returning " + i + " fields"); 
    for (Map.Entry entry : set) {
      if (entry == null) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Element is null"); 
      } else {
        Object object = entry.getValue();
        if (object == null) {
          arrayOfString[b] = (String)entry.getKey() + "=";
        } else if (object instanceof String) {
          arrayOfString[b] = (String)entry.getKey() + "=" + object.toString();
        } else {
          arrayOfString[b] = (String)entry.getKey() + "=(" + object.toString() + ")";
        } 
      } 
      b++;
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFields()", "Exit"); 
    return arrayOfString;
  }
  
  public String[] getFieldNames() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Entry"); 
    int i = this.descriptorMap.size();
    String[] arrayOfString = new String[i];
    Set set = this.descriptorMap.entrySet();
    byte b = 0;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Returning " + i + " fields"); 
    for (Map.Entry entry : set) {
      if (entry == null || entry.getKey() == null) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Field is null"); 
      } else {
        arrayOfString[b] = ((String)entry.getKey()).toString();
      } 
      b++;
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldNames()", "Exit"); 
    return arrayOfString;
  }
  
  public Object[] getFieldValues(String... paramVarArgs) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Entry"); 
    int i = (paramVarArgs == null) ? this.descriptorMap.size() : paramVarArgs.length;
    Object[] arrayOfObject = new Object[i];
    byte b = 0;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Returning " + i + " fields"); 
    if (paramVarArgs == null) {
      for (Object object : this.descriptorMap.values())
        arrayOfObject[b++] = object; 
    } else {
      for (b = 0; b < paramVarArgs.length; b++) {
        if (paramVarArgs[b] == null || paramVarArgs[b].equals("")) {
          arrayOfObject[b] = null;
        } else {
          arrayOfObject[b] = getFieldValue(paramVarArgs[b]);
        } 
      } 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "getFieldValues(String... fieldNames)", "Exit"); 
    return arrayOfObject;
  }
  
  public void setFields(String[] paramArrayOfString, Object[] paramArrayOfObject) throws RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Entry"); 
    if (paramArrayOfString == null || paramArrayOfObject == null || paramArrayOfString.length != paramArrayOfObject.length) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Illegal arguments"); 
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("fieldNames and fieldValues are null or invalid");
      throw new RuntimeOperationsException(illegalArgumentException, "fieldNames and fieldValues are null or invalid");
    } 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] == null || paramArrayOfString[b].equals("")) {
        if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Null field name encountered at element " + b); 
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("fieldNames is null or invalid");
        throw new RuntimeOperationsException(illegalArgumentException, "fieldNames is null or invalid");
      } 
      setField(paramArrayOfString[b], paramArrayOfObject[b]);
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "setFields(fieldNames,fieldValues)", "Exit"); 
  }
  
  public Object clone() throws RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "clone()", "Entry"); 
    return new DescriptorSupport(this);
  }
  
  public void removeField(String paramString) throws MBeanException, RuntimeOperationsException, XMLParseException {
    if (paramString == null || paramString.equals(""))
      return; 
    this.descriptorMap.remove(paramString);
  }
  
  public boolean equals(Object paramObject) { return (paramObject == this) ? true : (!(paramObject instanceof Descriptor) ? false : ((paramObject instanceof ImmutableDescriptor) ? paramObject.equals(this) : (new ImmutableDescriptor(this.descriptorMap)).equals(paramObject))); }
  
  public int hashCode() {
    int i = this.descriptorMap.size();
    return Util.hashCode((String[])this.descriptorMap.keySet().toArray(new String[i]), this.descriptorMap.values().toArray(new Object[i]));
  }
  
  public boolean isValid() throws RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Entry"); 
    Set set = this.descriptorMap.entrySet();
    if (set == null) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns false (null set)"); 
      return false;
    } 
    String str1 = (String)getFieldValue("name");
    String str2 = (String)getFieldValue("descriptorType");
    if (str1 == null || str2 == null || str1.equals("") || str2.equals(""))
      return false; 
    for (Map.Entry entry : set) {
      if (entry == null || entry.getValue() == null || validateField(((String)entry.getKey()).toString(), entry.getValue().toString()))
        continue; 
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Field " + (String)entry.getKey() + "=" + entry.getValue() + " is not valid"); 
      return false;
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "isValid()", "Returns true"); 
    return true;
  }
  
  private boolean validateField(String paramString, Object paramObject) {
    if (paramString == null || paramString.equals(""))
      return false; 
    String str = "";
    boolean bool1 = false;
    if (paramObject != null && paramObject instanceof String) {
      str = (String)paramObject;
      bool1 = true;
    } 
    boolean bool2 = (paramString.equalsIgnoreCase("Name") || paramString.equalsIgnoreCase("DescriptorType")) ? 1 : 0;
    if (bool2 || paramString.equalsIgnoreCase("SetMethod") || paramString.equalsIgnoreCase("GetMethod") || paramString.equalsIgnoreCase("Role") || paramString.equalsIgnoreCase("Class"))
      return (paramObject == null || !bool1) ? false : (!(bool2 && str.equals(""))); 
    if (paramString.equalsIgnoreCase("visibility")) {
      long l;
      if (paramObject != null && bool1) {
        l = toNumeric(str);
      } else if (paramObject instanceof Integer) {
        l = ((Integer)paramObject).intValue();
      } else {
        return false;
      } 
      return (l >= 1L && l <= 4L);
    } 
    if (paramString.equalsIgnoreCase("severity")) {
      long l;
      if (paramObject != null && bool1) {
        l = toNumeric(str);
      } else if (paramObject instanceof Integer) {
        l = ((Integer)paramObject).intValue();
      } else {
        return false;
      } 
      return (l >= 0L && l <= 6L);
    } 
    if (paramString.equalsIgnoreCase("PersistPolicy"))
      return (paramObject != null && bool1 && (str.equalsIgnoreCase("OnUpdate") || str.equalsIgnoreCase("OnTimer") || str.equalsIgnoreCase("NoMoreOftenThan") || str.equalsIgnoreCase("Always") || str.equalsIgnoreCase("Never") || str.equalsIgnoreCase("OnUnregister"))); 
    if (paramString.equalsIgnoreCase("PersistPeriod") || paramString.equalsIgnoreCase("CurrencyTimeLimit") || paramString.equalsIgnoreCase("LastUpdatedTimeStamp") || paramString.equalsIgnoreCase("LastReturnedTimeStamp")) {
      long l;
      if (paramObject != null && bool1) {
        l = toNumeric(str);
      } else if (paramObject instanceof Number) {
        l = ((Number)paramObject).longValue();
      } else {
        return false;
      } 
      return (l >= -1L);
    } 
    return paramString.equalsIgnoreCase("log") ? ((paramObject instanceof Boolean || (bool1 && (str.equalsIgnoreCase("T") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("F") || str.equalsIgnoreCase("false"))))) : true;
  }
  
  public String toXMLString() {
    StringBuilder stringBuilder = new StringBuilder("<Descriptor>");
    Set set = this.descriptorMap.entrySet();
    for (Map.Entry entry : set) {
      String str1 = (String)entry.getKey();
      Object object = entry.getValue();
      String str2 = null;
      if (object instanceof String) {
        String str = (String)object;
        if (!str.startsWith("(") || !str.endsWith(")"))
          str2 = quote(str); 
      } 
      if (str2 == null)
        str2 = makeFieldValue(object); 
      stringBuilder.append("<field name=\"").append(str1).append("\" value=\"").append(str2).append("\"></field>");
    } 
    stringBuilder.append("</Descriptor>");
    return stringBuilder.toString();
  }
  
  private static boolean isMagic(char paramChar) { return (paramChar < charToEntityMap.length && charToEntityMap[paramChar] != null); }
  
  private static String quote(String paramString) {
    boolean bool = false;
    for (byte b1 = 0; b1 < paramString.length(); b1++) {
      if (isMagic(paramString.charAt(b1))) {
        bool = true;
        break;
      } 
    } 
    if (!bool)
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b2 = 0; b2 < paramString.length(); b2++) {
      char c = paramString.charAt(b2);
      if (isMagic(c)) {
        stringBuilder.append(charToEntityMap[c]);
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringBuilder.toString();
  }
  
  private static String unquote(String paramString) {
    if (!paramString.startsWith("\"") || !paramString.endsWith("\""))
      throw new XMLParseException("Value must be quoted: <" + paramString + ">"); 
    StringBuilder stringBuilder = new StringBuilder();
    int i = paramString.length() - 1;
    for (int j = 1; j < i; j++) {
      char c = paramString.charAt(j);
      int k;
      Character character;
      if (c == '&' && (k = paramString.indexOf(';', j + 1)) >= 0 && (character = (Character)entityToCharMap.get(paramString.substring(j, k + true))) != null) {
        stringBuilder.append(character);
        j = k;
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringBuilder.toString();
  }
  
  private static String makeFieldValue(Object paramObject) {
    if (paramObject == null)
      return "(null)"; 
    Class clazz = paramObject.getClass();
    try {
      clazz.getConstructor(new Class[] { String.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      String str1 = "Class " + clazz + " does not have a public constructor with a single string arg";
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException(str1);
      throw new RuntimeOperationsException(illegalArgumentException, "Cannot make XML descriptor");
    } catch (SecurityException securityException) {}
    String str = quote(paramObject.toString());
    return "(" + clazz.getName() + "/" + str + ")";
  }
  
  private static Object parseQuotedFieldValue(String paramString) throws RuntimeOperationsException {
    Constructor constructor;
    paramString = unquote(paramString);
    if (paramString.equalsIgnoreCase("(null)"))
      return null; 
    if (!paramString.startsWith("(") || !paramString.endsWith(")"))
      return paramString; 
    int i = paramString.indexOf('/');
    if (i < 0)
      return paramString.substring(1, paramString.length() - 1); 
    String str1 = paramString.substring(1, i);
    try {
      ReflectUtil.checkPackageAccess(str1);
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class clazz = Class.forName(str1, false, classLoader);
      constructor = clazz.getConstructor(new Class[] { String.class });
    } catch (Exception exception) {
      throw new XMLParseException(exception, "Cannot parse value: <" + paramString + ">");
    } 
    String str2 = paramString.substring(i + 1, paramString.length() - 1);
    try {
      return constructor.newInstance(new Object[] { str2 });
    } catch (Exception exception) {
      String str = "Cannot construct instance of " + str1 + " with arg: <" + paramString + ">";
      throw new XMLParseException(exception, str);
    } 
  }
  
  public String toString() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Entry"); 
    String str = "";
    String[] arrayOfString = getFields();
    if (arrayOfString == null || arrayOfString.length == 0) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Empty Descriptor"); 
      return str;
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Printing " + arrayOfString.length + " fields"); 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (b == arrayOfString.length - 1) {
        str = str.concat(arrayOfString[b]);
      } else {
        str = str.concat(arrayOfString[b] + ", ");
      } 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINEST, DescriptorSupport.class.getName(), "toString()", "Exit returning " + str); 
    return str;
  }
  
  private long toNumeric(String paramString) {
    try {
      return Long.parseLong(paramString);
    } catch (Exception exception) {
      return -2L;
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Map map = (Map)Util.cast(getField.get("descriptor", null));
    init(null);
    if (map != null)
      this.descriptorMap.putAll(map); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    HashMap hashMap;
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    boolean bool = "1.0".equals(serialForm);
    if (bool)
      putField.put("currClass", "DescriptorSupport"); 
    SortedMap sortedMap = this.descriptorMap;
    if (sortedMap.containsKey("targetObject")) {
      sortedMap = new TreeMap(this.descriptorMap);
      sortedMap.remove("targetObject");
    } 
    if (bool || "1.2.0".equals(serialForm) || "1.2.1".equals(serialForm)) {
      hashMap = new HashMap();
      for (Map.Entry entry : sortedMap.entrySet())
        hashMap.put(((String)entry.getKey()).toLowerCase(), entry.getValue()); 
    } else {
      hashMap = new HashMap(sortedMap);
    } 
    putField.put("descriptor", hashMap);
    paramObjectOutputStream.writeFields();
  }
  
  static  {
    String str = null;
    int i = 0;
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      str = (String)AccessController.doPrivileged(getPropertyAction);
      i = "1.0".equals(str);
    } catch (Exception exception) {}
    serialForm = str;
    if (i) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 8071560848919417985L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -6292969195866300415L;
    } 
    entities = new String[] { " &#32;", "\"&quot;", "<&lt;", ">&gt;", "&&amp;", "\r&#13;", "\t&#9;", "\n&#10;", "\f&#12;" };
    entityToCharMap = new HashMap();
    char c = Character.MIN_VALUE;
    for (i = 0; i < entities.length; i++) {
      char c1 = entities[i].charAt(0);
      if (c1 > c)
        c = c1; 
    } 
    charToEntityMap = new String[c + '\001'];
    for (byte b = 0; b < entities.length; b++) {
      char c1 = entities[b].charAt(0);
      String str1 = entities[b].substring(1);
      charToEntityMap[c1] = str1;
      entityToCharMap.put(str1, Character.valueOf(c1));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\DescriptorSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */