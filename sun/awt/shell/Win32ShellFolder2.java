package sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

final class Win32ShellFolder2 extends ShellFolder {
  public static final int DESKTOP = 0;
  
  public static final int INTERNET = 1;
  
  public static final int PROGRAMS = 2;
  
  public static final int CONTROLS = 3;
  
  public static final int PRINTERS = 4;
  
  public static final int PERSONAL = 5;
  
  public static final int FAVORITES = 6;
  
  public static final int STARTUP = 7;
  
  public static final int RECENT = 8;
  
  public static final int SENDTO = 9;
  
  public static final int BITBUCKET = 10;
  
  public static final int STARTMENU = 11;
  
  public static final int DESKTOPDIRECTORY = 16;
  
  public static final int DRIVES = 17;
  
  public static final int NETWORK = 18;
  
  public static final int NETHOOD = 19;
  
  public static final int FONTS = 20;
  
  public static final int TEMPLATES = 21;
  
  public static final int COMMON_STARTMENU = 22;
  
  public static final int COMMON_PROGRAMS = 23;
  
  public static final int COMMON_STARTUP = 24;
  
  public static final int COMMON_DESKTOPDIRECTORY = 25;
  
  public static final int APPDATA = 26;
  
  public static final int PRINTHOOD = 27;
  
  public static final int ALTSTARTUP = 29;
  
  public static final int COMMON_ALTSTARTUP = 30;
  
  public static final int COMMON_FAVORITES = 31;
  
  public static final int INTERNET_CACHE = 32;
  
  public static final int COOKIES = 33;
  
  public static final int HISTORY = 34;
  
  public static final int ATTRIB_CANCOPY = 1;
  
  public static final int ATTRIB_CANMOVE = 2;
  
  public static final int ATTRIB_CANLINK = 4;
  
  public static final int ATTRIB_CANRENAME = 16;
  
  public static final int ATTRIB_CANDELETE = 32;
  
  public static final int ATTRIB_HASPROPSHEET = 64;
  
  public static final int ATTRIB_DROPTARGET = 256;
  
  public static final int ATTRIB_LINK = 65536;
  
  public static final int ATTRIB_SHARE = 131072;
  
  public static final int ATTRIB_READONLY = 262144;
  
  public static final int ATTRIB_GHOSTED = 524288;
  
  public static final int ATTRIB_HIDDEN = 524288;
  
  public static final int ATTRIB_FILESYSANCESTOR = 268435456;
  
  public static final int ATTRIB_FOLDER = 536870912;
  
  public static final int ATTRIB_FILESYSTEM = 1073741824;
  
  public static final int ATTRIB_HASSUBFOLDER = -2147483648;
  
  public static final int ATTRIB_VALIDATE = 16777216;
  
  public static final int ATTRIB_REMOVABLE = 33554432;
  
  public static final int ATTRIB_COMPRESSED = 67108864;
  
  public static final int ATTRIB_BROWSABLE = 134217728;
  
  public static final int ATTRIB_NONENUMERATED = 1048576;
  
  public static final int ATTRIB_NEWCONTENT = 2097152;
  
  public static final int SHGDN_NORMAL = 0;
  
  public static final int SHGDN_INFOLDER = 1;
  
  public static final int SHGDN_INCLUDE_NONFILESYS = 8192;
  
  public static final int SHGDN_FORADDRESSBAR = 16384;
  
  public static final int SHGDN_FORPARSING = 32768;
  
  FolderDisposer disposer = new FolderDisposer();
  
  private long pIShellIcon = -1L;
  
  private String folderType = null;
  
  private String displayName = null;
  
  private Image smallIcon = null;
  
  private Image largeIcon = null;
  
  private Boolean isDir = null;
  
  private boolean isPersonal;
  
  private static Map smallSystemImages;
  
  private static Map largeSystemImages;
  
  private static Map smallLinkedSystemImages;
  
  private static Map largeLinkedSystemImages;
  
  private static final int LVCFMT_LEFT = 0;
  
  private static final int LVCFMT_RIGHT = 1;
  
  private static final int LVCFMT_CENTER = 2;
  
  private static native void initIDs();
  
  private void setIShellFolder(long paramLong) { this.disposer.pIShellFolder = paramLong; }
  
  private void setRelativePIDL(long paramLong) { this.disposer.relativePIDL = paramLong; }
  
  private static String composePathForCsidl(int paramInt) throws IOException, InterruptedException {
    String str = getFileSystemPath(paramInt);
    return (str == null) ? ("ShellFolder: 0x" + Integer.toHexString(paramInt)) : str;
  }
  
  Win32ShellFolder2(final int csidl) throws IOException, InterruptedException {
    super(null, composePathForCsidl(paramInt));
    invoke(new Callable<Void>() {
          public Void call() throws InterruptedException {
            if (csidl == 0) {
              Win32ShellFolder2.this.initDesktop();
            } else {
              Win32ShellFolder2.this.initSpecial(Win32ShellFolder2.this.getDesktop().getIShellFolder(), csidl);
              long l = this.this$0.disposer.relativePIDL;
              Win32ShellFolder2.this.parent = Win32ShellFolder2.this.getDesktop();
              while (l != 0L) {
                long l1 = Win32ShellFolder2.copyFirstPIDLEntry(l);
                if (l1 != 0L) {
                  l = Win32ShellFolder2.getNextPIDLEntry(l);
                  if (l != 0L) {
                    Win32ShellFolder2.this.parent = new Win32ShellFolder2((Win32ShellFolder2)Win32ShellFolder2.this.parent, l1);
                    continue;
                  } 
                  this.this$0.disposer.relativePIDL = l1;
                } 
              } 
            } 
            return null;
          }
        }InterruptedException.class);
    Disposer.addRecord(this, this.disposer);
  }
  
  Win32ShellFolder2(Win32ShellFolder2 paramWin32ShellFolder2, long paramLong1, long paramLong2, String paramString) {
    super(paramWin32ShellFolder2, (paramString != null) ? paramString : "ShellFolder: ");
    this.disposer.pIShellFolder = paramLong1;
    this.disposer.relativePIDL = paramLong2;
    Disposer.addRecord(this, this.disposer);
  }
  
  Win32ShellFolder2(final Win32ShellFolder2 parent, final long relativePIDL) throws InterruptedException {
    super(paramWin32ShellFolder2, (String)invoke(new Callable<String>() {
            public String call() { return Win32ShellFolder2.getFileSystemPath(parent.getIShellFolder(), relativePIDL); }
          }RuntimeException.class));
    this.disposer.relativePIDL = paramLong;
    Disposer.addRecord(this, this.disposer);
  }
  
  private native void initDesktop();
  
  private native void initSpecial(long paramLong, int paramInt);
  
  public void setIsPersonal() { this.isPersonal = true; }
  
  protected Object writeReplace() throws ObjectStreamException { return invoke(new Callable<File>() {
          public File call() {
            if (Win32ShellFolder2.this.isFileSystem())
              return new File(Win32ShellFolder2.this.getPath()); 
            Win32ShellFolder2 win32ShellFolder2 = Win32ShellFolderManager2.getDrives();
            if (win32ShellFolder2 != null) {
              File[] arrayOfFile = win32ShellFolder2.listFiles();
              if (arrayOfFile != null)
                for (byte b = 0; b < arrayOfFile.length; b++) {
                  if (arrayOfFile[b] instanceof Win32ShellFolder2) {
                    Win32ShellFolder2 win32ShellFolder21 = (Win32ShellFolder2)arrayOfFile[b];
                    if (win32ShellFolder21.isFileSystem() && !win32ShellFolder21.hasAttribute(33554432))
                      return new File(win32ShellFolder21.getPath()); 
                  } 
                }  
            } 
            return new File("C:\\");
          }
        }); }
  
  protected void dispose() { this.disposer.dispose(); }
  
  static native long getNextPIDLEntry(long paramLong);
  
  static native long copyFirstPIDLEntry(long paramLong);
  
  private static native long combinePIDLs(long paramLong1, long paramLong2);
  
  static native void releasePIDL(long paramLong);
  
  private static native void releaseIShellFolder(long paramLong);
  
  private long getIShellFolder() {
    if (this.disposer.pIShellFolder == 0L)
      try {
        this.disposer.pIShellFolder = ((Long)invoke(new Callable<Long>() {
              public Long call() throws IOException {
                assert Win32ShellFolder2.this.isDirectory();
                assert Win32ShellFolder2.this.parent != null;
                long l1 = Win32ShellFolder2.this.getParentIShellFolder();
                if (l1 == 0L)
                  throw new InternalError("Parent IShellFolder was null for " + Win32ShellFolder2.this.getAbsolutePath()); 
                long l2 = Win32ShellFolder2.bindToObject(l1, this.this$0.disposer.relativePIDL);
                if (l2 == 0L)
                  throw new InternalError("Unable to bind " + Win32ShellFolder2.this.getAbsolutePath() + " to parent"); 
                return Long.valueOf(l2);
              }
            }RuntimeException.class)).longValue();
      } catch (InterruptedException interruptedException) {} 
    return this.disposer.pIShellFolder;
  }
  
  public long getParentIShellFolder() {
    Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)getParentFile();
    return (win32ShellFolder2 == null) ? getIShellFolder() : win32ShellFolder2.getIShellFolder();
  }
  
  public long getRelativePIDL() {
    if (this.disposer.relativePIDL == 0L)
      throw new InternalError("Should always have a relative PIDL"); 
    return this.disposer.relativePIDL;
  }
  
  private long getAbsolutePIDL() {
    if (this.parent == null)
      return getRelativePIDL(); 
    if (this.disposer.absolutePIDL == 0L)
      this.disposer.absolutePIDL = combinePIDLs(((Win32ShellFolder2)this.parent).getAbsolutePIDL(), getRelativePIDL()); 
    return this.disposer.absolutePIDL;
  }
  
  public Win32ShellFolder2 getDesktop() { return Win32ShellFolderManager2.getDesktop(); }
  
  public long getDesktopIShellFolder() { return getDesktop().getIShellFolder(); }
  
  private static boolean pathsEqual(String paramString1, String paramString2) { return paramString1.equalsIgnoreCase(paramString2); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Win32ShellFolder2))
      return !(paramObject instanceof File) ? super.equals(paramObject) : pathsEqual(getPath(), ((File)paramObject).getPath()); 
    Win32ShellFolder2 win32ShellFolder2 = (Win32ShellFolder2)paramObject;
    if ((this.parent == null && win32ShellFolder2.parent != null) || (this.parent != null && win32ShellFolder2.parent == null))
      return false; 
    if (isFileSystem() && win32ShellFolder2.isFileSystem())
      return (pathsEqual(getPath(), win32ShellFolder2.getPath()) && (this.parent == win32ShellFolder2.parent || this.parent.equals(win32ShellFolder2.parent))); 
    if (this.parent == win32ShellFolder2.parent || this.parent.equals(win32ShellFolder2.parent))
      try {
        return pidlsEqual(getParentIShellFolder(), this.disposer.relativePIDL, win32ShellFolder2.disposer.relativePIDL);
      } catch (InterruptedException interruptedException) {
        return false;
      }  
    return false;
  }
  
  private static boolean pidlsEqual(final long pIShellFolder, final long pidl1, final long pidl2) throws InterruptedException { return ((Boolean)invoke(new Callable<Boolean>() {
          public Boolean call() { return Boolean.valueOf((Win32ShellFolder2.compareIDs(pIShellFolder, pidl1, pidl2) == 0)); }
        }RuntimeException.class)).booleanValue(); }
  
  private static native int compareIDs(long paramLong1, long paramLong2, long paramLong3);
  
  public boolean isFileSystem() {
    if (this.cachedIsFileSystem == null)
      this.cachedIsFileSystem = Boolean.valueOf(hasAttribute(1073741824)); 
    return this.cachedIsFileSystem.booleanValue();
  }
  
  public boolean hasAttribute(final int attribute) {
    Boolean bool = (Boolean)invoke(new Callable<Boolean>() {
          public Boolean call() { return Boolean.valueOf(((Win32ShellFolder2.getAttributes0(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), attribute) & attribute) != 0)); }
        });
    return (bool != null && bool.booleanValue());
  }
  
  private static native int getAttributes0(long paramLong1, long paramLong2, int paramInt);
  
  private static String getFileSystemPath(long paramLong1, long paramLong2) {
    int i = 536936448;
    if (paramLong1 == Win32ShellFolderManager2.getNetwork().getIShellFolder() && getAttributes0(paramLong1, paramLong2, i) == i) {
      String str = getFileSystemPath(Win32ShellFolderManager2.getDesktop().getIShellFolder(), getLinkLocation(paramLong1, paramLong2, false));
      if (str != null && str.startsWith("\\\\"))
        return str; 
    } 
    return getDisplayNameOf(paramLong1, paramLong2, 32768);
  }
  
  static String getFileSystemPath(final int csidl) throws IOException, InterruptedException {
    String str = (String)invoke(new Callable<String>() {
          public String call() { return Win32ShellFolder2.getFileSystemPath0(csidl); }
        },  IOException.class);
    if (str != null) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkRead(str); 
    } 
    return str;
  }
  
  private static native String getFileSystemPath0(int paramInt) throws IOException, InterruptedException;
  
  private static boolean isNetworkRoot(String paramString) { return (paramString.equals("\\\\") || paramString.equals("\\") || paramString.equals("//") || paramString.equals("/")); }
  
  public File getParentFile() { return this.parent; }
  
  public boolean isDirectory() {
    if (this.isDir == null)
      if (hasAttribute(536870912) && !hasAttribute(134217728)) {
        this.isDir = Boolean.TRUE;
      } else if (isLink()) {
        ShellFolder shellFolder = getLinkLocation(false);
        this.isDir = Boolean.valueOf((shellFolder != null && shellFolder.isDirectory()));
      } else {
        this.isDir = Boolean.FALSE;
      }  
    return this.isDir.booleanValue();
  }
  
  private long getEnumObjects(final boolean includeHiddenFiles) throws InterruptedException { return ((Long)invoke(new Callable<Long>() {
          public Long call() throws IOException {
            boolean bool = (this.this$0.disposer.pIShellFolder == Win32ShellFolder2.this.getDesktopIShellFolder());
            return Long.valueOf(Win32ShellFolder2.this.getEnumObjects(this.this$0.disposer.pIShellFolder, bool, includeHiddenFiles));
          }
        }RuntimeException.class)).longValue(); }
  
  private native long getEnumObjects(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
  
  private native long getNextChild(long paramLong);
  
  private native void releaseEnumObjects(long paramLong);
  
  private static native long bindToObject(long paramLong1, long paramLong2);
  
  public File[] listFiles(final boolean includeHiddenFiles) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(getPath()); 
    try {
      File[] arrayOfFile = (File[])invoke(new Callable<File[]>() {
            public File[] call() throws InterruptedException {
              if (!Win32ShellFolder2.this.isDirectory())
                return null; 
              if (Win32ShellFolder2.this.isLink() && !Win32ShellFolder2.this.hasAttribute(536870912))
                return new File[0]; 
              Win32ShellFolder2 win32ShellFolder21 = Win32ShellFolderManager2.getDesktop();
              Win32ShellFolder2 win32ShellFolder22;
              long l1 = Win32ShellFolder2.this.getIShellFolder();
              ArrayList arrayList = new ArrayList();
              l2 = Win32ShellFolder2.this.getEnumObjects(includeHiddenFiles);
              if (l2 != 0L)
                try {
                  long l;
                  int i = 1342177280;
                  do {
                    l = Win32ShellFolder2.this.getNextChild(l2);
                    boolean bool = true;
                    if (l != 0L && (Win32ShellFolder2.getAttributes0(l1, l, i) & i) != 0) {
                      Win32ShellFolder2 win32ShellFolder2;
                      if (Win32ShellFolder2.this.equals(win32ShellFolder21) && win32ShellFolder22 != null && Win32ShellFolder2.pidlsEqual(l1, l, win32ShellFolder22.disposer.relativePIDL)) {
                        win32ShellFolder2 = win32ShellFolder22;
                      } else {
                        win32ShellFolder2 = new Win32ShellFolder2(Win32ShellFolder2.this, l);
                        bool = false;
                      } 
                      arrayList.add(win32ShellFolder2);
                    } 
                    if (!bool)
                      continue; 
                    Win32ShellFolder2.releasePIDL(l);
                  } while (l != 0L && !Thread.currentThread().isInterrupted());
                } finally {
                  Win32ShellFolder2.this.releaseEnumObjects(l2);
                }  
              return Thread.currentThread().isInterrupted() ? new File[0] : (File[])arrayList.toArray(new ShellFolder[arrayList.size()]);
            }
          }InterruptedException.class);
      return Win32ShellFolderManager2.checkFiles(arrayOfFile);
    } catch (InterruptedException interruptedException) {
      return new File[0];
    } 
  }
  
  Win32ShellFolder2 getChildByPath(final String filePath) throws InterruptedException { return (Win32ShellFolder2)invoke(new Callable<Win32ShellFolder2>() {
          public Win32ShellFolder2 call() {
            long l1 = Win32ShellFolder2.this.getIShellFolder();
            long l2 = Win32ShellFolder2.this.getEnumObjects(true);
            Win32ShellFolder2 win32ShellFolder2 = null;
            long l3;
            while ((l3 = Win32ShellFolder2.this.getNextChild(l2)) != 0L) {
              if (Win32ShellFolder2.getAttributes0(l1, l3, 1073741824) != 0) {
                String str = Win32ShellFolder2.getFileSystemPath(l1, l3);
                if (str != null && str.equalsIgnoreCase(filePath)) {
                  long l = Win32ShellFolder2.bindToObject(l1, l3);
                  win32ShellFolder2 = new Win32ShellFolder2(Win32ShellFolder2.this, l, l3, str);
                  break;
                } 
              } 
              Win32ShellFolder2.releasePIDL(l3);
            } 
            Win32ShellFolder2.this.releaseEnumObjects(l2);
            return win32ShellFolder2;
          }
        }InterruptedException.class); }
  
  public boolean isLink() {
    if (this.cachedIsLink == null)
      this.cachedIsLink = Boolean.valueOf(hasAttribute(65536)); 
    return this.cachedIsLink.booleanValue();
  }
  
  public boolean isHidden() { return hasAttribute(524288); }
  
  private static native long getLinkLocation(long paramLong1, long paramLong2, boolean paramBoolean);
  
  public ShellFolder getLinkLocation() { return getLinkLocation(true); }
  
  private ShellFolder getLinkLocation(final boolean resolve) { return (ShellFolder)invoke(new Callable<ShellFolder>() {
          public ShellFolder call() {
            if (!Win32ShellFolder2.this.isLink())
              return null; 
            Win32ShellFolder2 win32ShellFolder2 = null;
            long l = Win32ShellFolder2.getLinkLocation(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), resolve);
            if (l != 0L)
              try {
                win32ShellFolder2 = Win32ShellFolderManager2.createShellFolderFromRelativePIDL(Win32ShellFolder2.this.getDesktop(), l);
              } catch (InterruptedException interruptedException) {
              
              } catch (InternalError internalError) {} 
            return win32ShellFolder2;
          }
        }); }
  
  long parseDisplayName(final String name) throws IOException, InterruptedException { return ((Long)invoke(new Callable<Long>() {
          public Long call() throws IOException { return Long.valueOf(Win32ShellFolder2.parseDisplayName0(Win32ShellFolder2.this.getIShellFolder(), name)); }
        }IOException.class)).longValue(); }
  
  private static native long parseDisplayName0(long paramLong, String paramString) throws IOException;
  
  private static native String getDisplayNameOf(long paramLong1, long paramLong2, int paramInt);
  
  public String getDisplayName() {
    if (this.displayName == null)
      this.displayName = (String)invoke(new Callable<String>() {
            public String call() { return Win32ShellFolder2.getDisplayNameOf(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), 0); }
          }); 
    return this.displayName;
  }
  
  private static native String getFolderType(long paramLong);
  
  public String getFolderType() {
    if (this.folderType == null) {
      final long absolutePIDL = getAbsolutePIDL();
      this.folderType = (String)invoke(new Callable<String>() {
            public String call() { return Win32ShellFolder2.getFolderType(absolutePIDL); }
          });
    } 
    return this.folderType;
  }
  
  private native String getExecutableType(String paramString);
  
  public String getExecutableType() { return !isFileSystem() ? null : getExecutableType(getAbsolutePath()); }
  
  private static native long getIShellIcon(long paramLong);
  
  private static native int getIconIndex(long paramLong1, long paramLong2);
  
  private static native long getIcon(String paramString, boolean paramBoolean);
  
  private static native long extractIcon(long paramLong1, long paramLong2, boolean paramBoolean);
  
  private static native long getSystemIcon(int paramInt);
  
  private static native long getIconResource(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  private static native int[] getIconBits(long paramLong, int paramInt);
  
  private static native void disposeIcon(long paramLong);
  
  static native int[] getStandardViewButton0(int paramInt);
  
  private long getIShellIcon() {
    if (this.pIShellIcon == -1L)
      this.pIShellIcon = getIShellIcon(getIShellFolder()); 
    return this.pIShellIcon;
  }
  
  private static Image makeIcon(long paramLong, boolean paramBoolean) {
    if (paramLong != 0L && paramLong != -1L) {
      byte b = paramBoolean ? 32 : 16;
      int[] arrayOfInt = getIconBits(paramLong, b);
      if (arrayOfInt != null) {
        BufferedImage bufferedImage = new BufferedImage(b, b, 2);
        bufferedImage.setRGB(0, 0, b, b, arrayOfInt, 0, b);
        return bufferedImage;
      } 
    } 
    return null;
  }
  
  public Image getIcon(final boolean getLargeIcon) {
    Image image = paramBoolean ? this.largeIcon : this.smallIcon;
    if (image == null) {
      image = (Image)invoke(new Callable<Image>() {
            public Image call() {
              Image image = null;
              if (Win32ShellFolder2.this.isFileSystem()) {
                long l1 = (Win32ShellFolder2.this.parent != null) ? ((Win32ShellFolder2)Win32ShellFolder2.this.parent).getIShellIcon() : 0L;
                long l2 = Win32ShellFolder2.this.getRelativePIDL();
                int i = Win32ShellFolder2.getIconIndex(l1, l2);
                if (i > 0) {
                  Map map;
                  if (Win32ShellFolder2.this.isLink()) {
                    map = getLargeIcon ? largeLinkedSystemImages : smallLinkedSystemImages;
                  } else {
                    map = getLargeIcon ? largeSystemImages : smallSystemImages;
                  } 
                  image = (Image)map.get(Integer.valueOf(i));
                  if (image == null) {
                    long l = Win32ShellFolder2.getIcon(Win32ShellFolder2.this.getAbsolutePath(), getLargeIcon);
                    image = Win32ShellFolder2.makeIcon(l, getLargeIcon);
                    Win32ShellFolder2.disposeIcon(l);
                    if (image != null)
                      map.put(Integer.valueOf(i), image); 
                  } 
                } 
              } 
              if (image == null) {
                long l = Win32ShellFolder2.extractIcon(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), getLargeIcon);
                image = Win32ShellFolder2.makeIcon(l, getLargeIcon);
                Win32ShellFolder2.disposeIcon(l);
              } 
              if (image == null)
                image = Win32ShellFolder2.this.getIcon(getLargeIcon); 
              return image;
            }
          });
      if (paramBoolean) {
        this.largeIcon = image;
      } else {
        this.smallIcon = image;
      } 
    } 
    return image;
  }
  
  static Image getSystemIcon(SystemIcon paramSystemIcon) {
    long l = getSystemIcon(paramSystemIcon.getIconID());
    Image image = makeIcon(l, true);
    disposeIcon(l);
    return image;
  }
  
  static Image getShell32Icon(int paramInt, boolean paramBoolean) {
    boolean bool = true;
    byte b = paramBoolean ? 32 : 16;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    String str = (String)toolkit.getDesktopProperty("win.icon.shellIconBPP");
    if (str != null)
      bool = str.equals("4"); 
    long l = getIconResource("shell32.dll", paramInt, b, b, bool);
    if (l != 0L) {
      Image image = makeIcon(l, paramBoolean);
      disposeIcon(l);
      return image;
    } 
    return null;
  }
  
  public File getCanonicalFile() { return this; }
  
  public boolean isSpecial() { return (this.isPersonal || !isFileSystem() || this == getDesktop()); }
  
  public int compareTo(File paramFile) { return !(paramFile instanceof Win32ShellFolder2) ? ((isFileSystem() && !isSpecial()) ? super.compareTo(paramFile) : -1) : Win32ShellFolderManager2.compareShellFolders(this, (Win32ShellFolder2)paramFile); }
  
  public ShellFolderColumnInfo[] getFolderColumns() { return (ShellFolderColumnInfo[])invoke(new Callable<ShellFolderColumnInfo[]>() {
          public ShellFolderColumnInfo[] call() {
            ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = Win32ShellFolder2.this.doGetColumnInfo(Win32ShellFolder2.this.getIShellFolder());
            if (arrayOfShellFolderColumnInfo != null) {
              ArrayList arrayList = new ArrayList();
              for (byte b = 0; b < arrayOfShellFolderColumnInfo.length; b++) {
                ShellFolderColumnInfo shellFolderColumnInfo = arrayOfShellFolderColumnInfo[b];
                if (shellFolderColumnInfo != null) {
                  shellFolderColumnInfo.setAlignment(Integer.valueOf((shellFolderColumnInfo.getAlignment().intValue() == 1) ? 4 : ((shellFolderColumnInfo.getAlignment().intValue() == 2) ? 0 : 10)));
                  shellFolderColumnInfo.setComparator(new Win32ShellFolder2.ColumnComparator(Win32ShellFolder2.this, b));
                  arrayList.add(shellFolderColumnInfo);
                } 
              } 
              arrayOfShellFolderColumnInfo = new ShellFolderColumnInfo[arrayList.size()];
              arrayList.toArray(arrayOfShellFolderColumnInfo);
            } 
            return arrayOfShellFolderColumnInfo;
          }
        }); }
  
  public Object getFolderColumnValue(final int column) { return invoke(new Callable<Object>() {
          public Object call() throws ObjectStreamException { return Win32ShellFolder2.this.doGetColumnValue(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), column); }
        }); }
  
  private native ShellFolderColumnInfo[] doGetColumnInfo(long paramLong);
  
  private native Object doGetColumnValue(long paramLong1, long paramLong2, int paramInt);
  
  private static native int compareIDsByColumn(long paramLong1, long paramLong2, long paramLong3, int paramInt);
  
  public void sortChildren(final List<? extends File> files) { invoke(new Callable<Void>() {
          public Void call() throws InterruptedException {
            Collections.sort(files, new Win32ShellFolder2.ColumnComparator(Win32ShellFolder2.this, 0));
            return null;
          }
        }); }
  
  static  {
    initIDs();
    smallSystemImages = new HashMap();
    largeSystemImages = new HashMap();
    smallLinkedSystemImages = new HashMap();
    largeLinkedSystemImages = new HashMap();
  }
  
  private static class ColumnComparator extends Object implements Comparator<File> {
    private final Win32ShellFolder2 shellFolder;
    
    private final int columnIdx;
    
    public ColumnComparator(Win32ShellFolder2 param1Win32ShellFolder2, int param1Int) {
      this.shellFolder = param1Win32ShellFolder2;
      this.columnIdx = param1Int;
    }
    
    public int compare(final File o, final File o1) {
      Integer integer = (Integer)ShellFolder.invoke(new Callable<Integer>() {
            public Integer call() { return (o instanceof Win32ShellFolder2 && o1 instanceof Win32ShellFolder2) ? Integer.valueOf(Win32ShellFolder2.compareIDsByColumn(Win32ShellFolder2.ColumnComparator.this.shellFolder.getIShellFolder(), ((Win32ShellFolder2)o).getRelativePIDL(), ((Win32ShellFolder2)o1).getRelativePIDL(), Win32ShellFolder2.ColumnComparator.this.columnIdx)) : Integer.valueOf(0); }
          });
      return (integer == null) ? 0 : integer.intValue();
    }
  }
  
  static class FolderDisposer implements DisposerRecord {
    long absolutePIDL;
    
    long pIShellFolder;
    
    long relativePIDL;
    
    boolean disposed;
    
    public void dispose() {
      if (this.disposed)
        return; 
      ShellFolder.invoke(new Callable<Void>() {
            public Void call() throws InterruptedException {
              if (Win32ShellFolder2.FolderDisposer.this.relativePIDL != 0L)
                Win32ShellFolder2.releasePIDL(Win32ShellFolder2.FolderDisposer.this.relativePIDL); 
              if (Win32ShellFolder2.FolderDisposer.this.absolutePIDL != 0L)
                Win32ShellFolder2.releasePIDL(Win32ShellFolder2.FolderDisposer.this.absolutePIDL); 
              if (Win32ShellFolder2.FolderDisposer.this.pIShellFolder != 0L)
                Win32ShellFolder2.releaseIShellFolder(Win32ShellFolder2.FolderDisposer.this.pIShellFolder); 
              return null;
            }
          });
      this.disposed = true;
    }
  }
  
  public enum SystemIcon {
    IDI_APPLICATION(32512),
    IDI_HAND(32513),
    IDI_ERROR(32513),
    IDI_QUESTION(32514),
    IDI_EXCLAMATION(32515),
    IDI_WARNING(32515),
    IDI_ASTERISK(32516),
    IDI_INFORMATION(32516),
    IDI_WINLOGO(32517);
    
    private final int iconID;
    
    SystemIcon(int param1Int1) throws IOException, InterruptedException { this.iconID = param1Int1; }
    
    public int getIconID() { return this.iconID; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\shell\Win32ShellFolder2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */