package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Label;

public interface TableSwitchGenerator {
  void generateCase(int paramInt, Label paramLabel);
  
  void generateDefault();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\TableSwitchGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */