package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import sun.nio.fs.BasicFileAttributesHolder;

class FileTreeWalker implements Closeable {
  private final boolean followLinks;
  
  private final LinkOption[] linkOptions;
  
  private final int maxDepth;
  
  private final ArrayDeque<DirectoryNode> stack = new ArrayDeque();
  
  private boolean closed;
  
  FileTreeWalker(Collection<FileVisitOption> paramCollection, int paramInt) {
    boolean bool = false;
    for (FileVisitOption fileVisitOption : paramCollection) {
      switch (fileVisitOption) {
        case FOLLOW_LINKS:
          bool = true;
          continue;
      } 
      throw new AssertionError("Should not get here");
    } 
    if (paramInt < 0)
      throw new IllegalArgumentException("'maxDepth' is negative"); 
    this.followLinks = bool;
    new LinkOption[1][0] = LinkOption.NOFOLLOW_LINKS;
    this.linkOptions = bool ? new LinkOption[0] : new LinkOption[1];
    this.maxDepth = paramInt;
  }
  
  private BasicFileAttributes getAttributes(Path paramPath, boolean paramBoolean) throws IOException {
    BasicFileAttributes basicFileAttributes;
    if (paramBoolean && paramPath instanceof BasicFileAttributesHolder && System.getSecurityManager() == null) {
      basicFileAttributes = ((BasicFileAttributesHolder)paramPath).get();
      if (basicFileAttributes != null && (!this.followLinks || !basicFileAttributes.isSymbolicLink()))
        return basicFileAttributes; 
    } 
    try {
      basicFileAttributes = Files.readAttributes(paramPath, BasicFileAttributes.class, this.linkOptions);
    } catch (IOException iOException) {
      if (!this.followLinks)
        throw iOException; 
      basicFileAttributes = Files.readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
    } 
    return basicFileAttributes;
  }
  
  private boolean wouldLoop(Path paramPath, Object paramObject) {
    for (DirectoryNode directoryNode : this.stack) {
      Object object = directoryNode.key();
      if (paramObject != null && object != null) {
        if (paramObject.equals(object))
          return true; 
        continue;
      } 
      try {
        if (Files.isSameFile(paramPath, directoryNode.directory()))
          return true; 
      } catch (IOException|SecurityException iOException) {}
    } 
    return false;
  }
  
  private Event visit(Path paramPath, boolean paramBoolean1, boolean paramBoolean2) {
    BasicFileAttributes basicFileAttributes;
    try {
      basicFileAttributes = getAttributes(paramPath, paramBoolean2);
    } catch (IOException iOException) {
      return new Event(EventType.ENTRY, paramPath, iOException);
    } catch (SecurityException securityException) {
      if (paramBoolean1)
        return null; 
      throw securityException;
    } 
    int i = this.stack.size();
    if (i >= this.maxDepth || !basicFileAttributes.isDirectory())
      return new Event(EventType.ENTRY, paramPath, basicFileAttributes); 
    if (this.followLinks && wouldLoop(paramPath, basicFileAttributes.fileKey()))
      return new Event(EventType.ENTRY, paramPath, new FileSystemLoopException(paramPath.toString())); 
    DirectoryStream directoryStream = null;
    try {
      directoryStream = Files.newDirectoryStream(paramPath);
    } catch (IOException iOException) {
      return new Event(EventType.ENTRY, paramPath, iOException);
    } catch (SecurityException securityException) {
      if (paramBoolean1)
        return null; 
      throw securityException;
    } 
    this.stack.push(new DirectoryNode(paramPath, basicFileAttributes.fileKey(), directoryStream));
    return new Event(EventType.START_DIRECTORY, paramPath, basicFileAttributes);
  }
  
  Event walk(Path paramPath) {
    if (this.closed)
      throw new IllegalStateException("Closed"); 
    Event event = visit(paramPath, false, false);
    assert event != null;
    return event;
  }
  
  Event next() {
    Event event;
    DirectoryNode directoryNode = (DirectoryNode)this.stack.peek();
    if (directoryNode == null)
      return null; 
    do {
      Path path = null;
      IOException iOException = null;
      if (!directoryNode.skipped()) {
        Iterator iterator = directoryNode.iterator();
        try {
          if (iterator.hasNext())
            path = (Path)iterator.next(); 
        } catch (DirectoryIteratorException directoryIteratorException) {
          iOException = directoryIteratorException.getCause();
        } 
      } 
      if (path == null) {
        try {
          directoryNode.stream().close();
        } catch (IOException iOException1) {
          if (iOException != null) {
            iOException = iOException1;
          } else {
            iOException.addSuppressed(iOException1);
          } 
        } 
        this.stack.pop();
        return new Event(EventType.END_DIRECTORY, directoryNode.directory(), iOException);
      } 
      event = visit(path, true, true);
    } while (event == null);
    return event;
  }
  
  void pop() {
    if (!this.stack.isEmpty()) {
      DirectoryNode directoryNode = (DirectoryNode)this.stack.pop();
      try {
        directoryNode.stream().close();
      } catch (IOException iOException) {}
    } 
  }
  
  void skipRemainingSiblings() {
    if (!this.stack.isEmpty())
      ((DirectoryNode)this.stack.peek()).skip(); 
  }
  
  boolean isOpen() { return !this.closed; }
  
  public void close() {
    if (!this.closed) {
      while (!this.stack.isEmpty())
        pop(); 
      this.closed = true;
    } 
  }
  
  private static class DirectoryNode {
    private final Path dir;
    
    private final Object key;
    
    private final DirectoryStream<Path> stream;
    
    private final Iterator<Path> iterator;
    
    private boolean skipped;
    
    DirectoryNode(Path param1Path, Object param1Object, DirectoryStream<Path> param1DirectoryStream) {
      this.dir = param1Path;
      this.key = param1Object;
      this.stream = param1DirectoryStream;
      this.iterator = param1DirectoryStream.iterator();
    }
    
    Path directory() { return this.dir; }
    
    Object key() { return this.key; }
    
    DirectoryStream<Path> stream() { return this.stream; }
    
    Iterator<Path> iterator() { return this.iterator; }
    
    void skip() { this.skipped = true; }
    
    boolean skipped() { return this.skipped; }
  }
  
  static class Event {
    private final FileTreeWalker.EventType type;
    
    private final Path file;
    
    private final BasicFileAttributes attrs;
    
    private final IOException ioe;
    
    private Event(FileTreeWalker.EventType param1EventType, Path param1Path, BasicFileAttributes param1BasicFileAttributes, IOException param1IOException) {
      this.type = param1EventType;
      this.file = param1Path;
      this.attrs = param1BasicFileAttributes;
      this.ioe = param1IOException;
    }
    
    Event(FileTreeWalker.EventType param1EventType, Path param1Path, BasicFileAttributes param1BasicFileAttributes) { this(param1EventType, param1Path, param1BasicFileAttributes, null); }
    
    Event(FileTreeWalker.EventType param1EventType, Path param1Path, IOException param1IOException) { this(param1EventType, param1Path, null, param1IOException); }
    
    FileTreeWalker.EventType type() { return this.type; }
    
    Path file() { return this.file; }
    
    BasicFileAttributes attributes() { return this.attrs; }
    
    IOException ioeException() { return this.ioe; }
  }
  
  enum EventType {
    START_DIRECTORY, END_DIRECTORY, ENTRY;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\FileTreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */