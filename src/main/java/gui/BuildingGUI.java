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
 * GUI class for controlling the building temperature and managing rooms.
 * This class provides a graphical interface to:
 * - View apartments and common rooms with their temperatures.
 * - Set a global requested temperature for the building.
 * - Add new apartments and common rooms via a pop-up dialog.
 * - Automatically refresh temperature updates at fixed intervals.
 */
public class BuildingGUI {
    private Building building;

    // List models for displaying apartments and common rooms
    private DefaultListModel<String> apartmentListModel = new DefaultListModel<>();
    private DefaultListModel<String> commonRoomListModel = new DefaultListModel<>();

    private JList<String> apartmentList;
    private JList<String> commonRoomList;

    // Scheduler for real-time temperature updates
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructor to initialize the building GUI.
     *
     * @param building The building instance that this GUI manages.
     */
    public BuildingGUI(Building building) {
        this.building = building;
        createAndShowGUI();
        startTemperatureUpdates();
    }

    /**
     * Initializes and displays the main GUI window.
     * This includes:
     * - Temperature control input.
     * - Lists for displaying apartments and common rooms.
     * - A button to open the "Add Room" dialog.
     */
    private void createAndShowGUI() {
        JFrame frame = new JFrame(PropertyLoader.getProperty("gui.window.title"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(
            PropertyLoader.getIntProperty("gui.window.width"),
            PropertyLoader.getIntProperty("gui.window.height")
        );
        frame.setLayout(new BorderLayout());

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
     * Displays a pop-up dialog to add new apartments or common rooms.
     * The user selects a room type and enters necessary details.
     */
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add Room", true);
        dialog.setSize(
            PropertyLoader.getIntProperty("gui.dialog.width"),
            PropertyLoader.getIntProperty("gui.dialog.height")
        );
        dialog.setLayout(new FlowLayout());

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
     * Sets a placeholder text in a JTextField.
     * The placeholder disappears when the user starts typing and reappears when empty.
     *
     * @param textField       The JTextField where the placeholder is applied.
     * @param placeholderText The placeholder text.
     */
    private void setPlaceholder(JTextField textField, String placeholderText) {
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
     * Refreshes the lists of apartments and common rooms in the GUI.
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
     * Starts a scheduled task to update room temperatures every second.
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


