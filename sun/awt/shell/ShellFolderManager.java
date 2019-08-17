package sun.awt.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

class ShellFolderManager {
  public ShellFolder createShellFolder(File paramFile) throws FileNotFoundException { return new DefaultShellFolder(null, paramFile); }
  
  public Object get(String paramString) {
    if (paramString.equals("fileChooserDefaultFolder")) {
      File file = new File(System.getProperty("user.home"));
      try {
        return createShellFolder(file);
      } catch (FileNotFoundException fileNotFoundException) {
        return file;
      } 
    } 
    return paramString.equals("roots") ? File.listRoots() : (paramString.equals("fileChooserComboBoxFolders") ? get("roots") : (paramString.equals("fileChooserShortcutPanelFolders") ? new File[] { (File)get("fileChooserDefaultFolder") } : null));
  }
  
  public boolean isComputerNode(File paramFile) { return false; }
  
  public boolean isFileSystemRoot(File paramFile) { return (paramFile instanceof ShellFolder && !((ShellFolder)paramFile).isFileSystem()) ? false : ((paramFile.getParentFile() == null)); }
  
  protected ShellFolder.Invoker createInvoker() { return new DirectInvoker(null); }
  
  private static class DirectInvoker implements ShellFolder.Invoker {
    private DirectInvoker() {}
    
    public <T> T invoke(Callable<T> param1Callable) throws Exception { return (T)param1Callable.call(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\shell\ShellFolderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */