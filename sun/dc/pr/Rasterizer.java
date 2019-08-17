package sun.dc.pr;

import sun.dc.DuctusRenderingEngine;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathError;
import sun.dc.path.PathException;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.pipe.AATileGenerator;

public class Rasterizer implements AATileGenerator {
  public static final int EOFILL = 1;
  
  public static final int NZFILL = 2;
  
  public static final int STROKE = 3;
  
  public static final int ROUND = 10;
  
  public static final int SQUARE = 20;
  
  public static final int BUTT = 30;
  
  public static final int BEVEL = 40;
  
  public static final int MITER = 50;
  
  public static final int TILE_SIZE = 1 << PathFiller.tileSizeL2S;
  
  public static final int TILE_SIZE_L2S = PathFiller.tileSizeL2S;
  
  public static final int MAX_ALPHA = 1000000;
  
  public static final int MAX_MITER = 10;
  
  public static final int MAX_WN = 63;
  
  public static final int TILE_IS_ALL_0 = 0;
  
  public static final int TILE_IS_ALL_1 = 1;
  
  public static final int TILE_IS_GENERAL = 2;
  
  private static final int BEG = 1;
  
  private static final int PAC_FILL = 2;
  
  private static final int PAC_STROKE = 3;
  
  private static final int PATH = 4;
  
  private static final int SUBPATH = 5;
  
  private static final int RAS = 6;
  
  private int state = 1;
  
  private PathFiller filler = new PathFiller();
  
  private PathStroker stroker = new PathStroker(this.filler);
  
  private PathDasher dasher = new PathDasher(this.stroker);
  
  private PathConsumer curPC;
  
  public Rasterizer() { Disposer.addRecord(this, new ConsumerDisposer(this.filler, this.stroker, this.dasher)); }
  
  public void setUsage(int paramInt) throws PRError {
    if (this.state != 1)
      throw new PRError("setUsage: unexpected"); 
    if (paramInt == 1) {
      this.filler.setFillMode(1);
      this.curPC = this.filler;
      this.state = 2;
    } else if (paramInt == 2) {
      this.filler.setFillMode(2);
      this.curPC = this.filler;
      this.state = 2;
    } else if (paramInt == 3) {
      this.curPC = this.stroker;
      this.filler.setFillMode(2);
      this.stroker.setPenDiameter(1.0F);
      this.stroker.setPenT4(null);
      this.stroker.setCaps(10);
      this.stroker.setCorners(10, 0.0F);
      this.state = 3;
    } else {
      throw new PRError("setUsage: unknown usage type");
    } 
  }
  
  public void setPenDiameter(float paramFloat) throws PRError {
    if (this.state != 3)
      throw new PRError("setPenDiameter: unexpected"); 
    this.stroker.setPenDiameter(paramFloat);
  }
  
  public void setPenT4(float[] paramArrayOfFloat) throws PRError {
    if (this.state != 3)
      throw new PRError("setPenT4: unexpected"); 
    this.stroker.setPenT4(paramArrayOfFloat);
  }
  
  public void setPenFitting(float paramFloat, int paramInt) throws PRError {
    if (this.state != 3)
      throw new PRError("setPenFitting: unexpected"); 
    this.stroker.setPenFitting(paramFloat, paramInt);
  }
  
  public void setPenDisplacement(float paramFloat1, float paramFloat2) throws PRError {
    if (this.state != 3)
      throw new PRError("setPenDisplacement: unexpected"); 
    float[] arrayOfFloat = { 1.0F, 0.0F, 0.0F, 1.0F, paramFloat1, paramFloat2 };
    this.stroker.setOutputT6(arrayOfFloat);
  }
  
  public void setCaps(int paramInt) throws PRError {
    if (this.state != 3)
      throw new PRError("setCaps: unexpected"); 
    this.stroker.setCaps(paramInt);
  }
  
  public void setCorners(int paramInt, float paramFloat) throws PRError {
    if (this.state != 3)
      throw new PRError("setCorners: unexpected"); 
    this.stroker.setCorners(paramInt, paramFloat);
  }
  
  public void setDash(float[] paramArrayOfFloat, float paramFloat) throws PRError {
    if (this.state != 3)
      throw new PRError("setDash: unexpected"); 
    this.dasher.setDash(paramArrayOfFloat, paramFloat);
    this.curPC = this.dasher;
  }
  
  public void setDashT4(float[] paramArrayOfFloat) throws PRError {
    if (this.state != 3)
      throw new PRError("setDashT4: unexpected"); 
    this.dasher.setDashT4(paramArrayOfFloat);
  }
  
  public void beginPath(float[] paramArrayOfFloat) throws PRError { beginPath(); }
  
  public void beginPath() {
    if (this.state != 2 && this.state != 3)
      throw new PRError("beginPath: unexpected"); 
    try {
      this.curPC.beginPath();
      this.state = 4;
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } 
  }
  
  public void beginSubpath(float paramFloat1, float paramFloat2) throws PRError {
    if (this.state != 4 && this.state != 5)
      throw new PRError("beginSubpath: unexpected"); 
    try {
      this.curPC.beginSubpath(paramFloat1, paramFloat2);
      this.state = 5;
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } 
  }
  
  public void appendLine(float paramFloat1, float paramFloat2) throws PRError {
    if (this.state != 5)
      throw new PRError("appendLine: unexpected"); 
    try {
      this.curPC.appendLine(paramFloat1, paramFloat2);
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } 
  }
  
  public void appendQuadratic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) throws PRError {
    if (this.state != 5)
      throw new PRError("appendQuadratic: unexpected"); 
    try {
      this.curPC.appendQuadratic(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } 
  }
  
  public void appendCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) throws PRError {
    if (this.state != 5)
      throw new PRError("appendCubic: unexpected"); 
    try {
      this.curPC.appendCubic(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } 
  }
  
  public void closedSubpath() {
    if (this.state != 5)
      throw new PRError("closedSubpath: unexpected"); 
    try {
      this.curPC.closedSubpath();
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } 
  }
  
  public void endPath() {
    if (this.state != 4 && this.state != 5)
      throw new PRError("endPath: unexpected"); 
    try {
      this.curPC.endPath();
      this.state = 6;
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } catch (PathException pathException) {
      throw new PRException(pathException.getMessage());
    } 
  }
  
  public void useProxy(FastPathProducer paramFastPathProducer) throws PRError, PRException {
    if (this.state != 2 && this.state != 3)
      throw new PRError("useProxy: unexpected"); 
    try {
      this.curPC.useProxy(paramFastPathProducer);
      this.state = 6;
    } catch (PathError pathError) {
      throw new PRError(pathError.getMessage());
    } catch (PathException pathException) {
      throw new PRException(pathException.getMessage());
    } 
  }
  
  public void getAlphaBox(int[] paramArrayOfInt) throws PRError { this.filler.getAlphaBox(paramArrayOfInt); }
  
  public void setOutputArea(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2) throws PRError, PRException { this.filler.setOutputArea(paramFloat1, paramFloat2, paramInt1, paramInt2); }
  
  public int getTileState() throws PRError { return this.filler.getTileState(); }
  
  public void writeAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) throws PRError, PRException, InterruptedException { this.filler.writeAlpha(paramArrayOfByte, paramInt1, paramInt2, paramInt3); }
  
  public void writeAlpha(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3) throws PRError, PRException, InterruptedException { this.filler.writeAlpha(paramArrayOfChar, paramInt1, paramInt2, paramInt3); }
  
  public void nextTile() { this.filler.nextTile(); }
  
  public void reset() {
    this.state = 1;
    this.filler.reset();
    this.stroker.reset();
    this.dasher.reset();
  }
  
  public int getTileWidth() throws PRError { return TILE_SIZE; }
  
  public int getTileHeight() throws PRError { return TILE_SIZE; }
  
  public int getTypicalAlpha() throws PRError {
    int i = this.filler.getTileState();
    switch (i) {
      case 0:
        i = 0;
        break;
      case 1:
        i = 255;
        break;
      case 2:
        i = 128;
        break;
    } 
    return i;
  }
  
  public void getAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    synchronized (Rasterizer.class) {
      try {
        this.filler.writeAlpha(paramArrayOfByte, 1, paramInt2, paramInt1);
      } catch (PRException pRException) {
        throw new InternalError("Ductus AA error: " + pRException.getMessage());
      } catch (InterruptedException interruptedException) {
        Thread.currentThread().interrupt();
      } 
    } 
  }
  
  public void dispose() { DuctusRenderingEngine.dropRasterizer(this); }
  
  private static class ConsumerDisposer implements DisposerRecord {
    PathConsumer filler;
    
    PathConsumer stroker;
    
    PathConsumer dasher;
    
    public ConsumerDisposer(PathConsumer param1PathConsumer1, PathConsumer param1PathConsumer2, PathConsumer param1PathConsumer3) {
      this.filler = param1PathConsumer1;
      this.stroker = param1PathConsumer2;
      this.dasher = param1PathConsumer3;
    }
    
    public void dispose() {
      this.filler.dispose();
      this.stroker.dispose();
      this.dasher.dispose();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\dc\pr\Rasterizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */