package javax.management.relation;

public class RoleStatus {
  public static final int NO_ROLE_WITH_NAME = 1;
  
  public static final int ROLE_NOT_READABLE = 2;
  
  public static final int ROLE_NOT_WRITABLE = 3;
  
  public static final int LESS_THAN_MIN_ROLE_DEGREE = 4;
  
  public static final int MORE_THAN_MAX_ROLE_DEGREE = 5;
  
  public static final int REF_MBEAN_OF_INCORRECT_CLASS = 6;
  
  public static final int REF_MBEAN_NOT_REGISTERED = 7;
  
  public static boolean isRoleStatus(int paramInt) { return !(paramInt != 1 && paramInt != 2 && paramInt != 3 && paramInt != 4 && paramInt != 5 && paramInt != 6 && paramInt != 7); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\RoleStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */