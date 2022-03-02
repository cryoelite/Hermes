package com.itscryo.hermes.app.inbox.item_recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itscryo.hermes.databinding.MessageRecievedBinding
import com.itscryo.hermes.app.inbox.model.MessageRecieved

class InboxMessageRecievedViewHolder(val binding: MessageRecievedBinding) :
	RecyclerView.ViewHolder(binding.root) {
	companion object {
		fun from(parent: ViewGroup): InboxMessageRecievedViewHolder {
			val layoutInflater = LayoutInflater.from(parent.context)
			val binding =
				MessageRecievedBinding.inflate(layoutInflater, parent, false)
			return InboxMessageRecievedViewHolder(binding)
		}
	}

	fun bind(item: MessageRecieved) {
		binding.messageRecieved = item
		binding.executePendingBindings()
	}

}