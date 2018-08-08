package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.cloud.mapper.RecipeMapper
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.exception.FirebaseDataException
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_CHILD_TAGS_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_PATH
import com.example.linh.vietkitchen.util.LoggerUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeCloudDataSource(private val mapper: RecipeMapper = RecipeMapper()) : RecipeDataSource {
    private val database  by lazy { FirebaseDatabase.getInstance()}
    private val dbRefRecipe by lazy{ database.getReference(STORAGE_RECIPES_PATH)}
    private val dbRefUser by lazy { database.getReference(STORAGE_USER_PATH) }

    override fun getAllRecipes(tag: String?, limit: Int, startAtId: String?): Flowable<List<RecipeDomain>>? {
        val query = if(tag.isNullOrBlank()){
            if(startAtId.isNullOrBlank()){
                dbRefRecipe.orderByKey()
            }else{
                dbRefRecipe.orderByKey()
                        .startAt(startAtId)
            }
        }else{
            if(startAtId.isNullOrBlank()) {
                dbRefRecipe.orderByChild(STORAGE_RECIPES_CHILD_TAGS_PATH + tag)
                        .equalTo(true)
            }else{
                dbRefRecipe.orderByChild(STORAGE_RECIPES_CHILD_TAGS_PATH + tag)
                        .equalTo(true)
                        .startAt(startAtId)
            }
        }.limitToFirst(limit)
        return RxFirebaseDatabase.observeValueEvent(query)
                .observeOn(Schedulers.computation())
                .map{
                    Timber.d("onFetchData data's length ${it.children.count()}")
                    Timber.d("latest key ${it.children.last().key}")
                    LoggerUtil.logThread()
                    mapper.convertToDomain(it.children)
                }

    }

    override fun getLikedRecipes(ids: List<String>): Flowable<List<com.example.linh.vietkitchen.domain.model.Recipe>>? {
        return Flowable.fromIterable(ids)
                .observeOn(Schedulers.computation())
                .flatMap {key ->
                    Flowable.create(FlowableOnSubscribe<DataSnapshot> {emitter ->
                        val query = dbRefRecipe.child(key)
                        query.addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                                emitter.onError(FirebaseDataException(p0))
                            }
                            override fun onDataChange(p0: DataSnapshot) {
                                emitter.onNext(p0)
                                emitter.onComplete()
                            }
                        })
                    }, BackpressureStrategy.DROP)
                }.map {mapper.convertToDomain(it)}
                .toList().toFlowable()
    }

//    override fun getLikedRecipes(uid: String): Flowable<List<RecipeDomain>>? {
//        return getLikedRecipesId(uid)
//                .flatMap {listIds ->
//                    if (listIds.isNotEmpty()) {
//                        Flowable.fromIterable(listIds)
//                    } else {
//                        throw EmptyException()
//                    }
//                }.flatMap {key ->
//                    Flowable.create(FlowableOnSubscribe<DataSnapshot> {emitter ->
//                        val query = dbRefRecipe.child(key)
//                        query.addListenerForSingleValueEvent(object: ValueEventListener{
//                            override fun onCancelled(p0: DatabaseError) {
//                                emitter.onError(FirebaseDataException(p0))
//                            }
//                            override fun onDataChange(p0: DataSnapshot) {
//                                emitter.onNext(p0)
//                            }
//                        })
//                    }, BackpressureStrategy.DROP)
//                }.map {mapper.convertToDomain(it)}
//                .toList().toFlowable()
//
//    }

    //an example how to use an Rxjava  DisposableObserver natively
    //for retrieving data from firebase server
//    fun <T> getObservable(query: Query, clazz: Class<T>): Observable<T>? {
//        return Observable.create { emitter ->
//            query.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    Timber.d(dataSnapshot.toString())
//                    val value = dataSnapshot.getValue(clazz)
//                    if (value != null) {
//                        if (!emitter.isDisposed) {
//                            emitter.onNext(value)
//                        }
//                    } else {
//                        query.removeEventListener(this)
//                        if (!emitter.isDisposed) {
//                            emitter.onError(FirebaseRxDataCastException("Unable to cast Firebase data response to " + clazz.simpleName))
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    query.removeEventListener(this)
//                    if (!emitter.isDisposed) {
//                        emitter.onError(FirebaseRxDataException(error))
//                    }
//                }
//            })
//        }
//    }

    override fun putRecipeWithDumpData(): Completable? {
        return Completable.create { emitter ->
            dbRefRecipe.push().setValue(createADumpFood())
                    .addOnSuccessListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
        }

//        return RxFirebaseDatabase.setValue(dbRefRecipe, createADumpFood())
    }

    private fun createADumpFood(): Recipe {
        val name = "bò xào rau củ"
        val intro = "Chè hạt sen nhãn nhục không chỉ có vị thơm mát, ngọt dịu của hạt sen hòa quyện với nhãn nhục, mà còn là món ăn bổ dưỡng cho cơ thể. Như bạn cũng biết nhãn nhục ăn quá nhiều sẽ bị nóng nhưng có một cách để ăn nhãn nhục không lo bị nóng đó là chúng ta đem kết hợp nhãn nhục với hạt sen. ";
        val ingredients = mapOf(
                Pair("sườn già", Ingredient(200, "g")),
                Pair("cà rốt", Ingredient(200, "g")),
                Pair("khoai tây", Ingredient(200, "g")),
                Pair("bắp mỹ", Ingredient(200, "g")))
        val spices = "Muối, Mì chính(có thể thay bằng bột canh), Lá mùi tàu, hạt tiêu"
        val preliminaryProcessing = listOf<String>()
        val processing = listOf("Rửa xương lợn đã được mua. Sau đó cho vào xoong, cho nước vào và đun qua để xoong sôi lên 1 tí rồi tắt bếp. Đổ xương ra rổ và rửa lại(công đoạn này giúp loại bỏ bụi bẩn khi ngta bán ngoài chợ và giúp cho nước xương được trong. Lưu ý là không đun lâu như vậy sẽ làm mất chất trong xương lợn và khiến món ăn không được ngọt, ngon)",
                "Sau đó lại cho xương vào và đổ nước, hầm xương 25-35p",
                "Bí đao gọt vỏ cắt miếng. Sau khi xương hầm xong thì cho bí đao vào. Bí đao rất nhanh chín nên ta để sôi 5p rồi tắt bếp đi. Chín quá khi nguội nó sẽ chua bí đao, sau đó ta nêm gia vị vào cho hợp khẩu vị. Phần cuối cùng là ta cắt lá mùi tàu vào cho thơm. Như vậy là xong! Chúc mọi người có món ăn ngon!!!")
        val method = mapOf(Pair("chè", true))
        val benefit = mapOf(Pair("giải nhiệt", true))
        val season = mapOf(Pair("mùa hè", true))
        val region = "việt nam"
        val specialDay = "mùa hè"
        val tags = mapOf(
                Pair("thịt", true),
                Pair("thịt bò", true),
                Pair("thịt bò xào", true),
                Pair("bò xào", true)
        )
        val imageUrl = "http://media.phunutoday.vn/files/upload_images/2016/08/29/cach-lam-bo-xao-rau-cu-thom-ngon-hap-dan-phunutoday_vn.jpg"
        return Recipe( name, intro, ingredients, spices, preliminaryProcessing, processing, method,
                benefit, season, region, specialDay, tags, imageUrl)
    }
}