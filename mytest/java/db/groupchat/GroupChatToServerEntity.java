package com.example.aidltest.db.groupchat;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_chat_to_server")
public class GroupChatToServerEntity {
    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "server_id", defaultValue = "-1")
    private int serverId;

    @ColumnInfo(name = "group_chat_id", defaultValue = "-1")
    private int groupChatId;

    public GroupChatToServerEntity() {}

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(int groupChatId) {
        this.groupChatId = groupChatId;
    }

    @Override
    public String toString() {
        return "GroupChatToServerEntity{"
                + "_id="
                + _id
                + ", serverId="
                + serverId
                + ", groupChatId="
                + groupChatId
                + '}';
    }
}
