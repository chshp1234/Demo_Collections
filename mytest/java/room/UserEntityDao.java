package com.example.aidltest.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserEntityDao {

    @Query("select * from user")
    List<UserEntity> getUserList();

    @Query("select * from user where name = :name")
    UserEntity getUser(String name);

    @Query("delete from user where name = :name")
    int deleteUser(String name);

    @Query("update user set age = :age where id = :id")
    int update(int id, int age);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(UserEntity userEntity);

    @Delete()
    void deleteUser(UserEntity userEntity);
}
