package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

public interface JPEGImageDecoder {
  JPEGDecodeParam getJPEGDecodeParam();
  
  void setJPEGDecodeParam(JPEGDecodeParam paramJPEGDecodeParam);
  
  InputStream getInputStream();
  
  Raster decodeAsRaster() throws IOException, ImageFormatException;
  
  BufferedImage decodeAsBufferedImage() throws IOException, ImageFormatException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\image\codec\jpeg\JPEGImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */