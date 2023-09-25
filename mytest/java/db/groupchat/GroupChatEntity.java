package com.example.aidltest.db.groupchat;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_chat_detail")
public class GroupChatEntity {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "count")
    private int count;

    @ColumnInfo(name = "group_owner")
    private String groupOwner;

    @ColumnInfo(name = "group_of_announcement")
    private String groupOfAnnouncement;

    @ColumnInfo(name = "is_need_confirm")
    private boolean isNeedConfirm;

    @ColumnInfo(name = "qr_code_path")
    private String QRCodePath;

    @ColumnInfo(name = "create_time")
    private long createdTime;

    @ColumnInfo(name = "last_modified_time")
    private long lastModifiedTime;

    public GroupChatEntity() {}

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public String getGroupOfAnnouncement() {
        return groupOfAnnouncement;
    }

    public void setGroupOfAnnouncement(String groupOfAnnouncement) {
        this.groupOfAnnouncement = groupOfAnnouncement;
    }

    public boolean isNeedConfirm() {
        return isNeedConfirm;
    }

    public void setNeedConfirm(boolean needConfirm) {
        isNeedConfirm = needConfirm;
    }

    public String getQRCodePath() {
        return QRCodePath;
    }

    public void setQRCodePath(String QRCodePath) {
        this.QRCodePath = QRCodePath;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @Override
    public String toString() {
        return "GroupChatEntity{"
                + "_id="
                + _id
                + ", name='"
                + name
                + '\''
                + ", count='"
                + count
                + '\''
                + ", groupOwner='"
                + groupOwner
                + '\''
                + ", groupOfAnnouncement='"
                + groupOfAnnouncement
                + '\''
                + ", isNeedConfirm="
                + isNeedConfirm
                + ", QRCodePath='"
                + QRCodePath
                + '\''
                + ", createdTime="
                + createdTime
                + ", lastModifiedTime="
                + lastModifiedTime
                + '}'
                + "\n";
    }
}
