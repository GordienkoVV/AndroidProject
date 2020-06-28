package com.tracking.observer.data

import android.content.Context
import androidx.core.content.ContextCompat
import com.tracking.observer.R
import com.tracking.observer.domain.RouteDataSource
import com.tracking.observer.domain.data.RouteWay
import io.reactivex.Single

class RouteDataSourceImpl(
    private val context: Context
): RouteDataSource {

    private val nameMap = mapOf(
            Pair("1", R.string.route_1),
            Pair("2", R.string.route_2),
            Pair("3", R.string.route_3),
            Pair("15", R.string.route_15)
    )

    private val routeNames = context.resources.getStringArray(R.array.route_numbers).toList()

    private val colorIdList = listOf(
            R.color.colorRoute1,
            R.color.colorRoute1a,
            R.color.colorRoute2,
            R.color.colorRoute3,
            R.color.colorRoute4,
            R.color.colorRoute4a,
            R.color.colorRoute5,
            R.color.colorRoute5a,
            R.color.colorRoute6,
            R.color.colorRoute7,
            R.color.colorRoute7a,
            R.color.colorRoute8,
            R.color.colorRoute9,
            R.color.colorRoute9a,
            R.color.colorRoute10,
            R.color.colorRoute11,
            R.color.colorRoute12,
            R.color.colorRoute12a,
            R.color.colorRoute14,
            R.color.colorRoute14a,
            R.color.colorRoute15,
            R.color.colorRoute16,
            R.color.colorRoute19,
            R.color.colorRoute19a,
            R.color.colorRoute20,
            R.color.colorRoute21,
            R.color.colorRoute22
    )

    override fun getRouteNames(): Single<List<String>> {
        return Single.fromCallable { routeNames }
    }

    override fun getRoutes(names: List<String>): Single<List<RouteWay>> {
        return Single.fromCallable {
            val keys = if (names.isNotEmpty()) names else nameMap.keys.toList()
            keys.map { RouteWay(
                    name = it,
                    route = nameMap[it]?.let(context::getString) ?: "",
                    color = getColor(it)
            ) }
        }
    }

    private fun getColor(name: String): Int {
        val index = routeNames.indexOf(name)
        val colorResId = colorIdList.getOrNull(index)
                ?: colorIdList.first()
        return ContextCompat.getColor(context, colorResId)
    }

}
