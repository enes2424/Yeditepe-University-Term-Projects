public class DynamicBuffer {
	private int bufferSize;
	private static final int MIN_BUFFER = 4096;
	private static final int MAX_BUFFER = 131072;
	private static final int DEFAULT_BUFFER = 65507;

	private int totalPackets = 0;
	private int lostPackets = 0;
	private long totalLatency = 0;
	private int latencyCount = 0;
	private long lastAdjustTime = 0;
	private static final long ADJUST_INTERVAL_MS = 2000;

	public DynamicBuffer() {
		this.bufferSize = DEFAULT_BUFFER;
	}

	public void recordPacketLoss() {
		lostPackets++;
		totalPackets++;
	}

	public void recordPacketSuccess() {
		totalPackets++;
	}

	public void recordLatency(long latencyMs) {
		totalLatency += latencyMs;
		latencyCount++;
	}

	public int getBufferSize() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastAdjustTime > ADJUST_INTERVAL_MS) {
			adjustBuffer();
			lastAdjustTime = currentTime;
		}
		return bufferSize;
	}

	private void adjustBuffer() {
		if (totalPackets < 5) {
			return;
		}

		double packetLossRate = (double) lostPackets / totalPackets;
		long avgLatency = latencyCount > 0 ? totalLatency / latencyCount : 0;

		if (packetLossRate > 0.05 || avgLatency > 200) {
			bufferSize = Math.min((int) (bufferSize * 1.5), MAX_BUFFER);
		} else if (packetLossRate < 0.01 && avgLatency < 50) {
			bufferSize = Math.max((int) (bufferSize * 0.75), MIN_BUFFER);
		}

		totalPackets = 0;
		lostPackets = 0;
		totalLatency = 0;
		latencyCount = 0;
	}
}
