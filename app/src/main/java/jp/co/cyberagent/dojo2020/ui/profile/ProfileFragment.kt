package jp.co.cyberagent.dojo2020.ui.profile

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.UserItem
import jp.co.cyberagent.dojo2020.databinding.FragmentProfileBinding
import jp.co.cyberagent.dojo2020.ui.ext.showImage
import kotlinx.coroutines.FlowPreview

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(this, Bundle(), requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        return binding.root
    }

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            profileToolBarLayout.profileMaterialToolBar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            profileToolBarLayout.profileMaterialToolBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.share_item -> profileViewModel.saveProfile().let { true }
                    else -> {
                        Log.d(TAG, "else")
                        super.onOptionsItemSelected(it)
                    }
                }
            }

            profileViewModel.userItemListLiveData.observe(viewLifecycleOwner) { userItemList ->
                userItemList.forEach { item ->
                    when (item) {

                        is UserItem.AnalyticItem -> {
                            Log.d(TAG, item.value.toString())

                            val timeEachCategoryList = item.value.orEmpty()
                            val pieEntryList = timeEachCategoryList.map {
                                PieEntry(it.time.toFloat(), it.category.name)
                            }

                            val dataSet = PieDataSet(pieEntryList, "category").apply {
                                setColors(*ColorTemplate.JOYFUL_COLORS)

                                valueTextSize = 12f
                                valueTextColor = Color.WHITE
                            }

                            analyticGraphLayout.timeEachCategoryGraphPieChart.apply {
                                data = PieData(dataSet)
                                centerText = "statistics"

                                setEntryLabelTextSize(13f)
                                setEntryLabelColor(Color.BLACK)
                                setCenterTextSize(15f)

                                animateY(750)
                                invalidate() //更新
                            }
                        }

                        is UserItem.PrimaryAccountItem -> {
                            primaryAccountLayout.apply {
                                item.value?.also {
                                    Log.d(TAG, "USERITEM.PRIMARYACCOUNT")
                                    nameTextView.text = it.name
                                    iconImageButton.showImage(this, it.imageUri)
                                }
                            }
                        }

                        is UserItem.SecondaryAccountItem -> {
                            item.value?.takeIf { it.serviceName == "Twitter" }?.also { twitterAccount ->
                                secondaryTopAccountLayout.apply {
                                    accountIdTextView.text = twitterAccount.id
                                    accountImageButton.showImage(this, R.mipmap.twitter_logo)
                                }
                            }

                            item.value?.takeIf { it.serviceName == "GitHub" }?.also { githubAccount ->
                                secondaryBottomAccountLayout.apply {
                                    accountIdTextView.text = githubAccount.id
                                    accountImageButton.showImage(this, R.mipmap.github_logo)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}