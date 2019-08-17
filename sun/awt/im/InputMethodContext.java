package sun.awt.im;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodContext;
import java.security.AccessController;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import javax.swing.JFrame;
import sun.awt.InputMethodSupport;
import sun.security.action.GetPropertyAction;

public class InputMethodContext extends InputContext implements InputMethodContext {
  private boolean dispatchingCommittedText;
  
  private CompositionAreaHandler compositionAreaHandler;
  
  private Object compositionAreaHandlerLock = new Object();
  
  private static boolean belowTheSpotInputRequested;
  
  private boolean inputMethodSupportsBelowTheSpot;
  
  void setInputMethodSupportsBelowTheSpot(boolean paramBoolean) { this.inputMethodSupportsBelowTheSpot = paramBoolean; }
  
  boolean useBelowTheSpotInput() { return (belowTheSpotInputRequested && this.inputMethodSupportsBelowTheSpot); }
  
  private boolean haveActiveClient() {
    Component component = getClientComponent();
    return (component != null && component.getInputMethodRequests() != null);
  }
  
  public void dispatchInputMethodEvent(int paramInt1, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2) {
    Component component = getClientComponent();
    if (component != null) {
      InputMethodEvent inputMethodEvent = new InputMethodEvent(component, paramInt1, paramAttributedCharacterIterator, paramInt2, paramTextHitInfo1, paramTextHitInfo2);
      if (haveActiveClient() && !useBelowTheSpotInput()) {
        component.dispatchEvent(inputMethodEvent);
      } else {
        getCompositionAreaHandler(true).processInputMethodEvent(inputMethodEvent);
      } 
    } 
  }
  
  void dispatchCommittedText(Component paramComponent, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt) {
    if (paramInt == 0 || paramAttributedCharacterIterator.getEndIndex() <= paramAttributedCharacterIterator.getBeginIndex())
      return; 
    long l = System.currentTimeMillis();
    this.dispatchingCommittedText = true;
    try {
      InputMethodRequests inputMethodRequests = paramComponent.getInputMethodRequests();
      if (inputMethodRequests != null) {
        int i = paramAttributedCharacterIterator.getBeginIndex();
        AttributedCharacterIterator attributedCharacterIterator = (new AttributedString(paramAttributedCharacterIterator, i, i + paramInt)).getIterator();
        InputMethodEvent inputMethodEvent = new InputMethodEvent(paramComponent, 1100, attributedCharacterIterator, paramInt, null, null);
        paramComponent.dispatchEvent(inputMethodEvent);
      } else {
        char c;
        for (c = paramAttributedCharacterIterator.first(); paramInt-- > 0 && c != Character.MAX_VALUE; c = paramAttributedCharacterIterator.next()) {
          KeyEvent keyEvent = new KeyEvent(paramComponent, 400, l, 0, 0, c);
          paramComponent.dispatchEvent(keyEvent);
        } 
      } 
    } finally {
      this.dispatchingCommittedText = false;
    } 
  }
  
  public void dispatchEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof InputMethodEvent) {
      if (((Component)paramAWTEvent.getSource()).getInputMethodRequests() == null || (useBelowTheSpotInput() && !this.dispatchingCommittedText))
        getCompositionAreaHandler(true).processInputMethodEvent((InputMethodEvent)paramAWTEvent); 
    } else if (!this.dispatchingCommittedText) {
      super.dispatchEvent(paramAWTEvent);
    } 
  }
  
  private CompositionAreaHandler getCompositionAreaHandler(boolean paramBoolean) {
    synchronized (this.compositionAreaHandlerLock) {
      if (this.compositionAreaHandler == null)
        this.compositionAreaHandler = new CompositionAreaHandler(this); 
      this.compositionAreaHandler.setClientComponent(getClientComponent());
      if (paramBoolean)
        this.compositionAreaHandler.grabCompositionArea(false); 
      return this.compositionAreaHandler;
    } 
  }
  
  void grabCompositionArea(boolean paramBoolean) {
    synchronized (this.compositionAreaHandlerLock) {
      if (this.compositionAreaHandler != null) {
        this.compositionAreaHandler.grabCompositionArea(paramBoolean);
      } else {
        CompositionAreaHandler.closeCompositionArea();
      } 
    } 
  }
  
  void releaseCompositionArea() {
    synchronized (this.compositionAreaHandlerLock) {
      if (this.compositionAreaHandler != null)
        this.compositionAreaHandler.releaseCompositionArea(); 
    } 
  }
  
  boolean isCompositionAreaVisible() { return (this.compositionAreaHandler != null) ? this.compositionAreaHandler.isCompositionAreaVisible() : 0; }
  
  void setCompositionAreaVisible(boolean paramBoolean) {
    if (this.compositionAreaHandler != null)
      this.compositionAreaHandler.setCompositionAreaVisible(paramBoolean); 
  }
  
  public Rectangle getTextLocation(TextHitInfo paramTextHitInfo) { return getReq().getTextLocation(paramTextHitInfo); }
  
  public TextHitInfo getLocationOffset(int paramInt1, int paramInt2) { return getReq().getLocationOffset(paramInt1, paramInt2); }
  
  public int getInsertPositionOffset() { return getReq().getInsertPositionOffset(); }
  
  public AttributedCharacterIterator getCommittedText(int paramInt1, int paramInt2, AttributedCharacterIterator.Attribute[] paramArrayOfAttribute) { return getReq().getCommittedText(paramInt1, paramInt2, paramArrayOfAttribute); }
  
  public int getCommittedTextLength() { return getReq().getCommittedTextLength(); }
  
  public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute) { return getReq().cancelLatestCommittedText(paramArrayOfAttribute); }
  
  public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute) { return getReq().getSelectedText(paramArrayOfAttribute); }
  
  private InputMethodRequests getReq() { return (haveActiveClient() && !useBelowTheSpotInput()) ? getClientComponent().getInputMethodRequests() : getCompositionAreaHandler(false); }
  
  public Window createInputMethodWindow(String paramString, boolean paramBoolean) {
    InputMethodContext inputMethodContext;
    return (inputMethodContext = paramBoolean ? this : null).createInputMethodWindow(paramString, inputMethodContext, false);
  }
  
  public JFrame createInputMethodJFrame(String paramString, boolean paramBoolean) {
    InputMethodContext inputMethodContext;
    return (JFrame)(inputMethodContext = paramBoolean ? this : null).createInputMethodWindow(paramString, inputMethodContext, true);
  }
  
  static Window createInputMethodWindow(String paramString, InputContext paramInputContext, boolean paramBoolean) {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    if (paramBoolean)
      return new InputMethodJFrame(paramString, paramInputContext); 
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof InputMethodSupport)
      return ((InputMethodSupport)toolkit).createInputMethodWindow(paramString, paramInputContext); 
    throw new InternalError("Input methods must be supported");
  }
  
  public void enableClientWindowNotification(InputMethod paramInputMethod, boolean paramBoolean) { super.enableClientWindowNotification(paramInputMethod, paramBoolean); }
  
  void setCompositionAreaUndecorated(boolean paramBoolean) {
    if (this.compositionAreaHandler != null)
      this.compositionAreaHandler.setCompositionAreaUndecorated(paramBoolean); 
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.im.style", null));
    if (str == null)
      str = Toolkit.getProperty("java.awt.im.style", null); 
    belowTheSpotInputRequested = "below-the-spot".equals(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\InputMethodContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */