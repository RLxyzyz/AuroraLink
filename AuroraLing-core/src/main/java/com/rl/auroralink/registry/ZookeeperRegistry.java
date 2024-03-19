package com.rl.auroralink.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.rl.auroralink.config.RegistryConfig;
import com.rl.auroralink.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 任磊
 * @version 1.0
 * @project AuroraLink
 * @description zookeeper注册中心
 * @date 2024/3/18 21:20:08
 */
@Slf4j
public class ZookeeperRegistry implements Registry{
    private CuratorFramework client;


    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;


    /**
     * 本地注册的节点key集合(用于维护续期）
     * */
    private final Set<String> localRegisterNodeKeySet=new HashSet<>();

    /**
     * 注册中心本地缓存
     * */
    private final RegisterServiceCache registerServiceCache=new RegisterServiceCache();


    /**
     * 正在监听的集合
     * */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点*/
    private static final String ZOOKEEPER_ROOT_PATH="/rpc/zookeeper";


    @Override
    public void init(RegistryConfig registryConfig) {
        client= CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()),3))
                .build();
        //构建clientDiscover实例
        serviceDiscovery= ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZOOKEEPER_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        //启动服务

        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registry(ServiceMetaInfo serviceMetaInfo) throws Exception {
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));
        //添加节点到本地缓存
        String registerKey=ZOOKEEPER_ROOT_PATH+"/"+serviceMetaInfo.getServiceNodeKey();

        localRegisterNodeKeySet.add(registerKey);
    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress=serviceMetaInfo.getServiceHost()+":"+serviceMetaInfo.getServicePort();
        try {
            return ServiceInstance.<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //从本地缓存移除
        String registerKey=ZOOKEEPER_ROOT_PATH+"/"+serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //优先从本地缓存中寻找
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registerServiceCache.readCache();
        if(cacheServiceMetaInfoList!=null)
        {
            return cacheServiceMetaInfoList;
        }
        //查询服务信息
        try {
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);
            //解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());
            //写入缓存
            registerServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败！",e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException("节点下线失败",e);
            }
        }
        //释放资源
        if(client!=null){
            client.close();
        }

    }

    @Override
    public void heartBeat() {
        //不需要心跳机制，建立临时节点，如果服务器故障，则临时节点直接丢失
    }

    @Override
    public void watch(String serviceNodeKey) {

        String watchKey=ZOOKEEPER_ROOT_PATH+"/"+serviceNodeKey;

        boolean newWatch = watchingKeySet.add(watchKey);
        if (newWatch)
        {
            CuratorCache curatorCache=CuratorCache.build(client,watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(CuratorCacheListener.builder().forDeletes(childData -> {
                registerServiceCache.clearCache();
            }).build());
        }
    }
}
