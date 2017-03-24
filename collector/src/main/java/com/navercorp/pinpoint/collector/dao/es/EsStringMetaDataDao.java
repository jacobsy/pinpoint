/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.collector.dao.es;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.navercorp.pinpoint.collector.dao.StringMetaDataDao;
import com.navercorp.pinpoint.collector.util.EsTables;
import com.navercorp.pinpoint.collector.util.JsonUtils;
import com.navercorp.pinpoint.thrift.dto.TStringMetaData;

/**
 * @author yangjian
 */
@Repository
public class EsStringMetaDataDao implements StringMetaDataDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Client client;


    @Override
    public void insert(TStringMetaData stringMetaData) {
        if (stringMetaData == null) {
            throw new NullPointerException("stringMetaData must not be null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("insert:{}", stringMetaData);
        }
        IndexResponse response = client.prepareIndex(EsTables.STRING_METADATA,EsTables.STRING_METADATA_CF_STR_QUALI_STRING).setSource(JsonUtils.encode(stringMetaData)).execute().actionGet();
        debugInsert(response);
    }

    private void debugInsert(IndexResponse response){
    	String index = response.getIndex();  
        String type = response.getType();  
        String id = response.getId();  
        long version = response.getVersion();  
        boolean created = response.isCreated();  
        logger.debug("EsTraceDao insert:"+index+","+type+","+id+","+version+","+created);  
    }

}
