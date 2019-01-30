package info.nukoneko.cuc.android.kidspos.ui.main.itemlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.StringRes
import android.view.View
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.event.BarcodeEvent
import info.nukoneko.cuc.android.kidspos.event.EventBus
import org.greenrobot.eventbus.Subscribe

class ItemListViewModel(
        private val config: GlobalConfig,
        private val eventBus: EventBus) : ViewModel() {

    private val currentPrice = MutableLiveData<String>()
    fun getCurrentPriceText(): LiveData<String> = currentPrice

    private val currentStaff = MutableLiveData<String>()
    fun getCurrentStaffText(): LiveData<String> = currentStaff

    private val currentStaffVisibility = MutableLiveData<Int>()
    fun getCurrentStaffVisibility(): LiveData<Int> = currentStaffVisibility

    private val accountButtonEnabled = MutableLiveData<Boolean>()
    fun getAccountButtonEnabled(): LiveData<Boolean> = accountButtonEnabled

    var listener: Listener? = null

    private val data: MutableList<Item> = mutableListOf()

    private fun currentTotal(): Int = data.sumBy { it.price }

    init {
        updateViews()
    }

    fun onClickClear(@Suppress("UNUSED_PARAMETER") view: View?) {
        data.clear()
        updateViews()
    }

    fun onClickAccount(@Suppress("UNUSED_PARAMETER") view: View?) {
        listener?.onStartAccount(data)
    }

    fun onResume() {
        updateViews()
    }

    fun onStart() {
        eventBus.register(this)
    }

    fun onStop() {
        eventBus.unregister(this)
    }

    private fun updateViews() {
        listener?.onDataChanged(data)
        currentPrice.postValue("${currentTotal()} リバー")

        accountButtonEnabled.postValue(data.isNotEmpty())

        currentStaffVisibility.value = if (config.currentStaff == null) View.INVISIBLE else View.VISIBLE
        currentStaff.postValue("たんとう: ${config.currentStaff?.name ?: ""}")
    }

    @Subscribe
    fun onReadItemSuccessEvent(event: BarcodeEvent.ReadItemSuccess) {
        val item = event.value as? Item ?: return
        data.add(item)
        updateViews()
    }

    @Subscribe
    fun onReadStaffSuccessEvent(event: BarcodeEvent.ReadStaffSuccess) {
        val staff = event.value as? Staff ?: return
        config.currentStaff = staff
        updateViews()
    }

    @Subscribe
    fun onReadItemFailedEvent(event: BarcodeEvent.ReadItemFailed) {
        listener?.onShouldShowMessage(R.string.request_item_failed)
    }

    @Subscribe
    fun onReadStaffFailedEvent(event: BarcodeEvent.ReadStaffFailed) {
        listener?.onShouldShowMessage(R.string.request_staff_failed)
    }

    @Subscribe
    fun onReadReceiptFailedEvent(event: BarcodeEvent.ReadReceiptFailed) {
        listener?.onShouldShowMessage(R.string.read_receipt_failed)
    }

    interface Listener {
        fun onDataChanged(data: List<Item>)

        fun onStartAccount(data: List<Item>)

        fun onShouldShowMessage(message: String)

        fun onShouldShowMessage(@StringRes messageId: Int)
    }
}