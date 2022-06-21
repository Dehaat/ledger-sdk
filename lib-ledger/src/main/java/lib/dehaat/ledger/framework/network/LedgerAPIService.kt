package lib.dehaat.ledger.framework.network

import lib.dehaat.ledger.framework.model.creditlines.ResponseCreditLines
import lib.dehaat.ledger.framework.model.creditsummary.ResponseCreditSummary
import lib.dehaat.ledger.framework.model.detail.creditnote.ResponseCreditNoteDetail
import lib.dehaat.ledger.framework.model.detail.invoice.ResponseInvoiceDetail
import lib.dehaat.ledger.framework.model.detail.payment.ResponsePaymentDetail
import lib.dehaat.ledger.framework.model.transactions.ResponseTransactions
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LedgerAPIService {

    @GET("/finance/accounting/credit-summary")
    suspend fun getCreditSummary(
        @Query("partner_id") partnerId: String
    ): Response<ResponseCreditSummary>

    @GET("/finance/accounting/transactions")
    suspend fun getTransactions(
        @Query("partner_id") partnerId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("only_penalty_invoices") onlyPenaltyInvoices: Boolean,
        @Query("from_date") fromDate: Long?,
        @Query("to_date") toDate: Long?,
    ): Response<ResponseTransactions>

    @GET("/finance/accounting/credit-lines")
    suspend fun getCreditLines(
        @Query("partner_id") partnerId: String
    ): Response<ResponseCreditLines>

    @GET("/finance/invoice")
    suspend fun getInvoiceDetail(
        @Query("ledger_id") ledgerId: String,
        @Query("locus_id") locusId: String?,
        @Query("erp_id") erpId: String?
    ): Response<ResponseInvoiceDetail>

    @GET("/finance/payment")
    suspend fun getPaymentDetail(
        @Query("ledger_id") ledgerId: String,
        @Query("locus_id") locusId: String?,
        @Query("erp_id") erpId: String?,
        @Query("mode") mode: String?
    ): Response<ResponsePaymentDetail>

    @GET("/finance/credit-note")
    suspend fun getCreditNoteDetail(
        @Query("ledger_id") ledgerId: String,
        @Query("locus_id") locusId: String?,
        @Query("erp_id") erpId: String?
    ): Response<ResponseCreditNoteDetail>

}