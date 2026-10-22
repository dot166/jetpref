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
import dev.patrickgold.jetpref.datastore.annotations.PreferenceKey
import dev.patrickgold.jetpref.datastore.model.BooleanPreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.DoublePreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.FloatPreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.IntPreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.LocalTime
import dev.patrickgold.jetpref.datastore.model.LocalTimePreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.LongPreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.PreferenceData
import dev.patrickgold.jetpref.datastore.model.PreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.PreferenceType
import dev.patrickgold.jetpref.datastore.model.StringPreferenceSerializer
import dev.patrickgold.jetpref.datastore.model.preferenceDataOf
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

    /**
     * Declare a `boolean` preference entry. Its [key] must be unique within this preference model.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.1.0
     */
    fun boolean(
        @PreferenceKey key: String,
        default: Boolean,
    ): PreferenceData<Boolean> {
        return preferenceDataOf(
            key = key,
            default = default,
            type = PreferenceType.boolean(),
            serializer = BooleanPreferenceSerializer,
        )
    }

    /**
     * Declare a `double` preference entry. Its [key] must be unique within this preference model.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.1.0
     */
    fun double(
        @PreferenceKey key: String,
        default: Double,
    ): PreferenceData<Double> {
        return preferenceDataOf(
            key = key,
            default = default,
            type = PreferenceType.double(),
            serializer = DoublePreferenceSerializer,
        )
    }

    /**
     * Declare a `float` preference entry. Its [key] must be unique within this preference model.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.1.0
     */
    fun float(
        @PreferenceKey key: String,
        default: Float,
    ): PreferenceData<Float> {
        return preferenceDataOf(
            key = key,
            default = default,
            type = PreferenceType.float(),
            serializer = FloatPreferenceSerializer,
        )
    }

    /**
     * Declare an `int` preference entry. Its [key] must be unique within this preference model.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.1.0
     */
    fun int(
        @PreferenceKey key: String,
        default: Int,
    ): PreferenceData<Int> {
        return preferenceDataOf(
            key = key,
            default = default,
            type = PreferenceType.integer(),
            serializer = IntPreferenceSerializer,
        )
    }

    /**
     * Declare a `long` preference entry. Its [key] must be unique within this preference model.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.1.0
     */
    fun long(
        @PreferenceKey key: String,
        default: Long,
    ): PreferenceData<Long> {
        return preferenceDataOf(
            key = key,
            default = default,
            type = PreferenceType.long(),
            serializer = LongPreferenceSerializer,
        )
    }

    /**
     * Declare a `string` preference entry. Its [key] must be unique within this preference model.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.1.0
     */
    fun string(
        @PreferenceKey key: String,
        default: String,
    ): PreferenceData<String> {
        return preferenceDataOf(
            key = key,
            default = default,
            type = PreferenceType.string(),
            serializer = StringPreferenceSerializer,
        )
    }

    /**
     * Declare a custom preference entry. Its [key] must be unique within this preference model.
     *
     * In addition to the [key] and [default] value, this entry accepts a custom serializer. This allows
     * for any type to be persisted in the preference model. The custom type must be serializable to a
     * string with the given [serializer].
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     * @param serializer The serializer for the custom data type.
     *
     * @since 0.1.0
     */
    fun <V : Any> custom(
        @PreferenceKey key: String,
        default: V,
        serializer: PreferenceSerializer<V>,
    ): PreferenceData<V> {
        return preferenceDataOf(
            key = key,
            default = default,
            type = PreferenceType.string(),
            serializer = serializer,
        )
    }

    /**
     * Declare a `enum` preference entry. Its [key] must be unique within this preference model.
     *
     * Enum values will internally be represented as a string, using the constant name as the value.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.1.0
     */
    inline fun <reified V : Enum<V>> enum(
        @PreferenceKey key: String,
        default: V,
    ): PreferenceData<V> {
        val serializer = object : PreferenceSerializer<V> {
            override fun serialize(value: V): String {
                return value.toString()
            }

            override fun deserialize(value: String): V? {
                return try {
                    enumValueOf<V>(value)
                } catch (_: Exception) {
                    null
                }
            }
        }
        return custom(key, default, serializer)
    }

    /**
     * Declare a `local time` preference entry. Its [key] must be unique within this preference model.
     *
     * Local time values will internally be represented as a string.
     *
     * @param key The key of the preference, used for uniquely identifying this entry.
     * @param default The default value of this entry.
     *
     * @since 0.2.0
     */
    fun localTime(
        @PreferenceKey key: String,
        default: LocalTime,
    ): PreferenceData<LocalTime> {
        return custom(key, default, LocalTimePreferenceSerializer)
    }
}
