package com.itscryo.hermes.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.itscryo.hermes.domain.IMessageDBRepository
import com.itscryo.hermes.global_model.message_db_model.*
import com.itscryo.hermes.repository.MessageDBRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module()
@InstallIn(SingletonComponent::class)
class DatabaseModule {


	@Singleton
	@Provides
	fun provideDAO(messageDatabase: MessageDatabase): MessageDBRepository {
		return messageDatabase.messageDBRepository
	}

	@Singleton
	@Provides
	fun provideDB(@ApplicationContext context: Context): MessageDatabase {
		return MessageDatabase.getInstance(context)

	}
}

@Module(includes = [DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class DBImpl{

	@Singleton
	@Binds
	abstract fun messageBinder(messageDBRepo: MessageDBRepository): IMessageDBRepository
}


@Database(
	entities = [Message::class, MessageMedia::class, MessageText::class, User::class, UserImage::class, Conversation::class],
	version = 1,
	exportSchema = false
)
abstract class MessageDatabase : RoomDatabase() {
	abstract val messageDBRepository: MessageDBRepository


	companion object {
		@Volatile
		private var INSTANCE: MessageDatabase? = null

		private const val upsertConversationTrigger =
			"create trigger upsert_conversation after insert on Message " +
				  "begin insert or replace into Conversation (secondUserID, messageMediaID, messageTextID, unreadCount) " +
				  "values(new.secondUserID, " +
				  "new.messageMediaID, " +
				  "new.messageTextID, " +
				  "COALESCE((Select unreadCount from Conversation where secondUserID=new.secondUserID) +(CASE WHEN new.isRead=0 THEN 1 ELSE 0 END ), (CASE WHEN new.isRead=0 THEN 1 ELSE 0 END )));" +
				  " END"

		fun initScripts(db: SupportSQLiteDatabase) {
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