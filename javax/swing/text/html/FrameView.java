package javax.swing.text.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.swing.text.html.FrameEditorPaneTag;

class FrameView extends ComponentView implements HyperlinkListener {
  JEditorPane htmlPane;
  
  JScrollPane scroller;
  
  boolean editable;
  
  float width;
  
  float height;
  
  URL src;
  
  private boolean createdComponent;
  
  public FrameView(Element paramElement) { super(paramElement); }
  
  protected Component createComponent() {
    Element element = getElement();
    AttributeSet attributeSet = element.getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.SRC);
    if (str != null && !str.equals(""))
      try {
        URL uRL = ((HTMLDocument)element.getDocument()).getBase();
        this.src = new URL(uRL, str);
        this.htmlPane = new FrameEditorPane();
        this.htmlPane.addHyperlinkListener(this);
        JEditorPane jEditorPane = getHostPane();
        boolean bool = true;
        if (jEditorPane != null) {
          this.htmlPane.setEditable(jEditorPane.isEditable());
          String str1 = (String)jEditorPane.getClientProperty("charset");
          if (str1 != null)
            this.htmlPane.putClientProperty("charset", str1); 
          HTMLEditorKit hTMLEditorKit1 = (HTMLEditorKit)jEditorPane.getEditorKit();
          if (hTMLEditorKit1 != null)
            bool = hTMLEditorKit1.isAutoFormSubmission(); 
        } 
        this.htmlPane.setPage(this.src);
        HTMLEditorKit hTMLEditorKit = (HTMLEditorKit)this.htmlPane.getEditorKit();
        if (hTMLEditorKit != null)
          hTMLEditorKit.setAutoFormSubmission(bool); 
        Document document = this.htmlPane.getDocument();
        if (document instanceof HTMLDocument)
          ((HTMLDocument)document).setFrameDocumentState(true); 
        setMargin();
        createScrollPane();
        setBorder();
      } catch (MalformedURLException malformedURLException) {
        malformedURLException.printStackTrace();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      }  
    this.createdComponent = true;
    return this.scroller;
  }
  
  JEditorPane getHostPane() {
    Container container;
    for (container = getContainer(); container != null && !(container instanceof JEditorPane); container = container.getParent());
    return (JEditorPane)container;
  }
  
  public void setParent(View paramView) {
    if (paramView != null) {
      JTextComponent jTextComponent = (JTextComponent)paramView.getContainer();
      this.editable = jTextComponent.isEditable();
    } 
    super.setParent(paramView);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Container container = getContainer();
    if (container != null && this.htmlPane != null && this.htmlPane.isEditable() != ((JTextComponent)container).isEditable()) {
      this.editable = ((JTextComponent)container).isEditable();
      this.htmlPane.setEditable(this.editable);
    } 
    super.paint(paramGraphics, paramShape);
  }
  
  private void setMargin() {
    Insets insets2;
    int i = 0;
    Insets insets1 = this.htmlPane.getMargin();
    boolean bool = false;
    AttributeSet attributeSet = getElement().getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.MARGINWIDTH);
    if (insets1 != null) {
      insets2 = new Insets(insets1.top, insets1.left, insets1.right, insets1.bottom);
    } else {
      insets2 = new Insets(0, 0, 0, 0);
    } 
    if (str != null) {
      i = Integer.parseInt(str);
      if (i > 0) {
        insets2.left = i;
        insets2.right = i;
        bool = true;
      } 
    } 
    str = (String)attributeSet.getAttribute(HTML.Attribute.MARGINHEIGHT);
    if (str != null) {
      i = Integer.parseInt(str);
      if (i > 0) {
        insets2.top = i;
        insets2.bottom = i;
        bool = true;
      } 
    } 
    if (bool)
      this.htmlPane.setMargin(insets2); 
  }
  
  private void setBorder() {
    AttributeSet attributeSet = getElement().getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.FRAMEBORDER);
    if (str != null && (str.equals("no") || str.equals("0")))
      this.scroller.setBorder(null); 
  }
  
  private void createScrollPane() {
    AttributeSet attributeSet = getElement().getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.SCROLLING);
    if (str == null)
      str = "auto"; 
    if (!str.equals("no")) {
      if (str.equals("yes")) {
        this.scroller = new JScrollPane(22, 32);
      } else {
        this.scroller = new JScrollPane();
      } 
    } else {
      this.scroller = new JScrollPane(21, 31);
    } 
    JViewport jViewport = this.scroller.getViewport();
    jViewport.add(this.htmlPane);
    jViewport.setBackingStoreEnabled(true);
    this.scroller.setMinimumSize(new Dimension(5, 5));
    this.scroller.setMaximumSize(new Dimension(2147483647, 2147483647));
  }
  
  JEditorPane getOutermostJEditorPane() {
    View view = getParent();
    FrameSetView frameSetView = null;
    while (view != null) {
      if (view instanceof FrameSetView)
        frameSetView = (FrameSetView)view; 
      view = view.getParent();
    } 
    return (frameSetView != null) ? (JEditorPane)frameSetView.getContainer() : null;
  }
  
  private boolean inNestedFrameSet() {
    FrameSetView frameSetView = (FrameSetView)getParent();
    return frameSetView.getParent() instanceof FrameSetView;
  }
  
  public void hyperlinkUpdate(HyperlinkEvent paramHyperlinkEvent) {
    JEditorPane jEditorPane = getOutermostJEditorPane();
    if (jEditorPane == null)
      return; 
    if (!(paramHyperlinkEvent instanceof HTMLFrameHyperlinkEvent)) {
      jEditorPane.fireHyperlinkUpdate(paramHyperlinkEvent);
      return;
    } 
    HTMLFrameHyperlinkEvent hTMLFrameHyperlinkEvent = (HTMLFrameHyperlinkEvent)paramHyperlinkEvent;
    if (hTMLFrameHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      String str1 = hTMLFrameHyperlinkEvent.getTarget();
      String str2 = str1;
      if (str1.equals("_parent") && !inNestedFrameSet())
        str1 = "_top"; 
      if (paramHyperlinkEvent instanceof FormSubmitEvent) {
        HTMLEditorKit hTMLEditorKit = (HTMLEditorKit)jEditorPane.getEditorKit();
        if (hTMLEditorKit != null && hTMLEditorKit.isAutoFormSubmission()) {
          if (str1.equals("_top")) {
            try {
              movePostData(jEditorPane, str2);
              jEditorPane.setPage(hTMLFrameHyperlinkEvent.getURL());
            } catch (IOException iOException) {}
          } else {
            HTMLDocument hTMLDocument = (HTMLDocument)jEditorPane.getDocument();
            hTMLDocument.processHTMLFrameHyperlinkEvent(hTMLFrameHyperlinkEvent);
          } 
        } else {
          jEditorPane.fireHyperlinkUpdate(paramHyperlinkEvent);
        } 
        return;
      } 
      if (str1.equals("_top"))
        try {
          jEditorPane.setPage(hTMLFrameHyperlinkEvent.getURL());
        } catch (IOException iOException) {} 
      if (!jEditorPane.isEditable())
        jEditorPane.fireHyperlinkUpdate(new HTMLFrameHyperlinkEvent(jEditorPane, hTMLFrameHyperlinkEvent.getEventType(), hTMLFrameHyperlinkEvent.getURL(), hTMLFrameHyperlinkEvent.getDescription(), getElement(), hTMLFrameHyperlinkEvent.getInputEvent(), str1)); 
    } 
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    Element element = getElement();
    AttributeSet attributeSet = element.getAttributes();
    URL uRL1 = this.src;
    String str = (String)attributeSet.getAttribute(HTML.Attribute.SRC);
    URL uRL2 = ((HTMLDocument)element.getDocument()).getBase();
    try {
      if (!this.createdComponent)
        return; 
      Object object = movePostData(this.htmlPane, null);
      this.src = new URL(uRL2, str);
      if (uRL1.equals(this.src) && this.src.getRef() == null && object == null)
        return; 
      this.htmlPane.setPage(this.src);
      Document document = this.htmlPane.getDocument();
      if (document instanceof HTMLDocument)
        ((HTMLDocument)document).setFrameDocumentState(true); 
    } catch (MalformedURLException malformedURLException) {
    
    } catch (IOException iOException) {}
  }
  
  private Object movePostData(JEditorPane paramJEditorPane, String paramString) {
    Object object = null;
    JEditorPane jEditorPane = getOutermostJEditorPane();
    if (jEditorPane != null) {
      if (paramString == null)
        paramString = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME); 
      if (paramString != null) {
        String str = "javax.swing.JEditorPane.postdata." + paramString;
        Document document = jEditorPane.getDocument();
        object = document.getProperty(str);
        if (object != null) {
          paramJEditorPane.getDocument().putProperty("javax.swing.JEditorPane.postdata", object);
          document.putProperty(str, null);
        } 
      } 
    } 
    return object;
  }
  
  public float getMinimumSpan(int paramInt) { return 5.0F; }
  
  public float getMaximumSpan(int paramInt) { return 2.14748365E9F; }
  
  class FrameEditorPane extends JEditorPane implements FrameEditorPaneTag {
    public EditorKit getEditorKitForContentType(String param1String) {
      EditorKit editorKit = super.getEditorKitForContentType(param1String);
      JEditorPane jEditorPane = null;
      if ((jEditorPane = FrameView.this.getOutermostJEditorPane()) != null) {
        EditorKit editorKit1 = jEditorPane.getEditorKitForContentType(param1String);
        if (!editorKit.getClass().equals(editorKit1.getClass())) {
          editorKit = (EditorKit)editorKit1.clone();
          setEditorKitForContentType(param1String, editorKit);
        } 
      } 
      return editorKit;
    }
    
    FrameView getFrameView() { return FrameView.this; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\FrameView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */