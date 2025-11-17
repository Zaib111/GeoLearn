package view;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

// View for when user wants to view table of sorted + filtered countries
public class CountryTableView {
    private JTextField searchField;
    private JComboBox<String> regionComboBox;
    private JComboBox<String> subregionComboBox;

    public CountryTableView() {
        // Main panel
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

        HashMap<String, String[]> subregionHashMap = createSubregionHashMap();

        searchField = new JTextField(15);
        regionComboBox = new JComboBox<>(new String[]{"Asia", "North America", "South America", "Africa", "Europe", "Oceania"});
        regionComboBox.setPreferredSize(new Dimension(150, regionComboBox.getPreferredSize().height));
        subregionComboBox = new JComboBox<>();
        subregionComboBox.setPreferredSize(new Dimension(200, subregionComboBox.getPreferredSize().height));


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

        regionComboBox.setSelectedIndex(0);
        regionComboBox.getActionListeners()[0].actionPerformed(null);

        filterPanel.add(new JLabel("Search Country"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Region"));
        filterPanel.add(regionComboBox);
        filterPanel.add(new JLabel("Subregion"));
        filterPanel.add(subregionComboBox);

        panel.add(filterPanel);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                onSubmitButtonClick();
            }
        });

        panel.add(submitButton);

        // Make frame
        JFrame frame = new JFrame("Country Table View");

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private HashMap<String, String[]> createSubregionHashMap(){
        HashMap<String, String[]> subregionHashMap = new HashMap<>();
        subregionHashMap.put("Asia", new String[]{"Central Asia", "Eastern Asia", "South-eastern Asia", "Southern Asia", "Western Asia"});
        subregionHashMap.put("North America", new String[]{"Northern America", "Central America", "Caribbean"});
        subregionHashMap.put("South America", new String[]{"South America"});
        subregionHashMap.put("Africa", new String[]{"Eastern Africa", "Middle Africa", "Northern Africa", "Southern Africa", "Western Africa"});
        subregionHashMap.put("Europe", new String[]{"Eastern Europe", "Northern Europe", "Southern Europe", "Western Europe"});
        subregionHashMap.put("Oceania", new String[]{"Australia and New Zealand", "Melanesia", "Micronesia", "Polynesia"});

        return subregionHashMap;
    }

    private void onSubmitButtonClick() {
        String search = searchField.getText();
        String region = (String) regionComboBox.getSelectedItem();
        String subregion = (String) subregionComboBox.getSelectedItem();

        System.out.println(search + " " + region + " "  + subregion); // TODO: do something useful
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CountryTableView::new);
    }
}
