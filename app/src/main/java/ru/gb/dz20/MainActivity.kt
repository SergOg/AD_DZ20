package ru.gb.dz20

import android.graphics.PointF
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.SizeChangedListener
import com.yandex.runtime.image.ImageProvider
import ru.gb.dz20.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mapWindow: MapWindow
    private lateinit var map: Map
    private lateinit var placemarkMapObject: PlacemarkMapObject

    private val sizeChangedListener = SizeChangedListener { _, _, _ ->
        updateFocusInfo()
    }

    private val inputListener = object : InputListener {
        override fun onMapLongTap(map: Map, point: Point) {
            placemarkMapObject.geometry = point
        }

        override fun onMapTap(map: Map, point: Point) = Unit
    }

    private val geoObjectTapListener = GeoObjectTapListener {
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener true
        map.cameraPosition.run {
            val position = CameraPosition(point, zoom, azimuth, tilt)
            map.move(position, SMOOTH_ANIMATION, null)
        }
        val selectionMetadata =
            it.geoObject.metadataContainer.getItem(GeoObjectSelectionMetadata::class.java)
        map.selectGeoObject(selectionMetadata)
        Toast.makeText(
            this,
            "Tapped ${it.geoObject.name} id = ${selectionMetadata.objectId}",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapkitVersionTextView = binding.mapkitVersion.mapkitVersionValue
        mapkitVersionTextView.text = MapKitFactory.getInstance().version
        mapWindow = binding.mapview.mapWindow
        map = mapWindow.map
        mapWindow.addSizeChangedListener(sizeChangedListener)
        updateFocusInfo()
        map.addInputListener(inputListener)
        map.addTapListener(geoObjectTapListener)
        map.move(START_POSITION, START_ANIMATION) {
            Toast.makeText(this, "Initial camera move", Toast.LENGTH_SHORT).show()
        }
        createPlacemark(START_POSITION.target)
        drawResult()
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            buttonMinus.setOnClickListener { changeZoomByStep(-ZOOM_STEP) }
            buttonPlus.setOnClickListener { changeZoomByStep(ZOOM_STEP) }
            buttonLocation.setOnClickListener {
                val position = map.cameraPosition.run {
                    CameraPosition(placemarkMapObject.geometry, zoom, azimuth, tilt)
                }
                map.move(position, SMOOTH_ANIMATION, null)
            }
            buttonPin.setOnClickListener {
                throw Exception("My first exception does't work")


//                val focusPoint = mapWindow.focusPoint ?: return@setOnClickListener
//                val point = mapWindow.screenToWorld(focusPoint) ?: return@setOnClickListener
//                placemarkMapObject.geometry = point
            }
        }
    }

    private fun drawResult() {
        val imageProvider = ImageProvider.fromResource(this, R.drawable.search_result)
        for (i in POINTS.listIterator()) {
            map.mapObjects.addPlacemark().apply {
                geometry = i
                setIcon(imageProvider)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun createPlacemark(point: Point) {
        placemarkMapObject = map.mapObjects.addPlacemark().apply {
            geometry = point
            setIcon(
                ImageProvider.fromResource(this@MainActivity, R.drawable.ic_dollar_pin),
                IconStyle().apply { anchor = PointF(0.5f, 1.0f) })
            isDraggable = true
        }
    }

    private fun changeZoomByStep(value: Float) {
        with(map.cameraPosition) {
            map.move(
                CameraPosition(target, zoom + value, azimuth, tilt),
                SMOOTH_ANIMATION,
                null,
            )
        }
    }

    private fun updateFocusInfo() {
        val defaultPadding = resources.getDimension(R.dimen.default_focus_rect_padding)
        val bottomPadding = binding.layoutBottomCard.measuredHeight
        val rightPadding = binding.buttonMinus.measuredWidth
        mapWindow.focusRect = ScreenRect(
            ScreenPoint(defaultPadding, defaultPadding),
            ScreenPoint(
                mapWindow.width() - rightPadding - defaultPadding,
                mapWindow.height() - bottomPadding - defaultPadding,
            )
        )
        mapWindow.focusPoint = ScreenPoint(
            mapWindow.width() / 2f,
            mapWindow.height() / 2f,
        )
    }

    companion object {
        private const val ZOOM_STEP = 1f
        private val START_ANIMATION = Animation(Animation.Type.LINEAR, 1f)
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
        private val START_POSITION =
            CameraPosition(Point(55.753661, 37.619893), 15f, 0f, 0f)
        private val POINTS = listOf(
            Point(55.752492, 37.6231869),
            Point(55.755840, 37.6233107),
            Point(55.756896, 37.6217707),
            Point(55.760266, 37.6185306),
            Point(55.755367, 37.6178010),
            Point(55.751423, 37.6188716),
        )
    }
}