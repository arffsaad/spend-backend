package cyou.arfsd.spendbackend.Models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Spends {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)

    private Integer id;
    private Integer userid;
    private Integer amount;
    private Integer walletid;
    private String remark;
    private String recslug;

    @Column(name = "is_instalment", columnDefinition = "boolean default false")
    private Boolean isInstalment;

    @Column(name = "instalment_id", columnDefinition = "integer default null", nullable = true)
    private Integer instalmentId;

    @Column(name = "fulfilled_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fulfilled_at;

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

    public Integer getWalletid() {
        return walletid;
    }

    public void setWalletid(Integer walletid) {
        this.walletid=walletid;
    }

    public String getRemark(){
        return remark;
    }

    public void setRemark(String remark){
        this.remark=remark;
    }

    public Timestamp getFulfilled_at(){
        return fulfilled_at;
    }

    public void setFulfilled_at(Timestamp fulfilled_at){
        this.fulfilled_at=fulfilled_at;
    }

    public String getRecslug(){
        return recslug;
    }

    public void setRecslug(String recslug){
        this.recslug=recslug;
    }

    public void setIsInstalment(Boolean isInstalment) {
        this.isInstalment = isInstalment;
    }

    public Boolean getIsInstalment() {
        return isInstalment;
    }

    public void setInstalmentId(Integer instalmentId) {
        this.instalmentId = instalmentId;
    }

    public Integer getInstalmentId() {
        return instalmentId;
    }
}
