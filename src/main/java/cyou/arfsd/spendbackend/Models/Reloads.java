package cyou.arfsd.spendbackend.Models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Reloads {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)

    private Integer id;
    private Integer userid;
    private Integer amount;
    private String remark;

    @Column(name = "createdtime", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdtime;
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getUserid(){
        return userid;
    }

    public void setUserid(Integer userid){
        this.userid=userid;
    }

    public Integer getAmount(){
        return amount;
    }

    public void setAmount(Integer amount){
        this.amount=amount;
    }

    public String getRemark(){
        return remark;
    }

    public void setRemark(String remark){
        this.remark=remark;
    }

    public Timestamp getCreatedtime(){
        return createdtime;
    }

    public void setCreatedtime(Timestamp createdtime){
        this.createdtime=createdtime;
    }
}
