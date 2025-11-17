package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class CompareView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel heading = new JLabel("Compare Countries");
            heading.setFont(new Font("Dialog", Font.BOLD, 24));
            heading.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(heading);

            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            JPanel countPanel = new JPanel();
            countPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

            JLabel countLabel = new JLabel("Number of countries to compare:");
            Integer[] countChoices = {2, 3, 4, 5};
            JComboBox<Integer> countComboBox = new JComboBox<>(countChoices);
            countComboBox.setSelectedItem(2);

            countPanel.add(countLabel);
            countPanel.add(countComboBox);

            panel.add(countPanel);

            JPanel countriesPanel = new JPanel();
            countriesPanel.setLayout(new GridLayout(5, 2, 10, 10));

            String[] countryOptions = {"Canada", "USA"};
            @SuppressWarnings("unchecked")
            JComboBox<String>[] countryComboBoxes = new JComboBox[5];

            for (int i = 0; i < 5; i++) {
                JLabel countryLabel = new JLabel("Country " + (i + 1) + ":");
                JComboBox<String> countryComboBox = new JComboBox<>(countryOptions);

                countryComboBoxes[i] = countryComboBox;

                countriesPanel.add(countryLabel);
                countriesPanel.add(countryComboBox);
            }

            panel.add(countriesPanel);

            ActionListener updateEnabledDropdowns = e -> {
                int count = (Integer) countComboBox.getSelectedItem();
                for (int i = 0; i < countryComboBoxes.length; i++) {
                    countryComboBoxes[i].setEnabled(i < count);
                }
            };

            updateEnabledDropdowns.actionPerformed(null);
            countComboBox.addActionListener(updateEnabledDropdowns);

            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            JButton compareButton = new JButton("Compare Countries");
            compareButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            compareButton.addActionListener(e -> {
                int count = (Integer) countComboBox.getSelectedItem();
                System.out.print("Comparing: ");
                for (int i = 0; i < count; i++) {
                    System.out.print(countryComboBoxes[i].getSelectedItem() + " ");
                }
                System.out.println(); // TODO: integrate with controller later
            });

            panel.add(compareButton);

            JFrame frame = new JFrame("Compare Countries View");
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
public class CompareView {
}
