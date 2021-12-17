package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTabPane;
import eu.hansolo.fx.monitor.Monitor;
import eu.hansolo.fx.monitor.MonitorBuilder;
import eu.hansolo.fx.monitor.tools.Theme;
import eu.hansolo.fx.monitor.tools.Timespan;


import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.event.MouseEvent;


public class MainApp extends Application {

    //private GridPane grid;
    private Monitor monitorPres, monitorFlow;
    private int peep = 5;
    private int volume = 700;
    private int frequency = 12;
    Breathing breath;
    double kTime = 1.5;
    private Gauge gVolume, gPEEP, gFrequency;

    public MainApp() {
        // 6 minutes data per frame

    }

    public void createMonitorPr1es(){

    }

    public Scene createContent(){
        gVolume = GaugeBuilder.create()
                .skinType(Gauge.SkinType.BAR)
                .prefSize(150, 150)
                .foregroundBaseColor(Color.web("#ebeefd"))
                .barBackgroundColor(Color.web("#262c49"))
                .decimals(0)
                .maxValue(1400)
                .value(volume)
                .title("VOLUME")
                .gradientBarEnabled(true)
                .animated(true)
                .build();

        gVolume.setOnMousePressed(event -> {
            int newValue = getAngle(gVolume, event.getSceneX(), event.getSceneY());
            gVolume.setValue(newValue);
            volume = (newValue);
        });

        gPEEP = GaugeBuilder.create()
                .skinType(Gauge.SkinType.BAR)
                .prefSize(150, 150)
                .foregroundBaseColor(Color.web("#ebeefd"))
                .barBackgroundColor(Color.web("#262c49"))
                .decimals(0)
                .maxValue(10)
                .value(peep)
                .title("PEEP")
                .gradientBarEnabled(true)
                .animated(true)
                .build();

        gPEEP.setOnMousePressed(event -> {
            int newValue = getAngle(gPEEP, event.getSceneX(), event.getSceneY());
            gPEEP.setValue(newValue);
            peep = (newValue);
        });

        gFrequency = GaugeBuilder.create()
                .skinType(Gauge.SkinType.BAR)
                .prefSize(150, 150)
                .foregroundBaseColor(Color.web("#ebeefd"))
                .barBackgroundColor(Color.web("#262c49"))
                .decimals(0)
                .maxValue(20)
                .value(frequency)
                .title("FREQUENCY")
                .gradientBarEnabled(true)
                .animated(true)
                .build();

        gFrequency.setOnMousePressed(event -> {
            int newValue = getAngle(gFrequency, event.getSceneX(), event.getSceneY());
            gFrequency.setValue(newValue);
            frequency = (newValue);
        });

        breath = new Breathing(peep,kTime);

        monitorPres = MonitorBuilder.create()
                .lineWidth(2)
                .dotSize(4)
                .rasterVisible(true)
                .textVisible(true)
                .glowVisible(true)
                .crystalOverlayVisible(true)
                .lineFading(false)
                .timespan(Timespan.TEN_SECONDS)
                .colorTheme(Theme.GREEN)
                .speedFactor(1)
                .noOfSegments(150)
                .data(breath.getPresData())
                .build();

        monitorFlow = MonitorBuilder.create()
                .lineWidth(2)
                .dotSize(4)
                .rasterVisible(true)
                .textVisible(true)
                .glowVisible(true)
                .crystalOverlayVisible(true)
                .lineFading(false)
                .timespan(Timespan.TEN_SECONDS)
                .colorTheme(Theme.GREEN)
                .speedFactor(1)
                .noOfSegments(150)
                .data(breath.getFlowData())
                .build();

        JFXTabPane tabPane = new JFXTabPane();
        tabPane.setBackground(new Background(new BackgroundFill(Color.web("#10163a"), CornerRadii.EMPTY, Insets.EMPTY)));

        //tab main
        Tab tabMain = new Tab();
        tabMain.setText("Главная");
        tabMain.setContent(new Rectangle(200,200, Color.LIGHTSTEELBLUE));
        tabPane.getTabs().add(tabMain);

        //tab ivl simulator
        Tab tabGeneratorIVL = new Tab();
        tabGeneratorIVL.setText("ИВЛ Симулятор");
        tabGeneratorIVL.setContent(new Rectangle(200,200, Color.LIGHTSTEELBLUE));

        //кнопки управления
        GridPane controlGrid;
        controlGrid = new GridPane();
        controlGrid.setVgap(10);
        controlGrid.setHgap(10);
        Button butStart = new Button();
        Button butStop = new Button();
        butStart.setText("Start");
        butStop.setText("Stop");
        butStart.setOnMouseClicked(event -> {
            monitorPres.start();
            monitorFlow.start();
        });
        butStop.setOnMouseClicked(event -> {
            monitorPres.stop();
            monitorFlow.stop();
        });
        controlGrid.add(butStart,0,0);
        controlGrid.add(butStop,1,0);
        HBox controlComponentCompact = new HBox(10,controlGrid);

        //Собираем всё вместе
        GridPane gridSim = new GridPane();
        gridSim.setHgap(20);
        gridSim.setVgap(10);

        gridSim.add(gVolume,0,0);
        gridSim.add(gPEEP,0,1);
        gridSim.add(gFrequency,0,2);
        gridSim.add(monitorPres,1,0);
        gridSim.add(monitorFlow,1,1);
        gridSim.add(controlComponentCompact,1,2);

        gridSim.setPadding(new Insets(10));
        tabGeneratorIVL.setContent(gridSim);

        tabPane.getTabs().add(tabGeneratorIVL);

        Scene scene = new Scene(tabPane);
        return scene;
    }

    public int getAngle(Gauge gauge, double xMouse, double yMouse){
        int angleInt;
        Bounds bounds = gauge.getLayoutBounds();
        Point2D coordinates = gauge.localToScene(bounds.getMinX(), bounds.getMinY());
        int X = (int) coordinates.getX();
        int Y = (int) coordinates.getY();
        int width = (int) gauge.getWidth();
        int height = (int) gauge.getHeight();
        double x0 = X + (double)width/2;
        double y0 = Y +(double)height/2;
        double x = xMouse - x0;
        double y = y0 - yMouse;
        double theta = Math.atan2(y, x) + 1.5708;
        int angle;
        if((180 -  Math.toDegrees(theta))<0){
            angle = (int)(360 + 180 - Math.toDegrees(theta));
        }else{
            angle = (int)(180 - Math.toDegrees(theta));
        }
        angleInt = (int)gauge.getMaxValue()*angle/360;
        return angleInt;
    }



    @Override
    public void stop() {
        //animation.pause();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setScene(createContent());
        primaryStage.show();
//        play();

//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("bitService Test Tools v1.0");
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
