package m1app.com.albertsons.palletizerandroid.pojo;


public class PalletJob {
    private int quantity;
    private String numberOfLabels;
    private String seriesData;
    private String itemNumber;
    private String lotNumber;
    private String uom;
    private String description;
    private String pullDate;
    private String productionDate;
    private String printedDate;
    private String ipAddress;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNumberOfLabels() {
        return numberOfLabels;
    }

    public void setNumberOfLabels(String numberOfLabels) {
        this.numberOfLabels = numberOfLabels;
    }

    public String getSeriesData() {
        return seriesData;
    }

    public void setSeriesData(String seriesData) {
        this.seriesData = seriesData;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPullDate() {
        return pullDate;
    }

    public void setPullDate(String pullDate) {
        this.pullDate = pullDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPrintedDate() {
        return printedDate;
    }

    public void setPrintedDate(String printedDate) {
        this.printedDate = printedDate;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getProductionDate() {
        return productionDate;
    }



    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    @Override
    public String toString() {
        return "PalletJob{" +
                "quantity='" + quantity + '\'' +
                ", numberOfLabels='" + numberOfLabels + '\'' +
                ", seriesData='" + seriesData + '\'' +
                ", itemNumber='" + itemNumber + '\'' +
                ", lotNumber='" + lotNumber + '\'' +
                ", uom='" + uom + '\'' +
                ", description='" + description + '\'' +
                ", pullDate='" + pullDate + '\'' +
                '}';
    }
}
