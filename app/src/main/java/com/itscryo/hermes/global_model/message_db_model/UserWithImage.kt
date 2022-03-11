package com.itscryo.hermes.global_model.message_db_model

import androidx.room.Embedded

data class UserWithImage(@Embedded val user:User,@Embedded val image: UserImage)