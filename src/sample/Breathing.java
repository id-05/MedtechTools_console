package sample;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.pow;

public class Breathing {

    int PEEP;
    int allCount = 450;
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

    public int getPEEP() {
        return PEEP;
    }

    public void setPEEP(int PEEP) {
        this.PEEP = PEEP;

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
}
