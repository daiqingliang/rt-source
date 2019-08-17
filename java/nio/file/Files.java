package java.nio.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.spi.FileSystemProvider;
import java.nio.file.spi.FileTypeDetector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.nio.fs.DefaultFileTypeDetector;

public final class Files {
  private static final int BUFFER_SIZE = 8192;
  
  private static final int MAX_BUFFER_SIZE = 2147483639;
  
  private static FileSystemProvider provider(Path paramPath) { return paramPath.getFileSystem().provider(); }
  
  private static Runnable asUncheckedRunnable(Closeable paramCloseable) { return () -> {
        try {
          paramCloseable.close();
        } catch (IOException iOException) {
          throw new UncheckedIOException(iOException);
        } 
      }; }
  
  public static InputStream newInputStream(Path paramPath, OpenOption... paramVarArgs) throws IOException { return provider(paramPath).newInputStream(paramPath, paramVarArgs); }
  
  public static OutputStream newOutputStream(Path paramPath, OpenOption... paramVarArgs) throws IOException { return provider(paramPath).newOutputStream(paramPath, paramVarArgs); }
  
  public static SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs) throws IOException { return provider(paramPath).newByteChannel(paramPath, paramSet, paramVarArgs); }
  
  public static SeekableByteChannel newByteChannel(Path paramPath, OpenOption... paramVarArgs) throws IOException {
    HashSet hashSet = new HashSet(paramVarArgs.length);
    Collections.addAll(hashSet, paramVarArgs);
    return newByteChannel(paramPath, hashSet, new FileAttribute[0]);
  }
  
  public static DirectoryStream<Path> newDirectoryStream(Path paramPath) throws IOException { return provider(paramPath).newDirectoryStream(paramPath, AcceptAllFilter.FILTER); }
  
  public static DirectoryStream<Path> newDirectoryStream(Path paramPath, String paramString) throws IOException {
    if (paramString.equals("*"))
      return newDirectoryStream(paramPath); 
    FileSystem fileSystem = paramPath.getFileSystem();
    final PathMatcher matcher = fileSystem.getPathMatcher("glob:" + paramString);
    DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
        public boolean accept(Path param1Path) throws IOException { return matcher.matches(param1Path.getFileName()); }
      };
    return fileSystem.provider().newDirectoryStream(paramPath, filter);
  }
  
  public static DirectoryStream<Path> newDirectoryStream(Path paramPath, DirectoryStream.Filter<? super Path> paramFilter) throws IOException { return provider(paramPath).newDirectoryStream(paramPath, paramFilter); }
  
  public static Path createFile(Path paramPath, FileAttribute<?>... paramVarArgs) throws IOException {
    EnumSet enumSet = EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    newByteChannel(paramPath, enumSet, paramVarArgs).close();
    return paramPath;
  }
  
  public static Path createDirectory(Path paramPath, FileAttribute<?>... paramVarArgs) throws IOException {
    provider(paramPath).createDirectory(paramPath, paramVarArgs);
    return paramPath;
  }
  
  public static Path createDirectories(Path paramPath, FileAttribute<?>... paramVarArgs) throws IOException {
    try {
      createAndCheckIsDirectory(paramPath, paramVarArgs);
      return paramPath;
    } catch (FileAlreadyExistsException fileAlreadyExistsException) {
      throw fileAlreadyExistsException;
    } catch (IOException null) {
      SecurityException securityException = null;
      try {
        paramPath = paramPath.toAbsolutePath();
      } catch (SecurityException securityException1) {
        securityException = securityException1;
      } 
      Path path1 = paramPath.getParent();
      while (path1 != null) {
        try {
          provider(path1).checkAccess(path1, new AccessMode[0]);
          break;
        } catch (NoSuchFileException noSuchFileException) {
          path1 = path1.getParent();
        } 
      } 
      if (path1 == null) {
        if (securityException == null)
          throw new FileSystemException(paramPath.toString(), null, "Unable to determine if root directory exists"); 
        throw securityException;
      } 
      Path path2 = path1;
      for (Path path : path1.relativize(paramPath)) {
        path2 = path2.resolve(path);
        createAndCheckIsDirectory(path2, paramVarArgs);
      } 
      return paramPath;
    } 
  }
  
  private static void createAndCheckIsDirectory(Path paramPath, FileAttribute<?>... paramVarArgs) throws IOException {
    try {
      createDirectory(paramPath, paramVarArgs);
    } catch (FileAlreadyExistsException fileAlreadyExistsException) {
      if (!isDirectory(paramPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
        throw fileAlreadyExistsException; 
    } 
  }
  
  public static Path createTempFile(Path paramPath, String paramString1, String paramString2, FileAttribute<?>... paramVarArgs) throws IOException { return TempFileHelper.createTempFile((Path)Objects.requireNonNull(paramPath), paramString1, paramString2, paramVarArgs); }
  
  public static Path createTempFile(String paramString1, String paramString2, FileAttribute<?>... paramVarArgs) throws IOException { return TempFileHelper.createTempFile(null, paramString1, paramString2, paramVarArgs); }
  
  public static Path createTempDirectory(Path paramPath, String paramString, FileAttribute<?>... paramVarArgs) throws IOException { return TempFileHelper.createTempDirectory((Path)Objects.requireNonNull(paramPath), paramString, paramVarArgs); }
  
  public static Path createTempDirectory(String paramString, FileAttribute<?>... paramVarArgs) throws IOException { return TempFileHelper.createTempDirectory(null, paramString, paramVarArgs); }
  
  public static Path createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>... paramVarArgs) throws IOException {
    provider(paramPath1).createSymbolicLink(paramPath1, paramPath2, paramVarArgs);
    return paramPath1;
  }
  
  public static Path createLink(Path paramPath1, Path paramPath2) throws IOException {
    provider(paramPath1).createLink(paramPath1, paramPath2);
    return paramPath1;
  }
  
  public static void delete(Path paramPath) throws IOException { provider(paramPath).delete(paramPath); }
  
  public static boolean deleteIfExists(Path paramPath) throws IOException { return provider(paramPath).deleteIfExists(paramPath); }
  
  public static Path copy(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs) throws IOException {
    FileSystemProvider fileSystemProvider = provider(paramPath1);
    if (provider(paramPath2) == fileSystemProvider) {
      fileSystemProvider.copy(paramPath1, paramPath2, paramVarArgs);
    } else {
      CopyMoveHelper.copyToForeignTarget(paramPath1, paramPath2, paramVarArgs);
    } 
    return paramPath2;
  }
  
  public static Path move(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs) throws IOException {
    FileSystemProvider fileSystemProvider = provider(paramPath1);
    if (provider(paramPath2) == fileSystemProvider) {
      fileSystemProvider.move(paramPath1, paramPath2, paramVarArgs);
    } else {
      CopyMoveHelper.moveToForeignTarget(paramPath1, paramPath2, paramVarArgs);
    } 
    return paramPath2;
  }
  
  public static Path readSymbolicLink(Path paramPath) throws IOException { return provider(paramPath).readSymbolicLink(paramPath); }
  
  public static FileStore getFileStore(Path paramPath) throws IOException { return provider(paramPath).getFileStore(paramPath); }
  
  public static boolean isSameFile(Path paramPath1, Path paramPath2) throws IOException { return provider(paramPath1).isSameFile(paramPath1, paramPath2); }
  
  public static boolean isHidden(Path paramPath) throws IOException { return provider(paramPath).isHidden(paramPath); }
  
  public static String probeContentType(Path paramPath) throws IOException {
    for (FileTypeDetector fileTypeDetector : FileTypeDetectors.installeDetectors) {
      String str = fileTypeDetector.probeContentType(paramPath);
      if (str != null)
        return str; 
    } 
    return FileTypeDetectors.defaultFileTypeDetector.probeContentType(paramPath);
  }
  
  public static <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption... paramVarArgs) { return (V)provider(paramPath).getFileAttributeView(paramPath, paramClass, paramVarArgs); }
  
  public static <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption... paramVarArgs) throws IOException { return (A)provider(paramPath).readAttributes(paramPath, paramClass, paramVarArgs); }
  
  public static Path setAttribute(Path paramPath, String paramString, Object paramObject, LinkOption... paramVarArgs) throws IOException {
    provider(paramPath).setAttribute(paramPath, paramString, paramObject, paramVarArgs);
    return paramPath;
  }
  
  public static Object getAttribute(Path paramPath, String paramString, LinkOption... paramVarArgs) throws IOException {
    String str;
    if (paramString.indexOf('*') >= 0 || paramString.indexOf(',') >= 0)
      throw new IllegalArgumentException(paramString); 
    Map map = readAttributes(paramPath, paramString, paramVarArgs);
    assert map.size() == 1;
    int i = paramString.indexOf(':');
    if (i == -1) {
      str = paramString;
    } else {
      str = (i == paramString.length()) ? "" : paramString.substring(i + 1);
    } 
    return map.get(str);
  }
  
  public static Map<String, Object> readAttributes(Path paramPath, String paramString, LinkOption... paramVarArgs) throws IOException { return provider(paramPath).readAttributes(paramPath, paramString, paramVarArgs); }
  
  public static Set<PosixFilePermission> getPosixFilePermissions(Path paramPath, LinkOption... paramVarArgs) throws IOException { return ((PosixFileAttributes)readAttributes(paramPath, PosixFileAttributes.class, paramVarArgs)).permissions(); }
  
  public static Path setPosixFilePermissions(Path paramPath, Set<PosixFilePermission> paramSet) throws IOException {
    PosixFileAttributeView posixFileAttributeView = (PosixFileAttributeView)getFileAttributeView(paramPath, PosixFileAttributeView.class, new LinkOption[0]);
    if (posixFileAttributeView == null)
      throw new UnsupportedOperationException(); 
    posixFileAttributeView.setPermissions(paramSet);
    return paramPath;
  }
  
  public static UserPrincipal getOwner(Path paramPath, LinkOption... paramVarArgs) throws IOException {
    FileOwnerAttributeView fileOwnerAttributeView = (FileOwnerAttributeView)getFileAttributeView(paramPath, FileOwnerAttributeView.class, paramVarArgs);
    if (fileOwnerAttributeView == null)
      throw new UnsupportedOperationException(); 
    return fileOwnerAttributeView.getOwner();
  }
  
  public static Path setOwner(Path paramPath, UserPrincipal paramUserPrincipal) throws IOException {
    FileOwnerAttributeView fileOwnerAttributeView = (FileOwnerAttributeView)getFileAttributeView(paramPath, FileOwnerAttributeView.class, new LinkOption[0]);
    if (fileOwnerAttributeView == null)
      throw new UnsupportedOperationException(); 
    fileOwnerAttributeView.setOwner(paramUserPrincipal);
    return paramPath;
  }
  
  public static boolean isSymbolicLink(Path paramPath) throws IOException {
    try {
      return readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }).isSymbolicLink();
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public static boolean isDirectory(Path paramPath, LinkOption... paramVarArgs) {
    try {
      return readAttributes(paramPath, BasicFileAttributes.class, paramVarArgs).isDirectory();
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public static boolean isRegularFile(Path paramPath, LinkOption... paramVarArgs) {
    try {
      return readAttributes(paramPath, BasicFileAttributes.class, paramVarArgs).isRegularFile();
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public static FileTime getLastModifiedTime(Path paramPath, LinkOption... paramVarArgs) throws IOException { return readAttributes(paramPath, BasicFileAttributes.class, paramVarArgs).lastModifiedTime(); }
  
  public static Path setLastModifiedTime(Path paramPath, FileTime paramFileTime) throws IOException {
    ((BasicFileAttributeView)getFileAttributeView(paramPath, BasicFileAttributeView.class, new LinkOption[0])).setTimes(paramFileTime, null, null);
    return paramPath;
  }
  
  public static long size(Path paramPath) throws IOException { return readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[0]).size(); }
  
  private static boolean followLinks(LinkOption... paramVarArgs) {
    boolean bool = true;
    for (LinkOption linkOption : paramVarArgs) {
      if (linkOption == LinkOption.NOFOLLOW_LINKS) {
        bool = false;
      } else {
        if (linkOption == null)
          throw new NullPointerException(); 
        throw new AssertionError("Should not get here");
      } 
    } 
    return bool;
  }
  
  public static boolean exists(Path paramPath, LinkOption... paramVarArgs) {
    try {
      if (followLinks(paramVarArgs)) {
        provider(paramPath).checkAccess(paramPath, new AccessMode[0]);
      } else {
        readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
      } 
      return true;
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public static boolean notExists(Path paramPath, LinkOption... paramVarArgs) {
    try {
      if (followLinks(paramVarArgs)) {
        provider(paramPath).checkAccess(paramPath, new AccessMode[0]);
      } else {
        readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
      } 
      return false;
    } catch (NoSuchFileException noSuchFileException) {
      return true;
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  private static boolean isAccessible(Path paramPath, AccessMode... paramVarArgs) {
    try {
      provider(paramPath).checkAccess(paramPath, paramVarArgs);
      return true;
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public static boolean isReadable(Path paramPath) throws IOException { return isAccessible(paramPath, new AccessMode[] { AccessMode.READ }); }
  
  public static boolean isWritable(Path paramPath) throws IOException { return isAccessible(paramPath, new AccessMode[] { AccessMode.WRITE }); }
  
  public static boolean isExecutable(Path paramPath) throws IOException { return isAccessible(paramPath, new AccessMode[] { AccessMode.EXECUTE }); }
  
  public static Path walkFileTree(Path paramPath, Set<FileVisitOption> paramSet, int paramInt, FileVisitor<? super Path> paramFileVisitor) throws IOException {
    try (FileTreeWalker null = new FileTreeWalker(paramSet, paramInt)) {
      event = fileTreeWalker.walk(paramPath);
      do {
        IOException iOException;
        FileVisitResult fileVisitResult;
        switch (event.type()) {
          case ENTRY:
            iOException = event.ioeException();
            if (iOException == null) {
              assert event.attributes() != null;
              FileVisitResult fileVisitResult1 = paramFileVisitor.visitFile(event.file(), event.attributes());
              break;
            } 
            fileVisitResult = paramFileVisitor.visitFileFailed(event.file(), iOException);
            break;
          case START_DIRECTORY:
            fileVisitResult = paramFileVisitor.preVisitDirectory(event.file(), event.attributes());
            if (fileVisitResult == FileVisitResult.SKIP_SUBTREE || fileVisitResult == FileVisitResult.SKIP_SIBLINGS)
              fileTreeWalker.pop(); 
            break;
          case END_DIRECTORY:
            fileVisitResult = paramFileVisitor.postVisitDirectory(event.file(), event.ioeException());
            if (fileVisitResult == FileVisitResult.SKIP_SIBLINGS)
              fileVisitResult = FileVisitResult.CONTINUE; 
            break;
          default:
            throw new AssertionError("Should not get here");
        } 
        if (Objects.requireNonNull(fileVisitResult) != FileVisitResult.CONTINUE) {
          if (fileVisitResult == FileVisitResult.TERMINATE)
            break; 
          if (fileVisitResult == FileVisitResult.SKIP_SIBLINGS)
            fileTreeWalker.skipRemainingSiblings(); 
        } 
        event = fileTreeWalker.next();
      } while (event != null);
    } 
    return paramPath;
  }
  
  public static Path walkFileTree(Path paramPath, FileVisitor<? super Path> paramFileVisitor) throws IOException { return walkFileTree(paramPath, EnumSet.noneOf(FileVisitOption.class), 2147483647, paramFileVisitor); }
  
  public static BufferedReader newBufferedReader(Path paramPath, Charset paramCharset) throws IOException {
    CharsetDecoder charsetDecoder = paramCharset.newDecoder();
    InputStreamReader inputStreamReader = new InputStreamReader(newInputStream(paramPath, new OpenOption[0]), charsetDecoder);
    return new BufferedReader(inputStreamReader);
  }
  
  public static BufferedReader newBufferedReader(Path paramPath) throws IOException { return newBufferedReader(paramPath, StandardCharsets.UTF_8); }
  
  public static BufferedWriter newBufferedWriter(Path paramPath, Charset paramCharset, OpenOption... paramVarArgs) throws IOException {
    CharsetEncoder charsetEncoder = paramCharset.newEncoder();
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(newOutputStream(paramPath, paramVarArgs), charsetEncoder);
    return new BufferedWriter(outputStreamWriter);
  }
  
  public static BufferedWriter newBufferedWriter(Path paramPath, OpenOption... paramVarArgs) throws IOException { return newBufferedWriter(paramPath, StandardCharsets.UTF_8, paramVarArgs); }
  
  private static long copy(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    long l = 0L;
    byte[] arrayOfByte = new byte[8192];
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) > 0) {
      paramOutputStream.write(arrayOfByte, 0, i);
      l += i;
    } 
    return l;
  }
  
  public static long copy(InputStream paramInputStream, Path paramPath, CopyOption... paramVarArgs) throws IOException {
    OutputStream outputStream1;
    Objects.requireNonNull(paramInputStream);
    boolean bool = false;
    for (CopyOption copyOption : paramVarArgs) {
      if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
        bool = true;
      } else {
        if (copyOption == null)
          throw new NullPointerException("options contains 'null'"); 
        throw new UnsupportedOperationException(copyOption + " not supported");
      } 
    } 
    SecurityException securityException = null;
    if (bool)
      try {
        deleteIfExists(paramPath);
      } catch (SecurityException null) {
        securityException = outputStream1;
      }  
    try {
      outputStream1 = newOutputStream(paramPath, new OpenOption[] { StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE });
    } catch (FileAlreadyExistsException fileAlreadyExistsException) {
      if (securityException != null)
        throw securityException; 
      throw fileAlreadyExistsException;
    } 
    try (OutputStream null = outputStream1) {
      return copy(paramInputStream, outputStream2);
    } 
  }
  
  public static long copy(Path paramPath, OutputStream paramOutputStream) throws IOException {
    Objects.requireNonNull(paramOutputStream);
    try (InputStream null = newInputStream(paramPath, new OpenOption[0])) {
      return copy(inputStream, paramOutputStream);
    } 
  }
  
  private static byte[] read(InputStream paramInputStream, int paramInt) throws IOException {
    int i = paramInt;
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    while (true) {
      int k;
      while ((k = paramInputStream.read(arrayOfByte, j, i - j)) > 0)
        j += k; 
      if (k < 0 || (k = paramInputStream.read()) < 0)
        break; 
      if (i <= 2147483639 - i) {
        i = Math.max(i << 1, 8192);
      } else {
        if (i == 2147483639)
          throw new OutOfMemoryError("Required array size too large"); 
        i = 2147483639;
      } 
      arrayOfByte = Arrays.copyOf(arrayOfByte, i);
      arrayOfByte[j++] = (byte)k;
    } 
    return (i == j) ? arrayOfByte : Arrays.copyOf(arrayOfByte, j);
  }
  
  public static byte[] readAllBytes(Path paramPath) throws IOException {
    try(SeekableByteChannel null = newByteChannel(paramPath, new OpenOption[0]); InputStream null = Channels.newInputStream(seekableByteChannel)) {
      long l = seekableByteChannel.size();
      if (l > 2147483639L)
        throw new OutOfMemoryError("Required array size too large"); 
      return read(inputStream, (int)l);
    } 
  }
  
  public static List<String> readAllLines(Path paramPath, Charset paramCharset) throws IOException {
    try (BufferedReader null = newBufferedReader(paramPath, paramCharset)) {
      ArrayList arrayList = new ArrayList();
      while (true) {
        String str = bufferedReader.readLine();
        if (str == null)
          break; 
        arrayList.add(str);
      } 
      return arrayList;
    } 
  }
  
  public static List<String> readAllLines(Path paramPath) throws IOException { return readAllLines(paramPath, StandardCharsets.UTF_8); }
  
  public static Path write(Path paramPath, byte[] paramArrayOfByte, OpenOption... paramVarArgs) throws IOException {
    Objects.requireNonNull(paramArrayOfByte);
    try (OutputStream null = newOutputStream(paramPath, paramVarArgs)) {
      i = paramArrayOfByte.length;
      int j;
      for (j = i; j > 0; j -= k) {
        int k = Math.min(j, 8192);
        outputStream.write(paramArrayOfByte, i - j, k);
      } 
    } 
    return paramPath;
  }
  
  public static Path write(Path paramPath, Iterable<? extends CharSequence> paramIterable, Charset paramCharset, OpenOption... paramVarArgs) throws IOException {
    Objects.requireNonNull(paramIterable);
    CharsetEncoder charsetEncoder = paramCharset.newEncoder();
    OutputStream outputStream = newOutputStream(paramPath, paramVarArgs);
    try (BufferedWriter null = new BufferedWriter(new OutputStreamWriter(outputStream, charsetEncoder))) {
      for (CharSequence charSequence : paramIterable) {
        bufferedWriter.append(charSequence);
        bufferedWriter.newLine();
      } 
    } 
    return paramPath;
  }
  
  public static Path write(Path paramPath, Iterable<? extends CharSequence> paramIterable, OpenOption... paramVarArgs) throws IOException { return write(paramPath, paramIterable, StandardCharsets.UTF_8, paramVarArgs); }
  
  public static Stream<Path> list(Path paramPath) throws IOException {
    DirectoryStream directoryStream = newDirectoryStream(paramPath);
    try {
      final Iterator delegate = directoryStream.iterator();
      Iterator<Path> iterator2 = new Iterator<Path>() {
          public boolean hasNext() {
            try {
              return delegate.hasNext();
            } catch (DirectoryIteratorException directoryIteratorException) {
              throw new UncheckedIOException(directoryIteratorException.getCause());
            } 
          }
          
          public Path next() {
            try {
              return (Path)delegate.next();
            } catch (DirectoryIteratorException directoryIteratorException) {
              throw new UncheckedIOException(directoryIteratorException.getCause());
            } 
          }
        };
      return (Stream)StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator2, 1), false).onClose(asUncheckedRunnable(directoryStream));
    } catch (Error|RuntimeException error) {
      try {
        directoryStream.close();
      } catch (IOException iOException) {
        try {
          error.addSuppressed(iOException);
        } catch (Throwable throwable) {}
      } 
      throw error;
    } 
  }
  
  public static Stream<Path> walk(Path paramPath, int paramInt, FileVisitOption... paramVarArgs) throws IOException {
    FileTreeIterator fileTreeIterator = new FileTreeIterator(paramPath, paramInt, paramVarArgs);
    try {
      return ((Stream)StreamSupport.stream(Spliterators.spliteratorUnknownSize(fileTreeIterator, 1), false).onClose(fileTreeIterator::close)).map(paramEvent -> paramEvent.file());
    } catch (Error|RuntimeException error) {
      fileTreeIterator.close();
      throw error;
    } 
  }
  
  public static Stream<Path> walk(Path paramPath, FileVisitOption... paramVarArgs) throws IOException { return walk(paramPath, 2147483647, paramVarArgs); }
  
  public static Stream<Path> find(Path paramPath, int paramInt, BiPredicate<Path, BasicFileAttributes> paramBiPredicate, FileVisitOption... paramVarArgs) throws IOException {
    FileTreeIterator fileTreeIterator = new FileTreeIterator(paramPath, paramInt, paramVarArgs);
    try {
      return ((Stream)StreamSupport.stream(Spliterators.spliteratorUnknownSize(fileTreeIterator, 1), false).onClose(fileTreeIterator::close)).filter(paramEvent -> paramBiPredicate.test(paramEvent.file(), paramEvent.attributes())).map(paramEvent -> paramEvent.file());
    } catch (Error|RuntimeException error) {
      fileTreeIterator.close();
      throw error;
    } 
  }
  
  public static Stream<String> lines(Path paramPath, Charset paramCharset) throws IOException {
    BufferedReader bufferedReader = newBufferedReader(paramPath, paramCharset);
    try {
      return (Stream)bufferedReader.lines().onClose(asUncheckedRunnable(bufferedReader));
    } catch (Error|RuntimeException error) {
      try {
        bufferedReader.close();
      } catch (IOException iOException) {
        try {
          error.addSuppressed(iOException);
        } catch (Throwable throwable) {}
      } 
      throw error;
    } 
  }
  
  public static Stream<String> lines(Path paramPath) throws IOException { return lines(paramPath, StandardCharsets.UTF_8); }
  
  private static class AcceptAllFilter extends Object implements DirectoryStream.Filter<Path> {
    static final AcceptAllFilter FILTER = new AcceptAllFilter();
    
    public boolean accept(Path param1Path) throws IOException { return true; }
  }
  
  private static class FileTypeDetectors {
    static final FileTypeDetector defaultFileTypeDetector = createDefaultFileTypeDetector();
    
    static final List<FileTypeDetector> installeDetectors = loadInstalledDetectors();
    
    private static FileTypeDetector createDefaultFileTypeDetector() { return (FileTypeDetector)AccessController.doPrivileged(new PrivilegedAction<FileTypeDetector>() {
            public FileTypeDetector run() { return DefaultFileTypeDetector.create(); }
          }); }
    
    private static List<FileTypeDetector> loadInstalledDetectors() { return (List)AccessController.doPrivileged(new PrivilegedAction<List<FileTypeDetector>>() {
            public List<FileTypeDetector> run() {
              ArrayList arrayList = new ArrayList();
              ServiceLoader serviceLoader = ServiceLoader.load(FileTypeDetector.class, ClassLoader.getSystemClassLoader());
              for (FileTypeDetector fileTypeDetector : serviceLoader)
                arrayList.add(fileTypeDetector); 
              return arrayList;
            }
          }); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\Files.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */