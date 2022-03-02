package com.itscryo.hermes.app.inbox.item_recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.itscryo.hermes.app.inbox.model.*

class InboxRVAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(InboxItemDiffer()) {
	override fun onCreateViewHolder(
		parent: ViewGroup, viewType:
		Int
	): RecyclerView.ViewHolder {
		return when (viewType) {
			MessageType.SENT_MESSAGE.index -> InboxMessageSentViewHolder.from(parent)
			MessageType.RECIEVED_MESSAGE.index -> InboxMessageRecievedViewHolder.from(
				parent
			)
			else -> throw (Exception("Unknown ViewHolder value"))
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (holder) {
			is InboxMessageRecievedViewHolder -> {
				val item = getItem(position) as MessageRecieved
				holder.bind(item)
			}
			is InboxMessageSentViewHolder -> {
				val item= getItem(position) as MessageSent
				holder.bind(item)
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		return when (getItem(position)) {
			is MessageSent ->MessageType.SENT_MESSAGE.index
			is MessageRecieved -> MessageType.RECIEVED_MESSAGE.index
			else -> throw Exception("Unknown message type")
		}
	}
}