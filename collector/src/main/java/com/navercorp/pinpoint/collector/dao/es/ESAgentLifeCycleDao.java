package com.navercorp.pinpoint.collector.dao.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.collector.dao.AgentLifeCycleDao;
import com.navercorp.pinpoint.collector.dao.es.base.EsClient;
import com.navercorp.pinpoint.collector.util.EsIndexs;
import com.navercorp.pinpoint.common.server.bo.AgentLifeCycleBo;
import com.navercorp.pinpoint.common.util.TimeUtils;

@Repository("esAgentLifeCycleDao")
public class ESAgentLifeCycleDao implements AgentLifeCycleDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void insert(AgentLifeCycleBo agentLifeCycleBo) {
		// TODO Auto-generated method stub

		if (agentLifeCycleBo == null) {
			throw new NullPointerException("agentLifeCycleBo must not be null");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("insert agent life cycle. {}", agentLifeCycleBo.toString());
		}

		final String agentId = agentLifeCycleBo.getAgentId();
		final long startTimestamp = agentLifeCycleBo.getStartTimestamp();
		final long eventIdentifier = agentLifeCycleBo.getEventIdentifier();
		/*long reverseStartTimestamp = TimeUtils.reverseTimeMillis(startTimestamp);
		long reverseEventCounter = TimeUtils.reverseTimeMillis(eventIdentifier);*/
		String id = agentId + EsIndexs.ID_SEP + startTimestamp + EsIndexs.ID_SEP + eventIdentifier;

		try {
			ObjectMapper mapper = new ObjectMapper();
			byte[] json = mapper.writeValueAsBytes(agentLifeCycleBo);
			EsClient.client().prepareIndex(EsIndexs.AGENT_LIFECYCLE, EsIndexs.TYPE, id)
					.setSource(json).get();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.error("esAgentLifeCycleDao insert error. Cause:{}", e.getMessage(), e);
		}
	}

}
