package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

class FileTreeIterator extends Object implements Iterator<FileTreeWalker.Event>, Closeable {
  private final FileTreeWalker walker;
  
  private FileTreeWalker.Event next;
  
  FileTreeIterator(Path paramPath, int paramInt, FileVisitOption... paramVarArgs) throws IOException {
    this.walker = new FileTreeWalker(Arrays.asList(paramVarArgs), paramInt);
    this.next = this.walker.walk(paramPath);
    assert this.next.type() == FileTreeWalker.EventType.ENTRY || this.next.type() == FileTreeWalker.EventType.START_DIRECTORY;
    IOException iOException = this.next.ioeException();
    if (iOException != null)
      throw iOException; 
  }
  
  private void fetchNextIfNeeded() {
    if (this.next == null)
      for (FileTreeWalker.Event event = this.walker.next(); event != null; event = this.walker.next()) {
        IOException iOException = event.ioeException();
        if (iOException != null)
          throw new UncheckedIOException(iOException); 
        if (event.type() != FileTreeWalker.EventType.END_DIRECTORY) {
          this.next = event;
          return;
        } 
      }  
  }
  
  public boolean hasNext() {
    if (!this.walker.isOpen())
      throw new IllegalStateException(); 
    fetchNextIfNeeded();
    return (this.next != null);
  }
  
  public FileTreeWalker.Event next() {
    if (!this.walker.isOpen())
      throw new IllegalStateException(); 
    fetchNextIfNeeded();
    if (this.next == null)
      throw new NoSuchElementException(); 
    FileTreeWalker.Event event = this.next;
    this.next = null;
    return event;
  }
  
  public void close() { this.walker.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\FileTreeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */