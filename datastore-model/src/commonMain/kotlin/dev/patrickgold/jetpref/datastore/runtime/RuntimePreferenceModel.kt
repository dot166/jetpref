package dev.patrickgold.jetpref.datastore.runtime

import dev.patrickgold.jetpref.datastore.model.PreferenceData
import dev.patrickgold.jetpref.datastore.model.PreferenceModel

class RuntimePreferenceModel: PreferenceModel() {
    override var declaredPreferenceEntries: Map<TypedKey, PreferenceData<*>> = emptyMap()

    fun updatePreferences(entries: List<Pair<PreferenceData<*>, String>>) {
        val invalidEntries = entries
            .filter { it.first.key != it.second}
        if (invalidEntries.isNotEmpty()) {
            throw IllegalArgumentException("one or more entries is invalid: $invalidEntries")
        }
        val duplicates = entries
            .groupBy { (entry, _) -> entry.key }
            .filter { it.value.size > 1 }
            .mapValues { (_, duplicates) -> duplicates.map { (_, varName) -> varName } }
        if (duplicates.isNotEmpty()) {
            throw PreferenceModelDuplicateKeyException("dev.patrickgold.jetpref.datastore.runtime.RuntimePreferenceModel", duplicates)
        }
        declaredPreferenceEntries = entries
            .map { (entry, _) -> entry }
            .associateBy { TypedKey(it.type, it.key) }
    }
}
