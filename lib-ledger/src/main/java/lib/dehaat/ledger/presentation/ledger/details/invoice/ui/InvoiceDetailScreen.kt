package lib.dehaat.ledger.presentation.ledger.details.invoice.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lib.dehaat.ledger.initializer.getAmountInRupees
import lib.dehaat.ledger.initializer.themes.LedgerColors
import lib.dehaat.ledger.initializer.toDateMonthYear
import lib.dehaat.ledger.presentation.common.uicomponent.CommonContainer
import lib.dehaat.ledger.presentation.common.uicomponent.SpaceMedium
import lib.dehaat.ledger.presentation.ledger.components.CreditNoteKeyValue
import lib.dehaat.ledger.presentation.ledger.components.CreditNoteKeyValueInSummaryView
import lib.dehaat.ledger.presentation.ledger.components.CreditNoteKeyValueInSummaryViewWithTopPadding
import lib.dehaat.ledger.presentation.ledger.components.ProductView
import lib.dehaat.ledger.presentation.ledger.details.invoice.InvoiceDetailViewModel
import lib.dehaat.ledger.resources.text18Sp
import lib.dehaat.ledger.resources.textBold14Sp
import lib.dehaat.ledger.resources.textMedium14Sp

@Composable
fun InvoiceDetailScreen(
    viewModel: InvoiceDetailViewModel,
    ledgerColors: LedgerColors,
    onBackPress: () -> Unit,
    onDownloadInvoiceClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val invoiceData = uiState.invoiceDetailDataViewData
    val scrollState = rememberScrollState()

    CommonContainer(
        title = "Invoice Detail",
        onBackPress = onBackPress,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(
                    state = scrollState,
                    enabled = true,
                )
                .fillMaxSize()
                .background(Color.White)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CreditNoteKeyValue(
                "Invoice Amount",
                invoiceData?.summary?.amount.getAmountInRupees(),
                keyTextStyle = text18Sp(textColor = ledgerColors.CtaDarkColor),
                valueTextStyle = text18Sp(
                    fontWeight = FontWeight.Bold,
                    textColor = ledgerColors.CtaDarkColor
                ),
            )

            SpaceMedium()

            invoiceData?.loans?.forEach { loan ->
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(
                            shape = RoundedCornerShape(9.dp),
                            color = ledgerColors.InfoContainerBgColor
                        )
                        .padding(16.dp)
                ) {

                    CreditNoteKeyValueInSummaryView(
                        "Credit Account Number",
                        loan.loanAccountNo,
                        ledgerColors = ledgerColors,
                    )

                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Credit Status",
                        loan.status,
                        ledgerColors = ledgerColors,
                    )

                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Credit Amount",
                        loan.amount.getAmountInRupees(),
                        ledgerColors = ledgerColors,
                    )

                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Invoice Contribution in Credit Amount",
                        loan.invoiceContributionInLoan.getAmountInRupees(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Outstanding",
                        loan.totalOutstandingAmount.getAmountInRupees(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Principal o/s",
                        loan.principalOutstandingAmount.getAmountInRupees(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Interest o/s",
                        loan.interestOutstandingAmount.getAmountInRupees(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Penalty o/s",
                        loan.penaltyOutstandingAmount.getAmountInRupees(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Overdue Interest o/s",
                        loan.overdueInterestOutstandingAmount.getAmountInRupees(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Disbursal Date",
                        loan.disbursalDate.toDateMonthYear(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Interest-Free Period End Date",
                        loan.interestFreeEndDate.toDateMonthYear(),
                        ledgerColors = ledgerColors,
                    )
                    CreditNoteKeyValueInSummaryViewWithTopPadding(
                        "Financier",
                        loan.financier,
                        ledgerColors = ledgerColors,
                    )
                }
            }

            SpaceMedium()

            Column(modifier = Modifier) {
                Text(
                    modifier = Modifier,
                    text = "Product Details",
                    style = text18Sp(textColor = ledgerColors.CtaDarkColor),
                    maxLines = 1
                )
                val products = invoiceData?.productsInfo?.productList.orEmpty()
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Items: ${products.size}",
                    style = textMedium14Sp(textColor = ledgerColors.CtaColor),
                    maxLines = 1
                )

                SpaceMedium()
                products.forEachIndexed { index, product ->
                    ProductView(
                        modifier = Modifier.padding(end = 16.dp),
                        ledgerColors = ledgerColors,
                        name = product.name,
                        image = product.fname,
                        qty = product.quantity,
                        price = product.priceTotal
                    )
                    if (index < products.lastIndex)
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = ledgerColors.CreditViewHeaderDividerBColor,
                            thickness = 1.dp
                        )
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .background(
                        shape = RoundedCornerShape(9.dp),
                        color = ledgerColors.InfoContainerBgColor
                    )
                    .padding(16.dp)
            ) {
                CreditNoteKeyValueInSummaryView(
                    "Item Total", invoiceData?.productsInfo?.itemTotal.getAmountInRupees(),
                    ledgerColors = ledgerColors
                )

                CreditNoteKeyValueInSummaryViewWithTopPadding(
                    "Discount",
                    invoiceData?.productsInfo?.discount.getAmountInRupees(),
                    ledgerColors = ledgerColors,
                    valueTextStyle = textBold14Sp(textColor = ledgerColors.DownloadInvoiceColor),
                )

                CreditNoteKeyValueInSummaryViewWithTopPadding(
                    "GST",
                    invoiceData?.productsInfo?.gst.getAmountInRupees(),
                    ledgerColors = ledgerColors
                )

                Divider(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .background(color = ledgerColors.TabBorderColorDefault), thickness = 1.dp
                )

                CreditNoteKeyValue(
                    "Total Amount",
                    invoiceData?.productsInfo?.subTotal.getAmountInRupees(),
                    keyTextStyle = text18Sp(textColor = ledgerColors.CtaDarkColor),
                    valueTextStyle = text18Sp(
                        fontWeight = FontWeight.Bold,
                        textColor = ledgerColors.CtaDarkColor
                    ),
                    modifier = Modifier,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onDownloadInvoiceClick()
                        }
                        .padding(top = 16.dp)
                        .background(shape = RoundedCornerShape(40.dp), color = Color.White)
                        .border(
                            width = 1.dp,
                            color = ledgerColors.DownloadInvoiceColor,
                            shape = RoundedCornerShape(40.dp)
                        )
                        .padding(vertical = 16.dp, horizontal = 40.dp),
                    text = "Download Invoice",
                    color = ledgerColors.DownloadInvoiceColor,
                    maxLines = 1
                )
            }
        }
    }
}