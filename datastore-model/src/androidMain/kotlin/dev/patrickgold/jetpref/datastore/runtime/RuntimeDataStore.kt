/*
 * Copyright 2025 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.jetpref.datastore.runtime

import android.content.Context
import dev.patrickgold.jetpref.datastore.model.PreferenceData
import kotlinx.coroutines.runBlocking

/**
 * Runtime datastore implementation managing events, loading, and persisting of the model data.
 *
 * To construct a new runtimedatastore instance, see [dev.patrickgold.jetpref.datastore.jetprefRuntimeDataStore].
 *
 * @since 0.5.0
 */
class RuntimeDataStore internal constructor(): DataStore<RuntimePreferenceModel>(RuntimePreferenceModel()) {
    private var shouldPersist: Boolean = true
    private lateinit var datastoreName: String

    /**
     * Initialize the datastore with given [loadStrategy] and [persistStrategy]. If both strategies are
     * disabled, this model effectively operates as an in-memory-only model. Given a non-disabled load
     * strategy, the initialization handler will also immediately load the model with given reader into
     * memory. Any failure in the loading process will influence this request's result.
     *
     * Typically, this method is called once in the main method of the application, or for Android the
     * `Application.onCreate()` method. If this method is never called, the model behaves as if it had
     * been called with the following init request:
     * ```kt
     * datastore.init(
     *   loadStrategy = LoadStrategy.Disabled,
     *   persistStrategy = PersistStrategy.Disabled,
     * )
     * ```
     *
     * In special contexts it can make sense to call this method more than once. In this case, ALL existing
     * in-memory values get overridden irreversible (regardless of success or failure of the request), so use
     * this with caution!
     *
     * @param loadStrategy The load strategy for interaction with storage.
     * @param persistStrategy The persist strategy for interaction with storage.
     * @return A result object. When this method returns, the request is guaranteed to have been completed,
     *  either with success or with provided failure exception. In any case, the model is usable after this
     *  method returns, however data loss may be occurring if loading fails but persisting to the same file
     *  in the storage succeeds.
     *
     * @since 0.5.0
     */
    suspend fun init(
        loadStrategy: LoadStrategy,
        persistStrategy: PersistStrategy,
        datastoreName: String,
        shouldPersist: Boolean,
    ): Result<Unit> {
        this.datastoreName = datastoreName
        this.shouldPersist = shouldPersist
        return init(loadStrategy, persistStrategy)
    }

    fun updatePreferences(ctx: Context, entries: List<Pair<PreferenceData<*>, String>>) {
        model.updatePreferences(entries = entries)
        runBlocking {
            initAndroid(ctx, datastoreName, shouldPersist)
        }
    }
}
