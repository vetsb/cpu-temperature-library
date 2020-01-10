package com.cpu.temperature.library

import android.content.Context
import android.content.SharedPreferences
import com.cpu.temperature.library.data.interactor.CpuTemperatureFilePathsInteractor
import com.cpu.temperature.library.data.interactor.CpuTemperatureFilePathsInteractorImpl
import com.cpu.temperature.library.data.repository.CpuTemperatureFilePathsRepository
import com.cpu.temperature.library.data.repository.CpuTemperatureFilePathsRepositoryImpl
import com.cpu.temperature.library.extenstion.round
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Provides CPU temperature.
 */
class CpuTemperatureProvider(
    context: Context
) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            "${context.packageName}.cpu_temperature_file_paths",
            Context.MODE_PRIVATE
        )
    }

    private val repository: CpuTemperatureFilePathsRepository by lazy {
        CpuTemperatureFilePathsRepositoryImpl(
            preferences
        )
    }

    private val filesPathsProvider: CpuTemperatureFilePathsProvider by lazy {
        CpuTemperatureFilePathsProvider()
    }

    private val interactor: CpuTemperatureFilePathsInteractor by lazy {
        CpuTemperatureFilePathsInteractorImpl(
            repository,
            filesPathsProvider
        )
    }

    /**
     * Num of the digits after dot in temperature.
     */
    var roundingAccuracy = 2

    private val paths = interactor.getPaths()

    companion object {
        private const val MINIMUM_TEMPERATURE = 10000
        private const val MAXIMUM_TEMPERATURE = 99999
    }

    /**
     * Returns cpu temperature.
     *
     * This method reads @param paths, filters values(these must be in MINIMUM_TEMPERATURE to MAXIMUM_TEMPERATURE interval)
     * and returns average value with rounding.
     */
    fun getTemperature(): Float {
        if (paths.isEmpty()) {
            return 0F
        }

        var temperature = paths.asSequence()
            .mapToProcess()
            .mapToContent()
            .mapNotNull { it.toIntOrNull() }
            .filter { it in MINIMUM_TEMPERATURE..MAXIMUM_TEMPERATURE }
            .average()

        if (temperature.isNaN()) {
            temperature = 0.0
        } else {
            temperature /= 1000
        }

        return temperature.toFloat().round(roundingAccuracy)
    }

    /**
     * Converts temperature file path to Runtime.exec("cat FILEPATH").
     */
    private fun Sequence<String>.mapToProcess(): Sequence<Process> {
        return map { Runtime.getRuntime().exec("cat $it") }
    }

    /**
     * Converts Sequence<Process> to Sequence of the files content.
     */
    private fun Sequence<Process>.mapToContent(): Sequence<String> {
        return map { BufferedReader(InputStreamReader(it.inputStream)) }
            .map {
                var str = it.readLine()
                val builder = StringBuilder()

                while (str != null) {
                    builder.append(str).append(" ")

                    str = it.readLine()
                }

                builder.toString().trim()
            }
    }
}
