package app.views.explore_map;

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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import app.NavigationService;
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
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import app.controllers.ExploreMapController;
import app.views.AbstractView;
import app.views.ViewModel;
import lombok.Getter;
import lombok.Setter;

/**
 * View for the Explore Map use case following Clean Architecture.
 * Handles zoom/pan/mode changes directly without controller/interactor.
 * Uses generic ViewModel instead of custom ExploreMapViewModel.
 */
public class ExploreMapView extends AbstractView {
    // Zoom configuration
    private static final double MIN_ZOOM_SCALE = 0.1;
    private static final double MAX_ZOOM_SCALE = 50.0;
    private static final int MAX_ZOOM_IN_LEVELS = 5;

    // Cursor drawing sizes
    private static final int CURSOR_SIZE = 32;
    private static final int CURSOR_CIRCLE_SIZE = 16;
    private static final int CURSOR_CIRCLE_OFFSET = 4;
    private static final int CURSOR_LINE_START = 18;
    private static final int CURSOR_LINE_END = 28;
    private static final int CURSOR_CROSS_CENTER = 12;
    private static final int CURSOR_CROSS_MIN = 8;
    private static final int CURSOR_CROSS_MAX = 16;

    // Stroke widths
    private static final float STROKE_WIDTH_THIN = 1.0f;
    private static final float STROKE_WIDTH_MEDIUM = 1.5f;
    private static final float STROKE_WIDTH_THICK = 2.0f;
    private static final float STROKE_WIDTH_EXTRA_THICK = 3.0f;
    private static final double FALLBACK_STROKE_WIDTH = 0.5;

    // Interaction timing and zoom factors
    private static final int HOVER_DELAY_MS = 50;
    private static final double ZOOM_IN_FACTOR = 0.8;
    private static final double ZOOM_OUT_FACTOR = 1.25;
    private static final double ZOOM_WHEEL_IN_FACTOR = 0.8;
    private static final double ZOOM_WHEEL_OUT_FACTOR = 1.2;

    // Color components for fills
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

    // Selection cursor geometry
    private static final int ARROW_BASE_Y = 24;
    private static final int ARROW_TIP_X = 20;

    // Interaction modes
    private static final String MODE_PAN = "PAN";
    private static final String MODE_ZOOM = "ZOOM";
    private static final String MODE_SELECT = "SELECT";

    // Navigator
    NavigationService navigator;

    /**
     * -- GETTER --
     * Gets the view name for view switching.
     *
     * @return the view name
     */
    @Getter
    private final String viewName = "explore_map";

    // View model for this screen
    private final ViewModel<ExploreMapState> exploreMapViewModel;

    /**
     * -- SETTER --
     * Sets the controller for this view.
     *
     * @param newController the controller
     */
    @Setter
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
     *
     * @param viewModel the view model for this view
     */
    public ExploreMapView(final ViewModel<ExploreMapState> viewModel, NavigationService navigator) {
        super(viewModel);
        this.navigator = navigator;
        this.exploreMapViewModel = viewModel;

        // Ensure state exists so we can read/write safely.
        if (this.exploreMapViewModel.getState() == null) {
            this.exploreMapViewModel.updateState(new ExploreMapState());
        }

        initializeFactories();
        createCustomCursors();
        setUpUi();
    }

    @Override
    public void onViewOpened(String param) {
        // Automatically load the default world map shapefile on startup
        if (controller != null && (mapPane == null || mapContent == null)) {
            // Load the default shapefile from the resources folder
            final String defaultShapefilePath = "src/main/resources/shapefiles/ne_110m_admin_0_countries.shp";
            final File shapefileFile = new File(defaultShapefilePath);

            if (shapefileFile.exists()) {
                controller.loadMap(shapefileFile.getAbsolutePath());
            }
        }
    }

    @Override
    public void onViewClosed() {
        // Dispose map content when view is closed.
        if (mapContent != null) {
            mapContent.dispose();
            mapContent = null;
        }
        if (mapPane != null) {
            remove(mapPane);
            mapPane = null;
        }
        featureLayer = null;
        hoverLayer = null;
        selectedLayer = null;
        featureSource = null;
    }

    @Override
    public void onStateChange(final Object oldState, final Object newState) {
        if (newState instanceof ExploreMapState) {
            final ExploreMapState state = (ExploreMapState) newState;

            // Show any error reported by the interactor.
            if (state.getErrorMessage() != null) {
                JOptionPane.showMessageDialog(
                        this,
                        state.getErrorMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            else {
                // Lazily initialize map when data becomes available.
                if (state.isMapLoaded() && mapPane == null) {
                    final SimpleFeatureSource source = state.getFeatureSource();
                    if (source != null) {
                        initializeMap(source);
                    }
                }

                // Refresh selection highlight if needed.
                if (mapPane != null && featureSource != null) {
                    updateSelectedDisplay();
                }
            }
        }
    }

    /**
     * Initializes GeoTools factories used for styles and filters.
     */
    private void initializeFactories() {
        styleFactory = CommonFactoryFinder.getStyleFactory();
        filterFactory = CommonFactoryFinder.getFilterFactory();
    }

    /**
     * Prepares all cursors used by the interaction modes.
     */
    private void createCustomCursors() {
        defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        zoomCursor = createMagnifyingGlassCursor();
        selectCursor = createSelectionCursor();
    }

    /**
     * Builds the magnifying glass cursor used for zoom mode.
     *
     * @return the custom zoom cursor
     */
    private Cursor createMagnifyingGlassCursor() {
        final BufferedImage cursorImage = new BufferedImage(
                CURSOR_SIZE, CURSOR_SIZE, BufferedImage.TYPE_INT_ARGB
        );
        final Graphics2D g2d = cursorImage.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Outer circle and handle
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(STROKE_WIDTH_THICK));
        g2d.drawOval(
                CURSOR_CIRCLE_OFFSET,
                CURSOR_CIRCLE_OFFSET,
                CURSOR_CIRCLE_SIZE,
                CURSOR_CIRCLE_SIZE
        );
        g2d.drawLine(
                CURSOR_LINE_START,
                CURSOR_LINE_START,
                CURSOR_LINE_END,
                CURSOR_LINE_END
        );

        // Green cross inside lens
        g2d.setStroke(new BasicStroke(STROKE_WIDTH_MEDIUM));
        g2d.setColor(new Color(0, GREEN_COMPONENT, 0));
        g2d.drawLine(
                CURSOR_CROSS_CENTER,
                CURSOR_CROSS_MIN,
                CURSOR_CROSS_CENTER,
                CURSOR_CROSS_MAX
        );
        g2d.drawLine(
                CURSOR_CROSS_MIN,
                CURSOR_CROSS_CENTER,
                CURSOR_CROSS_MAX,
                CURSOR_CROSS_CENTER
        );

        g2d.dispose();

        final Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImage,
                new java.awt.Point(CURSOR_CROSS_CENTER, CURSOR_CROSS_CENTER),
                "ZoomCursor"
        );
        return cursor;
    }

    /**
     * Builds the arrow cursor used for selection mode.
     *
     * @return the custom selection cursor
     */
    private Cursor createSelectionCursor() {
        final BufferedImage cursorImage = new BufferedImage(
                CURSOR_SIZE, CURSOR_SIZE, BufferedImage.TYPE_INT_ARGB
        );
        final Graphics2D g2d = cursorImage.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(STROKE_WIDTH_THICK));

        // Simple polygon approximating an arrow pointer.
        final int[] xPoints = new int[7];
        xPoints[0] = CURSOR_CROSS_MIN;
        xPoints[1] = CURSOR_CROSS_MIN;
        xPoints[2] = CURSOR_CROSS_CENTER;
        xPoints[3] = CURSOR_CROSS_MAX;
        xPoints[4] = ARROW_TIP_X;
        xPoints[5] = CURSOR_CROSS_MAX;
        xPoints[6] = CURSOR_CROSS_CENTER;

        final int[] yPoints = new int[7];
        yPoints[0] = ARROW_BASE_Y;
        yPoints[1] = CURSOR_CROSS_MIN;
        yPoints[2] = CURSOR_CROSS_MIN;
        yPoints[3] = CURSOR_CROSS_CENTER;
        yPoints[4] = CURSOR_CROSS_MIN;
        yPoints[5] = CURSOR_CROSS_MAX;
        yPoints[6] = CURSOR_CROSS_MAX;

        g2d.fillPolygon(xPoints, yPoints, xPoints.length);

        // White outline for visibility.
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(STROKE_WIDTH_THIN));
        g2d.drawPolygon(xPoints, yPoints, xPoints.length);

        g2d.dispose();

        final Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImage,
                new java.awt.Point(CURSOR_CROSS_MIN, CURSOR_CROSS_MIN),
                "SelectCursor"
        );
        return cursor;
    }

    /**
     * Builds top-level layout and attaches the toolbar.
     */
    private void setUpUi() {
        setLayout(new BorderLayout());
        final JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
    }

    /**
     * Creates the toolbar with core map controls.
     *
     * @return configured toolbar panel
     */
    private JPanel createControlPanel() {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        final JButton loadButton = new JButton("Load World Map");
        loadButton.addActionListener(event -> loadShapefile());

        final JButton resetButton = new JButton("Reset View");
        resetButton.addActionListener(event -> handleResetView());

        panButton = new JButton("Pan Mode");
        panButton.addActionListener(event -> setInteractionMode(MODE_PAN));

        zoomButton = new JButton("Zoom Mode");
        zoomButton.addActionListener(event -> setInteractionMode(MODE_ZOOM));

        selectButton = new JButton("Select Mode");
        selectButton.addActionListener(event -> setInteractionMode(MODE_SELECT));

        panel.add(loadButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(resetButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(panButton);
        panel.add(zoomButton);
        panel.add(selectButton);

        return panel;
    }

    /**
     * Prompts the user for a shapefile and triggers map loading.
     */
    private void loadShapefile() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select World Map Shapefile");
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                boolean result = false;
                if (file.isDirectory()) {
                    result = true;
                }
                else {
                    if (file.getName().toLowerCase().endsWith(".shp")) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public String getDescription() {
                return "Shapefiles (*.shp)";
            }
        });

        final int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION && controller != null) {
            final File file = fileChooser.getSelectedFile();
            controller.loadMap(file.getAbsolutePath());
        }
    }


    /**
     * Resets the view to show the full map extent.
     */
    private void handleResetView() {
        if (mapPane != null && mapContent != null) {
            final ReferencedEnvelope bounds = mapContent.getMaxBounds();
            if (bounds != null) {
                currentZoomLevel = 0;
                SwingUtilities.invokeLater(
                        () -> mapPane.setDisplayArea(bounds)
                );
            }
        }
    }

    /**
     * Updates interaction mode and related UI for pan/zoom/select.
     *
     * @param mode new interaction mode
     */
    private void setInteractionMode(final String mode) {
        final ExploreMapState state = exploreMapViewModel.getState();
        state.setInteractionMode(mode);
        exploreMapViewModel.updateState(state);

        // Clear hover on modes that do not need it.
        if (!MODE_PAN.equals(mode) && !MODE_SELECT.equals(mode)) {
            hoveredFeature = null;
            updateHoverDisplay();
        }

        // Clear selection when leaving select mode.
        if (!MODE_SELECT.equals(mode)) {
            state.setSelectedFeature(null);
            state.setSelectedCountryName(null);
            exploreMapViewModel.updateState(state);
            updateSelectedDisplay();
        }

        updateCursor();
        updateModeButtons();
    }

    /**
     * Builds the map content and hooks listeners once data is ready.
     *
     * @param source feature source for the world layer
     */
    private void initializeMap(final SimpleFeatureSource source) {
        featureSource = source;
        defaultStyle = createDefaultStyle();

        if (mapContent != null) {
            mapContent.dispose();
        }

        mapContent = new MapContent();
        mapContent.setTitle("World Map");

        featureLayer = new FeatureLayer(featureSource, defaultStyle);
        mapContent.addLayer(featureLayer);

        // Replace any existing map pane.
        if (mapPane != null) {
            remove(mapPane);
        }

        mapPane = new JMapPane(mapContent);
        mapPane.setBackground(Color.WHITE);
        mapPane.setDoubleBuffered(true);

        configureRenderer();

        mapPane.addMouseListener(new ExploreMapMouseListener());
        mapPane.addComponentListener(new MapResizeListener());

        add(mapPane, BorderLayout.CENTER);
        updateCursor();
        updateModeButtons();

        SwingUtilities.invokeLater(this::resetDisplayToMaxBoundsAndRefresh);
    }

    /**
     * Configures renderer hints to render on a single thread.
     */
    private void configureRenderer() {
        final GTRenderer renderer = mapPane.getRenderer();
        if (renderer instanceof StreamingRenderer) {
            final StreamingRenderer streamingRenderer = (StreamingRenderer) renderer;
            final Map<Object, Object> hints = new HashMap<>();
            hints.put("renderingThreads", Integer.valueOf(1));
            streamingRenderer.setRendererHints(hints);
        }
    }

    /**
     * Fits the map to the full extent and repaints.
     */
    private void resetDisplayToMaxBoundsAndRefresh() {
        validate();
        repaint();

        if (mapPane != null && mapContent != null) {
            final ReferencedEnvelope bounds = mapContent.getMaxBounds();
            if (bounds != null) {
                mapPane.setDisplayArea(bounds);
            }
        }
    }

    /**
     * Handles hover movement over the map when not in zoom mode.
     *
     * @param event mouse move event
     */
    private void handleMouseMove(final MapMouseEvent event) {
        final ExploreMapState state = exploreMapViewModel.getState();
        final String mode = state.getInteractionMode();

        if (!MODE_ZOOM.equals(mode) && !isUpdatingStyle) {
            final org.geotools.geometry.Position2D worldPos = event.getWorldPos();
            final Coordinate coord = new Coordinate(worldPos.x, worldPos.y);
            final SimpleFeature feature = getFeatureAtPosition(coord);

            if (feature != hoveredFeature) {
                hoveredFeature = feature;

                if (hoverUpdateTimer != null && hoverUpdateTimer.isRunning()) {
                    hoverUpdateTimer.stop();
                }

                // Slight delay to avoid excessive hover updates.
                hoverUpdateTimer = new javax.swing.Timer(
                        HOVER_DELAY_MS,
                        timerEvent -> {
                            SwingUtilities.invokeLater(
                                    this::updateHoverDisplay
                            );
                        }
                );
                hoverUpdateTimer.setRepeats(false);
                hoverUpdateTimer.start();
            }
        }
    }

    /**
     * Handles clicks for zoom and selection interactions.
     * In zoom mode: left-click to zoom in, right-click to zoom out.
     *
     * @param event mouse click event
     */
    private void handleMouseClick(final MapMouseEvent event) {
        final ExploreMapState state = exploreMapViewModel.getState();
        final String mode = state.getInteractionMode();
        final org.geotools.geometry.Position2D worldPos = event.getWorldPos();

        if (MODE_ZOOM.equals(mode)) {
            // Check if it's a right-click (button 3)
            if (event.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                // Right-click: zoom out
                handleZoomOutAtPoint(worldPos.x, worldPos.y);
            } else {
                // Left-click: zoom in
                handleZoomAtPoint(worldPos.x, worldPos.y);
            }
        }
        else {
            if (MODE_SELECT.equals(mode) && controller != null) {
                controller.selectFeature(worldPos.x, worldPos.y);
                // Update the display to show selection highlight
                updateSelectedDisplay();
                // Navigate to country details if a country was selected
                final String selectedCountryName = exploreMapViewModel.getState().getSelectedCountryName();
                if (selectedCountryName != null) {
                    // Small delay to show the selection before navigating
                    javax.swing.Timer timer = new javax.swing.Timer(300, e -> {
                        navigator.navigateTo("country_details", selectedCountryName);
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }

    /**
     * Zooms in around a specific world coordinate.
     *
     * @param xCoord x coordinate in world space
     * @param yCoord y coordinate in world space
     */
    private void handleZoomAtPoint(final double xCoord, final double yCoord) {
        if (mapPane != null && currentZoomLevel < MAX_ZOOM_IN_LEVELS) {
            final ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
            if (currentBounds != null) {
                final double width = currentBounds.getWidth() * ZOOM_IN_FACTOR;
                final double height = currentBounds.getHeight() * ZOOM_IN_FACTOR;

                final ReferencedEnvelope newBounds = new ReferencedEnvelope(
                        xCoord - width / 2, xCoord + width / 2,
                        yCoord - height / 2, yCoord + height / 2,
                        currentBounds.getCoordinateReferenceSystem()
                );

                currentZoomLevel++;
                SwingUtilities.invokeLater(
                        () -> mapPane.setDisplayArea(newBounds)
                );
            }
        }
    }

    /**
     * Zooms out around a specific world coordinate.
     *
     * @param xCoord x coordinate in world space
     * @param yCoord y coordinate in world space
     */
    private void handleZoomOutAtPoint(final double xCoord, final double yCoord) {
        if (mapPane != null && currentZoomLevel > 0) {
            final ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
            if (currentBounds != null) {
                final double width = currentBounds.getWidth() * ZOOM_OUT_FACTOR;
                final double height = currentBounds.getHeight() * ZOOM_OUT_FACTOR;

                final ReferencedEnvelope newBounds = new ReferencedEnvelope(
                        xCoord - width / 2, xCoord + width / 2,
                        yCoord - height / 2, yCoord + height / 2,
                        currentBounds.getCoordinateReferenceSystem()
                );

                currentZoomLevel--;
                SwingUtilities.invokeLater(
                        () -> mapPane.setDisplayArea(newBounds)
                );
            }
        }
    }

    /**
     * Handles mouse wheel zoom while enforcing zoom limits.
     *
     * @param event wheel event
     */
    private void handleMouseWheel(final MapMouseEvent event) {
        if (mapPane != null) {
            final ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
            if (currentBounds != null) {
                final double width = currentBounds.getWidth();
                final double height = currentBounds.getHeight();

                final double newWidth;
                final double newHeight;
                final double zoomFactor;

                if (event.getWheelAmount() > 0) {
                    zoomFactor = ZOOM_WHEEL_OUT_FACTOR;
                }
                else {
                    zoomFactor = ZOOM_WHEEL_IN_FACTOR;
                }

                newWidth = width * zoomFactor;
                newHeight = height * zoomFactor;

                final ReferencedEnvelope fullBounds = mapContent.getMaxBounds();
                boolean withinLimits = true;
                if (fullBounds != null) {
                    final double fullWidth = fullBounds.getWidth();
                    if (newWidth < fullWidth / MAX_ZOOM_SCALE
                            || newWidth > fullWidth / MIN_ZOOM_SCALE) {
                        withinLimits = false;
                    }
                }

                if (withinLimits) {
                    final org.geotools.geometry.Position2D mousePos =
                            event.getWorldPos();
                    final double centerX = mousePos.x;
                    final double centerY = mousePos.y;

                    final ReferencedEnvelope newBounds = new ReferencedEnvelope(
                            centerX - newWidth / 2, centerX + newWidth / 2,
                            centerY - newHeight / 2, centerY + newHeight / 2,
                            currentBounds.getCoordinateReferenceSystem()
                    );

                    SwingUtilities.invokeLater(
                            () -> mapPane.setDisplayArea(newBounds)
                    );
                }
            }
        }
    }

    /**
     * Returns the feature at the given world position if one exists.
     *
     * @param worldPos world-space coordinate
     * @return feature under the cursor, or null
     */
    private SimpleFeature getFeatureAtPosition(final Coordinate worldPos) {
        SimpleFeature result = null;

        if (featureSource != null) {
            try (SimpleFeatureIterator iterator =
                         featureSource.getFeatures().features()) {
                while (iterator.hasNext() && result == null) {
                    final SimpleFeature feature = iterator.next();
                    final Geometry geometry =
                            (Geometry) feature.getDefaultGeometry();

                    if (geometry != null) {
                        final Point point =
                                geometry.getFactory().createPoint(worldPos);
                        if (geometry.contains(point)) {
                            result = feature;
                        }
                    }
                }
            }
            catch (IOException ex) {
                System.err.println(
                        "Error getting feature at position: "
                                + ex.getMessage()
                );
            }
        }

        return result;
    }

    /**
     * Updates the hover overlay to reflect the current hovered feature.
     */
    private void updateHoverDisplay() {
        if (mapContent != null && featureLayer != null && !isUpdatingStyle) {
            isUpdatingStyle = true;

            if (hoverLayer != null) {
                mapContent.removeLayer(hoverLayer);
                hoverLayer = null;
            }

            if (hoveredFeature != null) {
                final org.geotools.api.feature.simple.SimpleFeatureType featureType =
                        featureSource.getSchema();
                final SimpleFeatureBuilder featureBuilder =
                        new SimpleFeatureBuilder(featureType);
                featureBuilder.init(hoveredFeature);
                final SimpleFeature hoverFeature =
                        featureBuilder.buildFeature(null);

                final MemoryFeatureCollection collection =
                        new MemoryFeatureCollection(featureType);
                collection.add(hoverFeature);

                hoverLayer =
                        new FeatureLayer(collection, createHoverStyle());
                mapContent.addLayer(hoverLayer);
            }

            if (mapPane != null) {
                mapPane.repaint();
            }

            isUpdatingStyle = false;
        }
    }

    /**
     * Applies the current selection to a dedicated highlight layer.
     */
    private void applySelectedDisplayUpdates() {
        if (selectedLayer != null) {
            mapContent.removeLayer(selectedLayer);
            selectedLayer = null;
        }

        final ExploreMapState state = exploreMapViewModel.getState();
        final SimpleFeature selectedFeature = state.getSelectedFeature();

        if (selectedFeature != null) {
            final org.geotools.api.feature.simple.SimpleFeatureType featureType =
                    featureSource.getSchema();
            final SimpleFeatureBuilder featureBuilder =
                    new SimpleFeatureBuilder(featureType);
            featureBuilder.init(selectedFeature);
            final SimpleFeature selectFeature =
                    featureBuilder.buildFeature(null);

            final MemoryFeatureCollection collection =
                    new MemoryFeatureCollection(featureType);
            collection.add(selectFeature);

            selectedLayer = new FeatureLayer(
                    collection,
                    createSelectedStyle()
            );
            mapContent.addLayer(selectedLayer);

            if (state.getSelectedCountryName() != null) {
                System.out.println(
                        "Selected country: " + state.getSelectedCountryName()
                );
            }
        }

        if (mapPane != null) {
            mapPane.repaint();
        }
    }

    /**
     * Schedules selection highlight updates on the EDT.
     */
    private void updateSelectedDisplay() {
        if (mapContent != null && featureLayer != null) {
            SwingUtilities.invokeLater(this::applySelectedDisplayUpdates);
        }
    }

    /**
     * Creates the default map style used for the base world layer.
     *
     * @return style for the main feature layer
     */
    private Style createDefaultStyle() {
        Style style = createBasicFallbackStyle();

        if (styleFactory != null && filterFactory != null) {
            final org.geotools.api.style.Stroke stroke =
                    styleFactory.createStroke(
                            filterFactory.literal(Color.BLACK),
                            filterFactory.literal(STROKE_WIDTH_THIN)
                    );

            final Fill fill = styleFactory.createFill(
                    filterFactory.literal(
                            new Color(
                                    LIGHT_BLUE_R,
                                    LIGHT_BLUE_G,
                                    LIGHT_BLUE_B,
                                    Color.OPAQUE
                            )
                    )
            );

            final PolygonSymbolizer symbolizer =
                    styleFactory.createPolygonSymbolizer(stroke, fill, null);
            final Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            final FeatureTypeStyle fts =
                    styleFactory.createFeatureTypeStyle(rule);
            style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);
        }

        return style;
    }

    /**
     * Creates a very simple fallback style used if style setup fails.
     *
     * @return basic line-only style
     */
    private Style createBasicFallbackStyle() {
        final org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.GRAY),
                filterFactory.literal(FALLBACK_STROKE_WIDTH)
        );

        final LineSymbolizer lineSymbolizer =
                styleFactory.createLineSymbolizer(stroke, null);
        final Rule rule = styleFactory.createRule();
        rule.symbolizers().add(lineSymbolizer);

        final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        final Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * Creates the style used for hover feedback.
     *
     * @return style for hovered feature layer
     */
    private Style createHoverStyle() {
        Style style = createBasicFallbackStyle();

        if (styleFactory != null && filterFactory != null) {
            final org.geotools.api.style.Stroke redStroke =
                    styleFactory.createStroke(
                            filterFactory.literal(Color.RED),
                            filterFactory.literal(STROKE_WIDTH_EXTRA_THICK)
                    );

            final Fill fill = styleFactory.createFill(
                    filterFactory.literal(
                            new Color(
                                    HOVER_RED,
                                    HOVER_GREEN,
                                    HOVER_BLUE,
                                    HOVER_ALPHA
                            )
                    )
            );

            final PolygonSymbolizer symbolizer =
                    styleFactory.createPolygonSymbolizer(redStroke, fill, null);
            final Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            final FeatureTypeStyle fts =
                    styleFactory.createFeatureTypeStyle(rule);
            style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);
        }

        return style;
    }

    /**
     * Creates the style used for the selected feature overlay.
     *
     * @return style for selected feature layer
     */
    private Style createSelectedStyle() {
        Style style = createBasicFallbackStyle();

        if (styleFactory != null && filterFactory != null) {
            final Fill blueFill = styleFactory.createFill(
                    filterFactory.literal(
                            new Color(
                                    SELECTED_RED,
                                    SELECTED_GREEN,
                                    SELECTED_BLUE,
                                    SELECTED_ALPHA
                            )
                    )
            );

            final org.geotools.api.style.Stroke blackStroke =
                    styleFactory.createStroke(
                            filterFactory.literal(Color.BLACK),
                            filterFactory.literal(STROKE_WIDTH_EXTRA_THICK)
                    );

            final PolygonSymbolizer symbolizer =
                    styleFactory.createPolygonSymbolizer(
                            blackStroke,
                            blueFill,
                            null
                    );
            final Rule rule = styleFactory.createRule();
            rule.symbolizers().add(symbolizer);

            final FeatureTypeStyle fts =
                    styleFactory.createFeatureTypeStyle(rule);
            style = styleFactory.createStyle();
            style.featureTypeStyles().add(fts);
        }

        return style;
    }

    /**
     * Sets the cursor based on the current interaction mode.
     */
    private void updateCursor() {
        if (mapPane != null) {
            final ExploreMapState state = exploreMapViewModel.getState();
            final String mode = state.getInteractionMode();

            Cursor cursorToUse = defaultCursor;
            if (MODE_ZOOM.equals(mode)) {
                cursorToUse = zoomCursor;
            }
            else {
                if (MODE_SELECT.equals(mode)) {
                    cursorToUse = selectCursor;
                }
            }

            mapPane.setCursor(cursorToUse);
        }
    }

    /**
     * Enables and highlights the correct mode buttons.
     */
    private void updateModeButtons() {
        if (panButton != null && zoomButton != null && selectButton != null) {
            final ExploreMapState state = exploreMapViewModel.getState();
            final String mode = state.getInteractionMode();

            panButton.setEnabled(!MODE_PAN.equals(mode));
            zoomButton.setEnabled(!MODE_ZOOM.equals(mode));
            selectButton.setEnabled(!MODE_SELECT.equals(mode));

            if (MODE_PAN.equals(mode)) {
                panButton.setBackground(Color.LIGHT_GRAY);
            }
            else {
                panButton.setBackground(null);
            }

            if (MODE_ZOOM.equals(mode)) {
                zoomButton.setBackground(Color.LIGHT_GRAY);
            }
            else {
                zoomButton.setBackground(null);
            }

            if (MODE_SELECT.equals(mode)) {
                selectButton.setBackground(Color.LIGHT_GRAY);
            }
            else {
                selectButton.setBackground(null);
            }
        }
    }

    /**
     * Listener for map mouse events to keep anonymous inner length small.
     */
    private final class ExploreMapMouseListener implements MapMouseListener {
        @Override
        public void onMouseClicked(final MapMouseEvent ev) {
            handleMouseClick(ev);
        }

        @Override
        public void onMouseDragged(final MapMouseEvent ev) {
            // Drag behavior is handled by JMapPane itself.
        }

        @Override
        public void onMouseEntered(final MapMouseEvent ev) {
            // No special behavior on enter.
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
            // No special behavior on press.
        }

        @Override
        public void onMouseReleased(final MapMouseEvent ev) {
            // No special behavior on release.
        }

        @Override
        public void onMouseWheelMoved(final MapMouseEvent ev) {
            handleMouseWheel(ev);
        }
    }

    /**
     * Component listener for resizing the map pane.
     */
    private final class MapResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(final ComponentEvent event) {
            if (mapPane != null && mapContent != null) {
                final ReferencedEnvelope bounds = mapContent.getMaxBounds();
                if (bounds != null) {
                    mapPane.setDisplayArea(bounds);
                }
            }
        }
    }
}
