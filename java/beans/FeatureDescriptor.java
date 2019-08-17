package java.beans;

import com.sun.beans.TypeResolver;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

public class FeatureDescriptor {
  private static final String TRANSIENT = "transient";
  
  private Reference<? extends Class<?>> classRef;
  
  private boolean expert;
  
  private boolean hidden;
  
  private boolean preferred;
  
  private String shortDescription;
  
  private String name;
  
  private String displayName;
  
  private Hashtable<String, Object> table;
  
  public FeatureDescriptor() {}
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public String getDisplayName() { return (this.displayName == null) ? getName() : this.displayName; }
  
  public void setDisplayName(String paramString) { this.displayName = paramString; }
  
  public boolean isExpert() { return this.expert; }
  
  public void setExpert(boolean paramBoolean) { this.expert = paramBoolean; }
  
  public boolean isHidden() { return this.hidden; }
  
  public void setHidden(boolean paramBoolean) { this.hidden = paramBoolean; }
  
  public boolean isPreferred() { return this.preferred; }
  
  public void setPreferred(boolean paramBoolean) { this.preferred = paramBoolean; }
  
  public String getShortDescription() { return (this.shortDescription == null) ? getDisplayName() : this.shortDescription; }
  
  public void setShortDescription(String paramString) { this.shortDescription = paramString; }
  
  public void setValue(String paramString, Object paramObject) { getTable().put(paramString, paramObject); }
  
  public Object getValue(String paramString) { return (this.table != null) ? this.table.get(paramString) : null; }
  
  public Enumeration<String> attributeNames() { return getTable().keys(); }
  
  FeatureDescriptor(FeatureDescriptor paramFeatureDescriptor1, FeatureDescriptor paramFeatureDescriptor2) {
    paramFeatureDescriptor1.expert |= paramFeatureDescriptor2.expert;
    paramFeatureDescriptor1.hidden |= paramFeatureDescriptor2.hidden;
    paramFeatureDescriptor1.preferred |= paramFeatureDescriptor2.preferred;
    this.name = paramFeatureDescriptor2.name;
    this.shortDescription = paramFeatureDescriptor1.shortDescription;
    if (paramFeatureDescriptor2.shortDescription != null)
      this.shortDescription = paramFeatureDescriptor2.shortDescription; 
    this.displayName = paramFeatureDescriptor1.displayName;
    if (paramFeatureDescriptor2.displayName != null)
      this.displayName = paramFeatureDescriptor2.displayName; 
    this.classRef = paramFeatureDescriptor1.classRef;
    if (paramFeatureDescriptor2.classRef != null)
      this.classRef = paramFeatureDescriptor2.classRef; 
    addTable(paramFeatureDescriptor1.table);
    addTable(paramFeatureDescriptor2.table);
  }
  
  FeatureDescriptor(FeatureDescriptor paramFeatureDescriptor) {
    this.expert = paramFeatureDescriptor.expert;
    this.hidden = paramFeatureDescriptor.hidden;
    this.preferred = paramFeatureDescriptor.preferred;
    this.name = paramFeatureDescriptor.name;
    this.shortDescription = paramFeatureDescriptor.shortDescription;
    this.displayName = paramFeatureDescriptor.displayName;
    this.classRef = paramFeatureDescriptor.classRef;
    addTable(paramFeatureDescriptor.table);
  }
  
  private void addTable(Hashtable<String, Object> paramHashtable) {
    if (paramHashtable != null && !paramHashtable.isEmpty())
      getTable().putAll(paramHashtable); 
  }
  
  private Hashtable<String, Object> getTable() {
    if (this.table == null)
      this.table = new Hashtable(); 
    return this.table;
  }
  
  void setTransient(Transient paramTransient) {
    if (paramTransient != null && null == getValue("transient"))
      setValue("transient", Boolean.valueOf(paramTransient.value())); 
  }
  
  boolean isTransient() {
    Object object = getValue("transient");
    return (object instanceof Boolean) ? ((Boolean)object).booleanValue() : 0;
  }
  
  void setClass0(Class<?> paramClass) { this.classRef = getWeakReference(paramClass); }
  
  Class<?> getClass0() { return (this.classRef != null) ? (Class)this.classRef.get() : null; }
  
  static <T> Reference<T> getSoftReference(T paramT) { return (paramT != null) ? new SoftReference(paramT) : null; }
  
  static <T> Reference<T> getWeakReference(T paramT) { return (paramT != null) ? new WeakReference(paramT) : null; }
  
  static Class<?> getReturnType(Class<?> paramClass, Method paramMethod) {
    if (paramClass == null)
      paramClass = paramMethod.getDeclaringClass(); 
    return TypeResolver.erase(TypeResolver.resolveInClass(paramClass, paramMethod.getGenericReturnType()));
  }
  
  static Class<?>[] getParameterTypes(Class<?> paramClass, Method paramMethod) {
    if (paramClass == null)
      paramClass = paramMethod.getDeclaringClass(); 
    return TypeResolver.erase(TypeResolver.resolveInClass(paramClass, paramMethod.getGenericParameterTypes()));
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(getClass().getName());
    stringBuilder.append("[name=").append(this.name);
    appendTo(stringBuilder, "displayName", this.displayName);
    appendTo(stringBuilder, "shortDescription", this.shortDescription);
    appendTo(stringBuilder, "preferred", this.preferred);
    appendTo(stringBuilder, "hidden", this.hidden);
    appendTo(stringBuilder, "expert", this.expert);
    if (this.table != null && !this.table.isEmpty()) {
      stringBuilder.append("; values={");
      for (Map.Entry entry : this.table.entrySet())
        stringBuilder.append((String)entry.getKey()).append("=").append(entry.getValue()).append("; "); 
      stringBuilder.setLength(stringBuilder.length() - 2);
      stringBuilder.append("}");
    } 
    appendTo(stringBuilder);
    return stringBuilder.append("]").toString();
  }
  
  void appendTo(StringBuilder paramStringBuilder) {}
  
  static void appendTo(StringBuilder paramStringBuilder, String paramString, Reference<?> paramReference) {
    if (paramReference != null)
      appendTo(paramStringBuilder, paramString, paramReference.get()); 
  }
  
  static void appendTo(StringBuilder paramStringBuilder, String paramString, Object paramObject) {
    if (paramObject != null)
      paramStringBuilder.append("; ").append(paramString).append("=").append(paramObject); 
  }
  
  static void appendTo(StringBuilder paramStringBuilder, String paramString, boolean paramBoolean) {
    if (paramBoolean)
      paramStringBuilder.append("; ").append(paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\FeatureDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */