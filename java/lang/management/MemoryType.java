package java.lang.management;

import java.lang.management.MemoryType;

public static enum MemoryType {
  HEAP("Heap memory"),
  NON_HEAP("Non-heap memory");
  
  private final String description;
  
  private static final long serialVersionUID = 6992337162326171013L;
  
  MemoryType(String paramString1) { this.description = paramString1; }
  
  public String toString() { return this.description; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\MemoryType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */