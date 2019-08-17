package com.sun.imageio.plugins.jpeg;

import java.util.ListResourceBundle;

public class JPEGImageWriterResources extends ListResourceBundle {
  protected Object[][] getContents() { return new Object[][] { 
        { Integer.toString(0), "Only Rasters or band subsets may be written with a destination type. Destination type ignored." }, { Integer.toString(1), "Stream metadata ignored on write" }, { Integer.toString(2), "Metadata component ids incompatible with destination type. Metadata modified." }, { Integer.toString(3), "Metadata JFIF settings incompatible with destination type. Metadata modified." }, { Integer.toString(4), "Metadata Adobe settings incompatible with destination type. Metadata modified." }, { Integer.toString(5), "Metadata JFIF settings incompatible with image type. Metadata modified." }, { Integer.toString(6), "Metadata Adobe settings incompatible with image type. Metadata modified." }, { Integer.toString(7), "Metadata must be JPEGMetadata when writing a Raster. Metadata ignored." }, { Integer.toString(8), "Band subset not allowed for an IndexColorModel image.  Band subset ignored." }, { Integer.toString(9), "Thumbnails must be simple (possibly index) RGB or grayscale.  Incompatible thumbnail ignored." }, 
        { Integer.toString(10), "Thumbnails ignored for non-JFIF-compatible image." }, { Integer.toString(11), "Thumbnails require JFIF marker segment.  Missing node added to metadata." }, { Integer.toString(12), "Thumbnail clipped." }, { Integer.toString(13), "Metadata adjusted (made JFIF-compatible) for thumbnail." }, { Integer.toString(14), "RGB thumbnail can't be written as indexed.  Written as RGB" }, { Integer.toString(15), "Grayscale thumbnail can't be written as indexed.  Written as JPEG" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageWriterResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */