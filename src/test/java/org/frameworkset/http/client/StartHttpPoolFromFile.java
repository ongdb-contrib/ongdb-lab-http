package org.frameworkset.http.client;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.frameworkset.spi.remote.http.HttpRequestUtil;
import org.junit.Test;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.http.client.StartHttpPoolFromFile
 * @Description: TODO
 * @date 2020/4/28 15:04
 */
public class StartHttpPoolFromFile {
    @Test
    public void test() {
        //启动连接池
        HttpRequestUtil.startHttpPools("application.properties");
    }
}
