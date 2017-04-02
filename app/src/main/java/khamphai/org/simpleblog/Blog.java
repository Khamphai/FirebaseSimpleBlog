package khamphai.org.simpleblog;

/**
 * Created by K'Phai on 02/15/2017.
 */

public class Blog {
    private String title;
    private String des;
    private String image;

    public Blog() {

    }

    public Blog(String title, String des, String image) {
        this.title = title;
        this.des = des;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
