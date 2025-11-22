package view;

import adapters.ExploreMap.ExploreMapController;
import adapters.ExploreMap.ExploreMapState;
import adapters.ExploreMap.ExploreMapViewModel;
import data_access.ExploreMapDataAccessObject;
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

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * View for the Explore Map use case following Clean Architecture.
 */
public class ExploreMapView extends JPanel implements PropertyChangeListener {
    private static final double MIN_ZOOM_SCALE = 0.1;
    private static final double MAX_ZOOM_SCALE = 50.0;

    private final ExploreMapViewModel viewModel;
    private ExploreMapController controller;
    private ExploreMapDataAccessObject dataAccessObject;

    private JMapPane mapPane;
    private MapContent mapContent;
    private FeatureLayer featureLayer;
    private FeatureLayer hoverLayer;
    private FeatureLayer selectedLayer;
    private SimpleFeatureSource featureSource;
    private StyleFactory styleFactory;
    private FilterFactory filterFactory;
    private Style defaultStyle;

    private JButton panButton, zoomButton, selectButton;
    private Cursor defaultCursor, zoomCursor, selectCursor;

    // Enhanced synchronization for Graphics2D thread safety
    private volatile boolean isUpdatingStyle = false;
    private volatile boolean isUpdatingZoom = false;
    private volatile boolean isUpdatingSelection = false;
    private javax.swing.Timer hoverUpdateTimer;
    private javax.swing.Timer zoomUpdateTimer;
    private javax.swing.Timer selectionUpdateTimer;

    // Track last hover to reduce unnecessary updates
    private SimpleFeature lastHoveredFeature;
    private SimpleFeature lastSelectedFeature;

    public ExploreMapView(ExploreMapViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        initializeFactories();
        createCustomCursors();
        setupUI();
    }

    public void setController(ExploreMapController controller) {
        this.controller = controller;
    }

    public void setDataAccessObject(ExploreMapDataAccessObject dataAccessObject) {
        this.dataAccessObject = dataAccessObject;
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
                cursorImage, new Point(12, 12), "ZoomCursor");
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
                cursorImage, new Point(8, 8), "SelectCursor");
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

        JButton loadButton = new JButton(ExploreMapViewModel.LOAD_MAP_BUTTON_LABEL);
        loadButton.addActionListener(e -> loadShapefile());

        JButton zoomInButton = new JButton(ExploreMapViewModel.ZOOM_IN_BUTTON_LABEL);
        zoomInButton.addActionListener(e -> {
            if (controller != null && mapPane != null) {
                ReferencedEnvelope bounds = mapPane.getDisplayArea();
                if (bounds != null) {
                    controller.zoomIn(bounds.getCenterX(), bounds.getCenterY());
                }
            }
        });

        JButton zoomOutButton = new JButton(ExploreMapViewModel.ZOOM_OUT_BUTTON_LABEL);
        zoomOutButton.addActionListener(e -> {
            if (controller != null) {
                controller.zoomOut();
            }
        });

        JButton resetButton = new JButton(ExploreMapViewModel.RESET_BUTTON_LABEL);
        resetButton.addActionListener(e -> {
            if (controller != null) {
                controller.resetView();
            }
        });

        panButton = new JButton(ExploreMapViewModel.PAN_MODE_BUTTON_LABEL);
        panButton.addActionListener(e -> {
            if (controller != null) {
                controller.changeMode("PAN");
            }
        });

        zoomButton = new JButton(ExploreMapViewModel.ZOOM_MODE_BUTTON_LABEL);
        zoomButton.addActionListener(e -> {
            if (controller != null) {
                controller.changeMode("ZOOM");
            }
        });

        selectButton = new JButton(ExploreMapViewModel.SELECT_MODE_BUTTON_LABEL);
        selectButton.addActionListener(e -> {
            if (controller != null) {
                controller.changeMode("SELECT");
            }
        });

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

        // CRITICAL: Configure renderer for single-threaded mode to prevent Graphics2D threading issues
        try {
            org.geotools.renderer.GTRenderer renderer = mapPane.getRenderer();
            if (renderer instanceof org.geotools.renderer.lite.StreamingRenderer) {
                org.geotools.renderer.lite.StreamingRenderer streamingRenderer =
                    (org.geotools.renderer.lite.StreamingRenderer) renderer;

                // Disable multi-threading in the renderer
                java.util.Map<Object, Object> hints = new java.util.HashMap<>();
                hints.put("renderingThreads", 1); // Single thread only
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
                if (controller != null) {
                    controller.hoverFeature(0, 0);
                }
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

    private Style createDefaultStyle() {
        try {
            Stroke stroke = styleFactory.createStroke(
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
            return createBasicFallbackStyle();
        }
    }

    private Style createBasicFallbackStyle() {
        Stroke stroke = styleFactory.createStroke(
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
        ExploreMapState state = viewModel.getState();
        String mode = state.getInteractionMode();

        if ("ZOOM".equals(mode) || isUpdatingStyle) {
            return;
        }

        if (controller != null) {
            org.geotools.geometry.Position2D worldPos = ev.getWorldPos();
            controller.hoverFeature(worldPos.x, worldPos.y);
        }
    }

    private void handleMouseClick(MapMouseEvent ev) {
        ExploreMapState state = viewModel.getState();
        String mode = state.getInteractionMode();
        org.geotools.geometry.Position2D worldPos = ev.getWorldPos();

        if (controller == null) {
            return;
        }

        switch (mode) {
            case "ZOOM":
                controller.zoomIn(worldPos.x, worldPos.y);
                break;
            case "SELECT":
                controller.selectFeature(worldPos.x, worldPos.y);
                break;
            case "PAN":
            default:
                break;
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

    private void updateHoverDisplay() {
        if (mapContent == null || featureLayer == null || isUpdatingStyle) {
            return;
        }

        if (lastHoveredFeature != null && lastHoveredFeature.equals(viewModel.getState().getHoveredFeature())) {
            return; // No change in hovered feature
        }

        isUpdatingStyle = true;
        try {
            if (hoverLayer != null) {
                mapContent.removeLayer(hoverLayer);
                hoverLayer = null;
            }

            ExploreMapState state = viewModel.getState();
            SimpleFeature hoveredFeature = state.getHoveredFeature();

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

            lastHoveredFeature = viewModel.getState().getHoveredFeature();

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
        if (mapContent == null || featureLayer == null || isUpdatingSelection) {
            return;
        }

        ExploreMapState state = viewModel.getState();
        SimpleFeature selectedFeature = state.getSelectedFeature();

        // Check if selection actually changed to reduce unnecessary updates
        if (lastSelectedFeature != null && lastSelectedFeature.equals(selectedFeature)) {
            return; // No change in selected feature
        }

        if (!mapPane.isShowing()) {
            return; // Don't update if map is not visible/ready
        }

        isUpdatingSelection = true;

        try {
            if (selectedLayer != null) {
                mapContent.removeLayer(selectedLayer);
                selectedLayer = null;
            }

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

            lastSelectedFeature = selectedFeature;

            // Use invokeLater to ensure repaint happens on EDT when graphics context is ready
            SwingUtilities.invokeLater(() -> {
                try {
                    if (mapPane != null && mapPane.isShowing()) {
                        mapPane.repaint();
                    }
                } catch (Exception e) {
                    System.err.println("Error repainting after selection: " + e.getMessage());
                } finally {
                    // Delay before allowing next selection update
                    javax.swing.Timer resetTimer = new javax.swing.Timer(100, evt -> isUpdatingSelection = false);
                    resetTimer.setRepeats(false);
                    resetTimer.start();
                }
            });
        } catch (Exception e) {
            System.err.println("Error updating selected feature style: " + e.getMessage());
            isUpdatingSelection = false;
        }
    }

    private Style createHoverStyle() {
        try {
            Stroke redStroke = styleFactory.createStroke(
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

            Stroke blackStroke = styleFactory.createStroke(
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

    private void updateZoomDisplay() {
        if (mapPane == null || mapContent == null || isUpdatingZoom) {
            return;
        }

        ExploreMapState state = viewModel.getState();
        ReferencedEnvelope displayArea = state.getDisplayArea();

        if (displayArea != null) {
            ReferencedEnvelope currentBounds = mapPane.getDisplayArea();
            if (currentBounds == null || !mapPane.isShowing()) {
                return; // Don't update if map is not visible/ready
            }

            isUpdatingZoom = true;

            try {
                int zoomLevel = state.getZoomLevel();
                double zoomFactor = Math.pow(0.8, zoomLevel);

                double width = displayArea.getWidth() * zoomFactor;
                double height = displayArea.getHeight() * zoomFactor;

                double centerX = currentBounds.getCenterX();
                double centerY = currentBounds.getCenterY();

                ReferencedEnvelope newBounds = new ReferencedEnvelope(
                    centerX - width / 2, centerX + width / 2,
                    centerY - height / 2, centerY + height / 2,
                    currentBounds.getCoordinateReferenceSystem()
                );

                // Use invokeLater to ensure this happens on EDT when graphics context is ready
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (mapPane != null && mapPane.isShowing()) {
                            mapPane.setDisplayArea(newBounds);
                        }
                    } catch (Exception e) {
                        System.err.println("Error setting display area: " + e.getMessage());
                    } finally {
                        // Delay before allowing next zoom update
                        javax.swing.Timer resetTimer = new javax.swing.Timer(150, evt -> isUpdatingZoom = false);
                        resetTimer.setRepeats(false);
                        resetTimer.start();
                    }
                });
            } catch (Exception e) {
                System.err.println("Error in updateZoomDisplay: " + e.getMessage());
                isUpdatingZoom = false;
            }
        }
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

        if (state.isMapLoaded() && mapPane == null && dataAccessObject != null) {
            SimpleFeatureSource source = dataAccessObject.getFeatureSource();
            if (source != null) {
                initializeMap(source);
            }
        }

        if (mapPane != null && featureSource != null) {
            // Debounced hover updates
            if (hoverUpdateTimer != null && hoverUpdateTimer.isRunning()) {
                hoverUpdateTimer.stop();
            }
            hoverUpdateTimer = new javax.swing.Timer(50, e ->
                SwingUtilities.invokeLater(this::updateHoverDisplay));
            hoverUpdateTimer.setRepeats(false);
            hoverUpdateTimer.start();

            // Debounced selection updates
            if (selectionUpdateTimer != null && selectionUpdateTimer.isRunning()) {
                selectionUpdateTimer.stop();
            }
            selectionUpdateTimer = new javax.swing.Timer(75, e ->
                SwingUtilities.invokeLater(this::updateSelectedDisplay));
            selectionUpdateTimer.setRepeats(false);
            selectionUpdateTimer.start();
        }

        if (mapPane != null) {
            // Debounced zoom updates
            if (zoomUpdateTimer != null && zoomUpdateTimer.isRunning()) {
                zoomUpdateTimer.stop();
            }
            zoomUpdateTimer = new javax.swing.Timer(100, e ->
                SwingUtilities.invokeLater(this::updateZoomDisplay));
            zoomUpdateTimer.setRepeats(false);
            zoomUpdateTimer.start();
        }

        updateCursor();
        updateModeButtons();
    }
}
