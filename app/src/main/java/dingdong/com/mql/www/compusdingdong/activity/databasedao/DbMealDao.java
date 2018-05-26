package dingdong.com.mql.www.compusdingdong.activity.databasedao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbMealBean;
import dingdong.com.mql.www.compusdingdong.activity.database.DatabaseHelper;

/**
 * Created by MQL on 2018/5/23.
 */

public class DbMealDao {
    private Dao<DbMealBean,String> MealDao;

    private DatabaseHelper mDatabaseHelper;

    public DbMealDao(Context context) {
        mDatabaseHelper = DatabaseHelper.getHelp(context);
        try {
            MealDao = mDatabaseHelper.getMealDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public QueryBuilder getqueryQuery(){
        return MealDao.queryBuilder();
    }

    public void add(DbMealBean activity) {
        try {
            MealDao.create(activity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(DbMealBean activity) {
        try {
            MealDao.delete(activity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updata(DbMealBean activity) {
        try {
            MealDao.update(activity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DbMealBean queryForId(String id) {
        DbMealBean mealBean = null;
        try {
            mealBean = MealDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mealBean;
    }

    public List<DbMealBean> queryForAll() {
        List<DbMealBean> activitys = new ArrayList<>();
        try {
            activitys = MealDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activitys;
    }
    //清空表
    public void deleteAll(){
        try {
            MealDao.queryRaw("delete from DbMealBean");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
