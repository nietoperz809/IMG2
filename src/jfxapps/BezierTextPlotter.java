package jfxapps;

// Source - https://stackoverflow.com/a/17374726
// Posted by jewelsea, modified by community. See post 'Timeline' for change history
// Retrieved 2025-12-11, License - CC BY-SA 3.0

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Glow;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static common.ImageTools.imageToClipboard;

/**
 * Example of drawing text along a cubic curve.
 * Drag the anchors around to change the curve.
 */
public class BezierTextPlotter extends Application {
    private static String CURVED_TEXT;

// Source - https://stackoverflow.com/a/61771424
// Posted by sergioFC, modified by community. See post 'Timeline' for change history
// Retrieved 2025-12-12, License - CC BY-SA 4.0

    private static volatile boolean javaFxLaunched = false;

    public static void myLaunch(Class<? extends Application> applicationClass) {
        if (!javaFxLaunched) { // First time
            Platform.setImplicitExit(false);
            new Thread(()->Application.launch(applicationClass)).start();
            javaFxLaunched = true;
        } else { // Next times
            Platform.runLater(()->{
                try {
                    Application application = applicationClass.newInstance();
                    Stage primaryStage = new Stage();
                    application.start(primaryStage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static void runApp(String txt) throws Exception {
        CURVED_TEXT = txt;
        myLaunch (BezierTextPlotter.class);
    }

    public static void main(String[] args) throws Exception {
        CURVED_TEXT = "motherfucker";
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        final CubicCurve curve = createStartingCurve();

        Line controlLine1 = new BoundLine(curve.controlX1Property(), curve.controlY1Property(), curve.startXProperty(), curve.startYProperty());
        Line controlLine2 = new BoundLine(curve.controlX2Property(), curve.controlY2Property(), curve.endXProperty(), curve.endYProperty());

        Anchor start = new Anchor(Color.PALEGREEN, curve.startXProperty(), curve.startYProperty());
        Anchor control1 = new Anchor(Color.GOLD, curve.controlX1Property(), curve.controlY1Property());
        Anchor control2 = new Anchor(Color.GOLDENROD, curve.controlX2Property(), curve.controlY2Property());
        Anchor end = new Anchor(Color.TOMATO, curve.endXProperty(), curve.endYProperty());

        final Text text = new Text(CURVED_TEXT);
        text.setStyle("-fx-font-size: 40px");
        text.setEffect(new Glow());
        final ObservableList<Text> parts = FXCollections.observableArrayList();
        final ObservableList<PathTransition> transitions = FXCollections.observableArrayList();
        for (char character : text.textProperty().get().toCharArray()) {
            Text part = new Text(character + "");
            part.setEffect(text.getEffect());
            part.setStyle(text.getStyle());
            parts.add(part);
            part.setVisible(false);

            transitions.add(createPathTransition(curve, part));
        }

        final ObservableList<Node> controls = FXCollections.observableArrayList();
        controls.setAll(controlLine1, controlLine2, curve, start, control1, control2, end);

        final ToggleButton plot = new ToggleButton("Plot Text");
        plot.setOnAction(new PlotHandler(plot, parts, transitions, controls));

        final Button button = new Button("ImgToClip");
        button.setLayoutX(100);
        button.setOnAction(_ -> {
            BufferedImage img = SwingFXUtils.fromFXImage(stage.getScene().snapshot(null), null);
            imageToClipboard(img);
            //System.out.println(image);
        });

        Group content = new Group(controlLine1, controlLine2, curve, start, control1, control2, end, plot, button);
        content.getChildren().addAll(parts);

        stage.setTitle("Cubic Curve Manipulation Sample");
        stage.setScene(new Scene(content, 400, 400, Color.ALICEBLUE));
        stage.show();
    }

//    public BufferedImage getTransformedImg() {
//        final CountDownLatch latch = new CountDownLatch(1);
//        final AtomicReference<BufferedImage> ret = new AtomicReference<>();
//        Platform.runLater(() -> {
//            SnapshotParameters params = new SnapshotParameters();
//            params.setFill(Color.TRANSPARENT); // no white background
//            WritableImage fxImage = imgV.snapshot(params, null);
//            ret.set(SwingFXUtils.fromFXImage(fxImage, null));
//            latch.countDown();
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return ret.get();
//    }


    /*
    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("jfx app stop");
    }
*/

    private PathTransition createPathTransition(CubicCurve curve, Text text) {
        final PathTransition transition = new PathTransition(Duration.seconds(10), curve, text);

        transition.setAutoReverse(false);
        transition.setCycleCount(PathTransition.INDEFINITE);
        transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        transition.setInterpolator(Interpolator.LINEAR);

        return transition;
    }

    private CubicCurve createStartingCurve() {
        CubicCurve curve = new CubicCurve();
        curve.setStartX(50);
        curve.setStartY(200);
        curve.setControlX1(150);
        curve.setControlY1(300);
        curve.setControlX2(250);
        curve.setControlY2(50);
        curve.setEndX(350);
        curve.setEndY(200);
        curve.setStroke(Color.FORESTGREEN);
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0.6));
        return curve;
    }

    static class BoundLine extends Line {
        BoundLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
            startXProperty().bind(startX);
            startYProperty().bind(startY);
            endXProperty().bind(endX);
            endYProperty().bind(endY);
            setStrokeWidth(2);
            setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
            setStrokeLineCap(StrokeLineCap.BUTT);
            getStrokeDashArray().setAll(10.0, 5.0);
        }
    }

    // a draggable anchor displayed around a point.
    static class Anchor extends Circle {
        Anchor(Color color, DoubleProperty x, DoubleProperty y) {
            super(x.get(), y.get(), 10);
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);

            x.bind(centerXProperty());
            y.bind(centerYProperty());
            enableDrag();
        }

        // make a node movable by dragging it around with the mouse.
        private void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = getCenterX() - mouseEvent.getX();
                    dragDelta.y = getCenterY() - mouseEvent.getY();
                    getScene().setCursor(Cursor.MOVE);
                }
            });
            setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    double newX = mouseEvent.getX() + dragDelta.x;
                    if (newX > 0 && newX < getScene().getWidth()) {
                        setCenterX(newX);
                    }
                    double newY = mouseEvent.getY() + dragDelta.y;
                    if (newY > 0 && newY < getScene().getHeight()) {
                        setCenterY(newY);
                    }
                }
            });
            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.HAND);
                    }
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.DEFAULT);
                    }
                }
            });
        }

        // records relative x and y co-ordinates.
        private static class Delta {
            double x, y;
        }
    }

    // plots text along a path defined by provided bezier control points.
    private static class PlotHandler implements EventHandler<ActionEvent> {
        private final ToggleButton plot;
        private final ObservableList<Text> parts;
        private final ObservableList<PathTransition> transitions;
        private final ObservableList<Node> controls;

        public PlotHandler(ToggleButton plot, ObservableList<Text> parts, ObservableList<PathTransition> transitions, ObservableList<Node> controls) {
            this.plot = plot;
            this.parts = parts;
            this.transitions = transitions;
            this.controls = controls;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            if (plot.isSelected()) {
                for (int i = 0; i < parts.size(); i++) {
                    parts.get(i).setVisible(true);
                    final Transition transition = transitions.get(i);
                    transition.stop();
                    transition.jumpTo(Duration.seconds(10).multiply((i + 0.5) * 1.0 / parts.size()));
                    // just play a single animation frame to display the curved text, then stop
                    AnimationTimer timer = new AnimationTimer() {
                        int frameCounter = 0;

                        @Override
                        public void handle(long l) {
                            frameCounter++;
                            if (frameCounter == 1) {
                                transition.stop();
                                stop();
                            }
                        }
                    };
                    timer.start();
                    transition.play();
                }
                plot.setText("Show Controls");
            } else {
                plot.setText("Plot Text");
            }

            for (Node control : controls) {
                control.setVisible(!plot.isSelected());
            }

            for (Node part : parts) {
                part.setVisible(plot.isSelected());
            }
        }
    }
}
