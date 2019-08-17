package javax.management.openmbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabularType extends OpenType<TabularData> {
  static final long serialVersionUID = 6554071860220659261L;
  
  private CompositeType rowType;
  
  private List<String> indexNames;
  
  private Integer myHashCode = null;
  
  private String myToString = null;
  
  public TabularType(String paramString1, String paramString2, CompositeType paramCompositeType, String[] paramArrayOfString) throws OpenDataException {
    super(TabularData.class.getName(), paramString1, paramString2, false);
    if (paramCompositeType == null)
      throw new IllegalArgumentException("Argument rowType cannot be null."); 
    checkForNullElement(paramArrayOfString, "indexNames");
    checkForEmptyString(paramArrayOfString, "indexNames");
    for (byte b1 = 0; b1 < paramArrayOfString.length; b1++) {
      if (!paramCompositeType.containsKey(paramArrayOfString[b1]))
        throw new OpenDataException("Argument's element value indexNames[" + b1 + "]=\"" + paramArrayOfString[b1] + "\" is not a valid item name for rowType."); 
    } 
    this.rowType = paramCompositeType;
    ArrayList arrayList = new ArrayList(paramArrayOfString.length + 1);
    for (byte b2 = 0; b2 < paramArrayOfString.length; b2++)
      arrayList.add(paramArrayOfString[b2]); 
    this.indexNames = Collections.unmodifiableList(arrayList);
  }
  
  private static void checkForNullElement(Object[] paramArrayOfObject, String paramString) {
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0)
      throw new IllegalArgumentException("Argument " + paramString + "[] cannot be null or empty."); 
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] == null)
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + b + "] cannot be null."); 
    } 
  }
  
  private static void checkForEmptyString(String[] paramArrayOfString, String paramString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].trim().equals(""))
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + b + "] cannot be an empty string."); 
    } 
  }
  
  public CompositeType getRowType() { return this.rowType; }
  
  public List<String> getIndexNames() { return this.indexNames; }
  
  public boolean isValue(Object paramObject) {
    if (!(paramObject instanceof TabularData))
      return false; 
    TabularData tabularData = (TabularData)paramObject;
    TabularType tabularType = tabularData.getTabularType();
    return isAssignableFrom(tabularType);
  }
  
  boolean isAssignableFrom(OpenType<?> paramOpenType) {
    if (!(paramOpenType instanceof TabularType))
      return false; 
    TabularType tabularType = (TabularType)paramOpenType;
    return (!getTypeName().equals(tabularType.getTypeName()) || !getIndexNames().equals(tabularType.getIndexNames())) ? false : getRowType().isAssignableFrom(tabularType.getRowType());
  }
  
  public boolean equals(Object paramObject) {
    TabularType tabularType;
    if (paramObject == null)
      return false; 
    try {
      tabularType = (TabularType)paramObject;
    } catch (ClassCastException classCastException) {
      return false;
    } 
    return !getTypeName().equals(tabularType.getTypeName()) ? false : (!this.rowType.equals(tabularType.rowType) ? false : (!!this.indexNames.equals(tabularType.indexNames)));
  }
  
  public int hashCode() {
    if (this.myHashCode == null) {
      int i = 0;
      i += getTypeName().hashCode();
      i += this.rowType.hashCode();
      for (String str : this.indexNames)
        i += str.hashCode(); 
      this.myHashCode = Integer.valueOf(i);
    } 
    return this.myHashCode.intValue();
  }
  
  public String toString() {
    if (this.myToString == null) {
      StringBuilder stringBuilder = (new StringBuilder()).append(getClass().getName()).append("(name=").append(getTypeName()).append(",rowType=").append(this.rowType.toString()).append(",indexNames=(");
      String str = "";
      for (String str1 : this.indexNames) {
        stringBuilder.append(str).append(str1);
        str = ",";
      } 
      stringBuilder.append("))");
      this.myToString = stringBuilder.toString();
    } 
    return this.myToString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\TabularType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */