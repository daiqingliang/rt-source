package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Set;

class WindowsFileAttributeViews {
  static Basic createBasicView(WindowsPath paramWindowsPath, boolean paramBoolean) { return new Basic(paramWindowsPath, paramBoolean); }
  
  static Dos createDosView(WindowsPath paramWindowsPath, boolean paramBoolean) { return new Dos(paramWindowsPath, paramBoolean); }
  
  private static class Basic extends AbstractBasicFileAttributeView {
    final WindowsPath file;
    
    final boolean followLinks;
    
    Basic(WindowsPath param1WindowsPath, boolean param1Boolean) {
      this.file = param1WindowsPath;
      this.followLinks = param1Boolean;
    }
    
    public WindowsFileAttributes readAttributes() throws IOException {
      this.file.checkRead();
      try {
        return WindowsFileAttributes.get(this.file, this.followLinks);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(this.file);
        return null;
      } 
    }
    
    private long adjustForFatEpoch(long param1Long) { return (param1Long != -1L && param1Long < 119600064000000000L) ? 119600064000000000L : param1Long; }
    
    void setFileTimes(long param1Long1, long param1Long2, long param1Long3) throws IOException {
      l = -1L;
      try {
        int i = 33554432;
        if (!this.followLinks && this.file.getFileSystem().supportsLinks())
          i |= 0x200000; 
        l = WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), 256, 7, 3, i);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(this.file);
      } 
      try {
        WindowsNativeDispatcher.SetFileTime(l, param1Long1, param1Long2, param1Long3);
      } catch (WindowsException windowsException) {
        if (this.followLinks && windowsException.lastError() == 87)
          try {
            if (WindowsFileStore.create(this.file).type().equals("FAT")) {
              WindowsNativeDispatcher.SetFileTime(l, adjustForFatEpoch(param1Long1), adjustForFatEpoch(param1Long2), adjustForFatEpoch(param1Long3));
              windowsException = null;
            } 
          } catch (SecurityException securityException) {
          
          } catch (WindowsException windowsException1) {
          
          } catch (IOException iOException) {} 
        if (windowsException != null)
          windowsException.rethrowAsIOException(this.file); 
      } finally {
        WindowsNativeDispatcher.CloseHandle(l);
      } 
    }
    
    public void setTimes(FileTime param1FileTime1, FileTime param1FileTime2, FileTime param1FileTime3) throws IOException {
      if (param1FileTime1 == null && param1FileTime2 == null && param1FileTime3 == null)
        return; 
      this.file.checkWrite();
      long l1 = (param1FileTime3 == null) ? -1L : WindowsFileAttributes.toWindowsTime(param1FileTime3);
      long l2 = (param1FileTime2 == null) ? -1L : WindowsFileAttributes.toWindowsTime(param1FileTime2);
      long l3 = (param1FileTime1 == null) ? -1L : WindowsFileAttributes.toWindowsTime(param1FileTime1);
      setFileTimes(l1, l2, l3);
    }
  }
  
  static class Dos extends Basic implements DosFileAttributeView {
    private static final String READONLY_NAME = "readonly";
    
    private static final String ARCHIVE_NAME = "archive";
    
    private static final String SYSTEM_NAME = "system";
    
    private static final String HIDDEN_NAME = "hidden";
    
    private static final String ATTRIBUTES_NAME = "attributes";
    
    static final Set<String> dosAttributeNames = Util.newSet(basicAttributeNames, new String[] { "readonly", "archive", "system", "hidden", "attributes" });
    
    Dos(WindowsPath param1WindowsPath, boolean param1Boolean) { super(param1WindowsPath, param1Boolean); }
    
    public String name() { return "dos"; }
    
    public void setAttribute(String param1String, Object param1Object) throws IOException {
      if (param1String.equals("readonly")) {
        setReadOnly(((Boolean)param1Object).booleanValue());
        return;
      } 
      if (param1String.equals("archive")) {
        setArchive(((Boolean)param1Object).booleanValue());
        return;
      } 
      if (param1String.equals("system")) {
        setSystem(((Boolean)param1Object).booleanValue());
        return;
      } 
      if (param1String.equals("hidden")) {
        setHidden(((Boolean)param1Object).booleanValue());
        return;
      } 
      super.setAttribute(param1String, param1Object);
    }
    
    public Map<String, Object> readAttributes(String[] param1ArrayOfString) throws IOException {
      AbstractBasicFileAttributeView.AttributesBuilder attributesBuilder = AbstractBasicFileAttributeView.AttributesBuilder.create(dosAttributeNames, param1ArrayOfString);
      WindowsFileAttributes windowsFileAttributes = readAttributes();
      addRequestedBasicAttributes(windowsFileAttributes, attributesBuilder);
      if (attributesBuilder.match("readonly"))
        attributesBuilder.add("readonly", Boolean.valueOf(windowsFileAttributes.isReadOnly())); 
      if (attributesBuilder.match("archive"))
        attributesBuilder.add("archive", Boolean.valueOf(windowsFileAttributes.isArchive())); 
      if (attributesBuilder.match("system"))
        attributesBuilder.add("system", Boolean.valueOf(windowsFileAttributes.isSystem())); 
      if (attributesBuilder.match("hidden"))
        attributesBuilder.add("hidden", Boolean.valueOf(windowsFileAttributes.isHidden())); 
      if (attributesBuilder.match("attributes"))
        attributesBuilder.add("attributes", Integer.valueOf(windowsFileAttributes.attributes())); 
      return attributesBuilder.unmodifiableMap();
    }
    
    private void updateAttributes(int param1Int, boolean param1Boolean) throws IOException {
      this.file.checkWrite();
      String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
      try {
        int i = WindowsNativeDispatcher.GetFileAttributes(str);
        int j = i;
        if (param1Boolean) {
          j |= param1Int;
        } else {
          j &= (param1Int ^ 0xFFFFFFFF);
        } 
        if (j != i)
          WindowsNativeDispatcher.SetFileAttributes(str, j); 
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(this.file);
      } 
    }
    
    public void setReadOnly(boolean param1Boolean) throws IOException { updateAttributes(1, param1Boolean); }
    
    public void setHidden(boolean param1Boolean) throws IOException { updateAttributes(2, param1Boolean); }
    
    public void setArchive(boolean param1Boolean) throws IOException { updateAttributes(32, param1Boolean); }
    
    public void setSystem(boolean param1Boolean) throws IOException { updateAttributes(4, param1Boolean); }
    
    void setAttributes(WindowsFileAttributes param1WindowsFileAttributes) throws IOException {
      byte b = 0;
      if (param1WindowsFileAttributes.isReadOnly())
        b |= true; 
      if (param1WindowsFileAttributes.isHidden())
        b |= 0x2; 
      if (param1WindowsFileAttributes.isArchive())
        b |= 0x20; 
      if (param1WindowsFileAttributes.isSystem())
        b |= 0x4; 
      updateAttributes(b, true);
      setFileTimes(WindowsFileAttributes.toWindowsTime(param1WindowsFileAttributes.creationTime()), WindowsFileAttributes.toWindowsTime(param1WindowsFileAttributes.lastModifiedTime()), WindowsFileAttributes.toWindowsTime(param1WindowsFileAttributes.lastAccessTime()));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsFileAttributeViews.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */