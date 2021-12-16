package sample;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;

public class Breathing {

    int PEEP;
    int allCount = 450;

    Breathing(int PEEP){
        this.PEEP = PEEP;
    }

    public int getPEEP() {
        return PEEP;
    }

    public void setPEEP(int PEEP) {
        this.PEEP = PEEP;
    }

//    public double getYvalue(int Xvalue){
//        double buf;
//        double yPres = 0;
//        int delta = 1000;
//        int kdelta = 100;
//        double koefLongInspiration = 2.25;
//        double koefPauseInspiration = 1.6;
//        double koefLongExspiration = 1.125;

//        double bufi = (double) Xvalue / 100;
//        if(Xvalue<=(allCount/koefLongInspiration)){
//            buf = (pow((bufi),(1/bufi)) * delta);
//            if(buf<(PEEP * kdelta)){
//                yPres = PEEP * kdelta;
//            }else{
//                yPres = buf;
//            }
//        }
//        if((Xvalue>200)&&(Xvalue<282)){
//            buf = (pow((2),((0.5)))*delta);
//            if(buf<PEEP * kdelta){
//                yPres = PEEP * kdelta;
//            }else{
//                yPres = buf;
//            }
//        }
//        if(Xvalue>281){
//            buf = (pow((bufi-4),2)*delta);
//            if(buf<PEEP * kdelta){
//                yPres = PEEP * kdelta;
//            }else{
//                yPres = buf;
//            }
//        }
//        if(Xvalue>400){
//            buf = 0;
//            if(buf<PEEP * kdelta){
//                yPres = PEEP * kdelta;
//            }else{
//                yPres = buf;
//            }
//        }
//        //System.out.println("Xvalue = "+Xvalue +"    yPres = "+yPres);
//        return yPres;
//    }

    public double getYvalue(int Xvalue){
        double buf;
        double yPres = 0;
        int delta = 1000;
        int kdelta = 100;

        double koefLongInspiration = 2.25;
        double koefPauseInspiration = 1.6;
        double koefLongExspiration = 1.125;

        double bufi = (double) Xvalue / 100;
        if(Xvalue<=(allCount/koefLongInspiration)){
            buf = (pow((bufi),(double)(1/bufi)) * delta);
            if(buf<(PEEP * kdelta)){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }
        if((Xvalue>(allCount/koefLongInspiration))&&(Xvalue<=(allCount/koefPauseInspiration))){
            buf = (pow((2),((0.5)))*delta);
            if(buf<PEEP * kdelta){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }
        if((Xvalue>(allCount/koefPauseInspiration))&&(Xvalue<=(allCount/koefLongExspiration))){
            buf = (pow((bufi-4),2)*delta);
            if(buf<PEEP * kdelta){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }
        if(Xvalue>(allCount/koefLongExspiration)){
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
           data.add( -50*(getYvalue(i-1) - getYvalue(i) ));
                        System.out.println("Xvalue = "+i +"   getYvalue(i-1) = "+getYvalue(i-1)+
                    "   getYvalue(i) =  "+getYvalue(i)+"    yPres = " + "buf");
        }
//        List<Number> bufList = getPresData();
//        for(int i=1; i<bufList.size()-1; i++){
//            float buf = bufList.get(i-1).floatValue() - bufList.get(i).floatValue();
//            data.add(bufList.get(i-1).floatValue() - bufList.get(i).floatValue());
//            System.out.println("Xvalue = "+i +"   bufList.get(i-1).floatValue() = "+bufList.get(i-1).floatValue()+
//                    "   ufList.get(i).floatValue() =  "+bufList.get(i).floatValue()+"    yPres = "+buf);
//        }
        return data;
    }
}
