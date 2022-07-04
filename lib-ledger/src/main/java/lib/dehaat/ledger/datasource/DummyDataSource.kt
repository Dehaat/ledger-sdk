package lib.dehaat.ledger.datasource

import android.content.Context
import lib.dehaat.ledger.initializer.LedgerParentApp
import lib.dehaat.ledger.initializer.LedgerSDK
import lib.dehaat.ledger.initializer.callbacks.LedgerCallbacks
import lib.dehaat.ledger.presentation.model.creditsummary.CreditSummaryViewData
import lib.dehaat.ledger.presentation.model.creditsummary.CreditViewData
import lib.dehaat.ledger.presentation.model.creditsummary.InfoViewData
import lib.dehaat.ledger.presentation.model.creditsummary.OverdueViewData
import lib.dehaat.ledger.presentation.model.detail.invoice.InvoiceDetailDataViewData

object DummyDataSource {

    private val creditViewData = CreditViewData(
        externalFinancierSupported = true,
        totalCreditLimit = "totalCreditLimit",
        totalAvailableCreditLimit = "totalAvailableCreditLimit",
        totalOutstandingAmount = "totalOutstandingAmount",
        principalOutstandingAmount = "principalOutstandingAmount",
        interestOutstandingAmount = "interestOutstandingAmount",
        overdueInterestOutstandingAmount = "overdueInterestOutstandingAmount",
        penaltyOutstandingAmount = "penaltyOutstandingAmount"
    )

    private val infoViewData = InfoViewData(
        totalPurchaseAmount = "totalPurchaseAmount",
        totalPaymentAmount = "totalPaymentAmount",
        undeliveredInvoiceAmount = "undeliveredInvoiceAmount"
    )

    private val overdueViewData = OverdueViewData(
        totalOverdueLimit = "totalOverdueLimit",
        totalOverdueAmount = "totalOverdueAmount",
        minPaymentAmount = "minPaymentAmount",
        minPaymentDueDate = 78
    )

    val creditSummaryViewData = CreditSummaryViewData(
        creditViewData,
        overdueViewData,
        infoViewData
    )

    private val dbaApp = LedgerParentApp.DBA(
        ledgerCallBack = object : LedgerCallbacks {
            override fun onClickPayNow(
                creditSummaryViewData: CreditSummaryViewData?
            ) = Unit

            override fun onClickDownloadInvoice(
                invoiceDetailDataViewData: InvoiceDetailDataViewData?
            ) = Unit
        }
    )

    private val aimsApp = LedgerParentApp.AIMS()

    fun initDBA(context: Context) = LedgerSDK.init(
        context,
        dbaApp
    )

    fun initAIMS(context: Context) = LedgerSDK.init(
        context,
        aimsApp
    )
}
