package m1app.com.albertsons.palletizerandroid.pojo;


import java.io.Serializable;

public class User implements Serializable {

    private String authorization;

    public User (){}

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
