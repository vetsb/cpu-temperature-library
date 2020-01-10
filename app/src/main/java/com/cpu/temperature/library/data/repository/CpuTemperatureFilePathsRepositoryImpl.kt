package com.cpu.temperature.library.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * The CpuTemperatureFilePathsRepository's implementation.
 */
class CpuTemperatureFilePathsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : CpuTemperatureFilePathsRepository {

    companion object {
        private const val TEMPERATURE_FILE_PATHS_KEY = "TEMPERATURE_FILE_PATHS"
    }

    private var listType = object : TypeToken<List<String?>?>() {}.type

    private val gson: Gson by lazy {
        Gson()
    }

    /**
     * Gets the local value and tries to transform this to the object from json.
     */
    override fun getPaths(): List<String>? {
        val json = sharedPreferences.getString(TEMPERATURE_FILE_PATHS_KEY, null) ?: return null

        return try {
            gson.fromJson(json, listType)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    /**
     * Tries to transform the value to the json and saves this locally.
     */
    override fun setPaths(list: List<String>?) {
        val json = try {
            gson.toJson(list, listType)
        } catch (e: JsonIOException) {
            null
        }

        sharedPreferences
            .edit()
            .putString(TEMPERATURE_FILE_PATHS_KEY, json)
            .apply()
    }
}