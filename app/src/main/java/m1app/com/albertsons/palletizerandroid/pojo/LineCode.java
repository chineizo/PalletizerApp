package m1app.com.albertsons.palletizerandroid.pojo;

import java.io.Serializable;

/**
 * Created by chinedu on 1/18/18.
 */

public class LineCode implements Serializable {

    private String numberSeriesType;
    private String numberSeries;
    private String printerName;
    private String ipAddress;
    private String description;

    public String getNumberSeriesType() {
        return numberSeriesType;
    }

    public void setNumberSeriesType(String numberSeriesType) {
        this.numberSeriesType = numberSeriesType;
    }

    public String getNumberSeries() {
        return numberSeries;
    }

    public void setNumberSeries(String numberSeries) {
        this.numberSeries = numberSeries;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "LineCode{" +
                "numberSeriesType='" + numberSeriesType + '\'' +
                ", numberSeries='" + numberSeries + '\'' +
                ", printerName='" + printerName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
