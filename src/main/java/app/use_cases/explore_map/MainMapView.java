package app.use_cases.explore_map;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.*;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainMapView extends JFrame {

    private static final double MIN_ZOOM_SCALE = 0.1;  // Maximum zoom out
    private static final double MAX_ZOOM_SCALE = 50.0; // Maximum zoom in
    private static final int MAX_ZOOM_IN_LEVELS = 5;   // Maximum zoom in levels

    // Interaction modes
    public enum InteractionMode {
        PAN,        // Default mode - pan and hover
        ZOOM,       // Zoom mode - click to zoom in
        SELECT      // Selection mode - click to select countries
    }

    private JMapPane mapPane;
    private MapContent mapContent;
    private FeatureLayer featureLayer;
    private FeatureLayer hoverLayer;  // Overlay layer for hover effects
    private FeatureLayer selectedLayer; // Layer for selected countries
    private SimpleFeatureSource featureSource;
    private StyleFactory styleFactory;
    private FilterFactory filterFactory;
    private Style defaultStyle;
    private SimpleFeature hoveredFeature;
    private SimpleFeature selectedFeature;

    // Interaction mode and cursor management
    private InteractionMode currentMode = InteractionMode.PAN;
    private Cursor defaultCursor;
    private Cursor zoomCursor;
    private Cursor selectCursor;
    private JButton panButton, zoomButton, selectButton;

    // Basic synchronization for style updates - enhanced for Graphics2D thread safety
    private final Object styleLock = new Object();
    private volatile boolean isUpdatingStyle = false;

    // Timer to debounce hover updates and ensure single-threaded graphics operations
    private javax.swing.Timer hoverUpdateTimer;

    // Current zoom level, starts at 0 (default view)
    private int currentZoomLevel = 0;

    public MainMapView() {
        initializeFactories();
        createCustomCursors();
        setupFrame();
    }

    private void initializeFactories() {
        styleFactory = CommonFactoryFinder.getStyleFactory();
        filterFactory = CommonFactoryFinder.getFilterFactory();
    }

    private void createCustomCursors() {
        // Create default cursor
        defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

        // Create magnifying glass cursor for zoom mode
        zoomCursor = createMagnifyingGlassCursor();

        // Create selection cursor for country selection
        selectCursor = createSelectionCursor();
    }

    private Cursor createMagnifyingGlassCursor() {
        try {
            // Create a magnifying glass icon
            BufferedImage cursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cursorImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw magnifying glass
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.0f));

            // Glass circle
            g2d.drawOval(4, 4, 16, 16);

            // Handle
            g2d.drawLine(18, 18, 28, 28);

            // Plus sign inside for zoom in
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setColor(new Color(0, 120, 0));
            g2d.drawLine(12, 8, 12, 16);  // Vertical line
            g2d.drawLine(8, 12, 16, 12);  // Horizontal line

            g2d.dispose();

            return Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImage, new java.awt.Point(12, 12), "ZoomCursor");
        } catch (Exception e) {
            System.err.println("Failed to create zoom cursor: " + e.getMessage());
            return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        }
    }

    private Cursor createSelectionCursor() {
        try {
            // Create a selection/pointing hand cursor
            BufferedImage cursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cursorImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw hand pointer
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.0f));

            // Simple pointer arrow
            int[] xPoints = {8, 8, 12, 16, 20, 16, 12};
            int[] yPoints = {24, 8, 8, 12, 8, 16, 16};
            g2d.fillPolygon(xPoints, yPoints, 7);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawPolygon(xPoints, yPoints, 7);

            g2d.dispose();

            return Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImage, new java.awt.Point(8, 8), "SelectCursor");
        } catch (Exception e) {
            System.err.println("Failed to create select cursor: " + e.getMessage());
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
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

        // Mode buttons
        panButton = new JButton("Pan Mode");
        panButton.addActionListener(e -> setInteractionMode(InteractionMode.PAN));

        zoomButton = new JButton("Zoom Mode");
        zoomButton.addActionListener(e -> setInteractionMode(InteractionMode.ZOOM));

        selectButton = new JButton("Select Mode");
        selectButton.addActionListener(e -> setInteractionMode(InteractionMode.SELECT));

        panel.add(loadButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(zoomInButton);
        panel.add(zoomOutButton);
        panel.add(resetButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(panButton);
        panel.add(zoomButton);
        panel.add(selectButton);

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
                handleMouseClick(ev);
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
                    // Auto-fit the map to the window size when resized
                    SwingUtilities.invokeLater(() -> {
                        ReferencedEnvelope bounds = mapContent.getMaxBounds();
                        if (bounds != null) {
                            mapPane.setDisplayArea(bounds);
                        }
                    });
                }
            }
        });

        add(mapPane, BorderLayout.CENTER);

        // Initialize cursor and mode buttons after map is loaded
        updateCursor();
        updateModeButtons();

        SwingUtilities.invokeLater(() -> {
            validate();
            repaint();

            // Auto-fit the map to window size after loading
            if (mapPane != null && mapContent != null) {
                ReferencedEnvelope bounds = mapContent.getMaxBounds();
                if (bounds != null) {
                    mapPane.setDisplayArea(bounds);
                }
            }
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
        // Only show hover effects in PAN and SELECT modes
        if (currentMode == InteractionMode.ZOOM) {
            return;
        }

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
                hoveredFeature = feature;

                // Cancel any pending update
                if (hoverUpdateTimer != null && hoverUpdateTimer.isRunning()) {
                    hoverUpdateTimer.stop();
                }

                // Schedule update on EDT with debouncing to prevent multiple Graphics2D access
                hoverUpdateTimer = new javax.swing.Timer(50, e -> {
                    // This executes on EDT - safe for Graphics2D operations
                    SwingUtilities.invokeLater(this::updateHoverDisplayOnEDT);
                });
                hoverUpdateTimer.setRepeats(false);
                hoverUpdateTimer.start();
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

    private void updateHoverDisplayOnEDT() {
        // This method ensures ALL graphics operations happen on EDT only
        if (mapContent == null || featureLayer == null || isUpdatingStyle) {
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

            // Direct repaint - we're already on EDT
            if (mapPane != null) {
                mapPane.repaint();
            }

        } catch (Exception e) {
            System.err.println("Error updating hover display: " + e.getMessage());
        } finally {
            isUpdatingStyle = false;
        }
    }

    private Style createHoverStyle() {
        try {
            // RED borders for hover
            org.geotools.api.style.Stroke redStroke = styleFactory.createStroke(
                filterFactory.literal(Color.RED),
                filterFactory.literal(3.0)
            );

            Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(255, 200, 200, 150))
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
        hoveredFeature = null;

        // Cancel any pending updates
        if (hoverUpdateTimer != null && hoverUpdateTimer.isRunning()) {
            hoverUpdateTimer.stop();
        }

        // Ensure clear happens on EDT
        SwingUtilities.invokeLater(this::updateHoverDisplayOnEDT);
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
        if (mapPane == null || currentZoomLevel >= MAX_ZOOM_IN_LEVELS) {
            return; // Prevent zooming in beyond the maximum level
        }

        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        double zoomFactor = 0.8;
        double width = currentBounds.getWidth() * zoomFactor;
        double height = currentBounds.getHeight() * zoomFactor;

        double centerX = currentBounds.getCenterX();
        double centerY = currentBounds.getCenterY();

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - width / 2,
            centerX + width / 2,
            centerY - height / 2,
            centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));

        // Increment zoom level
        currentZoomLevel++;
    }

    private void zoomOut() {
        if (mapPane == null || currentZoomLevel <= 0) {
            return; // Prevent zooming out beyond the default view
        }

        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        double zoomFactor = 1.25;
        double width = currentBounds.getWidth() * zoomFactor;
        double height = currentBounds.getHeight() * zoomFactor;

        double centerX = currentBounds.getCenterX();
        double centerY = currentBounds.getCenterY();

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - width / 2,
            centerX + width / 2,
            centerY - height / 2,
            centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));

        // Decrement zoom level
        currentZoomLevel--;
    }

    private void resetView() {
        if (mapPane == null || mapContent == null) {
            return;
        }

        ReferencedEnvelope bounds = mapContent.getMaxBounds();
        if (bounds != null) {
            SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(bounds));
        }

        // Reset zoom level to default
        currentZoomLevel = 0;
    }

    private void setInteractionMode(InteractionMode mode) {
        currentMode = mode;

        // Clear any existing hover/selection when changing modes
        clearHover();
        if (mode != InteractionMode.SELECT) {
            clearSelection();
        }

        // Update cursor and button states
        updateCursor();
        updateModeButtons();
    }

    private void updateCursor() {
        if (mapPane == null) {
            return; // Map not loaded yet
        }

        Cursor cursor;
        switch (currentMode) {
            case ZOOM:
                cursor = zoomCursor;
                break;
            case SELECT:
                cursor = selectCursor;
                break;
            case PAN:
            default:
                cursor = defaultCursor;
                break;
        }
        mapPane.setCursor(cursor);
    }

    private void updateModeButtons() {
        if (panButton == null || zoomButton == null || selectButton == null) {
            return; // Buttons not initialized yet
        }

        // Update button appearance to show current mode
        panButton.setEnabled(currentMode != InteractionMode.PAN);
        zoomButton.setEnabled(currentMode != InteractionMode.ZOOM);
        selectButton.setEnabled(currentMode != InteractionMode.SELECT);

        // Optional: Change button background color to indicate active mode
        panButton.setBackground(currentMode == InteractionMode.PAN ? Color.LIGHT_GRAY : null);
        zoomButton.setBackground(currentMode == InteractionMode.ZOOM ? Color.LIGHT_GRAY : null);
        selectButton.setBackground(currentMode == InteractionMode.SELECT ? Color.LIGHT_GRAY : null);
    }

    private void handleMouseClick(MapMouseEvent ev) {
        switch (currentMode) {
            case ZOOM:
                // Zoom in at the clicked point
                zoomAtPoint(ev);
                break;
            case SELECT:
                // Select feature on click
                selectFeatureAtMouseEvent(ev);
                break;
            case PAN:
            default:
                // PAN mode uses default map pane behavior (dragging to pan)
                break;
        }
    }

    private void zoomAtPoint(MapMouseEvent ev) {
        if (mapPane == null || currentZoomLevel >= MAX_ZOOM_IN_LEVELS) {
            return; // Prevent zooming in beyond the maximum level
        }

        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        double zoomFactor = 0.8;
        double width = currentBounds.getWidth() * zoomFactor;
        double height = currentBounds.getHeight() * zoomFactor;

        // Use clicked point as center
        org.geotools.geometry.Position2D clickPos = ev.getWorldPos();
        double centerX = clickPos.x;
        double centerY = clickPos.y;

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - width / 2,
            centerX + width / 2,
            centerY - height / 2,
            centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));

        // Increment zoom level
        currentZoomLevel++;
    }

    private void selectFeatureAtMouseEvent(MapMouseEvent ev) {
        org.geotools.geometry.Position2D worldPos = ev.getWorldPos();
        Coordinate coord = new Coordinate(worldPos.x, worldPos.y);

        SimpleFeature feature = getFeatureAtPosition(coord);

        if (feature != null) {
            // Feature found under mouse click
            setSelectedFeature(feature);
        } else {
            // Clicked on empty space - clear selection
            clearSelection();
        }
    }

    private void setSelectedFeature(SimpleFeature feature) {
        selectedFeature = feature;

        // Update style of the selected feature
        updateSelectedFeatureStyle();

        // Print country name if available
        try {
            Object nameAttr = feature.getAttribute("NAME");
            if (nameAttr != null) {
                System.out.println("Selected country: " + nameAttr.toString());
            }
        } catch (Exception e) {
            System.out.println("Selected feature: " + feature.getID());
        }
    }

    private void updateSelectedFeatureStyle() {
        if (mapContent == null || featureLayer == null) {
            return;
        }

        try {
            // Clear existing selected layer if present
            if (selectedLayer != null) {
                mapContent.removeLayer(selectedLayer);
                selectedLayer = null;
            }

            // Create new selected layer for the currently selected feature
            if (selectedFeature != null) {
                SimpleFeatureType featureType = featureSource.getSchema();
                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

                // Copy the selected feature to the new layer
                featureBuilder.init(selectedFeature);
                SimpleFeature selectFeature = featureBuilder.buildFeature(null);

                MemoryFeatureCollection collection = new MemoryFeatureCollection(featureType);
                collection.add(selectFeature);

                selectedLayer = new FeatureLayer(collection, createSelectedStyle());
                mapContent.addLayer(selectedLayer);
            }

            // Repaint map to show the updated selection
            SwingUtilities.invokeLater(() -> {
                if (mapPane != null) {
                    mapPane.repaint();
                }
            });
        } catch (Exception e) {
            System.err.println("Error updating selected feature style: " + e.getMessage());
        }
    }

    private Style createSelectedStyle() {
        try {
            // BLUE fill with thick BLACK borders for selected features
            Fill blueFill = styleFactory.createFill(
                filterFactory.literal(new Color(100, 149, 237, 180))
            );

            org.geotools.api.style.Stroke blackStroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK),
                filterFactory.literal(3.0)
            );

            PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
                blackStroke, blueFill, null
            );

            Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
            Style style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);

            return style;
        } catch (Exception e) {
            System.err.println("Error creating selected style: " + e.getMessage());
            return defaultStyle;
        }
    }

    private void clearSelection() {
        selectedFeature = null;

        // Clear the selected layer if it exists
        if (selectedLayer != null) {
            mapContent.removeLayer(selectedLayer);
            selectedLayer = null;
        }

        // Repaint map to remove the selection
        SwingUtilities.invokeLater(() -> {
            if (mapPane != null) {
                mapPane.repaint();
            }
        });
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
