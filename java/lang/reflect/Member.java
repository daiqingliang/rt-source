package java.lang.reflect;

public interface Member {
  public static final int PUBLIC = 0;
  
  public static final int DECLARED = 1;
  
  Class<?> getDeclaringClass();
  
  String getName();
  
  int getModifiers();
  
  boolean isSynthetic();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Member.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */