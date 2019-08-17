package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import sun.awt.image.ByteComponentRaster;

class PSPathGraphics extends PathGraphics {
  private static final int DEFAULT_USER_RES = 72;
  
  PSPathGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt, boolean paramBoolean) { super(paramGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, paramBoolean); }
  
  public Graphics create() { return new PSPathGraphics((Graphics2D)getDelegate().create(), getPrinterJob(), getPrintable(), getPageFormat(), getPageIndex(), canDoRedraws()); }
  
  public void fill(Shape paramShape, Color paramColor) { deviceFill(paramShape.getPathIterator(new AffineTransform()), paramColor); }
  
  public void drawString(String paramString, int paramInt1, int paramInt2) { drawString(paramString, paramInt1, paramInt2); }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2) { drawString(paramString, paramFloat1, paramFloat2, getFont(), getFontRenderContext(), 0.0F); }
  
  protected boolean canDrawStringToWidth() { return true; }
  
  protected int platformFontCount(Font paramFont, String paramString) {
    PSPrinterJob pSPrinterJob = (PSPrinterJob)getPrinterJob();
    return pSPrinterJob.platformFontCount(paramFont, paramString);
  }
  
  protected void drawString(String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3) {
    if (paramString.length() == 0)
      return; 
    if (paramFont.hasLayoutAttributes() && !this.printingGlyphVector) {
      TextLayout textLayout = new TextLayout(paramString, paramFont, paramFontRenderContext);
      textLayout.draw(this, paramFloat1, paramFloat2);
      return;
    } 
    Font font = getFont();
    if (!font.equals(paramFont)) {
      setFont(paramFont);
    } else {
      font = null;
    } 
    boolean bool1 = false;
    float f1 = 0.0F;
    float f2 = 0.0F;
    boolean bool2 = getFont().isTransformed();
    if (bool2) {
      AffineTransform affineTransform = getFont().getTransform();
      int i = affineTransform.getType();
      if (i == 1) {
        f1 = (float)affineTransform.getTranslateX();
        f2 = (float)affineTransform.getTranslateY();
        if (Math.abs(f1) < 1.0E-5D)
          f1 = 0.0F; 
        if (Math.abs(f2) < 1.0E-5D)
          f2 = 0.0F; 
        bool2 = false;
      } 
    } 
    boolean bool = !bool2 ? 1 : 0;
    if (!PSPrinterJob.shapeTextProp && bool) {
      PSPrinterJob pSPrinterJob = (PSPrinterJob)getPrinterJob();
      if (pSPrinterJob.setFont(getFont())) {
        try {
          pSPrinterJob.setColor((Color)getPaint());
        } catch (ClassCastException classCastException) {
          if (font != null)
            setFont(font); 
          throw new IllegalArgumentException("Expected a Color instance");
        } 
        pSPrinterJob.setTransform(getTransform());
        pSPrinterJob.setClip(getClip());
        bool1 = pSPrinterJob.textOut(this, paramString, paramFloat1 + f1, paramFloat2 + f2, paramFont, paramFontRenderContext, paramFloat3);
      } 
    } 
    if (!bool1) {
      if (font != null) {
        setFont(font);
        font = null;
      } 
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
    } 
    if (font != null)
      setFont(font); 
  }
  
  protected boolean drawImageToPlatform(Image paramImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    BufferedImage bufferedImage = getBufferedImage(paramImage);
    if (bufferedImage == null)
      return true; 
    PSPrinterJob pSPrinterJob = (PSPrinterJob)getPrinterJob();
    AffineTransform affineTransform = getTransform();
    if (paramAffineTransform == null)
      paramAffineTransform = new AffineTransform(); 
    affineTransform.concatenate(paramAffineTransform);
    double[] arrayOfDouble = new double[6];
    affineTransform.getMatrix(arrayOfDouble);
    Point2D.Float float1 = new Point2D.Float(1.0F, 0.0F);
    Point2D.Float float2 = new Point2D.Float(0.0F, 1.0F);
    affineTransform.deltaTransform(float1, float1);
    affineTransform.deltaTransform(float2, float2);
    Point2D.Float float3 = new Point2D.Float(0.0F, 0.0F);
    double d1 = float1.distance(float3);
    double d2 = float2.distance(float3);
    double d3 = pSPrinterJob.getXRes();
    double d4 = pSPrinterJob.getYRes();
    double d5 = d3 / 72.0D;
    double d6 = d4 / 72.0D;
    int i = affineTransform.getType();
    boolean bool = ((i & 0x30) != 0) ? 1 : 0;
    if (bool) {
      if (d1 > d5)
        d1 = d5; 
      if (d2 > d6)
        d2 = d6; 
    } 
    if (d1 != 0.0D && d2 != 0.0D) {
      AffineTransform affineTransform1 = new AffineTransform(arrayOfDouble[0] / d1, arrayOfDouble[1] / d2, arrayOfDouble[2] / d1, arrayOfDouble[3] / d2, arrayOfDouble[4] / d1, arrayOfDouble[5] / d2);
      Rectangle2D.Float float = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
      Shape shape = affineTransform1.createTransformedShape(float);
      Rectangle2D rectangle2D = shape.getBounds2D();
      rectangle2D.setRect(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth() + 0.001D, rectangle2D.getHeight() + 0.001D);
      int j = (int)rectangle2D.getWidth();
      int k = (int)rectangle2D.getHeight();
      if (j > 0 && k > 0) {
        boolean bool1 = true;
        if (!paramBoolean && hasTransparentPixels(bufferedImage)) {
          bool1 = false;
          if (isBitmaskTransparency(bufferedImage))
            if (paramColor == null) {
              if (drawBitmaskImage(bufferedImage, paramAffineTransform, paramColor, paramInt1, paramInt2, paramInt3, paramInt4))
                return true; 
            } else if (paramColor.getTransparency() == 1) {
              bool1 = true;
            }  
          if (!canDoRedraws())
            bool1 = true; 
        } else {
          paramColor = null;
        } 
        if ((paramInt1 + paramInt3 > bufferedImage.getWidth(null) || paramInt2 + paramInt4 > bufferedImage.getHeight(null)) && canDoRedraws())
          bool1 = false; 
        if (!bool1) {
          affineTransform.getMatrix(arrayOfDouble);
          AffineTransform affineTransform3 = new AffineTransform(arrayOfDouble[0] / d5, arrayOfDouble[1] / d6, arrayOfDouble[2] / d5, arrayOfDouble[3] / d6, arrayOfDouble[4] / d5, arrayOfDouble[5] / d6);
          Rectangle2D.Float float5 = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
          Shape shape4 = affineTransform.createTransformedShape(float5);
          Rectangle2D rectangle2D1 = shape4.getBounds2D();
          rectangle2D1.setRect(rectangle2D1.getX(), rectangle2D1.getY(), rectangle2D1.getWidth() + 0.001D, rectangle2D1.getHeight() + 0.001D);
          int m = (int)rectangle2D1.getWidth();
          int n = (int)rectangle2D1.getHeight();
          int i1 = m * n * 3;
          int i2 = 8388608;
          double d7 = (d3 < d4) ? d3 : d4;
          int i3 = (int)d7;
          double d8 = 1.0D;
          double d9 = m / j;
          double d10 = n / k;
          double d11 = (d9 > d10) ? d10 : d9;
          int i4 = (int)(i3 / d11);
          if (i4 < 72)
            i4 = 72; 
          while (i1 > i2 && i3 > i4) {
            d8 *= 2.0D;
            i3 /= 2;
            i1 /= 4;
          } 
          if (i3 < i4)
            d8 = d7 / i4; 
          rectangle2D1.setRect(rectangle2D1.getX() / d8, rectangle2D1.getY() / d8, rectangle2D1.getWidth() / d8, rectangle2D1.getHeight() / d8);
          pSPrinterJob.saveState(getTransform(), getClip(), rectangle2D1, d8, d8);
          return true;
        } 
        BufferedImage bufferedImage1 = new BufferedImage((int)rectangle2D.getWidth(), (int)rectangle2D.getHeight(), 5);
        Graphics2D graphics2D = bufferedImage1.createGraphics();
        graphics2D.clipRect(0, 0, bufferedImage1.getWidth(), bufferedImage1.getHeight());
        graphics2D.translate(-rectangle2D.getX(), -rectangle2D.getY());
        graphics2D.transform(affineTransform1);
        if (paramColor == null)
          paramColor = Color.white; 
        graphics2D.drawImage(bufferedImage, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramColor, null);
        Shape shape1 = getClip();
        Shape shape2 = getTransform().createTransformedShape(shape1);
        AffineTransform affineTransform2 = AffineTransform.getScaleInstance(d1, d2);
        Shape shape3 = affineTransform2.createTransformedShape(shape);
        Area area1 = new Area(shape3);
        Area area2 = new Area(shape2);
        area1.intersect(area2);
        pSPrinterJob.setClip(area1);
        Rectangle2D.Float float4 = new Rectangle2D.Float((float)(rectangle2D.getX() * d1), (float)(rectangle2D.getY() * d2), (float)(rectangle2D.getWidth() * d1), (float)(rectangle2D.getHeight() * d2));
        ByteComponentRaster byteComponentRaster = (ByteComponentRaster)bufferedImage1.getRaster();
        pSPrinterJob.drawImageBGR(byteComponentRaster.getDataStorage(), float4.x, float4.y, (float)Math.rint(float4.width + 0.5D), (float)Math.rint(float4.height + 0.5D), 0.0F, 0.0F, bufferedImage1.getWidth(), bufferedImage1.getHeight(), bufferedImage1.getWidth(), bufferedImage1.getHeight());
        pSPrinterJob.setClip(getTransform().createTransformedShape(shape1));
        graphics2D.dispose();
      } 
    } 
    return true;
  }
  
  public void redrawRegion(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, Shape paramShape, AffineTransform paramAffineTransform) throws PrinterException {
    PSPrinterJob pSPrinterJob = (PSPrinterJob)getPrinterJob();
    Printable printable = getPrintable();
    PageFormat pageFormat = getPageFormat();
    int i = getPageIndex();
    BufferedImage bufferedImage = new BufferedImage((int)paramRectangle2D.getWidth(), (int)paramRectangle2D.getHeight(), 5);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    ProxyGraphics2D proxyGraphics2D = new ProxyGraphics2D(graphics2D, pSPrinterJob);
    proxyGraphics2D.setColor(Color.white);
    proxyGraphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    proxyGraphics2D.clipRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    proxyGraphics2D.translate(-paramRectangle2D.getX(), -paramRectangle2D.getY());
    float f1 = (float)(pSPrinterJob.getXRes() / paramDouble1);
    float f2 = (float)(pSPrinterJob.getYRes() / paramDouble2);
    proxyGraphics2D.scale((f1 / 72.0F), (f2 / 72.0F));
    proxyGraphics2D.translate(-pSPrinterJob.getPhysicalPrintableX(pageFormat.getPaper()) / pSPrinterJob.getXRes() * 72.0D, -pSPrinterJob.getPhysicalPrintableY(pageFormat.getPaper()) / pSPrinterJob.getYRes() * 72.0D);
    proxyGraphics2D.transform(new AffineTransform(getPageFormat().getMatrix()));
    proxyGraphics2D.setPaint(Color.black);
    printable.print(proxyGraphics2D, pageFormat, i);
    graphics2D.dispose();
    pSPrinterJob.setClip(paramAffineTransform.createTransformedShape(paramShape));
    Rectangle2D.Float float = new Rectangle2D.Float((float)(paramRectangle2D.getX() * paramDouble1), (float)(paramRectangle2D.getY() * paramDouble2), (float)(paramRectangle2D.getWidth() * paramDouble1), (float)(paramRectangle2D.getHeight() * paramDouble2));
    ByteComponentRaster byteComponentRaster = (ByteComponentRaster)bufferedImage.getRaster();
    pSPrinterJob.drawImageBGR(byteComponentRaster.getDataStorage(), float.x, float.y, float.width, float.height, 0.0F, 0.0F, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getWidth(), bufferedImage.getHeight());
  }
  
  protected void deviceFill(PathIterator paramPathIterator, Color paramColor) {
    PSPrinterJob pSPrinterJob = (PSPrinterJob)getPrinterJob();
    pSPrinterJob.deviceFill(paramPathIterator, paramColor, getTransform(), getClip());
  }
  
  protected void deviceFrameRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) { draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  protected void deviceDrawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) { draw(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  protected void deviceFillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) { fill(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4)); }
  
  protected void deviceClip(PathIterator paramPathIterator) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PSPathGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */