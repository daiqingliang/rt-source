package javax.swing.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class BorderUIResource implements Border, UIResource, Serializable {
  static Border etched;
  
  static Border loweredBevel;
  
  static Border raisedBevel;
  
  static Border blackLine;
  
  private Border delegate;
  
  public static Border getEtchedBorderUIResource() {
    if (etched == null)
      etched = new EtchedBorderUIResource(); 
    return etched;
  }
  
  public static Border getLoweredBevelBorderUIResource() {
    if (loweredBevel == null)
      loweredBevel = new BevelBorderUIResource(1); 
    return loweredBevel;
  }
  
  public static Border getRaisedBevelBorderUIResource() {
    if (raisedBevel == null)
      raisedBevel = new BevelBorderUIResource(0); 
    return raisedBevel;
  }
  
  public static Border getBlackLineBorderUIResource() {
    if (blackLine == null)
      blackLine = new LineBorderUIResource(Color.black); 
    return blackLine;
  }
  
  public BorderUIResource(Border paramBorder) {
    if (paramBorder == null)
      throw new IllegalArgumentException("null border delegate argument"); 
    this.delegate = paramBorder;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.delegate.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public Insets getBorderInsets(Component paramComponent) { return this.delegate.getBorderInsets(paramComponent); }
  
  public boolean isBorderOpaque() { return this.delegate.isBorderOpaque(); }
  
  public static class BevelBorderUIResource extends BevelBorder implements UIResource {
    public BevelBorderUIResource(int param1Int) { super(param1Int); }
    
    public BevelBorderUIResource(int param1Int, Color param1Color1, Color param1Color2) { super(param1Int, param1Color1, param1Color2); }
    
    @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
    public BevelBorderUIResource(int param1Int, Color param1Color1, Color param1Color2, Color param1Color3, Color param1Color4) { super(param1Int, param1Color1, param1Color2, param1Color3, param1Color4); }
  }
  
  public static class CompoundBorderUIResource extends CompoundBorder implements UIResource {
    @ConstructorProperties({"outsideBorder", "insideBorder"})
    public CompoundBorderUIResource(Border param1Border1, Border param1Border2) { super(param1Border1, param1Border2); }
  }
  
  public static class EmptyBorderUIResource extends EmptyBorder implements UIResource {
    public EmptyBorderUIResource(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    @ConstructorProperties({"borderInsets"})
    public EmptyBorderUIResource(Insets param1Insets) { super(param1Insets); }
  }
  
  public static class EtchedBorderUIResource extends EtchedBorder implements UIResource {
    public EtchedBorderUIResource() {}
    
    public EtchedBorderUIResource(int param1Int) { super(param1Int); }
    
    public EtchedBorderUIResource(Color param1Color1, Color param1Color2) { super(param1Color1, param1Color2); }
    
    @ConstructorProperties({"etchType", "highlightColor", "shadowColor"})
    public EtchedBorderUIResource(int param1Int, Color param1Color1, Color param1Color2) { super(param1Int, param1Color1, param1Color2); }
  }
  
  public static class LineBorderUIResource extends LineBorder implements UIResource {
    public LineBorderUIResource(Color param1Color) { super(param1Color); }
    
    @ConstructorProperties({"lineColor", "thickness"})
    public LineBorderUIResource(Color param1Color, int param1Int) { super(param1Color, param1Int); }
  }
  
  public static class MatteBorderUIResource extends MatteBorder implements UIResource {
    public MatteBorderUIResource(int param1Int1, int param1Int2, int param1Int3, int param1Int4, Color param1Color) { super(param1Int1, param1Int2, param1Int3, param1Int4, param1Color); }
    
    public MatteBorderUIResource(int param1Int1, int param1Int2, int param1Int3, int param1Int4, Icon param1Icon) { super(param1Int1, param1Int2, param1Int3, param1Int4, param1Icon); }
    
    public MatteBorderUIResource(Icon param1Icon) { super(param1Icon); }
  }
  
  public static class TitledBorderUIResource extends TitledBorder implements UIResource {
    public TitledBorderUIResource(String param1String) { super(param1String); }
    
    public TitledBorderUIResource(Border param1Border) { super(param1Border); }
    
    public TitledBorderUIResource(Border param1Border, String param1String) { super(param1Border, param1String); }
    
    public TitledBorderUIResource(Border param1Border, String param1String, int param1Int1, int param1Int2) { super(param1Border, param1String, param1Int1, param1Int2); }
    
    public TitledBorderUIResource(Border param1Border, String param1String, int param1Int1, int param1Int2, Font param1Font) { super(param1Border, param1String, param1Int1, param1Int2, param1Font); }
    
    @ConstructorProperties({"border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor"})
    public TitledBorderUIResource(Border param1Border, String param1String, int param1Int1, int param1Int2, Font param1Font, Color param1Color) { super(param1Border, param1String, param1Int1, param1Int2, param1Font, param1Color); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\BorderUIResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */