package javax.imageio;

import javax.imageio.metadata.IIOMetadata;

public interface ImageTranscoder {
  IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam);
  
  IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\ImageTranscoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */