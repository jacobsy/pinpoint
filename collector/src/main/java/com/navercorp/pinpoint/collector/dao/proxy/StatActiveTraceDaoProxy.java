package com.navercorp.pinpoint.collector.dao.proxy;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.navercorp.pinpoint.collector.dao.AgentStatDaoV2;
import com.navercorp.pinpoint.common.server.bo.stat.ActiveTraceBo;

@Repository("activeTraceDaoProxy")
public class StatActiveTraceDaoProxy implements AgentStatDaoV2<ActiveTraceBo> {

	@Autowired(required = false)
	@Qualifier("hbaseActiveTraceDao")
	AgentStatDaoV2<ActiveTraceBo> hbaseActiveTraceDao;
	
	@Resource
	AgentStatDaoV2<ActiveTraceBo> esActiveTraceDao;
	
	@Override
	public void insert(String agentId, List<ActiveTraceBo> agentStatDataPoints) {
		if(null != hbaseActiveTraceDao) {
			hbaseActiveTraceDao.insert(agentId, agentStatDataPoints);
		}
		esActiveTraceDao.insert(agentId, agentStatDataPoints);
	}

}
