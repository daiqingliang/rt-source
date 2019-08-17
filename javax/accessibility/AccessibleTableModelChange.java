package javax.accessibility;

public interface AccessibleTableModelChange {
  public static final int INSERT = 1;
  
  public static final int UPDATE = 0;
  
  public static final int DELETE = -1;
  
  int getType();
  
  int getFirstRow();
  
  int getLastRow();
  
  int getFirstColumn();
  
  int getLastColumn();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleTableModelChange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */