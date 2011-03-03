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
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.tree.*;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import org.jdom.JDOMException;
import voi.astro.util.NameResolver;

import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdom.Element;
import org.jfree.date.SpreadsheetDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.state.VisualizationState;
import org.tmt.fovast.astro.util.DegreeCoverter;
import org.tmt.fovast.instrumentconfig.FovastConfigHelper;

/**
 *
 */
public class VisualizationControlPanel extends JPanel
        implements VisualizationState.VisualizationStateListener,
        VisualizationWorkPanelListener {

    private static Logger logger = LoggerFactory.getLogger(VisualizationControlPanel.class);

    private final static String INSTRUMENT_CONTROL_XML = "resources/InstrumentControl.xml";
    
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
       URL urlString=VisualizationControlPanel.class.getResource("resources/help.gif");
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

        JPanel configPanel = new JPanel(new BorderLayout());
        configPanel.add(showTargetPanel, BorderLayout.NORTH);
        try {
            JTree tree = makeInstrumentTree();
            configPanel.add(tree, BorderLayout.CENTER);
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

    private JTree makeInstrumentTree() throws FovastInstrumentTree.SomeException {

        FovastConfigHelper configHelper = new FovastConfigHelper();
        FovastInstrumentTree instrumentTree = new FovastInstrumentTree(configHelper);
        return instrumentTree.getJTree();
        
//        FovastInstrumentGuiConfig
//                fovastInstrumentConfig =
//                FovastInstrumentGuiConfig.getFovastInstrumentConfig(
//                VisualizationControlPanel.class.getResource(INSTRUMENT_CONTROL_XML));
//
//        DefaultMutableTreeNode rootNode =
//                new DefaultMutableTreeNode("Instrument Config");
//
////        List<Element> children = document.getRootElement().getChildren();
////        makeInstrumentTree(rootNode, children);
//
//        JTree instrumentTree = new JTree(rootNode);
//        DefaultTreeCellRenderer defaultTreeCellRenderer =
//                new DefaultTreeCellRenderer();
//        defaultTreeCellRenderer.setOpenIcon(null);
//        defaultTreeCellRenderer.setClosedIcon(null);
//        defaultTreeCellRenderer.setLeafIcon(null);
//        defaultTreeCellRenderer.setBackground(raAfterLabel.getBackground());
//        defaultTreeCellRenderer.setBackgroundNonSelectionColor(raAfterLabel.getBackground());
//        //defaultTreeCellRenderer.setBackgroundSelectionColor(Color.RED);
//        instrumentTree.setCellRenderer(defaultTreeCellRenderer);
//        instrumentTree.setBackground(raAfterLabel.getBackground());
//
//        return instrumentTree;
    }

    private void makeInstrumentTree(DefaultMutableTreeNode node,
            List<Element> elements) {
        for(int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String eleId = ele.getAttributeValue("id");
            String eleLabel = ele.getAttributeValue("id");
            if(eleLabel == null)
                eleLabel = eleId;
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(eleLabel);
            node.add(childNode);
        }
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
    }

    @Override
    public void vslShowTarget(boolean show) {
        updateUIForShowTarget(show);
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


