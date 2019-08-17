package sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import sun.awt.OSInfo;
import sun.awt.windows.WToolkit;
import sun.misc.ThreadGroupUtils;

public class Win32ShellFolderManager2 extends ShellFolderManager {
  private static final int VIEW_LIST = 2;
  
  private static final int VIEW_DETAILS = 3;
  
  private static final int VIEW_PARENTFOLDER = 8;
  
  private static final int VIEW_NEWFOLDER = 11;
  
  private static final Image[] STANDARD_VIEW_BUTTONS;
  
  private static Win32ShellFolder2 desktop;
  
  private static Win32ShellFolder2 drives;
  
  private static Win32ShellFolder2 recent;
  
  private static Win32ShellFolder2 network;
  
  private static Win32ShellFolder2 personal;
  
  private static File[] roots;
  
  private static List topFolderList;
  
  public ShellFolder createShellFolder(File paramFile) throws FileNotFoundException {
    try {
      return createShellFolder(getDesktop(), paramFile);
    } catch (InterruptedException interruptedException) {
      throw new FileNotFoundException("Execution was interrupted");
    } 
  }
  
  static Win32ShellFolder2 createShellFolder(Win32ShellFolder2 paramWin32ShellFolder2, File paramFile) throws FileNotFoundException, InterruptedException {
    try {
      l = paramWin32ShellFolder2.parseDisplayName(paramFile.getCanonicalPath());
    } catch (IOException iOException) {
      l = 0L;
    } 
    if (l == 0L)
      throw new FileNotFoundException("File " + paramFile.getAbsolutePath() + " not found"); 
    try {
      return win32ShellFolder2;
    } finally {
      Win32ShellFolder2.releasePIDL(l);
    } 
  }
  
  static Win32ShellFolder2 createShellFolderFromRelativePIDL(Win32ShellFolder2 paramWin32ShellFolder2, long paramLong) throws InterruptedException {
    while (paramLong != 0L) {
      long l = Win32ShellFolder2.copyFirstPIDLEntry(paramLong);
      if (l != 0L)
        paramLong = (paramWin32ShellFolder2 = new Win32ShellFolder2(paramWin32ShellFolder2, l)).getNextPIDLEntry(paramLong); 
    } 
    return paramWin32ShellFolder2;
  }
  
  private static Image getStandardViewButton(int paramInt) {
    Image image = STANDARD_VIEW_BUTTONS[paramInt];
    if (image != null)
      return image; 
    BufferedImage bufferedImage = new BufferedImage(16, 16, 2);
    bufferedImage.setRGB(0, 0, 16, 16, Win32ShellFolder2.getStandardViewButton0(paramInt), 0, 16);
    STANDARD_VIEW_BUTTONS[paramInt] = bufferedImage;
    return bufferedImage;
  }
  
  static Win32ShellFolder2 getDesktop() {
    if (desktop == null)
      try {
        desktop = new Win32ShellFolder2(0);
      } catch (SecurityException securityException) {
      
      } catch (IOException iOException) {
      
      } catch (InterruptedException interruptedException) {} 
    return desktop;
  }
  
  static Win32ShellFolder2 getDrives() {
    if (drives == null)
      try {
        drives = new Win32ShellFolder2(17);
      } catch (SecurityException securityException) {
      
      } catch (IOException iOException) {
      
      } catch (InterruptedException interruptedException) {} 
    return drives;
  }
  
  static Win32ShellFolder2 getRecent() {
    if (recent == null)
      try {
        String str = Win32ShellFolder2.getFileSystemPath(8);
        if (str != null)
          recent = createShellFolder(getDesktop(), new File(str)); 
      } catch (SecurityException securityException) {
      
      } catch (InterruptedException interruptedException) {
      
      } catch (IOException iOException) {} 
    return recent;
  }
  
  static Win32ShellFolder2 getNetwork() {
    if (network == null)
      try {
        network = new Win32ShellFolder2(18);
      } catch (SecurityException securityException) {
      
      } catch (IOException iOException) {
      
      } catch (InterruptedException interruptedException) {} 
    return network;
  }
  
  static Win32ShellFolder2 getPersonal() {
    if (personal == null)
      try {
        String str = Win32ShellFolder2.getFileSystemPath(5);
        if (str != null) {
          Win32ShellFolder2 win32ShellFolder2 = getDesktop();
          personal = win32ShellFolder2.getChildByPath(str);
          if (personal == null)
            personal = createShellFolder(getDesktop(), new File(str)); 
          if (personal != null)
            personal.setIsPersonal(); 
        } 
      } catch (SecurityException securityException) {
      
      } catch (InterruptedException interruptedException) {
      
      } catch (IOException iOException) {} 
    return personal;
  }
  
  public Object get(String paramString) {
    if (paramString.equals("fileChooserDefaultFolder")) {
      Win32ShellFolder2 win32ShellFolder2 = getPersonal();
      if (win32ShellFolder2 == null)
        win32ShellFolder2 = getDesktop(); 
      return checkFile(win32ShellFolder2);
    } 
    if (paramString.equals("roots")) {
      if (roots == null) {
        Win32ShellFolder2 win32ShellFolder2 = getDesktop();
        if (win32ShellFolder2 != null) {
          roots = new File[] { win32ShellFolder2 };
        } else {
          roots = (File[])super.get(paramString);
        } 
      } 
      return checkFiles(roots);
    } 
    if (paramString.equals("fileChooserComboBoxFolders")) {
      Win32ShellFolder2 win32ShellFolder2 = getDesktop();
      if (win32ShellFolder2 != null && checkFile(win32ShellFolder2) != null) {
        ArrayList arrayList = new ArrayList();
        Win32ShellFolder2 win32ShellFolder21 = getDrives();
        Win32ShellFolder2 win32ShellFolder22 = getRecent();
        if (win32ShellFolder22 != null && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_2000) >= 0)
          arrayList.add(win32ShellFolder22); 
        arrayList.add(win32ShellFolder2);
        File[] arrayOfFile = checkFiles(win32ShellFolder2.listFiles());
        Arrays.sort(arrayOfFile);
        for (File file : arrayOfFile) {
          Win32ShellFolder2 win32ShellFolder23 = (Win32ShellFolder2)file;
          if (!win32ShellFolder23.isFileSystem() || (win32ShellFolder23.isDirectory() && !win32ShellFolder23.isLink())) {
            arrayList.add(win32ShellFolder23);
            if (win32ShellFolder23.equals(win32ShellFolder21)) {
              File[] arrayOfFile1 = checkFiles(win32ShellFolder23.listFiles());
              if (arrayOfFile1 != null && arrayOfFile1.length > 0) {
                List list = Arrays.asList(arrayOfFile1);
                win32ShellFolder23.sortChildren(list);
                arrayList.addAll(list);
              } 
            } 
          } 
        } 
        return checkFiles(arrayList);
      } 
      return super.get(paramString);
    } 
    if (paramString.equals("fileChooserShortcutPanelFolders")) {
      Object object;
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      ArrayList arrayList = new ArrayList();
      byte b = 0;
      do {
        object = toolkit.getDesktopProperty("win.comdlg.placesBarPlace" + b++);
        try {
          if (object instanceof Integer) {
            arrayList.add(new Win32ShellFolder2(((Integer)object).intValue()));
          } else if (object instanceof String) {
            arrayList.add(createShellFolder(new File((String)object)));
          } 
        } catch (IOException iOException) {
        
        } catch (InterruptedException interruptedException) {
          return new File[0];
        } 
      } while (object != null);
      if (arrayList.size() == 0)
        for (File file : new File[] { getRecent(), getDesktop(), getPersonal(), getDrives(), getNetwork() }) {
          if (file != null)
            arrayList.add(file); 
        }  
      return checkFiles(arrayList);
    } 
    if (paramString.startsWith("fileChooserIcon ")) {
      byte b;
      String str = paramString.substring(paramString.indexOf(" ") + 1);
      if (str.equals("ListView") || str.equals("ViewMenu")) {
        b = 2;
      } else if (str.equals("DetailsView")) {
        b = 3;
      } else if (str.equals("UpFolder")) {
        b = 8;
      } else if (str.equals("NewFolder")) {
        b = 11;
      } else {
        return null;
      } 
      return getStandardViewButton(b);
    } 
    if (paramString.startsWith("optionPaneIcon ")) {
      Win32ShellFolder2.SystemIcon systemIcon;
      if (paramString == "optionPaneIcon Error") {
        systemIcon = Win32ShellFolder2.SystemIcon.IDI_ERROR;
      } else if (paramString == "optionPaneIcon Information") {
        systemIcon = Win32ShellFolder2.SystemIcon.IDI_INFORMATION;
      } else if (paramString == "optionPaneIcon Question") {
        systemIcon = Win32ShellFolder2.SystemIcon.IDI_QUESTION;
      } else if (paramString == "optionPaneIcon Warning") {
        systemIcon = Win32ShellFolder2.SystemIcon.IDI_EXCLAMATION;
      } else {
        return null;
      } 
      return Win32ShellFolder2.getSystemIcon(systemIcon);
    } 
    if (paramString.startsWith("shell32Icon ") || paramString.startsWith("shell32LargeIcon ")) {
      String str = paramString.substring(paramString.indexOf(" ") + 1);
      try {
        int i = Integer.parseInt(str);
        if (i >= 0)
          return Win32ShellFolder2.getShell32Icon(i, paramString.startsWith("shell32LargeIcon ")); 
      } catch (NumberFormatException numberFormatException) {}
    } 
    return null;
  }
  
  private static File checkFile(File paramFile) {
    SecurityManager securityManager = System.getSecurityManager();
    return (securityManager == null || paramFile == null) ? paramFile : checkFile(paramFile, securityManager);
  }
  
  private static File checkFile(File paramFile, SecurityManager paramSecurityManager) {
    try {
      paramSecurityManager.checkRead(paramFile.getPath());
      if (paramFile instanceof Win32ShellFolder2) {
        Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)paramFile;
        if (win32ShellFolder2.isLink()) {
          Win32ShellFolder2 win32ShellFolder21 = (Win32ShellFolder2)win32ShellFolder2.getLinkLocation();
          if (win32ShellFolder21 != null)
            paramSecurityManager.checkRead(win32ShellFolder21.getPath()); 
        } 
      } 
      return paramFile;
    } catch (SecurityException securityException) {
      return null;
    } 
  }
  
  static File[] checkFiles(File[] paramArrayOfFile) {
    SecurityManager securityManager = System.getSecurityManager();
    return (securityManager == null || paramArrayOfFile == null || paramArrayOfFile.length == 0) ? paramArrayOfFile : checkFiles(Arrays.stream(paramArrayOfFile), securityManager);
  }
  
  private static File[] checkFiles(List<File> paramList) {
    SecurityManager securityManager = System.getSecurityManager();
    return (securityManager == null || paramList.isEmpty()) ? (File[])paramList.toArray(new File[paramList.size()]) : checkFiles(paramList.stream(), securityManager);
  }
  
  private static File[] checkFiles(Stream<File> paramStream, SecurityManager paramSecurityManager) { return (File[])paramStream.filter(paramFile -> (checkFile(paramFile, paramSecurityManager) != null)).toArray(paramInt -> new File[paramInt]); }
  
  public boolean isComputerNode(final File dir) {
    if (paramFile != null && paramFile == getDrives())
      return true; 
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return dir.getAbsolutePath(); }
        });
    return (str.startsWith("\\\\") && str.indexOf("\\", 2) < 0);
  }
  
  public boolean isFileSystemRoot(File paramFile) {
    if (paramFile != null) {
      Win32ShellFolder2 win32ShellFolder2 = getDrives();
      if (paramFile instanceof Win32ShellFolder2) {
        Win32ShellFolder2 win32ShellFolder21 = (Win32ShellFolder2)paramFile;
        if (win32ShellFolder21.isFileSystem()) {
          if (win32ShellFolder21.parent != null)
            return win32ShellFolder21.parent.equals(win32ShellFolder2); 
        } else {
          return false;
        } 
      } 
      String str = paramFile.getPath();
      if (str.length() != 3 || str.charAt(1) != ':')
        return false; 
      File[] arrayOfFile = win32ShellFolder2.listFiles();
      return (arrayOfFile != null && Arrays.asList(arrayOfFile).contains(paramFile));
    } 
    return false;
  }
  
  static int compareShellFolders(Win32ShellFolder2 paramWin32ShellFolder21, Win32ShellFolder2 paramWin32ShellFolder22) {
    boolean bool1 = paramWin32ShellFolder21.isSpecial();
    boolean bool2 = paramWin32ShellFolder22.isSpecial();
    if (bool1 || bool2) {
      if (topFolderList == null) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(getPersonal());
        arrayList.add(getDesktop());
        arrayList.add(getDrives());
        arrayList.add(getNetwork());
        topFolderList = arrayList;
      } 
      int i = topFolderList.indexOf(paramWin32ShellFolder21);
      int j = topFolderList.indexOf(paramWin32ShellFolder22);
      if (i >= 0 && j >= 0)
        return i - j; 
      if (i >= 0)
        return -1; 
      if (j >= 0)
        return 1; 
    } 
    return (bool1 && !bool2) ? -1 : ((bool2 && !bool1) ? 1 : compareNames(paramWin32ShellFolder21.getAbsolutePath(), paramWin32ShellFolder22.getAbsolutePath()));
  }
  
  static int compareNames(String paramString1, String paramString2) {
    int i = paramString1.compareToIgnoreCase(paramString2);
    return (i != 0) ? i : paramString1.compareTo(paramString2);
  }
  
  protected ShellFolder.Invoker createInvoker() { return new ComInvoker(null); }
  
  static native void initializeCom();
  
  static native void uninitializeCom();
  
  static  {
    WToolkit.loadLibraries();
    STANDARD_VIEW_BUTTONS = new Image[12];
    topFolderList = null;
  }
  
  private static class ComInvoker extends ThreadPoolExecutor implements ThreadFactory, ShellFolder.Invoker {
    private static Thread comThread;
    
    private ComInvoker() {
      super(1, 1, 0L, TimeUnit.DAYS, new LinkedBlockingQueue());
      allowCoreThreadTimeOut(false);
      setThreadFactory(this);
      final Runnable shutdownHook = new Runnable() {
          public void run() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                    Win32ShellFolderManager2.ComInvoker.null.this.this$0.shutdownNow();
                    return null;
                  }
                }); }
        };
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
              return null;
            }
          });
    }
    
    public Thread newThread(final Runnable task) {
      Runnable runnable = new Runnable() {
          public void run() {
            try {
              Win32ShellFolderManager2.initializeCom();
              task.run();
            } finally {
              Win32ShellFolderManager2.uninitializeCom();
            } 
          }
        };
      comThread = (Thread)AccessController.doPrivileged(() -> {
            ThreadGroup threadGroup = ThreadGroupUtils.getRootThreadGroup();
            Thread thread = new Thread(threadGroup, param1Runnable, "Swing-Shell");
            thread.setDaemon(true);
            return thread;
          });
      return comThread;
    }
    
    public <T> T invoke(Callable<T> param1Callable) throws Exception {
      final Future future;
      if (Thread.currentThread() == comThread)
        return (T)param1Callable.call(); 
      try {
        future = submit(param1Callable);
      } catch (RejectedExecutionException rejectedExecutionException) {
        throw new InterruptedException(rejectedExecutionException.getMessage());
      } 
      try {
        return (T)future.get();
      } catch (InterruptedException interruptedException) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                future.cancel(true);
                return null;
              }
            });
        throw interruptedException;
      } catch (ExecutionException executionException) {
        Throwable throwable = executionException.getCause();
        if (throwable instanceof Exception)
          throw (Exception)throwable; 
        if (throwable instanceof Error)
          throw (Error)throwable; 
        throw new RuntimeException("Unexpected error", throwable);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\shell\Win32ShellFolderManager2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */