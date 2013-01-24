import fr.dgac.ivy.IvyException;
import fr.lri.swingstates.canvas.CShape;
import fr.lri.swingstates.canvas.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** 
 * GUI of the environmental simulator.
 * Made with the Netbeans GUI Builder Matisse. Represents the view of
 * the MVC model.
 *
 * @author Hugo
 * @version 1.0
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        /* Launches the IvyListener */
        map = new Map();
        mapControler = new MapControler(jPanelMap, jPanelThermalColumn, 
                jPanelThermalColumnView, jToggleButtonThermalColumn, 
                jSliderMaximumSpeed, jLabelMaximumSpeed, 
                jLabelCoordinatesValues, jLabelGeoCoordinatesValues, map);
        vpControler = new VerticalProfileControler(jPanelVerticalProfile, map);
        try {
            IvyListener ivy = new IvyListener(map, mapControler);
        } catch (IvyException ex) {
            System.out.println("Unable to connect to the Ivy Bus : ");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Initialization of the view.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelMenu = new javax.swing.JPanel();
        jToggleButtonVerticalProfile = new javax.swing.JToggleButton();
        jToggleButtonThermalColumn = new javax.swing.JToggleButton();
        jToggleButtonTurbulences = new javax.swing.JToggleButton();
        jPanelMap = new javax.swing.JPanel();
        jPanelPropertiesEditor = new javax.swing.JPanel();
        jPanelEmpty = new javax.swing.JPanel();
        jPanelVerticalProfile = new javax.swing.JPanel();
        jPanelThermalColumn = new javax.swing.JPanel();
        jPanelThermalColumnEmpty = new javax.swing.JPanel();
        jPanelThermalColumnProperties = new javax.swing.JPanel();
        jPanelThermalColumnView = new javax.swing.JPanel();
        jPanelThermalColumnMaximumSpeed = new javax.swing.JPanel();
        jLabelMaximumSpeed = new javax.swing.JLabel();
        jSliderMaximumSpeed = new javax.swing.JSlider();
        jLabelCoordinates = new javax.swing.JLabel();
        jLabelCoordinatesValues = new javax.swing.JLabel();
        jLabelGeoCoordinates = new javax.swing.JLabel();
        jLabelGeoCoordinatesValues = new javax.swing.JLabel();
        jPanelTurbulences = new javax.swing.JPanel();
        jSliderIntensity = new javax.swing.JSlider();
        jLabelIntensity = new javax.swing.JLabel();
        jPanelTimeScale = new javax.swing.JPanel();
        jLabelTimeScale = new javax.swing.JLabel();
        jSpinnerTimeScale = new javax.swing.JSpinner();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuNewFile = new javax.swing.JMenuItem();
        jMenuSaveFile = new javax.swing.JMenuItem();
        jMenuLoadFile = new javax.swing.JMenuItem();
        jMenuExit = new javax.swing.JMenuItem();
        jMenuMeteorology = new javax.swing.JMenu();
        jMenuVerticalProfile = new javax.swing.JMenu();
        jItemVerticalProfile1 = new javax.swing.JMenuItem();
        jItemVerticalProfile2 = new javax.swing.JMenuItem();
        jMenuThermalColumn = new javax.swing.JMenu();
        jItemThermalColumn1 = new javax.swing.JMenuItem();
        jItemThermalColumn2 = new javax.swing.JMenuItem();
        jMenuTurbulences = new javax.swing.JMenuItem();
        jMenuGoogleMaps = new javax.swing.JMenu();
        jMenuReinitialize = new javax.swing.JMenuItem();
        jMenuMiscellaneous = new javax.swing.JMenu();
        jMenuThermalColumnColorChooser = new javax.swing.JMenu();
        jRadioButtonBlack = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonWhite = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonRed = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMagenta = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Paparazzi Environmental Simulator");
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setResizable(false);

        jPanelMain.setMinimumSize(new java.awt.Dimension(1152, 650));
        jPanelMain.setPreferredSize(new java.awt.Dimension(1152, 650));
        jPanelMain.setLayout(new java.awt.BorderLayout());

        jPanelWest.setMinimumSize(new java.awt.Dimension(852, 650));
        jPanelWest.setName("");
        jPanelWest.setPreferredSize(new java.awt.Dimension(852, 650));
        jPanelWest.setLayout(new java.awt.BorderLayout());

        jPanelMenu.setBorder(javax.swing.BorderFactory.createTitledBorder("Meteorological Phenomena"));
        jPanelMenu.setMaximumSize(new java.awt.Dimension(32767, 100));
        jPanelMenu.setMinimumSize(new java.awt.Dimension(852, 150));
        jPanelMenu.setName("");
        jPanelMenu.setPreferredSize(new java.awt.Dimension(852, 150));

        jToggleButtonVerticalProfile.setText("Vertical Profile");
        jToggleButtonVerticalProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonVerticalProfileActionPerformed(evt);
            }
        });
        jPanelMenu.add(jToggleButtonVerticalProfile);

        jToggleButtonThermalColumn.setText("Thermal Column");
        jToggleButtonThermalColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonThermalColumnActionPerformed(evt);
            }
        });
        jPanelMenu.add(jToggleButtonThermalColumn);

        jToggleButtonTurbulences.setText("Turbulences");
        jToggleButtonTurbulences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonTurbulencesActionPerformed(evt);
            }
        });
        jPanelMenu.add(jToggleButtonTurbulences);

        jPanelWest.add(jPanelMenu, java.awt.BorderLayout.CENTER);

        jPanelMap.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelMap.setMinimumSize(new java.awt.Dimension(852, 500));
        jPanelMap.setPreferredSize(new java.awt.Dimension(852, 500));

        javax.swing.GroupLayout jPanelMapLayout = new javax.swing.GroupLayout(jPanelMap);
        jPanelMap.setLayout(jPanelMapLayout);
        jPanelMapLayout.setHorizontalGroup(
            jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 848, Short.MAX_VALUE)
        );
        jPanelMapLayout.setVerticalGroup(
            jPanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
        );

        jPanelWest.add(jPanelMap, java.awt.BorderLayout.PAGE_START);

        jPanelMain.add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelPropertiesEditor.setMaximumSize(new java.awt.Dimension(30000, 80000));
        jPanelPropertiesEditor.setMinimumSize(new java.awt.Dimension(300, 650));
        jPanelPropertiesEditor.setPreferredSize(new java.awt.Dimension(300, 650));
        jPanelPropertiesEditor.setLayout(new java.awt.CardLayout());

        jPanelEmpty.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEmpty.setMinimumSize(new java.awt.Dimension(300, 650));
        jPanelEmpty.setName("");
        jPanelEmpty.setPreferredSize(new java.awt.Dimension(300, 650));

        javax.swing.GroupLayout jPanelEmptyLayout = new javax.swing.GroupLayout(jPanelEmpty);
        jPanelEmpty.setLayout(jPanelEmptyLayout);
        jPanelEmptyLayout.setHorizontalGroup(
            jPanelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 424, Short.MAX_VALUE)
        );
        jPanelEmptyLayout.setVerticalGroup(
            jPanelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 694, Short.MAX_VALUE)
        );

        jPanelPropertiesEditor.add(jPanelEmpty, "cardEmpty");

        jPanelVerticalProfile.setBorder(javax.swing.BorderFactory.createTitledBorder("Vertical Profile"));
        jPanelVerticalProfile.setMinimumSize(new java.awt.Dimension(300, 650));
        jPanelVerticalProfile.setPreferredSize(new java.awt.Dimension(300, 650));

        javax.swing.GroupLayout jPanelVerticalProfileLayout = new javax.swing.GroupLayout(jPanelVerticalProfile);
        jPanelVerticalProfile.setLayout(jPanelVerticalProfileLayout);
        jPanelVerticalProfileLayout.setHorizontalGroup(
            jPanelVerticalProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );
        jPanelVerticalProfileLayout.setVerticalGroup(
            jPanelVerticalProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 676, Short.MAX_VALUE)
        );

        jPanelPropertiesEditor.add(jPanelVerticalProfile, "cardVerticalProfile");

        jPanelThermalColumn.setBorder(javax.swing.BorderFactory.createTitledBorder("Thermal Column"));
        jPanelThermalColumn.setMinimumSize(new java.awt.Dimension(300, 650));
        jPanelThermalColumn.setPreferredSize(new java.awt.Dimension(300, 650));
        jPanelThermalColumn.setLayout(new java.awt.CardLayout());

        jPanelThermalColumnEmpty.setMinimumSize(new java.awt.Dimension(300, 650));
        jPanelThermalColumnEmpty.setPreferredSize(new java.awt.Dimension(300, 650));

        javax.swing.GroupLayout jPanelThermalColumnEmptyLayout = new javax.swing.GroupLayout(jPanelThermalColumnEmpty);
        jPanelThermalColumnEmpty.setLayout(jPanelThermalColumnEmptyLayout);
        jPanelThermalColumnEmptyLayout.setHorizontalGroup(
            jPanelThermalColumnEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );
        jPanelThermalColumnEmptyLayout.setVerticalGroup(
            jPanelThermalColumnEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 676, Short.MAX_VALUE)
        );

        jPanelThermalColumn.add(jPanelThermalColumnEmpty, "cardThermalColumnEmpty");

        jPanelThermalColumnProperties.setMinimumSize(new java.awt.Dimension(300, 650));
        jPanelThermalColumnProperties.setPreferredSize(new java.awt.Dimension(300, 650));
        jPanelThermalColumnProperties.setLayout(new java.awt.BorderLayout());

        jPanelThermalColumnView.setMinimumSize(new java.awt.Dimension(300, 500));
        jPanelThermalColumnView.setPreferredSize(new java.awt.Dimension(300, 500));

        javax.swing.GroupLayout jPanelThermalColumnViewLayout = new javax.swing.GroupLayout(jPanelThermalColumnView);
        jPanelThermalColumnView.setLayout(jPanelThermalColumnViewLayout);
        jPanelThermalColumnViewLayout.setHorizontalGroup(
            jPanelThermalColumnViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );
        jPanelThermalColumnViewLayout.setVerticalGroup(
            jPanelThermalColumnViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        jPanelThermalColumnProperties.add(jPanelThermalColumnView, java.awt.BorderLayout.PAGE_START);

        jPanelThermalColumnMaximumSpeed.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelThermalColumnMaximumSpeed.setMinimumSize(new java.awt.Dimension(300, 150));
        jPanelThermalColumnMaximumSpeed.setPreferredSize(new java.awt.Dimension(300, 150));

        jLabelMaximumSpeed.setText("Maximum Vertical Speed : 5 cm/s");

        jSliderMaximumSpeed.setMaximum(500);
        jSliderMaximumSpeed.setMinimum(5);
        jSliderMaximumSpeed.setToolTipText("");
        jSliderMaximumSpeed.setValue(5);
        jSliderMaximumSpeed.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jSliderMaximumSpeedMouseDragged(evt);
            }
        });

        jLabelCoordinates.setText("Coordinates (X, Y) :");

        jLabelCoordinatesValues.setText("(0.0, 0.0)");

        jLabelGeoCoordinates.setText("Coordinates (Lat, Long) :");

        jLabelGeoCoordinatesValues.setText("(0.0, 0.0)");

        javax.swing.GroupLayout jPanelThermalColumnMaximumSpeedLayout = new javax.swing.GroupLayout(jPanelThermalColumnMaximumSpeed);
        jPanelThermalColumnMaximumSpeed.setLayout(jPanelThermalColumnMaximumSpeedLayout);
        jPanelThermalColumnMaximumSpeedLayout.setHorizontalGroup(
            jPanelThermalColumnMaximumSpeedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelThermalColumnMaximumSpeedLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelThermalColumnMaximumSpeedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelMaximumSpeed)
                    .addGroup(jPanelThermalColumnMaximumSpeedLayout.createSequentialGroup()
                        .addComponent(jLabelCoordinates)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelCoordinatesValues))
                    .addComponent(jSliderMaximumSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelThermalColumnMaximumSpeedLayout.createSequentialGroup()
                        .addComponent(jLabelGeoCoordinates)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelGeoCoordinatesValues)))
                .addContainerGap(224, Short.MAX_VALUE))
        );
        jPanelThermalColumnMaximumSpeedLayout.setVerticalGroup(
            jPanelThermalColumnMaximumSpeedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelThermalColumnMaximumSpeedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelMaximumSpeed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderMaximumSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelThermalColumnMaximumSpeedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCoordinates)
                    .addComponent(jLabelCoordinatesValues))
                .addGap(18, 18, 18)
                .addGroup(jPanelThermalColumnMaximumSpeedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGeoCoordinates)
                    .addComponent(jLabelGeoCoordinatesValues))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanelThermalColumnProperties.add(jPanelThermalColumnMaximumSpeed, java.awt.BorderLayout.PAGE_END);

        jPanelThermalColumn.add(jPanelThermalColumnProperties, "cardThermalColumnProperties");

        jPanelPropertiesEditor.add(jPanelThermalColumn, "cardThermalColumn");

        jPanelTurbulences.setBorder(javax.swing.BorderFactory.createTitledBorder("Turbulences"));
        jPanelTurbulences.setMinimumSize(new java.awt.Dimension(300, 650));
        jPanelTurbulences.setPreferredSize(new java.awt.Dimension(300, 650));
        jPanelTurbulences.setLayout(new javax.swing.BoxLayout(jPanelTurbulences, javax.swing.BoxLayout.Y_AXIS));

        jSliderIntensity.setMaximum(3);
        jSliderIntensity.setValue(0);
        jSliderIntensity.setMaximumSize(new java.awt.Dimension(200, 23));
        jSliderIntensity.setMinimumSize(new java.awt.Dimension(200, 23));
        jSliderIntensity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderIntensityStateChanged(evt);
            }
        });
        jPanelTurbulences.add(jSliderIntensity);

        jLabelIntensity.setText("Intensity : 0");
        jLabelIntensity.setAlignmentX(5.0F);
        jLabelIntensity.setMaximumSize(new java.awt.Dimension(80, 14));
        jLabelIntensity.setMinimumSize(new java.awt.Dimension(80, 14));
        jLabelIntensity.setPreferredSize(new java.awt.Dimension(80, 14));
        jPanelTurbulences.add(jLabelIntensity);

        jPanelPropertiesEditor.add(jPanelTurbulences, "cardTurbulences");

        jLabelTimeScale.setText("Time Scale : ");

        jSpinnerTimeScale.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSpinnerTimeScale.setValue(1);
        jSpinnerTimeScale.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerTimeScaleStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelTimeScaleLayout = new javax.swing.GroupLayout(jPanelTimeScale);
        jPanelTimeScale.setLayout(jPanelTimeScaleLayout);
        jPanelTimeScaleLayout.setHorizontalGroup(
            jPanelTimeScaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTimeScaleLayout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabelTimeScale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSpinnerTimeScale, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(209, Short.MAX_VALUE))
        );
        jPanelTimeScaleLayout.setVerticalGroup(
            jPanelTimeScaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTimeScaleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTimeScaleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTimeScale)
                    .addComponent(jSpinnerTimeScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(668, Short.MAX_VALUE))
        );

        jPanelPropertiesEditor.add(jPanelTimeScale, "cardTimeScale");

        jPanelMain.add(jPanelPropertiesEditor, java.awt.BorderLayout.CENTER);

        jMenuFile.setText("File");

        jMenuNewFile.setText("New File");
        jMenuNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewFileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuNewFile);

        jMenuSaveFile.setText("Save File");
        jMenuSaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveFileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuSaveFile);

        jMenuLoadFile.setText("Load File");
        jMenuLoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuLoadFileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuLoadFile);

        jMenuExit.setText("Exit");
        jMenuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuExit);

        jMenuBar.add(jMenuFile);

        jMenuMeteorology.setText("Meteorology");

        jMenuVerticalProfile.setText("Vertical Profile");

        jItemVerticalProfile1.setText("Open Vertical Profile");
        jItemVerticalProfile1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jItemVerticalProfile1ActionPerformed(evt);
            }
        });
        jMenuVerticalProfile.add(jItemVerticalProfile1);

        jItemVerticalProfile2.setText("Reset Vertical Profile");
        jItemVerticalProfile2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jItemVerticalProfile2ActionPerformed(evt);
            }
        });
        jMenuVerticalProfile.add(jItemVerticalProfile2);

        jMenuMeteorology.add(jMenuVerticalProfile);

        jMenuThermalColumn.setText("Thermal Column");

        jItemThermalColumn1.setText("Open Thermal Column");
        jItemThermalColumn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jItemThermalColumn1ActionPerformed(evt);
            }
        });
        jMenuThermalColumn.add(jItemThermalColumn1);

        jItemThermalColumn2.setText("Reset Thermal Column");
        jItemThermalColumn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jItemThermalColumn2ActionPerformed(evt);
            }
        });
        jMenuThermalColumn.add(jItemThermalColumn2);

        jMenuMeteorology.add(jMenuThermalColumn);

        jMenuTurbulences.setText("Open Turbulences");
        jMenuTurbulences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuTurbulencesActionPerformed(evt);
            }
        });
        jMenuMeteorology.add(jMenuTurbulences);

        jMenuBar.add(jMenuMeteorology);

        jMenuGoogleMaps.setText("Google Maps");

        jMenuReinitialize.setText("Load GMaps");
        jMenuReinitialize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuReinitializeActionPerformed(evt);
            }
        });
        jMenuGoogleMaps.add(jMenuReinitialize);

        jMenuBar.add(jMenuGoogleMaps);

        jMenuMiscellaneous.setText("Miscellaneous");

        jMenuThermalColumnColorChooser.setText("Set Thermal Color");

        jRadioButtonBlack.setText("Black");
        jRadioButtonBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonBlackActionPerformed(evt);
            }
        });
        jMenuThermalColumnColorChooser.add(jRadioButtonBlack);

        jRadioButtonWhite.setSelected(true);
        jRadioButtonWhite.setText("White");
        jRadioButtonWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonWhiteActionPerformed(evt);
            }
        });
        jMenuThermalColumnColorChooser.add(jRadioButtonWhite);

        jRadioButtonRed.setText("Red");
        jRadioButtonRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonRedActionPerformed(evt);
            }
        });
        jMenuThermalColumnColorChooser.add(jRadioButtonRed);

        jRadioButtonMagenta.setText("Magenta");
        jRadioButtonMagenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMagentaActionPerformed(evt);
            }
        });
        jMenuThermalColumnColorChooser.add(jRadioButtonMagenta);

        jMenuMiscellaneous.add(jMenuThermalColumnColorChooser);
        jMenuMiscellaneous.add(jSeparator1);

        jMenuItem1.setText("Set Time Scale");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenuMiscellaneous.add(jMenuItem1);

        jMenuBar.add(jMenuMiscellaneous);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, 1280, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Listener for the slider parametering the intensity of the turbulences.
     */
    private void jSliderIntensityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderIntensityStateChanged
        jLabelIntensity.setText("Intensity : " + jSliderIntensity.getValue());
        map.setIntensity(jSliderIntensity.getValue());
    }//GEN-LAST:event_jSliderIntensityStateChanged

    /**
     * Listener for the toggle button opening the properties editor of the
     * turbulences.
     */
    private void jToggleButtonTurbulencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonTurbulencesActionPerformed
        jToggleButtonVerticalProfile.setSelected(false);
        jToggleButtonThermalColumn.setSelected(false);
        CardLayout cardL = (CardLayout) jPanelPropertiesEditor.getLayout();
        if(jToggleButtonTurbulences.isSelected()) {
            cardL.show(jPanelPropertiesEditor, "cardTurbulences");
        }
        else {
            jToggleButtonTurbulences.setSelected(true);
        }
    }//GEN-LAST:event_jToggleButtonTurbulencesActionPerformed

    /**
     * Listener for the toggle button opening the properties editor of the
     * vertical profile.
     */
    private void jToggleButtonVerticalProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonVerticalProfileActionPerformed
        jToggleButtonTurbulences.setSelected(false);
        jToggleButtonThermalColumn.setSelected(false);
        CardLayout cardL = (CardLayout) jPanelPropertiesEditor.getLayout();
        if(jToggleButtonVerticalProfile.isSelected()) {
            cardL.show(jPanelPropertiesEditor, "cardVerticalProfile");
        }
        else {
            jToggleButtonVerticalProfile.setSelected(true);
        }
    }//GEN-LAST:event_jToggleButtonVerticalProfileActionPerformed

    /**
     * Listener for the toggle button opening the properties editor of the
     * thermal columns.
     */
    private void jToggleButtonThermalColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonThermalColumnActionPerformed
        jToggleButtonVerticalProfile.setSelected(false);
        jToggleButtonTurbulences.setSelected(false);
        CardLayout cardL = (CardLayout) jPanelPropertiesEditor.getLayout();
        if(jToggleButtonThermalColumn.isSelected()) {
            cardL.show(jPanelPropertiesEditor, "cardThermalColumn");
        }
        else {
            jToggleButtonThermalColumn.setSelected(true);
        }
    }//GEN-LAST:event_jToggleButtonThermalColumnActionPerformed

    /**
     * Listener for the menu item opening the properties editor of the
     * turbulences.
     */
    private void jMenuTurbulencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuTurbulencesActionPerformed
        jToggleButtonVerticalProfile.setSelected(false);
        jToggleButtonThermalColumn.setSelected(false);
        jToggleButtonTurbulences.setSelected(true);
        CardLayout cardL = (CardLayout) jPanelPropertiesEditor.getLayout();
        cardL.show(jPanelPropertiesEditor, "cardTurbulences");
    }//GEN-LAST:event_jMenuTurbulencesActionPerformed

    /**
     * Listener for the menu item opening the properties editor of the vertical
     * profile.
     */
    private void jItemVerticalProfile1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jItemVerticalProfile1ActionPerformed
        jToggleButtonVerticalProfile.setSelected(true);
        jToggleButtonThermalColumn.setSelected(false);
        jToggleButtonTurbulences.setSelected(false);
        CardLayout cardL = (CardLayout) jPanelPropertiesEditor.getLayout();
        cardL.show(jPanelPropertiesEditor, "cardVerticalProfile");
    }//GEN-LAST:event_jItemVerticalProfile1ActionPerformed

    /**
     * Listener for the menu item reseting the vertical profile.
     */
    private void jItemVerticalProfile2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jItemVerticalProfile2ActionPerformed
        this.vpControler.resetVerticalProfile();
    }//GEN-LAST:event_jItemVerticalProfile2ActionPerformed

    /**
     * Listener for the menu item opening the properties editor of the thermals.
     */
    private void jItemThermalColumn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jItemThermalColumn1ActionPerformed
        jToggleButtonVerticalProfile.setSelected(false);
        jToggleButtonTurbulences.setSelected(false);
        jToggleButtonThermalColumn.setSelected(true);
        CardLayout cardL = (CardLayout) jPanelPropertiesEditor.getLayout();
        cardL.show(jPanelPropertiesEditor, "cardThermalColumn");
    }//GEN-LAST:event_jItemThermalColumn1ActionPerformed

    /**
     * Listener for the menu item reseting the thermal list.
     */
    private void jItemThermalColumn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jItemThermalColumn2ActionPerformed
        this.mapControler.resetThermal();
    }//GEN-LAST:event_jItemThermalColumn2ActionPerformed

    /**
     * Listener for the maximum speed slider of the thermals.
     */
    private void jSliderMaximumSpeedMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderMaximumSpeedMouseDragged
        map.setMaximumSpeed(this.mapControler.getCurrentThermal(), 
                (int)jSliderMaximumSpeed.getValue());
        jLabelMaximumSpeed.setText("Maximum Vertical Speed : " + 
                (int)this.mapControler.getCurrentThermal().getMaximumSpeed() + 
                " cm/s");
    }//GEN-LAST:event_jSliderMaximumSpeedMouseDragged

    /**
     * Listener for the menu item opening a new environment file.
     */
    private void jMenuNewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNewFileActionPerformed
        this.mapControler.resetThermal();
        this.vpControler.resetVerticalProfile();
        this.map.resetTurbulence();
        this.jSliderIntensity.setValue(0);
        this.jLabelIntensity.setText("Intensity : " + 
                jSliderIntensity.getValue());
    }//GEN-LAST:event_jMenuNewFileActionPerformed

    /**
     * Listener for the menu item saving the current designed environment.
     */
    private void jMenuSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveFileActionPerformed
        JPanel save = new SaveDialog();
        JFrame newFrame = new JFrame("Save File");
        Container pane = newFrame.getContentPane();
        pane.add(save);
        JFileChooser fileChooser = ((SaveDialog)save).getFileChooser();
        int rVal = fileChooser.showSaveDialog(pane);
        if(rVal == JFileChooser.APPROVE_OPTION) {
            File path = fileChooser.getCurrentDirectory();
            File file = fileChooser.getSelectedFile();

            String dirPath = path.getAbsolutePath();
            String fileName = file.getName();

            if(fileName.substring(fileName.length()-4, 
                    fileName.length()).contentEquals(".xml")) {
                SaveManager.save(map, dirPath + "/" + fileName);
            }
            else {
                SaveManager.save(map, dirPath + "/" + fileName + ".xml");
            }
        }
        else {
            newFrame.setVisible(false);
        }
    }//GEN-LAST:event_jMenuSaveFileActionPerformed

    /**
     * Listener for the menu item closing the application.
     */
    private void jMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExitActionPerformed
        System.exit(1);
    }//GEN-LAST:event_jMenuExitActionPerformed

    /**
     * Listener for the menu item loading the google maps.
     */
    private void jMenuReinitializeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuReinitializeActionPerformed
        Canvas canvasMap = this.mapControler.getCanvas();
        List<CShape> listGoogle = this.mapControler.getGoogleList();
        MapsGoogle GMap = new MapsGoogle(canvasMap, listGoogle, 
                MapControler.getOrigin(), mapControler.getHomePath());
    }//GEN-LAST:event_jMenuReinitializeActionPerformed

    /**
     * Listener for the menu item opening the loading dialog.
     */
    private void jMenuLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuLoadFileActionPerformed
        JPanel load = new LoadDialog();
        JFrame newFrame = new JFrame("Load File");
        Container pane = newFrame.getContentPane();
        pane.add(load);
        JFileChooser fileChooser = ((LoadDialog)load).getFileChooser();
        int rVal = fileChooser.showOpenDialog(load);
        if(rVal == JFileChooser.APPROVE_OPTION) {
            File path = fileChooser.getCurrentDirectory();
            File file = fileChooser.getSelectedFile();

            String dirPath = path.getAbsolutePath();
            String fileName = file.getName();

            if(fileName.substring(fileName.length()-4, 
                    fileName.length()).contentEquals(".xml")) {
                this.mapControler.resetThermal();
                this.vpControler.resetVerticalProfile();
                this.map.resetTurbulence();
                this.jSliderIntensity.setValue(0);
                this.jLabelIntensity.setText("Intensity : " + 
                        jSliderIntensity.getValue());
                SaveManager.load(map, dirPath + "/" + fileName);
                this.mapControler.loadView();
                this.vpControler.loadView();
                jLabelIntensity.setText("Intensity : " + 
                        map.getIntensity());
                jSliderIntensity.setValue(map.getIntensity());
            }
            else {
                System.out.println("Error : unsupported file format");
            }
        }
        else {
            newFrame.setVisible(false);
        }
    }//GEN-LAST:event_jMenuLoadFileActionPerformed

    /**
     * Listener for the menu item changing the thermal color.
     */
    private void jRadioButtonBlackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonBlackActionPerformed
        jRadioButtonWhite.setSelected(false);
        jRadioButtonRed.setSelected(false);
        jRadioButtonMagenta.setSelected(false);
        if(jRadioButtonBlack.isSelected()) {
            this.mapControler.setThermalColor(Color.BLACK);
        }
        else {
            jRadioButtonBlack.setSelected(true);
        }
    }//GEN-LAST:event_jRadioButtonBlackActionPerformed

    /**
     * Listener for the menu item changing the thermal color.
     */
    private void jRadioButtonWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonWhiteActionPerformed
        jRadioButtonBlack.setSelected(false);
        jRadioButtonRed.setSelected(false);
        jRadioButtonMagenta.setSelected(false);
        if(jRadioButtonWhite.isSelected()) {
            this.mapControler.setThermalColor(Color.WHITE);
        }
        else {
            jRadioButtonWhite.setSelected(true);
        }
    }//GEN-LAST:event_jRadioButtonWhiteActionPerformed

    /**
     * Listener for the menu item changing the thermal color.
     */
    private void jRadioButtonRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonRedActionPerformed
        jRadioButtonWhite.setSelected(false);
        jRadioButtonBlack.setSelected(false);
        jRadioButtonMagenta.setSelected(false);
        if(jRadioButtonRed.isSelected()) {
            this.mapControler.setThermalColor(Color.RED);
        }
        else {
            jRadioButtonRed.setSelected(true);
        }
    }//GEN-LAST:event_jRadioButtonRedActionPerformed

    /**
     * Listener for the menu item changing the thermal color.
     */
    private void jRadioButtonMagentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMagentaActionPerformed
        jRadioButtonWhite.setSelected(false);
        jRadioButtonBlack.setSelected(false);
        jRadioButtonRed.setSelected(false);
        if(jRadioButtonMagenta.isSelected()) {
            this.mapControler.setThermalColor(Color.MAGENTA);
        }
        else {
            jRadioButtonMagenta.setSelected(true);
        }
    }//GEN-LAST:event_jRadioButtonMagentaActionPerformed

    /**
     * Listener for the menu item opening the time scale panel.
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        jToggleButtonVerticalProfile.setSelected(false);
        jToggleButtonTurbulences.setSelected(false);
        jToggleButtonThermalColumn.setSelected(false);
        CardLayout cardL = (CardLayout) jPanelPropertiesEditor.getLayout();
        cardL.show(jPanelPropertiesEditor, "cardTimeScale");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * Listener for the menu item changing the time scale.
     */
    private void jSpinnerTimeScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerTimeScaleStateChanged
        try {
            if((Integer)jSpinnerTimeScale.getValue() >= 11) {
                jSpinnerTimeScale.setValue(10);
            }
            else if((Integer)jSpinnerTimeScale.getValue() <= 0) {
                jSpinnerTimeScale.setValue(1);
            }
            this.map.setTimeScale((Integer)jSpinnerTimeScale.getValue());
        }
        catch(Exception e) {
            System.out.println("Uncompatible value");
            jSpinnerTimeScale.setValue(1);
            this.map.setTimeScale((Integer)jSpinnerTimeScale.getValue());
        }
    }//GEN-LAST:event_jSpinnerTimeScaleStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : 
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        }

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jItemThermalColumn1;
    private javax.swing.JMenuItem jItemThermalColumn2;
    private javax.swing.JMenuItem jItemVerticalProfile1;
    private javax.swing.JMenuItem jItemVerticalProfile2;
    private javax.swing.JLabel jLabelCoordinates;
    private javax.swing.JLabel jLabelCoordinatesValues;
    private javax.swing.JLabel jLabelGeoCoordinates;
    private javax.swing.JLabel jLabelGeoCoordinatesValues;
    private javax.swing.JLabel jLabelIntensity;
    private javax.swing.JLabel jLabelMaximumSpeed;
    private javax.swing.JLabel jLabelTimeScale;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuExit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuGoogleMaps;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuLoadFile;
    private javax.swing.JMenu jMenuMeteorology;
    private javax.swing.JMenu jMenuMiscellaneous;
    private javax.swing.JMenuItem jMenuNewFile;
    private javax.swing.JMenuItem jMenuReinitialize;
    private javax.swing.JMenuItem jMenuSaveFile;
    private javax.swing.JMenu jMenuThermalColumn;
    private javax.swing.JMenu jMenuThermalColumnColorChooser;
    private javax.swing.JMenuItem jMenuTurbulences;
    private javax.swing.JMenu jMenuVerticalProfile;
    private javax.swing.JPanel jPanelEmpty;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelMap;
    private javax.swing.JPanel jPanelMenu;
    private javax.swing.JPanel jPanelPropertiesEditor;
    private javax.swing.JPanel jPanelThermalColumn;
    private javax.swing.JPanel jPanelThermalColumnEmpty;
    private javax.swing.JPanel jPanelThermalColumnMaximumSpeed;
    private javax.swing.JPanel jPanelThermalColumnProperties;
    private javax.swing.JPanel jPanelThermalColumnView;
    private javax.swing.JPanel jPanelTimeScale;
    private javax.swing.JPanel jPanelTurbulences;
    private javax.swing.JPanel jPanelVerticalProfile;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JRadioButtonMenuItem jRadioButtonBlack;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMagenta;
    private javax.swing.JRadioButtonMenuItem jRadioButtonRed;
    private javax.swing.JRadioButtonMenuItem jRadioButtonWhite;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSlider jSliderIntensity;
    private javax.swing.JSlider jSliderMaximumSpeed;
    private javax.swing.JSpinner jSpinnerTimeScale;
    private javax.swing.JToggleButton jToggleButtonThermalColumn;
    private javax.swing.JToggleButton jToggleButtonTurbulences;
    private javax.swing.JToggleButton jToggleButtonVerticalProfile;
    // End of variables declaration//GEN-END:variables
    private MapControler mapControler;
    private Map map;
    private VerticalProfileControler vpControler;
}
