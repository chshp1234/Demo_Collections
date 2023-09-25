package com.example.aidltest.db.groupchat;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GroupChatDao {

    @Query("select * from group_chat_detail")
    List<GroupChatEntity> getAllGroupChatInfo();

    @Query("select * from group_chat_detail where _id = :id")
    GroupChatEntity getGroupChatInfo(int id);

    @Query("select * from group_chat_detail where name = :name")
    GroupChatEntity getGroupChatInfo(String name);

    @Query("select _id from group_chat_detail where name = :name")
    int getId(String name);

    @Update
    int update(GroupChatEntity groupChatEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long add(GroupChatEntity groupChatEntity);

    @Insert()
    List<Long> addAll(List<GroupChatEntity> groupChatEntity);

    @Delete()
    void delete(GroupChatEntity groupChatEntity);
}
