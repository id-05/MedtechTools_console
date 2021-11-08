package sample;

import static java.lang.Math.pow;

public class Breathing {

    int PEEP;

    Breathing(int PEEP){
        this.PEEP = PEEP;
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
        if(Xvalue<201){
            buf = (pow((bufi),(double)(1/bufi)) * delta);
            if(buf<(PEEP * kdelta)){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }
        if((Xvalue>200)&&(Xvalue<282)){
            buf = (pow((2),((0.5)))*delta);
            if(buf<PEEP * kdelta){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }
        if(Xvalue>281){
            buf = (pow((bufi-4),2)*delta);
            if(buf<PEEP * kdelta){
                yPres = PEEP * kdelta;
            }else{
                yPres = buf;
            }
        }

        return yPres;
    }
}
