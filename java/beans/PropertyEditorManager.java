package java.beans;

public class PropertyEditorManager {
  public static void registerEditor(Class<?> paramClass1, Class<?> paramClass2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertiesAccess(); 
    ThreadGroupContext.getContext().getPropertyEditorFinder().register(paramClass1, paramClass2);
  }
  
  public static PropertyEditor findEditor(Class<?> paramClass) { return ThreadGroupContext.getContext().getPropertyEditorFinder().find(paramClass); }
  
  public static String[] getEditorSearchPath() { return ThreadGroupContext.getContext().getPropertyEditorFinder().getPackages(); }
  
  public static void setEditorSearchPath(String[] paramArrayOfString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertiesAccess(); 
    ThreadGroupContext.getContext().getPropertyEditorFinder().setPackages(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\PropertyEditorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */