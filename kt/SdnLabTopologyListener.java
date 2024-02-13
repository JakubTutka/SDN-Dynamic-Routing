package pl.edu.agh.kt;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.topology.ITopologyListener;


public class SdnLabTopologyListener implements ITopologyListener {
	
	protected static final Logger logger = LoggerFactory.getLogger(SdnLabTopologyListener.class);
	
	@Override
	public void topologyChanged(List<LDUpdate> linkUpdates) {
		logger.debug("Received topology status");
		
		for (ILinkDiscovery.LDUpdate update : linkUpdates) {
			switch (update.getOperation()) {
				case LINK_UPDATED:
					SdnLabListener.getRouting().addNeighbor(update.getSrc(), update.getSrcPort(), update.getDst(), 1);
					break;
				case LINK_REMOVED:	
					SdnLabListener.getRouting().removeNeighbor(update.getSrc(), update.getSrcPort(), update.getDst());
					break;
				case SWITCH_UPDATED:
					SdnLabListener.getRouting().runStatisticCollector(update.getSrc());
					break;
				case SWITCH_REMOVED:
					SdnLabListener.getRouting().removeSwitch(update.getSrc());
					break;
				case PORT_UP:
			        break;
				case PORT_DOWN:
			        break;
				default:
					break;
			}
		}
	}

}
