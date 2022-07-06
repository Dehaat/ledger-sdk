package lib.dehaat.ledger.data.source

import com.cleanarch.base.entity.result.api.APIResultEntity
import lib.dehaat.ledger.entities.creditlines.CreditLineEntity
import lib.dehaat.ledger.entities.creditsummary.CreditSummaryEntity
import lib.dehaat.ledger.entities.detail.creditnote.CreditNoteDetailEntity
import lib.dehaat.ledger.entities.detail.invoice.InvoiceDetailDataEntity
import lib.dehaat.ledger.entities.detail.payment.PaymentDetailEntity
import lib.dehaat.ledger.entities.transactions.TransactionEntity
import lib.dehaat.ledger.entities.transactionsummary.TransactionSummaryEntity
import lib.dehaat.ledger.framework.model.transactionsummary.ResponseTransactionSummary

interface ILedgerDataSource {

    suspend fun getCreditSummary(partnerId: String): APIResultEntity<CreditSummaryEntity?>

    suspend fun getTransactionSummary(
        partnerId: String
    ): APIResultEntity<TransactionSummaryEntity?>

    suspend fun getTransactions(
        partnerId: String,
        limit: Int,
        offset: Int,
        onlyPenaltyInvoices: Boolean,
        fromDate: Long?,
        toDate: Long?,
    ): APIResultEntity<List<TransactionEntity>>

    suspend fun getCreditLines(
        partnerId: String
    ): APIResultEntity<List<CreditLineEntity>>

    suspend fun getInvoiceDetail(
        ledgerId: String,
        locusId: String?,
        erpId: String?
    ): APIResultEntity<InvoiceDetailDataEntity?>

    suspend fun getPaymentDetail(
        ledgerId: String,
        locusId: String?,
        erpId: String?,
        mode: String?
    ): APIResultEntity<PaymentDetailEntity?>

    suspend fun getCreditNoteDetail(
        ledgerId: String,
        locusId: String?,
        erpId: String?
    ): APIResultEntity<CreditNoteDetailEntity?>

}