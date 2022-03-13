package com.itscryo.hermes.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.itscryo.hermes.global_model.message_db_model.*
import com.itscryo.hermes.repository.MessageDBRepository

@Database(
	entities = arrayOf(Message::class, MessageContent::class, User::class, UserImage::class, Conversation::class),
	version = 1,
	exportSchema = false
)
abstract class MessageDatabase : RoomDatabase() {
	abstract val messageDBRepository: MessageDBRepository


	companion object {
		@Volatile
		private var INSTANCE: MessageDatabase? = null
/*
		private const val updateConversationTrigger =
			"create trigger new_message_trigger after insert on Message " +
				  "begin update Conversation set unreadCount= unreadCount + 1, contentID= new.contentID where secondUserID=new.secondUserID; END"
*/

		private const val upsertConversationTrigger =
			"create trigger upsert_conversation after insert on Message " +
				  "begin insert or replace into Conversation (secondUserID, contentID, unreadCount) " +
				  "values(new.secondUserID, " +
				  "new.contentID, " +
				  "COALESCE((Select unreadCount from Conversation where secondUserID=new.secondUserID) + 1, (CASE WHEN new.isRead=0 THEN 1 ELSE 0 END )));" +
				  " END"

		/*private const val createConversationTrigger =
			"create trigger new_user_trigger after insert on User " +
				  "begin insert into Conversation(secondUserID, contentID, unreadCount) values (new.userID, null, 0); END"
*/
		fun initScripts(db: SupportSQLiteDatabase) {

/*
			db.execSQL(updateConversationTrigger)
			db.execSQL(createConversationTrigger)
*/
			db.execSQL(upsertConversationTrigger)
		}

		private val DB_CALLBACK = object : RoomDatabase.Callback() {
			override fun onCreate(db: SupportSQLiteDatabase) {
				super.onCreate(db)
				initScripts(db)
			}
		}


		fun getInstance(context: Context): MessageDatabase {
			synchronized(this) {
				var instance = INSTANCE

				if (instance == null) {
					instance = Room.databaseBuilder(
						context.applicationContext,
						MessageDatabase::class.java,
						"message_database"
					)
						.fallbackToDestructiveMigration()
						.addCallback(DB_CALLBACK)
						.build()
					INSTANCE = instance
				}
				return instance
			}
		}

	}
}