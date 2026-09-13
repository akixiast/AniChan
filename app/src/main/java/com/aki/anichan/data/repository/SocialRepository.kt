package com.aki.anichan.data.repository

import com.aki.anichan.data.response.SocialData
import com.aki.anichan.data.response.anilist.*
import com.aki.anichan.helper.pojo.NullableItem
import com.aki.anichan.type.ActivityType
import com.aki.anichan.type.LikeableType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface SocialRepository {
    val activityToBeEdited: Observable<NullableItem<Activity>>
    val newOrEditedActivity: Observable<NullableItem<Activity>>
    val replyToBeEdited: Observable<NullableItem<ActivityReply>>
    val newOrEditedReply: Observable<NullableItem<ActivityReply>>
    fun updateActivityToBeEdited(activity: Activity)
    fun clearActivityToBeEdited()
    fun clearNewOrEditedActivity()
    fun updateReplyToBeEdited(activityReply: ActivityReply)
    fun clearReplyToBeEdited()
    fun clearNewOrEditedReply()
    fun getSocialData(): Observable<SocialData>
    fun getActivityDetail(id: Int): Observable<Activity>
    fun getActivityList(page: Int, userId: Int?, typeIn: List<ActivityType>?, isFollowing: Boolean?): Observable<Page<Activity>>
    fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable
    fun toggleLike(id: Int, likeableType: LikeableType): Completable
    fun deleteActivity(id: Int): Completable
    fun deleteActivityReply(id: Int): Completable
    fun saveTextActivity(id: Int?, text: String): Observable<TextActivity>
    fun saveActivityReply(id: Int?, activityId: Int, text: String): Observable<ActivityReply>
    fun saveMessageActivity(id: Int?, recipientId: Int, message: String, private: Boolean): Observable<MessageActivity>
}