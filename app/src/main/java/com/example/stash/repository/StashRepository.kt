package com.example.stash.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.stash.graphql.*
import com.example.stash.graphql.type.FindFilterType
import com.example.stash.graphql.type.PerformerUpdateInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SceneItem(val id: String, val title: String, val thumbnail: String?, val streamUrl: String?, val duration: Double, val oCount: Int?, val rating: Int?)
data class ImageItem(val id: String, val title: String, val thumbnail: String?)
data class PerformerItem(val id: String, val name: String, val image: String?, val rating: Int?, val favorite: Boolean, val sceneCount: Int, val oCounter: Int?)
data class ServerStats(val totalScenes: Int, val totalImages: Int, val totalPerformers: Int, val totalPlaytime: Double, val totalOCount: Int)

class StashRepository(private val apollo: ApolloClient, private val baseUrl: String = "", private val apiKey: String = "") {
    private fun fullUrl(path: String?): String? {
        val result = if (path != null && baseUrl.isNotEmpty()) {
            val url = if (path.startsWith("http")) path else baseUrl.trimEnd('/') + path
            // Append API key as query parameter if not already present
            if (apiKey.isNotEmpty() && !url.contains("apikey=")) {
                val separator = if (url.contains("?")) "&" else "?"
                url + separator + "apikey=" + apiKey
            } else {
                url
            }
        } else path
        android.util.Log.d("StashRepository", "fullUrl: path=$path, baseUrl=$baseUrl, apiKey=${apiKey.take(4)}..., result=$result")
        return result
    }
    suspend fun newScenes(limit: Int = 20): List<SceneItem> = withContext(Dispatchers.IO) {
        val response = apollo.query(FindScenesQuery(filter = Optional.present(FindFilterType(per_page = Optional.present(limit), sort = Optional.present("created_at"), direction = Optional.present(com.example.stash.graphql.type.SortDirectionEnum.DESC))))).execute()
        response.data?.findScenes?.scenes?.map {
            SceneItem(
                id = it.id,
                title = it.title ?: "Untitled",
                thumbnail = fullUrl(it.paths?.screenshot),
                streamUrl = fullUrl(it.paths?.stream),
                duration = it.files.firstOrNull()?.duration ?: 0.0,
                oCount = it.o_counter,
                rating = it.rating100
            )
        } ?: emptyList()
    }

    suspend fun newPerformers(limit: Int = 20): List<PerformerItem> = withContext(Dispatchers.IO) {
        val response = apollo.query(FindPerformersQuery(filter = Optional.present(FindFilterType(per_page = Optional.present(limit), sort = Optional.present("created_at"), direction = Optional.present(com.example.stash.graphql.type.SortDirectionEnum.DESC))))).execute()
        response.data?.findPerformers?.performers?.map {
            PerformerItem(
                id = it.id,
                name = it.name ?: "Unknown",
                image = fullUrl(it.image_path),
                rating = it.rating100,
                favorite = it.favorite,
                sceneCount = it.scene_count,
                oCounter = it.o_counter
            )
        } ?: emptyList()
    }

    suspend fun newImages(limit: Int = 20): List<ImageItem> = withContext(Dispatchers.IO) {
        val response = apollo.query(FindImagesQuery(filter = Optional.present(FindFilterType(per_page = Optional.present(limit), sort = Optional.present("created_at"), direction = Optional.present(com.example.stash.graphql.type.SortDirectionEnum.DESC))))).execute()
        response.data?.findImages?.images?.map {
            ImageItem(
                id = it.id,
                title = it.title ?: "Untitled",
                thumbnail = fullUrl(it.paths?.thumbnail)
            )
        } ?: emptyList()
    }

    suspend fun continueWatching(): List<SceneItem> = withContext(Dispatchers.IO) {
        // Placeholder: return recently viewed scenes - would need play history tracking
        emptyList()
    }

    suspend fun reelsRandom(limit: Int = 50): List<SceneItem> = withContext(Dispatchers.IO) {
        val response = apollo.query(FindScenesQuery(filter = Optional.present(FindFilterType(per_page = Optional.present(limit), sort = Optional.present("random"))))).execute()
        response.data?.findScenes?.scenes?.map {
            SceneItem(
                id = it.id,
                title = it.title ?: "Untitled",
                thumbnail = fullUrl(it.paths?.screenshot),
                streamUrl = fullUrl(it.paths?.stream),
                duration = it.files.firstOrNull()?.duration ?: 0.0,
                oCount = it.o_counter,
                rating = it.rating100
            )
        } ?: emptyList()
    }

    suspend fun stats(): ServerStats = withContext(Dispatchers.IO) {
        val response = apollo.query(GetStatsQuery()).execute()
        val stats = response.data?.stats
        ServerStats(
            totalScenes = stats?.scene_count ?: 0,
            totalImages = stats?.image_count ?: 0,
            totalPerformers = stats?.performer_count ?: 0,
            totalPlaytime = stats?.total_play_duration ?: 0.0,
            totalOCount = stats?.total_o_count ?: 0
        )
    }

    suspend fun performerDetails(id: String): PerformerItem? = withContext(Dispatchers.IO) {
        val response = apollo.query(FindPerformerQuery(id)).execute()
        response.data?.findPerformer?.let {
            PerformerItem(
                id = it.id,
                name = it.name ?: "Unknown",
                image = fullUrl(it.image_path),
                rating = it.rating100,
                favorite = it.favorite,
                sceneCount = it.scene_count,
                oCounter = it.o_counter
            )
        }
    }

    suspend fun updatePerformer(id: String, rating: Int? = null, favorite: Boolean? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            apollo.mutation(PerformerUpdateMutation(PerformerUpdateInput(id = id, rating100 = Optional.presentIfNotNull(rating), favorite = Optional.presentIfNotNull(favorite)))).execute()
            true
        } catch (e: Exception) {
            false
        }
    }
}
