package view;

import adapters.ExploreMap.ExploreMapController;
import adapters.ExploreMap.ExploreMapState;
import adapters.ViewModel;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.*;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.*;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * View for the Explore Map use case following Clean Architecture.
 * Handles zoom/pan/mode changes directly without controller/interactor.
 * Uses generic ViewModel instead of custom ExploreMapViewModel.
 */
public class ExploreMapView extends JPanel implements PropertyChangeListener {
    private static final double MIN_ZOOM_SCALE = 0.1;
    private static final double MAX_ZOOM_SCALE = 50.0;
    private static final int MAX_ZOOM_IN_LEVELS = 5;

    private final String viewName = "explore_map";
    private final ViewModel<ExploreMapState> viewModel;
    private ExploreMapController controller;

    // Map components
    private JMapPane mapPane;
    private MapContent mapContent;
    private FeatureLayer featureLayer;
    private FeatureLayer hoverLayer;
    private FeatureLayer selectedLayer;
    private SimpleFeatureSource featureSource;
    private StyleFactory styleFactory;
    private FilterFactory filterFactory;
    private Style defaultStyle;

    // UI components
    private JButton panButton, zoomButton, selectButton;
    private Cursor defaultCursor, zoomCursor, selectCursor;

    // Local state for UI operations (not in ViewModel)
    private int currentZoomLevel = 0;
    private SimpleFeature hoveredFeature;

    // Thread safety for rendering
    private volatile boolean isUpdatingStyle = false;
    private javax.swing.Timer hoverUpdateTimer;

    public ExploreMapView(ViewModel<ExploreMapState> viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        // Initialize state if not already set
        if (this.viewModel.getState() == null) {
            this.viewModel.setState(new ExploreMapState());
        }

        initializeFactories();
        createCustomCursors();
        setupUI();
    }

    public void setController(ExploreMapController controller) {
        this.controller = controller;
    }

    public String getViewName() {
        return viewName;
    }

    private void initializeFactories() {
        styleFactory = CommonFactoryFinder.getStyleFactory();
        filterFactory = CommonFactoryFinder.getFilterFactory();
    }

    private void createCustomCursors() {
        defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        zoomCursor = createMagnifyingGlassCursor();
        selectCursor = createSelectionCursor();
    }

    private Cursor createMagnifyingGlassCursor() {
        try {
            BufferedImage cursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cursorImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(4, 4, 16, 16);
            g2d.drawLine(18, 18, 28, 28);

            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setColor(new Color(0, 120, 0));
            g2d.drawLine(12, 8, 12, 16);
            g2d.drawLine(8, 12, 16, 12);

            g2d.dispose();

            return Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImage, new java.awt.Point(12, 12), "ZoomCursor");
        } catch (Exception e) {
            return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        }
    }

    private Cursor createSelectionCursor() {
        try {
            BufferedImage cursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cursorImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.0f));

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
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton loadButton = new JButton("Load World Map");
        loadButton.addActionListener(e -> loadShapefile());

        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(e -> handleZoomIn());

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(e -> handleZoomOut());

        JButton resetButton = new JButton("Reset View");
        resetButton.addActionListener(e -> handleResetView());

        // Mode buttons - handled locally in the view
        panButton = new JButton("Pan Mode");
        panButton.addActionListener(e -> setInteractionMode("PAN"));

        zoomButton = new JButton("Zoom Mode");
        zoomButton.addActionListener(e -> setInteractionMode("ZOOM"));

        selectButton = new JButton("Select Mode");
        selectButton.addActionListener(e -> setInteractionMode("SELECT"));

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

    private void loadShapefile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select World Map Shapefile");
        fileChooser.setFileFilter(new FileFilter() {
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
            File file = fileChooser.getSelectedFile();
            if (controller != null) {
                controller.loadMap(file.getAbsolutePath());
            }
        }
    }

    // Zoom/pan methods - handled locally in the view (professor's recommendation)
    private void handleZoomIn() {
        if (mapPane == null || currentZoomLevel >= MAX_ZOOM_IN_LEVELS) {
            return;
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
            centerX - width / 2, centerX + width / 2,
            centerY - height / 2, centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        currentZoomLevel++;
        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
    }

    private void handleZoomOut() {
        if (mapPane == null || currentZoomLevel <= 0) {
            return;
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
            centerX - width / 2, centerX + width / 2,
            centerY - height / 2, centerY + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        currentZoomLevel--;
        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
    }

    private void handleResetView() {
        if (mapPane == null || mapContent == null) {
            return;
        }

        ReferencedEnvelope bounds = mapContent.getMaxBounds();
        if (bounds != null) {
            currentZoomLevel = 0;
            SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(bounds));
        }
    }

    private void setInteractionMode(String mode) {
        ExploreMapState state = viewModel.getState();
        state.setInteractionMode(mode);

        // Clear hover when changing modes
        if (!"PAN".equals(mode) && !"SELECT".equals(mode)) {
            hoveredFeature = null;
            updateHoverDisplay();
        }

        // Clear selection when leaving SELECT mode
        if (!"SELECT".equals(mode)) {
            state.setSelectedFeature(null);
            state.setSelectedCountryName(null);
            updateSelectedDisplay();
        }

        updateCursor();
        updateModeButtons();
    }

    private void initializeMap(SimpleFeatureSource source) {
        this.featureSource = source;
        defaultStyle = createDefaultStyle();

        if (mapContent != null) {
            mapContent.dispose();
        }

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

        // Configure single-threaded rendering to prevent Graphics2D threading issues
        try {
            org.geotools.renderer.GTRenderer renderer = mapPane.getRenderer();
            if (renderer instanceof org.geotools.renderer.lite.StreamingRenderer) {
                org.geotools.renderer.lite.StreamingRenderer streamingRenderer =
                    (org.geotools.renderer.lite.StreamingRenderer) renderer;
                java.util.Map<Object, Object> hints = new java.util.HashMap<>();
                hints.put("renderingThreads", 1);
                streamingRenderer.setRendererHints(hints);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not configure single-threaded rendering: " + e.getMessage());
        }

        mapPane.addMouseListener(new MapMouseListener() {
            @Override
            public void onMouseClicked(MapMouseEvent ev) {
                handleMouseClick(ev);
            }

            @Override
            public void onMouseDragged(MapMouseEvent ev) {}

            @Override
            public void onMouseEntered(MapMouseEvent ev) {}

            @Override
            public void onMouseExited(MapMouseEvent ev) {
                hoveredFeature = null;
                updateHoverDisplay();
            }

            @Override
            public void onMouseMoved(MapMouseEvent ev) {
                handleMouseMove(ev);
            }

            @Override
            public void onMousePressed(MapMouseEvent ev) {}

            @Override
            public void onMouseReleased(MapMouseEvent ev) {}

            @Override
            public void onMouseWheelMoved(MapMouseEvent ev) {
                handleMouseWheel(ev);
            }
        });

        mapPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (mapPane != null && mapContent != null) {
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
        updateCursor();
        updateModeButtons();

        SwingUtilities.invokeLater(() -> {
            validate();
            repaint();

            if (mapPane != null && mapContent != null) {
                ReferencedEnvelope bounds = mapContent.getMaxBounds();
                if (bounds != null) {
                    mapPane.setDisplayArea(bounds);
                }
            }
        });
    }

    private void handleMouseMove(MapMouseEvent ev) {
        ExploreMapState state = viewModel.getState();
        String mode = state.getInteractionMode();

        if ("ZOOM".equals(mode) || isUpdatingStyle) {
            return;
        }

        try {
            org.geotools.geometry.Position2D worldPos = ev.getWorldPos();
            Coordinate coord = new Coordinate(worldPos.x, worldPos.y);
            SimpleFeature feature = getFeatureAtPosition(coord);

            if (feature != hoveredFeature) {
                hoveredFeature = feature;

                if (hoverUpdateTimer != null && hoverUpdateTimer.isRunning()) {
                    hoverUpdateTimer.stop();
                }

                hoverUpdateTimer = new javax.swing.Timer(50, e ->
                    SwingUtilities.invokeLater(this::updateHoverDisplay));
                hoverUpdateTimer.setRepeats(false);
                hoverUpdateTimer.start();
            }
        } catch (Exception e) {
            System.err.println("Error handling mouse move: " + e.getMessage());
        }
    }

    private void handleMouseClick(MapMouseEvent ev) {
        ExploreMapState state = viewModel.getState();
        String mode = state.getInteractionMode();
        org.geotools.geometry.Position2D worldPos = ev.getWorldPos();

        switch (mode) {
            case "ZOOM":
                handleZoomAtPoint(worldPos.x, worldPos.y);
                break;
            case "SELECT":
                if (controller != null) {
                    controller.selectFeature(worldPos.x, worldPos.y);
                }
                break;
            case "PAN":
            default:
                // PAN mode uses default map pane behavior (dragging to pan)
                break;
        }
    }

    private void handleZoomAtPoint(double x, double y) {
        if (mapPane == null || currentZoomLevel >= MAX_ZOOM_IN_LEVELS) {
            return;
        }

        ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        double zoomFactor = 0.8;
        double width = currentBounds.getWidth() * zoomFactor;
        double height = currentBounds.getHeight() * zoomFactor;

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            x - width / 2, x + width / 2,
            y - height / 2, y + height / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        currentZoomLevel++;
        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
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
            if (newWidth < fullWidth / MAX_ZOOM_SCALE || newWidth > fullWidth / MIN_ZOOM_SCALE) {
                return;
            }
        }

        org.geotools.geometry.Position2D mousePos = ev.getWorldPos();
        double centerX = mousePos.x;
        double centerY = mousePos.y;

        ReferencedEnvelope newBounds = new ReferencedEnvelope(
            centerX - newWidth / 2, centerX + newWidth / 2,
            centerY - newHeight / 2, centerY + newHeight / 2,
            currentBounds.getCoordinateReferenceSystem()
        );

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
    }

    private SimpleFeature getFeatureAtPosition(Coordinate worldPos) {
        if (featureSource == null) {
            return null;
        }

        try {
            org.geotools.data.simple.SimpleFeatureCollection collection = featureSource.getFeatures();
            try (org.geotools.data.simple.SimpleFeatureIterator iterator = collection.features()) {
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
        } catch (Exception e) {
            System.err.println("Error getting feature at position: " + e.getMessage());
        }

        return null;
    }

    private void updateHoverDisplay() {
        if (mapContent == null || featureLayer == null || isUpdatingStyle) {
            return;
        }

        isUpdatingStyle = true;
        try {
            if (hoverLayer != null) {
                mapContent.removeLayer(hoverLayer);
                hoverLayer = null;
            }

            if (hoveredFeature != null) {
                org.geotools.api.feature.simple.SimpleFeatureType featureType = featureSource.getSchema();
                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
                featureBuilder.init(hoveredFeature);
                SimpleFeature hoverFeature = featureBuilder.buildFeature(null);

                MemoryFeatureCollection collection = new MemoryFeatureCollection(featureType);
                collection.add(hoverFeature);

                hoverLayer = new FeatureLayer(collection, createHoverStyle());
                mapContent.addLayer(hoverLayer);
            }

            if (mapPane != null) {
                mapPane.repaint();
            }
        } catch (Exception e) {
            System.err.println("Error updating hover display: " + e.getMessage());
        } finally {
            isUpdatingStyle = false;
        }
    }

    private void updateSelectedDisplay() {
        if (mapContent == null || featureLayer == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                if (selectedLayer != null) {
                    mapContent.removeLayer(selectedLayer);
                    selectedLayer = null;
                }

                ExploreMapState state = viewModel.getState();
                SimpleFeature selectedFeature = state.getSelectedFeature();

                if (selectedFeature != null) {
                    org.geotools.api.feature.simple.SimpleFeatureType featureType = featureSource.getSchema();
                    SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
                    featureBuilder.init(selectedFeature);
                    SimpleFeature selectFeature = featureBuilder.buildFeature(null);

                    MemoryFeatureCollection collection = new MemoryFeatureCollection(featureType);
                    collection.add(selectFeature);

                    selectedLayer = new FeatureLayer(collection, createSelectedStyle());
                    mapContent.addLayer(selectedLayer);

                    if (state.getSelectedCountryName() != null) {
                        System.out.println("Selected country: " + state.getSelectedCountryName());
                    }
                }

                if (mapPane != null) {
                    mapPane.repaint();
                }
            } catch (Exception e) {
                System.err.println("Error updating selected feature style: " + e.getMessage());
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

            PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);
            Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
            Style style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);

            return style;
        } catch (Exception e) {
            System.err.println("Error creating default style: " + e.getMessage());
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

    private Style createHoverStyle() {
        try {
            org.geotools.api.style.Stroke redStroke = styleFactory.createStroke(
                filterFactory.literal(Color.RED),
                filterFactory.literal(3.0)
            );

            Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(255, 200, 200, 150))
            );

            PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(redStroke, fill, null);
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

    private Style createSelectedStyle() {
        try {
            Fill blueFill = styleFactory.createFill(
                filterFactory.literal(new Color(100, 149, 237, 180))
            );

            org.geotools.api.style.Stroke blackStroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK),
                filterFactory.literal(3.0)
            );

            PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(blackStroke, blueFill, null);
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

    private void updateCursor() {
        if (mapPane == null) {
            return;
        }

        ExploreMapState state = viewModel.getState();
        String mode = state.getInteractionMode();

        Cursor cursor;
        switch (mode) {
            case "ZOOM":
                cursor = zoomCursor;
                break;
            case "SELECT":
                cursor = selectCursor;
                break;
            case "PAN":
            default:
                cursor = defaultCursor;
                break;
        }
        mapPane.setCursor(cursor);
    }

    private void updateModeButtons() {
        if (panButton == null || zoomButton == null || selectButton == null) {
            return;
        }

        ExploreMapState state = viewModel.getState();
        String mode = state.getInteractionMode();

        panButton.setEnabled(!"PAN".equals(mode));
        zoomButton.setEnabled(!"ZOOM".equals(mode));
        selectButton.setEnabled(!"SELECT".equals(mode));

        panButton.setBackground("PAN".equals(mode) ? Color.LIGHT_GRAY : null);
        zoomButton.setBackground("ZOOM".equals(mode) ? Color.LIGHT_GRAY : null);
        selectButton.setBackground("SELECT".equals(mode) ? Color.LIGHT_GRAY : null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ExploreMapState state = viewModel.getState();

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this,
                state.getErrorMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (state.isMapLoaded() && mapPane == null) {
            SimpleFeatureSource source = state.getFeatureSource();
            if (source != null) {
                initializeMap(source);
            }
        }

        if (mapPane != null && featureSource != null) {
            updateSelectedDisplay();
        }
    }
}
