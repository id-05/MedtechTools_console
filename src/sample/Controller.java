package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import org.usb4java.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.*;

public class Controller implements Initializable {

    Context context;
    SpiroDevice spiroDevice = new SpiroDevice();
    public static Timer timer;
    XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    XYChart.Series<Number, Number> seriesPres = new XYChart.Series<>();
    XYChart.Series<Number, Number> seriesFlow = new XYChart.Series<>();
    int i = 0;
    int amountOfPoints = 120;
    int speedInt = 50;
    int speedGenerate = 10;
    double maxY = 3.3;
    double minY = 0;
    int maxX = 800;
    int pointBreath = 400;
    int minX = 0;
    double maxdYflow = 2045;
    double mindYflow = -2045;
    double maxdYpres = 4095;
    double mindYpres = 0;
    boolean stop = true;
    byte[] bufbyte;
    int starti = 0;
    Breathing breathing;

    @FXML
    public Label peepLabel, freqLabel, ieratioLabel, volumeLabel;

    @FXML
    public LineChart lineChart, lineChartPres, lineChartFlow;

    @FXML
    private NumberAxis xAxis, yAxis, xAxisPres, yAxisPres, xAxisFlow, yAxisFlow;

    @FXML
    private Slider speed, countSlider, timeDt;

    @FXML
    public TextField text1, textquestion, taxtanswer;

    @FXML
    private CheckBox checkBox1;

    @FXML
    public Button startBut, stopBut;

    @FXML
    public Label connectStatus, Vdt, count;

    @FXML
    public TextArea outDemo;

    @FXML Slider peepSlider;

    @FXML
    public void onClickMethod(){
        String os;
        context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb.", result);
        }
        else {
            os =  "USB OK";
        }
        text1.setText(os);
    }

    @FXML
    public void onCheckMethod(){
        byte[] bufbyte = new byte[16];
        String buf;
        if(checkBox1.isSelected()) {
            buf = "cheked";
            for(int i=0; i<16; i++){
                bufbyte[i]='L';
            }
            sendToDevice(bufbyte);
        }
        else{
            buf = "uncheked";
            for(int i=0; i<16; i++){
                bufbyte[i]='D';
            }
            sendToDevice(bufbyte);
        }
        text1.setText(buf);
    }

    public void changeSpeed(){
        timer.cancel();
        speedInt = (int)speed.getValue();
        xAxis.setUpperBound(amountOfPoints*speedInt*0.001);
        series1.getData().clear();
        series2.getData().clear();
        series2.getData().add(new XYChart.Data<>(0, 0));
        i=0;
        startLoopRead();
        Vdt.setText(String.format("%.0f", (1/(speedInt*0.001)))+" Гц");
    }

    public void changeCount(){
        timer.cancel();
        amountOfPoints = (int)countSlider.getValue();
        xAxis.setUpperBound(amountOfPoints*speedInt*0.001);
        speedInt = (int)speed.getValue();
        xAxis.setUpperBound(amountOfPoints*speedInt*0.001);
        series1.getData().clear();
        series2.getData().clear();
        series2.getData().add(new XYChart.Data<>(0, 0));
        i=0;
        startLoopRead();
        Vdt.setText(String.format("%.0f", (1/(speedInt*0.001)))+" Гц");
        count.setText(String.valueOf(amountOfPoints));
    }

    public void checkDevice(){
        if(spiroDevice.isCheckDevice()){
            connectStatus.setText("Подключено");
        }else{
            connectStatus.setText("Не подключено");
        }
    }

    public void sendToDevice(byte[] bufbyte){
        spiroDevice.open();
        spiroDevice.write(bufbyte);
        spiroDevice.close();
    }

    public void getADCResult(){
        if(!stop) {
            //Double buf = SpiroDevice.readADCResult(16);
            spiroDevice.write(bufbyte);
            ByteBuffer buffer = SpiroDevice.readBufResult(16);

            Double buf = ((((buffer.get(0) & 0xff) << 8)) | (buffer.get(1) & 0xff)) * 0.000805;
            Double vref = ((((buffer.get(4) & 0xff) << 8)) | (buffer.get(5) & 0xff)) * 0.000805;
            Double temp = ((((buffer.get(2) & 0xff) << 8)) | (buffer.get(3) & 0xff)) * 0.000805;
            temp = ((1.43 - temp) / 0.0043 + 25);

            connectStatus.setText("vref = " + String.format("%.2f", vref) + "   temp = " + String.format("%.2f", temp));

            if (i == amountOfPoints) {
                i = 0;
            }

            if (series1.getData().size() > amountOfPoints - 1) {

                series1.getData().get(i).setYValue(buf);
                series2.getData().get(0).setYValue(buf);
                series2.getData().get(0).setXValue(i*speedInt*0.001);
            } else {

                series1.getData().add(new XYChart.Data<>(i*speedInt*0.001, buf));
                series2.getData().get(0).setYValue(buf);
                series2.getData().get(0).setXValue(i*speedInt*0.001);
            }
            i++;
        }
    }

    public void startLoopRead(){
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override  public void run() {
                        getADCResult();
                    }
                });
            }
        }, 0, speedInt);
    }

    public void startGenerate(){
        int delta = 1000;
        int kdelta = 100;
        double yPres = 0;
        seriesPres.getData().clear();
        seriesFlow.getData().clear();

        ArrayList<Float> values = new ArrayList<>();

        for(int i=1; i<400; i++){
            double bufi = (double) i / 100;
            double buf = 0;
            if(i<201){
                buf = (pow((bufi),(double)(1/bufi)) * delta);
                if(buf<(peepSlider.getValue() * kdelta)){
                    yPres = peepSlider.getValue() * kdelta;
                }else{
                    yPres = buf;
                }
            }
            if((i>200)&&(i<282)){
                buf = (pow((2),((0.5)))*delta);
                if(buf<peepSlider.getValue() * kdelta){
                    yPres = peepSlider.getValue() * kdelta;
                }else{
                    yPres = buf;
                }
            }
            if(i>281){
                buf = (pow((bufi-4),2)*delta);
                if(buf<peepSlider.getValue() * kdelta){
                    yPres = peepSlider.getValue() * kdelta;
                }else{
                    yPres = buf;
                }
            }
            values.add((float)yPres);
        }

        for(int i=1; i<399; i++){
            seriesPres.getData().add(new XYChart.Data<>(i, (double)values.get(i)));
            float y = values.get(i) - values.get(i+1);
            seriesFlow.getData().add(new XYChart.Data<>(i, -y*50));
        }
    }

    public void startLoopGenerate(){
        timer = new Timer(true);
        breathing = new Breathing(5);
        seriesPres.getData().clear();
        seriesFlow.getData().clear();
        xAxisPres.setLowerBound(0);
        xAxisPres.setUpperBound(maxX);
        xAxisFlow.setLowerBound(0);
        xAxisFlow.setUpperBound(maxX);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override  public void run() {
                        seriesPres.getData().add(new XYChart.Data<>(starti, (double)breathing.getYvalue(starti % pointBreath)));
                        if(starti>maxX){
                            xAxisPres.setLowerBound(starti-maxX);
                            xAxisPres.setUpperBound(starti);
                            xAxisFlow.setLowerBound(starti-maxX);
                            xAxisFlow.setUpperBound(starti);
                        }
                        starti++;
                        if(starti>1){
                            float y = seriesPres.getData().get(starti-2).getYValue().floatValue() - seriesPres.getData().get(starti-1).getYValue().floatValue();
                            seriesFlow.getData().add(new XYChart.Data<>(starti, -y*50));
                        }
                    }
                });
            }
        }, 0, speedGenerate);
    }

    public void stopGenerate(){
        timer.cancel();
        starti = 0;
        seriesPres.getData().clear();
        seriesFlow.getData().clear();
    }

    public void changeDtGenerate(){
        timer.cancel();
        speedGenerate = (int)timeDt.getValue();
        seriesPres.getData().clear();
        seriesFlow.getData().clear();
        startLoopGenerate();
        //Vdt.setText(String.format("%.0f", (1/(speedInt*0.001)))+" Гц");
    }

    public void changePEEP(){
        peepLabel.setText(String.format("%.0f",peepSlider.getValue()));
        breathing.setPEEP((int)peepSlider.getValue());
        //startGenerate();
    }

    public void changeVolume(){

    }

    public void changeIERatio(){

    }

    public void changeFreq(){

    }

    public void generateDemo(){
     //   StringBuilder sb = new StringBuilder();
        int delta = 1000;
//        float sum = 0;
//        int j = 0;
//        int startI = 0;
//        int stopI = 0;
//        float buf = 0;
        for(int i=1; i<400; i++){


            double bufi = (double) i / 100;
            double y = 0;
            double yFlow = 0;

            if(i<201){
                yFlow = (pow((bufi),(double) (1/bufi)) * delta);
            }
            if((i>200)&&(i<282)){
                yFlow = (pow((2),((0.5)))*delta);
            }
            if(i>281){
                yFlow = (pow((bufi-4),2)*delta);
            }
            seriesPres.getData().add(new XYChart.Data<>(i, yFlow));



            if(i<100){
                y = (pow((bufi),(double) (4/bufi)) * delta);
            }

            if((i>99)&&(i<200)){
                y = (pow((bufi-2.2),2)*delta);
            }

            if((i>199)&&(i<281)){
                y = 0;
            }

            if((i>280)){
                y =-(pow(0.8*(bufi-4),2)*delta);
            }

            //y = round((sin(i*(2*3.14/400))+1)*2047);
            seriesFlow.getData().add(new XYChart.Data<>(i, y));
//
//            if((y>0)&&(buf==0)){
//                startI = i;
//            }
//
//            if((y==0)&&(buf>0)){
//                stopI = i;
//            }
//
//            if(y>0){
//                sum = sum + (float)y;
//                j++;
//            }
//            sb.append(round(y)).append(",");
//            buf = (float)y;
        }
//        float s = (stopI-startI)*(sum)/j;
//        outDemo.setText(sb.toString()+"\n\ns = "+s+
//                "\n\nstart = "+startI+
//                "\n\nstop = "+stopI);
    }

    public void stopUI() throws InterruptedException {
        byte[] bufbyte = new byte[16];
        bufbyte[0]=0x00;
        for(int i=1; i<16; i++){
            bufbyte[i]='L';
        }
        stop = true;
        timer.cancel();

        spiroDevice.write(bufbyte);
        spiroDevice.close();
        startBut.setDisable(false);
        stopBut.setDisable(true);
    }

    public void startUI(){
        bufbyte = new byte[16];
        bufbyte[0]=0x01;
        for(int i=1; i<16; i++){
            bufbyte[i]='L';
        }
        stop = false;

        spiroDevice.open();
        spiroDevice.write(bufbyte);

        startLoopRead();

        startBut.setDisable(true);
        stopBut.setDisable(false);
    }

    public void sendAT(){
        byte[] bufbyte;
        bufbyte = textquestion.getText().getBytes(StandardCharsets.UTF_8);
        sendToDevice(bufbyte);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkDevice();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(amountOfPoints*speedInt*0.001);
        //xAxis.setTickUnit(3);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);
        //yAxis.setTickUnit(0.1);

        xAxisPres.setAutoRanging(false);
        xAxisPres.setLowerBound(minX);
        xAxisPres.setUpperBound(maxX);
        //xAxisPres.setTickUnit(1);
        xAxisPres.setTickLabelsVisible(false);

        yAxisPres.setAutoRanging(false);
        yAxisPres.setLowerBound(mindYpres);
        yAxisPres.setUpperBound(maxdYpres);
        //yAxisPres.setTickUnit(1);

        xAxisFlow.setAutoRanging(false);
        xAxisFlow.setLowerBound(minX);
        xAxisFlow.setUpperBound(maxX);
        xAxisFlow.setTickLabelsVisible(false);
        //xAxisFlow.setTickUnit(1);

        yAxisFlow.setAutoRanging(false);
        yAxisFlow.setLowerBound(mindYflow);
        yAxisFlow.setUpperBound(maxdYflow);
        //yAxisFlow.setTickUnit(1);

        series1.setName("Series 1");

        lineChart.setCreateSymbols(false);
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);

        lineChartPres.setCreateSymbols(false);
        lineChartPres.getData().add(seriesPres);

        lineChartFlow.setCreateSymbols(false);
        lineChartFlow.getData().add(seriesFlow);

        series2.getData().add(new XYChart.Data<>(0, 0));
        String color = "blue";
        series2.getNode().setStyle("-fx-stroke: " + color + ";");

        speedInt = (int)speed.getValue();
        countSlider.setValue(amountOfPoints);
        Vdt.setText(String.format("%.0f", (1/(speedInt*0.001)))+" Гц");
        count.setText(String.valueOf(amountOfPoints));
    }
}