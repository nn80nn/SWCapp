package nvi.messenger.swcapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Query("SELECT * FROM Message ORDER BY timestamp ASC")
    List<Message> getAllMessages();
}
