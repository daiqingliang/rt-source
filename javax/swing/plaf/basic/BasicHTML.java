package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.StringReader;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;
import sun.swing.SwingUtilities2;

public class BasicHTML {
  private static final String htmlDisable = "html.disable";
  
  public static final String propertyKey = "html";
  
  public static final String documentBaseKey = "html.base";
  
  private static BasicEditorKit basicHTMLFactory;
  
  private static ViewFactory basicHTMLViewFactory;
  
  private static final String styleChanges = "p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }";
  
  public static View createHTMLView(JComponent paramJComponent, String paramString) {
    BasicEditorKit basicEditorKit = getFactory();
    Document document = basicEditorKit.createDefaultDocument(paramJComponent.getFont(), paramJComponent.getForeground());
    Object object = paramJComponent.getClientProperty("html.base");
    if (object instanceof URL)
      ((HTMLDocument)document).setBase((URL)object); 
    StringReader stringReader = new StringReader(paramString);
    try {
      basicEditorKit.read(stringReader, document, 0);
    } catch (Throwable throwable) {}
    ViewFactory viewFactory = basicEditorKit.getViewFactory();
    View view = viewFactory.create(document.getDefaultRootElement());
    return new Renderer(paramJComponent, viewFactory, view);
  }
  
  public static int getHTMLBaseline(View paramView, int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    return (paramView instanceof Renderer) ? getBaseline(paramView.getView(0), paramInt1, paramInt2) : -1;
  }
  
  static int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null) {
      int i = getHTMLBaseline(view, paramInt3, paramInt4);
      return (i < 0) ? i : (paramInt1 + i);
    } 
    return paramInt1 + paramInt2;
  }
  
  static int getBaseline(View paramView, int paramInt1, int paramInt2) {
    if (hasParagraph(paramView)) {
      paramView.setSize(paramInt1, paramInt2);
      return getBaseline(paramView, new Rectangle(0, 0, paramInt1, paramInt2));
    } 
    return -1;
  }
  
  private static int getBaseline(View paramView, Shape paramShape) {
    if (paramView.getViewCount() == 0)
      return -1; 
    AttributeSet attributeSet = paramView.getElement().getAttributes();
    Object object = null;
    if (attributeSet != null)
      object = attributeSet.getAttribute(StyleConstants.NameAttribute); 
    byte b = 0;
    if (object == HTML.Tag.HTML && paramView.getViewCount() > 1)
      b++; 
    paramShape = paramView.getChildAllocation(b, paramShape);
    if (paramShape == null)
      return -1; 
    View view = paramView.getView(b);
    if (paramView instanceof javax.swing.text.ParagraphView) {
      Rectangle rectangle;
      if (paramShape instanceof Rectangle) {
        rectangle = (Rectangle)paramShape;
      } else {
        rectangle = paramShape.getBounds();
      } 
      return rectangle.y + (int)(rectangle.height * view.getAlignment(1));
    } 
    return getBaseline(view, paramShape);
  }
  
  private static boolean hasParagraph(View paramView) {
    if (paramView instanceof javax.swing.text.ParagraphView)
      return true; 
    if (paramView.getViewCount() == 0)
      return false; 
    AttributeSet attributeSet = paramView.getElement().getAttributes();
    Object object = null;
    if (attributeSet != null)
      object = attributeSet.getAttribute(StyleConstants.NameAttribute); 
    byte b = 0;
    if (object == HTML.Tag.HTML && paramView.getViewCount() > 1)
      b = 1; 
    return hasParagraph(paramView.getView(b));
  }
  
  public static boolean isHTMLString(String paramString) {
    if (paramString != null && paramString.length() >= 6 && paramString.charAt(0) == '<' && paramString.charAt(5) == '>') {
      String str = paramString.substring(1, 5);
      return str.equalsIgnoreCase("html");
    } 
    return false;
  }
  
  public static void updateRenderer(JComponent paramJComponent, String paramString) {
    View view1 = null;
    View view2 = (View)paramJComponent.getClientProperty("html");
    Boolean bool = (Boolean)paramJComponent.getClientProperty("html.disable");
    if (bool != Boolean.TRUE && isHTMLString(paramString))
      view1 = createHTMLView(paramJComponent, paramString); 
    if (view1 != view2 && view2 != null)
      for (byte b = 0; b < view2.getViewCount(); b++)
        view2.getView(b).setParent(null);  
    paramJComponent.putClientProperty("html", view1);
  }
  
  static BasicEditorKit getFactory() {
    if (basicHTMLFactory == null) {
      basicHTMLViewFactory = new BasicHTMLViewFactory();
      basicHTMLFactory = new BasicEditorKit();
    } 
    return basicHTMLFactory;
  }
  
  static class BasicDocument extends HTMLDocument {
    BasicDocument(StyleSheet param1StyleSheet, Font param1Font, Color param1Color) {
      super(param1StyleSheet);
      setPreservesUnknownTags(false);
      setFontAndColor(param1Font, param1Color);
    }
    
    private void setFontAndColor(Font param1Font, Color param1Color) { getStyleSheet().addRule(SwingUtilities2.displayPropertiesToCSS(param1Font, param1Color)); }
  }
  
  static class BasicEditorKit extends HTMLEditorKit {
    private static StyleSheet defaultStyles;
    
    public StyleSheet getStyleSheet() {
      if (defaultStyles == null) {
        defaultStyles = new StyleSheet();
        StringReader stringReader = new StringReader("p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }");
        try {
          defaultStyles.loadRules(stringReader, null);
        } catch (Throwable throwable) {}
        stringReader.close();
        defaultStyles.addStyleSheet(super.getStyleSheet());
      } 
      return defaultStyles;
    }
    
    public Document createDefaultDocument(Font param1Font, Color param1Color) {
      StyleSheet styleSheet1 = getStyleSheet();
      StyleSheet styleSheet2 = new StyleSheet();
      styleSheet2.addStyleSheet(styleSheet1);
      BasicHTML.BasicDocument basicDocument = new BasicHTML.BasicDocument(styleSheet2, param1Font, param1Color);
      basicDocument.setAsynchronousLoadPriority(2147483647);
      basicDocument.setPreservesUnknownTags(false);
      return basicDocument;
    }
    
    public ViewFactory getViewFactory() { return basicHTMLViewFactory; }
  }
  
  static class BasicHTMLViewFactory extends HTMLEditorKit.HTMLFactory {
    public View create(Element param1Element) {
      View view = super.create(param1Element);
      if (view instanceof ImageView)
        ((ImageView)view).setLoadsSynchronously(true); 
      return view;
    }
  }
  
  static class Renderer extends View {
    private int width;
    
    private View view;
    
    private ViewFactory factory;
    
    private JComponent host;
    
    Renderer(JComponent param1JComponent, ViewFactory param1ViewFactory, View param1View) {
      super(null);
      this.host = param1JComponent;
      this.factory = param1ViewFactory;
      this.view = param1View;
      this.view.setParent(this);
      setSize(this.view.getPreferredSpan(0), this.view.getPreferredSpan(1));
    }
    
    public AttributeSet getAttributes() { return null; }
    
    public float getPreferredSpan(int param1Int) { return (param1Int == 0) ? this.width : this.view.getPreferredSpan(param1Int); }
    
    public float getMinimumSpan(int param1Int) { return this.view.getMinimumSpan(param1Int); }
    
    public float getMaximumSpan(int param1Int) { return 2.14748365E9F; }
    
    public void preferenceChanged(View param1View, boolean param1Boolean1, boolean param1Boolean2) {
      this.host.revalidate();
      this.host.repaint();
    }
    
    public float getAlignment(int param1Int) { return this.view.getAlignment(param1Int); }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {
      Rectangle rectangle = param1Shape.getBounds();
      this.view.setSize(rectangle.width, rectangle.height);
      this.view.paint(param1Graphics, param1Shape);
    }
    
    public void setParent(View param1View) { throw new Error("Can't set parent on root view"); }
    
    public int getViewCount() { return 1; }
    
    public View getView(int param1Int) { return this.view; }
    
    public Shape modelToView(int param1Int, Shape param1Shape, Position.Bias param1Bias) throws BadLocationException { return this.view.modelToView(param1Int, param1Shape, param1Bias); }
    
    public Shape modelToView(int param1Int1, Position.Bias param1Bias1, int param1Int2, Position.Bias param1Bias2, Shape param1Shape) throws BadLocationException { return this.view.modelToView(param1Int1, param1Bias1, param1Int2, param1Bias2, param1Shape); }
    
    public int viewToModel(float param1Float1, float param1Float2, Shape param1Shape, Position.Bias[] param1ArrayOfBias) { return this.view.viewToModel(param1Float1, param1Float2, param1Shape, param1ArrayOfBias); }
    
    public Document getDocument() { return this.view.getDocument(); }
    
    public int getStartOffset() { return this.view.getStartOffset(); }
    
    public int getEndOffset() { return this.view.getEndOffset(); }
    
    public Element getElement() { return this.view.getElement(); }
    
    public void setSize(float param1Float1, float param1Float2) {
      this.width = (int)param1Float1;
      this.view.setSize(param1Float1, param1Float2);
    }
    
    public Container getContainer() { return this.host; }
    
    public ViewFactory getViewFactory() { return this.factory; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */