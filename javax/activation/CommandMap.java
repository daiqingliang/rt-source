package javax.activation;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class CommandMap {
  private static CommandMap defaultCommandMap = null;
  
  private static Map<ClassLoader, CommandMap> map = new WeakHashMap();
  
  public static CommandMap getDefaultCommandMap() {
    if (defaultCommandMap != null)
      return defaultCommandMap; 
    ClassLoader classLoader = SecuritySupport.getContextClassLoader();
    CommandMap commandMap = (CommandMap)map.get(classLoader);
    if (commandMap == null) {
      commandMap = new MailcapCommandMap();
      map.put(classLoader, commandMap);
    } 
    return commandMap;
  }
  
  public static void setDefaultCommandMap(CommandMap paramCommandMap) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkSetFactory();
      } catch (SecurityException securityException) {
        if (CommandMap.class.getClassLoader() == null || CommandMap.class.getClassLoader() != paramCommandMap.getClass().getClassLoader())
          throw securityException; 
      }  
    map.remove(SecuritySupport.getContextClassLoader());
    defaultCommandMap = paramCommandMap;
  }
  
  public abstract CommandInfo[] getPreferredCommands(String paramString);
  
  public CommandInfo[] getPreferredCommands(String paramString, DataSource paramDataSource) { return getPreferredCommands(paramString); }
  
  public abstract CommandInfo[] getAllCommands(String paramString);
  
  public CommandInfo[] getAllCommands(String paramString, DataSource paramDataSource) { return getAllCommands(paramString); }
  
  public abstract CommandInfo getCommand(String paramString1, String paramString2);
  
  public CommandInfo getCommand(String paramString1, String paramString2, DataSource paramDataSource) { return getCommand(paramString1, paramString2); }
  
  public abstract DataContentHandler createDataContentHandler(String paramString);
  
  public DataContentHandler createDataContentHandler(String paramString, DataSource paramDataSource) { return createDataContentHandler(paramString); }
  
  public String[] getMimeTypes() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\CommandMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */