package sun.nio.fs;

import com.sun.nio.file.ExtendedWatchEventModifier;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sun.misc.Unsafe;

class WindowsWatchService extends AbstractWatchService {
  private static final int WAKEUP_COMPLETION_KEY = 0;
  
  private final Poller poller;
  
  private static final int ALL_FILE_NOTIFY_EVENTS = 351;
  
  WindowsWatchService(WindowsFileSystem paramWindowsFileSystem) throws IOException {
    long l = 0L;
    try {
      l = WindowsNativeDispatcher.CreateIoCompletionPort(-1L, 0L, 0L);
    } catch (WindowsException windowsException) {
      throw new IOException(windowsException.getMessage());
    } 
    this.poller = new Poller(paramWindowsFileSystem, this, l);
    this.poller.start();
  }
  
  WatchKey register(Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs) throws IOException { return this.poller.register(paramPath, paramArrayOfKind, paramVarArgs); }
  
  void implClose() throws IOException { this.poller.close(); }
  
  private static class FileKey {
    private final int volSerialNumber;
    
    private final int fileIndexHigh;
    
    private final int fileIndexLow;
    
    FileKey(int param1Int1, int param1Int2, int param1Int3) {
      this.volSerialNumber = param1Int1;
      this.fileIndexHigh = param1Int2;
      this.fileIndexLow = param1Int3;
    }
    
    public int hashCode() { return this.volSerialNumber ^ this.fileIndexHigh ^ this.fileIndexLow; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof FileKey))
        return false; 
      FileKey fileKey = (FileKey)param1Object;
      return (this.volSerialNumber != fileKey.volSerialNumber) ? false : ((this.fileIndexHigh != fileKey.fileIndexHigh) ? false : ((this.fileIndexLow == fileKey.fileIndexLow)));
    }
  }
  
  private static class Poller extends AbstractPoller {
    private static final Unsafe UNSAFE = Unsafe.getUnsafe();
    
    private static final short SIZEOF_DWORD = 4;
    
    private static final short SIZEOF_OVERLAPPED = 32;
    
    private static final short OFFSETOF_HEVENT = (UNSAFE.addressSize() == 4) ? 16 : 24;
    
    private static final short OFFSETOF_NEXTENTRYOFFSET = 0;
    
    private static final short OFFSETOF_ACTION = 4;
    
    private static final short OFFSETOF_FILENAMELENGTH = 8;
    
    private static final short OFFSETOF_FILENAME = 12;
    
    private static final int CHANGES_BUFFER_SIZE = 16384;
    
    private final WindowsFileSystem fs;
    
    private final WindowsWatchService watcher;
    
    private final long port;
    
    private final Map<Integer, WindowsWatchService.WindowsWatchKey> ck2key;
    
    private final Map<WindowsWatchService.FileKey, WindowsWatchService.WindowsWatchKey> fk2key;
    
    private int lastCompletionKey;
    
    Poller(WindowsFileSystem param1WindowsFileSystem, WindowsWatchService param1WindowsWatchService, long param1Long) {
      this.fs = param1WindowsFileSystem;
      this.watcher = param1WindowsWatchService;
      this.port = param1Long;
      this.ck2key = new HashMap();
      this.fk2key = new HashMap();
      this.lastCompletionKey = 0;
    }
    
    void wakeup() throws IOException {
      try {
        WindowsNativeDispatcher.PostQueuedCompletionStatus(this.port, 0L);
      } catch (WindowsException windowsException) {
        throw new IOException(windowsException.getMessage());
      } 
    }
    
    Object implRegister(Path param1Path, Set<? extends WatchEvent.Kind<?>> param1Set, WatchEvent.Modifier... param1VarArgs) {
      WindowsPath windowsPath = (WindowsPath)param1Path;
      boolean bool = false;
      for (WatchEvent.Modifier modifier : param1VarArgs) {
        if (modifier == ExtendedWatchEventModifier.FILE_TREE) {
          bool = true;
        } else {
          if (modifier == null)
            return new NullPointerException(); 
          if (!(modifier instanceof com.sun.nio.file.SensitivityWatchEventModifier))
            return new UnsupportedOperationException("Modifier not supported"); 
        } 
      } 
      try {
        l = WindowsNativeDispatcher.CreateFile(windowsPath.getPathForWin32Calls(), 1, 7, 3, 1107296256);
      } catch (WindowsException windowsException) {
        return windowsException.asIOException(windowsPath);
      } 
      bool1 = false;
      try {
        WindowsWatchService.WindowsWatchKey windowsWatchKey2;
        WindowsFileAttributes windowsFileAttributes;
        try {
          windowsFileAttributes = WindowsFileAttributes.readAttributes(l);
        } catch (WindowsException windowsException) {
          return windowsException.asIOException(windowsPath);
        } 
        if (!windowsFileAttributes.isDirectory())
          return new NotDirectoryException(windowsPath.getPathForExceptionMessage()); 
        WindowsWatchService.FileKey fileKey = new WindowsWatchService.FileKey(windowsFileAttributes.volSerialNumber(), windowsFileAttributes.fileIndexHigh(), windowsFileAttributes.fileIndexLow());
        WindowsWatchService.WindowsWatchKey windowsWatchKey1 = (WindowsWatchService.WindowsWatchKey)this.fk2key.get(fileKey);
        if (windowsWatchKey1 != null && bool == windowsWatchKey1.watchSubtree()) {
          windowsWatchKey1.setEvents(param1Set);
          return windowsWatchKey1;
        } 
        int i = ++this.lastCompletionKey;
        if (i == 0)
          i = ++this.lastCompletionKey; 
        try {
          WindowsNativeDispatcher.CreateIoCompletionPort(l, this.port, i);
        } catch (WindowsException windowsException) {
          return new IOException(windowsException.getMessage());
        } 
        char c = '䀤';
        NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(c);
        long l1 = nativeBuffer.address();
        long l2 = l1 + c - 32L;
        long l3 = l2 - 4L;
        UNSAFE.setMemory(l2, 32L, (byte)0);
        try {
          createAndAttachEvent(l2);
          WindowsNativeDispatcher.ReadDirectoryChangesW(l, l1, 16384, bool, 351, l3, l2);
        } catch (WindowsException null) {
          closeAttachedEvent(l2);
          nativeBuffer.release();
          return new IOException(windowsWatchKey2.getMessage());
        } 
        if (windowsWatchKey1 == null) {
          windowsWatchKey2 = (new WindowsWatchService.WindowsWatchKey(windowsPath, this.watcher, fileKey)).init(l, param1Set, bool, nativeBuffer, l3, l2, i);
          this.fk2key.put(fileKey, windowsWatchKey2);
        } else {
          this.ck2key.remove(Integer.valueOf(windowsWatchKey1.completionKey()));
          releaseResources(windowsWatchKey1);
          windowsWatchKey2 = windowsWatchKey1.init(l, param1Set, bool, nativeBuffer, l3, l2, i);
        } 
        this.ck2key.put(Integer.valueOf(i), windowsWatchKey2);
        bool1 = true;
        return windowsWatchKey2;
      } finally {
        if (!bool1)
          WindowsNativeDispatcher.CloseHandle(l); 
      } 
    }
    
    private void releaseResources(WindowsWatchService.WindowsWatchKey param1WindowsWatchKey) {
      if (!param1WindowsWatchKey.isErrorStartingOverlapped())
        try {
          WindowsNativeDispatcher.CancelIo(param1WindowsWatchKey.handle());
          WindowsNativeDispatcher.GetOverlappedResult(param1WindowsWatchKey.handle(), param1WindowsWatchKey.overlappedAddress());
        } catch (WindowsException windowsException) {} 
      WindowsNativeDispatcher.CloseHandle(param1WindowsWatchKey.handle());
      closeAttachedEvent(param1WindowsWatchKey.overlappedAddress());
      param1WindowsWatchKey.buffer().cleaner().clean();
    }
    
    private void createAndAttachEvent(long param1Long) throws WindowsException {
      long l = WindowsNativeDispatcher.CreateEvent(false, false);
      UNSAFE.putAddress(param1Long + OFFSETOF_HEVENT, l);
    }
    
    private void closeAttachedEvent(long param1Long) throws WindowsException {
      long l = UNSAFE.getAddress(param1Long + OFFSETOF_HEVENT);
      if (l != 0L && l != -1L)
        WindowsNativeDispatcher.CloseHandle(l); 
    }
    
    void implCancelKey(WatchKey param1WatchKey) {
      WindowsWatchService.WindowsWatchKey windowsWatchKey = (WindowsWatchService.WindowsWatchKey)param1WatchKey;
      if (windowsWatchKey.isValid()) {
        this.fk2key.remove(windowsWatchKey.fileKey());
        this.ck2key.remove(Integer.valueOf(windowsWatchKey.completionKey()));
        windowsWatchKey.invalidate();
      } 
    }
    
    void implCloseAll() throws IOException {
      this.ck2key.values().forEach(WindowsWatchService.WindowsWatchKey::invalidate);
      this.fk2key.clear();
      this.ck2key.clear();
      WindowsNativeDispatcher.CloseHandle(this.port);
    }
    
    private WatchEvent.Kind<?> translateActionToEvent(int param1Int) {
      switch (param1Int) {
        case 3:
          return StandardWatchEventKinds.ENTRY_MODIFY;
        case 1:
        case 5:
          return StandardWatchEventKinds.ENTRY_CREATE;
        case 2:
        case 4:
          return StandardWatchEventKinds.ENTRY_DELETE;
      } 
      return null;
    }
    
    private void processEvents(WindowsWatchService.WindowsWatchKey param1WindowsWatchKey, int param1Int) {
      int i;
      long l = param1WindowsWatchKey.buffer().address();
      do {
        int j = UNSAFE.getInt(l + 4L);
        WatchEvent.Kind kind = translateActionToEvent(j);
        if (param1WindowsWatchKey.events().contains(kind)) {
          int k = UNSAFE.getInt(l + 8L);
          if (k % 2 != 0)
            throw new AssertionError("FileNameLength is not a multiple of 2"); 
          char[] arrayOfChar = new char[k / 2];
          UNSAFE.copyMemory(null, l + 12L, arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, k);
          WindowsPath windowsPath = WindowsPath.createFromNormalizedPath(this.fs, new String(arrayOfChar));
          param1WindowsWatchKey.signalEvent(kind, windowsPath);
        } 
        i = UNSAFE.getInt(l + 0L);
        l += i;
      } while (i != 0);
    }
    
    public void run() throws IOException {
      while (true) {
        WindowsNativeDispatcher.CompletionStatus completionStatus;
        try {
          completionStatus = WindowsNativeDispatcher.GetQueuedCompletionStatus(this.port);
        } catch (WindowsException windowsException) {
          windowsException.printStackTrace();
          return;
        } 
        if (completionStatus.completionKey() == 0L) {
          boolean bool1 = processRequests();
          if (bool1)
            return; 
          continue;
        } 
        WindowsWatchService.WindowsWatchKey windowsWatchKey = (WindowsWatchService.WindowsWatchKey)this.ck2key.get(Integer.valueOf((int)completionStatus.completionKey()));
        if (windowsWatchKey == null)
          continue; 
        boolean bool = false;
        int i = completionStatus.error();
        int j = completionStatus.bytesTransferred();
        if (i == 1022) {
          windowsWatchKey.signalEvent(StandardWatchEventKinds.OVERFLOW, null);
        } else if (i != 0 && i != 234) {
          bool = true;
        } else {
          if (j > 0) {
            processEvents(windowsWatchKey, j);
          } else if (i == 0) {
            windowsWatchKey.signalEvent(StandardWatchEventKinds.OVERFLOW, null);
          } 
          try {
            WindowsNativeDispatcher.ReadDirectoryChangesW(windowsWatchKey.handle(), windowsWatchKey.buffer().address(), 16384, windowsWatchKey.watchSubtree(), 351, windowsWatchKey.countAddress(), windowsWatchKey.overlappedAddress());
          } catch (WindowsException windowsException) {
            bool = true;
            windowsWatchKey.setErrorStartingOverlapped(true);
          } 
        } 
        if (bool) {
          implCancelKey(windowsWatchKey);
          windowsWatchKey.signal();
        } 
      } 
    }
  }
  
  private static class WindowsWatchKey extends AbstractWatchKey {
    private final WindowsWatchService.FileKey fileKey;
    
    private Set<? extends WatchEvent.Kind<?>> events;
    
    private boolean watchSubtree;
    
    private NativeBuffer buffer;
    
    private long countAddress;
    
    private long overlappedAddress;
    
    private int completionKey;
    
    private boolean errorStartingOverlapped;
    
    WindowsWatchKey(Path param1Path, AbstractWatchService param1AbstractWatchService, WindowsWatchService.FileKey param1FileKey) {
      super(param1Path, param1AbstractWatchService);
      this.fileKey = param1FileKey;
    }
    
    WindowsWatchKey init(long param1Long1, Set<? extends WatchEvent.Kind<?>> param1Set, boolean param1Boolean, NativeBuffer param1NativeBuffer, long param1Long2, long param1Long3, int param1Int) {
      this.handle = param1Long1;
      this.events = param1Set;
      this.watchSubtree = param1Boolean;
      this.buffer = param1NativeBuffer;
      this.countAddress = param1Long2;
      this.overlappedAddress = param1Long3;
      this.completionKey = param1Int;
      return this;
    }
    
    long handle() { return this.handle; }
    
    Set<? extends WatchEvent.Kind<?>> events() { return this.events; }
    
    void setEvents(Set<? extends WatchEvent.Kind<?>> param1Set) { this.events = param1Set; }
    
    boolean watchSubtree() { return this.watchSubtree; }
    
    NativeBuffer buffer() { return this.buffer; }
    
    long countAddress() { return this.countAddress; }
    
    long overlappedAddress() { return this.overlappedAddress; }
    
    WindowsWatchService.FileKey fileKey() { return this.fileKey; }
    
    int completionKey() { return this.completionKey; }
    
    void setErrorStartingOverlapped(boolean param1Boolean) { this.errorStartingOverlapped = param1Boolean; }
    
    boolean isErrorStartingOverlapped() { return this.errorStartingOverlapped; }
    
    void invalidate() throws IOException {
      ((WindowsWatchService)watcher()).poller.releaseResources(this);
      this.handle = -1L;
      this.buffer = null;
      this.countAddress = 0L;
      this.overlappedAddress = 0L;
      this.errorStartingOverlapped = false;
    }
    
    public boolean isValid() { return (this.handle != -1L); }
    
    public void cancel() throws IOException {
      if (isValid())
        ((WindowsWatchService)watcher()).poller.cancel(this); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsWatchService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */