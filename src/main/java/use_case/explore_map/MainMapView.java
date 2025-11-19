package use_case.explore_map;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.*;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.*;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class MainMapView extends JFrame {

    private static final double MIN_ZOOM_SCALE = 0.1;  // Maximum zoom out
    private static final double MAX_ZOOM_SCALE = 50.0; // Maximum zoom in

    private JMapPane mapPane;
    private MapContent mapContent;
    private FeatureLayer featureLayer;
    private SimpleFeatureSource featureSource;
    private StyleFactory styleFactory;
    private FilterFactory filterFactory;
    private Style defaultStyle;
    private SimpleFeature hoveredFeature;

    public MainMapView() {
        initializeFactories();
        setupFrame();
    }

    private void initializeFactories() {
        styleFactory = CommonFactoryFinder.getStyleFactory();
        filterFactory = CommonFactoryFinder.getFilterFactory();
    }

    private void setupFrame() {
        setTitle("GeoLearn - World Map Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Add control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton loadButton = new JButton("Load World Map");
        loadButton.addActionListener(e -> loadShapefile());

        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(e -> zoomIn());

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(e -> zoomOut());

        JButton resetButton = new JButton("Reset View");
        resetButton.addActionListener(e -> resetView());

        panel.add(loadButton);
        panel.add(zoomInButton);
        panel.add(zoomOutButton);
        panel.add(resetButton);

        return panel;
    }

    public void loadShapefile() {
        File file = selectShapeFile();
        if (file == null) {
            return;
        }

        try {
            loadMapFromFile(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading shapefile: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error loading shapefile: " + e.getMessage());
        }
    }

    private File selectShapeFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select World Map Shapefile");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".shp");
            }

            @Override
            public String getDescription() {
                return "Shapefiles (*.shp)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void loadMapFromFile(File file) throws IOException {
        // Clean up existing map
        if (mapContent != null) {
            mapContent.dispose();
        }

        // Load the shapefile
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        featureSource = store.getFeatureSource();

        // Create styles
        defaultStyle = createDefaultStyle();

        // Create map content
        mapContent = new MapContent();
        mapContent.setTitle("World Map");

        featureLayer = new FeatureLayer(featureSource, defaultStyle);
        mapContent.addLayer(featureLayer);

        // Create or update map pane
        if (mapPane != null) {
            remove(mapPane);
        }

        mapPane = new JMapPane(mapContent);
        mapPane.setBackground(Color.WHITE);

        // Add mouse listener for hover effect
        mapPane.addMouseListener(new MapMouseListener() {
            @Override
            public void onMouseClicked(MapMouseEvent ev) {
                // Can be used for country selection
            }

            @Override
            public void onMouseDragged(MapMouseEvent ev) {
                // Default drag behavior
            }

            @Override
            public void onMouseEntered(MapMouseEvent ev) {
                // Not needed
            }

            @Override
            public void onMouseExited(MapMouseEvent ev) {
                clearHover();
            }

            @Override
            public void onMouseMoved(MapMouseEvent ev) {
                handleMouseMove(ev);
            }

            @Override
            public void onMousePressed(MapMouseEvent ev) {
                // Not needed
            }

            @Override
            public void onMouseReleased(MapMouseEvent ev) {
                // Not needed
            }

            @Override
            public void onMouseWheelMoved(MapMouseEvent ev) {
                handleMouseWheel(ev);
            }
        });

        // Add component listener for window resize
        mapPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (mapPane != null && mapContent != null) {
                    mapPane.repaint();
                }
            }
        });

        add(mapPane, BorderLayout.CENTER);
        validate();
        repaint();
    }

    private Style createDefaultStyle() {
        // Create polygon symbolizer with light blue fill
        org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
            filterFactory.literal(Color.DARK_GRAY),
            filterFactory.literal(1)
        );

        Fill fill = styleFactory.createFill(
            filterFactory.literal(new Color(173, 216, 230))
        );

        PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
            stroke, fill, "geometry"
        );

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private Style createHoverStyle() {
        // Create polygon symbolizer with red fill for hover
        org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
            filterFactory.literal(Color.BLACK),
            filterFactory.literal(2)
        );

        Fill fill = styleFactory.createFill(
            filterFactory.literal(new Color(255, 0, 0, 180)) // Red with transparency
        );

        PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
            stroke, fill, "geometry"
        );

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private void handleMouseMove(MapMouseEvent ev) {
        try {
            // Convert screen coordinates to world coordinates
            org.geotools.geometry.Position2D worldPos = ev.getWorldPos();
            Coordinate coord = new Coordinate(worldPos.x, worldPos.y);

            // Find the feature at this position
            SimpleFeature feature = getFeatureAtPosition(coord);

            if (feature != hoveredFeature) {
                hoveredFeature = feature;
                updateHoverDisplay();
            }
        } catch (Exception e) {
            System.err.println("Error handling mouse move: " + e.getMessage());
        }
    }

    private SimpleFeature getFeatureAtPosition(Coordinate worldPos) {
        if (featureSource == null) {
            return null;
        }

        try {
            SimpleFeatureCollection collection = featureSource.getFeatures();
            try (SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();

                    if (geometry != null) {
                        Point point = geometry.getFactory().createPoint(worldPos);
                        if (geometry.contains(point)) {
                            return feature;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting feature at position: " + e.getMessage());
        }

        return null;
    }

    private void updateHoverDisplay() {
        if (mapContent == null || featureLayer == null) {
            return;
        }

        // Update the layer style based on hover
        if (hoveredFeature != null) {
            // Apply combined style showing both default and hover
            Style combinedStyle = createCombinedStyle();
            featureLayer.setStyle(combinedStyle);
        } else {
            featureLayer.setStyle(defaultStyle);
        }

        mapPane.repaint();
    }

    private Style createCombinedStyle() {
        // This creates a style that shows default countries + highlighted hover country
        // For simplicity, we'll just use the default style with hover overlay
        // In a more advanced implementation, you'd use filters

        // Default layer
        org.geotools.api.style.Stroke defaultStroke = styleFactory.createStroke(
            filterFactory.literal(Color.DARK_GRAY),
            filterFactory.literal(1)
        );
        Fill defaultFill = styleFactory.createFill(
            filterFactory.literal(new Color(173, 216, 230))
        );
        PolygonSymbolizer defaultSymbolizer = styleFactory.createPolygonSymbolizer(
            defaultStroke, defaultFill, "geometry"
        );

        Rule defaultRule = styleFactory.createRule();
        defaultRule.symbolizers().add(defaultSymbolizer);

        // Hover layer (on top)
        org.geotools.api.style.Stroke hoverStroke = styleFactory.createStroke(
            filterFactory.literal(Color.BLACK),
            filterFactory.literal(2)
        );
        Fill hoverFill = styleFactory.createFill(
            filterFactory.literal(new Color(255, 0, 0, 180))
        );
        PolygonSymbolizer hoverSymbolizer = styleFactory.createPolygonSymbolizer(
            hoverStroke, hoverFill, "geometry"
        );

        Rule hoverRule = styleFactory.createRule();
        hoverRule.symbolizers().add(hoverSymbolizer);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(
            defaultRule, hoverRule
        );

        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private void clearHover() {
        if (hoveredFeature != null) {
            hoveredFeature = null;
            updateHoverDisplay();
        }
    }

    private void handleMouseWheel(MapMouseEvent ev) {
        if (mapPane == null) {
            return;
        }

        // Get current display area
        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        // Calculate zoom factor
        double zoomFactor = ev.getWheelAmount() > 0 ? 1.2 : 0.8;

        // Calculate new bounds
        double width = currentBounds.getWidth();
        double height = currentBounds.getHeight();
        double newWidth = width * zoomFactor;
        double newHeight = height * zoomFactor;

        // Check zoom limits
        ReferencedEnvelope fullBounds = mapContent.getMaxBounds();
        if (fullBounds != null) {
            double currentScale = fullBounds.getWidth() / width;
            double newScale = currentScale / zoomFactor;

            if (newScale < MIN_ZOOM_SCALE || newScale > MAX_ZOOM_SCALE) {
                return; // Outside zoom limits
            }
        }

        // Center on mouse position
        org.geotools.geometry.Position2D mousePos = ev.getWorldPos();
        double centerX = mousePos.x;
        double centerY = mousePos.y;

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - newWidth / 2,
            centerX + newWidth / 2,
            centerY - newHeight / 2,
            centerY + newHeight / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        mapPane.setDisplayArea(newBounds);
    }

    private void zoomIn() {
        if (mapPane == null) {
            return;
        }

        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        double zoomFactor = 0.8;
        double width = currentBounds.getWidth() * zoomFactor;
        double height = currentBounds.getHeight() * zoomFactor;
        double centerX = currentBounds.getMedian(0);
        double centerY = currentBounds.getMedian(1);

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - width / 2,
            centerX + width / 2,
            centerY - height / 2,
            centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        mapPane.setDisplayArea(newBounds);
    }

    private void zoomOut() {
        if (mapPane == null) {
            return;
        }

        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        double zoomFactor = 1.25;
        double width = currentBounds.getWidth() * zoomFactor;
        double height = currentBounds.getHeight() * zoomFactor;
        double centerX = currentBounds.getMedian(0);
        double centerY = currentBounds.getMedian(1);

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - width / 2,
            centerX + width / 2,
            centerY - height / 2,
            centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        mapPane.setDisplayArea(newBounds);
    }

    private void resetView() {
        if (mapPane == null || mapContent == null) {
            return;
        }

        ReferencedEnvelope bounds = mapContent.getMaxBounds();
        if (bounds != null) {
            mapPane.setDisplayArea(bounds);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMapView view = new MainMapView();
            view.setVisible(true);
        });
    }
}
