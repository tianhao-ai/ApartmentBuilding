package gui;

import models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import utils.PropertyLoader;

/**
 * A graphical user interface for managing building temperature control system.
 * This class provides a comprehensive GUI that allows users to:
 * 
 * - Monitor real-time temperatures of apartments and common rooms
 * - Set and adjust the building's target temperature
 * - Add new apartments with owner information
 * - Add common areas (gym, library, laundry)
 * 
 * The interface automatically updates to reflect temperature changes
 * and maintains a consistent view of the building's state.
 *
 * @see Building
 * @see Room
 * @see Apartment
 * @see CommonRoom
 */
public class BuildingGUI {
    /** The building instance being managed */
    private Building building;

    /** List models for displaying room information */
    private DefaultListModel<String> apartmentListModel = new DefaultListModel<>();
    private DefaultListModel<String> commonRoomListModel = new DefaultListModel<>();

    /** UI components for room displays */
    private JList<String> apartmentList;
    private JList<String> commonRoomList;

    /** Scheduler for periodic UI updates */
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Creates a new BuildingGUI instance and initializes the interface.
     * This constructor will:
     * 
     * - Set up the main window and all UI components
     * - Initialize temperature monitoring
     * - Start periodic updates of the display
     *
     * @param building The building instance to be managed through this interface
     * @throws IllegalArgumentException if building is null
     */
    public BuildingGUI(Building building) {
        if (building == null) {
            throw new IllegalArgumentException("Building instance cannot be null");
        }
        this.building = building;
        createAndShowGUI();
        startTemperatureUpdates();
    }

    /**
     * Creates and displays the main application window.
     * Sets up all UI components including:
     * 
     * - Temperature control panel
     * - Room lists (apartments and common areas)
     * - Add room functionality
     */
    private void createAndShowGUI() {
        JFrame frame = new JFrame(PropertyLoader.getProperty("gui.window.title"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(
            PropertyLoader.getIntProperty("gui.window.width"),
            PropertyLoader.getIntProperty("gui.window.height")
        );
        frame.setLayout(new BorderLayout());

        // Center the main window on screen
        frame.setLocationRelativeTo(null);

        // ----- Panels for Apartments and Common Rooms -----
        JPanel roomPanel = new JPanel(new GridLayout(1, 2)); // Two columns layout
        JPanel apartmentPanel = new JPanel(new BorderLayout());
        JPanel commonRoomPanel = new JPanel(new BorderLayout());

        // Apartment List Panel
        apartmentList = new JList<>(apartmentListModel);
        JScrollPane apartmentScroll = new JScrollPane(apartmentList);
        apartmentPanel.add(new JLabel("Apartments"), BorderLayout.NORTH);
        apartmentPanel.add(apartmentScroll, BorderLayout.CENTER);

        // Common Room List Panel
        commonRoomList = new JList<>(commonRoomListModel);
        JScrollPane commonRoomScroll = new JScrollPane(commonRoomList);
        commonRoomPanel.add(new JLabel("Common Rooms"), BorderLayout.NORTH);
        commonRoomPanel.add(commonRoomScroll, BorderLayout.CENTER);

        // Add both panels to the main roomPanel
        roomPanel.add(apartmentPanel);
        roomPanel.add(commonRoomPanel);

        // ----- Temperature Control Panel -----
        JPanel tempPanel = new JPanel(new FlowLayout());
        JLabel tempLabel = new JLabel("Set Temperature:");
        JTextField tempField = new JTextField(PropertyLoader.getIntProperty("gui.temperature.field.width"));
        JButton setTempButton = new JButton("Set");

        tempPanel.add(tempLabel);
        tempPanel.add(tempField);
        tempPanel.add(setTempButton);

        // Action: Set the requested temperature
        setTempButton.addActionListener(e -> {
            try {
                double newTemp = Double.parseDouble(tempField.getText());
                building.setRequestedTemperature(newTemp);
                refreshRoomLists();
                JOptionPane.showMessageDialog(frame, "Temperature updated to " + newTemp + "°C!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid temperature value!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ----- "Add Room" Button -----
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> showAddRoomDialog());

        // Bottom Panel for the "Add Room" button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(addRoomButton);

        // Add all components to the frame
        frame.add(roomPanel, BorderLayout.CENTER);
        frame.add(tempPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Populate initial data
        refreshRoomLists();
        frame.setVisible(true);
    }

    /**
     * Displays a modal dialog for adding new rooms to the building.
     * The dialog provides options for:
     * 
     * - Adding apartments with owner information
     * - Adding common rooms of various types (GYM, LIBRARY, LAUNDRY)
     * 
     * Input validation is performed before adding any new room.
     */
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add Room", true);
        dialog.setSize(
            PropertyLoader.getIntProperty("gui.dialog.width"),
            PropertyLoader.getIntProperty("gui.dialog.height")
        );
        dialog.setLayout(new FlowLayout());

        // Center the dialog on screen
        dialog.setLocationRelativeTo(null);

        JLabel roomTypeLabel = new JLabel("Select Room Type:");
        String[] roomTypes = {"Apartment", "Common Room"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);

        JTextField ownerNameField = new JTextField(15);
        setPlaceholder(ownerNameField, "Enter owner's name");

        JComboBox<String> commonRoomDropdown = new JComboBox<>(new String[]{"Gym", "Library", "Laundry"});
        commonRoomDropdown.setVisible(false);

        JButton addRoomButton = new JButton("Add");

        // Action: Show the correct input field based on selection
        roomTypeCombo.addActionListener(e -> {
            String selectedType = (String) roomTypeCombo.getSelectedItem();
            ownerNameField.setVisible(selectedType.equals("Apartment"));
            commonRoomDropdown.setVisible(selectedType.equals("Common Room"));
        });

        // Action: Add a new room based on user input
        addRoomButton.addActionListener(e -> {
            String selectedType = (String) roomTypeCombo.getSelectedItem();
            if ("Apartment".equals(selectedType)) {
                String ownerName = ownerNameField.getText().trim();
                if (ownerName.isEmpty() || ownerName.equals("Enter owner's name")) {
                    JOptionPane.showMessageDialog(dialog, "Please provide an owner's name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                building.addRoom(new Apartment(ownerName));
                JOptionPane.showMessageDialog(dialog, "Apartment added for owner: " + ownerName);
            } else {
                String selectedCommonRoom = (String) commonRoomDropdown.getSelectedItem();
                CommonRoom.RoomType type = CommonRoom.RoomType.valueOf(selectedCommonRoom.toUpperCase());
                building.addRoom(new CommonRoom(type));
                JOptionPane.showMessageDialog(dialog, "Common Room of type '" + selectedCommonRoom + "' added!");
            }
            refreshRoomLists();
            dialog.dispose();
        });

        // Add components to dialog
        dialog.add(roomTypeLabel);
        dialog.add(roomTypeCombo);
        dialog.add(ownerNameField);
        dialog.add(commonRoomDropdown);
        dialog.add(addRoomButton);

        dialog.setVisible(true);
    }

    /**
     * Configures a text field with placeholder text functionality.
     * The placeholder text will:
     * 
     * - Display in gray when the field is empty
     * - Disappear when the field gains focus
     * - Reappear when the field loses focus and is empty
     *
     * @param textField The text field to configure
     * @param placeholderText The text to display as placeholder
     * @throws IllegalArgumentException if either parameter is null
     */
    private void setPlaceholder(JTextField textField, String placeholderText) {
        if (textField == null || placeholderText == null) {
            throw new IllegalArgumentException("TextField and placeholder text cannot be null");
        }
        textField.setForeground(Color.GRAY);
        textField.setText(placeholderText);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholderText);
                }
            }
        });
    }

    /**
     * Updates the display of all rooms in the GUI.
     * This method:
     * 
     * - Clears existing room displays
     * - Retrieves current room information from the building
     * - Updates both apartment and common room lists
     * - Formats temperature and status information
     * 
     * Updates are performed on the Event Dispatch Thread to ensure thread safety.
     */
    private void refreshRoomLists() {
        SwingUtilities.invokeLater(() -> {
            apartmentListModel.clear();
            commonRoomListModel.clear();

            for (Room room : building.getRooms()) {
                if (room instanceof Apartment) {
                    Apartment apt = (Apartment) room;
                    apartmentListModel.addElement("Apt " + apt.getApartmentNumber() +
                            " - Owner: " + apt.getOwnerName() +
                            " - Temp: " + String.format("%.2f", apt.getCurrentTemperature()) + "°C");
                } else if (room instanceof CommonRoom) {
                    CommonRoom cr = (CommonRoom) room;
                    commonRoomListModel.addElement("ID: " + cr.getId() +
                            " - Type: " + cr.getType() +
                            " - Temp: " + String.format("%.2f", cr.getCurrentTemperature()) + "°C");
                }
            }
        });
    }

    /**
     * Initiates periodic updates of the room temperature display.
     * Updates occur at intervals specified in the application properties.
     * The update frequency can be configured through the 'gui.refresh.interval' property.
     *
     * @see PropertyLoader#getIntProperty(String)
     */
    private void startTemperatureUpdates() {
        scheduler.scheduleAtFixedRate(
            this::refreshRoomLists,
            0,
            PropertyLoader.getIntProperty("gui.refresh.interval"),
            TimeUnit.SECONDS
        );
    }
}


