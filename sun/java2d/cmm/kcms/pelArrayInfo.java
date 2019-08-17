package sun.java2d.cmm.kcms;

class pelArrayInfo {
  int nPels;
  
  int nSrc;
  
  int srcSize;
  
  int nDest;
  
  int destSize;
  
  pelArrayInfo(ICC_Transform paramICC_Transform, int paramInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    this.nSrc = paramICC_Transform.getNumInComponents();
    this.nDest = paramICC_Transform.getNumOutComponents();
    this.nPels = paramInt;
    this.srcSize = this.nPels * this.nSrc;
    this.destSize = this.nPels * this.nDest;
    if (this.srcSize > paramArrayOfFloat1.length)
      throw new IllegalArgumentException("Inconsistent pel structure"); 
    if (paramArrayOfFloat2 != null)
      checkDest(paramArrayOfFloat2.length); 
  }
  
  pelArrayInfo(ICC_Transform paramICC_Transform, short[] paramArrayOfShort1, short[] paramArrayOfShort2) {
    this.srcSize = paramArrayOfShort1.length;
    initInfo(paramICC_Transform);
    this.destSize = this.nPels * this.nDest;
    if (paramArrayOfShort2 != null)
      checkDest(paramArrayOfShort2.length); 
  }
  
  pelArrayInfo(ICC_Transform paramICC_Transform, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    this.srcSize = paramArrayOfByte1.length;
    initInfo(paramICC_Transform);
    this.destSize = this.nPels * this.nDest;
    if (paramArrayOfByte2 != null)
      checkDest(paramArrayOfByte2.length); 
  }
  
  void initInfo(ICC_Transform paramICC_Transform) {
    this.nSrc = paramICC_Transform.getNumInComponents();
    this.nDest = paramICC_Transform.getNumOutComponents();
    this.nPels = this.srcSize / this.nSrc;
    if (this.nPels * this.nSrc != this.srcSize)
      throw new IllegalArgumentException("Inconsistent pel structure"); 
  }
  
  void checkDest(int paramInt) {
    if (this.destSize > paramInt)
      throw new IllegalArgumentException("Inconsistent pel structure"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\kcms\pelArrayInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */