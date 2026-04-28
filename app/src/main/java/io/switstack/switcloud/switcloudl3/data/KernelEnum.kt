package io.switstack.switcloud.switcloudl3.data

enum class KernelEnum(val id: Int, val kernelName: String) {
    KERNEL_MASTERCARD(2, "Mastercard"),
    KERNEL_VISA(3, "Visa"),
    KERNEL_AMEX(4, "American Express"),
    KERNEL_DISCOVER(6, "Discover"),
    KERNEL_RUPAY(13, "Rupay"),
    KERNEL_POINT(16, "Entry Point");

    companion object {
        fun fromId(id: Int?): KernelEnum? {
            return entries.find { it.id == id }
        }
    }
}