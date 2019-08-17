package jdk.management.resource;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

public class ResourceType {
  private static final WeakHashMap<String, ResourceType> types = new WeakHashMap(32);
  
  public static final ResourceType FILE_OPEN;
  
  public static final ResourceType FILE_READ;
  
  public static final ResourceType FILE_WRITE;
  
  public static final ResourceType STDERR_WRITE;
  
  public static final ResourceType STDIN_READ;
  
  public static final ResourceType STDOUT_WRITE;
  
  public static final ResourceType SOCKET_OPEN;
  
  public static final ResourceType SOCKET_READ;
  
  public static final ResourceType SOCKET_WRITE;
  
  public static final ResourceType DATAGRAM_OPEN;
  
  public static final ResourceType DATAGRAM_RECEIVED;
  
  public static final ResourceType DATAGRAM_SENT;
  
  public static final ResourceType DATAGRAM_READ;
  
  public static final ResourceType DATAGRAM_WRITE;
  
  public static final ResourceType THREAD_CREATED;
  
  public static final ResourceType THREAD_CPU;
  
  public static final ResourceType HEAP_RETAINED;
  
  public static final ResourceType HEAP_ALLOCATED;
  
  public static final ResourceType FILEDESCRIPTOR_OPEN = (HEAP_ALLOCATED = (HEAP_RETAINED = (THREAD_CPU = (THREAD_CREATED = (DATAGRAM_WRITE = (DATAGRAM_READ = (DATAGRAM_SENT = (DATAGRAM_RECEIVED = (DATAGRAM_OPEN = (SOCKET_WRITE = (SOCKET_READ = (SOCKET_OPEN = (STDOUT_WRITE = (STDIN_READ = (STDERR_WRITE = (FILE_WRITE = (FILE_READ = (FILE_OPEN = ofBuiltin("file.open")).ofBuiltin("file.read")).ofBuiltin("file.write")).ofBuiltin("stderr.write")).ofBuiltin("stdin.read")).ofBuiltin("stdout.write")).ofBuiltin("socket.open")).ofBuiltin("socket.read")).ofBuiltin("socket.write")).ofBuiltin("datagram.open")).ofBuiltin("datagram.received")).ofBuiltin("datagram.sent")).ofBuiltin("datagram.read")).ofBuiltin("datagram.write")).ofBuiltin("thread.created")).ofBuiltin("thread.cpu")).ofBuiltin("heap.retained")).ofBuiltin("heap.allocated")).ofBuiltin("filedescriptor.open");
  
  private final String name;
  
  private final boolean builtin;
  
  public static ResourceType of(String paramString) {
    synchronized (types) {
      return (ResourceType)types.computeIfAbsent(paramString, paramString -> new ResourceType(paramString, false));
    } 
  }
  
  static ResourceType ofBuiltin(String paramString) {
    synchronized (types) {
      return (ResourceType)types.computeIfAbsent(paramString, paramString -> new ResourceType(paramString, true));
    } 
  }
  
  private boolean isBuiltin() { return this.builtin; }
  
  static Set<ResourceType> builtinTypes() {
    synchronized (types) {
      HashSet hashSet = new HashSet(types.values());
      hashSet.removeIf(paramResourceType -> !paramResourceType.isBuiltin());
      return hashSet;
    } 
  }
  
  private ResourceType(String paramString, boolean paramBoolean) {
    this.name = (String)Objects.requireNonNull(paramString, "name");
    this.builtin = paramBoolean;
    if (paramString.length() == 0)
      throw new IllegalArgumentException("name must not be empty"); 
  }
  
  public String getName() { return this.name; }
  
  public String toString() { return this.name; }
  
  public int hashCode() {
    null = 5;
    return 17 * null + Objects.hashCode(this.name);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (getClass() != paramObject.getClass())
      return false; 
    ResourceType resourceType = (ResourceType)paramObject;
    return Objects.equals(this.name, resourceType.name);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ResourceType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */