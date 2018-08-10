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
        val name = "chè hạt sen với đường phèn"
        val intro = "Chè hạt sen nhãn nhục không chỉ có vị thơm mát, ngọt dịu của hạt sen hòa quyện với nhãn nhục, mà còn là món ăn bổ dưỡng cho cơ thể. Như bạn cũng biết nhãn nhục ăn quá nhiều sẽ bị nóng nhưng có một cách để ăn nhãn nhục không lo bị nóng đó là chúng ta đem kết hợp nhãn nhục với hạt sen. ";
        val ingredients = mapOf(
                Pair("sườn già", Ingredient(200, "g")),
                Pair("cà rốt", Ingredient(200, "g")),
                Pair("khoai tây", Ingredient(200, "g")),
                Pair("bắp mỹ", Ingredient(200, "g")))
        val spices = "Muối, Mì chính(có thể thay bằng bột canh), Lá mùi tàu, hạt tiêu"
        val preliminaryProcessing = listOf<ProcessStep>(
                ProcessStep("- Trước tiên, các bạn sơ chế hạt sen bằng cách lấy bỏ tâm của hạt sen ra, cắt bỏ phần đầu đen rồi rửa thật sạch. Lưu ý, nếu như dùng hạt sen khô thì ngâm hạt sen rồi lấy bỏ tâm sen đi nhé!",
                        "https://cachnauche.com/uploads/1/quy-trinh-cach-nau-che-hat-sen-duong-phen.jpg")
        )
        val processing = listOf(
            ProcessStep("Sau khi làm sạch hạt sen các bạn cho vào nồi, đổ nước ngập luộc lửa vừa vừa đến khi chín mềm thì tắt bếp. Cách khác thay vì luộc các bạn có thể hấp cách thủy đến khi chin mềm là được.",
                    "https://cachnauche.com/uploads/1/quy-trinh-cach-nau-che-hat-sen-duong-phen-1.jpg"),
                ProcessStep("- Đây là bước rất quan trọng, các bạn đem lượng đường phèn đã mua hòa với nước, bắc lên bếp đun vừa lửa cho tan đường rồi để một lúc cho nồi nước lắng cặn xuống dưới.\n" +
                        "\n" +
                        "- Sau đó đổ phần nước trong của đường phèn sang một chiếc nồi khác, rồi cho hạt sen đã luộc mềm lúc trước vào cùng và nấu lên.\n" +
                        "\n" +
                        "- Chú ý bước này các bạn để lửa riu riu cho hạt sen thấm vừa nước đường mà không bị nát đun tiếp đến khi sôi thì tắt bếp vag hoàn thành món chè.\n" +
                        "\n" +
                        "Đến lúc thưởng thức thành quả rồi đây…! Các bạn múc chè sen ra bát, nếu thích có dừa cho thơm và đẹp mắt thì rắc lên trên. Trong cách nấu chè hạt sen đường phèn ngon này tùy theo sở thích của từng người có thể ăn nóng hoặc nguội đều vô cùng tuyệt vời. Mùa hè này mà ăn lạnh thêm ít đá bào thì tuyệt lắm nhé.",
                        "https://cachnauche.com/uploads/1/cach-nau-che-hat-sen-duong-phen.jpg")
        )
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
        val imageUrl = "https://cachnauche.com/uploads/1/cach-nau-che-hat-sen-duong-phen.jpg"
        return Recipe( name, intro, ingredients, spices, preliminaryProcessing, processing, method,
                benefit, season, region, specialDay, tags, imageUrl)
    }
}