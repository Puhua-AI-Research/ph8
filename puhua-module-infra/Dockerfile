FROM eclipse-temurin:21-jre


## 创建目录，并使用它作为工作目录
RUN mkdir -p /puhua-module-infra-biz
WORKDIR /puhua-module-infra-biz
## 将后端项目的 Jar 文件，复制到镜像中
COPY ./puhua-module-infra-biz.jar app.jar

## 设置 TZ 时区
## 设置 JAVA_OPTS 环境变量，可通过 docker run -e "JAVA_OPTS=" 进行覆盖
ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms512m -Xmx512m"

## 暴露后端项目的 10302 端口
EXPOSE 10302

## 启动后端项目
CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar app.jar