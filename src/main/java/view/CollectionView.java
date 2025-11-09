package view;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The View for when the user wants to view the collections page
 */
public class CollectionView{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JPanel collectionsPanel = new JPanel();
            JButton viewCollections = new JButton("View Collections");
            JButton addCollection = new JButton("Add Collection");
            JButton removeCollection = new JButton("Remove Collection");
            JButton addToCollection = new JButton("Add To Collection");
            JButton removeFromCollection = new JButton("Remove From Collection");

            collectionsPanel.add(viewCollections);
            collectionsPanel.add(addCollection);
            collectionsPanel.add(removeCollection);
            collectionsPanel.add(addToCollection);
            collectionsPanel.add(removeFromCollection);

            addCollection.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){

                }
            });

            JFrame frame = new JFrame("Collection View");
            frame.setContentPane(collectionsPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
