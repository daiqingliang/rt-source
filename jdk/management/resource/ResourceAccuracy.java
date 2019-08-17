package jdk.management.resource;

public static enum ResourceAccuracy {
  LOW, MEDIUM, HIGH, HIGHEST;
  
  public ResourceAccuracy improve() { return equals(LOW) ? MEDIUM : (equals(MEDIUM) ? HIGH : HIGHEST); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ResourceAccuracy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */