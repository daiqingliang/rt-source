package sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

public abstract class ShellFolder extends File {
  private static final String COLUMN_NAME = "FileChooser.fileNameHeaderText";
  
  private static final String COLUMN_SIZE = "FileChooser.fileSizeHeaderText";
  
  private static final String COLUMN_DATE = "FileChooser.fileDateHeaderText";
  
  protected ShellFolder parent;
  
  private static final ShellFolderManager shellFolderManager;
  
  private static final Invoker invoker;
  
  private static final Comparator DEFAULT_COMPARATOR;
  
  private static final Comparator<File> FILE_COMPARATOR;
  
  ShellFolder(ShellFolder paramShellFolder, String paramString) {
    super((paramString != null) ? paramString : "ShellFolder");
    this.parent = paramShellFolder;
  }
  
  public boolean isFileSystem() { return !getPath().startsWith("ShellFolder"); }
  
  protected abstract Object writeReplace() throws ObjectStreamException;
  
  public String getParent() { return (this.parent == null && isFileSystem()) ? super.getParent() : ((this.parent != null) ? this.parent.getPath() : null); }
  
  public File getParentFile() { return (this.parent != null) ? this.parent : (isFileSystem() ? super.getParentFile() : null); }
  
  public File[] listFiles() { return listFiles(true); }
  
  public File[] listFiles(boolean paramBoolean) {
    File[] arrayOfFile = super.listFiles();
    if (!paramBoolean) {
      Vector vector = new Vector();
      boolean bool = (arrayOfFile == null) ? 0 : arrayOfFile.length;
      for (byte b = 0; b < bool; b++) {
        if (!arrayOfFile[b].isHidden())
          vector.addElement(arrayOfFile[b]); 
      } 
      arrayOfFile = (File[])vector.toArray(new File[vector.size()]);
    } 
    return arrayOfFile;
  }
  
  public abstract boolean isLink();
  
  public abstract ShellFolder getLinkLocation() throws FileNotFoundException;
  
  public abstract String getDisplayName();
  
  public abstract String getFolderType();
  
  public abstract String getExecutableType();
  
  public int compareTo(File paramFile) { return (paramFile == null || !(paramFile instanceof ShellFolder) || (paramFile instanceof ShellFolder && ((ShellFolder)paramFile).isFileSystem())) ? (isFileSystem() ? super.compareTo(paramFile) : -1) : (isFileSystem() ? 1 : getName().compareTo(paramFile.getName())); }
  
  public Image getIcon(boolean paramBoolean) { return null; }
  
  public static ShellFolder getShellFolder(File paramFile) throws FileNotFoundException {
    if (paramFile instanceof ShellFolder)
      return (ShellFolder)paramFile; 
    if (!paramFile.exists())
      throw new FileNotFoundException(); 
    return shellFolderManager.createShellFolder(paramFile);
  }
  
  public static Object get(String paramString) { return shellFolderManager.get(paramString); }
  
  public static boolean isComputerNode(File paramFile) { return shellFolderManager.isComputerNode(paramFile); }
  
  public static boolean isFileSystemRoot(File paramFile) { return shellFolderManager.isFileSystemRoot(paramFile); }
  
  public static File getNormalizedFile(File paramFile) throws IOException {
    File file = paramFile.getCanonicalFile();
    return paramFile.equals(file) ? file : new File(paramFile.toURI().normalize());
  }
  
  public static void sort(final List<? extends File> files) {
    if (paramList == null || paramList.size() <= 1)
      return; 
    invoke(new Callable<Void>() {
          public Void call() {
            File file = null;
            for (File file1 : files) {
              File file2 = file1.getParentFile();
              if (file2 == null || !(file1 instanceof ShellFolder)) {
                file = null;
                break;
              } 
              if (file == null) {
                file = file2;
                continue;
              } 
              if (file != file2 && !file.equals(file2)) {
                file = null;
                break;
              } 
            } 
            if (file instanceof ShellFolder) {
              ((ShellFolder)file).sortChildren(files);
            } else {
              Collections.sort(files, FILE_COMPARATOR);
            } 
            return null;
          }
        });
  }
  
  public void sortChildren(final List<? extends File> files) { invoke(new Callable<Void>() {
          public Void call() {
            Collections.sort(files, FILE_COMPARATOR);
            return null;
          }
        }); }
  
  public boolean isAbsolute() { return (!isFileSystem() || super.isAbsolute()); }
  
  public File getAbsoluteFile() { return isFileSystem() ? super.getAbsoluteFile() : this; }
  
  public boolean canRead() { return isFileSystem() ? super.canRead() : 1; }
  
  public boolean canWrite() { return isFileSystem() ? super.canWrite() : 0; }
  
  public boolean exists() { return (!isFileSystem() || isFileSystemRoot(this) || super.exists()); }
  
  public boolean isDirectory() { return isFileSystem() ? super.isDirectory() : 1; }
  
  public boolean isFile() { return isFileSystem() ? super.isFile() : (!isDirectory() ? 1 : 0); }
  
  public long lastModified() { return isFileSystem() ? super.lastModified() : 0L; }
  
  public long length() { return isFileSystem() ? super.length() : 0L; }
  
  public boolean createNewFile() { return isFileSystem() ? super.createNewFile() : 0; }
  
  public boolean delete() { return isFileSystem() ? super.delete() : 0; }
  
  public void deleteOnExit() {
    if (isFileSystem())
      super.deleteOnExit(); 
  }
  
  public boolean mkdir() { return isFileSystem() ? super.mkdir() : 0; }
  
  public boolean mkdirs() { return isFileSystem() ? super.mkdirs() : 0; }
  
  public boolean renameTo(File paramFile) { return isFileSystem() ? super.renameTo(paramFile) : 0; }
  
  public boolean setLastModified(long paramLong) { return isFileSystem() ? super.setLastModified(paramLong) : 0; }
  
  public boolean setReadOnly() { return isFileSystem() ? super.setReadOnly() : 0; }
  
  public String toString() { return isFileSystem() ? super.toString() : getDisplayName(); }
  
  public static ShellFolderColumnInfo[] getFolderColumns(File paramFile) {
    ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = null;
    if (paramFile instanceof ShellFolder)
      arrayOfShellFolderColumnInfo = ((ShellFolder)paramFile).getFolderColumns(); 
    if (arrayOfShellFolderColumnInfo == null)
      arrayOfShellFolderColumnInfo = new ShellFolderColumnInfo[] { new ShellFolderColumnInfo("FileChooser.fileNameHeaderText", Integer.valueOf(150), Integer.valueOf(10), true, null, FILE_COMPARATOR), new ShellFolderColumnInfo("FileChooser.fileSizeHeaderText", Integer.valueOf(75), Integer.valueOf(4), true, null, DEFAULT_COMPARATOR, true), new ShellFolderColumnInfo("FileChooser.fileDateHeaderText", Integer.valueOf(130), Integer.valueOf(10), true, null, DEFAULT_COMPARATOR, true) }; 
    return arrayOfShellFolderColumnInfo;
  }
  
  public ShellFolderColumnInfo[] getFolderColumns() { return null; }
  
  public static Object getFolderColumnValue(File paramFile, int paramInt) {
    long l;
    if (paramFile instanceof ShellFolder) {
      Object object = ((ShellFolder)paramFile).getFolderColumnValue(paramInt);
      if (object != null)
        return object; 
    } 
    if (paramFile == null || !paramFile.exists())
      return null; 
    switch (paramInt) {
      case 0:
        return paramFile;
      case 1:
        return paramFile.isDirectory() ? null : Long.valueOf(paramFile.length());
      case 2:
        if (isFileSystemRoot(paramFile))
          return null; 
        l = paramFile.lastModified();
        return (l == 0L) ? null : new Date(l);
    } 
    return null;
  }
  
  public Object getFolderColumnValue(int paramInt) { return null; }
  
  public static <T> T invoke(Callable<T> paramCallable) {
    try {
      return (T)invoke(paramCallable, RuntimeException.class);
    } catch (InterruptedException interruptedException) {
      return null;
    } 
  }
  
  public static <T, E extends Throwable> T invoke(Callable<T> paramCallable, Class<E> paramClass) throws InterruptedException, E {
    try {
      return (T)invoker.invoke(paramCallable);
    } catch (Exception exception) {
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      if (exception instanceof InterruptedException) {
        Thread.currentThread().interrupt();
        throw (InterruptedException)exception;
      } 
      if (paramClass.isInstance(exception))
        throw (Throwable)paramClass.cast(exception); 
      throw new RuntimeException("Unexpected error", exception);
    } 
  }
  
  static  {
    String str = (String)Toolkit.getDefaultToolkit().getDesktopProperty("Shell.shellFolderManager");
    Class clazz = null;
    try {
      clazz = Class.forName(str, false, null);
      if (!ShellFolderManager.class.isAssignableFrom(clazz))
        clazz = null; 
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (NullPointerException nullPointerException) {
    
    } catch (SecurityException securityException) {}
    if (clazz == null)
      clazz = ShellFolderManager.class; 
    try {
      shellFolderManager = (ShellFolderManager)clazz.newInstance();
    } catch (InstantiationException instantiationException) {
      throw new Error("Could not instantiate Shell Folder Manager: " + clazz.getName());
    } catch (IllegalAccessException illegalAccessException) {
      throw new Error("Could not access Shell Folder Manager: " + clazz.getName());
    } 
    invoker = shellFolderManager.createInvoker();
    DEFAULT_COMPARATOR = new Comparator() {
        public int compare(Object param1Object1, Object param1Object2) {
          byte b;
          if (param1Object1 == null && param1Object2 == null) {
            b = 0;
          } else if (param1Object1 != null && param1Object2 == null) {
            b = 1;
          } else if (param1Object1 == null && param1Object2 != null) {
            b = -1;
          } else if (param1Object1 instanceof Comparable) {
            b = ((Comparable)param1Object1).compareTo(param1Object2);
          } else {
            b = 0;
          } 
          return b;
        }
      };
    FILE_COMPARATOR = new Comparator<File>() {
        public int compare(File param1File1, File param1File2) {
          ShellFolder shellFolder1 = null;
          ShellFolder shellFolder2 = null;
          if (param1File1 instanceof ShellFolder) {
            shellFolder1 = (ShellFolder)param1File1;
            if (shellFolder1.isFileSystem())
              shellFolder1 = null; 
          } 
          if (param1File2 instanceof ShellFolder) {
            shellFolder2 = (ShellFolder)param1File2;
            if (shellFolder2.isFileSystem())
              shellFolder2 = null; 
          } 
          if (shellFolder1 != null && shellFolder2 != null)
            return shellFolder1.compareTo(shellFolder2); 
          if (shellFolder1 != null)
            return -1; 
          if (shellFolder2 != null)
            return 1; 
          String str1 = param1File1.getName();
          String str2 = param1File2.getName();
          int i = str1.compareToIgnoreCase(str2);
          return (i != 0) ? i : str1.compareTo(str2);
        }
      };
  }
  
  public static interface Invoker {
    <T> T invoke(Callable<T> param1Callable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\shell\ShellFolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */