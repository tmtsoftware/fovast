/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Exceptions;
import org.tmt.fovast.gui.FovastInstrumentTree.SomeException;
import org.tmt.fovast.instrumentconfig.Config;
import org.tmt.fovast.instrumentconfig.Value;
import voi.astro.util.NameResolver;

import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.state.VisualizationState;
import org.tmt.fovast.astro.util.DegreeCoverter;
import org.tmt.fovast.astro.util.XMLFileGenerator;
import org.tmt.fovast.instrumentconfig.BooleanValue;
import org.tmt.fovast.instrumentconfig.ConfigHelper;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import jsky.graphics.CanvasFigure;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 */
public class VisualizationControlPanel extends JPanel
        implements VisualizationState.VisualizationStateListener,
        VisualizationWorkPanelListener,Config.ConfigListener {

    private static Logger logger = LoggerFactory.getLogger(VisualizationControlPanel.class);

//    private final static String INSTRUMENT_CONTROL_XML = "resources/InstrumentControl.xml";

    private final static String HELP_ICON = "resources/images/Help24.gif";

    private ApplicationContext appContext;

    ApplicationActionMap actionMap;

    private JTextField sourceTextField;

    private JLabel sourceErrorMsgLabel;

    private JButton simbadButton;

    private JButton nedButton;

    private JTextField raTextField;

    private JTextField decTextField;

    private JLabel raErrorMsgLabel;

    private JLabel decErrorMsgLabel;

    private JLabel targetLabel;

    private JLabel imageLoadMsgLabel;

    private JPanel showTargetPanel;
    
    private VisualizationState visualization;

    private NumberFormat imageLoadBytesFormat;

    private JCheckBox showTargetCheckbox;

    private JCheckBox showDragCheckbox;

    private JLabel showTargetLabel;

    private JLabel showDragLabel;

    private  JLabel raFormatLabel;

    private  JLabel decFormatLabel;

    private JLabel raAfterLabel;

    private JLabel decAfterLabel;

    private JTree tree;

    private JPanel configPanel;

    private ConfigHelper configHelper;

    private JButton fetchButton;

    private JTable pointInfoTable;

    private DefaultTableModel model;

    //private MyTableModel model;

    private JPanel tempPanel;

    private JScrollPane pane;

    private JLabel focusLabel;

    private JComboBox focusComboBox;

    private JCheckBox autoSuggestGuideStars;

    public static final String DOWNLOAD_CACHE_DIR = "guideStarInfo.xml";

    private ArrayList<PointInfoForXML> prevInfoList = new ArrayList<PointInfoForXML>();

    private ArrayList<Object> pointsInLimits = new ArrayList<Object>();

    private ArrayList<Object> brightestStarList = new ArrayList<Object>();

    /**
     * flags to check check whether respective probe is checked or not in order to show
     * Focus Correction
     */
    private boolean isProbe1Selected = false , isProbe2Selected= false , isProbe3Selected= false;
    
    private boolean isIrisSelected ,isIfuMode , isImagingMode ;


    
    public VisualizationControlPanel(ApplicationContext appContext,
            VisualizationState visualizationState) {
        this.appContext = appContext;
        this.visualization = visualizationState;
        initComponents();

        visualization.addListener(this); 
        //other initialization
        imageLoadBytesFormat = NumberFormat.getInstance();
        imageLoadBytesFormat.setMaximumFractionDigits(2);
        imageLoadBytesFormat.setMinimumFractionDigits(2);
    }

    private void initComponents() {
        //TODO: Instruments tree to be shown


        //TODO:All text color font and padding should be outside app code.
        //TODO:All labels should be from resources file
        //Scan through the below code for relevant changes

        //setPreferredSize(new Dimension(150, 200));
        
        tempPanel = new JPanel(new BorderLayout());
        String[] columnNames ={"Element Name","Catalog Name","RA","DEC"};
                String[][] rowData =new String[5][4];
        model = new DefaultTableModel(rowData, columnNames);
        //model = new MyTableModel(rowData, columnNames);
        //pointInfoTable = new JTable(model);
        pointInfoTable = new JTable(model){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                  int Index_row, int Index_col) {
                  Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
                  //even index, selected or not selected
                  if(focusComboBox.getSelectedIndex() != -1){
//                  if (focusComboBox.getSelectedIndex() == 0 && Index_row == 1) {
                      //if(Index_row == 1 || Index_row == 2 || Index_row == 3 ){
                        for (int i = 0; i < model.getRowCount(); i++) {
                            if(Index_row == i ){
                            String xyz = (String) model.getValueAt(i, 0);
                            if (xyz != null) {
                                if (xyz.contains(focusComboBox.getSelectedItem().toString())) {
                                    
                                    comp.setBackground(Color.YELLOW);
                                }
                                else {
                                    comp.setBackground(Color.WHITE);
                                }
                            }
                             else
                                    comp.setBackground(Color.WHITE);
                            }

                        }
                      //}
                  }
//                }
//                else if(focusComboBox.getSelectedIndex() == 1 && Index_row == 2) {
//                  comp.setBackground(Color.YELLOW);
//                  }
//                else if(focusComboBox.getSelectedIndex() == 2 && Index_row == 3) {
//                  comp.setBackground(Color.YELLOW);
//                  }
//                  else {
//                  comp.setBackground(Color.white);
//                  }
                  return comp;
                  }
                  };
        pointInfoTable.setPreferredScrollableViewportSize(new Dimension(300, 80));
        pointInfoTable.setEnabled(false);
        pane = new JScrollPane(pointInfoTable,JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tempPanel.add(pane,BorderLayout.NORTH);
        System.out.println(appContext.getLocalStorage().getDirectory());
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder());

        //raDecPanel & contents
        //
        JPanel raDecPanel = new JPanel();

        //sourceName,ra, dec field labels
        JLabel sourceLabel = new JLabel("<html><body>Resolve&nbsp; <br/> Source:</body></html>");
        JLabel raLabel = new JLabel("RA:");
        JLabel decLabel = new JLabel("DEC:");

        //sourceName,ra, dec text fields
        raTextField = new JTextField(20);
        raTextField.addFocusListener(new FocusAdapter() {
            private String prevValue = null;

            @Override
            public void focusLost(FocusEvent e) {
                String newValue = raTextField.getText().trim();

                validateRaFieldAndShowErrorMsgField();

                //this is for cleaning name resolution messages on
                //manual RA Dec change
                if(!newValue.equals(prevValue)) {
                    sourceTextField.setText("");
                    sourceErrorMsgLabel.setText("");

                    prevValue = newValue;
                }
            }
        });

        decTextField = new JTextField(20);
        decTextField.addFocusListener(new FocusAdapter() {
            private String prevValue = null;

            @Override
            public void focusLost(FocusEvent e) {
                String newValue = decTextField.getText().trim();
                
                validateDecFieldAndShowErrorMsgField();

                //this is for cleaning name resolution messages on
                //manual RA Dec change
                if(!newValue.equals(prevValue)) {
                    sourceTextField.setText("");
                    sourceErrorMsgLabel.setText("");

                    prevValue = newValue;
                }
            }
        });

        sourceTextField = new JTextField(20);


        JPanel resolvePanel = new JPanel(new FlowLayout());
        simbadButton=new JButton("Simbad");
        simbadButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               clearErrorMsgsAndRaDecFields();
               if(validateSourceFieldAndShowErrorMsgField())
                    resolveName("Simbad");
          }
       });

        nedButton=new JButton("NED");
        nedButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               clearErrorMsgsAndRaDecFields();
               if(validateSourceFieldAndShowErrorMsgField())
                    resolveName("NED");
          }
       });

       resolvePanel.add(simbadButton);
       resolvePanel.add(nedButton);

       //ra, dec after labels
        raAfterLabel = new JLabel("J2000/FK5");
        decAfterLabel = new JLabel("J2000/FK5");

       JPanel raFormatPanel = new JPanel(new FlowLayout());
       URL urlString=VisualizationControlPanel.class.getResource(HELP_ICON);
       raFormatLabel=new JLabel(new ImageIcon(urlString));
       int delay=Integer.MAX_VALUE;
       ToolTipManager.sharedInstance().setDismissDelay(delay);
       raFormatLabel.setToolTipText("<html><body><b>Formats for RA:</b><br/>"
               + "<b>eg1:<b/> 12h 30m 40s<br/><b>eg2:<b/> 12:30:40<br/><b>eg3:<b/> 12 30 40<br/><b>eg4:<b/> 202.43 (in degrees)");
       raFormatPanel.add(raAfterLabel);
       raFormatPanel.add(raFormatLabel);

       JPanel decFormatPanel = new JPanel(new FlowLayout());
       decFormatLabel=new JLabel(new ImageIcon(urlString));
       decFormatLabel.setToolTipText("<html><body><b>Formats for DEC:</b><br/>"
               + "<b>eg1:<b/> 12d 30m 40s<br/><b>eg2:<b/> 12:30:40<br/><b>eg3:<b/> 12 30 40<br/><b>eg4:<b/> 102.43 (in degrees)");
       decFormatPanel.add(decAfterLabel);
       decFormatPanel.add(decFormatLabel);
        

        //ra, dec error msg labels
        raErrorMsgLabel = new JLabel();
        raErrorMsgLabel.setForeground(Color.RED);
        decErrorMsgLabel = new JLabel();
        decErrorMsgLabel.setForeground(Color.RED);
        sourceErrorMsgLabel = new JLabel();
        sourceErrorMsgLabel.setForeground(Color.RED);

        //set target button
        JButton panButton = new JButton("Set as science target");
        panButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setTargetButtonClicked();
                fetchButtonActionPerformed(e);
            }

        });
        //TODO: Set action for setting target and for taking props from res file
        //panButton.setAction();

        //target label
        targetLabel = new JLabel();

        //imageLoadStatusLabel
        imageLoadMsgLabel = new JLabel(" ");

        fetchButton = new JButton("Capture guide stars");
        fetchButton.setEnabled(false);
        fetchButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
                fetchButtonActionPerformed(event);
            }
       });

        autoSuggestGuideStars = new JCheckBox("Auto Suggest Guide Stars");
        autoSuggestGuideStars.setEnabled(false);
        autoSuggestGuideStars.addItemListener(new ItemListener() {
           public void itemStateChanged(ItemEvent event) {
               if(event.getStateChange() == ItemEvent.SELECTED){
                   ArrayList<String> tips = new ArrayList<String>();
                   brightestStarList = new ArrayList<Object>();
                   if(isIrisSelected){
                        tips.add("iris.oiwfs.probe1.limits");
                        tips.add("iris.oiwfs.probe2.limits");
                        tips.add("iris.oiwfs.probe3.limits");
                    }
//                    else{
//                        tips.add("mobie.guider.limits");
//                    }
                    for(int j=0;j<tips.size();j++){
                        visualization.capturePositions(tips.get(j),visualization.getConfig().getConfig(tips.get(j)));
                   }
                   if(visualization.getCatalogs().size()>0)
                        showBrightestStar();
               }
               if(event.getStateChange() == ItemEvent.DESELECTED){
                   brightestStarList = new ArrayList<Object>();
                   showFocusMarker();
               }
            }


       });
       
        //raDecPanel layout        
        //GridLayout raDecPanelLayout = new GridLayout(2, 2);
        GridBagLayout raDecPanelLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        raDecPanel.setLayout(raDecPanelLayout);
        raDecPanel.add(sourceLabel, gbc);

        //TODO: Text field size increases if split pane is expanded. Need to fix
        gbc.gridx = 1;
        Dimension raDecTextFieldDimension = new Dimension(100, 30);
        sourceTextField.setPreferredSize(raDecTextFieldDimension);
        sourceTextField.setMinimumSize(raDecTextFieldDimension);
        sourceTextField.setMaximumSize(raDecTextFieldDimension);
        sourceTextField.setSize(raDecTextFieldDimension);
        raDecPanel.add(sourceTextField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        raDecPanel.add(raLabel, gbc);

        //TODO: Text field size increases if split pane is expanded. Need to fix
        gbc.gridx = 1;
        raTextField.setPreferredSize(raDecTextFieldDimension);
        raTextField.setMinimumSize(raDecTextFieldDimension);
        raTextField.setMaximumSize(raDecTextFieldDimension);
        raTextField.setSize(raDecTextFieldDimension);
        //raTextField.setBorder(BorderFactory.createLineBorder(Color.black));
        raDecPanel.add(raTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 0, 0);
        //decLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        raDecPanel.add(decLabel, gbc);

        gbc.gridx = 1;
        decTextField.setPreferredSize(raDecTextFieldDimension);
        decTextField.setMinimumSize(raDecTextFieldDimension);
        decTextField.setMaximumSize(raDecTextFieldDimension);
        decTextField.setSize(raDecTextFieldDimension);
        //decTextField.setBorder(BorderFactory.createLineBorder(Color.black));
        raDecPanel.add(decTextField, gbc);
       
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 0, 0);
        raDecPanel.add(raErrorMsgLabel, gbc);

        gbc.gridy = 5;
        raDecPanel.add(decErrorMsgLabel, gbc);

        gbc.gridy = 1;
        raDecPanel.add(sourceErrorMsgLabel, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 0, 0);
        raDecPanel.add(panButton, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.weightx = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 0, 0);
        raDecPanel.add(autoSuggestGuideStars, gbc);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.weightx = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 0, 0);
        raDecPanel.add(fetchButton, gbc);

//        gbc.gridy = 9;
//        gbc.gridx = 1;
//        gbc.weightx = 2;
//        gbc.gridwidth = 3;
//        gbc.insets = new Insets(5, 0, 0, 0);
//        raDecPanel.add(saveButton, gbc);

        gbc.gridx = 2;
        gbc.gridy=0;
        raDecPanel.add(resolvePanel,gbc);

        gbc.gridx = 2;
        gbc.gridy=2;
        raDecPanel.add(raFormatPanel,gbc);

        gbc.gridx = 2;
        gbc.gridy=4;
        raDecPanel.add(decFormatPanel,gbc);


//        gbc.gridy = 7;
//        gbc.gridx = 0;
//        gbc.weightx = 1;
//        gbc.gridwidth = 3;
//        raDecPanel.add(targetLabel, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 0, 10, 0);
        raDecPanel.add(imageLoadMsgLabel, gbc);

        //raDecPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        
        //showTargetPanel
        showTargetPanel = new JPanel();
        showTargetPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel showBackgroundLabel = new JLabel("Show background image");
        JCheckBox showBackgroundCheckbox = new JCheckBox();

        showTargetLabel = new JLabel("Show target marker");
        showTargetLabel.setEnabled(false);
        showTargetCheckbox = new JCheckBox();
        showTargetCheckbox.setEnabled(false);
        showTargetCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                showTargetCbCliked();
            }
        });

        showDragLabel = new JLabel("Drag");
        showDragLabel.setEnabled(false);
        showDragCheckbox = new JCheckBox();
        showDragCheckbox.setEnabled(false);
        showDragCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                traverse();
            }
        });

        focusComboBox = new JComboBox();
        focusLabel = new JLabel("OIWFS focus correction");
        focusComboBox.addItem("probe1");
        focusComboBox.addItem("probe2");
        focusComboBox.addItem("probe3");
        focusComboBox.setSelectedIndex(-1);
        focusComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                    fetchButtonActionPerformed(null);
                    traverse();
                }
               }
        });
        focusComboBox.setEnabled(false);
        focusLabel.setEnabled(false);

        JPanel focusPanel = new JPanel(new FlowLayout());
        focusPanel.add(focusLabel);
        focusPanel.add(focusComboBox);

        //showTargetPanel layout
        showTargetPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcIcp = new GridBagConstraints();

        gbcIcp.gridx = 0;
        gbcIcp.gridy = 0;
        gbcIcp.gridwidth = GridBagConstraints.RELATIVE;
        gbcIcp.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 0, 0);
        //showTargetCheckbox.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        showTargetPanel.add(showTargetCheckbox, gbcIcp);

        gbcIcp.gridx = 1;
        gbcIcp.gridy = 0;
        gbcIcp.gridwidth = GridBagConstraints.REMAINDER;
        gbcIcp.anchor = GridBagConstraints.WEST;
        gbcIcp.weightx = 1;
        //showTargetLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        showTargetPanel.add(showTargetLabel, gbcIcp);

        gbcIcp.gridx = 0;
        gbcIcp.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        //showTargetCheckbox.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        showTargetPanel.add(showDragCheckbox, gbcIcp);

        gbcIcp.gridx = 1;
        gbcIcp.gridy = 1;
        gbcIcp.weightx = 1;
        //showTargetLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        showTargetPanel.add(showDragLabel, gbcIcp);

        gbcIcp.gridx = 0;
        gbcIcp.gridy = 2;
        gbcIcp.gridwidth = GridBagConstraints.REMAINDER;
        gbcIcp.anchor = GridBagConstraints.WEST;
        showTargetPanel.add(focusPanel, gbcIcp);



//  JTREE DOES NOT WORK WITH GRID BAG CONSTRAINTS THAT WELLL .. 
//        gbcIcp.gridx = 0;
//        gbcIcp.gridy = 1;
//        gbcIcp.weightx = 1;
//        gbcIcp.weighty = 1;
//        gbcIcp.gridwidth = 2;
//        try {
//            JTree tree = makeInstrumentTree();
//            showTargetPanel.add(tree, gbcIcp);
//        } catch(Exception ex) {
//            logger.error("Error while making instrument config tree", ex);
//        }
//        //showTargetPanel.add(new JLabel(" "), gbcIcp);

        configPanel = new JPanel(new BorderLayout());
        configPanel.add(showTargetPanel, BorderLayout.NORTH);
        configPanel.add(tempPanel,BorderLayout.CENTER);
        try {
            //passing null for config to make a dummy tree
            makeInstrumentTree(false, null);
            //configPanel.add(new JPanel(), BorderLayout.CENTER);
        } catch(Exception ex) {
            logger.error("Error while making instrument config tree", ex);
        }
        JPanel mainPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = getLayout().preferredLayoutSize(this);
                //dim.width returned is slightly greater than required
                //(probably because of Grid bag layout being used for some panels)
                //so setting it to 340 (rough calculation)
                return new Dimension(340, dim.height);
            }

        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10));
        mainPanel.add(raDecPanel, BorderLayout.NORTH);
        mainPanel.add(configPanel, BorderLayout.CENTER);     
        add(new JScrollPane(mainPanel));
    }

    public void fetchButtonActionPerformed(ActionEvent ae){

               ArrayList<Catalog> catalogs = visualization.getCatalogs();
               Double[] center = visualization.getTarget();
               double ra = center[0];
               double dec = center[1];
               //loop through catalogs
                XMLFileGenerator xf = new XMLFileGenerator();
                String fName = visualization.getFileName();
                ArrayList<PointInfoForXML> infoList = new ArrayList<PointInfoForXML>();

                String tempFile;
                if(fName.contains("(")){
                   String id = fName.substring(fName.indexOf('(')+1, fName.indexOf(')'));
                    tempFile = "guideStarInfo"+id+".xml";
               }
                else
                    tempFile = "guideStarInfo.xml";
                int flag = 0;
                File cacheFile = new File(appContext.getLocalStorage().getDirectory(),
                                    tempFile);
                if(!cacheFile.exists()){
                    flag = 1;
                }else
                    prevInfoList = parsePrevXml();
                infoList = populateList(catalogs,flag);
                if(ae!=null && ae.getSource() instanceof JButton){
                    if(((JButton)ae.getSource()).getText().equalsIgnoreCase("set as science target")){
                        infoList= new ArrayList<PointInfoForXML>();
                    }
                }
                if(fName.contains("(")){
                   String id = fName.substring(fName.indexOf('(')+1, fName.indexOf(')'));
                   xf.generateXML(infoList,ra,dec,Integer.parseInt(id));
                }else{
                    xf.generateXML(infoList,ra,dec,0);
                }
                String[] columnNames ={"Element Name","Catalog Name","RA","DEC"};
                String[][] rowData =new String[infoList.size()][4];
                rowData = populateDataForTable();
//                if(pointInfoTable != null){
//                    tempPanel.remove(pane);
//                }
//                pointInfoTable = new JTable();
                model = new DefaultTableModel(rowData, columnNames);
                //model = new MyTableModel(rowData, columnNames);
                pointInfoTable.setModel(model);
                pointInfoTable.setEnabled(false);
                configPanel.revalidate();
                //TODO:Remove these 4 lines if not reqd
//                Config config = visualization.getConfig();
//                if(focusComboBox.getSelectedIndex()==-1){
//                    config.setConfigElementProperty("iris.oiwfs.probe1.arm", "defaultFocus","null");
//                }else{
//                    config.setConfigElementProperty("iris.oiwfs.probe1.arm", "defaultFocus","red");
//                }
    }
    /**
     * Method to traverse the nodes of the tree
     */
    public void traverse() { 
    TreeModel model = tree.getModel();
    if (model != null) {
        DefaultMutableTreeNode root =
                    (DefaultMutableTreeNode)model.getRoot();
       // Object root = model.getRoot();
        walk(model,root);
        }
    else
       System.out.println("Tree is empty.");
    }

  protected void walk(TreeModel model, Object o){
      int cc;
      cc = model.getChildCount(o);
      for (int i = 0; i < cc; i++) {
          DefaultMutableTreeNode child = (DefaultMutableTreeNode) model.getChild(o, i);
          if (model.isLeaf(child)) {              
              String leafLabel = ((FovastInstrumentTree.UserObject) child.getUserObject()).getLabel();
              //to check if the particular node is Focus Correction checkbox
              if (leafLabel.equalsIgnoreCase("Focus Correction")) {
                  DefaultMutableTreeNode parent = (DefaultMutableTreeNode) child.getParent();
                  //get the label of parent
                  String parentLabel = ((FovastInstrumentTree.UserObject) parent.getUserObject()).getLabel();
                  String focusComboString = "no probe" ;
                  if(focusComboBox.getSelectedIndex()!=-1)
                     focusComboString = focusComboBox.getSelectedItem().toString();
//                  if(((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).isSelected()){
//                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setEditState(false);
                       //disable the Focus Correction checkbox
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(false);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setDisabled(true);
                      FovastInstrumentTree.CheckboxUserObject cuo =
                                    (FovastInstrumentTree.CheckboxUserObject)child.getUserObject();
                        Object value = cuo.getConfigOptionValue();
                        if(value == null) //means its simple on/off config
                            value = new BooleanValue(false);
                        configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
                  //}
                  if (parentLabel.equalsIgnoreCase(focusComboString)) {                      
//                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setEditState(true);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(true);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setDisabled(true);
                       cuo =  (FovastInstrumentTree.CheckboxUserObject)child.getUserObject();
                       value = cuo.getConfigOptionValue();
                        if(value == null) //means its simple on/off config
                            value = new BooleanValue(true);
                        configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
                   }else{
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(false);
                   }
              }
              else if(isIrisSelected && leafLabel.equalsIgnoreCase("Dragger"))
              {
                  ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setDisabled(true);
                  if (showDragCheckbox.isSelected()) {
//                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setEditState(true);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(true);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(false);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(true);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setDisabled(true);
                       FovastInstrumentTree.CheckboxUserObject cuo =  (FovastInstrumentTree.CheckboxUserObject)child.getUserObject();
                       Object value = cuo.getConfigOptionValue();
                        if(value == null) //means its simple on/off config
                            value = new BooleanValue(true);
                        configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
                   }else{
                       ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(false);
                      FovastInstrumentTree.CheckboxUserObject cuo =  (FovastInstrumentTree.CheckboxUserObject)child.getUserObject();
                      Object value = cuo.getConfigOptionValue();
                        if(value == null) //means its simple on/off config
                            value = new BooleanValue(false);
                        configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
                   }
              }
              else if(!isIrisSelected && leafLabel.equalsIgnoreCase("Show Dragger"))
              {
                  ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setDisabled(true);
                  if (showDragCheckbox.isSelected()) {
//                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setEditState(true);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(true);
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setDisabled(true);
                       FovastInstrumentTree.CheckboxUserObject cuo =  (FovastInstrumentTree.CheckboxUserObject)child.getUserObject();
                       Object value = cuo.getConfigOptionValue();
                        if(value == null) //means its simple on/off config
                            value = new BooleanValue(true);
                        configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
                   }else{
                      ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(false);
                      FovastInstrumentTree.CheckboxUserObject cuo =  (FovastInstrumentTree.CheckboxUserObject)child.getUserObject();
                      Object value = cuo.getConfigOptionValue();
                        if(value == null) //means its simple on/off config
                            value = new BooleanValue(false);
                        configHelper.setConfig(cuo.getConfigOptionId(), (Value) value);
                   }
              }
          } else {
              if(child.getUserObject() instanceof FovastInstrumentTree.CheckboxUserObject){
                    if(((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).getLabel().equalsIgnoreCase("Show AO Sweet Spot")){
                        //if(((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).isSelected()){
                            if(isIfuMode && isImagingMode){
                                ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getLastChild())).getUserObject()).setSelected(true);
                                ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getChildAt(1))).getUserObject()).setSelected(false);
                                ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getFirstChild())).getUserObject()).setSelected(false);
                                if(((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).isSelected()){
                                    FovastInstrumentTree.RadioUserObject ruo = ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getLastChild())).getUserObject());
                                    Object value = ruo.getConfigOptionValue();
                                    if(value == null) //means its simple on/off config
                                       value = "Both";
                                    configHelper.setConfig(ruo.getConfigOptionId(), (Value) value);
                                }
                            }else if(isIfuMode && !isImagingMode){
                                  ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getChildAt(1))).getUserObject()).setSelected(true);
                                  ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getFirstChild())).getUserObject()).setSelected(false);
                                ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getLastChild())).getUserObject()).setSelected(false);
                                if(((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).isSelected()){
                                    FovastInstrumentTree.RadioUserObject ruo = ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getChildAt(1))).getUserObject());
                                    Object value = ruo.getConfigOptionValue();
                                    if(value == null) //means its simple on/off config
                                       value = "IFU";
                                    configHelper.setConfig(ruo.getConfigOptionId(), (Value) value);
                                }
                            }else if(!isIfuMode && isImagingMode){
                                ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getFirstChild())).getUserObject()).setSelected(true);
                                ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getLastChild())).getUserObject()).setSelected(false);
                                ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getChildAt(1))).getUserObject()).setSelected(false);
                                if(((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).isSelected()){
                                    FovastInstrumentTree.RadioUserObject ruo = ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getFirstChild())).getUserObject());
                                    Object value = ruo.getConfigOptionValue();
                                    if(value == null) //means its simple on/off config
                                       value = "imageMode";
                                    configHelper.setConfig(ruo.getConfigOptionId(), (Value) value);
                                }
                            }else if(!isIfuMode && !isImagingMode){
                                ((FovastInstrumentTree.CheckboxUserObject)child.getUserObject()).setSelected(false);
                                 FovastInstrumentTree.RadioUserObject ruo = ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getFirstChild())).getUserObject());
                                 configHelper.setConfig(ruo.getConfigOptionId(), new BooleanValue(false));
                                 ruo = ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getLastChild())).getUserObject());
                                 configHelper.setConfig(ruo.getConfigOptionId(), new BooleanValue(false));
                                 ruo = ((FovastInstrumentTree.RadioUserObject)((DefaultMutableTreeNode)(child.getChildAt(1))).getUserObject());
                                 configHelper.setConfig(ruo.getConfigOptionId(), new BooleanValue(false));
                            }
                        //}
                    }
               }
              if(isIrisSelected){
                  if(child.getUserObject() instanceof FovastInstrumentTree.RadioUserObject){
                        if(((FovastInstrumentTree.RadioUserObject)child.getUserObject()).getLabel().equalsIgnoreCase("iris")){
                            ((FovastInstrumentTree.RadioUserObject)child.getNextSibling().getNextSibling().getUserObject()).setDisabled(true);
                            tree.repaint();
                        }
                  }
              }else{
                    if(child.getUserObject() instanceof FovastInstrumentTree.RadioUserObject){
                    if(((FovastInstrumentTree.RadioUserObject)child.getUserObject()).getLabel().equalsIgnoreCase("mobie")){
                            ((FovastInstrumentTree.RadioUserObject)child.getPreviousSibling().getPreviousSibling().getUserObject()).setDisabled(true);
                            tree.repaint();
                        }
                  }
              }              
              walk(model, child);
          }
      }
   }


    private void makeInstrumentTree(boolean enabled, Config config)
            throws FovastInstrumentTree.SomeException {

        if(tree != null)
            tempPanel.remove(tree);
            //configPanel.remove(tree);
        FovastInstrumentTree instrumentTree = null;
        if(config != null) {
            configHelper = new ConfigHelper(config);
            instrumentTree = new FovastInstrumentTree(configHelper);
            config.addConfigListener(this);
            configHelper.addConfigListener(VisualizationControlPanel.this);
        }
        else {
            instrumentTree = new FovastInstrumentTree(null);
        }
        tree = instrumentTree.getJTree();
        tree.setOpaque(true);
        tree.setBackground(configPanel.getBackground());
        tree.setEnabled(enabled);
        //configPanel.add(tree, BorderLayout.CENTER);
        //configPanel.revalidate();       
        tempPanel.add(tree, BorderLayout.CENTER);      
        tempPanel.revalidate();
    }

    private void validateRaFieldAndShowErrorMsgField() {
        Double ra = null;
        try {
            String text = raTextField.getText().trim();
            if (text.length() == 0) {
                return;
            }

            ra = DegreeCoverter.parseAndConvertRa(text);
            raErrorMsgLabel.setText("");
            if (!(ra >= 0 && ra < 360)) {
                //TODO: should be from resource file
                raErrorMsgLabel.setText("RA should be in [0, 360)");
            } else {
                return;
            }
        } 
        catch (Exception e) {
            raErrorMsgLabel.setText("Please enter RA in correct format");
            //TODO: should be from resource file
            logger.warn("Could not resolve ", e);
            
        }
        
        raTextField.setText("");
    }

    private void validateDecFieldAndShowErrorMsgField() {
        Double dec = null;
        try {
            String text = decTextField.getText().trim();
            if (text.length() == 0) {
                return;
            }

            dec = DegreeCoverter.parseAndConvertDec(text);
            decErrorMsgLabel.setText("");
            if (!(dec >= -90 && dec <= 90)) {
                //TODO: should be from resource file
                decErrorMsgLabel.setText("DEC should be in [-90, 90]");
            } else {
                return;
            }
        } catch (Exception e) {
            decErrorMsgLabel.setText("Please enter DEC in correct format");
            //TODO: should be from resource file
            logger.warn("Could not resolve ", e);
        }
       
        decTextField.setText("");
    }

    /**
     * Returns true if not empty
     *
     * @return
     */
    private boolean validateSourceFieldAndShowErrorMsgField() {
        
            String text = sourceTextField.getText().trim();
            if (text.length() == 0) {
                sourceErrorMsgLabel.setText("Source field is empty");
                return false;
            }

            return true;
    }
    
    private void setTargetButtonClicked() {
        //check if everythings ok
        if (raTextField.getText().trim().length() == 0) {
            //TODO: take from resource
            raErrorMsgLabel.setText("RA field is empty");
            return;
        }

        if (decTextField.getText().trim().length() == 0) {
            //TODO: take from resource 
            decErrorMsgLabel.setText("DEC field is empty");
            return;
        }
        
        String raValue=raTextField.getText();
        String decValue=decTextField.getText();
        double raDeg = 0;
        double decDeg = 0;

        try {
            raDeg=DegreeCoverter.parseAndConvertRa(raValue);
        } catch(Exception ex) {
            raErrorMsgLabel.setText("Please enter RA in correct format");
            return;
        }

        try {
            decDeg=DegreeCoverter.parseAndConvertDec(decValue);
        } catch(Exception ex) {
            decErrorMsgLabel.setText("Please enter DEC in correct format");
            return;
        }

        if(!(raDeg >= 0 && raDeg < 360)) {
            raErrorMsgLabel.setText("RA should be in [0, 360)");
            if((!(decDeg >= -90 && decDeg <= 90))){
                decErrorMsgLabel.setText("DEC should be in [-90, 90]");
            }
            return;
        }
        if((!(decDeg >= -90 && decDeg <= 90))){
            decErrorMsgLabel.setText("DEC should be in [-90, 90]");
            return;
        }

        visualization.setTarget(raDeg, decDeg, raTextField.getText().trim(),
                decTextField.getText().trim());
        updateUIForSetTarget(raDeg, decDeg, raTextField.getText().trim(),
                decTextField.getText().trim());
        
        //if(showTargetCheckbox.isSelected()) {
//        TUSHAR
//            visualization.showTarget(true);
//            updateUIForShowTarget(true);           
        //}
    }

    private void enableDisableShowTargetCheckBox(boolean enable) {
        showTargetCheckbox.setEnabled(enable);
        showTargetLabel.setEnabled(enable);
//        TUSHAR
//        showTargetCheckbox.setSelected(false);
//        showTargetCheckbox.setSelected(true);
        
 //       showDragCheckbox.setEnabled(enable);
 //       showDragLabel.setEnabled(enable);
//        focusComboBox.setEnabled(enable);
//        focusLabel.setEnabled(enable);
        if(tree != null)
            tree.setEnabled(enable);
    }

    public void showTargetCbCliked() {
        boolean show = showTargetCheckbox.isSelected();
        visualization.showTarget(show);
        updateUIForShowTarget(show);
    }

    public void showFocusMarker() {      
        visualization.showFocusTarget(brightestStarList);
    }

    //
    //VisualizationStateListener overrides start ...
    //

    @Override
    public void vslTargetChanged(double ra, double dec, String raEntered, String decEntered) {
        updateUIForSetTarget(ra, dec, raEntered, decEntered);
        rebuildTreeOnConfigChange(visualization.getConfig());          
    }

    @Override
    public void vslShowTarget(boolean show) {
        updateUIForShowTarget(show);        
    }

    @Override
    public void vslConfigChanged(Config config) {
    }

    public void rebuildTreeOnConfigChange(Config config) {
        try {
            makeInstrumentTree(true, config);
            configHelper.fireInitialEvents();
        } catch (SomeException ex) {
            logger.error("!!!! Error making instrument tree", ex);
        }
    }

    //
    //VisualizationStateListener overrides end ...
    //

    //
    //VisualizationWorkPanelListener overrides start ...
    //

    private boolean stCbChanged = false;
    private long sizeOfImageBeingLoaded = -1;
    @Override
    public void backgroundImageLoadStarted() {
        sizeOfImageBeingLoaded = -1;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                imageLoadMsgLabel.setText("Fetching image ...");
                if(showTargetCheckbox.isSelected()) {
                    stCbChanged = true;
                    showTargetCheckbox.setSelected(false);
                }
                enableDisableShowTargetCheckBox(false);
            }
        });
    }

    @Override
    public void backgroundImageBytesRead(final long bytesRead) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if(sizeOfImageBeingLoaded == -1) {
                    imageLoadMsgLabel.setText("Read " + imageLoadBytesFormat.format(
                        (bytesRead) / 1024.0) + " KB");
                }
                else {
                    imageLoadMsgLabel.setText("Read " + imageLoadBytesFormat.format(
                        (bytesRead) / 1024.0) + " KB of "
                        + imageLoadBytesFormat.format(sizeOfImageBeingLoaded / 1024.0)
                        + " KB");
                }
            }
        });
    }

    @Override
    public void backgroundImageLoadFailed() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                imageLoadMsgLabel.setText(" ");
                enableDisableShowTargetCheckBox(true);
                if(stCbChanged) {
                    showTargetCheckbox.setSelected(true);
                    stCbChanged = false;
                }
            }
        });
    }

    @Override
    public void backgroundImageLoadCompleted() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {               
                imageLoadMsgLabel.setText(" ");
                enableDisableShowTargetCheckBox(true);
                if(stCbChanged) {
                    showTargetCheckbox.setSelected(true);
                    stCbChanged = false;
                }
            }
        });
    }

    @Override
    public void setImageSize(long length) {
        if(length > 0)
            sizeOfImageBeingLoaded = length;
        else
            sizeOfImageBeingLoaded = -1;
    }

    //
    //VisualizationWorkPanelListener overrides end ...
    //


    public void clearErrorMsgsAndRaDecFields()
    {
      raErrorMsgLabel.setText("");
      decErrorMsgLabel.setText("");
      sourceErrorMsgLabel.setText("");
      raTextField.setText("");
      decTextField.setText("");     
    }

    public ArrayList<PointInfoForXML> parsePrevXml(){
        final ArrayList<PointInfoForXML> prevInfoList = new ArrayList<PointInfoForXML>();
        try {           
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                              
                boolean ename = false;
                boolean cname = false;
                boolean ra = false;
                boolean dec = false;
                boolean pointId = false;
                boolean focus = false;
                boolean k_m = false;
                boolean r_mag = false;
                boolean jmag= false;
                PointInfoForXML pt;
                int cnt = 0;
                String value;

                //String[] tempRowData = new String[4];
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("Element")) {
                        // Get names and values for each attribute
                        pt = new PointInfoForXML();
                        value = attributes.getValue(0);
                        ename = true;
                    }
                    if (qName.equalsIgnoreCase("catalog")) {
                        cname = true;
                    }
                    if (qName.equalsIgnoreCase("ra")) {
                        ra = true;
                    }
                    if (qName.equalsIgnoreCase("dec")) {
                        dec = true;
                    }
                    if (qName.equalsIgnoreCase("ID")) {
                        pointId = true;
                    }
                    if (qName.equalsIgnoreCase("focus")) {
                        focus = true;
                    }
                    if (qName.equalsIgnoreCase("k_m")) {
                        k_m = true;
                    }
                    if (qName.equalsIgnoreCase("jmag")) {
                        jmag = true;
                    }
                    if (qName.equalsIgnoreCase("r_mag")) {
                        r_mag = true;
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {

                    if (ename) {
                        //rowData1[cnt][0] = new String(ch, start, length);
                        pt.setElementId(value);
                        ename = false;
                    }
                    if (cname) {
                        pt.setCatalogLabel(new String(ch, start, length));
                        cname = false;
                    }
                    if (ra) {
                        pt.setRa(new String(ch, start, length));
                        ra = false;
                    }
                    if (dec) {
                        pt.setDec(new String(ch, start, length));
                        dec = false;
                    }
                    if(pointId){
                        pt.setPointId(new String(ch, start, length));
                        pointId = false;
                    }
                    if(jmag){
                        pt.setMag(Double.parseDouble(new String(ch, start, length)));
                         jmag = false;
                    }
                    if(k_m){
                        pt.setMag(Double.parseDouble(new String(ch, start, length)));
                         k_m = false;
                    }
                    if(r_mag){
                        pt.setMag(Double.parseDouble(new String(ch, start, length)));
                         r_mag = false;
                    }
                    if(focus){
                        pt.setFocus(Integer.parseInt(new String(ch, start, length)));
                         focus = false;
                        prevInfoList.add(pt);
                    }
                }
                //rowData1[0][0]="";
                //rowData1[0][0]="";
            };

            File downloadCacheDir;
            String fName = visualization.getFileName();
            if (fName.contains("(")) {
                String id = fName.substring(fName.indexOf('(') + 1, fName.indexOf(')'));
                downloadCacheDir = new File(appContext.getLocalStorage().getDirectory(), "guideStarInfo" + id + ".xml");
            } else {
                downloadCacheDir = new File(appContext.getLocalStorage().getDirectory(), "guideStarInfo.xml");
            }
            saxParser.parse(downloadCacheDir, handler);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ParserConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return prevInfoList;
    }

     public ArrayList<PointInfoForXML> populateList(ArrayList<Catalog> catalogs,int flag){
                ArrayList<PointInfoForXML> infoList = new ArrayList<PointInfoForXML>();
                double raMin=Double.MAX_VALUE , decMin=Double.MAX_VALUE , magMin=Double.MAX_VALUE;
                String idMin = "",cLabel="";
                Catalog c = null;         
                Config conf = visualization.getConfig();
                //string form of ra, dec (lets say 23, 34 ..
                ArrayList<String> tips = new ArrayList<String>();
                if(isIrisSelected){
                    tips.add("iris.oiwfs.probe1.arm");
                    tips.add("iris.oiwfs.probe2.arm");
                    tips.add("iris.oiwfs.probe3.arm");
                    tips.add("nfiraos.twfs.detector");
                }
//                    else{
//                    tips.add("mobie.guider.guider");
//                }
                for(int j = 0; j<tips.size();j++){
                    Iterator iter = catalogs.iterator();
                    double distMin=Double.MAX_VALUE;
                    String value = conf.getConfigElementProperty(tips.get(j), "position");
                    String focus;
                    if(focusComboBox.getSelectedIndex()==-1)
                        focus=null;
                    else
                        focus = focusComboBox.getSelectedItem().toString();
                    if(conf.getConfig(tips.get(j)) != null){
                        String[] raDecCenter = value.split(",");
                        double ra= Double.parseDouble(raDecCenter[0])*Math.PI/180;
                        double dec= Double.parseDouble(raDecCenter[1])*Math.PI/180;
                        double dec1=Double.MAX_VALUE,ra1=Double.MAX_VALUE,temp1=Double.MAX_VALUE,temp2=Double.MAX_VALUE,tempDist=Double.MAX_VALUE;
                        while(iter.hasNext())
                        {
                            //double dec1=Double.MAX_VALUE,ra1=Double.MAX_VALUE,temp1=Double.MAX_VALUE,temp2=Double.MAX_VALUE,tempDist=Double.MAX_VALUE;
                            c = (Catalog)iter.next();
                            Object[][] data=c.getData();
                            for (int i = 0; i < data.length; i++) {
                                Point2D.Double pos = new Point2D.Double((Double)data[i][0],(Double)data[i][1]);
                                ra1 = pos.getX()*Math.PI/180;
                                dec1 = pos.getY()*Math.PI/180;
                                temp1 = Math.cos(Math.PI/2-dec)*Math.cos(Math.PI/2-dec1);
                                temp2 = Math.sin(Math.PI/2-dec)*Math.sin(Math.PI/2-dec1)*Math.cos(ra-ra1);
                                tempDist = Math.acos(temp1+temp2);
                                tempDist = tempDist*180/Math.PI;
                                if(distMin > tempDist){
                                    distMin = tempDist;
                                    raMin = ra1;
                                    decMin = dec1;
                                    magMin = (Double)data[i][2];
                                    idMin = data[i][3].toString();
                                    cLabel = c.getLabel();
                                }
                            }
                        }
                        raMin=((raMin*180)/Math.PI);
                        decMin=((decMin*180)/Math.PI);
                        String isVisible = visualization.getConfig().getConfigElementProperty(tips.get(j),"position");
                        PointInfoForXML ptInfo = new PointInfoForXML();                        
                        if(distMin<=(5/3600d)){
                            System.out.println("valid point:"+distMin);
                            String raString = DegreeCoverter.degToHMS(raMin);
                            String secString = raString.substring(raString.lastIndexOf(':')+1);
                            double secValue = Double.parseDouble(secString);
                            int temp = (int) (secValue * 1000.0); // scale it
                            secValue = (double) ((temp) / 1000.0);
                            String finalRaString = raString.substring(0,raString.lastIndexOf(':')+1)+secValue;
                            ptInfo.setRa(finalRaString);

                            String decString = DegreeCoverter.degToDMS(decMin);
                            secString = decString.substring(decString.lastIndexOf(':')+1);
                            secValue = Double.parseDouble(secString);
                            temp = (int) (secValue * 100.0); // scale it
                            secValue = (double) ((temp) / 100.0);
                            String finalDecString = decString.substring(0,decString.lastIndexOf(':')+1)+secValue;
                            ptInfo.setDec(finalDecString);
                            
                            ptInfo.setElementId(tips.get(j));
                            ptInfo.setCatalogLabel(cLabel);
                            ptInfo.setMag(magMin);
                            ptInfo.setPointId(idMin);
                            if(focus != null && tips.get(j).contains(focus))
                                ptInfo.setFocus(1);
                            else
                                ptInfo.setFocus(0);
                            ptInfo.setIsSelected(true);
                        }
                        else{
                            System.out.println("invalid point");

                            String raString = DegreeCoverter.degToHMS(((ra*180)/Math.PI));
                            String secString = raString.substring(raString.lastIndexOf(':')+1);
                            double secValue = Double.parseDouble(secString);
                            int temp = (int) (secValue * 1000.0); // scale it
                            secValue = (double) ((temp) / 1000.0);
                            String finalRaString = raString.substring(0,raString.lastIndexOf(':')+1)+secValue;
                            ptInfo.setRa(finalRaString);

                            String decString = DegreeCoverter.degToDMS((dec*180)/Math.PI);
                            secString = decString.substring(decString.lastIndexOf(':')+1);
                            secValue = Double.parseDouble(secString);
                            temp = (int) (secValue * 100.0); // scale it
                            secValue = (double) ((temp) / 100.0);
                            String finalDecString = decString.substring(0,decString.lastIndexOf(':')+1)+secValue;
                            ptInfo.setDec(finalDecString);

                            ptInfo.setElementId(tips.get(j));
                            ptInfo.setCatalogLabel("No catalog");
                            ptInfo.setMag(-99.9);
                            if(tips.get(j).contains("probe1"))
                                ptInfo.setPointId("probe1 center");
                            else if(tips.get(j).contains("probe2"))
                                ptInfo.setPointId("probe2 center");
                            else if(tips.get(j).contains("probe3"))
                                ptInfo.setPointId("probe3 center");
                            else if(tips.get(j).contains("twfs"))
                                ptInfo.setPointId("TWFS Detector");
                            else
                                ptInfo.setPointId("Mobie Guider");
                            if(focus != null && tips.get(j).contains(focus))
                                ptInfo.setFocus(1);
                            else
                                ptInfo.setFocus(0);
                            ptInfo.setIsSelected(true);
                        }
                         infoList.add(ptInfo);                 
               }else if(flag == 1 && conf.getConfig(tips.get(j)) == null){
                        //default values
                        PointInfoForXML pt1 = new PointInfoForXML();
                        pt1.setCatalogLabel("null");
                        pt1.setDec(DegreeCoverter.degToDMS(0.0));
                        if(focus != null && tips.get(j).contains(focus))
                                pt1.setFocus(1);
                            else
                                pt1.setFocus(0);
                        pt1.setMag(-99.9);
                        pt1.setRa(DegreeCoverter.degToHMS(0.0));
                        pt1.setPointId("null");
                        pt1.setElementId(tips.get(j));
                        infoList.add(pt1);
               }else if(flag != 1 && conf.getConfig(tips.get(j)) == null){
                        //load old values
                        PointInfoForXML pt1 = new PointInfoForXML();
                        boolean addPoint=false;
                        for(int tp=0;tp<prevInfoList.size();tp++){
                            if(prevInfoList.get(tp).getElementId().equalsIgnoreCase(tips.get(j))){
                                addPoint=true;
                            }
                        }
                        if(addPoint){
                        //the size of the prev infolist will never exceed 4
                        //the check is written because in case the size is less than 4
                        //it means that the xml also doesnt have a particular entry
                        //which may throw a null pointer
                        if(prevInfoList.size()>0 && j<prevInfoList.size()-1){
                            pt1.setCatalogLabel(prevInfoList.get(j).getCatalogLabel());
                            pt1.setDec(prevInfoList.get(j).getDec());
                            pt1.setFocus(prevInfoList.get(j).getFocus());
                            pt1.setMag(prevInfoList.get(j).getMag());
                            pt1.setRa(prevInfoList.get(j).getRa());
                            pt1.setPointId(prevInfoList.get(j).getPointId());
                            pt1.setElementId(tips.get(j));
                            infoList.add(pt1);
                        }
                        }
               }

         }
               return infoList;
    }

     public String[][] populateDataForTable(){
        final String[][] rowData1 = new String[5][4];
        
        try {           
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                boolean ename = false;
                boolean cname = false;
                boolean ra = false;
                boolean dec = false;
                boolean sRa = false;
                boolean sDec = false;
                int cnt = 0;
                String value;
                //String[] tempRowData = new String[4];
                @Override
                public void startElement(String uri, String localName,String qName,
                Attributes attributes) throws SAXException {              
                if (qName.equalsIgnoreCase("Element")) {
                    // Get names and values for each attribute
                    value = attributes.getValue(0);
                    ename = true;
                }
                if (qName.equalsIgnoreCase("catalog")) {
                    cname = true;
                }
                if (qName.equalsIgnoreCase("ra")) {
                    ra = true;
                }
                if (qName.equalsIgnoreCase("dec")) {
                    dec = true;
                }
                if (qName.equalsIgnoreCase("SourceRa")) {
                    sRa = true;
                }
                if (qName.equalsIgnoreCase("SourceDec")) {
                    sDec = true;
                }
                if (qName.equalsIgnoreCase("Source")) {
                    ename = true;
                    cname = true;
                }
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {                  
                if (ename) {                 
                    //rowData1[cnt][0] = new String(ch, start, length);
                    if(cnt == 0)
                        rowData1[cnt][0]="base position";
                    else
                    rowData1[cnt][0] = value;
                    ename = false;
                }
                if (cname) {
                    if(cnt == 0)
                        rowData1[cnt][1]="null";
                    else
                    rowData1[cnt][1] = new String(ch, start, length);
                    cname = false;
                }
                if (ra) {
                    rowData1[cnt][2] = new String(ch, start, length);
                    ra = false;
                }
                if (dec) {
                    rowData1[cnt][3] = new String(ch, start, length);
                    cnt++;
                    dec = false;
                }
                if(sRa){
                    rowData1[cnt][2] = new String(ch, start, length);
                    sRa = false;
                }
                if (sDec) {
                    rowData1[cnt][3] = new String(ch, start, length);
                    cnt++;
                    sDec = false;
                }
            }
            //rowData1[0][0]="";
          };
          File downloadCacheDir;
          String fName = visualization.getFileName();
                if(fName.contains("(")){
                   String id = fName.substring(fName.indexOf('(')+1, fName.indexOf(')'));
                   downloadCacheDir =
                    new File(appContext.getLocalStorage().getDirectory(),
                    "guideStarInfo"+id+".xml");
                }else{
                    downloadCacheDir =
                    new File(appContext.getLocalStorage().getDirectory(),
                    "guideStarInfo.xml");
                }
          saxParser.parse(downloadCacheDir, handler);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ParserConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return rowData1;
     }

    public void resolveName(String type) {
        double[] tempRaDec;
        String sName = sourceTextField.getText();

        if (type.equals("Simbad")) {
            try {
                tempRaDec = NameResolver.getCoordsFromSimbad(sName);
                raTextField.setText("" + tempRaDec[0]);
                decTextField.setText("" + tempRaDec[1]);
            } catch (Exception e) {
                //Could not resolve. Suppress exception and continue resolving
                JOptionPane.showMessageDialog(showTargetPanel,
                        "<html><body>Could not resolve.<br/>" +
                        "This might be a problem with http proxy settings.<br/>" +
                        "To set http proxy goto File->Proxy Settings");
                logger.warn("Could not resolve ", e);
            }
        } else if (type.equals("NED")) {
            try {
                tempRaDec = NameResolver.getCoordsFromNED(sName);
                raTextField.setText("" + tempRaDec[0]);
                decTextField.setText("" + tempRaDec[1]);
            } catch (Exception e) {
                //Could not resolve. Suppress exception and continue resolving
                JOptionPane.showMessageDialog(showTargetPanel,
                        "<html><body>Could not resolve.<br/>" +
                        "This might be a problem with http proxy settings.<br/>" +
                        "To set http proxy goto File->Proxy Settings");
                logger.warn("Could not resolve ", e);
            }
        }
    }

    private void updateUIForSetTarget(Double ra, Double dec, String raEntered,
            String decEntered) {
        //TODO: parameterised message should come from resource map
        //TODO: This being event handler we cannot rely on text field values
        // RA, DEC format should be preserved in the model.
//        if(raEntered == null && decEntered == null)
//            targetLabel.setText("Target: " + ra + ",  " + dec);
//        else
//            targetLabel.setText("Target: " + raEntered + ",  " + decEntered);
        enableDisableShowTargetCheckBox(true);
        //also show the marker
        //showTargetCheckbox.setSelected(true);
    }

    private void updateUIForShowTarget(Boolean show) {
        if(show) {
            if(!showTargetCheckbox.isSelected())
                showTargetCheckbox.setSelected(true);
        } else {
            if(showTargetCheckbox.isSelected())
                showTargetCheckbox.setSelected(false);
        }
    }

    public void clearAndInitializeConfigStructures(Config config) {
        config.addConfigListener(this);
    }

    @Override
    public void updateConfigElementValue(String confElementId, Value value, boolean isDisplayElement) {       
        
    }

    @Override
    public void updateConfigElementProperty(String confElementId, String propKey, String propValue) {
        //do nothing
    }

    @Override
    public void enableConfig(String confElementId, boolean enable, boolean isDisplayElement) {
         if(confElementId.equalsIgnoreCase("iris.sweetspot.group") && enable){
              traverse();
          }
          if(confElementId.equalsIgnoreCase("iris.sciencedetector") && enable){
              isImagingMode = true;
              traverse();
          }
          if(confElementId.equalsIgnoreCase("iris.sciencedetector") && !enable){
              isImagingMode = false;
              traverse();
          }
          if(confElementId.equalsIgnoreCase("iris.ifuimager") && enable){
//              showDragLabel.setEnabled(true);
//              showDragCheckbox.setEnabled(true);
              isIfuMode = true;
              traverse();
          }
          if(confElementId.equalsIgnoreCase("iris.ifuimager") && !enable){
//              showDragLabel.setEnabled(false);
//              showDragCheckbox.setEnabled(false);
//              showDragCheckbox.setSelected(false);
              isIfuMode = false;
              traverse();
          }
          if(confElementId.equalsIgnoreCase("iris") && enable){
              focusComboBox.setEnabled(true);
              focusLabel.setEnabled(true);
              showDragLabel.setEnabled(true);
              showDragCheckbox.setEnabled(true);
              isIrisSelected=true;
              fetchButton.setEnabled(true);
              autoSuggestGuideStars.setEnabled(true);
              traverse();
              //showBrightestStar();
          }else if(confElementId.equalsIgnoreCase("iris") && !enable){
              focusComboBox.setEnabled(false);
              focusLabel.setEnabled(false);
              isIrisSelected=false;
              traverse();
          }else if(confElementId.equalsIgnoreCase("mobie") && enable){
              showDragLabel.setEnabled(true);
              showDragCheckbox.setEnabled(true);
              traverse();
              fetchButton.setEnabled(true);
              //autoSuggestGuideStars.setEnabled(true);
              //showBrightestStar();
          }
          if(confElementId.contains("oiwfs")){
              if(confElementId.contains("probe1") && enable){
                    isProbe1Selected=true;
              }
              if(confElementId.contains("probe1") && !enable){
                    isProbe1Selected=false;
              }
              else if(confElementId.contains("probe2") && enable)
              {
                    isProbe2Selected=true;
              }
              else if(confElementId.contains("probe2") && !enable)
              {
                    isProbe2Selected=false;
              }
              else if(confElementId.contains("probe3") && enable)
              {
                    isProbe3Selected=true;
              }
              else if(confElementId.contains("probe3") && !enable)
              {
                    isProbe3Selected=false;
              }
              if(isProbe1Selected || isProbe2Selected || isProbe3Selected){
                  focusComboBox.setEnabled(true);
                  focusLabel.setEnabled(true);
                  traverse();
              }else{
                  focusComboBox.setEnabled(false);
                  focusLabel.setEnabled(false);
              }


          }
       if(confElementId.equalsIgnoreCase("mobie.drag") || confElementId.equalsIgnoreCase("iris.ifuimager.drag")){
           traverse();
           tree.repaint();
       }       
    }

    @Override
    public void vslShowFocus(ArrayList<Object> pointsInLimits) {
        //do nothing
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void showBrightestStar() {        
        ArrayList<String> tips = new ArrayList<String>();
       if(isIrisSelected){
            Value bValue=visualization.getConfig().getConfig("iris.oiwfs.probe1.limits");
            if(bValue != null && ((BooleanValue)bValue).getValue() == true)
            tips.add("iris.oiwfs.probe1.limits");
            bValue=visualization.getConfig().getConfig("iris.oiwfs.probe2.limits");
            if(bValue != null && ((BooleanValue)bValue).getValue() == true)
            tips.add("iris.oiwfs.probe2.limits");
            bValue=visualization.getConfig().getConfig("iris.oiwfs.probe3.limits");
           
            if(bValue != null && ((BooleanValue)bValue).getValue() == true)
            tips.add("iris.oiwfs.probe3.limits");
        }
//       else{
//             Value bValue=visualization.getConfig().getConfig("mobie.guider.limits");
//              if(bValue != null && ((BooleanValue)bValue).getValue() == true)
//            tips.add("mobie.guider.limits");
//        }
        for(int j=0;j<tips.size();j++){
            ArrayList<Catalog> catalogs = visualization.getCatalogs();
            Catalog c = null;
            Iterator iter = catalogs.iterator();
            pointsInLimits = new ArrayList<Object>();
            double dec1=Double.MAX_VALUE,ra1=Double.MAX_VALUE,temp1=Double.MAX_VALUE,
                temp2=Double.MAX_VALUE,distFromPieCentre=Double.MAX_VALUE,distFromOpenCenter=Double.MAX_VALUE,
                temp3=Double.MAX_VALUE,temp4=Double.MAX_VALUE;
            if(isIrisSelected){
                String pieCenter = visualization.getConfig().getConfigElementProperty(tips.get(j), "position");
                String openCenter = visualization.getConfig().getConfigElementProperty(tips.get(j), "position1");
                String[] raDecCenterForPie = pieCenter.split(",");
                double raForPie= Double.parseDouble(raDecCenterForPie[0])*Math.PI/180;
                double decForPie= Double.parseDouble(raDecCenterForPie[1])*Math.PI/180;
                String[] raDecCenterForOpen = openCenter.split(",");
                double raForOpen= Double.parseDouble(raDecCenterForOpen[0])*Math.PI/180;
                double decForOpen= Double.parseDouble(raDecCenterForOpen[1])*Math.PI/180;

            while(iter.hasNext())
            {
                c = (Catalog)iter.next();
            }
            Object[][] data=c.getData();
            for (int i = 0; i < data.length; i++) {

                Point2D.Double pos = new Point2D.Double((Double)data[i][0],(Double)data[i][1]);
                ra1 = pos.getX()*Math.PI/180;
                dec1 = pos.getY()*Math.PI/180;

                temp1 = Math.cos(Math.PI/2-decForPie)*Math.cos(Math.PI/2-dec1);
                temp2 = Math.sin(Math.PI/2-decForPie)*Math.sin(Math.PI/2-dec1)*Math.cos(raForPie-ra1);
                distFromPieCentre = Math.acos(temp1+temp2);
                distFromPieCentre = distFromPieCentre*180/Math.PI;

                temp3 = Math.cos(Math.PI/2-decForOpen)*Math.cos(Math.PI/2-dec1);
                temp4 = Math.sin(Math.PI/2-decForOpen)*Math.sin(Math.PI/2-dec1)*Math.cos(raForOpen-ra1);
                distFromOpenCenter = Math.acos(temp3+temp4);
                distFromOpenCenter = distFromOpenCenter*180/Math.PI;

                if(distFromPieCentre < 2.375998076/60d && distFromOpenCenter < 1/60d){
                    pointsInLimits.add(data[i]);
                }
            }
//            }
            sortByMag();
          }
//            else{
//            String pieCenter = visualization.getConfig().getConfigElementProperty(tips.get(j), "position");
//
//            String[] raDecCenterForPie = pieCenter.split(",");
//            double raForPie= Double.parseDouble(raDecCenterForPie[0])*Math.PI/180;
//            double decForPie= Double.parseDouble(raDecCenterForPie[1])*Math.PI/180;
//
//            while(iter.hasNext())
//            {
//                c = (Catalog)iter.next();
//                Object[][] data=c.getData();
//                for (int i = 0; i < data.length; i++) {
//
//                    Point2D.Double pos = new Point2D.Double((Double)data[i][0],(Double)data[i][1]);
//                    ra1 = pos.getX()*Math.PI/180;
//                    dec1 = pos.getY()*Math.PI/180;
//
//                    temp1 = Math.cos(Math.PI/2-decForPie)*Math.cos(Math.PI/2-dec1);
//                    temp2 = Math.sin(Math.PI/2-decForPie)*Math.sin(Math.PI/2-dec1)*Math.cos(raForPie-ra1);
//                    distFromPieCentre = Math.acos(temp1+temp2);
//                    distFromPieCentre = distFromPieCentre*180/Math.PI;
//
//                    if( distFromPieCentre < 1.9/60d && distFromOpenCenter > 0.7/60d ){
//                        pointsInLimits.add(data[i]);
//                    }
//                }
//            }
//            sortByMag();
//          }
        }
        showFocusMarker();
    }

    private void sortByMag() {
       double magMax=Double.MAX_VALUE;
       int index=0;
       for(int i=0;i<pointsInLimits.size();i++){
           Object[] temp = (Object[]) pointsInLimits.get(i);
           if((Double)temp[2]<magMax && (Double)temp[2]>0){
               magMax =(Double)temp[2];
               index=i;
           }           
       }
       Object[] brightestObject=(Object[]) pointsInLimits.get(index);
       brightestStarList.add(brightestObject);
    }
    @Override
    public void capturePostions(String confElementId,Value value) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

//    /**
//     * As of also enables and selects the target checkbox
//     *
//     * @param center
//     */
//    public void setCenter(Point2D.Double center){
//        showTargetLabel.setEnabled(true);
//        showTargetCheckbox.setEnabled(true);
//        showTargetCheckbox.setSelected(true);
//        stCbChanged=true;
//        targetLabel.setText("Target: " + center.x + ",  " + center.y);
//    }
//
}

