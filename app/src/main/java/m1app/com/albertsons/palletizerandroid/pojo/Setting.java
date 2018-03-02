package m1app.com.albertsons.palletizerandroid.pojo;


import java.io.Serializable;

public class Setting implements Serializable {

    private String companyNumber;
    private String facilityCode;

    public Setting (){}

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facityCode) {
        this.facilityCode = facityCode;
    }
}
