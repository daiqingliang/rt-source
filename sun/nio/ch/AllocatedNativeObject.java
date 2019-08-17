package sun.nio.ch;

class AllocatedNativeObject extends NativeObject {
  AllocatedNativeObject(int paramInt, boolean paramBoolean) { super(paramInt, paramBoolean); }
  
  void free() {
    if (this.allocationAddress != 0L) {
      unsafe.freeMemory(this.allocationAddress);
      this.allocationAddress = 0L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\AllocatedNativeObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */