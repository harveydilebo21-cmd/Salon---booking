package com.salon.booking;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The salon's public details, read from application.properties (salon.business.*).
 * Templates use them as ${@business.name}, ${@business.phone} and so on, so changing a
 * phone number or the opening hours never means editing HTML.
 */
@Component("business")
@ConfigurationProperties(prefix = "salon.business")
public class BusinessInfo {

    private String name = "Your Salon Name";
    private String tagline = "Professional hair care, by appointment";
    private String about = "";
    private String phone = "000 000 0000";
    private String whatsapp = "";
    private String email = "hello@example.com";
    private String address = "1 Example Street, Your Town";
    private List<String> hours = List.of("Mon to Fri: 08:00 - 17:00", "Sat: 08:00 - 14:00", "Sun: Closed");
    private String instagram = "";
    private List<String> gallery = List.of();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<String> getHours() { return hours; }
    public void setHours(List<String> hours) { this.hours = hours; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public List<String> getGallery() { return gallery; }
    public void setGallery(List<String> gallery) { this.gallery = gallery; }
}