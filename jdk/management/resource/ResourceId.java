package jdk.management.resource;

@FunctionalInterface
public interface ResourceId {
  String getName();
  
  default ResourceAccuracy getAccuracy() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ResourceId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */