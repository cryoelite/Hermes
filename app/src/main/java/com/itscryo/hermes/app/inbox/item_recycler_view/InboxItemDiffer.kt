package com.itscryo.hermes.app.inbox.item_recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.itscryo.hermes.app.inbox.model.Message

class InboxItemDiffer : DiffUtil.ItemCallback<Message>() {
	override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
		return oldItem.userName == newItem.userName

	}

	override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
		if (oldItem.messageType == newItem.messageType)
		{
			if(oldItem.message== newItem.message && oldItem.time== newItem.time) {
				return true
			}
		}
		return false
	}
}
