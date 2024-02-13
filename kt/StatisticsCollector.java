package pl.edu.agh.kt;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.projectfloodlight.openflow.protocol.OFPortStatsEntry;
import org.projectfloodlight.openflow.protocol.OFPortStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequest;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.ListenableFuture;
import net.floodlightcontroller.core.IOFSwitch;

public class StatisticsCollector {

    private StatisticsCollector(IOFSwitch sw) {
        new Timer().scheduleAtFixedRate(new PortStatisticsPoller(sw), 0, PORT_STATISTICS_POLLING_INTERVAL);
    }

    private class PortStatisticsPoller extends TimerTask {
        private final Logger logger = LoggerFactory.getLogger(PortStatisticsPoller.class);
        private IOFSwitch sw;

        public PortStatisticsPoller(IOFSwitch sw) {
            this.sw = sw;
        }

        @SuppressWarnings("unchecked")
		@Override
        public void run() {
            synchronized (StatisticsCollector.this) {
                ListenableFuture<?> future;
                List<OFStatsReply> values = null;
                OFStatsRequest<?> req = sw.getOFFactory().buildPortStatsRequest().setPortNo(OFPort.ANY).build();
                try {
                    if (req != null) {
                        future = sw.writeStatsRequest(req);
                        values = (List<OFStatsReply>) future.get(PORT_STATISTICS_POLLING_INTERVAL * 1000 / 2, TimeUnit.MILLISECONDS);
                    }
                    OFPortStatsReply psr = (OFPortStatsReply) values.get(0);
                    for (OFPortStatsEntry pse : psr.getEntries()) {
                        if (pse.getPortNo().getPortNumber() > 0) {
                            int kbytes = (int) ((pse.getTxBytes().getValue() * 8) / (PORT_STATISTICS_POLLING_INTERVAL / 1000) / 1000);
                            int cost = kbytes / 100;
                            if(cost > 1){
                            	SdnLabListener.getRouting().setCost(sw.getId(), pse.getPortNo(), cost);
                            } else {
                            	SdnLabListener.getRouting().setCost(sw.getId(), pse.getPortNo(), 1);
                            }
                        }
                    }
                    
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    logger.error("Error during statistics polling for Switch: {}", sw.getId(), ex);
                }
            }
        }
    }

    public static final int PORT_STATISTICS_POLLING_INTERVAL = 5000; // in ms

    public static void createInstance(IOFSwitch sw) {
        synchronized (StatisticsCollector.class) {
        	new StatisticsCollector(sw);
        }
    }
    
}
