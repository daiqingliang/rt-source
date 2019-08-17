package com.sun.beans.finder;

final class Signature {
  private final Class<?> type;
  
  private final String name;
  
  private final Class<?>[] args;
  
  Signature(Class<?> paramClass, Class<?>[] paramArrayOfClass) { this(paramClass, null, paramArrayOfClass); }
  
  Signature(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass) {
    this.type = paramClass;
    this.name = paramString;
    this.args = paramArrayOfClass;
  }
  
  Class<?> getType() { return this.type; }
  
  String getName() { return this.name; }
  
  Class<?>[] getArgs() { return this.args; }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof Signature) ? (((signature = (Signature)paramObject).isEqual(signature.type, this.type) && isEqual(signature.name, this.name) && isEqual(signature.args, this.args))) : false); }
  
  private static boolean isEqual(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  private static boolean isEqual(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2) {
    if (paramArrayOfClass1 == null || paramArrayOfClass2 == null)
      return (paramArrayOfClass1 == paramArrayOfClass2); 
    if (paramArrayOfClass1.length != paramArrayOfClass2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfClass1.length; b++) {
      if (!isEqual(paramArrayOfClass1[b], paramArrayOfClass2[b]))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    if (this.code == 0) {
      int i = 17;
      i = addHashCode(i, this.type);
      i = addHashCode(i, this.name);
      if (this.args != null)
        for (Class clazz : this.args)
          i = addHashCode(i, clazz);  
      this.code = i;
    } 
    return this.code;
  }
  
  private static int addHashCode(int paramInt, Object paramObject) {
    paramInt *= 37;
    return (paramObject != null) ? (paramInt + paramObject.hashCode()) : paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\Signature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */