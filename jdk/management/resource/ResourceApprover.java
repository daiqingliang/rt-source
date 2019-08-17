package jdk.management.resource;

@FunctionalInterface
public interface ResourceApprover {
  long request(ResourceMeter paramResourceMeter, long paramLong1, long paramLong2, ResourceId paramResourceId);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ResourceApprover.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */