package com.cpu.temperature.library.data.repository

/**
 * The repository of the cpu temperature file paths.
 * It's needed for saving file paths locally. Because these paths never changes and
 * we don't need get these again and again.
 */
interface CpuTemperatureFilePathsRepository {

    /**
     * Getter
     */
    fun getPaths(): List<String>?

    /**
     * Setter
     */
    fun setPaths(list: List<String>?)
}