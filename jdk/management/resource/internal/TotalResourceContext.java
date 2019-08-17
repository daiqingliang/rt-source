package jdk.management.resource.internal;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceMeter;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

public class TotalResourceContext implements ResourceContext {
  private static final TotalResourceContext totalContext = new TotalResourceContext("Total");
  
  final ConcurrentHashMap<ResourceType, TotalMeter> totalMeters = new ConcurrentHashMap();
  
  private final String name;
  
  private TotalResourceContext(String paramString) { this.name = paramString; }
  
  public String getName() { return this.name; }
  
  public static TotalResourceContext getTotalContext() { return totalContext; }
  
  public void close() {}
  
  public ResourceRequest getResourceRequest(ResourceType paramResourceType) { return null; }
  
  public TotalMeter getMeter(ResourceType paramResourceType) { return (TotalMeter)this.totalMeters.get(paramResourceType); }
  
  static void validateMeter(ResourceType paramResourceType) { totalContext.totalMeters.computeIfAbsent(paramResourceType, paramResourceType -> new TotalMeter(paramResourceType, 0L, 0L)); }
  
  public Stream<ResourceMeter> meters() { return this.totalMeters.entrySet().stream().map(paramEntry -> (TotalMeter)paramEntry.getValue()); }
  
  public String toString() {
    StringJoiner stringJoiner = new StringJoiner("; ", this.name + "[", "]");
    meters().forEach(paramResourceMeter -> paramStringJoiner.add(paramResourceMeter.toString()));
    return stringJoiner.toString();
  }
  
  static class TotalMeter implements ResourceMeter {
    private final ResourceType type;
    
    private long value;
    
    private long allocated;
    
    TotalMeter(ResourceType param1ResourceType, long param1Long1, long param1Long2) {
      this.type = param1ResourceType;
      this.value = param1Long1;
      this.allocated = param1Long2;
    }
    
    void addValue(long param1Long) { this.value += param1Long; }
    
    void addAllocated(long param1Long) { this.allocated += param1Long; }
    
    public long getValue() {
      null = 0L;
      synchronized (this) {
        null += this.value;
      } 
      return SimpleResourceContext.getContexts().reduceValuesToLong(1000L, param1SimpleResourceContext -> {
            ResourceMeter resourceMeter = param1SimpleResourceContext.getMeter(this.type);
            return (resourceMeter == null) ? 0L : resourceMeter.getValue();
          }0L, (param1Long1, param1Long2) -> param1Long1 + param1Long2);
    }
    
    public long getAllocated() {
      null = 0L;
      synchronized (this) {
        null += this.allocated;
      } 
      return SimpleResourceContext.getContexts().reduceValuesToLong(1000L, param1SimpleResourceContext -> {
            ResourceMeter resourceMeter = param1SimpleResourceContext.getMeter(this.type);
            return (resourceMeter == null) ? 0L : resourceMeter.getAllocated();
          }0L, (param1Long1, param1Long2) -> param1Long1 + param1Long2);
    }
    
    public ResourceType getType() { return this.type; }
    
    public String toString() { return this.type.toString() + ": " + Long.toString(getValue()) + "/" + Long.toString(getAllocated()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\TotalResourceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */