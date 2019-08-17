package sun.awt.image;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.StateTrackable;
import sun.java2d.StateTrackableDelegate;
import sun.java2d.SurfaceData;

public class SunWritableRaster extends WritableRaster {
  private static DataStealer stealer;
  
  private StateTrackableDelegate theTrackable = stealTrackable(this.dataBuffer);
  
  public static void setDataStealer(DataStealer paramDataStealer) {
    if (stealer != null)
      throw new InternalError("Attempt to set DataStealer twice"); 
    stealer = paramDataStealer;
  }
  
  public static byte[] stealData(DataBufferByte paramDataBufferByte, int paramInt) { return stealer.getData(paramDataBufferByte, paramInt); }
  
  public static short[] stealData(DataBufferUShort paramDataBufferUShort, int paramInt) { return stealer.getData(paramDataBufferUShort, paramInt); }
  
  public static int[] stealData(DataBufferInt paramDataBufferInt, int paramInt) { return stealer.getData(paramDataBufferInt, paramInt); }
  
  public static StateTrackableDelegate stealTrackable(DataBuffer paramDataBuffer) { return stealer.getTrackable(paramDataBuffer); }
  
  public static void setTrackable(DataBuffer paramDataBuffer, StateTrackableDelegate paramStateTrackableDelegate) { stealer.setTrackable(paramDataBuffer, paramStateTrackableDelegate); }
  
  public static void makeTrackable(DataBuffer paramDataBuffer) { stealer.setTrackable(paramDataBuffer, StateTrackableDelegate.createInstance(StateTrackable.State.STABLE)); }
  
  public static void markDirty(DataBuffer paramDataBuffer) { stealer.getTrackable(paramDataBuffer).markDirty(); }
  
  public static void markDirty(WritableRaster paramWritableRaster) {
    if (paramWritableRaster instanceof SunWritableRaster) {
      ((SunWritableRaster)paramWritableRaster).markDirty();
    } else {
      markDirty(paramWritableRaster.getDataBuffer());
    } 
  }
  
  public static void markDirty(Image paramImage) { SurfaceData.getPrimarySurfaceData(paramImage).markDirty(); }
  
  public SunWritableRaster(SampleModel paramSampleModel, Point paramPoint) { super(paramSampleModel, paramPoint); }
  
  public SunWritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { super(paramSampleModel, paramDataBuffer, paramPoint); }
  
  public SunWritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, WritableRaster paramWritableRaster) { super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramWritableRaster); }
  
  public final void markDirty() { this.theTrackable.markDirty(); }
  
  public static interface DataStealer {
    byte[] getData(DataBufferByte param1DataBufferByte, int param1Int);
    
    short[] getData(DataBufferUShort param1DataBufferUShort, int param1Int);
    
    int[] getData(DataBufferInt param1DataBufferInt, int param1Int);
    
    StateTrackableDelegate getTrackable(DataBuffer param1DataBuffer);
    
    void setTrackable(DataBuffer param1DataBuffer, StateTrackableDelegate param1StateTrackableDelegate);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\SunWritableRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */