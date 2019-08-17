package sun.java2d.loops;

class SolidPixelWriter extends PixelWriter {
  protected Object srcData;
  
  SolidPixelWriter(Object paramObject) { this.srcData = paramObject; }
  
  public void writePixel(int paramInt1, int paramInt2) { this.dstRast.setDataElements(paramInt1, paramInt2, this.srcData); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\SolidPixelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */