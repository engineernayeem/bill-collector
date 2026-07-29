package com.example.utils

import android.content.Context
import android.content.Intent
import com.example.data.models.CustomerEntity
import com.example.data.models.PaymentEntity

object PdfReceiptGenerator {

    fun generateTextReceipt(payment: PaymentEntity, customer: CustomerEntity?): String {
        return """
        ========================================
                   BILL COLLECTOR ISP
                  PAYMENT RECEIPT / INVOICE
        ========================================
        Receipt No : ${payment.receiptNumber}
        Date       : ${DateUtils.formatDateTime(payment.paymentDate)}
        Payment    : ${payment.paymentMethod}
        ----------------------------------------
        CUSTOMER INFORMATION:
        ID         : ${customer?.customerId ?: payment.customerId}
        Name       : ${payment.customerName}
        Phone      : ${customer?.mobileNumber ?: "N/A"}
        Address    : ${customer?.address ?: "N/A"}
        Package    : ${customer?.packageName ?: "N/A"} (${customer?.packageSpeedMbps ?: 0} Mbps)
        ----------------------------------------
        PAYMENT BREAKDOWN:
        Previous Due  : ${CurrencyUtils.formatCurrency(payment.previousDue)}
        Current Bill  : ${CurrencyUtils.formatCurrency(payment.currentBill)}
        ----------------------------------------
        PAID AMOUNT   : ${CurrencyUtils.formatCurrency(payment.amount)}
        REMAINING DUE : ${CurrencyUtils.formatCurrency(payment.remainingDue)}
        ----------------------------------------
        Note          : ${payment.paymentNote.ifBlank { "Monthly Internet Bill" }}
        Status        : PAID / CONFIRMED
        ========================================
           Thank you for choosing our ISP service!
        ========================================
        """.trimIndent()
    }

    fun shareReceipt(context: Context, receiptText: String, recipientPhone: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "ISP Internet Payment Receipt")
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        val chooser = Intent.createChooser(intent, "Share Payment Receipt")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
