package java.awt;

import java.awt.peer.FileDialogPeer;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AWTAccessor;

public class FileDialog extends Dialog {
  public static final int LOAD = 0;
  
  public static final int SAVE = 1;
  
  int mode;
  
  String dir;
  
  String file;
  
  private File[] files;
  
  private boolean multipleMode = false;
  
  FilenameFilter filter;
  
  private static final String base = "filedlg";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = 5035145889651310422L;
  
  private static native void initIDs();
  
  public FileDialog(Frame paramFrame) { this(paramFrame, "", 0); }
  
  public FileDialog(Frame paramFrame, String paramString) { this(paramFrame, paramString, 0); }
  
  public FileDialog(Frame paramFrame, String paramString, int paramInt) {
    super(paramFrame, paramString, true);
    setMode(paramInt);
    setLayout(null);
  }
  
  public FileDialog(Dialog paramDialog) { this(paramDialog, "", 0); }
  
  public FileDialog(Dialog paramDialog, String paramString) { this(paramDialog, paramString, 0); }
  
  public FileDialog(Dialog paramDialog, String paramString, int paramInt) {
    super(paramDialog, paramString, true);
    setMode(paramInt);
    setLayout(null);
  }
  
  String constructComponentName() {
    synchronized (FileDialog.class) {
      return "filedlg" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.parent != null && this.parent.getPeer() == null)
        this.parent.addNotify(); 
      if (this.peer == null)
        this.peer = getToolkit().createFileDialog(this); 
      super.addNotify();
    } 
  }
  
  public int getMode() { return this.mode; }
  
  public void setMode(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
        this.mode = paramInt;
        return;
    } 
    throw new IllegalArgumentException("illegal file dialog mode");
  }
  
  public String getDirectory() { return this.dir; }
  
  public void setDirectory(String paramString) {
    this.dir = (paramString != null && paramString.equals("")) ? null : paramString;
    FileDialogPeer fileDialogPeer = (FileDialogPeer)this.peer;
    if (fileDialogPeer != null)
      fileDialogPeer.setDirectory(this.dir); 
  }
  
  public String getFile() { return this.file; }
  
  public File[] getFiles() {
    synchronized (getObjectLock()) {
      if (this.files != null)
        return (File[])this.files.clone(); 
      return new File[0];
    } 
  }
  
  private void setFiles(File[] paramArrayOfFile) {
    synchronized (getObjectLock()) {
      this.files = paramArrayOfFile;
    } 
  }
  
  public void setFile(String paramString) {
    this.file = (paramString != null && paramString.equals("")) ? null : paramString;
    FileDialogPeer fileDialogPeer = (FileDialogPeer)this.peer;
    if (fileDialogPeer != null)
      fileDialogPeer.setFile(this.file); 
  }
  
  public void setMultipleMode(boolean paramBoolean) {
    synchronized (getObjectLock()) {
      this.multipleMode = paramBoolean;
    } 
  }
  
  public boolean isMultipleMode() {
    synchronized (getObjectLock()) {
      return this.multipleMode;
    } 
  }
  
  public FilenameFilter getFilenameFilter() { return this.filter; }
  
  public void setFilenameFilter(FilenameFilter paramFilenameFilter) {
    this.filter = paramFilenameFilter;
    FileDialogPeer fileDialogPeer = (FileDialogPeer)this.peer;
    if (fileDialogPeer != null)
      fileDialogPeer.setFilenameFilter(paramFilenameFilter); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    if (this.dir != null && this.dir.equals(""))
      this.dir = null; 
    if (this.file != null && this.file.equals(""))
      this.file = null; 
  }
  
  protected String paramString() {
    String str = super.paramString();
    str = str + ",dir= " + this.dir;
    str = str + ",file= " + this.file;
    return str + ((this.mode == 0) ? ",load" : ",save");
  }
  
  boolean postsOldMouseEvents() { return false; }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setFileDialogAccessor(new AWTAccessor.FileDialogAccessor() {
          public void setFiles(FileDialog param1FileDialog, File[] param1ArrayOfFile) { param1FileDialog.setFiles(param1ArrayOfFile); }
          
          public void setFile(FileDialog param1FileDialog, String param1String) { param1FileDialog.file = "".equals(param1String) ? null : param1String; }
          
          public void setDirectory(FileDialog param1FileDialog, String param1String) { param1FileDialog.dir = "".equals(param1String) ? null : param1String; }
          
          public boolean isMultipleMode(FileDialog param1FileDialog) {
            synchronized (param1FileDialog.getObjectLock()) {
              return param1FileDialog.multipleMode;
            } 
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\FileDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */