package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;

class SynthPainterImpl extends SynthPainter {
  private NimbusStyle style;
  
  SynthPainterImpl(NimbusStyle paramNimbusStyle) { this.style = paramNimbusStyle; }
  
  private void paint(Painter paramPainter, SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform) {
    if (paramPainter != null)
      if (paramGraphics instanceof Graphics2D) {
        Graphics2D graphics2D = (Graphics2D)paramGraphics;
        if (paramAffineTransform != null)
          graphics2D.transform(paramAffineTransform); 
        graphics2D.translate(paramInt1, paramInt2);
        paramPainter.paint(graphics2D, paramSynthContext.getComponent(), paramInt3, paramInt4);
        graphics2D.translate(-paramInt1, -paramInt2);
        if (paramAffineTransform != null)
          try {
            graphics2D.transform(paramAffineTransform.createInverse());
          } catch (NoninvertibleTransformException noninvertibleTransformException) {
            noninvertibleTransformException.printStackTrace();
          }  
      } else {
        BufferedImage bufferedImage = new BufferedImage(paramInt3, paramInt4, 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        if (paramAffineTransform != null)
          graphics2D.transform(paramAffineTransform); 
        paramPainter.paint(graphics2D, paramSynthContext.getComponent(), paramInt3, paramInt4);
        graphics2D.dispose();
        paramGraphics.drawImage(bufferedImage, paramInt1, paramInt2, null);
        bufferedImage = null;
      }  
  }
  
  private void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform) {
    JComponent jComponent = paramSynthContext.getComponent();
    Color color = (jComponent != null) ? jComponent.getBackground() : null;
    if (color == null || color.getAlpha() > 0) {
      Painter painter = this.style.getBackgroundPainter(paramSynthContext);
      if (painter != null)
        paint(painter, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramAffineTransform); 
    } 
  }
  
  private void paintForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform) {
    Painter painter = this.style.getForegroundPainter(paramSynthContext);
    if (painter != null)
      paint(painter, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramAffineTransform); 
  }
  
  private void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform) {
    Painter painter = this.style.getBorderPainter(paramSynthContext);
    if (painter != null)
      paint(painter, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramAffineTransform); 
  }
  
  private void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    JComponent jComponent = paramSynthContext.getComponent();
    boolean bool = jComponent.getComponentOrientation().isLeftToRight();
    if (paramSynthContext.getComponent() instanceof javax.swing.JSlider)
      bool = true; 
    if (paramInt5 == 1 && bool) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 1) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.rotate(Math.toRadians(90.0D));
      affineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 0 && bool) {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } 
  }
  
  private void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    JComponent jComponent = paramSynthContext.getComponent();
    boolean bool = jComponent.getComponentOrientation().isLeftToRight();
    if (paramInt5 == 1 && bool) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.rotate(Math.toRadians(90.0D));
      paintBorder(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 1) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.rotate(Math.toRadians(90.0D));
      affineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBorder(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 0 && bool) {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } 
  }
  
  private void paintForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    JComponent jComponent = paramSynthContext.getComponent();
    boolean bool = jComponent.getComponentOrientation().isLeftToRight();
    if (paramInt5 == 1 && bool) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.rotate(Math.toRadians(90.0D));
      paintForeground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 1) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.rotate(Math.toRadians(90.0D));
      affineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintForeground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 0 && bool) {
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } 
  }
  
  public void paintArrowButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } 
  }
  
  public void paintArrowButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintArrowButtonForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    String str = paramSynthContext.getComponent().getName();
    boolean bool = paramSynthContext.getComponent().getComponentOrientation().isLeftToRight();
    if ("Spinner.nextButton".equals(str) || "Spinner.previousButton".equals(str)) {
      if (bool) {
        paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
      } else {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate(paramInt3, 0.0D);
        affineTransform.scale(-1.0D, 1.0D);
        paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, affineTransform);
      } 
    } else if (paramInt5 == 7) {
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else if (paramInt5 == 1) {
      if (bool) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(-1.0D, 1.0D);
        affineTransform.rotate(Math.toRadians(90.0D));
        paintForeground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, affineTransform);
      } else {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(90.0D));
        affineTransform.translate(0.0D, -(paramInt1 + paramInt3));
        paintForeground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, affineTransform);
      } 
    } else if (paramInt5 == 3) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt3, 0.0D);
      affineTransform.scale(-1.0D, 1.0D);
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, affineTransform);
    } else if (paramInt5 == 5) {
      if (bool) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90.0D));
        affineTransform.translate(-paramInt4, 0.0D);
        paintForeground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
      } else {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(-1.0D, 1.0D);
        affineTransform.rotate(Math.toRadians(-90.0D));
        affineTransform.translate(-(paramInt4 + paramInt2), -(paramInt3 + paramInt1));
        paintForeground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
      } 
    } 
  }
  
  public void paintButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintCheckBoxMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintCheckBoxMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintCheckBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintCheckBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintColorChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintColorChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintComboBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } 
  }
  
  public void paintComboBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintDesktopIconBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintDesktopIconBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintDesktopPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintDesktopPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintEditorPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintEditorPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintFileChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintFileChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintFormattedTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } 
  }
  
  public void paintFormattedTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.translate(-paramInt3, 0.0D);
      paintBorder(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } 
  }
  
  public void paintInternalFrameTitlePaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintInternalFrameTitlePaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintInternalFrameBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintInternalFrameBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintLabelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintLabelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintListBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintListBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintMenuBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintMenuBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintOptionPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintOptionPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintPanelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintPanelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintPasswordFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintPasswordFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintPopupMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintPopupMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintProgressBarForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintRadioButtonMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintRadioButtonMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintRadioButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintRadioButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintRootPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintRootPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintScrollBarThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintScrollBarThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintScrollPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintScrollPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSeparatorForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSliderThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (paramSynthContext.getComponent().getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE) {
      if (paramInt5 == 0) {
        paramInt5 = 1;
      } else {
        paramInt5 = 0;
      } 
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    } else {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    } 
  }
  
  public void paintSliderThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintSpinnerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSpinnerBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (paramInt5 == 1) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
    } else {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } 
  }
  
  public void paintSplitPaneDividerForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSplitPaneDragDivider(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSplitPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintSplitPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (paramInt5 == 2) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 4) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.rotate(Math.toRadians(90.0D));
      affineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, affineTransform);
    } else if (paramInt5 == 3) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(1.0D, -1.0D);
      affineTransform.translate(0.0D, -paramInt4);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } else {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } 
  }
  
  public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (paramInt6 == 2) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, affineTransform);
    } else if (paramInt6 == 4) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.rotate(Math.toRadians(90.0D));
      affineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, affineTransform);
    } else if (paramInt6 == 3) {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(1.0D, -1.0D);
      affineTransform.translate(0.0D, -paramInt4);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } else {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } 
  }
  
  public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTabbedPaneContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTableHeaderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTableHeaderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTableBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTableBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTextAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTextAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTextPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTextPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } 
  }
  
  public void paintTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight()) {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    } else {
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(-1.0D, 1.0D);
      affineTransform.translate(-paramInt3, 0.0D);
      paintBorder(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, affineTransform);
    } 
  }
  
  public void paintToggleButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToggleButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void paintToolTipBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintToolTipBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTreeBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTreeBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTreeCellBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTreeCellBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintTreeCellFocus(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void paintViewportBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
  
  public void paintViewportBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\SynthPainterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */