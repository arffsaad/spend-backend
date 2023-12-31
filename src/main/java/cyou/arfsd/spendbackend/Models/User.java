package cyou.arfsd.spendbackend.Models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"email"})
)
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)

    private Integer id;

    private String name;

    // password mana bro, simpan hash terus. Do hash conversion directly in Frontend app to avoid transport sniffing or mitm.

    private String email;

    private String password;

    private String salt;

    private String token;

    @Column(name = "validUntil", columnDefinition = "TIMESTAMP DEFAULT NULL", nullable = true)
    private Timestamp validUntil;

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Timestamp validUntil){
        this.validUntil = validUntil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }
}
