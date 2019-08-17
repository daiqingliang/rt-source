package jdk.internal.instrumentation;

import java.util.HashMap;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

final class MaxLocalsTracker extends ClassVisitor {
  private final HashMap<String, Integer> maxLocalsMap = new HashMap();
  
  public MaxLocalsTracker() { super(327680); }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) { return new MaxLocalsMethodVisitor(key(paramString1, paramString2)); }
  
  public int getMaxLocals(String paramString1, String paramString2) {
    Integer integer = (Integer)this.maxLocalsMap.get(key(paramString1, paramString2));
    if (integer == null)
      throw new IllegalArgumentException("No maxLocals could be found for " + paramString1 + paramString2); 
    return integer.intValue();
  }
  
  private static String key(String paramString1, String paramString2) { return paramString1 + paramString2; }
  
  private final class MaxLocalsMethodVisitor extends MethodVisitor {
    private String key;
    
    public MaxLocalsMethodVisitor(String param1String) {
      super(327680);
      this.key = param1String;
    }
    
    public void visitMaxs(int param1Int1, int param1Int2) { MaxLocalsTracker.this.maxLocalsMap.put(this.key, Integer.valueOf(param1Int2)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\MaxLocalsTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */