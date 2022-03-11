packa  ge com.itscryo.hermes.app.inbox.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.itscryo.hermes.app.inbox.MessageDBToMessage
import com.itscryo.hermes.app.inbox.MessageDBToMessage.Companion.from
import com.itscryo.hermes.app.inbox.model.Message
import com.itscryo.hermes.global_model.message_db_model.UserWithImage
import com.itscryo.hermes.service.MessageDatabase

class MessageModel : ViewModel() {
	lateinit var messageList: List<LiveData<Message>>

	lateinit var userList: List<LiveData<UserWithImage>>


	suspend fun initAsync(
		database: MessageDatabase,
		context: Context
	) {
		if (!::messageList.isInitialized || !::userList.isInitialized) {
			val messagesDB = database.messageDBRepository.getMessagesWithContentAsync()
			val messageDBToMessage = MessageDBToMessage(database, context)
			val messages = messagesDB.map {
				messageDBToMessage.from(it)
			}
			messageList = messages
			userList =
				database.messageDBRepository.getUsersWithImagesAsync()
		}
	}

}