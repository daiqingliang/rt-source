package javax.print.attribute.standard;

import java.util.HashMap;
import java.util.Vector;
import javax.print.attribute.Attribute;
import javax.print.attribute.Size2DSyntax;

public class MediaSize extends Size2DSyntax implements Attribute {
  private static final long serialVersionUID = -1967958664615414771L;
  
  private MediaSizeName mediaName;
  
  private static HashMap mediaMap = new HashMap(100, 10.0F);
  
  private static Vector sizeVector = new Vector(100, 10);
  
  public MediaSize(float paramFloat1, float paramFloat2, int paramInt) {
    super(paramFloat1, paramFloat2, paramInt);
    if (paramFloat1 > paramFloat2)
      throw new IllegalArgumentException("X dimension > Y dimension"); 
    sizeVector.add(this);
  }
  
  public MediaSize(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt1, paramInt2, paramInt3);
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("X dimension > Y dimension"); 
    sizeVector.add(this);
  }
  
  public MediaSize(float paramFloat1, float paramFloat2, int paramInt, MediaSizeName paramMediaSizeName) {
    super(paramFloat1, paramFloat2, paramInt);
    if (paramFloat1 > paramFloat2)
      throw new IllegalArgumentException("X dimension > Y dimension"); 
    if (paramMediaSizeName != null && mediaMap.get(paramMediaSizeName) == null) {
      this.mediaName = paramMediaSizeName;
      mediaMap.put(this.mediaName, this);
    } 
    sizeVector.add(this);
  }
  
  public MediaSize(int paramInt1, int paramInt2, int paramInt3, MediaSizeName paramMediaSizeName) {
    super(paramInt1, paramInt2, paramInt3);
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("X dimension > Y dimension"); 
    if (paramMediaSizeName != null && mediaMap.get(paramMediaSizeName) == null) {
      this.mediaName = paramMediaSizeName;
      mediaMap.put(this.mediaName, this);
    } 
    sizeVector.add(this);
  }
  
  public MediaSizeName getMediaSizeName() { return this.mediaName; }
  
  public static MediaSize getMediaSizeForName(MediaSizeName paramMediaSizeName) { return (MediaSize)mediaMap.get(paramMediaSizeName); }
  
  public static MediaSizeName findMedia(float paramFloat1, float paramFloat2, int paramInt) {
    MediaSize mediaSize = ISO.A4;
    if (paramFloat1 <= 0.0F || paramFloat2 <= 0.0F || paramInt < 1)
      throw new IllegalArgumentException("args must be +ve values"); 
    double d = (paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2);
    float f1 = paramFloat1;
    float f2 = paramFloat2;
    for (byte b = 0; b < sizeVector.size(); b++) {
      MediaSize mediaSize1 = (MediaSize)sizeVector.elementAt(b);
      float[] arrayOfFloat = mediaSize1.getSize(paramInt);
      if (paramFloat1 == arrayOfFloat[0] && paramFloat2 == arrayOfFloat[1]) {
        mediaSize = mediaSize1;
        break;
      } 
      f1 = paramFloat1 - arrayOfFloat[0];
      f2 = paramFloat2 - arrayOfFloat[1];
      double d1 = (f1 * f1 + f2 * f2);
      if (d1 < d) {
        d = d1;
        mediaSize = mediaSize1;
      } 
    } 
    return mediaSize.getMediaSizeName();
  }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof MediaSize); }
  
  public final Class<? extends Attribute> getCategory() { return MediaSize.class; }
  
  public final String getName() { return "media-size"; }
  
  static  {
    MediaSize mediaSize1 = ISO.A4;
    MediaSize mediaSize2 = JIS.B5;
    MediaSize mediaSize3 = NA.LETTER;
    MediaSize mediaSize4 = Engineering.C;
    MediaSize mediaSize5 = Other.EXECUTIVE;
  }
  
  public static final class Engineering {
    public static final MediaSize A = new MediaSize(8.5F, 11.0F, 25400, MediaSizeName.A);
    
    public static final MediaSize B = new MediaSize(11.0F, 17.0F, 25400, MediaSizeName.B);
    
    public static final MediaSize C = new MediaSize(17.0F, 22.0F, 25400, MediaSizeName.C);
    
    public static final MediaSize D = new MediaSize(22.0F, 34.0F, 25400, MediaSizeName.D);
    
    public static final MediaSize E = new MediaSize(34.0F, 44.0F, 25400, MediaSizeName.E);
  }
  
  public static final class ISO {
    public static final MediaSize A0 = new MediaSize(841, 1189, 1000, MediaSizeName.ISO_A0);
    
    public static final MediaSize A1 = new MediaSize(594, 841, 1000, MediaSizeName.ISO_A1);
    
    public static final MediaSize A2 = new MediaSize(420, 594, 1000, MediaSizeName.ISO_A2);
    
    public static final MediaSize A3 = new MediaSize(297, 420, 1000, MediaSizeName.ISO_A3);
    
    public static final MediaSize A4 = new MediaSize(210, 297, 1000, MediaSizeName.ISO_A4);
    
    public static final MediaSize A5 = new MediaSize(148, 210, 1000, MediaSizeName.ISO_A5);
    
    public static final MediaSize A6 = new MediaSize(105, 148, 1000, MediaSizeName.ISO_A6);
    
    public static final MediaSize A7 = new MediaSize(74, 105, 1000, MediaSizeName.ISO_A7);
    
    public static final MediaSize A8 = new MediaSize(52, 74, 1000, MediaSizeName.ISO_A8);
    
    public static final MediaSize A9 = new MediaSize(37, 52, 1000, MediaSizeName.ISO_A9);
    
    public static final MediaSize A10 = new MediaSize(26, 37, 1000, MediaSizeName.ISO_A10);
    
    public static final MediaSize B0 = new MediaSize(1000, 1414, 1000, MediaSizeName.ISO_B0);
    
    public static final MediaSize B1 = new MediaSize(707, 1000, 1000, MediaSizeName.ISO_B1);
    
    public static final MediaSize B2 = new MediaSize(500, 707, 1000, MediaSizeName.ISO_B2);
    
    public static final MediaSize B3 = new MediaSize(353, 500, 1000, MediaSizeName.ISO_B3);
    
    public static final MediaSize B4 = new MediaSize(250, 353, 1000, MediaSizeName.ISO_B4);
    
    public static final MediaSize B5 = new MediaSize(176, 250, 1000, MediaSizeName.ISO_B5);
    
    public static final MediaSize B6 = new MediaSize(125, 176, 1000, MediaSizeName.ISO_B6);
    
    public static final MediaSize B7 = new MediaSize(88, 125, 1000, MediaSizeName.ISO_B7);
    
    public static final MediaSize B8 = new MediaSize(62, 88, 1000, MediaSizeName.ISO_B8);
    
    public static final MediaSize B9 = new MediaSize(44, 62, 1000, MediaSizeName.ISO_B9);
    
    public static final MediaSize B10 = new MediaSize(31, 44, 1000, MediaSizeName.ISO_B10);
    
    public static final MediaSize C3 = new MediaSize(324, 458, 1000, MediaSizeName.ISO_C3);
    
    public static final MediaSize C4 = new MediaSize(229, 324, 1000, MediaSizeName.ISO_C4);
    
    public static final MediaSize C5 = new MediaSize(162, 229, 1000, MediaSizeName.ISO_C5);
    
    public static final MediaSize C6 = new MediaSize(114, 162, 1000, MediaSizeName.ISO_C6);
    
    public static final MediaSize DESIGNATED_LONG = new MediaSize(110, 220, 1000, MediaSizeName.ISO_DESIGNATED_LONG);
  }
  
  public static final class JIS {
    public static final MediaSize B0 = new MediaSize(1030, 1456, 1000, MediaSizeName.JIS_B0);
    
    public static final MediaSize B1 = new MediaSize(728, 1030, 1000, MediaSizeName.JIS_B1);
    
    public static final MediaSize B2 = new MediaSize(515, 728, 1000, MediaSizeName.JIS_B2);
    
    public static final MediaSize B3 = new MediaSize(364, 515, 1000, MediaSizeName.JIS_B3);
    
    public static final MediaSize B4 = new MediaSize(257, 364, 1000, MediaSizeName.JIS_B4);
    
    public static final MediaSize B5 = new MediaSize(182, 257, 1000, MediaSizeName.JIS_B5);
    
    public static final MediaSize B6 = new MediaSize(128, 182, 1000, MediaSizeName.JIS_B6);
    
    public static final MediaSize B7 = new MediaSize(91, 128, 1000, MediaSizeName.JIS_B7);
    
    public static final MediaSize B8 = new MediaSize(64, 91, 1000, MediaSizeName.JIS_B8);
    
    public static final MediaSize B9 = new MediaSize(45, 64, 1000, MediaSizeName.JIS_B9);
    
    public static final MediaSize B10 = new MediaSize(32, 45, 1000, MediaSizeName.JIS_B10);
    
    public static final MediaSize CHOU_1 = new MediaSize(142, 332, 1000);
    
    public static final MediaSize CHOU_2 = new MediaSize(119, 277, 1000);
    
    public static final MediaSize CHOU_3 = new MediaSize(120, 235, 1000);
    
    public static final MediaSize CHOU_4 = new MediaSize(90, 205, 1000);
    
    public static final MediaSize CHOU_30 = new MediaSize(92, 235, 1000);
    
    public static final MediaSize CHOU_40 = new MediaSize(90, 225, 1000);
    
    public static final MediaSize KAKU_0 = new MediaSize(287, 382, 1000);
    
    public static final MediaSize KAKU_1 = new MediaSize(270, 382, 1000);
    
    public static final MediaSize KAKU_2 = new MediaSize(240, 332, 1000);
    
    public static final MediaSize KAKU_3 = new MediaSize(216, 277, 1000);
    
    public static final MediaSize KAKU_4 = new MediaSize(197, 267, 1000);
    
    public static final MediaSize KAKU_5 = new MediaSize(190, 240, 1000);
    
    public static final MediaSize KAKU_6 = new MediaSize(162, 229, 1000);
    
    public static final MediaSize KAKU_7 = new MediaSize(142, 205, 1000);
    
    public static final MediaSize KAKU_8 = new MediaSize(119, 197, 1000);
    
    public static final MediaSize KAKU_20 = new MediaSize(229, 324, 1000);
    
    public static final MediaSize KAKU_A4 = new MediaSize(228, 312, 1000);
    
    public static final MediaSize YOU_1 = new MediaSize(120, 176, 1000);
    
    public static final MediaSize YOU_2 = new MediaSize(114, 162, 1000);
    
    public static final MediaSize YOU_3 = new MediaSize(98, 148, 1000);
    
    public static final MediaSize YOU_4 = new MediaSize(105, 235, 1000);
    
    public static final MediaSize YOU_5 = new MediaSize(95, 217, 1000);
    
    public static final MediaSize YOU_6 = new MediaSize(98, 190, 1000);
    
    public static final MediaSize YOU_7 = new MediaSize(92, 165, 1000);
  }
  
  public static final class NA {
    public static final MediaSize LETTER = new MediaSize(8.5F, 11.0F, 25400, MediaSizeName.NA_LETTER);
    
    public static final MediaSize LEGAL = new MediaSize(8.5F, 14.0F, 25400, MediaSizeName.NA_LEGAL);
    
    public static final MediaSize NA_5X7 = new MediaSize(5, 7, 25400, MediaSizeName.NA_5X7);
    
    public static final MediaSize NA_8X10 = new MediaSize(8, 10, 25400, MediaSizeName.NA_8X10);
    
    public static final MediaSize NA_NUMBER_9_ENVELOPE = new MediaSize(3.875F, 8.875F, 25400, MediaSizeName.NA_NUMBER_9_ENVELOPE);
    
    public static final MediaSize NA_NUMBER_10_ENVELOPE = new MediaSize(4.125F, 9.5F, 25400, MediaSizeName.NA_NUMBER_10_ENVELOPE);
    
    public static final MediaSize NA_NUMBER_11_ENVELOPE = new MediaSize(4.5F, 10.375F, 25400, MediaSizeName.NA_NUMBER_11_ENVELOPE);
    
    public static final MediaSize NA_NUMBER_12_ENVELOPE = new MediaSize(4.75F, 11.0F, 25400, MediaSizeName.NA_NUMBER_12_ENVELOPE);
    
    public static final MediaSize NA_NUMBER_14_ENVELOPE = new MediaSize(5.0F, 11.5F, 25400, MediaSizeName.NA_NUMBER_14_ENVELOPE);
    
    public static final MediaSize NA_6X9_ENVELOPE = new MediaSize(6.0F, 9.0F, 25400, MediaSizeName.NA_6X9_ENVELOPE);
    
    public static final MediaSize NA_7X9_ENVELOPE = new MediaSize(7.0F, 9.0F, 25400, MediaSizeName.NA_7X9_ENVELOPE);
    
    public static final MediaSize NA_9x11_ENVELOPE = new MediaSize(9.0F, 11.0F, 25400, MediaSizeName.NA_9X11_ENVELOPE);
    
    public static final MediaSize NA_9x12_ENVELOPE = new MediaSize(9.0F, 12.0F, 25400, MediaSizeName.NA_9X12_ENVELOPE);
    
    public static final MediaSize NA_10x13_ENVELOPE = new MediaSize(10.0F, 13.0F, 25400, MediaSizeName.NA_10X13_ENVELOPE);
    
    public static final MediaSize NA_10x14_ENVELOPE = new MediaSize(10.0F, 14.0F, 25400, MediaSizeName.NA_10X14_ENVELOPE);
    
    public static final MediaSize NA_10X15_ENVELOPE = new MediaSize(10.0F, 15.0F, 25400, MediaSizeName.NA_10X15_ENVELOPE);
  }
  
  public static final class Other {
    public static final MediaSize EXECUTIVE = new MediaSize(7.25F, 10.5F, 25400, MediaSizeName.EXECUTIVE);
    
    public static final MediaSize LEDGER = new MediaSize(11.0F, 17.0F, 25400, MediaSizeName.LEDGER);
    
    public static final MediaSize TABLOID = new MediaSize(11.0F, 17.0F, 25400, MediaSizeName.TABLOID);
    
    public static final MediaSize INVOICE = new MediaSize(5.5F, 8.5F, 25400, MediaSizeName.INVOICE);
    
    public static final MediaSize FOLIO = new MediaSize(8.5F, 13.0F, 25400, MediaSizeName.FOLIO);
    
    public static final MediaSize QUARTO = new MediaSize(8.5F, 10.83F, 25400, MediaSizeName.QUARTO);
    
    public static final MediaSize ITALY_ENVELOPE = new MediaSize(110, 230, 1000, MediaSizeName.ITALY_ENVELOPE);
    
    public static final MediaSize MONARCH_ENVELOPE = new MediaSize(3.87F, 7.5F, 25400, MediaSizeName.MONARCH_ENVELOPE);
    
    public static final MediaSize PERSONAL_ENVELOPE = new MediaSize(3.625F, 6.5F, 25400, MediaSizeName.PERSONAL_ENVELOPE);
    
    public static final MediaSize JAPANESE_POSTCARD = new MediaSize(100, 148, 1000, MediaSizeName.JAPANESE_POSTCARD);
    
    public static final MediaSize JAPANESE_DOUBLE_POSTCARD = new MediaSize(148, 200, 1000, MediaSizeName.JAPANESE_DOUBLE_POSTCARD);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\MediaSize.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */