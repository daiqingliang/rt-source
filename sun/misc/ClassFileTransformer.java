package sun.misc;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class ClassFileTransformer {
  private static final List<ClassFileTransformer> transformers = new ArrayList();
  
  public static void add(ClassFileTransformer paramClassFileTransformer) {
    synchronized (transformers) {
      transformers.add(paramClassFileTransformer);
    } 
  }
  
  public static ClassFileTransformer[] getTransformers() {
    synchronized (transformers) {
      ClassFileTransformer[] arrayOfClassFileTransformer = new ClassFileTransformer[transformers.size()];
      return (ClassFileTransformer[])transformers.toArray(arrayOfClassFileTransformer);
    } 
  }
  
  public abstract byte[] transform(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws ClassFormatError;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ClassFileTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */