package sample;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.pow;

public class Breathing {

    int PEEP;
    int allCount = 800;
    int frequency = 12;
    int volume = 700;
    double ie = 2; //I:E
    String mode = "CMV";

    double kLInsp = 2.25;
    double kPInsp = 1.6;
    double kLExsp = 1.125;

    Breathing(int PEEP, double kTime){
        this.PEEP = PEEP;
        allCount = (int)(allCount * kTime);
        kLInsp = kLInsp * kTime;
        kPInsp = kPInsp * kTime;
        kLExsp = kLExsp * kTime;
    }

    Breathing(int PEEP, double kTime, String mode){
        this.PEEP = PEEP;
        this.mode = mode;
        double t_one = (double)60/frequency;
        double t_i = (t_one)/(1+ie);
        double t_e = ie*t_i;
        allCount = (int)(allCount * kTime);
        kLInsp = kLInsp * kTime;
        kPInsp = kPInsp * kTime;
        kLExsp = kLExsp * kTime;
    }

    public void getAlternateY(int X){
        switch (mode){
            case "CMV":{

                break;
            }
            case "PCV":{
                break;
            }
            default:break;
        }
    }

    public double getYvalue(int Xvalue){
        double buf;
        double yPres = 0;
        int delta = 1000;
        int kdelta = 100;


        double bufi = (double) Xvalue / 100;

        if(Xvalue<=(allCount/kLInsp)){
            buf = (pow((bufi),(1/bufi)) * delta);
            if(buf<(PEEP * kdelta)){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }

        if((Xvalue>(allCount/kLInsp))&&(Xvalue<=(allCount/kPInsp))){
            buf = (pow((2),((0.5)))*delta);
            if(buf<PEEP * kdelta){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }

        if((Xvalue>(allCount/kPInsp))&&(Xvalue<=(allCount/kLExsp))){
            buf = (pow((bufi-4),2)*delta);
            if(buf<PEEP * kdelta){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }

        if(Xvalue>(allCount/kLExsp)){
            buf = 0;
            if(buf<PEEP * kdelta){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }

        yPres = Xvalue;
        return yPres;
    }

    public List<Number> getPresData(){
        List<Number> data = new ArrayList<>();
        for(int i=0; i<allCount; i++){
            data.add(getYvalue(i));
        }
        return data;
    }

    public List<Number> getFlowData(){
        List<Number> data = new ArrayList<>();
        for(int i=1; i<allCount; i++){
           data.add( -1*(getYvalue(i-1) - getYvalue(i) ));
        }
        return data;
    }

    public static enum Mode {
        CMV,
        PCV;

        private Mode() {
        }
    }
}
