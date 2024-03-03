import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.media3.exoplayer.upstream.BandwidthMeter

@UnstableApi
class TrafficSpeedCalculatorBandwidthMeter(private val delegate: BandwidthMeter) :
    BandwidthMeter by delegate {

    private var _lastTimestamp: Long = 0
    private var _trafficBytes: Long = 0


    override fun getTransferListener(): TransferListener {
        return Calculator(delegate.transferListener) {
            _trafficBytes += it
        }
    }

    fun getNetworkSpeed(): Long {
        val now = System.currentTimeMillis()
        if (now == _lastTimestamp) {
            return 0
        }
        val speed = _trafficBytes * 1000 / 1024 / (now - _lastTimestamp)
        _lastTimestamp = now
        _trafficBytes = 0
        return speed
    }


    private class Calculator(
        private val delegate: TransferListener?,
        private val onBytesReceived: (Int) -> Unit
    ) : TransferListener {
        override fun onTransferInitializing(
            source: DataSource,
            dataSpec: DataSpec,
            isNetwork: Boolean
        ) {
            delegate?.onTransferInitializing(source, dataSpec, isNetwork)
        }

        override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
            delegate?.onTransferStart(source, dataSpec, isNetwork)
        }

        override fun onBytesTransferred(
            source: DataSource,
            dataSpec: DataSpec,
            isNetwork: Boolean,
            bytesTransferred: Int
        ) {
            delegate?.onBytesTransferred(source, dataSpec, isNetwork, bytesTransferred)
            onBytesReceived(bytesTransferred)
        }

        override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
            delegate?.onTransferEnd(source, dataSpec, isNetwork)
        }

    }
}