package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.model.Ingredient
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class RecipeProviderTest {
    @Test fun testRequestFoodReturnValue(){
//        val ds = mock(RecipeDataSource::class.java)
//        val food = createDumpFood()
//        val flowable = Flowable.just(listOf(food))
//        `when`(ds.requestRecipesByCategory()).then{flowable}
//        val provider = RecipeProvider(listOf(ds))
//        assertNotNull(provider.requestRecipesByTag())
    }

    @Test fun testRequestFoodNoValue(){
//        val ds = mock(RecipeDataSource::class.java)
//        val flowable = Flowable.empty<List<Recipe>>()
//        `when`(ds.requestRecipesByCategory()).then{flowable}
//        val provider = RecipeProvider(listOf(ds))
//        assertNotNull(provider.requestRecipesByTag())

    }

//    private fun createDumpFood(): Recipe {
//        val id = "vaaskdfhskjdf"
//        val name = "Chè hạt sen nhãn nhục"
//        val intro = "Chè hạt sen nhãn nhục không chỉ có vị thơm mát, ngọt dịu của hạt sen hòa quyện với nhãn nhục, mà còn là món ăn bổ dưỡng cho cơ thể. Như bạn cũng biết nhãn nhục ăn quá nhiều sẽ bị nóng nhưng có một cách để ăn nhãn nhục không lo bị nóng đó là chúng ta đem kết hợp nhãn nhục với hạt sen. ";
//        val ingredients = mapOf(
//                Pair("Hạt sen tươi", Ingredient("Hạt sen tươi", 200, "g")),
//                Pair("Nhãn nhục", Ingredient("Nhãn nhục", 100, "g")),
//                Pair("Đường phèn", Ingredient("Đường phèn",100, "g")))
//        val spices = "hành lá, ngò rí, hành củ, hạt tiêu"
//        val preliminaryProcessing = listOf(
//                "Nếu dùng hạt sen tươi, bạn bóc sạch vỏ, tách bỏ tim sen, nếu dùng hạt sen khô, bạn ngâm mềm trước khi nấu",
//                "Nhãn nhục mua về bạn cũng bóc vỏ, bỏ hạt rồi để riêng ra chén. Nếu dùng nhãn nhục khô, bạn rửa sạch, ngâm vào nước lạnh cho nở rồi xả lại dưới vòi nước cho sạch cát và bụi, để vào rổ cho ráo nước.")
//        val processing = listOf(
//                "Sau khi sơ chế, bạn tiến hành nấu mềm hạt sen, đối với hạt sen tươi nấu mềm sẽ giúp hạt sen tươi bớt mủ, nấu hạt sen khô giúp hạt nềm đều khi nấu và tránh bị sượng. Sau khi hầm chín hạt sen, bạn vớt hạt sen ra ngoài. Nếu muốn nước chè không bị đục, sau khi hầm hạt sen xong, bạn xả sơ hạt sen qua nước lạnh.",
//                "Sau khi hầm hạt sen xong, bạn khéo léo nhét hạt sen vào bên trong nhãn nhục. Bạn làm cho hết hạt sen và nhãn nhục.\n" +
//                        "\n" +
//                        "Đường phèn bạn cho vào nồi nước cho sôi và đường tan hết. Tiếp theo, bạn cho hạt sen nhãn nhục vào nấu khoảng 10 phút. Bạn lưu ý không nấu quá lâu vì sẽ làm mất đi độ giòn của nhãn nhục. Nếm thử xem vừa miệng rồi thì tắt bếp.\n" +
//                        "\n" +
//                        "Bạn có thể thưởng thức chè hạt sen nhãn nhục nóng hoặc lạnh đều rất ngon. Vậy là chúng tôi đã giới thiệu xong cách nấu chè hạt sen nhãn nhục thơm mát, bổ dưỡng để cả gia đình thưởng thức rồi. Về cơ bản thì cách nấu chè không có gì khó, chỉ mất thời gian một chút ở khâu chuẩn bị nguyên liệu. Đừng ngại bỏ chút thời gian để vào bếp và chế biến cho gia đình nhé, vừa ngon lại vừa đảm bảo an toàn vệ sinh.\n" +
//                        "\n" +
//                        "Chúc bạn thành công khi thực hiện.")
//        val method = mapOf(Pair("chè", true))
//        val benefit = mapOf(Pair("giải nhiệt", true))
//        val season = mapOf(Pair("mùa hè", true))
//        val region = "việt nam"
//        val specialDay = "mùa hè"
//        val imageUrl = "https://daubepgiadinh.vn/wp-content/uploads/2018/05/che-hat-sen-nhan-nhuc.jpg"
//        val thumbUrl = imageUrl
//        return Recipe(id, name, intro, ingredients, spices, "", "", method,
//                benefit, season, region, specialDay, thumbUrl, imageUrl)
//    }
}