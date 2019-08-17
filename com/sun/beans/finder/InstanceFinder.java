package com.sun.beans.finder;

class InstanceFinder<T> extends Object {
  private static final String[] EMPTY = new String[0];
  
  private final Class<? extends T> type;
  
  private final boolean allow;
  
  private final String suffix;
  
  InstanceFinder(Class<? extends T> paramClass, boolean paramBoolean, String paramString, String... paramVarArgs) {
    this.type = paramClass;
    this.allow = paramBoolean;
    this.suffix = paramString;
    this.packages = (String[])paramVarArgs.clone();
  }
  
  public String[] getPackages() { return (String[])this.packages.clone(); }
  
  public void setPackages(String... paramVarArgs) { this.packages = (paramVarArgs != null && paramVarArgs.length > 0) ? (String[])paramVarArgs.clone() : EMPTY; }
  
  public T find(Class<?> paramClass) {
    if (paramClass == null)
      return null; 
    String str = paramClass.getName() + this.suffix;
    Object object = instantiate(paramClass, str);
    if (object != null)
      return (T)object; 
    if (this.allow) {
      object = instantiate(paramClass, null);
      if (object != null)
        return (T)object; 
    } 
    int i = str.lastIndexOf('.') + 1;
    if (i > 0)
      str = str.substring(i); 
    for (String str1 : this.packages) {
      object = instantiate(paramClass, str1, str);
      if (object != null)
        return (T)object; 
    } 
    return null;
  }
  
  protected T instantiate(Class<?> paramClass, String paramString) {
    if (paramClass != null)
      try {
        if (paramString != null)
          paramClass = ClassFinder.findClass(paramString, paramClass.getClassLoader()); 
        if (this.type.isAssignableFrom(paramClass))
          return (T)paramClass.newInstance(); 
      } catch (Exception exception) {} 
    return null;
  }
  
  protected T instantiate(Class<?> paramClass, String paramString1, String paramString2) { return (T)instantiate(paramClass, paramString1 + '.' + paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\InstanceFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */