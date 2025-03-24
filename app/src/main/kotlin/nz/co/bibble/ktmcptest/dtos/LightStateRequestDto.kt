
package nz.co.bibble.ktmcptest.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LightStateRequestDto(
    val on: OnState? = null,
    val color: ColorState? = null,
    val dimming: DimmingState? = null,
)

@Serializable data class OnState(val on: Boolean)

@Serializable
data class ColorState(
    val xy: XYCoordinates,
)

@Serializable data class XYCoordinates(val x: Double, val y: Double)

@Serializable data class DimmingState(val brightness: Double)
