package com.example.aidltest.db.groupchat;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupChatToServerDao {

    @Query("select * from group_chat_to_server")
    List<GroupChatToServerEntity> getAll();

    @Query(
            "select * from group_chat_detail where group_chat_detail._id ="
                    + " (select group_chat_id from group_chat_to_server where server_id = :serverId)")
    GroupChatEntity getGroupChatId(int serverId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(GroupChatToServerEntity groupChatToServerEntity);
}
