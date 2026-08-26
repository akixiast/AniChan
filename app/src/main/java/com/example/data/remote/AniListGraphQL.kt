package com.example.data.remote

import com.example.data.model.AiringScheduleItem
import com.example.data.model.AniListUserProfile
import com.example.data.model.CharacterItem
import com.example.data.model.MediaItem
import com.example.data.model.MediaRecommendation
import com.example.data.model.MediaRelation
import com.example.data.model.MediaStatus
import com.example.data.model.MediaType
import com.example.data.model.ReviewItem
import com.example.data.model.StaffItem
import com.example.data.model.UserMediaEntry
import org.json.JSONArray
import org.json.JSONObject

object AniListGraphQL {

    const val BASE_URL = "https://graphql.anilist.co/"

    val USER_BY_NAME_QUERY = """
        query (${'$'}userName: String) {
          User(name: ${'$'}userName) {
            id
            name
            about
            avatar {
              large
              medium
            }
            bannerImage
            statistics {
              anime {
                count
                meanScore
                minutesWatched
                episodesWatched
              }
              manga {
                count
                meanScore
                chaptersRead
                volumesRead
              }
            }
          }
        }
    """.trimIndent()

    val USER_SEARCH_QUERY = """
        query (${'$'}search: String) {
          User(search: ${'$'}search) {
            id
            name
            about
            avatar {
              large
              medium
            }
            bannerImage
            statistics {
              anime {
                count
                meanScore
                minutesWatched
                episodesWatched
              }
              manga {
                count
                meanScore
                chaptersRead
                volumesRead
              }
            }
          }
        }
    """.trimIndent()

    val VIEWER_QUERY = """
        query {
          Viewer {
            id
            name
            about
            avatar {
              large
              medium
            }
            bannerImage
            statistics {
              anime {
                count
                meanScore
                minutesWatched
                episodesWatched
              }
              manga {
                count
                meanScore
                chaptersRead
                volumesRead
              }
            }
          }
        }
    """.trimIndent()

    val MEDIA_LIST_COLLECTION_QUERY = """
        query (${'$'}userId: Int, ${'$'}userName: String, ${'$'}type: MediaType) {
          MediaListCollection(userId: ${'$'}userId, userName: ${'$'}userName, type: ${'$'}type) {
            lists {
              name
              isCustomList
              status
              entries {
                id
                mediaId
                status
                score(format: POINT_10_DECIMAL)
                progress
                progressVolumes
                repeat
                notes
                updatedAt
                media {
                  id
                  idMal
                  title {
                    romaji
                    english
                    native
                  }
                  type
                  format
                  status
                  episodes
                  chapters
                  volumes
                  coverImage {
                    extraLarge
                    large
                    medium
                  }
                  bannerImage
                  genres
                  averageScore
                }
              }
            }
          }
        }
    """.trimIndent()

    val SAVE_MEDIA_LIST_ENTRY_MUTATION = """
        mutation (${'$'}mediaId: Int, ${'$'}status: MediaListStatus, ${'$'}score: Float, ${'$'}progress: Int, ${'$'}progressVolumes: Int, ${'$'}notes: String, ${'$'}repeat: Int) {
          SaveMediaListEntry(mediaId: ${'$'}mediaId, status: ${'$'}status, score: ${'$'}score, progress: ${'$'}progress, progressVolumes: ${'$'}progressVolumes, notes: ${'$'}notes, repeat: ${'$'}repeat) {
            id
            mediaId
            status
            score
            progress
            progressVolumes
            updatedAt
          }
        }
    """.trimIndent()

    val DELETE_MEDIA_LIST_ENTRY_MUTATION = """
        mutation (${'$'}id: Int) {
          DeleteMediaListEntry(id: ${'$'}id) {
            deleted
          }
        }
    """.trimIndent()

    val TRENDING_QUERY = """
        query (${'$'}page: Int, ${'$'}perPage: Int, ${'$'}type: MediaType) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            pageInfo {
              total
              currentPage
              hasNextPage
            }
            media(type: ${'$'}type, sort: [TRENDING_DESC, POPULARITY_DESC]) {
              id
              idMal
              title {
                romaji
                english
                native
              }
              type
              format
              status
              description
              season
              seasonYear
              episodes
              duration
              chapters
              volumes
              coverImage {
                extraLarge
                large
                medium
              }
              bannerImage
              genres
              averageScore
              meanScore
              popularity
              trending
              favourites
              studios(isMain: true) {
                nodes {
                  name
                }
              }
              nextAiringEpisode {
                airingAt
                timeUntilAiring
                episode
              }
            }
          }
        }
    """.trimIndent()

    val SEASONAL_QUERY = """
        query (${'$'}season: MediaSeason, ${'$'}seasonYear: Int, ${'$'}page: Int, ${'$'}perPage: Int) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            pageInfo {
              hasNextPage
            }
            media(season: ${'$'}season, seasonYear: ${'$'}seasonYear, type: ANIME, sort: [POPULARITY_DESC]) {
              id
              idMal
              title {
                romaji
                english
                native
              }
              type
              format
              status
              description
              season
              seasonYear
              episodes
              duration
              coverImage {
                extraLarge
                large
                medium
              }
              bannerImage
              genres
              averageScore
              popularity
              studios(isMain: true) {
                nodes {
                  name
                }
              }
              nextAiringEpisode {
                airingAt
                timeUntilAiring
                episode
              }
            }
          }
        }
    """.trimIndent()

    val POPULAR_QUERY = """
        query (${'$'}page: Int, ${'$'}perPage: Int, ${'$'}type: MediaType) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            media(type: ${'$'}type, sort: [SCORE_DESC, POPULARITY_DESC]) {
              id
              idMal
              title {
                romaji
                english
                native
              }
              type
              format
              status
              description
              seasonYear
              episodes
              chapters
              coverImage {
                extraLarge
                large
                medium
              }
              bannerImage
              genres
              averageScore
              popularity
              studios(isMain: true) {
                nodes {
                  name
                }
              }
            }
          }
        }
    """.trimIndent()

    val SEARCH_QUERY = """
        query (${'$'}search: String, ${'$'}type: MediaType, ${'$'}genre: String, ${'$'}tag: String, ${'$'}season: MediaSeason, ${'$'}seasonYear: Int, ${'$'}format: MediaFormat, ${'$'}status: MediaStatus, ${'$'}countryOfOrigin: CountryCode, ${'$'}sort: [MediaSort], ${'$'}page: Int, ${'$'}perPage: Int) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            pageInfo {
              total
              currentPage
              hasNextPage
              lastPage
            }
            media(search: ${'$'}search, type: ${'$'}type, genre: ${'$'}genre, tag: ${'$'}tag, season: ${'$'}season, seasonYear: ${'$'}seasonYear, format: ${'$'}format, status: ${'$'}status, countryOfOrigin: ${'$'}countryOfOrigin, sort: ${'$'}sort) {
              id
              idMal
              title {
                romaji
                english
                native
              }
              type
              format
              status
              description
              season
              seasonYear
              episodes
              chapters
              volumes
              duration
              coverImage {
                extraLarge
                large
                medium
              }
              bannerImage
              genres
              averageScore
              meanScore
              popularity
              favourites
              trending
              studios(isMain: true) {
                nodes {
                  name
                }
              }
              nextAiringEpisode {
                airingAt
                timeUntilAiring
                episode
              }
            }
          }
        }
    """.trimIndent()

    val AIRING_SCHEDULE_QUERY = """
        query (${'$'}airingAtGreater: Int, ${'$'}airingAtLesser: Int, ${'$'}page: Int, ${'$'}perPage: Int) {
          Page(page: ${'$'}page, perPage: ${'$'}perPage) {
            airingSchedules(airingAt_greater: ${'$'}airingAtGreater, airingAt_lesser: ${'$'}airingAtLesser, sort: TIME) {
              id
              airingAt
              timeUntilAiring
              episode
              media {
                id
                idMal
                title {
                  romaji
                  english
                  native
                }
                type
                format
                status
                episodes
                coverImage {
                  extraLarge
                  large
                  medium
                }
                bannerImage
                genres
                averageScore
                studios(isMain: true) {
                  nodes {
                    name
                  }
                }
              }
            }
          }
        }
    """.trimIndent()

    val DETAILS_QUERY = """
        query (${'$'}id: Int) {
          Media(id: ${'$'}id) {
            id
            idMal
            title {
              romaji
              english
              native
            }
            type
            format
            status
            description
            season
            seasonYear
            episodes
            duration
            chapters
            volumes
            source
            trailer {
              id
              site
            }
            coverImage {
              extraLarge
              large
              medium
            }
            bannerImage
            genres
            averageScore
            meanScore
            popularity
            favourites
            trending
            studios {
              nodes {
                name
                isAnimationStudio
              }
            }
            nextAiringEpisode {
              airingAt
              timeUntilAiring
              episode
            }
            characters(sort: [ROLE, RELEVANCE], perPage: 12) {
              edges {
                role
                node {
                  id
                  name {
                    full
                    native
                  }
                  image {
                    large
                    medium
                  }
                }
                voiceActors(language: JAPANESE, sort: [RELEVANCE]) {
                  id
                  name {
                    full
                  }
                  image {
                    large
                    medium
                  }
                  languageV2
                }
              }
            }
            staff(perPage: 6) {
              edges {
                role
                node {
                  id
                  name {
                    full
                  }
                  image {
                    medium
                  }
                }
              }
            }
            relations {
              edges {
                relationType
                node {
                  id
                  title {
                    romaji
                    english
                  }
                  type
                  format
                  status
                  coverImage {
                    medium
                    large
                  }
                  averageScore
                }
              }
            }
            recommendations(perPage: 6, sort: [RATING_DESC]) {
              nodes {
                id
                rating
                mediaRecommendation {
                  id
                  title {
                    romaji
                    english
                  }
                  type
                  format
                  coverImage {
                    medium
                    large
                  }
                  averageScore
                }
              }
            }
            reviews(perPage: 4, sort: [RATING_DESC]) {
              nodes {
                id
                summary
                score
                user {
                  name
                  avatar {
                    medium
                  }
                }
              }
            }
          }
        }
    """.trimIndent()

    fun parseMediaItem(json: JSONObject): MediaItem {
        val id = json.optInt("id", 0)
        val idMal = if (json.has("idMal") && !json.isNull("idMal")) json.optInt("idMal") else null
        val titleObj = json.optJSONObject("title")
        val titleRomaji = titleObj?.optString("romaji", "") ?: ""
        val titleEnglish = titleObj?.optString("english", null)
        val titleNative = titleObj?.optString("native", null)

        val typeStr = json.optString("type", "ANIME")
        val type = if (typeStr == "MANGA") MediaType.MANGA else MediaType.ANIME

        val format = json.optString("format", "TV")
        val statusStr = json.optString("status", "UNKNOWN")
        val status = try {
            MediaStatus.valueOf(statusStr)
        } catch (e: Exception) {
            MediaStatus.UNKNOWN
        }

        val description = json.optString("description", "")
        val season = if (json.has("season") && !json.isNull("season")) json.optString("season") else null
        val seasonYear = if (json.has("seasonYear") && !json.isNull("seasonYear")) json.optInt("seasonYear") else null
        val episodes = if (json.has("episodes") && !json.isNull("episodes")) json.optInt("episodes") else null
        val duration = if (json.has("duration") && !json.isNull("duration")) json.optInt("duration") else null
        val chapters = if (json.has("chapters") && !json.isNull("chapters")) json.optInt("chapters") else null
        val volumes = if (json.has("volumes") && !json.isNull("volumes")) json.optInt("volumes") else null

        val coverObj = json.optJSONObject("coverImage")
        val coverExtraLarge = coverObj?.optString("extraLarge", "") ?: ""
        val coverLarge = coverObj?.optString("large", "") ?: ""
        val coverMedium = coverObj?.optString("medium", "") ?: ""

        val bannerImage = if (json.has("bannerImage") && !json.isNull("bannerImage")) json.optString("bannerImage") else null

        val genresList = mutableListOf<String>()
        val genresArr = json.optJSONArray("genres")
        if (genresArr != null) {
            for (i in 0 until genresArr.length()) {
                genresList.add(genresArr.optString(i))
            }
        }

        val avgScore = if (json.has("averageScore") && !json.isNull("averageScore")) json.optInt("averageScore") else null
        val meanScore = if (json.has("meanScore") && !json.isNull("meanScore")) json.optInt("meanScore") else null
        val popularity = json.optInt("popularity", 0)
        val favourites = json.optInt("favourites", 0)
        val trending = json.optInt("trending", 0)
        val source = if (json.has("source") && !json.isNull("source")) json.optString("source") else null

        val trailerObj = json.optJSONObject("trailer")
        val trailerId = trailerObj?.optString("id", null)
        val trailerSite = trailerObj?.optString("site", null)

        val studiosList = mutableListOf<String>()
        val studiosObj = json.optJSONObject("studios")
        val studioNodes = studiosObj?.optJSONArray("nodes")
        if (studioNodes != null) {
            for (i in 0 until studioNodes.length()) {
                val s = studioNodes.optJSONObject(i)?.optString("name")
                if (!s.isNullOrBlank()) studiosList.add(s)
            }
        }

        val nextAiringObj = json.optJSONObject("nextAiringEpisode")
        val nextEpisode = nextAiringObj?.optInt("episode")
        val nextTimeUntil = nextAiringObj?.optLong("timeUntilAiring")

        // Parse characters
        val charactersList = mutableListOf<CharacterItem>()
        val charactersObj = json.optJSONObject("characters")
        val charEdges = charactersObj?.optJSONArray("edges")
        if (charEdges != null) {
            for (i in 0 until charEdges.length()) {
                val edge = charEdges.optJSONObject(i) ?: continue
                val role = edge.optString("role", "MAIN")
                val node = edge.optJSONObject("node") ?: continue
                val charId = node.optInt("id", 0)
                val nameObj = node.optJSONObject("name")
                val charName = nameObj?.optString("full", "Unknown") ?: "Unknown"
                val charNative = nameObj?.optString("native")
                val imgObj = node.optJSONObject("image")
                val imgLarge = imgObj?.optString("large", "") ?: ""
                val imgMed = imgObj?.optString("medium", "") ?: ""

                var vaName: String? = null
                var vaImg: String? = null
                var vaLang: String? = null
                val vaArr = edge.optJSONArray("voiceActors")
                if (vaArr != null && vaArr.length() > 0) {
                    val vaObj = vaArr.optJSONObject(0)
                    vaName = vaObj?.optJSONObject("name")?.optString("full")
                    vaImg = vaObj?.optJSONObject("image")?.optString("medium")
                    vaLang = vaObj?.optString("languageV2", "Japanese")
                }

                charactersList.add(
                    CharacterItem(
                        id = charId,
                        nameFull = charName,
                        nameNative = charNative,
                        role = role,
                        imageLarge = imgLarge,
                        imageMedium = imgMed,
                        voiceActorName = vaName,
                        voiceActorImage = vaImg,
                        voiceActorLanguage = vaLang
                    )
                )
            }
        }

        // Parse staff
        val staffList = mutableListOf<StaffItem>()
        val staffObj = json.optJSONObject("staff")
        val staffEdges = staffObj?.optJSONArray("edges")
        if (staffEdges != null) {
            for (i in 0 until staffEdges.length()) {
                val edge = staffEdges.optJSONObject(i) ?: continue
                val role = edge.optString("role", "")
                val node = edge.optJSONObject("node") ?: continue
                val staffId = node.optInt("id", 0)
                val name = node.optJSONObject("name")?.optString("full", "") ?: ""
                val img = node.optJSONObject("image")?.optString("medium", "") ?: ""
                if (name.isNotBlank()) {
                    staffList.add(StaffItem(id = staffId, nameFull = name, role = role, imageMedium = img))
                }
            }
        }

        // Parse relations
        val relationsList = mutableListOf<MediaRelation>()
        val relationsObj = json.optJSONObject("relations")
        val relEdges = relationsObj?.optJSONArray("edges")
        if (relEdges != null) {
            for (i in 0 until relEdges.length()) {
                val edge = relEdges.optJSONObject(i) ?: continue
                val relType = edge.optString("relationType", "OTHER")
                val node = edge.optJSONObject("node") ?: continue
                val relMedia = parseMediaItem(node)
                relationsList.add(MediaRelation(id = relMedia.id, relationType = relType, media = relMedia))
            }
        }

        // Parse recommendations
        val recList = mutableListOf<MediaRecommendation>()
        val recObj = json.optJSONObject("recommendations")
        val recNodes = recObj?.optJSONArray("nodes")
        if (recNodes != null) {
            for (i in 0 until recNodes.length()) {
                val node = recNodes.optJSONObject(i) ?: continue
                val recId = node.optInt("id", 0)
                val rating = node.optInt("rating", 0)
                val mediaRec = node.optJSONObject("mediaRecommendation") ?: continue
                val recMedia = parseMediaItem(mediaRec)
                recList.add(MediaRecommendation(id = recId, rating = rating, media = recMedia))
            }
        }

        // Parse reviews
        val reviewList = mutableListOf<ReviewItem>()
        val revObj = json.optJSONObject("reviews")
        val revNodes = revObj?.optJSONArray("nodes")
        if (revNodes != null) {
            for (i in 0 until revNodes.length()) {
                val node = revNodes.optJSONObject(i) ?: continue
                val revId = node.optInt("id", 0)
                val summary = node.optString("summary", "")
                val score = node.optInt("score", 0)
                val user = node.optJSONObject("user")
                val userName = user?.optString("name", "AniList User") ?: "AniList User"
                val avatar = user?.optJSONObject("avatar")?.optString("medium", "") ?: ""
                reviewList.add(ReviewItem(id = revId, summary = summary, score = score, userName = userName, userAvatar = avatar))
            }
        }

        return MediaItem(
            id = id,
            idMal = idMal,
            titleRomaji = titleRomaji,
            titleEnglish = titleEnglish,
            titleNative = titleNative,
            type = type,
            format = format,
            status = status,
            description = description,
            season = season,
            seasonYear = seasonYear,
            episodes = episodes,
            duration = duration,
            chapters = chapters,
            volumes = volumes,
            coverImageExtraLarge = coverExtraLarge,
            coverImageLarge = coverLarge.ifBlank { coverExtraLarge },
            coverImageMedium = coverMedium.ifBlank { coverLarge },
            bannerImage = bannerImage,
            genres = genresList,
            averageScore = avgScore,
            meanScore = meanScore,
            popularity = popularity,
            favourites = favourites,
            trending = trending,
            studios = studiosList,
            source = source,
            trailerId = trailerId,
            trailerSite = trailerSite,
            nextAiringEpisode = nextEpisode,
            nextAiringTimeUntilAiring = nextTimeUntil,
            characters = charactersList,
            staff = staffList,
            relations = relationsList,
            recommendations = recList,
            reviews = reviewList
        )
    }

    fun parseSearchPage(jsonResponse: String): com.example.data.model.SearchResult {
        val list = mutableListOf<MediaItem>()
        var hasNextPage = false
        var total = 0
        var currentPage = 1
        try {
            val root = JSONObject(jsonResponse)
            val data = root.optJSONObject("data")
            val page = data?.optJSONObject("Page")
            if (page != null) {
                val pageInfo = page.optJSONObject("pageInfo")
                hasNextPage = pageInfo?.optBoolean("hasNextPage", false) ?: false
                total = pageInfo?.optInt("total", 0) ?: 0
                currentPage = pageInfo?.optInt("currentPage", 1) ?: 1

                val mediaArr = page.optJSONArray("media")
                if (mediaArr != null) {
                    for (i in 0 until mediaArr.length()) {
                        val itemJson = mediaArr.optJSONObject(i) ?: continue
                        list.add(parseMediaItem(itemJson))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return com.example.data.model.SearchResult(
            items = list,
            hasNextPage = hasNextPage,
            totalCount = total,
            currentPage = currentPage
        )
    }

    fun parseMediaList(jsonResponse: String): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        try {
            val root = JSONObject(jsonResponse)
            val data = root.optJSONObject("data") ?: return emptyList()
            val page = data.optJSONObject("Page") ?: return emptyList()
            val mediaArr = page.optJSONArray("media") ?: return emptyList()
            for (i in 0 until mediaArr.length()) {
                val itemJson = mediaArr.optJSONObject(i) ?: continue
                list.add(parseMediaItem(itemJson))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun parseSingleMedia(jsonResponse: String): MediaItem? {
        return try {
            val root = JSONObject(jsonResponse)
            val data = root.optJSONObject("data") ?: return null
            val mediaObj = data.optJSONObject("Media") ?: return null
            parseMediaItem(mediaObj)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseAiringSchedule(jsonResponse: String): List<AiringScheduleItem> {
        val list = mutableListOf<AiringScheduleItem>()
        try {
            val root = JSONObject(jsonResponse)
            val data = root.optJSONObject("data") ?: return emptyList()
            val page = data.optJSONObject("Page") ?: return emptyList()
            val schedulesArr = page.optJSONArray("airingSchedules") ?: return emptyList()
            for (i in 0 until schedulesArr.length()) {
                val s = schedulesArr.optJSONObject(i) ?: continue
                val id = s.optInt("id", 0)
                val episode = s.optInt("episode", 0)
                val airingAt = s.optLong("airingAt", 0L)
                val timeUntilAiring = s.optLong("timeUntilAiring", 0L)
                val mediaObj = s.optJSONObject("media") ?: continue
                val media = parseMediaItem(mediaObj)
                list.add(AiringScheduleItem(id = id, episode = episode, airingAt = airingAt, timeUntilAiring = timeUntilAiring, media = media))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun parseUserProfile(jsonResponse: String): AniListUserProfile? {
        return try {
            val root = JSONObject(jsonResponse)
            val data = root.optJSONObject("data") ?: return null
            val user = data.optJSONObject("Viewer") ?: data.optJSONObject("User") ?: return null
            val id = user.optInt("id", 0)
            val name = user.optString("name", "")
            val about = user.optString("about", "")
            val avatar = user.optJSONObject("avatar")
            val avatarUrl = avatar?.optString("large") ?: avatar?.optString("medium") ?: ""
            val bannerUrl = user.optString("bannerImage", "")

            val stats = user.optJSONObject("statistics")
            val animeStats = stats?.optJSONObject("anime")
            val mangaStats = stats?.optJSONObject("manga")

            val animeCount = animeStats?.optInt("count", 0) ?: 0
            val episodesWatched = animeStats?.optInt("episodesWatched", 0) ?: 0
            val minutesWatched = animeStats?.optInt("minutesWatched", 0) ?: 0
            val animeMeanScore = animeStats?.optDouble("meanScore", 0.0)?.toFloat() ?: 0f

            val mangaCount = mangaStats?.optInt("count", 0) ?: 0
            val chaptersRead = mangaStats?.optInt("chaptersRead", 0) ?: 0
            val volumesRead = mangaStats?.optInt("volumesRead", 0) ?: 0
            val mangaMeanScore = mangaStats?.optDouble("meanScore", 0.0)?.toFloat() ?: 0f

            AniListUserProfile(
                id = id,
                name = name,
                avatarUrl = avatarUrl,
                bannerUrl = bannerUrl.ifBlank { null },
                about = about,
                animeCount = animeCount,
                episodesWatched = episodesWatched,
                minutesWatched = minutesWatched,
                animeMeanScore = animeMeanScore,
                mangaCount = mangaCount,
                chaptersRead = chaptersRead,
                volumesRead = volumesRead,
                mangaMeanScore = mangaMeanScore
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseUserMediaList(jsonResponse: String, defaultType: String = "ANIME"): List<UserMediaEntry> {
        val result = mutableListOf<UserMediaEntry>()
        try {
            val root = JSONObject(jsonResponse)
            val data = root.optJSONObject("data") ?: return emptyList()
            val collection = data.optJSONObject("MediaListCollection") ?: return emptyList()
            val lists = collection.optJSONArray("lists") ?: return emptyList()

            for (i in 0 until lists.length()) {
                val listObj = lists.optJSONObject(i) ?: continue
                val entries = listObj.optJSONArray("entries") ?: continue

                for (j in 0 until entries.length()) {
                    val entryObj = entries.optJSONObject(j) ?: continue
                    val mediaId = entryObj.optInt("mediaId", 0)
                    if (mediaId == 0) continue

                    val anilistStatus = entryObj.optString("status", "CURRENT")
                    val status = when (anilistStatus) {
                        "CURRENT" -> "WATCHING"
                        "COMPLETED" -> "COMPLETED"
                        "PLANNING" -> "PLANNING"
                        "PAUSED" -> "PAUSED"
                        "DROPPED" -> "DROPPED"
                        "REPEATING" -> "REWATCHING"
                        else -> "WATCHING"
                    }

                    val score = entryObj.optDouble("score", 0.0).toFloat()
                    val progress = entryObj.optInt("progress", 0)
                    val progressVolumes = entryObj.optInt("progressVolumes", 0)
                    val repeat = entryObj.optInt("repeat", 0)
                    val notes = entryObj.optString("notes", "")
                    val updatedAt = entryObj.optLong("updatedAt", System.currentTimeMillis() / 1000) * 1000

                    val mediaObj = entryObj.optJSONObject("media")
                    val titleObj = mediaObj?.optJSONObject("title")
                    val title = titleObj?.optString("english")?.ifBlank { null }
                        ?: titleObj?.optString("romaji")
                        ?: "Untitled"

                    val mediaType = mediaObj?.optString("type", defaultType) ?: defaultType
                    val format = mediaObj?.optString("format", "TV") ?: "TV"
                    val episodes = if (mediaObj?.has("episodes") == true && !mediaObj.isNull("episodes")) mediaObj.optInt("episodes") else null
                    val chapters = if (mediaObj?.has("chapters") == true && !mediaObj.isNull("chapters")) mediaObj.optInt("chapters") else null

                    val coverObj = mediaObj?.optJSONObject("coverImage")
                    val coverImage = coverObj?.optString("extraLarge")
                        ?: coverObj?.optString("large")
                        ?: coverObj?.optString("medium")
                        ?: ""
                    val bannerImage = mediaObj?.optString("bannerImage", "")?.ifBlank { null }

                    val genresArr = mediaObj?.optJSONArray("genres")
                    val genresList = mutableListOf<String>()
                    if (genresArr != null) {
                        for (g in 0 until genresArr.length()) {
                            genresList.add(genresArr.optString(g))
                        }
                    }

                    result.add(
                        UserMediaEntry(
                            mediaId = mediaId,
                            type = mediaType,
                            title = title,
                            coverImage = coverImage,
                            bannerImage = bannerImage,
                            totalEpisodes = episodes,
                            totalChapters = chapters,
                            progress = progress,
                            volumesProgress = progressVolumes,
                            status = status,
                            score = score,
                            isFavorite = false,
                            isAddedLocally = false,
                            notes = notes,
                            repeatCount = repeat,
                            format = format,
                            genresCsv = genresList.joinToString(","),
                            updatedAt = updatedAt
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}

