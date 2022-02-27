package com.itscryo.hermes.inbox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itscryo.hermes.databinding.MessageSentBinding
import com.itscryo.hermes.model.MessageSent

class InboxMessageSentViewHolder(val binding: MessageSentBinding) :
	RecyclerView.ViewHolder(binding.root) {
	companion object {
		fun from(parent: ViewGroup): InboxMessageSentViewHolder {
			val layoutInflater = LayoutInflater.from(parent.context)
			val binding =
				MessageSentBinding.inflate(layoutInflater, parent, false)
			return InboxMessageSentViewHolder(binding)
		}
	}

	fun bind(item: MessageSent) {
		binding.messageSent = item
		binding.executePendingBindings()
	}

}
