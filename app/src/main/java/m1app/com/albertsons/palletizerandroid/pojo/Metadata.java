
package m1app.com.albertsons.palletizerandroid.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Metadata implements Serializable {

    @SerializedName("Field")
    @Expose
    private List<Field> field = null;

    public List<Field> getField() {
        return field;
    }

    public void setField(List<Field> field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "field=" + field +
                '}';
    }
}
