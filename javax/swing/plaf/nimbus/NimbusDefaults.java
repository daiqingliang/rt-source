package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthStyle;
import sun.font.FontUtilities;
import sun.swing.plaf.synth.DefaultSynthStyle;

final class NimbusDefaults {
  private Map<Region, List<LazyStyle>> m = new HashMap();
  
  private Map<String, Region> registeredRegions = new HashMap();
  
  private Map<JComponent, Map<Region, SynthStyle>> overridesCache = new WeakHashMap();
  
  private DefaultSynthStyle defaultStyle = new DefaultSynthStyle();
  
  private FontUIResource defaultFont = FontUtilities.getFontConfigFUIR("sans", 0, 12);
  
  private ColorTree colorTree = new ColorTree(null);
  
  private DefaultsListener defaultsListener = new DefaultsListener(null);
  
  private Map<DerivedColor, DerivedColor> derivedColors = new HashMap();
  
  void initialize() {
    UIManager.addPropertyChangeListener(this.defaultsListener);
    UIManager.getDefaults().addPropertyChangeListener(this.colorTree);
  }
  
  void uninitialize() {
    UIManager.removePropertyChangeListener(this.defaultsListener);
    UIManager.getDefaults().removePropertyChangeListener(this.colorTree);
  }
  
  NimbusDefaults() {
    this.defaultStyle.setFont(this.defaultFont);
    register(Region.ARROW_BUTTON, "ArrowButton");
    register(Region.BUTTON, "Button");
    register(Region.TOGGLE_BUTTON, "ToggleButton");
    register(Region.RADIO_BUTTON, "RadioButton");
    register(Region.CHECK_BOX, "CheckBox");
    register(Region.COLOR_CHOOSER, "ColorChooser");
    register(Region.PANEL, "ColorChooser:\"ColorChooser.previewPanelHolder\"");
    register(Region.LABEL, "ColorChooser:\"ColorChooser.previewPanelHolder\":\"OptionPane.label\"");
    register(Region.COMBO_BOX, "ComboBox");
    register(Region.TEXT_FIELD, "ComboBox:\"ComboBox.textField\"");
    register(Region.ARROW_BUTTON, "ComboBox:\"ComboBox.arrowButton\"");
    register(Region.LABEL, "ComboBox:\"ComboBox.listRenderer\"");
    register(Region.LABEL, "ComboBox:\"ComboBox.renderer\"");
    register(Region.SCROLL_PANE, "\"ComboBox.scrollPane\"");
    register(Region.FILE_CHOOSER, "FileChooser");
    register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrameTitlePane");
    register(Region.INTERNAL_FRAME, "InternalFrame");
    register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrame:InternalFrameTitlePane");
    register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"");
    register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"");
    register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"");
    register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"");
    register(Region.DESKTOP_ICON, "DesktopIcon");
    register(Region.DESKTOP_PANE, "DesktopPane");
    register(Region.LABEL, "Label");
    register(Region.LIST, "List");
    register(Region.LABEL, "List:\"List.cellRenderer\"");
    register(Region.MENU_BAR, "MenuBar");
    register(Region.MENU, "MenuBar:Menu");
    register(Region.MENU_ITEM_ACCELERATOR, "MenuBar:Menu:MenuItemAccelerator");
    register(Region.MENU_ITEM, "MenuItem");
    register(Region.MENU_ITEM_ACCELERATOR, "MenuItem:MenuItemAccelerator");
    register(Region.RADIO_BUTTON_MENU_ITEM, "RadioButtonMenuItem");
    register(Region.MENU_ITEM_ACCELERATOR, "RadioButtonMenuItem:MenuItemAccelerator");
    register(Region.CHECK_BOX_MENU_ITEM, "CheckBoxMenuItem");
    register(Region.MENU_ITEM_ACCELERATOR, "CheckBoxMenuItem:MenuItemAccelerator");
    register(Region.MENU, "Menu");
    register(Region.MENU_ITEM_ACCELERATOR, "Menu:MenuItemAccelerator");
    register(Region.POPUP_MENU, "PopupMenu");
    register(Region.POPUP_MENU_SEPARATOR, "PopupMenuSeparator");
    register(Region.OPTION_PANE, "OptionPane");
    register(Region.SEPARATOR, "OptionPane:\"OptionPane.separator\"");
    register(Region.PANEL, "OptionPane:\"OptionPane.messageArea\"");
    register(Region.LABEL, "OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\"");
    register(Region.PANEL, "Panel");
    register(Region.PROGRESS_BAR, "ProgressBar");
    register(Region.SEPARATOR, "Separator");
    register(Region.SCROLL_BAR, "ScrollBar");
    register(Region.ARROW_BUTTON, "ScrollBar:\"ScrollBar.button\"");
    register(Region.SCROLL_BAR_THUMB, "ScrollBar:ScrollBarThumb");
    register(Region.SCROLL_BAR_TRACK, "ScrollBar:ScrollBarTrack");
    register(Region.SCROLL_PANE, "ScrollPane");
    register(Region.VIEWPORT, "Viewport");
    register(Region.SLIDER, "Slider");
    register(Region.SLIDER_THUMB, "Slider:SliderThumb");
    register(Region.SLIDER_TRACK, "Slider:SliderTrack");
    register(Region.SPINNER, "Spinner");
    register(Region.PANEL, "Spinner:\"Spinner.editor\"");
    register(Region.FORMATTED_TEXT_FIELD, "Spinner:Panel:\"Spinner.formattedTextField\"");
    register(Region.ARROW_BUTTON, "Spinner:\"Spinner.previousButton\"");
    register(Region.ARROW_BUTTON, "Spinner:\"Spinner.nextButton\"");
    register(Region.SPLIT_PANE, "SplitPane");
    register(Region.SPLIT_PANE_DIVIDER, "SplitPane:SplitPaneDivider");
    register(Region.TABBED_PANE, "TabbedPane");
    register(Region.TABBED_PANE_TAB, "TabbedPane:TabbedPaneTab");
    register(Region.TABBED_PANE_TAB_AREA, "TabbedPane:TabbedPaneTabArea");
    register(Region.TABBED_PANE_CONTENT, "TabbedPane:TabbedPaneContent");
    register(Region.TABLE, "Table");
    register(Region.LABEL, "Table:\"Table.cellRenderer\"");
    register(Region.TABLE_HEADER, "TableHeader");
    register(Region.LABEL, "TableHeader:\"TableHeader.renderer\"");
    register(Region.TEXT_FIELD, "\"Table.editor\"");
    register(Region.TEXT_FIELD, "\"Tree.cellEditor\"");
    register(Region.TEXT_FIELD, "TextField");
    register(Region.FORMATTED_TEXT_FIELD, "FormattedTextField");
    register(Region.PASSWORD_FIELD, "PasswordField");
    register(Region.TEXT_AREA, "TextArea");
    register(Region.TEXT_PANE, "TextPane");
    register(Region.EDITOR_PANE, "EditorPane");
    register(Region.TOOL_BAR, "ToolBar");
    register(Region.BUTTON, "ToolBar:Button");
    register(Region.TOGGLE_BUTTON, "ToolBar:ToggleButton");
    register(Region.TOOL_BAR_SEPARATOR, "ToolBarSeparator");
    register(Region.TOOL_TIP, "ToolTip");
    register(Region.TREE, "Tree");
    register(Region.TREE_CELL, "Tree:TreeCell");
    register(Region.LABEL, "Tree:\"Tree.cellRenderer\"");
    register(Region.ROOT_PANE, "RootPane");
  }
  
  void initializeDefaults(UIDefaults paramUIDefaults) {
    addColor(paramUIDefaults, "text", 0, 0, 0, 255);
    addColor(paramUIDefaults, "control", 214, 217, 223, 255);
    addColor(paramUIDefaults, "nimbusBase", 51, 98, 140, 255);
    addColor(paramUIDefaults, "nimbusBlueGrey", "nimbusBase", 0.032459438F, -0.52518797F, 0.19607842F, 0);
    addColor(paramUIDefaults, "nimbusOrange", 191, 98, 4, 255);
    addColor(paramUIDefaults, "nimbusGreen", 176, 179, 50, 255);
    addColor(paramUIDefaults, "nimbusRed", 169, 46, 34, 255);
    addColor(paramUIDefaults, "nimbusBorder", "nimbusBlueGrey", 0.0F, -0.017358616F, -0.11372548F, 0);
    addColor(paramUIDefaults, "nimbusSelection", "nimbusBase", -0.010750473F, -0.04875779F, -0.007843137F, 0);
    addColor(paramUIDefaults, "nimbusInfoBlue", 47, 92, 180, 255);
    addColor(paramUIDefaults, "nimbusAlertYellow", 255, 220, 35, 255);
    addColor(paramUIDefaults, "nimbusFocus", 115, 164, 209, 255);
    addColor(paramUIDefaults, "nimbusSelectedText", 255, 255, 255, 255);
    addColor(paramUIDefaults, "nimbusSelectionBackground", 57, 105, 138, 255);
    addColor(paramUIDefaults, "nimbusDisabledText", 142, 143, 145, 255);
    addColor(paramUIDefaults, "nimbusLightBackground", 255, 255, 255, 255);
    addColor(paramUIDefaults, "infoText", "text", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "info", 242, 242, 189, 255);
    addColor(paramUIDefaults, "menuText", "text", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "menu", "nimbusBase", 0.021348298F, -0.6150531F, 0.39999998F, 0);
    addColor(paramUIDefaults, "scrollbar", "nimbusBlueGrey", -0.006944418F, -0.07296763F, 0.09019607F, 0);
    addColor(paramUIDefaults, "controlText", "text", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "controlHighlight", "nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
    addColor(paramUIDefaults, "controlLHighlight", "nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
    addColor(paramUIDefaults, "controlShadow", "nimbusBlueGrey", -0.0027777553F, -0.0212406F, 0.13333333F, 0);
    addColor(paramUIDefaults, "controlDkShadow", "nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
    addColor(paramUIDefaults, "textHighlight", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "textHighlightText", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "textInactiveText", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "desktop", "nimbusBase", -0.009207249F, -0.13984653F, -0.07450983F, 0);
    addColor(paramUIDefaults, "activeCaption", "nimbusBlueGrey", 0.0F, -0.049920253F, 0.031372547F, 0);
    addColor(paramUIDefaults, "inactiveCaption", "nimbusBlueGrey", -0.00505054F, -0.055526316F, 0.039215684F, 0);
    paramUIDefaults.put("defaultFont", new FontUIResource(this.defaultFont));
    paramUIDefaults.put("InternalFrame.titleFont", new DerivedFont("defaultFont", 1.0F, Boolean.valueOf(true), null));
    addColor(paramUIDefaults, "textForeground", "text", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "background", "control", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TitledBorder.position", "ABOVE_TOP");
    paramUIDefaults.put("FileView.fullRowSelection", Boolean.TRUE);
    paramUIDefaults.put("ArrowButton.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ArrowButton.size", new Integer(16));
    paramUIDefaults.put("ArrowButton[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ArrowButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ArrowButton[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ArrowButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Button.contentMargins", new InsetsUIResource(6, 14, 6, 14));
    paramUIDefaults.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
    paramUIDefaults.put("Button[Default].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 1, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Default+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 2, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Default+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 3, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Default+Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 4, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    addColor(paramUIDefaults, "Button[Default+Pressed].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Button[Default+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 5, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Default+Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 6, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    addColor(paramUIDefaults, "Button[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Button[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 7, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 8, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 9, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 10, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 11, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 12, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Button[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 13, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton.contentMargins", new InsetsUIResource(6, 14, 6, 14));
    addColor(paramUIDefaults, "ToggleButton[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ToggleButton[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 1, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 2, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 3, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 4, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 5, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 6, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 7, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 8, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 9, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 10, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Focused+Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 11, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 12, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ToggleButton[Focused+MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 13, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    addColor(paramUIDefaults, "ToggleButton[Disabled+Selected].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ToggleButton[Disabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 14, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("RadioButton.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "RadioButton[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("RadioButton[Disabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Enabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Focused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Focused+MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Focused+Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Focused+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 11, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 12, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Focused+Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 13, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 14, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Focused+MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 15, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton[Disabled+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 16, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButton.icon", new NimbusIcon("RadioButton", "iconPainter", 18, 18));
    paramUIDefaults.put("CheckBox.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "CheckBox[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("CheckBox[Disabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 3, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Enabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 4, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Focused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 5, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 6, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Focused+MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Focused+Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Focused+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 11, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 12, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Focused+Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 13, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 14, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Focused+MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 15, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox[Disabled+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 16, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBox.icon", new NimbusIcon("CheckBox", "iconPainter", 18, 18));
    paramUIDefaults.put("ColorChooser.contentMargins", new InsetsUIResource(5, 0, 0, 0));
    addColor(paramUIDefaults, "ColorChooser.swatchesDefaultRecentColor", 255, 255, 255, 255);
    paramUIDefaults.put("ColorChooser:\"ColorChooser.previewPanelHolder\".contentMargins", new InsetsUIResource(0, 5, 10, 5));
    paramUIDefaults.put("ColorChooser:\"ColorChooser.previewPanelHolder\":\"OptionPane.label\".contentMargins", new InsetsUIResource(0, 10, 10, 10));
    paramUIDefaults.put("ComboBox.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ComboBox.States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,Editable");
    paramUIDefaults.put("ComboBox.Editable", new ComboBoxEditableState());
    paramUIDefaults.put("ComboBox.forceOpaque", Boolean.TRUE);
    paramUIDefaults.put("ComboBox.buttonWhenNotEditable", Boolean.TRUE);
    paramUIDefaults.put("ComboBox.rendererUseListColors", Boolean.FALSE);
    paramUIDefaults.put("ComboBox.pressedWhenPopupVisible", Boolean.TRUE);
    paramUIDefaults.put("ComboBox.squareButton", Boolean.FALSE);
    paramUIDefaults.put("ComboBox.popupInsets", new InsetsUIResource(-2, 2, 0, 2));
    paramUIDefaults.put("ComboBox.padding", new InsetsUIResource(3, 3, 3, 3));
    paramUIDefaults.put("ComboBox[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 1, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Disabled+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 2, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 3, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 4, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 5, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 6, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 7, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 8, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Enabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 9, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Disabled+Editable].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 10, new Insets(6, 5, 6, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Editable+Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 11, new Insets(6, 5, 6, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Editable+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 12, new Insets(5, 5, 5, 5), new Dimension(142, 27), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Editable+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 13, new Insets(4, 5, 5, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox[Editable+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 14, new Insets(4, 5, 5, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.textField\".contentMargins", new InsetsUIResource(0, 6, 0, 3));
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.textField\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ComboBox:\"ComboBox.textField\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 1, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.textField\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 2, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.textField\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ComboBox:\"ComboBox.textField\"[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 3, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\".States", "Enabled,MouseOver,Pressed,Disabled,Editable");
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\".Editable", new ComboBoxArrowButtonEditableState());
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\".size", new Integer(19));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Disabled+Editable].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 5, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 6, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 7, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 8, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 9, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 10, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 11, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 12, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 13, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Selected].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 14, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ComboBox:\"ComboBox.listRenderer\".contentMargins", new InsetsUIResource(2, 4, 2, 4));
    paramUIDefaults.put("ComboBox:\"ComboBox.listRenderer\".opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.listRenderer\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.listRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.listRenderer\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.listRenderer\"[Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ComboBox:\"ComboBox.renderer\".contentMargins", new InsetsUIResource(2, 4, 2, 4));
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.renderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.renderer\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "ComboBox:\"ComboBox.renderer\"[Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("\"ComboBox.scrollPane\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("FileChooser.contentMargins", new InsetsUIResource(10, 10, 10, 10));
    paramUIDefaults.put("FileChooser.opaque", Boolean.TRUE);
    paramUIDefaults.put("FileChooser.usesSingleFilePane", Boolean.TRUE);
    paramUIDefaults.put("FileChooser[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 1, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser[Enabled].fileIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 2, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.fileIcon", new NimbusIcon("FileChooser", "fileIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].directoryIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 3, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.directoryIcon", new NimbusIcon("FileChooser", "directoryIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].upFolderIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 4, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.upFolderIcon", new NimbusIcon("FileChooser", "upFolderIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].newFolderIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 5, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.newFolderIcon", new NimbusIcon("FileChooser", "newFolderIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].hardDriveIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 7, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.hardDriveIcon", new NimbusIcon("FileChooser", "hardDriveIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].floppyDriveIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 8, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.floppyDriveIcon", new NimbusIcon("FileChooser", "floppyDriveIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].homeFolderIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 9, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.homeFolderIcon", new NimbusIcon("FileChooser", "homeFolderIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].detailsViewIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 10, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.detailsViewIcon", new NimbusIcon("FileChooser", "detailsViewIconPainter", 16, 16));
    paramUIDefaults.put("FileChooser[Enabled].listViewIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 11, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("FileChooser.listViewIcon", new NimbusIcon("FileChooser", "listViewIconPainter", 16, 16));
    paramUIDefaults.put("InternalFrameTitlePane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("InternalFrameTitlePane.maxFrameIconSize", new DimensionUIResource(18, 18));
    paramUIDefaults.put("InternalFrame.contentMargins", new InsetsUIResource(1, 6, 6, 6));
    paramUIDefaults.put("InternalFrame.States", "Enabled,WindowFocused");
    paramUIDefaults.put("InternalFrame.WindowFocused", new InternalFrameWindowFocusedState());
    paramUIDefaults.put("InternalFrame[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFramePainter", 1, new Insets(25, 6, 6, 6), new Dimension(25, 36), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame[Enabled+WindowFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFramePainter", 2, new Insets(25, 6, 6, 6), new Dimension(25, 36), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane.contentMargins", new InsetsUIResource(3, 0, 3, 0));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane.States", "Enabled,WindowFocused");
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane.WindowFocused", new InternalFrameTitlePaneWindowFocusedState());
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane.titleAlignment", "CENTER");
    addColor(paramUIDefaults, "InternalFrame:InternalFrameTitlePane[Enabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".WindowNotFocused", new InternalFrameTitlePaneMenuButtonWindowNotFocusedState());
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".test", "am InternalFrameTitlePane.menuButton");
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Enabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Disabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Enabled+WindowNotFocused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[MouseOver+WindowNotFocused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Pressed+WindowNotFocused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".icon", new NimbusIcon("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"", "iconPainter", 19, 18));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".WindowNotFocused", new InternalFrameTitlePaneIconifyButtonWindowNotFocusedState());
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused,WindowMaximized");
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".WindowNotFocused", new InternalFrameTitlePaneMaximizeButtonWindowNotFocusedState());
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".WindowMaximized", new InternalFrameTitlePaneMaximizeButtonWindowMaximizedState());
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Disabled+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowMaximized+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 8, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 9, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 10, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 11, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 12, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 13, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 14, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".WindowNotFocused", new InternalFrameTitlePaneCloseButtonWindowNotFocusedState());
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("DesktopIcon.contentMargins", new InsetsUIResource(4, 6, 5, 4));
    paramUIDefaults.put("DesktopIcon[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.DesktopIconPainter", 1, new Insets(5, 5, 5, 5), new Dimension(28, 26), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("DesktopPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("DesktopPane.opaque", Boolean.TRUE);
    paramUIDefaults.put("DesktopPane[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.DesktopPanePainter", 1, new Insets(0, 0, 0, 0), new Dimension(300, 232), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("Label.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "Label[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("List.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("List.opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "List.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("List.rendererUseListColors", Boolean.FALSE);
    paramUIDefaults.put("List.rendererUseUIBorder", Boolean.TRUE);
    paramUIDefaults.put("List.cellNoFocusBorder", new BorderUIResource(BorderFactory.createEmptyBorder(2, 5, 2, 5)));
    paramUIDefaults.put("List.focusCellHighlightBorder", new BorderUIResource(new PainterBorder("Tree:TreeCell[Enabled+Focused].backgroundPainter", new Insets(2, 5, 2, 5))));
    addColor(paramUIDefaults, "List.dropLineColor", "nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "List[Selected].textForeground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "List[Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "List[Disabled+Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "List[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("List:\"List.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("List:\"List.cellRenderer\".opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "List:\"List.cellRenderer\"[Selected].textForeground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "List:\"List.cellRenderer\"[Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "List:\"List.cellRenderer\"[Disabled+Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "List:\"List.cellRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("MenuBar.contentMargins", new InsetsUIResource(2, 6, 2, 6));
    paramUIDefaults.put("MenuBar[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuBarPainter", 1, new Insets(1, 0, 0, 0), new Dimension(18, 22), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("MenuBar[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuBarPainter", 2, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("MenuBar:Menu.contentMargins", new InsetsUIResource(1, 4, 2, 4));
    addColor(paramUIDefaults, "MenuBar:Menu[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "MenuBar:Menu[Enabled].textForeground", 35, 35, 36, 255);
    addColor(paramUIDefaults, "MenuBar:Menu[Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("MenuBar:Menu[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuBarMenuPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("MenuBar:Menu:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("MenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
    paramUIDefaults.put("MenuItem.textIconGap", new Integer(5));
    addColor(paramUIDefaults, "MenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "MenuItem[Enabled].textForeground", 35, 35, 36, 255);
    addColor(paramUIDefaults, "MenuItem[MouseOver].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("MenuItem[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("MenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "MenuItem:MenuItemAccelerator[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "MenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("RadioButtonMenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
    paramUIDefaults.put("RadioButtonMenuItem.textIconGap", new Integer(5));
    addColor(paramUIDefaults, "RadioButtonMenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "RadioButtonMenuItem[Enabled].textForeground", 35, 35, 36, 255);
    addColor(paramUIDefaults, "RadioButtonMenuItem[MouseOver].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("RadioButtonMenuItem[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "RadioButtonMenuItem[MouseOver+Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("RadioButtonMenuItem[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 4, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButtonMenuItem[Disabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButtonMenuItem[Enabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 6, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButtonMenuItem[MouseOver+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 7, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("RadioButtonMenuItem.checkIcon", new NimbusIcon("RadioButtonMenuItem", "checkIconPainter", 9, 10));
    paramUIDefaults.put("RadioButtonMenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "RadioButtonMenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("CheckBoxMenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
    paramUIDefaults.put("CheckBoxMenuItem.textIconGap", new Integer(5));
    addColor(paramUIDefaults, "CheckBoxMenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "CheckBoxMenuItem[Enabled].textForeground", 35, 35, 36, 255);
    addColor(paramUIDefaults, "CheckBoxMenuItem[MouseOver].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("CheckBoxMenuItem[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "CheckBoxMenuItem[MouseOver+Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("CheckBoxMenuItem[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 4, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBoxMenuItem[Disabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBoxMenuItem[Enabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 6, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBoxMenuItem[MouseOver+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 7, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("CheckBoxMenuItem.checkIcon", new NimbusIcon("CheckBoxMenuItem", "checkIconPainter", 9, 10));
    paramUIDefaults.put("CheckBoxMenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "CheckBoxMenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("Menu.contentMargins", new InsetsUIResource(1, 12, 2, 5));
    paramUIDefaults.put("Menu.textIconGap", new Integer(5));
    addColor(paramUIDefaults, "Menu[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "Menu[Enabled].textForeground", 35, 35, 36, 255);
    addColor(paramUIDefaults, "Menu[Enabled+Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("Menu[Enabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("Menu[Disabled].arrowIconPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 4, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Menu[Enabled].arrowIconPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Menu[Enabled+Selected].arrowIconPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 6, new Insets(1, 1, 1, 1), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Menu.arrowIcon", new NimbusIcon("Menu", "arrowIconPainter", 9, 10));
    paramUIDefaults.put("Menu:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "Menu:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("PopupMenu.contentMargins", new InsetsUIResource(6, 1, 6, 1));
    paramUIDefaults.put("PopupMenu.opaque", Boolean.TRUE);
    paramUIDefaults.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);
    paramUIDefaults.put("PopupMenu[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PopupMenuPainter", 1, new Insets(9, 0, 11, 0), new Dimension(220, 313), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("PopupMenu[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PopupMenuPainter", 2, new Insets(11, 2, 11, 2), new Dimension(220, 313), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("PopupMenuSeparator.contentMargins", new InsetsUIResource(1, 0, 2, 0));
    paramUIDefaults.put("PopupMenuSeparator[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PopupMenuSeparatorPainter", 1, new Insets(1, 1, 1, 1), new Dimension(3, 3), true, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("OptionPane.contentMargins", new InsetsUIResource(15, 15, 15, 15));
    paramUIDefaults.put("OptionPane.opaque", Boolean.TRUE);
    paramUIDefaults.put("OptionPane.buttonOrientation", new Integer(4));
    paramUIDefaults.put("OptionPane.messageAnchor", new Integer(17));
    paramUIDefaults.put("OptionPane.separatorPadding", new Integer(0));
    paramUIDefaults.put("OptionPane.sameSizeButtons", Boolean.FALSE);
    paramUIDefaults.put("OptionPane:\"OptionPane.separator\".contentMargins", new InsetsUIResource(1, 0, 0, 0));
    paramUIDefaults.put("OptionPane:\"OptionPane.messageArea\".contentMargins", new InsetsUIResource(0, 0, 10, 0));
    paramUIDefaults.put("OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\".contentMargins", new InsetsUIResource(0, 10, 10, 10));
    paramUIDefaults.put("OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPaneMessageAreaOptionPaneLabelPainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("OptionPane[Enabled].errorIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 2, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("OptionPane.errorIcon", new NimbusIcon("OptionPane", "errorIconPainter", 48, 48));
    paramUIDefaults.put("OptionPane[Enabled].informationIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 3, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("OptionPane.informationIcon", new NimbusIcon("OptionPane", "informationIconPainter", 48, 48));
    paramUIDefaults.put("OptionPane[Enabled].questionIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 4, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("OptionPane.questionIcon", new NimbusIcon("OptionPane", "questionIconPainter", 48, 48));
    paramUIDefaults.put("OptionPane[Enabled].warningIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 5, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("OptionPane.warningIcon", new NimbusIcon("OptionPane", "warningIconPainter", 48, 48));
    paramUIDefaults.put("Panel.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Panel.opaque", Boolean.TRUE);
    paramUIDefaults.put("ProgressBar.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ProgressBar.States", "Enabled,Disabled,Indeterminate,Finished");
    paramUIDefaults.put("ProgressBar.Indeterminate", new ProgressBarIndeterminateState());
    paramUIDefaults.put("ProgressBar.Finished", new ProgressBarFinishedState());
    paramUIDefaults.put("ProgressBar.tileWhenIndeterminate", Boolean.TRUE);
    paramUIDefaults.put("ProgressBar.tileWidth", new Integer(27));
    paramUIDefaults.put("ProgressBar.paintOutsideClip", Boolean.TRUE);
    paramUIDefaults.put("ProgressBar.rotateText", Boolean.TRUE);
    paramUIDefaults.put("ProgressBar.vertictalSize", new DimensionUIResource(19, 150));
    paramUIDefaults.put("ProgressBar.horizontalSize", new DimensionUIResource(150, 19));
    paramUIDefaults.put("ProgressBar.cycleTime", new Integer(250));
    paramUIDefaults.put("ProgressBar.minBarSize", new DimensionUIResource(6, 6));
    paramUIDefaults.put("ProgressBar.glowWidth", new Integer(2));
    paramUIDefaults.put("ProgressBar[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 1, new Insets(5, 5, 5, 5), new Dimension(29, 19), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    addColor(paramUIDefaults, "ProgressBar[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ProgressBar[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 2, new Insets(5, 5, 5, 5), new Dimension(29, 19), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ProgressBar[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 3, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ProgressBar[Enabled+Finished].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 4, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ProgressBar[Enabled+Indeterminate].progressPadding", new Integer(3));
    paramUIDefaults.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 5, new Insets(3, 3, 3, 3), new Dimension(30, 13), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ProgressBar[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 6, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ProgressBar[Disabled+Finished].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 7, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ProgressBar[Disabled+Indeterminate].progressPadding", new Integer(3));
    paramUIDefaults.put("ProgressBar[Disabled+Indeterminate].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 8, new Insets(3, 3, 3, 3), new Dimension(30, 13), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Separator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Separator[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SeparatorPainter", 1, new Insets(0, 40, 0, 40), new Dimension(100, 3), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ScrollBar.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ScrollBar.opaque", Boolean.TRUE);
    paramUIDefaults.put("ScrollBar.incrementButtonGap", new Integer(-8));
    paramUIDefaults.put("ScrollBar.decrementButtonGap", new Integer(-8));
    paramUIDefaults.put("ScrollBar.thumbHeight", new Integer(15));
    paramUIDefaults.put("ScrollBar.minimumThumbSize", new DimensionUIResource(29, 29));
    paramUIDefaults.put("ScrollBar.maximumThumbSize", new DimensionUIResource(1000, 1000));
    paramUIDefaults.put("ScrollBar:\"ScrollBar.button\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ScrollBar:\"ScrollBar.button\".size", new Integer(25));
    paramUIDefaults.put("ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 1, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ScrollBar:\"ScrollBar.button\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 2, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 3, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 4, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ScrollBar:ScrollBarThumb.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 2, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 4, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 5, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ScrollBar:ScrollBarTrack.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("ScrollBar:ScrollBarTrack[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarTrackPainter", 1, new Insets(5, 5, 5, 5), new Dimension(18, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarTrackPainter", 2, new Insets(5, 10, 5, 9), new Dimension(34, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("ScrollPane.contentMargins", new InsetsUIResource(3, 3, 3, 3));
    paramUIDefaults.put("ScrollPane.useChildTextComponentFocus", Boolean.TRUE);
    paramUIDefaults.put("ScrollPane[Enabled+Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ScrollPane[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("Viewport.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Viewport.opaque", Boolean.TRUE);
    paramUIDefaults.put("Slider.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Slider.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
    paramUIDefaults.put("Slider.ArrowShape", new SliderArrowShapeState());
    paramUIDefaults.put("Slider.thumbWidth", new Integer(17));
    paramUIDefaults.put("Slider.thumbHeight", new Integer(17));
    paramUIDefaults.put("Slider.trackBorder", new Integer(0));
    paramUIDefaults.put("Slider.paintValue", Boolean.FALSE);
    addColor(paramUIDefaults, "Slider.tickColor", 35, 40, 48, 255);
    paramUIDefaults.put("Slider:SliderThumb.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Slider:SliderThumb.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
    paramUIDefaults.put("Slider:SliderThumb.ArrowShape", new SliderThumbArrowShapeState());
    paramUIDefaults.put("Slider:SliderThumb[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 1, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 2, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 3, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 4, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 5, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 6, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 7, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[ArrowShape+Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 8, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[ArrowShape+Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 9, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[ArrowShape+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 10, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[ArrowShape+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 11, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[ArrowShape+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 12, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[ArrowShape+Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 13, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderThumb[ArrowShape+Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 14, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Slider:SliderTrack.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Slider:SliderTrack.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
    paramUIDefaults.put("Slider:SliderTrack.ArrowShape", new SliderTrackArrowShapeState());
    paramUIDefaults.put("Slider:SliderTrack[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderTrackPainter", 1, new Insets(6, 5, 6, 5), new Dimension(23, 17), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
    paramUIDefaults.put("Slider:SliderTrack[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderTrackPainter", 2, new Insets(6, 5, 6, 5), new Dimension(23, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Spinner:\"Spinner.editor\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\".contentMargins", new InsetsUIResource(6, 6, 5, 6));
    addColor(paramUIDefaults, "Spinner:Panel:\"Spinner.formattedTextField\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 1, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 2, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 3, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "Spinner:Panel:\"Spinner.formattedTextField\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 4, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 5, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\".size", new Integer(20));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 1, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 2, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 3, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 4, new Insets(3, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 5, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 6, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 7, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 8, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 9, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Focused].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 10, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 11, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 12, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 13, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.previousButton\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 14, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\".size", new Integer(20));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 1, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 2, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 3, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 4, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 5, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 6, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 7, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 8, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 9, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Focused].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 10, new Insets(3, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 11, new Insets(3, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 12, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 13, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Spinner:\"Spinner.nextButton\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 14, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("SplitPane.contentMargins", new InsetsUIResource(1, 1, 1, 1));
    paramUIDefaults.put("SplitPane.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
    paramUIDefaults.put("SplitPane.Vertical", new SplitPaneVerticalState());
    paramUIDefaults.put("SplitPane.size", new Integer(10));
    paramUIDefaults.put("SplitPane.dividerSize", new Integer(10));
    paramUIDefaults.put("SplitPane.centerOneTouchButtons", Boolean.TRUE);
    paramUIDefaults.put("SplitPane.oneTouchButtonOffset", new Integer(30));
    paramUIDefaults.put("SplitPane.oneTouchExpandable", Boolean.FALSE);
    paramUIDefaults.put("SplitPane.continuousLayout", Boolean.TRUE);
    paramUIDefaults.put("SplitPane:SplitPaneDivider.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("SplitPane:SplitPaneDivider.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
    paramUIDefaults.put("SplitPane:SplitPaneDivider.Vertical", new SplitPaneDividerVerticalState());
    paramUIDefaults.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 1, new Insets(3, 0, 3, 0), new Dimension(68, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("SplitPane:SplitPaneDivider[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 2, new Insets(3, 0, 3, 0), new Dimension(68, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 3, new Insets(0, 24, 0, 24), new Dimension(68, 10), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 4, new Insets(5, 0, 5, 0), new Dimension(10, 38), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("TabbedPane.tabAreaStatesMatchSelectedTab", Boolean.TRUE);
    paramUIDefaults.put("TabbedPane.nudgeSelectedLabel", Boolean.FALSE);
    paramUIDefaults.put("TabbedPane.tabRunOverlay", new Integer(2));
    paramUIDefaults.put("TabbedPane.tabOverlap", new Integer(-1));
    paramUIDefaults.put("TabbedPane.extendTabsToBase", Boolean.TRUE);
    paramUIDefaults.put("TabbedPane.useBasicArrows", Boolean.TRUE);
    addColor(paramUIDefaults, "TabbedPane.shadow", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "TabbedPane.darkShadow", "text", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "TabbedPane.highlight", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TabbedPane:TabbedPaneTab.contentMargins", new InsetsUIResource(2, 8, 3, 8));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 1, new Insets(7, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 2, new Insets(7, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 3, new Insets(7, 6, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "TabbedPane:TabbedPaneTab[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 4, new Insets(6, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 5, new Insets(7, 7, 0, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 6, new Insets(7, 7, 0, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 7, new Insets(7, 9, 0, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "TabbedPane:TabbedPaneTab[Pressed+Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 8, new Insets(7, 9, 0, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 9, new Insets(7, 7, 3, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 10, new Insets(7, 9, 3, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 11, new Insets(7, 9, 3, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTabArea.contentMargins", new InsetsUIResource(3, 10, 4, 10));
    paramUIDefaults.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 1, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 2, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 3, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 4, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TabbedPane:TabbedPaneContent.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Table.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Table.opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "Table.textForeground", 35, 35, 36, 255);
    addColor(paramUIDefaults, "Table.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Table.showGrid", Boolean.FALSE);
    paramUIDefaults.put("Table.intercellSpacing", new DimensionUIResource(0, 0));
    addColor(paramUIDefaults, "Table.alternateRowColor", "nimbusLightBackground", 0.0F, 0.0F, -0.05098039F, 0, false);
    paramUIDefaults.put("Table.rendererUseTableColors", Boolean.TRUE);
    paramUIDefaults.put("Table.rendererUseUIBorder", Boolean.TRUE);
    paramUIDefaults.put("Table.cellNoFocusBorder", new BorderUIResource(BorderFactory.createEmptyBorder(2, 5, 2, 5)));
    paramUIDefaults.put("Table.focusCellHighlightBorder", new BorderUIResource(new PainterBorder("Tree:TreeCell[Enabled+Focused].backgroundPainter", new Insets(2, 5, 2, 5))));
    addColor(paramUIDefaults, "Table.dropLineColor", "nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "Table.dropLineShortColor", "nimbusOrange", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "Table[Enabled+Selected].textForeground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0, false);
    addColor(paramUIDefaults, "Table[Enabled+Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0, false);
    addColor(paramUIDefaults, "Table[Disabled+Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0, false);
    paramUIDefaults.put("Table:\"Table.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Table:\"Table.cellRenderer\".opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "Table:\"Table.cellRenderer\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0, false);
    paramUIDefaults.put("TableHeader.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("TableHeader.opaque", Boolean.TRUE);
    paramUIDefaults.put("TableHeader.rightAlignSortArrow", Boolean.TRUE);
    paramUIDefaults.put("TableHeader[Enabled].ascendingSortIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderPainter", 1, new Insets(0, 0, 0, 2), new Dimension(7, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Table.ascendingSortIcon", new NimbusIcon("TableHeader", "ascendingSortIconPainter", 7, 7));
    paramUIDefaults.put("TableHeader[Enabled].descendingSortIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderPainter", 2, new Insets(0, 0, 0, 0), new Dimension(7, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Table.descendingSortIcon", new NimbusIcon("TableHeader", "descendingSortIconPainter", 7, 7));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\".contentMargins", new InsetsUIResource(2, 5, 4, 5));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\".opaque", Boolean.TRUE);
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Sorted");
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\".Sorted", new TableHeaderRendererSortedState());
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 1, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 2, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 3, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 4, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 5, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 6, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 7, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TableHeader:\"TableHeader.renderer\"[Disabled+Sorted].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 8, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("\"Table.editor\".contentMargins", new InsetsUIResource(3, 5, 3, 5));
    paramUIDefaults.put("\"Table.editor\".opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "\"Table.editor\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "\"Table.editor\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("\"Table.editor\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableEditorPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("\"Table.editor\"[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableEditorPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "\"Table.editor\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("\"Tree.cellEditor\".contentMargins", new InsetsUIResource(2, 5, 2, 5));
    paramUIDefaults.put("\"Tree.cellEditor\".opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "\"Tree.cellEditor\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "\"Tree.cellEditor\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("\"Tree.cellEditor\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellEditorPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("\"Tree.cellEditor\"[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellEditorPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "\"Tree.cellEditor\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
    addColor(paramUIDefaults, "TextField.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "TextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextField[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TextField[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "TextField[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextField[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "TextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextField[Disabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TextField[Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TextField[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("FormattedTextField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
    addColor(paramUIDefaults, "FormattedTextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("FormattedTextField[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("FormattedTextField[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "FormattedTextField[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("FormattedTextField[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "FormattedTextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("FormattedTextField[Disabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("FormattedTextField[Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("FormattedTextField[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("PasswordField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
    addColor(paramUIDefaults, "PasswordField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("PasswordField[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("PasswordField[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "PasswordField[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("PasswordField[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "PasswordField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("PasswordField[Disabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("PasswordField[Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("PasswordField[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TextArea.contentMargins", new InsetsUIResource(6, 6, 6, 6));
    paramUIDefaults.put("TextArea.States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,NotInScrollPane");
    paramUIDefaults.put("TextArea.NotInScrollPane", new TextAreaNotInScrollPaneState());
    addColor(paramUIDefaults, "TextArea[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextArea[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("TextArea[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "TextArea[Disabled+NotInScrollPane].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextArea[Disabled+NotInScrollPane].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("TextArea[Enabled+NotInScrollPane].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 4, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "TextArea[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextArea[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "TextArea[Disabled+NotInScrollPane].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextArea[Disabled+NotInScrollPane].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 6, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TextArea[Focused+NotInScrollPane].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 7, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TextArea[Enabled+NotInScrollPane].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 8, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("TextPane.contentMargins", new InsetsUIResource(4, 6, 4, 6));
    paramUIDefaults.put("TextPane.opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "TextPane[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextPane[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("TextPane[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "TextPane[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("TextPane[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("EditorPane.contentMargins", new InsetsUIResource(4, 6, 4, 6));
    paramUIDefaults.put("EditorPane.opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "EditorPane[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("EditorPane[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("EditorPane[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "EditorPane[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("EditorPane[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("ToolBar.contentMargins", new InsetsUIResource(2, 2, 2, 2));
    paramUIDefaults.put("ToolBar.opaque", Boolean.TRUE);
    paramUIDefaults.put("ToolBar.States", "North,East,West,South");
    paramUIDefaults.put("ToolBar.North", new ToolBarNorthState());
    paramUIDefaults.put("ToolBar.East", new ToolBarEastState());
    paramUIDefaults.put("ToolBar.West", new ToolBarWestState());
    paramUIDefaults.put("ToolBar.South", new ToolBarSouthState());
    paramUIDefaults.put("ToolBar[North].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 1, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("ToolBar[South].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 2, new Insets(1, 0, 0, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("ToolBar[East].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 3, new Insets(1, 0, 0, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("ToolBar[West].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 4, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("ToolBar[Enabled].handleIconPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 5, new Insets(5, 5, 5, 5), new Dimension(11, 38), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar.handleIcon", new NimbusIcon("ToolBar", "handleIconPainter", 11, 38));
    paramUIDefaults.put("ToolBar:Button.contentMargins", new InsetsUIResource(4, 4, 4, 4));
    paramUIDefaults.put("ToolBar:Button[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 2, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:Button[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:Button[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:Button[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:Button[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton.contentMargins", new InsetsUIResource(4, 4, 4, 4));
    paramUIDefaults.put("ToolBar:ToggleButton[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 2, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 7, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 8, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 9, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Focused+Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 10, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 11, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBar:ToggleButton[Focused+MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 12, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    addColor(paramUIDefaults, "ToolBar:ToggleButton[Disabled+Selected].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ToolBar:ToggleButton[Disabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 13, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
    paramUIDefaults.put("ToolBarSeparator.contentMargins", new InsetsUIResource(2, 0, 3, 0));
    addColor(paramUIDefaults, "ToolBarSeparator.textForeground", "nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("ToolTip.contentMargins", new InsetsUIResource(4, 4, 4, 4));
    paramUIDefaults.put("ToolTip[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolTipPainter", 1, new Insets(1, 1, 1, 1), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("Tree.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("Tree.opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "Tree.textForeground", "text", 0.0F, 0.0F, 0.0F, 0, false);
    addColor(paramUIDefaults, "Tree.textBackground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0, false);
    addColor(paramUIDefaults, "Tree.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Tree.rendererFillBackground", Boolean.FALSE);
    paramUIDefaults.put("Tree.leftChildIndent", new Integer(12));
    paramUIDefaults.put("Tree.rightChildIndent", new Integer(4));
    paramUIDefaults.put("Tree.drawHorizontalLines", Boolean.FALSE);
    paramUIDefaults.put("Tree.drawVerticalLines", Boolean.FALSE);
    paramUIDefaults.put("Tree.showRootHandles", Boolean.FALSE);
    paramUIDefaults.put("Tree.rendererUseTreeColors", Boolean.TRUE);
    paramUIDefaults.put("Tree.repaintWholeRow", Boolean.TRUE);
    paramUIDefaults.put("Tree.rowHeight", new Integer(0));
    paramUIDefaults.put("Tree.rendererMargins", new InsetsUIResource(2, 0, 1, 5));
    addColor(paramUIDefaults, "Tree.selectionForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0, false);
    addColor(paramUIDefaults, "Tree.selectionBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0, false);
    addColor(paramUIDefaults, "Tree.dropLineColor", "nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Tree:TreeCell.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "Tree:TreeCell[Enabled].background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    addColor(paramUIDefaults, "Tree:TreeCell[Enabled+Focused].background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Tree:TreeCell[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "Tree:TreeCell[Enabled+Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("Tree:TreeCell[Enabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    addColor(paramUIDefaults, "Tree:TreeCell[Focused+Selected].textForeground", 255, 255, 255, 255);
    paramUIDefaults.put("Tree:TreeCell[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 4, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
    paramUIDefaults.put("Tree:\"Tree.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
    addColor(paramUIDefaults, "Tree:\"Tree.cellRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
    paramUIDefaults.put("Tree[Enabled].leafIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 4, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Tree.leafIcon", new NimbusIcon("Tree", "leafIconPainter", 16, 16));
    paramUIDefaults.put("Tree[Enabled].closedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 5, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Tree.closedIcon", new NimbusIcon("Tree", "closedIconPainter", 16, 16));
    paramUIDefaults.put("Tree[Enabled].openIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 6, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Tree.openIcon", new NimbusIcon("Tree", "openIconPainter", 16, 16));
    paramUIDefaults.put("Tree[Enabled].collapsedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Tree[Enabled+Selected].collapsedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Tree.collapsedIcon", new NimbusIcon("Tree", "collapsedIconPainter", 18, 7));
    paramUIDefaults.put("Tree[Enabled].expandedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Tree[Enabled+Selected].expandedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
    paramUIDefaults.put("Tree.expandedIcon", new NimbusIcon("Tree", "expandedIconPainter", 18, 7));
    paramUIDefaults.put("RootPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
    paramUIDefaults.put("RootPane.opaque", Boolean.TRUE);
    addColor(paramUIDefaults, "RootPane.background", "control", 0.0F, 0.0F, 0.0F, 0);
  }
  
  void register(Region paramRegion, String paramString) {
    if (paramRegion == null || paramString == null)
      throw new IllegalArgumentException("Neither Region nor Prefix may be null"); 
    List list = (List)this.m.get(paramRegion);
    if (list == null) {
      list = new LinkedList();
      list.add(new LazyStyle(paramString, null));
      this.m.put(paramRegion, list);
    } else {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        LazyStyle lazyStyle;
        if (paramString.equals(lazyStyle.prefix))
          return; 
      } 
      list.add(new LazyStyle(paramString, null));
    } 
    this.registeredRegions.put(paramRegion.getName(), paramRegion);
  }
  
  SynthStyle getStyle(JComponent paramJComponent, Region paramRegion) {
    if (paramJComponent == null || paramRegion == null)
      throw new IllegalArgumentException("Neither comp nor r may be null"); 
    List list = (List)this.m.get(paramRegion);
    if (list == null || list.size() == 0)
      return this.defaultStyle; 
    LazyStyle lazyStyle = null;
    Iterator iterator = list.iterator();
    while (iterator.hasNext()) {
      LazyStyle lazyStyle1;
      if (lazyStyle1.matches(paramJComponent) && (lazyStyle == null || lazyStyle.parts.length < lazyStyle1.parts.length || (lazyStyle.parts.length == lazyStyle1.parts.length && lazyStyle.simple && !lazyStyle1.simple)))
        lazyStyle = lazyStyle1; 
    } 
    return (lazyStyle == null) ? this.defaultStyle : lazyStyle.getStyle(paramJComponent, paramRegion);
  }
  
  public void clearOverridesCache(JComponent paramJComponent) { this.overridesCache.remove(paramJComponent); }
  
  private void addColor(UIDefaults paramUIDefaults, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    ColorUIResource colorUIResource = new ColorUIResource(new Color(paramInt1, paramInt2, paramInt3, paramInt4));
    this.colorTree.addColor(paramString, colorUIResource);
    paramUIDefaults.put(paramString, colorUIResource);
  }
  
  private void addColor(UIDefaults paramUIDefaults, String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt) { addColor(paramUIDefaults, paramString1, paramString2, paramFloat1, paramFloat2, paramFloat3, paramInt, true); }
  
  private void addColor(UIDefaults paramUIDefaults, String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, boolean paramBoolean) {
    DerivedColor derivedColor = getDerivedColor(paramString1, paramString2, paramFloat1, paramFloat2, paramFloat3, paramInt, paramBoolean);
    paramUIDefaults.put(paramString1, derivedColor);
  }
  
  public DerivedColor getDerivedColor(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, boolean paramBoolean) { return getDerivedColor(null, paramString, paramFloat1, paramFloat2, paramFloat3, paramInt, paramBoolean); }
  
  private DerivedColor getDerivedColor(String paramString1, String paramString2, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, boolean paramBoolean) {
    DerivedColor derivedColor;
    if (paramBoolean) {
      derivedColor = new DerivedColor.UIResource(paramString2, paramFloat1, paramFloat2, paramFloat3, paramInt);
    } else {
      derivedColor = new DerivedColor(paramString2, paramFloat1, paramFloat2, paramFloat3, paramInt);
    } 
    if (this.derivedColors.containsKey(derivedColor))
      return (DerivedColor)this.derivedColors.get(derivedColor); 
    this.derivedColors.put(derivedColor, derivedColor);
    derivedColor.rederiveColor();
    this.colorTree.addColor(paramString1, derivedColor);
    return derivedColor;
  }
  
  private class ColorTree implements PropertyChangeListener {
    private Node root = new Node(null, null);
    
    private Map<String, Node> nodes = new HashMap();
    
    private ColorTree() {}
    
    public Color getColor(String param1String) { return ((Node)this.nodes.get(param1String)).color; }
    
    public void addColor(String param1String, Color param1Color) {
      Node node1 = getParentNode(param1Color);
      Node node2 = new Node(param1Color, node1);
      node1.children.add(node2);
      if (param1String != null)
        this.nodes.put(param1String, node2); 
    }
    
    private Node getParentNode(Color param1Color) {
      Node node = this.root;
      if (param1Color instanceof DerivedColor) {
        String str = ((DerivedColor)param1Color).getUiDefaultParentName();
        Node node1 = (Node)this.nodes.get(str);
        if (node1 != null)
          node = node1; 
      } 
      return node;
    }
    
    public void update() { this.root.update(); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      Node node = (Node)this.nodes.get(str);
      if (node != null) {
        node.parent.children.remove(node);
        Color color = (Color)param1PropertyChangeEvent.getNewValue();
        Node node1 = getParentNode(color);
        node.set(color, node1);
        node1.children.add(node);
        node.update();
      } 
    }
    
    class Node {
      Color color;
      
      Node parent;
      
      List<Node> children = new LinkedList();
      
      Node(Color param2Color, Node param2Node) { set(param2Color, param2Node); }
      
      public void set(Color param2Color, Node param2Node) {
        this.color = param2Color;
        this.parent = param2Node;
      }
      
      public void update() {
        if (this.color instanceof DerivedColor)
          ((DerivedColor)this.color).rederiveColor(); 
        for (Node node : this.children)
          node.update(); 
      }
    }
  }
  
  private class DefaultsListener implements PropertyChangeListener {
    private DefaultsListener() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if ("lookAndFeel".equals(param1PropertyChangeEvent.getPropertyName()))
        NimbusDefaults.this.colorTree.update(); 
    }
  }
  
  static final class DerivedFont implements UIDefaults.ActiveValue {
    private float sizeOffset;
    
    private Boolean bold;
    
    private Boolean italic;
    
    private String parentKey;
    
    public DerivedFont(String param1String, float param1Float, Boolean param1Boolean1, Boolean param1Boolean2) {
      if (param1String == null)
        throw new IllegalArgumentException("You must specify a key"); 
      this.parentKey = param1String;
      this.sizeOffset = param1Float;
      this.bold = param1Boolean1;
      this.italic = param1Boolean2;
    }
    
    public Object createValue(UIDefaults param1UIDefaults) {
      Font font = param1UIDefaults.getFont(this.parentKey);
      if (font != null) {
        float f = Math.round(font.getSize2D() * this.sizeOffset);
        int i = font.getStyle();
        if (this.bold != null)
          if (this.bold.booleanValue()) {
            i |= 0x1;
          } else {
            i &= 0xFFFFFFFE;
          }  
        if (this.italic != null)
          if (this.italic.booleanValue()) {
            i |= 0x2;
          } else {
            i &= 0xFFFFFFFD;
          }  
        return font.deriveFont(i, f);
      } 
      return null;
    }
  }
  
  private static final class LazyPainter implements UIDefaults.LazyValue {
    private int which;
    
    private AbstractRegionPainter.PaintContext ctx;
    
    private String className;
    
    LazyPainter(String param1String, int param1Int, Insets param1Insets, Dimension param1Dimension, boolean param1Boolean) {
      if (param1String == null)
        throw new IllegalArgumentException("The className must be specified"); 
      this.className = param1String;
      this.which = param1Int;
      this.ctx = new AbstractRegionPainter.PaintContext(param1Insets, param1Dimension, param1Boolean);
    }
    
    LazyPainter(String param1String, int param1Int, Insets param1Insets, Dimension param1Dimension, boolean param1Boolean, AbstractRegionPainter.PaintContext.CacheMode param1CacheMode, double param1Double1, double param1Double2) {
      if (param1String == null)
        throw new IllegalArgumentException("The className must be specified"); 
      this.className = param1String;
      this.which = param1Int;
      this.ctx = new AbstractRegionPainter.PaintContext(param1Insets, param1Dimension, param1Boolean, param1CacheMode, param1Double1, param1Double2);
    }
    
    public Object createValue(UIDefaults param1UIDefaults) {
      try {
        Object object;
        if (param1UIDefaults == null || !(object = param1UIDefaults.get("ClassLoader") instanceof ClassLoader)) {
          object = Thread.currentThread().getContextClassLoader();
          if (object == null)
            object = ClassLoader.getSystemClassLoader(); 
        } 
        Class clazz = Class.forName(this.className, true, (ClassLoader)object);
        Constructor constructor = clazz.getConstructor(new Class[] { AbstractRegionPainter.PaintContext.class, int.class });
        if (constructor == null)
          throw new NullPointerException("Failed to find the constructor for the class: " + this.className); 
        return constructor.newInstance(new Object[] { this.ctx, Integer.valueOf(this.which) });
      } catch (Exception exception) {
        exception.printStackTrace();
        return null;
      } 
    }
  }
  
  private final class LazyStyle {
    private String prefix;
    
    private boolean simple = true;
    
    private Part[] parts;
    
    private NimbusStyle style;
    
    private LazyStyle(String param1String) {
      if (param1String == null)
        throw new IllegalArgumentException("The prefix must not be null"); 
      this.prefix = param1String;
      String str = param1String;
      if (str.endsWith("cellRenderer\"") || str.endsWith("renderer\"") || str.endsWith("listRenderer\""))
        str = str.substring(str.lastIndexOf(":\"") + 1); 
      List list = split(str);
      this.parts = new Part[list.size()];
      for (byte b = 0; b < this.parts.length; b++) {
        if ((this.parts[b]).named)
          this.simple = false; 
      } 
    }
    
    SynthStyle getStyle(JComponent param1JComponent, Region param1Region) {
      if (param1JComponent.getClientProperty("Nimbus.Overrides") != null) {
        Map map = (Map)NimbusDefaults.this.overridesCache.get(param1JComponent);
        SynthStyle synthStyle = null;
        if (map == null) {
          map = new HashMap();
          NimbusDefaults.this.overridesCache.put(param1JComponent, map);
        } else {
          synthStyle = (SynthStyle)map.get(param1Region);
        } 
        if (synthStyle == null) {
          synthStyle = new NimbusStyle(this.prefix, param1JComponent);
          map.put(param1Region, synthStyle);
        } 
        return synthStyle;
      } 
      if (this.style == null)
        this.style = new NimbusStyle(this.prefix, null); 
      return this.style;
    }
    
    boolean matches(JComponent param1JComponent) { return matches(param1JComponent, this.parts.length - 1); }
    
    private boolean matches(Component param1Component, int param1Int) {
      if (param1Int < 0)
        return true; 
      if (param1Component == null)
        return false; 
      String str = param1Component.getName();
      if ((this.parts[param1Int]).named && (this.parts[param1Int]).s.equals(str))
        return matches(param1Component.getParent(), param1Int - 1); 
      if (!(this.parts[param1Int]).named) {
        Class clazz = (this.parts[param1Int]).c;
        if (clazz != null && clazz.isAssignableFrom(param1Component.getClass()))
          return matches(param1Component.getParent(), param1Int - 1); 
        if (clazz == null && NimbusDefaults.this.registeredRegions.containsKey((this.parts[param1Int]).s)) {
          Region region = (Region)NimbusDefaults.this.registeredRegions.get((this.parts[param1Int]).s);
          Component component = region.isSubregion() ? param1Component : param1Component.getParent();
          if (region == Region.INTERNAL_FRAME_TITLE_PANE && component != null && component instanceof JInternalFrame.JDesktopIcon) {
            JInternalFrame.JDesktopIcon jDesktopIcon = (JInternalFrame.JDesktopIcon)component;
            component = jDesktopIcon.getInternalFrame();
          } 
          return matches(component, param1Int - 1);
        } 
      } 
      return false;
    }
    
    private List<String> split(String param1String) {
      ArrayList arrayList = new ArrayList();
      byte b1 = 0;
      boolean bool = false;
      byte b2 = 0;
      for (byte b3 = 0; b3 < param1String.length(); b3++) {
        char c = param1String.charAt(b3);
        if (c == '[') {
          b1++;
        } else if (c == '"') {
          bool = !bool ? 1 : 0;
        } else if (c == ']') {
          if (--b1 < 0)
            throw new RuntimeException("Malformed prefix: " + param1String); 
        } else if (c == ':' && !bool && b1 == 0) {
          arrayList.add(param1String.substring(b2, b3));
          b2 = b3 + 1;
        } 
      } 
      if (b2 < param1String.length() - 1 && !bool && b1 == 0)
        arrayList.add(param1String.substring(b2)); 
      return arrayList;
    }
    
    private final class Part {
      private String s;
      
      private boolean named;
      
      private Class c;
      
      Part(String param2String) {
        this.named = (param2String.charAt(0) == '"' && param2String.charAt(param2String.length() - 1) == '"');
        if (this.named) {
          this.s = param2String.substring(1, param2String.length() - 1);
        } else {
          this.s = param2String;
          try {
            this.c = Class.forName("javax.swing.J" + param2String);
          } catch (Exception exception) {}
          try {
            this.c = Class.forName(param2String.replace("_", "."));
          } catch (Exception exception) {}
        } 
      }
    }
  }
  
  private static final class PainterBorder implements Border, UIResource {
    private Insets insets;
    
    private Painter painter;
    
    private String painterKey;
    
    PainterBorder(String param1String, Insets param1Insets) {
      this.insets = param1Insets;
      this.painterKey = param1String;
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (this.painter == null) {
        this.painter = (Painter)UIManager.get(this.painterKey);
        if (this.painter == null)
          return; 
      } 
      param1Graphics.translate(param1Int1, param1Int2);
      if (param1Graphics instanceof Graphics2D) {
        this.painter.paint((Graphics2D)param1Graphics, param1Component, param1Int3, param1Int4);
      } else {
        BufferedImage bufferedImage = new BufferedImage(param1Int3, param1Int4, 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        this.painter.paint(graphics2D, param1Component, param1Int3, param1Int4);
        graphics2D.dispose();
        param1Graphics.drawImage(bufferedImage, param1Int1, param1Int2, null);
        bufferedImage = null;
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component) { return (Insets)this.insets.clone(); }
    
    public boolean isBorderOpaque() { return false; }
  }
  
  private final class Part {
    private String s;
    
    private boolean named;
    
    private Class c;
    
    Part(String param1String) {
      this.named = (param1String.charAt(0) == '"' && param1String.charAt(param1String.length() - 1) == '"');
      if (this.named) {
        this.s = param1String.substring(1, param1String.length() - 1);
      } else {
        this.s = param1String;
        try {
          this.c = Class.forName("javax.swing.J" + param1String);
        } catch (Exception exception) {}
        try {
          this.c = Class.forName(param1String.replace("_", "."));
        } catch (Exception exception) {}
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\NimbusDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */