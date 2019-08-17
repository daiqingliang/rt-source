package sun.swing.plaf.synth;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;

public abstract class SynthFileChooserUI extends BasicFileChooserUI implements SynthUI {
  private JButton approveButton;
  
  private JButton cancelButton;
  
  private SynthStyle style;
  
  private Action fileNameCompletionAction = new FileNameCompletionAction();
  
  private FileFilter actualFileFilter = null;
  
  private GlobFilter globFilter = null;
  
  private String fileNameCompletionString;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthFileChooserUIImpl((JFileChooser)paramJComponent); }
  
  public SynthFileChooserUI(JFileChooser paramJFileChooser) { super(paramJFileChooser); }
  
  public SynthContext getContext(JComponent paramJComponent) { return new SynthContext(paramJComponent, Region.FILE_CHOOSER, this.style, getComponentState(paramJComponent)); }
  
  protected SynthContext getContext(JComponent paramJComponent, int paramInt) {
    Region region = SynthLookAndFeel.getRegion(paramJComponent);
    return new SynthContext(paramJComponent, Region.FILE_CHOOSER, this.style, paramInt);
  }
  
  private Region getRegion(JComponent paramJComponent) { return SynthLookAndFeel.getRegion(paramJComponent); }
  
  private int getComponentState(JComponent paramJComponent) { return paramJComponent.isEnabled() ? (paramJComponent.isFocusOwner() ? 257 : 1) : 8; }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthStyle synthStyle = SynthLookAndFeel.getStyleFactory().getStyle(paramJComponent, Region.FILE_CHOOSER);
    if (synthStyle != this.style) {
      if (this.style != null)
        this.style.uninstallDefaults(getContext(paramJComponent, 1)); 
      this.style = synthStyle;
      SynthContext synthContext = getContext(paramJComponent, 1);
      this.style.installDefaults(synthContext);
      Border border = paramJComponent.getBorder();
      if (border == null || border instanceof UIResource)
        paramJComponent.setBorder(new UIBorder(this.style.getInsets(synthContext, null))); 
      this.directoryIcon = this.style.getIcon(synthContext, "FileView.directoryIcon");
      this.fileIcon = this.style.getIcon(synthContext, "FileView.fileIcon");
      this.computerIcon = this.style.getIcon(synthContext, "FileView.computerIcon");
      this.hardDriveIcon = this.style.getIcon(synthContext, "FileView.hardDriveIcon");
      this.floppyDriveIcon = this.style.getIcon(synthContext, "FileView.floppyDriveIcon");
      this.newFolderIcon = this.style.getIcon(synthContext, "FileChooser.newFolderIcon");
      this.upFolderIcon = this.style.getIcon(synthContext, "FileChooser.upFolderIcon");
      this.homeFolderIcon = this.style.getIcon(synthContext, "FileChooser.homeFolderIcon");
      this.detailsViewIcon = this.style.getIcon(synthContext, "FileChooser.detailsViewIcon");
      this.listViewIcon = this.style.getIcon(synthContext, "FileChooser.listViewIcon");
    } 
  }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    SwingUtilities.replaceUIActionMap(paramJComponent, createActionMap());
  }
  
  public void installComponents(JFileChooser paramJFileChooser) {
    SynthContext synthContext = getContext(paramJFileChooser, 1);
    this.cancelButton = new JButton(this.cancelButtonText);
    this.cancelButton.setName("SynthFileChooser.cancelButton");
    this.cancelButton.setIcon(synthContext.getStyle().getIcon(synthContext, "FileChooser.cancelIcon"));
    this.cancelButton.setMnemonic(this.cancelButtonMnemonic);
    this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
    this.cancelButton.addActionListener(getCancelSelectionAction());
    this.approveButton = new JButton(getApproveButtonText(paramJFileChooser));
    this.approveButton.setName("SynthFileChooser.approveButton");
    this.approveButton.setIcon(synthContext.getStyle().getIcon(synthContext, "FileChooser.okIcon"));
    this.approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
    this.approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    this.approveButton.addActionListener(getApproveSelectionAction());
  }
  
  public void uninstallComponents(JFileChooser paramJFileChooser) { paramJFileChooser.removeAll(); }
  
  protected void installListeners(JFileChooser paramJFileChooser) {
    super.installListeners(paramJFileChooser);
    getModel().addListDataListener(new ListDataListener() {
          public void contentsChanged(ListDataEvent param1ListDataEvent) { new SynthFileChooserUI.DelayedSelectionUpdater(SynthFileChooserUI.this); }
          
          public void intervalAdded(ListDataEvent param1ListDataEvent) { new SynthFileChooserUI.DelayedSelectionUpdater(SynthFileChooserUI.this); }
          
          public void intervalRemoved(ListDataEvent param1ListDataEvent) {}
        });
  }
  
  protected abstract ActionMap createActionMap();
  
  protected void installDefaults(JFileChooser paramJFileChooser) {
    super.installDefaults(paramJFileChooser);
    updateStyle(paramJFileChooser);
  }
  
  protected void uninstallDefaults(JFileChooser paramJFileChooser) {
    super.uninstallDefaults(paramJFileChooser);
    SynthContext synthContext = getContext(getFileChooser(), 1);
    this.style.uninstallDefaults(synthContext);
    this.style = null;
  }
  
  protected void installIcons(JFileChooser paramJFileChooser) {}
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    if (paramJComponent.isOpaque()) {
      paramGraphics.setColor(this.style.getColor(synthContext, ColorType.BACKGROUND));
      paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    } 
    this.style.getPainter(synthContext).paintFileChooserBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public abstract void setFileName(String paramString);
  
  public abstract String getFileName();
  
  protected void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doMultiSelectionChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (!getFileChooser().isMultiSelectionEnabled())
      getFileChooser().setSelectedFiles(null); 
  }
  
  protected void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (getFileChooser().getControlButtonsAreShown()) {
      this.approveButton.setText(getApproveButtonText(getFileChooser()));
      this.approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
      this.approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
    } 
  }
  
  protected void doAncestorChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser) { return new SynthFCPropertyChangeListener(null); }
  
  private void updateFileNameCompletion() {
    if (this.fileNameCompletionString != null && this.fileNameCompletionString.equals(getFileName())) {
      File[] arrayOfFile = (File[])getModel().getFiles().toArray(new File[0]);
      String str = getCommonStartString(arrayOfFile);
      if (str != null && str.startsWith(this.fileNameCompletionString))
        setFileName(str); 
      this.fileNameCompletionString = null;
    } 
  }
  
  private String getCommonStartString(File[] paramArrayOfFile) {
    String str1 = null;
    String str2 = null;
    byte b = 0;
    if (paramArrayOfFile.length == 0)
      return null; 
    while (true) {
      for (byte b1 = 0; b1 < paramArrayOfFile.length; b1++) {
        String str = paramArrayOfFile[b1].getName();
        if (!b1) {
          if (str.length() == b)
            return str1; 
          str2 = str.substring(0, b + true);
        } 
        if (!str.startsWith(str2))
          return str1; 
      } 
      str1 = str2;
      b++;
    } 
  }
  
  private void resetGlobFilter() {
    if (this.actualFileFilter != null) {
      JFileChooser jFileChooser = getFileChooser();
      FileFilter fileFilter = jFileChooser.getFileFilter();
      if (fileFilter != null && fileFilter.equals(this.globFilter)) {
        jFileChooser.setFileFilter(this.actualFileFilter);
        jFileChooser.removeChoosableFileFilter(this.globFilter);
      } 
      this.actualFileFilter = null;
    } 
  }
  
  private static boolean isGlobPattern(String paramString) { return ((File.separatorChar == '\\' && paramString.indexOf('*') >= 0) || (File.separatorChar == '/' && (paramString.indexOf('*') >= 0 || paramString.indexOf('?') >= 0 || paramString.indexOf('[') >= 0))); }
  
  public Action getFileNameCompletionAction() { return this.fileNameCompletionAction; }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser) { return this.approveButton; }
  
  protected JButton getCancelButton(JFileChooser paramJFileChooser) { return this.cancelButton; }
  
  public void clearIconCache() {}
  
  private class DelayedSelectionUpdater implements Runnable {
    DelayedSelectionUpdater() { SwingUtilities.invokeLater(this); }
    
    public void run() { SynthFileChooserUI.this.updateFileNameCompletion(); }
  }
  
  private class FileNameCompletionAction extends AbstractAction {
    protected FileNameCompletionAction() { super("fileNameCompletion"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JFileChooser jFileChooser = SynthFileChooserUI.this.getFileChooser();
      String str = SynthFileChooserUI.this.getFileName();
      if (str != null)
        str = str.trim(); 
      SynthFileChooserUI.this.resetGlobFilter();
      if (str == null || str.equals("") || (jFileChooser.isMultiSelectionEnabled() && str.startsWith("\"")))
        return; 
      FileFilter fileFilter = jFileChooser.getFileFilter();
      if (SynthFileChooserUI.this.globFilter == null)
        SynthFileChooserUI.this.globFilter = new SynthFileChooserUI.GlobFilter(SynthFileChooserUI.this); 
      try {
        SynthFileChooserUI.this.globFilter.setPattern(!SynthFileChooserUI.isGlobPattern(str) ? (str + "*") : str);
        if (!(fileFilter instanceof SynthFileChooserUI.GlobFilter))
          SynthFileChooserUI.this.actualFileFilter = fileFilter; 
        jFileChooser.setFileFilter(null);
        jFileChooser.setFileFilter(SynthFileChooserUI.this.globFilter);
        SynthFileChooserUI.this.fileNameCompletionString = str;
      } catch (PatternSyntaxException patternSyntaxException) {}
    }
  }
  
  class GlobFilter extends FileFilter {
    Pattern pattern;
    
    String globPattern;
    
    public void setPattern(String param1String) {
      char[] arrayOfChar1 = param1String.toCharArray();
      char[] arrayOfChar2 = new char[arrayOfChar1.length * 2];
      boolean bool1 = (File.separatorChar == '\\') ? 1 : 0;
      boolean bool2 = false;
      byte b = 0;
      this.globPattern = param1String;
      if (bool1) {
        int i = arrayOfChar1.length;
        if (param1String.endsWith("*.*"))
          i -= 2; 
        for (byte b1 = 0; b1 < i; b1++) {
          if (arrayOfChar1[b1] == '*')
            arrayOfChar2[b++] = '.'; 
          arrayOfChar2[b++] = arrayOfChar1[b1];
        } 
      } else {
        for (byte b1 = 0; b1 < arrayOfChar1.length; b1++) {
          switch (arrayOfChar1[b1]) {
            case '*':
              if (!bool2)
                arrayOfChar2[b++] = '.'; 
              arrayOfChar2[b++] = '*';
              break;
            case '?':
              arrayOfChar2[b++] = bool2 ? '?' : '.';
              break;
            case '[':
              bool2 = true;
              arrayOfChar2[b++] = arrayOfChar1[b1];
              if (b1 < arrayOfChar1.length - 1)
                switch (arrayOfChar1[b1 + true]) {
                  case '!':
                  case '^':
                    arrayOfChar2[b++] = '^';
                    b1++;
                    break;
                  case ']':
                    arrayOfChar2[b++] = arrayOfChar1[++b1];
                    break;
                }  
              break;
            case ']':
              arrayOfChar2[b++] = arrayOfChar1[b1];
              bool2 = false;
              break;
            case '\\':
              if (b1 == 0 && arrayOfChar1.length > 1 && arrayOfChar1[1] == '~') {
                arrayOfChar2[b++] = arrayOfChar1[++b1];
                break;
              } 
              arrayOfChar2[b++] = '\\';
              if (b1 < arrayOfChar1.length - 1 && "*?[]".indexOf(arrayOfChar1[b1 + 1]) >= 0) {
                arrayOfChar2[b++] = arrayOfChar1[++b1];
                break;
              } 
              arrayOfChar2[b++] = '\\';
              break;
            default:
              if (!Character.isLetterOrDigit(arrayOfChar1[b1]))
                arrayOfChar2[b++] = '\\'; 
              arrayOfChar2[b++] = arrayOfChar1[b1];
              break;
          } 
        } 
      } 
      this.pattern = Pattern.compile(new String(arrayOfChar2, 0, b), 2);
    }
    
    public boolean accept(File param1File) { return (param1File == null) ? false : (param1File.isDirectory() ? true : this.pattern.matcher(param1File.getName()).matches()); }
    
    public String getDescription() { return this.globPattern; }
  }
  
  private class SynthFCPropertyChangeListener implements PropertyChangeListener {
    private SynthFCPropertyChangeListener() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str.equals("fileSelectionChanged")) {
        SynthFileChooserUI.this.doFileSelectionModeChanged(param1PropertyChangeEvent);
      } else if (str.equals("SelectedFileChangedProperty")) {
        SynthFileChooserUI.this.doSelectedFileChanged(param1PropertyChangeEvent);
      } else if (str.equals("SelectedFilesChangedProperty")) {
        SynthFileChooserUI.this.doSelectedFilesChanged(param1PropertyChangeEvent);
      } else if (str.equals("directoryChanged")) {
        SynthFileChooserUI.this.doDirectoryChanged(param1PropertyChangeEvent);
      } else if (str == "MultiSelectionEnabledChangedProperty") {
        SynthFileChooserUI.this.doMultiSelectionChanged(param1PropertyChangeEvent);
      } else if (str == "AccessoryChangedProperty") {
        SynthFileChooserUI.this.doAccessoryChanged(param1PropertyChangeEvent);
      } else if (str == "ApproveButtonTextChangedProperty" || str == "ApproveButtonToolTipTextChangedProperty" || str == "DialogTypeChangedProperty" || str == "ControlButtonsAreShownChangedProperty") {
        SynthFileChooserUI.this.doControlButtonsChanged(param1PropertyChangeEvent);
      } else if (str.equals("componentOrientation")) {
        ComponentOrientation componentOrientation = (ComponentOrientation)param1PropertyChangeEvent.getNewValue();
        JFileChooser jFileChooser = (JFileChooser)param1PropertyChangeEvent.getSource();
        if (componentOrientation != (ComponentOrientation)param1PropertyChangeEvent.getOldValue())
          jFileChooser.applyComponentOrientation(componentOrientation); 
      } else if (str.equals("ancestor")) {
        SynthFileChooserUI.this.doAncestorChanged(param1PropertyChangeEvent);
      } 
    }
  }
  
  private class UIBorder extends AbstractBorder implements UIResource {
    private Insets _insets;
    
    UIBorder(Insets param1Insets) {
      if (param1Insets != null) {
        this._insets = new Insets(param1Insets.top, param1Insets.left, param1Insets.bottom, param1Insets.right);
      } else {
        this._insets = null;
      } 
    }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!(param1Component instanceof JComponent))
        return; 
      JComponent jComponent = (JComponent)param1Component;
      SynthContext synthContext = SynthFileChooserUI.this.getContext(jComponent);
      SynthStyle synthStyle = synthContext.getStyle();
      if (synthStyle != null)
        synthStyle.getPainter(synthContext).paintFileChooserBorder(synthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      if (param1Insets == null)
        param1Insets = new Insets(0, 0, 0, 0); 
      if (this._insets != null) {
        param1Insets.top = this._insets.top;
        param1Insets.bottom = this._insets.bottom;
        param1Insets.left = this._insets.left;
        param1Insets.right = this._insets.right;
      } else {
        param1Insets.top = param1Insets.bottom = param1Insets.right = param1Insets.left = 0;
      } 
      return param1Insets;
    }
    
    public boolean isBorderOpaque() { return false; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\plaf\synth\SynthFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */