package com.rl.auroralink.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONUtil;
import com.rl.auroralink.config.RegistryConfig;
import com.rl.auroralink.model.ServiceMetaInfo;
import com.rl.auroralink.utils.ConfigUtils;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
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

    private final Set<String> localRegisterNodeKeySet=new HashSet<>();

    /**
     * 注册中心缓存
     * */
    private static final RegisterServiceCache registryServiceCache=new RegisterServiceCache();


    /**
     * 正在监听的集合
     * */
    private final Set<String> watchingKeySet=new ConcurrentHashSet<>();

    //根节点
    private static final String ETCD_DEFAULT_ROOT_PATH="/rpc/";
    @Override
    public void init(RegistryConfig registryConfig) {
        client=Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient=client.getKVClient();
        heartBeat();
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


        //添加到本地维护的节点集合
        localRegisterNodeKeySet.add(registryKey);


    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registryKey=ETCD_DEFAULT_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo),StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_DEFAULT_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听 key 的变化
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            // 写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");

        //下线节点
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8));
            }catch (Exception e)
            {
                throw new RuntimeException("节点下线失败",e);
            }
        }
        if(kvClient!=null)
        {
            kvClient.close();
        }
        if (client!=null)
        {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        registry(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient=client.getWatchClient();

        //开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);

        watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8),response -> {
            for (WatchEvent event : response.getEvents()) {
                switch (event.getEventType())
                {
                    //key删除时触发
                    case DELETE:
                        //清除缓存
                        registryServiceCache.clearCache();
                        break;
                    case PUT: default:break;

                }
            }
        });
    }
}
