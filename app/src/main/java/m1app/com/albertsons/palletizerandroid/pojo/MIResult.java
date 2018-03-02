
package m1app.com.albertsons.palletizerandroid.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MIResult implements Serializable {


    private String serialNumber;

    @SerializedName("@code")
    @Expose
    private String errorCode;

    @SerializedName("@type")
    @Expose
    private String errorType;

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("Program")
    @Expose
    private String program;
    @SerializedName("Transaction")
    @Expose
    private String transaction;
    @SerializedName("Metadata")
    @Expose
    private Metadata metadata;
    @SerializedName("MIRecord")
    @Expose
    private List<MIRecord> mIRecord = null;

    private String lotNumber;

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<MIRecord> getMIRecord() {
        return mIRecord;
    }

    public void setMIRecord(List<MIRecord> mIRecord) {
        this.mIRecord = mIRecord;
    }

    public String getCode() {
        return errorCode;
    }

    public void setCode(String code) {
        this.errorCode = code;
    }

    public String getType() {
        return errorType;
    }

    public void setType(String type) {
        this.errorType = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setLotNumber (String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getLotNumber () {
        return lotNumber;
    }

    @Override
    public String toString() {
        return "MIResult{" +
                "program='" + program + '\'' +
                ", transaction='" + transaction + '\'' +
                ", metadata=" + metadata +
                ", mIRecord=" + mIRecord +
                ", code=" + errorCode +
                ", type=" + errorType +
                ", message=" + message +
                '}';
    }
}
