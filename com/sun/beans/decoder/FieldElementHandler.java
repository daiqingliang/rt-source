package com.sun.beans.decoder;

import com.sun.beans.finder.FieldFinder;
import java.lang.reflect.Field;

final class FieldElementHandler extends AccessorElementHandler {
  private Class<?> type;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("class")) {
      this.type = getOwner().findClass(paramString2);
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  protected boolean isArgument() { return (super.isArgument() && this.type != null); }
  
  protected Object getContextBean() { return (this.type != null) ? this.type : super.getContextBean(); }
  
  protected Object getValue(String paramString) {
    try {
      return getFieldValue(getContextBean(), paramString);
    } catch (Exception exception) {
      getOwner().handleException(exception);
      return null;
    } 
  }
  
  protected void setValue(String paramString, Object paramObject) {
    try {
      setFieldValue(getContextBean(), paramString, paramObject);
    } catch (Exception exception) {
      getOwner().handleException(exception);
    } 
  }
  
  static Object getFieldValue(Object paramObject, String paramString) throws IllegalAccessException, NoSuchFieldException { return findField(paramObject, paramString).get(paramObject); }
  
  private static void setFieldValue(Object paramObject1, String paramString, Object paramObject2) throws IllegalAccessException, NoSuchFieldException { findField(paramObject1, paramString).set(paramObject1, paramObject2); }
  
  private static Field findField(Object paramObject, String paramString) throws NoSuchFieldException { return (paramObject instanceof Class) ? FieldFinder.findStaticField((Class)paramObject, paramString) : FieldFinder.findField(paramObject.getClass(), paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\FieldElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */