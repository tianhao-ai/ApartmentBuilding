package gui;

import models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        JPanel roomPanel = new JPanel(new GridLayout(1, 2)); // Two columns
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

        // Add Room Panel
        JPanel addRoomPanel = new JPanel(new FlowLayout());
        JLabel roomTypeLabel = new JLabel("Add Room:");
        String[] roomTypes = {"Apartment", "Common Room"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        JTextField ownerOrTypeField = new JTextField(10);
        JButton addRoomButton = new JButton("Add");

        addRoomPanel.add(roomTypeLabel);
        addRoomPanel.add(roomTypeCombo);
        addRoomPanel.add(ownerOrTypeField);
        addRoomPanel.add(addRoomButton);

        addRoomButton.addActionListener(e -> {
            String selectedType = (String) roomTypeCombo.getSelectedItem();
            String inputText = ownerOrTypeField.getText();

            if (selectedType.equals("Apartment")) {
                if (inputText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please provide an owner's name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                building.addRoom(new Apartment(inputText));
                JOptionPane.showMessageDialog(frame, "Apartment added for owner: " + inputText);
            } else if (selectedType.equals("Common Room")) {
                try {
                    CommonRoom.RoomType type = CommonRoom.RoomType.valueOf(inputText.toUpperCase());
                    building.addRoom(new CommonRoom(type));
                    JOptionPane.showMessageDialog(frame, "Common Room of type '" + inputText + "' added!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid type! Use Gym, Library, or Laundry.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            refreshRoomLists();
        });

        // Add components to frame
        frame.add(roomPanel, BorderLayout.CENTER);
        frame.add(tempPanel, BorderLayout.NORTH);
        frame.add(addRoomPanel, BorderLayout.SOUTH);

        refreshRoomLists(); // Populate initial data

        frame.setVisible(true);
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
                    commonRoomListModel.addElement(cr.getType() +
                            " - Temp: " + String.format("%.2f", cr.getCurrentTemperature()) + "°C");
                }
            }
        });
    }

    private void startTemperatureUpdates() {
        scheduler.scheduleAtFixedRate(this::refreshRoomLists, 0, 1, TimeUnit.SECONDS);
    }
}
