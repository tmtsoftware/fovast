/*
 *  Copyright 2011 TMT.
 *
 *  License and source copyright header text to be decided
 *
 */
package org.tmt.fovast.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
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

    private final static String INSTRUMENT_CONTROL_XML = "resources/InstrumentControl.xml";

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

    private JLabel showTargetLabel;

    private  JLabel raFormatLabel;

    private  JLabel decFormatLabel;

    private JLabel raAfterLabel;

    private JLabel decAfterLabel;

    private JTree tree;

    private JPanel configPanel;

    private ConfigHelper configHelper;

    private JButton fetchButton;

    private JTable pointInfoTable;

    public static final String DOWNLOAD_CACHE_DIR = "guideStarInfo.xml";
    
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
            }

        });
        //TODO: Set action for setting target and for taking props from res file
        //panButton.setAction();

        //target label
        targetLabel = new JLabel();

        //imageLoadStatusLabel
        imageLoadMsgLabel = new JLabel(" ");

        fetchButton = new JButton("Capture Points");
        //fetchButton.setEnabled(false);
        fetchButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {

               ArrayList<Catalog> catalogs = visualization.getCatalogs();
               Double[] center = visualization.getTarget();
               double ra = center[0];
               double dec = center[1];
               //loop through catalogs
                XMLFileGenerator xf = new XMLFileGenerator();
                String fName = visualization.getFileName();
                if(fName.contains("(")){
                   String id = fName.substring(fName.indexOf('(')+1, fName.indexOf(')'));
                   xf.generateXML(populateList(catalogs),ra,dec,Integer.parseInt(id));
                }else{
                    xf.generateXML(populateList(catalogs),ra,dec,0);
                }
                String[] columnNames ={"Element Name","Catalog Name","RA","DEC"};
                String[][] rowData =new String[4][4];
                rowData = populateDataForTable();
                pointInfoTable = new JTable(rowData, columnNames);
                pointInfoTable.setEnabled(false);
                JScrollPane pane = new JScrollPane(pointInfoTable,JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                pane.setPreferredSize(new Dimension(300, 100));
//                pane.setMaximumSize(new Dimension(100, 50));
//                pane.setMinimumSize(new Dimension(100, 50));
                configPanel.add(pane,BorderLayout.CENTER);
                configPanel.repaint();
                configPanel.validate();
          }
       });

//       saveButton = new JButton("Save");
//        saveButton.addActionListener(new ActionListener() {
//           public void actionPerformed(ActionEvent event) {
//               XMLFileGenerator xf = new XMLFileGenerator();
//               xf.saveXML(appContext);
//            }
//       });

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


        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        raDecPanel.add(targetLabel, gbc);

        gbc.gridy = 8;
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

    private void makeInstrumentTree(boolean enabled, Config config)
            throws FovastInstrumentTree.SomeException {

        if(tree != null)
            configPanel.remove(tree);
        FovastInstrumentTree instrumentTree = null;
        if(config != null) {
            configHelper = new ConfigHelper(config);
            instrumentTree = new FovastInstrumentTree(configHelper);
            config.addConfigListener(this);
        }
        else {
            instrumentTree = new FovastInstrumentTree(null);
        }
        tree = instrumentTree.getJTree();
        tree.setOpaque(true);
        tree.setBackground(configPanel.getBackground());
        tree.setEnabled(enabled);
        configPanel.add(tree, BorderLayout.SOUTH);
        configPanel.revalidate();

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
            visualization.showTarget(true);
            updateUIForShowTarget(true);
        //}
    }

    private void enableDisableShowTargetCheckBox(boolean enable) {
        showTargetCheckbox.setEnabled(enable);
        showTargetLabel.setEnabled(enable);
        if(tree != null)
            tree.setEnabled(enable);
    }

    public void showTargetCbCliked() {
        boolean show = showTargetCheckbox.isSelected();
        visualization.showTarget(show);
        updateUIForShowTarget(show);
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

     public ArrayList<PointInfoForXML> populateList(ArrayList<Catalog> catalogs){
                ArrayList<PointInfoForXML> infoList = new ArrayList<PointInfoForXML>();
                double raMin=Double.MAX_VALUE , decMin=Double.MAX_VALUE , magMin=Double.MAX_VALUE;
                String idMin = "";
                Catalog c = null;
                
              
                Config conf = visualization.getConfig();
                //string form of ra, dec (lets say 23, 34 ..
                ArrayList<String> tips = new ArrayList<String>();
                tips.add("iris.oiwfs.probe1.arm");
                tips.add("iris.oiwfs.probe2.arm");
                tips.add("iris.oiwfs.probe3.arm");
                tips.add("nfiraos.twfs.detector");
                if(catalogs.isEmpty()== true){
                    JOptionPane.showMessageDialog(showTargetPanel,"Load a catalog first");
                }
                for(int j = 0; j<tips.size();j++){
                    Iterator iter = catalogs.iterator();
                    double distMin=Double.MAX_VALUE;
                    String value = conf.getConfigElementProperty(tips.get(j), "position");
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
                            }
                        }
                    }
                    raMin=((raMin*180)/Math.PI);
                    decMin=((decMin*180)/Math.PI);
                    PointInfoForXML ptInfo = new PointInfoForXML();
                    if(distMin<=((Math.sqrt(2.0))*2)/3600d){
                        System.out.println("valid point:"+distMin);
                        ptInfo.setRa(raMin);
                        ptInfo.setDec(decMin);
                        ptInfo.setElementId(tips.get(j));
                        ptInfo.setCatalogLabel(c.getLabel());
                        ptInfo.setJmag(magMin);
                        ptInfo.setPointId(idMin);
                        ptInfo.setFocus(0); // TO BE DONE
                    }
                    else{
                        System.out.println("invalid point");
                        ptInfo.setRa(((ra*180)/Math.PI));
                        ptInfo.setDec(((dec*180)/Math.PI));
                        ptInfo.setElementId(tips.get(j));
                        ptInfo.setCatalogLabel("No catalog");
                        ptInfo.setJmag(-99.9);
                        if(tips.get(j).contains("probe1"))
                            ptInfo.setPointId("probe1 center");
                        else if(tips.get(j).contains("probe2"))
                            ptInfo.setPointId("probe2 center");
                        else if(tips.get(j).contains("probe3"))
                            ptInfo.setPointId("probe3 center");
                        else
                            ptInfo.setPointId("TWFS Dectector");

                        ptInfo.setFocus(0); // TO BE DONE
                    }
                   infoList.add(ptInfo);
               }
               return infoList;
    }

     public String[][] populateDataForTable(){
        final String[][] rowData1 = new String[4][4];
        
        try {           
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                boolean ename = false;
                boolean cname = false;
                boolean ra = false;
                boolean dec = false;
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
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                if (ename) {                 
                    //rowData1[cnt][0] = new String(ch, start, length);
                    rowData1[cnt][0] = value;
                    ename = false;
                }
                if (cname) {
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
        if(raEntered == null && decEntered == null)
            targetLabel.setText("Target: " + ra + ",  " + dec);
        else
            targetLabel.setText("Target: " + raEntered + ",  " + decEntered);
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
//        if(confElementId.equals("iris.oiwfs.probe1.arm") ||
//                confElementId.equals("iris.oiwfs.probe2.arm") ||
//                confElementId.equals("iris.oiwfs.probe3.arm")){
//            BooleanValue val=(BooleanValue)value;
//                  fetchButton.setEnabled(val.getValue());
//
//        }   
    }

    @Override
    public void updateConfigElementProperty(String confElementId, String propKey, String propValue) {
        //do nothing
    }

    @Override
    public void enableConfig(String confElementId, boolean enable, boolean isDisplayElement) {      
//        if(confElementId.equals("iris.oiwfs.probe1.arm") ||
//                confElementId.equals("iris.oiwfs.probe2.arm") ||
//                confElementId.equals("iris.oiwfs.probe3.arm")){
//            fetchButton.setEnabled(enable);
//        }
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


