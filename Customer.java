package model;
import java.io.Serializable;
public class Customer implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private String name, email, mobile;
    private final String customerId;
    private Address address;
    private static long customerCounter = 100;
    private static synchronized String generateCustomerId() { return "CUST" + (++customerCounter); }
    public Customer(String name, String email, String mobile) { this.name=name; this.email=email; this.mobile=mobile; this.customerId=generateCustomerId(); }
    public String getName(){return name;} public String getEmail(){return email;} public String getMobile(){return mobile;} public String getCustomerId(){return customerId;}
    public Address getAddress(){return address;} public void setAddress(Address address){this.address=address;}
    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;
        private String line, city, pincode;
        public Address(String line,String city,String pincode){this.line=line;this.city=city;this.pincode=pincode;}
        public String getLine(){return line;} public String getCity(){return city;} public String getPincode(){return pincode;}
        @Override public String toString(){return line+", "+city+" - "+pincode;}
    }
    @Override public Customer clone(){try{Customer copy=(Customer)super.clone(); if(address!=null) copy.address=new Address(address.line,address.city,address.pincode); return copy;}catch(CloneNotSupportedException e){throw new AssertionError(e);}}
    @Override public String toString(){return "Customer ID: "+customerId+"\nName: "+name+"\nEmail: "+email+"\nMobile: "+mobile+"\nAddress: "+(address==null?"Not provided":address);}
}
