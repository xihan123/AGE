package cn.xihan.age.util


import com.qiniu.android.dns.DnsManager
import com.qiniu.android.dns.IResolver
import com.qiniu.android.dns.NetworkInfo.normal
import com.qiniu.android.dns.Record
import com.qiniu.android.dns.dns.DnsUdpResolver
import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/1/20 2:38
 * @介绍 :
 */
class OkHttpDns : Dns {

    var dnsManager: DnsManager? = null

    @Throws(UnknownHostException::class)
    override fun lookup(hostname: String): List<InetAddress> {
        dnsManager?.let {
            try {
                val records = it.queryRecords(hostname)
                if (records == null || records.isEmpty()) {
                    return Dns.SYSTEM.lookup(hostname)
                }
                val ips = records2Ip(records)
                if (ips.isNullOrEmpty()) {
                    return Dns.SYSTEM.lookup(hostname)
                }
                val result: MutableList<InetAddress> = ArrayList()
                ips.forEach { ip ->
                    result.addAll(mutableListOf(*InetAddress.getAllByName(ip)))
//                    logDebug("ip: $ip")
                }
                return result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } ?: return Dns.SYSTEM.lookup(hostname)
        return Dns.SYSTEM.lookup(hostname)
    }

    companion object {
        private val SYSTEM = Dns.SYSTEM
        var instance: OkHttpDns? = null
            get() {
                if (field == null) {
                    field = OkHttpDns()
                }
                return field
            }
            private set

        private fun records2Ip(records: Array<Record>?): Array<String>? {
            if (records.isNullOrEmpty()) {
                return null
            }
            val a = ArrayList<String>(records.size)
            records.mapTo(a) { it.value }
            return if (a.size == 0) {
                null
            } else a.toTypedArray()
        }
    }

    init {
        val resolvers = arrayOfNulls<IResolver>(1)
        try {
            resolvers[0] = DnsUdpResolver("210.2.4.8")
            dnsManager = DnsManager(normal, resolvers)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}