package gui;

import models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BuildingGUI {
    private Building building;

    // List Models for Apartments and Common Rooms
    private DefaultListModel<String> apartmentListModel = new DefaultListModel<>();
    private DefaultListModel<String> commonRoomListModel = new DefaultListModel<>();

    private JList<String> apartmentList;
    private JList<String> commonRoomList;

    // Executor for real-time temperature updates
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BuildingGUI(Building building) {
        this.building = building;
        createAndShowGUI();
        startTemperatureUpdates();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Building Controls");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // Panels for Apartments and Common Rooms
        JPanel roomPanel = new JPanel(new GridLayout(1, 2));
        JPanel apartmentPanel = new JPanel(new BorderLayout());
        JPanel commonRoomPanel = new JPanel(new BorderLayout());

        // Apartment List
        apartmentList = new JList<>(apartmentListModel);
        JScrollPane apartmentScroll = new JScrollPane(apartmentList);
        apartmentPanel.add(new JLabel("Apartments"), BorderLayout.NORTH);
        apartmentPanel.add(apartmentScroll, BorderLayout.CENTER);

        // Common Room List
        commonRoomList = new JList<>(commonRoomListModel);
        JScrollPane commonRoomScroll = new JScrollPane(commonRoomList);
        commonRoomPanel.add(new JLabel("Common Rooms"), BorderLayout.NORTH);
        commonRoomPanel.add(commonRoomScroll, BorderLayout.CENTER);

        // Add to the roomPanel
        roomPanel.add(apartmentPanel);
        roomPanel.add(commonRoomPanel);

        // Temperature Control
        JPanel tempPanel = new JPanel(new FlowLayout());
        JLabel tempLabel = new JLabel("Set Temperature:");
        JTextField tempField = new JTextField(5);
        JButton setTempButton = new JButton("Set");

        tempPanel.add(tempLabel);
        tempPanel.add(tempField);
        tempPanel.add(setTempButton);

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

        // "Add Room" Button
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> showAddRoomDialog());

        // Bottom Panel for Adding Rooms
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(addRoomButton);

        // Add components to frame
        frame.add(roomPanel, BorderLayout.CENTER);
        frame.add(tempPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        refreshRoomLists();
        frame.setVisible(true);
    }

    private void showAddRoomDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add Room", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new FlowLayout());

        JLabel roomTypeLabel = new JLabel("Select Room Type:");
        String[] roomTypes = {"Apartment", "Common Room"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);

        JTextField ownerNameField = new JTextField(15);
        setPlaceholder(ownerNameField, "Enter owner's name");

        JComboBox<String> commonRoomDropdown = new JComboBox<>(new String[]{"Gym", "Library", "Laundry"});
        commonRoomDropdown.setVisible(false);

        JButton addRoomButton = new JButton("Add");

        // Handle room type selection
        roomTypeCombo.addActionListener(e -> {
            String selectedType = (String) roomTypeCombo.getSelectedItem();
            ownerNameField.setVisible(selectedType.equals("Apartment"));
            commonRoomDropdown.setVisible(selectedType.equals("Common Room"));
        });

        // Handle adding a new room
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

    private void startTemperatureUpdates() {
        scheduler.scheduleAtFixedRate(this::refreshRoomLists, 0, 1, TimeUnit.SECONDS);
    }
}

