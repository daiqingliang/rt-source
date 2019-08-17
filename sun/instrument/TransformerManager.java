package sun.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class TransformerManager {
  private TransformerInfo[] mTransformerList = new TransformerInfo[0];
  
  private boolean mIsRetransformable;
  
  TransformerManager(boolean paramBoolean) { this.mIsRetransformable = paramBoolean; }
  
  boolean isRetransformable() { return this.mIsRetransformable; }
  
  public void addTransformer(ClassFileTransformer paramClassFileTransformer) {
    TransformerInfo[] arrayOfTransformerInfo1 = this.mTransformerList;
    TransformerInfo[] arrayOfTransformerInfo2 = new TransformerInfo[arrayOfTransformerInfo1.length + 1];
    System.arraycopy(arrayOfTransformerInfo1, 0, arrayOfTransformerInfo2, 0, arrayOfTransformerInfo1.length);
    arrayOfTransformerInfo2[arrayOfTransformerInfo1.length] = new TransformerInfo(paramClassFileTransformer);
    this.mTransformerList = arrayOfTransformerInfo2;
  }
  
  public boolean removeTransformer(ClassFileTransformer paramClassFileTransformer) {
    boolean bool = false;
    TransformerInfo[] arrayOfTransformerInfo = this.mTransformerList;
    int i = arrayOfTransformerInfo.length;
    int j = i - 1;
    int k = 0;
    for (int m = i - 1; m >= 0; m--) {
      if (arrayOfTransformerInfo[m].transformer() == paramClassFileTransformer) {
        bool = true;
        k = m;
        break;
      } 
    } 
    if (bool) {
      TransformerInfo[] arrayOfTransformerInfo1 = new TransformerInfo[j];
      if (k > 0)
        System.arraycopy(arrayOfTransformerInfo, 0, arrayOfTransformerInfo1, 0, k); 
      if (k < j)
        System.arraycopy(arrayOfTransformerInfo, k + 1, arrayOfTransformerInfo1, k, j - k); 
      this.mTransformerList = arrayOfTransformerInfo1;
    } 
    return bool;
  }
  
  boolean includesTransformer(ClassFileTransformer paramClassFileTransformer) {
    for (TransformerInfo transformerInfo : this.mTransformerList) {
      if (transformerInfo.transformer() == paramClassFileTransformer)
        return true; 
    } 
    return false;
  }
  
  private TransformerInfo[] getSnapshotTransformerList() { return this.mTransformerList; }
  
  public byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfByte) {
    byte[] arrayOfByte2;
    boolean bool = false;
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    byte[] arrayOfByte1 = paramArrayOfByte;
    for (byte b = 0; b < arrayOfTransformerInfo.length; b++) {
      TransformerInfo transformerInfo = arrayOfTransformerInfo[b];
      ClassFileTransformer classFileTransformer = transformerInfo.transformer();
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = classFileTransformer.transform(paramClassLoader, paramString, paramClass, paramProtectionDomain, arrayOfByte1);
      } catch (Throwable throwable) {}
      if (arrayOfByte != null) {
        bool = true;
        arrayOfByte1 = arrayOfByte;
      } 
    } 
    if (bool) {
      arrayOfByte2 = arrayOfByte1;
    } else {
      arrayOfByte2 = null;
    } 
    return arrayOfByte2;
  }
  
  int getTransformerCount() {
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    return arrayOfTransformerInfo.length;
  }
  
  boolean setNativeMethodPrefix(ClassFileTransformer paramClassFileTransformer, String paramString) {
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    for (byte b = 0; b < arrayOfTransformerInfo.length; b++) {
      TransformerInfo transformerInfo = arrayOfTransformerInfo[b];
      ClassFileTransformer classFileTransformer = transformerInfo.transformer();
      if (classFileTransformer == paramClassFileTransformer) {
        transformerInfo.setPrefix(paramString);
        return true;
      } 
    } 
    return false;
  }
  
  String[] getNativeMethodPrefixes() {
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    String[] arrayOfString = new String[arrayOfTransformerInfo.length];
    for (byte b = 0; b < arrayOfTransformerInfo.length; b++) {
      TransformerInfo transformerInfo = arrayOfTransformerInfo[b];
      arrayOfString[b] = transformerInfo.getPrefix();
    } 
    return arrayOfString;
  }
  
  private class TransformerInfo {
    final ClassFileTransformer mTransformer;
    
    String mPrefix;
    
    TransformerInfo(ClassFileTransformer param1ClassFileTransformer) {
      this.mTransformer = param1ClassFileTransformer;
      this.mPrefix = null;
    }
    
    ClassFileTransformer transformer() { return this.mTransformer; }
    
    String getPrefix() { return this.mPrefix; }
    
    void setPrefix(String param1String) { this.mPrefix = param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\instrument\TransformerManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */