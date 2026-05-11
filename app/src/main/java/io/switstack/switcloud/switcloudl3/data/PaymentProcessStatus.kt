package io.switstack.switcloud.switcloudl3.data

sealed class PaymentProcessStatus {
    object Idle : PaymentProcessStatus() // all leds grey in cas of fail or if not ready
    object Ready : PaymentProcessStatus() // first blinking led
    object Step1Confirmation : PaymentProcessStatus() // first led on
    object Step2Confirmation : PaymentProcessStatus() // first and second led on
    object Step3Confirmation : PaymentProcessStatus() // first, second and third led on
    object Completed : PaymentProcessStatus() // fourth led on
}