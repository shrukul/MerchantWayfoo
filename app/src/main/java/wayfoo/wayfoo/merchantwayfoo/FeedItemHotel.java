package wayfoo.wayfoo.merchantwayfoo;

/**
 * Created by Axle on 04/02/2016.
 */
public class FeedItemHotel {
    private String title,amt;
    private int b2;
    private String type,veg,price,available;

    FeedItemHotel(){}

    FeedItemHotel(int b2, String title, String price, String veg, String amt, String type) {
        this.title=title;
        this.b2=b2;
        this.veg=veg;
        this.type=type;
        this.price=price;
        this.amt=amt;
    }

    FeedItemHotel(String title, String price, String veg, String amt, String type) {
        this.title=title;
        this.veg=veg;
        this.type=type;
        this.price=price;
        this.amt=amt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getID() {
        return b2;
    }

    public void setID(int b2) {
        this.b2 = b2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVeg() {
        return veg;
    }

    public void setVeg(String veg) {
        this.veg = veg;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getAvailable() {
        return available;
    }
}