package dingdong.com.mql.www.compusdingdong.activity.databasedao;

import android.content.Context;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.database.DatabaseHelper;

/**
 * DbActivityBean类/表控制类
 * Created by MQL on 2018/5/21.
 */

public class DbActivityDao {
    private Dao<DbActivityBean,String> ActivityDao;

    private DatabaseHelper mDatabaseHelper;

    public DbActivityDao(Context context) {
        mDatabaseHelper = DatabaseHelper.getHelp(context);
        try {
            ActivityDao = mDatabaseHelper.getActivityDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public QueryBuilder getqueryQuery(){
        return ActivityDao.queryBuilder();
    }

    public void add(DbActivityBean activity) {
        try {
            ActivityDao.create(activity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(DbActivityBean activity) {
        try {
            ActivityDao.delete(activity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updata(DbActivityBean activity) {
        try {
            ActivityDao.update(activity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DbActivityBean queryForId(String id) {
        DbActivityBean activityBean = null;
        try {
            activityBean = ActivityDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activityBean;
    }

    public List<DbActivityBean> queryForAll() {
        List<DbActivityBean> activitys = new ArrayList<>();
        try {
            activitys = ActivityDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activitys;
    }
    //清空表
    public void deleteAll(){
        try {
            ActivityDao.queryRaw("delete from DbActivityBean");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
