package com.rl.auroralink.registry;

import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONUtil;
import com.rl.auroralink.config.RegistryConfig;
import com.rl.auroralink.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description Etcd注册中心
 * @date 2024/3/18 11:29:59
 */
public class EtcdRegistry implements Registry{
    private Client client;

    private KV kvClient;

    //根节点
    private static final String ETCD_DEFAULT_ROOT_PATH="/rpc/";
    @Override
    public void init(RegistryConfig registryConfig) {
        client=Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient=client.getKVClient();
    }

    @Override
    public void registry(ServiceMetaInfo serviceMetaInfo) throws Exception {

        Lease leaseClient=client.getLeaseClient();

        //创建30秒的租约
        long leaseId=leaseClient.grant(30).get().getID();

        //设置需要存储的键值对

        String registryKey=ETCD_DEFAULT_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();

        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);

        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对和租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();


    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        kvClient.delete(ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo),StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = ETCD_DEFAULT_ROOT_PATH+serviceKey+"/";
        try {
            //前缀搜索
            GetOption getOption=GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();
            return keyValues.stream().map(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value,ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        }catch (Exception e)
        {
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        if(kvClient!=null)
        {
            kvClient.close();
        }
        if (client!=null)
        {
            client.close();
        }
    }
}
