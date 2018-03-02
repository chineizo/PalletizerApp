
package m1app.com.albertsons.palletizerandroid.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MIRecord implements Serializable {

    @SerializedName("RowIndex")
    @Expose
    private String rowIndex;
    @SerializedName("NameValue")
    @Expose
    private List<NameValue> nameValue = null;

    private String lotNumber;

    public String getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }

    public List<NameValue> getNameValue() {
        return nameValue;
    }

    public void setNameValue(List<NameValue> nameValue) {
        this.nameValue = nameValue;
    }

    public void setLotNumber (String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getLotNumber () {
        return lotNumber;
    }

    @Override
    public String toString() {
        return "MIRecord{" +
                "rowIndex='" + rowIndex + '\'' +
                ", nameValue=" + nameValue +
                '}';
    }
}
