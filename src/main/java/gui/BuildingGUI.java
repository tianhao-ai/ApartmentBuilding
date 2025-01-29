package gui;

import models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuildingGUI {
    private Building building;

    public BuildingGUI(Building building) {
        this.building = building;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Building Controls");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Display current building info
        JTextArea buildingInfo = new JTextArea(building.toString());
        buildingInfo.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(buildingInfo);

        // Controls for temperature
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new FlowLayout());
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
                buildingInfo.setText(building.toString());
                JOptionPane.showMessageDialog(frame, "Temperature updated to " + newTemp + "°C!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid temperature value!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Controls for adding rooms
        JPanel addRoomPanel = new JPanel();
        addRoomPanel.setLayout(new FlowLayout());
        JLabel roomTypeLabel = new JLabel("Add Room:");
        String[] roomTypes = {"Apartment", "Common Room"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        JTextField ownerNameField = new JTextField(10); // For owner name only
        JButton addRoomButton = new JButton("Add");

        addRoomPanel.add(roomTypeLabel);
        addRoomPanel.add(roomTypeCombo);
        addRoomPanel.add(ownerNameField);
        addRoomPanel.add(addRoomButton);

        addRoomButton.addActionListener(e -> {
            String selectedType = (String) roomTypeCombo.getSelectedItem();
            String inputText = ownerNameField.getText();

            if (selectedType.equals("Apartment")) {
                if (inputText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please provide an owner's name for the apartment!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                building.addRoom(new Apartment(inputText));
                JOptionPane.showMessageDialog(frame, "Apartment with owner '" + inputText + "' added!");
            } else if (selectedType.equals("Common Room")) {
                try {
                    CommonRoom.RoomType type = CommonRoom.RoomType.valueOf(inputText.toUpperCase());
                    building.addRoom(new CommonRoom(type));
                    JOptionPane.showMessageDialog(frame, "Common Room of type '" + inputText + "' added!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid common room type! Use Gym, Library, or Laundry.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            buildingInfo.setText(building.toString());
        });

        // Add components to the frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(tempPanel, BorderLayout.NORTH);
        frame.add(addRoomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
