package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.response.PagingResponse
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.*
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_CHILD_CATEGORIES
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_PATH
import com.example.linh.vietkitchen.util.ResponseCode.RESPONSE_SUCCESS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import timber.log.Timber
import java.lang.NullPointerException
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeCloudDataSource : RecipeDataSource {
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val dbRefRecipe by lazy { database.getReference(STORAGE_RECIPES_PATH) }
    private val dbRefUser by lazy { database.getReference(STORAGE_USER_PATH) }
    private val storage = FirebaseStorage.getInstance()
    private val storageRecipeRef = storage.reference.child("images/recipes/")

    override suspend fun getAllRecipes(tag: String?, limit: Int, startAtId: String?): PagingResponse<List<DataSnapshot>> {
        val isLoadingMore = startAtId != null
        val limitFixed = if (isLoadingMore) limit + 1 else limit
        val query = if (tag.isNullOrBlank()) {
            if (isLoadingMore) {
                dbRefRecipe.orderByKey()
                        .endAt(startAtId)
            } else {
                dbRefRecipe.orderByKey()
            }
        } else {
            if (isLoadingMore) {
                dbRefRecipe.orderByChild("$STORAGE_RECIPES_CHILD_CATEGORIES/$tag")
                        .equalTo(true)
                        .endAt(startAtId)
            } else {
                dbRefRecipe.orderByChild("$STORAGE_RECIPES_CHILD_CATEGORIES/$tag")
                        .equalTo(true)
            }
        }.limitToLast(limitFixed)


        startAtId?.let { Timber.d("getAllRecipes from $startAtId") }

        val dataSnapshot = query.addListenerForSingleValueEventAwait()
        val listDataSnapshot = if (isLoadingMore){
            dataSnapshot.children.toList().dropLast(1)
        }else{
            dataSnapshot.children
        }.reversed()
        val lastId = listDataSnapshot.last().key
        val hasReachEnd = listDataSnapshot.count() < limit
        return PagingResponse(RESPONSE_SUCCESS, listDataSnapshot, hasReachEnd, lastId)
    }

    override suspend fun getLikedRecipes(ids: List<String>): Response<List<DataSnapshot>> {
        val result = mutableListOf<DataSnapshot>()
        ids.forEach {key ->
            val query = dbRefRecipe.child(key)
            result.add(query.addListenerForSingleValueEventAwait())
        }
        return Response(RESPONSE_SUCCESS, result)
    }

    override suspend fun deleteRecipe(recipe: Recipe): Response<Boolean> {
        val isSuccess = dbRefRecipe.child(recipe.id!!).removeValueAwait()
        return Response(RESPONSE_SUCCESS, isSuccess)
    }

    override suspend fun putRecipe(recipe: Recipe): Response<String>? {
        val id = dbRefRecipe.push().setValueAwait(recipe)
        return Response(RESPONSE_SUCCESS, id)
    }

    override suspend fun updateRecipe(recipe: Recipe): Response<Boolean> {
        val id = recipe.id
        if (id.isNullOrBlank()) throw NullPointerException("id must be not null")
        recipe.id = null
        dbRefRecipe.child(id).setValueAwait(recipe)
        return Response(RESPONSE_SUCCESS, true)
    }

    override suspend fun putRecipeWithDumpData(): Response<Boolean>? {
//        return Completable.create { emitter ->
//            dbRefRecipe.push().setValue(createADumpFood())
//                    .addOnSuccessListener {
//                        if(!emitter.isDisposed) {
//                            emitter.onComplete()
//                        }
//                    }
//                    .addOnFailureListener {
//                        if(!emitter.isDisposed) {
//                            emitter.onError(it)
//                        }
//                    }
//        }.observeOn(Schedulers.computation())

//        return RxFirebaseDatabase.setValue(dbRefRecipe, createADumpFood())
        return null
    }

    override suspend fun uploadImages(multiPartFileList: List<ImageUpload>): Response<List<ImageUpload>> {
        multiPartFileList.forEach {
            uploadImage(it)
        }
        return Response(RESPONSE_SUCCESS, multiPartFileList)
    }



    private suspend fun uploadImage(image: ImageUpload): Response<ImageUpload> {
        val dirRef = image.remoteDir?.let {
            storageRecipeRef.child(it)
        } ?: storageRecipeRef
        val storageRecipeImageRef = dirRef.child(image.fileName)
        return storageRecipeImageRef.putImageAwait(image)
    }

    override suspend fun deleteImages(fileUrls: List<String>): Response<Boolean> {
        var result = true
        fileUrls.forEach {
            val isSuccess = deleteImage(it)
            if (!isSuccess.data!!){
                result = false
            }
        }
        return Response(RESPONSE_SUCCESS, result)
    }

    private suspend fun deleteImage(url: String): Response<Boolean>{
            return storage.getReferenceFromUrl(url)
                    .deleteAwait()
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