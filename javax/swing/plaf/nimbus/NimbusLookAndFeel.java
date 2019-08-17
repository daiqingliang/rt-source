package javax.swing.plaf.nimbus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;
import sun.security.action.GetPropertyAction;
import sun.swing.ImageIconUIResource;
import sun.swing.plaf.GTKKeybindings;
import sun.swing.plaf.WindowsKeybindings;
import sun.swing.plaf.synth.SynthIcon;

public class NimbusLookAndFeel extends SynthLookAndFeel {
  private static final String[] COMPONENT_KEYS = { 
      "ArrowButton", "Button", "CheckBox", "CheckBoxMenuItem", "ColorChooser", "ComboBox", "DesktopPane", "DesktopIcon", "EditorPane", "FileChooser", 
      "FormattedTextField", "InternalFrame", "InternalFrameTitlePane", "Label", "List", "Menu", "MenuBar", "MenuItem", "OptionPane", "Panel", 
      "PasswordField", "PopupMenu", "PopupMenuSeparator", "ProgressBar", "RadioButton", "RadioButtonMenuItem", "RootPane", "ScrollBar", "ScrollBarTrack", "ScrollBarThumb", 
      "ScrollPane", "Separator", "Slider", "SliderTrack", "SliderThumb", "Spinner", "SplitPane", "TabbedPane", "Table", "TableHeader", 
      "TextArea", "TextField", "TextPane", "ToggleButton", "ToolBar", "ToolTip", "Tree", "Viewport" };
  
  private NimbusDefaults defaults = new NimbusDefaults();
  
  private UIDefaults uiDefaults;
  
  private DefaultsListener defaultsListener = new DefaultsListener(null);
  
  private Map<String, Map<String, Object>> compiledDefaults = null;
  
  private boolean defaultListenerAdded = false;
  
  public void initialize() {
    super.initialize();
    this.defaults.initialize();
    setStyleFactory(new SynthStyleFactory() {
          public SynthStyle getStyle(JComponent param1JComponent, Region param1Region) { return NimbusLookAndFeel.this.defaults.getStyle(param1JComponent, param1Region); }
        });
  }
  
  public void uninitialize() {
    super.uninitialize();
    this.defaults.uninitialize();
    ImageCache.getInstance().flush();
    UIManager.getDefaults().removePropertyChangeListener(this.defaultsListener);
  }
  
  public UIDefaults getDefaults() {
    if (this.uiDefaults == null) {
      String str = getSystemProperty("os.name");
      boolean bool = (str != null && str.contains("Windows")) ? 1 : 0;
      this.uiDefaults = super.getDefaults();
      this.defaults.initializeDefaults(this.uiDefaults);
      if (bool) {
        WindowsKeybindings.installKeybindings(this.uiDefaults);
      } else {
        GTKKeybindings.installKeybindings(this.uiDefaults);
      } 
      this.uiDefaults.put("TitledBorder.titlePosition", Integer.valueOf(1));
      this.uiDefaults.put("TitledBorder.border", new BorderUIResource(new LoweredBorder()));
      this.uiDefaults.put("TitledBorder.titleColor", getDerivedColor("text", 0.0F, 0.0F, 0.23F, 0, true));
      this.uiDefaults.put("TitledBorder.font", new NimbusDefaults.DerivedFont("defaultFont", 1.0F, Boolean.valueOf(true), null));
      this.uiDefaults.put("OptionPane.isYesLast", Boolean.valueOf(!bool));
      this.uiDefaults.put("Table.scrollPaneCornerComponent", new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults param1UIDefaults) { return new TableScrollPaneCorner(); }
          });
      this.uiDefaults.put("ToolBarSeparator[Enabled].backgroundPainter", new ToolBarSeparatorPainter());
      for (String str1 : COMPONENT_KEYS) {
        String str2 = str1 + ".foreground";
        if (!this.uiDefaults.containsKey(str2))
          this.uiDefaults.put(str2, new NimbusProperty(str1, "textForeground", null)); 
        str2 = str1 + ".background";
        if (!this.uiDefaults.containsKey(str2))
          this.uiDefaults.put(str2, new NimbusProperty(str1, "background", null)); 
        str2 = str1 + ".font";
        if (!this.uiDefaults.containsKey(str2))
          this.uiDefaults.put(str2, new NimbusProperty(str1, "font", null)); 
        str2 = str1 + ".disabledText";
        if (!this.uiDefaults.containsKey(str2))
          this.uiDefaults.put(str2, new NimbusProperty(str1, "Disabled", "textForeground", null)); 
        str2 = str1 + ".disabled";
        if (!this.uiDefaults.containsKey(str2))
          this.uiDefaults.put(str2, new NimbusProperty(str1, "Disabled", "background", null)); 
      } 
      this.uiDefaults.put("FileView.computerIcon", new LinkProperty("FileChooser.homeFolderIcon", null));
      this.uiDefaults.put("FileView.directoryIcon", new LinkProperty("FileChooser.directoryIcon", null));
      this.uiDefaults.put("FileView.fileIcon", new LinkProperty("FileChooser.fileIcon", null));
      this.uiDefaults.put("FileView.floppyDriveIcon", new LinkProperty("FileChooser.floppyDriveIcon", null));
      this.uiDefaults.put("FileView.hardDriveIcon", new LinkProperty("FileChooser.hardDriveIcon", null));
    } 
    return this.uiDefaults;
  }
  
  public static NimbusStyle getStyle(JComponent paramJComponent, Region paramRegion) { return (NimbusStyle)SynthLookAndFeel.getStyle(paramJComponent, paramRegion); }
  
  public String getName() { return "Nimbus"; }
  
  public String getID() { return "Nimbus"; }
  
  public String getDescription() { return "Nimbus Look and Feel"; }
  
  public boolean shouldUpdateStyleOnAncestorChanged() { return true; }
  
  protected boolean shouldUpdateStyleOnEvent(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if ("name" == str || "ancestor" == str || "Nimbus.Overrides" == str || "Nimbus.Overrides.InheritDefaults" == str || "JComponent.sizeVariant" == str) {
      JComponent jComponent = (JComponent)paramPropertyChangeEvent.getSource();
      this.defaults.clearOverridesCache(jComponent);
      return true;
    } 
    return super.shouldUpdateStyleOnEvent(paramPropertyChangeEvent);
  }
  
  public void register(Region paramRegion, String paramString) { this.defaults.register(paramRegion, paramString); }
  
  private String getSystemProperty(String paramString) { return (String)AccessController.doPrivileged(new GetPropertyAction(paramString)); }
  
  public Icon getDisabledIcon(JComponent paramJComponent, Icon paramIcon) {
    if (paramIcon instanceof SynthIcon) {
      SynthIcon synthIcon = (SynthIcon)paramIcon;
      BufferedImage bufferedImage = EffectUtils.createCompatibleTranslucentImage(synthIcon.getIconWidth(), synthIcon.getIconHeight());
      Graphics2D graphics2D = bufferedImage.createGraphics();
      synthIcon.paintIcon(paramJComponent, graphics2D, 0, 0);
      graphics2D.dispose();
      return new ImageIconUIResource(GrayFilter.createDisabledImage(bufferedImage));
    } 
    return super.getDisabledIcon(paramJComponent, paramIcon);
  }
  
  public Color getDerivedColor(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, boolean paramBoolean) { return this.defaults.getDerivedColor(paramString, paramFloat1, paramFloat2, paramFloat3, paramInt, paramBoolean); }
  
  protected final Color getDerivedColor(Color paramColor1, Color paramColor2, float paramFloat, boolean paramBoolean) {
    int i = deriveARGB(paramColor1, paramColor2, paramFloat);
    return paramBoolean ? new ColorUIResource(i) : new Color(i);
  }
  
  protected final Color getDerivedColor(Color paramColor1, Color paramColor2, float paramFloat) { return getDerivedColor(paramColor1, paramColor2, paramFloat, true); }
  
  static Object resolveToolbarConstraint(JToolBar paramJToolBar) {
    if (paramJToolBar != null) {
      Container container = paramJToolBar.getParent();
      if (container != null) {
        LayoutManager layoutManager = container.getLayout();
        if (layoutManager instanceof BorderLayout) {
          BorderLayout borderLayout = (BorderLayout)layoutManager;
          Object object = borderLayout.getConstraints(paramJToolBar);
          return (object == "South" || object == "East" || object == "West") ? object : "North";
        } 
      } 
    } 
    return "North";
  }
  
  static int deriveARGB(Color paramColor1, Color paramColor2, float paramFloat) {
    int i = paramColor1.getRed() + Math.round((paramColor2.getRed() - paramColor1.getRed()) * paramFloat);
    int j = paramColor1.getGreen() + Math.round((paramColor2.getGreen() - paramColor1.getGreen()) * paramFloat);
    int k = paramColor1.getBlue() + Math.round((paramColor2.getBlue() - paramColor1.getBlue()) * paramFloat);
    int m = paramColor1.getAlpha() + Math.round((paramColor2.getAlpha() - paramColor1.getAlpha()) * paramFloat);
    return (m & 0xFF) << 24 | (i & 0xFF) << 16 | (j & 0xFF) << 8 | k & 0xFF;
  }
  
  static String parsePrefix(String paramString) {
    if (paramString == null)
      return null; 
    boolean bool = false;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '"') {
        bool = !bool ? 1 : 0;
      } else if ((c == '[' || c == '.') && !bool) {
        return paramString.substring(0, b);
      } 
    } 
    return null;
  }
  
  Map<String, Object> getDefaultsForPrefix(String paramString) {
    if (this.compiledDefaults == null) {
      this.compiledDefaults = new HashMap();
      for (Map.Entry entry : UIManager.getDefaults().entrySet()) {
        if (entry.getKey() instanceof String)
          addDefault((String)entry.getKey(), entry.getValue()); 
      } 
      if (!this.defaultListenerAdded) {
        UIManager.getDefaults().addPropertyChangeListener(this.defaultsListener);
        this.defaultListenerAdded = true;
      } 
    } 
    return (Map)this.compiledDefaults.get(paramString);
  }
  
  private void addDefault(String paramString, Object paramObject) {
    if (this.compiledDefaults == null)
      return; 
    String str = parsePrefix(paramString);
    if (str != null) {
      Map map = (Map)this.compiledDefaults.get(str);
      if (map == null) {
        map = new HashMap();
        this.compiledDefaults.put(str, map);
      } 
      map.put(paramString, paramObject);
    } 
  }
  
  private class DefaultsListener implements PropertyChangeListener {
    private DefaultsListener() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("UIDefaults".equals(str)) {
        NimbusLookAndFeel.this.compiledDefaults = null;
      } else {
        NimbusLookAndFeel.this.addDefault(str, param1PropertyChangeEvent.getNewValue());
      } 
    }
  }
  
  private class LinkProperty implements UIDefaults.ActiveValue, UIResource {
    private String dstPropName;
    
    private LinkProperty(String param1String) { this.dstPropName = param1String; }
    
    public Object createValue(UIDefaults param1UIDefaults) { return UIManager.get(this.dstPropName); }
  }
  
  private class NimbusProperty implements UIDefaults.ActiveValue, UIResource {
    private String prefix;
    
    private String state = null;
    
    private String suffix;
    
    private boolean isFont;
    
    private NimbusProperty(String param1String1, String param1String2) {
      this.prefix = param1String1;
      this.suffix = param1String2;
      this.isFont = "font".equals(param1String2);
    }
    
    private NimbusProperty(String param1String1, String param1String2, String param1String3) {
      this(param1String1, param1String3);
      this.state = param1String2;
    }
    
    public Object createValue(UIDefaults param1UIDefaults) {
      Object object = null;
      if (this.state != null)
        object = NimbusLookAndFeel.this.uiDefaults.get(this.prefix + "[" + this.state + "]." + this.suffix); 
      if (object == null)
        object = NimbusLookAndFeel.this.uiDefaults.get(this.prefix + "[Enabled]." + this.suffix); 
      if (object == null)
        if (this.isFont) {
          object = NimbusLookAndFeel.this.uiDefaults.get("defaultFont");
        } else {
          object = NimbusLookAndFeel.this.uiDefaults.get(this.suffix);
        }  
      return object;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\NimbusLookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */