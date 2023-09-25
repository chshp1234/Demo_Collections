package com.example.aidltest.room;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.MyApplication;

import static com.example.aidltest.room.MyDatabase.VERSION;

@Database(
        entities = {UserEntity.class},
        exportSchema = false,
        version = VERSION)
public abstract class MyDatabase extends RoomDatabase {

    public static final String DB_NAME = "my_database";
    public static final int VERSION = 1;

    public static MyDatabase getDatabase() {
        return SingletonInstance.INSTANCE;
    }

    public abstract UserEntityDao getUserEntityDao();

    private static class SingletonInstance {
        private static final MyDatabase INSTANCE =
                Room.databaseBuilder(MyApplication.getContext(), MyDatabase.class, DB_NAME)
                        .addCallback(
                                new Callback() {
                                    // 第一次创建数据库时调用，但是在创建所有表之后调用的
                                    @Override
                                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                        LogUtils.d("onCreate: " + db);
                                        super.onCreate(db);
                                    }

                                    // 当数据库被打开时调用
                                    @Override
                                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                        LogUtils.d("onOpen: " + db);
                                        super.onOpen(db);
                                    }

                                    @Override
                                    public void onDestructiveMigration(
                                            @NonNull SupportSQLiteDatabase db) {
                                        LogUtils.d("onDestructiveMigration: " + db);
                                        super.onDestructiveMigration(db);
                                    }
                                })
                        .allowMainThreadQueries() // 允许在主线程查询数据
                        .build();
    }
}
