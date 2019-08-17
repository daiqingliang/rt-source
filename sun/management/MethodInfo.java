package sun.management;

import java.io.Serializable;

public class MethodInfo implements Serializable {
  private String name;
  
  private long type;
  
  private int compileSize;
  
  private static final long serialVersionUID = 6992337162326171013L;
  
  MethodInfo(String paramString, long paramLong, int paramInt) {
    this.name = paramString;
    this.type = paramLong;
    this.compileSize = paramInt;
  }
  
  public String getName() { return this.name; }
  
  public long getType() { return this.type; }
  
  public int getCompileSize() { return this.compileSize; }
  
  public String toString() { return getName() + " type = " + getType() + " compileSize = " + getCompileSize(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MethodInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */