package com.cpu.temperature.library

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Due to Android based on Linux we can get temperature from the system files.
 * The directory sys/class/thermal has many files that mean some temperature.
 * But we take only ones contains the "cpu" word in the title.
 * It's necessary for the clearer calculation.
 */
class CpuTemperatureFilePathsProvider {

    /**
     * Returns file paths in sys/class/thermal that contains CPU's cores temperature.
     */
    fun getPaths() = getThermalZones()
        .mapToThermalFiles()
        .toMapThermalFileToProcess()
        .toMapThermalFileToContent()
        .filterIsCpuCoreTemperature()
        .mapToTemperatureFiles()

    /**
     * Returns all directory titles in sys/class/thermal that contains thermal_zone in their titles.
     * These directories contain files that describe these thermal zone. For example, these have type, temperature, limits, mode etc.
     */
    private fun getThermalZones(): Sequence<String> {
        val process = Runtime.getRuntime().exec("ls sys/class/thermal")
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        var line = reader.readLine()
        val builder = StringBuilder()

        while (line != null) {
            builder.append(line).append(",")

            line = reader.readLine()
        }

        return builder.trim().split(",")
            .asSequence()
            .filter { it.isNotEmpty() }
            .filter { it.contains("thermal_zone") }
    }

    /**
     * Converts thermal_zoneN (N - 1, 2, 3...) to /sys/class/thermal/thermal_zoneN.
     */
    private fun Sequence<String>.mapToThermalFiles(): Sequence<String> {
        return map { "/sys/class/thermal/$it" }
    }

    /**
     * Converts /sys/class/thermal/thermal_zoneN to Map<String, Process>.
     * String - the directory path to thermal_zone.
     * Process - Runtime.exec("cat thermal_zoneN/type")
     */
    private fun Sequence<String>.toMapThermalFileToProcess(): Map<String, Process> {
        return map { it to Runtime.getRuntime().exec("cat $it/type") }
            .toMap()
    }

    /**
     * Converts Map<String, Process> to Map<String, String>.
     * The first string - the directory path to thermal_zone.
     * The second string - the type of the current thermal_zone.
     */
    private fun Map<String, Process>.toMapThermalFileToContent(): Map<String, String> {
        return map { it.key to BufferedReader(InputStreamReader(it.value.inputStream)) }
            .map {
                var str = it.second.readLine()
                val builder = StringBuilder()

                while (str != null) {
                    builder.append(str).append(" ")

                    str = it.second.readLine()
                }

                it.first to builder.toString().trim()
            }
            .toMap()
    }

    /**
     * Filters Map<String, String>. It's necessary that the type of the thermal_zone contains "cpu" word.
     */
    private fun Map<String, String>.filterIsCpuCoreTemperature(): Map<String, String> {
        return filter { it.value.contains("cpu") }
    }

    /**
     * Converts Map<String, String> to List<String>.
     *
     * @return The list of the file paths to the thermal_zone temperature value.
     */
    private fun Map<String, String>.mapToTemperatureFiles(): List<String> {
        return map { "${it.key}/temp" }
            .toList()
    }
}
