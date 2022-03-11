package com.itscryo.hermes.repository

import com.itscryo.hermes.global_model.UserData

import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.UserPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import javax.inject.Inject

@Module
@InstallIn(ActivityComponent::class)
abstract class LocalRepoModule{

	@Binds
	abstract  fun bindLocalRepo(
		localImpl: LocalRepository
	): ILocalRepository
}


class LocalRepository  @Inject constructor(): ILocalRepository {
	override suspend fun storeUserCredAsync(userData: UserData): Deferred<Unit> {
		val deferred= CompletableDeferred<kotlin.Unit>()
		deferred.complete(Unit)
		return deferred
	}

	override suspend fun storePrefsAsync(userPrefs: UserPreferences): Deferred<Unit> {
		val deferred= CompletableDeferred<kotlin.Unit>()
		deferred.complete(Unit)
		return deferred
	}

}