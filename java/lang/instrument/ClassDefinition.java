package java.lang.instrument;

import java.lang.instrument.ClassDefinition;

public final class ClassDefinition {
  private final Class<?> mClass;
  
  private final byte[] mClassFile;
  
  public ClassDefinition(Class<?> paramClass, byte[] paramArrayOfByte) {
    if (paramClass == null || paramArrayOfByte == null)
      throw new NullPointerException(); 
    this.mClass = paramClass;
    this.mClassFile = paramArrayOfByte;
  }
  
  public Class<?> getDefinitionClass() { return this.mClass; }
  
  public byte[] getDefinitionClassFile() { return this.mClassFile; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\instrument\ClassDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */