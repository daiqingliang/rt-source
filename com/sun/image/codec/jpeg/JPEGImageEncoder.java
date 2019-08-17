package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.OutputStream;

public interface JPEGImageEncoder {
  OutputStream getOutputStream();
  
  void setJPEGEncodeParam(JPEGEncodeParam paramJPEGEncodeParam);
  
  JPEGEncodeParam getJPEGEncodeParam();
  
  JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage paramBufferedImage) throws ImageFormatException;
  
  void encode(BufferedImage paramBufferedImage) throws IOException, ImageFormatException;
  
  void encode(BufferedImage paramBufferedImage, JPEGEncodeParam paramJPEGEncodeParam) throws IOException, ImageFormatException;
  
  int getDefaultColorId(ColorModel paramColorModel);
  
  JPEGEncodeParam getDefaultJPEGEncodeParam(Raster paramRaster, int paramInt) throws ImageFormatException;
  
  JPEGEncodeParam getDefaultJPEGEncodeParam(int paramInt1, int paramInt2) throws ImageFormatException;
  
  JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam paramJPEGDecodeParam) throws ImageFormatException;
  
  void encode(Raster paramRaster) throws IOException, ImageFormatException;
  
  void encode(Raster paramRaster, JPEGEncodeParam paramJPEGEncodeParam) throws IOException, ImageFormatException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\image\codec\jpeg\JPEGImageEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */