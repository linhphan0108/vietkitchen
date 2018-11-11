package com.example.linh.vietkitchen.data.cloud

import android.net.Uri
import com.example.linh.vietkitchen.domain.mapper.RecipeMapper
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.exception.FirebaseDataException
import com.example.linh.vietkitchen.exception.FirebaseNoDataException
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_CHILD_TAGS_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_PATH
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeCloudDataSource(private val mapper: RecipeMapper = RecipeMapper()) : RecipeDataSource {

    private val database by lazy { FirebaseDatabase.getInstance() }
    private val dbRefRecipe by lazy { database.getReference(STORAGE_RECIPES_PATH) }
    private val dbRefUser by lazy { database.getReference(STORAGE_USER_PATH) }
    private val storage = FirebaseStorage.getInstance()
    private val storageRecipeRef = storage.reference.child("images/recipes/")

    override fun getAllRecipes(tag: String?, limit: Int, startAtId: String?): Flowable<List<DataSnapshot>> {
        val isLoadingMore = startAtId != null
        val limitFixed = if (isLoadingMore) limit + 1 else limit
        return Flowable.create(FlowableOnSubscribe<DataSnapshot> { emitter ->
            val query = if (tag.isNullOrBlank()) {
                if (isLoadingMore) {
                    dbRefRecipe.orderByKey()
                            .startAt(startAtId)
                } else {
                    dbRefRecipe.orderByKey()
                }
            } else {
                if (isLoadingMore) {
                    dbRefRecipe.orderByChild(STORAGE_RECIPES_CHILD_TAGS_PATH + tag)
                            .equalTo(true)
                            .startAt(startAtId)
                } else {
                    dbRefRecipe.orderByChild(STORAGE_RECIPES_CHILD_TAGS_PATH + tag)
                            .equalTo(true)
                }
            }.limitToFirst(limitFixed)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    if(!emitter.isCancelled) {
                        emitter.onError(FirebaseDataException(p0))
                        emitter.onComplete()
                    }
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(!emitter.isCancelled) {
                        if (p0.value != null) {
                            emitter.onNext(p0)
                        }
                        emitter.onComplete()
                    }
                }
            })
        }, BackpressureStrategy.DROP)
                .observeOn(Schedulers.computation())
                .doOnNext {
                    val minCount = if (isLoadingMore) 1 else 0
                    if (it.children.count() <= minCount) throw FirebaseNoDataException()
                }
                .map {
                    val shouldRemoveItems = if (isLoadingMore) 1 else 0
                    it.children.drop(shouldRemoveItems)
                }

    }

    override fun getLikedRecipes(ids: List<String>): Flowable<DataSnapshot> {
        return Flowable.fromIterable(ids)
                .observeOn(Schedulers.computation())
                .flatMap {key ->
                    Flowable.create(FlowableOnSubscribe<DataSnapshot> {emitter ->
                        val query = dbRefRecipe.child(key)
                        query.addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                                if(!emitter.isCancelled) {
                                    emitter.onError(FirebaseDataException(p0))
                                    emitter.onComplete()
                                }
                            }
                            override fun onDataChange(p0: DataSnapshot) {
                                if(!emitter.isCancelled) {
                                    if (p0.value != null) {
                                        emitter.onNext(p0)
                                    }
                                    emitter.onComplete()
                                }
                            }
                        })
                    }, BackpressureStrategy.DROP)
                }
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

    override fun putRecipe(recipe: Recipe): Flowable<String> {
        return Flowable.create (FlowableOnSubscribe<String> { emitter ->
            dbRefRecipe.push().setValue(recipe){databaseError, databaseReference ->
                if (databaseError == null){
                    if(!emitter.isCancelled) {
                        emitter.onNext(databaseReference.key.toString())
                        emitter.onComplete()
                    }
                }else{
                    if(!emitter.isCancelled) {
                        emitter.onError(databaseError.toException())
                    }
                }
            }
        }, BackpressureStrategy.DROP)
                .observeOn(Schedulers.computation())
    }

    override fun putRecipeWithDumpData(): Completable? {
        return Completable.create { emitter ->
            dbRefRecipe.push().setValue(createADumpFood())
                    .addOnSuccessListener {
                        if(!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                    }
                    .addOnFailureListener {
                        if(!emitter.isDisposed) {
                            emitter.onError(it)
                        }
                    }
        }.observeOn(Schedulers.computation())

//        return RxFirebaseDatabase.setValue(dbRefRecipe, createADumpFood())
    }

    override fun uploadImages(multiPartFileList: List<ImageUpload>): Flowable<ImageUpload> {
        return Flowable.fromIterable(multiPartFileList)
                .concatMap {
                    uploadImage(it)
                }.observeOn(Schedulers.computation())
    }

    private fun uploadImage(image: ImageUpload): Flowable<ImageUpload>? {
        return Flowable.create(FlowableOnSubscribe<ImageUpload> {emitter ->
            val storageRecipeImageRef = storageRecipeRef.child(image.fileName)
            storageRecipeImageRef.putFile(Uri.fromFile(File(image.optimizedPath)))
                    .addOnProgressListener { taskSnapshot ->
                        val progress: Int = (100 * taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount).toInt()
                        val message = ImageUpload(image.fileName, image.originalPath, image.optimizedPath, progress)
                        emitter.onNext(message)
                    }
                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation storageRecipeImageRef.downloadUrl
                    })
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val remoteUri = (task.result as Uri).toString()
                            val message = ImageUpload(image.fileName, image.originalPath, image.optimizedPath, 100, remoteUri)
                            emitter.onNext(message)
                            emitter.onComplete()
                            Timber.d("uploaded ${image.originalPath} into storage ")
                        } else {
                            // Handle failures
                            Timber.e(task.exception)
                        }
                    }
        }, BackpressureStrategy.DROP)
                .observeOn(Schedulers.computation())
    }



    private fun createADumpFood(): Recipe {
        val name = "canh khổ qua nhồi thịt"
        val intro = "Chè hạt sen nhãn nhục không chỉ có vị thơm mát, ngọt dịu của hạt sen hòa quyện với nhãn nhục, mà còn là món ăn bổ dưỡng cho cơ thể. Như bạn cũng biết nhãn nhục ăn quá nhiều sẽ bị nóng nhưng có một cách để ăn nhãn nhục không lo bị nóng đó là chúng ta đem kết hợp nhãn nhục với hạt sen. "
        val ingredients = ""
        val spices = "Muối, Mì chính(có thể thay bằng bột canh), Lá mùi tàu, hạt tiêu"
        val preparation =  "Trước tiên, các bạn lấy khoảng vài thìa cà phê bột ngô hòa với một xíu nước.\n" +
                        "Sau đó, các bạn dùng dao thật sắc và có bản to, bạn lách dao vào giữa miếng thịt để có thể tạo được thành những lát mỏng.\n" +
                        "Tiếp theo, các bạn dùng búa nhỏ chuyên dụng để đập thịt cho mềm, bạn đập hết một mặt thì lập tiếp sang mặt kế bên. Nếu không có búa, các bạn có thể dùng đầu nhụt của dao để đập thịt cho mềm, giúp thịt ngấm gia vị dễ hơn." +
                        "<annotation src=\"http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-1.jpg\"/>" +
                        "Tiếp theo, các bạn dùng búa nhỏ chuyên dụng để đập thịt cho mềm, bạn đập hết một mặt thì lập tiếp sang mặt kế bên. Nếu không có búa, các bạn có thể dùng đầu nhụt của dao để đập thịt cho mềm, giúp thịt ngấm gia vị dễ hơn." +
                        "<annotation src=\"https://znews-photo-td.zadn.vn/w660/Uploaded/Ohunoaa/2017_01_17/canh_1.jpg\"/>" +
                        "Dưa leo mua về các bạn rửa sạch, đem ngâm nước muối, sau đó gọt sạch vỏ, cắt thành những khúc nhỏ có độ dày vừa phải và chiều dài bằng với bằng với chiều ngang của miếng thịt." +
                        "<annotation src=\"http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-31.jpg\"/>"

        val processing = "Trước tiên, các bạn trải miếng thịt lên một mặt phẳng, cho một miếng dưa chuột vào trong. Các bạn lấy tay quết một chút nước bột ngô vào mép miếng thịt để tạo độ kết dính và dần cuộn chặt miếng thịt lại." +
                        "<annotation src=\"https://znews-photo-td.zadn.vn/w660/Uploaded/Ohunoaa/2017_01_17/canh1.jpg\"/>" +
                        "Trứng gà các bạn đập sẵn ra bát rồi dùng đũa đánh nhẹ cho đều, bạn có thể cho thêm một xíu bột ngọt và tiêu rồi đánh tan." +
                        "<annotation src=\"http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-5.jpg\"/>" +
                        "Sau đó, các bạn lấy các cuộn thịt lăn đều qua bột ngô sao cho các miếng thịt đều được phủ đầy bột, sau đó nhúng vào trứng.\n" +
                        "Tiếp đến, bạn phủ một lớp áo cuối cùng cho cuộn thịt bằng bột chiên xù và cũng tương tự như bột ngô, cuộn thịt phải được phủ đầy bột." +
                        "<annotation src=\"http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-6.jpg\"/>" +
                        "Tiếp theo, các bạn bắc một cái chảo lên bếp, cho dầu ăn ngập chảo, khi dầu ăn sôi thì các bạn thả từng miếng thịt vào chiên vàng. Thịt chín các bạn vớt ra để ráo dầu. Các bạn thấm qua giấy thấm dầu một lần nữa để món ăn không còn nhiều dầu ăn. Khi chiên, các bạn nhớ lật đều các mặt để thịt chín đều và có vỏ ngoài đẹp mắt." +
                        "<annotation src=\"http://sotaynauan.com/wp-content/uploads/2016/08/mon-moi-an-vat-thit-cuon-gion-tan-voi-tuong-ot-cay-hap-dan-7.jpg\"/>" +
                        "Cuối cùng, các bạn trình bày món ăn ra đĩa, ăn kèm với tương ớt hoặc sốt chua ngọt." +
                        "<annotation src=\"http://sotaynauan.com/wp-content/uploads/2016/08/thit-cuon-dua-leo-chien-xu-sp2-c83ae1.jpg\"/>" +
                        "Chúc các bạn thành công và ngon miệng với món ăn ngon tuyệt này nhé!"

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
        val categories = mapOf<String, Boolean>()
        val thumbImageUrl = "https://znews-photo-td.zadn.vn/w660/Uploaded/Ohunoaa/2017_01_17/IMG_7731.JPG"
        val imageUrl = "https://znews-photo-td.zadn.vn/w660/Uploaded/Ohunoaa/2017_01_17/IMG_7731.JPG"
        return Recipe( name, intro, ingredients, spices, preparation, processing, null, categories,
                tags, thumbImageUrl, imageUrl)
    }
}