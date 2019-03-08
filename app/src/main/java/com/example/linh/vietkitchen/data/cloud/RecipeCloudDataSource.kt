package com.example.linh.vietkitchen.data.cloud

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.example.linh.vietkitchen.data.mapper.RecipeMapper
import com.example.linh.vietkitchen.data.response.*
import com.example.linh.vietkitchen.extension.*
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_CHILD_CATEGORIES
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_CHILD_TAGS
import com.example.linh.vietkitchen.util.Constants.STORAGE_RECIPES_PATH
import com.example.linh.vietkitchen.util.TimberUtils
import com.example.linh.vietkitchen.util.transform
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import timber.log.Timber
import java.lang.NullPointerException
import javax.inject.Inject
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeCloudDataSource @Inject constructor
(   private val mapper: RecipeMapper,
    private val database: FirebaseDatabase, private val storage: FirebaseStorage){

    private val dbRefRecipe by lazy { database.getReference(STORAGE_RECIPES_PATH) }
    private val storageRecipeRef = storage.reference.child("images/recipes/")

    fun requestRecipesByTag(tag: String?, limit: Int, startAtId: String?): LiveData<ApiResponse<PagingResponse<List<Recipe>>>> {
        return requestRecipes(null, tag, limit, startAtId)
    }

    fun requestRecipesByCategory(category: String?, limit: Int, startAtId: String?): LiveData<ApiResponse<PagingResponse<List<Recipe>>>> {
        return requestRecipes(category, null, limit, startAtId)
    }

    fun getLikedRecipes(ids: List<String>): LiveData<ApiResponse<List<Recipe>>> {
       val liveData = MutableLiveData<ApiResponse<List<Recipe>>>()
        val result = mutableListOf<Recipe>()
        var counter = 0
        ids.forEach {key ->
            val query = dbRefRecipe.child(key)
            val liveDataQuery = query.addListenerForSingleValueEventAwait()
            ApiObserver(liveDataQuery){apiResponse ->
                counter++
                when(apiResponse) {
                    is ApiSuccessResponse -> {
                        result.add(mapper.toData(apiResponse.data))
                        if(counter == ids.size) {
                            liveData.value = ApiResponse.createSuccess(result.toList())
                        }
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }

        return liveData
    }

    fun deleteRecipe(id: String): LiveData<ApiResponse<Boolean>> {
        return dbRefRecipe.child(id).removeValueAwait()
    }

    fun putRecipe(recipe: Recipe): LiveData<ApiResponse<String>> {
        return dbRefRecipe.push().setValueAwait(recipe)
    }

    fun updateRecipe(recipe: Recipe): LiveData<ApiResponse<Boolean>> {
        val id = recipe.id
        if (id.isNullOrBlank()) throw NullPointerException("id must be not null")
        recipe.id = null
        return Transformations.map(dbRefRecipe.child(id).setValueAwait(recipe)){apiResponse ->
            return@map when(apiResponse){
                is ApiSuccessResponse -> {ApiResponse.createSuccess(true)}
                else -> {ApiResponse.createSuccess(false)}
            }
        }

    }

//    suspend fun putRecipeWithDumpData(): Response<Boolean>? {
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
//        return null
//    }

    fun uploadImages(multiPartFileList: List<ImageUpload>): LiveData<ApiResponse<List<ImageUpload>>> {
        val result = mutableListOf<ImageUpload>()
        val liveData = MutableLiveData<ApiResponse<List<ImageUpload>>>()
        multiPartFileList.forEach {
            val apiResponse = uploadImage(it).value
            when(apiResponse){
                is ApiSuccessResponse -> {
                    result.add(apiResponse.data)
                    liveData.value = ApiResponse.createSuccess(result)
                }
                is ApiErrorResponse -> {}
                is ApiEmptyResponse -> {}
            }
        }
        return liveData
    }



    private fun uploadImage(image: ImageUpload): LiveData<ApiResponse<ImageUpload>> {
        val dirRef = image.remoteDir?.let {
            storageRecipeRef.child(it)
        } ?: storageRecipeRef
        val storageRecipeImageRef = dirRef.child(image.fileName)
        return storageRecipeImageRef.putImageAwait(image)
    }

    fun deleteImages(fileUrls: List<String>): LiveData<ApiResponse<Boolean>> {
        var result = true
        fileUrls.forEach {
            val apiResponse = deleteImage(it).value
            when(apiResponse){
                is ApiSuccessResponse -> {

                }
                is ApiErrorResponse -> {result = false}
                is ApiEmptyResponse -> {result = false}
            }
        }
        return MutableLiveData<ApiResponse<Boolean>>().apply {
            value = ApiResponse.createSuccess(result)
        }
    }

    private fun deleteImage(url: String): LiveData<ApiResponse<Boolean>>{
            return storage.getReferenceFromUrl(url)
                    .deleteAwait()
    }

    private fun requestRecipes(category: String?, tag: String?, limit: Int, startAtId: String?): LiveData<ApiResponse<PagingResponse<List<Recipe>>>> {
        val isLoadingMore = startAtId != null
        val limitFixed = if (isLoadingMore) limit + 1 else limit
        val query = if (category.isNotNullAndNotBlank()) {
            if (isLoadingMore) {
                Timber.d("isLoadingMore startAtId = $startAtId")
                dbRefRecipe.orderByChild("$STORAGE_RECIPES_CHILD_CATEGORIES/$category")
                        .equalTo(true)
//                        .endAt(startAtId)
            } else {
                dbRefRecipe.orderByChild("$STORAGE_RECIPES_CHILD_CATEGORIES/$category")
                        .equalTo(true)
            }
        } else if (tag.isNotNullAndNotBlank()){
            if (isLoadingMore) {
                dbRefRecipe.orderByChild("$STORAGE_RECIPES_CHILD_TAGS/$tag")
                        .equalTo(true)
//                        .endAt(startAtId)
            } else {
                dbRefRecipe.orderByChild("$STORAGE_RECIPES_CHILD_TAGS/$tag")
                        .equalTo(true)
            }
        }else{
            if (isLoadingMore) {
                dbRefRecipe.orderByKey()
                        .endAt(startAtId)
            } else {
                dbRefRecipe.orderByKey()

            }.limitToLast(limitFixed)
        }


        startAtId?.let { Timber.d("requestRecipesByCategory from $startAtId") }

        val responseLiveData = query.addListenerForSingleValueEventAwait()
        return responseLiveData.transform { response ->
            TimberUtils.checkNotMainThread()
            when(response){
                is ApiSuccessResponse -> {
                    val dataSnapshot = response.data
                    val listDataSnapshot = if (isLoadingMore){
                        dataSnapshot.children.toList().dropLast(1)
                    }else{
                        dataSnapshot.children
                    }.reversed()
                    val listRecipes = listDataSnapshot.let {
                        Timber.d("onFetchData data's length ${listDataSnapshot.count()}")
                        Timber.d("latest key ${listDataSnapshot.last().key}")
                        mapper.toData(listDataSnapshot) }
                    val hasReachEnd = listDataSnapshot.count() < limit
                            || tag.isNotNullAndNotBlank() || category.isNotNullAndNotBlank()
                    val nextPage = if(hasReachEnd) null else listRecipes.last().id
                    val pagingResponse = PagingResponse(listRecipes, hasReachEnd, nextPage)
                    ApiResponse.createSuccess(pagingResponse)
                }
                is ApiEmptyResponse -> {ApiResponse.createEmpty()}
                is ApiErrorResponse -> {ApiResponse.createError(response.errorMessage)}
                else -> {ApiResponse.createEmpty()}
            }
        }
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

class ApiObserver(private val liveData: LiveData<ApiResponse<DataSnapshot>>,
                  private val callback:(apiResponse: ApiResponse<DataSnapshot>) -> Unit)
    : Observer<ApiResponse<DataSnapshot>> {
    init {
        liveData.observeForever(this)
    }
    override fun onChanged(t: ApiResponse<DataSnapshot>?) {
        t?.let(callback)
        liveData.removeObserver(this)
    }
}