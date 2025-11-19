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
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class MainMapView extends JFrame {

    private static final double MIN_ZOOM_SCALE = 0.1;  // Maximum zoom out
    private static final double MAX_ZOOM_SCALE = 50.0; // Maximum zoom in

    private JMapPane mapPane;
    private MapContent mapContent;
    private FeatureLayer featureLayer;
    private FeatureLayer hoverLayer;  // Overlay layer for hover effects
    private SimpleFeatureSource featureSource;
    private StyleFactory styleFactory;
    private FilterFactory filterFactory;
    private Style defaultStyle;
    private SimpleFeature hoveredFeature;

    // Basic synchronization for style updates
    private final Object styleLock = new Object();
    private volatile boolean isUpdatingStyle = false;

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
        if (mapContent != null) {
            mapContent.dispose();
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        featureSource = store.getFeatureSource();

        defaultStyle = createDefaultStyle();

        mapContent = new MapContent();
        mapContent.setTitle("World Map");

        featureLayer = new FeatureLayer(featureSource, defaultStyle);
        mapContent.addLayer(featureLayer);

        if (mapPane != null) {
            remove(mapPane);
        }

        mapPane = new JMapPane(mapContent);
        mapPane.setBackground(Color.WHITE);
        mapPane.setDoubleBuffered(true);

        mapPane.addMouseListener(new MapMouseListener() {
            @Override
            public void onMouseClicked(MapMouseEvent ev) {
            }

            @Override
            public void onMouseDragged(MapMouseEvent ev) {
            }

            @Override
            public void onMouseEntered(MapMouseEvent ev) {
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
            }

            @Override
            public void onMouseReleased(MapMouseEvent ev) {
            }

            @Override
            public void onMouseWheelMoved(MapMouseEvent ev) {
                handleMouseWheel(ev);
            }
        });

        mapPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (mapPane != null && mapContent != null) {
                    SwingUtilities.invokeLater(() -> mapPane.repaint());
                }
            }
        });

        add(mapPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            validate();
            repaint();
        });
    }

    private Style createDefaultStyle() {
        try {
            org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK),
                filterFactory.literal(1.0)
            );

            Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(200, 220, 240, 255))
            );

            PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
                stroke, fill, null
            );

            Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
            Style style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);

            return style;
        } catch (Exception e) {
            System.err.println("Error creating default style: " + e.getMessage());
            e.printStackTrace();
            return createBasicFallbackStyle();
        }
    }

    private Style createBasicFallbackStyle() {
        org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
            filterFactory.literal(Color.GRAY),
            filterFactory.literal(0.5)
        );

        LineSymbolizer lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(lineSymbolizer);

        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private void handleMouseMove(MapMouseEvent ev) {
        // Prevent concurrent style updates
        if (isUpdatingStyle) {
            return;
        }

        try {
            org.geotools.geometry.Position2D worldPos = ev.getWorldPos();
            Coordinate coord = new Coordinate(worldPos.x, worldPos.y);

            SimpleFeature feature = getFeatureAtPosition(coord);

            // Only update if feature actually changed to reduce style update frequency
            if (feature != hoveredFeature) {
                synchronized (styleLock) {
                    if (isUpdatingStyle) {
                        return; // Double-check after acquiring lock
                    }
                    hoveredFeature = feature;
                    updateHoverDisplaySynchronized();
                }
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
        synchronized (styleLock) {
            updateHoverDisplaySynchronized();
        }
    }

    private void updateHoverDisplaySynchronized() {
        if (mapContent == null || featureLayer == null) {
            return;
        }

        isUpdatingStyle = true;
        try {
            // Clear existing hover layer if present
            if (hoverLayer != null) {
                mapContent.removeLayer(hoverLayer);
                hoverLayer = null;
            }

            // Create new hover layer if a feature is hovered
            if (hoveredFeature != null) {
                SimpleFeatureType featureType = featureSource.getSchema();
                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

                // Copy the hovered feature to the new layer
                featureBuilder.init(hoveredFeature);
                SimpleFeature hoverFeature = featureBuilder.buildFeature(null);

                MemoryFeatureCollection collection = new MemoryFeatureCollection(featureType);
                collection.add(hoverFeature);

                hoverLayer = new FeatureLayer(collection, createHoverStyle());
                mapContent.addLayer(hoverLayer);
            }

            // Simple repaint without additional synchronization
            SwingUtilities.invokeLater(() -> {
                if (mapPane != null) {
                    mapPane.repaint();
                }
                isUpdatingStyle = false;
            });
        } catch (Exception e) {
            isUpdatingStyle = false;
            System.err.println("Error updating hover display: " + e.getMessage());
        }
    }

    private Style createHoverStyle() {
        try {
            // RED borders only - keeps the light blue fill
            org.geotools.api.style.Stroke redStroke = styleFactory.createStroke(
                filterFactory.literal(Color.RED),
                filterFactory.literal(3.0)
            );

            Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(200, 220, 240, 255))
            );

            PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
                redStroke, fill, null
            );

            Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
            Style style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);

            return style;
        } catch (Exception e) {
            System.err.println("Error creating hover style: " + e.getMessage());
            return defaultStyle;
        }
    }

    private void clearHover() {
        synchronized (styleLock) {
            if (hoveredFeature != null && !isUpdatingStyle) {
                hoveredFeature = null;
                updateHoverDisplaySynchronized();
            }
        }
    }

    private void handleMouseWheel(MapMouseEvent ev) {
        if (mapPane == null) {
            return;
        }

        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        double zoomFactor = ev.getWheelAmount() > 0 ? 1.2 : 0.8;

        double width = currentBounds.getWidth();
        double height = currentBounds.getHeight();
        double newWidth = width * zoomFactor;
        double newHeight = height * zoomFactor;

        ReferencedEnvelope fullBounds = mapContent.getMaxBounds();
        if (fullBounds != null) {
            double fullWidth = fullBounds.getWidth();

            if (newWidth < fullWidth / MAX_ZOOM_SCALE) {
                return;
            }

            if (newWidth > fullWidth / MIN_ZOOM_SCALE) {
                return;
            }
        }

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

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
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

        ReferencedEnvelope fullBounds = mapContent.getMaxBounds();
        if (fullBounds != null) {
            double fullWidth = fullBounds.getWidth();
            if (width < fullWidth / MAX_ZOOM_SCALE) {
                return;
            }
        }

        double centerX = currentBounds.getMedian(0);
        double centerY = currentBounds.getMedian(1);

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - width / 2,
            centerX + width / 2,
            centerY - height / 2,
            centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
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

        ReferencedEnvelope fullBounds = mapContent.getMaxBounds();
        if (fullBounds != null) {
            double fullWidth = fullBounds.getWidth();
            if (width > fullWidth / MIN_ZOOM_SCALE) {
                return;
            }
        }

        double centerX = currentBounds.getMedian(0);
        double centerY = currentBounds.getMedian(1);

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - width / 2,
            centerX + width / 2,
            centerY - height / 2,
            centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
    }

    private void resetView() {
        if (mapPane == null || mapContent == null) {
            return;
        }

        ReferencedEnvelope bounds = mapContent.getMaxBounds();
        if (bounds != null) {
            SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(bounds));
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.opengl", "false");

        SwingUtilities.invokeLater(() -> {
            MainMapView view = new MainMapView();
            view.setVisible(true);
        });
    }
}
