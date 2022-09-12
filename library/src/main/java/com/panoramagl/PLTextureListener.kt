package com.panoramagl

interface PLTextureListener {

    fun didLoad(texture: PLITexture?)
    fun didError(e: Throwable)
}
