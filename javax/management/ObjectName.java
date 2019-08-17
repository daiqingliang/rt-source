package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ObjectName extends Object implements Comparable<ObjectName>, QueryExp {
  private static final long oldSerialVersionUID = -5467795090068647408L;
  
  private static final long newSerialVersionUID = 1081892073854801359L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("domain", String.class), new ObjectStreamField("propertyList", Hashtable.class), new ObjectStreamField("propertyListString", String.class), new ObjectStreamField("canonicalName", String.class), new ObjectStreamField("pattern", boolean.class), new ObjectStreamField("propertyPattern", boolean.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[0];
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private static final Property[] _Empty_property_array;
  
  private String _canonicalName;
  
  private Property[] _kp_array;
  
  private Property[] _ca_array;
  
  private int _domain_length = 0;
  
  private Map<String, String> _propertyList;
  
  private boolean _domain_pattern = false;
  
  private boolean _property_list_pattern = false;
  
  private boolean _property_value_pattern = false;
  
  public static final ObjectName WILDCARD;
  
  private void construct(String paramString) throws MalformedObjectNameException {
    if (paramString == null)
      throw new NullPointerException("name cannot be null"); 
    if (paramString.length() == 0) {
      this._canonicalName = "*:*";
      this._kp_array = _Empty_property_array;
      this._ca_array = _Empty_property_array;
      this._domain_length = 1;
      this._propertyList = null;
      this._domain_pattern = true;
      this._property_list_pattern = true;
      this._property_value_pattern = false;
      return;
    } 
    char[] arrayOfChar1 = paramString.toCharArray();
    int i = arrayOfChar1.length;
    char[] arrayOfChar2 = new char[i];
    int j = 0;
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      byte b;
      switch (arrayOfChar1[b1]) {
        case ':':
          this._domain_length = b1++;
          break;
        case '=':
          b = ++b1;
          while (b < i && arrayOfChar1[b++] != ':') {
            if (b == i)
              throw new MalformedObjectNameException("Domain part must be specified"); 
          } 
          continue;
        case '\n':
          throw new MalformedObjectNameException("Invalid character '\\n' in domain name");
        case '*':
        case '?':
          this._domain_pattern = true;
          b1++;
          continue;
      } 
    } 
    if (b1 == i)
      throw new MalformedObjectNameException("Key properties cannot be empty"); 
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, this._domain_length);
    arrayOfChar2[this._domain_length] = ':';
    j = this._domain_length + 1;
    HashMap hashMap = new HashMap();
    byte b2 = 0;
    String[] arrayOfString = new String[10];
    this._kp_array = new Property[10];
    this._property_list_pattern = false;
    this._property_value_pattern = false;
    while (b1 < i) {
      byte b7;
      boolean bool1;
      PatternProperty patternProperty;
      char c1 = arrayOfChar1[b1];
      if (c1 == '*') {
        if (this._property_list_pattern)
          throw new MalformedObjectNameException("Cannot have several '*' characters in pattern property list"); 
        this._property_list_pattern = true;
        if (++b1 < i && arrayOfChar1[b1] != ',')
          throw new MalformedObjectNameException("Invalid character found after '*': end of name or ',' expected"); 
        if (b1 == i) {
          if (!b2) {
            this._kp_array = _Empty_property_array;
            this._ca_array = _Empty_property_array;
            this._propertyList = Collections.emptyMap();
          } 
          break;
        } 
        b1++;
        continue;
      } 
      byte b3 = b1;
      byte b4 = b3;
      if (arrayOfChar1[b3] == '=')
        throw new MalformedObjectNameException("Invalid key (empty)"); 
      char c2;
      while (b3 < i && (c2 = arrayOfChar1[b3++]) != '=') {
        String str1;
        switch (c2) {
          case '\n':
          case '*':
          case ',':
          case ':':
          case '?':
            str1 = (c2 == '\n') ? "\\n" : ("" + c2);
            throw new MalformedObjectNameException("Invalid character '" + str1 + "' in key part of property");
        } 
      } 
      if (arrayOfChar1[b3 - 1] != '=')
        throw new MalformedObjectNameException("Unterminated key property part"); 
      byte b6 = b3;
      byte b5 = b6 - b4 - 1;
      boolean bool2 = false;
      if (b3 < i && arrayOfChar1[b3] == '"') {
        bool1 = true;
        while (++b3 < i && (c2 = arrayOfChar1[b3]) != '"') {
          if (c2 == '\\') {
            if (++b3 == i)
              throw new MalformedObjectNameException("Unterminated quoted value"); 
            switch (c2 = arrayOfChar1[b3]) {
              case '"':
              case '*':
              case '?':
              case '\\':
              case 'n':
                continue;
            } 
            throw new MalformedObjectNameException("Invalid escape sequence '\\" + c2 + "' in quoted value");
          } 
          if (c2 == '\n')
            throw new MalformedObjectNameException("Newline in quoted value"); 
          switch (c2) {
            case '*':
            case '?':
              bool2 = true;
          } 
        } 
        if (b3 == i)
          throw new MalformedObjectNameException("Unterminated quoted value"); 
        b7 = ++b3 - b6;
      } else {
        bool1 = false;
        while (b3 < i && (c2 = arrayOfChar1[b3]) != ',') {
          String str1;
          switch (c2) {
            case '*':
            case '?':
              bool2 = true;
              b3++;
              continue;
            case '\n':
            case '"':
            case ':':
            case '=':
              str1 = (c2 == '\n') ? "\\n" : ("" + c2);
              throw new MalformedObjectNameException("Invalid character '" + str1 + "' in value part of property");
          } 
          b3++;
        } 
        b7 = b3 - b6;
      } 
      if (b3 == i - 1) {
        if (bool1)
          throw new MalformedObjectNameException("Invalid ending character `" + arrayOfChar1[b3] + "'"); 
        throw new MalformedObjectNameException("Invalid ending comma");
      } 
      b3++;
      if (!bool2) {
        patternProperty = new Property(b4, b5, b7);
      } else {
        this._property_value_pattern = true;
        patternProperty = new PatternProperty(b4, b5, b7);
      } 
      String str = paramString.substring(b4, b4 + b5);
      if (b2 == arrayOfString.length) {
        String[] arrayOfString1 = new String[b2 + 10];
        System.arraycopy(arrayOfString, 0, arrayOfString1, 0, b2);
        arrayOfString = arrayOfString1;
      } 
      arrayOfString[b2] = str;
      addProperty(patternProperty, b2, hashMap, str);
      b2++;
      b1 = b3;
    } 
    setCanonicalName(arrayOfChar1, arrayOfChar2, arrayOfString, hashMap, j, b2);
  }
  
  private void construct(String paramString, Map<String, String> paramMap) throws MalformedObjectNameException {
    if (paramString == null)
      throw new NullPointerException("domain cannot be null"); 
    if (paramMap == null)
      throw new NullPointerException("key property list cannot be null"); 
    if (paramMap.isEmpty())
      throw new MalformedObjectNameException("key property list cannot be empty"); 
    if (!isDomain(paramString))
      throw new MalformedObjectNameException("Invalid domain: " + paramString); 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramString).append(':');
    this._domain_length = paramString.length();
    int i = paramMap.size();
    this._kp_array = new Property[i];
    String[] arrayOfString = new String[i];
    HashMap hashMap = new HashMap();
    byte b = 0;
    for (Map.Entry entry : paramMap.entrySet()) {
      String str2;
      PatternProperty patternProperty;
      if (stringBuilder.length() > 0)
        stringBuilder.append(","); 
      String str1 = (String)entry.getKey();
      try {
        str2 = (String)entry.getValue();
      } catch (ClassCastException classCastException) {
        throw new MalformedObjectNameException(classCastException.getMessage());
      } 
      int k = stringBuilder.length();
      checkKey(str1);
      stringBuilder.append(str1);
      arrayOfString[b] = str1;
      stringBuilder.append("=");
      boolean bool = checkValue(str2);
      stringBuilder.append(str2);
      if (!bool) {
        patternProperty = new Property(k, str1.length(), str2.length());
      } else {
        this._property_value_pattern = true;
        patternProperty = new PatternProperty(k, str1.length(), str2.length());
      } 
      addProperty(patternProperty, b, hashMap, str1);
      b++;
    } 
    int j = stringBuilder.length();
    char[] arrayOfChar1 = new char[j];
    stringBuilder.getChars(0, j, arrayOfChar1, 0);
    char[] arrayOfChar2 = new char[j];
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, this._domain_length + 1);
    setCanonicalName(arrayOfChar1, arrayOfChar2, arrayOfString, hashMap, this._domain_length + 1, this._kp_array.length);
  }
  
  private void addProperty(Property paramProperty, int paramInt, Map<String, Property> paramMap, String paramString) throws MalformedObjectNameException {
    if (paramMap.containsKey(paramString))
      throw new MalformedObjectNameException("key `" + paramString + "' already defined"); 
    if (paramInt == this._kp_array.length) {
      Property[] arrayOfProperty = new Property[paramInt + 10];
      System.arraycopy(this._kp_array, 0, arrayOfProperty, 0, paramInt);
      this._kp_array = arrayOfProperty;
    } 
    this._kp_array[paramInt] = paramProperty;
    paramMap.put(paramString, paramProperty);
  }
  
  private void setCanonicalName(char[] paramArrayOfChar1, char[] paramArrayOfChar2, String[] paramArrayOfString, Map<String, Property> paramMap, int paramInt1, int paramInt2) {
    if (this._kp_array != _Empty_property_array) {
      String[] arrayOfString = new String[paramInt2];
      Property[] arrayOfProperty = new Property[paramInt2];
      System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramInt2);
      Arrays.sort(arrayOfString);
      paramArrayOfString = arrayOfString;
      System.arraycopy(this._kp_array, 0, arrayOfProperty, 0, paramInt2);
      this._kp_array = arrayOfProperty;
      this._ca_array = new Property[paramInt2];
      int i;
      for (i = 0; i < paramInt2; i++)
        this._ca_array[i] = (Property)paramMap.get(paramArrayOfString[i]); 
      i = paramInt2 - 1;
      for (byte b = 0; b <= i; b++) {
        Property property = this._ca_array[b];
        int j = property._key_length + property._value_length + 1;
        System.arraycopy(paramArrayOfChar1, property._key_index, paramArrayOfChar2, paramInt1, j);
        property.setKeyIndex(paramInt1);
        paramInt1 += j;
        if (b != i) {
          paramArrayOfChar2[paramInt1] = ',';
          paramInt1++;
        } 
      } 
    } 
    if (this._property_list_pattern) {
      if (this._kp_array != _Empty_property_array)
        paramArrayOfChar2[paramInt1++] = ','; 
      paramArrayOfChar2[paramInt1++] = '*';
    } 
    this._canonicalName = (new String(paramArrayOfChar2, 0, paramInt1)).intern();
  }
  
  private static int parseKey(char[] paramArrayOfChar, int paramInt) throws MalformedObjectNameException {
    int i = paramInt;
    int j = paramInt;
    int k = paramArrayOfChar.length;
    while (i < k) {
      String str;
      char c = paramArrayOfChar[i++];
      switch (c) {
        case '\n':
        case '*':
        case ',':
        case ':':
        case '?':
          str = (c == '\n') ? "\\n" : ("" + c);
          throw new MalformedObjectNameException("Invalid character in key: `" + str + "'");
        case '=':
          j = i - 1;
          break;
      } 
      if (i < k)
        continue; 
      j = i;
    } 
    return j;
  }
  
  private static int[] parseValue(char[] paramArrayOfChar, int paramInt) throws MalformedObjectNameException {
    boolean bool = false;
    int i = paramInt;
    int j = paramInt;
    int k = paramArrayOfChar.length;
    char c = paramArrayOfChar[paramInt];
    if (c == '"') {
      if (++i == k)
        throw new MalformedObjectNameException("Invalid quote"); 
      while (i < k) {
        char c1 = paramArrayOfChar[i];
        if (c1 == '\\') {
          if (++i == k)
            throw new MalformedObjectNameException("Invalid unterminated quoted character sequence"); 
          c1 = paramArrayOfChar[i];
          switch (c1) {
            case '*':
            case '?':
            case '\\':
            case 'n':
              break;
            case '"':
              if (i + 1 == k)
                throw new MalformedObjectNameException("Missing termination quote"); 
              break;
            default:
              throw new MalformedObjectNameException("Invalid quoted character sequence '\\" + c1 + "'");
          } 
        } else {
          if (c1 == '\n')
            throw new MalformedObjectNameException("Newline in quoted value"); 
          if (c1 == '"') {
            i++;
            break;
          } 
          switch (c1) {
            case '*':
            case '?':
              bool = true;
              break;
          } 
        } 
        if (++i >= k && c1 != '"')
          throw new MalformedObjectNameException("Missing termination quote"); 
      } 
      j = i;
      if (i < k && paramArrayOfChar[i++] != ',')
        throw new MalformedObjectNameException("Invalid quote"); 
    } else {
      while (i < k) {
        String str;
        char c1 = paramArrayOfChar[i++];
        switch (c1) {
          case '*':
          case '?':
            bool = true;
            if (i < k)
              continue; 
            j = i;
            break;
          case '\n':
          case ':':
          case '=':
            str = (c1 == '\n') ? "\\n" : ("" + c1);
            throw new MalformedObjectNameException("Invalid character `" + str + "' in value");
          case ',':
            j = i - 1;
            break;
        } 
        if (i < k)
          continue; 
        j = i;
      } 
    } 
    return new int[] { j, bool ? 1 : 0 };
  }
  
  private static boolean checkValue(String paramString) throws MalformedObjectNameException {
    if (paramString == null)
      throw new NullPointerException("Invalid value (null)"); 
    int i = paramString.length();
    if (i == 0)
      return false; 
    char[] arrayOfChar = paramString.toCharArray();
    int[] arrayOfInt = parseValue(arrayOfChar, 0);
    int j = arrayOfInt[0];
    boolean bool = (arrayOfInt[1] == 1);
    if (j < i)
      throw new MalformedObjectNameException("Invalid character in value: `" + arrayOfChar[j] + "'"); 
    return bool;
  }
  
  private static void checkKey(String paramString) throws MalformedObjectNameException {
    if (paramString == null)
      throw new NullPointerException("Invalid key (null)"); 
    int i = paramString.length();
    if (i == 0)
      throw new MalformedObjectNameException("Invalid key (empty)"); 
    char[] arrayOfChar = paramString.toCharArray();
    int j = parseKey(arrayOfChar, 0);
    if (j < i)
      throw new MalformedObjectNameException("Invalid character in value: `" + arrayOfChar[j] + "'"); 
  }
  
  private boolean isDomain(String paramString) throws MalformedObjectNameException {
    if (paramString == null)
      return true; 
    int i = paramString.length();
    byte b = 0;
    while (b < i) {
      char c = paramString.charAt(b++);
      switch (c) {
        case '\n':
        case ':':
          return false;
        case '*':
        case '?':
          this._domain_pattern = true;
      } 
    } 
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    String str;
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      String str1 = (String)getField.get("propertyListString", "");
      boolean bool = getField.get("propertyPattern", false);
      if (bool)
        str1 = (str1.length() == 0) ? "*" : (str1 + ",*"); 
      str = (String)getField.get("domain", "default") + ":" + str1;
    } else {
      paramObjectInputStream.defaultReadObject();
      str = (String)paramObjectInputStream.readObject();
    } 
    try {
      construct(str);
    } catch (NullPointerException nullPointerException) {
      throw new InvalidObjectException(nullPointerException.toString());
    } catch (MalformedObjectNameException malformedObjectNameException) {
      throw new InvalidObjectException(malformedObjectNameException.toString());
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("domain", this._canonicalName.substring(0, this._domain_length));
      putField.put("propertyList", getKeyPropertyList());
      putField.put("propertyListString", getKeyPropertyListString());
      putField.put("canonicalName", this._canonicalName);
      putField.put("pattern", (this._domain_pattern || this._property_list_pattern));
      putField.put("propertyPattern", this._property_list_pattern);
      paramObjectOutputStream.writeFields();
    } else {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeObject(getSerializedNameString());
    } 
  }
  
  public static ObjectName getInstance(String paramString) throws MalformedObjectNameException, NullPointerException { return new ObjectName(paramString); }
  
  public static ObjectName getInstance(String paramString1, String paramString2, String paramString3) throws MalformedObjectNameException { return new ObjectName(paramString1, paramString2, paramString3); }
  
  public static ObjectName getInstance(String paramString, Hashtable<String, String> paramHashtable) throws MalformedObjectNameException { return new ObjectName(paramString, paramHashtable); }
  
  public static ObjectName getInstance(ObjectName paramObjectName) { return paramObjectName.getClass().equals(ObjectName.class) ? paramObjectName : Util.newObjectName(paramObjectName.getSerializedNameString()); }
  
  public ObjectName(String paramString) throws MalformedObjectNameException { construct(paramString); }
  
  public ObjectName(String paramString1, String paramString2, String paramString3) throws MalformedObjectNameException {
    Map map = Collections.singletonMap(paramString2, paramString3);
    construct(paramString1, map);
  }
  
  public ObjectName(String paramString, Hashtable<String, String> paramHashtable) throws MalformedObjectNameException { construct(paramString, paramHashtable); }
  
  public boolean isPattern() { return (this._domain_pattern || this._property_list_pattern || this._property_value_pattern); }
  
  public boolean isDomainPattern() { return this._domain_pattern; }
  
  public boolean isPropertyPattern() { return (this._property_list_pattern || this._property_value_pattern); }
  
  public boolean isPropertyListPattern() { return this._property_list_pattern; }
  
  public boolean isPropertyValuePattern() { return this._property_value_pattern; }
  
  public boolean isPropertyValuePattern(String paramString) throws MalformedObjectNameException {
    if (paramString == null)
      throw new NullPointerException("key property can't be null"); 
    for (byte b = 0; b < this._ca_array.length; b++) {
      Property property = this._ca_array[b];
      String str = property.getKeyString(this._canonicalName);
      if (str.equals(paramString))
        return property instanceof PatternProperty; 
    } 
    throw new IllegalArgumentException("key property not found");
  }
  
  public String getCanonicalName() { return this._canonicalName; }
  
  public String getDomain() { return this._canonicalName.substring(0, this._domain_length); }
  
  public String getKeyProperty(String paramString) { return (String)_getKeyPropertyList().get(paramString); }
  
  private Map<String, String> _getKeyPropertyList() {
    synchronized (this) {
      if (this._propertyList == null) {
        this._propertyList = new HashMap();
        int i = this._ca_array.length;
        for (int j = i - 1; j >= 0; j--) {
          Property property = this._ca_array[j];
          this._propertyList.put(property.getKeyString(this._canonicalName), property.getValueString(this._canonicalName));
        } 
      } 
    } 
    return this._propertyList;
  }
  
  public Hashtable<String, String> getKeyPropertyList() { return new Hashtable(_getKeyPropertyList()); }
  
  public String getKeyPropertyListString() {
    if (this._kp_array.length == 0)
      return ""; 
    int i = this._canonicalName.length() - this._domain_length - 1 - (this._property_list_pattern ? 2 : 0);
    char[] arrayOfChar1 = new char[i];
    char[] arrayOfChar2 = this._canonicalName.toCharArray();
    writeKeyPropertyListString(arrayOfChar2, arrayOfChar1, 0);
    return new String(arrayOfChar1);
  }
  
  private String getSerializedNameString() {
    int i = this._canonicalName.length();
    char[] arrayOfChar1 = new char[i];
    char[] arrayOfChar2 = this._canonicalName.toCharArray();
    int j = this._domain_length + 1;
    System.arraycopy(arrayOfChar2, 0, arrayOfChar1, 0, j);
    int k = writeKeyPropertyListString(arrayOfChar2, arrayOfChar1, j);
    if (this._property_list_pattern)
      if (k == j) {
        arrayOfChar1[k] = '*';
      } else {
        arrayOfChar1[k] = ',';
        arrayOfChar1[k + 1] = '*';
      }  
    return new String(arrayOfChar1);
  }
  
  private int writeKeyPropertyListString(char[] paramArrayOfChar1, char[] paramArrayOfChar2, int paramInt) {
    if (this._kp_array.length == 0)
      return paramInt; 
    char[] arrayOfChar1 = paramArrayOfChar2;
    char[] arrayOfChar2 = paramArrayOfChar1;
    int i = paramInt;
    int j = this._kp_array.length;
    int k = j - 1;
    for (byte b = 0; b < j; b++) {
      Property property = this._kp_array[b];
      int m = property._key_length + property._value_length + 1;
      System.arraycopy(arrayOfChar2, property._key_index, arrayOfChar1, i, m);
      i += m;
      if (b < k)
        arrayOfChar1[i++] = ','; 
    } 
    return i;
  }
  
  public String getCanonicalKeyPropertyListString() {
    if (this._ca_array.length == 0)
      return ""; 
    int i = this._canonicalName.length();
    if (this._property_list_pattern)
      i -= 2; 
    return this._canonicalName.substring(this._domain_length + 1, i);
  }
  
  public String toString() { return getSerializedNameString(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ObjectName))
      return false; 
    ObjectName objectName = (ObjectName)paramObject;
    String str = objectName._canonicalName;
    return (this._canonicalName == str);
  }
  
  public int hashCode() { return this._canonicalName.hashCode(); }
  
  public static String quote(String paramString) {
    StringBuilder stringBuilder = new StringBuilder("\"");
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      switch (c) {
        case '\n':
          c = 'n';
          stringBuilder.append('\\');
          break;
        case '"':
        case '*':
        case '?':
        case '\\':
          stringBuilder.append('\\');
          break;
      } 
      stringBuilder.append(c);
    } 
    stringBuilder.append('"');
    return stringBuilder.toString();
  }
  
  public static String unquote(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = paramString.length();
    if (i < 2 || paramString.charAt(0) != '"' || paramString.charAt(i - 1) != '"')
      throw new IllegalArgumentException("Argument not quoted"); 
    for (byte b = 1; b < i - 1; b++) {
      char c = paramString.charAt(b);
      if (c == '\\') {
        if (b == i - 2)
          throw new IllegalArgumentException("Trailing backslash"); 
        c = paramString.charAt(++b);
        switch (c) {
          case 'n':
            c = '\n';
            break;
          case '"':
          case '*':
          case '?':
          case '\\':
            break;
          default:
            throw new IllegalArgumentException("Bad character '" + c + "' after backslash");
        } 
      } else {
        switch (c) {
          case '\n':
          case '"':
          case '*':
          case '?':
            throw new IllegalArgumentException("Invalid unescaped character '" + c + "' in the string to unquote");
        } 
      } 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  public boolean apply(ObjectName paramObjectName) {
    if (paramObjectName == null)
      throw new NullPointerException(); 
    return (paramObjectName._domain_pattern || paramObjectName._property_list_pattern || paramObjectName._property_value_pattern) ? false : ((!this._domain_pattern && !this._property_list_pattern && !this._property_value_pattern) ? this._canonicalName.equals(paramObjectName._canonicalName) : ((matchDomains(paramObjectName) && matchKeys(paramObjectName)) ? 1 : 0));
  }
  
  private final boolean matchDomains(ObjectName paramObjectName) { return this._domain_pattern ? Util.wildmatch(paramObjectName.getDomain(), getDomain()) : getDomain().equals(paramObjectName.getDomain()); }
  
  private final boolean matchKeys(ObjectName paramObjectName) {
    if (this._property_value_pattern && !this._property_list_pattern && paramObjectName._ca_array.length != this._ca_array.length)
      return false; 
    if (this._property_value_pattern || this._property_list_pattern) {
      Map map = paramObjectName._getKeyPropertyList();
      Property[] arrayOfProperty = this._ca_array;
      String str = this._canonicalName;
      for (int i = arrayOfProperty.length - 1; i >= 0; i--) {
        Property property = arrayOfProperty[i];
        String str3 = property.getKeyString(str);
        String str4 = (String)map.get(str3);
        if (str4 == null)
          return false; 
        if (this._property_value_pattern && property instanceof PatternProperty) {
          if (!Util.wildmatch(str4, property.getValueString(str)))
            return false; 
        } else if (!str4.equals(property.getValueString(str))) {
          return false;
        } 
      } 
      return true;
    } 
    String str1 = paramObjectName.getCanonicalKeyPropertyListString();
    String str2 = getCanonicalKeyPropertyListString();
    return str1.equals(str2);
  }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) {}
  
  public int compareTo(ObjectName paramObjectName) {
    if (paramObjectName == this)
      return 0; 
    int i = getDomain().compareTo(paramObjectName.getDomain());
    if (i != 0)
      return i; 
    String str1 = getKeyProperty("type");
    String str2 = paramObjectName.getKeyProperty("type");
    if (str1 == null)
      str1 = ""; 
    if (str2 == null)
      str2 = ""; 
    int j = str1.compareTo(str2);
    return (j != 0) ? j : getCanonicalName().compareTo(paramObjectName.getCanonicalName());
  }
  
  static  {
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      compat = (str != null && str.equals("1.0"));
    } catch (Exception exception) {}
    if (compat) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = -5467795090068647408L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 1081892073854801359L;
    } 
    _Empty_property_array = new Property[0];
    WILDCARD = Util.newObjectName("*:*");
  }
  
  private static class PatternProperty extends Property {
    PatternProperty(int param1Int1, int param1Int2, int param1Int3) { super(param1Int1, param1Int2, param1Int3); }
  }
  
  private static class Property {
    int _key_index;
    
    int _key_length;
    
    int _value_length;
    
    Property(int param1Int1, int param1Int2, int param1Int3) {
      this._key_index = param1Int1;
      this._key_length = param1Int2;
      this._value_length = param1Int3;
    }
    
    void setKeyIndex(int param1Int) { this._key_index = param1Int; }
    
    String getKeyString(String param1String) { return param1String.substring(this._key_index, this._key_index + this._key_length); }
    
    String getValueString(String param1String) {
      int i = this._key_index + this._key_length + 1;
      int j = i + this._value_length;
      return param1String.substring(i, j);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\ObjectName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */