package jdk.management.resource;

public interface ResourceMeter {
  long getValue();
  
  long getAllocated();
  
  ResourceType getType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ResourceMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */