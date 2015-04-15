package cropper_helper.data;

/**
 * Created by diego on 15/04/15.
 */
public class ThermalSingleValue {

    private Double value;
    private int year;

    public ThermalSingleValue(Double v,int y) {
        value = v;
        year = y;
    }

    public Double getValue() {
        return value;
    }

    public int getYear() {
        return year;
    }

    public void setValue(Double v) {
        value = v;
    }

    public void setYear(int y) {
        year = y;
    }
}
