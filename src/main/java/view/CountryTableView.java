package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

// View for when user wants to view table of sorted + filtered countries
public class CountryTableView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create main panel
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Heading
            JLabel heading = new JLabel("Filter Countries");
            heading.setFont(new Font("Dialog", Font.BOLD, 24));
            heading.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(heading);

            // Filter panel
            JPanel filterPanel = new JPanel();
            filterPanel.setBorder(new EmptyBorder(40, 0, 20, 0));

            JLabel searchLabel = new JLabel("Search Country");
            JTextField searchField = new JTextField(20);

            JLabel regionLabel = new JLabel("Region");
            String[] regionChoices = {"Asia", "North America", "South America", "Africa", "Europe", "Oceania"};
            JComboBox<String> regionComboBox = new JComboBox<>(regionChoices);

            HashMap<String, String[]> subregionHashMap = new HashMap<>();
            subregionHashMap.put("Asia", new String[]{"Central Asia", "Eastern Asia", "South-eastern Asia", "Southern Asia", "Western Asia"});
            subregionHashMap.put("North America", new String[]{"Northern America", "Central America", "Caribbean"});
            subregionHashMap.put("South America", new String[]{"South America"});
            subregionHashMap.put("Africa", new String[]{"Eastern Africa", "Middle Africa", "Northern Africa", "Southern Africa", "Western Africa"});
            subregionHashMap.put("Europe", new String[]{"Eastern Europe", "Northern Europe", "Southern Europe", "Western Europe"});
            subregionHashMap.put("Oceania", new String[]{"Australia and New Zealand", "Melanesia", "Micronesia", "Polynesia"});
            JLabel subregionLabel = new JLabel("Region");
            JComboBox<String> subregionComboBox = new JComboBox<>(subregionHashMap.get("Asia"));

            regionComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    subregionComboBox.removeAllItems();
                    String currentRegion = regionComboBox.getSelectedItem().toString();
                    for (String subregion : subregionHashMap.get(currentRegion)) {
                        subregionComboBox.addItem(subregion);
                    }
                }
            });

            filterPanel.add(searchLabel);
            filterPanel.add(searchField);
            filterPanel.add(regionLabel);
            filterPanel.add(regionComboBox);
            filterPanel.add(subregionLabel);
            filterPanel.add(subregionComboBox);

            panel.add(filterPanel);

            // Submit button
            JButton submitButton = new JButton("Submit");
            submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    String search = searchField.getText();
                    String region = regionComboBox.getSelectedItem().toString();
                    String subregion = subregionComboBox.getSelectedItem().toString();

                    System.out.println(search + " " + region + " "  + subregion); // TODO: do something useful
                }
            });

            panel.add(submitButton);

            // Make frame
            JFrame frame = new JFrame("Country Table View");

            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
