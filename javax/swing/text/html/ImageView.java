package javax.swing.text.html;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class ImageView extends View {
  private static boolean sIsInc = false;
  
  private static int sIncRate = 100;
  
  private static final String PENDING_IMAGE = "html.pendingImage";
  
  private static final String MISSING_IMAGE = "html.missingImage";
  
  private static final String IMAGE_CACHE_PROPERTY = "imageCache";
  
  private static final int DEFAULT_WIDTH = 38;
  
  private static final int DEFAULT_HEIGHT = 38;
  
  private static final int DEFAULT_BORDER = 2;
  
  private static final int LOADING_FLAG = 1;
  
  private static final int LINK_FLAG = 2;
  
  private static final int WIDTH_FLAG = 4;
  
  private static final int HEIGHT_FLAG = 8;
  
  private static final int RELOAD_FLAG = 16;
  
  private static final int RELOAD_IMAGE_FLAG = 32;
  
  private static final int SYNC_LOAD_FLAG = 64;
  
  private AttributeSet attr;
  
  private Image image;
  
  private Image disabledImage;
  
  private int width;
  
  private int height;
  
  private int state = 48;
  
  private Container container;
  
  private Rectangle fBounds = new Rectangle();
  
  private Color borderColor;
  
  private short borderSize;
  
  private short leftInset;
  
  private short rightInset;
  
  private short topInset;
  
  private short bottomInset;
  
  private ImageObserver imageObserver = new ImageHandler(null);
  
  private View altView;
  
  private float vAlign;
  
  public ImageView(Element paramElement) { super(paramElement); }
  
  public String getAltText() { return (String)getElement().getAttributes().getAttribute(HTML.Attribute.ALT); }
  
  public URL getImageURL() {
    String str = (String)getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
    if (str == null)
      return null; 
    URL uRL = ((HTMLDocument)getDocument()).getBase();
    try {
      return new URL(uRL, str);
    } catch (MalformedURLException malformedURLException) {
      return null;
    } 
  }
  
  public Icon getNoImageIcon() { return (Icon)UIManager.getLookAndFeelDefaults().get("html.missingImage"); }
  
  public Icon getLoadingImageIcon() { return (Icon)UIManager.getLookAndFeelDefaults().get("html.pendingImage"); }
  
  public Image getImage() {
    sync();
    return this.image;
  }
  
  private Image getImage(boolean paramBoolean) {
    Image image1 = getImage();
    if (!paramBoolean) {
      if (this.disabledImage == null)
        this.disabledImage = GrayFilter.createDisabledImage(image1); 
      image1 = this.disabledImage;
    } 
    return image1;
  }
  
  public void setLoadsSynchronously(boolean paramBoolean) {
    synchronized (this) {
      if (paramBoolean) {
        this.state |= 0x40;
      } else {
        this.state = (this.state | 0x40) ^ 0x40;
      } 
    } 
  }
  
  public boolean getLoadsSynchronously() { return ((this.state & 0x40) != 0); }
  
  protected StyleSheet getStyleSheet() {
    HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
    return hTMLDocument.getStyleSheet();
  }
  
  public AttributeSet getAttributes() {
    sync();
    return this.attr;
  }
  
  public String getToolTipText(float paramFloat1, float paramFloat2, Shape paramShape) { return getAltText(); }
  
  protected void setPropertiesFromAttributes() {
    StyleSheet styleSheet = getStyleSheet();
    this.attr = styleSheet.getViewAttributes(this);
    this.borderSize = (short)getIntAttr(HTML.Attribute.BORDER, isLink() ? 2 : 0);
    this.leftInset = this.rightInset = (short)(getIntAttr(HTML.Attribute.HSPACE, 0) + this.borderSize);
    this.topInset = this.bottomInset = (short)(getIntAttr(HTML.Attribute.VSPACE, 0) + this.borderSize);
    this.borderColor = ((StyledDocument)getDocument()).getForeground(getAttributes());
    AttributeSet attributeSet1 = getElement().getAttributes();
    Object object = attributeSet1.getAttribute(HTML.Attribute.ALIGN);
    this.vAlign = 1.0F;
    if (object != null) {
      object = object.toString();
      if ("top".equals(object)) {
        this.vAlign = 0.0F;
      } else if ("middle".equals(object)) {
        this.vAlign = 0.5F;
      } 
    } 
    AttributeSet attributeSet2 = (AttributeSet)attributeSet1.getAttribute(HTML.Tag.A);
    if (attributeSet2 != null && attributeSet2.isDefined(HTML.Attribute.HREF)) {
      synchronized (this) {
        this.state |= 0x2;
      } 
    } else {
      synchronized (this) {
        this.state = (this.state | 0x2) ^ 0x2;
      } 
    } 
  }
  
  public void setParent(View paramView) {
    View view = getParent();
    super.setParent(paramView);
    this.container = (paramView != null) ? getContainer() : null;
    if (view != paramView)
      synchronized (this) {
        this.state |= 0x10;
      }  
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    synchronized (this) {
      this.state |= 0x30;
    } 
    preferenceChanged(null, true, true);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    sync();
    Rectangle rectangle1 = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    Rectangle rectangle2 = paramGraphics.getClipBounds();
    this.fBounds.setBounds(rectangle1);
    paintHighlights(paramGraphics, paramShape);
    paintBorder(paramGraphics, rectangle1);
    if (rectangle2 != null)
      paramGraphics.clipRect(rectangle1.x + this.leftInset, rectangle1.y + this.topInset, rectangle1.width - this.leftInset - this.rightInset, rectangle1.height - this.topInset - this.bottomInset); 
    Container container1 = getContainer();
    Image image1 = getImage((container1 == null || container1.isEnabled()));
    if (image1 != null) {
      if (!hasPixels(image1)) {
        Icon icon = getLoadingImageIcon();
        if (icon != null)
          icon.paintIcon(container1, paramGraphics, rectangle1.x + this.leftInset, rectangle1.y + this.topInset); 
      } else {
        paramGraphics.drawImage(image1, rectangle1.x + this.leftInset, rectangle1.y + this.topInset, this.width, this.height, this.imageObserver);
      } 
    } else {
      Icon icon = getNoImageIcon();
      if (icon != null)
        icon.paintIcon(container1, paramGraphics, rectangle1.x + this.leftInset, rectangle1.y + this.topInset); 
      View view = getAltView();
      if (view != null && ((this.state & 0x4) == 0 || this.width > 38)) {
        Rectangle rectangle = new Rectangle(rectangle1.x + this.leftInset + 38, rectangle1.y + this.topInset, rectangle1.width - this.leftInset - this.rightInset - 38, rectangle1.height - this.topInset - this.bottomInset);
        view.paint(paramGraphics, rectangle);
      } 
    } 
    if (rectangle2 != null)
      paramGraphics.setClip(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height); 
  }
  
  private void paintHighlights(Graphics paramGraphics, Shape paramShape) {
    if (this.container instanceof JTextComponent) {
      JTextComponent jTextComponent = (JTextComponent)this.container;
      Highlighter highlighter = jTextComponent.getHighlighter();
      if (highlighter instanceof LayeredHighlighter)
        ((LayeredHighlighter)highlighter).paintLayeredHighlights(paramGraphics, getStartOffset(), getEndOffset(), paramShape, jTextComponent, this); 
    } 
  }
  
  private void paintBorder(Graphics paramGraphics, Rectangle paramRectangle) {
    Color color = this.borderColor;
    if ((this.borderSize > 0 || this.image == null) && color != null) {
      short s1 = this.leftInset - this.borderSize;
      short s2 = this.topInset - this.borderSize;
      paramGraphics.setColor(color);
      boolean bool = (this.image == null) ? 1 : this.borderSize;
      for (int i = 0; i < bool; i++)
        paramGraphics.drawRect(paramRectangle.x + s1 + i, paramRectangle.y + s2 + i, paramRectangle.width - i - i - s1 - s1 - 1, paramRectangle.height - i - i - s2 - s2 - 1); 
    } 
  }
  
  public float getPreferredSpan(int paramInt) {
    sync();
    if (paramInt == 0 && (this.state & 0x4) == 4) {
      getPreferredSpanFromAltView(paramInt);
      return (this.width + this.leftInset + this.rightInset);
    } 
    if (paramInt == 1 && (this.state & 0x8) == 8) {
      getPreferredSpanFromAltView(paramInt);
      return (this.height + this.topInset + this.bottomInset);
    } 
    Image image1 = getImage();
    if (image1 != null) {
      switch (paramInt) {
        case 0:
          return (this.width + this.leftInset + this.rightInset);
        case 1:
          return (this.height + this.topInset + this.bottomInset);
      } 
      throw new IllegalArgumentException("Invalid axis: " + paramInt);
    } 
    View view = getAltView();
    float f = 0.0F;
    if (view != null)
      f = view.getPreferredSpan(paramInt); 
    switch (paramInt) {
      case 0:
        return f + (this.width + this.leftInset + this.rightInset);
      case 1:
        return f + (this.height + this.topInset + this.bottomInset);
    } 
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt) {
    switch (paramInt) {
      case 1:
        return this.vAlign;
    } 
    return super.getAlignment(paramInt);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    int i = getStartOffset();
    int j = getEndOffset();
    if (paramInt >= i && paramInt <= j) {
      Rectangle rectangle = paramShape.getBounds();
      if (paramInt == j)
        rectangle.x += rectangle.width; 
      rectangle.width = 0;
      return rectangle;
    } 
    return null;
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    Rectangle rectangle = (Rectangle)paramShape;
    if (paramFloat1 < (rectangle.x + rectangle.width)) {
      paramArrayOfBias[0] = Position.Bias.Forward;
      return getStartOffset();
    } 
    paramArrayOfBias[0] = Position.Bias.Backward;
    return getEndOffset();
  }
  
  public void setSize(float paramFloat1, float paramFloat2) {
    sync();
    if (getImage() == null) {
      View view = getAltView();
      if (view != null)
        view.setSize(Math.max(0.0F, paramFloat1 - (38 + this.leftInset + this.rightInset)), Math.max(0.0F, paramFloat2 - (this.topInset + this.bottomInset))); 
    } 
  }
  
  private boolean isLink() { return ((this.state & 0x2) == 2); }
  
  private boolean hasPixels(Image paramImage) { return (paramImage != null && paramImage.getHeight(this.imageObserver) > 0 && paramImage.getWidth(this.imageObserver) > 0); }
  
  private float getPreferredSpanFromAltView(int paramInt) {
    if (getImage() == null) {
      View view = getAltView();
      if (view != null)
        return view.getPreferredSpan(paramInt); 
    } 
    return 0.0F;
  }
  
  private void repaint(long paramLong) {
    if (this.container != null && this.fBounds != null)
      this.container.repaint(paramLong, this.fBounds.x, this.fBounds.y, this.fBounds.width, this.fBounds.height); 
  }
  
  private int getIntAttr(HTML.Attribute paramAttribute, int paramInt) {
    AttributeSet attributeSet = getElement().getAttributes();
    if (attributeSet.isDefined(paramAttribute)) {
      int i;
      String str = (String)attributeSet.getAttribute(paramAttribute);
      if (str == null) {
        i = paramInt;
      } else {
        try {
          i = Math.max(0, Integer.parseInt(str));
        } catch (NumberFormatException numberFormatException) {
          i = paramInt;
        } 
      } 
      return i;
    } 
    return paramInt;
  }
  
  private void sync() {
    int i = this.state;
    if ((i & 0x20) != 0)
      refreshImage(); 
    i = this.state;
    if ((i & 0x10) != 0) {
      synchronized (this) {
        this.state = (this.state | 0x10) ^ 0x10;
      } 
      setPropertiesFromAttributes();
    } 
  }
  
  private void refreshImage() {
    synchronized (this) {
      this.state = (this.state | true | 0x20 | 0x4 | 0x8) ^ 0x2C;
      this.image = null;
      this.width = this.height = 0;
    } 
    try {
      loadImage();
      updateImageSize();
    } finally {
      synchronized (this) {
        this.state = (this.state | true) ^ true;
      } 
    } 
  }
  
  private void loadImage() {
    URL uRL = getImageURL();
    Image image1 = null;
    if (uRL != null) {
      Dictionary dictionary = (Dictionary)getDocument().getProperty("imageCache");
      if (dictionary != null) {
        image1 = (Image)dictionary.get(uRL);
      } else {
        image1 = Toolkit.getDefaultToolkit().createImage(uRL);
        if (image1 != null && getLoadsSynchronously()) {
          ImageIcon imageIcon = new ImageIcon();
          imageIcon.setImage(image1);
        } 
      } 
    } 
    this.image = image1;
  }
  
  private void updateImageSize() {
    int i = 0;
    int j = 0;
    int k = 0;
    Image image1 = getImage();
    if (image1 != null) {
      Element element = getElement();
      AttributeSet attributeSet = element.getAttributes();
      i = getIntAttr(HTML.Attribute.WIDTH, -1);
      if (i > 0)
        k |= 0x4; 
      j = getIntAttr(HTML.Attribute.HEIGHT, -1);
      if (j > 0)
        k |= 0x8; 
      if (i <= 0) {
        i = image1.getWidth(this.imageObserver);
        if (i <= 0)
          i = 38; 
      } 
      if (j <= 0) {
        j = image1.getHeight(this.imageObserver);
        if (j <= 0)
          j = 38; 
      } 
      if ((k & 0xC) != 0) {
        Toolkit.getDefaultToolkit().prepareImage(image1, i, j, this.imageObserver);
      } else {
        Toolkit.getDefaultToolkit().prepareImage(image1, -1, -1, this.imageObserver);
      } 
      boolean bool = false;
      synchronized (this) {
        if (this.image != null) {
          if ((k & 0x4) == 4 || this.width == 0)
            this.width = i; 
          if ((k & 0x8) == 8 || this.height == 0)
            this.height = j; 
        } else {
          bool = true;
          if ((k & 0x4) == 4)
            this.width = i; 
          if ((k & 0x8) == 8)
            this.height = j; 
        } 
        this.state |= k;
        this.state = (this.state | true) ^ true;
      } 
      if (bool)
        updateAltTextView(); 
    } else {
      this.width = this.height = 38;
      updateAltTextView();
    } 
  }
  
  private void updateAltTextView() {
    String str = getAltText();
    if (str != null) {
      ImageLabelView imageLabelView = new ImageLabelView(getElement(), str);
      synchronized (this) {
        this.altView = imageLabelView;
      } 
    } 
  }
  
  private View getAltView() {
    View view;
    synchronized (this) {
      view = this.altView;
    } 
    if (view != null && view.getParent() == null)
      view.setParent(getParent()); 
    return view;
  }
  
  private void safePreferenceChanged() {
    if (SwingUtilities.isEventDispatchThread()) {
      Document document = getDocument();
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readLock(); 
      preferenceChanged(null, true, true);
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } else {
      SwingUtilities.invokeLater(new Runnable() {
            public void run() { ImageView.this.safePreferenceChanged(); }
          });
    } 
  }
  
  private class ImageHandler implements ImageObserver {
    private ImageHandler() {}
    
    public boolean imageUpdate(Image param1Image, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      if ((param1Image != ImageView.this.image && param1Image != ImageView.this.disabledImage) || ImageView.this.image == null || ImageView.this.getParent() == null)
        return false; 
      if ((param1Int1 & 0xC0) != 0) {
        ImageView.this.repaint(0L);
        synchronized (ImageView.this) {
          if (ImageView.this.image == param1Image) {
            ImageView.this.image = null;
            if ((ImageView.this.state & 0x4) != 4)
              ImageView.this.width = 38; 
            if ((ImageView.this.state & 0x8) != 8)
              ImageView.this.height = 38; 
          } else {
            ImageView.this.disabledImage = null;
          } 
          if ((ImageView.this.state & true) == 1)
            return false; 
        } 
        ImageView.this.updateAltTextView();
        ImageView.this.safePreferenceChanged();
        return false;
      } 
      if (ImageView.this.image == param1Image) {
        short s = 0;
        if ((param1Int1 & 0x2) != 0 && !ImageView.this.getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT))
          s = (short)(s | true); 
        if ((param1Int1 & true) != 0 && !ImageView.this.getElement().getAttributes().isDefined(HTML.Attribute.WIDTH))
          s = (short)(s | 0x2); 
        synchronized (ImageView.this) {
          if ((s & true) == 1 && (ImageView.this.state & 0x4) == 0)
            ImageView.this.width = param1Int4; 
          if ((s & 0x2) == 2 && (ImageView.this.state & 0x8) == 0)
            ImageView.this.height = param1Int5; 
          if ((ImageView.this.state & true) == 1)
            return true; 
        } 
        if (s != 0) {
          ImageView.this.safePreferenceChanged();
          return true;
        } 
      } 
      if ((param1Int1 & 0x30) != 0) {
        ImageView.this.repaint(0L);
      } else if ((param1Int1 & 0x8) != 0 && sIsInc) {
        ImageView.this.repaint(sIncRate);
      } 
      return ((param1Int1 & 0x20) == 0);
    }
  }
  
  private class ImageLabelView extends InlineView {
    private Segment segment;
    
    private Color fg;
    
    ImageLabelView(Element param1Element, String param1String) {
      super(param1Element);
      reset(param1String);
    }
    
    public void reset(String param1String) { this.segment = new Segment(param1String.toCharArray(), 0, param1String.length()); }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {
      GlyphView.GlyphPainter glyphPainter = getGlyphPainter();
      if (glyphPainter != null) {
        param1Graphics.setColor(getForeground());
        glyphPainter.paint(this, param1Graphics, param1Shape, getStartOffset(), getEndOffset());
      } 
    }
    
    public Segment getText(int param1Int1, int param1Int2) {
      if (param1Int1 < 0 || param1Int2 > this.segment.array.length)
        throw new RuntimeException("ImageLabelView: Stale view"); 
      this.segment.offset = param1Int1;
      this.segment.count = param1Int2 - param1Int1;
      return this.segment;
    }
    
    public int getStartOffset() { return 0; }
    
    public int getEndOffset() { return this.segment.array.length; }
    
    public View breakView(int param1Int1, int param1Int2, float param1Float1, float param1Float2) { return this; }
    
    public Color getForeground() {
      View view;
      if (this.fg == null && (view = getParent()) != null) {
        Document document = getDocument();
        AttributeSet attributeSet = view.getAttributes();
        if (attributeSet != null && document instanceof StyledDocument)
          this.fg = ((StyledDocument)document).getForeground(attributeSet); 
      } 
      return this.fg;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\ImageView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */