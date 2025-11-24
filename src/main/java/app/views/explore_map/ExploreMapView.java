package app.views.explore_map;

import app.controllers.ExploreMapController;
import app.views.AbstractView;
import app.views.ViewModel;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Fill;
import org.geotools.api.style.LineSymbolizer;
import org.geotools.api.style.PolygonSymbolizer;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.data.memory.MemoryFeatureCollection;
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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * View for the Explore Map use case following Clean Architecture.
 * Handles zoom/pan/mode changes directly without controller/interactor.
 * Uses generic ViewModel instead of custom ExploreMapViewModel.
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity",
        "checkstyle:JavaNCSS", "checkstyle:MethodLength"})
public class ExploreMapView extends AbstractView {
    private static final double MIN_ZOOM_SCALE = 0.1;
    private static final double MAX_ZOOM_SCALE = 50.0;
    private static final int MAX_ZOOM_IN_LEVELS = 5;
    private static final int CURSOR_SIZE = 32;
    private static final int CURSOR_CIRCLE_SIZE = 16;
    private static final int CURSOR_CIRCLE_OFFSET = 4;
    private static final int CURSOR_LINE_START = 18;
    private static final int CURSOR_LINE_END = 28;
    private static final int CURSOR_CROSS_CENTER = 12;
    private static final int CURSOR_CROSS_MIN = 8;
    private static final int CURSOR_CROSS_MAX = 16;
    private static final float STROKE_WIDTH_THIN = 1.0f;
    private static final float STROKE_WIDTH_MEDIUM = 1.5f;
    private static final float STROKE_WIDTH_THICK = 2.0f;
    private static final float STROKE_WIDTH_EXTRA_THICK = 3.0f;
    private static final int HOVER_DELAY_MS = 50;
    private static final double ZOOM_IN_FACTOR = 0.8;
    private static final double ZOOM_OUT_FACTOR = 1.25;
    private static final double ZOOM_WHEEL_IN_FACTOR = 0.8;
    private static final double ZOOM_WHEEL_OUT_FACTOR = 1.2;
    private static final int GREEN_COMPONENT = 120;
    private static final int LIGHT_BLUE_R = 200;
    private static final int LIGHT_BLUE_G = 220;
    private static final int LIGHT_BLUE_B = 240;
    private static final int HOVER_RED = 255;
    private static final int HOVER_GREEN = 200;
    private static final int HOVER_BLUE = 200;
    private static final int HOVER_ALPHA = 150;
    private static final int SELECTED_RED = 100;
    private static final int SELECTED_GREEN = 149;
    private static final int SELECTED_BLUE = 237;
    private static final int SELECTED_ALPHA = 180;
    private static final int ARROW_BASE_Y = 24;
    private static final int ARROW_TIP_X = 20;

    private final String viewName = "explore_map";
    private final ViewModel<ExploreMapState> exploreMapViewModel;
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
    private JButton panButton;
    private JButton zoomButton;
    private JButton selectButton;
    private Cursor defaultCursor;
    private Cursor zoomCursor;
    private Cursor selectCursor;

    // Local state for UI operations (not in ViewModel)
    private int currentZoomLevel;
    private SimpleFeature hoveredFeature;

    // Thread safety for rendering
    private boolean isUpdatingStyle;
    private javax.swing.Timer hoverUpdateTimer;

    /**
     * Creates a new ExploreMapView.
     * @param viewModel the view model for this view
     */
    public ExploreMapView(final ViewModel<ExploreMapState> viewModel) {
        super(viewModel);
        this.exploreMapViewModel = viewModel;

        // Initialize state if not already set
        if (this.exploreMapViewModel.getState() == null) {
            this.exploreMapViewModel.updateState(new ExploreMapState());
        }

        initializeFactories();
        createCustomCursors();
        setupUI();
    }

    /**
     * Sets the controller for this view.
     * @param controller the controller
     */
    public void setController(final ExploreMapController controller) {
        this.controller = controller;
    }

    /**
     * Gets the view name.
     * @return the view name
     */
    public String getViewName() {
        return viewName;
    }

    @Override
    public void onViewOpened(String param) {
        // Initialize view when opened
    }

    @Override
    public void onViewClosed() {
        // Cleanup when view is closed
        if (mapContent != null) {
            mapContent.dispose();
        }
    }

    @Override
    public void onStateChange(final Object oldState, final Object newState) {
        if (!(newState instanceof ExploreMapState)) {
            return;
        }

        final ExploreMapState state = (ExploreMapState) newState;

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (state.isMapLoaded() && mapPane == null) {
            final SimpleFeatureSource source = state.getFeatureSource();
            if (source != null) {
                initializeMap(source);
            }
        }

        if (mapPane != null && featureSource != null) {
            updateSelectedDisplay();
        }
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
            final BufferedImage cursorImage = new BufferedImage(
                    CURSOR_SIZE, CURSOR_SIZE, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2d = cursorImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH_THICK));
            g2d.drawOval(CURSOR_CIRCLE_OFFSET, CURSOR_CIRCLE_OFFSET,
                    CURSOR_CIRCLE_SIZE, CURSOR_CIRCLE_SIZE);
            g2d.drawLine(CURSOR_LINE_START, CURSOR_LINE_START, CURSOR_LINE_END, CURSOR_LINE_END);

            g2d.setStroke(new BasicStroke(STROKE_WIDTH_MEDIUM));
            g2d.setColor(new Color(0, GREEN_COMPONENT, 0));
            g2d.drawLine(CURSOR_CROSS_CENTER, CURSOR_CROSS_MIN,
                    CURSOR_CROSS_CENTER, CURSOR_CROSS_MAX);
            g2d.drawLine(CURSOR_CROSS_MIN, CURSOR_CROSS_CENTER,
                    CURSOR_CROSS_MAX, CURSOR_CROSS_CENTER);

            g2d.dispose();

            return Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImage, new java.awt.Point(CURSOR_CROSS_CENTER, CURSOR_CROSS_CENTER),
                    "ZoomCursor");
        }
        catch (Exception ex) {
            return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        }
    }

    private Cursor createSelectionCursor() {
        try {
            final BufferedImage cursorImage = new BufferedImage(
                    CURSOR_SIZE, CURSOR_SIZE, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2d = cursorImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH_THICK));

            final int[] xPoints = {CURSOR_CROSS_MIN, CURSOR_CROSS_MIN, CURSOR_CROSS_CENTER,
                    CURSOR_CROSS_MAX, ARROW_TIP_X, CURSOR_CROSS_MAX, CURSOR_CROSS_CENTER};
            final int[] yPoints = {ARROW_BASE_Y, CURSOR_CROSS_MIN, CURSOR_CROSS_MIN,
                    CURSOR_CROSS_CENTER, CURSOR_CROSS_MIN, CURSOR_CROSS_MAX, CURSOR_CROSS_MAX};
            g2d.fillPolygon(xPoints, yPoints, xPoints.length);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH_THIN));
            g2d.drawPolygon(xPoints, yPoints, xPoints.length);

            g2d.dispose();

            return Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImage, new java.awt.Point(CURSOR_CROSS_MIN, CURSOR_CROSS_MIN),
                    "SelectCursor");
        }
        catch (Exception ex) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        final JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
    }

    private JPanel createControlPanel() {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        final JButton loadButton = new JButton("Load World Map");
        loadButton.addActionListener(event -> loadShapefile());

        final JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(event -> handleZoomIn());

        final JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(event -> handleZoomOut());

        final JButton resetButton = new JButton("Reset View");
        resetButton.addActionListener(event -> handleResetView());

        // Mode buttons - handled locally in the view
        panButton = new JButton("Pan Mode");
        panButton.addActionListener(event -> setInteractionMode("PAN"));

        zoomButton = new JButton("Zoom Mode");
        zoomButton.addActionListener(event -> setInteractionMode("ZOOM"));

        selectButton = new JButton("Select Mode");
        selectButton.addActionListener(event -> setInteractionMode("SELECT"));

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
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select World Map Shapefile");
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".shp");
            }

            @Override
            public String getDescription() {
                return "Shapefiles (*.shp)";
            }
        });

        final int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
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

        final ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        final double width = currentBounds.getWidth() * ZOOM_IN_FACTOR;
        final double height = currentBounds.getHeight() * ZOOM_IN_FACTOR;

        final double centerX = currentBounds.getCenterX();
        final double centerY = currentBounds.getCenterY();

        final ReferencedEnvelope newBounds = new ReferencedEnvelope(
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

        final ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        final double width = currentBounds.getWidth() * ZOOM_OUT_FACTOR;
        final double height = currentBounds.getHeight() * ZOOM_OUT_FACTOR;

        final double centerX = currentBounds.getCenterX();
        final double centerY = currentBounds.getCenterY();

        final ReferencedEnvelope newBounds = new ReferencedEnvelope(
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

        final ReferencedEnvelope bounds = mapContent.getMaxBounds();
        if (bounds != null) {
            currentZoomLevel = 0;
            SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(bounds));
        }
    }

    private void setInteractionMode(final String mode) {
        final ExploreMapState state = exploreMapViewModel.getState();
        state.setInteractionMode(mode);
        exploreMapViewModel.updateState(state);

        // Clear hover when changing modes
        if (!"PAN".equals(mode) && !"SELECT".equals(mode)) {
            hoveredFeature = null;
            updateHoverDisplay();
        }

        // Clear selection when leaving SELECT mode
        if (!"SELECT".equals(mode)) {
            state.setSelectedFeature(null);
            state.setSelectedCountryName(null);
            exploreMapViewModel.updateState(state);
            updateSelectedDisplay();
        }

        updateCursor();
        updateModeButtons();
    }

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:ExecutableStatementCount"})
    private void initializeMap(final SimpleFeatureSource source) {
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
            final org.geotools.renderer.GTRenderer renderer = mapPane.getRenderer();
            if (renderer instanceof org.geotools.renderer.lite.StreamingRenderer) {
                final org.geotools.renderer.lite.StreamingRenderer streamingRenderer =
                        (org.geotools.renderer.lite.StreamingRenderer) renderer;
                final java.util.Map<Object, Object> hints = new java.util.HashMap<>();
                hints.put("renderingThreads", 1);
                streamingRenderer.setRendererHints(hints);
            }
        }
        catch (Exception ex) {
            System.err.println("Warning: Could not configure single-threaded rendering: "
                    + ex.getMessage());
        }

        mapPane.addMouseListener(new MapMouseListener() {
            @Override
            public void onMouseClicked(final MapMouseEvent ev) {
                handleMouseClick(ev);
            }

            @Override
            public void onMouseDragged(final MapMouseEvent ev) {
                // No action needed
            }

            @Override
            public void onMouseEntered(final MapMouseEvent ev) {
                // No action needed
            }

            @Override
            public void onMouseExited(final MapMouseEvent ev) {
                hoveredFeature = null;
                updateHoverDisplay();
            }

            @Override
            public void onMouseMoved(final MapMouseEvent ev) {
                handleMouseMove(ev);
            }

            @Override
            public void onMousePressed(final MapMouseEvent ev) {
                // No action needed
            }

            @Override
            public void onMouseReleased(final MapMouseEvent ev) {
                // No action needed
            }

            @Override
            public void onMouseWheelMoved(final MapMouseEvent ev) {
                handleMouseWheel(ev);
            }
        });

        mapPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent event) {
                if (mapPane != null && mapContent != null) {
                    SwingUtilities.invokeLater(() -> {
                        final ReferencedEnvelope bounds = mapContent.getMaxBounds();
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
                final ReferencedEnvelope bounds = mapContent.getMaxBounds();
                if (bounds != null) {
                    mapPane.setDisplayArea(bounds);
                }
            }
        });
    }

    private void handleMouseMove(final MapMouseEvent event) {
        final ExploreMapState state = exploreMapViewModel.getState();
        final String mode = state.getInteractionMode();

        if ("ZOOM".equals(mode) || isUpdatingStyle) {
            return;
        }

        try {
            final org.geotools.geometry.Position2D worldPos = event.getWorldPos();
            final Coordinate coord = new Coordinate(worldPos.x, worldPos.y);
            final SimpleFeature feature = getFeatureAtPosition(coord);

            if (feature != hoveredFeature) {
                hoveredFeature = feature;

                if (hoverUpdateTimer != null && hoverUpdateTimer.isRunning()) {
                    hoverUpdateTimer.stop();
                }

                hoverUpdateTimer = new javax.swing.Timer(HOVER_DELAY_MS, ev ->
                        SwingUtilities.invokeLater(this::updateHoverDisplay));
                hoverUpdateTimer.setRepeats(false);
                hoverUpdateTimer.start();
            }
        }
        catch (Exception ex) {
            System.err.println("Error handling mouse move: " + ex.getMessage());
        }
    }

    private void handleMouseClick(final MapMouseEvent event) {
        final ExploreMapState state = exploreMapViewModel.getState();
        final String mode = state.getInteractionMode();
        final org.geotools.geometry.Position2D worldPos = event.getWorldPos();

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

    private void handleZoomAtPoint(final double xCoord, final double yCoord) {
        if (mapPane == null || currentZoomLevel >= MAX_ZOOM_IN_LEVELS) {
            return;
        }

        final ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        final double width = currentBounds.getWidth() * ZOOM_IN_FACTOR;
        final double height = currentBounds.getHeight() * ZOOM_IN_FACTOR;

        final ReferencedEnvelope newBounds = new ReferencedEnvelope(
                xCoord - width / 2, xCoord + width / 2,
                yCoord - height / 2, yCoord + height / 2,
                currentBounds.getCoordinateReferenceSystem()
        );

        currentZoomLevel++;
        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
    }

    private void handleMouseWheel(final MapMouseEvent event) {
        if (mapPane == null) {
            return;
        }

        final ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
        if (currentBounds == null) {
            return;
        }

        final double zoomFactor = event.getWheelAmount() > 0
                ? ZOOM_WHEEL_OUT_FACTOR : ZOOM_WHEEL_IN_FACTOR;
        final double width = currentBounds.getWidth();
        final double height = currentBounds.getHeight();
        final double newWidth = width * zoomFactor;
        final double newHeight = height * zoomFactor;

        final ReferencedEnvelope fullBounds = mapContent.getMaxBounds();
        if (fullBounds != null) {
            final double fullWidth = fullBounds.getWidth();
            if (newWidth < fullWidth / MAX_ZOOM_SCALE || newWidth > fullWidth / MIN_ZOOM_SCALE) {
                return;
            }
        }

        final org.geotools.geometry.Position2D mousePos = event.getWorldPos();
        final double centerX = mousePos.x;
        final double centerY = mousePos.y;

        final ReferencedEnvelope newBounds = new ReferencedEnvelope(
                centerX - newWidth / 2, centerX + newWidth / 2,
                centerY - newHeight / 2, centerY + newHeight / 2,
                currentBounds.getCoordinateReferenceSystem()
        );

        SwingUtilities.invokeLater(() -> mapPane.setDisplayArea(newBounds));
    }

    private SimpleFeature getFeatureAtPosition(final Coordinate worldPos) {
        if (featureSource == null) {
            return null;
        }

        try {
            final org.geotools.data.simple.SimpleFeatureCollection collection =
                    featureSource.getFeatures();
            try (org.geotools.data.simple.SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    final SimpleFeature feature = iterator.next();
                    final Geometry geometry = (Geometry) feature.getDefaultGeometry();

                    if (geometry != null) {
                        final Point point = geometry.getFactory().createPoint(worldPos);
                        if (geometry.contains(point)) {
                            return feature;
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            System.err.println("Error getting feature at position: " + ex.getMessage());
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
                final org.geotools.api.feature.simple.SimpleFeatureType featureType =
                        featureSource.getSchema();
                final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
                featureBuilder.init(hoveredFeature);
                final SimpleFeature hoverFeature = featureBuilder.buildFeature(null);

                final MemoryFeatureCollection collection = new MemoryFeatureCollection(featureType);
                collection.add(hoverFeature);

                hoverLayer = new FeatureLayer(collection, createHoverStyle());
                mapContent.addLayer(hoverLayer);
            }

            if (mapPane != null) {
                mapPane.repaint();
            }
        }
        catch (Exception ex) {
            System.err.println("Error updating hover display: " + ex.getMessage());
        }
        finally {
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

                final ExploreMapState state = exploreMapViewModel.getState();
                final SimpleFeature selectedFeature = state.getSelectedFeature();

                if (selectedFeature != null) {
                    final org.geotools.api.feature.simple.SimpleFeatureType featureType =
                            featureSource.getSchema();
                    final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
                    featureBuilder.init(selectedFeature);
                    final SimpleFeature selectFeature = featureBuilder.buildFeature(null);

                    final MemoryFeatureCollection collection =
                            new MemoryFeatureCollection(featureType);
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
            }
            catch (Exception ex) {
                System.err.println("Error updating selected feature style: " + ex.getMessage());
            }
        });
    }

    private Style createDefaultStyle() {
        try {
            final org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
                    filterFactory.literal(Color.BLACK),
                    filterFactory.literal(STROKE_WIDTH_THIN)
            );

            final Fill fill = styleFactory.createFill(
                    filterFactory.literal(new Color(LIGHT_BLUE_R, LIGHT_BLUE_G,
                            LIGHT_BLUE_B, Color.OPAQUE))
            );

            final PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
                    stroke, fill, null);
            final Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
            final Style style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);

            return style;
        }
        catch (Exception ex) {
            System.err.println("Error creating default style: " + ex.getMessage());
            return createBasicFallbackStyle();
        }
    }

    private Style createBasicFallbackStyle() {
        final org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.GRAY),
                filterFactory.literal(0.5)
        );

        final LineSymbolizer lineSymbolizer = styleFactory.createLineSymbolizer(stroke, null);
        final Rule rule = styleFactory.createRule();
        rule.symbolizers().add(lineSymbolizer);

        final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        final Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private Style createHoverStyle() {
        try {
            final org.geotools.api.style.Stroke redStroke = styleFactory.createStroke(
                    filterFactory.literal(Color.RED),
                    filterFactory.literal(STROKE_WIDTH_EXTRA_THICK)
            );

            final Fill fill = styleFactory.createFill(
                    filterFactory.literal(new Color(HOVER_RED, HOVER_GREEN,
                            HOVER_BLUE, HOVER_ALPHA))
            );

            final PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
                    redStroke, fill, null);
            final Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
            final Style style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);

            return style;
        }
        catch (Exception ex) {
            System.err.println("Error creating hover style: " + ex.getMessage());
            return defaultStyle;
        }
    }

    private Style createSelectedStyle() {
        try {
            final Fill blueFill = styleFactory.createFill(
                    filterFactory.literal(new Color(SELECTED_RED, SELECTED_GREEN,
                            SELECTED_BLUE, SELECTED_ALPHA))
            );

            final org.geotools.api.style.Stroke blackStroke = styleFactory.createStroke(
                    filterFactory.literal(Color.BLACK),
                    filterFactory.literal(STROKE_WIDTH_EXTRA_THICK)
            );

            final PolygonSymbolizer symbolizer = styleFactory.createPolygonSymbolizer(
                    blackStroke, blueFill, null);
            final Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
            final Style style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);

            return style;
        }
        catch (Exception ex) {
            System.err.println("Error creating selected style: " + ex.getMessage());
            return defaultStyle;
        }
    }

    private void updateCursor() {
        if (mapPane == null) {
            return;
        }

        final ExploreMapState state = exploreMapViewModel.getState();
        final String mode = state.getInteractionMode();

        final Cursor cursor;
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

        final ExploreMapState state = exploreMapViewModel.getState();
        final String mode = state.getInteractionMode();

        panButton.setEnabled(!"PAN".equals(mode));
        zoomButton.setEnabled(!"ZOOM".equals(mode));
        selectButton.setEnabled(!"SELECT".equals(mode));

        panButton.setBackground("PAN".equals(mode) ? Color.LIGHT_GRAY : null);
        zoomButton.setBackground("ZOOM".equals(mode) ? Color.LIGHT_GRAY : null);
        selectButton.setBackground("SELECT".equals(mode) ? Color.LIGHT_GRAY : null);
    }
}

