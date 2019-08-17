package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class TruncatedFileException extends RuntimeException {
  private Raster ras = null;
  
  private BufferedImage bi = null;
  
  public TruncatedFileException(BufferedImage paramBufferedImage) {
    super("Premature end of input file");
    this.bi = paramBufferedImage;
    this.ras = paramBufferedImage.getData();
  }
  
  public TruncatedFileException(Raster paramRaster) {
    super("Premature end of input file");
    this.ras = paramRaster;
  }
  
  public Raster getRaster() { return this.ras; }
  
  public BufferedImage getBufferedImage() { return this.bi; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\image\codec\jpeg\TruncatedFileException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */