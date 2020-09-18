package jp.co.cyberagent.dojo2020.ui.profile

import android.graphics.Color
import android.os.Bundle
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
import jp.co.cyberagent.dojo2020.databinding.FragmentProfileBinding
import jp.co.cyberagent.dojo2020.ui.ext.showImage
import kotlinx.coroutines.FlowPreview

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(requireContext())
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
                    R.id.share_item -> true
                    else -> super.onOptionsItemSelected(it)
                }
            }

            profileViewModel.firebaseUserInfoLiveData.observe(viewLifecycleOwner) { firebaseUserInfo ->
                with(primaryAccountLayout) {
                    iconImageButton.showImage(
                        this,
                        firebaseUserInfo?.imageUri
                    )
                }
            }

            profileViewModel.profileLiveData.observe(viewLifecycleOwner) { profile ->
                profile?.accountList?.forEach {
                    if (it.serviceName == "Twitter") {
                        secondaryTopAccountLayout.apply {
                            iconImageButton.showImage(this, R.mipmap.twitter_logo)
                            idTextView.text = it.id
                        }
                    }

                    if (it.serviceName == "GitHub") {
                        secondaryBottomAccountLayout.apply {
                            iconImageButton.showImage(this, R.mipmap.github_logo)
                            idTextView.text = it.id
                        }
                    }
                }

            }

            profileViewModel.timeEachCategoryLiveData.observe(viewLifecycleOwner) { timeEachCategoryList ->
                val pieEntryList = timeEachCategoryList
                    .map { PieEntry(it.time.toFloat(), it.category.name) }

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
        }
    }
}