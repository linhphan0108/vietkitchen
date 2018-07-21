package com.example.linh.vietkitchen.ui.home.homeFragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.adapter.FoodAdapter
import com.example.linh.vietkitchen.ui.mvpBase.BaseFragment
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : BaseFragment<HomeFragmentContractView, HomeFragmentPresenter>(), HomeFragmentContractView {
    companion object {
        val STORAGE_FOOD = "foods"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var foodAdapter: FoodAdapter

    val storage by lazy { FirebaseStorage.getInstance() }
    val storageRef by lazy {  storage.reference.child(STORAGE_FOOD) }


    //region lifecycle =============================================================================
    override fun getFragmentLayoutRes() = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Timber.e("on create")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.e("on activity created")
        setupRecyclerView()
        presenter.requestFoods()
    }

    override fun onStart() {
        super.onStart()
        Timber.e("on start")
    }

    override fun onResume() {
        super.onResume()
        Timber.e("on resume")
    }

    override fun onPause() {
        super.onPause()
        Timber.e("on pause")
    }

    override fun onStop() {
        super.onStop()
        Timber.e("on stop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.e("on destroy view")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("on destroy")
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
        Timber.e("on attach")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        Timber.e("on Detach")
    }
    //endregion lifecycle

    //region MVP callbacks =========================================================================
    override fun initPresenter() = HomeFragmentPresenter()

    override val viewContext: Context?
        get() = context

    override fun getViewContract(): HomeFragmentContractView {
        return this
    }

    override fun onFoodsRequestSuccess(foods: List<Food>) {
        foodAdapter.updateItemThenNotify(foods)
    }

    override fun onFoodsRequestFailed(msg: String) {
        toast(msg)
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion MVP callbacks

    //region inner methods =========================================================================
    private fun createAnPutDumpDataToFirebaseDb(){
//        val name = "Chè hạt sen nhãn nhục"
//        val intro = "Chè hạt sen nhãn nhục không chỉ có vị thơm mát, ngọt dịu của hạt sen hòa quyện với nhãn nhục, mà còn là món ăn bổ dưỡng cho cơ thể. Như bạn cũng biết nhãn nhục ăn quá nhiều sẽ bị nóng nhưng có một cách để ăn nhãn nhục không lo bị nóng đó là chúng ta đem kết hợp nhãn nhục với hạt sen. ";
//        val ingredients = mapOf(
//                Pair("Hạt sen tươi", Ingredient(200, "g")),
//                Pair("Nhãn nhục", Ingredient(100, "g")),
//                Pair("Đường phèn", Ingredient(100, "g")))
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
//        val f = Food(name, intro, ingredients, spices, preliminaryProcessing, processing, method,
//                benefit, season, region, specialDay, imageUrl)


//        dbRef.push().setValue(f)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful){
//                        toast("data pushed")
//                    }else{
//                        toast("failed to push data")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    toast("on failure listener called")
//                    exception.printStackTrace()
//                }
    }

    private fun setupRecyclerView(){
        foodAdapter = FoodAdapter(listOf())
        rcvFood.layoutManager = LinearLayoutManager(context)
        rcvFood.itemAnimator = DefaultItemAnimator()
        rcvFood.adapter = foodAdapter
    }
    //endregion inner methods

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
