package javax.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.StrokeBorder;
import javax.swing.border.TitledBorder;

public class BorderFactory {
  static final Border sharedRaisedBevel = new BevelBorder(0);
  
  static final Border sharedLoweredBevel = new BevelBorder(1);
  
  private static Border sharedSoftRaisedBevel;
  
  private static Border sharedSoftLoweredBevel;
  
  static final Border sharedEtchedBorder = new EtchedBorder();
  
  private static Border sharedRaisedEtchedBorder;
  
  static final Border emptyBorder = new EmptyBorder(0, 0, 0, 0);
  
  private static Border sharedDashedBorder;
  
  public static Border createLineBorder(Color paramColor) { return new LineBorder(paramColor, 1); }
  
  public static Border createLineBorder(Color paramColor, int paramInt) { return new LineBorder(paramColor, paramInt); }
  
  public static Border createLineBorder(Color paramColor, int paramInt, boolean paramBoolean) { return new LineBorder(paramColor, paramInt, paramBoolean); }
  
  public static Border createRaisedBevelBorder() { return createSharedBevel(0); }
  
  public static Border createLoweredBevelBorder() { return createSharedBevel(1); }
  
  public static Border createBevelBorder(int paramInt) { return createSharedBevel(paramInt); }
  
  public static Border createBevelBorder(int paramInt, Color paramColor1, Color paramColor2) { return new BevelBorder(paramInt, paramColor1, paramColor2); }
  
  public static Border createBevelBorder(int paramInt, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) { return new BevelBorder(paramInt, paramColor1, paramColor2, paramColor3, paramColor4); }
  
  static Border createSharedBevel(int paramInt) { return (paramInt == 0) ? sharedRaisedBevel : ((paramInt == 1) ? sharedLoweredBevel : null); }
  
  public static Border createRaisedSoftBevelBorder() {
    if (sharedSoftRaisedBevel == null)
      sharedSoftRaisedBevel = new SoftBevelBorder(0); 
    return sharedSoftRaisedBevel;
  }
  
  public static Border createLoweredSoftBevelBorder() {
    if (sharedSoftLoweredBevel == null)
      sharedSoftLoweredBevel = new SoftBevelBorder(1); 
    return sharedSoftLoweredBevel;
  }
  
  public static Border createSoftBevelBorder(int paramInt) { return (paramInt == 0) ? createRaisedSoftBevelBorder() : ((paramInt == 1) ? createLoweredSoftBevelBorder() : null); }
  
  public static Border createSoftBevelBorder(int paramInt, Color paramColor1, Color paramColor2) { return new SoftBevelBorder(paramInt, paramColor1, paramColor2); }
  
  public static Border createSoftBevelBorder(int paramInt, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) { return new SoftBevelBorder(paramInt, paramColor1, paramColor2, paramColor3, paramColor4); }
  
  public static Border createEtchedBorder() { return sharedEtchedBorder; }
  
  public static Border createEtchedBorder(Color paramColor1, Color paramColor2) { return new EtchedBorder(paramColor1, paramColor2); }
  
  public static Border createEtchedBorder(int paramInt) {
    switch (paramInt) {
      case 0:
        if (sharedRaisedEtchedBorder == null)
          sharedRaisedEtchedBorder = new EtchedBorder(0); 
        return sharedRaisedEtchedBorder;
      case 1:
        return sharedEtchedBorder;
    } 
    throw new IllegalArgumentException("type must be one of EtchedBorder.RAISED or EtchedBorder.LOWERED");
  }
  
  public static Border createEtchedBorder(int paramInt, Color paramColor1, Color paramColor2) { return new EtchedBorder(paramInt, paramColor1, paramColor2); }
  
  public static TitledBorder createTitledBorder(String paramString) { return new TitledBorder(paramString); }
  
  public static TitledBorder createTitledBorder(Border paramBorder) { return new TitledBorder(paramBorder); }
  
  public static TitledBorder createTitledBorder(Border paramBorder, String paramString) { return new TitledBorder(paramBorder, paramString); }
  
  public static TitledBorder createTitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2) { return new TitledBorder(paramBorder, paramString, paramInt1, paramInt2); }
  
  public static TitledBorder createTitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont) { return new TitledBorder(paramBorder, paramString, paramInt1, paramInt2, paramFont); }
  
  public static TitledBorder createTitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont, Color paramColor) { return new TitledBorder(paramBorder, paramString, paramInt1, paramInt2, paramFont, paramColor); }
  
  public static Border createEmptyBorder() { return emptyBorder; }
  
  public static Border createEmptyBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new EmptyBorder(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public static CompoundBorder createCompoundBorder() { return new CompoundBorder(); }
  
  public static CompoundBorder createCompoundBorder(Border paramBorder1, Border paramBorder2) { return new CompoundBorder(paramBorder1, paramBorder2); }
  
  public static MatteBorder createMatteBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) { return new MatteBorder(paramInt1, paramInt2, paramInt3, paramInt4, paramColor); }
  
  public static MatteBorder createMatteBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Icon paramIcon) { return new MatteBorder(paramInt1, paramInt2, paramInt3, paramInt4, paramIcon); }
  
  public static Border createStrokeBorder(BasicStroke paramBasicStroke) { return new StrokeBorder(paramBasicStroke); }
  
  public static Border createStrokeBorder(BasicStroke paramBasicStroke, Paint paramPaint) { return new StrokeBorder(paramBasicStroke, paramPaint); }
  
  public static Border createDashedBorder(Paint paramPaint) { return createDashedBorder(paramPaint, 1.0F, 1.0F, 1.0F, false); }
  
  public static Border createDashedBorder(Paint paramPaint, float paramFloat1, float paramFloat2) { return createDashedBorder(paramPaint, 1.0F, paramFloat1, paramFloat2, false); }
  
  public static Border createDashedBorder(Paint paramPaint, float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean) {
    boolean bool = (!paramBoolean && paramPaint == null && paramFloat1 == 1.0F && paramFloat2 == 1.0F && paramFloat3 == 1.0F) ? 1 : 0;
    if (bool && sharedDashedBorder != null)
      return sharedDashedBorder; 
    if (paramFloat1 < 1.0F)
      throw new IllegalArgumentException("thickness is less than 1"); 
    if (paramFloat2 < 1.0F)
      throw new IllegalArgumentException("length is less than 1"); 
    if (paramFloat3 < 0.0F)
      throw new IllegalArgumentException("spacing is less than 0"); 
    byte b1 = paramBoolean ? 1 : 2;
    byte b2 = paramBoolean ? 1 : 0;
    float[] arrayOfFloat = { paramFloat1 * (paramFloat2 - 1.0F), paramFloat1 * (paramFloat3 + 1.0F) };
    Border border = createStrokeBorder(new BasicStroke(paramFloat1, b1, b2, paramFloat1 * 2.0F, arrayOfFloat, 0.0F), paramPaint);
    if (bool)
      sharedDashedBorder = border; 
    return border;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\BorderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */