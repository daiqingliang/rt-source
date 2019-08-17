package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.List;

abstract class FileLockTable {
  public static FileLockTable newSharedFileLockTable(Channel paramChannel, FileDescriptor paramFileDescriptor) throws IOException { return new SharedFileLockTable(paramChannel, paramFileDescriptor); }
  
  public abstract void add(FileLock paramFileLock) throws OverlappingFileLockException;
  
  public abstract void remove(FileLock paramFileLock) throws OverlappingFileLockException;
  
  public abstract List<FileLock> removeAll();
  
  public abstract void replace(FileLock paramFileLock1, FileLock paramFileLock2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\FileLockTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */