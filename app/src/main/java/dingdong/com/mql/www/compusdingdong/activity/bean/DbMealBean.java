package dingdong.com.mql.www.compusdingdong.activity.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by MQL on 2018/5/23.
 */
@DatabaseTable
public class DbMealBean {
    @DatabaseField(id = true)
    private String dbmealId;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] dbmealpicture;
    @DatabaseField
    private String dbcreatuser;
    @DatabaseField
    private String dbmealname;
    @DatabaseField
    private String dbmealsite;
    @DatabaseField
    private String dbmealintroduction;

    public DbMealBean() {
    }

    public DbMealBean(String dbmealId,
                      byte[] dbmealpicture,
                      String dbcreatuser,
                      String dbmealname,
                      String dbmealsite,
                      String dbmealintroduction) {
        this.dbmealId = dbmealId;
        this.dbmealpicture = dbmealpicture;
        this.dbcreatuser = dbcreatuser;
        this.dbmealname = dbmealname;
        this.dbmealsite = dbmealsite;
        this.dbmealintroduction = dbmealintroduction;
    }

    public String getDbmealId() {
        return dbmealId;
    }

    public void setDbmealId(String dbmealId) {
        this.dbmealId = dbmealId;
    }

    public byte[] getDbmealpicture() {
        return dbmealpicture;
    }

    public void setDbmealpicture(byte[] dbmealpicture) {
        this.dbmealpicture = dbmealpicture;
    }

    public String getDbcreatuser() {
        return dbcreatuser;
    }

    public void setDbcreatuser(String dbcreatuser) {
        this.dbcreatuser = dbcreatuser;
    }

    public String getDbmealname() {
        return dbmealname;
    }

    public void setDbmealname(String dbmealname) {
        this.dbmealname = dbmealname;
    }

    public String getDbmealsite() {
        return dbmealsite;
    }

    public void setDbmealsite(String dbmealsite) {
        this.dbmealsite = dbmealsite;
    }

    public String getDbmealintroduction() {
        return dbmealintroduction;
    }

    public void setDbmealintroduction(String dbmealintroduction) {
        this.dbmealintroduction = dbmealintroduction;
    }
}
