package sun.management;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularType;

public abstract class LazyCompositeData implements CompositeData, Serializable {
  private CompositeData compositeData;
  
  private static final long serialVersionUID = -2190411934472666714L;
  
  public boolean containsKey(String paramString) { return compositeData().containsKey(paramString); }
  
  public boolean containsValue(Object paramObject) { return compositeData().containsValue(paramObject); }
  
  public boolean equals(Object paramObject) { return compositeData().equals(paramObject); }
  
  public Object get(String paramString) { return compositeData().get(paramString); }
  
  public Object[] getAll(String[] paramArrayOfString) { return compositeData().getAll(paramArrayOfString); }
  
  public CompositeType getCompositeType() { return compositeData().getCompositeType(); }
  
  public int hashCode() { return compositeData().hashCode(); }
  
  public String toString() { return compositeData().toString(); }
  
  public Collection<?> values() { return compositeData().values(); }
  
  private CompositeData compositeData() {
    if (this.compositeData != null)
      return this.compositeData; 
    this.compositeData = getCompositeData();
    return this.compositeData;
  }
  
  protected Object writeReplace() throws ObjectStreamException { return compositeData(); }
  
  protected abstract CompositeData getCompositeData();
  
  static String getString(CompositeData paramCompositeData, String paramString) {
    if (paramCompositeData == null)
      throw new IllegalArgumentException("Null CompositeData"); 
    return (String)paramCompositeData.get(paramString);
  }
  
  static boolean getBoolean(CompositeData paramCompositeData, String paramString) {
    if (paramCompositeData == null)
      throw new IllegalArgumentException("Null CompositeData"); 
    return ((Boolean)paramCompositeData.get(paramString)).booleanValue();
  }
  
  static long getLong(CompositeData paramCompositeData, String paramString) {
    if (paramCompositeData == null)
      throw new IllegalArgumentException("Null CompositeData"); 
    return ((Long)paramCompositeData.get(paramString)).longValue();
  }
  
  static int getInt(CompositeData paramCompositeData, String paramString) {
    if (paramCompositeData == null)
      throw new IllegalArgumentException("Null CompositeData"); 
    return ((Integer)paramCompositeData.get(paramString)).intValue();
  }
  
  protected static boolean isTypeMatched(CompositeType paramCompositeType1, CompositeType paramCompositeType2) {
    if (paramCompositeType1 == paramCompositeType2)
      return true; 
    Set set = paramCompositeType1.keySet();
    return !paramCompositeType2.keySet().containsAll(set) ? false : set.stream().allMatch(paramString -> isTypeMatched(paramCompositeType1.getType(paramString), paramCompositeType2.getType(paramString)));
  }
  
  protected static boolean isTypeMatched(TabularType paramTabularType1, TabularType paramTabularType2) {
    if (paramTabularType1 == paramTabularType2)
      return true; 
    List list1 = paramTabularType1.getIndexNames();
    List list2 = paramTabularType2.getIndexNames();
    return !list1.equals(list2) ? false : isTypeMatched(paramTabularType1.getRowType(), paramTabularType2.getRowType());
  }
  
  protected static boolean isTypeMatched(ArrayType<?> paramArrayType1, ArrayType<?> paramArrayType2) {
    if (paramArrayType1 == paramArrayType2)
      return true; 
    int i = paramArrayType1.getDimension();
    int j = paramArrayType2.getDimension();
    return (i != j) ? false : isTypeMatched(paramArrayType1.getElementOpenType(), paramArrayType2.getElementOpenType());
  }
  
  private static boolean isTypeMatched(OpenType<?> paramOpenType1, OpenType<?> paramOpenType2) {
    if (paramOpenType1 instanceof CompositeType) {
      if (!(paramOpenType2 instanceof CompositeType))
        return false; 
      if (!isTypeMatched((CompositeType)paramOpenType1, (CompositeType)paramOpenType2))
        return false; 
    } else if (paramOpenType1 instanceof TabularType) {
      if (!(paramOpenType2 instanceof TabularType))
        return false; 
      if (!isTypeMatched((TabularType)paramOpenType1, (TabularType)paramOpenType2))
        return false; 
    } else if (paramOpenType1 instanceof ArrayType) {
      if (!(paramOpenType2 instanceof ArrayType))
        return false; 
      if (!isTypeMatched((ArrayType)paramOpenType1, (ArrayType)paramOpenType2))
        return false; 
    } else if (!paramOpenType1.equals(paramOpenType2)) {
      return false;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\LazyCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */