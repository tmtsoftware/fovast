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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.util.HashMap;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.tmt.fovast.mvc.ChangeListener;
import org.tmt.fovast.controller.VisualizationController;
import org.tmt.fovast.state.VisualizationState;

/**
 *
 * @author vivekananda_moosani
 */
public class VisualizationControlPanel extends JPanel
        implements ChangeListener, VisualizationWorkPanelListener {

    private ApplicationContext appContext;

    ApplicationActionMap actionMap;

    private JTextField raTextField;

    private JTextField decTextField;

    private JLabel raErrorMsgLabel;

    private JLabel decErrorMsgLabel;

    private JLabel targetLabel;

    private JLabel imageLoadMsgLabel;

    private JPanel instrumentConfigPanel;
    
    private final VisualizationController controller;

    private NumberFormat imageLoadBytesFormat;

    private JCheckBox showTargetCheckbox;

    private JLabel showTargetLabel;

    public VisualizationControlPanel(ApplicationContext appContext,
            VisualizationController controller) {
        this.appContext = appContext;
        this.controller = controller;
        //actionMap = appContext.getActionMap(new VisualizationActions(this));
        initComponents();

        controller.addChangeListener(this);

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

        setPreferredSize(new Dimension(200, 200));
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10));

        //raDecPanel & contents
        //
        JPanel raDecPanel = new JPanel();

        //ra, dec field labels
        JLabel raLabel = new JLabel("RA:");
        JLabel decLabel = new JLabel("DEC:");

        //ra, dec text fields
        raTextField = new JTextField(20);
        raTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                validateRaFieldAndShowErrorMsgField();
            }

        });
        decTextField = new JTextField(20);
        decTextField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                validateDecFieldAndShowErrorMsgField();
            }

        });

        //ra, dec after labels
        //TODO: deg constraint to be removed ... 
        JLabel raAfterLabel = new JLabel("(deg - J2000/FK5)");
        JLabel decAfterLabel = new JLabel("(deg - J2000/FK5)");

        //ra, dec error msg labels
        raErrorMsgLabel = new JLabel();
        raErrorMsgLabel.setForeground(Color.RED);
        decErrorMsgLabel = new JLabel();
        decErrorMsgLabel.setForeground(Color.RED);

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
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        raDecPanel.setLayout(raDecPanelLayout);
        //raLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        raDecPanel.add(raLabel, gbc);

        //TODO: Text field size increases if split pane is expanded. Need to fix
        gbc.gridx = 1;
        Dimension raDecTextFieldDimension = new Dimension(100, 30);
        raTextField.setPreferredSize(raDecTextFieldDimension);
        raTextField.setMinimumSize(raDecTextFieldDimension);
        raTextField.setMaximumSize(raDecTextFieldDimension);
        //raTextField.setBorder(BorderFactory.createLineBorder(Color.black));
        raDecPanel.add(raTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0);
        //decLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        raDecPanel.add(decLabel, gbc);

        gbc.gridx = 1;
        decTextField.setPreferredSize(raDecTextFieldDimension);
        decTextField.setMinimumSize(raDecTextFieldDimension);
        decTextField.setMaximumSize(raDecTextFieldDimension);
        //decTextField.setBorder(BorderFactory.createLineBorder(Color.black));
        raDecPanel.add(decTextField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        raDecPanel.add(raAfterLabel, gbc);

        gbc.gridy = 1;
        raDecPanel.add(decAfterLabel, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 0, 0);
        raDecPanel.add(raErrorMsgLabel, gbc);

        gbc.gridy = 3;
        raDecPanel.add(decErrorMsgLabel, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 0, 0, 0);
        raDecPanel.add(panButton, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 0, 0, 0);
        raDecPanel.add(targetLabel, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 0, 20, 0);
        raDecPanel.add(imageLoadMsgLabel, gbc);

        //raDecPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        add(raDecPanel, BorderLayout.NORTH);

        //
        //instrumentConfigPanel
        instrumentConfigPanel = new JPanel();

        JLabel showBackgroundLabel = new JLabel("Show background image");
        JCheckBox showBackgroundCheckbox = new JCheckBox();

        showTargetLabel = new JLabel("Show target marker");
        showTargetLabel.setEnabled(false);
        showTargetCheckbox = new JCheckBox();
        showTargetCheckbox.setEnabled(false);
        showTargetCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showTargetCbCliked();
            }
        });

        //instrumentConfigPanel layout
        instrumentConfigPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcIcp = new GridBagConstraints();

        gbcIcp.gridx = 0;
        gbcIcp.gridy = 0;
        gbcIcp.gridwidth = GridBagConstraints.RELATIVE;
        gbcIcp.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        //showTargetCheckbox.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        instrumentConfigPanel.add(showTargetCheckbox, gbcIcp);

        gbcIcp.gridx = 1;
        gbcIcp.gridy = 0;
        gbcIcp.gridwidth = GridBagConstraints.REMAINDER;
        gbcIcp.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        //showTargetLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        instrumentConfigPanel.add(showTargetLabel, gbcIcp);

        gbcIcp.gridx = 0;
        gbcIcp.gridy = 1;
        gbcIcp.weightx = 1;
        gbcIcp.weighty = 1;
        instrumentConfigPanel.add(new JLabel(" "), gbcIcp);
        
        add(instrumentConfigPanel, BorderLayout.CENTER);       
    }

    private void validateRaFieldAndShowErrorMsgField() {
        Double ra = null;
        try {
            String text = raTextField.getText().trim();
            if (text.length() == 0) {
                return;
            }

            //TODO: to be replaced by RaDecUtil methods later.
            ra = Double.parseDouble(text);
            raErrorMsgLabel.setText("");
            if (!(ra >= 0 && ra < 360)) {
                //TODO: should be from resource file
                raErrorMsgLabel.setText("RA should be in [0, 360)");
            } else {
                return;
            }
        } catch (NumberFormatException ex) {
            //TODO: should be from resource file
            raErrorMsgLabel.setText("RA should be a numeric value");
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

            //TODO: to be replaced by RaDecUtil methods later.
            dec = Double.parseDouble(text);
            decErrorMsgLabel.setText("");
            if (!(dec >= -90 && dec <= 90)) {
                //TODO: should be from resource file
                decErrorMsgLabel.setText("DEC should be in [-180, 180]");
            } else {
                return;
            }
        } catch (NumberFormatException ex) {
            //TODO: should be from resource file
            decErrorMsgLabel.setText("DEC should be a numeric value");
        }
        decTextField.setText("");
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

        controller.setTarget(
                Double.parseDouble(raTextField.getText()),
                Double.parseDouble(decTextField.getText()));

        if(showTargetCheckbox.isSelected()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                   //controller.showTarget(true);
                }
            });
        }
    }

    private void enableDisableShowTargetCheckBox(boolean enable) {
        showTargetCheckbox.setEnabled(enable);
        showTargetLabel.setEnabled(enable);
    }

    public void showTargetCbCliked() {
        controller.showTarget(showTargetCheckbox.isSelected());
    }

    @Override
    public void update(Object source, String eventKey, HashMap<String, Object> args) {

        if (source.equals(controller)) {
            if (eventKey.equals(VisualizationState.TARGET_CHANGED_EVENT_KEY)) {
                Double ra = (Double) args.get(VisualizationState.TARGET_RA_ARG_KEY);
                Double dec = (Double) args.get(VisualizationState.TARGET_DEC_ARG_KEY);

                //TODO: parameterised message should come from resource map
                targetLabel.setText("Target: " + ra + ",  " + dec);
                enableDisableShowTargetCheckBox(true);
            }
        } else {
            throw new RuntimeException("Event from source uninterested in: " +
                    source.toString());
        }

        //TODO: As of now nothing to do for viscontrolpanel - update()
        //throw new UnsupportedOperationException("Not supported yet.");

        //TODO: Should not throw runtimeexceptions if and unwanted event key is encountered
        //this has to be done else where update is implemented
    }

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
}



