package dingdong.com.mql.www.compusdingdong.activity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbMealBean;

/**
 * 数据库管理类
 * Created by MQL on 2018/5/21.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME="dingdong.db";//数据库名称

    private static final int DB_VERSION=1;//数据库版本

    private static DatabaseHelper instance;//单例模式中的数据库唯一对象

    private DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public static synchronized DatabaseHelper getHelp(Context context){
        if(instance == null){
            synchronized (DatabaseHelper.class){
                if(instance == null){
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, DbActivityBean.class);//创建表
            TableUtils.createTable(connectionSource, DbMealBean.class);//创建表
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource,DbActivityBean.class,true);//删除表
            TableUtils.dropTable(connectionSource,DbMealBean.class,true);//删除表
            onCreate(sqLiteDatabase, connectionSource);//创建数据库
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Dao<DbActivityBean, String> userDao;

    public Dao<DbActivityBean, String> getActivityDao() throws SQLException{
        if(userDao == null){
            userDao = getDao(DbActivityBean.class);
        }
        return userDao;
    }

    private Dao<DbMealBean, String> mealDao;

    public Dao<DbMealBean, String> getMealDao() throws SQLException{
        if(mealDao == null){
            mealDao = getDao(DbMealBean.class);
        }
        return mealDao;
    }
}
