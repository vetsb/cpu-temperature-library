package com.cpu.temperature.library.data.interactor

import com.cpu.temperature.library.CpuTemperatureFilePathsProvider
import com.cpu.temperature.library.data.repository.CpuTemperatureFilePathsRepository

/**
 * The CpuTemperatureFilePathsInteractor's implementation.
 */
class CpuTemperatureFilePathsInteractorImpl(
    private val repository: CpuTemperatureFilePathsRepository,
    private val filePathsProvider: CpuTemperatureFilePathsProvider
) : CpuTemperatureFilePathsInteractor {

    /**
     * Tries to get local paths. If there is nothing it gets
     * these from CpuTemperatureFilePathsProvider, saves it and returns.
     */
    override fun getPaths(): List<String> {
        var paths = repository.getPaths()

        if (paths == null) {
            paths = filePathsProvider.getPaths()

            setPaths(paths)
        }

        return paths
    }

    override fun setPaths(list: List<String>?) {
        repository.setPaths(list)
    }
}