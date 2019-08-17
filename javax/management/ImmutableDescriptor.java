package javax.management;

import com.sun.jmx.mbeanserver.Util;
import java.io.InvalidObjectException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ImmutableDescriptor implements Descriptor {
  private static final long serialVersionUID = 8853308591080540165L;
  
  private final String[] names;
  
  private final Object[] values;
  
  private int hashCode = -1;
  
  public static final ImmutableDescriptor EMPTY_DESCRIPTOR = new ImmutableDescriptor(new String[0]);
  
  public ImmutableDescriptor(String[] paramArrayOfString, Object[] paramArrayOfObject) { this(makeMap(paramArrayOfString, paramArrayOfObject)); }
  
  public ImmutableDescriptor(String... paramVarArgs) { this(makeMap(paramVarArgs)); }
  
  public ImmutableDescriptor(Map<String, ?> paramMap) {
    if (paramMap == null)
      throw new IllegalArgumentException("Null Map"); 
    TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    for (Map.Entry entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      if (str == null || str.equals(""))
        throw new IllegalArgumentException("Empty or null field name"); 
      if (treeMap.containsKey(str))
        throw new IllegalArgumentException("Duplicate name: " + str); 
      treeMap.put(str, entry.getValue());
    } 
    int i = treeMap.size();
    this.names = (String[])treeMap.keySet().toArray(new String[i]);
    this.values = treeMap.values().toArray(new Object[i]);
  }
  
  private Object readResolve() throws InvalidObjectException {
    boolean bool = false;
    if (this.names == null || this.values == null || this.names.length != this.values.length)
      bool = true; 
    if (!bool) {
      if (this.names.length == 0 && getClass() == ImmutableDescriptor.class)
        return EMPTY_DESCRIPTOR; 
      Comparator comparator = String.CASE_INSENSITIVE_ORDER;
      String str = "";
      for (byte b = 0; b < this.names.length; b++) {
        if (this.names[b] == null || comparator.compare(str, this.names[b]) >= 0) {
          bool = true;
          break;
        } 
        str = this.names[b];
      } 
    } 
    if (bool)
      throw new InvalidObjectException("Bad names or values"); 
    return this;
  }
  
  private static SortedMap<String, ?> makeMap(String[] paramArrayOfString, Object[] paramArrayOfObject) {
    if (paramArrayOfString == null || paramArrayOfObject == null)
      throw new IllegalArgumentException("Null array parameter"); 
    if (paramArrayOfString.length != paramArrayOfObject.length)
      throw new IllegalArgumentException("Different size arrays"); 
    TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      if (str == null || str.equals(""))
        throw new IllegalArgumentException("Empty or null field name"); 
      Object object = treeMap.put(str, paramArrayOfObject[b]);
      if (object != null)
        throw new IllegalArgumentException("Duplicate field name: " + str); 
    } 
    return treeMap;
  }
  
  private static SortedMap<String, ?> makeMap(String[] paramArrayOfString) {
    if (paramArrayOfString == null)
      throw new IllegalArgumentException("Null fields parameter"); 
    String[] arrayOfString1 = new String[paramArrayOfString.length];
    String[] arrayOfString2 = new String[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      int i = str.indexOf('=');
      if (i < 0)
        throw new IllegalArgumentException("Missing = character: " + str); 
      arrayOfString1[b] = str.substring(0, i);
      arrayOfString2[b] = str.substring(i + 1);
    } 
    return makeMap(arrayOfString1, arrayOfString2);
  }
  
  public static ImmutableDescriptor union(Descriptor... paramVarArgs) {
    int i = findNonEmpty(paramVarArgs, 0);
    if (i < 0)
      return EMPTY_DESCRIPTOR; 
    if (paramVarArgs[i] instanceof ImmutableDescriptor && findNonEmpty(paramVarArgs, i + 1) < 0)
      return (ImmutableDescriptor)paramVarArgs[i]; 
    TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    ImmutableDescriptor immutableDescriptor = EMPTY_DESCRIPTOR;
    for (Descriptor descriptor : paramVarArgs) {
      if (descriptor != null) {
        String[] arrayOfString;
        if (descriptor instanceof ImmutableDescriptor) {
          ImmutableDescriptor immutableDescriptor1 = (ImmutableDescriptor)descriptor;
          arrayOfString = immutableDescriptor1.names;
          if (immutableDescriptor1.getClass() == ImmutableDescriptor.class && arrayOfString.length > immutableDescriptor.names.length)
            immutableDescriptor = immutableDescriptor1; 
        } else {
          arrayOfString = descriptor.getFieldNames();
        } 
        for (String str : arrayOfString) {
          Object object1 = descriptor.getFieldValue(str);
          Object object2 = treeMap.put(str, object1);
          if (object2 != null) {
            boolean bool;
            if (object2.getClass().isArray()) {
              bool = Arrays.deepEquals(new Object[] { object2 }, new Object[] { object1 });
            } else {
              bool = object2.equals(object1);
            } 
            if (!bool) {
              String str1 = "Inconsistent values for descriptor field " + str + ": " + object2 + " :: " + object1;
              throw new IllegalArgumentException(str1);
            } 
          } 
        } 
      } 
    } 
    return (immutableDescriptor.names.length == treeMap.size()) ? immutableDescriptor : new ImmutableDescriptor(treeMap);
  }
  
  private static boolean isEmpty(Descriptor paramDescriptor) { return (paramDescriptor == null) ? true : ((paramDescriptor instanceof ImmutableDescriptor) ? ((((ImmutableDescriptor)paramDescriptor).names.length == 0)) : ((paramDescriptor.getFieldNames().length == 0))); }
  
  private static int findNonEmpty(Descriptor[] paramArrayOfDescriptor, int paramInt) {
    for (int i = paramInt; i < paramArrayOfDescriptor.length; i++) {
      if (!isEmpty(paramArrayOfDescriptor[i]))
        return i; 
    } 
    return -1;
  }
  
  private int fieldIndex(String paramString) { return Arrays.binarySearch(this.names, paramString, String.CASE_INSENSITIVE_ORDER); }
  
  public final Object getFieldValue(String paramString) {
    checkIllegalFieldName(paramString);
    int i = fieldIndex(paramString);
    if (i < 0)
      return null; 
    Object object1 = this.values[i];
    if (object1 == null || !object1.getClass().isArray())
      return object1; 
    if (object1 instanceof Object[])
      return ((Object[])object1).clone(); 
    int j = Array.getLength(object1);
    Object object2 = Array.newInstance(object1.getClass().getComponentType(), j);
    System.arraycopy(object1, 0, object2, 0, j);
    return object2;
  }
  
  public final String[] getFields() {
    String[] arrayOfString = new String[this.names.length];
    for (byte b = 0; b < arrayOfString.length; b++) {
      Object object = this.values[b];
      if (object == null) {
        object = "";
      } else if (!(object instanceof String)) {
        object = "(" + object + ")";
      } 
      arrayOfString[b] = this.names[b] + "=" + object;
    } 
    return arrayOfString;
  }
  
  public final Object[] getFieldValues(String... paramVarArgs) {
    if (paramVarArgs == null)
      return (Object[])this.values.clone(); 
    Object[] arrayOfObject = new Object[paramVarArgs.length];
    for (byte b = 0; b < paramVarArgs.length; b++) {
      String str = paramVarArgs[b];
      if (str != null && !str.equals(""))
        arrayOfObject[b] = getFieldValue(str); 
    } 
    return arrayOfObject;
  }
  
  public final String[] getFieldNames() { return (String[])this.names.clone(); }
  
  public boolean equals(Object paramObject) {
    Object[] arrayOfObject;
    String[] arrayOfString;
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Descriptor))
      return false; 
    if (paramObject instanceof ImmutableDescriptor) {
      arrayOfString = ((ImmutableDescriptor)paramObject).names;
    } else {
      arrayOfString = ((Descriptor)paramObject).getFieldNames();
      Arrays.sort(arrayOfString, String.CASE_INSENSITIVE_ORDER);
    } 
    if (this.names.length != arrayOfString.length)
      return false; 
    for (byte b = 0; b < this.names.length; b++) {
      if (!this.names[b].equalsIgnoreCase(arrayOfString[b]))
        return false; 
    } 
    if (paramObject instanceof ImmutableDescriptor) {
      arrayOfObject = ((ImmutableDescriptor)paramObject).values;
    } else {
      arrayOfObject = ((Descriptor)paramObject).getFieldValues(arrayOfString);
    } 
    return Arrays.deepEquals(this.values, arrayOfObject);
  }
  
  public int hashCode() {
    if (this.hashCode == -1)
      this.hashCode = Util.hashCode(this.names, this.values); 
    return this.hashCode;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("{");
    for (byte b = 0; b < this.names.length; b++) {
      if (b)
        stringBuilder.append(", "); 
      stringBuilder.append(this.names[b]).append("=");
      Object object = this.values[b];
      if (object != null && object.getClass().isArray()) {
        String str = Arrays.deepToString(new Object[] { object });
        str = str.substring(1, str.length() - 1);
        object = str;
      } 
      stringBuilder.append(String.valueOf(object));
    } 
    return stringBuilder.append("}").toString();
  }
  
  public boolean isValid() { return true; }
  
  public Descriptor clone() { return this; }
  
  public final void setFields(String[] paramArrayOfString, Object[] paramArrayOfObject) {
    if (paramArrayOfString == null || paramArrayOfObject == null)
      illegal("Null argument"); 
    if (paramArrayOfString.length != paramArrayOfObject.length)
      illegal("Different array sizes"); 
    byte b;
    for (b = 0; b < paramArrayOfString.length; b++)
      checkIllegalFieldName(paramArrayOfString[b]); 
    for (b = 0; b < paramArrayOfString.length; b++)
      setField(paramArrayOfString[b], paramArrayOfObject[b]); 
  }
  
  public final void setField(String paramString, Object paramObject) throws RuntimeOperationsException {
    checkIllegalFieldName(paramString);
    int i = fieldIndex(paramString);
    if (i < 0)
      unsupported(); 
    Object object = this.values[i];
    if ((object == null) ? (paramObject != null) : !object.equals(paramObject))
      unsupported(); 
  }
  
  public final void removeField(String paramString) {
    if (paramString != null && fieldIndex(paramString) >= 0)
      unsupported(); 
  }
  
  static Descriptor nonNullDescriptor(Descriptor paramDescriptor) { return (paramDescriptor == null) ? EMPTY_DESCRIPTOR : paramDescriptor; }
  
  private static void checkIllegalFieldName(String paramString) {
    if (paramString == null || paramString.equals(""))
      illegal("Null or empty field name"); 
  }
  
  private static void unsupported() {
    UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("Descriptor is read-only");
    throw new RuntimeOperationsException(unsupportedOperationException);
  }
  
  private static void illegal(String paramString) {
    IllegalArgumentException illegalArgumentException = new IllegalArgumentException(paramString);
    throw new RuntimeOperationsException(illegalArgumentException);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\ImmutableDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */