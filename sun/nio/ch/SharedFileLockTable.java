package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SharedFileLockTable extends FileLockTable {
  private static ConcurrentHashMap<FileKey, List<FileLockReference>> lockMap = new ConcurrentHashMap();
  
  private static ReferenceQueue<FileLock> queue = new ReferenceQueue();
  
  private final Channel channel;
  
  private final FileKey fileKey;
  
  SharedFileLockTable(Channel paramChannel, FileDescriptor paramFileDescriptor) throws IOException {
    this.channel = paramChannel;
    this.fileKey = FileKey.create(paramFileDescriptor);
  }
  
  public void add(FileLock paramFileLock) throws OverlappingFileLockException {
    List list = (List)lockMap.get(this.fileKey);
    while (true) {
      if (list == null) {
        List list1;
        list = new ArrayList(2);
        synchronized (list) {
          list1 = (List)lockMap.putIfAbsent(this.fileKey, list);
          if (list1 == null) {
            list.add(new FileLockReference(paramFileLock, queue, this.fileKey));
            break;
          } 
        } 
        list = list1;
      } 
      synchronized (list) {
        List list1 = (List)lockMap.get(this.fileKey);
        if (list == list1) {
          checkList(list, paramFileLock.position(), paramFileLock.size());
          list.add(new FileLockReference(paramFileLock, queue, this.fileKey));
          break;
        } 
        list = list1;
      } 
    } 
    removeStaleEntries();
  }
  
  private void removeKeyIfEmpty(FileKey paramFileKey, List<FileLockReference> paramList) {
    assert Thread.holdsLock(paramList);
    assert lockMap.get(paramFileKey) == paramList;
    if (paramList.isEmpty())
      lockMap.remove(paramFileKey); 
  }
  
  public void remove(FileLock paramFileLock) throws OverlappingFileLockException {
    assert paramFileLock != null;
    List list = (List)lockMap.get(this.fileKey);
    if (list == null)
      return; 
    synchronized (list) {
      for (byte b = 0; b < list.size(); b++) {
        FileLockReference fileLockReference = (FileLockReference)list.get(b);
        FileLock fileLock = (FileLock)fileLockReference.get();
        if (fileLock == paramFileLock) {
          assert fileLock != null && fileLock.acquiredBy() == this.channel;
          fileLockReference.clear();
          list.remove(b);
          break;
        } 
      } 
    } 
  }
  
  public List<FileLock> removeAll() {
    ArrayList arrayList = new ArrayList();
    List list = (List)lockMap.get(this.fileKey);
    if (list != null)
      synchronized (list) {
        for (byte b = 0; b < list.size(); b++) {
          FileLockReference fileLockReference = (FileLockReference)list.get(b);
          FileLock fileLock = (FileLock)fileLockReference.get();
          if (fileLock != null && fileLock.acquiredBy() == this.channel) {
            fileLockReference.clear();
            list.remove(b);
            arrayList.add(fileLock);
            continue;
          } 
        } 
        removeKeyIfEmpty(this.fileKey, list);
      }  
    return arrayList;
  }
  
  public void replace(FileLock paramFileLock1, FileLock paramFileLock2) {
    List list = (List)lockMap.get(this.fileKey);
    assert list != null;
    synchronized (list) {
      for (byte b = 0; b < list.size(); b++) {
        FileLockReference fileLockReference = (FileLockReference)list.get(b);
        FileLock fileLock = (FileLock)fileLockReference.get();
        if (fileLock == paramFileLock1) {
          fileLockReference.clear();
          list.set(b, new FileLockReference(paramFileLock2, queue, this.fileKey));
          break;
        } 
      } 
    } 
  }
  
  private void checkList(List<FileLockReference> paramList, long paramLong1, long paramLong2) throws OverlappingFileLockException {
    assert Thread.holdsLock(paramList);
    for (FileLockReference fileLockReference : paramList) {
      FileLock fileLock = (FileLock)fileLockReference.get();
      if (fileLock != null && fileLock.overlaps(paramLong1, paramLong2))
        throw new OverlappingFileLockException(); 
    } 
  }
  
  private void removeStaleEntries() {
    FileLockReference fileLockReference;
    while ((fileLockReference = (FileLockReference)queue.poll()) != null) {
      FileKey fileKey1 = fileLockReference.fileKey();
      List list = (List)lockMap.get(fileKey1);
      if (list != null)
        synchronized (list) {
          list.remove(fileLockReference);
          removeKeyIfEmpty(fileKey1, list);
        }  
    } 
  }
  
  private static class FileLockReference extends WeakReference<FileLock> {
    private FileKey fileKey;
    
    FileLockReference(FileLock param1FileLock, ReferenceQueue<FileLock> param1ReferenceQueue, FileKey param1FileKey) {
      super(param1FileLock, param1ReferenceQueue);
      this.fileKey = param1FileKey;
    }
    
    FileKey fileKey() { return this.fileKey; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\SharedFileLockTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */