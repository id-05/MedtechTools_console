package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTabPane;
import eu.hansolo.fx.monitor.Monitor;
import eu.hansolo.fx.monitor.MonitorBuilder;
import eu.hansolo.fx.monitor.tools.Theme;
import eu.hansolo.fx.monitor.tools.Timespan;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.swing.*;


public class MainApp extends Application {

    //private GridPane grid;
    private Monitor monitorPres, monitorFlow;
    private int peep = 0;
    private int volume = 700;
    Breathing breath;

    public MainApp() {
        // 6 minutes data per frame

    }

    public void createMonitorPr1es(){

    }

    public Scene createContent(){
        breath = new Breathing(peep);
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

        //регулятор volume
        Label volumeValue = new Label(String.valueOf(volume));
        volumeValue.setTextFill(Color.WHITE);
        Label volumeLabel = new Label("VOLUME");
        volumeLabel.setTextFill(Color.WHITE);
        JFXSlider sliderVolume = new JFXSlider();
        sliderVolume.setValue(volume);
        sliderVolume.setMax(1400);
        sliderVolume.setMin(0);
        sliderVolume.setOnMouseReleased(event -> {
            volume = (int) sliderVolume.getValue();
            volumeValue.setText(String.valueOf(volume));
            breath = new Breathing(peep);
            monitorPres.setData(breath.getPresData());
        });
        GridPane vGrid;
        vGrid = new GridPane();
        vGrid.setHgap(10);
        vGrid.setVgap(10);
        vGrid.add(volumeValue,0,0);
        vGrid.add(sliderVolume,0,1);
        vGrid.add(volumeLabel,0,2);
        VBox volumeComponent = new VBox(10,vGrid);

        //регулятор peep
        Label peepValue = new Label(String.valueOf(peep));
        peepValue.setTextFill(Color.WHITE);
        Label peepLabel = new Label("PEEP");
        peepLabel.setTextFill(Color.WHITE);
        JFXSlider peepVolume = new JFXSlider();
        peepVolume.setValue(peep);
        peepVolume.setMax(10);
        peepVolume.setMin(0);
        peepVolume.setOnMouseReleased(event -> {
            peep = (int) peepVolume.getValue();
            peepValue.setText(String.valueOf(peep));
            breath = new Breathing(peep);
            monitorPres.setData(breath.getPresData());
            monitorFlow.setData(breath.getFlowData());
        });
        GridPane peepGrid;
        peepGrid = new GridPane();
        peepGrid.setHgap(10);
        peepGrid.setVgap(10);
        peepGrid.add(peepValue,0,0);
        peepGrid.add(peepVolume,0,1);
        peepGrid.add(peepLabel,0,2);
        VBox peepComponent = new VBox(10,peepGrid);

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

        gridSim.add(volumeComponent,0,0);
        gridSim.add(peepComponent,0,1);
        gridSim.add(monitorPres,1,0);
        gridSim.add(monitorFlow,1,1);
        gridSim.add(controlComponentCompact,1,2);

        gridSim.setPadding(new Insets(10));
        tabGeneratorIVL.setContent(gridSim);

        tabPane.getTabs().add(tabGeneratorIVL);

        Scene scene = new Scene(tabPane);
        return scene;
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
