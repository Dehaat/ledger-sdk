package lib.dehaat.ledger.presentation.ledger.transactions

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cleanarch.base.entity.result.api.APIResultEntity
import com.dehaat.androidbase.helper.callInViewModelScope
import com.dehaat.androidbase.helper.tryCatchWithReturn
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lib.dehaat.ledger.domain.usecases.GetTransactionsUseCase
import lib.dehaat.ledger.entities.transactions.TransactionEntity
import lib.dehaat.ledger.framework.model.BaseAPIErrorResponse
import lib.dehaat.ledger.framework.network.BasePagingSourceWithResponse
import lib.dehaat.ledger.presentation.LedgerConstants.ERROR_LOGS
import lib.dehaat.ledger.presentation.LedgerConstants.KEY_PARTNER_ID
import lib.dehaat.ledger.presentation.common.BaseViewModel
import lib.dehaat.ledger.presentation.common.UiEvent
import lib.dehaat.ledger.presentation.ledger.transactions.state.TransactionsViewModelState
import lib.dehaat.ledger.presentation.mapper.LedgerViewDataMapper
import lib.dehaat.ledger.presentation.model.transactions.DaysToFilter
import lib.dehaat.ledger.presentation.model.transactions.TransactionViewData
import javax.inject.Inject

@HiltViewModel
class LedgerTransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val mapper: LedgerViewDataMapper,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val partnerId by lazy {
        savedStateHandle.get<String>(KEY_PARTNER_ID) ?: throw Exception(
            "Partner id should not null"
        )
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> get() = _uiEvent

    private val viewModelState = MutableStateFlow(TransactionsViewModelState())
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    var transactionsList: Flow<PagingData<TransactionViewData>> private set

    init {
        transactionsList = getTransactionPaging()
    }

    fun applyOnlyPenaltyInvoicesFilter(onlyPenaltyInvoices: Boolean) {
        viewModelState.update {
            it.copy(onlyPenaltyInvoices = onlyPenaltyInvoices)
        }
        refresh()
    }

    fun applyDaysFilter(dayFilter: DaysToFilter) {
        viewModelState.update {
            it.copy(daysToFilter = dayFilter)
        }
        getTransactionPaging()
        refresh()
    }

    private fun getTransactionPaging() = Pager(
        config = PagingConfig(pageSize = 1, enablePlaceholders = true),
        pagingSourceFactory = { getPagingSource() }
    ).flow.cachedIn(viewModelScope)

    private fun getPagingSource() =
        object :
            BasePagingSourceWithResponse<TransactionViewData, List<TransactionEntity>>(
                { pageNumber: Int, pageSize: Int ->
                    val response = getTransactionsFromServer(pageSize, pageNumber)
                    processTransactionListResponse(response)
                },
                onResponse = { _, _ -> },
                parseDataList = {
                    mapper.toTransactionsDataEntity(it)
                }
            ) {}

    private suspend fun getTransactionsFromServer(
        pageSize: Int,
        pageNumber: Int
    ): APIResultEntity<List<TransactionEntity>> {
        val (fromDate, toDate) = getFromAndToDate()
        return getTransactionsUseCase.invoke(
            partnerId = partnerId,
            fromDate = fromDate,
            toDate = toDate,
            onlyPenaltyInvoices = viewModelState.value.onlyPenaltyInvoices,
            limit = pageSize,
            offset = (pageNumber - 1) * pageSize
        )
    }

    private fun getFromAndToDate(): Pair<Long?, Long?> {
        return when (val daysToFilter = viewModelState.value.daysToFilter) {
            DaysToFilter.All -> Pair(null, null)
            DaysToFilter.SevenDays -> calculateTimeInMillisecond(7)
            DaysToFilter.OneMonth -> calculateTimeInMillisecond(31)
            DaysToFilter.ThreeMonth -> calculateTimeInMillisecond(31 * 3)
            is DaysToFilter.CustomDays -> calculateCustomDaysMillisecond(daysToFilter)
        }
    }

    private fun calculateCustomDaysMillisecond(dayCount: DaysToFilter.CustomDays): Pair<Long, Long> {
        val currentDaySec = dayCount.toDateMilliSec / 1000
        val pastDaySec = dayCount.fromDateMilliSec / 1000
        return Pair(pastDaySec, currentDaySec)
    }

    private fun calculateTimeInMillisecond(dayCount: Int): Pair<Long, Long> {
        val daysSec = dayCount * 24 * 60 * 60
        val currentDaySec = System.currentTimeMillis() / 1000
        val pastDaySec = currentDaySec.minus(daysSec)
        return Pair(pastDaySec, currentDaySec)
    }

    private fun processTransactionListResponse(response: APIResultEntity<List<TransactionEntity>>) =
        when (response) {
            is APIResultEntity.Success -> {
                response.data
            }
            is APIResultEntity.Failure -> {
                sendShowSnackBarEvent((response.getFailureError()))
                emptyList()
            }
        }

    private fun sendShowSnackBarEvent(message: String) {
        Log.d(ERROR_LOGS, "sendShowSnackBarEvent: $message")
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar(message))
        }
    }

    private fun refresh() {
        callInViewModelScope { _uiEvent.emit(UiEvent.RefreshList) }
    }
}

fun APIResultEntity.Failure.getFailureError() = when (this) {
    is APIResultEntity.Failure.ErrorException -> this.exceptionError.message.nullToValue("API Exception")
    is APIResultEntity.Failure.ErrorFailure -> parseAPIErrorBody(
        this.responseErrorBody,
        this.responseMessage
    )
}

fun String?.nullToValue(value: String = "--") = this ?: value


fun parseAPIErrorBody(errorBodyResponse: String?, defaultError: String = "Error!!") =
    errorBodyResponse?.let { errorBody ->
        tryCatchWithReturn(defaultError) {
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<BaseAPIErrorResponse> =
                moshi.adapter(BaseAPIErrorResponse::class.java)
            jsonAdapter.fromJson(errorBody)?.error?.message ?: defaultError
        }
    } ?: defaultError
