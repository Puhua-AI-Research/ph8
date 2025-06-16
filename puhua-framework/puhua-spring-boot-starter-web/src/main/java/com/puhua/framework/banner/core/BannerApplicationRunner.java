package com.puhua.framework.banner.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 * @author 中航普华
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("\n----------------------------------------------------------\n\t" +
                        "项目启动成功！\n\t" +
                        "官方网站: \t{} \n\t" +
                        "----------------------------------------------------------",
                "https://ph8.co");
    }

}
