import java.io.Serializable;

public class RecIntegral implements Serializable {

    public double Up1;
    public double Down1;
    public double Step1;
    public double Res;

    public RecIntegral(double Down1, double Up1, double Step1, double Res){
        this.Down1 = Down1;
        this.Up1 = Up1;
        this.Step1 = Step1;
        this.Res = Res;

    }

}

