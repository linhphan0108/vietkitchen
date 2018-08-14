package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.cloud.mapper.RecipeMapper
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.exception.FirebaseDataException
import com.example.linh.vietkitchen.exception.FirebaseNoDataException
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
                                emitter.onComplete()
                            }
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.value != null) {
                                    emitter.onNext(p0)
                                }
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
        val name = "thịt cuộn chiên giòn ngon khó cưỡng"
        val intro = "Chè hạt sen nhãn nhục không chỉ có vị thơm mát, ngọt dịu của hạt sen hòa quyện với nhãn nhục, mà còn là món ăn bổ dưỡng cho cơ thể. Như bạn cũng biết nhãn nhục ăn quá nhiều sẽ bị nóng nhưng có một cách để ăn nhãn nhục không lo bị nóng đó là chúng ta đem kết hợp nhãn nhục với hạt sen. ";
        val ingredients = mapOf(
                Pair("sườn già", Ingredient(200, "g")),
                Pair("cà rốt", Ingredient(200, "g")),
                Pair("khoai tây", Ingredient(200, "g")),
                Pair("bắp mỹ", Ingredient(200, "g")))
        val spices = "Muối, Mì chính(có thể thay bằng bột canh), Lá mùi tàu, hạt tiêu"
        val preliminaryProcessing = listOf<ProcessStep>(
                ProcessStep("Trước tiên, các bạn lấy khoảng vài thìa cà phê bột ngô hòa với một xíu nước.\n" +
                        "Sau đó, các bạn dùng dao thật sắc và có bản to, bạn lách dao vào giữa miếng thịt để có thể tạo được thành những lát mỏng.\n" +
                        "Tiếp theo, các bạn dùng búa nhỏ chuyên dụng để đập thịt cho mềm, bạn đập hết một mặt thì lập tiếp sang mặt kế bên. Nếu không có búa, các bạn có thể dùng đầu nhụt của dao để đập thịt cho mềm, giúp thịt ngấm gia vị dễ hơn.",
                        "http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-1.jpg"),
                ProcessStep("Sau đó, các bạn cho thịt vào một bát tô, trộn thịt với các nguyên liệu gồm: một chút muối, nước tương, tiêu và một chút rượu vào đảo đều cho ngấm gia vị. Các bạn ướp thịt trong 5-10 phút cho thịt ngấm đều gia vị.",
                        "http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-2.jpg"),
                ProcessStep(" Dưa leo mua về các bạn rửa sạch, đem ngâm nước muối, sau đó gọt sạch vỏ, cắt thành những khúc nhỏ có độ dày vừa phải và chiều dài bằng với bằng với chiều ngang của miếng thịt.",
                        "http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-31.jpg")
        )
        val processing = listOf(
            ProcessStep("  Trước tiên, các bạn trải miếng thịt lên một mặt phẳng, cho một miếng dưa chuột vào trong. Các bạn lấy tay quết một chút nước bột ngô vào mép miếng thịt để tạo độ kết dính và dần cuộn chặt miếng thịt lại.",
                    "http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-4.jpg"),
                ProcessStep(" Trứng gà các bạn đập sẵn ra bát rồi dùng đũa đánh nhẹ cho đều, bạn có thể cho thêm một xíu bột ngọt và tiêu rồi đánh tan.",
                        "http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-5.jpg"),
                ProcessStep(" Sau đó, các bạn lấy các cuộn thịt lăn đều qua bột ngô sao cho các miếng thịt đều được phủ đầy bột, sau đó nhúng vào trứng.\n" +
                        " Tiếp đến, bạn phủ một lớp áo cuối cùng cho cuộn thịt bằng bột chiên xù và cũng tương tự như bột ngô, cuộn thịt phải được phủ đầy bột.",
                        "http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-6.jpg"),
                ProcessStep("Tiếp theo, các bạn bắc một cái chảo lên bếp, cho dầu ăn ngập chảo, khi dầu ăn sôi thì các bạn thả từng miếng thịt vào chiên vàng. Thịt chín các bạn vớt ra để ráo dầu. Các bạn thấm qua giấy thấm dầu một lần nữa để món ăn không còn nhiều dầu ăn. Khi chiên, các bạn nhớ lật đều các mặt để thịt chín đều và có vỏ ngoài đẹp mắt.",
                        "http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-7.jpg"),
                ProcessStep("Cuối cùng, các bạn trình bày món ăn ra đĩa, ăn kèm với tương ớt hoặc sốt chua ngọt.",
                        "http://sotaynauan.com/wp-content/uploads/2016/08/thit-cuon-dua-leo-chien-xu-sp2-c83ae1.jpg"),
                ProcessStep("Chúc các bạn thành công và ngon miệng với món ăn ngon tuyệt này nhé!")
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
        val imageUrl = "http://sotaynauan.com/wp-content/uploads/2016/08/thit-cuon-dua-leo-chien-xu-sp2-c83ae1.jpg"
        return Recipe( name, intro, ingredients, spices, preliminaryProcessing, processing, method,
                benefit, season, region, specialDay, tags, imageUrl)
    }
}