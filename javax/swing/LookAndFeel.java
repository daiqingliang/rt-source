package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.text.JTextComponent;
import sun.awt.SunToolkit;
import sun.swing.DefaultLayoutStyle;
import sun.swing.ImageIconUIResource;
import sun.swing.SwingUtilities2;

public abstract class LookAndFeel {
  public static void installColors(JComponent paramJComponent, String paramString1, String paramString2) {
    Color color1 = paramJComponent.getBackground();
    if (color1 == null || color1 instanceof javax.swing.plaf.UIResource)
      paramJComponent.setBackground(UIManager.getColor(paramString1)); 
    Color color2 = paramJComponent.getForeground();
    if (color2 == null || color2 instanceof javax.swing.plaf.UIResource)
      paramJComponent.setForeground(UIManager.getColor(paramString2)); 
  }
  
  public static void installColorsAndFont(JComponent paramJComponent, String paramString1, String paramString2, String paramString3) {
    Font font = paramJComponent.getFont();
    if (font == null || font instanceof javax.swing.plaf.UIResource)
      paramJComponent.setFont(UIManager.getFont(paramString3)); 
    installColors(paramJComponent, paramString1, paramString2);
  }
  
  public static void installBorder(JComponent paramJComponent, String paramString) {
    Border border = paramJComponent.getBorder();
    if (border == null || border instanceof javax.swing.plaf.UIResource)
      paramJComponent.setBorder(UIManager.getBorder(paramString)); 
  }
  
  public static void uninstallBorder(JComponent paramJComponent) {
    if (paramJComponent.getBorder() instanceof javax.swing.plaf.UIResource)
      paramJComponent.setBorder(null); 
  }
  
  public static void installProperty(JComponent paramJComponent, String paramString, Object paramObject) {
    if (SunToolkit.isInstanceOf(paramJComponent, "javax.swing.JPasswordField")) {
      if (!((JPasswordField)paramJComponent).customSetUIProperty(paramString, paramObject))
        paramJComponent.setUIProperty(paramString, paramObject); 
    } else {
      paramJComponent.setUIProperty(paramString, paramObject);
    } 
  }
  
  public static JTextComponent.KeyBinding[] makeKeyBindings(Object[] paramArrayOfObject) {
    JTextComponent.KeyBinding[] arrayOfKeyBinding = new JTextComponent.KeyBinding[paramArrayOfObject.length / 2];
    for (byte b = 0; b < arrayOfKeyBinding.length; b++) {
      Object object = paramArrayOfObject[2 * b];
      KeyStroke keyStroke = (object instanceof KeyStroke) ? (KeyStroke)object : KeyStroke.getKeyStroke((String)object);
      String str = (String)paramArrayOfObject[2 * b + 1];
      arrayOfKeyBinding[b] = new JTextComponent.KeyBinding(keyStroke, str);
    } 
    return arrayOfKeyBinding;
  }
  
  public static InputMap makeInputMap(Object[] paramArrayOfObject) {
    InputMapUIResource inputMapUIResource = new InputMapUIResource();
    loadKeyBindings(inputMapUIResource, paramArrayOfObject);
    return inputMapUIResource;
  }
  
  public static ComponentInputMap makeComponentInputMap(JComponent paramJComponent, Object[] paramArrayOfObject) {
    ComponentInputMapUIResource componentInputMapUIResource = new ComponentInputMapUIResource(paramJComponent);
    loadKeyBindings(componentInputMapUIResource, paramArrayOfObject);
    return componentInputMapUIResource;
  }
  
  public static void loadKeyBindings(InputMap paramInputMap, Object[] paramArrayOfObject) {
    if (paramArrayOfObject != null) {
      byte b = 0;
      int i = paramArrayOfObject.length;
      while (b < i) {
        Object object = paramArrayOfObject[b++];
        KeyStroke keyStroke = (object instanceof KeyStroke) ? (KeyStroke)object : KeyStroke.getKeyStroke((String)object);
        paramInputMap.put(keyStroke, paramArrayOfObject[b]);
        b++;
      } 
    } 
  }
  
  public static Object makeIcon(Class<?> paramClass, String paramString) { return SwingUtilities2.makeIcon(paramClass, paramClass, paramString); }
  
  public LayoutStyle getLayoutStyle() { return DefaultLayoutStyle.getInstance(); }
  
  public void provideErrorFeedback(Component paramComponent) {
    Toolkit toolkit = null;
    if (paramComponent != null) {
      toolkit = paramComponent.getToolkit();
    } else {
      toolkit = Toolkit.getDefaultToolkit();
    } 
    toolkit.beep();
  }
  
  public static Object getDesktopPropertyValue(String paramString, Object paramObject) {
    Object object = Toolkit.getDefaultToolkit().getDesktopProperty(paramString);
    return (object == null) ? paramObject : ((object instanceof Color) ? new ColorUIResource((Color)object) : ((object instanceof Font) ? new FontUIResource((Font)object) : object));
  }
  
  public Icon getDisabledIcon(JComponent paramJComponent, Icon paramIcon) { return (paramIcon instanceof ImageIcon) ? new ImageIconUIResource(GrayFilter.createDisabledImage(((ImageIcon)paramIcon).getImage())) : null; }
  
  public Icon getDisabledSelectedIcon(JComponent paramJComponent, Icon paramIcon) { return getDisabledIcon(paramJComponent, paramIcon); }
  
  public abstract String getName();
  
  public abstract String getID();
  
  public abstract String getDescription();
  
  public boolean getSupportsWindowDecorations() { return false; }
  
  public abstract boolean isNativeLookAndFeel();
  
  public abstract boolean isSupportedLookAndFeel();
  
  public void initialize() {}
  
  public void uninitialize() {}
  
  public UIDefaults getDefaults() { return null; }
  
  public String toString() { return "[" + getDescription() + " - " + getClass().getName() + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\LookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */