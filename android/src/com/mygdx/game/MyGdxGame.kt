package com.mygdx.game

import android.graphics.Color.parseColor
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import javax.microedition.khronos.opengles.GL10
import com.badlogic.gdx.graphics.Color as Color

class LibGDX3DTest: ApplicationAdapter() {
    private lateinit var camera: PerspectiveCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var cubeModel: Model
    private lateinit var cubeModelInstance: ModelInstance
    private lateinit var environment: Environment

    override fun create() {
        super.create()
        createCamera()
        adjustCameraPosition()
        adjustCameraRange()
        createModelBatch()
        createCubeModel()
        createCubeModelInstance()
        createEnvironment()
    }

    override fun render() {
        super.render()
        clearScreen()
//        spinCamera()
        updateCamera()
        renderModelBatch()
    }

    override fun dispose() {
        super.dispose()
        disposeModelBatch()
        disposeCubeModel()
    }

    // Utils - Create

    private fun createCamera() {
        camera = PerspectiveCamera(75f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }

    private fun adjustCameraPosition() {
        camera.position?.set(4f, 1.5f, 3f)
        camera.lookAt(0f,0f,0f)
    }

    private fun adjustCameraRange() {
        camera.near = 0.1f
        camera.far = 300f
    }

    private fun createModelBatch() {
        modelBatch = ModelBatch()
    }

    private fun createCubeModel() {
        val modelBuilder = ModelBuilder()
        val attributes = (Usage.Position or Usage.Normal).toLong()
        val material = Material(ColorAttribute.createDiffuse(Color(parseColor("#FA9F93FF"))))
        cubeModel = modelBuilder.createBox(8f, .3f, 3f, material, attributes)
    }

    private fun createCubeModelInstance() {
        cubeModelInstance = ModelInstance(cubeModel, -5f, -1.5f, -3f)
    }

    private fun createEnvironment() {
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 100f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, 0.5f))
    }

    // Utils - render

    private fun clearScreen() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, -100f)
        val GL_DEPTH_BUFFER_BIT = 256
        val GL_COLOR_BUFFER_BIT = 16384
        Gdx.gl.glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
    }

//    private fun spinCamera() {
//        val axis = Vector3(0.25f, 1f, 0.5f)
//        camera.rotateAround(Vector3.Zero, axis, 1f)
//    }

    private fun updateCamera() {
        camera.update()
    }

    private fun renderModelBatch() {
        modelBatch.begin(camera)
        modelBatch.render(cubeModelInstance, environment)
        modelBatch.end()
    }

    // Utils - Dispose

    private fun disposeModelBatch() {
        modelBatch.dispose()
    }

    private fun disposeCubeModel() {
        cubeModel.dispose()
    }
}