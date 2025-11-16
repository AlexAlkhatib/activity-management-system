package CodeWithAlex.ActivityManagementSystem.entity;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "Activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String name;
    private String place;
    private String club;
    private String category;
    private double price;
    private int note;

    @ManyToMany(mappedBy = "activities")
    private Collection<User> users;

    public Activity(){

    }
    public Activity(String name, String place, String club, String category, double price, int note) {
        this.name = name;
        this.place = place;
        this.club = club;
        this.category = category;
        this.price = price;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> userPackages) {
        this.users = users;
    }
}
