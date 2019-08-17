package sun.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultRowSorter;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Position;
import sun.awt.AWTAccessor;
import sun.awt.shell.ShellFolder;
import sun.awt.shell.ShellFolderColumnInfo;

public class FilePane extends JPanel implements PropertyChangeListener {
  public static final String ACTION_APPROVE_SELECTION = "approveSelection";
  
  public static final String ACTION_CANCEL = "cancelSelection";
  
  public static final String ACTION_EDIT_FILE_NAME = "editFileName";
  
  public static final String ACTION_REFRESH = "refresh";
  
  public static final String ACTION_CHANGE_TO_PARENT_DIRECTORY = "Go Up";
  
  public static final String ACTION_NEW_FOLDER = "New Folder";
  
  public static final String ACTION_VIEW_LIST = "viewTypeList";
  
  public static final String ACTION_VIEW_DETAILS = "viewTypeDetails";
  
  private Action[] actions;
  
  public static final int VIEWTYPE_LIST = 0;
  
  public static final int VIEWTYPE_DETAILS = 1;
  
  private static final int VIEWTYPE_COUNT = 2;
  
  private int viewType = -1;
  
  private JPanel[] viewPanels = new JPanel[2];
  
  private JPanel currentViewPanel;
  
  private String[] viewTypeActionNames;
  
  private String filesListAccessibleName = null;
  
  private String filesDetailsAccessibleName = null;
  
  private JPopupMenu contextMenu;
  
  private JMenu viewMenu;
  
  private String viewMenuLabelText;
  
  private String refreshActionLabelText;
  
  private String newFolderActionLabelText;
  
  private String kiloByteString;
  
  private String megaByteString;
  
  private String gigaByteString;
  
  private String renameErrorTitleText;
  
  private String renameErrorText;
  
  private String renameErrorFileExistsText;
  
  private static final Cursor waitCursor = Cursor.getPredefinedCursor(3);
  
  private final KeyListener detailsKeyListener = new KeyAdapter() {
      private final long timeFactor;
      
      private final StringBuilder typedString = new StringBuilder();
      
      private long lastTime = 1000L;
      
      public void keyTyped(KeyEvent param1KeyEvent) {
        BasicDirectoryModel basicDirectoryModel = FilePane.this.getModel();
        int i = basicDirectoryModel.getSize();
        if (FilePane.this.detailsTable == null || i == 0 || param1KeyEvent.isAltDown() || param1KeyEvent.isControlDown() || param1KeyEvent.isMetaDown())
          return; 
        InputMap inputMap = FilePane.this.detailsTable.getInputMap(1);
        KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(param1KeyEvent);
        if (inputMap != null && inputMap.get(keyStroke) != null)
          return; 
        int j = FilePane.this.detailsTable.getSelectionModel().getLeadSelectionIndex();
        if (j < 0)
          j = 0; 
        if (j >= i)
          j = i - 1; 
        char c = param1KeyEvent.getKeyChar();
        long l = param1KeyEvent.getWhen();
        if (l - this.lastTime < this.timeFactor) {
          if (this.typedString.length() == 1 && this.typedString.charAt(0) == c) {
            j++;
          } else {
            this.typedString.append(c);
          } 
        } else {
          j++;
          this.typedString.setLength(0);
          this.typedString.append(c);
        } 
        this.lastTime = l;
        if (j >= i)
          j = 0; 
        int k = getNextMatch(j, i - 1);
        if (k < 0 && j > 0)
          k = getNextMatch(0, j - 1); 
        if (k >= 0) {
          FilePane.this.detailsTable.getSelectionModel().setSelectionInterval(k, k);
          Rectangle rectangle = FilePane.this.detailsTable.getCellRect(k, FilePane.this.detailsTable.convertColumnIndexToView(0), false);
          FilePane.this.detailsTable.scrollRectToVisible(rectangle);
        } 
      }
      
      private int getNextMatch(int param1Int1, int param1Int2) {
        BasicDirectoryModel basicDirectoryModel = FilePane.this.getModel();
        JFileChooser jFileChooser = FilePane.this.getFileChooser();
        FilePane.DetailsTableRowSorter detailsTableRowSorter = FilePane.this.getRowSorter();
        String str = this.typedString.toString().toLowerCase();
        for (int i = param1Int1; i <= param1Int2; i++) {
          File file = (File)basicDirectoryModel.getElementAt(detailsTableRowSorter.convertRowIndexToModel(i));
          String str1 = jFileChooser.getName(file).toLowerCase();
          if (str1.startsWith(str))
            return i; 
        } 
        return -1;
      }
    };
  
  private FocusListener editorFocusListener = new FocusAdapter() {
      public void focusLost(FocusEvent param1FocusEvent) {
        if (!param1FocusEvent.isTemporary())
          FilePane.this.applyEdit(); 
      }
    };
  
  private static FocusListener repaintListener = new FocusListener() {
      public void focusGained(FocusEvent param1FocusEvent) { repaintSelection(param1FocusEvent.getSource()); }
      
      public void focusLost(FocusEvent param1FocusEvent) { repaintSelection(param1FocusEvent.getSource()); }
      
      private void repaintSelection(Object param1Object) {
        if (param1Object instanceof JList) {
          repaintListSelection((JList)param1Object);
        } else if (param1Object instanceof JTable) {
          repaintTableSelection((JTable)param1Object);
        } 
      }
      
      private void repaintListSelection(JList param1JList) {
        int[] arrayOfInt = param1JList.getSelectedIndices();
        for (int i : arrayOfInt) {
          Rectangle rectangle = param1JList.getCellBounds(i, i);
          param1JList.repaint(rectangle);
        } 
      }
      
      private void repaintTableSelection(JTable param1JTable) {
        int i = param1JTable.getSelectionModel().getMinSelectionIndex();
        int j = param1JTable.getSelectionModel().getMaxSelectionIndex();
        if (i == -1 || j == -1)
          return; 
        int k = param1JTable.convertColumnIndexToView(0);
        Rectangle rectangle1 = param1JTable.getCellRect(i, k, false);
        Rectangle rectangle2 = param1JTable.getCellRect(j, k, false);
        Rectangle rectangle3 = rectangle1.union(rectangle2);
        param1JTable.repaint(rectangle3);
      }
    };
  
  private boolean smallIconsView = false;
  
  private Border listViewBorder;
  
  private Color listViewBackground;
  
  private boolean listViewWindowsStyle;
  
  private boolean readOnly;
  
  private boolean fullRowSelection = false;
  
  private ListSelectionModel listSelectionModel;
  
  private JList list;
  
  private JTable detailsTable;
  
  private static final int COLUMN_FILENAME = 0;
  
  private File newFolderFile;
  
  private FileChooserUIAccessor fileChooserUIAccessor;
  
  private DetailsTableModel detailsTableModel;
  
  private DetailsTableRowSorter rowSorter;
  
  private DetailsTableCellEditor tableCellEditor;
  
  int lastIndex = -1;
  
  File editFile = null;
  
  JTextField editCell = null;
  
  protected Action newFolderAction;
  
  private Handler handler;
  
  public FilePane(FileChooserUIAccessor paramFileChooserUIAccessor) {
    super(new BorderLayout());
    this.fileChooserUIAccessor = paramFileChooserUIAccessor;
    installDefaults();
    createActionMap();
  }
  
  public void uninstallUI() {
    if (getModel() != null)
      getModel().removePropertyChangeListener(this); 
  }
  
  protected JFileChooser getFileChooser() { return this.fileChooserUIAccessor.getFileChooser(); }
  
  protected BasicDirectoryModel getModel() { return this.fileChooserUIAccessor.getModel(); }
  
  public int getViewType() { return this.viewType; }
  
  public void setViewType(int paramInt) {
    JTable jTable;
    if (paramInt == this.viewType)
      return; 
    int i = this.viewType;
    this.viewType = paramInt;
    JPanel jPanel = null;
    JList jList = null;
    switch (paramInt) {
      case 0:
        if (this.viewPanels[paramInt] == null) {
          jPanel = this.fileChooserUIAccessor.createList();
          if (jPanel == null)
            jPanel = createList(); 
          this.list = (JList)findChildComponent(jPanel, JList.class);
          if (this.listSelectionModel == null) {
            this.listSelectionModel = this.list.getSelectionModel();
            if (this.detailsTable != null)
              this.detailsTable.setSelectionModel(this.listSelectionModel); 
          } else {
            this.list.setSelectionModel(this.listSelectionModel);
          } 
        } 
        this.list.setLayoutOrientation(1);
        jList = this.list;
        break;
      case 1:
        if (this.viewPanels[paramInt] == null) {
          jPanel = this.fileChooserUIAccessor.createDetailsView();
          if (jPanel == null)
            jPanel = createDetailsView(); 
          this.detailsTable = (JTable)findChildComponent(jPanel, JTable.class);
          this.detailsTable.setRowHeight(Math.max(this.detailsTable.getFont().getSize() + 4, 17));
          if (this.listSelectionModel != null)
            this.detailsTable.setSelectionModel(this.listSelectionModel); 
        } 
        jTable = this.detailsTable;
        break;
    } 
    if (jPanel != null) {
      this.viewPanels[paramInt] = jPanel;
      recursivelySetInheritsPopupMenu(jPanel, true);
    } 
    boolean bool = false;
    if (this.currentViewPanel != null) {
      Component component = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
      bool = (component == this.detailsTable || component == this.list) ? 1 : 0;
      remove(this.currentViewPanel);
    } 
    this.currentViewPanel = this.viewPanels[paramInt];
    add(this.currentViewPanel, "Center");
    if (bool && jTable != null)
      jTable.requestFocusInWindow(); 
    revalidate();
    repaint();
    updateViewMenu();
    firePropertyChange("viewType", i, paramInt);
  }
  
  public Action getViewTypeAction(int paramInt) { return new ViewTypeAction(this, paramInt); }
  
  private static void recursivelySetInheritsPopupMenu(Container paramContainer, boolean paramBoolean) {
    if (paramContainer instanceof JComponent)
      ((JComponent)paramContainer).setInheritsPopupMenu(paramBoolean); 
    int i = paramContainer.getComponentCount();
    for (byte b = 0; b < i; b++)
      recursivelySetInheritsPopupMenu((Container)paramContainer.getComponent(b), paramBoolean); 
  }
  
  protected void installDefaults() {
    Locale locale = getFileChooser().getLocale();
    this.listViewBorder = UIManager.getBorder("FileChooser.listViewBorder");
    this.listViewBackground = UIManager.getColor("FileChooser.listViewBackground");
    this.listViewWindowsStyle = UIManager.getBoolean("FileChooser.listViewWindowsStyle");
    this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
    this.viewMenuLabelText = UIManager.getString("FileChooser.viewMenuLabelText", locale);
    this.refreshActionLabelText = UIManager.getString("FileChooser.refreshActionLabelText", locale);
    this.newFolderActionLabelText = UIManager.getString("FileChooser.newFolderActionLabelText", locale);
    this.viewTypeActionNames = new String[2];
    this.viewTypeActionNames[0] = UIManager.getString("FileChooser.listViewActionLabelText", locale);
    this.viewTypeActionNames[1] = UIManager.getString("FileChooser.detailsViewActionLabelText", locale);
    this.kiloByteString = UIManager.getString("FileChooser.fileSizeKiloBytes", locale);
    this.megaByteString = UIManager.getString("FileChooser.fileSizeMegaBytes", locale);
    this.gigaByteString = UIManager.getString("FileChooser.fileSizeGigaBytes", locale);
    this.fullRowSelection = UIManager.getBoolean("FileView.fullRowSelection");
    this.filesListAccessibleName = UIManager.getString("FileChooser.filesListAccessibleName", locale);
    this.filesDetailsAccessibleName = UIManager.getString("FileChooser.filesDetailsAccessibleName", locale);
    this.renameErrorTitleText = UIManager.getString("FileChooser.renameErrorTitleText", locale);
    this.renameErrorText = UIManager.getString("FileChooser.renameErrorText", locale);
    this.renameErrorFileExistsText = UIManager.getString("FileChooser.renameErrorFileExistsText", locale);
  }
  
  public Action[] getActions() {
    if (this.actions == null) {
      ArrayList arrayList = new ArrayList(8);
      class FilePaneAction extends AbstractAction {
        FilePaneAction(FilePane this$0, String param1String) { this(param1String, param1String); }
        
        FilePaneAction(String param1String1, String param1String2) {
          super(param1String1);
          putValue("ActionCommandKey", param1String2);
        }
        
        public void actionPerformed(ActionEvent param1ActionEvent) {
          String str = (String)getValue("ActionCommandKey");
          if (str == "cancelSelection") {
            if (FilePane.this.editFile != null) {
              FilePane.this.cancelEdit();
            } else {
              FilePane.this.getFileChooser().cancelSelection();
            } 
          } else if (str == "editFileName") {
            JFileChooser jFileChooser = FilePane.this.getFileChooser();
            int i = FilePane.this.listSelectionModel.getMinSelectionIndex();
            if (i >= 0 && FilePane.this.editFile == null && (!jFileChooser.isMultiSelectionEnabled() || jFileChooser.getSelectedFiles().length <= 1))
              FilePane.this.editFileName(i); 
          } else if (str == "refresh") {
            FilePane.this.getFileChooser().rescanCurrentDirectory();
          } 
        }
        
        public boolean isEnabled() {
          String str = (String)getValue("ActionCommandKey");
          return (str == "cancelSelection") ? FilePane.this.getFileChooser().isEnabled() : ((str == "editFileName") ? ((!FilePane.this.readOnly && FilePane.this.getFileChooser().isEnabled()) ? 1 : 0) : 1);
        }
      };
      arrayList.add(new FilePaneAction("cancelSelection"));
      arrayList.add(new FilePaneAction(this, "editFileName"));
      arrayList.add(new FilePaneAction(this, this.refreshActionLabelText, "refresh"));
      Action action = this.fileChooserUIAccessor.getApproveSelectionAction();
      if (action != null)
        arrayList.add(action); 
      action = this.fileChooserUIAccessor.getChangeToParentDirectoryAction();
      if (action != null)
        arrayList.add(action); 
      action = getNewFolderAction();
      if (action != null)
        arrayList.add(action); 
      action = getViewTypeAction(0);
      if (action != null)
        arrayList.add(action); 
      action = getViewTypeAction(1);
      if (action != null)
        arrayList.add(action); 
      this.actions = (Action[])arrayList.toArray(new Action[arrayList.size()]);
    } 
    return this.actions;
  }
  
  protected void createActionMap() { addActionsToMap(getActionMap(), getActions()); }
  
  public static void addActionsToMap(ActionMap paramActionMap, Action[] paramArrayOfAction) {
    if (paramActionMap != null && paramArrayOfAction != null)
      for (Action action : paramArrayOfAction) {
        String str = (String)action.getValue("ActionCommandKey");
        if (str == null)
          str = (String)action.getValue("Name"); 
        paramActionMap.put(str, action);
      }  
  }
  
  private void updateListRowCount(JList paramJList) {
    if (this.smallIconsView) {
      paramJList.setVisibleRowCount(getModel().getSize() / 3);
    } else {
      paramJList.setVisibleRowCount(-1);
    } 
  }
  
  public JPanel createList() {
    JPanel jPanel = new JPanel(new BorderLayout());
    final JFileChooser fileChooser = getFileChooser();
    final JList<Object> list = new JList<Object>() {
        public int getNextMatch(String param1String, int param1Int, Position.Bias param1Bias) {
          ListModel listModel = getModel();
          int i = listModel.getSize();
          if (param1String == null || param1Int < 0 || param1Int >= i)
            throw new IllegalArgumentException(); 
          boolean bool = (param1Bias == Position.Bias.Backward) ? 1 : 0;
          int j;
          for (j = param1Int; bool ? (j >= 0) : (j < i); j += (bool ? -1 : 1)) {
            String str = fileChooser.getName((File)listModel.getElementAt(j));
            if (str.regionMatches(true, 0, param1String, 0, param1String.length()))
              return j; 
          } 
          return -1;
        }
      };
    jList.setCellRenderer(new FileRenderer());
    jList.setLayoutOrientation(1);
    jList.putClientProperty("List.isFileList", Boolean.TRUE);
    if (this.listViewWindowsStyle)
      jList.addFocusListener(repaintListener); 
    updateListRowCount(jList);
    getModel().addListDataListener(new ListDataListener() {
          public void intervalAdded(ListDataEvent param1ListDataEvent) { FilePane.this.updateListRowCount(list); }
          
          public void intervalRemoved(ListDataEvent param1ListDataEvent) { FilePane.this.updateListRowCount(list); }
          
          public void contentsChanged(ListDataEvent param1ListDataEvent) {
            if (FilePane.this.isShowing())
              FilePane.this.clearSelection(); 
            FilePane.this.updateListRowCount(list);
          }
        });
    getModel().addPropertyChangeListener(this);
    if (jFileChooser.isMultiSelectionEnabled()) {
      jList.setSelectionMode(2);
    } else {
      jList.setSelectionMode(0);
    } 
    jList.setModel(new SortableListModel());
    jList.addListSelectionListener(createListSelectionListener());
    jList.addMouseListener(getMouseHandler());
    JScrollPane jScrollPane = new JScrollPane(jList);
    if (this.listViewBackground != null)
      jList.setBackground(this.listViewBackground); 
    if (this.listViewBorder != null)
      jScrollPane.setBorder(this.listViewBorder); 
    jList.putClientProperty("AccessibleName", this.filesListAccessibleName);
    jPanel.add(jScrollPane, "Center");
    return jPanel;
  }
  
  private DetailsTableModel getDetailsTableModel() {
    if (this.detailsTableModel == null)
      this.detailsTableModel = new DetailsTableModel(getFileChooser()); 
    return this.detailsTableModel;
  }
  
  private void updateDetailsColumnModel(JTable paramJTable) {
    if (paramJTable != null) {
      ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = this.detailsTableModel.getColumns();
      DefaultTableColumnModel defaultTableColumnModel = new DefaultTableColumnModel();
      for (byte b = 0; b < arrayOfShellFolderColumnInfo.length; b++) {
        ShellFolderColumnInfo shellFolderColumnInfo = arrayOfShellFolderColumnInfo[b];
        TableColumn tableColumn = new TableColumn(b);
        String str = shellFolderColumnInfo.getTitle();
        if (str != null && str.startsWith("FileChooser.") && str.endsWith("HeaderText")) {
          String str1 = UIManager.getString(str, paramJTable.getLocale());
          if (str1 != null)
            str = str1; 
        } 
        tableColumn.setHeaderValue(str);
        Integer integer = shellFolderColumnInfo.getWidth();
        if (integer != null)
          tableColumn.setPreferredWidth(integer.intValue()); 
        defaultTableColumnModel.addColumn(tableColumn);
      } 
      if (!this.readOnly && defaultTableColumnModel.getColumnCount() > 0)
        defaultTableColumnModel.getColumn(0).setCellEditor(getDetailsTableCellEditor()); 
      paramJTable.setColumnModel(defaultTableColumnModel);
    } 
  }
  
  private DetailsTableRowSorter getRowSorter() {
    if (this.rowSorter == null)
      this.rowSorter = new DetailsTableRowSorter(); 
    return this.rowSorter;
  }
  
  private DetailsTableCellEditor getDetailsTableCellEditor() {
    if (this.tableCellEditor == null)
      this.tableCellEditor = new DetailsTableCellEditor(new JTextField()); 
    return this.tableCellEditor;
  }
  
  public JPanel createDetailsView() {
    final JFileChooser chooser = getFileChooser();
    JPanel jPanel = new JPanel(new BorderLayout());
    JTable jTable = new JTable(getDetailsTableModel()) {
        protected boolean processKeyBinding(KeyStroke param1KeyStroke, KeyEvent param1KeyEvent, int param1Int, boolean param1Boolean) {
          if (param1KeyEvent.getKeyCode() == 27 && getCellEditor() == null) {
            chooser.dispatchEvent(param1KeyEvent);
            return true;
          } 
          return super.processKeyBinding(param1KeyStroke, param1KeyEvent, param1Int, param1Boolean);
        }
        
        public void tableChanged(TableModelEvent param1TableModelEvent) {
          super.tableChanged(param1TableModelEvent);
          if (param1TableModelEvent.getFirstRow() == -1)
            FilePane.this.updateDetailsColumnModel(this); 
        }
      };
    jTable.setRowSorter(getRowSorter());
    jTable.setAutoCreateColumnsFromModel(false);
    jTable.setComponentOrientation(jFileChooser.getComponentOrientation());
    jTable.setAutoResizeMode(0);
    jTable.setShowGrid(false);
    jTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
    jTable.addKeyListener(this.detailsKeyListener);
    Font font = this.list.getFont();
    jTable.setFont(font);
    jTable.setIntercellSpacing(new Dimension(0, 0));
    AlignableTableHeaderRenderer alignableTableHeaderRenderer = new AlignableTableHeaderRenderer(jTable.getTableHeader().getDefaultRenderer());
    jTable.getTableHeader().setDefaultRenderer(alignableTableHeaderRenderer);
    DetailsTableCellRenderer detailsTableCellRenderer = new DetailsTableCellRenderer(jFileChooser);
    jTable.setDefaultRenderer(Object.class, detailsTableCellRenderer);
    jTable.getColumnModel().getSelectionModel().setSelectionMode(0);
    jTable.addMouseListener(getMouseHandler());
    jTable.putClientProperty("Table.isFileList", Boolean.TRUE);
    if (this.listViewWindowsStyle)
      jTable.addFocusListener(repaintListener); 
    ActionMap actionMap = SwingUtilities.getUIActionMap(jTable);
    actionMap.remove("selectNextRowCell");
    actionMap.remove("selectPreviousRowCell");
    actionMap.remove("selectNextColumnCell");
    actionMap.remove("selectPreviousColumnCell");
    jTable.setFocusTraversalKeys(0, null);
    jTable.setFocusTraversalKeys(1, null);
    JScrollPane jScrollPane = new JScrollPane(jTable);
    jScrollPane.setComponentOrientation(jFileChooser.getComponentOrientation());
    LookAndFeel.installColors(jScrollPane.getViewport(), "Table.background", "Table.foreground");
    jScrollPane.addComponentListener(new ComponentAdapter() {
          public void componentResized(ComponentEvent param1ComponentEvent) {
            JScrollPane jScrollPane = (JScrollPane)param1ComponentEvent.getComponent();
            FilePane.this.fixNameColumnWidth((jScrollPane.getViewport().getSize()).width);
            jScrollPane.removeComponentListener(this);
          }
        });
    jScrollPane.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent param1MouseEvent) {
            JScrollPane jScrollPane = (JScrollPane)param1MouseEvent.getComponent();
            JTable jTable = (JTable)jScrollPane.getViewport().getView();
            if (!param1MouseEvent.isShiftDown() || jTable.getSelectionModel().getSelectionMode() == 0) {
              FilePane.this.clearSelection();
              TableCellEditor tableCellEditor = jTable.getCellEditor();
              if (tableCellEditor != null)
                tableCellEditor.stopCellEditing(); 
            } 
          }
        });
    jTable.setForeground(this.list.getForeground());
    jTable.setBackground(this.list.getBackground());
    if (this.listViewBorder != null)
      jScrollPane.setBorder(this.listViewBorder); 
    jPanel.add(jScrollPane, "Center");
    this.detailsTableModel.fireTableStructureChanged();
    jTable.putClientProperty("AccessibleName", this.filesDetailsAccessibleName);
    return jPanel;
  }
  
  private void fixNameColumnWidth(int paramInt) {
    TableColumn tableColumn = this.detailsTable.getColumnModel().getColumn(0);
    int i = (this.detailsTable.getPreferredSize()).width;
    if (i < paramInt)
      tableColumn.setPreferredWidth(tableColumn.getPreferredWidth() + paramInt - i); 
  }
  
  public ListSelectionListener createListSelectionListener() { return this.fileChooserUIAccessor.createListSelectionListener(); }
  
  private int getEditIndex() { return this.lastIndex; }
  
  private void setEditIndex(int paramInt) { this.lastIndex = paramInt; }
  
  private void resetEditIndex() { this.lastIndex = -1; }
  
  private void cancelEdit() {
    if (this.editFile != null) {
      this.editFile = null;
      this.list.remove(this.editCell);
      repaint();
    } else if (this.detailsTable != null && this.detailsTable.isEditing()) {
      this.detailsTable.getCellEditor().cancelCellEditing();
    } 
  }
  
  private void editFileName(int paramInt) {
    int i;
    Icon icon;
    ComponentOrientation componentOrientation;
    Rectangle rectangle;
    JFileChooser jFileChooser = getFileChooser();
    File file = jFileChooser.getCurrentDirectory();
    if (this.readOnly || !canWrite(file))
      return; 
    ensureIndexIsVisible(paramInt);
    switch (this.viewType) {
      case 0:
        this.editFile = (File)getModel().getElementAt(getRowSorter().convertRowIndexToModel(paramInt));
        rectangle = this.list.getCellBounds(paramInt, paramInt);
        if (this.editCell == null) {
          this.editCell = new JTextField();
          this.editCell.setName("Tree.cellEditor");
          this.editCell.addActionListener(new EditActionListener());
          this.editCell.addFocusListener(this.editorFocusListener);
          this.editCell.setNextFocusableComponent(this.list);
        } 
        this.list.add(this.editCell);
        this.editCell.setText(jFileChooser.getName(this.editFile));
        componentOrientation = this.list.getComponentOrientation();
        this.editCell.setComponentOrientation(componentOrientation);
        icon = jFileChooser.getIcon(this.editFile);
        i = (icon == null) ? 20 : (icon.getIconWidth() + 4);
        if (componentOrientation.isLeftToRight()) {
          this.editCell.setBounds(i + rectangle.x, rectangle.y, rectangle.width - i, rectangle.height);
        } else {
          this.editCell.setBounds(rectangle.x, rectangle.y, rectangle.width - i, rectangle.height);
        } 
        this.editCell.requestFocus();
        this.editCell.selectAll();
        break;
      case 1:
        this.detailsTable.editCellAt(paramInt, 0);
        break;
    } 
  }
  
  private void applyEdit() {
    if (this.editFile != null && this.editFile.exists()) {
      JFileChooser jFileChooser = getFileChooser();
      String str1 = jFileChooser.getName(this.editFile);
      String str2 = this.editFile.getName();
      String str3 = this.editCell.getText().trim();
      if (!str3.equals(str1)) {
        String str = str3;
        int i = str2.length();
        int j = str1.length();
        if (i > j && str2.charAt(j) == '.')
          str = str3 + str2.substring(j); 
        FileSystemView fileSystemView = jFileChooser.getFileSystemView();
        File file = fileSystemView.createFileObject(this.editFile.getParentFile(), str);
        if (file.exists()) {
          JOptionPane.showMessageDialog(jFileChooser, MessageFormat.format(this.renameErrorFileExistsText, new Object[] { str2 }), this.renameErrorTitleText, 0);
        } else if (getModel().renameFile(this.editFile, file)) {
          if (fileSystemView.isParent(jFileChooser.getCurrentDirectory(), file))
            if (jFileChooser.isMultiSelectionEnabled()) {
              jFileChooser.setSelectedFiles(new File[] { file });
            } else {
              jFileChooser.setSelectedFile(file);
            }  
        } else {
          JOptionPane.showMessageDialog(jFileChooser, MessageFormat.format(this.renameErrorText, new Object[] { str2 }), this.renameErrorTitleText, 0);
        } 
      } 
    } 
    if (this.detailsTable != null && this.detailsTable.isEditing())
      this.detailsTable.getCellEditor().stopCellEditing(); 
    cancelEdit();
  }
  
  public Action getNewFolderAction() {
    if (!this.readOnly && this.newFolderAction == null)
      this.newFolderAction = new AbstractAction(this.newFolderActionLabelText) {
          private Action basicNewFolderAction;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.basicNewFolderAction == null)
              this.basicNewFolderAction = FilePane.this.fileChooserUIAccessor.getNewFolderAction(); 
            JFileChooser jFileChooser = FilePane.this.getFileChooser();
            File file1 = jFileChooser.getSelectedFile();
            this.basicNewFolderAction.actionPerformed(param1ActionEvent);
            File file2 = jFileChooser.getSelectedFile();
            if (file2 != null && !file2.equals(file1) && file2.isDirectory())
              FilePane.this.newFolderFile = file2; 
          }
        }; 
    return this.newFolderAction;
  }
  
  void setFileSelected() {
    if (getFileChooser().isMultiSelectionEnabled() && !isDirectorySelected()) {
      File[] arrayOfFile = getFileChooser().getSelectedFiles();
      Object[] arrayOfObject = this.list.getSelectedValues();
      this.listSelectionModel.setValueIsAdjusting(true);
      try {
        int i = this.listSelectionModel.getLeadSelectionIndex();
        int j = this.listSelectionModel.getAnchorSelectionIndex();
        Arrays.sort(arrayOfFile);
        Arrays.sort(arrayOfObject);
        byte b1 = 0;
        byte b2;
        for (b2 = 0; b1 < arrayOfFile.length && b2 < arrayOfObject.length; b2++) {
          int k = arrayOfFile[b1].compareTo((File)arrayOfObject[b2]);
          if (k < 0) {
            doSelectFile(arrayOfFile[b1++]);
            continue;
          } 
          if (k > 0) {
            doDeselectFile(arrayOfObject[b2++]);
            continue;
          } 
          b1++;
        } 
        while (b1 < arrayOfFile.length)
          doSelectFile(arrayOfFile[b1++]); 
        while (b2 < arrayOfObject.length)
          doDeselectFile(arrayOfObject[b2++]); 
        if (this.listSelectionModel instanceof DefaultListSelectionModel) {
          ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(i);
          this.listSelectionModel.setAnchorSelectionIndex(j);
        } 
      } finally {
        this.listSelectionModel.setValueIsAdjusting(false);
      } 
    } else {
      File file;
      JFileChooser jFileChooser = getFileChooser();
      if (isDirectorySelected()) {
        file = getDirectory();
      } else {
        file = jFileChooser.getSelectedFile();
      } 
      int i;
      if (file != null && (i = getModel().indexOf(file)) >= 0) {
        int j = getRowSorter().convertRowIndexToView(i);
        this.listSelectionModel.setSelectionInterval(j, j);
        ensureIndexIsVisible(j);
      } else {
        clearSelection();
      } 
    } 
  }
  
  private void doSelectFile(File paramFile) {
    int i = getModel().indexOf(paramFile);
    if (i >= 0) {
      i = getRowSorter().convertRowIndexToView(i);
      this.listSelectionModel.addSelectionInterval(i, i);
    } 
  }
  
  private void doDeselectFile(Object paramObject) {
    int i = getRowSorter().convertRowIndexToView(getModel().indexOf(paramObject));
    this.listSelectionModel.removeSelectionInterval(i, i);
  }
  
  private void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    applyEdit();
    File file = (File)paramPropertyChangeEvent.getNewValue();
    JFileChooser jFileChooser = getFileChooser();
    if (file != null && ((jFileChooser.isFileSelectionEnabled() && !file.isDirectory()) || (file.isDirectory() && jFileChooser.isDirectorySelectionEnabled())))
      setFileSelected(); 
  }
  
  private void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    applyEdit();
    File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
    JFileChooser jFileChooser = getFileChooser();
    if (arrayOfFile != null && arrayOfFile.length > 0 && (arrayOfFile.length > 1 || jFileChooser.isDirectorySelectionEnabled() || !arrayOfFile[0].isDirectory()))
      setFileSelected(); 
  }
  
  private void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    getDetailsTableModel().updateColumnInfo();
    JFileChooser jFileChooser = getFileChooser();
    FileSystemView fileSystemView = jFileChooser.getFileSystemView();
    applyEdit();
    resetEditIndex();
    ensureIndexIsVisible(0);
    File file = jFileChooser.getCurrentDirectory();
    if (file != null) {
      if (!this.readOnly)
        getNewFolderAction().setEnabled(canWrite(file)); 
      this.fileChooserUIAccessor.getChangeToParentDirectoryAction().setEnabled(!fileSystemView.isRoot(file));
    } 
    if (this.list != null)
      this.list.clearSelection(); 
  }
  
  private void doFilterChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    applyEdit();
    resetEditIndex();
    clearSelection();
  }
  
  private void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    applyEdit();
    resetEditIndex();
    clearSelection();
  }
  
  private void doMultiSelectionChanged(PropertyChangeEvent paramPropertyChangeEvent) {
    if (getFileChooser().isMultiSelectionEnabled()) {
      this.listSelectionModel.setSelectionMode(2);
    } else {
      this.listSelectionModel.setSelectionMode(0);
      clearSelection();
      getFileChooser().setSelectedFiles(null);
    } 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (this.viewType == -1)
      setViewType(0); 
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str.equals("SelectedFileChangedProperty")) {
      doSelectedFileChanged(paramPropertyChangeEvent);
    } else if (str.equals("SelectedFilesChangedProperty")) {
      doSelectedFilesChanged(paramPropertyChangeEvent);
    } else if (str.equals("directoryChanged")) {
      doDirectoryChanged(paramPropertyChangeEvent);
    } else if (str.equals("fileFilterChanged")) {
      doFilterChanged(paramPropertyChangeEvent);
    } else if (str.equals("fileSelectionChanged")) {
      doFileSelectionModeChanged(paramPropertyChangeEvent);
    } else if (str.equals("MultiSelectionEnabledChangedProperty")) {
      doMultiSelectionChanged(paramPropertyChangeEvent);
    } else if (str.equals("CancelSelection")) {
      applyEdit();
    } else if (str.equals("busy")) {
      setCursor(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue() ? waitCursor : null);
    } else if (str.equals("componentOrientation")) {
      ComponentOrientation componentOrientation = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
      JFileChooser jFileChooser = (JFileChooser)paramPropertyChangeEvent.getSource();
      if (componentOrientation != paramPropertyChangeEvent.getOldValue())
        jFileChooser.applyComponentOrientation(componentOrientation); 
      if (this.detailsTable != null) {
        this.detailsTable.setComponentOrientation(componentOrientation);
        this.detailsTable.getParent().getParent().setComponentOrientation(componentOrientation);
      } 
    } 
  }
  
  private void ensureIndexIsVisible(int paramInt) {
    if (paramInt >= 0) {
      if (this.list != null)
        this.list.ensureIndexIsVisible(paramInt); 
      if (this.detailsTable != null)
        this.detailsTable.scrollRectToVisible(this.detailsTable.getCellRect(paramInt, 0, true)); 
    } 
  }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {
    int i = getModel().indexOf(paramFile);
    if (i >= 0)
      ensureIndexIsVisible(getRowSorter().convertRowIndexToView(i)); 
  }
  
  public void rescanCurrentDirectory() { getModel().validateFileCache(); }
  
  public void clearSelection() {
    if (this.listSelectionModel != null) {
      this.listSelectionModel.clearSelection();
      if (this.listSelectionModel instanceof DefaultListSelectionModel) {
        ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(0);
        this.listSelectionModel.setAnchorSelectionIndex(0);
      } 
    } 
  }
  
  public JMenu getViewMenu() {
    if (this.viewMenu == null) {
      this.viewMenu = new JMenu(this.viewMenuLabelText);
      ButtonGroup buttonGroup = new ButtonGroup();
      for (byte b = 0; b < 2; b++) {
        JRadioButtonMenuItem jRadioButtonMenuItem = new JRadioButtonMenuItem(new ViewTypeAction(this, b));
        buttonGroup.add(jRadioButtonMenuItem);
        this.viewMenu.add(jRadioButtonMenuItem);
      } 
      updateViewMenu();
    } 
    return this.viewMenu;
  }
  
  private void updateViewMenu() {
    if (this.viewMenu != null) {
      Component[] arrayOfComponent = this.viewMenu.getMenuComponents();
      for (Component component : arrayOfComponent) {
        if (component instanceof JRadioButtonMenuItem) {
          JRadioButtonMenuItem jRadioButtonMenuItem = (JRadioButtonMenuItem)component;
          if (((ViewTypeAction)jRadioButtonMenuItem.getAction()).viewType == this.viewType)
            jRadioButtonMenuItem.setSelected(true); 
        } 
      } 
    } 
  }
  
  public JPopupMenu getComponentPopupMenu() {
    JPopupMenu jPopupMenu = getFileChooser().getComponentPopupMenu();
    if (jPopupMenu != null)
      return jPopupMenu; 
    JMenu jMenu = getViewMenu();
    if (this.contextMenu == null) {
      this.contextMenu = new JPopupMenu();
      if (jMenu != null) {
        this.contextMenu.add(jMenu);
        if (this.listViewWindowsStyle)
          this.contextMenu.addSeparator(); 
      } 
      ActionMap actionMap = getActionMap();
      Action action1 = actionMap.get("refresh");
      Action action2 = actionMap.get("New Folder");
      if (action1 != null) {
        this.contextMenu.add(action1);
        if (this.listViewWindowsStyle && action2 != null)
          this.contextMenu.addSeparator(); 
      } 
      if (action2 != null)
        this.contextMenu.add(action2); 
    } 
    if (jMenu != null)
      jMenu.getPopupMenu().setInvoker(jMenu); 
    return this.contextMenu;
  }
  
  protected Handler getMouseHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected boolean isDirectorySelected() { return this.fileChooserUIAccessor.isDirectorySelected(); }
  
  protected File getDirectory() { return this.fileChooserUIAccessor.getDirectory(); }
  
  private Component findChildComponent(Container paramContainer, Class paramClass) {
    int i = paramContainer.getComponentCount();
    for (byte b = 0; b < i; b++) {
      Component component = paramContainer.getComponent(b);
      if (paramClass.isInstance(component))
        return component; 
      if (component instanceof Container) {
        Component component1 = findChildComponent((Container)component, paramClass);
        if (component1 != null)
          return component1; 
      } 
    } 
    return null;
  }
  
  public boolean canWrite(File paramFile) {
    if (!paramFile.exists())
      return false; 
    try {
      if (paramFile instanceof ShellFolder)
        return paramFile.canWrite(); 
      if (usesShellFolder(getFileChooser()))
        try {
          return ShellFolder.getShellFolder(paramFile).canWrite();
        } catch (FileNotFoundException fileNotFoundException) {
          return false;
        }  
      return paramFile.canWrite();
    } catch (SecurityException securityException) {
      return false;
    } 
  }
  
  public static boolean usesShellFolder(JFileChooser paramJFileChooser) {
    Boolean bool = (Boolean)paramJFileChooser.getClientProperty("FileChooser.useShellFolder");
    return (bool == null) ? paramJFileChooser.getFileSystemView().equals(FileSystemView.getFileSystemView()) : bool.booleanValue();
  }
  
  private class AlignableTableHeaderRenderer implements TableCellRenderer {
    TableCellRenderer wrappedRenderer;
    
    public AlignableTableHeaderRenderer(TableCellRenderer param1TableCellRenderer) { this.wrappedRenderer = param1TableCellRenderer; }
    
    public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
      Component component = this.wrappedRenderer.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
      int i = param1JTable.convertColumnIndexToModel(param1Int2);
      ShellFolderColumnInfo shellFolderColumnInfo = FilePane.this.detailsTableModel.getColumns()[i];
      Integer integer = shellFolderColumnInfo.getAlignment();
      if (integer == null)
        integer = Integer.valueOf(0); 
      if (component instanceof JLabel)
        ((JLabel)component).setHorizontalAlignment(integer.intValue()); 
      return component;
    }
  }
  
  private class DelayedSelectionUpdater implements Runnable {
    File editFile;
    
    DelayedSelectionUpdater(FilePane this$0) { this(null); }
    
    DelayedSelectionUpdater(File param1File) {
      this.editFile = param1File;
      if (this$0.isShowing())
        SwingUtilities.invokeLater(this); 
    }
    
    public void run() {
      FilePane.this.setFileSelected();
      if (this.editFile != null) {
        FilePane.this.editFileName(FilePane.this.getRowSorter().convertRowIndexToView(FilePane.this.getModel().indexOf(this.editFile)));
        this.editFile = null;
      } 
    }
  }
  
  private class DetailsTableCellEditor extends DefaultCellEditor {
    private final JTextField tf;
    
    public DetailsTableCellEditor(JTextField param1JTextField) {
      super(param1JTextField);
      this.tf = param1JTextField;
      param1JTextField.setName("Table.editor");
      param1JTextField.addFocusListener(this$0.editorFocusListener);
    }
    
    public Component getTableCellEditorComponent(JTable param1JTable, Object param1Object, boolean param1Boolean, int param1Int1, int param1Int2) {
      Component component = super.getTableCellEditorComponent(param1JTable, param1Object, param1Boolean, param1Int1, param1Int2);
      if (param1Object instanceof File) {
        this.tf.setText(FilePane.this.getFileChooser().getName((File)param1Object));
        this.tf.selectAll();
      } 
      return component;
    }
  }
  
  class DetailsTableCellRenderer extends DefaultTableCellRenderer {
    JFileChooser chooser;
    
    DateFormat df;
    
    DetailsTableCellRenderer(JFileChooser param1JFileChooser) {
      this.chooser = param1JFileChooser;
      this.df = DateFormat.getDateTimeInstance(3, 3, param1JFileChooser.getLocale());
    }
    
    public void setBounds(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (getHorizontalAlignment() == 10 && !FilePane.this.fullRowSelection) {
        param1Int3 = Math.min(param1Int3, (getPreferredSize()).width + 4);
      } else {
        param1Int1 -= 4;
      } 
      super.setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    public Insets getInsets(Insets param1Insets) {
      param1Insets = super.getInsets(param1Insets);
      param1Insets.left += 4;
      param1Insets.right += 4;
      return param1Insets;
    }
    
    public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
      String str;
      if ((param1JTable.convertColumnIndexToModel(param1Int2) != 0 || (FilePane.this.listViewWindowsStyle && !param1JTable.isFocusOwner())) && !FilePane.this.fullRowSelection)
        param1Boolean1 = false; 
      super.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
      setIcon(null);
      int i = param1JTable.convertColumnIndexToModel(param1Int2);
      ShellFolderColumnInfo shellFolderColumnInfo = FilePane.this.detailsTableModel.getColumns()[i];
      Integer integer = shellFolderColumnInfo.getAlignment();
      if (integer == null)
        integer = Integer.valueOf((param1Object instanceof Number) ? 4 : 10); 
      setHorizontalAlignment(integer.intValue());
      if (param1Object == null) {
        str = "";
      } else if (param1Object instanceof File) {
        File file = (File)param1Object;
        str = this.chooser.getName(file);
        Icon icon = this.chooser.getIcon(file);
        setIcon(icon);
      } else if (param1Object instanceof Long) {
        long l = ((Long)param1Object).longValue() / 1024L;
        if (FilePane.this.listViewWindowsStyle) {
          str = MessageFormat.format(FilePane.this.kiloByteString, new Object[] { Long.valueOf(l + 1L) });
        } else if (l < 1024L) {
          str = MessageFormat.format(FilePane.this.kiloByteString, new Object[] { Long.valueOf((l == 0L) ? 1L : l) });
        } else {
          l /= 1024L;
          if (l < 1024L) {
            str = MessageFormat.format(FilePane.this.megaByteString, new Object[] { Long.valueOf(l) });
          } else {
            l /= 1024L;
            str = MessageFormat.format(FilePane.this.gigaByteString, new Object[] { Long.valueOf(l) });
          } 
        } 
      } else if (param1Object instanceof Date) {
        str = this.df.format((Date)param1Object);
      } else {
        str = param1Object.toString();
      } 
      setText(str);
      return this;
    }
  }
  
  class DetailsTableModel extends AbstractTableModel implements ListDataListener {
    JFileChooser chooser;
    
    BasicDirectoryModel directoryModel;
    
    ShellFolderColumnInfo[] columns;
    
    int[] columnMap;
    
    DetailsTableModel(JFileChooser param1JFileChooser) {
      this.chooser = param1JFileChooser;
      this.directoryModel = this$0.getModel();
      this.directoryModel.addListDataListener(this);
      updateColumnInfo();
    }
    
    void updateColumnInfo() {
      File file = this.chooser.getCurrentDirectory();
      if (file != null && FilePane.usesShellFolder(this.chooser))
        try {
          file = ShellFolder.getShellFolder(file);
        } catch (FileNotFoundException fileNotFoundException) {} 
      ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = ShellFolder.getFolderColumns(file);
      ArrayList arrayList = new ArrayList();
      this.columnMap = new int[arrayOfShellFolderColumnInfo.length];
      for (byte b = 0; b < arrayOfShellFolderColumnInfo.length; b++) {
        ShellFolderColumnInfo shellFolderColumnInfo = arrayOfShellFolderColumnInfo[b];
        if (shellFolderColumnInfo.isVisible()) {
          this.columnMap[arrayList.size()] = b;
          arrayList.add(shellFolderColumnInfo);
        } 
      } 
      this.columns = new ShellFolderColumnInfo[arrayList.size()];
      arrayList.toArray(this.columns);
      this.columnMap = Arrays.copyOf(this.columnMap, this.columns.length);
      List list = (FilePane.this.rowSorter == null) ? null : FilePane.this.rowSorter.getSortKeys();
      fireTableStructureChanged();
      restoreSortKeys(list);
    }
    
    private void restoreSortKeys(List<? extends RowSorter.SortKey> param1List) {
      if (param1List != null) {
        for (byte b = 0; b < param1List.size(); b++) {
          RowSorter.SortKey sortKey = (RowSorter.SortKey)param1List.get(b);
          if (sortKey.getColumn() >= this.columns.length) {
            param1List = null;
            break;
          } 
        } 
        if (param1List != null)
          FilePane.this.rowSorter.setSortKeys(param1List); 
      } 
    }
    
    public int getRowCount() { return this.directoryModel.getSize(); }
    
    public int getColumnCount() { return this.columns.length; }
    
    public Object getValueAt(int param1Int1, int param1Int2) { return getFileColumnValue((File)this.directoryModel.getElementAt(param1Int1), param1Int2); }
    
    private Object getFileColumnValue(File param1File, int param1Int) { return (param1Int == 0) ? param1File : ShellFolder.getFolderColumnValue(param1File, this.columnMap[param1Int]); }
    
    public void setValueAt(Object param1Object, int param1Int1, int param1Int2) {
      if (param1Int2 == 0) {
        final JFileChooser chooser = FilePane.this.getFileChooser();
        File file = (File)getValueAt(param1Int1, param1Int2);
        if (file != null) {
          String str1 = jFileChooser.getName(file);
          String str2 = file.getName();
          String str3 = ((String)param1Object).trim();
          if (!str3.equals(str1)) {
            String str = str3;
            int i = str2.length();
            int j = str1.length();
            if (i > j && str2.charAt(j) == '.')
              str = str3 + str2.substring(j); 
            FileSystemView fileSystemView = jFileChooser.getFileSystemView();
            final File f2 = fileSystemView.createFileObject(file.getParentFile(), str);
            if (file1.exists()) {
              JOptionPane.showMessageDialog(jFileChooser, MessageFormat.format(FilePane.this.renameErrorFileExistsText, new Object[] { str2 }), FilePane.this.renameErrorTitleText, 0);
            } else if (FilePane.this.getModel().renameFile(file, file1)) {
              if (fileSystemView.isParent(jFileChooser.getCurrentDirectory(), file1))
                SwingUtilities.invokeLater(new Runnable() {
                      public void run() {
                        if (chooser.isMultiSelectionEnabled()) {
                          chooser.setSelectedFiles(new File[] { f2 });
                        } else {
                          chooser.setSelectedFile(f2);
                        } 
                      }
                    }); 
            } else {
              JOptionPane.showMessageDialog(jFileChooser, MessageFormat.format(FilePane.this.renameErrorText, new Object[] { str2 }), FilePane.this.renameErrorTitleText, 0);
            } 
          } 
        } 
      } 
    }
    
    public boolean isCellEditable(int param1Int1, int param1Int2) {
      File file = FilePane.this.getFileChooser().getCurrentDirectory();
      return (!FilePane.this.readOnly && param1Int2 == 0 && FilePane.this.canWrite(file));
    }
    
    public void contentsChanged(ListDataEvent param1ListDataEvent) {
      new FilePane.DelayedSelectionUpdater(FilePane.this);
      fireTableDataChanged();
    }
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) {
      int i = param1ListDataEvent.getIndex0();
      int j = param1ListDataEvent.getIndex1();
      if (i == j) {
        File file = (File)FilePane.this.getModel().getElementAt(i);
        if (file.equals(FilePane.this.newFolderFile)) {
          new FilePane.DelayedSelectionUpdater(file);
          FilePane.this.newFolderFile = null;
        } 
      } 
      fireTableRowsInserted(param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1());
    }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) { fireTableRowsDeleted(param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1()); }
    
    public ShellFolderColumnInfo[] getColumns() { return this.columns; }
  }
  
  private class DetailsTableRowSorter extends TableRowSorter<TableModel> {
    public DetailsTableRowSorter() { setModelWrapper(new SorterModelWrapper(null)); }
    
    public void updateComparators(ShellFolderColumnInfo[] param1ArrayOfShellFolderColumnInfo) {
      for (byte b = 0; b < param1ArrayOfShellFolderColumnInfo.length; b++) {
        Comparator comparator = param1ArrayOfShellFolderColumnInfo[b].getComparator();
        if (comparator != null)
          comparator = new FilePane.DirectoriesFirstComparatorWrapper(FilePane.this, b, comparator); 
        setComparator(b, comparator);
      } 
    }
    
    public void sort() { ShellFolder.invoke(new Callable<Void>() {
            public Void call() {
              FilePane.DetailsTableRowSorter.this.sort();
              return null;
            }
          }); }
    
    public void modelStructureChanged() {
      super.modelStructureChanged();
      updateComparators(FilePane.this.detailsTableModel.getColumns());
    }
    
    private class SorterModelWrapper extends DefaultRowSorter.ModelWrapper<TableModel, Integer> {
      private SorterModelWrapper() {}
      
      public TableModel getModel() { return FilePane.DetailsTableRowSorter.this.this$0.getDetailsTableModel(); }
      
      public int getColumnCount() { return FilePane.DetailsTableRowSorter.this.this$0.getDetailsTableModel().getColumnCount(); }
      
      public int getRowCount() { return FilePane.DetailsTableRowSorter.this.this$0.getDetailsTableModel().getRowCount(); }
      
      public Object getValueAt(int param2Int1, int param2Int2) { return FilePane.DetailsTableRowSorter.this.this$0.getModel().getElementAt(param2Int1); }
      
      public Integer getIdentifier(int param2Int) { return Integer.valueOf(param2Int); }
    }
  }
  
  private class DirectoriesFirstComparatorWrapper extends Object implements Comparator<File> {
    private Comparator comparator;
    
    private int column;
    
    public DirectoriesFirstComparatorWrapper(int param1Int, Comparator param1Comparator) {
      this.column = param1Int;
      this.comparator = param1Comparator;
    }
    
    public int compare(File param1File1, File param1File2) {
      if (param1File1 != null && param1File2 != null) {
        boolean bool1 = FilePane.this.getFileChooser().isTraversable(param1File1);
        boolean bool2 = FilePane.this.getFileChooser().isTraversable(param1File2);
        if (bool1 && !bool2)
          return -1; 
        if (!bool1 && bool2)
          return 1; 
      } 
      return FilePane.this.detailsTableModel.getColumns()[this.column].isCompareByColumn() ? this.comparator.compare(FilePane.this.getDetailsTableModel().getFileColumnValue(param1File1, this.column), FilePane.this.getDetailsTableModel().getFileColumnValue(param1File2, this.column)) : this.comparator.compare(param1File1, param1File2);
    }
  }
  
  class EditActionListener implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) { FilePane.this.applyEdit(); }
  }
  
  public static interface FileChooserUIAccessor {
    JFileChooser getFileChooser();
    
    BasicDirectoryModel getModel();
    
    JPanel createList();
    
    JPanel createDetailsView();
    
    boolean isDirectorySelected();
    
    File getDirectory();
    
    Action getApproveSelectionAction();
    
    Action getChangeToParentDirectoryAction();
    
    Action getNewFolderAction();
    
    MouseListener createDoubleClickListener(JList param1JList);
    
    ListSelectionListener createListSelectionListener();
  }
  
  protected class FileRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      if (FilePane.this.listViewWindowsStyle && !param1JList.isFocusOwner())
        param1Boolean1 = false; 
      super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      File file = (File)param1Object;
      String str = FilePane.this.getFileChooser().getName(file);
      setText(str);
      setFont(param1JList.getFont());
      Icon icon = FilePane.this.getFileChooser().getIcon(file);
      if (icon != null) {
        setIcon(icon);
      } else if (FilePane.this.getFileChooser().getFileSystemView().isTraversable(file).booleanValue()) {
        setText(str + File.separator);
      } 
      return this;
    }
  }
  
  private class Handler implements MouseListener {
    private MouseListener doubleClickListener;
    
    private Handler() {}
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      int i;
      JComponent jComponent = (JComponent)param1MouseEvent.getSource();
      if (jComponent instanceof JList) {
        i = SwingUtilities2.loc2IndexFileList(FilePane.this.list, param1MouseEvent.getPoint());
      } else if (jComponent instanceof JTable) {
        JTable jTable = (JTable)jComponent;
        Point point = param1MouseEvent.getPoint();
        i = jTable.rowAtPoint(point);
        boolean bool = SwingUtilities2.pointOutsidePrefSize(jTable, i, jTable.columnAtPoint(point), point);
        if (bool && !FilePane.this.fullRowSelection)
          return; 
        if (i >= 0 && FilePane.this.list != null && FilePane.this.listSelectionModel.isSelectedIndex(i)) {
          Rectangle rectangle = FilePane.this.list.getCellBounds(i, i);
          MouseEvent mouseEvent = new MouseEvent(FilePane.this.list, param1MouseEvent.getID(), param1MouseEvent.getWhen(), param1MouseEvent.getModifiers(), rectangle.x + 1, rectangle.y + rectangle.height / 2, param1MouseEvent.getXOnScreen(), param1MouseEvent.getYOnScreen(), param1MouseEvent.getClickCount(), param1MouseEvent.isPopupTrigger(), param1MouseEvent.getButton());
          AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
          mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(param1MouseEvent));
          param1MouseEvent = mouseEvent;
        } 
      } else {
        return;
      } 
      if (i >= 0 && SwingUtilities.isLeftMouseButton(param1MouseEvent)) {
        JFileChooser jFileChooser = FilePane.this.getFileChooser();
        if (param1MouseEvent.getClickCount() == 1 && jComponent instanceof JList) {
          if ((!jFileChooser.isMultiSelectionEnabled() || jFileChooser.getSelectedFiles().length <= 1) && i >= 0 && FilePane.this.listSelectionModel.isSelectedIndex(i) && FilePane.this.getEditIndex() == i && FilePane.this.editFile == null) {
            FilePane.this.editFileName(i);
          } else if (i >= 0) {
            FilePane.this.setEditIndex(i);
          } else {
            FilePane.this.resetEditIndex();
          } 
        } else if (param1MouseEvent.getClickCount() == 2) {
          FilePane.this.resetEditIndex();
        } 
      } 
      if (getDoubleClickListener() != null)
        getDoubleClickListener().mouseClicked(param1MouseEvent); 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      JComponent jComponent = (JComponent)param1MouseEvent.getSource();
      if (jComponent instanceof JTable) {
        JTable jTable = (JTable)param1MouseEvent.getSource();
        TransferHandler transferHandler1 = FilePane.this.getFileChooser().getTransferHandler();
        TransferHandler transferHandler2 = jTable.getTransferHandler();
        if (transferHandler1 != transferHandler2)
          jTable.setTransferHandler(transferHandler1); 
        boolean bool = FilePane.this.getFileChooser().getDragEnabled();
        if (bool != jTable.getDragEnabled())
          jTable.setDragEnabled(bool); 
      } else if (jComponent instanceof JList && getDoubleClickListener() != null) {
        getDoubleClickListener().mouseEntered(param1MouseEvent);
      } 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() instanceof JList && getDoubleClickListener() != null)
        getDoubleClickListener().mouseExited(param1MouseEvent); 
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() instanceof JList && getDoubleClickListener() != null)
        getDoubleClickListener().mousePressed(param1MouseEvent); 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() instanceof JList && getDoubleClickListener() != null)
        getDoubleClickListener().mouseReleased(param1MouseEvent); 
    }
    
    private MouseListener getDoubleClickListener() {
      if (this.doubleClickListener == null && FilePane.this.list != null)
        this.doubleClickListener = FilePane.this.fileChooserUIAccessor.createDoubleClickListener(FilePane.this.list); 
      return this.doubleClickListener;
    }
  }
  
  private class SortableListModel extends AbstractListModel<Object> implements TableModelListener, RowSorterListener {
    public SortableListModel() {
      this$0.getDetailsTableModel().addTableModelListener(this);
      this$0.getRowSorter().addRowSorterListener(this);
    }
    
    public int getSize() { return FilePane.this.getModel().getSize(); }
    
    public Object getElementAt(int param1Int) { return FilePane.this.getModel().getElementAt(FilePane.this.getRowSorter().convertRowIndexToModel(param1Int)); }
    
    public void tableChanged(TableModelEvent param1TableModelEvent) { fireContentsChanged(this, 0, getSize()); }
    
    public void sorterChanged(RowSorterEvent param1RowSorterEvent) { fireContentsChanged(this, 0, getSize()); }
  }
  
  class ViewTypeAction extends AbstractAction {
    private int viewType;
    
    ViewTypeAction(FilePane this$0, int param1Int) {
      super(this$0.viewTypeActionNames[param1Int]);
      this.viewType = param1Int;
      switch (param1Int) {
        case 0:
          str = "viewTypeList";
          break;
        case 1:
          str = "viewTypeDetails";
          break;
        default:
          str = (String)getValue("Name");
          break;
      } 
      putValue("ActionCommandKey", str);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { this.this$0.setViewType(this.viewType); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\FilePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */