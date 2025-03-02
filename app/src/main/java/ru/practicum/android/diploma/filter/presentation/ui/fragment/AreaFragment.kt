package ru.practicum.android.diploma.filter.presentation.ui.fragment

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.ui.BaseFragment
import ru.practicum.android.diploma.databinding.FragmentAreaBinding
import ru.practicum.android.diploma.filter.domain.model.Area
import ru.practicum.android.diploma.filter.presentation.viewmodel.AreaViewModel
import ru.practicum.android.diploma.util.hide
import ru.practicum.android.diploma.util.show

class AreaFragment : BaseFragment<FragmentAreaBinding, AreaViewModel>(FragmentAreaBinding::inflate) {

    override val viewModel: AreaViewModel by viewModel()
    private var countryNameField: String? = null
    private var regionNameField: String? = null
    private var fieldCheckFlag = false

    override fun initViews() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tetCountry.setOnClickListener {
            findNavController().navigate(R.id.action_areaFragment_to_countryFragment)
        }

        binding.tetRegion.setOnClickListener {
            findNavController().navigate(R.id.action_areaFragment_to_regionFragment)
        }

        binding.tilCountry.setEndIconOnClickListener {
            if (!binding.tetCountry.text.isNullOrEmpty()) {
                viewModel.updateArea(Area(country = null, region = null)) // полностью сбрасываем страну
            }
        }

        binding.tilRegion.setEndIconOnClickListener {
            if (!binding.tetRegion.text.isNullOrEmpty()) {
                val currentArea = viewModel.selectedArea.value
                viewModel.updateArea(
                    currentArea!!.copy(country = currentArea.country, region = null)
                )
            }
        }

        binding.btnSelect.setOnClickListener {
            viewModel.saveFilters()
            findNavController().navigateUp()
        }

        // TextWatcher для tetCountry и tetRegion
        setTextChangedListeners()
    }

    override fun subscribe() {
        viewModel.getActualFilterState()
        viewModel.selectedArea.observe(viewLifecycleOwner) { area ->
            val countryName = area?.country?.name.orEmpty()
            val regionName = area?.region?.name.orEmpty()

            updateFieldsAndButtonVisibility(countryName, regionName)

            updateCountryIcon(countryName)

            updateRegionIcon(regionName)

            if (regionName.isNotEmpty() && countryName.isEmpty()) {
                viewModel.getCountryByRegion()
            }
        }
    }

    private fun updateFieldsAndButtonVisibility(countryName: String, regionName: String) {
        binding.tetCountry.setText(countryName)
        binding.tetRegion.setText(regionName)

        if (countryName != countryNameField || regionName != regionNameField) {
            countryNameField = countryName
            regionNameField = regionName

            if (!fieldCheckFlag) {
                fieldCheckFlag = true
            } else {
                if (countryName.isNotEmpty() || regionName.isNotEmpty()) {
                    binding.btnSelect.show()
                } else {
                    binding.btnSelect.hide()
                }
            }
        }
    }

    private fun updateCountryIcon(countryName: String) {
        if (countryName.isNotEmpty()) {
            binding.tilCountry.endIconDrawable = requireContext().getDrawable(R.drawable.ic_close)
            binding.tilCountry.setEndIconOnClickListener {
                viewModel.updateArea(Area(country = null, region = null))
            }
        } else {
            binding.tilCountry.endIconDrawable = requireContext().getDrawable(R.drawable.ic_arrow_forward)
            binding.tilCountry.setEndIconOnClickListener {
                findNavController().navigate(R.id.action_areaFragment_to_countryFragment)
            }
        }
    }

    private fun updateRegionIcon(regionName: String) {
        if (regionName.isNotEmpty()) {
            binding.tilRegion.endIconDrawable = requireContext().getDrawable(R.drawable.ic_close)
            binding.tilRegion.setEndIconOnClickListener {
                val currentArea = viewModel.selectedArea.value
                viewModel.updateArea(currentArea?.copy(region = null) ?: Area(null, null))
            }
        } else {
            binding.tilRegion.endIconDrawable = requireContext().getDrawable(R.drawable.ic_arrow_forward)
            binding.tilRegion.setEndIconOnClickListener {
                findNavController().navigate(R.id.action_areaFragment_to_regionFragment)
            }
        }
    }

    // Добавляем TextWatcher для tetCountry и tetRegion
    private fun setTextChangedListeners() {
        val countryTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                with(binding.tilCountry) {
                    if (s.isNullOrBlank()) {
                        defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.gray, null))
                    } else {
                        defaultHintTextColor = ColorStateList.valueOf(
                            resources.getColor(
                                if (isDarkTheme()) {
                                    R.color.white
                                } else {
                                    R.color.black
                                },
                                null
                            )
                        )
                    }
                }
            }
        }

        val regionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                with(binding.tilRegion) {
                    if (s.isNullOrBlank()) {
                        defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.gray, null))
                    } else {
                        defaultHintTextColor = ColorStateList.valueOf(
                            resources.getColor(
                                if (isDarkTheme()) {
                                    R.color.white
                                } else {
                                    R.color.black
                                },
                                null
                            )
                        )
                    }
                }
            }
        }

        binding.tetCountry.addTextChangedListener(countryTextWatcher)
        binding.tetRegion.addTextChangedListener(regionTextWatcher)
    }

    // Проверка темы (светлая/темная)
    private fun isDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

}
