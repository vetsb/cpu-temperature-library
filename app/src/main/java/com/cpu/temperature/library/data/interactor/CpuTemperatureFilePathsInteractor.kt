package com.cpu.temperature.library.data.interactor

/**
 * Provides file paths from the repository
 */
interface CpuTemperatureFilePathsInteractor {

    /**
     * Getter
     */
    fun getPaths(): List<String>

    /**
     * Setter
     */
    fun setPaths(list: List<String>?)
}