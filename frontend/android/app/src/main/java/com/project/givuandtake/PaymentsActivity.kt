package com.project.givuandtake

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.tosspayments.paymentsdk.PaymentWidget
import com.tosspayments.paymentsdk.model.PaymentCallback
import com.tosspayments.paymentsdk.model.PaymentWidgetStatusListener
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.view.PaymentMethod
import com.tosspayments.paymentsdk.view.Agreement
import com.project.givuandtake.databinding.ActivityPaymentsBinding

class PaymentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientKey = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq" // 클라이언트 키
        val customerKey = "test_customer_123" // 구매자 키

        // PaymentWidget 초기화
        val paymentWidget = PaymentWidget(
            activity = this@PaymentsActivity,
            clientKey = clientKey,
            customerKey = customerKey
        )

        // 결제 위젯 렌더링 리스너
        val paymentMethodWidgetStatusListener = object : PaymentWidgetStatusListener {
            override fun onLoad() {
                Log.d("PaymentWidget", "결제위젯 렌더링 완료")
            }

            override fun onFail(fail: TossPaymentResult.Fail) {
                Log.e("PaymentWidget", "결제 위젯 로드 실패: ${fail.errorMessage}")
            }
        }

        // 결제 방법 및 약관 위젯 렌더링
        paymentWidget.run {
            renderPaymentMethods(
                method = binding.paymentWidget,
                amount = PaymentMethod.Rendering.Amount(10000),  // 금액 설정
                paymentWidgetStatusListener = paymentMethodWidgetStatusListener
            )
            renderAgreement(binding.agreementWidget)
        }

        // 결제 버튼 클릭 시 결제 요청
        binding.payButton.setOnClickListener {
            paymentWidget.requestPayment(
                paymentInfo = PaymentMethod.PaymentInfo(orderId = "wBWO9RJXO0UYqJMV4er8J", orderName = "Gift Payment"),
                paymentCallback = object : PaymentCallback {
                    override fun onPaymentSuccess(success: TossPaymentResult.Success) {
                        Log.i("Payment Success", "결제 성공: ${success.paymentKey}")
                    }

                    override fun onPaymentFailed(fail: TossPaymentResult.Fail) {
                        Log.e("Payment Fail", "결제 실패: ${fail.errorMessage}")
                    }
                }
            )
        }
    }
}
