package com.itscryo.hermes.inbox

import androidx.recyclerview.widget.DiffUtil
import com.itscryo.hermes.model.Message

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
