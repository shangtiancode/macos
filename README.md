# macos & mubbo by alishangtian.com

## 1.下载源码
    git clone git@github.com:shangtiancode/macos.git

## 2.构建
    cd macos && mvn -U clean package -Dmaven.test.skip=true

## 3.启动macos服务注册发现中心

    cd macos/macos-broker/target
    java -Dmacos.server.listenPort=10000 -Dmacos.broker.clusterNodes=127.0.0.1:10001 -jar macos-broker-0.0.1.release.jar
    java -Dmacos.server.listenPort=10001 -Dmacos.broker.clusterNodes=127.0.0.1:10002 -jar macos-broker-0.0.1.release.jar
    java -Dmacos.server.listenPort=10002 -Dmacos.broker.clusterNodes=127.0.0.1:10000 -jar macos-broker-0.0.1.release.jar

    说明：上述命令本地启动了三台macos节点，macos.server.listenPort表示监听的端口号，macos.broker.clusterNodes表示集群中的节点，用于集群发现

## 4.启动服务（demo）

    cd macos/macos-demo/target
    java -Dnetty.server.listenPort=9000 -Dserver.port=8000 -jar macos-demo-0.0.1.release.jar

    说明：上述命令启动了一个服务，包含服务提供方和消费方，netty.server.listenPort表示本地rpc服务监听的端口，server.port表示springboot web应用监听的端口

## 5.测试

    测试链接：http://127.0.0.1:8000/insert?id=10
    结果是100