import java.util.LinkedList;
import java.util.List;

public class RecIntegral {

private List<String> info;

    public RecIntegral(List<String> info){
        this.info = new LinkedList<>(info);
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(int index, String info){
        this.info.set(index, info);
    }


}

