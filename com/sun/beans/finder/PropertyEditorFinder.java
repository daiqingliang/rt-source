package com.sun.beans.finder;

import com.sun.beans.WeakCache;
import com.sun.beans.editors.EnumEditor;
import java.beans.PropertyEditor;

public final class PropertyEditorFinder extends InstanceFinder<PropertyEditor> {
  private static final String DEFAULT = "sun.beans.editors";
  
  private static final String DEFAULT_NEW = "com.sun.beans.editors";
  
  private final WeakCache<Class<?>, Class<?>> registry = new WeakCache();
  
  public PropertyEditorFinder() {
    super(PropertyEditor.class, false, "Editor", new String[] { "sun.beans.editors" });
    this.registry.put(byte.class, com.sun.beans.editors.ByteEditor.class);
    this.registry.put(short.class, com.sun.beans.editors.ShortEditor.class);
    this.registry.put(int.class, com.sun.beans.editors.IntegerEditor.class);
    this.registry.put(long.class, com.sun.beans.editors.LongEditor.class);
    this.registry.put(boolean.class, com.sun.beans.editors.BooleanEditor.class);
    this.registry.put(float.class, com.sun.beans.editors.FloatEditor.class);
    this.registry.put(double.class, com.sun.beans.editors.DoubleEditor.class);
  }
  
  public void register(Class<?> paramClass1, Class<?> paramClass2) {
    synchronized (this.registry) {
      this.registry.put(paramClass1, paramClass2);
    } 
  }
  
  public PropertyEditor find(Class<?> paramClass) {
    Class clazz;
    synchronized (this.registry) {
      clazz = (Class)this.registry.get(paramClass);
    } 
    PropertyEditor propertyEditor = (PropertyEditor)instantiate(clazz, null);
    if (propertyEditor == null) {
      propertyEditor = (PropertyEditor)super.find(paramClass);
      if (propertyEditor == null && null != paramClass.getEnumConstants())
        propertyEditor = new EnumEditor(paramClass); 
    } 
    return propertyEditor;
  }
  
  protected PropertyEditor instantiate(Class<?> paramClass, String paramString1, String paramString2) { return (PropertyEditor)super.instantiate(paramClass, "sun.beans.editors".equals(paramString1) ? "com.sun.beans.editors" : paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\PropertyEditorFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */