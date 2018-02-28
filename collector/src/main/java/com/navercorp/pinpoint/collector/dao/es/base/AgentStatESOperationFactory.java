package com.navercorp.pinpoint.collector.dao.es.base;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.collector.util.EsIndexs;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatUtils;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;

/**
 * 转换实体
 * @author futao
 *
 */

public class AgentStatESOperationFactory {

	public static <T extends AgentStatDataPoint> boolean createPuts( String agentId, AgentStatType agentStatType, List<T> agentStatDataPoints) {
        if (agentStatDataPoints == null || agentStatDataPoints.isEmpty()) {
            return false;
        }
        TransportClient client = EsClient.client();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        
        
        Map<Long, List<T>> timeslots = slotAgentStatDataPoints(agentStatDataPoints);
        try {
	        for (Map.Entry<Long, List<T>> timeslot : timeslots.entrySet()) {
	            long baseTimestamp = timeslot.getKey();
	            List<T> slottedAgentStatDataPoints = timeslot.getValue();
	            String id = agentId + EsIndexs.ID_SEP + agentStatType.getTypeCode() + EsIndexs.ID_SEP + baseTimestamp;
	            ObjectMapper mapper = new ObjectMapper();
				byte[] json = mapper.writeValueAsBytes(slottedAgentStatDataPoints);
	            bulkRequest.add(client.prepareIndex(EsIndexs.AGENT_STAT_V2, EsIndexs.TYPE, id)
	                    .setSource(jsonBuilder()
	                                .startObject()
	                                    .field("stats", slottedAgentStatDataPoints)
	                                    .field("agentStatType", agentStatType.getTypeCode())
	                                .endObject()
	                              )
	                    );
	        }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BulkResponse bulkResponse = bulkRequest.get();
        return bulkResponse.hasFailures();
    }
	
	private static <T extends AgentStatDataPoint> Map<Long, List<T>> slotAgentStatDataPoints(List<T> agentStatDataPoints) {
        Map<Long, List<T>> timeslots = new TreeMap<Long, List<T>>();
        for (T agentStatDataPoint : agentStatDataPoints) {
            long timestamp = agentStatDataPoint.getTimestamp();
            long timeslot = AgentStatUtils.getBaseTimestamp(timestamp);
            List<T> slottedDataPoints = timeslots.get(timeslot);
            if (slottedDataPoints == null) {
                slottedDataPoints = new ArrayList<T>();
                timeslots.put(timeslot, slottedDataPoints);
            }
            slottedDataPoints.add(agentStatDataPoint);
        }
        return timeslots;
    }
}
